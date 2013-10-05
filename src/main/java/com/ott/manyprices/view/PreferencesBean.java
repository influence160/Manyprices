package com.ott.manyprices.view;

import javax.ejb.Stateful;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

@Named
@Stateful
@ApplicationScoped
public class PreferencesBean {
    
    private boolean showPurshasePrice = false;

    public boolean isShowPurshasePrice() {
        return showPurshasePrice;
    }

    public void setShowPurshasePrice(boolean showPurshasePrice) {
        this.showPurshasePrice = showPurshasePrice;
    }
    
    public void changeShowPurshasePrices() {
	showPurshasePrice = !showPurshasePrice;
    }

}
