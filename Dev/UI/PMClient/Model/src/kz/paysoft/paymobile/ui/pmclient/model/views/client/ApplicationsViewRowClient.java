package kz.paysoft.paymobile.ui.pmclient.model.views.client;

import kz.paysoft.paymobile.ui.pmclient.model.views.common.ApplicationsViewRow;

import oracle.jbo.client.remote.RowImpl;
// ---------------------------------------------------------------------
// ---    File generated by Oracle ADF Business Components Design Time.
// ---    Tue Jun 08 17:58:09 BDST 2010
// ---    Custom code may be added to this class.
// ---    Warning: Do not modify method signatures of generated methods.
// ---------------------------------------------------------------------
public class ApplicationsViewRowClient extends RowImpl implements ApplicationsViewRow {
    /**
     * This is the default constructor (do not remove).
     */
    public ApplicationsViewRowClient() {
    }

    public void prepareToDecline() {
        Object _ret = getApplicationModuleProxy().riInvokeExportedMethod(this,"prepareToDecline",null,null);
        return;
    }
}
