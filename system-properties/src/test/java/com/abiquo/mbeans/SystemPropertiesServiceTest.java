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

package com.abiquo.mbeans;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileOutputStream;

import org.junit.Test;

public class SystemPropertiesServiceTest
{

    @Test
    public void loadsResourceConfigFile()
    {
        new SystemPropertiesService().load();

        String abiquoVersion = System.getProperty("abiquo.version");
        assertNotNull(abiquoVersion);
    }

    @Test
    public void loadSystemFile() throws Exception
    {
        File config = new File("/tmp/config.properties");
        config.createNewFile();

        FileOutputStream stream = new FileOutputStream(config);
        stream.write("abiquo.version=666".getBytes());

        System.setProperty("abiquo.config.path", config.getAbsolutePath());
        new SystemPropertiesService().load();

        String abiquoVersion = System.getProperty("abiquo.version");
        assertEquals("666", abiquoVersion);
    }

    @Test
    public void doesNotLoadSystemFileUnlessExist()
    {
        System.setProperty("abiquo.config.path", "/tmp/foooooo");
        new SystemPropertiesService().load();

        String abiquoVersion = System.getProperty("abiquo.version");
        assertNotNull(abiquoVersion);
    }

    @Test
    public void doesNotOverrideSystemProperties()
    {
        System.setProperty("abiquo.version", "12345");
        new SystemPropertiesService().load();

        String abiquoVersion = System.getProperty("abiquo.version");
        assertEquals("12345", abiquoVersion);
    }
}
