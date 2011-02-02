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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;

public class SystemPropertiesService implements SystemPropertiesServiceMBean
{
    private static final String ABIQUO_ROOT = "/opt/abiquo/config";

    private static final String ABIQUO_PROPERTIES_FILE = "abiquo.properties";

    private static final String ABIQUO_CONFIG_PATH = ABIQUO_ROOT + "/" + ABIQUO_PROPERTIES_FILE;

    private String configPath = System.getProperty("abiquo.config.path", ABIQUO_CONFIG_PATH);

    public SystemPropertiesService()
    {
        MBeanServer server = getServer();

        ObjectName name = null;
        try
        {
            name =
                new ObjectName("Abiquo:Name=Configuration,Type=com.abiquo.mbeans.SystemPropertiesServiceMBean");
            if (!server.isRegistered(name))
            {
                server.registerMBean(this, name);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public String getConfigPath()
    {
        return configPath;
    }

    @Override
    public String getSystemProperty(String name)
    {
        return System.getProperty(name);
    }

    @Override
    public Properties getSystemProperties() throws IOException
    {
        Properties props =
            load(getClass().getClassLoader().getResourceAsStream(ABIQUO_PROPERTIES_FILE));

        Properties system = new Properties();
        for (Object key : props.keySet())
        {
            system.setProperty(key.toString(), System.getProperty(key.toString()));
        }

        return system;
    }

    public SystemPropertiesService load()
    {
        try
        {
            load(configPath);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return this;
    }

    private void load(final String path) throws IOException
    {
        Properties props =
            load(getClass().getClassLoader().getResourceAsStream(ABIQUO_PROPERTIES_FILE));

        URL url = toURL(path, ABIQUO_ROOT);
        if (url != null)
        {
            load(url.openConnection().getInputStream(), System.getProperties());
        }

        for (Object key : props.keySet())
        {
            System.setProperty(key.toString(), System.getProperty(key.toString(), props.get(key)
                .toString()));
        }
    }

    private Properties load(InputStream is) throws IOException
    {
        return load(is, new Properties());
    }

    private Properties load(InputStream is, Properties props) throws IOException
    {
        props.load(is);
        is.close();

        return props;
    }

    private MBeanServer getServer()
    {
        MBeanServer mbserver = null;

        List<MBeanServer> mbservers = MBeanServerFactory.findMBeanServer(null);

        if (mbservers.size() > 0)
        {
            mbserver = (MBeanServer) mbservers.get(0);
        }

        if (mbserver == null)
        {
            mbserver = MBeanServerFactory.createMBeanServer();
        }

        return mbserver;
    }

    private URL toURL(String urlspec, final String relativePrefix) throws MalformedURLException
    {
        urlspec = urlspec.trim();

        URL url;

        try
        {
            url = new URL(urlspec);
            if (url.getProtocol().equals("file"))
            {
                url = makeURLFromFilespec(url.getFile(), relativePrefix);
            }
        }
        catch (Exception e)
        {
            try
            {
                url = makeURLFromFilespec(urlspec, relativePrefix);
            }
            catch (IOException n)
            {
                throw new MalformedURLException(n.toString());
            }
        }

        return url;
    }

    private static URL makeURLFromFilespec(final String filespec, final String relativePrefix)
        throws IOException
    {
        File file = new File(filespec);

        if (relativePrefix != null && !file.isAbsolute())
        {
            file = new File(relativePrefix, filespec);
        }

        file = file.getCanonicalFile();

        if (!file.exists())
        {
            return null;
        }

        return file.toURI().toURL();
    }
}
