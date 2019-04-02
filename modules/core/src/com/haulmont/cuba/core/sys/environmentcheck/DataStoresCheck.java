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

    @Override
    public List<CheckFailedResult> doCheck() {
        List<CheckFailedResult> result = new ArrayList<>();
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
                    DbProperties dbProperties = new DbProperties(dbMetaData.getURL());
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
                        result.add(new CheckFailedResult("Main Database checked but SEC_USER table is not found", null));
                    }
                    connection.close();
                } catch (SQLException e) {
                    result.add(new CheckFailedResult("Error connecting to main Database", e));
                } finally {
                    try {
                        if (connection != null) {
                            connection.close();
                        }
                    } catch (SQLException e) {
                        result.add(new CheckFailedResult("Exception while closing connection to main Database", e));
                    }
                }
            } else {
                connection = null;
                try {
                    connection = dataSource.getConnection();
                    connection.getMetaData();
                } catch (SQLException e) {
                    result.add(new CheckFailedResult("Error connecting to main Database", e));
                } finally {
                    try {
                        if (connection != null) {
                            connection.close();
                        }
                    } catch (SQLException e) {
                        result.add(new CheckFailedResult("Exception while closing connection to main Database", e));
                    }
                }
            }
        } catch (DataSourceLookupFailureException e) {
            result.add(new CheckFailedResult("Can not find JNDI datasource for main Data Store", e));
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
                        result.add(new CheckFailedResult(
                                String.format("Can not find JNDI datasource for additional Data Store: %s", storeName),
                                null));
                    }
                } catch (SQLException e) {
                    result.add(new CheckFailedResult(
                            String.format("Error connecting to additional Data Store: %s", storeName),
                            e));
                } finally {
                    try {
                        if (connection != null) {
                            connection.close();
                        }
                    } catch (SQLException e) {
                        result.add(new CheckFailedResult(
                                String.format("Exception while closing connection to additional Data Store: %s", storeName),
                                e));
                    }
                }
            }
        }
        return result;
    }
}
