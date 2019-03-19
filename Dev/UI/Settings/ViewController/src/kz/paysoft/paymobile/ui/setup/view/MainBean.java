/*
 * Copyright (C) 2010 Yerzhan Tulepov. All rights reserved.
 *
 * 2010.06.03 Yerzhan Tulepov         Created.
 */

package kz.paysoft.paymobile.ui.setup.view;

import javax.faces.component.UIComponent;

import oracle.adf.view.rich.component.rich.fragment.RichDeclarativeComponent;
import oracle.adf.view.rich.context.AdfFacesContext;


public class MainBean {

    private RichDeclarativeComponent interfaces;
    private RichDeclarativeComponent currencies;
    private RichDeclarativeComponent countries;
    private RichDeclarativeComponent docTypes;
    private RichDeclarativeComponent statusDetails;
    private RichDeclarativeComponent globalParams;
    private RichDeclarativeComponent cipherSuites;
    private RichDeclarativeComponent srTypes;
    private RichDeclarativeComponent srTypeCodes;

    public MainBean() {
    }

    private void updateButtons(RichDeclarativeComponent tableDef) {
        UIComponent commit = tableDef.findComponent("dc_pc1:dc_commit");
        UIComponent rollback = tableDef.findComponent("dc_pc1:dc_rollback");
        UIComponent rowsCount = tableDef.findComponent("dc_pc1:dc_rowsCount");
        if (commit != null) {
            AdfFacesContext.getCurrentInstance().addPartialTarget(commit);
        }
        if (rollback != null) {
            AdfFacesContext.getCurrentInstance().addPartialTarget(rollback);
        }
        if (rowsCount != null) {
            AdfFacesContext.getCurrentInstance().addPartialTarget(rowsCount);
        }
    }

    /**
     * @param interfaces
     */
    public void setInterfaces(RichDeclarativeComponent interfaces) {
        this.interfaces = interfaces;
        updateButtons(interfaces);
    }

    /**
     * @return
     */
    public RichDeclarativeComponent getInterfaces() {
        return interfaces;
    }

    /**
     * @param currencies
     */
    public void setCurrencies(RichDeclarativeComponent currencies) {
        this.currencies = currencies;
        updateButtons(currencies);
    }

    /**
     * @return
     */
    public RichDeclarativeComponent getCurrencies() {
        return currencies;
    }

    /**
     * @param countries
     */
    public void setCountries(RichDeclarativeComponent countries) {
        this.countries = countries;
        updateButtons(countries);
    }

    /**
     * @return
     */
    public RichDeclarativeComponent getCountries() {
        return countries;
    }

    /**
     * @param docTypes
     */
    public void setDocTypes(RichDeclarativeComponent docTypes) {
        this.docTypes = docTypes;
        updateButtons(docTypes);
    }

    /**
     * @return
     */
    public RichDeclarativeComponent getDocTypes() {
        return docTypes;
    }

    /**
     * @param statusDetails
     */
    public void setStatusDetails(RichDeclarativeComponent statusDetails) {
        this.statusDetails = statusDetails;
        updateButtons(statusDetails);
    }

    /**
     * @return
     */
    public RichDeclarativeComponent getStatusDetails() {
        return statusDetails;
    }

    /**
     * @param globalParams
     */
    public void setGlobalParams(RichDeclarativeComponent globalParams) {
        this.globalParams = globalParams;
        updateButtons(globalParams);
    }

    /**
     * @return
     */
    public RichDeclarativeComponent getGlobalParams() {
        return globalParams;
    }

    /**
     * @param cipherSuites
     */
    public void setCipherSuites(RichDeclarativeComponent cipherSuites) {
        this.cipherSuites = cipherSuites;
        updateButtons(cipherSuites);
    }

    /**
     * @return
     */
    public RichDeclarativeComponent getCipherSuites() {
        return cipherSuites;
    }

    /**
     * @param srTypes
     */
    public void setSrTypes(RichDeclarativeComponent srTypes) {
        this.srTypes = srTypes;
        updateButtons(srTypes);
    }

    /**
     * @return
     */
    public RichDeclarativeComponent getSrTypes() {
        return srTypes;
    }

    /**
     * @param srTypeCodes
     */
    public void setSrTypeCodes(RichDeclarativeComponent srTypeCodes) {
        this.srTypeCodes = srTypeCodes;
        updateButtons(srTypeCodes);
    }

    /**
     * @return
     */
    public RichDeclarativeComponent getSrTypeCodes() {
        return srTypeCodes;
    }
}
