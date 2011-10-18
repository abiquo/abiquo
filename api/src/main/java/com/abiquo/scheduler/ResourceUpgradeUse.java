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

package com.abiquo.scheduler;

import java.io.StringReader;
import java.rmi.NoSuchObjectException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.apache.wink.common.internal.providers.entity.csv.CsvReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.abiquo.scheduler.workload.NotEnoughResourcesException;
import com.abiquo.server.core.cloud.HypervisorDAO;
import com.abiquo.server.core.cloud.State;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualApplianceDAO;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.VirtualMachineDAO;
import com.abiquo.server.core.infrastructure.Datastore;
import com.abiquo.server.core.infrastructure.DatastoreDAO;
import com.abiquo.server.core.infrastructure.InfrastructureRep;
import com.abiquo.server.core.infrastructure.Machine;
import com.abiquo.server.core.infrastructure.Rack;
import com.abiquo.server.core.infrastructure.management.Rasd;
import com.abiquo.server.core.infrastructure.management.RasdDAO;
import com.abiquo.server.core.infrastructure.network.IpPoolManagement;
import com.abiquo.server.core.infrastructure.network.IpPoolManagementDAO;
import com.abiquo.server.core.infrastructure.network.NetworkAssignment;
import com.abiquo.server.core.infrastructure.network.NetworkAssignmentDAO;
import com.abiquo.server.core.infrastructure.network.VLANNetwork;
import com.abiquo.server.core.infrastructure.network.VLANNetworkDAO;

/**
 * Updates the following resource.
 * <ul>
 * <li>hardware resource (ram, cpu) on the physical machine</li>
 * <li>datastore utilization (hd) on the physical machine.</li>
 * <li>network resources on the virtual datacenter</li>
 * </ul>
 */
@Component
public class ResourceUpgradeUse implements IResourceUpgradeUse
{

    @Autowired
    InfrastructureRep datacenterRepo;

    @Autowired
    DatastoreDAO datastoreDao;

    @Autowired
    VirtualApplianceDAO virtualApplianceDao;

    @Autowired
    NetworkAssignmentDAO netAssignDao;

    @Autowired
    IpPoolManagementDAO ipPoolManDao;

    @Autowired
    VLANNetworkDAO vlanNetworkDao;

    @Autowired
    RasdDAO rasdDao;

    @Autowired
    VirtualMachineDAO vmachineDao;

    @Autowired
    HypervisorDAO hypervisorDao;

    private final static Logger log = LoggerFactory.getLogger(ResourceUpgradeUse.class);

    /**
     * @throws ResourceUpgradeUseException, if the operation can be performed: there isn't enough
     *             resources to allocate the virtual machine, the virtual appliances is not on any
     *             virtual datacenter.
     */
    @Override
    public void updateUse(final Integer virtualApplianceId, final VirtualMachine virtualMachine)
        throws ResourceUpgradeUseException
    {
        updateUse(virtualApplianceId, virtualMachine, false);
    }

    @Override
    public void updateUseHa(final Integer virtualApplianceId, final VirtualMachine virtualMachine,
        final Integer sourceHypervisorId)
    {
        updateUse(virtualApplianceId, virtualMachine, true); // upgrade resources on the target HA
                                                             // hypervisor
        // free resources on the original hypervisor
        Machine sourceMachine = hypervisorDao.findById(sourceHypervisorId).getMachine();
        updateUsagePhysicalMachine(sourceMachine, virtualMachine, true);
    }

    private void updateUse(final Integer virtualApplianceId, final VirtualMachine virtualMachine,
        final boolean isHA)
    {
        if (virtualMachine.getHypervisor() == null
            || virtualMachine.getHypervisor().getMachine() == null)
        {
            throw new ResourceUpgradeUseException("Virtual machine is not allocated on any hypervisor / machine");
        }

        Machine physicalMachine = virtualMachine.getHypervisor().getMachine();

        try
        {
            updateUsagePhysicalMachine(physicalMachine, virtualMachine, false);

            if (!isHA)
            {
                updateUsageDatastore(virtualMachine, false);
                updateNetworkingResources(physicalMachine, virtualMachine, virtualApplianceId);

                virtualMachine.setState(State.LOCKED);
            }
            else
            {
                updateNewtorkingResourcesHA(physicalMachine, virtualMachine);
            }

            vmachineDao.flush();
        }
        catch (final ConstraintViolationException cve)
        {

            final StringBuilder msg = new StringBuilder("Invalid database mapping, caused by:");
            for (ConstraintViolation< ? > cv : cve.getConstraintViolations())
            {
                msg.append(String.format("\nEnitity :%s Property :%s InvalidValue :%s\n%s", cv
                    .getRootBeanClass().getName(), cv.getPropertyPath().toString(), cv
                    .getInvalidValue(), cv.getMessage()));
            }

            cve.printStackTrace(); // FIXME
            throw new ResourceUpgradeUseException(msg.toString());
        }
        catch (final Exception e)
        // HibernateException NotEnoughResourcesException NoSuchObjectException
        {
            e.printStackTrace(); // FIXME
            throw new ResourceUpgradeUseException("Can not update resource utilization"
                + e.getMessage());
        }
    }

    @Override
    public void rollbackUse(final VirtualMachine virtualMachine)
    {

        final Machine physicalMachine = virtualMachine.getHypervisor().getMachine();

        try
        {
            updateUsageDatastore(virtualMachine, true);

            updateUsagePhysicalMachine(physicalMachine, virtualMachine, true);

            rollbackNetworkingResources(physicalMachine, virtualMachine);

            virtualMachine.setState(State.ALLOCATED);
            vmachineDao.flush();
        }
        catch (final Exception e) // HibernateException NotEnoughResourcesException
        // NoSuchObjectException
        {
            throw new ResourceUpgradeUseException("Can not update resource utilization"
                + e.getMessage());
        }
    }

    /**
     * Updates the ''vswitch'' of the RASD associated to the current Virtual Machine's
     * IpPoolManagements.
     */
    private void updateNewtorkingResourcesHA(final Machine haPhysicalTarget,
        final VirtualMachine virtualMachine) throws NotEnoughResourcesException,
        NoSuchObjectException
    {
        final String vswitch = haPhysicalTarget.getVirtualSwitch();

        List<IpPoolManagement> ippoolManagementList =
            ipPoolManDao.findByVirtualMachine(virtualMachine);

        log.debug("Update the vswitch to {}", vswitch);
        for (final IpPoolManagement ipPoolManagement : ippoolManagementList)
        {
            // already assigned VLAN TAG if (vlanNetwork.getTag() == null)
            Rasd rasd = ipPoolManagement.getRasd();
            rasd.setConnection(vswitch);
            rasdDao.flush();
        }// iterate over VlanNetwork

    }

    /**
     * Updates the networking resources
     * 
     * @param session the session
     * @param virtualMachine the virtual machine
     * @param physicalTarget the physical target
     * @param virtualApplianceId
     * @throws NotEnoughResourcesException
     * @throws NoSuchObjectException
     */
    private void updateNetworkingResources(final Machine physicalTarget,
        final VirtualMachine virtualMachine, final int virtualApplianceId)
        throws NotEnoughResourcesException, NoSuchObjectException
    {
        final VirtualAppliance vapp = virtualApplianceDao.findById(virtualApplianceId);

        final VirtualDatacenter virtualDatacenter = vapp.getVirtualDatacenter();

        final List<NetworkAssignment> networksAssignedList =
            netAssignDao.findByVirtualDatacenter(virtualDatacenter);

        List<IpPoolManagement> ippoolManagementList =
            ipPoolManDao.findByVirtualMachine(virtualMachine);

        for (final IpPoolManagement ipPoolManagement : ippoolManagementList)
        {
            // Get the network and the rack, entities that perform the network assignment
            VLANNetwork vlanNetwork = ipPoolManagement.getVlanNetwork();
            Rack rack = physicalTarget.getRack();

            // Discover the tag of the vlan if it is the first address to be deployed.
            if (vlanNetwork.getTag() == null)
            {
                List<VLANNetwork> publicVLANs =
                    vlanNetworkDao.findPublicVLANNetworksByDatacenter(rack.getDatacenter());
                List<Integer> vlanTagsUsed = vlanNetworkDao.getVLANTagsUsedInRack(rack);
                vlanTagsUsed.addAll(getPublicVLANTagsFROMVLANNetworkList(publicVLANs));

                Integer freeTag = getFreeVLANFromUsedList(vlanTagsUsed, rack);
                log.debug("The VLAN tag chosen for the vlan network: {} is : {}",
                    vlanNetwork.getId(), freeTag);
                vlanNetwork.setTag(freeTag);

                vlanNetworkDao.flush();
            }
            Rasd rasd = ipPoolManagement.getRasd();
            rasd.setAllocationUnits(String.valueOf(vlanNetwork.getTag()));
            rasd.setParent(ipPoolManagement.getNetworkName());
            rasd.setConnection(physicalTarget.getVirtualSwitch());
            rasdDao.flush();

            final NetworkAssignment nb =
                new NetworkAssignment(virtualDatacenter, rack, vlanNetwork);
            if (!networksAssignedList.contains(nb))
            {
                netAssignDao.persist(nb);
            }
        }
    }

    /**
     * Rollback networking resources
     * 
     * @param session the session
     * @param virtualMachine the virtual machine
     * @param physicalTarget the physical machine
     */
    private void rollbackNetworkingResources(final Machine physicalTarget,
        final VirtualMachine virtualMachine)
    {

        List<IpPoolManagement> ippoolManagementList =
            ipPoolManDao.findByVirtualMachine(virtualMachine);

        for (final IpPoolManagement ipPoolManagement : ippoolManagementList)
        {
            VLANNetwork vlanNetwork = ipPoolManagement.getVlanNetwork();

            final boolean assigned =
                ipPoolManDao.isVlanAssignedToDifferentVM(virtualMachine.getId(), vlanNetwork);

            if (!assigned)
            {
                if (!vlanNetworkDao.isPublic(vlanNetwork))
                {
                    vlanNetwork.setTag(null);
                    vlanNetworkDao.flush();
                    // vlanNetworkDao.persist(vlanNetwork);
                }

                NetworkAssignment na = netAssignDao.findByVlanNetwork(vlanNetwork);

                if (na != null)
                {
                    netAssignDao.remove(na);
                }
            }
        }

    }

    /**
     * Set the resource usage on PhysicalMachine after instantiating the new VirtualMachine. It
     * access DB throw Hibernate.
     * 
     * @param machine, the machine to reduce/increase its resource capacity.
     * @param used, the VirtualImage requirements to substract/add.
     * @param isAdd, true if reducing the amount of resources on the PhysicalMachine. Else it adds
     *            capacity (as a rollback on VirtualImage deploy Exception).
     */
    public void updateUsagePhysicalMachine(final Machine machine, final VirtualMachine used,
        final boolean isRollback)
    {

        final int newCpu =
            isRollback ? machine.getVirtualCpusUsed() - used.getCpu() : machine
                .getVirtualCpusUsed() + used.getCpu();

        final int newRam =
            isRollback ? machine.getVirtualRamUsedInMb() - used.getRam() : machine
                .getVirtualRamUsedInMb() + used.getRam();

        if (used.getVirtualImage().getStateful() == 1)
        {
            used.setHdInBytes(0l); // stateful virtual images doesn't use the datastores
        }

        final Long newHd =
            isRollback ? machine.getVirtualHardDiskUsedInBytes() - used.getHdInBytes() : machine
                .getVirtualHardDiskUsedInBytes() + used.getHdInBytes();

        // prevent to set negative usage
        machine.setVirtualCpusUsed(newCpu >= 0 ? newCpu : 0);
        machine.setVirtualRamUsedInMb(newRam >= 0 ? newRam : 0);
        machine.setVirtualHardDiskUsedInBytes(newHd >= 0 ? newHd : 0);

        machine.setRealCpuCores(machine.getVirtualCpuCores());
        machine.setRealHardDiskInBytes(machine.getVirtualHardDiskInBytes());
        machine.setRealRamInMb(machine.getVirtualRamInMb());

        datacenterRepo.updateMachine(machine);
    }

    @Override
    public void updateUsed(final Machine machine, final int cpuIncrease, final int ramIncrease)
    {
        machine.setVirtualCpusUsed(machine.getVirtualCpusUsed() + cpuIncrease);
        machine.setVirtualRamUsedInMb(machine.getVirtualRamUsedInMb() + ramIncrease);

        datacenterRepo.updateMachine(machine);
    }

    /**
     * Updates the datastore with the used size by the virtual machine (if a shared datastore update
     * all its references).
     * 
     * @param virtual the virtual machine that contains the datastore to update
     * @param session the hibernate session
     */
    private void updateUsageDatastore(final VirtualMachine virtual, final boolean isRollback)
    {

        if (virtual.getVirtualImage().getStateful() == 1)
        {
            // statefull images doesn't update the datastore utilization.
            return;
        }

        Datastore datastore = virtual.getDatastore();

        // updates the datastore utilization for all the shared datastores.
        List<Datastore> datastoresShared = datastoreDao.findShares(datastore);

        for (Datastore dstore : datastoresShared)
        {
            updateDatastore(dstore, virtual.getHdInBytes(), isRollback);
        }

        datastoreDao.flush();
    }

    private void updateDatastore(final Datastore datastore, final Long requestSize,
        final boolean isRollback)
    {
        final Long actualSize = datastore.getUsedSize();

        final Long newUsed = isRollback ? actualSize - requestSize : actualSize + requestSize;

        if (newUsed > datastore.getSize())
        {

            log.error("Target datastore usage is over capacity !!!!! datastore : %s",
                datastore.getName());
        }

        datastore.setUsedSize(newUsed >= 0 ? newUsed : 0); // prevent negative usage
    }

    /**
     * Gets a free VLAN from the list used VLAN
     * 
     * @param rack
     * @param vlan ports
     * @return
     * @throws SchedulerException
     */
    public Integer getFreeVLANFromUsedList(final List<Integer> vlanIds, final Rack rack)
        throws NotEnoughResourcesException
    {
        Integer candidatePort = rack.getVlanIdMin();

        // Adding Vlans Id not to add

        vlanIds.addAll(getVlansIdAvoidAsCollection(rack));

        if (vlanIds.isEmpty())
        {
            return candidatePort;
        }

        // Create a HashSet which allows no duplicates
        HashSet<Integer> hashSet = new HashSet<Integer>(vlanIds);

        // Assign the HashSet to a new ArrayList
        List<Integer> vlanIdsOrdered = new ArrayList<Integer>(hashSet);
        Collections.sort(vlanIdsOrdered);

        List<Integer> vlanTemp = new ArrayList<Integer>(vlanIdsOrdered);

        // Removing id min to vlan id min
        for (Integer vlanId : vlanTemp)
        {
            if (vlanId.intValue() < rack.getVlanIdMin())
            {
                vlanIdsOrdered.remove(vlanId);
            }
        }

        if (vlanIdsOrdered.isEmpty())
        {
            return candidatePort;
        }

        // Checking the minimal interval
        if (vlanIdsOrdered.get(0).compareTo(rack.getVlanIdMin()) != 0)
        {
            return candidatePort;
        }

        int next = 1;

        // Searching a gap in the vlan used list
        for (int i = 0; i < vlanIdsOrdered.size(); i++)
        {
            if (vlanIds.get(i) == rack.getVlanIdMax())
            {
                throw new NotEnoughResourcesException("The maximun number of VLAN id has been reached");
            }
            if (next == vlanIdsOrdered.size()
                || vlanIdsOrdered.get(next) != vlanIdsOrdered.get(i) + 1)
            {
                return vlanIdsOrdered.get(i) + 1;
            }
            next++;
        }

        return candidatePort;
    }

    public Collection<Integer> getVlansIdAvoidAsCollection(final Rack rack)
    {

        Collection<Integer> vlans_avoided_collection = new HashSet<Integer>();
        String avoidedVLANs = rack.getVlansIdAvoided();

        if (avoidedVLANs == null || avoidedVLANs.isEmpty())
        {
            return vlans_avoided_collection;
        }

        CsvReader reader = new CsvReader(new StringReader(avoidedVLANs));
        String[] line = reader.readLine();

        if (line != null)
        {
            try
            {
                for (String vlan_id : line)
                {
                    if (vlan_id.split("-").length > 1)
                    {
                        String[] interval = vlan_id.split("-");
                        Integer min = Integer.valueOf(interval[0]);
                        Integer max = Integer.valueOf(interval[1]);
                        if (min.compareTo(max) > 0)
                        {
                            Integer temp = max;
                            max = min;
                            min = temp;
                        }
                        else
                        {
                            for (int i = min; i <= max; i++)
                            {
                                if (i > rack.getVlanIdMin() || i < rack.getVlanIdMax())
                                {
                                    vlans_avoided_collection.add(i);
                                }
                            }
                        }
                    }
                    else
                    {
                        Integer vlanIdCandidate = Integer.valueOf(vlan_id);
                        if (vlanIdCandidate > rack.getVlanIdMin()
                            || vlanIdCandidate < rack.getVlanIdMax())
                        {
                            vlans_avoided_collection.add(vlanIdCandidate);
                        }
                    }
                }
            }
            catch (NumberFormatException e)
            {
                log.debug("Ignoring not recognize vlan's id", e);
            }
        }
        return vlans_avoided_collection;
    }

    public List<Integer> getPublicVLANTagsFROMVLANNetworkList(
        final List<VLANNetwork> vlanNetworkList)
    {
        List<Integer> publicIdsList = new ArrayList<Integer>();
        for (VLANNetwork vlanNetwork : vlanNetworkList)
        {
            publicIdsList.add(vlanNetwork.getTag());
        }

        return publicIdsList;
    }
}
