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

package com.abiquo.abiserver.persistence.dao.user.hibernate;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

import com.abiquo.abiserver.business.hibernate.pojohb.user.EnterpriseHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualhardware.DatacenterLimitHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualhardware.LimitHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualhardware.ResourceAllocationLimitHB;
import com.abiquo.abiserver.exception.PersistenceException;
import com.abiquo.abiserver.persistence.dao.user.EnterpriseDAO;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAO;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAOFactory;

/**
 * Class that implements the extra DAO functions for the
 * {@link com.abiquo.abiserver.persistence.dao.user.EnterpriseDAO} interface
 * 
 * @author jdevesa@abiquo.com
 */
public class EnterpriseDAOHibernate extends HibernateDAO<EnterpriseHB, Integer> implements
    EnterpriseDAO
{
    private final static String ALL_IDS = "GET_ALL_ENTERPRISE_IDS";

    private final static String GET_BY_VIRTAL_APP = "GET_BY_VIRTAL_APP";

    private final static String GET_DATACENTER_LIMITS = "GET_DATACENTER_LIMITS";

    private final static String GET_ENTERPRISE_FROM_VLAN_ID =
        "ENTERPRISE.GET_ENTERPRISE_RESERVED_BY_VLAN_ID";

    @Override
    public EnterpriseHB findByVirtualAppliance(final Integer idVirtualApp)
    {
        final Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();

        final Query query = session.getNamedQuery(GET_BY_VIRTAL_APP);
        query.setInteger("idVirtualApp", idVirtualApp);

        return (EnterpriseHB) query.uniqueResult();
    }

    // private static final String SUM_VM_RESOURCES =
    // "select sum(vm.cpu), sum(vm.ram), sum(vm.hd) from virtualmachine vm, hypervisor hy, physicalmachine pm "
    // + " where hy.id = vm.idHypervisor and pm.idPhysicalMachine = hy.idPhysicalMachine "
    // + " and vm.idEnterprise = :enterpriseId and STRCMP(vm.state, :not_deployed) != 0";

    @Deprecated
    // TODO deprecate this: server not longer check for CPU, RAM and HD resource allocation
    // limits
    @Override
    public ResourceAllocationLimitHB getTotalResourceUtilization(int idEnterprise)
        throws PersistenceException
    {

        // final Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();
        //
        // Object[] vmResources =
        // (Object[]) session.createSQLQuery(SUM_VM_RESOURCES)
        // .setParameter("enterpriseId", idEnterprise)
        // .setParameter("not_deployed", VirtualMachineState.NOT_DEPLOYED.toString())
        // .uniqueResult();
        //
        // Long cpu = vmResources[0] == null ? 0 : ((BigDecimal) vmResources[0]).longValue();
        // Long ram = vmResources[1] == null ? 0 : ((BigDecimal) vmResources[1]).longValue();
        // Long hd = vmResources[2] == null ? 0 : ((BigDecimal) vmResources[2]).longValue();

        ResourceAllocationLimitHB limits = new ResourceAllocationLimitHB();
        limits.setCpu(new LimitHB(0, 0));
        limits.setRam(new LimitHB(0, 0));
        limits.setHd(new LimitHB(0, 0));

        return limits;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Integer> findAllIds()
    {
        return HibernateDAOFactory.getSessionFactory().getCurrentSession().getNamedQuery(ALL_IDS)
            .list();
    }

    @Override
    public DatacenterLimitHB getDatacenterLimit(final int idEnterprise, final int idDatacenter)
    {
        final Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();
        final Query query = session.getNamedQuery(GET_DATACENTER_LIMITS);
        query.setInteger("idEnterprise", idEnterprise);
        query.setInteger("idDatacenter", idDatacenter);

        return (DatacenterLimitHB) query.uniqueResult();
    }

    @Override
    public EnterpriseHB getEnterpriseFromReservedVlanID(Integer vlanId) throws PersistenceException
    {
        try
        {
            final Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();
            final Query query = session.getNamedQuery(GET_ENTERPRISE_FROM_VLAN_ID);
            query.setInteger("vlan_id", vlanId);

            return (query.uniqueResult() == null) ? null : (EnterpriseHB) query.uniqueResult();

        }
        catch (HibernateException he)
        {
            throw new PersistenceException(he.getMessage());
        }
    }

}
