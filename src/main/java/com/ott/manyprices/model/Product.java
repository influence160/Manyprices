package com.ott.manyprices.model;

import javax.persistence.Entity;
import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.Size;

import java.lang.Override;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Entity
@NamedQueries({
    @NamedQuery(name=Product.QUERY_FIND_BY_NAME, query="FROM Product p where p.name = :name"),
    @NamedQuery(name=Product.QUERY_GET_ALL, query="SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.purchasePrice" +
    		" LEFT JOIN FETCH p.sellItems s" + 
    		" LEFT JOIN FETCH p.entries e" +
    		" order by p.name asc")
	
})
public class Product implements Serializable
{

    public static final String QUERY_FIND_BY_NAME = "findProductByName";
    public static final String QUERY_GET_ALL = "getAllProducts";
   
   @Id
   private @GeneratedValue(strategy = GenerationType.AUTO)
   @Column(name = "id", updatable = false, nullable = false)
   Long id = null;
   @Version
   private @Column(name = "version")
   int version = 0;
   @Column(unique=true)
   private String name;
   private String dimention;
   private String description;
   @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "product")
   private ProductPrice purchasePrice = new ProductPrice();
   @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "id.product")
   private Set<CustomerPrice> prices = new HashSet<CustomerPrice>();
   @ManyToOne(fetch = FetchType.LAZY)
   private Category category;
   private Date dateUpdated;
   @OneToMany(cascade = { CascadeType.REMOVE }, fetch = FetchType.LAZY, mappedBy = "product")
   private Set<SellItem> sellItems = new HashSet<SellItem>();
   @OneToMany(cascade = { CascadeType.REMOVE }, fetch = FetchType.LAZY, mappedBy = "product")
   private Set<Entry> entries = new HashSet<Entry>();
   private int quantitee;
   @Column(length=1024)
   @Size(max=1024)
   private String note;
   
   
   public Product(){
       purchasePrice.setProduct(this);
   }

   public Product(Long id) {
	   this();
	   this.id = id;
   }

   @PreUpdate
   @PrePersist
   private void prePersist()
   {
      dateUpdated = new Date();
   }

   public Long getId()
   {
      return this.id;
   }

   public void setId(final Long id)
   {
      this.id = id;
   }

   public int getVersion()
   {
      return this.version;
   }

   public void setVersion(final int version)
   {
      this.version = version;
   }

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public String getDimention()
   {
      return dimention;
   }

   public void setDimention(String dimention)
   {
      this.dimention = dimention;
   }

   public String getDescription()
   {
      return description;
   }

   public void setDescription(String description)
   {
      this.description = description;
   }

   public ProductPrice getPurchasePrice() 
   {
      return purchasePrice;
   }

   public void setPurchasePrice(ProductPrice purchasePrice) 
   {
	  purchasePrice.setProduct(this);
	  this.purchasePrice = purchasePrice;
   }
   
   public void setPurchasePrice(CustomerPrice purchasePrice) 
   {
       if(this.purchasePrice == null) {
	   ProductPrice productPrice = new ProductPrice(purchasePrice);
	   setPurchasePrice(productPrice);
       }
       else {
	   this.purchasePrice.setCustomer(purchasePrice.getCustomer());
	   this.purchasePrice.setPrice(purchasePrice.getPrice());
       }
   }

   public Set<CustomerPrice> getPrices() 
   {
      return prices;
   }

   public void setPrices(Set<CustomerPrice> prices)
   {
      this.prices = prices;
   }

   public void addPrice(CustomerPrice customerPrice) {
	prices.add(customerPrice);
	customerPrice.setProduct(this);
   }
    
   public Category getCategory()
   {
      return category;
   }

   public void setCategory(Category category)
   {
      this.category = category;
   }

   public Date getDateUpdated()
   {
      return dateUpdated;
   }

   public void setQuantitee(int quantitee) {
	  this.quantitee = quantitee;
   }

   public int getQuantitee() {
	  return quantitee;
   }
   
//   public int getQuantiteeEnStock() {
//       int sotck = this.quantitee;
//       for (SellItem sellItem : sellItems) {
////	   if (this.getDateUpdated().after(sellItem.getDate())) {
//	       sotck -= sellItem.getQuantitee();
////	   }
//       }
//       for (Entry entry : entries) {
////	   if (this.getDateUpdated().after(entry.getDate())) {
//	       sotck += entry.getQuantitee();
////	   }
//       }
//       return sotck;
//   }
   
  // public void setQuantiteeEnStock(int quantiteeEnStock) {
  //     this.quantitee = quantiteeEnStock;
  // }
   
   @Transient
   public float getTotalPrice() {
	   return getPurchasePrice().getPrice() * getQuantitee();
   }
	
   public String getNote() {
	  return note;
   }

   public void setNote(String note) {
	  this.note = note;
   }
   
   public float getMinPrice(){
	   if(prices == null || prices.isEmpty()){
		   return 0;
	   }
	   Iterator<CustomerPrice> i = prices.iterator();
	   float min = i.next().getPrice();
	   while(i.hasNext()){
		   CustomerPrice cp = i.next();
		   if(cp.getPrice() < min)
			   min = cp.getPrice();
	   }
	   return min;
   }
   
   public Set<Customer> getCurrentSellers(){
	   Set<Customer> customers = new HashSet<Customer>();
	   for(CustomerPrice cp : prices){
		   if(cp.getCustomer() != null)
			   customers.add(cp.getCustomer());
	   }
	   return customers;
   }

   public String toString()
   {
      String result = "";
      if (id != null)
         result += id;
      return result;
   }

   @Override
   public boolean equals(Object that)
   {
      if (this == that)
      {
         return true;
      }
      if (that == null)
      {
         return false;
      }
      if (getClass() != that.getClass())
      {
         return false;
      }
      if (id != null)
      {
         return id.equals(((Product) that).id);
      }
      return super.equals(that);
   }

   @Override
   public int hashCode()
   {
      if (id != null)
      {
         return id.hashCode();
      }
      return super.hashCode();
   }

}