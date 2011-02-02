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

import java.util.Locale;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.abiserver.exception.AbiCloudException;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.util.resources.ResourceManager;
import com.abiquo.util.resources.XMLResourceBundle;

/**
 * @author abiquo
 */
public class ErrorManager extends Properties
{
    private static final Logger logger = LoggerFactory.getLogger(ErrorManager.class);

    private static ErrorManager singletonObject;

    /**
     * The prefix for the error codes generated
     */
    private final String prefix;

    private final ResourceManager resourceManager;

    /**
     * It could happen that the access method may be called twice from 2 different classes at the
     * same time and hence more than one object being created. This could violate the design patter
     * principle. In order to prevent the simultaneous invocation of the getter method by 2 threads
     * or classes simultaneously we add the synchronized keyword to the method declaration
     * 
     * @param args
     * @return
     */
    public static synchronized ErrorManager getInstance(String... args)
    {

        if (ErrorManager.singletonObject == null)
        {
            ErrorManager.singletonObject = new ErrorManager(args.length > 0 ? args[0] : "");
        }

        return ErrorManager.singletonObject;

    }

    private ErrorManager(String prefix)
    {
        this.prefix = prefix;

        resourceManager = new ResourceManager(ErrorManager.class);

        // Load the .properties files for this object
        resourceManager.getProperties(this);
        // resourceManager.getProperties(this, "counter");

    }

    private BasicResult basicResult;

    public boolean hasError()
    {

        if (basicResult != null)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public BasicResult getBasicResult()
    {
        return basicResult;
    }

    /**
     * This will be used in the future
     * 
     * @param errorCode
     * @return
     */
    public String getErrorString(int errorCode)
    {
        return errorCode < 10 ? "00" + errorCode : "0" + errorCode;
    }

    /**
     * @param resourceManager a reference to a ResourceManager object
     * @param faultResult a BasicResult object
     * @param bundleBaseName basename of a resource bundle
     * @param msgs optional array of strings that will be used to replace place holder in message
     *            strings in the resource bundle
     */
    public void reportError(ResourceManager resourceManager, BasicResult faultResult,
        String bundleBaseName, String... msgs)
    {
        this.reportError(resourceManager, faultResult, bundleBaseName, null, 0, msgs);
    }

    public void reportError(ResourceManager resourceManager, BasicResult faultResult,
        String bundleBaseName, Integer idVirtualAppliance, String... msgs)
    {
        this.reportError(resourceManager, faultResult, bundleBaseName, null, idVirtualAppliance,
            msgs);
    }

    /**
     * @param resourceManager
     * @param faultResult
     * @param bundleBaseName
     * @param exception
     * @param msgs
     */
    public void reportError(ResourceManager resourceManager, BasicResult faultResult,
        String bundleBaseName, Exception exception, String... msg)
    {
        this.reportError(resourceManager, faultResult, bundleBaseName, exception, 0, msg);
    }

    // This reports an error for a virtual machine
    public void reportError(ResourceManager resourceManager, BasicResult faultResult,
        String bundleBaseName, Exception exception, Integer idVirtualAppliance, String... msgs)
    {

        AbiCloudError error =
            createAbiError(resourceManager, bundleBaseName, exception, idVirtualAppliance, msgs);

        error.handleError(faultResult);

    }

    // This reports an error for a virtual machine
    public AbiCloudError createAbiError(ResourceManager resourceManager, String bundleBaseName,
        Integer idVirtualAppliance, String... msgs)
    {
        return this.createAbiError(resourceManager, bundleBaseName, null, idVirtualAppliance, msgs);
    }

    // This reports an error for a virtual machine
    public AbiCloudError createAbiError(ResourceManager resourceManager, String bundleBaseName,
        Exception exception, Integer idVirtualAppliance, String... msgs)
    {

        ResourceBundle bundle = resourceManager.getResourceBundle();

        // NOTE - this is just a quick fix
        String errorCode = ""; // bundle.getString(bundleBaseName + ".errorCode");

        Object errorCodeObj = bundle.getString(bundleBaseName + ".errorCode");

        if (errorCodeObj != null)
        {
            errorCode = errorCodeObj.toString();
        }
        else
        {
            logger.warn("No value exists for the bundle name: " + bundleBaseName + ".errorCode");
        }

        String errorName = this.getProperty(errorCode);

        Object objLogMsg = null;
        Object objExtraMsg = null;

        // We use handleGetObject as the the attribute [KEY_BASE].logMsg is not always defined
        if (bundle instanceof PropertyResourceBundle)
        {

            PropertyResourceBundle pBundle = (PropertyResourceBundle) bundle;

            objLogMsg = pBundle.handleGetObject(bundleBaseName + ".logMsg");
            objExtraMsg = pBundle.handleGetObject(bundleBaseName + ".extraMsg");

        }
        else if (bundle instanceof XMLResourceBundle)
        {

            XMLResourceBundle xmlBundle = (XMLResourceBundle) bundle;

            objLogMsg = xmlBundle.handleGetObject(bundleBaseName + ".logMsg");
            objExtraMsg = xmlBundle.handleGetObject(bundleBaseName + ".extraMsg");

        }

        String logMsg = objLogMsg != null ? objLogMsg.toString() : "";
        String extraMsg = objExtraMsg != null ? objExtraMsg.toString() + "\n" : "";

        // This is will be change based on the language selected by the client
        String bundleName = resourceManager.getLocale().toString();
        String errorMsg = null, contactInstructions = null;

        if (containsKey(bundleName + ".errorMsg"))
        {
            errorMsg = this.getProperty(bundleName + ".errorMsg");
            contactInstructions = this.getProperty(bundleName + ".errorMsg.contactInstructions");
        }
        else
        {
            this.getProperty("errorMsg");
            contactInstructions = this.getProperty("errorMsg.contactInstructions");
        }

        return new AbiCloudError(prefix + errorCode,
            errorName,
            errorMsg,
            extraMsg,
            contactInstructions,
            logMsg,
            msgs,
            exception,
            idVirtualAppliance);

    }

    public String reportError(AbiCloudException abiCloudException)
    {
        return reportError(abiCloudException, Locale.getDefault().toString(), 0);
    }

    public String reportError(AbiCloudException abiCloudException, Integer idVirtualAppliance)
    {
        return reportError(abiCloudException, Locale.getDefault().toString(), idVirtualAppliance);
    }

    public String reportError(AbiCloudException abiCloudException, String localeString)
    {
        return reportError(abiCloudException, localeString, 0);
    }

    public String reportError(AbiCloudException abiCloudException, String localeString,
        Integer idVirtualAppliance)
    {
        int errorCode = abiCloudException.getAbicloudErrorCode();
        String errorName = getProperty(errorCode + "." + localeString);
        String errorMsg = abiCloudException.getLocalizedMessage(localeString) + "\n";
        String errorCodeString = getErrorString(errorCode);

        // This is will be change based on the language selected by the client
        String bundleName = resourceManager.getLocale().toString();
        String errorMsgTemplate = null, contactInstructions = null;

        if (containsKey(bundleName + ".errorMsg"))
        {
            errorMsgTemplate = this.getProperty(bundleName + ".errorMsg");
            contactInstructions = this.getProperty(bundleName + ".errorMsg.contactInstructions");
        }
        else
        {
            this.getProperty("errorMsg");
            contactInstructions = this.getProperty("errorMsg.contactInstructions");
        }

        AbiCloudError abiError =
            new AbiCloudError(errorMsgTemplate,
                errorMsg,
                errorName,
                errorCodeString,
                contactInstructions,
                abiCloudException,
                idVirtualAppliance);

        abiError.logMessage();

        return abiError.getMessage(true);
    }

}
