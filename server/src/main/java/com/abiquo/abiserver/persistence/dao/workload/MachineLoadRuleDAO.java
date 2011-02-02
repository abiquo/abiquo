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

package com.abiquo.abiserver.persistence.dao.workload;

import java.util.Collection;
import java.util.List;

import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.PhysicalmachineHB;
import com.abiquo.abiserver.business.hibernate.pojohb.workload.MachineLoadRuleHB;
import com.abiquo.abiserver.persistence.DAO;

public interface MachineLoadRuleDAO extends DAO<MachineLoadRuleHB, Integer>
{
    /**
     * Returns the {@link MachineLoadRuleHB} that apply to the given datacenter.
     * 
     * @param idDatacenter The id of the datacenter.
     * @return The <code>MachineLoadRuleHB</code> that apply to the given datacenter.
     */
    public List<MachineLoadRuleHB> getRulesForDatacenter(final Integer idDatacenter);

    /**
     * Returns the {@link MachineLoadRuleHB} that apply to the given rack.
     * 
     * @param idRack The id of the rack.
     * @return The <code>MachineLoadRuleHB</code> that apply to the given rack.
     */
    public List<MachineLoadRuleHB> getRulesForRack(final Integer idRack);

    /**
     * Returns the {@link MachineLoadRuleHB} that apply to the given physical machine.
     * 
     * @param idPhysicalMachine The id of the physical machine.
     * @return The <code>MachineLoadRuleHB</code> that apply to the given physical machine.
     */
    public List<MachineLoadRuleHB> getRulesForMachine(final Integer idPhysicalMachine);

    /**
     * Find rules from machines and find rules from racks hosting the machines
     */
    public List<MachineLoadRuleHB> findCandidateMachineRules(
        Collection<PhysicalmachineHB> firstPassCandidateMachines);

    /**
     * Delete the rules for the given datacenter.
     * 
     * @param idDatacenter The id of the datacenter.
     */
    public void deleteRulesForDatacenter(final Integer idDatacenter);

    /**
     * Delete the rules for the given rack.
     * 
     * @param idRack The id of the rack.
     */
    public void deleteRulesForRack(final Integer idRack);

    /**
     * Delete the rules for the given physical machine.
     * 
     * @param idPhysicalMachine The id of the physical machine.
     */
    public void deleteRulesForMachine(final Integer idPhysicalMachine);
}
