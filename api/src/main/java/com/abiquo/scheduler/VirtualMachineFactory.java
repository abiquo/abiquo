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

package com.abiquo.scheduler;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.api.config.ConfigService;
import com.abiquo.api.services.config.SystemPropertyService;
import com.abiquo.scheduler.workload.NotEnoughResourcesException;
import com.abiquo.server.core.cloud.Hypervisor;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.config.SystemProperty;
import com.abiquo.server.core.infrastructure.Datastore;
import com.abiquo.server.core.infrastructure.InfrastructureRep;
import com.abiquo.server.core.infrastructure.Machine;

/**
 * Builds a VirtualMachine from a PhysicalMachine and a VirtualImage.
 * 
 * @transactional required-read only
 */

/**
 * TODO this should be a @Repository
 */
@Component
public class VirtualMachineFactory
{
    /** Use an invalid port to indicate a disabled vrdp. */
    protected static final int DISABLED_VRDPORT = 65535 + 10;

    private final static Logger log = LoggerFactory.getLogger(VirtualMachineFactory.class);

    @Autowired
    protected InfrastructureRep datacenterRepo;

    @Autowired
    private SystemPropertyService systemPropertyService;

    public VirtualMachineFactory()
    {
    }

    public VirtualMachineFactory(final EntityManager em)
    {
        this.datacenterRepo = new InfrastructureRep(em);
        this.systemPropertyService = new SystemPropertyService(em);
    }

    /** The remote desktop min port **/
    public final static int MIN_REMOTE_DESKTOP_PORT = Integer.valueOf(ConfigService
        .getSystemProperty(ConfigService.MIN_REMOTE_DESKTOP_PORT, "5900"));

    public final static int MAX_REMOTE_DESKTOP_PORT = Integer.valueOf(ConfigService
        .getSystemProperty(ConfigService.MAX_REMOTE_DESKTOP_PORT, "65534"));

    protected final static String ALLOW_RDP_PROPERTY = "client.virtual.allowVMRemoteAccess";

    /**
     * Create a Virtual Machine on the given PhysicalMachine to deploy the given
     * VirtualMachineTemplate.
     * 
     * @param machine, the machine hypervisor will be used to create the new virtual machine
     *            template.
     * @return a new VirtualMachine instance inside physical to load image.
     *         <p>
     *         TODO: creating default Hypervisor instance
     *         <p>
     *         TODO: VdrpIP, VdrpPort The hypervisors shall be discovered when the physical machine
     *         are loaded so we recover the hypervisors from the DB
     * @throws NotEnoughResourcesException, if the target machine haven't enough resources to hold
     *             the virtual machine
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public VirtualMachine createVirtualMachine(final Machine machine,
        final VirtualMachine virtualMachine) throws NotEnoughResourcesException
    {
        // TODO set UUID and name
        // TODO default also high disponibility flag)
        // To define a new UUID when the VM is ready to be instanced decomment the line below
        // virtualMachine.setUUID(UUID.randomUUID().toString());
        // virtualMachine.setDescription(image.getDescription()); // same description as the vimage

        final Hypervisor hypervisor = machine.getHypervisor();
        virtualMachine.setHypervisor(hypervisor);

        if (virtualMachine.getDatastore() == null)
        {
            final long datastoreRequ = virtualMachine.getVirtualMachineTemplate().getDiskFileSize();
            final Datastore datastore = selectDatastore(machine, datastoreRequ);
            virtualMachine.setDatastore(datastore);

            virtualMachine.setVdrpPort(isRemoteAccessEnabled() ? selectVrdpPort(machine)
                : DISABLED_VRDPORT);
        }
        else
        // its an HA reallocation, the datastore was already
        {
            final String currentDatastoreUuid = virtualMachine.getDatastore().getDatastoreUUID();

            // find the shared datastore on the target machine
            Datastore datastore =
                datacenterRepo.findDatastoreByUuidAndMachine(currentDatastoreUuid, machine);

            virtualMachine.setDatastore(datastore);
        }

        virtualMachine.setVdrpIP(hypervisor.getIpService());

        return virtualMachine;
    }

    /**
     * Selects the larger datastore from the physical machine datastore list
     * 
     * @param physical the physical machine
     * @param session
     * @return the target datastore where the virtual machine will be deployed
     * @throws SchedulerException
     */
    private Datastore selectDatastore(final Machine machine, final Long hdDiskRequired)
        throws NotEnoughResourcesException
    {
        List<Datastore> datastores = datacenterRepo.findMachineDatastores(machine);

        if (datastores.isEmpty())
        {
            final String cause = "The target physical machine has no datastores.";
            throw new NotEnoughResourcesException(cause);
        }

        // Getting the enabled datastores
        Long freeLargerSize = 0L;
        Datastore betterDatastore = null;
        for (final Datastore datastore : datastores)
        {
            if (datastore.isEnabled())
            {
                final Long currentLargerSize = datastore.getSize() - datastore.getUsedSize();
                if (freeLargerSize < currentLargerSize)
                {
                    freeLargerSize = currentLargerSize;
                    betterDatastore = datastore;
                }
            }
        }

        if (betterDatastore == null || freeLargerSize < hdDiskRequired)
        {
            final String cause =
                "The target physical machine has no datastores enabled with the required free size.";
            throw new NotEnoughResourcesException(cause);
        }

        // // updating datastore XXX set on resourceupgradeuse
        // betterDatastore.setUsedSize(betterDatastore.getUsedSize() + hdImageRequired);
        // session.update(dsHB);

        log.debug("The selected datastore for deploying is: {}", betterDatastore.getName());
        return betterDatastore;

    }

    protected int selectVrdpPort(final Machine machine) throws NotEnoughResourcesException
    {
        final List<Integer> usedPorts =
            datacenterRepo.findUsedRemoteDesktopPortsInRack(machine.getRack());
        Integer candidatePort = getNextFreeRemoteDesktopPort(usedPorts);
        log.debug("The assigned remote desktop port is: {}", candidatePort);
        return candidatePort;
    }

    /**
     * Gets a free port from the list used port
     * 
     * @param portsInUse
     * @return
     * @throws SchedulerException
     */
    protected Integer getNextFreeRemoteDesktopPort(final List<Integer> portsInUse)
        throws NotEnoughResourcesException
    {
        // Sort ports (we don't care about portsInUse having repeated elements)
        Collections.sort(portsInUse);

        List<Integer> allowedPorts = new LinkedList<Integer>();
        for (int i = MIN_REMOTE_DESKTOP_PORT; i <= MAX_REMOTE_DESKTOP_PORT; i++)
        {
            allowedPorts.add(i);
        }
        allowedPorts.removeAll(portsInUse);

        if (allowedPorts.isEmpty())
        {
            throw new NotEnoughResourcesException("The maximun number of remote desktop ports has been reached");
        }

        return allowedPorts.get(0);
    }

    private boolean isRemoteAccessEnabled()
    {
        SystemProperty allowRdp = systemPropertyService.findByName(ALLOW_RDP_PROPERTY);
        return allowRdp == null || !allowRdp.getValue().startsWith("0");
    }
}
