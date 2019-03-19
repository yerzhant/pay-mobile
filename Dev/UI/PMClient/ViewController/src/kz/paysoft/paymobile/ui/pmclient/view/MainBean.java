/*
 * Copyright (C) 2010 Yerzhan Tulepov. All rights reserved.
 *
 * 2010.06.06 Yerzhan Tulepov         Created.
 */

package kz.paysoft.paymobile.ui.pmclient.view;

import javax.faces.component.UIComponent;

import oracle.adf.view.rich.component.rich.fragment.RichDeclarativeComponent;
import oracle.adf.view.rich.context.AdfFacesContext;


public class MainBean {
    private RichDeclarativeComponent contractivContracts;
    private RichDeclarativeComponent contractivAccounts;
    private RichDeclarativeComponent fxRates;
    private RichDeclarativeComponent clientsAccounts;
    private RichDeclarativeComponent clientsContracts;
    private RichDeclarativeComponent clients;
    private RichDeclarativeComponent contractAccounts;
    private RichDeclarativeComponent contracts;
    private RichDeclarativeComponent appProcessingSearch;
    private RichDeclarativeComponent systemLog;

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

    public void setContractivContracts(RichDeclarativeComponent contractivContracts) {
        this.contractivContracts = contractivContracts;
        updateButtons(contractivContracts);
    }

    public RichDeclarativeComponent getContractivContracts() {
        return contractivContracts;
    }

    public void setContractivAccounts(RichDeclarativeComponent contractivAccounts) {
        this.contractivAccounts = contractivAccounts;
        updateButtons(contractivAccounts);
    }

    public RichDeclarativeComponent getContractivAccounts() {
        return contractivAccounts;
    }

    public void setFxRates(RichDeclarativeComponent fxRates) {
        this.fxRates = fxRates;
        updateButtons(fxRates);
    }

    public RichDeclarativeComponent getFxRates() {
        return fxRates;
    }

    public void setClientsAccounts(RichDeclarativeComponent clientsAccounts) {
        this.clientsAccounts = clientsAccounts;
        updateButtons(clientsAccounts);
    }

    public RichDeclarativeComponent getClientsAccounts() {
        return clientsAccounts;
    }

    public void setClientsContracts(RichDeclarativeComponent clientsContracts) {
        this.clientsContracts = clientsContracts;
        updateButtons(clientsContracts);
    }

    public RichDeclarativeComponent getClientsContracts() {
        return clientsContracts;
    }

    public void setClients(RichDeclarativeComponent clients) {
        this.clients = clients;
        updateButtons(clients);
    }

    public RichDeclarativeComponent getClients() {
        return clients;
    }

    public void setContractAccounts(RichDeclarativeComponent contractAccounts) {
        this.contractAccounts = contractAccounts;
        updateButtons(contractAccounts);
    }

    public RichDeclarativeComponent getContractAccounts() {
        return contractAccounts;
    }

    public void setContracts(RichDeclarativeComponent contracts) {
        this.contracts = contracts;
        updateButtons(contracts);
    }

    public RichDeclarativeComponent getContracts() {
        return contracts;
    }

    public void setAppProcessingSearch(RichDeclarativeComponent appProcessingSearch) {
        this.appProcessingSearch = appProcessingSearch;
        updateButtons(appProcessingSearch);
    }

    public RichDeclarativeComponent getAppProcessingSearch() {
        return appProcessingSearch;
    }

    public void setSystemLog(RichDeclarativeComponent systemLog) {
        this.systemLog = systemLog;
        updateButtons(systemLog);
    }

    public RichDeclarativeComponent getSystemLog() {
        return systemLog;
    }
}
