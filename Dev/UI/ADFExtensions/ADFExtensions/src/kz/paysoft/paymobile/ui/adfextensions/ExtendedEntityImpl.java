/*
 * Copyright (C) 2010 Yerzhan Tulepov. All rights reserved.
 *
 * 2010.05.23 Yerzhan Tulepov         Created.
 */

package kz.paysoft.paymobile.ui.adfextensions;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import oracle.adf.share.ADFContext;

import oracle.jbo.AttributeDef;
import oracle.jbo.JboException;
import oracle.jbo.domain.DBSequence;
import oracle.jbo.domain.Date;
import oracle.jbo.server.AttributeDefImpl;
import oracle.jbo.server.EntityImpl;
import oracle.jbo.server.TransactionEvent;

import oracle.jdbc.OracleResultSet;


public class ExtendedEntityImpl extends EntityImpl {
    public ExtendedEntityImpl() {
        super();
    }

    @Override
    public void remove() {
        if (getAttribute("Status") != null && !getAttribute("Status").equals("C")) {
            throw new JboException("Can''t delete a history record.");
        }
        try {
            OracleResultSet rset = (OracleResultSet)getDBTransaction().createStatement(1).executeQuery("SELECT SYSDATE FROM DUAL");
            rset.next();
            setAttribute("Date", new Date(rset.getDATE(1)));
        } catch (SQLException e) {
            throw new JboException(e);
        }
        setAttribute("Status", "D");
        //setAttribute("User", ((SessionImpl)getDBTransaction().getSession()).getUserPrincipalName()); // Bug 9546149.
        setAttribute("User", ADFContext.getCurrent().getSecurityContext().getUserName());
        setAttribute("Hu", ((DBSequence)getAttribute("Id")).getSequenceNumber());
        super.remove();
    }

    @Override
    protected void doDML(int operation, TransactionEvent transactionEvent) {
        if (operation == DML_UPDATE && !getAttribute("Status").equals("C")) {
            throw new JboException("Can''t update a history record.");
        }
        boolean deleting = false;
        if (operation == DML_DELETE) {
            operation = DML_UPDATE;
            deleting = true;
        }
        Statement stmt = null;
        OracleResultSet rset = null;
        PreparedStatement pstmt = null;
        try {
            if (operation == DML_UPDATE) {
                StringBuilder sql = new StringBuilder("SELECT");
                String comma = "";
                for (AttributeDef attr : getStructureDef().getAttributeDefs()) {
                    if (attr.getAttributeKind() == AttributeDef.ATTR_PERSISTENT && !attr.getColumnName().equals("ID$") && !attr.getColumnName().equals("STATUS$") &&
                        !attr.getColumnName().equals("HU$") && !attr.getColumnName().equals("CID$")) {
                        sql.append(comma + " " + attr.getColumnName());
                        comma = ",";
                    }
                }
                sql.append(", SYSDATE FROM " + getEntityDef().getSource() + " WHERE ID$ = " + getAttribute("Id")); // TODO: Maybe it is necessary to add + " FOR UPDATE".
                try {
                    stmt = getDBTransaction().createStatement(1);
                    rset = (OracleResultSet)stmt.executeQuery(sql.toString());
                    rset.next();
                    sql = new StringBuilder("INSERT INTO " + getEntityDef().getSource() + "(ID$, STATUS$, HU$, CID$");
                    for (AttributeDef attr : getStructureDef().getAttributeDefs()) {
                        if (attr.getAttributeKind() == AttributeDef.ATTR_PERSISTENT && !attr.getColumnName().equals("ID$") && !attr.getColumnName().equals("STATUS$") &&
                            !attr.getColumnName().equals("HU$") && !attr.getColumnName().equals("CID$")) {
                            sql.append(", " + attr.getColumnName());
                        }
                    }
                    sql.append(") VALUES (" + getEntityDef().getSource() + "_SEQ.NEXTVAL, 'H', " + getEntityDef().getSource() + "_SEQ.CURRVAL, " + getAttribute("Id"));
                    for (AttributeDef attr : getStructureDef().getAttributeDefs()) {
                        if (attr.getAttributeKind() == AttributeDef.ATTR_PERSISTENT && !attr.getColumnName().equals("ID$") && !attr.getColumnName().equals("STATUS$") &&
                            !attr.getColumnName().equals("HU$") && !attr.getColumnName().equals("CID$")) {
                            sql.append(", ?");
                        }
                    }
                    sql.append(")");
                    pstmt = getDBTransaction().createPreparedStatement(sql.toString(), 0);
                    int i = 1;
                    for (AttributeDef attr : getStructureDef().getAttributeDefs()) {
                        if (attr.getAttributeKind() == AttributeDef.ATTR_PERSISTENT && !attr.getColumnName().equals("ID$") && !attr.getColumnName().equals("STATUS$") &&
                            !attr.getColumnName().equals("HU$") && !attr.getColumnName().equals("CID$")) {
                            pstmt.setObject(i, rset.getObject(i), attr.getSQLType());
                            i++;
                        }
                    }
                    if (!deleting) {
                        setAttribute("Date", new Date(rset.getDATE(i)));
                    }
                } catch (SQLException e) {
                    throw new JboException(e);
                }
            }
            super.doDML(operation, transactionEvent);
            if (operation == DML_UPDATE) {
                if (pstmt != null) {
                    try {
                        pstmt.executeUpdate();
                    } catch (SQLException e) {
                        throw new JboException(e);
                    }
                }
            }
        } finally {
            try {
                if (rset != null) {
                    rset.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                throw new JboException(e);
            }
        }
    }

    @Override
    protected Object getHistoryContextForAttribute(AttributeDefImpl attributeDefImpl) { // Bug 9546149.
        if (attributeDefImpl.getHistoryKind() == AttributeDefImpl.HISTORY_MODIFY_USER) {
            return ADFContext.getCurrent().getSecurityContext().getUserName();
        } else {
            return super.getHistoryContextForAttribute(attributeDefImpl);
        }
    }
}

