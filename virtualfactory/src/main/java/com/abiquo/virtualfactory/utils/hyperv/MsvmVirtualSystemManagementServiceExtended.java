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

/**
 * Copyright (c) 2009, Hyper9 All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. Neither the name of Hyper9 nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission. THIS SOFTWARE IS PROVIDED
 * BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

//Changes done: this class is just an extension of MsvmVirtualSystemManagementService class

package com.abiquo.virtualfactory.utils.hyperv;

import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.IJIComObject;
import org.jinterop.dcom.core.JIArray;
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
import com.hyper9.jwbem.msvm.virtualsystemmanagement.MsvmVirtualSystemManagementService;

/**
 * Extended MsvmVirtualSystemManagementService adding new needed features
 * 
 * @author pnavarro
 */
public class MsvmVirtualSystemManagementServiceExtended extends MsvmVirtualSystemManagementService
{
    private IJIDispatch dispatch = null;

    private SWbemMethod defineVirtualSystem;

    private SWbemMethod addVirtualSystemResources;

    private SWbemMethod destroyVirtualSystem;

    /** The logger. */
    private static final Logger logger =
        LoggerFactory.getLogger(MsvmVirtualSystemManagementServiceExtended.class);

    /**
     * Initializes a new instance of the MsvmVirtualSystemManagementService class.
     * 
     * @param dispatch The dispatch object
     * @param service The service connection
     */
    public MsvmVirtualSystemManagementServiceExtended(IJIDispatch dispatch, SWbemServices service)
    {
        super(dispatch, service);
        this.dispatch = dispatch;
    }

    /**
     * Gets the hosts MsvmVirtualSystemManagementServiceExtended.
     * 
     * @param service The service connection.
     * @return The hosts MsvmVirtualSystemManagementServiceExtended.
     * @throws Exception When an error occurs.
     */
    static public MsvmVirtualSystemManagementServiceExtended getManagementServiceExtended(
        final SWbemServices service) throws Exception
    {
        // Get the management service.
        final String wql = "SELECT * FROM Msvm_VirtualSystemManagementService";
        final SWbemObjectSet<MsvmVirtualSystemManagementServiceExtended> objSetMgmtSvc =
            service.execQuery(wql, MsvmVirtualSystemManagementServiceExtended.class);
        final int size = objSetMgmtSvc.getSize();
        if (size != 1)
        {
            throw new Exception("There should be exactly 1 Msvm_VirtualSystemManagementService");
        }
        final MsvmVirtualSystemManagementServiceExtended mgmtSvc = objSetMgmtSvc.iterator().next();
        return mgmtSvc;
    }

    /**
     * Destroys a virtual system in hyper-v
     * 
     * @param vmDispatch TODO
     * @return the error code of
     */
    public int destroyVirtualSystem(IJIDispatch vmDispatch) throws Exception
    {
        JIVariant tmp = vmDispatch.get("Path_");
        IJIDispatch dispatchTemp =
            (IJIDispatch) JIObjectFactory.narrowObject(tmp.getObjectAsComObject().queryInterface(
                IJIDispatch.IID));
        String virtualSystemPath = dispatchTemp.get("Path").getObjectAsString2();
        JIVariant[] results =
            dispatch.callMethodA("DestroyVirtualSystem", new Object[] {
            new JIString(virtualSystemPath), JIVariant.EMPTY_BYREF(), JIVariant.EMPTY_BYREF()});
        int error = results[0].getObjectAsInt();
        if (results.length > 1)
        {
            if (error != 0)
            {
                if (error == 4096)
                {
                    logger.debug("Destroying virtual system...");
                    String jobPath = results[2].getObjectAsVariant().getObjectAsString2();
                    HyperVUtils.monitorJob(jobPath, service.getObjectDispatcher());
                }
                else
                {
                    String message =
                        "The virtual system could no te destroyed " + virtualSystemPath;
                    logger.error(message);
                    throw new JIException(error, message);
                }
            }
        }
        return error;
    }

    /**
     * Destroys a virtual system in hyper-v
     * 
     * @param vmDispatch A reference to the virtual computer system instance to be destroyed.
     */
    public void destroyVirtualSystem2(IJIDispatch vmDispatch) throws Exception
    {
        if (this.destroyVirtualSystem == null)
        {
            for (final SWbemMethod m : super.getMethods())
            {
                if (m.getName().equals("DestroyVirtualSystem"))
                {
                    this.destroyVirtualSystem = m;
                }
            }
        }

        JIVariant tmp = vmDispatch.get("Path_");
        IJIDispatch dispatchTemp =
            (IJIDispatch) JIObjectFactory.narrowObject(tmp.getObjectAsComObject().queryInterface(
                IJIDispatch.IID));
        String virtualSystemPath = dispatchTemp.get("Path").getObjectAsString2();

        SWbemObject inParams = this.destroyVirtualSystem.getInParameters();

        inParams.getObjectDispatcher().put("ComputerSystem",
            new JIVariant(new JIString(virtualSystemPath)));

        Object[] methodParams =
            new Object[] {new JIString("DestroyVirtualSystem"),
            new JIVariant(inParams.getObjectDispatcher()), new Integer(0), JIVariant.NULL(),};

        // Execute the method.
        super.objectDispatcher.callMethodA("ExecMethod_", methodParams);
        // JIVariant[] tmp =
        // dispatch.callMethodA("DefineVirtualSystem", new Object[] {
        // new JIString(globalSettingDataText), JIVariant.OPTIONAL_PARAM(),
        // JIVariant.OPTIONAL_PARAM(),});
        // int defRes = tmp[0].getObjectAsInt();
    }

    /**
     * Creates a new virtual computer system definition.
     * 
     * @param globalSettingDataText the Global setting data text of the virtual system to create
     * @throws JIException
     */
    public void defineVirtualSystem(String globalSettingDataText) throws JIException
    {
        if (this.defineVirtualSystem == null)
        {
            for (final SWbemMethod m : super.getMethods())
            {
                if (m.getName().equals("DefineVirtualSystem"))
                {
                    this.defineVirtualSystem = m;
                }
            }
        }

        SWbemObject inParams = this.defineVirtualSystem.getInParameters();

        inParams.getObjectDispatcher().put("SystemSettingData",
            new JIVariant(new JIString(globalSettingDataText)));

        Object[] methodParams =
            new Object[] {new JIString("DefineVirtualSystem"),
            new JIVariant(inParams.getObjectDispatcher()), new Integer(0), JIVariant.NULL(),};

        // Execute the method.
        JIVariant[] results = super.objectDispatcher.callMethodA("ExecMethod_", methodParams);

        // Get the out parameters.
        // JIVariant outParamsVar = results[0];
        // IJIComObject co = outParamsVar.getObjectAsComObject();
        // IJIDispatch outParamsDisp = (IJIDispatch) JIObjectFactory.narrowObject(co);

        // Get the out parameter virtualSystemResources and convert it into an
        // array of JIVariants.
        // JIVariant definedSystem = outParamsDisp.get("DefinedSystem");
    }

    /**
     * Add resources to an existing virtual computer system
     * 
     * @param vmDispatch A reference to the computer system instance to which the resource is to be
     *            added.
     * @param newResourceAllocationDispatch the new resource allocation setting data reference to be
     *            added.
     * @return the resource allocation setting data path of the added resource
     * @throws JIException
     */
    public String addVirtualSystemResources(IJIDispatch vmDispatch,
        IJIDispatch newResourceAllocationDispatch) throws JIException
    {
        // Getting the dispatcher of the VM Path
        IJIDispatch vmPathDispatcher =
            (IJIDispatch) JIObjectFactory.narrowObject(vmDispatch.get("Path_")
                .getObjectAsComObject().queryInterface(IJIDispatch.IID));

        // Getting the virtual machine path
        String vmPath = vmPathDispatcher.get("Path").getObjectAsString2();

        // Getting the dispatcher of the resource path

        String resourceText =
            newResourceAllocationDispatch.callMethodA("GetText_", new Object[] {new Integer(1)})[0]
                .getObjectAsString2();

        JIVariant[] tmp =
            dispatch.callMethodA("AddVirtualSystemResources", new Object[] {new JIString(vmPath),
            new JIArray(new JIString[] {new JIString(resourceText)}), JIVariant.EMPTY_BYREF(),
            JIVariant.EMPTY_BYREF()});

        int result = tmp[0].getObjectAsInt();

        JIVariant resultVariant = tmp[2];

        JIVariant variant2 = resultVariant.getObjectAsVariant();

        JIArray newResourcesArr = variant2.getObjectAsArray();

        if (newResourcesArr == null)
        {
            throw new JIException(32768, "The resource could not be added");
        }

        JIVariant[] newResourcesVarArr = (JIVariant[]) newResourcesArr.getArrayInstance();

        String newResourcePath = newResourcesVarArr[0].getObjectAsString2();

        String name = newResourceAllocationDispatch.get("ElementName").getObjectAsString2();
        if (result == 0)
        {
            logger.debug(name + " added to " + vmPath);
        }
        else
        {
            if (result == 4096)
            {
                logger.debug("Addind resources...");
                String jobPath = tmp[1].getObjectAsVariant().getObjectAsString2();
                HyperVUtils.monitorJob(jobPath, service.getObjectDispatcher());
            }
            else
            {
                logger.error(name + " addition to " + vmPath + " failed with error code " + result);
                throw new IllegalStateException("Cannot add resource " + name + " to "
                    + vmDispatch.get("ElementName").getObjectAsString2());
            }
        }

        return newResourcePath;
    }

    /**
     * Add resources to an existing virtual computer system
     * 
     * @param vmDispatch A reference to the computer system instance to which the resource is to be
     *            added.
     * @param newResourceAllocationDispatch the new resource allocation setting data reference to be
     *            added.
     * @return the resource allocation setting data path of the added resource
     * @deprecated
     * @throws JIException
     */
    public String addVirtualSystemResources2(IJIDispatch vmDispatch,
        IJIDispatch newResourceAllocationDispatch) throws JIException
    {
        if (this.addVirtualSystemResources == null)
        {
            for (final SWbemMethod m : super.getMethods())
            {
                if (m.getName().equals("AddVirtualSystemResources"))
                {
                    this.addVirtualSystemResources = m;
                }
            }
        }

        // Getting the dispatcher of the VM Path
        IJIDispatch vmPathDispatcher =
            (IJIDispatch) JIObjectFactory.narrowObject(vmDispatch.get("Path_")
                .getObjectAsComObject().queryInterface(IJIDispatch.IID));

        // Getting the virtual machine path
        String vmPath = vmPathDispatcher.get("Path").getObjectAsString2();

        // Getting the dispatcher of the resource path

        String resourceText =
            newResourceAllocationDispatch.callMethodA("GetText_", new Object[] {new Integer(1)})[0]
                .getObjectAsString2();

        SWbemObject inParams = this.addVirtualSystemResources.getInParameters();

        inParams.getObjectDispatcher().put("TargetSystem", new JIVariant(new JIString(vmPath)));

        inParams.getObjectDispatcher().put("ResourceSettingData",
            new JIVariant(new JIArray(new JIString[] {new JIString(resourceText)})));

        Object[] methodParams =
            new Object[] {new JIString("AddVirtualSystemResources"),
            new JIVariant(inParams.getObjectDispatcher()), new Integer(0), JIVariant.NULL(),};

        // Execute the method.
        JIVariant[] results = super.objectDispatcher.callMethodA("ExecMethod_", methodParams);

        // Get the out parameters.
        JIVariant outParamsVar = results[0];
        IJIComObject co = outParamsVar.getObjectAsComObject();
        IJIDispatch outParamsDisp = (IJIDispatch) JIObjectFactory.narrowObject(co);

        // Get the out parameter virtualSystemResources and convert it into an
        // array of JIVariants.
        JIVariant newResourcesVars = outParamsDisp.get("NewResources");
        JIArray newResourcesVarsJIArr = newResourcesVars.getObjectAsArray();
        JIVariant[] newResourcesVarsJIVarArr =
            (JIVariant[]) newResourcesVarsJIArr.getArrayInstance();

        String newResourceCoString = newResourcesVarsJIVarArr[0].getObjectAsString2();

        return newResourceCoString;

    }

    /**
     * Add resources to an existing virtual computer system
     * 
     * @param vmDispatch A reference to the computer system instance to which the resource is to be
     *            added.
     * @param newResourceAllocationDispatch the new resource allocation setting data reference to be
     *            added.
     * @throws JIException
     */
    public void addVirtualSystemResourcesVoid(IJIDispatch vmDispatch,
        IJIDispatch newResourceAllocationDispatch) throws JIException
    {
        // Getting the dispatcher of the VM Path
        IJIDispatch vmPathDispatcher =
            (IJIDispatch) JIObjectFactory.narrowObject(vmDispatch.get("Path_")
                .getObjectAsComObject().queryInterface(IJIDispatch.IID));

        // Getting the virtual machine path
        String vmPath = vmPathDispatcher.get("Path").getObjectAsString2();

        // Getting the dispatcher of the resource path

        String resourceText =
            newResourceAllocationDispatch.callMethodA("GetText_", new Object[] {new Integer(1)})[0]
                .getObjectAsString2();

        JIVariant[] tmp =
            dispatch.callMethodA("AddVirtualSystemResources", new Object[] {new JIString(vmPath),
            new JIArray(new JIString[] {new JIString(resourceText)}), JIVariant.EMPTY_BYREF(),
            JIVariant.EMPTY_BYREF()});

        int result = tmp[0].getObjectAsInt();

        String name = newResourceAllocationDispatch.get("ElementName").getObjectAsString2();
        if (result == 0)
        {
            logger.debug(name + " added to " + vmPath);
        }
        else
        {
            if (result == 4096)
            {
                logger.debug("Addind resources...");
                String jobPath = tmp[1].getObjectAsVariant().getObjectAsString2();
                HyperVUtils.monitorJob(jobPath, service.getObjectDispatcher());
            }
            else
            {
                logger.error(name + " addition to " + vmPath + " failed with error code " + result);
                throw new IllegalStateException("Cannot add resource " + name + " to "
                    + vmDispatch.get("ElementName").getObjectAsString2());
            }
        }
    }

    /**
     * Removes resources to an existing virtual computer system
     * 
     * @param vmDispatch A reference to the computer system instance to which the resource is to be
     *            added.
     * @param resourceAllocationDispatch the resource allocation setting data reference to be
     *            removed.
     * @throws JIException
     */
    public void removeVirtualSystemResources(IJIDispatch vmDispatch,
        IJIDispatch resourceAllocationDispatch) throws JIException
    {
        // Getting the dispatcher of the VM Path
        IJIDispatch vmPathDispatcher =
            (IJIDispatch) JIObjectFactory.narrowObject(vmDispatch.get("Path_")
                .getObjectAsComObject().queryInterface(IJIDispatch.IID));

        // Getting the virtual machine path
        String vmPath = vmPathDispatcher.get("Path").getObjectAsString2();

        // Getting the dispatcher of the resource path

        String resourceText =
            resourceAllocationDispatch.callMethodA("GetText_", new Object[] {new Integer(1)})[0]
                .getObjectAsString2();

        JIVariant[] tmp =
            dispatch.callMethodA("RemoveVirtualSystemResources", new Object[] {
            new JIString(vmPath), new JIArray(new JIString[] {new JIString(resourceText)}),
            JIVariant.EMPTY_BYREF(), JIVariant.EMPTY_BYREF()});

        int result = tmp[0].getObjectAsInt();

        String name = resourceAllocationDispatch.get("ElementName").getObjectAsString2();

        if (result == 0)
        {
            logger.debug(name + " added to " + vmPath);
        }
        else
        {
            if (result == 4096)
            {
                logger.debug("Removing resources...");
                String jobPath = tmp[1].getObjectAsVariant().getObjectAsString2();
                HyperVUtils.monitorJob(jobPath, service.getObjectDispatcher());
            }
            else
            {
                logger.error(name + " deleting to " + vmPath + " failed with error code " + result);
                throw new IllegalStateException("Cannot remove resource " + name + " to "
                    + vmDispatch.get("ElementName").getObjectAsString2());
            }
        }

    }

    /**
     * Modifies the setting data for existing resources on a virtual computer system
     * 
     * @param vmDispatch A reference to the virtual computer system whose resources are to be
     *            modified.
     * @param modifiedResourceAllocationSettingData the resource allocation setting data reference
     *            to be modified.
     * @throws JIException
     */
    public void modifyVirtualSystemResources(IJIDispatch vmDispatch,
        IJIDispatch modifiedResourceAllocationSettingData) throws JIException
    {
        // Getting the dispatcher of the VM Path
        IJIDispatch vmPathDispatcher =
            (IJIDispatch) JIObjectFactory.narrowObject(vmDispatch.get("Path_")
                .getObjectAsComObject().queryInterface(IJIDispatch.IID));

        // Getting the virtual machine path
        String vmPath = vmPathDispatcher.get("Path").getObjectAsString2();

        // Getting the dispatcher of the resource path

        String resourceText =
            modifiedResourceAllocationSettingData.callMethodA("GetText_",
                new Object[] {new Integer(1)})[0].getObjectAsString2();

        JIVariant[] tmp =
            dispatch.callMethodA("ModifyVirtualSystemResources", new Object[] {
            new JIString(vmPath), new JIArray(new JIString[] {new JIString(resourceText)}),
            JIVariant.EMPTY_BYREF(), JIVariant.EMPTY_BYREF()});

        int result = tmp[0].getObjectAsInt();

        String name = modifiedResourceAllocationSettingData.get("ElementName").getObjectAsString2();
        if (result == 0)
        {
            logger.debug(name + " added to " + vmPath);
        }
        else
        {
            if (result == 4096)
            {
                logger.debug("Modifying resources...");
                String jobPath = tmp[1].getObjectAsVariant().getObjectAsString2();
                HyperVUtils.monitorJob(jobPath, service.getObjectDispatcher());
            }
            else
            {
                logger.error(name + " modification to " + vmPath + " failed with error code "
                    + result);
                throw new IllegalStateException("Cannot modify resource " + name + " to "
                    + vmDispatch.get("ElementName").getObjectAsString2());
            }
        }
    }

    /**
     * Modifies the settings for an existing virtual computer system.
     * 
     * @param vmDispatch A reference to the virtual computer system to be modified.
     * @param systemSettingData describes the modified setting values for the virtual computer
     *            system.
     * @throws JIException
     */
    public void modifyVirtualSystem(IJIDispatch vmDispatch, IJIDispatch systemSettingData)
        throws JIException
    {
        // Getting the dispatcher of the VM Path
        IJIDispatch vmPathDispatcher =
            (IJIDispatch) JIObjectFactory.narrowObject(vmDispatch.get("Path_")
                .getObjectAsComObject().queryInterface(IJIDispatch.IID));

        // Getting the virtual machine path
        String vmPath = vmPathDispatcher.get("Path").getObjectAsString2();

        // Getting the dispatcher of the resource path

        String resourceText =
            systemSettingData.callMethodA("GetText_", new Object[] {new Integer(1)})[0]
                .getObjectAsString2();

        JIVariant[] tmp =
            dispatch.callMethodA("ModifyVirtualSystem", new Object[] {new JIString(vmPath),
            new JIArray(new JIString[] {new JIString(resourceText)}), JIVariant.EMPTY_BYREF(),
            JIVariant.EMPTY_BYREF()});

        int result = tmp[0].getObjectAsInt();

        String name = systemSettingData.get("ElementName").getObjectAsString2();
        if (result == 0)
        {
            logger.debug("Setting data properly modified" + vmPath);
        }
        else
        {
            if (result == 4096)
            {
                logger.debug("Modifying setting data...");
                String jobPath = tmp[1].getObjectAsVariant().getObjectAsString2();
                HyperVUtils.monitorJob(jobPath, service.getObjectDispatcher());
            }
            else
            {
                logger.error(name + " addition to " + vmPath + " failed with error code " + result);
                throw new IllegalStateException("Setting data cannot be modified to"
                    + vmDispatch.get("ElementName").getObjectAsString2());
            }
        }
    }
}
