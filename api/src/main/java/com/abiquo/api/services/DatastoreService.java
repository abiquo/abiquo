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

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.server.core.infrastructure.InfrastructureRep;
import com.abiquo.server.core.infrastructure.Datastore;
import com.abiquo.server.core.infrastructure.DatastoreDto;
import com.abiquo.server.core.infrastructure.Machine;

@Service
@Transactional(readOnly = true)
public class DatastoreService extends DefaultApiService
{
    @Autowired
    InfrastructureRep repo;

    public DatastoreService()
    {

    }

    public DatastoreService(EntityManager em)
    {
        repo = new InfrastructureRep(em);
    }

    public List<Datastore> getMachineDatastores(Integer machineId)
    {
        if (machineId == 0)
        {
            addValidationErrors(APIError.INVALID_ID);
            flushErrors();
        }

        Machine machine = getMachine(machineId);

        return repo.findMachineDatastores(machine);
    }

    public Datastore getDatastore(Integer id)
    {
        if (id == 0)
        {
            addValidationErrors(APIError.INVALID_ID);
            flushErrors();
        }

        return repo.findDatastoreById(id);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Datastore addDatastore(DatastoreDto dto, Integer machineId)
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
            datastore.setShared(dto.isShared());
            datastore.setEnabled(dto.isEnabled());

            checkValidDatastore(datastore);
            repo.insertDatastore(datastore);
        }

        return datastore;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Datastore updateDatastore(Integer datastoreId, DatastoreDto dto)
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
        old.setShared(dto.isShared());
        old.setDatastoreUUID(dto.getDatastoreUUID());

        checkValidDatastore(old);

        repo.updateDatastore(old);
        return old;
    }

    public boolean isAssignedTo(int datacenterId, int rackId, int machineId, int datastoreId)
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

    private void checkValidDatastore(Datastore datastore)
    {
        if (!datastore.isValid())
        {
            addValidationErrors(datastore.getValidationErrors());
        }

        flushErrors();
    }

    private void checkDuplicatedDatastoreToInsert(DatastoreDto dto)
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

    private void checkDuplicatedDatastoreToUpdate(Datastore datastore, DatastoreDto dto)
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

    private Machine getMachine(Integer machineId)
    {
        Machine machine = repo.findMachineById(machineId);
        if (machine == null)
        {
            addConflictErrors(APIError.NON_EXISTENT_MACHINE);
            flushErrors();
        }
        return machine;
    }
}
