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

package com.abiquo.abiserver.persistence.dao.infrastructure;

import java.util.List;

import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.PhysicalmachineHB;
import com.abiquo.abiserver.business.hibernate.pojohb.user.EnterpriseHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualmachineHB;
import com.abiquo.abiserver.exception.PersistenceException;
import com.abiquo.abiserver.persistence.DAO;

/**
 * Specific interface to work with the {@link
 * com.abiquo.abiserver.business.hibernate.pojohb.interface.PhysicalmachineHB} Exposes all the
 * methods that this entity will need to interact with the data source
 * 
 * @author jdevesa@abiquo.com
 */
public interface PhysicalMachineDAO extends DAO<PhysicalmachineHB, Integer>
{

    /**
     * Gets the physical machines on a given rack of the datacenter of the provided virtual
     * datacenter. Only physical machines on MANAGED state. XXX do not apply the resource
     * restrictions (oversubscription rules)
     */
    List<PhysicalmachineHB> getByRackAndVirtualDatacenter(Integer idRack,
        Integer idVirtualDatacenter, Long hdRequiredOnDatastore, EnterpriseHB enterprise);

    /**
     * Return the number of virtual machines deployed in the physical machine
     * 
     * @param pmHB physical machine entity
     * @return number of deployed virtual machines
     */
    Long getNumberOfDeployedVirtualMachines(PhysicalmachineHB pmHB);

    /**
     * Return all the virtual machines deployed in the physical machine
     * 
     * @param machineId, physical machine identifier.
     * @return number of deployed virtual machines
     */
    List<VirtualmachineHB> getDeployedVirtualMachines(Integer machineId);

    /**
     * Get the IP of the hypervisor on the provided physical machien.
     * 
     * @param machineId, physical machine identifier.
     */
    String getHypervisorIP(int machineId);

    /**
     * Gets the physical machine with the hypervisor running on the provided ip.
     * 
     * @param hypervisorIp, the hypervisor ip of the desired machien.
     * @param idDataCenter, current machine's datacenter id
     * @return the physical machine with the hypervisor on the provided ip.
     */
    PhysicalmachineHB findByIp(String hypervisorIp, Integer iDataCenter);

    /**
     * Get the list of all the hypervisors ip of all the physical machines.
     */
    List<String> findAllIp();

    /**
     * Change the machine state.
     * 
     * @param machineId, physical machine identifier.
     * @param idPhysicalMachineState,
     * @throws PersistenceException
     */
    void setPhysicalMachineState(Integer machineId, int idPhysicalMachineState)
        throws PersistenceException;

    /**
     * Return the list of {#@link VirtualmachineHB} that are not managed by abicloud in a given
     * physical machine. All VMs in community version are managed by default
     * 
     * @param hostId
     * @return
     */
    List<VirtualmachineHB> getNotDeployedVirtualMachines(Integer hostId);

    /**
     * Get the list of physical machines that use this datastore.
     * 
     * @param datastoreId identifier of the datastore
     * @return list of physical machines.
     */
    List<PhysicalmachineHB> getPhysicalMachineListByDatastore(Integer datastoreId);

    /**
     * Gets the list of physical machines by Rack
     * 
     * @param rackId the identifier of the rack
     * @return list of physical machines
     */
    List<PhysicalmachineHB> getPhysicalMachineByRack(Integer rackId, String filters);

    /**
     * Gets the number of deployed virtualmachines in a physical machine owned by an enterprise
     * @param pmHB the physical machine of the virtualmachine
     * @param idEnterprise the enterprise to find the virtual machines owned by other enterprise than this one
     * @return the number of deployed virtual machines
     */
    Long getNumberOfDeployedVirtualMachinesOwnedByOtherEnterprise(PhysicalmachineHB pmHB, Integer idEnterprise);

    /**
     * This method gets de deployedVirtualMachine by abiquo of a PhysicalMachine
     * 
     * @param machineId the physicalMachineID
     * @return the list of virtualMachine
     */
    public List<VirtualmachineHB> getDeployedAbiquoVirtualMachines(Integer machineId);
    
    /**
     * This method updates the usedResources of a physicalMachine
     * @param idPhysicalMachine is the id of the physical Machine object
     */
    public void updateUsedResourcesByPhysicalMachine(final Integer idPhysicalMachine);

}
