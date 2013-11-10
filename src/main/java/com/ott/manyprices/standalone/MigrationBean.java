package com.ott.manyprices.standalone;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import com.ott.manyprices.model.Category;
import com.ott.manyprices.model.Customer;
import com.ott.manyprices.model.CustomerPrice;
import com.ott.manyprices.model.Entry;
import com.ott.manyprices.model.Product;
import com.ott.manyprices.model.SellItem;

@Named
@RequestScoped
@Stateful
public class MigrationBean implements Serializable{

    private static final long serialVersionUID = 1L;
    
    NumberFormat numberFormat = NumberFormat.getInstance();

    @PersistenceContext(type = PersistenceContextType.EXTENDED)
    private EntityManager em;
    
    private List<List<String>> data;
    
    private String fileName;

    public void migration() throws ParseException {
	loadDatas("donnees.txt");
	System.out.println(data);
	for (List<String> list : data) {
	    System.out.println("--------------- ligne " + list);
	    String customerName = list.get(4);
	    Customer customer;
	    try {
		customer = findCustomerByName(customerName);
	    } catch (NoResultException e) {
		customer = createCustomer(customerName);
	    }
	    
	    String categoryName = list.get(1);
	    Category category;
	    try {
		category = findCategoryByName(categoryName);
	    } catch (NoResultException e) {
		category = createCategory(categoryName);
	    }
	    
	    String productName = list.get(3);
	    Product product;
	    try {
		product = findProductByName(productName);
	    } catch (NoResultException e) {
		product = createProduct(productName);
	    }
	    
	    category.addProduct(product);
	    product.setQuantitee(Integer.parseInt(list.get(7)));
	    
	    CustomerPrice customerPrice = new CustomerPrice();
	    customerPrice.setCustomer(customer);
	    customerPrice.setProduct(product);
	    customerPrice.setPrice(Float.parseFloat(list.get(8).replaceFirst(",", ".")));
	    customerPrice = em.merge(customerPrice);
	    product.addPrice(customerPrice);
	    product.setPurchasePrice(customerPrice);
	    
	    for (Entry entry : customer.getEntries()) {
		em.remove(entry);
	    }
	    
	    Entry entry = new Entry();
	    entry.setCustomer(customer);
	    entry.setProduct(product);
	    entry.setPrice(Float.parseFloat(list.get(8).replaceFirst(",", ".")));
	    entry.setQuantitee(Integer.parseInt(list.get(5)));
	    customer.addEntry(entry);
	    
	    String sortie = list.get(6);
	    SellItem sellItem = null;
	    if (sortie != null && !sortie.isEmpty() && !"0".equals(sortie)) {
		sellItem = new SellItem();
		sellItem.setProduct(product);
		sellItem.setQuantitee(Integer.parseInt(sortie));
		em.merge(sellItem);
	    }
	}
    }


    private void loadDatas(String fileName) {
	try {
	    data = new ArrayList<List<String>>();
	    InputStream input = MigrationBean.class.getClassLoader()
		    .getResourceAsStream(fileName);
	    BufferedReader br = new BufferedReader(new InputStreamReader(input));
	    String ligne;
	    while ((ligne = br.readLine()) != null) {
		List<String> l = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(ligne, "	");
		String token;
		while (st.hasMoreTokens()) {
		    token = st.nextToken();
		    l.add(token);
		}
		data.add(l);
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	    throw new RuntimeException(e.getMessage(), e);
	}
    }

    private Customer findCustomerByName(String name) {
	return em.merge(em.createNamedQuery(Customer.QUERY_FIND_BY_NAME, Customer.class)
		.setParameter("name", name).getSingleResult());
    }

    private Customer createCustomer(String cName) {
	Customer c = new Customer();
	c.setName(cName);
	return em.merge(c);
//	return c;
    }

    private Category findCategoryByName(String name) {
	return em.merge(em.createNamedQuery(Category.QUERY_FIND_BY_NAME, Category.class)
		.setParameter("name", name).getSingleResult());
    }

    private Category createCategory(String cName) {
	Category c = new Category();
	c.setName(cName);
	return em.merge(c);
//	return c;
    }
    
    private Product findProductByName(String name) {
	return em.merge(em.createNamedQuery(Product.QUERY_FIND_BY_NAME, Product.class)
		.setParameter("name", name).getSingleResult());
    }

    private Product createProduct(String cName) {
	Product c = new Product();
	c.setName(cName);
	return em.merge(c);
//	return c;
    }

    public String getFileName() {
        return fileName;
    }


    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    
}
