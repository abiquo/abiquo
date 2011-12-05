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
import org.testng.annotations.BeforeMethod;

import com.abiquo.server.core.appslibrary.AppsLibraryGenerator;
import com.abiquo.server.core.appslibrary.CategoryGenerator;
import com.abiquo.server.core.appslibrary.IconGenerator;
import com.abiquo.server.core.appslibrary.TemplateDefinitionGenerator;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplateGenerator;
import com.abiquo.server.core.cloud.HypervisorGenerator;
import com.abiquo.server.core.cloud.NodeVirtualImageGenerator;
import com.abiquo.server.core.cloud.VirtualApplianceGenerator;
import com.abiquo.server.core.cloud.VirtualDatacenterGenerator;
import com.abiquo.server.core.cloud.VirtualImageConversionGenerator;
import com.abiquo.server.core.cloud.VirtualMachineGenerator;
import com.abiquo.server.core.cloud.stateful.NodeVirtualImageStatefulConversionGenerator;
import com.abiquo.server.core.cloud.stateful.VirtualApplianceStatefulConversionGenerator;
import com.abiquo.server.core.config.SystemPropertyGenerator;
import com.abiquo.server.core.enterprise.EnterpriseGenerator;
import com.abiquo.server.core.enterprise.PrivilegeGenerator;
import com.abiquo.server.core.enterprise.RoleGenerator;
import com.abiquo.server.core.enterprise.RoleLdapGenerator;
import com.abiquo.server.core.enterprise.UserGenerator;
import com.abiquo.server.core.infrastructure.DatacenterGenerator;
import com.abiquo.server.core.infrastructure.DatacenterLimitsGenerator;
import com.abiquo.server.core.infrastructure.DatastoreGenerator;
import com.abiquo.server.core.infrastructure.MachineGenerator;
import com.abiquo.server.core.infrastructure.RackGenerator;
import com.abiquo.server.core.infrastructure.RemoteServiceGenerator;
import com.abiquo.server.core.infrastructure.RepositoryGenerator;
import com.abiquo.server.core.infrastructure.UcsRackGenerator;
import com.abiquo.server.core.infrastructure.management.RasdGenerator;
import com.abiquo.server.core.infrastructure.management.RasdManagementGenerator;
import com.abiquo.server.core.infrastructure.network.IpPoolManagementGenerator;
import com.abiquo.server.core.infrastructure.network.VLANNetworkGenerator;
import com.abiquo.server.core.infrastructure.storage.InitiatorMappingGenerator;
import com.abiquo.server.core.infrastructure.storage.VolumeManagementGenerator;
import com.abiquo.server.core.pricing.PricingTemplateGenerator;
import com.softwarementors.commons.test.SeedGenerator;

@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
TransactionalTestExecutionListener.class})
@ContextConfiguration(locations = {"classpath:springresources/applicationContext-test.xml"})
public abstract class AbstractGeneratorTest extends AbstractTestNGSpringContextTests
{
    protected SeedGenerator seed = new SeedGenerator();

    protected VirtualDatacenterGenerator vdcGenerator = new VirtualDatacenterGenerator(seed);

    protected EnterpriseGenerator enterpriseGenerator = new EnterpriseGenerator(seed);

    protected VirtualApplianceGenerator vappGenerator = new VirtualApplianceGenerator(seed);

    protected DatacenterGenerator datacenterGenerator = new DatacenterGenerator(seed);

    protected DatacenterLimitsGenerator datacenterLimitsGenerator =
        new DatacenterLimitsGenerator(seed);

    protected RackGenerator rackGenerator = new RackGenerator(seed);

    protected UcsRackGenerator ucsRackGenerator = new UcsRackGenerator(seed);

    protected MachineGenerator machineGenerator = new MachineGenerator(seed);

    protected HypervisorGenerator hypervisorGenerator = new HypervisorGenerator(seed);

    protected RemoteServiceGenerator remoteServiceGenerator = new RemoteServiceGenerator(seed);

    protected VirtualApplianceGenerator virtualApplianceGenerator =
        new VirtualApplianceGenerator(seed);

    protected RasdManagementGenerator rasdManagementGenerator = new RasdManagementGenerator(seed);

    protected RasdGenerator rasdGenerator = new RasdGenerator(seed);

    protected VolumeManagementGenerator volumeManagementGenerator =
        new VolumeManagementGenerator(seed);

    protected VirtualMachineTemplateGenerator virtualMachineTemplateGenerator =
        new VirtualMachineTemplateGenerator(seed);

    protected VirtualImageConversionGenerator conversionGenerator =
        new VirtualImageConversionGenerator(seed);

    protected NodeVirtualImageGenerator nodeVirtualImageGenerator =
        new NodeVirtualImageGenerator(seed);

    protected RoleGenerator roleGenerator = new RoleGenerator(seed);

    protected UserGenerator userGenerator = new UserGenerator(seed);

    protected VirtualMachineGenerator vmGenerator = new VirtualMachineGenerator(seed);

    protected DatastoreGenerator datastoreGenerator = new DatastoreGenerator(seed);

    protected IpPoolManagementGenerator ipGenerator = new IpPoolManagementGenerator(seed);

    protected VLANNetworkGenerator vlanGenerator = new VLANNetworkGenerator(seed);

    protected SystemPropertyGenerator systemPropertyGenerator = new SystemPropertyGenerator(seed);

    protected PrivilegeGenerator privilegeGenerator = new PrivilegeGenerator(seed);

    protected RoleLdapGenerator roleLdapGenerator = new RoleLdapGenerator(seed);

    protected CategoryGenerator categoryGenerator = new CategoryGenerator(seed);

    protected TemplateDefinitionGenerator templateDefGenerator =
        new TemplateDefinitionGenerator(seed);

    protected AppsLibraryGenerator appsLibraryGenerator = new AppsLibraryGenerator(seed);

    protected IconGenerator iconGenerator = new IconGenerator(seed);

    protected RepositoryGenerator repositoryGenerator = new RepositoryGenerator(seed);

    protected InitiatorMappingGenerator initiatorMappingGenerator =
        new InitiatorMappingGenerator(seed);

    protected VirtualImageConversionGenerator virtualImageConversionGenerator =
        new VirtualImageConversionGenerator(seed);

    protected VirtualApplianceStatefulConversionGenerator virtualApplianceStatefulConversionGenerator =
        new VirtualApplianceStatefulConversionGenerator(seed);

    protected NodeVirtualImageStatefulConversionGenerator nodeVirtualImageStatefulConversionGenerator =
        new NodeVirtualImageStatefulConversionGenerator(seed);

    protected PricingTemplateGenerator pricingTemplateGenerator =
        new PricingTemplateGenerator(seed);

    protected void setup(final Object... entities)
    {
        EntityManager em = getEntityManager();
        rollbackActiveTransaction(em);
        em.getTransaction().begin();
        for (Object entity : entities)
        {
            em.persist(entity);
        }
        em.getTransaction().commit();
        em.close();
    }

    protected void update(final Object... entities)
    {
        EntityManager em = getEntityManager();
        rollbackActiveTransaction(em);
        em.getTransaction().begin();
        for (Object entity : entities)
        {
            em.merge(entity);
        }
        em.getTransaction().commit();
        em.close();
    }

    @BeforeMethod
    public void setup()
    {
        // Set system properties for tests
        // WARINING!!! This value should be the same than the used in the POM.xml to define the
        // system property in the jetty runtime!!!
        System.setProperty("abiquo.server.networking.vlanPerVdc", "4");
    }

    @AfterMethod
    public void tearDown()
    {
        String[] entities =
            {"ip_pool_management", "volume_management", "diskstateful_conversions",
            "initiator_mapping", "rasd_management", "rasd", "nodevirtualimage", "nodenetwork",
            "nodestorage", "noderelationtype", "node", "virtualmachine", "virtualimage",
            "virtualimage_conversions", "node_virtual_image_stateful_conversions",
            "virtual_appliance_conversions", "virtualapp", "vappstateful_conversions",
            "virtualdatacenter", "vlan_network", "vlan_network_assignment",
            "network_configuration", "chef_runlist", "storage_pool", "tier", "storage_device",
            "remote_service", "datastore_assignment", "datastore", "hypervisor",
            "workload_machine_load_rule", "physicalmachine", "rack", "ucs_rack", "datacenter",
            "repository", "workload_fit_policy_rule", "network", "session", "user",
            "roles_privileges", "role_ldap", "role", "privilege", "enterprise",
            "enterprise_limits_by_datacenter", "workload_enterprise_exclusion_rule",
            "ovf_package_list_has_ovf_package", "ovf_package", "ovf_package_list", "category",
            "apps_library", "license", "system_properties", "vdc_enterprise_stats",
            "vapp_enterprise_stats", "dc_enterprise_stats", "enterprise_resources_stats",
            "cloud_usage_stats", "log", "metering", "tasks", "alerts", "heartbeatlog", "icon",
            "repository", "register", "costCodeCurrency", "pricingCostCode", "pricingTier",
            "pricingTemplate", "currency", "costCode"};

        tearDown(entities);
    }

    protected void tearDown(final String... entities)
    {
        EntityManager em = getEntityManager();
        rollbackActiveTransaction(em);
        em.getTransaction().begin();

        for (String entity : entities)
        {
            em.createNativeQuery("delete from " + entity).executeUpdate();
        }

        em.getTransaction().commit();
        em.close();
    }

    public void tearDownButNoCloseEntityManager()
    {
        String[] entities =
            {"ip_pool_management", "volume_management", "diskstateful_conversions",
            "initiator_mapping", "rasd_management", "rasd", "nodevirtualimage", "nodenetwork",
            "nodestorage", "noderelationtype", "node", "virtualmachine", "virtualimage",
            "virtualimage_conversions", "node_virtual_image_stateful_conversions",
            "virtual_appliance_conversions", "virtualapp", "vappstateful_conversions",
            "virtualdatacenter", "vlan_network", "vlan_network_assignment",
            "network_configuration", "chef_runlist", "storage_pool", "tier", "storage_device",
            "remote_service", "datastore_assignment", "datastore", "hypervisor",
            "workload_machine_load_rule", "physicalmachine", "rack", "ucs_rack", "datacenter",
            "repository", "workload_fit_policy_rule", "network", "session", "user",
            "roles_privileges", "role_ldap", "role", "privilege", "enterprise",
            "enterprise_limits_by_datacenter", "workload_enterprise_exclusion_rule",
            "ovf_package_list_has_ovf_package", "ovf_package", "ovf_package_list", "category",
            "apps_library", "license", "system_properties", "vdc_enterprise_stats",
            "vapp_enterprise_stats", "dc_enterprise_stats", "enterprise_resources_stats",
            "cloud_usage_stats", "log", "metering", "tasks", "alerts", "heartbeatlog", "icon",
            "repository", "register", "costCodeCurrency", "pricingCostCode", "pricingTier",
            "pricingTemplate", "currency", "costCode"};

        tearDownButNoCloseEntityManager(entities);
    }

    protected void tearDownButNoCloseEntityManager(final String... entities)
    {
        EntityManager em = getEntityManager();
        rollbackActiveTransaction(em);
        em.getTransaction().begin();

        for (String entity : entities)
        {
            em.createNativeQuery("delete from " + entity).executeUpdate();
        }

        em.getTransaction().commit();
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
        EntityManager em = null;

        if (TransactionSynchronizationManager.hasResource(emf))
        {
            EntityManagerHolder emHolder = getResource(emf);
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

    protected void commitActiveTransaction(final EntityManager em)
    {
        if (em.getTransaction().isActive())
        {
            em.getTransaction().commit();
        }
    }

    protected void rollbackActiveTransaction(final EntityManager em)
    {
        if (em.getTransaction().isActive())
        {
            em.getTransaction().rollback();
        }
    }

    private EntityManagerHolder getResource(final EntityManagerFactory emf)
    {
        return (EntityManagerHolder) TransactionSynchronizationManager.getResource(emf);
    }

}
