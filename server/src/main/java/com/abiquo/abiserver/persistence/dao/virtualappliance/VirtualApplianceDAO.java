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

import java.util.Collection;
import java.util.List;

import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.StateEnum;
import com.abiquo.abiserver.business.hibernate.pojohb.user.UserHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualappHB;
import com.abiquo.abiserver.exception.PersistenceException;
import com.abiquo.abiserver.persistence.DAO;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.abiserver.pojo.virtualappliance.VirtualAppliance;

/**
 * Specific interface to work with the
 * {@link com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualappHB} Exposes all
 * the methods that this entity will need to interact with the data source
 * 
 * @author jdevesa@abiquo.com
 */
public interface VirtualApplianceDAO extends DAO<VirtualappHB, Integer>
{

    // Publish all the needed extra functions
    VirtualappHB findByIdNamed(Integer id);

    /**
     * Gets the Virtual Datacenter id for the current virtual appliance.
     * 
     * @param idVirtualApp, a valid virtual appliance identifier.
     * @return the virtual datacenter the provided virtual app belongs to.
     */
    public Integer getVirtualDatacenterId(final Integer idVirtualApp);

    public VirtualappHB findByIdNamedExtended(Integer id);

    /**
     * Gets all the VirtualAppliances with some ImageNode using the provided virtual image
     * identifier.
     * 
     * @param virtualImageId, a valid virtual image identifier
     * @return
     * @throws PersistenceException
     */
    // TODO virtualImageId SHOULD BE an Integeer !
    List<VirtualappHB> findByUsingVirtualImage(String virtualImageId) throws PersistenceException;

    /**
     * Gets all the VirtualAppliances with some ImageNode on the provided repository.
     * 
     * @param idRepository, valid virtual image repository identifier
     * @return
     * @throws PersistenceException
     */
    List<VirtualappHB> findByUsingVirtualImageOnRepository(final Integer idRepository);

    /**
     * Gets all the deployed virtual appliances
     * 
     * @return
     * @throws PersistenceException
     */
    List<VirtualappHB> findAllDeployed() throws PersistenceException;

    /**
     * Checks if the state of a given virtual appliance is actually the last valid state in the Data
     * Base If it is the same, the state of the virtual appliance will be updated to
     * State.IN_PROGRESS, and a boolean will be returned to true, to indicate that the virtual
     * appliance can be manipulated Otherwise, the current state will be returned, and the boolean
     * will be set to false, indicating that the virtual appliance can not be manipulated
     * 
     * @param virtualAppliance The virtual appliance that will be checked
     * @param subState the subState associated to the IN_PROGRESS state
     * @return A DataResult object, containing a boolean that indicates if the virtual appliance can
     *         be manipulated and, in any case, it will contain the virtualAppliance with the
     *         current values in Data Base (this returned VirtualAppliance will also contain the
     *         node list!)
     * @throws Exception An Exception is thrown if there was a problem connecting to the Data base
     */
    public DataResult<VirtualAppliance> checkVirtualApplianceState(
        VirtualAppliance virtualAppliance, StateEnum subState) throws Exception;

    public VirtualappHB blockVirtualAppliance(VirtualappHB virtualApp, StateEnum subState)
        throws PersistenceException;

    public VirtualappHB makePersistentBasic(VirtualappHB entity) throws PersistenceException;

    public VirtualappHB makePersistentExtended(VirtualappHB entity) throws PersistenceException;

    /**
     * Retrieves the {@link VirtualappHB} object by one of its virtual machines.
     * 
     * @param vmId identifier of the virtual machine.
     * @return a VirtualappHB object if its found
     * @throws PersistenceException if any problems occurs accessing to database.
     */
    VirtualappHB getVirtualAppByVirtualMachine(Integer vmId) throws PersistenceException;

    Collection<VirtualappHB> getVirtualAppliancesByEnterprise(UserHB user, Integer enterpriseId);

    Collection<VirtualappHB> getVirtualAppliancesByEnterpriseAndDatacenter(UserHB user,
        Integer enterpriseId, Integer datacenteId);
}
