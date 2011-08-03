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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.api.services.config.SystemPropertyService;
import com.abiquo.scheduler.workload.NotEnoughResourcesException;
import com.abiquo.server.core.cloud.Hypervisor;
import com.abiquo.server.core.cloud.HypervisorDAO;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.config.SystemProperty;
import com.abiquo.server.core.infrastructure.Datastore;
import com.abiquo.server.core.infrastructure.DatastoreDAO;
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
@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
public class VirtualMachineFactory
{
    /** Use an invalid port to indicate a disabled vrdp. */
    protected static final int DISABLED_VRDPORT = 65535 + 10;

    private final static Logger log = LoggerFactory.getLogger(VirtualMachineFactory.class);

    @Autowired
    InfrastructureRep datacenterRepo;

    @Autowired
    HypervisorDAO hypervisorDao;

    @Autowired
    // TODO move to InfastructureRep
    DatastoreDAO datastoreDao;

    @Autowired
    SystemPropertyService systemPropertyService;

    /** The remote desktop min port **/
    public final static int MIN_REMOTE_DESKTOP_PORT = 5900;

    public final static int MAX_REMOTE_DESKTOP_PORT = 65534;

    protected final static String ALLOW_RDP_PROPERTY = "client.virtual.allowVMRemoteAccess";

    /**
     * Create a Virtual Machine on the given PhysicalMachine to deploy the given VirtualImage.
     * 
     * @param machine, the machine hypervisor will be used to create the new virtual image.
     * @param image, the virtual image (vm template) to be deployed.
     * @return a new VirtualMachine instance inside physical to load image.
     *         <p>
     *         TODO: creating default Hypervisor instance
     *         <p>
     *         TODO: VdrpIP, VdrpPort The hypervisors shall be discovered when the physical machine
     *         are loaded so we recover the hypervisors from the DB
     * @throws NotEnoughResourcesException, if the target machine haven't enough resources to hold
     *             the virtual machine
     */
    public VirtualMachine createVirtualMachine(final Machine machine, VirtualMachine virtualMachine)
        throws NotEnoughResourcesException
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
            final long datastoreRequ = virtualMachine.getVirtualImage().getDiskFileSize();
            final Datastore datastore = selectDatastore(machine, datastoreRequ);
            virtualMachine.setDatastore(datastore);
        }
        else
        // its an HA reallocation, the datastore was already
        {
            final String currentDatastoreUuid = virtualMachine.getDatastore().getDatastoreUUID();

            // find the shared datastore on the target machine
            Datastore datastore = datastoreDao.findDatastore(currentDatastoreUuid, machine);

            virtualMachine.setDatastore(datastore);
        }

        final int vdrpPort = selectVrdpPort(machine);
        virtualMachine.setVdrpIP(hypervisor.getIpService());
        virtualMachine.setVdrpPort(vdrpPort);

        return virtualMachine;
    }

    /**
     * Selects the larger datastore from the physical machine datastore list
     * 
     * @param physical the physical machine
     * @param session
     * @param image
     * @return the target datastore where the virtual machine will be deployed
     * @throws SchedulerException
     */
    private Datastore selectDatastore(final Machine machine, final Long hdImageRequired)
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

        if (betterDatastore == null || freeLargerSize < hdImageRequired)
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

    private int selectVrdpPort(Machine machine) throws NotEnoughResourcesException
    {
        SystemProperty allowRdp = systemPropertyService.findByName(ALLOW_RDP_PROPERTY);

        if (allowRdp != null && allowRdp.getValue().startsWith("0"))
        {
            return DISABLED_VRDPORT;
        }

        final List<Integer> rdpPorts = hypervisorDao.getUsedPorts(machine.getHypervisor().getId());

        Integer candidatePort = getFreePortFromUsedList(rdpPorts);

        log.debug("The VRDP assigned port is: " + candidatePort);

        return candidatePort;
    }

    /**
     * Gets a free port from the list used port
     * 
     * @param rdpPorts
     * @return
     * @throws SchedulerException
     */
    protected Integer getFreePortFromUsedList(final List<Integer> rdpPorts)
        throws NotEnoughResourcesException
    {
        final Integer candidatePort = MIN_REMOTE_DESKTOP_PORT;

        if (rdpPorts.isEmpty())
        {
            return candidatePort;
        }

        Collections.sort(rdpPorts);

        if (rdpPorts.get(0) > 0 && rdpPorts.get(0) != MIN_REMOTE_DESKTOP_PORT)
        {
            return candidatePort;
        }

        int next = 1;

        for (int i = 0; i < rdpPorts.size(); i++)
        {
            if (rdpPorts.get(i) == MAX_REMOTE_DESKTOP_PORT)
            {
                final String cause = "The maximun number of remote desktop ports has been reached";
                throw new NotEnoughResourcesException(cause);
            }

            // Ignoring the virtual machine ports less than 0
            if (rdpPorts.get(i) < MIN_REMOTE_DESKTOP_PORT)
            {
                next++;
                continue;
            }

            if (next == rdpPorts.size() || rdpPorts.get(next) != rdpPorts.get(i) + 1)
            {
                return rdpPorts.get(i) + 1;
            }
            next++;
        }

        return candidatePort;
    }
}
