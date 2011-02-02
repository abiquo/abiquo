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
package com.abiquo.virtualfactory.utils.hyperv;

import static org.jinterop.dcom.impls.JIObjectFactory.narrowObject;

import java.util.ArrayList;

import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.IJIComObject;
import org.jinterop.dcom.core.IJIUnsigned;
import org.jinterop.dcom.core.JIArray;
import org.jinterop.dcom.core.JIString;
import org.jinterop.dcom.core.JIVariant;
import org.jinterop.dcom.impls.JIObjectFactory;
import org.jinterop.dcom.impls.automation.IJIDispatch;
import org.jinterop.dcom.impls.automation.IJIEnumVariant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hyper-v utils
 * 
 * @author pnavarro
 */
public class HyperVUtils
{
    /** The logger */
    private static final Logger logger = LoggerFactory.getLogger(HyperVUtils.class);

    /**
     * Monitor a job
     * 
     * @param jobPath the job path to monitor
     * @param dispatch the dispatch object to get the job state from
     * @throws JIException if an error occurs
     */
    public static void monitorJob(String jobPath, IJIDispatch dispatch) throws JIException
    {
        int jobState = 0;
        IJIDispatch jobDispatch = null;
        while (true)
        {
            jobDispatch =
                (IJIDispatch) JIObjectFactory.narrowObject(dispatch.callMethodA("Get",
                    new Object[] {new JIString(jobPath)})[0].getObjectAsComObject().queryInterface(
                    IJIDispatch.IID));
            jobState = jobDispatch.get("JobState").getObjectAsInt();
            if (jobState != 3 && jobState != 4)
            {
                break;
            }
            logger.debug("Monitoring Job. " + jobDispatch.get("PercentComplete").getObjectAsInt()
                + "% complete");
            logger.debug("JobState: " + jobState);
            try
            {
                Thread.sleep(1000);
            }
            catch (InterruptedException e)
            {
                logger.warn("Monitoring Job interrupted but ignored.");
            }
        }
        if (jobState != 7)
        {
            jobDispatch =
                (IJIDispatch) JIObjectFactory.narrowObject(dispatch.callMethodA("Get",
                    new Object[] {new JIString(jobPath)})[0].getObjectAsComObject().queryInterface(
                    IJIDispatch.IID));
            int errorCode = jobDispatch.get("ErrorCode").getObjectAsInt();
            String errorDesc = jobDispatch.get("ErrorDescription").getObjectAsString2();
            // String ls = System.getProperty("line.separator");
            logger.error("Monitoring job result error:" + "\tErrorCode: " + errorCode
                + " ErrorDescription: " + errorDesc);
            throw new JIException(errorCode,
                "An error was occurred when calling an operation in a Hyper-V machine "
                    + "ErrorCode: " + errorCode + ". ErrorDescription: " + errorDesc);

        }
    }

    public static JIVariant[][] enumToJIVariantArray(JIVariant[] set) throws JIException
    {
        IJIDispatch dispatchTemp =
            (IJIDispatch) narrowObject(set[0].getObjectAsComObject()
                .queryInterface(IJIDispatch.IID));
        JIVariant toConvertVariant = dispatchTemp.get("_NewEnum");
        IJIComObject comObjectTemp = toConvertVariant.getObjectAsComObject();
        IJIEnumVariant enumVariant =
            (IJIEnumVariant) narrowObject(comObjectTemp.queryInterface(IJIEnumVariant.IID));
        ArrayList<JIVariant[]> res = new ArrayList<JIVariant[]>();
        int i = 0, threshold = 1000000;
        while (true)
        {
            Object[] values = null;
            try
            {
                values = enumVariant.next(1);
            }
            catch (JIException e)
            {
                break;
            }
            if (values != null)
            {
                JIArray array = (JIArray) values[0];
                res.add((JIVariant[]) array.getArrayInstance());
            }
            i++;
            if (i >= threshold)
            {
                break;
            }
        }
        return res.toArray(new JIVariant[res.size()][]);
    }

    /**
     * Gets an array of string of the parameter property from the dispatch object passed as
     * parameter
     * 
     * @param dispatch the dispatch to get the property from
     * @param property the property to get
     * @return an array of the string
     * @throws JIException
     */
    public static String[] getArrayString(IJIDispatch dispatch, String property) throws JIException
    {
        JIVariant[] tmp =
            (JIVariant[]) dispatch.get(property).getObjectAsArray().getArrayInstance();
        String[] res = new String[tmp.length];
        for (int i = 0; i < tmp.length; i++)
        {
            res[i] = tmp[i].getObjectAsString2();
        }
        return res;
    }

    /**
     * Gets the Device number of a mounted Lun from is IQN
     * 
     * @param dispatch the WMI service object dispatcher
     * @param iqn the iqn to get the device from
     * @param lunId the LUN
     * @return the device number
     * @throws JIException
     */
    public static int getDeviceNumberFromMountedLUN(IJIDispatch objectDispatcher, String iqn,
        String lunId) throws JIException
    {
        Integer lun = Integer.parseInt(lunId);

        String query = "Select * From MSiSCSIInitiator_SessionClass Where TargetName='" + iqn + "'";

        JIVariant[] res =
            objectDispatcher.callMethodA("ExecQuery", new Object[] {new JIString(query)});

        JIVariant[][] initiator_sesion = HyperVUtils.enumToJIVariantArray(res);

        if (initiator_sesion.length == 0)
        {
            throw new JIException(32768, "An initiator session can't be found for this iqn : "
                + iqn);
        }

        JIVariant initiationSerssionVariant = initiator_sesion[0][0];

        IJIDispatch sessionDispatch =
            (IJIDispatch) JIObjectFactory.narrowObject(initiationSerssionVariant
                .getObjectAsComObject().queryInterface(IJIDispatch.IID));

        String sessionId = sessionDispatch.get("SessionId").getObjectAsString2();

        JIVariant devicesResult = sessionDispatch.get("Devices");

        JIArray devicesArray = devicesResult.getObjectAsArray();

        if (devicesArray == null)
        {
            throw new JIException(32768, "The resource could not be added");
        }

        JIVariant[] devicesVariantArray = (JIVariant[]) devicesArray.getArrayInstance();

        if (devicesVariantArray.length == 0)
        {
            throw new JIException(32768,
                "No device mounted was found in the hypervisor for the iqn: " + iqn);
        }

        int deviceNumber = 0;

        for (int i = 0; i < devicesVariantArray.length; i++)
        {
            JIVariant deviceVariant = devicesVariantArray[i];

            IJIDispatch deviceDispatch =
                (IJIDispatch) JIObjectFactory.narrowObject(deviceVariant.getObjectAsComObject()
                    .queryInterface(IJIDispatch.IID));
            IJIUnsigned scsiLun = deviceDispatch.get("ScsiLun").getObjectAsUnsigned();
            int scsiLunValue = scsiLun.getValue().intValue();

            if (scsiLunValue == lun)
            {
                deviceNumber = deviceDispatch.get("DeviceNumber").getObjectAsInt();
                return deviceNumber;
            }

        }

        throw new JIException(32768, "Not device was found with lun: " + lunId);

    }

    /**
     * Gets the session logged in the iSCSI from the IQN of the mounted LUN
     * 
     * @param objectDispatcher the resource object dispatcher
     * @param iqn the iqn is needed to get the session associated
     * @return the session ID
     * @throws JIException
     */
    public static String getSessionIDFromMountedLun(IJIDispatch objectDispatcher, String iqn)
        throws JIException
    {
        String query = "Select * From MSiSCSIInitiator_SessionClass Where TargetName='" + iqn + "'";

        JIVariant[] res =
            objectDispatcher.callMethodA("ExecQuery", new Object[] {new JIString(query)});

        JIVariant[][] initiator_sesion = HyperVUtils.enumToJIVariantArray(res);

        JIVariant initiationSerssionVariant = initiator_sesion[0][0];

        IJIDispatch sessionDispatch =
            (IJIDispatch) JIObjectFactory.narrowObject(initiationSerssionVariant
                .getObjectAsComObject().queryInterface(IJIDispatch.IID));

        String sessionId = sessionDispatch.get("SessionId").getObjectAsString2();

        return sessionId;
    }

    /**
     * Gets the physical disk resource allocation setting data from the matrix that contains the
     * disk drive path as host property.
     * 
     * @param diskDrivePath the disk drive path to get
     * @param physicalDiskList
     * @return the resource associated with the mounted lun disk
     * @throws JIException
     */
    public static IJIDispatch getPhysicalDiskResourceByDiskDrivePath(String diskDrivePath,
        JIVariant[][] physicalDiskList) throws JIException
    {
        IJIDispatch targetphysicalDiskResource = null;

        for (int i = 0; i < physicalDiskList.length; i++)
        {
            IJIDispatch physicalDiskResource =
                (IJIDispatch) JIObjectFactory.narrowObject(physicalDiskList[i][0]
                    .getObjectAsComObject().queryInterface(IJIDispatch.IID));
            JIVariant hostResult = physicalDiskResource.get("HostResource");

            JIArray hostsArray = hostResult.getObjectAsArray();

            JIVariant[] hostsVariantArray = (JIVariant[]) hostsArray.getArrayInstance();

            // Getting just the first host
            String host = hostsVariantArray[0].getObjectAsString2();

            if (diskDrivePath.equals(host))
            {
                targetphysicalDiskResource = physicalDiskResource;
                return targetphysicalDiskResource;
            }
        }

        return targetphysicalDiskResource;

    }

    /**
     * Creates new instance of the class
     * 
     * @param objectDispatcher the service where the class is located
     * @param instance the class name
     * @return the dispatch of the instance
     * @throws JIException
     */
    public static IJIDispatch createNewInstance(IJIDispatch objectDispatcher, String instance)
        throws JIException
    {
        IJIDispatch instanceClass =
            (IJIDispatch) JIObjectFactory.narrowObject(objectDispatcher.callMethodA("Get",
                new Object[] {new JIString(instance)})[0].getObjectAsComObject().queryInterface(
                IJIDispatch.IID));

        JIVariant[] tmp = instanceClass.callMethodA("SpawnInstance_", null);

        return (IJIDispatch) JIObjectFactory.narrowObject(tmp[0].getObjectAsComObject()
            .queryInterface(IJIDispatch.IID));
    }
}
