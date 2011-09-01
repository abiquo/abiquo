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
package com.abiquo.testng;

import static com.abiquo.testng.TestConfig.getParameter;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ISuite;
import org.testng.ISuiteListener;

/**
 * Creates and destroys the test database for the current test suite.
 * 
 * @author ibarrera
 */
public class TestDatabaseListener implements ISuiteListener
{
    private static final Logger LOGGER = LoggerFactory.getLogger(TestDatabaseListener.class);

    private static final String DB_USER = "db.user";

    private static final String DB_PASS = "db.pass";

    private static final String CREATE_SCRIPT = "db.create";

    private static final String INSERT_SCRIPT = "db.insert";

    private static final String DROP_SCRIPT = "db.delete";

    private DB db;

    @Override
    public void onStart(final ISuite suite)
    {
        String user = getParameter(DB_USER, TestConfig.DEFAULT_DB_USER);
        String pass = getParameter(DB_PASS, TestConfig.DEFAULT_DB_PASS);
        long start = System.currentTimeMillis();
        LOGGER.info("Generating test database...");

        db = new DB(user, pass);
        db.execute(getParameter(CREATE_SCRIPT));
        db.execute(getParameter(INSERT_SCRIPT));

        LOGGER.info("Database created in {} milliseconds", System.currentTimeMillis() - start);
    }

    @Override
    public void onFinish(final ISuite suite)
    {
        LOGGER.info("Deleting test database...");

        long start = System.currentTimeMillis();

        db.execute(getParameter(DROP_SCRIPT));

        LOGGER.info("Database dropped in {} milliseconds", System.currentTimeMillis() - start);
    }

    private static class DB
    {
        private String user;

        private String pass;

        public DB(final String user, final String pass)
        {
            super();
            this.user = user;
            this.pass = pass;
        }

        public void execute(final String script)
        {
            String[] command =
                {"mysql", "kinton_test", "--host=localhost", "--user=" + user,
                "--password=" + pass, "-e", "source " + script};

            try
            {
                Process pr = Runtime.getRuntime().exec(command);
                int exitVal = pr.waitFor();

                if (exitVal != 0)
                {
                    if (LOGGER.isErrorEnabled())
                    {
                        BufferedReader err =
                            new BufferedReader(new InputStreamReader(pr.getErrorStream()));

                        String line = null;
                        while ((line = err.readLine()) != null)
                        {
                            LOGGER.error(line);
                        }
                    }
                }
            }
            catch (Exception ex)
            {
                throw new RuntimeException("Error running script: " + script, ex);
            }
        }
    }

}
