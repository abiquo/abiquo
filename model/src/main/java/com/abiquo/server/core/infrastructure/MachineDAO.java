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
import com.abiquo.server.core.infrastructure.Machine.State;

@Repository("jpaMachineDAO")
@SuppressWarnings("unchecked")
public class MachineDAO extends DefaultDAOBase<Integer, Machine>
{

    public MachineDAO()
    {
        super(Machine.class);
    }

    public MachineDAO(final EntityManager entityManager)
    {
        super(Machine.class, entityManager);
    }

    private static Criterion sameDatacenter(final Datacenter datacenter)
    {
        assert datacenter != null;

        return Restrictions.eq(Machine.DATACENTER_PROPERTY, datacenter);
    }

    private static Criterion sameRack(final Rack rack)
    {
        assert rack != null;

        return Restrictions.eq(Machine.RACK_PROPERTY, rack);
    }

    private static Criterion sameId(final Integer id)
    {
        return Restrictions.eq(Machine.ID_PROPERTY, id);
    }

    private static Criterion sameName(final String name)
    {
        assert !StringUtils.isEmpty(name);

        return Restrictions.eq(Machine.NAME_PROPERTY, name);
    }

    private Criterion sameEnterprise(final Enterprise enterprise)
    {
        return Restrictions.eq(Machine.ENTERPRISE_PROPERTY, enterprise);
    }

    public List<Machine> findMachines(final Datacenter datacenter)
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
    public Machine isMachineInAllocator(final Integer machineId)
    {
        // The way to define the virtual machines in the allocator is:
        // All the virtual machines with an hypervisor associated and with state=NOT_DEPLOYED
        Query query = getSession().createQuery(QUERY_IS_MACHINE_IN_ALLOCATOR);
        query.setParameter("machineId", machineId);

        return (Machine) query.uniqueResult();

    }

    public List<Machine> findRackMachines(final Rack rack)
    {
        assert rack != null;
        assert isManaged2(rack);

        Criteria criteria = createCriteria(sameRack(rack));
        criteria.addOrder(Order.asc(Machine.NAME_PROPERTY));
        List<Machine> result = getResultList(criteria);
        return result;
    }

    public List<Machine> findRackEnabledForHAMachines(final Rack rack)
    {
        Criteria criteria = createCriteria(sameRack(rack));

        // Is a managed one
        criteria.add(Restrictions.eq(Machine.STATE_PROPERTY, State.MANAGED));

        // Has fencing capabilities
        criteria.add(Restrictions.isNotNull(Machine.IPMI_IP_PROPERTY));
        criteria.add(Restrictions.isNotNull(Machine.IPMI_USER_PROPERTY));
        criteria.add(Restrictions.isNotNull(Machine.IPMI_PASSWORD_PROPERTY));

        // Order by name
        criteria.addOrder(Order.asc(Machine.NAME_PROPERTY));

        List<Machine> result = getResultList(criteria);
        return result;
    }

    public boolean existsAnyWithDatacenterAndName(final Datacenter datacenter, final String name)
    {
        assert datacenter != null;
        assert !StringUtils.isEmpty(name);

        return existsAnyByCriterions(sameDatacenter(datacenter), sameName(name));
    }

    public boolean existsAnyOtherWithDatacenterAndName(final Machine machine, final String name)
    {
        assert machine != null;
        assert !StringUtils.isEmpty(name);

        return existsAnyOtherByCriterions(machine, sameDatacenter(machine.getDatacenter()),
            sameName(name));
    }

    public int deleteRackMachines(final Rack rack)
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

    public List<Machine> findCandidateMachines(final Integer idRack,
        final Integer idVirtualDatacenter, final Long hdRequiredOnDatastore,
        final Enterprise enterprise)
    {

        List<Machine> machines;

        if (enterprise.getIsReservationRestricted())
        {
            machines =
                findFirstCandidateMachinesReservedRestricted(idRack, idVirtualDatacenter,
                    hdRequiredOnDatastore, enterprise);
        }
        else
        {
            machines =
                findFirstCandidateMachines(idRack, idVirtualDatacenter, hdRequiredOnDatastore,
                    enterprise);
        }

        // StringBuilder sbcandidates = new StringBuilder();
        List<Integer> candidatesids = new LinkedList<Integer>();
        for (Machine m : machines)
        {
            candidatesids.add(m.getId());
        }

        // with datastore
        Query datastoreQuery = getSession().createQuery(QUERY_CANDIDATE_DATASTORE);
        datastoreQuery.setLong("hdRequiredOnRepository", hdRequiredOnDatastore);
        datastoreQuery.setParameterList("candidates", candidatesids);

        List<Integer> includedIds = datastoreQuery.list();

        if (includedIds.size() == 0)
        {
            throw new PersistenceException(String.format(
                "There isn't any machine with the required datastore capacity [%d]",
                hdRequiredOnDatastore));
        }

        // execute the enterprise exclusion rule
        Query excludedQuery = getSession().createQuery(QUERY_CANDIDATE_NO_ENTERPRISE_EXCLUDED);
        excludedQuery.setParameter("enterpriseId", enterprise.getId());
        List<Integer> excludedMachineIds = excludedQuery.list();

        List<Machine> notExcludedMachines = new LinkedList<Machine>();

        for (Machine m : machines)
        {
            Integer machineId = m.getId();

            if (!excludedMachineIds.contains(machineId) && includedIds.contains(machineId))
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

    private List<Machine> findFirstCandidateMachines(final Integer idRack,
        final Integer idVirtualDatacenter, final Long hdRequiredOnDatastore,
        final Enterprise enterprise)
    {
        List<Machine> machines = null;

        Query query = getSession().createQuery(QUERY_CANDIDATE_MACHINES);

        query.setInteger("idVirtualDataCenter", idVirtualDatacenter);
        query.setInteger("idRack", idRack);
        query.setParameter("state", com.abiquo.server.core.infrastructure.Machine.State.MANAGED);
        query.setParameter("enterpriseId", enterprise.getId());

        machines = query.list();

        if (machines == null || machines.size() == 0)
        {
            whyNotCandidateMachines(idRack, idVirtualDatacenter, hdRequiredOnDatastore, enterprise,
                null);
        }

        return machines;
    }

    private List<Machine> findFirstCandidateMachinesReservedRestricted(final Integer idRack,
        final Integer idVirtualDatacenter, final Long hdRequiredOnDatastore,
        final Enterprise enterprise)
    {

        List<Machine> machines = null;

        List<Machine> reservMachines = findReservedMachines(enterprise);

        if (reservMachines != null && reservMachines.size() != 0)
        {
            List<Integer> reserveds = new LinkedList<Integer>();
            for (Machine m : reservMachines)
            {
                reserveds.add(m.getId());
            }

            Query query = getSession().createQuery(QUERY_CANDIDATE_MACHINES_RESERVED);
            query.setInteger("idVirtualDataCenter", idVirtualDatacenter);
            query.setInteger("idRack", idRack);
            query
                .setParameter("state", com.abiquo.server.core.infrastructure.Machine.State.MANAGED);
            query.setParameterList("reserveds", reserveds);

            machines = query.list();

            if (machines == null || machines.size() == 0)
            {
                whyNotCandidateMachines(idRack, idVirtualDatacenter, hdRequiredOnDatastore,
                    enterprise, reserveds);
            }

            return machines;
        }
        else
        {
            final String msg =
                String.format("Enterprise work in restricted reserved machines "
                    + "and any machine is reserver current enterprise : %s", enterprise.getName());

            throw new PersistenceException(msg);
        }
    }

    private List<Machine> findFirstCandidateMachinesReservedRestrictedHAExclude(
        final Integer idRack, final Integer idVirtualDatacenter, final Enterprise enterprise,
        final Integer originalHypervisorId)
    {

        List<Machine> machines = null;

        List<Machine> reservMachines = findReservedMachines(enterprise);

        if (reservMachines != null && reservMachines.size() != 0)
        {
            List<Integer> reserveds = new LinkedList<Integer>();
            for (Machine m : reservMachines)
            {
                reserveds.add(m.getId());
            }

            Query query =
                getSession().createQuery(QUERY_CANDIDATE_MACHINES_RESERVED_HA_EXCLUDE_ORIGINAL);
            query.setInteger("idVirtualDataCenter", idVirtualDatacenter);
            query.setInteger("idRack", idRack);
            query
                .setParameter("state", com.abiquo.server.core.infrastructure.Machine.State.MANAGED);
            query.setParameterList("reserveds", reserveds);
            query.setInteger("enterpriseId", enterprise.getId());
            query.setInteger("originalHypervisorId", originalHypervisorId);

            machines = query.list();

            if (machines == null || machines.size() == 0)
            {
                whyNotCandidateMachines(idRack, idVirtualDatacenter, 0l, enterprise, reserveds);
            }

            return machines;
        }
        else
        {
            final String msg =
                String.format("Enterprise work in restricted reserved machines "
                    + "and any machine is reserver current enterprise : %s", enterprise.getName());

            throw new PersistenceException(msg);
        }

    }

    /**
     * Do not require additional space on the datastore. Used during HA, selects a machine different
     * of the ''originalHypervisorId'' with the same ''datastoreUuid'' enabled.
     */
    public List<Machine> findCandidateMachines(final Integer idRack,
        final Integer idVirtualDatacenter, final Enterprise enterprise, final String datastoreUuid,
        final Integer originalHypervisorId)
    {

        List<Machine> machines = null;
        if (enterprise.getIsReservationRestricted())
        {
            machines =
                findFirstCandidateMachinesReservedRestrictedHAExclude(idRack, idVirtualDatacenter,
                    enterprise, originalHypervisorId);
        }

        else
        {
            Query query = getSession().createQuery(QUERY_CANDIDATE_MACHINES_HA_EXCLUDE_ORIGINAL);
            query.setInteger("idVirtualDataCenter", idVirtualDatacenter);
            query.setInteger("idRack", idRack);
            query
                .setParameter("state", com.abiquo.server.core.infrastructure.Machine.State.MANAGED);
            query.setParameter("enterpriseId", enterprise.getId());
            query.setParameter("originalHypervisorId", originalHypervisorId);

            machines = query.list();
        }

        if (machines == null || machines.size() == 0)
        {
            Query query = getSession().createQuery(QUERY_CANDIDATE_MACHINES_HA_EXCLUDE_ORIGINAL);
            query.setInteger("idVirtualDataCenter", idVirtualDatacenter);
            query.setInteger("idRack", idRack);
            query
                .setParameter("state", com.abiquo.server.core.infrastructure.Machine.State.MANAGED);
            query.setParameter("enterpriseId", enterprise.getId());

            machines = query.list();

            if (machines == null || machines.size() == 0)
            {
                throw new PersistenceException(String.format(
                    "There isn't any MANAGED machine on the required rack [%d] and virtual datacenter [%d] available for the current enterpirse [%s]. "
                        + "Pleas check the machine reservation policies.", idRack,
                    idVirtualDatacenter, enterprise.getName()));
            }
            else
            {
                throw new PersistenceException(String.format(
                    "The only MANAGED machine on the required rack [%d] and virtual datacenter [%d] available for the current enterpirse [%s]"
                        + "is the target of the high availability (so can't be used) ", idRack,
                    idVirtualDatacenter, enterprise.getName()));
            }
        }

        // StringBuilder sbcandidates = new StringBuilder();
        List<Integer> candidatesids = new LinkedList<Integer>();
        for (Machine m : machines)
        {
            candidatesids.add(m.getId());
        }

        // with datastore
        Query datastoreQuery = getSession().createQuery(QUERY_CANDIDATE_DATASTORE_HA_DATASTOREUUID);
        datastoreQuery.setParameterList("candidates", candidatesids);
        datastoreQuery.setParameter("datastoreUuid", datastoreUuid);

        List<Integer> includedIds = datastoreQuery.list();

        if (includedIds.size() == 0)
        {
            throw new PersistenceException(String.format(
                "There isn't any machine with the required shared datastore [%s]", datastoreUuid));
        }

        // execute the enterprise exclusion rule
        Query excludedQuery = getSession().createQuery(QUERY_CANDIDATE_NO_ENTERPRISE_EXCLUDED);
        excludedQuery.setParameter("enterpriseId", enterprise.getId());
        List<Integer> excludedMachineIds = excludedQuery.list();

        List<Machine> notExcludedMachines = new LinkedList<Machine>();

        for (Machine m : machines)
        {
            Integer machineId = m.getId();

            if (!excludedMachineIds.contains(machineId) && includedIds.contains(machineId))
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

    private void whyNotCandidateMachines(final Integer idRack, final Integer idVirtualDatacenter,
        final Long hdRequiredOnDatastore, final Enterprise enterprise, final List<Integer> reserveds)
        throws PersistenceException
    {

        if (reserveds != null)
        {

            StringBuilder reservedMachinesB =
                new StringBuilder(String.format(
                    "Enterprise %s have the follow machine reservations : ", enterprise.getName()));
            for (Integer mid : reserveds)
            {
                reservedMachinesB.append(mid + ' ');
            }

            /**
             * rack and hypervisor type
             */
            Query query1 =
                getSession().createQuery(WHY_QUERY_CANDIDATE_SAME_VDC_RACK_AND_TYPE_AND_RESERVED);
            query1.setInteger("idVirtualDataCenter", idVirtualDatacenter);
            query1.setInteger("idRack", idRack);
            query1.setParameterList("reserveds", reserveds);

            List<Integer> query1res = query1.list();

            if (query1res.size() == 0)
            {
                throw new PersistenceException(String.format(
                    "%s\nThere isn't any machine on the required rack [%d] and virtual datacenter [%d]. "
                        + "Please check the racks and hypervisor technology on the infrastructure.",
                    reservedMachinesB.toString(), idRack, idVirtualDatacenter));
            }

            /**
             * rack, hypervisor type and managed state
             */
            Query query2 =
                getSession().createQuery(
                    WHT_QUERY_CANDIDATE_SAME_VDC_RACK_AND_TYPE_AND_STATE_AND_RESERVED_RESTRICTED);
            query2.setInteger("idVirtualDataCenter", idVirtualDatacenter);
            query2.setInteger("idRack", idRack);
            query2.setParameter("state",
                com.abiquo.server.core.infrastructure.Machine.State.MANAGED);
            query2.setParameterList("reserveds", reserveds);

            List<Integer> query2res = query2.list();

            if (query2res.size() == 0)
            {
                throw new PersistenceException(String.format(
                    "%s\nThere isn't any MANAGED machine on the required rack [%d] and virtual datacenter [%d]. "
                        + "Please check the machine health on the infrastructure.",
                    reservedMachinesB.toString(), idRack, idVirtualDatacenter));
            }

            /**
             * rack, hypervisor type, managed state, enterprise reservation and datastore capacity.
             */
            throw new PersistenceException(String.format(
                "%s\nThere isn't any machine with the required datastore capacity [%d]",
                reservedMachinesB.toString(), hdRequiredOnDatastore));
        } // reserved machines
        else
        {
            /**
             * rack and hypervisor type
             */
            Query query1 = getSession().createQuery(WHY_QUERY_CANDIDATE_SAME_VDC_RACK_AND_TYPE);
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
            Query query2 =
                getSession().createQuery(WHT_QUERY_CANDIDATE_SAME_VDC_RACK_AND_TYPE_AND_STATE);
            query2.setInteger("idVirtualDataCenter", idVirtualDatacenter);
            query2.setInteger("idRack", idRack);
            query2.setParameter("state",
                com.abiquo.server.core.infrastructure.Machine.State.MANAGED);

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
                    WHT_QUERY_CANDIDATE_SAME_VDC_RACK_AND_TYPE_AND_STATE_AND_RESERVED);
            query3.setInteger("idVirtualDataCenter", idVirtualDatacenter);
            query3.setInteger("idRack", idRack);
            query3.setParameter("state",
                com.abiquo.server.core.infrastructure.Machine.State.MANAGED);
            query3.setParameter("enterpriseId", enterprise.getId());

            List<Integer> query3res = query3.list();

            if (query3res.size() == 0)
            {
                throw new PersistenceException(String.format(
                    "There isn't any MANAGED machine on the required rack [%d] and virtual datacenter [%d] available for the current enterpirse [%s]. "
                        + "Pleas check the machine reservation policies.", idRack,
                    idVirtualDatacenter, enterprise.getName()));
            }

            /**
             * rack, hypervisor type, managed state, enterprise reservation and datastore capacity.
             */
            throw new PersistenceException(String.format(
                "There isn't any machine with the required datastore capacity [%d]",
                hdRequiredOnDatastore));
        }
    }

    public List<Machine> findReservedMachines(final Enterprise enterprise)
    {
        Criteria criteria = createCriteria(sameEnterprise(enterprise));
        criteria.addOrder(Order.asc(Machine.NAME_PROPERTY));
        List<Machine> result = getResultList(criteria);
        return result;
    }

    public Machine findReservedMachine(final Enterprise enterprise, final Integer machineId)
    {
        Criteria criteria = createCriteria(sameEnterprise(enterprise), sameId(machineId));
        return getSingleResult(criteria);
    }

    public void reserveMachine(final Machine machine, final Enterprise enterprise)
    {
        machine.setEnterprise(enterprise);
        flush();
    }

    public void releaseMachine(final Machine machine)
    {
        machine.setEnterprise(null);
        flush();
    }

    private final static String WHY_QUERY_CANDIDATE_SAME_VDC_RACK_AND_TYPE = //
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
            "AND m.enterprise is null";

    private final static String WHT_QUERY_CANDIDATE_SAME_VDC_RACK_AND_TYPE_AND_STATE = //
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
            "AND m.enterprise is null";

    private final static String WHY_QUERY_CANDIDATE_SAME_VDC_RACK_AND_TYPE_AND_RESERVED = //
        "SELECT m.id FROM " + //
            "com.abiquo.server.core.infrastructure.Machine m, " + //
            "com.abiquo.server.core.cloud.VirtualDatacenter vdc, " + //
            "com.abiquo.server.core.cloud.Hypervisor h " + //
            "JOIN m.datacenter dc " + // managed machine on the VDC and Rack
            "WHERE m.id in (:reserveds) " + //
            "AND m = h.machine " + //
            "AND h.type = vdc.hypervisorType " + //
            "AND dc.id = vdc.datacenter.id " + //
            "AND m.rack.id = :idRack " + //
            "AND vdc.id = :idVirtualDataCenter ";

    private final static String WHT_QUERY_CANDIDATE_SAME_VDC_RACK_AND_TYPE_AND_STATE_AND_RESERVED_RESTRICTED = //
        "SELECT m.id FROM " + //
            "com.abiquo.server.core.infrastructure.Machine m, " + //
            "com.abiquo.server.core.cloud.VirtualDatacenter vdc, " + //
            "com.abiquo.server.core.cloud.Hypervisor h " + //
            "JOIN m.datacenter dc " + // managed machine on the VDC and Rack
            "WHERE m.id in (:reserveds) " + //
            "AND m = h.machine " + //
            "AND h.type = vdc.hypervisorType " + //
            "AND dc.id = vdc.datacenter.id " + //
            "AND m.rack.id = :idRack " + //
            "AND vdc.id = :idVirtualDataCenter " + //
            "AND m.state = :state ";

    // "AND m.enterprise is null OR m.enterprise.id = :enterpriseId "; // reserved machines

    private final static String WHT_QUERY_CANDIDATE_SAME_VDC_RACK_AND_TYPE_AND_STATE_AND_RESERVED = //
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
            "AND m.enterprise is null OR m.enterprise.id = :enterpriseId ";

    private final static String QUERY_CANDIDATE_MACHINES_HA_EXCLUDE_ORIGINAL = //
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
            "AND h.id <> :originalHypervisorId";

    private final static String QUERY_CANDIDATE_MACHINES_RESERVED = //
        "SELECT m FROM " + //
            "com.abiquo.server.core.infrastructure.Machine m, " + //
            "com.abiquo.server.core.cloud.VirtualDatacenter vdc, " + //
            "com.abiquo.server.core.cloud.Hypervisor h " + //
            "JOIN m.datacenter dc " + // managed machine on the VDC and Rack
            "WHERE m.id in (:reserveds) " + //
            "AND m = h.machine " + //
            "AND h.type = vdc.hypervisorType " + //
            "AND dc.id = vdc.datacenter.id " + //
            "AND m.rack.id = :idRack " + //
            "AND vdc.id = :idVirtualDataCenter " + //
            "AND m.state = :state ";// + // reserved machines

    private final static String QUERY_CANDIDATE_MACHINES_RESERVED_HA_EXCLUDE_ORIGINAL = //
        "SELECT m FROM " + //
            "com.abiquo.server.core.infrastructure.Machine m, " + //
            "com.abiquo.server.core.cloud.VirtualDatacenter vdc, " + //
            "com.abiquo.server.core.cloud.Hypervisor h " + //
            "JOIN m.datacenter dc " + // managed machine on the VDC and Rack
            "WHERE m.id in (:reserveds) " + //
            "AND m = h.machine " + //
            "AND h.type = vdc.hypervisorType " + //
            "AND dc.id = vdc.datacenter.id " + //
            "AND m.rack.id = :idRack " + //
            "AND vdc.id = :idVirtualDataCenter " + //
            "AND m.state = :state " + // reserved machines
            "AND m.enterprise is null OR m.enterprise.id = :enterpriseId " + //
            "AND h.id <> :originalHypervisorId";

    // "AND m.enterprise is null"; //"AND m.enterprise is null OR m.enterprise.id = :enterpriseId ";

    private final static String QUERY_CANDIDATE_DATASTORE = //
        "  SELECT py.id FROM "
            + //
            "  com.abiquo.server.core.infrastructure.Datastore datastore, "
            + //
            "  com.abiquo.server.core.infrastructure.Machine py "
            + //
            "    WHERE py.id in (:candidates)"
            + "    AND (datastore.size - datastore.usedSize) > :hdRequiredOnRepository " + //
            "    AND py in elements(datastore.machines) " + //
            "    AND datastore.size > datastore.usedSize " + //
            "    AND datastore.enabled = true";

    private final static String QUERY_CANDIDATE_DATASTORE_HA_DATASTOREUUID = //
        "  SELECT py.id FROM " + //
            "  com.abiquo.server.core.infrastructure.Datastore datastore, " + //
            "  com.abiquo.server.core.infrastructure.Machine py " + //
            "    WHERE py.id in (:candidates)" + //
            // "    AND (datastore.size - datastore.usedSize) > :hdRequiredOnRepository " + //
            "    AND py in elements(datastore.machines) " + //
            "    AND datastore.size > datastore.usedSize " + //
            "    AND datastore.enabled = true " + //
            "    AND datastore.datastoreUUID = :datastoreUuid";

    private static final String QUERY_IS_MACHINE_IN_ALLOCATOR =
        "SELECT m FROM com.abiquo.server.core.infrastructure.Machine m " + "WHERE m.id not in ( "
            + "SELECT mac.id FROM " + "com.abiquo.server.core.cloud.VirtualMachine vm "
            + "join vm.hypervisor h " + "join h.machine mac "
            + "WHERE vm.state != 'RUNNING' AND vm.state != 'POWERED_OFF' "
            + ") AND m.id = :machineId";

    public Machine findByIds(final Integer datacenterId, final Integer rackId,
        final Integer machineId)
    {
        return findUniqueByCriterions(Restrictions.eq("datacenter.id", datacenterId),
            Restrictions.eq("rack.id", rackId), Restrictions.eq("id", machineId));
    }

}
