package kz.paysoft.paymobile.ui.pmclient.model;


import java.sql.SQLException;

import kz.paysoft.paymobile.ui.pmclient.model.common.PMClientService;
import kz.paysoft.paymobile.ui.pmclient.model.views.AccountsViewRowImpl;
import kz.paysoft.paymobile.ui.pmclient.model.views.AppAccountsViewRowImpl;
import kz.paysoft.paymobile.ui.pmclient.model.views.ApplicationsViewRowImpl;
import kz.paysoft.paymobile.ui.pmclient.model.views.ClientsViewRowImpl;
import kz.paysoft.paymobile.ui.pmclient.model.views.ContractsViewRowImpl;

import oracle.jbo.JboException;
import oracle.jbo.RowIterator;
import oracle.jbo.server.ApplicationModuleImpl;
import oracle.jbo.server.ViewLinkImpl;
import oracle.jbo.server.ViewObjectImpl;

import oracle.jdbc.OracleCallableStatement;


// ---------------------------------------------------------------------
// ---    File generated by Oracle ADF Business Components Design Time.
// ---    Tue Jun 08 17:21:55 BDST 2010
// ---    Custom code may be added to this class.
// ---    Warning: Do not modify method signatures of generated methods.
// ---------------------------------------------------------------------
public class PMClientServiceImpl extends ApplicationModuleImpl implements PMClientService {
    /**
     * This is the default constructor (do not remove).
     */
    public PMClientServiceImpl() {
    }

    public void getClientAndContract() {
        ApplicationsViewRowImpl app = (ApplicationsViewRowImpl)getApplications().getCurrentRow();
        if (app != null && app.getStatus().equals("P")) {
            app.setStatus("A");
            ViewObjectImpl appClient = getAppClient();
            appClient.setWhereClause("Client.COUNTRY_ID$ = " + app.getCountryId() + " AND Client.DOC_TYPE_ID$ = " + app.getDocTypeId() +
                                     " AND Client.DOC_ID = " + app.getDocId());
            appClient.executeQuery();
            if (appClient.first() == null) {
                ClientsViewRowImpl client = (ClientsViewRowImpl)appClient.createRow();
                appClient.insertRow(client);
                client.setCountryId(app.getCountryId());
                client.setDocTypeId(app.getDocTypeId());
                client.setDocId(app.getDocId());
                client.setBranchId(app.getBranchId());
                client.setLastName(app.getLastName());
                client.setFirstName(app.getFirstName());
                client.setBirthDate(app.getBirthDate());
                client.setDocDate(app.getDocDate());
                client.setDocIssuer(app.getDocIssuer());
                client.setAddress(app.getAddress());
                client.setMiddleName(app.getMiddleName());
                client.setTaxPayerId(app.getTaxPayerId());
            }

            ContractsViewRowImpl contract = (ContractsViewRowImpl)getAppContract().createRow();
            getAppContract().insertRow(contract);
            contract.setAppliedAt(app.getAppliedAt());
            contract.setPhoneNumber(app.getPhoneNumber());
            contract.setCipherSuite(app.getCipherSuite());
            contract.setSi(app.getId());

            RowIterator appAccounts = app.getAppAccountsView();
            while (appAccounts.hasNext()) {
                AppAccountsViewRowImpl appAccount = (AppAccountsViewRowImpl)appAccounts.next();
                AccountsViewRowImpl account = (AccountsViewRowImpl)getAppAppAccounts().createRow();
                getAppAppAccounts().insertRow(account);
                account.setKind(appAccount.getKind());
                account.setAnInterface(appAccount.getAnInterface());
                account.setANumber(appAccount.getANumber());
                account.setName(appAccount.getName());
                account.setPriority(appAccount.getPriority());
                account.setSingleAmount(appAccount.getSingleAmount());
                account.setSingleCurrency(appAccount.getSingleCurrency());
                account.setDayAmount(appAccount.getDayAmount());
                account.setDayCurrency(appAccount.getDayCurrency());
                account.setDayQuantity(appAccount.getDayQuantity());
                account.setMonthAmount(appAccount.getMonthAmount());
                account.setMonthCurrency(appAccount.getMonthCurrency());
                account.setMonthQuantity(appAccount.getMonthQuantity());
                account.setFromHour(appAccount.getFromHour());
                account.setFromMin(appAccount.getFromMin());
                account.setToHour(appAccount.getToHour());
                account.setToMin(appAccount.getToMin());
            }
        } else {
            throw new JboException(MessageBundle.class, MessageBundle.OPERATION_NOT_PERMITTED, null);
        }
    }

    public String getCurrentAppStatus() {
        ApplicationsViewRowImpl app = (ApplicationsViewRowImpl)getApplications().getCurrentRow();
        return app == null ? "" : app.getStatus();
    }

    public void activateContract() {
        ContractsViewRowImpl contract = (ContractsViewRowImpl)getContracts().getCurrentRow();
        if (contract == null) {
            return;
        }
        if (contract.getStatus().equals("A")) {
            throw new JboException(MessageBundle.class, MessageBundle.CONTRACT_ALREADY_APPROVED, null);
        }
        contract.setStatus("A");
    }

    public void activateContractAndNotify() {
        ContractsViewRowImpl contract = (ContractsViewRowImpl)getContracts().getCurrentRow();
        if (contract == null) {
            return;
        }
        activateContract();
        OracleCallableStatement stmt =
            (OracleCallableStatement)getDBTransaction().createCallableStatement("BEGIN pay_mobile.pm_client_pkg.create_notification(?, ?, ?); END;",
                                                                                0);
        try {
            stmt.setNUMBER("p_phone_number", contract.getPhoneNumber());
            stmt.setNUMBER("p_csi", contract.getSi());
            stmt.setNUMBER("p_contract_id$", contract.getId().getSequenceNumber());
            stmt.execute();
        } catch (SQLException e) {
            throw new JboException(e);
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    throw new JboException(e);
                }
            }
        }
    }

    /**
     * Container's getter for FxRates.
     * @return FxRates
     */
    public ViewObjectImpl getFxRates() {
        return (ViewObjectImpl)findViewObject("FxRates");
    }

    /**
     * Container's getter for CClients.
     * @return CClients
     */
    public ViewObjectImpl getCClients() {
        return (ViewObjectImpl)findViewObject("CClients");
    }

    /**
     * Container's getter for CHClients.
     * @return CHClients
     */
    public ViewObjectImpl getCHClients() {
        return (ViewObjectImpl)findViewObject("CHClients");
    }

    /**
     * Container's getter for DClients.
     * @return DClients
     */
    public ViewObjectImpl getDClients() {
        return (ViewObjectImpl)findViewObject("DClients");
    }

    /**
     * Container's getter for DHClients.
     * @return DHClients
     */
    public ViewObjectImpl getDHClients() {
        return (ViewObjectImpl)findViewObject("DHClients");
    }

    /**
     * Container's getter for CContracts.
     * @return CContracts
     */
    public ViewObjectImpl getCContracts() {
        return (ViewObjectImpl)findViewObject("CContracts");
    }

    /**
     * Container's getter for CHContracts.
     * @return CHContracts
     */
    public ViewObjectImpl getCHContracts() {
        return (ViewObjectImpl)findViewObject("CHContracts");
    }

    /**
     * Container's getter for DContracts.
     * @return DContracts
     */
    public ViewObjectImpl getDContracts() {
        return (ViewObjectImpl)findViewObject("DContracts");
    }

    /**
     * Container's getter for DHContracts.
     * @return DHContracts
     */
    public ViewObjectImpl getDHContracts() {
        return (ViewObjectImpl)findViewObject("DHContracts");
    }

    /**
     * Container's getter for DAccounts.
     * @return DAccounts
     */
    public ViewObjectImpl getDAccounts() {
        return (ViewObjectImpl)findViewObject("DAccounts");
    }

    /**
     * Container's getter for DHAccounts.
     * @return DHAccounts
     */
    public ViewObjectImpl getDHAccounts() {
        return (ViewObjectImpl)findViewObject("DHAccounts");
    }

    /**
     * Container's getter for CAccounts.
     * @return CAccounts
     */
    public ViewObjectImpl getCAccounts() {
        return (ViewObjectImpl)findViewObject("CAccounts");
    }

    /**
     * Container's getter for CHAccounts.
     * @return CHAccounts
     */
    public ViewObjectImpl getCHAccounts() {
        return (ViewObjectImpl)findViewObject("CHAccounts");
    }

    /**
     * Container's getter for CFxRates.
     * @return CFxRates
     */
    public ViewObjectImpl getCFxRates() {
        return (ViewObjectImpl)findViewObject("CFxRates");
    }

    /**
     * Container's getter for CHFxRates.
     * @return CHFxRates
     */
    public ViewObjectImpl getCHFxRates() {
        return (ViewObjectImpl)findViewObject("CHFxRates");
    }

    /**
     * Container's getter for DFxRates.
     * @return DFxRates
     */
    public ViewObjectImpl getDFxRates() {
        return (ViewObjectImpl)findViewObject("DFxRates");
    }

    /**
     * Container's getter for DHFxRates.
     * @return DHFxRates
     */
    public ViewObjectImpl getDHFxRates() {
        return (ViewObjectImpl)findViewObject("DHFxRates");
    }

    /**
     * Container's getter for Applications.
     * @return Applications
     */
    public ViewObjectImpl getApplications() {
        return (ViewObjectImpl)findViewObject("Applications");
    }

    /**
     * Container's getter for AppAccounts.
     * @return AppAccounts
     */
    public ViewObjectImpl getAppAccounts() {
        return (ViewObjectImpl)findViewObject("AppAccounts");
    }

    /**
     * Container's getter for Clients.
     * @return Clients
     */
    public ViewObjectImpl getClients() {
        return (ViewObjectImpl)findViewObject("Clients");
    }

    /**
     * Container's getter for Contracts.
     * @return Contracts
     */
    public ViewObjectImpl getClientContracts() {
        return (ViewObjectImpl)findViewObject("ClientContracts");
    }

    /**
     * Container's getter for Accounts.
     * @return Accounts
     */
    public ViewObjectImpl getClientAccounts() {
        return (ViewObjectImpl)findViewObject("ClientAccounts");
    }

    /**
     * Container's getter for FromClientSrs.
     * @return FromClientSrs
     */
    public ViewObjectImpl getClientSrs() {
        return (ViewObjectImpl)findViewObject("ClientSrs");
    }

    /**
     * Container's getter for Srs.
     * @return Srs
     */
    public ViewObjectImpl getSrs() {
        return (ViewObjectImpl)findViewObject("Srs");
    }

    /**
     * Container's getter for Messages.
     * @return Messages
     */
    public ViewObjectImpl getMessages() {
        return (ViewObjectImpl)findViewObject("Messages");
    }

    /**
     * Container's getter for SrsSystemLog.
     * @return SrsSystemLog
     */
    public ViewObjectImpl getSrsSystemLog() {
        return (ViewObjectImpl)findViewObject("SrsSystemLog");
    }

    /**
     * Container's getter for FromClientSystemLog.
     * @return FromClientSystemLog
     */
    public ViewObjectImpl getClientSystemLog() {
        return (ViewObjectImpl)findViewObject("ClientSystemLog");
    }

    /**
     * Container's getter for ActiveSrs.
     * @return ActiveSrs
     */
    public ViewObjectImpl getActiveSrs() {
        return (ViewObjectImpl)findViewObject("ActiveSrs");
    }

    /**
     * Container's getter for FromActiveSrsSrs.
     * @return FromActiveSrsSrs
     */
    public ViewObjectImpl getActiveSrsSrs() {
        return (ViewObjectImpl)findViewObject("ActiveSrsSrs");
    }

    /**
     * Container's getter for SystemLog.
     * @return SystemLog
     */
    public ViewObjectImpl getSystemLog() {
        return (ViewObjectImpl)findViewObject("SystemLog");
    }

    /**
     * Container's getter for AppClient.
     * @return AppClient
     */
    public ViewObjectImpl getAppClient() {
        return (ViewObjectImpl)findViewObject("AppClient");
    }

    /**
     * Container's getter for AppContract.
     * @return AppContract
     */
    public ViewObjectImpl getAppContract() {
        return (ViewObjectImpl)findViewObject("AppContract");
    }

    /**
     * Container's getter for ClientsLink1.
     * @return ClientsLink1
     */
    public ViewLinkImpl getClientsLink1() {
        return (ViewLinkImpl)findViewLink("ClientsLink1");
    }

    /**
     * Container's getter for ClientsLink2.
     * @return ClientsLink2
     */
    public ViewLinkImpl getClientsLink2() {
        return (ViewLinkImpl)findViewLink("ClientsLink2");
    }

    /**
     * Container's getter for ContractsLink1.
     * @return ContractsLink1
     */
    public ViewLinkImpl getContractsLink1() {
        return (ViewLinkImpl)findViewLink("ContractsLink1");
    }

    /**
     * Container's getter for ContractsLink2.
     * @return ContractsLink2
     */
    public ViewLinkImpl getContractsLink2() {
        return (ViewLinkImpl)findViewLink("ContractsLink2");
    }

    /**
     * Container's getter for AccountsLink1.
     * @return AccountsLink1
     */
    public ViewLinkImpl getAccountsLink1() {
        return (ViewLinkImpl)findViewLink("AccountsLink1");
    }

    /**
     * Container's getter for AccountsToContractLink1.
     * @return AccountsToContractLink1
     */
    public ViewLinkImpl getAccountsToContractLink1() {
        return (ViewLinkImpl)findViewLink("AccountsToContractLink1");
    }

    /**
     * Container's getter for AccountsLink2.
     * @return AccountsLink2
     */
    public ViewLinkImpl getAccountsLink2() {
        return (ViewLinkImpl)findViewLink("AccountsLink2");
    }

    /**
     * Container's getter for FxRatesLink1.
     * @return FxRatesLink1
     */
    public ViewLinkImpl getFxRatesLink1() {
        return (ViewLinkImpl)findViewLink("FxRatesLink1");
    }

    /**
     * Container's getter for FxRatesLink2.
     * @return FxRatesLink2
     */
    public ViewLinkImpl getFxRatesLink2() {
        return (ViewLinkImpl)findViewLink("FxRatesLink2");
    }

    /**
     * Container's getter for AppAccountsToApplicationLink1.
     * @return AppAccountsToApplicationLink1
     */
    public ViewLinkImpl getAppAccountsToApplicationLink1() {
        return (ViewLinkImpl)findViewLink("AppAccountsToApplicationLink1");
    }

    /**
     * Container's getter for ContractsToClientLink1.
     * @return ContractsToClientLink1
     */
    public ViewLinkImpl getContractsToClientLink1() {
        return (ViewLinkImpl)findViewLink("ContractsToClientLink1");
    }

    /**
     * Container's getter for AccountsToContractLink2.
     * @return AccountsToContractLink2
     */
    public ViewLinkImpl getAccountsToContractLink2() {
        return (ViewLinkImpl)findViewLink("AccountsToContractLink2");
    }

    /**
     * Container's getter for SrsToContractLink1.
     * @return SrsToContractLink1
     */
    public ViewLinkImpl getSrsToContractLink1() {
        return (ViewLinkImpl)findViewLink("SrsToContractLink1");
    }

    /**
     * Container's getter for MessagesToSrLink1.
     * @return MessagesToSrLink1
     */
    public ViewLinkImpl getMessagesToSrLink1() {
        return (ViewLinkImpl)findViewLink("MessagesToSrLink1");
    }

    /**
     * Container's getter for SystemLogToSrLink1.
     * @return SystemLogToSrLink1
     */
    public ViewLinkImpl getSystemLogToSrLink1() {
        return (ViewLinkImpl)findViewLink("SystemLogToSrLink1");
    }

    /**
     * Container's getter for SystemLogToSrLink2.
     * @return SystemLogToSrLink2
     */
    public ViewLinkImpl getSystemLogToSrLink2() {
        return (ViewLinkImpl)findViewLink("SystemLogToSrLink2");
    }

    /**
     * Container's getter for ActiveSrToSrLink1.
     * @return ActiveSrToSrLink1
     */
    public ViewLinkImpl getActiveSrToSrLink1() {
        return (ViewLinkImpl)findViewLink("ActiveSrToSrLink1");
    }

    /**
     * Container's getter for ContractsToClientLink2.
     * @return ContractsToClientLink2
     */
    public ViewLinkImpl getContractsToClientLink2() {
        return (ViewLinkImpl)findViewLink("ContractsToClientLink2");
    }

    /**
     * Container's getter for AppAppAccounts.
     * @return AppAppAccounts
     */
    public ViewObjectImpl getAppAppAccounts() {
        return (ViewObjectImpl)findViewObject("AppAppAccounts");
    }

    /**
     * Container's getter for AccountsToContractLink3.
     * @return AccountsToContractLink3
     */
    public ViewLinkImpl getAccountsToContractLink3() {
        return (ViewLinkImpl)findViewLink("AccountsToContractLink3");
    }

    /**
     * Container's getter for Contracts.
     * @return Contracts
     */
    public ViewObjectImpl getContracts() {
        return (ViewObjectImpl)findViewObject("Contracts");
    }

    /**
     * Container's getter for ContractClients.
     * @return ContractClients
     */
    public ViewObjectImpl getContractClients() {
        return (ViewObjectImpl)findViewObject("ContractClients");
    }

    /**
     * Container's getter for ContractsToClientLink3.
     * @return ContractsToClientLink3
     */
    public ViewLinkImpl getContractsToClientLink3() {
        return (ViewLinkImpl)findViewLink("ContractsToClientLink3");
    }

    /**
     * Container's getter for ContractAccounts.
     * @return ContractAccounts
     */
    public ViewObjectImpl getContractAccounts() {
        return (ViewObjectImpl)findViewObject("ContractAccounts");
    }

    /**
     * Container's getter for AccountsToContractLink4.
     * @return AccountsToContractLink4
     */
    public ViewLinkImpl getAccountsToContractLink4() {
        return (ViewLinkImpl)findViewLink("AccountsToContractLink4");
    }

    /**
     * Container's getter for ContractSrs.
     * @return ContractSrs
     */
    public ViewObjectImpl getContractSrs() {
        return (ViewObjectImpl)findViewObject("ContractSrs");
    }

    /**
     * Container's getter for SrsToContractLink2.
     * @return SrsToContractLink2
     */
    public ViewLinkImpl getSrsToContractLink2() {
        return (ViewLinkImpl)findViewLink("SrsToContractLink2");
    }

    /**
     * Container's getter for ContractSystemLog.
     * @return ContractSystemLog
     */
    public ViewObjectImpl getContractSystemLog() {
        return (ViewObjectImpl)findViewObject("ContractSystemLog");
    }

    /**
     * Container's getter for SystemLogToSrLink3.
     * @return SystemLogToSrLink3
     */
    public ViewLinkImpl getSystemLogToSrLink3() {
        return (ViewLinkImpl)findViewLink("SystemLogToSrLink3");
    }
}
