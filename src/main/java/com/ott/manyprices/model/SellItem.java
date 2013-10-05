package com.ott.manyprices.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Entity
public class SellItem {

    @Id
    private @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    Long id = null;
    @OneToOne(optional=false)
    @NotNull
    private Product product;
    @Min(1)
    private int quantitee;
    private Date date;

    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
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

    public Date getDate() {
	return date;
    }

    public void setDate(Date date) {
	this.date = date;
    }

    @PrePersist
    private void prePersist() {
	date = new Date();
    }
    
}
