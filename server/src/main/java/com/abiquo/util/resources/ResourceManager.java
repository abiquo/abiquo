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

package com.abiquo.util.resources;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class ResourceManager.
 * 
 * @author abiquo
 *         <p>
 *         A manager for property files and resource bundles that have the same baseName Structure a
 *         of resources are as follows resources properties txt - properties files as plain text
 *         files having a .properties suffix xml - properties files as xml file locale txt -
 *         resourcebundle as properties text file xml - resourcebundle as a properties xml file For
 *         example a base name of MyResource could have the following variants of property and
 *         resource bundle files. <br>
 *         Property files <br>=
 *         ===========================================================
 *         <ol>
 *         <li>MyResource.properties</li>
 *         <li>MyResource.[SUBSECTION].properties</li>
 *         <li>MyResource.xml</li>
 *         <li>MyResource.[SUBSECTION].xml</li>
 *         </ol>
 *         <br>
 *         Resource files <br>=
 *         ============================================================
 *         <li>MyResource.properties</li>
 *         <li>MyResource.[SUBSECTION].properties (rare cases)
 *         <li>MyResource_LANGUAGE.properties</li>
 *         <li>MyResource.[SUBSECTION]_LANGUAGE.properties</li> (Rare cases)
 *         <li>MyResource_LANGUAGE_COUNTRY.properties</li>
 *         <li>MyResource.[SUBSECTION]_LANGUAGE_COUNTRY.properties</li>
 *         <li>MyResource.xml</li>
 *         <li>MyResource.[SUBSECTION].xml (rare cases)
 *         <li>MyResource_LANGUAGE.xml</li>
 *         <li>MyResource.[SUBSECTION]_LANGUAGE.xml</li> (Rare cases)
 *         <li>MyResource_LANGUAGE_COUNTRY.xml</li>
 *         <li>MyResource.[SUBSECTION]_LANGUAGE_COUNTRY.xml</li>
 * 
 *         <pre>
 * Example
 * The class com.foo.Foo should could have the following properties files (as .properties and .xml)
 * and resource bundles in the resources directory as so:
 * src/main/resources/
 * locale/
 * txt/
 * com/
 * foo/
 * Foo.properties
 * Foo_es.properties
 * properties/
 * txt/
 * com/
 * foo/
 * Foo.properties
 * xml/
 * com/
 * foo/
 * Foo.xml
 * To get the resource manager for this class do the following
 * &lt;code&gt;ResourceManager resourceManager = new ResourceManager(Foo.class); &lt;/code&gt;
 * 
 * The following can hence be done:
 * 1. Retrieve the properties files that
 * &lt;code&gt;Properties p = resourceManager.getProperties();&lt;/code&gt;
 * 2. Retrieve the properties file in xml format
 * &lt;code&gt;Properties p = resourceManager.getPropertiesFromXML();&lt;/code&gt;
 * 
 * 3. Retrieve the resource bundle by one of the following ways
 * &lt;code&gt;PropertyResourceBundle bundle = (PropertyResourceBundle) BasicCommand.resourceManager.getResourceBundle();&lt;/code&gt;
 * &lt;br&gt;
 * &lt;code&gt;XMLResourceBundle bundle = (XMLResourceBundle) BasicCommand.resourceManager.getResourceBundle();&lt;/code&gt;
 * 
 * </pre>
 */
public class ResourceManager
{

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(ResourceManager.class);

    /** The control. */
    private final ResourceBundle.Control control;

    /** The class loader. */
    private final ClassLoader classLoader;

    /** The locale. */
    private Locale locale;

    /**
     * Base name of the properties file or resource bundle (or both) that is managed by this
     * ResourceManage object.
     */
    private String baseName;

    /**
     * Instantiates a new resource manager.
     */
    public ResourceManager()
    {
        classLoader = this.getClass().getClassLoader();
        control = new ResourceBundleControl();
        locale = Locale.getDefault();
    }

    /**
     * The Constructor.
     * 
     * @param baseName baseName of the property file or ResourceBundle (if it serves as one).
     * 
     *            <pre>
     * The file(s) must be located somewhere in the src/main/resources/SUB_DIRECTORY where
     * SUB_DIRECTORY could be
     * properties - location of property or Resource bundle files stored as .properties files
     * xml - location of property or Resource bundle files stored as in .xml files
     * </pre>
     */
    public ResourceManager(String baseName)
    {

        this();

        // Check if someone supplied a baseName with an .xml or .properties extension, if so remove
        // the extension
        if (baseName.endsWith(".properties") || baseName.endsWith(".xml"))
        {
            baseName = baseName.substring(0, baseName.lastIndexOf("."));
        }

        this.baseName = baseName;
    }

    /**
     * The Constructor.
     * 
     * @param cl reference to the a <code>Class</code> representing the class use properties file or
     *            resource bundle will be handled by the instance created. The fully qualified name
     *            of the class represented by <code>cl</code> is used to obtain the base name of the
     *            .properties file(s) or resource bundle(s)- <br>
     * 
     *            <pre>
     * For example if cl holds information on the class com.foo.Foo then the baseName will be com/foo/Foo - from which the following files can be obtained
     * com/foo/Foo.properties
     * com/foo/Foo_es.properties
     * com/foo/Foo_en_UK.properties
     * com/foo/Foo_ca_ES.properties
     * com/foo/Foo_es.xml
     * com/foo/Foo.1.properties
     * com/foo/Foo.2.properties
     * </br>
     */
    public ResourceManager(Class< ? > cl)
    {
        this();
        this.setBaseName(cl);
    }

    /**
     * Sets the basename of this object.
     * 
     * @param baseName <code>String</code> containing the relative path of the baseName
     */
    public final void setBaseName(String baseName)
    {
        this.baseName = baseName;
    }

    /**
     * Sets the <code>basename</code> of this ResourceManager object to a relativefile path that
     * corresponds the full package name of class represented by the argument that passed to it. for
     * example if the class represented by argument is com.foo.Foo then the <code>basename</code>
     * will be com/foo/Foo
     * 
     * @param cl <code>Class</code> object from which the basename will be obtained.
     */
    public final void setBaseName(Class< ? > cl)
    {
        baseName = cl.getName().replace(".", "/");
    }

    /**
     * Sets the locale.
     * 
     * @param locale the new locale
     */
    public final void setLocale(Locale locale)
    {
        this.locale = locale;
    }

    /**
     * Sets the locale.
     * 
     * @param localeStr a string representeation of the Locale to be created e.g es_ES
     */
    public final void setLocale(String localeStr)
    {
        this.setLocale(createLocaleFromString(localeStr));
    }

    public Locale getLocale()
    {
        return locale;
    }

    /**
     * Creates the <code>Locale</code> from a <code>String</code> representation of a
     * <code>Locale</code>.
     * 
     * <pre>
     *   For example: the String es_ES will produce a Locale for spanish from Spain.
     * </pre>
     * 
     * @param localeStr <code>String</code> representation of the <code>Locale</code> to be created.
     * @return a <code>Locale</code> object
     */
    private Locale createLocaleFromString(String localeStr)
    {
        String[] strs = localeStr.split("_");

        Locale locale;

        if (strs.length == 1)
        {
            locale = new Locale(strs[0]);
        }
        else if (strs.length == 2)
        {
            locale = new Locale(strs[0], strs[1]);
        }
        else if (strs.length == 3)
        {
            locale = new Locale(strs[0], strs[1], strs[2]);
        }
        else
        {
            locale = Locale.getDefault();
        }

        return locale;
    }

    /**
     * Retrieves properties from a simple .properties text file
     * 
     * @param args the args
     * @return a reference to a Properties object
     */
    public Properties getProperties(String... args)
    {
        return this.getProperties(null, args);
    }

    /**
     * Gets the properties.
     * 
     * @param p the p
     * @param args the args
     * @return the properties
     */
    public Properties getProperties(Properties p, String... args)
    {
        return this.getProperties(p, args, false);
    }

    /**
     * Retrieves properties from a simple XML file.
     * 
     * @param args the args
     * @return a reference to a Properties object
     */
    public Properties getPropertiesFromXML(String... args)
    {
        return this.getProperties(null, args);
    }

    /**
     * Gets the properties from xml.
     * 
     * @param p the p
     * @param args the args
     * @return the properties from xml
     */
    public Properties getPropertiesFromXML(Properties p, String... args)
    {
        return this.getProperties(p, args, true);
    }

    /**
     * Gets the properties.
     * 
     * @param p the p
     * @param args and array of Objects with the following order [String,Boolean]
     * @return the properties
     */
    private Properties getProperties(Properties p, Object... args)
    {

        if (p == null)
        {
            p = new Properties();
        }

        String[] strs = (String[]) args[0];
        String subSection = strs.length > 0 ? strs[0] : "";
        boolean isXML = (Boolean) args[1];

        String fileName = subSection.length() > 0 ? baseName + "." + subSection : baseName;

        try
        {

            if (isXML)
            {
                p.loadFromXML(classLoader
                    .getResourceAsStream(ResourceConstants.RESOURCES_PROPERTIES_XML_ROOT_DIR
                        + fileName + ".xml"));
            }
            else
            {
                p.load(classLoader
                    .getResourceAsStream(ResourceConstants.RESOURCES_PROPERTIES_TXT_ROOT_DIR
                        + fileName + ".properties"));
            }
        }
        catch (NullPointerException e)
        {
            logger.warn("File Name: " + fileName, e);
        }
        catch (IOException e)
        {
            logger.error("File Name: " + fileName, e);
        }
        catch (Exception e)
        {
            logger.error("File Name: " + fileName, e);
        }

        return p;
    }

    /**
     * Gets the resource bundle.
     * 
     * <pre>
     * It call getResourceBundle(Locale locale,String ... args)
     * passing the Locale of the current ResourceManagerInstance.
     * </pre>
     * 
     * @param args the args
     * @return the resource bundle
     */
    public ResourceBundle getResourceBundle(String... args)
    {
        return this.getResourceBundle(locale, args);
    }

    /**
     * Gets the resource bundle according to the Locale passed to it as a argument.
     * 
     * @param locale the locale
     * @param args the args
     * @return a reference to a ResourceBundle
     */
    public ResourceBundle getResourceBundle(Locale locale, String... args)
    {

        String bundleBaseName = baseName;

        ResourceBundle resourceBundle = null;

        try
        {
            String subSection = args.length > 0 ? args[0] : "";

            if (subSection.length() > 0)
            {
                bundleBaseName += "." + subSection;
            }

            resourceBundle = ResourceBundle.getBundle(bundleBaseName, locale, control);
        }
        catch (NullPointerException e)
        {
            logger.warn("The basename is null - a null value will be returned");
        }

        catch (MissingResourceException e)
        {
            logger.warn("No Resource matching the bundleBaseName. " + bundleBaseName
                + "  has been found");
        }
        finally
        {
            // If an exception has occurred - an empty ResourceBundle is returned whose
            // handleGetObject returns an empty String
            if (resourceBundle == null)
            {
                resourceBundle = new ResourceBundle()
                {

                    @Override
                    public Enumeration<String> getKeys()
                    {
                        return null;
                    }

                    @Override
                    protected Object handleGetObject(String key)
                    {
                        return new String();
                    }

                };
            }

        }

        return resourceBundle;

    }

    /**
     * Get the <code>ResourceBundle</code> object according to the <code>String</code>
     * representation of the locale passed as an argument, and extra optional arguments. The locale
     * created from a string representation of the locale
     * 
     * @param localeStr <code>String</code> from which the <code>Locale</code> will be created. The
     *            created <code>Locale</code> will then be used to be determine which
     *            <code>ResourceBundle</code> to return. This argument can have the following format
     *            <ul>
     *            <li>[LANGUAGE] : two letter representation of the language in lowercase e.g
     *            <code>es</es> for spanish</li>
     *            <li>[LANGUAGE_COUNTRY]: two letter representation of the language in lowercase
     *            followed by an underscore and a 2 letter abbreviation of the country it belongs to
     *            for example es_ES for the Spanish language in Spain</li>
     *            <li>[LANGUAGE_COUNTRY_VARIANT]: 2 letter representation of the language in
     *            lowercase followed by an underscore and a 2 letter abbreviation of the country, an
     *            underscore and a variant of the language in lowercase
     *            </ul>
     * @param args the args
     * @return <code>ResourceBundle</code> object according to the <code>String</code>
     *         representation of the locale passed as an argument, and extra optional arguments
     */
    public ResourceBundle getResourceBundle2(String localeStr, String... args)
    {
        return this.getResourceBundle(createLocaleFromString(localeStr), args);
    }

    /**
     * Gets the message.
     * 
     * @param key the key
     * @param args the args
     * @return the message
     */
    public final String getMessage(String key, String... args)
    {
        // Gets the message via a PropertyResourceBundle - another method should use
        // XMLResourceBundle
        // Need to check if it is an XMLResourceBundle
        PropertyResourceBundle bundle = (PropertyResourceBundle) this.getResourceBundle();

        bundle.handleGetObject(key);

        return this.getBundleNameValue(bundle, key, args);

    }

    /**
     * Gets the bundle name value.
     * 
     * @param bundle the bundle
     * @param bundleName the bundle name
     * @param args the args
     * @return the bundle name value
     */
    private final <T extends ResourceBundle> String getBundleNameValue(T bundle, String bundleName,
        Object[] args)
    {
        String msg = "";

        try
        {

            msg = bundle.getObject(bundleName).toString();

            msg = MessageFormat.format(msg, args);
        }
        catch (NullPointerException e)
        {
            logger.warn("The bundlename supplied is {" + bundleName
                + "} an empty string will be returned");
        }
        catch (MissingResourceException e)
        {
            logger.warn("No entry found for the bundle name : [" + bundleName
                + "} an empty string will be returned");
        }

        return msg;
    }

    /**
     * @param p reference to a a
     *            <code>Properties</object> from which a set of key/value pairs will be obtained .
     * @param propertyNames a <code>String[]</code> containing a list of keys in the Properties
     *            arguments, using this list a new Properties element is created containing only the
     *            keys that correspond each element in this array. The resulting
     *            <code>Properties</code> is then save to a file.
     * @param args (optional) this is an <code>String[]</code> which has only on element - the name
     *            of the [SUBSECTION] part as described in the class description. TODO put link to
     *            part of the documentation
     */
    public void saveProperties(Properties p, String[] propertyNames, String... args)
    {
        Properties p2 = new Properties();

        for (String propertyName : propertyNames)
        {
            p2.setProperty(propertyName, p.getProperty(propertyName));
        }

        this.saveProperties(p2, args);
    }

    /**
     * Save properties.
     * 
     * @param p the p
     * @param args (optional) an array of Strings composing of only element which will be string
     *            appended to the base name of the properties file to be create <br>
     *            For example if the baseName is "foo" and args[0] is "xx" then the file created
     *            will be foo.xx.properties
     */
    public void saveProperties(Properties p, String... args)
    {

        File file =
            createPropertiesFile(args, ResourceConstants.RESOURCES_PROPERTIES_TXT_ROOT_DIR,
                ".properties");

        try
        {
            logger.debug("Saving the properties file : " + file + " ... ");

            p.store(new PrintWriter(file), null);

            logger.debug("Properties file: " + file + " saved ");
        }
        catch (Exception e)
        {
            logger.error("Unable to write the properties  to the file: " + file, e);
        }

    }

    /**
     * Save properties to xml.
     * 
     * @param p the p
     * @param args the args
     */
    public void savePropertiesToXML(Properties p, String... args)
    {
        File file =
            createPropertiesFile(args, ResourceConstants.RESOURCES_PROPERTIES_XML_ROOT_DIR, ".xml");

        try
        {
            p.storeToXML(new FileOutputStream(file), null, "UTF-8");
        }
        catch (Exception e)
        {
            logger.error("Unable to write the properties  to the file: " + file, e);
        }

    }

    /**
     * Gets the properties file name.
     * 
     * @param obj the obj
     * @return the properties file name
     */
    private File createPropertiesFile(Object obj, String rootDir, String fileExtension)
    {
        String subSection;

        if (obj instanceof String)
        {
            subSection = (String) obj;
        }
        else
        {
            String[] args = (String[]) obj;
            subSection = args.length > 0 ? args[0] : "";
        }

        String fileName = subSection.length() > 0 ? baseName + "." + subSection : baseName;

        fileName = fileName.replaceAll("/$", "") + fileExtension;

        fileName =
            classLoader.getResource("").getPath().replaceAll(File.separator + "$", "")
                + File.separator + rootDir + fileName;

        // Check if the root directory of the file exists

        File file = new File(fileName);

        if (!file.exists())
        {

            if (!file.getParentFile().exists())
            {
                file.getParentFile().mkdirs();
            }
        }

        return file;
    }

}
