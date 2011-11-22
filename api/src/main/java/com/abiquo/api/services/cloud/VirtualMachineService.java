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
import java.util.Set;
import java.util.UUID;

import javax.persistence.EntityManager;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.services.DefaultApiService;
import com.abiquo.api.services.EnterpriseService;
import com.abiquo.api.services.InfrastructureService;
import com.abiquo.api.services.MachineService;
import com.abiquo.api.services.NetworkService;
import com.abiquo.api.services.RemoteServiceService;
import com.abiquo.api.services.StorageService;
import com.abiquo.api.services.UserService;
import com.abiquo.api.services.VirtualMachineAllocatorService;
import com.abiquo.api.services.appslibrary.VirtualImageService;
import com.abiquo.api.services.stub.TarantinoService;
import com.abiquo.commons.amqp.impl.tarantino.TarantinoRequestProducer;
import com.abiquo.commons.amqp.impl.tarantino.domain.DiskDescription;
import com.abiquo.commons.amqp.impl.tarantino.domain.HypervisorConnection;
import com.abiquo.commons.amqp.impl.tarantino.domain.StateTransition;
import com.abiquo.commons.amqp.impl.tarantino.domain.DiskDescription.DiskControllerType;
import com.abiquo.commons.amqp.impl.tarantino.domain.VirtualMachineDefinition.PrimaryDisk;
import com.abiquo.commons.amqp.impl.tarantino.domain.builder.VirtualMachineDescriptionBuilder;
import com.abiquo.commons.amqp.impl.tarantino.domain.dto.DatacenterTasks;
import com.abiquo.commons.amqp.impl.tarantino.domain.operations.ApplyVirtualMachineStateOp;
import com.abiquo.commons.amqp.impl.tarantino.domain.operations.ReconfigureVirtualMachineOp;
import com.abiquo.model.enumerator.DiskFormatType;
import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.model.transport.error.ErrorDto;
import com.abiquo.model.transport.error.ErrorsDto;
import com.abiquo.server.core.appslibrary.VirtualImage;
import com.abiquo.server.core.appslibrary.VirtualImageConversion;
import com.abiquo.server.core.cloud.Hypervisor;
import com.abiquo.server.core.cloud.NodeVirtualImage;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualDatacenterRep;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.VirtualMachineDto;
import com.abiquo.server.core.cloud.VirtualMachineRep;
import com.abiquo.server.core.cloud.VirtualMachineState;
import com.abiquo.server.core.cloud.VirtualMachineStateTransition;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.User;
import com.abiquo.server.core.infrastructure.InfrastructureRep;
import com.abiquo.server.core.infrastructure.RemoteService;
import com.abiquo.server.core.infrastructure.network.IpPoolManagement;
import com.abiquo.server.core.infrastructure.network.NetworkConfiguration;
import com.abiquo.server.core.infrastructure.storage.DiskManagement;
import com.abiquo.server.core.infrastructure.storage.StorageRep;
import com.abiquo.tracer.ComponentType;
import com.abiquo.tracer.EventType;
import com.abiquo.tracer.SeverityType;

@Service
@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
public class VirtualMachineService extends DefaultApiService
{
    @Autowired
    protected VirtualMachineRep repo;

    @Autowired
    protected VirtualDatacenterRep vdcRepo;

    @Autowired
    protected VirtualApplianceService vappService;

    @Autowired
    protected RemoteServiceService remoteServiceService;

    @Autowired
    UserService userService;

    @Autowired
    protected EnterpriseService enterpriseService;

    @Autowired
    protected VirtualMachineAllocatorService vmAllocatorService;

    @Autowired
    protected VirtualDatacenterService vdcService;

    @Autowired
    protected MachineService machineService;

    @Autowired
    protected NetworkService ipService;

    @Autowired
    protected StorageRep storageRep;

    @Autowired
    protected InfrastructureRep infRep;

    @Autowired
    protected VirtualImageService vimageService;

    @Autowired
    protected TarantinoService tarantino;

    /** The logger object **/
    private final static Logger logger = LoggerFactory.getLogger(VirtualMachineService.class);

    @Autowired
    protected StorageService storageService;

    public VirtualMachineService()
    {

    }

    public VirtualMachineService(final EntityManager em)
    {
        this.repo = new VirtualMachineRep(em);
        this.storageService = new StorageService(em);
        this.vdcRepo = new VirtualDatacenterRep(em);
        this.vappService = new VirtualApplianceService(em);
        this.userService = new UserService(em);
        this.infRep = new InfrastructureRep(em);
        this.storageRep = new StorageRep(em);
        vdcService = new VirtualDatacenterService(em);
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

        VirtualAppliance vapp = vappService.getVirtualAppliance(vdcId, vappId);

        if (vm == null || !isAssignedTo(vmId, vapp.getId()))
        {
            logger.error("Error retrieving the virtual machine: {} does not exist", vmId);
            addNotFoundErrors(APIError.NON_EXISTENT_VIRTUALMACHINE);
            flushErrors();
        }
        logger.debug("virtual machine {} found", vmId);
        return vm;
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

    @Transactional(propagation = Propagation.REQUIRED)
    public String reconfigureVirtualMachine(final Integer vdcId, final Integer vappId,
        final Integer vmId, final VirtualMachineDto dto)
    {
        logger.debug("Starting the reconfigure of the virtual machine {}", vmId);
        // We need to operate with concrete and this also check that the VirtualMachine belongs to
        // those VirtualAppliance and VirtualDatacenter
        VirtualMachine virtualMachine = getVirtualMachine(vdcId, vappId, vmId);

        logger.debug("Check for permissions");
        // The user must have the proper permission
        userService.checkCurrentEnterpriseForPostMethods(virtualMachine.getEnterprise());
        logger.debug("Permission granted");

        logger.debug("Checking the virtual machine state. It must be in OFF");
        checkVirtualMachineStateAllowsReconfigure(virtualMachine);
        logger.debug("The state is valid for deploy");

        logger.debug("Cloning and setting the data to become the new virtual machine");
        VirtualMachine newVirtualMachine = virtualMachine.clone();
        logger.trace("Clonned");
        virtualMachineFromDto(dto, newVirtualMachine);
        logger.debug("Modified");

        logger.debug("Updating the hardware needs in DB for virtual machine {}",
            virtualMachine.getUuid());
        // There might be different hardware needs. This call also recalculate.
        vmAllocatorService.checkAllocate(vappId, vmId, dto, false);
        logger.debug("Updated the hardware needs in DB for virtual machine {}",
            virtualMachine.getUuid());

        logger.debug("Creating the DatacenterTask");

        // A datacenter task is a set of jobs and datacenter task. This is, the deploy of a
        // VirtualMachine is the definition of the VirtualMachine and the job, power on
        DatacenterTasks reconfigureTask = new DatacenterTasks();

        VirtualDatacenter virtualDatacenter = vdcService.getVirtualDatacenter(vdcId);

        // Tasks needs the definition of the virtual machine and the new one
        VirtualAppliance virtualAppliance = vappService.getVirtualAppliance(vdcId, vappId);
        VirtualMachineDescriptionBuilder vmDesc =
            createVirtualMachineDefinitionBuilder(virtualMachine, virtualDatacenter,
                virtualAppliance);
        VirtualMachineDescriptionBuilder newVmDesc =
            createVirtualMachineDefinitionBuilder(virtualMachine, virtualDatacenter,
                virtualAppliance);

        // The id identifies this job and is neede to create the ids of the items. It is
        // hyerarchic
        // so Task 1 and its job would be 1.1, another 1.2
        reconfigureTask.setId(virtualMachine.getUuid());

        logger.debug("Configure the hypervisor connection");
        // Hypervisor connection related configuration
        HypervisorConnection hypervisorConnection =
            hypervisorConnectionConfiguration(virtualMachine);
        logger.debug("Hypervisor connection configuration done");

        logger.debug("Reconfigure job");
        ReconfigureVirtualMachineOp configJob =
            reconfigureJobConfiguration(virtualMachine, newVirtualMachine, reconfigureTask, vmDesc,
                newVmDesc, hypervisorConnection);
        logger.debug("Reconfigure job done with id {}", configJob.getId());

        // The jobs are to be rolled back
        reconfigureTask.setDependent(Boolean.TRUE);
        reconfigureTask.getJobs().add(configJob);

        TarantinoRequestProducer producer =
            new TarantinoRequestProducer(virtualDatacenter.getDatacenter().getName());

        try
        {
            logger.trace("Deploying of the virtual machine id {} to tarantino: open channel",
                virtualMachine.getId());
            producer.openChannel();
            logger.trace(
                "Deploying of the virtual machine id {} to tarantino: channel opened successfully",
                virtualMachine.getId());
            logger.trace("Deploying of the virtual machine id {} to tarantino: publishing to amqp",
                virtualMachine.getId());
            producer.publish(reconfigureTask);
            logger.trace(
                "Deploying of the virtual machine id {} to tarantino: published successfully",
                virtualMachine.getId());
            logger.info("Deploying of the virtual machine id {} in tarantino!",
                virtualMachine.getId());
            tracer.log(SeverityType.INFO, ComponentType.VIRTUAL_MACHINE, EventType.VM_RECONFIGURE,
                "Reconfigure of the virtual machine with name " + virtualMachine.getName()
                    + " enqueued successfully!");
            tracer.systemLog(SeverityType.INFO, ComponentType.VIRTUAL_MACHINE,
                EventType.VM_RECONFIGURE, "Reconfigure of the virtual machine with name "
                    + virtualMachine.getName() + " enqueued successfully!");

        }
        catch (IOException e)
        {

            logger.error("Error enqueuing the deploy task dto to Tarantino with error: "
                + e.getMessage());

            tracer.log(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE, EventType.VM_DEPLOY,
                APIError.GENERIC_OPERATION_ERROR.getMessage());

            // For the Admin to know all errors
            tracer
                .systemLog(
                    SeverityType.CRITICAL,
                    ComponentType.VIRTUAL_MACHINE,
                    EventType.VM_DEPLOY,
                    "The enqueuing in Tarantino failed. Rabbitmq might be down or not configured. The error message was "
                        + e.getMessage());
            // There is no point in continue
            addNotFoundErrors(APIError.GENERIC_OPERATION_ERROR);
            flushErrors();
        }
        finally
        {
            closeProducerChannel(producer);
        }

        logger.debug("Updating the virtual machine in the DB with id {}", virtualMachine.getId());
        updateVirtualMachineFromDto(dto, virtualMachine);
        logger.debug("Updated virtual machine ");
        return "link";
    }

    private VirtualMachine updateVirtualMachineFromDto(final VirtualMachineDto dto,
        final VirtualMachine old)
    {
        virtualMachineFromDto(dto, old);
        updateVirtualMachine(old);
        return old;
    }

    protected VirtualMachine createFromDto(final VirtualMachineDto dto)
    {
        return null; // TODO
    }

    protected VirtualMachine updateFromDto(final VirtualMachineDto dto, final VirtualMachine old)
    {
        return null; // TODO
    }

    private VirtualMachine virtualMachineFromDto(final VirtualMachineDto dto,
        final VirtualMachine old)
    {
        old.setName(dto.getName());
        old.setDescription(dto.getDescription());
        old.setCpu(dto.getCpu());
        old.setRam(dto.getRam());
        old.setHdInBytes(dto.getHd());
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

    /**
     * Block the virtual by changing its state to IN_PROGRESS
     * 
     * @param vm VirtualMachine to be blocked
     */
    public void blockVirtualMachine(final VirtualMachine vm)
    {
        if (vm.getState() == VirtualMachineState.LOCKED)
        {
            addConflictErrors(APIError.VIRTUAL_MACHINE_ALREADY_IN_PROGRESS);
            flushErrors();
        }

        vm.setState(VirtualMachineState.LOCKED);
        updateVirtualMachine(vm);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public VirtualMachineStateTransition validMachineStateChange(
        final VirtualMachine virtualMachine, final VirtualMachineState newState)
    {
        if (virtualMachine.getState() == VirtualMachineState.NOT_ALLOCATED
            || virtualMachine.getState() == VirtualMachineState.ALLOCATED)
        {
            addConflictErrors(APIError.VIRTUAL_MACHINE_NOT_DEPLOYED);
            flushErrors();
        }
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

        // We need the Enterprise
        Enterprise enterprise = enterpriseService.getEnterprise(enterpriseId);
        virtualMachine.setEnterprise(enterprise);

        VirtualAppliance virtualAppliance = checkVdcVappAndPrivilege(virtualMachine, vdcId, vappId);

        // We need the VirtualImage
        VirtualImage virtualImage =
            vimageService.getVirtualImage(enterpriseId, virtualAppliance.getVirtualDatacenter()
                .getDatacenter().getId(), vImageId);
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
        VirtualAppliance virtualAppliance = vappService.getVirtualAppliance(vdcId, vappId);

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
     * @param foreceEnterpriseSoftLimits Do we should take care of the soft limits?
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

        logger
            .debug("Allocating with force enterpise  soft limits : " + foreceEnterpriseSoftLimits);
        /*
         * Select a machine to allocate the virtual machine, Check limits, Check resources If one of
         * the above fail we cannot allocate the VirtualMachine
         */
        vmAllocatorService.allocateVirtualMachine(vmId, vappId, foreceEnterpriseSoftLimits);
        vmAllocatorService.updateVirtualMachineUse(vappId, virtualMachine);
        logger.debug("Allocated!");

        logger.debug("Check remote services");
        // The remote services must be up for this Datacenter if we are to deploy
        checkRemoteServicesByVirtualDatacenter(vdcId);
        logger.debug("Remote services are ok!");

        logger.debug("Mapping the external volumes");
        // We need to map all attached volumes if any
        initiatorMappings(virtualMachine);
        logger.debug("Mapping done!");

        // This code must be here because if the execution fails beyond the VMS subscribe we need to
        // unsuscribe the machine
        TarantinoRequestProducer producer = null;

        logger.debug("Registering the machine VSM");
        // In order to be aware of the messages from the hypervisors we need to subscribe to VSM
        VirtualDatacenter virtualDatacenter = vdcService.getVirtualDatacenter(vdcId);
        RemoteService vsmRS = findRemoteServiceWithTypeInDatacenter(virtualDatacenter);
        try
        {
            machineService.getVsm().subscribe(vsmRS, virtualMachine);
            logger.debug("Machine registered!");

            logger.debug("Creating the DatacenterTask");

            // A datacenter task is a set of jobs and datacenter task. This is, the deploy of a
            // VirtualMachine is the definition of the VirtualMachine and the job, power on
            DatacenterTasks deployTask = new DatacenterTasks();

            // Tasks needs the definition of the virtual machine
            VirtualAppliance virtualAppliance = vappService.getVirtualAppliance(vdcId, vappId);
            VirtualMachineDescriptionBuilder vmDesc =
                createVirtualMachineDefinitionBuilder(virtualMachine, virtualDatacenter,
                    virtualAppliance);

            // The id identifies this job and is neede to create the ids of the items. It is
            // hyerarchic
            // so Task 1 and its job would be 1.1, another 1.2
            deployTask.setId(virtualMachine.getUuid());

            logger.debug("Configure the hypervisor connection");
            // Hypervisor connection related configuration
            HypervisorConnection hypervisorConnection =
                hypervisorConnectionConfiguration(virtualMachine);
            logger.debug("Hypervisor connection configuration done");

            logger.debug("Configuration job");
            ApplyVirtualMachineStateOp configJob =
                configureJobConfiguration(virtualMachine, deployTask, vmDesc, hypervisorConnection);
            logger.debug("Configuration job done with id {}", configJob.getId());

            logger.debug("Apply state job");
            ApplyVirtualMachineStateOp stateJob =
                applyStateVirtualMachineConfiguration(virtualMachine, deployTask, vmDesc,
                    hypervisorConnection, VirtualMachineStateTransition.POWERON);
            logger.debug("Apply state job done with id {}", stateJob.getId());

            // The jobs are to be rolled back
            deployTask.setDependent(Boolean.TRUE);
            deployTask.getJobs().add(configJob);
            deployTask.getJobs().add(stateJob);

            producer = new TarantinoRequestProducer(virtualDatacenter.getDatacenter().getName());
            logger.trace("Deploying of the virtual machine id {} to tarantino: open channel",
                virtualMachine.getId());
            producer.openChannel();
            logger.trace(
                "Deploying of the virtual machine id {} to tarantino: channel open sucessfully",
                virtualMachine.getId());
            logger.trace("Deploying of the virtual machine id {} to tarantino: publishing to amqp",
                virtualMachine.getId());
            producer.publish(deployTask);
            logger.trace(
                "Deploying of the virtual machine id {} to tarantino: published successfully",
                virtualMachine.getId());

            logger.info("Deploying of the virtual machine id {} in tarantino!",
                virtualMachine.getId());
            tracer.log(SeverityType.INFO, ComponentType.VIRTUAL_MACHINE, EventType.VM_DEPLOY,
                "Deploy of the virtual machine with name " + virtualMachine.getName()
                    + " enqueued successfully!");
            // For the Admin to know all errors
            tracer.systemLog(SeverityType.INFO, ComponentType.VIRTUAL_MACHINE, EventType.VM_DEPLOY,
                "The enqueuing in Tarantino was OK.");
            logger.info("Generating the link to the status! {}", virtualMachine.getId());

        }
        catch (Exception e)
        {

            logger.error("Error enqueuing the deploy task dto to Tarantino with error: "
                + e.getMessage());

            tracer.log(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE, EventType.VM_DEPLOY,
                APIError.GENERIC_OPERATION_ERROR.getMessage());

            // For the Admin to know all errors
            tracer
                .systemLog(
                    SeverityType.CRITICAL,
                    ComponentType.VIRTUAL_MACHINE,
                    EventType.VM_DEPLOY,
                    "The enqueuing in Tarantino failed. Rabbitmq might be down or not configured. The error message was "
                        + e.getMessage());

            // We need to unsuscribe the machine
            logger.debug("Error enqueuing the deploy task dto to Tarantino with error: "
                + e.getMessage() + " unmonitoring the machine: " + virtualMachine.getName());
            machineService.getVsm().unsubscribe(vsmRS, virtualMachine);

            // There is no point in continue
            addNotFoundErrors(APIError.GENERIC_OPERATION_ERROR);
            flushErrors();
        }
        finally
        {
            closeProducerChannel(producer);
        }
        return "link";
    }

    private RemoteService findRemoteServiceWithTypeInDatacenter(
        final VirtualDatacenter virtualDatacenter)
    {
        List<RemoteService> services =
            infRep.findRemoteServiceWithTypeInDatacenter(virtualDatacenter.getDatacenter(),
                RemoteServiceType.VIRTUAL_SYSTEM_MONITOR);
        RemoteService vsmRS = null;
        if (!services.isEmpty())
        {
            // Only one remote service of each type by datacenter.
            vsmRS = services.get(0);
        }
        else
        {
            addNotFoundErrors(APIError.NON_EXISTENT_REMOTE_SERVICE_TYPE);
            flushErrors();
        }
        return vsmRS;
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
     * Documented in Abiquo EE.
     * 
     * @param virtualMachine
     * @param vmDesc void
     */
    protected void secondaryScsiDefinition(final VirtualMachine virtualMachine,
        final VirtualMachineDescriptionBuilder vmDesc)
    {
        // PREMIUM
        logger.debug("auxDiscsDefinition community implementation");
    }

    /**
     * Add the secondary hard disks.
     * 
     * @param virtualMachine virtual machine object.
     * @param vmDesc definition to send.
     */
    protected void secondaryHardDisksDefinition(final VirtualMachine virtualMachine,
        final VirtualMachineDescriptionBuilder vmDesc)
    {
        List<DiskManagement> hardDisks = storageRep.findHardDisksByVirtualMachine(virtualMachine);
        String datastore =
            FilenameUtils.concat(virtualMachine.getDatastore().getRootPath(), virtualMachine
                .getDatastore().getDirectory());

        DiskControllerType cntrlType =
            getDiskController(virtualMachine.getHypervisor().getType(), false, false);

        Integer sequence = 1;

        for (DiskManagement imHard : hardDisks)
        {
            vmDesc.addSecondaryHardDisk(imHard.getSizeInMb() * 1048576, sequence, datastore,
                cntrlType);

            sequence++;
        }
    }

    private ApplyVirtualMachineStateOp applyStateVirtualMachineConfiguration(
        final VirtualMachine virtualMachine, final DatacenterTasks deployTask,
        final VirtualMachineDescriptionBuilder vmDesc,
        final HypervisorConnection hypervisorConnection,
        final VirtualMachineStateTransition stateTransition)
    {
        ApplyVirtualMachineStateOp stateJob = new ApplyVirtualMachineStateOp();
        stateJob.setVirtualMachine(vmDesc.build(virtualMachine.getUuid()));
        stateJob.setHypervisorConnection(hypervisorConnection);
        stateJob.setTransaction(com.abiquo.commons.amqp.impl.tarantino.domain.StateTransition
            .fromValue(stateTransition.name()));
        stateJob.setId(deployTask.getId() + "." + virtualMachine.getUuid());
        return stateJob;
    }

    private ApplyVirtualMachineStateOp configureJobConfiguration(
        final VirtualMachine virtualMachine, final DatacenterTasks deployTask,
        final VirtualMachineDescriptionBuilder vmDesc,
        final HypervisorConnection hypervisorConnection)
    {
        ApplyVirtualMachineStateOp configJob = new ApplyVirtualMachineStateOp();
        configJob.setVirtualMachine(vmDesc.build(virtualMachine.getUuid()));
        configJob.setHypervisorConnection(hypervisorConnection);
        configJob.setTransaction(StateTransition.CONFIGURE);
        configJob.setId(deployTask.getId() + "." + virtualMachine.getUuid() + "configure");
        return configJob;
    }

    private ReconfigureVirtualMachineOp reconfigureJobConfiguration(
        final VirtualMachine virtualMachine, final VirtualMachine newVirtualMachine,
        final DatacenterTasks deployTask, final VirtualMachineDescriptionBuilder vmDesc,
        final VirtualMachineDescriptionBuilder newVmDesc,
        final HypervisorConnection hypervisorConnection)
    {
        ReconfigureVirtualMachineOp reconfigJob = new ReconfigureVirtualMachineOp();
        reconfigJob.setVirtualMachine(vmDesc.build(virtualMachine.getUuid()));
        reconfigJob.setHypervisorConnection(hypervisorConnection);
        reconfigJob.setNewVirtualMachine(newVmDesc.build(newVirtualMachine.getUuid()));
        reconfigJob.setId(deployTask.getId() + "." + virtualMachine.getUuid() + "configure");
        return reconfigJob;
    }

    public HypervisorConnection hypervisorConnectionConfiguration(
        final VirtualMachine virtualMachine)
    {
        HypervisorConnection hypervisorConnection = new HypervisorConnection();
        hypervisorConnection.setHypervisorType(HypervisorConnection.HypervisorType
            .valueOf(virtualMachine.getHypervisor().getType().name()));
        // XXX Dummy implementation
        // hypervisorConnection.setHypervisorType(HypervisorConnection.HypervisorType.TEST);
        hypervisorConnection.setIp(virtualMachine.getHypervisor().getIp());
        hypervisorConnection.setLoginPassword(virtualMachine.getHypervisor().getPassword());
        hypervisorConnection.setLoginUser(virtualMachine.getHypervisor().getUser());
        return hypervisorConnection;
    }

    /**
     * In community there are no statful image. If some {@link VirtualImageConversion} attached use
     * his properties when defining the {@link PrimaryDisk}, else use the {@link VirtualImage}
     * 
     * @param virtualMachine
     * @param vmDesc
     * @param idDatacenter void
     */
    protected void primaryDiskDefinitionConfiguration(final VirtualMachine virtualMachine,
        final VirtualMachineDescriptionBuilder vmDesc, final Integer idDatacenter)
    {
        String datastore =
            virtualMachine.getDatastore().getRootPath()
                + virtualMachine.getDatastore().getDirectory();

        // Repository Manager address
        List<RemoteService> services =
            infRep.findRemoteServiceWithTypeInDatacenter(infRep.findById(idDatacenter),
                RemoteServiceType.APPLIANCE_MANAGER);
        RemoteService repositoryManager = null;
        if (!services.isEmpty())
        {
            // Only one remote service of each type by datacenter.
            repositoryManager = services.get(0);
        }
        else
        {
            addNotFoundErrors(APIError.NON_EXISTENT_REMOTE_SERVICE_TYPE);
            flushErrors();
        }

        final VirtualImage vimage = virtualMachine.getVirtualImage();
        final HypervisorType htype = virtualMachine.getHypervisor().getType();

        final VirtualImageConversion conversion = virtualMachine.getVirtualImageConversion();

        final DiskFormatType format =
            conversion != null ? conversion.getTargetType() : vimage.getDiskFormatType();
        final Long size = conversion != null ? conversion.getSize() : vimage.getDiskFileSize();
        final String path = conversion != null ? conversion.getTargetPath() : vimage.getPath();
        final DiskControllerType cntrlType = getDiskController(htype, true, false);

        if (cntrlType != null && cntrlType == DiskControllerType.SCSI
            && format == DiskFormatType.VMDK_SPARSE)
        {
            addConflictErrors(APIError.VIRTUAL_MACHINE_ESXI_INCOMPATIBLE_DISK_CONTROLLER);
            flushErrors();
        }

        vmDesc.primaryDisk(DiskDescription.DiskFormatType.valueOf(format.name()), size,
            virtualMachine.getVirtualImage().getRepository().getUrl(), path, datastore,
            repositoryManager.getUri(), cntrlType);
    }

    /**
     * Only ESXi. Else return null.
     * <p>
     * Reads the ''abiquo.esxi.diskController'' properties or use the default: IDE for
     * non-persistent primary disks and SCSI for aux disk and persistent primary.
     * 
     * @param hypervisorType, the target hypervisor type
     * @param isPrimary, primary or secondary disk being added.
     * @param aux disks always isStateful
     */
    protected DiskControllerType getDiskController(final HypervisorType hypervisorType,
        final boolean isPrimary, final boolean isStateful)
    {

        if (hypervisorType != HypervisorType.VMX_04)
        {
            return null;
        }
        else
        {
            final String primaryOrSecondary = isPrimary ? "primary" : "secondary";

            final String controllerProperty =
                System.getProperty("abiquo.diskController." + primaryOrSecondary);

            if (!StringUtils.isEmpty(controllerProperty))
            {
                try
                {
                    return DiskControllerType.valueOf(controllerProperty.toUpperCase());
                }
                catch (Exception e)
                {
                    logger.error("Invalid ''abiquo.diskController.{}'' property,"
                        + "should use IDE/SCSI, but is {}", primaryOrSecondary, controllerProperty);
                }
            }

            if (isStateful)
            {
                return DiskControllerType.SCSI;
            }
            else
            {
                return DiskControllerType.IDE;
            }
        }
    }

    private void vnicDefinitionConfiguration(final VirtualMachine virtualMachine,
        final VirtualMachineDescriptionBuilder vmDesc)
    {
        List<IpPoolManagement> ipPoolManagementByMachine =
            vdcRepo.findIpsByVirtualMachine(virtualMachine);

        for (IpPoolManagement i : ipPoolManagementByMachine)
        {
            if (i.getConfigureGateway())
            {
                // This interface is the one that configures the Network parameters.
                // We force the forward mode to BRIDGED
                logger.debug("Network configuration with gateway");

                NetworkConfiguration configuration = i.getVlanNetwork().getConfiguration();
                vmDesc.addNetwork(i.getMac(), i.getIp(), virtualMachine.getHypervisor()
                    .getMachine().getVirtualSwitch(), i.getNetworkName(), i.getVlanNetwork()
                    .getTag() == null ? 0 : i.getVlanNetwork().getTag(), i.getName(), configuration
                    .getFenceMode(), configuration.getAddress(), configuration.getGateway(),
                    configuration.getNetMask(), configuration.getPrimaryDNS(), configuration
                        .getSecondaryDNS(), configuration.getSufixDNS(), Integer.valueOf(i
                        .getRasd().getConfigurationName()));
                continue;
            }
            logger.debug("Network configuration without gateway");
            // Only the data not related to the network since this data is configured based on the
            // configureNetwork parameter
            vmDesc.addNetwork(i.getMac(), i.getIp(), virtualMachine.getHypervisor().getMachine()
                .getVirtualSwitch(), i.getNetworkName(), i.getVlanNetwork().getTag(), i.getName(),
                null, null, null, null, null, null, null,
                Integer.valueOf(i.getRasd().getConfigurationName()));
        }
    }

    protected void bootstrapConfiguration(final VirtualMachine virtualMachine,
        final VirtualMachineDescriptionBuilder vmDesc, final VirtualDatacenter virtualDatacenter,
        final VirtualAppliance virtualAppliance)
    {
        // PREMIUM
        logger.debug("bootstrap community implementation");
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
     * </ul>
     * 
     * @param virtualMachine with a state void
     */
    private void checkVirtualMachineStateAllowsReconfigure(final VirtualMachine virtualMachine)
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

        logger.debug("Deallocating");
        // Free the resources and recalculate.
        vmAllocatorService.deallocateVirtualMachine(vmId);
        vmAllocatorService.updateVirtualMachineUse(vappId, virtualMachine);
        logger.debug("Deallocated!");

        logger.debug("Check remote services");
        // The remote services must be up for this Datacenter if we are to deploy
        checkRemoteServicesByVirtualDatacenter(vdcId);
        logger.debug("Remote services are ok!");

        VirtualDatacenter virtualDatacenter = vdcService.getVirtualDatacenter(vdcId);
        VirtualAppliance virtualAppliance = vappService.getVirtualAppliance(vdcId, vappId);

        DatacenterTasks deployTask = new DatacenterTasks();

        // Tasks needs the definition of the virtual machine
        VirtualMachineDescriptionBuilder vmDesc =
            createVirtualMachineDefinitionBuilder(virtualMachine, virtualDatacenter,
                virtualAppliance);

        // The id identifies this job and is neede to create the ids of the items. It is hyerarchic
        // so Task 1 and its job would be 1.1, another 1.2
        deployTask.setId(virtualMachine.getUuid());

        logger.debug("Configure the hypervisor connection");
        // Hypervisor connection related configuration
        HypervisorConnection hypervisorConnection =
            hypervisorConnectionConfiguration(virtualMachine);
        logger.debug("Hypervisor connection configuration done");

        logger.debug("Apply state job");
        ApplyVirtualMachineStateOp powerOffJob =
            applyStateVirtualMachineConfiguration(virtualMachine, deployTask, vmDesc,
                hypervisorConnection, VirtualMachineStateTransition.POWEROFF);
        logger.debug("Apply state job done with id {}", powerOffJob.getId());

        logger.debug("Apply state job");
        ApplyVirtualMachineStateOp stateJob =
            applyStateVirtualMachineConfiguration(virtualMachine, deployTask, vmDesc,
                hypervisorConnection, VirtualMachineStateTransition.DECONFIGURE);
        logger.debug("Apply state job done with id {}", stateJob.getId());

        // The jobs are to be rolled back
        deployTask.setDependent(Boolean.TRUE);
        deployTask.getJobs().add(powerOffJob);
        deployTask.getJobs().add(stateJob);

        TarantinoRequestProducer producer =
            new TarantinoRequestProducer(virtualDatacenter.getDatacenter().getName());

        try
        {
            logger.trace("Deploying of the virtual machine id {} to tarantino: open channel",
                virtualMachine.getId());
            producer.openChannel();
            logger.trace(
                "Deploying of the virtual machine id {} to tarantino: channel opened successfully",
                virtualMachine.getId());
            logger.trace("Deploying of the virtual machine id {} to tarantino: publishing to amqp",
                virtualMachine.getId());
            producer.publish(deployTask);
            logger.trace(
                "Deploying of the virtual machine id {} to tarantino: published successfully",
                virtualMachine.getId());
        }
        catch (Exception e)
        {

            logger.error("Error enqueuing the deploy task dto to Tarantino with error: "
                + e.getMessage());
            tracer.log(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE, EventType.VM_UNDEPLOY,
                APIError.GENERIC_OPERATION_ERROR.getMessage());
            // For the Admin to know all errors
            tracer
                .systemLog(
                    SeverityType.CRITICAL,
                    ComponentType.VIRTUAL_MACHINE,
                    EventType.VM_UNDEPLOY,
                    "The enqueuing in Tarantino failed. Rabbitmq might be down or not configured. The error message was "
                        + e.getMessage());

            // There is no point in continue
            addNotFoundErrors(APIError.GENERIC_OPERATION_ERROR);
            flushErrors();
        }
        finally
        {
            closeProducerChannel(producer);
        }

        logger.info("Undeploying of the virtual machine id {} in tarantino!",
            virtualMachine.getId());
        tracer.log(SeverityType.INFO, ComponentType.VIRTUAL_MACHINE, EventType.VM_UNDEPLOY,
            "Undeploy of the virtual machine with name " + virtualMachine.getName()
                + " enqueued successfully!");
        // For the Admin to know all errors
        tracer.systemLog(SeverityType.INFO, ComponentType.VIRTUAL_MACHINE, EventType.VM_UNDEPLOY,
            "The enqueuing in Tarantino was OK.");

        // The machine is in an appropriate state and tarantino has done the undeploy, now we have
        // to leave the DB in an appropriate state
        undeployInDb(virtualMachine);

        return "link";
    }

    private void undeployInDb(final VirtualMachine virtualMachine)
    {
        logger.debug("The virtual machine state to NOT_DEPLOYED");
        virtualMachine.setState(VirtualMachineState.ALLOCATED);
        // Hypervisor == null in order to delete the relation between
        // virtualMachine
        // and physicalMachine
        virtualMachine.setHypervisor(null);
        // Datastore == null in order to delete the relation virtualmachine
        // datastore
        virtualMachine.setDatastore(null);

        repo.update(virtualMachine);
        logger.debug("The state is valid  undeploy");
    }

    /**
     * Resumes a {@link VirtualMachine}. This involves some steps. <br>
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
     * @param foreceEnterpriseSoftLimits Do we should take care of the soft limits?
     * @param restBuilder injected restbuilder context parameter
     * @throws Exception
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void resumeVirtualMachine(final Integer vmId, final Integer vappId, final Integer vdcId,
        final Boolean foreceEnterpriseSoftLimits)
    {
        logger.debug("Starting the deploy of the virtual machine {}", vmId);
        // We need to operate with concrete and this also check that the VirtualMachine belongs to
        // those VirtualAppliance and VirtualDatacenter
        VirtualMachine virtualMachine = getVirtualMachine(vdcId, vappId, vmId);

        logger.debug("Check for permissions");
        // The user must have the proper permission
        userService.checkCurrentEnterpriseForPostMethods(virtualMachine.getEnterprise());
        logger.debug("Permission granted");

        logger
            .debug("Checking the virtual machine state. It must be in either NOT_ALLOCATED or NOT_DEPLOYED");
        // If the machine is already allocated we did compute its resources consume before, now
        // we've been doubling it
        checkVirtualMachineStateAllowsDeploy(virtualMachine);
        logger.debug("The state is valid for deploy");

        logger
            .debug("Allocating with force enterpise  soft limits : " + foreceEnterpriseSoftLimits);
        /*
         * Select a machine to allocate the virtual machine, Check limits, Check resources If one of
         * the above fail we cannot allocate the VirtualMachine
         */
        vmAllocatorService.allocateVirtualMachine(vmId, vappId, foreceEnterpriseSoftLimits);
        vmAllocatorService.updateVirtualMachineUse(vappId, virtualMachine);
        logger.debug("Allocated!");

        logger.debug("Check remote services");
        // The remote services must be up for this Datacenter if we are to deploy
        checkRemoteServicesByVirtualDatacenter(vdcId);
        logger.debug("Remote services are ok!");

        logger.debug("Registering the machine VSM");
        // In order to be aware of the messages from the hypervisors we need to subscribe to VSM
        VirtualDatacenter virtualDatacenter = vdcService.getVirtualDatacenter(vdcId);
        RemoteService vsmRS = findRemoteServiceWithTypeInDatacenter(virtualDatacenter);
        machineService.getVsm().unsubscribe(vsmRS, virtualMachine);
        logger.debug("Machine registered!");

        logger.debug("Creating the DatacenterTask");

        // A datacenter task is a set of jobs and datacenter task. This is, the deploy of a
        // VirtualMachine is the definition of the VirtualMachine and the job, power on
        DatacenterTasks deployTask = new DatacenterTasks();

        // The id identifies this job and is neede to create the ids of the items. It is hyerarchic
        // so Task 1 and its job would be 1.1, another 1.2
        deployTask.setId(virtualMachine.getUuid());

        // Tasks needs the definition of the virtual machine
        VirtualAppliance virtualAppliance = vappService.getVirtualAppliance(vdcId, vappId);
        VirtualMachineDescriptionBuilder vmDesc =
            createVirtualMachineDefinitionBuilder(virtualMachine, virtualDatacenter,
                virtualAppliance);

        logger.debug("Configure the hypervisor connection");
        // Hypervisor connection related configuration
        HypervisorConnection hypervisorConnection =
            hypervisorConnectionConfiguration(virtualMachine);
        logger.debug("Hypervisor connection configuration done");

        logger.debug("Apply state job");
        ApplyVirtualMachineStateOp stateJob =
            applyStateVirtualMachineConfiguration(virtualMachine, deployTask, vmDesc,
                hypervisorConnection, VirtualMachineStateTransition.RESUME);
        logger.debug("Apply state job done with id {}", stateJob.getId());

        // The jobs are to be rolled back
        deployTask.setDependent(Boolean.TRUE);
        deployTask.getJobs().add(stateJob);

        TarantinoRequestProducer producer =
            new TarantinoRequestProducer(virtualDatacenter.getDatacenter().getName());

        try
        {
            logger.trace("Deploying of the virtual machine id {} to tarantino: open channel",
                virtualMachine.getId());
            producer.openChannel();
            logger.trace("Deploying of the virtual machine id {} to tarantino: channel opened",
                virtualMachine.getId());
            logger.trace("Deploying of the virtual machine id {} to tarantino: published to amqp",
                virtualMachine.getId());
            producer.publish(deployTask);
            logger.trace(
                "Deploying of the virtual machine id {} to tarantino: published successfully",
                virtualMachine.getId());
        }
        catch (Exception e)
        {

            logger.error("Error enqueuing the deploy task dto to Tarantino with error: "
                + e.getMessage());
            tracer.log(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE, EventType.VM_DEPLOY,
                APIError.GENERIC_OPERATION_ERROR.getMessage());

            // For the Admin to know all errors
            tracer
                .systemLog(
                    SeverityType.CRITICAL,
                    ComponentType.VIRTUAL_MACHINE,
                    EventType.VM_DEPLOY,
                    "The enqueuing in Tarantino failed. Rabbitmq might be down or not configured. The error message was "
                        + e.getMessage());

            // There is no point in continue
            addNotFoundErrors(APIError.GENERIC_OPERATION_ERROR);
            flushErrors();
        }
        finally
        {
            closeProducerChannel(producer);
        }
        logger.info("Deploying of the virtual machine id {} in tarantino!", virtualMachine.getId());
        tracer.log(SeverityType.INFO, ComponentType.VIRTUAL_MACHINE, EventType.VM_DEPLOY,
            "Deploy of the virtual machine with name " + virtualMachine.getName()
                + " enqueued successfully!");
        // For the Admin to know all errors
        tracer.systemLog(SeverityType.INFO, ComponentType.VIRTUAL_MACHINE, EventType.VM_DEPLOY,
            "The enqueuing in Tarantino was OK.");
    }

    /**
     * Creates a {@link VirtualMachineDescriptionBuilder}.
     * 
     * @param virtualMachine to mount definition.
     * @param virtualDatacenter additional data.
     * @return VirtualMachineDescriptionBuilder
     */
    public VirtualMachineDescriptionBuilder createVirtualMachineDefinitionBuilder(
        final VirtualMachine virtualMachine, final VirtualDatacenter virtualDatacenter,
        final VirtualAppliance virtualAppliance)
    {
        VirtualMachineDescriptionBuilder vmDesc = new VirtualMachineDescriptionBuilder();
        logger.debug("Creating disk information");
        // Disk related configuration
        primaryDiskDefinitionConfiguration(virtualMachine, vmDesc, virtualDatacenter
            .getDatacenter().getId());
        logger.debug("Disk information created!");
        vmDesc.hardware(virtualMachine.getCpu(), virtualMachine.getRam());

        vmDesc.setRdPort(virtualMachine.getVdrpPort());

        logger.debug("Creating the network related configuration");
        // Network related configuration
        vnicDefinitionConfiguration(virtualMachine, vmDesc);
        logger.debug("Network configuration done!");

        logger.debug("Creating the bootstrap configuration");
        bootstrapConfiguration(virtualMachine, vmDesc, virtualDatacenter, virtualAppliance);
        logger.debug("Bootstrap configuration done!");

        logger.debug("Configure secondary iSCSI volumes");
        secondaryScsiDefinition(virtualMachine, vmDesc);
        logger.debug("Configure secondary iSCSI done!");

        logger.debug("Configure secondary Hard Disks");
        secondaryHardDisksDefinition(virtualMachine, vmDesc);
        logger.debug("Configure secondary Hard Disks done!");

        return vmDesc;
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
        if (VirtualMachineState.NOT_ALLOCATED.equals(virtualMachine.getState()))
        {
            addConflictErrors(APIError.VIRTUAL_MACHINE_UNALLOCATED_STATE);
            flushErrors();
        }

        // TODO revisar
        checkPauseAllowed(virtualMachine, state);
        // This returns the StateTransition or error
        VirtualMachineStateTransition validMachineStateChange =
            validMachineStateChange(virtualMachine, state);

        blockVirtualMachine(virtualMachine);

        VirtualDatacenter virtualDatacenter = vdcService.getVirtualDatacenter(vdcId);
        VirtualAppliance virtualAppliance = vappService.getVirtualAppliance(vdcId, vappId);
        VirtualMachineDescriptionBuilder machineDescriptionBuilder =
            createVirtualMachineDefinitionBuilder(virtualMachine, virtualDatacenter,
                virtualAppliance);
        DatacenterTasks deployTask = new DatacenterTasks();

        // The id identifies this job and is neede to create the ids of the items. It is
        // hyerarchic
        // so Task 1 and its job would be 1.1, another 1.2
        deployTask.setId(virtualMachine.getUuid());

        logger.debug("Configure the hypervisor connection");
        // Hypervisor connection related configuration
        HypervisorConnection hypervisorConnection =
            hypervisorConnectionConfiguration(virtualMachine);
        logger.debug("Hypervisor connection configuration done");

        logger.debug("Apply state job");
        ApplyVirtualMachineStateOp stateJob =
            applyStateVirtualMachineConfiguration(virtualMachine, deployTask,
                machineDescriptionBuilder, hypervisorConnection, validMachineStateChange);
        logger.debug("Apply state job done with id {}", stateJob.getId());

        // The jobs are to be rolled back
        deployTask.setDependent(Boolean.TRUE);
        deployTask.getJobs().add(stateJob);

        TarantinoRequestProducer producer =
            new TarantinoRequestProducer(virtualDatacenter.getDatacenter().getName());

        try
        {
            logger.trace("Deploying of the virtual machine id {} to tarantino: open channel",
                virtualMachine.getId());
            producer.openChannel();
            logger.trace("Deploying of the virtual machine id {} to tarantino: channel opened",
                virtualMachine.getId());
            logger.trace("Deploying of the virtual machine id {} to tarantino: publishing to amqp",
                virtualMachine.getId());
            producer.publish(deployTask);
            logger.trace(
                "Deploying of the virtual machine id {} to tarantino: published successfully",
                virtualMachine.getId());
        }
        catch (Exception e)
        {

            logger.error("Error enqueuing the change state task dto to Tarantino with error: "
                + e.getMessage());
            tracer.log(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE, EventType.VM_STATE,
                APIError.GENERIC_OPERATION_ERROR.getMessage());
            // For the Admin to know all errors
            tracer
                .systemLog(
                    SeverityType.CRITICAL,
                    ComponentType.VIRTUAL_MACHINE,
                    EventType.VM_STATE,
                    "The enqueuing in Tarantino failed. Rabbitmq might be down or not configured. The error message was "
                        + e.getMessage());

            // There is no point in continue
            addNotFoundErrors(APIError.GENERIC_OPERATION_ERROR);
            flushErrors();
        }
        finally
        {
            closeProducerChannel(producer);
        }

        logger.info("Applying the new state of the virtual machine id {} in tarantino!",
            virtualMachine.getId());
        tracer.log(SeverityType.INFO, ComponentType.VIRTUAL_MACHINE, EventType.VM_STATE,
            "Applying the state of the virtual machine with name " + virtualMachine.getName()
                + " enqueued successfully!");
        // For the Admin to know all errors
        tracer.systemLog(SeverityType.INFO, ComponentType.VIRTUAL_MACHINE, EventType.VM_STATE,
            "The enqueuing in Tarantino was OK.");

        // Here we add the url which contains the status
        return "link";
    }

    /**
     * Checks one by one all {@link RemoteService} associated with the @{link Datacenter}.
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
                InfrastructureService.checkRemoteServiceStatus(r.getType(), r.getUri());
            errors.addAll(checkRemoteServiceStatus);
        }

        return errors;
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public NodeVirtualImage getNodeVirtualImage(final Integer vdcId, final Integer vappId,
        final Integer vmId)
    {
        VirtualMachine vm = repo.findVirtualMachineById(vmId);

        VirtualAppliance vapp = vappService.getVirtualAppliance(vdcId, vappId);

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
        VirtualAppliance vapp = vappService.getVirtualAppliance(vdcId, vappId);

        if (vapp == null)
        {
            logger.error("Error retrieving the virtual appliance: {} does not exist", vappId);
            addNotFoundErrors(APIError.NON_EXISTENT_VIRTUALAPPLIANCE);
            flushErrors();
        }
        logger.debug("virtual appliance {} found", vappId);

        return vapp.getNodes();
    }
}
