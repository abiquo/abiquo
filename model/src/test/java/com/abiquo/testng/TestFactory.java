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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Factory;

/**
 * Factory to create TestNG tests based on the simple class name of the test class.
 * 
 * @author ibarrera
 */
public class TestFactory
{
    private static final Logger LOGGER = LoggerFactory.getLogger(TestFactory.class);

    private static final String TEST_TO_RUN = "test.name";

    private static final String DEFAULT_PACKAGE = "com.abiquo";

    @Factory
    public Object[] createTest()
    {
        Class< ? > testClass = null;

        String[] testClassesToRun = getParameter(TEST_TO_RUN).split(",");
        Object[] testClasses = new Object[testClassesToRun.length];

        for (int i = 0; i < testClassesToRun.length; i++)
        {
            try
            {
                LOGGER.debug("Looking for test class: {}", testClassesToRun[i]);
                testClass = findClass(DEFAULT_PACKAGE, testClassesToRun[i]);
            }
            catch (Exception ex)
            {
                throw new RuntimeException("Could not load test class: " + testClassesToRun[i]);
            }

            if (testClass == null)
            {
                throw new RuntimeException("Test class not found: " + testClassesToRun[i]);
            }

            try
            {
                LOGGER.info("Loading test class: {}", testClass.getName());
                testClasses[i] = testClass.newInstance();
            }
            catch (Exception ex)
            {
                throw new RuntimeException("Could not load test class: " + testClassesToRun[i]);
            }
        }

        return testClasses;
    }

    private static Class< ? > findClass(final String packageName, final String simpleClassName)
        throws ClassNotFoundException, IOException
    {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);

        List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements())
        {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }

        for (File directory : dirs)
        {
            Class< ? > clazz = findClass(directory, packageName, simpleClassName);
            if (clazz != null)
            {
                return clazz;
            }
        }

        return null;
    }

    private static Class< ? > findClass(final File directory, final String packageName,
        final String simpleClassName) throws ClassNotFoundException
    {
        if (!directory.exists())
        {
            return null;
        }

        File[] files = directory.listFiles();
        for (File file : files)
        {
            if (file.isDirectory())
            {
                Class< ? > clazz =
                    findClass(file, packageName + "." + file.getName(), simpleClassName);
                if (clazz != null)
                {
                    return clazz;
                }
            }
            else if (file.getName().equals(simpleClassName + ".class"))
            {
                return Class.forName(packageName + '.'
                    + file.getName().substring(0, file.getName().length() - 6));
            }
        }

        return null;
    }
}
