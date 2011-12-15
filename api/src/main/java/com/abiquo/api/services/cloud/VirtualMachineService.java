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

package com.abiquo.api.services.cloud;

import static com.abiquo.api.resources.appslibrary.VirtualMachineTemplateResource.VIRTUAL_MACHINE_TEMPLATE;
import static com.abiquo.api.util.URIResolver.buildPath;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.ws.rs.core.MultivaluedMap;

import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.exceptions.APIException;
import com.abiquo.api.exceptions.BadRequestException;
import com.abiquo.api.resources.EnterpriseResource;
import com.abiquo.api.resources.EnterprisesResource;
import com.abiquo.api.resources.appslibrary.DatacenterRepositoriesResource;
import com.abiquo.api.resources.appslibrary.DatacenterRepositoryResource;
import com.abiquo.api.resources.appslibrary.VirtualMachineTemplateResource;
import com.abiquo.api.resources.appslibrary.VirtualMachineTemplatesResource;
import com.abiquo.api.resources.cloud.DiskResource;
import com.abiquo.api.resources.cloud.DisksResource;
import com.abiquo.api.resources.cloud.IpAddressesResource;
import com.abiquo.api.resources.cloud.PrivateNetworkResource;
import com.abiquo.api.resources.cloud.PrivateNetworksResource;
import com.abiquo.api.resources.cloud.VirtualDatacenterResource;
import com.abiquo.api.resources.cloud.VirtualDatacentersResource;
import com.abiquo.api.services.DefaultApiService;
import com.abiquo.api.services.NetworkService;
import com.abiquo.api.services.RemoteServiceService;
import com.abiquo.api.services.UserService;
import com.abiquo.api.services.VirtualMachineAllocatorService;
import com.abiquo.api.services.stub.TarantinoJobCreator;
import com.abiquo.api.services.stub.TarantinoService;
import com.abiquo.api.util.URIResolver;
import com.abiquo.commons.amqp.impl.tarantino.domain.builder.VirtualMachineDescriptionBuilder;
import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.model.enumerator.NetworkType;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.model.transport.SingleResourceTransportDto;
import com.abiquo.model.transport.error.CommonError;
import com.abiquo.model.transport.error.ErrorDto;
import com.abiquo.model.transport.error.ErrorsDto;
import com.abiquo.model.util.ModelTransformer;
import com.abiquo.scheduler.VirtualMachineRequirementsFactory;
import com.abiquo.server.core.appslibrary.AppsLibraryRep;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplate;
import com.abiquo.server.core.cloud.Hypervisor;
import com.abiquo.server.core.cloud.NodeVirtualImage;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualApplianceRep;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualDatacenterRep;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.VirtualMachineDto;
import com.abiquo.server.core.cloud.VirtualMachineRep;
import com.abiquo.server.core.cloud.VirtualMachineState;
import com.abiquo.server.core.cloud.VirtualMachineStateTransition;
import com.abiquo.server.core.enterprise.DatacenterLimits;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.EnterpriseRep;
import com.abiquo.server.core.enterprise.User;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.InfrastructureRep;
import com.abiquo.server.core.infrastructure.RemoteService;
import com.abiquo.server.core.infrastructure.management.Rasd;
import com.abiquo.server.core.infrastructure.management.RasdDAO;
import com.abiquo.server.core.infrastructure.management.RasdManagement;
import com.abiquo.server.core.infrastructure.management.RasdManagementDAO;
import com.abiquo.server.core.infrastructure.network.IpPoolManagement;
import com.abiquo.server.core.infrastructure.network.IpPoolManagement.Type;
import com.abiquo.server.core.infrastructure.network.VLANNetwork;
import com.abiquo.server.core.infrastructure.storage.DiskManagement;
import com.abiquo.server.core.infrastructure.storage.StorageRep;
import com.abiquo.server.core.infrastructure.storage.VolumeManagement;
import com.abiquo.server.core.scheduler.VirtualMachineRequirements;
import com.abiquo.server.core.util.network.IPNetworkRang;
import com.abiquo.tracer.ComponentType;
import com.abiquo.tracer.EventType;
import com.abiquo.tracer.SeverityType;

@Service
@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
public class VirtualMachineService extends DefaultApiService
{
    /** The logger object **/
    private final static Logger LOGGER = LoggerFactory.getLogger(VirtualMachineService.class);

    @Autowired
    protected VirtualMachineRep repo;

    @Autowired
    protected RasdManagementDAO rasdDao;

    @Autowired
    protected RasdDAO rasdRawRao;

    @Autowired
    protected VirtualApplianceRep vappRep;

    @Autowired
    protected VirtualDatacenterRep vdcRep;

    @Autowired
    private RemoteServiceService remoteServiceService;

    @Autowired
    private UserService userService;

    @Autowired
    protected EnterpriseRep enterpriseRep;

    @Autowired
    private StorageRep storageRep;

    @Autowired
    private VirtualMachineAllocatorService vmAllocatorService;

    @Autowired
    private VirtualMachineRequirementsFactory vmRequirements;

    @Autowired
    private VirtualDatacenterService vdcService;

    @Autowired
    private InfrastructureRep infRep;

    @Autowired
    protected AppsLibraryRep appsLibRep;

    @Autowired
    private TarantinoService tarantino;

    @Autowired
    private TarantinoJobCreator jobCreator;

    @Autowired
    private NetworkService ipService;

    public VirtualMachineService()
    {

    }

    public VirtualMachineService(final EntityManager em)
    {
        this.repo = new VirtualMachineRep(em);
        this.rasdDao = new RasdManagementDAO(em);
        this.vappRep = new VirtualApplianceRep(em);
        this.vdcRep = new VirtualDatacenterRep(em);
        this.remoteServiceService = new RemoteServiceService(em);
        this.userService = new UserService(em);
        this.enterpriseRep = new EnterpriseRep(em);
        this.vmAllocatorService = new VirtualMachineAllocatorService(em);
        this.vmRequirements = new VirtualMachineRequirementsFactory(); // XXX
        this.vdcService = new VirtualDatacenterService(em);
        this.infRep = new InfrastructureRep(em);
        this.appsLibRep = new AppsLibraryRep(em);
        this.tarantino = new TarantinoService(em);
        this.storageRep = new StorageRep(em);
        this.jobCreator = new TarantinoJobCreator(em);
        this.ipService = new NetworkService(em);
    }

    public Collection<VirtualMachine> findByHypervisor(final Hypervisor hypervisor)
    {
        assert hypervisor != null;
        return repo.findByHypervisor(hypervisor);
    }

    public Collection<VirtualMachine> findByEnterprise(final Enterprise enterprise)
    {
        assert enterprise != null;
        return repo.findByEnterprise(enterprise);
    }

    public Collection<VirtualMachine> findVirtualMachinesByUser(final Enterprise enterprise,
        final User user)
    {
        return repo.findVirtualMachinesByUser(enterprise, user);
    }

    public List<VirtualMachine> findByVirtualAppliance(final VirtualAppliance vapp)
    {
        return repo.findVirtualMachinesByVirtualAppliance(vapp.getId());
    }

    public VirtualMachine findByUUID(final String uuid)
    {
        return repo.findByUUID(uuid);
    }

    public VirtualMachine findByName(final String name)
    {
        return repo.findByName(name);
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public VirtualMachine getVirtualMachine(final Integer vdcId, final Integer vappId,
        final Integer vmId)
    {
        VirtualMachine vm = repo.findVirtualMachineById(vmId);

        VirtualAppliance vapp = getVirtualApplianceAndCheckVirtualDatacenter(vdcId, vappId);

        if (vm == null || !isAssignedTo(vmId, vapp.getId()))
        {
            LOGGER.error("Error retrieving the virtual machine: {} does not exist", vmId);
            addNotFoundErrors(APIError.NON_EXISTENT_VIRTUALMACHINE);
            flushErrors();
        }
        LOGGER.debug("virtual machine {} found", vmId);
        return vm;
    }

    /**
     * This method is semi-duplicated from VirtualApplianceService, but bean can not be used due
     * cross references
     */
    private VirtualAppliance getVirtualApplianceAndCheckVirtualDatacenter(final Integer vdcId,
        final Integer vappId)
    {
        // checks vdc exist
        vdcService.getVirtualDatacenter(vdcId);

        VirtualAppliance vapp = vappRep.findById(vappId);
        if (vapp == null || !vapp.getVirtualDatacenter().getId().equals(vdcId))
        {
            addNotFoundErrors(APIError.NON_EXISTENT_VIRTUALAPPLIANCE);
            flushErrors();
        }
        return vapp;
    }

    public VirtualMachine getVirtualMachine(final Integer vmId)
    {
        return repo.findVirtualMachineById(vmId);
    }

    public VirtualMachine getVirtualMachineByHypervisor(final Hypervisor hyp, final Integer vmId)
    {
        VirtualMachine vm = repo.findVirtualMachineByHypervisor(hyp, vmId);
        if (vm == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_VIRTUALMACHINE);
            flushErrors();
        }
        return vm;
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void addVirtualMachine(final VirtualMachine virtualMachine)
    {
        validate(virtualMachine);
        repo.insert(virtualMachine);
    }

/**
     * Gets the DTO object and validates all of its parameters. Prepares the {@link VirtualMachine} object
     * and sends the object to the method {@link VirtualMachineService#reconfigureVirtualMachine(VirtualDatacenter, VirtualAppliance, VirtualMachine, VirtualMachine).
     * 
     * @param vdcId identifier of the {@link VirtualDatacenter}
     * @param vappId identifier of the {@link VirtualAppliance}
     * @param vmId identifier of the {@link VirtualMachine}
     * @param dto input {@link VirtualMachineDto} object with all its links.
     * @return the link to the asnyncronous task.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public String reconfigureVirtualMachine(final Integer vdcId, final Integer vappId,
        final Integer vmId, final VirtualMachineDto dto)
    {
        VirtualDatacenter vdc = getVirtualDatacenter(vdcId);
        // We need to operate with concrete and this also check that the VirtualMachine belongs to
        // those VirtualAppliance and VirtualDatacenter
        VirtualMachine virtualMachine = getVirtualMachine(vdcId, vappId, vmId);
        VirtualAppliance virtualAppliance =
            getVirtualApplianceAndCheckVirtualDatacenter(vdcId, vappId);

        return reconfigureVirtualMachine(vdc, virtualAppliance, virtualMachine,
            buildVirtualMachineFromDto(vdc, virtualAppliance, dto));
    }

    /**
     * <pre>
     * Prepare the machine to reconfigure. That means:
     * - Check the new allocation requirements
     * - Create the temporal register in database with the old values of the virtual machine for rollback purposes.
     * - Prepares and send the tarantino job.
     * - Returns the link to the asynchronous task to query in order to see the progress.
     * </pre>
     * 
     * This method assumes: - Any of the input params is null. - The 'isAssigned' checks are already
     * done: the virtual machine actually belongs to virtual appliance and the virtual appliance
     * actually belongs to virtual datacenter.
     * 
     * @param vdc {@link VirtualDatacenter} object where the virtual machine to reconfigure belongs
     *            to.
     * @param vapp {@link VirtualAppliance} object where the virtual machine to reconfigure belongs
     *            to.
     * @param newValues {@link VirtualMachine} exactly as we want to be after the reconfigure.
     * @return a String containing the URI where to check the progress.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public String reconfigureVirtualMachine(final VirtualDatacenter vdc,
        final VirtualAppliance vapp, final VirtualMachine vm, final VirtualMachine newValues)
    {
        LOGGER.debug("Starting the reconfigure of the virtual machine {}", vm.getId());

        LOGGER.debug("Check for permissions");
        // The user must have the proper permission
        userService.checkCurrentEnterpriseForPostMethods(vm.getEnterprise());
        newValues.setEnterprise(vm.getEnterprise());
        LOGGER.debug("Permission granted");

        LOGGER.debug("Checking the virtual machine state. It must be in NOT_ALLOCATED or OFF");
        checkVirtualMachineStateAllowsReconfigure(vm);
        LOGGER.debug("The state is valid for reconfigure");

        VirtualMachine backUpVm = null;
        VirtualMachineDescriptionBuilder virtualMachineTarantino = null;

        // if NOT_ALLOCATED isn't necessary to check the resource limits and
        // insert the 'backup' resources
        if (vm.getState() == VirtualMachineState.OFF)
        {
            // There might be different hardware needs. This call also recalculate.
            LOGGER.debug("Updating the hardware needs in DB for virtual machine {}", vm.getId());
            VirtualMachineRequirements requirements =
                vmRequirements.createVirtualMachineRequirements(vm, newValues);
            vmAllocatorService.checkAllocate(vapp.getId(), vm.getId(), requirements, false);

            LOGGER.debug("Updated the hardware needs in DB for virtual machine {}",
                newValues.getId());

            LOGGER
                .debug("Creating the temporary register in Virtual Machine for rollback purposes");
            backUpVm = createBackUpMachine(vm);
            repo.insert(backUpVm);
            createBackUpResources(vm, backUpVm);
            insertBackUpResources(backUpVm);
            LOGGER.debug("Rollback register has id {}" + vm.getId());

            // Before to update the virtualmachine to new values, create the tarantino descriptor
            // (only if the VM is deployed and OFF, othwerwise it won't have a datastore)
            virtualMachineTarantino = jobCreator.toTarantinoDto(vm, vapp);
        }

        // update the old virtual machine with the new virtual machine values.
        // and set the ID of the backupmachine (which has the old values) for recovery purposes.
        LOGGER.debug("Updating the virtual machine in the DB with id {}", vm.getId());
        updateVirtualMachineToNewValues(vapp, vm, newValues);
        LOGGER.debug("Updated virtual machine {}", vm.getId());

        // it is required a tarantino Task ?
        if (vm.getState() == VirtualMachineState.NOT_ALLOCATED)
        {
            return null;
        }

        VirtualMachineDescriptionBuilder newVirtualMachineTarantino =
            jobCreator.toTarantinoDto(vm, vapp);

        // A datacenter task is a set of jobs and datacenter task. This is, the deploy of a
        // VirtualMachine is the definition of the VirtualMachine and the job, power on
        return tarantino.reconfigureVirtualMachine(vm, virtualMachineTarantino,
            newVirtualMachineTarantino);
    }

    /**
     * Insert the backup resources in database.
     * 
     * @param backUpVm
     */
    private void insertBackUpResources(final VirtualMachine backUpVm)
    {

        for (IpPoolManagement ip : backUpVm.getIps())
        {
            vdcRep.insertTemporalIpManagement(ip);
        }
        for (VolumeManagement vol : backUpVm.getVolumes())
        {
            storageRep.insertTemporalVolume(vol);
        }
        for (DiskManagement disk : backUpVm.getDisks())
        {
            storageRep.insertTemporalHardDisk(disk);
        }

        // XXX this a kind of magic !!!
        backUpVm.setIps(null);
        backUpVm.setVolumes(null);
        backUpVm.setDisks(null);

        repo.update(backUpVm);
    }

    /**
     * Just assign the new virtual machine values to the new ones.
     * 
     * @param old old virtual machine instance
     * @param vmnew new virtual machine values
     */
    private void updateVirtualMachineToNewValues(final VirtualAppliance vapp,
        final VirtualMachine old, final VirtualMachine vmnew)
    {
        // update the new values of the virtual machine
        old.setCpu(vmnew.getCpu());
        old.setDescription(vmnew.getDescription());
        old.setRam(vmnew.getRam());
        if (old.getState() == VirtualMachineState.OFF)
        {
            old.setState(VirtualMachineState.LOCKED);
        }

        // dellocate older values, and save the used slots
        List<Integer> usedNICslots = dellocateOldNICs(old, vmnew);
        List<Integer> usedStorageSlots = dellocateOldDisks(old, vmnew);
        usedStorageSlots.addAll(dellocateOldVolumes(old, vmnew));

        // allocate the new values.
        allocateNewNICs(vapp, old, vmnew.getIps(), usedNICslots); // FIXME getOnlyNew Ipd
        

        List<RasdManagement> storageResources = new ArrayList<RasdManagement>();
        storageResources.addAll(vmnew.getDisks()); // FIXME getOnlyNew Disks
        storageResources.addAll(getOnlyDeatachedRasd(old.getVolumes(), vmnew.getVolumes()));

        allocateNewStorages(vapp, old, storageResources, usedStorageSlots);

        // save the new configuration
        repo.update(old);
    }

    private List<VolumeManagement> getOnlyDeatachedRasd(final List<VolumeManagement> currentRasds,
        final List<VolumeManagement> newRasds)
    {
        List<VolumeManagement> reallyNewRasd = new LinkedList<VolumeManagement>();

        for (VolumeManagement newRasd : newRasds)
        {
            if (!newRasd.isAttached()) // TODO attached in the same VM
            {
                reallyNewRasd.add(newRasd); 
            }
        }

        return reallyNewRasd;
    }

    /**
     * Check if the resource is into the list of new resources.
     * 
     * @param resource {@link RasdManagement} resource to check
     * @param resources list of new Resources of the machine
     * @return true if the resource is into the new list.
     */
    private boolean resourceIntoNewList(final RasdManagement resource,
        final List< ? extends RasdManagement> newResources)
    {
        for (RasdManagement newResource : newResources)
        {
            // Since the values point to the same rasd, the id should be the same
            if (resource.getRasd().getId().equals(newResource.getRasd().getId()))
            {
                return true;
            }
        }

        return false;
    }

    /** set the virtual machine state to LOCKED (when an async task is needed) */
    private void lockVirtualMachine(final VirtualMachine virtualMachine)
    {
        virtualMachine.setState(VirtualMachineState.LOCKED);
        repo.update(virtualMachine);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateVirtualMachine(final VirtualMachine vm)
    {
        userService.checkCurrentEnterpriseForPostMethods(vm.getEnterprise());
        repo.update(vm);
    }

    /**
     * Check if a virtual machine is defined into a virtual appliance.
     * 
     * @param vmId identifier of the virtual machine
     * @param vappId identifier of the virtual appliance
     * @return True if it is, false otherwise.
     */
    public boolean isAssignedTo(final Integer vmId, final Integer vappId)
    {
        List<VirtualMachine> vms = repo.findVirtualMachinesByVirtualAppliance(vappId);
        for (VirtualMachine vm : vms)
        {
            if (vm.getId().equals(vmId))
            {
                return true;
            }
        }
        return false;
    }

    public VirtualMachineStateTransition validMachineStateChange(
        final VirtualMachine virtualMachine, final VirtualMachineState newState)
    {
        VirtualMachineStateTransition validTransition =
            VirtualMachineStateTransition.getValidVmStateChangeTransition(
                virtualMachine.getState(), newState);
        if (validTransition == null)
        {
            addConflictErrors(APIError.VIRTUAL_MACHINE_STATE_CHANGE_ERROR);
            flushErrors();
        }
        return validTransition;
    }

    /**
     * Compare the state of vm with the state passed through parameter
     * 
     * @param vm VirtualMachine to which compare the state
     * @param state a valid VirtualMachine state
     * @return true if its the same state, false otherwise
     */
    public Boolean sameState(final VirtualMachine vm, final VirtualMachineState state)
    {
        String actual = vm.getState().toOVF();// OVFGeneratorService.getActualState(vm);
        return state.toOVF().equalsIgnoreCase(actual);
    }

    public void checkPauseAllowed(final VirtualMachine vm, final VirtualMachineState state)
    {
        if (vm.getHypervisor().getType() == HypervisorType.XEN_3
            && state == VirtualMachineState.PAUSED)
        {
            addConflictErrors(APIError.VIRTUAL_MACHINE_PAUSE_UNSUPPORTED);
            flushErrors();
        }
    }

    /**
     * Check that {@link VirtualMachine} is in appropriate state. *
     * <ul>
     * <li>{@link VirtualMachineState#ON}</li>
     * </ul>
     * 
     * @param vm {@link VirtualMachine}. void
     */
    public void checkResetAllowed(final VirtualMachine vm)
    {
        if (vm.getState() != VirtualMachineState.ON)
        {
            addConflictErrors(APIError.VIRTUAL_MACHINE_INVALID_STATE_RESET);
            flushErrors();
        }
    }

    /**
     * The {@link VirtualMachine} is in appropriate state.
     * <ul>
     * <li>{@link VirtualMachineState#OFF}</li>
     * </ul>
     * 
     * @param vm
     */
    public void checkSnapshotAllowed(final VirtualMachine vm)
    {
        if (vm.getState() != VirtualMachineState.OFF)
        {
            addConflictErrors(APIError.VIRTUAL_MACHINE_INVALID_STATE_SNAPSHOT);
            flushErrors();
        }
    }

    /**
     * Delete a {@link VirtualMachine}. And the {@link Node}.
     * 
     * @param virtualMachine to delete. void
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void deleteVirtualMachine(final Integer vmId, final Integer vappId, final Integer vdcId)
    {
        // We need to operate with concrete and this also check that the VirtualMachine belongs
        // to
        // those VirtualAppliance and VirtualDatacenter
        VirtualMachine virtualMachine = getVirtualMachine(vdcId, vappId, vmId);

        // The user must have the proper permission
        userService.checkCurrentEnterpriseForPostMethods(virtualMachine.getEnterprise());

        if (!virtualMachine.getState().equals(VirtualMachineState.NOT_ALLOCATED)
            && !virtualMachine.getState().equals(VirtualMachineState.UNKNOWN))
        {
            LOGGER
                .error(
                    "Delete virtual machine error, the State must be NOT_ALLOCATED or UNKNOWN but was {}",
                    virtualMachine.getState().name());
            tracer.log(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE, EventType.VM_DELETE,
                "virtualMachine.deleteFailed", virtualMachine.getName(), virtualMachine.getState()
                    .name());
            addConflictErrors(APIError.VIRTUAL_MACHINE_INVALID_STATE_DELETE);
            flushErrors();
        }
        LOGGER.debug("Deleting the virtual machine with UUID {}", virtualMachine.getUuid());
        NodeVirtualImage nodeVirtualImage = repo.findNodeVirtualImageByVm(virtualMachine);
        LOGGER.trace("Deleting the node virtual image with id {}", nodeVirtualImage.getId());
        repo.deleteNodeVirtualImage(nodeVirtualImage);
        LOGGER.trace("Deleted node virtual image!");

        // Does it has volumes? PREMIUM
        detachVolumesFromVirtualMachine(virtualMachine);
        LOGGER.debug("Detached the virtual machine's volumes with UUID {}",
            virtualMachine.getUuid());

        detachIps(virtualMachine);

        repo.deleteVirtualMachine(virtualMachine);
        tracer.log(SeverityType.INFO, ComponentType.VIRTUAL_MACHINE, EventType.VM_DELETE,
            "virtualMachine.delete");
    }

    /**
     * Deletes the {@link Rasd} of an {@link IpPoolManagement}.
     * 
     * @param virtualMachine void
     */
    private void detachIps(final VirtualMachine virtualMachine)
    {
        for (IpPoolManagement ip : virtualMachine.getIps())
        {
            vdcRep.deleteRasd(ip.getRasd());
            ip.detach();
            if (Type.EXTERNAL == ip.getType())
            {
                ip.setVirtualDatacenter(null);
                ip.setMac(null);
                ip.setName(null);
            }
        }
    }

    /**
     * This method is properly documented in the premium edition.
     * 
     * @param virtualMachine void
     */
    protected void detachVolumesFromVirtualMachine(final VirtualMachine virtualMachine)
    {
        // PREMIUM
    }

    /**
     * Persists a {@link VirtualMachine}. If the preconditions are met.
     * 
     * @param virtualMachine to create. void
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public VirtualMachine createVirtualMachine(final Integer vdcId, final Integer vappId,
        final VirtualMachineDto dto)
    {
        VirtualDatacenter vdc = getVirtualDatacenter(vdcId);
        // We need to operate with concrete and this also check that the VirtualMachine belongs to
        // those VirtualAppliance and VirtualDatacenter
        VirtualAppliance virtualAppliance =
            getVirtualApplianceAndCheckVirtualDatacenter(vdcId, vappId);

        // First we get from dto. All the values wi
        VirtualMachine virtualMachine = buildVirtualMachineFromDto(vdc, virtualAppliance, dto);
        virtualMachine.setUuid(UUID.randomUUID().toString());
        virtualMachine.setName("ABQ_" + virtualMachine.getUuid());

        // Set the user and enterprise
        virtualMachine.setUser(userService.getCurrentUser());
        virtualMachine.setEnterprise(userService.getCurrentUser().getEnterprise());

        // We check for a suitable conversion (PREMIUM)
        attachVirtualMachineTemplateConversion(virtualAppliance.getVirtualDatacenter(),
            virtualMachine);

        // At this stage the virtual machine is not associated with any hypervisor
        virtualMachine.setState(VirtualMachineState.NOT_ALLOCATED);

        // A user can only create virtual machine
        validate(virtualMachine);
        repo.createVirtualMachine(virtualMachine);

        // The entity that defines the relation between a virtual machine, virtual applicance and
        // virtual machine template is VirtualImageNode
        createNodeVirtualImage(virtualMachine, virtualAppliance);

        // We must add the default NIC. This is the very next free IP in the virtual datacenter's
        // default VLAN
        ipService.assignDefaultNICToVirtualMachine(virtualMachine.getId());

        return virtualMachine;
    }

    /**
     * Sets the virtual machine HD requirements based on the {@link VirtualMachineTemplate}
     * <p>
     * It also set the required CPU and RAM if it wasn't specified in the requested
     * {@link VirtualMachineDto}
     */
    protected void setVirtualMachineTemplateRequirementsIfNotAlreadyDefined(
        final VirtualMachine vmachine, final VirtualMachineTemplate vmtemplate)
    {
        if (vmachine.getCpu() == 0)
        {
            vmachine.setCpu(vmtemplate.getCpuRequired());
        }
        if (vmachine.getRam() == 0)
        {
            vmachine.setRam(vmtemplate.getRamRequired());
        }

        if (vmtemplate.isStateful())
        {
            vmachine.setHdInBytes(0);
        }
        else
        {
            vmachine.setHdInBytes(vmtemplate.getHdRequiredInBytes());
        }
    }

    /**
     * This code is semiduplicated from VirtualMachineTemplateService but can't be used due cross
     * refrerence dep
     */
    private VirtualMachineTemplate getVirtualMachineTemplateAndValidateEnterpriseAndDatacenter(
        final Integer enterpriseId, final Integer datacenterId,
        final Integer virtualMachineTemplateId)
    {

        Datacenter datacenter = infRep.findById(datacenterId);
        Enterprise enterprise = enterpriseRep.findById(enterpriseId);

        if (datacenter == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_DATACENTER);
        }
        if (enterprise == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_ENTERPRISE);
        }
        flushErrors();

        DatacenterLimits limits = infRep.findDatacenterLimits(enterprise, datacenter);
        if (limits == null)
        {
            addConflictErrors(APIError.ENTERPRISE_NOT_ALLOWED_DATACENTER);
            flushErrors();
        }

        VirtualMachineTemplate virtualMachineTemplate =
            appsLibRep.findVirtualMachineTemplateById(virtualMachineTemplateId);
        if (virtualMachineTemplate == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_VIRTUAL_MACHINE_TEMPLATE);
            flushErrors();
        }

        return virtualMachineTemplate;
    }

    /**
     * Creates the {@link NodeVirtualImage} that is the relation of {@link VirtualMachine}
     * {@link VirtualAppliance} and {@link VirtualMachineTemplate}.
     * 
     * @param virtualMachine virtual machine to be associated with the virtual appliance. It must
     *            contain the virtual machine template.
     * @param virtualAppliance void where the virtual machine exists.
     */
    private void createNodeVirtualImage(final VirtualMachine virtualMachine,
        final VirtualAppliance virtualAppliance)
    {
        LOGGER.debug("Create node virtual image with name virtual machine: {}",
            virtualMachine.getName());
        NodeVirtualImage nodeVirtualImage =
            new NodeVirtualImage(virtualMachine.getName(),
                virtualAppliance,
                virtualMachine.getVirtualMachineTemplate(),
                virtualMachine);
        repo.insertNodeVirtualImage(nodeVirtualImage);
        LOGGER.debug("Node virtual image created!");
    }

    /**
     * Prepares the virtual image, in premium it sets the conversion. Attachs the conversion if
     * premium to the {@link VirtualMachine}.
     * 
     * @param virtualDatacenter from where we retrieve the hypervisor type.
     * @param virtualMachine virtual machine to persist.
     * @return VirtualImage in premium the conversion.
     */
    public void attachVirtualMachineTemplateConversion(final VirtualDatacenter virtualDatacenter,
        final VirtualMachine virtualMachine)
    {
        // COMMUNITY does nothing.
        LOGGER.debug("attachVirtualImageConversion community edition");
    }

    /**
     * Deploys a {@link VirtualMachine}. This involves some steps. <br>
     * <ol>
     * <li>Select a machine to allocate the virtual machine</li>
     * <li>Check limits</li>
     * <li>Check resources</li>
     * <li>Check remote services</li>
     * <li>In premium call initiator</li>
     * <li>Subscribe to VSM</li>
     * <li>Build the Task DTO</li>
     * <li>Build the Configure DTO</li>
     * <li>Build the Power On DTO</li>
     * <li>Enqueue in tarantino</li>
     * <li>Register in redis</li>
     * <li>Add Task DTO to rabbitmq</li>
     * <li>Enable the resource <code>Progress<code></li>
     * </ol>
     * 
     * @param vdcId VirtualDatacenter id
     * @param vappId VirtualAppliance id
     * @param vmId VirtualMachine id
     * @param forceEnterpriseSoftLimits Do we should take care of the soft limits?
     * @param restBuilder injected restbuilder context parameter
     * @throws Exception
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public String deployVirtualMachine(final Integer vmId, final Integer vappId,
        final Integer vdcId, final Boolean foreceEnterpriseSoftLimits)
    {
        LOGGER.debug("Starting the deploy of the virtual machine {}", vmId);
        // We need to operate with concrete and this also check that the VirtualMachine belongs to
        // those VirtualAppliance and VirtualDatacenter
        VirtualMachine virtualMachine = getVirtualMachine(vdcId, vappId, vmId);

        LOGGER.debug("Check for permissions");
        // The user must have the proper permission
        userService.checkCurrentEnterpriseForPostMethods(virtualMachine.getEnterprise());
        LOGGER.debug("Permission granted");

        LOGGER.debug("Checking the virtual machine state. It must be in NOT_ALLOCATED");
        // If the machine is already allocated we did compute its resources consume before, now
        // we've been doubling it
        checkVirtualMachineStateAllowsDeploy(virtualMachine);
        LOGGER.debug("The state is valid for deploy");

        LOGGER.debug("Check remote services");
        // The remote services must be up for this Datacenter if we are to deploy
        checkRemoteServicesByVirtualDatacenter(vdcId);
        LOGGER.debug("Remote services are ok!");

        // Tasks needs the definition of the virtual machine
        VirtualAppliance virtualAppliance =
            getVirtualApplianceAndCheckVirtualDatacenter(vdcId, vappId);

        LOGGER
            .debug("Allocating with force enterpise  soft limits : " + foreceEnterpriseSoftLimits);
        try
        {

            /*
             * Select a machine to allocate the virtual machine, Check limits, Check resources If
             * one of the above fail we cannot allocate the VirtualMachine
             */
            vmAllocatorService.allocateVirtualMachine(vmId, vappId, foreceEnterpriseSoftLimits);
            LOGGER.debug("Allocated!");

            lockVirtualMachine(virtualMachine);

            LOGGER.debug("Mapping the external volumes");
            // We need to map all attached volumes if any
            initiatorMappings(virtualMachine);
            LOGGER.debug("Mapping done!");

            VirtualMachineDescriptionBuilder vmDesc =
                jobCreator.toTarantinoDto(virtualMachine, virtualAppliance);

            LOGGER.info("Generating the link to the status! {}", virtualMachine.getId());
            return tarantino.deployVirtualMachine(virtualMachine, vmDesc);
        }
        catch (APIException e)
        {

            setVirtualMachineUnknown(virtualMachine);
            /*
             * Select a machine to allocate the virtual machine, Check limits, Check resources If
             * one of the above fail we cannot allocate the VirtualMachine It also perform the
             * resource recompute
             */
            vmAllocatorService.deallocateVirtualMachine(vmId);
            throw e;
        }
        catch (Exception e)
        {
            tracer.log(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE, EventType.VM_DEPLOY,
                APIError.GENERIC_OPERATION_ERROR.getMessage());

            // For the Admin to know all errors
            tracer.systemLog(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE,
                EventType.VM_DEPLOY, "virtualMachine.deploy", e.toString());
            LOGGER
                .error(
                    "Error deploying setting the virtual machine to UNKNOWN virtual machine name {}: {}",
                    virtualMachine.getUuid(), e.toString());

            setVirtualMachineUnknown(virtualMachine);
            vmAllocatorService.deallocateVirtualMachine(vmId);
            addUnexpectedErrors(APIError.STATUS_INTERNAL_SERVER_ERROR);
            flushErrors();
        }
        return null;
    }

    /**
     * Checks the {@link RemoteService} of the {@link VirtualDatacenter} and logs if any error.
     * 
     * @param vdcId void
     */
    private void checkRemoteServicesByVirtualDatacenter(final Integer vdcId)
    {
        LOGGER.debug("Checking remote services for virtual datacenter {}", vdcId);
        VirtualDatacenter virtualDatacenter = vdcService.getVirtualDatacenter(vdcId);
        ErrorsDto rsErrors =
            checkRemoteServiceStatusByDatacenter(virtualDatacenter.getDatacenter().getId());
        if (!rsErrors.isEmpty())
        {
            LOGGER.error("Some errors found while cheking remote services");
            tracer.log(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE, EventType.VM_DEPLOY,
                APIError.GENERIC_OPERATION_ERROR.getMessage());
            // For the Admin to know all errors
            traceAdminErrors(rsErrors, SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE,
                EventType.VM_DEPLOY, "remoteServices.down", true);

            // There is no point in continue
            addNotFoundErrors(APIError.GENERIC_OPERATION_ERROR);
            flushErrors();
        }
        LOGGER.debug("Remote services Ok!");
    }

    /**
     * The {@link VirtualMachineState} allowed for deploy are: <br>
     * <ul>
     * <li>NOT_ALLOCATED</li>
     * </ul>
     * 
     * @param virtualMachine with a state void
     */
    private void checkVirtualMachineStateAllowsDeploy(final VirtualMachine virtualMachine)
    {
        if (!VirtualMachineState.NOT_ALLOCATED.equals(virtualMachine.getState()))
        {
            tracer.log(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE, EventType.VM_DEPLOY,
                APIError.VIRTUAL_MACHINE_INVALID_STATE_DEPLOY.getMessage());
            tracer.systemLog(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE,
                EventType.VM_DEPLOY, "virtualMachine.deployedOrAllocated");
            addConflictErrors(APIError.VIRTUAL_MACHINE_INVALID_STATE_DEPLOY);
            flushErrors();
        }
        LOGGER.debug("The virtual machine is in state {}" + virtualMachine.getState().name());
    }

    /**
     * The {@link VirtualMachineState} allowed for deploy are: <br>
     * <ul>
     * <li>OFF</li>
     * <li>ALLOCATED</li>
     * <li>NOT_ALLOCTED</li>
     * </ul>
     * 
     * @param virtualMachine with a state void
     */
    private void checkVirtualMachineStateAllowsReconfigure(final VirtualMachine virtualMachine)
    {
        if (!virtualMachine.getState().reconfigureAllowed())
        {
            final String current =
                String.format("VirtualMachine % in %", virtualMachine.getUuid(), virtualMachine
                    .getState().name());

            tracer.log(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE,
                EventType.VM_RECONFIGURE, APIError.VIRTUAL_MACHINE_INCOHERENT_STATE.getMessage());

            tracer.systemLog(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE,
                EventType.VM_RECONFIGURE, APIError.VIRTUAL_MACHINE_INCOHERENT_STATE.getMessage()
                    + "\n" + current);

            addConflictErrors(APIError.VIRTUAL_MACHINE_INCOHERENT_STATE);
            flushErrors();
        }

        LOGGER.debug("The virtual machine is in state {}", virtualMachine.getState().name());
    }

    /**
     * The {@link VirtualMachineState} allowed for undeploy are: <br>
     * <ul>
     * <li>PAUSED</li>
     * <li>POWERED_OFF</li>
     * <li>RUNNING</li>
     * </ul>
     * 
     * @param virtualMachine with a state void
     */
    private void checkVirtualMachineStateAllowsUndeploy(final VirtualMachine virtualMachine)
    {
        switch (virtualMachine.getState())
        {
            case PAUSED:
            case OFF:
            case ON:
            {
                LOGGER.debug("The virtual machine is in state {}"
                    + virtualMachine.getState().name());
                break;
            }
            default:
            {
                tracer.log(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE,
                    EventType.VM_UNDEPLOY,
                    APIError.VIRTUAL_MACHINE_INVALID_STATE_UNDEPLOY.getMessage());

                tracer.systemLog(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE,
                    EventType.VM_UNDEPLOY, "virtualMachine.cannotUndeployed");
                addConflictErrors(APIError.VIRTUAL_MACHINE_INVALID_STATE_UNDEPLOY);
                flushErrors();

            }
        }
    }

    /**
     * Properly documented in Premium.
     * 
     * @param virtualMachine void
     */
    protected void initiatorMappings(final VirtualMachine virtualMachine)
    {
        // PREMIUM
        LOGGER.debug("initiatorMappings community edition");
    }

    /**
     * /** Trace the Errors from a {@link ErrorsDto} to promote encapsulation.
     * 
     * @param rsErrors void
     * @param severityType severity.
     * @param componentType component.
     * @param eventType type.
     * @param msg message.
     * @param appendExceptionMsg should we append the exception message? the format would be
     *            <code>: error message</code> void
     */
    private void traceAdminErrors(final ErrorsDto rsErrors, final SeverityType severityType,
        final ComponentType componentType, final EventType eventType, final String msg,
        final boolean appendExceptionMsg)
    {
        for (ErrorDto e : rsErrors.getCollection())
        {
            tracer.systemLog(severityType, componentType, eventType, msg
                + (appendExceptionMsg ? ": " + e.getMessage() : ""));
        }
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public String undeployVirtualMachine(final Integer vmId, final Integer vappId,
        final Integer vdcId)
    {
        LOGGER.debug("Starting the undeploy of the virtual machine {}", vmId);
        // We need to operate with concrete and this also check that the VirtualMachine belongs to
        // those VirtualAppliance and VirtualDatacenter
        VirtualMachine virtualMachine = getVirtualMachine(vdcId, vappId, vmId);

        LOGGER.debug("Check for permissions");
        // The user must have the proper permission
        userService.checkCurrentEnterpriseForPostMethods(virtualMachine.getEnterprise());
        LOGGER.debug("Permission granted");

        LOGGER
            .debug(
                "Checking that the virtual machine id {} is in an appropriate state. Valid states are OFF, ON, PAUSED",
                virtualMachine.getId());
        // Not every state is valid for a virtual machine to deploy
        checkVirtualMachineStateAllowsUndeploy(virtualMachine);
        LOGGER
            .debug("The virtual machine id {} is in an appropriate state", virtualMachine.getId());

        LOGGER.debug("Check remote services");
        // The remote services must be up for this Datacenter if we are to deploy
        checkRemoteServicesByVirtualDatacenter(vdcId);
        LOGGER.debug("Remote services are ok!");

        VirtualAppliance virtualAppliance =
            getVirtualApplianceAndCheckVirtualDatacenter(vdcId, vappId);

        try
        {
            VirtualMachineState currentState =
                VirtualMachineState.valueOf(virtualMachine.getState().name());
            lockVirtualMachine(virtualMachine);

            // Tasks needs the definition of the virtual machine
            VirtualMachineDescriptionBuilder vmDesc =
                jobCreator.toTarantinoDto(virtualMachine, virtualAppliance);

            String idAsyncTask =
                tarantino.undeployVirtualMachine(virtualMachine, vmDesc, currentState);
            LOGGER.info("Undeploying of the virtual machine id {} in tarantino!",
                virtualMachine.getId());
            tracer.log(SeverityType.INFO, ComponentType.VIRTUAL_MACHINE, EventType.VM_UNDEPLOY,
                "virtualMachine.enqueued", virtualMachine.getName());
            // For the Admin to know all errors
            tracer.systemLog(SeverityType.INFO, ComponentType.VIRTUAL_MACHINE,
                EventType.VM_UNDEPLOY, "virtualMachine.enqueuedTarantino");

            return idAsyncTask;

        }
        catch (APIException e)
        {

            setVirtualMachineUnknown(virtualMachine);
            throw e;
        }
        catch (Exception e)
        {
            tracer.log(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE, EventType.VM_UNDEPLOY,
                APIError.GENERIC_OPERATION_ERROR.getMessage());

            // For the Admin to know all errors
            tracer.systemLog(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE,
                EventType.VM_UNDEPLOY, "virtualMachine.undeployError", e.toString());
            LOGGER
                .error(
                    "Error undeploying setting the virtual machine to UNKNOWN virtual machine name {}: {}",
                    virtualMachine.getUuid(), e.toString());

            setVirtualMachineUnknown(virtualMachine);
            addUnexpectedErrors(APIError.STATUS_INTERNAL_SERVER_ERROR);
            flushErrors();

        }
        return null;
    }

    /**
     * Changes the state of the VirtualMachine to the state passed
     * 
     * @param vappId Virtual Appliance Id
     * @param vdcId VirtualDatacenter Id
     * @param state The state to which change
     * @throws Exception
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public String applyVirtualMachineState(final Integer vmId, final Integer vappId,
        final Integer vdcId, final VirtualMachineState state)
    {
        VirtualMachine virtualMachine = getVirtualMachine(vdcId, vappId, vmId);
        userService.checkCurrentEnterpriseForPostMethods(virtualMachine.getEnterprise());
        // The change state applies on the hypervisor. Now there is a NOT_ALLOCATED to get rid of
        // the if(!hypervisor)
        if (!virtualMachine.getState().existsInHypervisor())
        {
            addConflictErrors(APIError.VIRTUAL_MACHINE_UNALLOCATED_STATE);
            flushErrors();
        }

        checkPauseAllowed(virtualMachine, state);

        // This returns the StateTransition or error
        VirtualMachineStateTransition validMachineStateChange =
            validMachineStateChange(virtualMachine, state);

        VirtualAppliance virtualAppliance =
            getVirtualApplianceAndCheckVirtualDatacenter(vdcId, vappId);
        VirtualMachineDescriptionBuilder machineDescriptionBuilder =
            jobCreator.toTarantinoDto(virtualMachine, virtualAppliance);

        String location =
            tarantino.applyVirtualMachineState(virtualMachine, machineDescriptionBuilder,
                validMachineStateChange);
        LOGGER.info("Applying the new state of the virtual machine id {} in tarantino!",
            virtualMachine.getId());
        tracer.log(SeverityType.INFO, ComponentType.VIRTUAL_MACHINE, EventType.VM_STATE,
            "virtualMachine.applyVirtualMachineEnqueued", virtualMachine.getName());
        // For the Admin to know all errors
        tracer.systemLog(SeverityType.INFO, ComponentType.VIRTUAL_MACHINE, EventType.VM_STATE,
            "virtualMachine.applyVirtualMachineTarantinoEnqueued");

        lockVirtualMachine(virtualMachine);
        // tasksService.
        // Here we add the url which contains the status
        return location;
    }

    /**
     * Reset the VirtualMachine to the state passed
     * 
     * @param vappId Virtual Appliance Id
     * @param vdcId VirtualDatacenter Id
     * @param state The state to which change
     * @throws Exception
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public String resetVirtualMachine(final Integer vmId, final Integer vappId,
        final Integer vdcId, final VirtualMachineStateTransition state)
    {
        VirtualMachine virtualMachine = getVirtualMachine(vdcId, vappId, vmId);
        userService.checkCurrentEnterpriseForPostMethods(virtualMachine.getEnterprise());
        // The change state applies on the hypervisor. Now there is a NOT_ALLOCATED to get rid of
        // the if(!hypervisor)
        if (!virtualMachine.getState().existsInHypervisor())
        {
            addConflictErrors(APIError.VIRTUAL_MACHINE_UNALLOCATED_STATE);
            flushErrors();
        }

        checkResetAllowed(virtualMachine);

        VirtualAppliance virtualAppliance =
            getVirtualApplianceAndCheckVirtualDatacenter(vdcId, vappId);
        VirtualMachineDescriptionBuilder machineDescriptionBuilder =
            jobCreator.toTarantinoDto(virtualMachine, virtualAppliance);

        String location =
            tarantino.applyVirtualMachineState(virtualMachine, machineDescriptionBuilder, state);
        LOGGER.info("Applying the reset of the virtual machine id {} in tarantino!",
            virtualMachine.getId());
        tracer.log(SeverityType.INFO, ComponentType.VIRTUAL_MACHINE, EventType.VM_STATE,
            "virtualMachine.resetVirtualMachineEnqueued", virtualMachine.getName());
        // For the Admin to know all errors
        tracer.systemLog(SeverityType.INFO, ComponentType.VIRTUAL_MACHINE, EventType.VM_STATE,
            "virtualMachine.resetVirtualMachineTarantinoEnqueued");

        lockVirtualMachine(virtualMachine);
        // tasksService.
        // Here we add the url which contains the status
        return location;
    }

    /**
     * Changes the state of the VirtualMachine to the state passed
     * 
     * @param vappId Virtual Appliance Id
     * @param vdcId VirtualDatacenter Id
     * @param state The state to which change
     * @throws Exception
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public String virtualMachineSnapshot(final Integer vmId, final Integer vappId,
        final Integer vdcId, final VirtualMachineStateTransition state)
    {
        VirtualMachine virtualMachine = getVirtualMachine(vdcId, vappId, vmId);
        userService.checkCurrentEnterpriseForPostMethods(virtualMachine.getEnterprise());
        // The change state applies on the hypervisor. Now there is a NOT_ALLOCATED to get rid of
        // the if(!hypervisor)
        if (!virtualMachine.getState().existsInHypervisor())
        {
            addConflictErrors(APIError.VIRTUAL_MACHINE_UNALLOCATED_STATE);
            flushErrors();
        }

        checkSnapshotAllowed(virtualMachine);
        VirtualAppliance virtualAppliance =
            getVirtualApplianceAndCheckVirtualDatacenter(vdcId, vappId);
        VirtualMachineDescriptionBuilder machineDescriptionBuilder =
            jobCreator.toTarantinoDto(virtualMachine, virtualAppliance);

        String location =
            tarantino.applyVirtualMachineState(virtualMachine, machineDescriptionBuilder, state);
        LOGGER.info("Applying the snapshot of the virtual machine id {} in tarantino!",
            virtualMachine.getId());
        tracer.log(SeverityType.INFO, ComponentType.VIRTUAL_MACHINE, EventType.VM_STATE,
            "virtualMachine.virtualMachineSnapshotEnqueued", virtualMachine.getName());
        // For the Admin to know all errors
        tracer.systemLog(SeverityType.INFO, ComponentType.VIRTUAL_MACHINE, EventType.VM_STATE,
            "virtualMachine.virtualMachineSnapshotTarantinoEnqueued");

        lockVirtualMachine(virtualMachine);
        // tasksService.
        // Here we add the url which contains the status
        return location;
    }

    /**
     * Checks one by one all {@link RemoteService} a ssociated with the @{link Datacenter}.
     * 
     * @param datacenterId
     * @return ErrorsDto
     */
    public ErrorsDto checkRemoteServiceStatusByDatacenter(final Integer datacenterId)
    {

        List<RemoteService> remoteServicesByDatacenter =
            infRep.findRemoteServicesByDatacenter(infRep.findById(datacenterId));

        ErrorsDto errors = new ErrorsDto();
        for (RemoteService r : remoteServicesByDatacenter)
        {
            ErrorsDto checkRemoteServiceStatus =
                remoteServiceService.checkRemoteServiceStatus(r.getDatacenter(), r.getType(),
                    r.getUri());
            errors.addAll(checkRemoteServiceStatus);
        }

        return errors;
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public NodeVirtualImage getNodeVirtualImage(final Integer vdcId, final Integer vappId,
        final Integer vmId)
    {
        VirtualMachine vm = repo.findVirtualMachineById(vmId);

        VirtualAppliance vapp = getVirtualApplianceAndCheckVirtualDatacenter(vdcId, vappId);

        if (vm == null || !isAssignedTo(vmId, vapp.getId()))
        {
            LOGGER.error("Error retrieving the virtual machine: {} does not exist", vmId);
            addNotFoundErrors(APIError.NON_EXISTENT_VIRTUALMACHINE);
            flushErrors();
        }
        LOGGER.debug("virtual machine {} found", vmId);

        NodeVirtualImage nodeVirtualImage = repo.findNodeVirtualImageByVm(vm);
        if (nodeVirtualImage == null)
        {
            LOGGER.error("Error retrieving the node virtual image of machine: {} does not exist",
                vmId);
            addNotFoundErrors(APIError.NODE_VIRTUAL_MACHINE_IMAGE_NOT_EXISTS);
            flushErrors();
        }
        return nodeVirtualImage;
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public List<NodeVirtualImage> getNodeVirtualImages(final Integer vdcId, final Integer vappId)
    {
        VirtualAppliance vapp = getVirtualApplianceAndCheckVirtualDatacenter(vdcId, vappId);

        if (vapp == null)
        {
            LOGGER.error("Error retrieving the virtual appliance: {} does not exist", vappId);
            addNotFoundErrors(APIError.NON_EXISTENT_VIRTUALAPPLIANCE);
            flushErrors();
        }
        LOGGER.debug("virtual appliance {} found", vappId);

        return vapp.getNodes();
    }

    /** set the virtual machine state to UNKNOWN (when an async task is needed) */
    private void setVirtualMachineUnknown(final VirtualMachine virtualMachine)
    {
        virtualMachine.setState(VirtualMachineState.UNKNOWN);
        repo.update(virtualMachine);
    }

    /**
     * Builds a {@link VirtualMachine} object from {@link VirtualMachineDto} object.
     * 
     * @param dto transfer input object
     * @return output pojo object.
     */
    protected VirtualMachine buildVirtualMachineFromDto(final VirtualDatacenter vdc,
        final VirtualAppliance vapp, final VirtualMachineDto dto)
    {
        VirtualMachine vm = null;
        try
        {
            vm = ModelTransformer.persistenceFromTransport(VirtualMachine.class, dto);
        }
        catch (Exception e)
        {
            addUnexpectedErrors(APIError.STATUS_BAD_REQUEST);
            flushErrors();
        }

        // get the machine template and set the values of the virtual machine
        // according with the template and its overriden values.
        vm.setVirtualMachineTemplate(getVirtualMachineTemplateFromDto(dto));
        setVirtualMachineTemplateRequirementsIfNotAlreadyDefined(vm, vm.getVirtualMachineTemplate());

        // Get the resources from dto
        List<IpPoolManagement> ips = getNICsFromDto(vdc, dto);
        List<DiskManagement> disks = getHardDisksFromDto(vdc, dto);

        // Set the values for the virtualmachine
        vm.setIps(ips);
        vm.setDisks(disks);

        return vm;
    }

    /**
     * Allocate the POJOs of {@link DiskManagement} and {@link VolumeManagement} for the current
     * virtual datacenter, virtual appliance, virtual machine and the attachment order.
     * 
     * @param vapp {@link VirtualAppliance} object where the resource will be allocated.
     * @param vm {@link VirtualMachine} object where the resource will be allocated.
     * @param resources the list of resources that will be allocated.
     */
    protected void allocateNewStorages(final VirtualAppliance vapp, final VirtualMachine vm,
        final List< ? extends RasdManagement> resources, final List<Integer> blackList)
    {
        // When we allocate a resource, we need to set a unique attachment order for each one.
        // The function #getStorageFreeAttachmentSlot do the work. However, it only takes
        // the information from database, and we need to have a list of integers of the
        // already assigned slots before in the loop. 'blackList' stores them.

        for (RasdManagement resource : resources)
        {
            boolean allocated =
                allocateResource(vm, vapp, resource, getFreeAttachmentSlot(blackList));
            if (allocated)
            {
                // In Hyper-v only 2 attached volumes are allowed
                if (vm.getHypervisor() != null
                    && vm.getHypervisor().getType() == HypervisorType.HYPERV_301
                    && blackList.size() >= 2)
                {
                    addConflictErrors(APIError.VOLUME_TOO_MUCH_ATTACHMENTS);
                    flushErrors();
                }

                // if it is new allocated, we set the integer into the 'blacklisted' list.
                Integer blacklisted =
                    Integer.valueOf(Long.valueOf(resource.getAttachmentOrder()).toString());
                blackList.add(blacklisted);

                if (resource instanceof DiskManagement)
                {
                    vdcRep.updateDisk((DiskManagement) resource);
                }
                else
                {
                    storageRep.updateVolume((VolumeManagement) resource);
                }
            }
        }
    }

    /**
     * Allocate the POJOs of {@link IpPoolManagement} for the current virtual datacenter, virtual
     * appliance, virtual machine and the attachment order.
     * 
     * @param vapp {@link VirtualAppliance} object where the resource will be allocated.
     * @param vm {@link VirtualMachine} object where the resource will be allocated.
     * @param resources the list of resources that will be allocated.
     */
    protected void allocateNewNICs(final VirtualAppliance vapp, final VirtualMachine vm,
        final List<IpPoolManagement> resources, final List<Integer> blackList)
    {
        // When we allocate a resource, we need to set a unique attachment order for each one.
        // The function #getStorageFreeAttachmentSlot do the work. However, it only takes
        // the information from database, and we need to have a list of integers of the
        // already assigned slots before in the loop. 'blackList' stores them.
        for (IpPoolManagement ip : resources)
        {
            boolean allocated = allocateResource(vm, vapp, ip, getFreeAttachmentSlot(blackList));
            if (allocated)
            {
                if (ip.getVlanNetwork().getType().equals(NetworkType.EXTERNAL)
                    || ip.getVlanNetwork().getType().equals(NetworkType.UNMANAGED))
                {
                    String mac =
                        IPNetworkRang.requestRandomMacAddress(vapp.getVirtualDatacenter()
                            .getHypervisorType());
                    String name = mac.replace(":", "") + "_host";
                    ip.setMac(mac);
                    ip.setName(name);
                    ip.setVirtualDatacenter(vapp.getVirtualDatacenter());
                }

                Rasd rasd = NetworkService.createRasdEntity(vm, ip);
                vdcRep.insertRasd(rasd);

                ip.setRasd(rasd);
                vdcRep.updateIpManagement(ip);

                // if it is new allocated, we set the integer into the 'blacklisted' list.
                Integer blacklisted =
                    Integer.valueOf(Long.valueOf(ip.getAttachmentOrder()).toString());
                blackList.add(blacklisted);
            }
        }
    }

    /**
     * Dellocate the NICs of the {@link VirtualMachine} 'oldVm' parameter that are not anymore in
     * the new configuration 'newVm' parameter. Return the list of 'attachment orders' needed to
     * allocate
     * 
     * @param oldVm {@link VirtualMachine} with the 'old' configuration.
     * @param newVm {@link VirtualMachine} with the 'new' configuration.
     * @return the list of attachment order still in oldVm
     */
    protected List<Integer> dellocateOldNICs(final VirtualMachine oldVm, final VirtualMachine newVm)
    {
        List<Integer> oldNicsAttachments = new ArrayList<Integer>();

        // dellocate the old ips that are not in the new virtual machine.
        for (IpPoolManagement ip : oldVm.getIps())
        {
            if (!resourceIntoNewList(ip, newVm.getIps()))
            {
                ip.detach();
                vdcRep.updateIpManagement(ip);
            }
            else
            {
                Integer blacklisted =
                    Integer.valueOf(Long.valueOf(ip.getAttachmentOrder()).toString());
                oldNicsAttachments.add(blacklisted);
            }
        }
        return oldNicsAttachments;
    }

    /**
     * Dellocate the Disks of the {@link VirtualMachine} 'oldVm' parameter that are not anymore in
     * the new configuration 'newVm' parameter. Return the list of 'attachment orders' needed to
     * allocate after that.
     * 
     * @param oldVm {@link VirtualMachine} with the 'old' configuration.
     * @param newVm {@link VirtualMachine} with the 'new' configuration.
     * @return the list of attachment order still in oldVm
     */
    protected List<Integer> dellocateOldDisks(final VirtualMachine oldVm, final VirtualMachine newVm)
    {
        List<Integer> oldDisksAttachments = new ArrayList<Integer>();

        // dellocate the old disks that are not in the new virtual machine.
        for (DiskManagement disk : oldVm.getDisks())
        {
            if (!resourceIntoNewList(disk, newVm.getDisks()))
            {
                disk.detach();
                vdcRep.updateDisk(disk);
            }
            else
            {
                Integer blacklisted =
                    Integer.valueOf(Long.valueOf(disk.getAttachmentOrder()).toString());
                oldDisksAttachments.add(blacklisted);
            }
        }
        return oldDisksAttachments;
    }

    /**
     * Dellocate the Volumes of the {@link VirtualMachine} 'oldVm' parameter that are not anymore in
     * the new configuration 'newVm' parameter. Return the list of 'attachment orders' needed to
     * allocate after that.
     * 
     * @param oldVm {@link VirtualMachine} with the 'old' configuration.
     * @param newVm {@link VirtualMachine} with the 'new' configuration.
     * @return the list of attachment order still in oldVm
     */
    protected List<Integer> dellocateOldVolumes(final VirtualMachine oldVm,
        final VirtualMachine newVm)
    {
        List<Integer> oldVolumesAttachments = new ArrayList<Integer>();

        // dellocate the old disks that are not in the new virtual machine.
        for (VolumeManagement vol : oldVm.getVolumes())
        {
            if (!resourceIntoNewList(vol, newVm.getVolumes()))
            {
                if (!vol.isAttached())
                {
                    addConflictErrors(APIError.VOLUME_NOT_ATTACHED);
                    flushErrors();
                }
                vol.detach();
                storageRep.updateVolume(vol);
            }
            else
            {
                Integer blacklisted =
                    Integer.valueOf(Long.valueOf(vol.getAttachmentOrder()).toString());
                oldVolumesAttachments.add(blacklisted);
            }
        }
        return oldVolumesAttachments;
    }

    /**
     * Get the next free attachment slot to be used to attach a disk, volume, or nic to the virtual
     * machine.
     * 
     * @param vm The virtual machine where the disk will be attached.
     * @return The free slot to use.
     */
    protected int getFreeAttachmentSlot(final List<Integer> blackList)
    {
        // Find the first free slot
        int i = RasdManagement.FIRST_ATTACHMENT_SEQUENCE;
        while (true)
        {
            if (!blackList.contains(i))
            {
                return i; // Found gap
            }

            i++;
        }
    }

    /**
     * Validates the given object with links to a hard disk and returns the referenced hard disk.
     * 
     * @param links The links to validate the hard disk.
     * @param expectedVirtualDatacenter The expected virtual datacenter to be found in the link.
     * @return The list of {@link DiskManagement} referenced by the link.
     * @throws Exception If the link is not valid.
     */
    public List<DiskManagement> getHardDisksFromDto(final VirtualDatacenter vdc,
        final SingleResourceTransportDto dto)
    {
        List<DiskManagement> disks = new LinkedList<DiskManagement>();

        // Validate and load each volume from the link list
        for (RESTLink link : dto.searchLinks(DiskResource.DISK))
        {
            String path =
                buildPath(VirtualDatacentersResource.VIRTUAL_DATACENTERS_PATH,
                    VirtualDatacenterResource.VIRTUAL_DATACENTER_PARAM, DisksResource.DISKS_PATH,
                    DiskResource.DISK_PARAM);

            MultivaluedMap<String, String> pathValues =
                URIResolver.resolveFromURI(path, link.getHref());

            // URI needs to have an identifier to a VDC, and another one to the volume
            if (pathValues == null
                || !pathValues.containsKey(VirtualDatacenterResource.VIRTUAL_DATACENTER)
                || !pathValues.containsKey(DiskResource.DISK))
            {
                throw new BadRequestException(APIError.HD_ATTACH_INVALID_LINK);
            }

            // Volume provided in link must belong to the same virtual datacenter
            Integer vdcId =
                Integer.parseInt(pathValues.getFirst(VirtualDatacenterResource.VIRTUAL_DATACENTER));
            if (!vdcId.equals(vdc.getId()))
            {
                throw new BadRequestException(APIError.HD_ATTACH_INVALID_VDC_LINK);
            }

            Integer diskId = Integer.parseInt(pathValues.getFirst(DiskResource.DISK));

            DiskManagement disk = vdcRep.findHardDiskByVirtualDatacenter(vdc, diskId);
            if (disk == null)
            {
                String errorCode = APIError.HD_NON_EXISTENT_HARD_DISK.getCode();
                String message =
                    APIError.HD_NON_EXISTENT_HARD_DISK.getMessage() + ": Hard Disk id " + diskId;
                CommonError error = new CommonError(errorCode, message);
                addNotFoundErrors(error);
            }
            else
            {
                disks.add(disk);
            }
        }

        // Throw the exception with all the disks we have not found.
        flushErrors();

        return disks;
    }

    /**
     * Validates the given object with links to a NIC and returns the referenced list of
     * {@link IpPoolManagement}.
     * 
     * @param links The links to validate the hard disk.
     * @param expectedVirtualDatacenter The expected virtual datacenter to be found in the link.
     * @return The list of {@link IpPoolManagement} referenced by the link.
     * @throws Exception If the link is not valid.
     */
    public List<IpPoolManagement> getNICsFromDto(final VirtualDatacenter vdc,
        final SingleResourceTransportDto dto)
    {
        List<IpPoolManagement> ips = new LinkedList<IpPoolManagement>();

        // Validate and load each volume from the link list
        for (RESTLink link : dto.searchLinks(PrivateNetworkResource.PRIVATE_IP))
        {
            // Parse the URI with the expected parameters and extract the identifier values.
            String buildPath =
                buildPath(VirtualDatacentersResource.VIRTUAL_DATACENTERS_PATH,
                    VirtualDatacenterResource.VIRTUAL_DATACENTER_PARAM,
                    PrivateNetworksResource.PRIVATE_NETWORKS_PATH,
                    PrivateNetworkResource.PRIVATE_NETWORK_PARAM, IpAddressesResource.IP_ADDRESSES,
                    IpAddressesResource.IP_ADDRESS_PARAM);
            MultivaluedMap<String, String> ipsValues =
                URIResolver.resolveFromURI(buildPath, link.getHref());

            // URI needs to have an identifier to a VDC, another one to a Private Network
            // and another one to Private IP
            if (ipsValues == null
                || !ipsValues.containsKey(VirtualDatacenterResource.VIRTUAL_DATACENTER)
                || !ipsValues.containsKey(PrivateNetworkResource.PRIVATE_NETWORK)
                || !ipsValues.containsKey(IpAddressesResource.IP_ADDRESS))
            {
                throw new BadRequestException(APIError.VLANS_PRIVATE_IP_INVALID_LINK);
            }

            // Private IP must belong to the same Virtual Datacenter where the Virtual Machine
            // belongs to.
            Integer idVdc =
                Integer.parseInt(ipsValues.getFirst(VirtualDatacenterResource.VIRTUAL_DATACENTER));
            if (!idVdc.equals(vdc.getId()))
            {
                throw new BadRequestException(APIError.VLANS_IP_LINK_INVALID_VDC);
            }

            // Extract the vlanId and ipId values to execute the association.
            Integer vlanId =
                Integer.parseInt(ipsValues.getFirst(PrivateNetworkResource.PRIVATE_NETWORK));
            Integer ipId = Integer.parseInt(ipsValues.getFirst(IpAddressesResource.IP_ADDRESS));
            VLANNetwork vlan = vdcRep.findVlanByVirtualDatacenterId(vdc, vlanId);
            if (vlan == null)
            {
                String errorCode = APIError.VLANS_NON_EXISTENT_VIRTUAL_NETWORK.getCode();
                String message =
                    APIError.VLANS_NON_EXISTENT_VIRTUAL_NETWORK.getMessage() + ": Vlan id "
                        + vlanId;
                CommonError error = new CommonError(errorCode, message);
                addNotFoundErrors(error);
                continue;
            }
            IpPoolManagement ip = vdcRep.findIp(vlan, ipId);
            if (ip == null)
            {
                String errorCode = APIError.NON_EXISTENT_IP.getCode();
                String message =
                    APIError.NON_EXISTENT_IP.getMessage() + ": Vlan id " + vlan.getId();
                CommonError error = new CommonError(errorCode, message);
                addNotFoundErrors(error);
                continue;
            }

            ips.add(ip);
        }

        // Throw the exception with all the disks we have not found.
        flushErrors();

        return ips;
    }

    /**
     * Get the object {@link VirtualMachineTemplate} from the input dto.
     * 
     * @param dto the object that should have the link to a virtual machine template.
     * @return the found {@link virtualMachineTemplateObject}
     */
    public VirtualMachineTemplate getVirtualMachineTemplateFromDto(
        final SingleResourceTransportDto dto)
    {
        String vmTemplatePath =
            buildPath(
                EnterprisesResource.ENTERPRISES_PATH,
                EnterpriseResource.ENTERPRISE_PARAM, //
                DatacenterRepositoriesResource.DATACENTER_REPOSITORIES_PATH,
                DatacenterRepositoryResource.DATACENTER_REPOSITORY_PARAM,
                VirtualMachineTemplatesResource.VIRTUAL_MACHINE_TEMPLATES_PATH,
                VirtualMachineTemplateResource.VIRTUAL_MACHINE_TEMPLATE_PARAM);

        RESTLink link = dto.searchLink(VIRTUAL_MACHINE_TEMPLATE);

        if (link == null)
        {
            addValidationErrors(APIError.LINKS_VIRTUAL_MACHINE_TEMPLATE_NOT_FOUND);
            flushErrors();
        }

        Integer entId = null;
        Integer dcId = null;
        Integer templId = null;
        try
        {
            MultivaluedMap<String, String> pathValues =
                URIResolver.resolveFromURI(vmTemplatePath, link.getHref());

            // URI needs to have an identifier to a ENTERPRISE, another to a DATACENTER_REPOSITORY
            // and another one to the TEMPLATE
            if (pathValues == null || !pathValues.containsKey(EnterpriseResource.ENTERPRISE)
                || !pathValues.containsKey(DatacenterRepositoryResource.DATACENTER_REPOSITORY)
                || !pathValues.containsKey(VirtualMachineTemplateResource.VIRTUAL_MACHINE_TEMPLATE))
            {
                throw new BadRequestException(APIError.LINKS_VIRTUAL_MACHINE_TEMPLATE_INVALID_URI);
            }

            entId = Integer.valueOf(pathValues.getFirst(EnterpriseResource.ENTERPRISE));
            dcId =
                Integer.valueOf(pathValues
                    .getFirst(DatacenterRepositoryResource.DATACENTER_REPOSITORY));
            templId =
                Integer.valueOf(pathValues
                    .getFirst(VirtualMachineTemplateResource.VIRTUAL_MACHINE_TEMPLATE));
        }
        catch (Exception e)
        {
            // unhandled exception parsing the uri
            addValidationErrors(APIError.LINKS_INVALID_LINK);
            flushErrors();
        }

        return getVirtualMachineTemplateAndValidateEnterpriseAndDatacenter(entId, dcId, templId);
    }

    /**
     * Gets a VirtualDatacenter. Raises an exception if it does not exist.
     * 
     * @param vdcId identifier of the virtual datacenter.
     * @return the found {@link VirtualDatacenter} instance.
     */
    protected VirtualDatacenter getVirtualDatacenter(final Integer vdcId)
    {
        VirtualDatacenter vdc = vdcRep.findById(vdcId);
        if (vdc == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_VIRTUAL_DATACENTER);
            flushErrors();
        }
        return vdc;
    }

    /**
     * Creates the backup object with a virtual machine.
     * 
     * @param vm original {@link VirtualMachine} obj.
     * @return the backup object.
     */
    public VirtualMachine createBackUpObject(final VirtualMachine vm)
    {
        VirtualMachine backUpVm = createBackUpMachine(vm);
        createBackUpResources(vm, backUpVm);
        return backUpVm;
    }

    /**
     * Copy of the Virtual Machine object.
     * 
     * @param vm {@link VirtualMachine} object original
     * @return the copy of the input param.
     */
    protected VirtualMachine createBackUpMachine(final VirtualMachine vm)
    {
        VirtualMachine tmp = new VirtualMachine();

        // backup virtual machine properties
        tmp.setCpu(vm.getCpu());
        tmp.setDatastore(vm.getDatastore());
        tmp.setDescription(vm.getDescription());
        tmp.setEnterprise(vm.getEnterprise());
        tmp.setHdInBytes(vm.getHdInBytes());
        tmp.setHighDisponibility(vm.getHighDisponibility());
        tmp.setHypervisor(vm.getHypervisor());
        tmp.setIdType(vm.getIdType());
        tmp.setName("tmp_" + vm.getName());
        tmp.setPassword(vm.getPassword());
        tmp.setRam(vm.getRam());
        tmp.setState(VirtualMachineState.LOCKED);
        tmp.setSubState(vm.getSubState());
        tmp.setUser(vm.getUser());
        tmp.setUuid(vm.getUuid());
        tmp.setVdrpIP(vm.getVdrpIP());
        tmp.setVdrpPort(vm.getVdrpPort());
        tmp.setVirtualImageConversion(vm.getVirtualImageConversion());
        tmp.setVirtualMachineTemplate(vm.getVirtualMachineTemplate());
        tmp.setTemporal(vm.getId());

        return tmp;
    }

    /**
     * Copy the resources of the Virtual Machine.
     * 
     * @param vm original virtualmachine
     * @param tmp backup virtualmachine where the resources will be copied.
     */
    protected void createBackUpResources(final VirtualMachine vm, final VirtualMachine tmp)
    {
        // Backup disks
        List<DiskManagement> disksTemp = new ArrayList<DiskManagement>();
        for (DiskManagement disk : vm.getDisks())
        {
            DiskManagement disktmp = new DiskManagement();
            disktmp.setSequence(disk.getSequence());
            disktmp.setDatastore(disk.getDatastore());
            disktmp.setTemporal(disk.getId());
            disktmp.setIdResourceType(disk.getIdResourceType());
            disktmp.setRasd(disk.getRasd());
            disktmp.setReadOnly(disk.getReadOnly());
            disktmp.setSizeInMb(disk.getSizeInMb());
            disktmp.setVirtualAppliance(disk.getVirtualAppliance());
            disktmp.setVirtualDatacenter(disk.getVirtualDatacenter());
            disktmp.setVirtualMachine(tmp);

            disksTemp.add(disktmp);
        }
        tmp.setDisks(disksTemp);

        // Backup NICs
        List<IpPoolManagement> ipsTemp = new ArrayList<IpPoolManagement>();
        for (IpPoolManagement ip : vm.getIps())
        {
            IpPoolManagement ipTmp = new IpPoolManagement();
            ipTmp.setSequence(ip.getSequence());
            ipTmp.setTemporal(ip.getId());
            ipTmp.setIdResourceType(ip.getIdResourceType());
            Hibernate.initialize(ip.getRasd());
            ipTmp.setRasd(ip.getRasd());
            ipTmp.setVirtualAppliance(ip.getVirtualAppliance());
            ipTmp.setVirtualDatacenter(ip.getVirtualDatacenter());
            ipTmp.setVirtualMachine(tmp);

            ipTmp.setName(ip.getName());
            ipTmp.setVlanNetwork(ip.getVlanNetwork());
            ipTmp.setMac(ip.getMac());
            ipTmp.setConfigureGateway(ip.getConfigureGateway());
            ipTmp.setAvailable(ip.getAvailable());
            ipTmp.setNetworkName(ip.getNetworkName());
            ipTmp.setQuarantine(ip.getQuarantine());
            ipTmp.setIp(ip.getIp());

            ipsTemp.add(ipTmp);
        }
        tmp.setIps(ipsTemp);

        // Backup Volumes
        List<VolumeManagement> volsTemp = new ArrayList<VolumeManagement>();
        for (VolumeManagement vol : vm.getVolumes())
        {
            VolumeManagement volTmp = new VolumeManagement();
            volTmp.setSequence(vol.getSequence());
            volTmp.setTemporal(vol.getId());
            volTmp.setIdResourceType(vol.getIdResourceType());
            volTmp.setRasd(vol.getRasd());
            volTmp.setVirtualAppliance(vol.getVirtualAppliance());
            volTmp.setVirtualDatacenter(vol.getVirtualDatacenter());
            volTmp.setVirtualMachine(tmp);

            volTmp.setStoragePool(vol.getStoragePool());
            volTmp.setVirtualMachineTemplate(vol.getVirtualMachineTemplate());
            volTmp.setIdScsi(vol.getIdScsi());
            volTmp.setState(vol.getState());
            volTmp.setUsedSizeInMB(vol.getUsedSizeInMB());

            volsTemp.add(volTmp);
        }
        tmp.setVolumes(volsTemp);
    }

    /**
     * Gets the {@link VirtualMachine} backup created to store reconfigure previous state.
     * 
     * @return the virtualmachine with ''temp'' == provided vm identifier
     */
    public VirtualMachine getBackupVirtualMachine(final VirtualMachine vmachine)
    {
        final VirtualMachine vmbackup = repo.findBackup(vmachine);

        if (vmbackup == null)
        {
            addNotFoundErrors(APIError.VIRTUAL_MACHINE_BACKUP_NOT_FOUND);
            flushErrors();
        }

        return vmbackup;
    }

    /**
     * Cleanup backup resources
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void deleteBackupResources(final VirtualMachine vm)
    {

        try
        {
            rasdDao.enableTemporalOnlyFilter();

            List<RasdManagement> rasds = vm.getRasdManagements();

            // we need to first delete the vm (as it updates the rasd_man)
            repo.deleteVirtualMachine(vm);

            for (RasdManagement rasd : rasds)
            {
                // refresh as the vm delete was updated the rasd
                rasdDao.remove(rasdDao.findById(rasd.getId()));
            }

            rasdDao.flush();
        }
        finally
        {
            rasdDao.restoreDefaultFilters();
        }

        // This is what we like
        // try
        // {
        // rasdDao.enableTemporalOnlyFilter();
        //
        // for (RasdManagement rasd : vm.getRasdManagements())
        // {
        // rasdDao.remove(rasd);
        // }
        //
        // repo.deleteVirtualMachine(repo.findVirtualMachineById(vm.getId()));
        //
        // rasdDao.flush();
        // }
        // finally
        // {
        // rasdDao.restoreDefaultFilters();
        // }
    }

    /**
     * Updates all the attributes and resource attachments of ''updatedVm'' from the backup
     * ''rollbackVm''.
     * 
     * @param updatedVm, current state of the virtual machine (not applied in the hypervisor)
     * @param rollbackVm, state of updaedVm previous to the reconfigure.
     * @return updatedVm with the attributes and resource attachments of rollbackVm.
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public VirtualMachine restoreBackupVirtualMachine(final VirtualMachine updatedVm,
        final VirtualMachine rollbackVm)
    {

        // will use VsmServiceStub to force a refresh
        // updatedVm.setState(VirtualMachineState.LOCKED);

        // backup virtual machine properties
        updatedVm.setCpu(rollbackVm.getCpu());
        updatedVm.setDatastore(rollbackVm.getDatastore());
        updatedVm.setDescription(rollbackVm.getDescription());
        updatedVm.setEnterprise(rollbackVm.getEnterprise());
        updatedVm.setHdInBytes(rollbackVm.getHdInBytes());
        updatedVm.setHighDisponibility(rollbackVm.getHighDisponibility());
        updatedVm.setHypervisor(rollbackVm.getHypervisor());
        updatedVm.setIdType(rollbackVm.getIdType());
        updatedVm.setName(rollbackVm.getName().substring("tmp_".length()));
        updatedVm.setPassword(rollbackVm.getPassword());
        updatedVm.setRam(rollbackVm.getRam());
        updatedVm.setSubState(rollbackVm.getSubState());
        updatedVm.setUser(rollbackVm.getUser());
        updatedVm.setUuid(rollbackVm.getUuid());
        updatedVm.setVdrpIP(rollbackVm.getVdrpIP());
        updatedVm.setVdrpPort(rollbackVm.getVdrpPort());
        updatedVm.setVirtualImageConversion(rollbackVm.getVirtualImageConversion());
        updatedVm.setVirtualMachineTemplate(rollbackVm.getVirtualMachineTemplate());

        List<RasdManagement> updatedResources = updatedVm.getRasdManagements();
        List<RasdManagement> rollbackResources = getBackupResources(rollbackVm);

        repo.deleteVirtualMachine(rollbackVm);
        LOGGER.debug("removed backup virtual machine");

        for (RasdManagement updatedRasd : updatedResources)
        {
            RasdManagement rollbackRasd = getBackupResource(rollbackResources, updatedRasd.getId());

            if (rollbackRasd == null)
            {
                LOGGER.trace("restore: detach resource " + updatedRasd.getId());
                updatedRasd.detach();
            }
        }

        for (RasdManagement rollbackRasd : rollbackResources)
        {
            RasdManagement originalRasd = rasdDao.findById(rollbackRasd.getTemporal());

            if (!originalRasd.isAttached())
            {
                // Re attach the resource to the virtual machine
                LOGGER.trace("restore: attach resource " + originalRasd.getId());
                originalRasd.attach(originalRasd.getSequence(), updatedVm);

            }

            rasdDao.remove(rasdDao.findById(rollbackRasd.getId())); // refresh as the vm was deleted
        }

        repo.update(updatedVm);
        rasdDao.flush();
        // update virtual machine resources

        LOGGER.info("restored virtual machine {} from backup", updatedVm.getUuid());

        return updatedVm;
    }

    /**
     * Get the resources attached to the provided backup virtualmachine.
     */
    private List<RasdManagement> getBackupResources(final VirtualMachine rollbackVm)
    {
        try
        {
            rasdDao.enableTemporalOnlyFilter();
            return rollbackVm.getRasdManagements();
        }
        finally
        {
            rasdDao.restoreDefaultFilters();
        }
    }

    /**
     * Find the backup resources with temporal pointing to the provided resource identifier.
     * 
     * @return resource with temporal == provided resource id, null if not found
     */
    private RasdManagement getBackupResource(final List<RasdManagement> rollbackResources,
        final Integer tempRasdManId)
    {
        for (RasdManagement rasdman : rollbackResources)
        {
            if (tempRasdManId.equals(rasdman.getTemporal()))
            {
                return rasdman;
            }
        }
        return null;
    }

    /*
     * @param vmId
     * @return VirtualMachine with DC.
     */
    public VirtualMachine getVirtualMachineInitialized(final Integer vmId)
    {
        VirtualMachine virtualMachine = repo.findVirtualMachineById(vmId);

        if (virtualMachine == null)
        {
            return null;
        }

        if (virtualMachine.getHypervisor() != null)
        {
            Hibernate.initialize(virtualMachine.getHypervisor().getMachine().getDatacenter());
        }
        if (virtualMachine.getEnterprise() != null)
        {
            Hibernate.initialize(virtualMachine.getEnterprise());
        }
        if (virtualMachine.getDatastore() != null)
        {
            Hibernate.initialize(virtualMachine.getDatastore());
        }
        if (virtualMachine.getVirtualMachineTemplate() != null)
        {
            Hibernate.initialize(virtualMachine.getVirtualMachineTemplate());
        }

        return virtualMachine;
    }

    /**
     * Provides a standard method to allocate a resource and check if its already allocated.
     * 
     * @param vm {@link VirtualMachine} virtual machine where the resource will be allocated.
     * @param vapp {@link VirtualAppliance} virtual appiance where the resource will be allocated.
     * @param resource resource to allocate
     * @param attachOrder the number of allocation order for this resource.
     * @return true if the resource has been allocated, false if it was previously allocated.
     */
    protected boolean allocateResource(final VirtualMachine vm, final VirtualAppliance vapp,
        final RasdManagement resource, final Integer attachOrder)
    {
        if (resource.getVirtualMachine() != null)
        {
            if (!resource.getVirtualMachine().getTemporal().equals(vm.getId()))
            {
                addConflictErrors(APIError.RESOURCE_ALREADY_ASSIGNED_TO_A_VIRTUAL_MACHINE);
                flushErrors();
            }

            // else do nothing, the resource is already asigned to this virtual machine.
            return false;
        }
        else
        {
            resource.attach(attachOrder, vm, vapp);
            return true;
        }
    }

    /**
     * This method writes without care for permissions.
     * 
     * @param vm void
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateVirtualMachineBySystem(final VirtualMachine vm)
    {
        repo.update(vm);
    }

}
