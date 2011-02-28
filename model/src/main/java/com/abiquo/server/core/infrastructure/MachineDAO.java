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

package com.abiquo.server.core.infrastructure;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.abiquo.server.core.common.persistence.DefaultDAOBase;
import com.abiquo.server.core.enterprise.Enterprise;

@Repository("jpaMachineDAO")
@SuppressWarnings("unchecked")
public class MachineDAO extends DefaultDAOBase<Integer, Machine>
{

    public MachineDAO()
    {
        super(Machine.class);
    }

    public MachineDAO(EntityManager entityManager)
    {
        super(Machine.class, entityManager);
    }

    private static Criterion sameDatacenter(Datacenter datacenter)
    {
        assert datacenter != null;

        return Restrictions.eq(Machine.DATACENTER_PROPERTY, datacenter);
    }

    private static Criterion sameRack(Rack rack)
    {
        assert rack != null;

        return Restrictions.eq(Machine.RACK_PROPERTY, rack);
    }

    private static Criterion sameId(Integer id)
    {
        return Restrictions.eq(Machine.ID_PROPERTY, id);
    }

    private static Criterion sameName(String name)
    {
        assert !StringUtils.isEmpty(name);

        return Restrictions.eq(Machine.NAME_PROPERTY, name);
    }

    private Criterion sameEnterprise(Enterprise enterprise)
    {
        return Restrictions.eq(Machine.ENTERPRISE_PROPERTY, enterprise);
    }

    public List<Machine> findMachines(Datacenter datacenter)
    {
        assert datacenter != null;
        assert isManaged2(datacenter);

        Criteria criteria = createCriteria(sameDatacenter(datacenter));
        criteria.addOrder(Order.asc(Machine.NAME_PROPERTY));
        List<Machine> result = getResultList(criteria);
        return result;
    }

    /**
     * @return the list of physical machines of the infrastructure without virtual machines in the
     *         allocator.
     */
    public List<Machine> findMachineWithoutVMsInAllocator()
    {
        // The way to define the virtual machines in the allocator is:
        // All the virtual machines with an hypervisor associated and with state=NOT_DEPLOYED
        Query query = getSession().createQuery(QUERY_MACHINES_WITHOUT_VMS_IN_ALLOCATOR);

        return query.list();

    }

    public List<Machine> findRackMachines(Rack rack)
    {
        assert rack != null;
        assert isManaged2(rack);

        Criteria criteria = createCriteria(sameRack(rack));
        criteria.addOrder(Order.asc(Machine.NAME_PROPERTY));
        List<Machine> result = getResultList(criteria);
        return result;
    }

    public boolean existsAnyWithDatacenterAndName(Datacenter datacenter, String name)
    {
        assert datacenter != null;
        assert !StringUtils.isEmpty(name);

        return existsAnyByCriterions(sameDatacenter(datacenter), sameName(name));
    }

    public boolean existsAnyOtherWithDatacenterAndName(Machine machine, String name)
    {
        assert machine != null;
        assert !StringUtils.isEmpty(name);

        return existsAnyOtherByCriterions(machine, sameDatacenter(machine.getDatacenter()),
            sameName(name));
    }

    public int deleteRackMachines(Rack rack)
    {
        assert rack != null;
        assert isManaged2(rack);

        Collection<Machine> machines = findRackMachines(rack);
        for (Machine machine : machines)
        {
            remove(machine);
        }
        return machines.size();
    }

    public List<Machine> findCandidateMachines(Integer idRack, Integer idVirtualDatacenter,
        Long hdRequiredOnDatastore, Enterprise enterprise)
    {
        // checkQuery(idRack, idVirtualDatacenter, hdRequiredOnDatastore, enterprise);

        Query query = getSession().createQuery(QUERY_CANDIDATE_MACHINES);

        query.setInteger("idVirtualDataCenter", idVirtualDatacenter);
        query.setInteger("idRack", idRack);
        query.setLong("hdRequiredOnRepository", hdRequiredOnDatastore);
        query.setParameter("state", com.abiquo.server.core.infrastructure.Machine.State.MANAGED);
        query.setParameter("enterpriseId", enterprise.getId());

        List<Machine> machines = query.list();

        if (machines == null || machines.size() == 0)
        {
            whyNotCandidateMachines(idRack, idVirtualDatacenter, hdRequiredOnDatastore, enterprise);
        }

        // execute the enterprise exclusion rule
        Query excludedQuery = getSession().createQuery(QUERY_CANDIDATE_NO_ENTERPRISE_EXCLUDED);
        excludedQuery.setParameter("enterpriseId", enterprise.getId());
        List<Integer> excludedMachineIds = excludedQuery.list();

        List<Machine> notExcludedMachines = new LinkedList<Machine>();

        for (Machine m : machines)
        {
            Integer machineId = m.getId();

            if (!excludedMachineIds.contains(machineId))
            {
                notExcludedMachines.add(m);
            }
        }

        if (notExcludedMachines.size() == 0)
        {
            throw new PersistenceException("All the candiate machines are excluded by other enterprsies "
                + "with virtual machines deployed on it. Please check the enterprise affinity rules.");
        }

        return notExcludedMachines;
    }

    private void whyNotCandidateMachines(Integer idRack, Integer idVirtualDatacenter,
        Long hdRequiredOnDatastore, Enterprise enterprise) throws PersistenceException
    {
        /**
         * rack and hypervisor type
         */
        Query query1 = getSession().createQuery(QUERY_CANDIDATE_SAME_VDC_RACK_AND_TYPE);
        query1.setInteger("idVirtualDataCenter", idVirtualDatacenter);
        query1.setInteger("idRack", idRack);

        List<Integer> query1res = query1.list();

        if (query1res.size() == 0)
        {
            throw new PersistenceException(String.format(
                "There isn't any machine on the required rack [%d] and virtual datacenter [%d]. "
                    + "Please check the racks and hypervisor technology on the infrastructure.",
                idRack, idVirtualDatacenter));
        }

        /**
         * rack, hypervisor type and managed state
         */
        Query query2 = getSession().createQuery(QUERY_CANDIDATE_SAME_VDC_RACK_AND_TYPE_AND_STATE);
        query2.setInteger("idVirtualDataCenter", idVirtualDatacenter);
        query2.setInteger("idRack", idRack);
        query2.setParameter("state", com.abiquo.server.core.infrastructure.Machine.State.MANAGED);

        List<Integer> query2res = query2.list();

        if (query2res.size() == 0)
        {
            throw new PersistenceException(String.format(
                "There isn't any MANAGED machine on the required rack [%d] and virtual datacenter [%d]. "
                    + "Please check the machine health on the infrastructure.", idRack,
                idVirtualDatacenter));
        }

        /**
         * rack, hypervisor type, managed state and enterprise reservation
         */
        Query query3 =
            getSession().createQuery(
                QUERY_CANDIDATE_SAME_VDC_RACK_AND_TYPE_AND_STATE_AND_RESERVATION);
        query3.setInteger("idVirtualDataCenter", idVirtualDatacenter);
        query3.setInteger("idRack", idRack);
        query3.setParameter("state", com.abiquo.server.core.infrastructure.Machine.State.MANAGED);
        query3.setParameter("enterpriseId", enterprise.getId());

        List<Integer> query3res = query3.list();

        if (query3res.size() == 0)
        {
            throw new PersistenceException(String.format(
                "There isn't any MANAGED machine on the required rack [%d] and virtual datacenter [%d] available for the current enterpirse [%s]. "
                    + "Pleas check the machine reservation policies.", idRack, idVirtualDatacenter,
                enterprise.getName()));
        }

        /**
         * rack, hypervisor type, managed state, enterprise reservation and datastore capacity.
         */
        throw new PersistenceException(String.format(
            "There isn't any machine with the required datastore capacity [%d]",
            hdRequiredOnDatastore));

    }

    public List<Machine> findReservedMachines(Enterprise enterprise)
    {
        Criteria criteria = createCriteria(sameEnterprise(enterprise));
        criteria.addOrder(Order.asc(Machine.NAME_PROPERTY));
        List<Machine> result = getResultList(criteria);
        return result;
    }

    public Machine findReservedMachine(Enterprise enterprise, Integer machineId)
    {
        Criteria criteria = createCriteria(sameEnterprise(enterprise), sameId(machineId));
        return getSingleResult(criteria);
    }

    public void reserveMachine(Machine machine, Enterprise enterprise)
    {
        machine.setEnterprise(enterprise);
        flush();
    }

    public void releaseMachine(Machine machine)
    {
        machine.setEnterprise(null);
        flush();
    }

    private final static String QUERY_CANDIDATE_SAME_VDC_RACK_AND_TYPE = //
        "SELECT m.id FROM " + //
            "com.abiquo.server.core.infrastructure.Machine m, " + //
            "com.abiquo.server.core.cloud.VirtualDatacenter vdc, " + //
            "com.abiquo.server.core.cloud.Hypervisor h " + //
            "JOIN m.datacenter dc " + // managed machine on the VDC and Rack
            "WHERE m = h.machine " + //
            "AND h.type = vdc.hypervisorType " + //
            "AND dc.id = vdc.datacenter.id " + //
            "AND m.rack.id = :idRack " + //
            "AND vdc.id = :idVirtualDataCenter ";

    private final static String QUERY_CANDIDATE_SAME_VDC_RACK_AND_TYPE_AND_STATE = //
        "SELECT m.id FROM " + //
            "com.abiquo.server.core.infrastructure.Machine m, " + //
            "com.abiquo.server.core.cloud.VirtualDatacenter vdc, " + //
            "com.abiquo.server.core.cloud.Hypervisor h " + //
            "JOIN m.datacenter dc " + // managed machine on the VDC and Rack
            "WHERE m = h.machine " + //
            "AND h.type = vdc.hypervisorType " + //
            "AND dc.id = vdc.datacenter.id " + //
            "AND m.rack.id = :idRack " + //
            "AND vdc.id = :idVirtualDataCenter " + //
            "AND m.state = :state ";

    private final static String QUERY_CANDIDATE_SAME_VDC_RACK_AND_TYPE_AND_STATE_AND_RESERVATION = //
        "SELECT m.id FROM " + //
            "com.abiquo.server.core.infrastructure.Machine m, " + //
            "com.abiquo.server.core.cloud.VirtualDatacenter vdc, " + //
            "com.abiquo.server.core.cloud.Hypervisor h " + //
            "JOIN m.datacenter dc " + // managed machine on the VDC and Rack
            "WHERE m = h.machine " + //
            "AND h.type = vdc.hypervisorType " + //
            "AND dc.id = vdc.datacenter.id " + //
            "AND m.rack.id = :idRack " + //
            "AND vdc.id = :idVirtualDataCenter " + //
            "AND m.state = :state " + //
            "AND m.enterprise is null OR m.enterprise.id = :enterpriseId "; // reserved machines

    //
    // private final static String QUERY_CANDIDATE_ENOUGH_DATASTORE = //
    // "  SELECT py.id FROM " + //
    // "  com.abiquo.server.core.infrastructure.Datastore datastore, " + //
    // "  com.abiquo.server.core.infrastructure.Machine py " + //
    // "    WHERE (datastore.size - datastore.usedSize) > :hdRequiredOnRepository " + //
    // "    AND py in elements(datastore.machines) " + //
    // "    AND datastore.size > datastore.usedSize ";//

    private final static String QUERY_CANDIDATE_NO_ENTERPRISE_EXCLUDED = //
        "SELECT DISTINCT vm.hypervisor.machine.id "
            + //
            "FROM com.abiquo.server.core.cloud.VirtualMachine vm WHERE "
            + //
            "   (vm.enterprise.id IN "
            + "      ( SELECT rule.enterprise2.id FROM com.abiquo.server.core.scheduler.EnterpriseExclusionRule rule WHERE rule.enterprise1.id = :enterpriseId )"
            + "   ) OR "
            + "   (vm.enterprise.id IN "
            + "      ( SELECT rule.enterprise1.id FROM com.abiquo.server.core.scheduler.EnterpriseExclusionRule rule WHERE rule.enterprise2.id = :enterpriseId )"
            + "   ) "
            + //
            "   OR " // enterprise doesn't have exlusion rules
            + //
            "   (vm.enterprise.id IN "
            + "      ( SELECT rule.enterprise1.id FROM com.abiquo.server.core.scheduler.EnterpriseExclusionRule rule WHERE rule.enterprise2.id = :enterpriseId )"
            + "   ) ";

    // TODO use criteria (problems with SELECT DISTICNT)
    private final static String QUERY_CANDIDATE_MACHINES = //
        "SELECT m FROM " + //
            "com.abiquo.server.core.infrastructure.Machine m, " + //
            "com.abiquo.server.core.cloud.VirtualDatacenter vdc, " + //
            "com.abiquo.server.core.cloud.Hypervisor h " + //
            "JOIN m.datacenter dc " + // managed machine on the VDC and Rack
            "WHERE m = h.machine " + //
            "AND h.type = vdc.hypervisorType " + //
            "AND dc.id = vdc.datacenter.id " + //
            "AND m.rack.id = :idRack " + //
            "AND vdc.id = :idVirtualDataCenter " + //
            "AND m.state = :state " + // reserved machines
            "AND m.enterprise is null OR m.enterprise.id = :enterpriseId " + //
            "AND m.id IN " + //
            "(" + // with the appropiate datastore
            "  SELECT py.id FROM " + //
            "  com.abiquo.server.core.infrastructure.Datastore datastore, " + //
            "  com.abiquo.server.core.infrastructure.Machine py " + //
            "    WHERE (datastore.size - datastore.usedSize) > :hdRequiredOnRepository " + //
            "    AND py in elements(datastore.machines) " + //
            "    AND datastore.size > datastore.usedSize " + //
            ") ";

    private static final String QUERY_MACHINES_WITHOUT_VMS_IN_ALLOCATOR =
    // Physical Machines with virtual machines deployed with state different
    // of RUNNING and POWERED_OFF
        "SELECT m FROM com.abiquo.server.core.infrastructure.Machine m " + "WHERE m.id not in ( "
            + "SELECT mac.id FROM " + "com.abiquo.server.core.cloud.VirtualMachine vm "
            + "join vm.hypervisor h " + "join h.machine mac "
            + "WHERE vm.state != 'RUNNING' AND vm.state != 'POWERED_OFF' " + ")";

    // "WHERE vm.state = 'RUNNING' OR vm.state = 'POWERED_OFF' " +
    // "UNION " +
    // //union with physical machines without virtual machines deployed
    // "SELECT m FROM " +
    // "com.abiquo.server.core.cloud.VirtualMachine vm " +
    // "right join vm.hypervisor.machine m " +
    // "WHERE vm.hypervisor is null";
    //

}
