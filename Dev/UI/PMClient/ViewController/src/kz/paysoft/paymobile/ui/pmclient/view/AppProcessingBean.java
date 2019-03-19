/*
 * Copyright (C) 2010 Yerzhan Tulepov. All rights reserved.
 *
 * 2010.06.09 Yerzhan Tulepov         Created.
 */

package kz.paysoft.paymobile.ui.pmclient.view;

import javax.faces.context.FacesContext;

import kz.paysoft.paymobile.ui.pmclient.model.common.PMClientService;

import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.view.rich.component.rich.data.RichTable;


public final class AppProcessingBean {
    private DCBindingContainer bindings;
    private boolean cantGetClientAndContract;
    private RichTable searchTable;

    public AppProcessingBean() {
    }

    private DCBindingContainer getBindings() {
        if (bindings == null) {
            FacesContext fc = FacesContext.getCurrentInstance();
            bindings = (DCBindingContainer)fc.getApplication().evaluateExpressionGet(fc, "#{bindings}", DCBindingContainer.class);
        }
        return bindings;
    }

    public final void setCantGetClientAndContract(boolean canGetClientAndContract) {
        this.cantGetClientAndContract = canGetClientAndContract;
    }

    public final boolean isCantGetClientAndContract() {
        return cantGetClientAndContract;
    }

    public void setSearchTable(RichTable searchTable) {
        this.searchTable = searchTable;
        PMClientService service = (PMClientService)getBindings().findIteratorBinding("ApplicationsIterator").getDataControl().getDataProvider();
        if (service.getCurrentAppStatus().equals("P")) {
            cantGetClientAndContract = false;
        } else {
            cantGetClientAndContract = true;
        }
    }

    public RichTable getSearchTable() {
        return searchTable;
    }
}
