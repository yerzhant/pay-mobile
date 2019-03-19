/*
 * Copyright (C) 2010 Yerzhan Tulepov. All rights reserved.
 *
 * 2010.05.21   Yerzhan Tulepov         Created.
 */

package kz.paysoft.paymobile.ui.appentry.model;

import java.io.IOException;
import java.io.InputStream;

import java.util.Properties;

import oracle.mds.core.MetadataObject;
import oracle.mds.core.RestrictedSession;
import oracle.mds.cust.CacheHint;
import oracle.mds.cust.CustomizationClass;

public class CustomerCC extends CustomizationClass {
    private static final String name = "customer";

    public CustomerCC() {
        super();
    }

    /**
     * @return
     */
    public CacheHint getCacheHint() {
        return CacheHint.ALL_USERS;
    }

    /**
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * @param restrictedSession
     * @param metadataObject
     * @return
     */
    public String[] getValue(RestrictedSession restrictedSession, MetadataObject metadataObject) {
        Properties properties = new Properties();
        String configuredValue = null;
        InputStream is = CustomerCC.class.getResourceAsStream("/customization.properties");
        if (is != null) {
            try {
                properties.load(is);
                String propValue = properties.getProperty(name);
                configuredValue = propValue;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new String[] { configuredValue };
    }
}
