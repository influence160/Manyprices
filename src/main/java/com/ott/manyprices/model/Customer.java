package com.ott.manyprices.model;

import javax.persistence.Entity;
import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.lang.Override;
import java.util.HashSet;
import java.util.Set;

@Entity
@NamedQueries({ 
    @NamedQuery(name = Customer.QUERY_FIND_BY_NAME, query = "FROM Customer c where c.name = :name")
})
public class Customer implements Serializable {

    public static final String QUERY_FIND_BY_NAME = "findCustomerByName";

    @Id
    private @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    Long id = null;
    @Version
    private @Column(name = "version")
    int version = 0;
    @NotNull
    @Size(min = 3, max = 30, message = "De 3 a 30 caractères")
    private String name;
    private String description;
    private String telephone;
    @OneToMany(mappedBy = "id.customer", cascade = CascadeType.ALL, fetch=FetchType.LAZY)
    private Set<CustomerPrice> prices = new HashSet<CustomerPrice>();
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "customer", cascade = { CascadeType.ALL })
    private Set<Entry> entries = new HashSet<Entry>();

    public Customer() {

    }

    public Customer(Long id) {
	this.id = id;
    }

    public Long getId() {
	return this.id;
    }

    public void setId(final Long id) {
	this.id = id;
    }

    public int getVersion() {
	return this.version;
    }

    public void setVersion(final int version) {
	this.version = version;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    public String getTelephone() {
	return telephone;
    }

    public void setTelephone(String telephone) {
	this.telephone = telephone;
    }

    public Set<CustomerPrice> getPrices() {
	return prices;
    }

    public void setPrices(Set<CustomerPrice> prices) {
	this.prices = prices;
    }

    public Set<Entry> getEntries() {
	return entries;
    }

    public void setEntries(Set<Entry> entries) {
	this.entries = entries;
    }
    
    public void addEntry(Entry entry) {
	this.entries.add(entry);
	entry.setCustomer(this);
    }

    public void removeEntry(Entry entry) {
	this.entries.remove(entry);
	entry.setCustomer(null);
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
	    return id.equals(((Customer) that).id);
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