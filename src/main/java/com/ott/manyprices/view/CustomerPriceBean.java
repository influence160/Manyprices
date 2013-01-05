package com.ott.manyprices.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateful;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
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

import com.ott.manyprices.model.Customer;
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
@ConversationScoped
public class CustomerPriceBean implements Serializable
{

   private static final long serialVersionUID = 1L;

   /*
    * Support creating and retrieving CustomerPrice entities
    */

   private Long id;

   public Long getId()
   {
      return this.id;
   }

   public void setId(Long id)
   {
      this.id = id;
   }

   private CustomerPrice customerPrice;

   public CustomerPrice getCustomerPrice()
   {
      return this.customerPrice;
   }

   @Inject
   private Conversation conversation;

   @PersistenceContext(type = PersistenceContextType.EXTENDED)
   private EntityManager entityManager;

   public String create()
   {

      this.conversation.begin();
      return "create?faces-redirect=true";
   }

   public void retrieve()
   {

      if (FacesContext.getCurrentInstance().isPostback())
      {
         return;
      }

      if (this.conversation.isTransient())
      {
         this.conversation.begin();
      }

      if (this.id == null)
      {
         this.customerPrice = this.example;
      }
      else
      {
         this.customerPrice = findById(getId());
      }
   }

   public CustomerPrice findById(Long id)
   {
//	  return (CustomerPrice) entityManager.createQuery("SELECT UNIQUE cp from CustomerPrice cp " +
//	   		" JOIN cp.id.customer" +
//	   		" JOIN cp.id.product").getSingleResult();
      return this.entityManager.find(CustomerPrice.class, id);
   }

   /*
    * Support updating and deleting CustomerPrice entities
    */

   public String update()
   {
      this.conversation.end();

      try
      {
         if (this.id == null)
         {
            this.entityManager.persist(this.customerPrice);
            return "search?faces-redirect=true";
         }
         else
         {
            this.entityManager.merge(this.customerPrice);
            return "view?faces-redirect=true&id=" + this.customerPrice.getId();
         }
      }
      catch (Exception e)
      {
         FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.getMessage()));
         return null;
      }
   }

   public String delete()
   {
      this.conversation.end();

      try
      {
         this.entityManager.remove(findById(getId()));
         this.entityManager.flush();
         return "search?faces-redirect=true";
      }
      catch (Exception e)
      {
         FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.getMessage()));
         return null;
      }
   }

   /*
    * Support searching CustomerPrice entities with pagination
    */

   private int page;
   private long count;
   private List<CustomerPrice> pageItems;

   private CustomerPrice example = new CustomerPrice();

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
      return 10;
   }

   public CustomerPrice getExample()
   {
      return this.example;
   }

   public void setExample(CustomerPrice example)
   {
      this.example = example;
   }

   public void search()
   {
      this.page = 0;
   }

   public void paginate()
   {

      CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();

      // Populate this.count

      CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
      Root<CustomerPrice> root = countCriteria.from(CustomerPrice.class);
      countCriteria = countCriteria.select(builder.count(root)).where(getSearchPredicates(root));
      this.count = this.entityManager.createQuery(countCriteria).getSingleResult();

      // Populate this.pageItems

      CriteriaQuery<CustomerPrice> criteria = builder.createQuery(CustomerPrice.class);
      root = criteria.from(CustomerPrice.class);
      TypedQuery<CustomerPrice> query = this.entityManager.createQuery(criteria.select(root).where(getSearchPredicates(root)));
      query.setFirstResult(this.page * getPageSize()).setMaxResults(getPageSize());
      this.pageItems = query.getResultList();
   }

   private Predicate[] getSearchPredicates(Root<CustomerPrice> root)
   {

      CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
      List<Predicate> predicatesList = new ArrayList<Predicate>();

      Product product = this.example.getProduct();
      if (product != null)
      {
         predicatesList.add(builder.equal(root.get("product"), product));
      }
      Customer customer = this.example.getCustomer();
      if (customer != null)
      {
         predicatesList.add(builder.equal(root.get("customer"), customer));
      }

      return predicatesList.toArray(new Predicate[predicatesList.size()]);
   }

   public List<CustomerPrice> getPageItems()
   {
      return this.pageItems;
   }

   public long getCount()
   {
      return this.count;
   }

   /*
    * Support listing and POSTing back CustomerPrice entities (e.g. from inside an
    * HtmlSelectOneMenu)
    */

   public List<CustomerPrice> getAll()
   {

      CriteriaQuery<CustomerPrice> criteria = this.entityManager.getCriteriaBuilder().createQuery(CustomerPrice.class);
      return this.entityManager.createQuery(criteria.select(criteria.from(CustomerPrice.class))).getResultList();
   }

   @Resource
   private SessionContext sessionContext;

   public Converter getConverter()
   {

      final CustomerPriceBean ejbProxy = this.sessionContext.getBusinessObject(CustomerPriceBean.class);

      return new Converter()
      {

         @Override
         public Object getAsObject(FacesContext context, UIComponent component, String value)
         {

            return ejbProxy.findById(Long.valueOf(value));
         }

         @Override
         public String getAsString(FacesContext context, UIComponent component, Object value)
         {

            if (value == null)
            {
               return "";
            }

            return String.valueOf(((CustomerPrice) value).getId());
         }
      };
   }

   /*
    * Support adding children to bidirectional, one-to-many tables
    */

   private CustomerPrice add = new CustomerPrice();

   public CustomerPrice getAdd()
   {
      return this.add;
   }

   public CustomerPrice getAdded()
   {
      CustomerPrice added = this.add;
      this.add = new CustomerPrice();
      return added;
   }
}