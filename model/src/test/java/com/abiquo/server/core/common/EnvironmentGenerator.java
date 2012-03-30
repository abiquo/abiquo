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
package com.abiquo.server.core.common;

import java.util.ArrayList;
import java.util.List;

import com.abiquo.model.enumerator.MachineState;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.model.enumerator.StorageTechnologyType;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplateGenerator;
import com.abiquo.server.core.cloud.Hypervisor;
import com.abiquo.server.core.cloud.HypervisorGenerator;
import com.abiquo.server.core.cloud.NodeVirtualImage;
import com.abiquo.server.core.cloud.NodeVirtualImageGenerator;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualApplianceGenerator;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualDatacenterGenerator;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.VirtualMachineGenerator;
import com.abiquo.server.core.cloud.VirtualMachineState;
import com.abiquo.server.core.enterprise.DatacenterLimits;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.EnterpriseGenerator;
import com.abiquo.server.core.enterprise.Privilege;
import com.abiquo.server.core.enterprise.Role;
import com.abiquo.server.core.enterprise.RoleGenerator;
import com.abiquo.server.core.enterprise.User;
import com.abiquo.server.core.enterprise.UserGenerator;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.DatacenterGenerator;
import com.abiquo.server.core.infrastructure.DatacenterLimitsGenerator;
import com.abiquo.server.core.infrastructure.Datastore;
import com.abiquo.server.core.infrastructure.DatastoreGenerator;
import com.abiquo.server.core.infrastructure.Machine;
import com.abiquo.server.core.infrastructure.RemoteService;
import com.abiquo.server.core.infrastructure.RemoteServiceGenerator;
import com.abiquo.server.core.infrastructure.network.IpPoolManagement;
import com.abiquo.server.core.infrastructure.network.IpPoolManagementGenerator;
import com.abiquo.server.core.infrastructure.network.VLANNetwork;
import com.abiquo.server.core.infrastructure.network.VLANNetworkGenerator;
import com.abiquo.server.core.infrastructure.storage.DiskManagement;
import com.abiquo.server.core.infrastructure.storage.DiskManagementGenerator;
import com.abiquo.server.core.infrastructure.storage.InitiatorMapping;
import com.abiquo.server.core.infrastructure.storage.InitiatorMappingGenerator;
import com.abiquo.server.core.infrastructure.storage.StorageDevice;
import com.abiquo.server.core.infrastructure.storage.StorageDeviceGenerator;
import com.abiquo.server.core.infrastructure.storage.StoragePool;
import com.abiquo.server.core.infrastructure.storage.StoragePoolGenerator;
import com.abiquo.server.core.infrastructure.storage.VolumeManagement;
import com.abiquo.server.core.infrastructure.storage.VolumeManagementGenerator;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.softwarementors.commons.test.SeedGenerator;

/**
 * Utility class to generate complex test environments.
 * 
 * @author Ignasi Barrera
 */
public class EnvironmentGenerator
{
    public static final String SYSADMIN = "sysadmin";

    private final DatacenterGenerator datacenterGenerator;

    private final DatacenterLimitsGenerator datacenterLimitsGenerator;

    private final DatastoreGenerator datastoreGenerator;

    private final StorageDeviceGenerator deviceGenerator;

    private final DiskManagementGenerator diskGenerator;

    private final EnterpriseGenerator enterpriseGenerator;

    /** The entities of the generated environment. */
    private final List<Object> entities;

    private final HypervisorGenerator hypervisorGenerator;

    private final InitiatorMappingGenerator initiatorMappingGenerator;

    private final IpPoolManagementGenerator ipGenerator;

    private final NodeVirtualImageGenerator nodeVirtualImageGenerator;

    private final VirtualMachineGenerator virtualMachineGenerator;

    private final VirtualMachineTemplateGenerator virtualMachineTemplateGenerator;

    private final StoragePoolGenerator poolGenerator;

    private final RemoteServiceGenerator remoteServiceGenerator;

    private final RoleGenerator roleGenerator;

    private final UserGenerator userGenerator;

    private final VirtualApplianceGenerator vappGenerator;

    private final VirtualDatacenterGenerator vdcGenerator;

    private final VLANNetworkGenerator vlanGenerator;

    private final VolumeManagementGenerator volumeGenerator;

    public EnvironmentGenerator(final SeedGenerator seed)
    {
        super();
        enterpriseGenerator = new EnterpriseGenerator(seed);
        roleGenerator = new RoleGenerator(seed);
        userGenerator = new UserGenerator(seed);
        datacenterGenerator = new DatacenterGenerator(seed);
        datacenterLimitsGenerator = new DatacenterLimitsGenerator(seed);
        remoteServiceGenerator = new RemoteServiceGenerator(seed);
        deviceGenerator = new StorageDeviceGenerator(seed);
        poolGenerator = new StoragePoolGenerator(seed);
        vdcGenerator = new VirtualDatacenterGenerator(seed);
        vappGenerator = new VirtualApplianceGenerator(seed);
        nodeVirtualImageGenerator = new NodeVirtualImageGenerator(seed);
        virtualMachineGenerator = new VirtualMachineGenerator(seed);
        virtualMachineTemplateGenerator = new VirtualMachineTemplateGenerator(seed);
        volumeGenerator = new VolumeManagementGenerator(seed);
        vlanGenerator = new VLANNetworkGenerator(seed);
        hypervisorGenerator = new HypervisorGenerator(seed);
        datastoreGenerator = new DatastoreGenerator(seed);
        initiatorMappingGenerator = new InitiatorMappingGenerator(seed);
        diskGenerator = new DiskManagementGenerator(seed);
        ipGenerator = new IpPoolManagementGenerator(seed);
        entities = new ArrayList<Object>();
    }

    /**
     * Add the given entity to the environment.
     * 
     * @param entity The entity to add.
     */
    public void add(final Object entity)
    {
        entities.add(entity);
    }

    /**
     * Generates and adds the following entities to the environment. The virtual machine is
     * <code>VirtualMachine.MANAGED</code>.
     * <ol>
     * <li>A virtual appliance in the generated virtual datacenter</li>
     * <li>An image repository in the given datacenter and enterprise</li>
     * <li>An image category</li>
     * <li>A virtual image in the generated repository and category</li>
     * <li>A NodeVirtualImage linking the generated image with the generated appliance</li>
     * <li>A virtual machine linked to the generated NodeVirtualImage, belonging to the user in the
     * environment, and allocated to the generated hypervisor and datastore</li>
     * </ol>
     * 
     * @return The environment entities.
     */
    public List<Object> generateAllocatedVirtualMachine()
    {
        // Generated the not allocated virtual machine first
        generateNotAllocatedVirtualMachine();

        // Entities that should be already added to the environment
        VirtualMachine vm = get(VirtualMachine.class);
        Hypervisor hypervisor = get(Hypervisor.class);
        Datastore datastore = get(Datastore.class);

        // Allocate the virtual machine
        vm.setHypervisor(hypervisor);
        vm.setDatastore(datastore);
        vm.setState(VirtualMachineState.OFF); // Allocated and powered off
        vm.setIdType(VirtualMachine.MANAGED);
        return getEnvironment();
    }

    /**
     * <b>This method <i>must</i> preceed {@link #generateEnterprise()} and
     * {@link #generateInfrastructure()} {@link #generateVirtualDatacenter()} </b><br>
     * Generates and adds the following entities to the environment.<br>
     * A managed virtual machine must be assigned to a hypervisor.
     * <ol>
     * <li>A virtual appliance in the generated virtual datacenter</li>
     * <li>An image repository in the given datacenter and enterprise</li>
     * <li>An image category</li>
     * <li>A virtual image in the generated repository and category</li>
     * <li>A NodeVirtualImage linking the generated image with the generated appliance</li>
     * <li>A virtual machine linked to the generated NodeVirtualImage, belonging to the user in the
     * environment, and allocated to the generated hypervisor and datastore</li>
     * </ol>
     * 
     * @return The environment entities.
     */
    public List<Object> generateNotManagedAllocatedVirtualMachine()
    {
        // Entities that should be already added to the environment
        Enterprise enterprise = get(Enterprise.class);
        Hypervisor hypervisor = get(Hypervisor.class);
        Datastore datastore = get(Datastore.class);
        User user = get(User.class);
        VirtualDatacenter vdc = get(VirtualDatacenter.class);
        VirtualAppliance vapp = vappGenerator.createInstance(vdc);
        NodeVirtualImage node = nodeVirtualImageGenerator.createInstance(vapp, user);
        VirtualMachine vm =
            virtualMachineGenerator.createInstance(node.getVirtualImage(), enterprise, hypervisor,
                datastore, user, "vmNotManaged");

        // Allocate the virtual machine
        vm.setHypervisor(hypervisor);
        vm.setDatastore(datastore);
        vm.setIdType(VirtualMachine.NOT_MANAGED);
        vm.setState(VirtualMachineState.OFF); // Allocated and powered off

        add(vapp);
        add(node.getVirtualImage().getRepository());
        add(node.getVirtualImage().getCategory());
        add(node.getVirtualImage());
        add(node);
        add(vm);

        return getEnvironment();
    }

    /**
     * Generates and adds the following entitities to the environment.
     * <ol>
     * <li>A disk in the generated virtual datacenter and storage pool</li>
     * <li>Its Rasd</li>
     * </ol>
     * 
     * @return
     */
    public List<Object> generateDisk()
    {
        VirtualDatacenter vdc = get(VirtualDatacenter.class);

        DiskManagement disk = diskGenerator.createInstance(vdc);

        add(disk.getRasd());
        add(disk);

        return getEnvironment();
    }

    /**
     * Generates and adds the following entities to the environment:
     * <ol>
     * <li>An enterprise</li>
     * <li>Cloud admin related Privileges</li>
     * <li>Cloud admin role</li>
     * <li>Cloud admin user in the generated enterprise</li>
     * </ol>
     * 
     * @return The environment entities.
     */
    public List<Object> generateEnterprise()
    {
        Enterprise ent = enterpriseGenerator.createInstanceNoLimits();
        Role role = roleGenerator.createInstanceSysAdmin();
        User user = userGenerator.createInstance(ent, role, SYSADMIN, SYSADMIN);

        add(ent);
        for (Privilege p : role.getPrivileges())
        {
            add(p);
        }
        add(role);
        add(user);

        return getEnvironment();
    }

    /**
     * Generates and adds the following entities to the environment:
     * <ol>
     * <li>An enterprise</li>
     * <li>The given user related Privileges</li>
     * <li>The given user role</li>
     * <li>The given user user in the generated enterprise</li>
     * </ol>
     * 
     * @param user The given user to create
     * @return The environment entities.
     */
    public List<Object> generateEnterprise(final User user)
    {
        Enterprise ent = enterpriseGenerator.createInstanceNoLimits();
        user.setEnterprise(ent);

        add(ent);
        for (Privilege p : user.getRole().getPrivileges())
        {
            add(p);
        }
        add(user.getRole());
        add(user);

        return getEnvironment();
    }

    /**
     * <b>This method <i>must</i> be preceeded by {@link #generateEnterprise()}</b><br>
     * Generates and adds the following entities to the environment:
     * <ol>
     * <li>A datacenter (the given enterprise is allowed to use it)</li>
     * <li>The AM remote service</li>
     * <li>The SSM remote service</li>
     * <li>A rack in the generated datacenter</li>
     * <li>A physical machine in the generated rack</li>
     * <li>A datastore in the physical machine</li>
     * <li>A hypervisor for the physical machine</li>
     * <li>A storage device in the generated datacenter</li>
     * <li>A storage tier in the generated datacenter</li>
     * <li>A storage pool in the generated tier and device</li>
     * </ol>
     * 
     * @return The environment entities.
     */
    public List<Object> generateInfrastructure()
    {
        // Entities that should be already added to the environment
        Enterprise enterprise = get(Enterprise.class);

        // Datacenter
        Datacenter dc = datacenterGenerator.createUniqueInstance();
        DatacenterLimits dcLimits = datacenterLimitsGenerator.createInstance(enterprise, dc);
        RemoteService am =
            remoteServiceGenerator.createInstance(RemoteServiceType.APPLIANCE_MANAGER, dc);
        RemoteService ssm =
            remoteServiceGenerator.createInstance(RemoteServiceType.STORAGE_SYSTEM_MONITOR, dc);
        RemoteService dhcp =
            remoteServiceGenerator.createInstance(RemoteServiceType.DHCP_SERVICE, dc);
        RemoteService vsm =
            remoteServiceGenerator.createInstance(RemoteServiceType.VIRTUAL_SYSTEM_MONITOR, dc);

        // Compute
        Hypervisor hypervisor = hypervisorGenerator.createInstance(dc);
        Datastore datastore = datastoreGenerator.createInstance(hypervisor.getMachine());

        // Storage
        StorageDevice device = deviceGenerator.createInstance(dc);
        device.setStorageTechnology(StorageTechnologyType.NEXENTA);
        StoragePool pool = poolGenerator.createInstance(device);

        Machine machine = hypervisor.getMachine();
        machine.setState(MachineState.MANAGED);
        machine.setVirtualCpuCores(Integer.MAX_VALUE);
        machine.setVirtualRamInMb(Integer.MAX_VALUE);
        machine.setVirtualCpusUsed(0);
        machine.setVirtualRamUsedInMb(0);

        add(dc);
        add(dcLimits);
        add(am);
        add(ssm);
        add(dhcp);
        add(vsm);

        add(hypervisor.getMachine().getRack());
        add(hypervisor.getMachine());
        add(datastore);
        add(hypervisor);

        add(device);
        add(pool.getTier());
        add(pool);

        return getEnvironment();
    }

    /**
     * Generates and adds the following entities to the environment.
     * <ol>
     * <li>A virtual appliance in the generated virtual datacenter</li>
     * <li>An image repository in the given datacenter and enterprise</li>
     * <li>An image category</li>
     * <li>A virtual image in the generated repository and category</li>
     * <li>A NodeVirtualImage linking the generated image with the generated appliance</li>
     * <li>A virtual machine linked to the generated NodeVirtualImage, belonging to the user in the
     * environment</li>
     * </ol>
     * 
     * @return The environment entities.
     */
    public List<Object> generateNotAllocatedVirtualMachine()
    {
        // Entities that should be already added to the environment
        VirtualDatacenter vdc = get(VirtualDatacenter.class);
        User user = get(User.class);

        VirtualAppliance vapp = vappGenerator.createInstance(vdc);
        NodeVirtualImage node = nodeVirtualImageGenerator.createInstance(vapp, user);
        VirtualMachine vm = node.getVirtualMachine();
        vm.setState(VirtualMachineState.NOT_ALLOCATED);

        add(vapp);
        add(node.getVirtualImage().getRepository());
        add(node.getVirtualImage().getCategory());
        add(node.getVirtualImage());
        add(node);
        add(vm);

        return getEnvironment();
    }

    /**
     * Generates and adds the following entities to the environment.
     * <ol>
     * <li>A ip in the generated virtual datacenter and its default vlan.</li>
     * <li>Its Rasd</li>
     * </ol>
     * 
     * @return
     */
    public List<Object> generatePrivateIp()
    {
        VirtualDatacenter vdc = get(VirtualDatacenter.class);
        VLANNetwork vlan = vdc.getDefaultVlan();
        IpPoolManagement ip = ipGenerator.createInstance(vdc, vlan, "10.60.1.24");

        add(ip.getRasd());
        add(ip);

        return getEnvironment();
    }

    /**
     * Generates and adds the following entities to the environment.
     * <ol>
     * <li>A network for the virtual datacenter</li>
     * <li>A DHCP remote service</li>
     * <li>A DHCP entity</li>
     * <li>A network configuration for the dhcp service</li>
     * <li>A private VLAN with the generated network configuration</li>
     * <li>A virtual datacenter in the generated datacenter and enterprise</li>
     * </ol>
     * 
     * @return The environment entities.
     */
    public List<Object> generateVirtualDatacenter()
    {
        // Entities that should be already added to the environment
        Datacenter datacenter = get(Datacenter.class);
        Enterprise enterprise = get(Enterprise.class);
        Hypervisor hypervisor = get(Hypervisor.class);

        VirtualDatacenter vdc =
            vdcGenerator.createInstance(datacenter, enterprise, hypervisor.getType());
        VLANNetwork vlan = vlanGenerator.createInstance(vdc.getNetwork());
        vlan.setTag(3);
        // vlan.getConfiguration().getDhcp().getRemoteService().setDatacenter(datacenter);
        vdc.setDefaultVlan(vlan);

        add(vdc.getNetwork());
        // add(vlan.getConfiguration().getDhcp().getRemoteService());
        // add(vlan.getConfiguration().getDhcp());
        add(vlan.getConfiguration());
        add(vlan);
        add(vdc);

        return getEnvironment();
    }

    /**
     * Generates and adds the following entities to the environment.
     * <ol>
     * <li>A volume in the generated virtual datacenter and storage pool</li>
     * <li>An initiator mapping for the generated volume</li>
     * </ol>
     * 
     * @return The environment entities.
     */
    public List<Object> generateVolume()
    {
        // Entities that should be already added to the environment
        StoragePool pool = get(StoragePool.class);
        VirtualDatacenter vdc = get(VirtualDatacenter.class);

        VolumeManagement volume = volumeGenerator.createInstance(pool, vdc);
        InitiatorMapping mapping = initiatorMappingGenerator.createInstance(volume);

        add(volume.getRasd());
        add(volume);
        add(mapping);

        return getEnvironment();
    }

    /**
     * Get the entity of the given class in the current environment.
     * 
     * @param clazz The class of the entity to get.
     * @return The entity of the given class in the current environment.
     */
    @SuppressWarnings("unchecked")
    public <T> T get(final Class<T> clazz)
    {
        return (T) Iterables.find(entities, Predicates.instanceOf(clazz));
    }

    /**
     * Get the entities of the given class in the current environment.
     * 
     * @param clazz The class of the entity to get.
     * @return The entities of the given class in the current environment.
     */
    public <T> List<T> getAll(final Class<T> clazz)
    {
        return Lists.newLinkedList(Iterables.filter(entities, clazz));
    }

    /**
     * Get the entities of the current environment.
     * 
     * @return The entities of the current environment.
     */
    public List<Object> getEnvironment()
    {
        return entities;
    }
}
