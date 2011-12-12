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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.ws.rs.core.MultivaluedMap;

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
import com.abiquo.api.services.TaskService;
import com.abiquo.api.services.UserService;
import com.abiquo.api.services.VirtualMachineAllocatorService;
import com.abiquo.api.services.stub.TarantinoJobCreator;
import com.abiquo.api.services.stub.TarantinoService;
import com.abiquo.api.util.URIResolver;
import com.abiquo.commons.amqp.impl.tarantino.TarantinoRequestProducer;
import com.abiquo.commons.amqp.impl.tarantino.domain.builder.VirtualMachineDescriptionBuilder;
import com.abiquo.model.enumerator.HypervisorType;
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
import com.abiquo.server.core.infrastructure.network.IpPoolManagement;
import com.abiquo.server.core.infrastructure.network.VLANNetwork;
import com.abiquo.server.core.infrastructure.storage.DiskManagement;
import com.abiquo.server.core.infrastructure.storage.VolumeManagement;
import com.abiquo.server.core.scheduler.VirtualMachineRequirements;
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
    protected VirtualApplianceRep vappRep;

    @Autowired
    protected VirtualDatacenterRep vdcRep;

    @Autowired
    private RemoteServiceService remoteServiceService;

    @Autowired
    private UserService userService;

    @Autowired
    private EnterpriseRep enterpriseRep;

    @Autowired
    private VirtualMachineAllocatorService vmAllocatorService;

    @Autowired
    private VirtualMachineRequirementsFactory vmRequirements;

    @Autowired
    private VirtualDatacenterService vdcService;

    @Autowired
    private InfrastructureRep infRep;

    @Autowired
    private AppsLibraryRep appsLibRep;

    @Autowired
    private TarantinoService tarantino;

    // job creator should be used ONLY inside the TarantinoService
    @Deprecated
    @Autowired
    private TarantinoJobCreator jobCreator;

    @Autowired
    private NetworkService ipService;

    @Autowired
    private TaskService tasksService;

    public VirtualMachineService()
    {

    }

    public VirtualMachineService(final EntityManager em)
    {
        this.repo = new VirtualMachineRep(em);
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
        this.jobCreator = new TarantinoJobCreator(em);
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
    public String reconfigureVirtualMachine(Integer vdcId, Integer vappId, Integer vmId,
        VirtualMachineDto dto)
    {
        VirtualDatacenter vdc = getVirtualDatacenter(vdcId);
        // We need to operate with concrete and this also check that the VirtualMachine belongs to
        // those VirtualAppliance and VirtualDatacenter
        VirtualMachine virtualMachine = getVirtualMachine(vdcId, vappId, vmId);
        VirtualAppliance virtualAppliance =
            getVirtualApplianceAndCheckVirtualDatacenter(vdcId, vappId);

        return reconfigureVirtualMachine(vdc, virtualAppliance, virtualMachine,
            buildVirtualMachineFromDto(vdc, dto));
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
     * @param newValues {@link VirtualMachine} exactly as we want to be after the
     *            reconfigure.
     * @return a String containing the URI where to check the progress.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public String reconfigureVirtualMachine(final VirtualDatacenter vdc,
        final VirtualAppliance vapp, final VirtualMachine vm,
        final VirtualMachine newValues)
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

        // if NOT_ALLOCATED isn't necessary to check the resource limits
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
            VirtualMachine backUpVm = createBackUpObject(vm);
            repo.insert(backUpVm);
            LOGGER.debug("Rollback register has id {}" + vm.getId());
        }

        // Before to update the virtualmachine to new values, create the tarantino descriptor
        VirtualMachineDescriptionBuilder virtualMachineTarantino =
            jobCreator.toTarantinoDto(vm, vapp);
        
        // update the old virtual machine with the new virtual machine values.
        // and set the ID of the backupmachine (which has the old values) for recovery purposes.
        LOGGER.debug("Updating the virtual machine in the DB with id {}", vm.getId());
        updateVirtualMachineToNewValues(vm, newValues);
        repo.update(vm);
        LOGGER.debug("Updated virtual machine {}", vm.getId());

        // next step: 

        // it is required a tarantino Task ?
        if (vm.getState() == VirtualMachineState.OFF)
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
     * Just assign the new virtual machine values to the new ones.
     * 
     * @param old old virtual machine instance
     * @param vmnew new virtual machine values
     */
    private void updateVirtualMachineToNewValues(VirtualMachine old, final VirtualMachine vmnew)
    {
        old.setCpu(vmnew.getCpu());
        old.setDescription(vmnew.getDescription());
        old.setRam(vmnew.getRam());
        old.setState(VirtualMachineState.LOCKED); // Always locked, we are reconfiguring!!!
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
            VirtualMachineStateTransition.getValidTransition(virtualMachine.getState(), newState);
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
     * Delete a {@link VirtualMachine}. And the {@link Node}.
     * 
     * @param virtualMachine to delete. void
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void deleteVirtualMachine(final Integer vmId, final Integer vappId, final Integer vdcId)
    {
        // We need to operate with concrete and this also check that the VirtualMachine belongs to
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
                "Delete of the virtual appliance with name " + virtualMachine.getName()
                    + " failed with due to an invalid state. Should be NOT_DEPLOYED, but was "
                    + virtualMachine.getState().name());
            addConflictErrors(APIError.VIRTUAL_MACHINE_INVALID_STATE_DELETE);
            flushErrors();
        }
        LOGGER.debug("Deleting the virtual machine with UUID {}", virtualMachine.getUuid());
        NodeVirtualImage nodeVirtualImage = repo.findNodeVirtualImageByVm(virtualMachine);
        LOGGER.trace("Deleting the node virtual image with id {}", nodeVirtualImage.getId());
        repo.deleteNodeVirtualImage(nodeVirtualImage);
        LOGGER.trace("Deleted node virtual image!");
        LOGGER.trace("Deleted node virtual image!");

        // Does it has volumes? PREMIUM
        detachVolumesFromVirtualMachine(virtualMachine);
        LOGGER.debug("Detached the virtual machine's volumes with UUID {}",
            virtualMachine.getUuid());

        repo.deleteVirtualMachine(virtualMachine);
        tracer
            .log(
                SeverityType.INFO,
                ComponentType.VIRTUAL_MACHINE,
                EventType.VM_DELETE,
                "Delete of the virtual appliance with name "
                    + virtualMachine.getName()
                    + " failed with due to an invalid state. Should be NOT_DEPLOYED, but was successful!");
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
    public VirtualMachine createVirtualMachine(final Integer vdcId,
        final Integer vappId, final VirtualMachineDto dto)
    {
        VirtualDatacenter vdc = getVirtualDatacenter(vdcId);
        // We need to operate with concrete and this also check that the VirtualMachine belongs to
        // those VirtualAppliance and VirtualDatacenter
        VirtualAppliance virtualAppliance =
            getVirtualApplianceAndCheckVirtualDatacenter(vdcId, vappId);
        
        // First we get from dto. All the values wi
        VirtualMachine virtualMachine = buildVirtualMachineFromDto(vdc, dto); 
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
    private void setVirtualMachineTemplateRequirementsIfNotAlreadyDefined(
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
        LOGGER.debug("Create node virtual image with name virtual machine: {}"
            + virtualMachine.getName());
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
                EventType.VM_DEPLOY,
                "The machine is should be now in state UNKNOWN. Unexpected Error: " + e.toString());
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
                EventType.VM_DEPLOY, "The Remote Service is down or not configured", true);

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
                EventType.VM_DEPLOY, "The Virtual Machine is already deployed or Allocated.");
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

        LOGGER.debug("The virtual machine is in state {}" + virtualMachine.getState().name());
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
                    EventType.VM_UNDEPLOY,
                    "The Virtual Machine is in a state in which cannot be undeployed.");
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
                "Undeploy of the virtual machine with name " + virtualMachine.getName()
                    + " enqueued successfully!");
            // For the Admin to know all errors
            tracer.systemLog(SeverityType.INFO, ComponentType.VIRTUAL_MACHINE,
                EventType.VM_UNDEPLOY, "The enqueuing in Tarantino was OK.");

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
                EventType.VM_UNDEPLOY,
                "The machine is should be now in state UNKNOWN. Unexpected Error: " + e.toString());
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
            "Applying the state of the virtual machine with name " + virtualMachine.getName()
                + " enqueued successfully!");
        // For the Admin to know all errors
        tracer.systemLog(SeverityType.INFO, ComponentType.VIRTUAL_MACHINE, EventType.VM_STATE,
            "The enqueuing in Tarantino was OK. The virtual machine is locked");

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
        final VirtualMachineDto dto)
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
        
        vm.setVirtualMachineTemplate(getVirtualMachineTemplateFromDto(dto));
        setVirtualMachineTemplateRequirementsIfNotAlreadyDefined(vm, vm.getVirtualMachineTemplate());
        vm.setDisks(getHardDisksFromDto(vdc, dto));
        vm.setIps(getNICsFromDto(vdc, dto));

        return vm;
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
     * Validates the given object with links to a NIC and returns the referenced list of {@link IpPoolManagement}.
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
                    APIError.VLANS_NON_EXISTENT_VIRTUAL_NETWORK.getMessage() + ": Vlan id " + vlanId;
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
    public VirtualMachineTemplate getVirtualMachineTemplateFromDto(final SingleResourceTransportDto dto)
    {
        String vmTemplatePath = buildPath(EnterprisesResource.ENTERPRISES_PATH,
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
        
            // URI needs to have an identifier to a ENTERPRISE, another to a DATACENTER_REPOSITORY and another one to the TEMPLATE
            if (pathValues == null
                || !pathValues.containsKey(EnterpriseResource.ENTERPRISE)
                || !pathValues.containsKey(DatacenterRepositoryResource.DATACENTER_REPOSITORY)
                || !pathValues.containsKey(VirtualMachineTemplateResource.VIRTUAL_MACHINE_TEMPLATE))
            {
                throw new BadRequestException(APIError.LINKS_VIRTUAL_MACHINE_TEMPLATE_INVALID_URI);
            }
            
            entId = Integer.valueOf(pathValues.getFirst(EnterpriseResource.ENTERPRISE));
            dcId = Integer.valueOf(pathValues.getFirst(DatacenterRepositoryResource.DATACENTER_REPOSITORY));
            templId = Integer.valueOf(pathValues.getFirst(VirtualMachineTemplateResource.VIRTUAL_MACHINE_TEMPLATE));
        }
        catch(Exception e)
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
     * Copy of the Virtual Machine object.
     * 
     * @param vm {@link VirtualMachine} object original
     * @return the copy of the input param.
     */
    public VirtualMachine createBackUpObject(final VirtualMachine vm)
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
        tmp.setName(vm.getName());
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

        // Backup disks
        List<DiskManagement> disksTemp = new ArrayList<DiskManagement>();
        for (DiskManagement disk : vm.getDisks())
        {
            DiskManagement disktmp = new DiskManagement();
            disktmp.setAttachmentOrder(disk.getAttachmentOrder());
            disktmp.setDatastore(disk.getDatastore());
            disktmp.setDescription(disk.getDescription());
            disktmp.setTemporal(disk.getId());
            disktmp.setIdResourceType(disk.getIdResourceType());
            disktmp.setRasd(disk.getRasd());
            disktmp.setReadOnly(disk.getReadOnly());
            disktmp.setSizeInMb(disk.getSizeInMb());
            disktmp.setVirtualAppliance(disk.getVirtualAppliance());
            disktmp.setVirtualDatacenter(disk.getVirtualDatacenter());
            disktmp.setVirtualMachine(disk.getVirtualMachine());
            
            disksTemp.add(disktmp);
        }
        tmp.setDisks(disksTemp);
        
        // Backup NICs
        List<IpPoolManagement> ipsTemp = new ArrayList<IpPoolManagement>();
        for (IpPoolManagement ip : vm.getIps())
        {
            IpPoolManagement ipTmp = new IpPoolManagement();
            ipTmp.setAttachmentOrder(ip.getAttachmentOrder());
            ipTmp.setDescription(ip.getDescription());
            ipTmp.setTemporal(ip.getId());
            ipTmp.setIdResourceType(ip.getIdResourceType());
            ipTmp.setRasd(ip.getRasd());
            ipTmp.setVirtualAppliance(ip.getVirtualAppliance());
            ipTmp.setVirtualDatacenter(ip.getVirtualDatacenter());
            ipTmp.setVirtualMachine(ip.getVirtualMachine());
            
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
            volTmp.setAttachmentOrder(vol.getAttachmentOrder());
            volTmp.setDescription(vol.getDescription());
            volTmp.setTemporal(vol.getId());
            volTmp.setIdResourceType(vol.getIdResourceType());
            volTmp.setRasd(vol.getRasd());
            volTmp.setVirtualAppliance(vol.getVirtualAppliance());
            volTmp.setVirtualDatacenter(vol.getVirtualDatacenter());
            volTmp.setVirtualMachine(vol.getVirtualMachine());
            
            volTmp.setStoragePool(vol.getStoragePool());
            volTmp.setVirtualMachineTemplate(vol.getVirtualMachineTemplate());
            volTmp.setIdScsi(vol.getIdScsi());
            volTmp.setState(vol.getState());
            volTmp.setUsedSizeInMB(vol.getUsedSizeInMB());
        }
        tmp.setVolumes(volsTemp);
        return tmp;
    }

}
