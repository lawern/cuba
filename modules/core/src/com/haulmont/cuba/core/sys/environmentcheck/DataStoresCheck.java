/*
 * Copyright (c) 2008-2019 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.haulmont.cuba.core.sys.environmentcheck;

import com.haulmont.cuba.core.sys.AppContext;
import com.haulmont.cuba.core.sys.dbupdate.DbProperties;
import com.haulmont.cuba.core.sys.persistence.DbmsSpecificFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.DataSourceLookupFailureException;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DataStoresCheck implements EnvironmentCheck {
    protected static final Logger log = LoggerFactory.getLogger(DataStoresCheck.class);

    @Override
    public List<String> doCheck() {
        List<String> result = new ArrayList<>();
        JndiDataSourceLookup lookup = new JndiDataSourceLookup();
        DataSource dataSource;
        Connection connection;
        String mainDsJndiName = AppContext.getProperty("cuba.dataSourceJndiName");
        try {
            dataSource = lookup.getDataSource(mainDsJndiName == null ? "jdbc/CubaDS" : mainDsJndiName);

            if (!Boolean.TRUE.equals(Boolean.valueOf(AppContext.getProperty("cuba.automaticDatabaseUpdate")))) {
                connection = null;
                try {
                    connection = dataSource.getConnection();
                    DatabaseMetaData dbMetaData = connection.getMetaData();
                    DbProperties dbProperties = new DbProperties(getConnectionUrl(connection));
                    boolean isSchemaByUser = DbmsSpecificFactory.getDbmsFeatures().isSchemaByUser();
                    String schemaName = isSchemaByUser ?
                            dbMetaData.getUserName() : dbProperties.getCurrentSchemaProperty();
                    ResultSet tables = dbMetaData.getTables(null, schemaName, "%", null);
                    boolean found = false;
                    while (tables.next()) {
                        String tableName = tables.getString("TABLE_NAME");
                        if ("SEC_USER".equalsIgnoreCase(tableName)) {
                            found = true;
                        }
                    }
                    if (!found) {
                        result.add("Database checked but SEC_USER table is not found");
                    }
                    connection.close();
                } catch (SQLException e) {
                    result.add("Error connecting to main Database");
                } finally {
                    try {
                        if (connection != null) {
                            connection.close();
                        }
                    } catch (SQLException e) {
                        result.add("Exception while closing the Database connection");
                    }
                }
            } else {
                connection = null;
                try {
                    connection = dataSource.getConnection();
                    connection.getMetaData();
                } catch (SQLException e) {
                    result.add("Error connecting to main Database");
                } finally {
                    try {
                        if (connection != null) {
                            connection.close();
                        }
                    } catch (SQLException e) {
                        result.add("Exception while closing the Database connection");
                    }
                }
            }
        } catch (DataSourceLookupFailureException e) {
            result.add("Can not find JNDI datasource for main Data Store");
        }

        String additionalStores = AppContext.getProperty("cuba.additionalStores");
        if (additionalStores != null) {
            for (String storeName : additionalStores.replaceAll("\\s", "").split(",")) {
                connection = null;
                String storeJndiName = AppContext.getProperty("cuba.dataSourceJndiName_" + storeName);
                try {
                    dataSource = lookup.getDataSource(storeJndiName == null ? "" : storeJndiName);
                    connection = dataSource.getConnection();
                    connection.getMetaData();
                } catch (DataSourceLookupFailureException e) {
                    String beanName = AppContext.getProperty("cuba.storeImpl_" + storeName);
                    if (beanName == null) {
                        result.add(String.format("Can not find JNDI datasource for additional Data Store: %s", storeName));
                    }
                } catch (SQLException e) {
                    result.add(String.format("Error connecting to additional Data Store: %s", storeName));
                } finally {
                    try {
                        if (connection != null) {
                            connection.close();
                        }
                    } catch (SQLException e) {
                        result.add(String.format("Exception while closing connection to additional Data Store: %s", storeName));
                    }
                }
            }
        }
        return result;
    }

    protected static String getConnectionUrl(Connection connection) {
        try {
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            return databaseMetaData.getURL();
        } catch (Throwable e) {
            return null;
        }
    }
}
