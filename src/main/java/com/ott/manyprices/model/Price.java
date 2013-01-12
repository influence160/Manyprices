package com.ott.manyprices.model;


public abstract class Price {
     
	 public abstract Product getProduct();
     public abstract void setProduct(Product product);
     public abstract Customer getCustomer();
     public abstract void setCustomer(Customer customer);
     public abstract float getPrice();
     public abstract void setPrice(float price);
     
}
