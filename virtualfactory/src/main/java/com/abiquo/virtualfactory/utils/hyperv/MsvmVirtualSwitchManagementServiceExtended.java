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

//Changes done: this class is just an extension of MsvmVirtualSwitchManagementService class

package com.abiquo.virtualfactory.utils.hyperv;

import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.IJIComObject;
import org.jinterop.dcom.core.JIString;
import org.jinterop.dcom.core.JIVariant;
import org.jinterop.dcom.impls.JIObjectFactory;
import org.jinterop.dcom.impls.automation.IJIDispatch;

import com.hyper9.jwbem.SWbemMethod;
import com.hyper9.jwbem.SWbemObject;
import com.hyper9.jwbem.SWbemObjectSet;
import com.hyper9.jwbem.SWbemServices;
import com.hyper9.jwbem.msvm.networking.MsvmVirtualSwitchManagementService;

/**
 * @author pnavarro
 */
public class MsvmVirtualSwitchManagementServiceExtended extends MsvmVirtualSwitchManagementService
{
    private SWbemMethod createSwitch;

    private IJIDispatch dispatch;

    private SWbemMethod createSwitchPort;

    private SWbemMethod deleteSwitchPort;

    public MsvmVirtualSwitchManagementServiceExtended(IJIDispatch objectDispatcher,
        SWbemServices service)
    {
        super(objectDispatcher, service);
        this.dispatch = objectDispatcher;
    }

    /**
     * Gets the host's MsvmVirtualSwitchManagementService.
     * 
     * @param service The service connection.
     * @return The host's MsvmVirtualSwitchManagementService.
     * @throws Exception When an error occurs.
     */
    static public MsvmVirtualSwitchManagementServiceExtended getManagementService(
        final SWbemServices service) throws Exception
    {
        // Get the management service.
        final String wql = "SELECT * FROM Msvm_VirtualSwitchManagementService";
        final SWbemObjectSet<MsvmVirtualSwitchManagementServiceExtended> objSetMgmtSvc =
            service.execQuery(wql, MsvmVirtualSwitchManagementServiceExtended.class);
        final int size = objSetMgmtSvc.getSize();
        if (size != 1)
        {
            throw new Exception("There should be exactly 1 Msvm_VirtualSwitchManagementService");
        }
        final MsvmVirtualSwitchManagementServiceExtended mgmtSvc = objSetMgmtSvc.iterator().next();
        return mgmtSvc;
    }

    /**
     * Creates a new virtual switch.
     * 
     * @param name The name of the switch. This name must be unique to all virtual switches in the
     *            system.
     * @param friendlyName A user-readable name for the switch
     * @param numLearnableAddresses The maximum number of MAC addresses that can be learned by the
     *            switch
     * @return the object path of the created switch
     * @throws JIException
     */
    public String createSwitch(String name, String friendlyName, int numLearnableAddresses)
        throws JIException
    {
        if (this.createSwitch == null)
        {
            for (final SWbemMethod m : super.getMethods())
            {
                if (m.getName().equals("CreateSwitch"))
                {
                    this.createSwitch = m;
                }
            }
        }

        SWbemObject inParams = this.createSwitch.getInParameters();

        inParams.getObjectDispatcher().put("Name", new JIVariant(new JIString(name)));

        inParams.getObjectDispatcher().put("FriendlyName",
            new JIVariant(new JIString(friendlyName)));

        inParams.getObjectDispatcher().put("NumLearnableAddresses",
            new JIVariant(numLearnableAddresses));

        inParams.getObjectDispatcher().put("ScopeOfResidence", JIVariant.NULL());

        Object[] methodParams =
            new Object[] {new JIString("CreateSwitch"),
            new JIVariant(inParams.getObjectDispatcher()), new Integer(0), JIVariant.NULL(),};

        // Execute the method.
        JIVariant[] results = super.objectDispatcher.callMethodA("ExecMethod_", methodParams);

        // Get the out parameters.
        JIVariant outParamsVar = results[0];
        IJIComObject co = outParamsVar.getObjectAsComObject();
        IJIDispatch outParamsDisp = (IJIDispatch) JIObjectFactory.narrowObject(co);

        // Get the out parameter virtualSystemResources and convert it into an
        // array of JIVariants.
        JIVariant createdSwitch = outParamsDisp.get("CreatedVirtualSwitch");
        String switchPath = createdSwitch.getObjectAsString2();
        return switchPath;

    }

    public String createSwitchPort(final String virtualSwitchPath, final String name,
        final String friendlyName, final String scope) throws Exception
    {
        if (this.createSwitchPort == null)
        {
            for (final SWbemMethod m : super.getMethods())
            {
                if (m.getName().equals("CreateSwitchPort"))
                {
                    this.createSwitchPort = m;
                }
            }
        }

        // Get the IN parameters.
        SWbemObject inParams = this.createSwitchPort.getInParameters();
        inParams.getObjectDispatcher().put("VirtualSwitch",
            new JIVariant(new JIString(virtualSwitchPath)));
        inParams.getObjectDispatcher().put("Name", new JIVariant(new JIString(name)));
        inParams.getObjectDispatcher().put("FriendlyName",
            new JIVariant(new JIString(friendlyName)));
        if (scope == null)
        {
            inParams.getObjectDispatcher().put("ScopeOfResidence", JIVariant.NULL());
        }
        else
        {
            inParams.getObjectDispatcher().put("ScopeOfResidence",
                new JIVariant(new JIString(scope)));
        }

        Object[] methodParams =
            new Object[] {new JIString("CreateSwitchPort"),
            new JIVariant(inParams.getObjectDispatcher()), new Integer(0), JIVariant.NULL(),};

        JIVariant[] results = super.objectDispatcher.callMethodA("ExecMethod_", methodParams);

        // Get the out parameters.
        JIVariant outParamsVar = results[0];
        IJIComObject co = outParamsVar.getObjectAsComObject();
        IJIDispatch outParamsDisp = (IJIDispatch) JIObjectFactory.narrowObject(co);

        // Get the out parameter CreatedSwitchPort and convert it into an
        // array of JIVariants.
        JIVariant cspVar = outParamsDisp.get("CreatedSwitchPort");
        String cspPath = cspVar.getObjectAsString2();

        return cspPath;
    }

    public void deleteSwitchPort(final String virtualSwitchPath) throws Exception
    {
        if (this.deleteSwitchPort == null)
        {
            for (final SWbemMethod m : super.getMethods())
            {
                if (m.getName().equals("DeleteSwitchPort"))
                {
                    this.deleteSwitchPort = m;
                }
            }
        }

        // Get the IN parameters.
        SWbemObject inParams = this.deleteSwitchPort.getInParameters();
        inParams.getObjectDispatcher().put("SwitchPort",
            new JIVariant(new JIString(virtualSwitchPath)));
        Object[] methodParams =
            new Object[] {new JIString("DeleteSwitchPort"),
            new JIVariant(inParams.getObjectDispatcher()), new Integer(0), JIVariant.NULL(),};

        JIVariant[] results = super.objectDispatcher.callMethodA("ExecMethod_", methodParams);
    }

}
