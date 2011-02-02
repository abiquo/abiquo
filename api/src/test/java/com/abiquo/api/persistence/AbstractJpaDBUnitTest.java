/**
 * Abiquo community edition
 * cloud management application for hybrid clouds
 * Copyright (C) 2008-2010 - Abiquo Holdings S.L.
 *
 * This application is free software; you can redistribute it and/or
 * modify it under the terms of the GNU LESSER GENERAL PUBLIC
 * LICENSE as published by the Free Software Foundation under
 * version 3 of the License
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * LESSER GENERAL PUBLIC LICENSE v.3 for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package com.abiquo.api.persistence;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.sql.DataSource;

import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import com.abiquo.api.common.BooleanTypeFactory;

@TestExecutionListeners( {DependencyInjectionTestExecutionListener.class,
TransactionalTestExecutionListener.class})
@ContextConfiguration(locations = {"classpath:springresources/applicationContext-test.xml"})
public abstract class AbstractJpaDBUnitTest extends AbstractTransactionalTestNGSpringContextTests
{
    @Autowired
    private DataSource dataSource;

    protected DataSource getDataSource()
    {
        return this.dataSource;
    }

    @BeforeTransaction
    public void onSetUpInTransaction() throws Exception
    {
        DataSource ds = getDataSource();
        Connection conn = ds.getConnection();
        try
        {
            deleteIntermediateTables(conn);

            IDatabaseConnection connection = new DatabaseConnection(conn);
            connection.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY,
                new BooleanTypeFactory());

            DatabaseOperation operation = DatabaseOperation.CLEAN_INSERT;

            executeOperation(connection, operation);
        }
        finally
        {
            DataSourceUtils.releaseConnection(conn, ds);
        }
    }

    private void deleteIntermediateTables(Connection conn) throws Exception
    {
        if (intermediateTables() != null)
        {
            Statement stmt = conn.createStatement();
            for (String table : intermediateTables())
            {
                try
                {
                    stmt.execute("delete from " + table);
                }
                catch (SQLException e)
                {
                    // ignoring not found
                }
            }
        }
    }

    protected List<String> intermediateTables()
    {
        return null;
    }

    private void executeOperation(IDatabaseConnection connection, DatabaseOperation operation)
        throws Exception
    {
        for (String data : data())
        {
            InputStream flatXml = getClass().getResourceAsStream(data);
            operation.execute(connection, new FlatXmlDataSet(flatXml, false));
        }
    }

    protected abstract List<String> data();
}
