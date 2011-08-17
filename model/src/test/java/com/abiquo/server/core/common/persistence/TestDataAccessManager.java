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

package com.abiquo.server.core.common.persistence;

import java.util.List;

import com.abiquo.server.core.appslibrary.AppsLibrary;
import com.abiquo.server.core.appslibrary.Category;
import com.abiquo.server.core.cloud.Hypervisor;
import com.abiquo.server.core.cloud.NodeVirtualImage;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualImage;
import com.abiquo.server.core.cloud.VirtualImageConversion;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.stateful.DiskStatefulConversion;
import com.abiquo.server.core.cloud.stateful.NodeVirtualImageStatefulConversion;
import com.abiquo.server.core.cloud.stateful.VirtualApplianceStatefulConversion;
import com.abiquo.server.core.config.License;
import com.abiquo.server.core.config.SystemProperty;
import com.abiquo.server.core.enterprise.DatacenterLimits;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.Privilege;
import com.abiquo.server.core.enterprise.Role;
import com.abiquo.server.core.enterprise.RoleLdap;
import com.abiquo.server.core.enterprise.Session;
import com.abiquo.server.core.enterprise.User;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.Datastore;
import com.abiquo.server.core.infrastructure.Machine;
import com.abiquo.server.core.infrastructure.Rack;
import com.abiquo.server.core.infrastructure.RemoteService;
import com.abiquo.server.core.infrastructure.Repository;
import com.abiquo.server.core.infrastructure.management.Rasd;
import com.abiquo.server.core.infrastructure.management.RasdManagement;
import com.abiquo.server.core.infrastructure.network.Dhcp;
import com.abiquo.server.core.infrastructure.network.IpPoolManagement;
import com.abiquo.server.core.infrastructure.network.Network;
import com.abiquo.server.core.infrastructure.network.NetworkAssignment;
import com.abiquo.server.core.infrastructure.network.NetworkConfiguration;
import com.abiquo.server.core.infrastructure.network.VLANNetwork;
import com.abiquo.server.core.infrastructure.storage.StorageDevice;
import com.abiquo.server.core.infrastructure.storage.StoragePool;
import com.abiquo.server.core.infrastructure.storage.Tier;
import com.abiquo.server.core.infrastructure.storage.VolumeManagement;
import com.abiquo.server.core.scheduler.EnterpriseExclusionRule;
import com.abiquo.server.core.scheduler.FitPolicyRule;
import com.abiquo.server.core.scheduler.MachineLoadRule;
import com.abiquo.server.core.statistics.CloudUsage;
import com.abiquo.server.core.statistics.DatacenterResources;
import com.abiquo.server.core.statistics.EnterpriseResources;
import com.abiquo.server.core.statistics.VirtualAppResources;
import com.abiquo.server.core.statistics.VirtualDatacenterResources;
import com.abiquo.server.core.tasks.Task;
import com.softwarementors.bzngine.engines.jpa.test.configuration.EntityManagerFactoryForTesting;
import com.softwarementors.bzngine.engines.jpa.test.configuration.JpaEntityManagerFactoryForTesting;
import com.softwarementors.bzngine.engines.jpa.test.configuration.PersistentClassRemovalInformation;
import com.softwarementors.bzngine.entities.PersistentEntity;
import com.softwarementors.commons.collections.ListUtils;

public class TestDataAccessManager
{
    private static JpaEntityManagerFactoryForTesting factory;

    private static List<String> associationTablesInAssociationDeletionOrder =
        ListUtils.createList();

    private static List<PersistentClassRemovalInformation<PersistentEntity< ? >, ? >> persistentClassesInEntityDeletionOrder =
        ListUtils.createList();

    static
    {
        initializePersistentInstanceRemovalSupport();
    }

    @SuppressWarnings("unchecked")
    private static <T extends PersistentEntity< ? >> void addPersistentClassesToCleanInRemovalOrder(
        final Class< ? >... classes)
    {
        for (Class< ? > cls : classes)
        {
            persistentClassesInEntityDeletionOrder.add(new PersistentClassRemovalInformation(cls));
        }
    }

    private static void addIntermediateTablesToCleanInRemovalOrder(final String... tables)
    {
        for (String table : tables)
        {
            associationTablesInAssociationDeletionOrder.add(table);
        }
    }

    public static EntityManagerFactoryForTesting getFactory()
    {
        if (factory == null)
        {
            factory =
                new JpaEntityManagerFactoryForTesting("abiquoPersistence",
                    persistentClassesInEntityDeletionOrder,
                    associationTablesInAssociationDeletionOrder);
        }

        return factory;
    }

    /*
     * public static void closeFactory() { if( factory != null ) { factory.close(); factory = null;
     * } }
     */

    private static void initializePersistentInstanceRemovalSupport()
    {
        /**
         * Please Notice that arguments ORDER in this method is important to avoid persistence
         * problems.
         */
        addPersistentClassesToCleanInRemovalOrder(NetworkAssignment.class, NodeVirtualImage.class,
            EnterpriseExclusionRule.class, FitPolicyRule.class, MachineLoadRule.class,
            VirtualAppResources.class, VirtualAppliance.class, VirtualMachine.class,
            AppsLibrary.class, VolumeManagement.class, VirtualImageConversion.class,
            VirtualImage.class, Category.class, IpPoolManagement.class, RasdManagement.class,
            VLANNetwork.class, NetworkConfiguration.class, Dhcp.class,
            VirtualDatacenterResources.class, VirtualDatacenter.class, DatacenterResources.class,
            DatacenterLimits.class, Session.class, User.class, RoleLdap.class, Role.class,
            Privilege.class, EnterpriseResources.class, Enterprise.class, Hypervisor.class,
            Datastore.class, Machine.class, Rack.class, StoragePool.class, Tier.class,
            StorageDevice.class, RemoteService.class, Repository.class, CloudUsage.class,
            Datacenter.class, Network.class, SystemProperty.class, Rasd.class, License.class,
            Task.class, NodeVirtualImageStatefulConversion.class, DiskStatefulConversion.class,
            VirtualApplianceStatefulConversion.class);
        /*
         * ,OVFPackageList.class, OVFPackage.class, AppsLibrary.class, Icon.class, Category.class
         */
        // XXX after virtualmachine -- OVFPackageList.class, OVFPackage.class,
        // AppsLibrary.class,
        // Icon.class,
        // XXX last -- Category.class

        addIntermediateTablesToCleanInRemovalOrder(Machine.DATASTORES_ASSOCIATION_TABLE,
            DatacenterLimits.TABLE_NAME, EnterpriseResources.TABLE_NAME, Role.ASSOCIATION_TABLE
        /* OVFPackageList.ASSOCIATION_TABLE */);
    }
}
