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

package com.abiquo.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is a helper to load classes in abicloud_server context. Very usefully i plugfins and
 * extensions.
 * 
 * @author jdevesa@abiquo.com
 */
public class AbiClassLoader
{

    /**
     * The Constant logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(AbiClassLoader.class);

    /**
     * Local class Loader
     */
    // private static final URLClassLoader urlLoader = (URLClassLoader)
    // ClassLoader.getSystemClassLoader();
    private static final URLClassLoader urlLoader =
        (URLClassLoader) AbiClassLoader.class.getClassLoader();

    /**
     * Public method to return all the classes for a given package. It works in two ways: one for
     * directory (own project) classes and the other one for load classes from Jar. This method is
     * the only point of access for this class.
     * <p>
     * 
     * @param packageName name of the classes to return
     * @return ArrayList<Class> with all the related classes.
     */
    public static ArrayList<Class< ? extends Object>> loadClassesFromPackage(String requestedPackage)
    {

        ArrayList<Class< ? extends Object>> classesToRecover =
            new ArrayList<Class< ? extends Object>>();

        if (requestedPackage == null || requestedPackage.isEmpty())
        {
            return classesToRecover;
        }

        /*
         * Get all the sources where find the classes and browse them. (It is possible that a
         * package would be inside a directory and, for instance, two packages in the same time. In
         * this case, we will have three sources.
         */
        try
        {
            Enumeration<URL> allSources;
            String parsedName = requestedPackage.replace('.', File.separatorChar);

            allSources = urlLoader.findResources(parsedName);

            while (allSources.hasMoreElements())
            {

                URL urlSource = allSources.nextElement();
                String basePath = urlSource.getPath();
                
                /*
                 * The URL source converts the spaces of the local paths
                 * to the '%20' string. We replace to 'space' again. 
                 */
                basePath = basePath.replace("%20", " ");
                
                File rootDirectory = new File(basePath);
                if (rootDirectory.isDirectory())
                {
                    classesToRecover.addAll(loadClassesFromDir(rootDirectory, parsedName));
                }
                else
                {
                    // Maybe its a jar...
                    ArrayList<Class< ? extends Object>> jarClasses =
                        loadClassesFromJar(urlSource, parsedName);
                    
                    // logger.error(" url is "+ urlSource);
                    
                    if (jarClasses != null)
                    {
                        classesToRecover.addAll(jarClasses);
                    }
                }
            }
        }
        catch (IOException e)
        {
            logger.error(requestedPackage + ": can not find the resources");
        }

        return classesToRecover;
    }

    /**
     * From a given Directory, get all the classes of its files. It works in recursive way.
     * 
     * @param parentDir directory to be scanned
     * @param parsedName the parsed(with '/' instead of '.') name of the directory that contains the
     *            file
     * @return ArrayList containing the existing classes into the directory
     */
    private static ArrayList<Class< ? extends Object>> loadClassesFromDir(File parentDir,
        String parsedName)
    {
        ArrayList<Class< ? extends Object>> childrenToRecover =
            new ArrayList<Class< ? extends Object>>();

        // loop for all its children
        for (File children : parentDir.listFiles())
        {
            // if it is a directory...
            if (children.isDirectory())
            {
                // add all its children recursively
                childrenToRecover.addAll(loadClassesFromDir(children, parsedName
                    + System.getProperty("file.separator") + children.getName()));
            }
            else
            {
                // Gets the file path an
                String javaFilePath =
                    parsedName + System.getProperty("file.separator") + children.getName();

                // add class if loadClass is not null
                Class< ? extends Object> tmpClass = loadAbiClass(javaFilePath);
                if (tmpClass != null)
                {
                    childrenToRecover.add(tmpClass);
                }
            }
        }

        return childrenToRecover;
    }

    /**
     * In this case, we load all classes inside a jar.
     * 
     * @param jarURL entry jar URL (with the template 'jar:file:com.xxx.xxxx.xxx!' )
     * @param parsedName the name of the package but with '/' instead of '.' (to browse the package)
     * @return ArrayList containing the existing classes into the jar
     */
    private static ArrayList<Class< ? extends Object>> loadClassesFromJar(URL jarURL,
        String parsedName)
    {
        /*
         * First of all, we create a jarconnection in order to find all the desired classes, After
         * this, we call our loadAbiClass method
         */

        ArrayList<Class< ? extends Object>> childrenJarToRecover =
            new ArrayList<Class< ? extends Object>>();

        /* Create JarConnection and open an Input Stream and browse the jar */
       // JarURLConnection jarCon;
        try
        {

        	 // logger.error(" url is ex_"+ jarURL.toExternalForm());
             // logger.error(" url is "+ jarURL.toString());
              
        	//jarCon = (JarURLConnection) jarURL.openConnection();
            //String jarName = jarCon.getJarFile().getName();
            
            JarInputStream jarFile = new JarInputStream(new FileInputStream(jarURL.toExternalForm()));
            JarEntry jarEntry;

            while ((jarEntry = jarFile.getNextJarEntry()) != null)
            {
                if ((jarEntry.getName().startsWith(parsedName))
                    && (jarEntry.getName().endsWith(".class")))
                {
                    Class< ? extends Object> tmpClass = loadAbiClass(jarEntry.getName());
                    if (tmpClass != null)
                    {
                        childrenJarToRecover.add(tmpClass);
                    }

                }

            }
        }
        catch (IOException e)
        {
            logger.error("Exception thrown accessing to jar ");
        }

        return childrenJarToRecover;

    }

    /**
     * Load a class from a given path.
     * 
     * @param path path where the class should be.
     * @return a Class stored in path. Null otherwise.
     */
    private static Class< ? extends Object> loadAbiClass(String javaFilePath)
    {

        Class< ? extends Object> desiredClass;

        if (javaFilePath.endsWith(".class"))
        {
            // Parsing the javaFilePath like com/yyy/xxxxx/zzzzz.class
            // to com.yyy.xxxxx.zzzzz
            String parsedFilePath = javaFilePath.replace(File.separatorChar, '.');
            parsedFilePath = parsedFilePath.substring(0, parsedFilePath.lastIndexOf('.'));

            try
            {
                desiredClass = urlLoader.loadClass(parsedFilePath);
            }
            catch (ClassNotFoundException e)
            {
                // invalid .class file
                logger.debug(javaFilePath + " is an invalid. Discarded");
                desiredClass = null;
            }
        }
        else
        {
            // maybe a .java or a .xml.. don't want to load!
            desiredClass = null;
        }

        return desiredClass;

    }

}
