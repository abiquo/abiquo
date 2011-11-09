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

/*
 * To change this template, choose Tools || Templates
 * and open the template in the editor.
 */

package com.abiquo.util;

import java.text.MessageFormat;
import java.util.Date;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.LogHB;
import com.abiquo.abiserver.persistence.hibernate.HibernateUtil;
import com.abiquo.abiserver.pojo.result.BasicResult;

/**
 * @author abiquo This class holds the information on an error generated during the use of the
 *         application
 */
public class AbiCloudError
{

    private static final Logger logger = LoggerFactory.getLogger(AbiCloudError.class);

    /** General Storage error */
    public static final int STORAGE_ERROR = 20;

    /** General Network error */
    public static final int NETWORK_ERROR = 21;

    /**
     * General infrastructure error
     */
    public static final int INFRASTRUCTURE_ERROR = 22;

    /**
     * General Virtual Image error
     */
    public static final int VIRTUAL_IMAGE_ERROR = 23;

    public static final int PUBLIC_IP_USED = 12;

    private final Integer idVirtualAppliance;

    private final String errorCode;

    private final String errorName;

    private final String errorMsgTemplate;

    private final String errorMsg;

    private final String logMsgTemplate;

    private final Exception exception;

    private final String errorID;

    private final String[] msgs;

    private final String contactInstructions;

    /**
     * @param errorCode the error code of the type of error that was generated
     * @param errorName the associated name with the above error code
     * @param errorMsg the standard error message
     * @param errorMsg customized error message that provides more information on the generated
     *            error
     * @param contactInstructions Message advising the user on how to go about resolving the problem
     *            that has arisen
     * @param logMsgTemplate a message template used for reporting messages to the the log file
     * @param msgs an array of strings containing values for which the place holders (if any) in
     *            <code>logMsgTemplate</code> will be replaced
     * @param errorID the unique id associated with the generated error
     * @param e a reference to an <code>Exception</code> object if an Exception was thrown
     * @param idVirtualAppliance the id of the virtual machine - can be null if the error if the
     *            operation does not involve a virtualAppliance
     */
    public AbiCloudError(String errorCode, String errorName, String errorMsgTemplate,
        String errorMsg, String contactInstructions, String logMsgTemplate, String[] msgs,
        Exception exception, Integer idVirtualAppliance)
    {

        this.errorCode = errorCode;
        this.errorName = errorName;
        this.errorMsgTemplate = errorMsgTemplate;
        this.errorID = String.valueOf(System.currentTimeMillis() / 1000);
        this.exception = exception;
        this.logMsgTemplate = logMsgTemplate;
        this.msgs = msgs;
        this.errorMsg = errorMsg;
        this.contactInstructions = contactInstructions;
        this.idVirtualAppliance = idVirtualAppliance;

    }

    public AbiCloudError(String errorMsgTemplate, String extraMsg, String errorName,
        String errorCode, String contactInstructions, Exception exception,
        Integer idVirtualAppliance)
    {

        this.errorMsgTemplate = errorMsgTemplate;
        this.errorName = errorName;
        this.errorCode = errorCode;
        this.errorID = String.valueOf(System.currentTimeMillis() / 1000);
        this.contactInstructions = contactInstructions;
        this.idVirtualAppliance = idVirtualAppliance;
        errorMsg = extraMsg;

        // THIS WILL BE REMOVED LATER - IT IS SO FOR BACKWARD COMPATIBILITY WITH THE EARLIER VERSION
        this.exception = exception;
        msgs = new String[] {};
        logMsgTemplate = "";
    }

    /**
     * Formats the message that will be sent or stored in the log file and database
     * 
     * @return
     */
    public String getMessage(boolean appendContactInstructions)
    {

        String msg = errorMsgTemplate;

        // // THIS WAS TO ADD EXTRA INFORMATION - BUT SINCE IN THE FUTURE all the command will throw
        // an AbicloudException it is not longer needed
        // if (exception != null && exception.getMessage() != null)
        // {
        // msg += "\n" + exception.getMessage();
        // }

        if (appendContactInstructions)
        {
            msg += "\n\n" + contactInstructions;
        }

        return MessageFormat.format(msg, new Object[] {errorName, errorMsg, errorCode, errorID});
    }

    /**
     * Sets the message of a <code>BasicResult</code> object, its success to <code>false</code> and
     * also logs the error Message.
     * 
     * @param result
     * @param error
     */
    public final void handleError(BasicResult result)
    {

        logMessage();

        if (result != null)
        {
            result.setSuccess(false);
            result.setMessage(getMessage(true));
        }

    }

    /**
     * Logs the error to the log file and also stores the error in the database if the error
     * occurred during a modification,creation or deletion of a VirtuaAppliance
     */
    public void logMessage()
    {
        // Construction of the message to be logged - the usage of logMsgTemplate will be redundant
        // in the future
        String logMessage =
            errorName + "  " + MessageFormat.format(logMsgTemplate, (Object[]) msgs);
        logMessage +=
            "[Error code:" + errorCode + " Timestamp:" + errorID + "]. " + getMessage(false);

        logger.error(logMessage, exception);

        if (idVirtualAppliance != null && idVirtualAppliance > 0)
        {
            Transaction transaction = null;

            try
            {

                Session session = HibernateUtil.getSession();
                transaction = session.beginTransaction();

                LogHB virtualApplianceLogHB = new LogHB();

                virtualApplianceLogHB.setIdVirtualAppliance(idVirtualAppliance);
                virtualApplianceLogHB.setDescription(getMessage(false));
                virtualApplianceLogHB.setLogDate(new Date());

                session.save(virtualApplianceLogHB);

                transaction.commit();

            }
            catch (Exception e)
            {
                logger.error("SQL Error", e);

                if (transaction != null && transaction.isActive())
                {
                    transaction.rollback();
                }

            }

        }

    }
}
