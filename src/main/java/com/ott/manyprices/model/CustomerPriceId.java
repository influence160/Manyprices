package com.ott.manyprices.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Embeddable
public class CustomerPriceId implements Serializable {

	@NotNull
	@ManyToOne(fetch=FetchType.LAZY)
	private Product product;
	@NotNull
	@ManyToOne(fetch=FetchType.LAZY)
	private Customer customer;
	
	

	public CustomerPriceId() {
	}
	
	public CustomerPriceId(Long productId, Long customerId){
		product = new Product(productId);
		customer = new Customer(customerId);
	}

	public Product getProduct(){
		return this.product;
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
	

	@Override
	public boolean equals(Object that) {
		if (this == that) {
			return true;
		}
		if (that == null) {
			return false;
		}
		if (getClass() != that.getClass()) {
			return false;
		}
		CustomerPriceId cpId2 = (CustomerPriceId) that;
		if (product != null && customer != null) {
				return product.equals(cpId2.getProduct()) && customer.equals(cpId2.getCustomer());
		}
		return super.equals(that);
	}

	@Override
	public int hashCode() {
		if (product != null) {
			return product.hashCode() ;
		}
		return super.hashCode();
	}

	
}