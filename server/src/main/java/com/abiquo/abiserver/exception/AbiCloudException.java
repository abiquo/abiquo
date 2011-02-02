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

package com.abiquo.abiserver.exception;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import com.abiquo.util.resources.ResourceManager;
import com.abiquo.util.resources.XMLResourceBundle;

/**
 * An abstract Exception class from which all Exceptions in abicloud inherit from AbiCloudException.
 * This class has methods which facilitate the retrieval of messages according to the
 * <p>
 * </p>
 * <code>Locale</code> Classes that inherit from this class must have:
 * 
 * <pre>
 * 1. Have the following constructors:
 * 
 * AbiCloudException()
 * AbiCloudException(String message, Object... arguments)
 * AbiCloudException(String message, Throwable t, Object... arguments)
 * AbiCloudException(Throwable t)
 *  
 * 
 * 
 * 2. Have a .properties file whose relative path must coincide with the fully qualified name of
 * the Exception for example com.foo.FooException must have the file com/foo/FooException.properties
 * 
 * When creating an instance of an AbiCloudException subclass, the String passed to the
 * Constructor must be a bundle name from its property file 
 * For example. If the file com/foo/FooException.properties has the following entries
 * 
 *      exception1=FooException message 1
 *      exception2=FooException message 2 {0}
 *      exception3=FooException message 3 {0}, {1}
 *      
 *   The following FooException object can be created: 
 *      FooException e = new FooException(&quot;exception1&quot;)
 *      e.getMesage() will return the String: FooException message 1
 *      
 *      
 *      FooException e2 = new FooException(&quot;exception2&quot;,&quot;Value1&quot;);
 *      e2.getMesage() will return the String: FooException message 2 Value1
 *     
 *      
 *      FooException e3 = new FooException(&quot;exception2&quot;,&quot;Value1&quot;,&quot;Value2&quot;);
 *      e3.getMesage() will return the String: FooException message 3 Value1, Value2
 * </pre>
 * 
 * @author abiquo
 */
public abstract class AbiCloudException extends RuntimeException
{

    /** The resource manager. */
    private final ResourceManager resourceManager = new ResourceManager();

    /** The arguments. */
    private Object[] arguments = {};

    private int abicloudErrorCode;

    public final int getAbicloudErrorCode()
    {
        return abicloudErrorCode;
    }

    /**
     * Instantiates an AbiCloudException object.
     */
    public AbiCloudException()
    {
        super();
    }

    /**
     * Instantiates an AbiCloudException with an AbiCloud error code
     * 
     * @param abicloudErrorCode
     */
    public AbiCloudException(int abicloudErrorCode)
    {
        super();
        this.abicloudErrorCode = abicloudErrorCode;
    }

    public AbiCloudException(String message, int abicloudErrorCode, String... arguments)
    {
        this(message, arguments);
        this.abicloudErrorCode = abicloudErrorCode;
    }

    /**
     * Instantiates an AbiCloudException object.
     * 
     * @param message the message
     * @param arguments the arguments
     */
    public AbiCloudException(String message, String... arguments)
    {
        super(message);

        this.arguments = arguments;
    }

    /**
     * Instantiates an AbiCloudException object.
     * 
     * @param message the message
     * @param t the t
     * @param arguments the arguments
     */
    public AbiCloudException(String message, Throwable t, int abicloudErrorCode,
        String... arguments)
    {
        this(message, t, arguments);
        this.abicloudErrorCode = abicloudErrorCode;

    }

    public AbiCloudException(String message, Throwable t, String... arguments)
    {
        super(message, t);
        this.arguments = arguments;
    }

    /**
     * Instantiates an AbiCloudException object.
     * 
     * @param t the t
     */
    public AbiCloudException(Throwable t)
    {
        super(t);
    }

    /**
     * @param t
     * @param abicloudErrorCode
     */
    public AbiCloudException(Throwable t, int abicloudErrorCode)
    {
        super(t);
        this.abicloudErrorCode = abicloudErrorCode;
    }

    /**
     * Gets the localized message.
     * 
     * @param locale a reference to the Locale whose language the message will be returned in
     * @return String containing the exception message according to the <code>Locale</code> supplied
     *         as an argument
     */
    public String getLocalizedMessage(Locale locale)
    {

        // set the basename of resourceManager.
        resourceManager.setBaseName(this.getClass());

        ResourceBundle bundle = resourceManager.getResourceBundle(locale);

        // Get the message used to create this object, this will correspond to a bundleName in the
        // Or it could be the message itself - in which case there will be no entry in the
        // corresponding
        // Resources file for this object
        // ResourceBundle for the given Locale in the argument locale
        String bundleName = super.getMessage();

        Object objMsg = null;

        if (bundle instanceof PropertyResourceBundle)
        {
            PropertyResourceBundle pBundle = (PropertyResourceBundle) bundle;

            objMsg = pBundle.handleGetObject(bundleName);
        }
        else if (bundle instanceof XMLResourceBundle)
        {
            XMLResourceBundle xmlBundle = (XMLResourceBundle) bundle;

            objMsg = xmlBundle.handleGetObject(bundleName);
        }

        // If there is no bundle for the given bundle name we return the bundleName that was
        // passed!!!!!
        if (objMsg != null)
        {
            return MessageFormat.format(objMsg.toString(), arguments);
        }
        else
        {
            return bundleName; // new String();
        }
    }

    public String getLocalizedMessage(String locale)
    {
        resourceManager.setLocale(locale);

        return getLocalizedMessage();
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Throwable#getLocalizedMessage()
     */
    @Override
    public String getLocalizedMessage()
    {
        return this.getLocalizedMessage(Locale.getDefault());
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Throwable#getMessage()
     */
    @Override
    public String getMessage()
    {
        return this.getLocalizedMessage(Locale.getDefault());
    }
}
