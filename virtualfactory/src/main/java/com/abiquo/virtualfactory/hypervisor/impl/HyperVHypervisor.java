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
package com.abiquo.virtualfactory.hypervisor.impl;

import static com.abiquo.virtualfactory.utils.hyperv.HyperVUtils.enumToJIVariantArray;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.JIString;
import org.jinterop.dcom.core.JIVariant;
import org.jinterop.dcom.impls.automation.IJIDispatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.virtualfactory.constants.MessageValues;
import com.abiquo.virtualfactory.exception.HypervisorException;
import com.abiquo.virtualfactory.exception.VirtualMachineException;
import com.abiquo.virtualfactory.machine.impl.HyperVMachine;
import com.abiquo.virtualfactory.model.AbiCloudModel;
import com.abiquo.virtualfactory.model.AbsVirtualMachine;
import com.abiquo.virtualfactory.model.IHypervisor;
import com.abiquo.virtualfactory.model.State;
import com.abiquo.virtualfactory.model.config.Configuration;
import com.abiquo.virtualfactory.model.config.HyperVHypervisorConfiguration;
import com.abiquo.virtualfactory.model.config.VirtualMachineConfiguration;
import com.abiquo.virtualfactory.utils.hyperv.HyperVConstants;
import com.hyper9.jwbem.SWbemLocator;
import com.hyper9.jwbem.SWbemObjectSet;
import com.hyper9.jwbem.SWbemServices;
import com.hyper9.jwbem.msvm.virtualsystem.MsvmComputerSystem;

/**
 * Hyper-v class connection connector using DCOM through j-interop
 * 
 * @author pnavarro
 */
public class HyperVHypervisor implements IHypervisor
{

    /** The Constant logger. */
    private final static Logger logger = LoggerFactory.getLogger(HyperVHypervisor.class.getName());

    /**
     * The SWbem service for the virtualization namespace.
     */
    private SWbemServices virtService;

    /**
     * The SWbem service for the win32 namespace.
     */
    private SWbemServices cim2Service;

    /**
     * The SWbem service for the wmi namespace.
     */
    private SWbemServices wmiService;

    /**
     * User
     */
    private String user;

    /**
     * Password
     */
    private String password;

    private URL url;

    /**
     * @see com.abiquo.virtualfactory.model.IHypervisor#connect(java.net.URL)
     */
    @Override
    public void connect(final URL address) throws HypervisorException
    {
        this.url = address;

        try
        {
            SWbemLocator loc = new SWbemLocator();
            virtService =
                loc.connect(address.getHost(), "127.0.0.1", HyperVConstants.VIRTUALIZATION_NS,
                    getUser(), getPassword());
            cim2Service =
                loc.connect(address.getHost(), "127.0.0.1", HyperVConstants.CIM_NS, getUser(),
                    getPassword());

        }
        catch (Exception e)
        {
            logger.debug("An error was occurred when connecting to the hypervisor", e);
            throw new HypervisorException(MessageValues.CONN_EXCP_I, e);
        }

    }

    /**
     * @see com.abiquo.virtualfactory.model.IHypervisor#createMachine(com.abiquo.virtualfactory.model.config.VirtualMachineConfiguration)
     */
    @Override
    public AbsVirtualMachine createMachine(final VirtualMachineConfiguration config)
        throws VirtualMachineException
    {
        return new HyperVMachine(config);
    }

    /**
     * @see com.abiquo.virtualfactory.model.IHypervisor#getAddress()
     */
    @Override
    public URL getAddress()
    {
        return url;
    }

    /**
     * @see com.abiquo.virtualfactory.model.IHypervisor#getHypervisorType()
     */
    @Override
    public String getHypervisorType()
    {
        return HypervisorType.HYPERV_301.getValue();
    }

    /**
     * @see com.abiquo.virtualfactory.model.IHypervisor#init(java.net.URL)
     */
    @Override
    public void init(final URL address, final String user, final String password)
        throws HypervisorException
    {
        AbiCloudModel model = AbiCloudModel.getInstance();
        Configuration mainConfig = model.getConfigManager().getConfiguration();
        HyperVHypervisorConfiguration config = mainConfig.getHypervConfig();
        this.user = user;
        this.password = password;
        url = address;
    }

    /**
     * @see com.abiquo.virtualfactory.model.IHypervisor#login(java.lang.String, java.lang.String)
     */
    @Override
    public void login(final String user, final String password)
    {
        this.user = user;
        this.password = password;

    }

    /**
     * @see com.abiquo.virtualfactory.model.IHypervisor#logout()
     */
    @Override
    public void logout()
    {
        this.getVirtualizationService().getLocator().disconnect();
    }

    /**
     * Reconnects to the hypervisors
     * 
     * @throws HypervisorException
     */
    public void reconnect() throws HypervisorException
    {
        // Tries to execute a test query to test connectivity
        SWbemObjectSet<MsvmComputerSystem> testObject =
            this.getVirtualizationService().execQuery(getVirtServiceTestQuery(),
                MsvmComputerSystem.class);

        if (testObject == null)
        {
            connect(this.url);
        }
    }

    /**
     * Gets the test query in order to test connectivity before reconnect
     * 
     * @return the test query
     */
    private String getVirtServiceTestQuery()
    {
        StringBuilder queryAux = new StringBuilder();
        List<String> propList = new ArrayList<String>();
        propList.add("Name");
        propList.add("ElementName");

        for (int x = 0; x < propList.size(); ++x)
        {
            queryAux.append(propList.get(x));
            if (x < propList.size() - 1)
            {
                queryAux.append(",");
            }
        }

        String qf = "SELECT %s FROM Msvm_ComputerSystem WHERE Caption='Hosting Computer System'";
        String query = String.format(qf, queryAux.toString());

        return query;

    }

    /**
     * Forces reconnection to use
     * 
     * @throws HypervisorException
     */
    public void forceReconnect() throws HypervisorException
    {
        connect(this.url);
    }

    /**
     * Gets the virtualization service
     * 
     * @return the virtService
     */
    public SWbemServices getVirtualizationService()
    {
        return virtService;
    }

    /**
     * Gets the Common information model service
     * 
     * @return the cim service
     */
    public SWbemServices getCIMService()
    {
        return cim2Service;
    }

    /**
     * Gets the Windows Management Instrumentation service
     * 
     * @return the wmi service
     */
    public SWbemServices getWMIService() throws HypervisorException
    {
        try
        {
            SWbemLocator loc = new SWbemLocator();
            wmiService =
                loc.connect(url.getHost(), "127.0.0.1", HyperVConstants.WMI_NS, getUser(),
                    getPassword());
        }
        catch (Exception e)
        {
            throw new HypervisorException(e);
        }
        return wmiService;
    }

    /**
     * Gets the user
     * 
     * @return the user
     */
    public String getUser()
    {
        return user;
    }

    /**
     * Gets the password
     * 
     * @return the password
     */
    public String getPassword()
    {
        return password;
    }

    @Override
    public void disconnect() throws HypervisorException
    {
        // TODO Auto-generated method stub

    }

    /**
     * Private helper to execute query with the virtualization service
     * 
     * @param query the query to execute
     * @return array of results
     * @throws JIException
     */
    public JIVariant[] execQuery(final String query) throws JIException
    {
        SWbemServices service = this.getVirtualizationService();

        IJIDispatch objectDispatcher = service.getObjectDispatcher();

        Object[] inParams =
            new Object[] {new JIString(query), JIVariant.OPTIONAL_PARAM(),
            JIVariant.OPTIONAL_PARAM(), JIVariant.OPTIONAL_PARAM(),};

        JIVariant[] results = objectDispatcher.callMethodA("ExecQuery", inParams);
        return results;
    }

    @Override
    public AbsVirtualMachine getMachine(final VirtualMachineConfiguration virtualMachineConfig)
        throws HypervisorException
    {
        try
        {
            init(this.url, this.user, this.password);
            connect(this.url);
            // Preparing the query
            String query =
                "Select * From Msvm_VirtualSystemGlobalSettingData Where ElementName='"
                    + virtualMachineConfig.getMachineName() + "'";

            JIVariant[] result = execQuery(query);

            JIVariant[][] tmpSet = enumToJIVariantArray(result);
            if (tmpSet.length > 0)
            {
                virtualMachineConfig.setHypervisor(this);
                AbsVirtualMachine vm = createMachine(virtualMachineConfig);
                vm.setState(State.DEPLOYED);
                return vm;
            }
            else
            {
                logger.debug(MessageValues.VM_NOT_FOUND + virtualMachineConfig.getMachineName());
                return null;
            }

        }
        catch (Exception e)
        {
            throw new HypervisorException(MessageValues.VM_NOT_FOUND
                + virtualMachineConfig.getMachineName(), e);
        }
        finally
        {
            logout();
        }

    }
}
