/*
 * Copyright (C) 2010 Yerzhan Tulepov. All rights reserved.
 *
 * 2010.05.28 Yerzhan Tulepov         Created.
 */

package kz.paysoft.paymobile.ui.declarativecomponents;

import javax.el.ELContext;
import javax.el.ExpressionFactory;

import javax.faces.context.FacesContext;

import oracle.adf.view.rich.component.rich.fragment.RichDeclarativeComponent;


public class TableBean {
    private static final String VIEW_OBJECT = "viewObject";
    private static final String BASE_VIEW = "baseView";
    private static final String CHANGES_OF = "ChangesOf";
    private static final String DELETED = "Deleted";
    private static final String COMPONENT = "#{component}";

    public TableBean() {
    }

    public String toChangesOf() {
        return switchTo(CHANGES_OF, VIEW_OBJECT);
    }

    public String toDeleted() {
        return switchTo(DELETED, VIEW_OBJECT);
    }

    public String toBase() {
        return switchTo("", BASE_VIEW);
    }

    private final String getAttrValue(String attrName) {
        ExpressionFactory ef = FacesContext.getCurrentInstance().getApplication().getExpressionFactory();
        ELContext ec = FacesContext.getCurrentInstance().getELContext();
        RichDeclarativeComponent rdc = (RichDeclarativeComponent)ef.createValueExpression(ec, COMPONENT, Object.class).getValue(ec);
        return (String)rdc.getAttributes().get(attrName);
    }

    private final String switchTo(String infix, String attrName) {
        return "to" + infix + getAttrValue(attrName);
    }
}
