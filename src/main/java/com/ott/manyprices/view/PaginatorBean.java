package com.ott.manyprices.view;

import java.util.List;

import com.ott.manyprices.model.Product;

public interface PaginatorBean<T> {

	   public int getPage();

	   public void setPage(int page);

	   public int getPageSize();
	   
	   public List<T> getPageItems();

	   public long getCount();
	   public int getPagesCount();
}
