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

package com.abiquo.abiserver.persistence.dao.virtualappliance;

import java.util.List;

import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualappHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualmachineHB;
import com.abiquo.abiserver.exception.PersistenceException;
import com.abiquo.abiserver.persistence.DAO;

/**
 * Specific interface to work with the
 * {@link com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualmachineHB} Exposes
 * all the methods that this entity will need to interact with the data source
 * 
 * @author jdevesa@abiquo.com
 */
public interface VirtualMachineDAO extends DAO<VirtualmachineHB, Integer>
{

    /**
     * Executes a search of the persisted objects based by its UUID.
     * 
     * @param uuid uuid of the virtual machine
     * @return a {@link VirtualmachineHB} object that matches with the request.
     */
    public VirtualmachineHB findByUUID(String uuid) throws PersistenceException;

    /**
     * Search the virtual machined based by its name
     * 
     * @param name name of the virtual machine
     * @return a {@link VirtualmachineHB} object that matches with the request.
     * @throws PersistenceException for any Persistence exception.
     */
    public VirtualmachineHB findByName(String name) throws PersistenceException;

    /**
     * Executes the same search of the method findByUUID but truncating the last 4 characters.
     * Special case for the ESXi hypervisor
     * 
     * @param uuid identifier of the machine.
     * @return a {@link VirtualmachineHB} object that matches with the request.
     */
    public List<VirtualmachineHB> findByNameTruncated(String name) throws PersistenceException;

    /**
     * Retrieve all the Virtual Machines running in a datastore
     * 
     * @param datastore identifier of the datastore.
     * @return list of virtual machines.
     */
    public List<VirtualmachineHB> findByDatastore(Integer datastoreId) throws PersistenceException;

    /**
     * This method gets the vApp from a vMachine
     * 
     * @param vmID it's the id of the VM
     * @return the vApp object
     */
    public VirtualappHB findVirtualAppFromVM(Integer vmID) throws PersistenceException;

}
