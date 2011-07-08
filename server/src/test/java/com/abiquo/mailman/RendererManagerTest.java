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

package com.abiquo.mailman;

import static org.testng.Assert.assertTrue;

import java.util.Properties;

import org.testng.annotations.Test;

/**
 * 
 */
public class RendererManagerTest
{
    /**
     * Test method for
     * {@link com.abiquo.mailman.RendererManager#generateBody(java.util.Properties, java.lang.String, java.lang.String, java.lang.String)}
     * .
     */
    @Test
    public final void testGenerateBody()
    {
        Properties properties = new Properties();
        properties.setProperty("username", "testusername");
        properties.setProperty("password", "testpassword");
        properties.setProperty("name", "testname");
        properties.setProperty("lastname", "testlastname");
        properties.setProperty("lang", "en");

        String body = RendererManager.generateBody(properties, "test", "en", "default");

        assertTrue(body.indexOf("testlastname") > 0 && body.indexOf("testname") > 0
            && body.indexOf("Hello World!") > 0 && body.indexOf("en") > 0
            && body.indexOf("testusername") > 0 && body.indexOf("testpassword") > 0);
    }

    // /**
    // * @throws java.lang.Exception
    // */
    // @Before
    // public void setUp() throws Exception
    // {
    // }
    //
    // /**
    // * @throws java.lang.Exception
    // */
    // @After
    // public void tearDown() throws Exception
    // {
    // }
}
