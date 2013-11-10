package com.ott.manyprices.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateful;
import javax.el.ValueExpression;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIParameter;
import javax.faces.component.html.HtmlColumn;
import javax.faces.component.html.HtmlDataTable;
import javax.faces.component.html.HtmlOutcomeTargetLink;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.ott.manyprices.model.Category;
import com.ott.manyprices.model.CustomerPrice;
import com.ott.manyprices.model.Product;

/**
 * Backing bean for CustomerPrice entities.
 * <p>
 * This class provides CRUD functionality for all CustomerPrice entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD framework or
 * custom base class.
 */

@Named
@Stateful
@SessionScoped
public class PriceComparisonBean implements Serializable
{
	
   @Inject
   private Conversation conversation;

   @PersistenceContext(type = PersistenceContextType.EXTENDED)
   private EntityManager entityManager;
	   
   private List<Map<String, Object>> pricesList;
   private List<String> customerNames;
   
   /*
    * Support searching Product entities with pagination
    */

   private int page;
   private int pageSize = 10;
   private long count;
   private List<Product> pageItems;

   private Product example = new Product();

   public int getPage()
   {
      return this.page;
   }

   public void setPage(int page)
   {
      this.page = page;
   }

   public int getPageSize()
   {
      return pageSize;
   }

   public void setPageSize(int pageSize)
   {
      this.pageSize= pageSize;
   }

   public Product getExample()
   {
      return this.example;
   }

   public void setExample(Product example)
   {
      this.example = example;
   }

   public void search()
   {
      this.page = 0;
      populateDataTable();
   }
   
   public void _paginate()
   {

      CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();

      // Populate this.count

      CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
      Root<Product> root = countCriteria.from(Product.class);
      countCriteria = countCriteria.select(builder.count(root)).where(getSearchPredicates(root));
      this.count = this.entityManager.createQuery(countCriteria).getSingleResult();

      // Populate this.pageItems

      CriteriaQuery<Product> criteria = builder.createQuery(Product.class);
      root = criteria.from(Product.class);
      TypedQuery<Product> query = this.entityManager.createQuery(criteria.select(root).where(getSearchPredicates(root)));
      query.setFirstResult(this.page * getPageSize()).setMaxResults(getPageSize());
      this.pageItems = query.getResultList();
   }

   private Predicate[] getSearchPredicates(Root<Product> root)
   {

      CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
      List<Predicate> predicatesList = new ArrayList<Predicate>();

      String name = this.example.getName();
      if (name != null && !"".equals(name))
      {
         predicatesList.add(builder.like(root.<String> get("name"), '%' + name + '%'));
      }
      String dimention = this.example.getDimention();
      if (dimention != null && !"".equals(dimention))
      {
         predicatesList.add(builder.like(root.<String> get("dimention"), '%' + dimention + '%'));
      }
      String description = this.example.getDescription();
      if (description != null && !"".equals(description))
      {
         predicatesList.add(builder.like(root.<String> get("description"), '%' + description + '%'));
      }
      Category category = this.example.getCategory();
      if (category != null)
      {
         predicatesList.add(builder.equal(root.get("category"), category));
      }

      return predicatesList.toArray(new Predicate[predicatesList.size()]);
   }
   public void paginate()
   {
//	   ProductBean productBean = getProductBean();
//	   productBean.paginate();
	   _paginate();
	   List<Product> pageItems = getPageItems();
	   customerNames = new ArrayList<String>();
	   pricesList = new ArrayList<Map<String, Object>>();
	   for(Product product : pageItems){
		   Map<String, Object> productPrices = new HashMap<String, Object>();
		   productPrices.put("productName", product.getName());
		   productPrices.put("productId", product.getId());
		   for(CustomerPrice cp : product.getPrices()){
			   String customer = cp.getCustomer().getName();
			   if(!customerNames.contains(customer)){
				   customerNames.add(customer);
			   }
			   productPrices.put(customer, cp.getPrice());
		   }
		   pricesList.add(productPrices);
	   }
   }

   private HtmlPanelGroup dataTableGroup; // Placeholder.
   private HtmlDataTable dataTable;
   // Actions -----------------------------------------------------------------------------------

   private void populateDataTable() {
	   paginate();
	   while(dataTable.getChildren().size() > 1){
		   dataTable.getChildren().remove(1);
	   }
       for(String customer : customerNames){
           HtmlColumn customerColumn = new HtmlColumn();
           dataTable.getChildren().add(customerColumn);

           // Create <h:outputText value="ID"> for <f:facet name="header"> of 'ID' column.
           HtmlOutputText customerHeader = new HtmlOutputText();
           customerHeader.setValue(customer);
           customerColumn.setHeader(customerHeader);

           // Create <h:outputText value="#{dataItem.id}"> for the body of 'ID' column.
          // HtmlOutcomeTargetLink nameLink = new HtmlOutcomeTargetLink();
           HtmlOutputText customerOutput = new HtmlOutputText();
           customerOutput.setValueExpression("value",
               createValueExpression("#{_item['" + customer + "']}", String.class));
           customerColumn.getChildren().add(customerOutput);
       }
   }
   
   private void createDataTable(){
       // Create <h:dataTable id="productBeanPageItems" styleClass="data-table" value="#{productBean.pricesList}" var="_item">
       dataTable = new HtmlDataTable();
       dataTable.setValueExpression("value",
           createValueExpression("#{priceComparisonBean.pricesList}", List.class));
       dataTable.setVar("_item");
       dataTable.setId("priceComparisonBeanPageItems");
       dataTable.setStyleClass("data-table matrice");
       
       // Create <h:column> for 'ID' column.
       HtmlColumn nameColumn = new HtmlColumn();
       dataTable.getChildren().add(nameColumn);

       // Create <h:outputText value="#{msg.product} \\ #{msg.customer}">
       HtmlOutputText nameHeader = new HtmlOutputText();
       nameHeader.setValueExpression("value", createValueExpression("#{msg.product} \\ #{msg.customer}", String.class));
       nameColumn.setHeader(nameHeader);

       // Create <h:link styleClass="right-column" outcome="/product/view">
       //			<f:param name="id" value="#{_item['productId']}"/>
       //			<h:outputText value="#{_item['productName']}"/>
       //		</h:link>
       HtmlOutcomeTargetLink nameLink = new HtmlOutcomeTargetLink();
       nameLink.setOutcome("/product/view");
       UIParameter productIdParam = new UIParameter();
       productIdParam.setName("id");
       productIdParam.setValueExpression("value",
		   createValueExpression("#{_item['productId']}", Long.class));
       nameLink.getChildren().add(productIdParam);
       HtmlOutputText nameOutput = new HtmlOutputText();
       nameOutput.setValueExpression("value",
           createValueExpression("#{_item['productName']}", String.class));
       nameLink.getChildren().add(nameOutput);
       nameColumn.getChildren().add(nameLink);
              dataTableGroup = new HtmlPanelGroup();
       dataTableGroup.getChildren().add(dataTable);
   }

   // Getters -----------------------------------------------------------------------------------

   public HtmlPanelGroup getDataTableGroup() {
       // This will be called once in the first RESTORE VIEW phase.
       if (dataTableGroup == null) {
    	   createDataTable(); // Populate datatable.
       }
       return dataTableGroup;
   }

   // Setters -----------------------------------------------------------------------------------

   public void setDataTableGroup(HtmlPanelGroup dataTableGroup) {
       this.dataTableGroup = dataTableGroup;
   }

   // Helpers -----------------------------------------------------------------------------------

   private ValueExpression createValueExpression(String valueExpression, Class<?> valueType) {
       FacesContext facesContext = FacesContext.getCurrentInstance();
       return facesContext.getApplication().getExpressionFactory().createValueExpression(
           facesContext.getELContext(), valueExpression, valueType);
   }
   
//	private ProductBean getProductBean() {
//		FacesContext context = FacesContext.getCurrentInstance();
//		return (ProductBean) context.getApplication().getExpressionFactory()
//        .createValueExpression(context.getELContext(), "#{productBean}", ProductBean.class)
//        .getValue(context.getELContext());
//	}
	

   public List<Product> getPageItems()
   {
      return this.pageItems;
   }

   public long getCount()
   {
      return this.count;
   }

   public List<Map<String, Object>> getPricesList() {
	  return pricesList;
   }

   public List<String> getCustomerNames() {
	  return customerNames;
   }
}
