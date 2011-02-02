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

package com.abiquo.abiserver.commands.impl;

import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.DatastoreHB;
import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.PhysicalmachineHB;
import com.abiquo.abiserver.commands.BasicCommand;
import com.abiquo.abiserver.commands.DatastoreCommand;
import com.abiquo.abiserver.exception.DatastoreCommandException;
import com.abiquo.abiserver.exception.PersistenceException;
import com.abiquo.abiserver.persistence.DAOFactory;
import com.abiquo.abiserver.persistence.dao.infrastructure.DatastoreDAO;
import com.abiquo.abiserver.persistence.dao.infrastructure.PhysicalMachineDAO;
import com.abiquo.abiserver.persistence.dao.virtualappliance.VirtualMachineDAO;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAOFactory;
import com.abiquo.abiserver.pojo.authentication.UserSession;
import com.abiquo.server.core.enumerator.HypervisorType;

/**
 * Implements the functionality of the {@link DatastoreCommand} interface.
 * 
 * @author jdevesa@abiquo.com
 */
public class DatastoreCommandImpl extends BasicCommand implements DatastoreCommand
{

    /**
     * Factory of DAOs and transaction manager.
     */
    DAOFactory factory;

    /**
     * Most used DAO of the class.
     */
    DatastoreDAO dataDAO;

    /**
     * Default constructor.
     */
    public DatastoreCommandImpl()
    {

    }

    @Override
    public DatastoreHB createDatastore(UserSession userSession, DatastoreHB newDatastore,
        Integer physicalMachineId) throws DatastoreCommandException
    {
        try
        {
            factory = HibernateDAOFactory.instance();

            factory.beginConnection();

            // Create the Datastore
            newDatastore =
                (DatastoreHB) HibernateDAOFactory.getSessionFactory().getCurrentSession().merge(
                    newDatastore);

            // Persist the relation between the datastore and the physicalmachine.
            PhysicalMachineDAO pmDAO = factory.getPhysicalMachineDAO();
            
            PhysicalmachineHB pmHB = pmDAO.findById(physicalMachineId);
            pmHB.getDatastoresHB().add(newDatastore);

            pmDAO.makePersistent(pmHB);

            factory.endConnection();

            return newDatastore;
        }
        catch (PersistenceException e)
        {
            factory.rollbackConnection();
            throw new DatastoreCommandException("Unhandled exception");
        }

    }

    @Override
    public DatastoreHB editDatastore(UserSession userSession, DatastoreHB datastore)
        throws DatastoreCommandException
    {
        DatastoreHB dataHB;

        try
        {
            factory = HibernateDAOFactory.instance();

            factory.beginConnection();

            dataDAO = factory.getDatastoreDAO();
            VirtualMachineDAO vmDAO = factory.getVirtualMachineDAO();
            PhysicalMachineDAO pmDAO = factory.getPhysicalMachineDAO();
            Integer datastoreId = datastore.getIdDatastore();

            dataHB = dataDAO.findById(datastoreId);
            if (dataHB == null)
            {
                throw new DatastoreCommandException("Datastore to edit doesn't exist in Database");
            }

            // The changes in the datastore deploy directory are not allowed if there is any virtual machine deployed
            if (vmDAO.findByDatastore(datastoreId).size() > 0)
            {
                if (!datastore.getDirectory().equals(dataHB.getDirectory())
                    || !datastore.getName().equals(dataHB.getName())
                    || !datastore.getRootPath().equals(dataHB.getRootPath()))
                {

                    throw new DatastoreCommandException("There are virtual machines deployed in the datastore. Can not edit.");

                }
            }

            // Currently we only use one pm per datastore...
            PhysicalmachineHB pmHB = pmDAO.getPhysicalMachineListByDatastore(datastoreId).get(0);
            pmHB.setRealStorage(pmHB.getRealStorage() - dataHB.getSize() + datastore.getSize());
            pmDAO.makePersistent(pmHB);

            if (!datastore.getDirectory().equals(dataHB.getDirectory()))
            {
                // ESXi and XenServer manage the directory on their own way. So, we can not edit it
                if (pmHB.getHypervisor().getType().getValue().equalsIgnoreCase(HypervisorType.VMX_04.getValue())
                    || pmHB.getHypervisor().getType().getValue().equalsIgnoreCase(HypervisorType.XENSERVER.getValue()))
                {
                    throw new DatastoreCommandException("Can not edit the directory for this Hypervisor");
                }
            }

            dataHB.setRootPath(datastore.getRootPath());
            dataHB.setDirectory(datastore.getDirectory());
            dataHB.setEnabled(datastore.getEnabled());
            dataHB.setName(datastore.getName());
            dataHB.setSize(datastore.getSize());
            dataHB.setShared(datastore.getShared());
            dataHB.setUsedSize(datastore.getUsedSize());
            dataHB = dataDAO.makePersistent(dataHB);

            factory.endConnection();
        }
        catch (PersistenceException e)
        {
            throw new DatastoreCommandException("Unhandled exception");
        }

        return dataHB;

    }
}
