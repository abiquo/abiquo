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

package com.abiquo.api.resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;

import com.abiquo.api.persistence.AbstractJpaDBUnitTest;

/**
 * This class should never be used again. Use AbstractJpaGeneratorTest instead.
 * 
 * @deprecated
 */
public abstract class AbstractResourceIT extends AbstractJpaDBUnitTest
{

    protected RestClient client = new RestClient();

    // @BeforeClass
    // public static void setupServer() throws Exception
    // {
    // Jetty.start();
    // }
    //
    // @AfterClass
    // public static void tearDownServer() throws Exception
    // {
    // Jetty.stop();
    // }

    protected String prettyPrint(Resource resource) throws IOException
    {
        InputStream stream = resource.get(InputStream.class);
        BufferedReader r = new BufferedReader(new InputStreamReader(stream));

        StringBuilder builder = new StringBuilder();
        for (String line = r.readLine(); line != null; line = r.readLine())
        {
            builder.append(line);
        }
        return builder.toString();
    }
}
