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
import javax.validation.constraints.Size; //import javax.validation.constraints.NotNull;
//import javax.validation.constraints.Size;

import java.lang.Override;
import java.util.HashSet;
import java.util.Set;

@Entity
@NamedQueries({
    @NamedQuery(name="findCategoryByName", query="FROM Customer c where name = :name")
	
})
public class Category implements Serializable {

   public static final String QUERY_FIND_BY_NAME = "findCategoryByName";
   
   @Id
   private @GeneratedValue(strategy = GenerationType.AUTO)
   @Column(name = "id", updatable = false, nullable = false)
   Long id = null;
   @Version
   private @Column(name = "version")
   int version = 0;
   @NotNull
   @Size(min = 3, max = 30, message = "Le nom de la catègorie doit avoir entre 3 et 30 caractères")
   @Column(unique = true, nullable = false)
   private String name;
   @OneToMany(fetch = FetchType.LAZY, mappedBy = "category", cascade={CascadeType.PERSIST})
   private Set<Product> products = new HashSet<Product>();

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

   public Set<Product> getProducts()
   {
      return products;
   }

   public void setProducts(Set<Product> products)
   {
      this.products = products;
   }

   public String toString()
   {
      String result = "[Category " + id + " name = " + name + "]";
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
         return id.equals(((Category) that).id);
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