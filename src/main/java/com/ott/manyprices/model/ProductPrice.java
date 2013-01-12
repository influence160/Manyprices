package com.ott.manyprices.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

@Entity
public class ProductPrice extends Price implements Serializable {

      @Id
      private @GeneratedValue(strategy = GenerationType.AUTO)
      @Column(name = "id", updatable = false, nullable = false)
      Long id = null;
      @Version
      private @Column(name = "version")
      int version = 0;
      @NotNull
      @OneToOne(fetch=FetchType.LAZY)
      private Product product;   
      //@NotNull
      @OneToOne(fetch=FetchType.EAGER)
      private Customer customer;
      private float price;
      
      public ProductPrice(){
    	  
      }
      
      public ProductPrice(CustomerPrice cp){
    	  this.customer = cp.getCustomer();
    	  this.product = cp.getProduct();
    	  this.price = cp.getPrice();
      }
      
      //TODO preupdate => preupdate product and customer
      public Long getId() {
         return id;
      }
      public void setId(Long id) {
         this.id = id;
      }
      public int getVersion() {
         return version;
      }
      public void setVersion(int version) {
         this.version = version;
      }
      public Product getProduct() {
         return product;
      }
      public void setProduct(Product product) {
         this.product = product;
      }
      public Customer getCustomer() {
         return customer;
      }
      public void setCustomer(Customer customer) {
         this.customer = customer;
      }
      public float getPrice() {
         return price;
      }
      public void setPrice(float price) {
         this.price = price;
      }
      
      
}
