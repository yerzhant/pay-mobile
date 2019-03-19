package kz.paysoft.paymobile.ui.pmclient.model.views;

import oracle.jbo.RowSet;
import oracle.jbo.domain.DBSequence;
import oracle.jbo.domain.Number;
import oracle.jbo.server.AttributeDefImpl;
import oracle.jbo.server.EntityImpl;
import oracle.jbo.server.ViewRowImpl;
// ---------------------------------------------------------------------
// ---    File generated by Oracle ADF Business Components Design Time.
// ---    Tue Jun 08 22:49:48 BDST 2010
// ---    Custom code may be added to this class.
// ---    Warning: Do not modify method signatures of generated methods.
// ---------------------------------------------------------------------
public class AppAccountsViewRowImpl extends ViewRowImpl {
    /**
     * AttributesEnum: generated enum for identifying attributes and accessors. Do not modify.
     */
    public enum AttributesEnum {
        Id {
            public Object get(AppAccountsViewRowImpl obj) {
                return obj.getId();
            }

            public void put(AppAccountsViewRowImpl obj, Object value) {
                obj.setId((DBSequence)value);
            }
        }
        ,
        ApplicationId {
            public Object get(AppAccountsViewRowImpl obj) {
                return obj.getApplicationId();
            }

            public void put(AppAccountsViewRowImpl obj, Object value) {
                obj.setApplicationId((Number)value);
            }
        }
        ,
        Kind {
            public Object get(AppAccountsViewRowImpl obj) {
                return obj.getKind();
            }

            public void put(AppAccountsViewRowImpl obj, Object value) {
                obj.setKind((String)value);
            }
        }
        ,
        AnInterface {
            public Object get(AppAccountsViewRowImpl obj) {
                return obj.getAnInterface();
            }

            public void put(AppAccountsViewRowImpl obj, Object value) {
                obj.setAnInterface((String)value);
            }
        }
        ,
        ANumber {
            public Object get(AppAccountsViewRowImpl obj) {
                return obj.getANumber();
            }

            public void put(AppAccountsViewRowImpl obj, Object value) {
                obj.setANumber((String)value);
            }
        }
        ,
        Name {
            public Object get(AppAccountsViewRowImpl obj) {
                return obj.getName();
            }

            public void put(AppAccountsViewRowImpl obj, Object value) {
                obj.setName((String)value);
            }
        }
        ,
        Priority {
            public Object get(AppAccountsViewRowImpl obj) {
                return obj.getPriority();
            }

            public void put(AppAccountsViewRowImpl obj, Object value) {
                obj.setPriority((Number)value);
            }
        }
        ,
        SingleAmount {
            public Object get(AppAccountsViewRowImpl obj) {
                return obj.getSingleAmount();
            }

            public void put(AppAccountsViewRowImpl obj, Object value) {
                obj.setSingleAmount((Number)value);
            }
        }
        ,
        SingleCurrency {
            public Object get(AppAccountsViewRowImpl obj) {
                return obj.getSingleCurrency();
            }

            public void put(AppAccountsViewRowImpl obj, Object value) {
                obj.setSingleCurrency((String)value);
            }
        }
        ,
        DayAmount {
            public Object get(AppAccountsViewRowImpl obj) {
                return obj.getDayAmount();
            }

            public void put(AppAccountsViewRowImpl obj, Object value) {
                obj.setDayAmount((Number)value);
            }
        }
        ,
        DayCurrency {
            public Object get(AppAccountsViewRowImpl obj) {
                return obj.getDayCurrency();
            }

            public void put(AppAccountsViewRowImpl obj, Object value) {
                obj.setDayCurrency((String)value);
            }
        }
        ,
        DayQuantity {
            public Object get(AppAccountsViewRowImpl obj) {
                return obj.getDayQuantity();
            }

            public void put(AppAccountsViewRowImpl obj, Object value) {
                obj.setDayQuantity((Number)value);
            }
        }
        ,
        MonthAmount {
            public Object get(AppAccountsViewRowImpl obj) {
                return obj.getMonthAmount();
            }

            public void put(AppAccountsViewRowImpl obj, Object value) {
                obj.setMonthAmount((Number)value);
            }
        }
        ,
        MonthCurrency {
            public Object get(AppAccountsViewRowImpl obj) {
                return obj.getMonthCurrency();
            }

            public void put(AppAccountsViewRowImpl obj, Object value) {
                obj.setMonthCurrency((String)value);
            }
        }
        ,
        MonthQuantity {
            public Object get(AppAccountsViewRowImpl obj) {
                return obj.getMonthQuantity();
            }

            public void put(AppAccountsViewRowImpl obj, Object value) {
                obj.setMonthQuantity((Number)value);
            }
        }
        ,
        FromHour {
            public Object get(AppAccountsViewRowImpl obj) {
                return obj.getFromHour();
            }

            public void put(AppAccountsViewRowImpl obj, Object value) {
                obj.setFromHour((Number)value);
            }
        }
        ,
        FromMin {
            public Object get(AppAccountsViewRowImpl obj) {
                return obj.getFromMin();
            }

            public void put(AppAccountsViewRowImpl obj, Object value) {
                obj.setFromMin((Number)value);
            }
        }
        ,
        ToHour {
            public Object get(AppAccountsViewRowImpl obj) {
                return obj.getToHour();
            }

            public void put(AppAccountsViewRowImpl obj, Object value) {
                obj.setToHour((Number)value);
            }
        }
        ,
        ToMin {
            public Object get(AppAccountsViewRowImpl obj) {
                return obj.getToMin();
            }

            public void put(AppAccountsViewRowImpl obj, Object value) {
                obj.setToMin((Number)value);
            }
        }
        ,
        AccountKinds {
            public Object get(AppAccountsViewRowImpl obj) {
                return obj.getAccountKinds();
            }

            public void put(AppAccountsViewRowImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        Interfaces {
            public Object get(AppAccountsViewRowImpl obj) {
                return obj.getInterfaces();
            }

            public void put(AppAccountsViewRowImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        Currencies {
            public Object get(AppAccountsViewRowImpl obj) {
                return obj.getCurrencies();
            }

            public void put(AppAccountsViewRowImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ;
        private static AttributesEnum[] vals = null;
        private static int firstIndex = 0;

        public abstract Object get(AppAccountsViewRowImpl object);

        public abstract void put(AppAccountsViewRowImpl object, Object value);

        public int index() {
            return AttributesEnum.firstIndex() + ordinal();
        }

        public static int firstIndex() {
            return firstIndex;
        }

        public static int count() {
            return AttributesEnum.firstIndex() + AttributesEnum.staticValues().length;
        }

        public static AttributesEnum[] staticValues() {
            if (vals == null) {
                vals = AttributesEnum.values();
            }
            return vals;
        }
    }
    public static final int ID = AttributesEnum.Id.index();
    public static final int APPLICATIONID = AttributesEnum.ApplicationId.index();
    public static final int KIND = AttributesEnum.Kind.index();
    public static final int ANINTERFACE = AttributesEnum.AnInterface.index();
    public static final int ANUMBER = AttributesEnum.ANumber.index();
    public static final int NAME = AttributesEnum.Name.index();
    public static final int PRIORITY = AttributesEnum.Priority.index();
    public static final int SINGLEAMOUNT = AttributesEnum.SingleAmount.index();
    public static final int SINGLECURRENCY = AttributesEnum.SingleCurrency.index();
    public static final int DAYAMOUNT = AttributesEnum.DayAmount.index();
    public static final int DAYCURRENCY = AttributesEnum.DayCurrency.index();
    public static final int DAYQUANTITY = AttributesEnum.DayQuantity.index();
    public static final int MONTHAMOUNT = AttributesEnum.MonthAmount.index();
    public static final int MONTHCURRENCY = AttributesEnum.MonthCurrency.index();
    public static final int MONTHQUANTITY = AttributesEnum.MonthQuantity.index();
    public static final int FROMHOUR = AttributesEnum.FromHour.index();
    public static final int FROMMIN = AttributesEnum.FromMin.index();
    public static final int TOHOUR = AttributesEnum.ToHour.index();
    public static final int TOMIN = AttributesEnum.ToMin.index();
    public static final int ACCOUNTKINDS = AttributesEnum.AccountKinds.index();
    public static final int INTERFACES = AttributesEnum.Interfaces.index();
    public static final int CURRENCIES = AttributesEnum.Currencies.index();

    /**
     * This is the default constructor (do not remove).
     */
    public AppAccountsViewRowImpl() {
    }

    /**
     * Gets AppAccount entity object.
     * @return the AppAccount
     */
    public EntityImpl getAppAccount() {
        return (EntityImpl)getEntity(0);
    }

    /**
     * Gets the attribute value for ID$ using the alias name Id.
     * @return the ID$
     */
    public DBSequence getId() {
        return (DBSequence)getAttributeInternal(ID);
    }

    /**
     * Sets <code>value</code> as attribute value for ID$ using the alias name Id.
     * @param value value to set the ID$
     */
    public void setId(DBSequence value) {
        setAttributeInternal(ID, value);
    }

    /**
     * Gets the attribute value for APPLICATION_ID$ using the alias name ApplicationId.
     * @return the APPLICATION_ID$
     */
    public Number getApplicationId() {
        return (Number) getAttributeInternal(APPLICATIONID);
    }

    /**
     * Sets <code>value</code> as attribute value for APPLICATION_ID$ using the alias name ApplicationId.
     * @param value value to set the APPLICATION_ID$
     */
    public void setApplicationId(Number value) {
        setAttributeInternal(APPLICATIONID, value);
    }

    /**
     * Gets the attribute value for KIND using the alias name Kind.
     * @return the KIND
     */
    public String getKind() {
        return (String) getAttributeInternal(KIND);
    }

    /**
     * Sets <code>value</code> as attribute value for KIND using the alias name Kind.
     * @param value value to set the KIND
     */
    public void setKind(String value) {
        setAttributeInternal(KIND, value);
    }

    /**
     * Gets the attribute value for INTERFACE using the alias name AnInterface.
     * @return the INTERFACE
     */
    public String getAnInterface() {
        return (String) getAttributeInternal(ANINTERFACE);
    }

    /**
     * Sets <code>value</code> as attribute value for INTERFACE using the alias name AnInterface.
     * @param value value to set the INTERFACE
     */
    public void setAnInterface(String value) {
        setAttributeInternal(ANINTERFACE, value);
    }

    /**
     * Gets the attribute value for A_NUMBER using the alias name ANumber.
     * @return the A_NUMBER
     */
    public String getANumber() {
        return (String) getAttributeInternal(ANUMBER);
    }

    /**
     * Sets <code>value</code> as attribute value for A_NUMBER using the alias name ANumber.
     * @param value value to set the A_NUMBER
     */
    public void setANumber(String value) {
        setAttributeInternal(ANUMBER, value);
    }

    /**
     * Gets the attribute value for NAME using the alias name Name.
     * @return the NAME
     */
    public String getName() {
        return (String) getAttributeInternal(NAME);
    }

    /**
     * Sets <code>value</code> as attribute value for NAME using the alias name Name.
     * @param value value to set the NAME
     */
    public void setName(String value) {
        setAttributeInternal(NAME, value);
    }

    /**
     * Gets the attribute value for PRIORITY using the alias name Priority.
     * @return the PRIORITY
     */
    public Number getPriority() {
        return (Number) getAttributeInternal(PRIORITY);
    }

    /**
     * Sets <code>value</code> as attribute value for PRIORITY using the alias name Priority.
     * @param value value to set the PRIORITY
     */
    public void setPriority(Number value) {
        setAttributeInternal(PRIORITY, value);
    }

    /**
     * Gets the attribute value for SINGLE_AMOUNT using the alias name SingleAmount.
     * @return the SINGLE_AMOUNT
     */
    public Number getSingleAmount() {
        return (Number) getAttributeInternal(SINGLEAMOUNT);
    }

    /**
     * Sets <code>value</code> as attribute value for SINGLE_AMOUNT using the alias name SingleAmount.
     * @param value value to set the SINGLE_AMOUNT
     */
    public void setSingleAmount(Number value) {
        setAttributeInternal(SINGLEAMOUNT, value);
    }

    /**
     * Gets the attribute value for SINGLE_CURRENCY using the alias name SingleCurrency.
     * @return the SINGLE_CURRENCY
     */
    public String getSingleCurrency() {
        return (String) getAttributeInternal(SINGLECURRENCY);
    }

    /**
     * Sets <code>value</code> as attribute value for SINGLE_CURRENCY using the alias name SingleCurrency.
     * @param value value to set the SINGLE_CURRENCY
     */
    public void setSingleCurrency(String value) {
        setAttributeInternal(SINGLECURRENCY, value);
    }

    /**
     * Gets the attribute value for DAY_AMOUNT using the alias name DayAmount.
     * @return the DAY_AMOUNT
     */
    public Number getDayAmount() {
        return (Number) getAttributeInternal(DAYAMOUNT);
    }

    /**
     * Sets <code>value</code> as attribute value for DAY_AMOUNT using the alias name DayAmount.
     * @param value value to set the DAY_AMOUNT
     */
    public void setDayAmount(Number value) {
        setAttributeInternal(DAYAMOUNT, value);
    }

    /**
     * Gets the attribute value for DAY_CURRENCY using the alias name DayCurrency.
     * @return the DAY_CURRENCY
     */
    public String getDayCurrency() {
        return (String) getAttributeInternal(DAYCURRENCY);
    }

    /**
     * Sets <code>value</code> as attribute value for DAY_CURRENCY using the alias name DayCurrency.
     * @param value value to set the DAY_CURRENCY
     */
    public void setDayCurrency(String value) {
        setAttributeInternal(DAYCURRENCY, value);
    }

    /**
     * Gets the attribute value for DAY_QUANTITY using the alias name DayQuantity.
     * @return the DAY_QUANTITY
     */
    public Number getDayQuantity() {
        return (Number) getAttributeInternal(DAYQUANTITY);
    }

    /**
     * Sets <code>value</code> as attribute value for DAY_QUANTITY using the alias name DayQuantity.
     * @param value value to set the DAY_QUANTITY
     */
    public void setDayQuantity(Number value) {
        setAttributeInternal(DAYQUANTITY, value);
    }

    /**
     * Gets the attribute value for MONTH_AMOUNT using the alias name MonthAmount.
     * @return the MONTH_AMOUNT
     */
    public Number getMonthAmount() {
        return (Number) getAttributeInternal(MONTHAMOUNT);
    }

    /**
     * Sets <code>value</code> as attribute value for MONTH_AMOUNT using the alias name MonthAmount.
     * @param value value to set the MONTH_AMOUNT
     */
    public void setMonthAmount(Number value) {
        setAttributeInternal(MONTHAMOUNT, value);
    }

    /**
     * Gets the attribute value for MONTH_CURRENCY using the alias name MonthCurrency.
     * @return the MONTH_CURRENCY
     */
    public String getMonthCurrency() {
        return (String) getAttributeInternal(MONTHCURRENCY);
    }

    /**
     * Sets <code>value</code> as attribute value for MONTH_CURRENCY using the alias name MonthCurrency.
     * @param value value to set the MONTH_CURRENCY
     */
    public void setMonthCurrency(String value) {
        setAttributeInternal(MONTHCURRENCY, value);
    }

    /**
     * Gets the attribute value for MONTH_QUANTITY using the alias name MonthQuantity.
     * @return the MONTH_QUANTITY
     */
    public Number getMonthQuantity() {
        return (Number) getAttributeInternal(MONTHQUANTITY);
    }

    /**
     * Sets <code>value</code> as attribute value for MONTH_QUANTITY using the alias name MonthQuantity.
     * @param value value to set the MONTH_QUANTITY
     */
    public void setMonthQuantity(Number value) {
        setAttributeInternal(MONTHQUANTITY, value);
    }

    /**
     * Gets the attribute value for FROM_HOUR using the alias name FromHour.
     * @return the FROM_HOUR
     */
    public Number getFromHour() {
        return (Number) getAttributeInternal(FROMHOUR);
    }

    /**
     * Sets <code>value</code> as attribute value for FROM_HOUR using the alias name FromHour.
     * @param value value to set the FROM_HOUR
     */
    public void setFromHour(Number value) {
        setAttributeInternal(FROMHOUR, value);
    }

    /**
     * Gets the attribute value for FROM_MIN using the alias name FromMin.
     * @return the FROM_MIN
     */
    public Number getFromMin() {
        return (Number) getAttributeInternal(FROMMIN);
    }

    /**
     * Sets <code>value</code> as attribute value for FROM_MIN using the alias name FromMin.
     * @param value value to set the FROM_MIN
     */
    public void setFromMin(Number value) {
        setAttributeInternal(FROMMIN, value);
    }

    /**
     * Gets the attribute value for TO_HOUR using the alias name ToHour.
     * @return the TO_HOUR
     */
    public Number getToHour() {
        return (Number) getAttributeInternal(TOHOUR);
    }

    /**
     * Sets <code>value</code> as attribute value for TO_HOUR using the alias name ToHour.
     * @param value value to set the TO_HOUR
     */
    public void setToHour(Number value) {
        setAttributeInternal(TOHOUR, value);
    }

    /**
     * Gets the attribute value for TO_MIN using the alias name ToMin.
     * @return the TO_MIN
     */
    public Number getToMin() {
        return (Number) getAttributeInternal(TOMIN);
    }

    /**
     * Sets <code>value</code> as attribute value for TO_MIN using the alias name ToMin.
     * @param value value to set the TO_MIN
     */
    public void setToMin(Number value) {
        setAttributeInternal(TOMIN, value);
    }

    /**
     * Gets the view accessor <code>RowSet</code> AccountKinds.
     */
    public RowSet getAccountKinds() {
        return (RowSet)getAttributeInternal(ACCOUNTKINDS);
    }

    /**
     * Gets the view accessor <code>RowSet</code> Interfaces.
     */
    public RowSet getInterfaces() {
        return (RowSet)getAttributeInternal(INTERFACES);
    }

    /**
     * Gets the view accessor <code>RowSet</code> Currencies.
     */
    public RowSet getCurrencies() {
        return (RowSet)getAttributeInternal(CURRENCIES);
    }

    /**
     * getAttrInvokeAccessor: generated method. Do not modify.
     * @param index the index identifying the attribute
     * @param attrDef the attribute

     * @return the attribute value
     * @throws Exception
     */
    protected Object getAttrInvokeAccessor(int index, AttributeDefImpl attrDef) throws Exception {
        if ((index >= AttributesEnum.firstIndex()) && (index < AttributesEnum.count())) {
            return AttributesEnum.staticValues()[index - AttributesEnum.firstIndex()].get(this);
        }
        return super.getAttrInvokeAccessor(index, attrDef);
    }

    /**
     * setAttrInvokeAccessor: generated method. Do not modify.
     * @param index the index identifying the attribute
     * @param value the value to assign to the attribute
     * @param attrDef the attribute

     * @throws Exception
     */
    protected void setAttrInvokeAccessor(int index, Object value, AttributeDefImpl attrDef) throws Exception {
        if ((index >= AttributesEnum.firstIndex()) && (index < AttributesEnum.count())) {
            AttributesEnum.staticValues()[index - AttributesEnum.firstIndex()].put(this, value);
            return;
        }
        super.setAttrInvokeAccessor(index, value, attrDef);
    }
}