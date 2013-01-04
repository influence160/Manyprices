package com.ott.manyprices.view;

import java.io.Serializable;
import java.util.Locale;

import javax.ejb.Stateful;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;

@Named("languageBean")
@Stateful
@ApplicationScoped
public class LanguageBean implements Serializable {
	
	private static final long serialVersionUID = -2192535384516314758L;
	
	private Locale currentLocale;

	public void changeToFr(){
		FacesContext.getCurrentInstance()
		.getViewRoot().setLocale(Locale.FRENCH);
		currentLocale = Locale.FRENCH;
	}
	
	
	public void changeToEn(){
		FacesContext.getCurrentInstance()
		.getViewRoot().setLocale(Locale.ENGLISH);
		currentLocale = Locale.ENGLISH;
	}
	
	public String getCurrentLanguage(){
		return FacesContext.getCurrentInstance().getViewRoot().getLocale().getDisplayLanguage();
	}
	
	public Locale getCurrentLocale(){
		return currentLocale;
	}

}
