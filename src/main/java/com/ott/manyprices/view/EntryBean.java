package com.ott.manyprices.view;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateful;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.SessionScoped;
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
import com.ott.manyprices.model.Entry;
import com.ott.manyprices.model.Product;
import com.ott.manyprices.model.SellItem;

/**
 * Backing bean for Entry entities.
 * <p>
 * This class provides CRUD functionality for all Entry entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD framework or
 * custom base class.
 */

@Named
@Stateful
@ConversationScoped
public class EntryBean implements Serializable
{

   private static final long serialVersionUID = 1L;

   /*
    * Support creating and retrieving Entry entities
    */
   
   public EntryBean() {
	Calendar today = Calendar.getInstance();
	dateAfter = dateFormat.format(today.getTime());
	today.add(Calendar.DAY_OF_MONTH, 1);
	dateBefore = dateFormat.format(today.getTime());
   }

   private Long id;

   public Long getId()
   {
      return this.id;
   }

   public void setId(Long id)
   {
      this.id = id;
   }

   private Entry entry;

   public Entry getEntry()
   {
      return this.entry;
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
         this.entry = this.example;
      }
      else
      {
         this.entry = findById(getId());
      }
   }

   public Entry findById(Long id)
   {

      return this.entityManager.find(Entry.class, id);
   }

   /*
    * Support updating and deleting Entry entities
    */

   public String update()
   {
      this.conversation.end();

      try
      {
         if (this.id == null)
         {
            this.entityManager.persist(this.entry);
            return "search?faces-redirect=true";
         }
         else
         {
            this.entityManager.merge(this.entry);
            return "view?faces-redirect=true&id=" + this.entry.getId();
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
    * Support searching Entry entities with pagination
    */

   private int page;
   private int pageSize = 100;
   private long count;
   private double totalPrices;
   private List<Entry> pageItems;

   private Entry example = new Entry();
   
   private static final String DATE_PATTERN = "dd-MM-yyyy";
   private static final DateFormat dateFormat = new SimpleDateFormat(
	    DATE_PATTERN);
   private String dateBefore;
   private String dateAfter;

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

   public Entry getExample()
   {
      return this.example;
   }

   public void setExample(Entry example)
   {
      this.example = example;
   }

   public String getDateBefore() {
	return dateBefore;
   }

   public void setDateBefore(String dateBefore) {
	this.dateBefore = dateBefore;
   }

   public String getDateAfter() {
	return dateAfter;
   }

   public void setDateAfter(String dateAfter) {
	this.dateAfter = dateAfter;
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
      Root<Entry> root = countCriteria.from(Entry.class);
      countCriteria = countCriteria.select(builder.count(root)).where(getSearchPredicates(root));
      this.count = this.entityManager.createQuery(countCriteria).getSingleResult();

      // Populate this.pageItems

      CriteriaQuery<Entry> criteria = builder.createQuery(Entry.class);
      root = criteria.from(Entry.class);
      TypedQuery<Entry> query = this.entityManager.createQuery(criteria.select(root).where(getSearchPredicates(root)));
      query.setFirstResult(this.page * getPageSize()).setMaxResults(getPageSize());
      this.pageItems = query.getResultList();
	if (this.count > 0) {
	    CriteriaQuery<Object> totalPricesCriteria = builder
		    .createQuery(Object.class);
	    root = totalPricesCriteria.from(Entry.class);
	    totalPricesCriteria = totalPricesCriteria.select(
		    builder.sum(builder.prod(root.<Double> get("price"),
			    root.<Integer> get("quantitee")))).where(
		    getSearchPredicates(root));
	    TypedQuery<Object> tq = this.entityManager
		    .createQuery(totalPricesCriteria);
	    this.totalPrices = (Double) tq.getSingleResult();
	} else {
	    this.totalPrices = 0;
	}
   }

   private Predicate[] getSearchPredicates(Root<Entry> root)
   {

      CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
      List<Predicate> predicatesList = new ArrayList<Predicate>();
      
	try {
	    if (dateAfter != null && !dateAfter.trim().isEmpty()) {
		Date dateA = dateFormat.parse(dateAfter);
		predicatesList.add(builder.greaterThanOrEqualTo(
			root.<Date> get("date"), dateA));
	    }
	    if (dateBefore != null && !dateBefore.trim().isEmpty()) {
		Date dateB = dateFormat.parse(dateBefore);
		predicatesList.add(builder.lessThanOrEqualTo(
			root.<Date> get("date"), dateB));
	    }
	} catch (ParseException e) {
	    throw new RuntimeException(e.getMessage(), e);
	}
	
      Customer customer = this.example.getCustomer();
      if (customer != null)
      {
         predicatesList.add(builder.equal(root.get("customer"), customer));
      }
      
      Product product = this.example.getProduct();
      if (product != null)
      {
         predicatesList.add(builder.equal(root.get("product"), product));
      }

      return predicatesList.toArray(new Predicate[predicatesList.size()]);
   }

   public List<Entry> getPageItems()
   {
      return this.pageItems;
   }

   public long getCount()
   {
      return this.count;
   }

   public double getTotalPrices() {
       return this.totalPrices;
   }
   
   /*
    * Support listing and POSTing back Entry entities (e.g. from inside an
    * HtmlSelectOneMenu)
    */

   public List<Entry> getAll()
   {

      CriteriaQuery<Entry> criteria = this.entityManager.getCriteriaBuilder().createQuery(Entry.class);
      return this.entityManager.createQuery(criteria.select(criteria.from(Entry.class))).getResultList();
   }

   @Resource
   private SessionContext sessionContext;

   public Converter getConverter()
   {

      final EntryBean ejbProxy = this.sessionContext.getBusinessObject(EntryBean.class);

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

            return String.valueOf(((Entry) value).getId());
         }
      };
   }

   /*
    * Support adding children to bidirectional, one-to-many tables
    */

   private Entry add = new Entry();

   public Entry getAdd()
   {
      return this.add;
   }
   
   

   public Entry getAdded()
   {
      Entry added = this.add;
      this.add = new Entry();
      return added;
   }
   
   public void removeEntry(Entry entry) {
	   entityManager.remove(entry);
   }
}