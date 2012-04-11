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

package com.abiquo.api.services;

import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.services.stub.NodecollectorServiceStub;
import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.server.core.cloud.Hypervisor;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.Datastore;
import com.abiquo.server.core.infrastructure.DatastoreDto;
import com.abiquo.server.core.infrastructure.InfrastructureRep;
import com.abiquo.server.core.infrastructure.Machine;
import com.abiquo.server.core.infrastructure.Rack;
import com.abiquo.server.core.infrastructure.RemoteService;
import com.abiquo.server.core.util.network.IPAddress;

@Service
@Transactional(readOnly = true)
public class DatastoreService extends DefaultApiService
{
    @Autowired
    InfrastructureRep repo;

    @Autowired
    protected NodecollectorServiceStub nodecollectorServiceStub;

    public DatastoreService()
    {

    }

    public DatastoreService(final EntityManager em)
    {
        repo = new InfrastructureRep(em);
    }

    public List<Datastore> getMachineDatastores(final Integer machineId)
    {
        if (machineId == 0)
        {
            addValidationErrors(APIError.INVALID_ID);
            flushErrors();
        }

        Machine machine = getMachine(machineId);

        return repo.findMachineDatastores(machine);
    }

    public Datastore getDatastore(final Integer id)
    {
        if (id == 0)
        {
            addValidationErrors(APIError.INVALID_ID);
            flushErrors();
        }

        return repo.findDatastoreById(id);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Datastore addDatastore(final DatastoreDto dto, final Integer machineId)
    {
        Machine machine = getMachine(machineId);

        Datastore datastore = null;
        if (dto.getId() != null && dto.getId() > 0)
        {
            datastore = getDatastore(dto.getId());

            if (datastore == null)
            {
                addValidationErrors(APIError.DATASTORE_NON_EXISTENT);
                flushErrors();
            }

            datastore.addToMachines(machine);
            repo.updateDatastore(datastore);
        }
        else
        {
            checkDuplicatedDatastoreToInsert(dto);

            datastore =
                new Datastore(machine, dto.getName(), dto.getRootPath(), dto.getDirectory());
            datastore.setEnabled(dto.isEnabled());

            checkValidDatastore(datastore);
            repo.insertDatastore(datastore);
        }

        return datastore;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Datastore updateDatastore(final Integer datastoreId, final DatastoreDto dto)
    {
        Datastore old = getDatastore(datastoreId);

        if (old == null)
        {
            addConflictErrors(APIError.DATASTORE_NON_EXISTENT);
            flushErrors();
        }

        checkDuplicatedDatastoreToUpdate(old, dto);

        old.setName(dto.getName());
        old.setDirectory(dto.getDirectory());
        old.setEnabled(dto.isEnabled());
        old.setDatastoreUUID(dto.getDatastoreUUID());

        checkValidDatastore(old);

        repo.updateDatastore(old);
        return old;
    }

    public boolean isAssignedTo(final int datacenterId, final int rackId, final int machineId,
        final int datastoreId)
    {
        Datastore datastore = getDatastore(datastoreId);

        if (datastore == null)
        {
            return false;
        }

        for (Machine machine : datastore.getMachines())
        {
            if (machine.getId() == machineId && machine.getDatacenter().getId() == datacenterId
                && machine.getRack().getId() == rackId)
            {
                return true;
            }
        }

        return false;
    }

    private void checkValidDatastore(final Datastore datastore)
    {
        if (!datastore.isValid())
        {
            addValidationErrors(datastore.getValidationErrors());
        }

        flushErrors();
    }

    private void checkDuplicatedDatastoreToInsert(final DatastoreDto dto)
    {
        if (repo.existAnyDatastoreWithName(dto.getName()))
        {
            addConflictErrors(APIError.DATASTORE_DUPLICATED_NAME);
        }
        if (repo.existAnyDatastoreWithDirectory(dto.getDirectory()))
        {
            addConflictErrors(APIError.DATASTORE_DUPLICATED_DIRECTORY);
        }
        flushErrors();
    }

    private void checkDuplicatedDatastoreToUpdate(final Datastore datastore, final DatastoreDto dto)
    {
        if (repo.existAnyOtherDatastoreWithName(datastore, dto.getName()))
        {
            addConflictErrors(APIError.DATASTORE_DUPLICATED_NAME);
        }
        if (repo.existAnyOtherDatastoreWithDirectory(datastore, dto.getDirectory()))
        {
            addConflictErrors(APIError.DATASTORE_DUPLICATED_DIRECTORY);
        }
        flushErrors();
    }

    private Machine getMachine(final Integer machineId)
    {
        Machine machine = repo.findMachineById(machineId);
        if (machine == null)
        {
            addConflictErrors(APIError.NON_EXISTENT_MACHINE);
            flushErrors();
        }
        return machine;
    }

    /**
     * Refreshes the datastores of the physical machine. Machine must belong to rack and rack must
     * belong to datacenter. Otherwise a NotFoundException will be raised.
     * 
     * @param datacenterId identifier of the datacenter
     * @param rackId identifier of the rack
     * @param machineId identifier of the machine.
     */
    @Transactional(readOnly = false)
    public void refreshDatastores(final Integer datacenterId, final Integer rackId,
        final Integer machineId)
    {
        Machine machine = getMachine(datacenterId, rackId, machineId);
        Rack rack = machine.getRack();
        Datacenter dc = rack.getDatacenter();

        Machine remoteMachine = getRemoteMachine(dc, machine.getHypervisor());

        List<Datastore> dbDatastores = machine.getDatastores();
        List<Datastore> rmDatastores = remoteMachine.getDatastores();
        Iterator<Datastore> rmDatastoresIt = rmDatastores.iterator();

        // First add the remote Datastores
        while (rmDatastoresIt.hasNext())
        {
            Datastore remote = rmDatastoresIt.next();
            if (!dbDatastores.contains(remote))
            {
                repo.insertDatastore(remote);
                machine.getDatastores().add(remote);
                repo.updateMachine(machine);
            }
        }
    }

    /**
     * Private method to retrieve the physical machine.
     * 
     * @param datacenterId
     * @param rackId
     * @param machineId
     * @return
     */
    private Machine getMachine(final Integer datacenterId, final Integer rackId,
        final Integer machineId)
    {
        Machine machine = repo.findMachineById(machineId);
        if (machine == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_MACHINE);
            flushErrors();
        }
        if (!(machine.getDatacenter().getId().equals(datacenterId) && machine.getRack().getId()
            .equals(rackId)))
        {
            addNotFoundErrors(APIError.NOT_ASSIGNED_MACHINE_DATACENTER_RACK);
            flushErrors();
        }
        return machine;
    }

    /**
     * Get the remote machine
     * 
     * @param dc
     * @param hyp
     * @return
     */
    private Machine getRemoteMachine(final Datacenter dc, final Hypervisor hyp)
    {
        // Get the data to call the nodecollector.
        String ip = hyp.getIpService();
        HypervisorType hypType = hyp.getType();
        String user = hyp.getUser();
        String password = hyp.getPassword();
        Integer port = hyp.getPort();

        // Get the remote service to perform the call
        List<RemoteService> services =
            repo.findRemoteServiceWithTypeInDatacenter(dc, RemoteServiceType.NODE_COLLECTOR);
        RemoteService nodecollector = null;

        if (!services.isEmpty())
        {
            // Only one remote service of each type by datacenter.
            nodecollector = services.get(0);
        }
        else
        {
            addNotFoundErrors(APIError.NON_EXISTENT_REMOTE_SERVICE_TYPE);
            flushErrors();
        }

        return nodecollectorServiceStub.getRemoteHypervisor(nodecollector,
            IPAddress.newIPAddress(ip), hypType, user, password, port);
    }
}
