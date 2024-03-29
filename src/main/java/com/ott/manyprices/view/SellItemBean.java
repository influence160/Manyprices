package com.ott.manyprices.view;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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

import com.ott.manyprices.model.Product;
import com.ott.manyprices.model.ProductPrice;
import com.ott.manyprices.model.SellItem;

/**
 * Backing bean for SellItem entities.
 * <p>
 * This class provides CRUD functionality for all SellItem entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD
 * framework or custom base class.
 */

@Named
@Stateful
@ConversationScoped
public class SellItemBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /*
     * Support creating and retrieving SellItem entities
     */

    public SellItemBean() {
	Calendar today = Calendar.getInstance();
	dateAfter = dateFormat.format(today.getTime());
	today.add(Calendar.DAY_OF_MONTH, 1);
	dateBefore = dateFormat.format(today.getTime());
    }

    private Long id;
    private Long productId;

    public Long getId() {
	return this.id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    private SellItem sellItem;

    public SellItem getSellItem() {
	return this.sellItem;
    }

    @Inject
    private Conversation conversation;

    @PersistenceContext(type = PersistenceContextType.EXTENDED)
    private EntityManager entityManager;

    public String create() {

	this.conversation.begin();
	return "create?faces-redirect=true";
    }

    public void retrieve() {

	if (FacesContext.getCurrentInstance().isPostback()) {
	    return;
	}

	if (this.conversation.isTransient()) {
	    this.conversation.begin();
	}

	if (this.id == null) {
	    this.sellItem = this.example;
	} else {
	    this.sellItem = findById(getId());
	}
	
	if (this.productId != null) {
	    Product pr = entityManager.find(Product.class, productId);
	    this.sellItem.setProduct(pr);
	    this.sellItem.setUnitPrice(pr.getPurchasePrice().getPrice() * 135 / 100);
	}
    }

    public SellItem findById(Long id) {

	return this.entityManager.find(SellItem.class, id);
    }

    /*
     * Support updating and deleting SellItem entities
     */

    public String update() {
	this.conversation.end();

	try {
	    if (this.id == null) {
		this.entityManager.persist(this.sellItem);
		return "search?faces-redirect=true";
	    } else {
		this.entityManager.merge(this.sellItem);
		return "view?faces-redirect=true&id=" + this.sellItem.getId();
	    }
	} catch (Exception e) {
	    FacesContext.getCurrentInstance().addMessage(null,
		    new FacesMessage(e.getMessage()));
	    return null;
	}
    }

    public String delete() {
	this.conversation.end();

	try {
	    this.entityManager.remove(findById(getId()));
	    this.entityManager.flush();
	    return "search?faces-redirect=true";
	} catch (Exception e) {
	    FacesContext.getCurrentInstance().addMessage(null,
		    new FacesMessage(e.getMessage()));
	    return null;
	}
    }

    /*
     * Support searching SellItem entities with pagination
     */

    private int page;
    private int pageSize = 100;
    private long count;
    private int quantitees;
    private double totalPrices;
    private List<SellItem> pageItems;

    private SellItem example = new SellItem();
    private static final String DATE_PATTERN = "dd-MM-yyyy";
    private static final DateFormat dateFormat = new SimpleDateFormat(
	    DATE_PATTERN);
    private String dateBefore;
    private String dateAfter;

    public int getPage() {
	return this.page;
    }

    public void setPage(int page) {
	this.page = page;
    }

    public int getPageSize() {
	return pageSize;
    }

    public void setPageSize(int pageSize) {
	this.pageSize = pageSize;
    }

    public SellItem getExample() {
	return this.example;
    }

    public void setExample(SellItem example) {
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

    public void search() {
	this.page = 0;
    }

    public void paginate() throws ParseException {

	CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();

	// Populate this.count
	CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
	Root<SellItem> root = countCriteria.from(SellItem.class);
	countCriteria = countCriteria.select(builder.count(root)).where(
		getSearchPredicates(root));
	this.count = this.entityManager.createQuery(countCriteria)
		.getSingleResult();

//	 Populate this.pageItems
	 CriteriaQuery<SellItem> criteria =
	 builder.createQuery(SellItem.class);
	 root = criteria.from(SellItem.class);
	 TypedQuery<SellItem> query =
	 this.entityManager.createQuery(criteria.select(root).where(getSearchPredicates(root)));
	 query.setFirstResult(this.page *
	 getPageSize()).setMaxResults(getPageSize());
	 this.pageItems = query.getResultList();

//	String jpq = "SELECT s FROM SellItem s "
//		+ " LEFT JOIN FETCH s.product p"
//		+ " LEFT JOIN FETCH p.purchasePrice" + " WHERE 1=1 ";
//	String where = "";
//	if (dateAfter != null && !dateAfter.trim().isEmpty()) {
//	    where += " AND s.date >= :dateAfter";
//	}
//	if (dateBefore != null && !dateBefore.trim().isEmpty()) {
//	    where += " AND s.date <= :dateBefore";
//	}
//	Product product = this.example.getProduct();
//	if (product != null) {
//	    where += " AND s.product = :product";
//	}
//
//	jpq += where;
//	TypedQuery<SellItem> query = this.entityManager.createQuery(jpq,
//		SellItem.class);
//
//	if (dateAfter != null && !dateAfter.trim().isEmpty()) {
//	    Date dateA = dateFormat.parse(dateAfter);
//	    query.setParameter("dateAfter", dateA);
//	}
//	if (dateBefore != null && !dateBefore.trim().isEmpty()) {
//	    Date dateB = dateFormat.parse(dateBefore);
//	    query.setParameter("dateBefore", dateB);
//	}
//	if (product != null) {
//	    query.setParameter("product", product);
//	}
//	this.pageItems = query.getResultList();

	if (this.count > 0) {
	    CriteriaQuery<Object> totalPricesCriteria = builder
		    .createQuery(Object.class);
	    root = totalPricesCriteria.from(SellItem.class);
	    totalPricesCriteria = totalPricesCriteria.select(
		    builder.sum(builder.prod(root.<Double> get("unitPrice"),
			    root.<Integer> get("quantitee")))).where(
		    getSearchPredicates(root));
	    TypedQuery<Object> tq = this.entityManager
		    .createQuery(totalPricesCriteria);
	    this.totalPrices = (Double) tq.getSingleResult();
	    
	    CriteriaQuery<Integer> quantiteesCriteria = builder.createQuery(Integer.class);
	    root = quantiteesCriteria.from(SellItem.class);
	    quantiteesCriteria = quantiteesCriteria.select(builder.sum(root.<Integer> get("quantitee"))).where(
		getSearchPredicates(root));
	    this.quantitees = this.entityManager.createQuery(quantiteesCriteria)
		.getSingleResult();
	} else {
	    this.totalPrices = 0;
	}
    }

    private Predicate[] getSearchPredicates(Root<SellItem> root) {

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
	String remarque = this.example.getRemarque();
	if (remarque != null && !remarque.isEmpty())
	{
	    predicatesList.add(builder.like(root.<String> get("remarque"), '%' + remarque + '%'));
	}
	Product product = this.example.getProduct();
	if (product != null) {
	    predicatesList.add(builder.equal(root.get("product"), product));
	}

	return predicatesList.toArray(new Predicate[predicatesList.size()]);
    }

    private String getSearchWhere() {
	String where = "";
	if (dateAfter != null && !dateAfter.trim().isEmpty()) {
	    // Date dateA = dateFormat.parse(dateAfter);
	    where += " date AFTER :dateAfter";
	}
	if (dateBefore != null && !dateBefore.trim().isEmpty()) {
	    // Date dateB = DATEFORMAT.parse(dateBefore);
	    where += " date BEFORE :dateAfter";
	}
	Product product = this.example.getProduct();
	if (product != null) {
	    where += " s.product = :product";
	}

	return where;
    }

    public List<SellItem> getPageItems() {
	return this.pageItems;
    }

    public long getCount() {
	return this.count;
    }

    public double getTotalPrices() {
        return this.totalPrices;
    }

    public int getQuantitees() {
        return quantitees;
    }
    
    /*
     * Support listing and POSTing back SellItem entities (e.g. from inside an
     * HtmlSelectOneMenu)
     */

    public List<SellItem> getAll() {

	CriteriaQuery<SellItem> criteria = this.entityManager
		.getCriteriaBuilder().createQuery(SellItem.class);
	return this.entityManager.createQuery(
		criteria.select(criteria.from(SellItem.class))).getResultList();
    }

    @Resource
    private SessionContext sessionContext;

    public Converter getConverter() {

	final SellItemBean ejbProxy = this.sessionContext
		.getBusinessObject(SellItemBean.class);

	return new Converter() {

	    @Override
	    public Object getAsObject(FacesContext context,
		    UIComponent component, String value) {

		return ejbProxy.findById(Long.valueOf(value));
	    }

	    @Override
	    public String getAsString(FacesContext context,
		    UIComponent component, Object value) {

		if (value == null) {
		    return "";
		}

		return String.valueOf(((SellItem) value).getId());
	    }
	};
    }

    /*
     * Support adding children to bidirectional, one-to-many tables
     */

    private SellItem add = new SellItem();

    public SellItem getAdd() {
	return this.add;
    }

    public SellItem getAdded() {
	SellItem added = this.add;
	this.add = new SellItem();
	return added;
    }

    public static void main(String[] args) {
	System.out.println(dateFormat.format(new Date()));
    }

}