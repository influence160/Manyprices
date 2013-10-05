package com.ott.manyprices.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;
import javax.validation.constraints.Min;

@Entity
public class Entry {

    @Id
    private @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    Long id = null;
    @ManyToOne(fetch = FetchType.LAZY)
    private Customer customer;
    @OneToOne
    private Product product;
    @Min(1)
    private int quantitee;
    private float price;
    private Date date;

    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    public Customer getCustomer() {
	return customer;
    }

    public void setCustomer(Customer customer) {
	this.customer = customer;
    }

    public Product getProduct() {
	return product;
    }

    public void setProduct(Product product) {
	this.product = product;
    }

    public int getQuantitee() {
	return quantitee;
    }

    public void setQuantitee(int quantitee) {
	this.quantitee = quantitee;
    }

    public float getPrice() {
	return price;
    }

    public void setPrice(float price) {
	this.price = price;
    }

    @PrePersist
    private void prePersist() {
	date = new Date();
    }

    @Transient
    public float getTotalPrice() {
	return getPrice() * getQuantitee();
    }

    public Date getDate() {
	return date;
    }

    public void setDate(Date date) {
	this.date = date;
    }

    public String toString() {
	String result = "";
	if (id != null)
	    result += id;
	return result;
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
	if (id != null) {
	    return id.equals(((Entry) that).id);
	}
	return super.equals(that);
    }

    @Override
    public int hashCode() {
	if (id != null) {
	    return id.hashCode();
	}
	return super.hashCode();
    }
}
