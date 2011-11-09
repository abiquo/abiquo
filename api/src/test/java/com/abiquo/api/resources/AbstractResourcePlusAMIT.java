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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.springframework.web.context.ContextLoaderListener;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

/**
 * Integration Tests with ApplianceManager and API contexts This class should never be used again.
 * Use AbstractJpaGeneratorTest instead.
 * 
 * @author destevez
 * @deprecated
 */
public abstract class AbstractResourcePlusAMIT extends AbstractResourceIT
{

    private static Server server;

    public static final String VM_REPOSITORY_TEST = "/tmp/testvmrepo";

    private static final int AM_SERVICE_MAPPING_PORT = 9010;

    protected static String contextConfigLocation =
        "classpath:META-INF/server/wink-core-context.xml, "
            + "classpath:springresources/applicationContext-test.xml";

    protected static String amContextConfigLocation = "classpath:springresources/cxf.xml";

    @BeforeClass
    public static void setupServer() throws Exception
    {
        // TODO
        // server = new Server(AM_SERVICE_MAPPING_PORT);
        //
        // Context amContextHandler = new Context(server, "/am", Context.SESSIONS);
        //
        // Map<String, String> amInitParameters = new HashMap<String, String>();
        // amInitParameters.put("contextConfigLocation", amContextConfigLocation);
        //
        // amContextHandler.setInitParams(amInitParameters);
        // amContextHandler.addEventListener(new ContextLoaderListener());
        //
        // ServletHolder amServletHolder = new ServletHolder(new CXFServlet());
        // amContextHandler.addServlet(amServletHolder, "/*");
        //
        // // VM Test Repository for AM
        // File vmrepo = new File(VM_REPOSITORY_TEST);
        // if (vmrepo != null && vmrepo.exists())
        // {
        // deleteDirectory(vmrepo);
        // }
        // vmrepo.mkdirs();
        //
        // server.start();
    }

    static protected boolean deleteDirectory(File path)
    {
        if (path.exists())
        {
            File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++)
            {
                if (files[i].isDirectory())
                {
                    deleteDirectory(files[i]);
                }
                else
                {
                    files[i].delete();
                }
            }
        }
        return (path.delete());
    }

    @AfterClass
    public static void tearDownServet() throws Exception
    {
        if (server != null)
        {
            server.stop();
        }

        File vmrepo = new File(VM_REPOSITORY_TEST);
        if (vmrepo != null || !vmrepo.exists())
        {
            vmrepo.delete();
        }
    }

}
