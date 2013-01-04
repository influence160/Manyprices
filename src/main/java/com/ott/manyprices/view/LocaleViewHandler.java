package com.ott.manyprices.view;

import java.io.IOException;
import java.util.Locale;

import javax.faces.FacesException;
import javax.faces.application.ViewHandler;
import javax.faces.application.ViewHandlerWrapper;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

public class LocaleViewHandler extends ViewHandlerWrapper {

    private final ViewHandler base;

    public LocaleViewHandler(ViewHandler base) {
        this.base = base;
    }
	    
	@Override
	public Locale calculateLocale(FacesContext context) {
		LanguageBean languageBean = (LanguageBean)context.getApplication()
	            .evaluateExpressionGet(context, "#{languageBean}", LanguageBean.class);
		if(languageBean != null){
			Locale locale = languageBean.getCurrentLocale();
			if(locale != null)
				return locale;
		}
		return context.getApplication().getDefaultLocale();
	}
	@Override
	public ViewHandler getWrapped() {
		return base;
	}

}
