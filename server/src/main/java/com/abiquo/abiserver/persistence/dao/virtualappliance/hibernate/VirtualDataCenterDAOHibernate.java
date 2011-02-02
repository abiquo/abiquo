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

package com.abiquo.abiserver.persistence.dao.virtualappliance.hibernate;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualDataCenterHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualhardware.LimitHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualhardware.ResourceAllocationLimitHB;
import com.abiquo.abiserver.exception.PersistenceException;
import com.abiquo.abiserver.persistence.dao.virtualappliance.VirtualDataCenterDAO;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAO;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAOFactory;

/**
 * Class that implements the extra DAO functions for the
 * {@link com.abiquo.abiserver.persistence.dao.virtualappliance.VirtualDataCenterDAO} interface
 * 
 * @author jdevesa@abiquo.com
 */
@SuppressWarnings("unchecked")
public class VirtualDataCenterDAOHibernate extends HibernateDAO<VirtualDataCenterHB, Integer>
    implements VirtualDataCenterDAO
{

    private static final String VIRTUAL_DATACENTER_GET_BY_NETWORK =
        "VIRTUAL_DATACENTER.GET_BY_NETWORK";

    private static final String VIRTUAL_DATACENTER_GET_BY_VAPP = "VIRTUAL_DATACENTER_GET_BY_VAPP";

    private static final String VIRTUAL_DATACENTERS_BY_ENTERPRISE =
        "VIRTUAL_DATACENTER_BY_ENTERPRISE";

    private static final String VIRTUAL_DATACENTERS_BY_ENTERPRISE_AND_DATACENTER =
        "VIRTUAL_DATACENTER_BY_ENTERPRISE_AND_DATACENTER";

//    private static final String SUM_VM_RESOURCES =
//        "select sum(vm.cpu), sum(vm.ram), sum(vm.hd) from virtualmachine vm, nodevirtualimage vi, node n, virtualapp a  "
//            + "where vi.idVM = vm.idVM and vi.idNode = n.idNode and n.idVirtualApp = a.idVirtualApp "
//            + "and a.idVirtualDataCenter = :virtualDatacenterId and STRCMP(vm.state, :not_deployed) != 0";

    private static final String SUM_VOLUMES_RESOURCES =
        "select sum(r.limitResource) from rasd r, rasd_management rm where r.instanceID = rm.idResource and rm.idResourceType = '8' and rm.idVirtualDatacenter = :virtualDatacenterId";

    private static final String COUNT_PUBLIC_IP_RESOURCES =
        "select count(*) from ip_pool_management ipm, network_configuration nc, vlan_network vn, datacenter dc, rasd_management rm "
            + " where ipm.dhcp_service_id=nc.dhcp_service_id and vn.network_configuration_id = nc.network_configuration_id and vn.network_id = dc.network_id and rm.idManagement = ipm.idManagement "
            + " and ipm.mac is not null " //
            + " and rm.idVirtualDataCenter = :virtualDatacenterId";

    // "select count(rm.idManagement) from rasd r, rasd_management rm "
    // +
    // "where r.resourceType = rm.idResourceType and rm.idResourceType = '10' and r.resourceSubType = '1' and rm.idVirtualDatacenter = :virtualDatacenterId";

    @Override
    public VirtualDataCenterHB findByIdNamed(Integer id)
    {
        return (VirtualDataCenterHB) getSession().get("VirtualDataCenterHB", id);
    }

    @Override
    public VirtualDataCenterHB getVirtualDatacenterFromNetworkId(Integer idNetwork)
        throws PersistenceException
    {
        VirtualDataCenterHB vdcHB;

        try
        {
            Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();
            Query query = session.getNamedQuery(VIRTUAL_DATACENTER_GET_BY_NETWORK);
            query.setInteger("network_id", idNetwork);
            vdcHB = (VirtualDataCenterHB) query.uniqueResult();
        }
        catch (HibernateException he)
        {
            throw new PersistenceException(he.getMessage(), he);
        }

        return vdcHB;
    }

    @Override
    public VirtualDataCenterHB getVirtualDatacenterFromVirtualAppliance(Integer vappId)
        throws PersistenceException
    {
        try
        {
            Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();
            Query query = session.getNamedQuery(VIRTUAL_DATACENTER_GET_BY_VAPP);
            query.setInteger("vappId", vappId);
            return (VirtualDataCenterHB) query.uniqueResult();
        }
        catch (HibernateException he)
        {
            throw new PersistenceException(he.getMessage(), he);
        }

    }

    @Override
    public Collection<VirtualDataCenterHB> getVirtualDatacentersFromEnterprise(Integer enterpriseId)
    {
        Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();
        return session.getNamedQuery(VIRTUAL_DATACENTERS_BY_ENTERPRISE)
            .setParameter("enterpriseId", enterpriseId).list();
    }

    @Override
    public Collection<VirtualDataCenterHB> getVirtualDatacentersFromEnterpriseAndDatacenter(
        Integer enterpriseId, Integer datacenterId)
    {
        Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();
        return session.getNamedQuery(VIRTUAL_DATACENTERS_BY_ENTERPRISE_AND_DATACENTER)
            .setParameter("enterpriseId", enterpriseId).setParameter("datacenterId", datacenterId)
            .list();
    }

    private final static Long MB_TO_BYTES = 1024l * 1024l;

    @Override
    public ResourceAllocationLimitHB getCurrentResourcesAllocated(int virtualDatacenterId)
    {
        VirtualDataCenterHB vdc = findById(virtualDatacenterId);

        Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();

        // TODO deprecate this: server not longer check for CPU, RAM and HD resource allocation
        // limits
        //
        // Object[] vmResources =
        // (Object[]) getSession().createSQLQuery(SUM_VM_RESOURCES)
        // .setParameter("virtualDatacenterId", virtualDatacenterId)
        // .setParameter("not_deployed", VirtualMachineState.NOT_DEPLOYED.toString())
        // .uniqueResult();
        //
        // Long cpu = vmResources[0] == null ? 0 : ((BigDecimal) vmResources[0]).longValue();
        // Long ram = vmResources[1] == null ? 0 : ((BigDecimal) vmResources[1]).longValue();
        // Long hd = vmResources[2] == null ? 0 : ((BigDecimal) vmResources[2]).longValue();

        BigDecimal storage =
            (BigDecimal) session.createSQLQuery(SUM_VOLUMES_RESOURCES)
                .setParameter("virtualDatacenterId", virtualDatacenterId).uniqueResult();

        BigInteger publicIps =
            (BigInteger) session.createSQLQuery(COUNT_PUBLIC_IP_RESOURCES)
                .setParameter("virtualDatacenterId", virtualDatacenterId).uniqueResult();

        // privateVlan = vdc.getNetwork().getNetworks().size();

        ResourceAllocationLimitHB limits = new ResourceAllocationLimitHB();
        limits.setCpu(new LimitHB(0, 0));
        limits.setRam(new LimitHB(0, 0));
        limits.setCpu(new LimitHB(0, 0));
        limits.setStorage(new LimitHB(storage == null ? 0 : storage.longValue() * MB_TO_BYTES, 0));
        limits.setPublicIP(new LimitHB(publicIps == null ? 0 : publicIps.longValue(), 0));
        // limits.setVlan(new LimitHB(privateVlan, 0));

        return limits;
    }
}
