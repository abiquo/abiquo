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

package com.abiquo.api.common;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.testng.annotations.AfterMethod;

import com.abiquo.server.core.cloud.HypervisorGenerator;
import com.abiquo.server.core.cloud.NodeVirtualImageGenerator;
import com.abiquo.server.core.cloud.VirtualApplianceGenerator;
import com.abiquo.server.core.cloud.VirtualDatacenterGenerator;
import com.abiquo.server.core.cloud.VirtualImageGenerator;
import com.abiquo.server.core.cloud.VirtualMachineGenerator;
import com.abiquo.server.core.config.SystemPropertyGenerator;
import com.abiquo.server.core.enterprise.EnterpriseGenerator;
import com.abiquo.server.core.enterprise.RoleGenerator;
import com.abiquo.server.core.enterprise.UserGenerator;
import com.abiquo.server.core.infrastructure.DatacenterGenerator;
import com.abiquo.server.core.infrastructure.DatastoreGenerator;
import com.abiquo.server.core.infrastructure.MachineGenerator;
import com.abiquo.server.core.infrastructure.RackGenerator;
import com.abiquo.server.core.infrastructure.RemoteServiceGenerator;
import com.abiquo.server.core.infrastructure.management.RasdManagementGenerator;
import com.abiquo.server.core.infrastructure.network.IpPoolManagementGenerator;
import com.abiquo.server.core.infrastructure.network.VLANNetworkGenerator;
import com.softwarementors.commons.test.SeedGenerator;

@TestExecutionListeners( {DependencyInjectionTestExecutionListener.class,
TransactionalTestExecutionListener.class})
@ContextConfiguration(locations = {"classpath:springresources/applicationContext-test.xml"})
public class AbstractGeneratorTest extends AbstractTestNGSpringContextTests
{
    protected SeedGenerator seed = new SeedGenerator();

    protected VirtualDatacenterGenerator vdcGenerator = new VirtualDatacenterGenerator(seed);

    protected EnterpriseGenerator enterpriseGenerator = new EnterpriseGenerator(seed);

    protected VirtualApplianceGenerator vappGenerator = new VirtualApplianceGenerator(seed);

    protected DatacenterGenerator datacenterGenerator = new DatacenterGenerator(seed);

    protected RackGenerator rackGenerator = new RackGenerator(seed);

    protected MachineGenerator machineGenerator = new MachineGenerator(seed);

    protected HypervisorGenerator hypervisorGenerator = new HypervisorGenerator(seed);

    protected RemoteServiceGenerator remoteServiceGenerator = new RemoteServiceGenerator(seed);

    protected VirtualApplianceGenerator virtualApplianceGenerator =
        new VirtualApplianceGenerator(seed);

    protected RasdManagementGenerator rasdGenerator = new RasdManagementGenerator(seed);

    protected VirtualImageGenerator virtualImageGenerator = new VirtualImageGenerator(seed);

    protected NodeVirtualImageGenerator nodeVirtualImageGenerator =
        new NodeVirtualImageGenerator(seed);

    protected RoleGenerator roleGenerator = new RoleGenerator(seed);

    protected UserGenerator userGenerator = new UserGenerator(seed);

    protected VirtualMachineGenerator vmGenerator = new VirtualMachineGenerator(seed);

    protected DatastoreGenerator datastoreGenerator = new DatastoreGenerator(seed);

    protected IpPoolManagementGenerator ipGenerator = new IpPoolManagementGenerator(seed);

    protected VLANNetworkGenerator vlanGenerator = new VLANNetworkGenerator(seed);

    protected SystemPropertyGenerator systemPropertyGenerator = new SystemPropertyGenerator(seed);

    protected void setup(Object... entities)
    {
        EntityManager em = getEntityManager();
        closeActiveTransaction(em);
        em.getTransaction().begin();
        for (Object entity : entities)
        {
            em.persist(entity);
        }
        em.getTransaction().commit();
    }
    
    @AfterMethod
    public void tearDown()
    {
        String[] entities = { "ip_pool_management", "volume_management", "diskstateful_conversions", "initiator_mapping", "rasd_management", 
            "rasd", "nodevirtualimage", "nodenetwork", "nodestorage", "noderelationtype", "node", "virtualmachine", "virtualimage", 
            "virtualimage_conversions", "node_virtual_image_stateful_conversions", "virtual_appliance_conversions", "virtualapp", 
            "vappstateful_conversions", "virtualdatacenter", "vlan_network", "vlan_network_assignment", "network_configuration", "dhcp_service",
            "storage_pool", "tier", "storage_device", "remote_service", "datastore_assignment", "datastore", "hypervisor", 
            "workload_machine_load_rule", "physicalmachine", "rack", "datacenter", "repository", "workload_fit_policy_rule", "network",
            "session", "user", "role", "enterprise", "enterprise_limits_by_datacenter", "workload_enterprise_exclusion_rule", 
            "ovf_package_list_has_ovf_package", "ovf_package", "ovf_package_list", "apps_library", "license", 
            "system_properties", "vdc_enterprise_stats", "vapp_enterprise_stats", "dc_enterprise_stats", "enterprise_resources_stats", 
            "cloud_usage_stats", "log", "metering", "tasks", "alerts", "heartbeatlog", "icon", "register" };
        
        tearDown(entities);
    }

    protected void tearDown(String... entities)
    {
        EntityManager em = getEntityManager();
        closeActiveTransaction(em);
        em.getTransaction().begin();

        for (String entity : entities)
        {
            em.createNativeQuery("delete from " + entity).executeUpdate();
        }

        em.getTransaction().commit();
        em.close();
    }

    private EntityManagerFactory getEntityManagerFactory()
    {
        return applicationContext.getBean(EntityManagerFactory.class);
    }

    protected EntityManager getEntityManagerWithAnActiveTransaction()
    {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();

        return em;
    }

    protected EntityManager getEntityManager()
    {
        EntityManagerFactory emf = getEntityManagerFactory();
        EntityManager em;

        if (TransactionSynchronizationManager.hasResource(emf))
        {
            EntityManagerHolder emHolder = unbind(emf);
            em = emHolder.getEntityManager();
            if (!em.isOpen())
            {
                em = emf.createEntityManager();
            }
        }
        else
        {
            em = emf.createEntityManager();
            TransactionSynchronizationManager.bindResource(emf, new EntityManagerHolder(em));
        }
        return em;
    }

    private EntityManagerHolder unbind(EntityManagerFactory emf)
    {
        return (EntityManagerHolder) TransactionSynchronizationManager.unbindResource(emf);
    }

    private void closeActiveTransaction(EntityManager em)
    {
        if (em.getTransaction().isActive())
        {
            em.getTransaction().rollback();
        }
    }

}
