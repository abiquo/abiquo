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

package com.abiquo.virtualfactory.model;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.UUID;

import org.dmtf.schemas.ovf.envelope._1.ContentType;
import org.dmtf.schemas.ovf.envelope._1.EnvelopeType;
import org.dmtf.schemas.ovf.envelope._1.VSSDType;
import org.dmtf.schemas.ovf.envelope._1.VirtualHardwareSectionType;
import org.dmtf.schemas.ovf.envelope._1.VirtualSystemType;
import org.dmtf.schemas.wbem.wscim._1.common.CimString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.abiquo.ovfmanager.ovf.OVFEnvelopeUtils;
import com.abiquo.virtualfactory.context.ApplicationContextProvider;
import com.abiquo.virtualfactory.exception.HypervisorException;
import com.abiquo.virtualfactory.exception.PluginException;
import com.abiquo.virtualfactory.exception.VirtualMachineException;
import com.abiquo.virtualfactory.model.config.HypervisorConfiguration;
import com.abiquo.virtualfactory.model.config.VirtualMachineConfiguration;
import com.abiquo.virtualfactory.model.ovf.OVFModelConvertable;
import com.abiquo.virtualfactory.plugin.HypervisorManager;
import com.abiquo.virtualfactory.utils.AbicloudConstants;
import com.abiquo.virtualfactory.virtualappliance.VirtualapplianceresourceDeployable;

/**
 * The Class VirtualSystemModel represents a mini model to maintain a list of virtual systems.
 */
public class VirtualSystemModel
{

    /** The logger. */
    private final Logger logger = LoggerFactory.getLogger(VirtualSystemModel.class);

    /**
     * Controller where obtain hypervisor singleton instances (identified by hypervisor type +
     * address).
     */
    private HypervisorManager hypervisors;

    /** The singleton instance for this class. */
    private static VirtualSystemModel singleton;

    /**
     * Singleton accessor.
     * 
     * @return the model
     */
    public static VirtualSystemModel getModel()
    {
        if (singleton == null)
        {
            singleton = new VirtualSystemModel();
        }

        return singleton;
    }

    /**
     * Gets a virtual machine by id.
     * 
     * @param machineId the id of the machine to get
     * @return the machine
     * @throws VirtualMachineException the virtual machine exception
     * @throws HypervisorException
     * @throws PluginException
     * @throws MalformedURLException
     * @throw VirtualMachineException if there is any machine with the required machineId
     */
    public AbsVirtualMachine getMachine(HypervisorConfiguration hypervisorConfiguration,
        VirtualMachineConfiguration virtualMachineConfig) throws VirtualMachineException,
        MalformedURLException, PluginException, HypervisorException
    {
        IHypervisor hyper =
            hypervisors.getHypervisor(hypervisorConfiguration.getHypervisorType(),
                new URL(hypervisorConfiguration.getAddressManagement()), hypervisorConfiguration
                    .getAdminUser(), hypervisorConfiguration.getAdminPassword());

        return hyper.getMachine(virtualMachineConfig);
    }

    /**
     * Creates a new virtual machine instance using the Hypervisor type located on address.
     * 
     * @param type the Hypervisor type
     * @param address the Hypervisor address
     * @param config the config
     * @param user the admin user
     * @param password the admin passowrd
     * @return the abs virtual machine
     * @throws VirtualMachineException it there is any plugin to instantiate an hypervisor for the
     *             given type
     */
    public AbsVirtualMachine createVirtualMachine(String type, URL address,
        VirtualMachineConfiguration config, String user, String password)
        throws VirtualMachineException
    {
        // param : String virtualSystemId, String machineName

        IHypervisor hyper;
        AbsVirtualMachine machine;
        try
        {
            hyper = hypervisors.getHypervisor(type, address, user, password);

            logger.info("Got hypervisor type: " + type + "\t at address:" + address.toString());

            config.setHypervisor(hyper);

            machine = hyper.createMachine(config);

            logger.info("Created machine name:" + config.getMachineName() + "\t : "
                + config.getMachineId().toString());
        }
        catch (PluginException e)
        {
            throw new VirtualMachineException(e);
        }
        catch (Exception e) // attaching repository
        {
            // final String msg =
            // "The repository ["+repository.toASCIIString()+"]can not be attached ";
            throw new VirtualMachineException(e);
        }

        return machine;
    }

    public HypervisorManager getHypervisors()
    {
        return hypervisors;
    }

    public void setHypervisors(HypervisorManager hypervisors)
    {
        this.hypervisors = hypervisors;
    }

}
