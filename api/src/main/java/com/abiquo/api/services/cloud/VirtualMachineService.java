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

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.exceptions.APIException;
import com.abiquo.api.services.DefaultApiService;
import com.abiquo.api.services.RemoteServiceService;
import com.abiquo.api.services.UserService;
import com.abiquo.api.services.VirtualMachineAllocatorService;
import com.abiquo.api.services.stub.TarantinoJobCreator;
import com.abiquo.api.services.stub.TarantinoService;
import com.abiquo.commons.amqp.impl.tarantino.TarantinoRequestProducer;
import com.abiquo.commons.amqp.impl.tarantino.domain.builder.VirtualMachineDescriptionBuilder;
import com.abiquo.commons.amqp.impl.tarantino.domain.dto.DatacenterTasks;
import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.model.transport.error.ErrorDto;
import com.abiquo.model.transport.error.ErrorsDto;
import com.abiquo.scheduler.VirtualMachineRequirementsFactory;
import com.abiquo.server.core.appslibrary.AppsLibraryRep;
import com.abiquo.server.core.appslibrary.VirtualImage;
import com.abiquo.server.core.cloud.Hypervisor;
import com.abiquo.server.core.cloud.NodeVirtualImage;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualApplianceRep;
import com.abiquo.server.core.cloud.VirtualDatacenter;
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
import com.abiquo.server.core.scheduler.VirtualMachineRequirements;
import com.abiquo.tracer.ComponentType;
import com.abiquo.tracer.EventType;
import com.abiquo.tracer.SeverityType;

@Service
@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
public class VirtualMachineService extends DefaultApiService
{
    /** The logger object **/
    private final static Logger logger = LoggerFactory.getLogger(VirtualMachineService.class);

    @Autowired
    protected VirtualMachineRep repo;

    @Autowired
    protected VirtualApplianceRep vappRep;

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

    @Deprecated
    // job creator should be used ONLY inside the TarantinoService
    @Autowired
    private TarantinoJobCreator jobCreator;

    public VirtualMachineService()
    {

    }

    public VirtualMachineService(final EntityManager em)
    {
        this.repo = new VirtualMachineRep(em);
        this.vappRep = new VirtualApplianceRep(em);
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
            logger.error("Error retrieving the virtual machine: {} does not exist", vmId);
            addNotFoundErrors(APIError.NON_EXISTENT_VIRTUALMACHINE);
            flushErrors();
        }
        logger.debug("virtual machine {} found", vmId);
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

    @Transactional(propagation = Propagation.REQUIRED)
    public VirtualMachine updateVirtualMachine(final Integer vdcId, final Integer vappId,
        final Integer vmId, final VirtualMachineDto dto)
    {
        VirtualMachine old = getVirtualMachine(vdcId, vappId, vmId);
        return updateVirtualMachineFromDto(dto, old);
    }

    /**
     * @return the tarantino task Id if required, null if no {@link DatacenterTasks} to tarantino is
     *         required.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public String reconfigureVirtualMachine(final Integer vdcId, final Integer vappId,
        final Integer vmId, final VirtualMachineDto newVirtualMachineDto)
    {
        logger.debug("Starting the reconfigure of the virtual machine {}", vmId);

        // We need to operate with concrete and this also check that the VirtualMachine belongs to
        // those VirtualAppliance and VirtualDatacenter
        VirtualMachine virtualMachine = getVirtualMachine(vdcId, vappId, vmId);
        VirtualAppliance virtualAppliance =
            getVirtualApplianceAndCheckVirtualDatacenter(vdcId, vappId);

        logger.debug("Check for permissions");
        // The user must have the proper permission
        userService.checkCurrentEnterpriseForPostMethods(virtualMachine.getEnterprise());
        logger.debug("Permission granted");

        logger.debug("Checking the virtual machine state. It must be in NOT_ALLOCATED or OFF");
        checkVirtualMachineStateAllowsReconfigure(virtualMachine);
        logger.debug("The state is valid for reconfigure");

        // if NOT_ALLOCATED isn't necessary to check the resource limits
        if (virtualMachine.getState() != VirtualMachineState.NOT_ALLOCATED)
        {
            // There might be different hardware needs. This call also recalculate.
            logger.debug("Updating the hardware needs in DB for virtual machine {}", vmId);
            VirtualMachineRequirements requirements =
                vmRequirements.createVirtualMachineRequirements(virtualMachine,
                    newVirtualMachineDto);
            vmAllocatorService.checkAllocate(vappId, vmId, requirements, false);
            logger.debug("Updated the hardware needs in DB for virtual machine {}", vmId);
        }

        // Current definition in tarantino representation
        VirtualMachineDescriptionBuilder virtualMachineTarantino =
            jobCreator.toTarantinoDto(virtualMachine, virtualAppliance);

        logger.debug("Updating the virtual machine in the DB with id {}", vmId);
        VirtualMachine newVirtualMachine = updateFromDto(newVirtualMachineDto, virtualMachine);
        logger.debug("Updated virtual machine {}", vmId);

        // it is required a tarantino Task ?
        if (!virtualMachine.getState().existsInHypervisor())
        {
            return null; // updated in BBDD and done
        }

        // lock the virtual machine during the async task
        lockVirtualMachine(virtualMachine);

        VirtualMachineDescriptionBuilder newVirtualMachineTarantino =
            jobCreator.toTarantinoDto(newVirtualMachine, virtualAppliance);

        // A datacenter task is a set of jobs and datacenter task. This is, the deploy of a
        // VirtualMachine is the definition of the VirtualMachine and the job, power on
        return tarantino.reconfigureVirtualMachine(virtualMachine, virtualMachineTarantino,
            newVirtualMachineTarantino);
    }

    /** set the virtual machine state to LOCKED (when an async task is needed) */
    private void lockVirtualMachine(final VirtualMachine virtualMachine)
    {
        virtualMachine.setState(VirtualMachineState.LOCKED);
        repo.update(virtualMachine);
    }

    protected VirtualMachine createFromDto(final VirtualMachineDto dto)
    {
        return null; // TODO
    }

    protected VirtualMachine updateFromDto(final VirtualMachineDto dto, final VirtualMachine old)
    {
        return null; // TODO
    }

    @Deprecated
    private VirtualMachine updateVirtualMachineFromDto(final VirtualMachineDto dto,
        final VirtualMachine old)
    {
        virtualMachineFromDto(dto, old);
        updateVirtualMachine(old);
        return old;
    }

    @Deprecated
    private VirtualMachine virtualMachineFromDto(final VirtualMachineDto dto,
        final VirtualMachine old)
    {
        old.setName(dto.getName());
        old.setDescription(dto.getDescription());
        old.setCpu(dto.getCpu());
        old.setRam(dto.getRam());
        old.setHdInBytes(dto.getHdInBytes());
        old.setHighDisponibility(dto.getHighDisponibility());

        if (StringUtils.isNotBlank(old.getPassword()))
        {
            old.setPassword(dto.getPassword());
        }
        else
        {
            old.setPassword(null);
        }
        return old;
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
     * Delete a {@link VirtualMachine}.
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
            logger
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
        logger.debug("Deleting the virtual machine with UUID {}", virtualMachine.getUuid());
        NodeVirtualImage nodeVirtualImage = repo.findNodeVirtualImageByVm(virtualMachine);
        logger.trace("Deleting the node virtual image with id {}", nodeVirtualImage.getId());
        repo.deleteNodeVirtualImage(nodeVirtualImage);
        logger.trace("Deleted node virtual image!");
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
     * Persists a {@link VirtualMachine}. If the preconditions are met.
     * 
     * @param virtualMachine to create. void
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public VirtualMachine createVirtualMachine(final VirtualMachine virtualMachine,
        final Integer enterpriseId, final Integer vImageId, final Integer vdcId,
        final Integer vappId)
    {
        // generates the random identifier
        virtualMachine.setUuid(UUID.randomUUID().toString());
        virtualMachine.setName("ABQ_" + virtualMachine.getUuid());

        // We need the Enterprise
        Enterprise enterprise = getEnterpriseAndValidateUser(enterpriseId);
        virtualMachine.setEnterprise(enterprise);

        VirtualAppliance virtualAppliance = checkVdcVappAndPrivilege(virtualMachine, vdcId, vappId);

        // We need the VirtualImage
        VirtualImage virtualImage =
            getVirtualImageAndValidateEnterpriseAndDatacenter(enterpriseId, virtualAppliance.getVirtualDatacenter().getDatacenter()
                .getId(), vImageId);
        checkVirtualImageCanBeUsed(virtualImage, virtualAppliance);
        virtualMachine.setVirtualImage(virtualImage);

        // We check for a suitable conversion (PREMIUM)
        attachVirtualImageConversion(virtualAppliance.getVirtualDatacenter(), virtualMachine);

        // At this stage the virtual machine is not associated with any hypervisor
        virtualMachine.setState(VirtualMachineState.NOT_ALLOCATED);

        // A user can only create virtual machine
        virtualMachine.setUser(userService.getCurrentUser());
        repo.createVirtualMachine(virtualMachine);

        // The entity that defines the relation between a virtual machine, virtual applicance and
        // virtual image is VirtualImageNode
        createNodeVirtualImage(virtualMachine, virtualAppliance);

        return virtualMachine;
    }

    /**
     * This code is semiduplicated from VirtualImageService but can't be used due cross refrerence
     * dep
     */
    private VirtualImage getVirtualImageAndValidateEnterpriseAndDatacenter(final Integer enterpriseId, final Integer datacenterId,
        final Integer virtualImageId)
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

        VirtualImage virtualImage = appsLibRep.findVirtualImageById(virtualImageId);
        if (virtualImage == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_VIRTUALIMAGE);
            flushErrors();
        }

        return virtualImage;
    }

    /**
     * This code is semiduplicated from EnterpriseService but can't be used due cross refrerence dep
     */
    private Enterprise getEnterpriseAndValidateUser(final Integer id)
    {
        Enterprise enterprise = enterpriseRep.findById(id);
        if (enterprise == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_ENTERPRISE);
            flushErrors();
        }

        // userService.checkEnterpriseAdminCredentials(enterprise);
        userService.checkCurrentEnterprise(enterprise);
        return enterprise;
    }

    /** Checks correct datacenter and enterprise. */
    private void checkVirtualImageCanBeUsed(final VirtualImage vimage, final VirtualAppliance vapp)
    {
        if (vimage.getRepository().getDatacenter().getId() != vapp.getVirtualDatacenter()
            .getDatacenter().getId())
        {
            addConflictErrors(APIError.VIRTUAL_MACHINE_IMAGE_NOT_IN_DATACENTER);
        }

        if (!vimage.isShared() && vimage.getEnterprise().getId() != vapp.getEnterprise().getId())
        {
            addConflictErrors(APIError.VIRTUAL_MACHINE_IMAGE_NOT_ALLOWED);
        }

        flushErrors();
    }

    /**
     * Check if the current request is ok. Checks if the {@link VirtualAppliance} belongs to the
     * {@link VirtualMachine} and if the user has the appropiate grant.<br>
     * <br>
     * Throws <b>all</b> the exceptions.
     * 
     * @param virtualMachine virtual machine.
     * @param vdcId virtual datacenter.
     * @param vappId virtuap appliance.
     * @return VirtualAppliance
     */
    private VirtualAppliance checkVdcVappAndPrivilege(final VirtualMachine virtualMachine,
        final Integer vdcId, final Integer vappId)
    {
        VirtualAppliance virtualAppliance =
            getVirtualApplianceAndCheckVirtualDatacenter(vdcId, vappId);

        userService.checkCurrentEnterpriseForPostMethods(virtualMachine.getEnterprise());
        return virtualAppliance;
    }

    /**
     * Creates the {@link NodeVirtualImage} that is the relation of {@link VirtualMachine}
     * {@link VirtualAppliance} and {@link VirtualImage}.
     * 
     * @param virtualMachine virtual machine to be associated with the virtual appliance. It must
     *            contain the virtual image.
     * @param virtualAppliance void where the virtual machine exists.
     */
    private void createNodeVirtualImage(final VirtualMachine virtualMachine,
        final VirtualAppliance virtualAppliance)
    {
        logger.debug("Create node virtual image with name virtual machine: {}"
            + virtualMachine.getName());
        NodeVirtualImage nodeVirtualImage =
            new NodeVirtualImage(virtualMachine.getName(),
                virtualAppliance,
                virtualMachine.getVirtualImage(),
                virtualMachine);
        repo.insertNodeVirtualImage(nodeVirtualImage);
        logger.debug("Node virtual image created!");
    }

    /**
     * Prepares the virtual image, in premium it sets the conversion. Attachs the conversion if
     * premium to the {@link VirtualMachine}.
     * 
     * @param virtualDatacenter from where we retrieve the hypervisor type.
     * @param virtualMachine virtual machine to persist.
     * @return VirtualImage in premium the conversion.
     */
    public void attachVirtualImageConversion(final VirtualDatacenter virtualDatacenter,
        final VirtualMachine virtualMachine)
    {
        // COMMUNITY does nothing.
        logger.debug("attachVirtualImageConversion community edition");
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
        logger.debug("Starting the deploy of the virtual machine {}", vmId);
        // We need to operate with concrete and this also check that the VirtualMachine belongs to
        // those VirtualAppliance and VirtualDatacenter
        VirtualMachine virtualMachine = getVirtualMachine(vdcId, vappId, vmId);

        logger.debug("Check for permissions");
        // The user must have the proper permission
        userService.checkCurrentEnterpriseForPostMethods(virtualMachine.getEnterprise());
        logger.debug("Permission granted");

        logger.debug("Checking the virtual machine state. It must be in NOT_ALLOCATED");
        // If the machine is already allocated we did compute its resources consume before, now
        // we've been doubling it
        checkVirtualMachineStateAllowsDeploy(virtualMachine);
        logger.debug("The state is valid for deploy");

        logger.debug("Check remote services");
        // The remote services must be up for this Datacenter if we are to deploy
        checkRemoteServicesByVirtualDatacenter(vdcId);
        logger.debug("Remote services are ok!");

        // Tasks needs the definition of the virtual machine
        VirtualAppliance virtualAppliance =
            getVirtualApplianceAndCheckVirtualDatacenter(vdcId, vappId);

        logger
            .debug("Allocating with force enterpise  soft limits : " + foreceEnterpriseSoftLimits);
        try
        {

            /*
             * Select a machine to allocate the virtual machine, Check limits, Check resources If
             * one of the above fail we cannot allocate the VirtualMachine
             */
            final VirtualMachineRequirements requirements =
                vmRequirements.createVirtualMachineRequirements(virtualMachine);

            vmAllocatorService.allocateVirtualMachine(vmId, vappId, requirements,
                foreceEnterpriseSoftLimits);
            vmAllocatorService.updateVirtualMachineUse(vappId, virtualMachine);
            logger.debug("Allocated!");

            lockVirtualMachine(virtualMachine);

            logger.debug("Mapping the external volumes");
            // We need to map all attached volumes if any
            initiatorMappings(virtualMachine);
            logger.debug("Mapping done!");

            VirtualMachineDescriptionBuilder vmDesc =
                jobCreator.toTarantinoDto(virtualMachine, virtualAppliance);

            logger.info("Generating the link to the status! {}", virtualMachine.getId());
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
            logger
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

    public void closeProducerChannel(final TarantinoRequestProducer producer)
    {
        try
        {
            if (producer == null)
            {
                return;
            }
            producer.closeChannel();
        }
        catch (IOException e)
        {
            logger.error("Error closing the producer channel with error: " + e.getMessage());
            tracer.log(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE, EventType.VM_DEPLOY,
                APIError.GENERIC_OPERATION_ERROR.getMessage());

            // For the Admin to know all errors
            tracer.systemLog(
                SeverityType.CRITICAL,
                ComponentType.VIRTUAL_MACHINE,
                EventType.VM_DEPLOY,
                "Error closing the producer channel with error:. The error message was "
                    + e.getMessage());
        }
    }

    /**
     * Checks the {@link RemoteService} of the {@link VirtualDatacenter} and logs if any error.
     * 
     * @param vdcId void
     */
    private void checkRemoteServicesByVirtualDatacenter(final Integer vdcId)
    {
        logger.debug("Checking remote services for virtual datacenter {}", vdcId);
        VirtualDatacenter virtualDatacenter = vdcService.getVirtualDatacenter(vdcId);
        ErrorsDto rsErrors =
            checkRemoteServiceStatusByDatacenter(virtualDatacenter.getDatacenter().getId());
        if (!rsErrors.isEmpty())
        {
            logger.error("Some errors found while cheking remote services");
            tracer.log(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE, EventType.VM_DEPLOY,
                APIError.GENERIC_OPERATION_ERROR.getMessage());
            // For the Admin to know all errors
            traceAdminErrors(rsErrors, SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE,
                EventType.VM_DEPLOY, "The Remote Service is down or not configured", true);

            // There is no point in continue
            addNotFoundErrors(APIError.GENERIC_OPERATION_ERROR);
            flushErrors();
        }
        logger.debug("Remote services Ok!");
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
        logger.debug("The virtual machine is in state {}" + virtualMachine.getState().name());
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

        logger.debug("The virtual machine is in state {}" + virtualMachine.getState().name());
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
                logger.debug("The virtual machine is in state {}"
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
        logger.debug("initiatorMappings community edition");
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
        logger.debug("Starting the undeploy of the virtual machine {}", vmId);
        // We need to operate with concrete and this also check that the VirtualMachine belongs to
        // those VirtualAppliance and VirtualDatacenter
        VirtualMachine virtualMachine = getVirtualMachine(vdcId, vappId, vmId);

        logger.debug("Check for permissions");
        // The user must have the proper permission
        userService.checkCurrentEnterpriseForPostMethods(virtualMachine.getEnterprise());
        logger.debug("Permission granted");

        logger
            .debug(
                "Checking that the virtual machine id {} is in an appropriate state. Valid states are OFF, ON, PAUSED",
                virtualMachine.getId());
        // Not every state is valid for a virtual machine to deploy
        checkVirtualMachineStateAllowsUndeploy(virtualMachine);
        logger
            .debug("The virtual machine id {} is in an appropriate state", virtualMachine.getId());

        logger.debug("Check remote services");
        // The remote services must be up for this Datacenter if we are to deploy
        checkRemoteServicesByVirtualDatacenter(vdcId);
        logger.debug("Remote services are ok!");

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

            String location =
                tarantino.undeployVirtualMachine(virtualMachine, vmDesc, currentState);
            logger.info("Undeploying of the virtual machine id {} in tarantino!",
                virtualMachine.getId());
            tracer.log(SeverityType.INFO, ComponentType.VIRTUAL_MACHINE, EventType.VM_UNDEPLOY,
                "Undeploy of the virtual machine with name " + virtualMachine.getName()
                    + " enqueued successfully!");
            // For the Admin to know all errors
            tracer.systemLog(SeverityType.INFO, ComponentType.VIRTUAL_MACHINE,
                EventType.VM_UNDEPLOY, "The enqueuing in Tarantino was OK.");

            return location;

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
            logger
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
        logger.info("Applying the new state of the virtual machine id {} in tarantino!",
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
            logger.error("Error retrieving the virtual machine: {} does not exist", vmId);
            addNotFoundErrors(APIError.NON_EXISTENT_VIRTUALMACHINE);
            flushErrors();
        }
        logger.debug("virtual machine {} found", vmId);

        NodeVirtualImage nodeVirtualImage = repo.findNodeVirtualImageByVm(vm);
        if (nodeVirtualImage == null)
        {
            logger.error("Error retrieving the node virtual image of machine: {} does not exist",
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
            logger.error("Error retrieving the virtual appliance: {} does not exist", vappId);
            addNotFoundErrors(APIError.NON_EXISTENT_VIRTUALAPPLIANCE);
            flushErrors();
        }
        logger.debug("virtual appliance {} found", vappId);

        return vapp.getNodes();
    }

    /** set the virtual machine state to UNKNOWN (when an async task is needed) */
    private void setVirtualMachineUnknown(final VirtualMachine virtualMachine)
    {
        virtualMachine.setState(VirtualMachineState.UNKNOWN);
        repo.update(virtualMachine);
    }
}
