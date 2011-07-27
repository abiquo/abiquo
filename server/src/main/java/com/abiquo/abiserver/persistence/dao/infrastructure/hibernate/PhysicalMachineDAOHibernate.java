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

package com.abiquo.abiserver.persistence.dao.infrastructure.hibernate;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.PhysicalmachineHB;
import com.abiquo.abiserver.business.hibernate.pojohb.user.EnterpriseHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualmachineHB;
import com.abiquo.abiserver.exception.PersistenceException;
import com.abiquo.abiserver.persistence.dao.infrastructure.PhysicalMachineDAO;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAO;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAOFactory;

/**
 * * Class that implements the extra DAO functions for the
 * {@link com.abiquo.abiserver.persistence.dao.infrastructure.PhysicalMachineDAO} interface
 * 
 * @author jdevesa@abiquo.com
 */
public class PhysicalMachineDAOHibernate extends HibernateDAO<PhysicalmachineHB, Integer> implements
    PhysicalMachineDAO
{

    private static final String PHYSICALMACHINE_GET_NUMBER_OF_DEPLOYED_MACHINES =
        "PHYSICALMACHINE.GET_NUMBER_OF_DEPLOYED_MACHINES";

    private static final String PHYSICALMACHINE_GET_DEPLOYED_VIRTUAL_MACHINES =
        "PHYSICALMACHINE.GET_DEPLOYED_VIRTUAL_MACHINES";

    private static final String PHYSICALMACHINE_GET_DEPLOYED_ABIQUO_VIRTUAL_MACHINES =
        "PHYSICALMACHINE.GET_DEPLOYED_ABIQUO_VIRTUAL_MACHINES";

    private static final String PHYSICALMACHINE_GET_HYPERVISOR_IP =
        "PHYSICALMACHINE.GET_HYPERVISOR_IP";

    private static final String PHYSICALMACHINE_GET_FROM_IP = "PHYSICALMACHINE.GET_FROM_IP";

    private static final String PHYSICALMACHINE_GET_ALL_HYPERVISOR_IP =
        "PHYSICALMACHINE.GET_ALL_HYPERVISOR_IP";

    private static final String PHYSICALMACHINE_GET_NOT_MANAGED_VIRTUAL_MACHINES =
        "PHYSICALMACHINE.GET_NOT_MANAGED_VIRTUAL_MACHINES";

    private static final String PHYSICALMACHINE_GET_LIST_BY_DATASTORE =
        "PHYSICALMACHINE.GET_LIST_BY_DATASTORE";

    private static final String PHYSICALMACHINE_GET_LIST_BY_RACK =
        "PHYSICALMACHINE.GET_LIST_BY_RACK";

    private static final String FIRST_PASS_QUERY = "PHYSICALMACHINE.FIRST_PASS_QUERY";

    private static final String PHYSICALMACHINE_GET_NUMBER_OF_DEPLOYED_MACHINES_OWNED_BY_OTHER_ENTERPRISE =
        "PHYSICALMACHINE.GET_NUMBER_OF_DEPLOYED_MACHINES_OWNED_BY_OTHER_ENTERPRISE";

    @Override
    public List<PhysicalmachineHB> getByRackAndVirtualDatacenter(Integer idRack,
        Integer idVirtualDatacenter, Long hdRequiredOnDatastore, EnterpriseHB enterprise)
    {

        // "(pm.ram - pm.ramUsed) >= " + vimage.getRamRequired() +
        // " and ((pm.cpu * pm.cpuRatio) - pm.cpuUsed) >= " + vimage.getCpuRequired();
        Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();
        Query pmQuery = session.getNamedQuery(FIRST_PASS_QUERY);

        pmQuery.setInteger("idVirtualDataCenter", idVirtualDatacenter);
        pmQuery.setInteger("idRack", idRack);
        pmQuery.setLong("hdRequiredOnRepository", hdRequiredOnDatastore);
        pmQuery.setParameter("enterprise", enterprise);

        return (List<PhysicalmachineHB>) pmQuery.list();
    }

    // implement extra functionality
    @Override
    public Long getNumberOfDeployedVirtualMachines(PhysicalmachineHB pmHB)
    {
        Long numberOfDeployedMachines;

        Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();
        Query pmQuery = session.getNamedQuery(PHYSICALMACHINE_GET_NUMBER_OF_DEPLOYED_MACHINES);
        pmQuery.setInteger("idphysicalmachine", pmHB.getIdPhysicalMachine());
        numberOfDeployedMachines = (Long) pmQuery.uniqueResult();

        return numberOfDeployedMachines;
    }

    @Override
    public Long getNumberOfDeployedVirtualMachinesOwnedByOtherEnterprise(PhysicalmachineHB pmHB,
        Integer idEnterprise)
    {
        Long numberOfDeployedMachines;

        Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();
        Query pmQuery =
            session
                .getNamedQuery(PHYSICALMACHINE_GET_NUMBER_OF_DEPLOYED_MACHINES_OWNED_BY_OTHER_ENTERPRISE);
        pmQuery.setInteger("idphysicalmachine", pmHB.getIdPhysicalMachine());
        pmQuery.setInteger("identerprise", idEnterprise);
        List<Long> longList = (List<Long>) pmQuery.list();
        numberOfDeployedMachines = longList.get(0);
        return numberOfDeployedMachines;
    }

    @Override
    public List<VirtualmachineHB> getDeployedVirtualMachines(Integer machineId)
    {
        Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();

        Query pmQuery = session.getNamedQuery(PHYSICALMACHINE_GET_DEPLOYED_VIRTUAL_MACHINES);
        pmQuery.setInteger("idphysicalmachine", machineId);

        return (List<VirtualmachineHB>) pmQuery.list();
    }

    @Override
    public List<VirtualmachineHB> getDeployedAbiquoVirtualMachines(Integer machineId)
    {
        Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();

        Query pmQuery = session.getNamedQuery(PHYSICALMACHINE_GET_DEPLOYED_ABIQUO_VIRTUAL_MACHINES);
        pmQuery.setInteger("idphysicalmachine", machineId);

        return (List<VirtualmachineHB>) pmQuery.list();
    }

    @Override
    public String getHypervisorIP(int machineId)
    {
        Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();

        Query pmQuery = session.getNamedQuery(PHYSICALMACHINE_GET_HYPERVISOR_IP);
        pmQuery.setInteger("idphysicalmachine", machineId);

        String hyperIp = (String) pmQuery.uniqueResult();

        return hyperIp;
    }

    @Override
    public void setPhysicalMachineState(Integer machineId, int idPhysicalMachineState)
        throws PersistenceException
    {
        PhysicalmachineHB pm = findById(machineId);

        pm.setIdState(idPhysicalMachineState);

        makePersistent(pm);
    }

    @Override
    public PhysicalmachineHB findByIp(String hypervisorIp, Integer idDataCenter)
    {
        Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();

        Query pmQuery = session.getNamedQuery(PHYSICALMACHINE_GET_FROM_IP);
        pmQuery.setString("ipPhysicalMachine", hypervisorIp);
        pmQuery.setInteger("idDataCenter", idDataCenter);

        PhysicalmachineHB pm = (PhysicalmachineHB) pmQuery.uniqueResult();

        return pm;
    }

    @Override
    public List<String> findAllIp()
    {
        Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();

        Query pmQuery = session.getNamedQuery(PHYSICALMACHINE_GET_ALL_HYPERVISOR_IP);

        return (List<String>) pmQuery.list();
    }

    @Override
    public List<VirtualmachineHB> getNotDeployedVirtualMachines(Integer hostId)
    {
        Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();

        Query pmQuery = session.getNamedQuery(PHYSICALMACHINE_GET_NOT_MANAGED_VIRTUAL_MACHINES);
        pmQuery.setInteger("idphysicalmachine", hostId);

        return (List<VirtualmachineHB>) pmQuery.list();
    }

    @Override
    public List<PhysicalmachineHB> getPhysicalMachineListByDatastore(Integer datastoreId)
    {
        Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();

        Query pmQuery = session.getNamedQuery(PHYSICALMACHINE_GET_LIST_BY_DATASTORE);
        pmQuery.setInteger("idDatastore", datastoreId);

        return pmQuery.list();
    }

    @Override
    public List<PhysicalmachineHB> getPhysicalMachineByRack(Integer rackId, String filters)
    {
        Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();

        String namedQuery =
            filters == null || filters.isEmpty() ? PHYSICALMACHINE_GET_LIST_BY_RACK
                : "PHYSICALMACHINE.GET_LIST_BY_RACK_AND_ENTERPRISE";

        Query pmQuery = session.getNamedQuery(namedQuery);
        pmQuery.setInteger("idRack", rackId);
        if (filters != null && !filters.isEmpty())
        {
            pmQuery.setString("filterLike", "%" + filters + "%");
        }

        return pmQuery.list();
    }

    @Override
    public void updateUsedResourcesByPhysicalMachine(Integer idPhysicalMachine)
    {
        final Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();
        final String update =
            "update physicalmachine p, "
                + "(SELECT hy.idPhysicalMachine, IFNULL(SUM(vm.ram),0) ram, IFNULL(SUM(vm.cpu),0) cpu, IFNULL(SUM(vm.hd),0) hd "
                + "FROM virtualmachine vm right join hypervisor hy on vm.idHypervisor = hy.id, "
                + "physicalmachine pm "
                + "WHERE (vm.state is null or vm.state != 'NOT_DEPLOYED') AND pm.idPhysicalMachine = hy.idPhysicalMachine "
                + "AND pm.idPhysicalMachine = :idPhysicalMachine "
                + "group by hy.idPhysicalMachine) x "
                + "set p.ramused = x.ram, p.cpuused = x.cpu, p.hdused = x.hd where p.idPhysicalMachine = x.idPhysicalMachine ";
        final Query pmQuery = session.createSQLQuery(update);
        pmQuery.setInteger("idPhysicalMachine", idPhysicalMachine);
        pmQuery.executeUpdate();

    }

}
