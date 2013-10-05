package com.ott.manyprices.standalone;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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
import com.ott.manyprices.model.Product;

@Named
@RequestScoped
@Stateful
public class MigrationBean implements Serializable{

    private static final long serialVersionUID = 1L;

    @PersistenceContext(type = PersistenceContextType.EXTENDED)
    private EntityManager em;
    
    private List<List<String>> data;

    public void migration() {
	loadDatas("donnees.txt");
	System.out.println(data);
	for (List<String> list : data) {
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
	    
	    String productName = list.get(1);
	    Product product;
	    try {
		product = findProductByName(productName);
	    } catch (NoResultException e) {
		product = createProduct(productName);
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
	return em.createNamedQuery(Customer.QUERY_FIND_BY_NAME, Customer.class)
		.setParameter("name", name).getSingleResult();
    }

	private Customer createCustomer(String cName) {
		Customer c = new Customer();
		c.setName(cName);
		return em.merge(c);
	}

    private Category findCategoryByName(String name) {
	return em.createNamedQuery(Category.QUERY_FIND_BY_NAME, Category.class)
		.setParameter("name", name).getSingleResult();
    }

	private Category createCategory(String cName) {
		Category c = new Category();
		c.setName(cName);
		return em.merge(c);
	}

	private Product findProductByName(String name) {
		return em.createNamedQuery(Product.QUERY_FIND_BY_NAME, Product.class)
				.setParameter("name", name).getSingleResult();
	}

	private Product createProduct(String cName) {
		Product c = new Product();
		c.setName(cName);
		return em.merge(c);
	}
}
