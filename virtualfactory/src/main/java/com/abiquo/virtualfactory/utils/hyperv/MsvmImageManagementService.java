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

import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.IJIComObject;
import org.jinterop.dcom.core.JIString;
import org.jinterop.dcom.core.JIVariant;
import org.jinterop.dcom.impls.JIObjectFactory;
import org.jinterop.dcom.impls.automation.IJIDispatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hyper9.jwbem.SWbemMethod;
import com.hyper9.jwbem.SWbemObject;
import com.hyper9.jwbem.SWbemObjectSet;
import com.hyper9.jwbem.SWbemServices;
import com.hyper9.jwbem.msvm.MsvmObject;

/**
 * This class represents the Msvm_ImageManagementService class.
 * 
 * @author pnavarro
 */
public class MsvmImageManagementService extends MsvmObject
{
    private SWbemMethod convertVirtualHardDisk;

    private SWbemMethod createVirtualHardDisk;

    public static final int Fixed = 2;

    public static final int Dynamic = 3;

    public static final int PhysicalDrive = 5;

    private IJIDispatch dispatch;

    private SWbemServices service;

    /** The logger. */
    private static final Logger logger = LoggerFactory.getLogger(MsvmImageManagementService.class);

    /**
     * Initializes a new instance of the MsvmVirtualSystemManagementService class.
     * 
     * @param dispatch The dispatch object.
     * @param service The service connection.
     */
    public MsvmImageManagementService(IJIDispatch dispatch, SWbemServices service)
    {
        super(dispatch, service);
        this.dispatch = dispatch;
        this.service = service;
    }

    /**
     * Gets the hosts Msvm_ImageManagementService.
     * 
     * @param service The service connection.
     * @return The hosts Msvm_ImageManagementService.
     * @throws Exception When an error occurs.
     */
    static public MsvmImageManagementService getManagementService(final SWbemServices service)
        throws Exception
    {
        // Get the management service.
        final String wql = "SELECT * FROM Msvm_ImageManagementService";
        final SWbemObjectSet<MsvmImageManagementService> objSetMgmtSvc =
            service.execQuery(wql, MsvmImageManagementService.class);
        final int size = objSetMgmtSvc.getSize();
        if (size != 1)
        {
            throw new Exception("There should be exactly 1 Msvm_ImageManagementService");
        }
        final MsvmImageManagementService mgmtSvc = objSetMgmtSvc.iterator().next();
        return mgmtSvc;
    }

    /**
     * Converts the type of an existing virtual hard disk.
     * 
     * @param sourcePath A fully-qualified path that specifies the location of the virtual hard disk
     *            file. This file will not be modified as a result of this operation
     * @param destinationPath A fully-qualified path that specifies the location of the destination
     *            virtual hard disk file.
     * @param type The type of the new virtual hard disk file. 2 Fixed, 3 Dynamic, 5 PhysicalDrive
     * @throws JIException
     */
    public void convertVirtualHardDisk2(String sourcePath, String destinationPath, int type)
        throws JIException
    {
        if (this.convertVirtualHardDisk == null)
        {
            for (final SWbemMethod m : super.getMethods())
            {
                if (m.getName().equals("ConvertVirtualHardDisk"))
                {
                    this.convertVirtualHardDisk = m;
                }
            }
        }

        SWbemObject inParams = this.convertVirtualHardDisk.getInParameters();

        inParams.getObjectDispatcher().put("SourcePath", new JIVariant(sourcePath));
        inParams.getObjectDispatcher().put("DestinationPath", new JIVariant(destinationPath));
        inParams.getObjectDispatcher().put("Type", new JIVariant(type));

        Object[] methodParams =
            new Object[] {new JIString("ConvertVirtualHardDisk"),
            new JIVariant(inParams.getObjectDispatcher()), new Integer(0), JIVariant.NULL(),};

        // Execute the method.
        JIVariant[] results = super.objectDispatcher.callMethodA("ExecMethod_", methodParams);

        // Get the out parameters.
        JIVariant outParamsVar = results[0];
        IJIComObject co = outParamsVar.getObjectAsComObject();
        IJIDispatch outParamsDisp = (IJIDispatch) JIObjectFactory.narrowObject(co);

        // Get the out parameter virtualSystemResources and convert it into an
        // array of JIVariants.
        JIVariant jobVariant = outParamsDisp.get("Job");
        String jobPath = jobVariant.getObjectAsString2();

        // HyperVUtils.monitorJobState(jobPath, dispatch);
    }

    /**
     * Converts the type of an existing virtual hard disk.
     * 
     * @param sourcePath A fully-qualified path that specifies the location of the virtual hard disk
     *            file. This file will not be modified as a result of this operation
     * @param destinationPath A fully-qualified path that specifies the location of the destination
     *            virtual hard disk file.
     * @param type The type of the new virtual hard disk file. 2 Fixed, 3 Dynamic, 5 PhysicalDrive
     * @throws JIException
     */
    public void convertVirtualHardDisk(String sourcePath, String destinationPath, int type)
        throws JIException
    {

        // Execute the method.
        JIVariant[] results =
            dispatch.callMethodA("ConvertVirtualHardDisk", new Object[] {new JIVariant(sourcePath),
            new JIVariant(destinationPath), new JIVariant(type), JIVariant.EMPTY_BYREF(),
            JIVariant.EMPTY_BYREF()});

        int result = results[0].getObjectAsInt();
        if (result == 0)
        {
            logger.debug(sourcePath + " converted to " + destinationPath);
        }
        else
        {
            if (result == 4096)
            {
                logger.debug("Converting disk resources...");
                String jobPath = results[2].getObjectAsVariant().getObjectAsString2();
                HyperVUtils.monitorJob(jobPath, service.getObjectDispatcher());
                // catch (JIException e)
                // {
                // int errorCode = e.getErrorCode();
                // if (errorCode == 32768)
                // {
                // //TODO analyze if the file exists
                // }
                // else
                // {
                // throw e;
                // }
                // }
            }
            else
            {
                logger.error(sourcePath + " couldn't be converted to " + destinationPath
                    + " failed with error code " + result);
                throw new IllegalStateException(sourcePath + " couldn't be converted to "
                    + destinationPath);
            }
        }
    }

    public void createFixedVirtualHardDisk(String destinationPath) throws JIException
    {

        // Execute the method.
        JIVariant[] results =
            dispatch.callMethodA("CreateDynamicVirtualHardDisk", new Object[] {
            new JIVariant(destinationPath), new JIVariant(new Long(500))});

        int result = results[0].getObjectAsInt();
        if (result == 0)
        {
            logger.debug("VHD created in " + destinationPath);
        }
        else
        {
            if (result == 4096)
            {
                logger.debug("Creating disk resources...");
                String jobPath = results[2].getObjectAsVariant().getObjectAsString2();
                HyperVUtils.monitorJob(jobPath, service.getObjectDispatcher());
                // catch (JIException e)
                // {
                // int errorCode = e.getErrorCode();
                // if (errorCode == 32768)
                // {
                // //TODO analyze if the file exists
                // }
                // else
                // {
                // throw e;
                // }
                // }
            }
            else
            {
                logger.error("VHD couldn't be created in " + destinationPath
                    + " failed with error code " + result);
                throw new IllegalStateException("VHD couldn't be created in " + destinationPath);
            }
        }
    }

    public void createFixedVirtualHardDisk2(String destinationPath) throws JIException
    {
        if (this.createVirtualHardDisk == null)
        {
            for (final SWbemMethod m : super.getMethods())
            {
                if (m.getName().equals("CreateFixedVirtualHardDisk"))
                {
                    this.createVirtualHardDisk = m;
                }
            }
        }

        SWbemObject inParams = this.createVirtualHardDisk.getInParameters();

        long mega = 1048576;
        
        inParams.getObjectDispatcher().put("Path", new JIVariant(destinationPath));
        inParams.getObjectDispatcher().put("MaxInternalSize", new JIVariant(3*mega));

        Object[] methodParams =
            new Object[] {new JIString("CreateFixedVirtualHardDisk"),
            new JIVariant(inParams.getObjectDispatcher()), new Integer(0), JIVariant.NULL(),};

        // Execute the method.
        JIVariant[] results = super.objectDispatcher.callMethodA("ExecMethod_", methodParams);
        
        // TODO: ErrorCode: 32768 ErrorDescription: 'The system failed to create 'C:\aquimismo.vhd'. Error Code: The file exists

        // Get the out parameters.
        JIVariant outParamsVar = results[0];
        IJIComObject co = outParamsVar.getObjectAsComObject();
        IJIDispatch outParamsDisp = (IJIDispatch) JIObjectFactory.narrowObject(co);

        // Get the out parameter virtualSystemResources and convert it into an
        // array of JIVariants.
        JIVariant jobVariant = outParamsDisp.get("Job");
        String jobPath = jobVariant.getObjectAsString2();

         HyperVUtils.monitorJob(jobPath, service.getObjectDispatcher());
    }

}
