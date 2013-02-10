package com.ott.manyprices.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

@Entity
@AssociationOverrides({
		@AssociationOverride(name = "id.product", joinColumns = @JoinColumn(name = "product_id")),
		@AssociationOverride(name = "id.sideB", joinColumns = @JoinColumn(name = "customer_id")) })
public class CustomerPrice extends Price implements Serializable {

	@EmbeddedId
	private @Column(name = "id", updatable = false, nullable = false)
	CustomerPriceId id = new CustomerPriceId();
	@Version
	private @Column(name = "version")
	int version = 0;
	@javax.validation.constraints.Digits(integer = 12 ,fraction = 3)
	private float price;
    private Date dateUpdated;
    
    @PreUpdate
    @PrePersist
    private void prePersist()
    {
       dateUpdated = new Date();
    }
	public CustomerPriceId getId() {
		return id;
	}

	public void setId(CustomerPriceId id) {
		this.id = id;
	}

	public int getVersion() {
		return this.version;
	}

	public void setVersion(final int version) {
		this.version = version;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public Date getDateUpdated() {
		return dateUpdated;
	}

	public void setDateUpdated(Date dateUpdated) {
		this.dateUpdated = dateUpdated;
	}

	@NotNull
	@Transient
	public Product getProduct() {
		return this.id.getProduct();
	}

	public void setProduct(Product product) {
		this.id.setProduct(product);
	}

	@NotNull
	@Transient
	public Customer getCustomer() {
		return id.getCustomer();
	}

	public void setCustomer(Customer customer) {
		this.id.setCustomer(customer);
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
			return id.equals(((CustomerPrice) that).getId());
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