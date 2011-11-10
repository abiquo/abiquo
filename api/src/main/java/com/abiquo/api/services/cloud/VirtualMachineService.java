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

import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;
import org.dmtf.schemas.ovf.envelope._1.EnvelopeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.services.DefaultApiService;
import com.abiquo.api.services.EnterpriseService;
import com.abiquo.api.services.InfrastructureService;
import com.abiquo.api.services.MachineService;
import com.abiquo.api.services.NetworkService;
import com.abiquo.api.services.RemoteServiceService;
import com.abiquo.api.services.UserService;
import com.abiquo.api.services.VirtualMachineAllocatorService;
import com.abiquo.api.services.ovf.OVFGeneratorService;
import com.abiquo.commons.amqp.impl.tarantino.TarantinoRequestProducer;
import com.abiquo.commons.amqp.impl.tarantino.domain.DiskDescription;
import com.abiquo.commons.amqp.impl.tarantino.domain.HypervisorConnection;
import com.abiquo.commons.amqp.impl.tarantino.domain.StateTransition;
import com.abiquo.commons.amqp.impl.tarantino.domain.builder.VirtualMachineDescriptionBuilder;
import com.abiquo.commons.amqp.impl.tarantino.domain.dto.DatacenterTasks;
import com.abiquo.commons.amqp.impl.tarantino.domain.operations.ApplyVirtualMachineStateOp;
import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.model.transport.error.ErrorDto;
import com.abiquo.model.transport.error.ErrorsDto;
import com.abiquo.ovfmanager.ovf.xml.OVFSerializer;
import com.abiquo.server.core.cloud.Hypervisor;
import com.abiquo.server.core.cloud.NodeVirtualImage;
import com.abiquo.server.core.cloud.NodeVirtualImageDAO;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualApplianceState;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualDatacenterRep;
import com.abiquo.server.core.cloud.VirtualImage;
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
import com.abiquo.server.core.infrastructure.network.Network;
import com.abiquo.server.core.infrastructure.network.NetworkConfiguration;
import com.abiquo.server.core.infrastructure.storage.DiskManagement;
import com.abiquo.server.core.infrastructure.storage.StorageRep;
import com.abiquo.tracer.ComponentType;
import com.abiquo.tracer.EventType;
import com.abiquo.tracer.SeverityType;
import com.sun.ws.management.client.Resource;
import com.sun.ws.management.client.ResourceFactory;

@Service
@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
public class VirtualMachineService extends DefaultApiService
{
    private static final String RESOURCE_URI =
        "http://schemas.dmtf.org/ovf/envelope/1/virtualApplianceService/virtualApplianceResource";

    @Autowired
    protected VirtualMachineRep repo;

    @Autowired
    protected VirtualDatacenterRep vdcRepo;

    @Autowired
    protected VirtualApplianceService vappService;

    @Autowired
    protected OVFGeneratorService ovfService;

    @Autowired
    protected RemoteServiceService remoteServiceService;

    @Autowired
    UserService userService;

    @Autowired
    protected EnterpriseService enterpriseService;

    @Autowired
    protected VirtualMachineAllocatorService vmAllocatorService;

    @Autowired
    protected NodeVirtualImageDAO nodeVirtualImageDAO;

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

    /** The logger object **/
    private final static Logger logger = LoggerFactory.getLogger(VirtualMachineService.class);

    public VirtualMachineService()
    {

    }

    public VirtualMachineService(final EntityManager em)
    {
        this.repo = new VirtualMachineRep(em);
        this.vdcRepo = new VirtualDatacenterRep(em);
        this.vappService = new VirtualApplianceService(em);
        this.userService = new UserService(em);
        this.infRep = new InfrastructureRep(em);
        this.storageRep = new StorageRep(em);
        vdcService = new VirtualDatacenterService(em);
        this.ovfService = new OVFGeneratorService(em);
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
        VirtualMachine vm = repo.findVirtualMachineById(vmId);

        return vm;
    }

    public void addVirtualMachine(final VirtualMachine virtualMachine)
    {
        repo.insert(virtualMachine);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public VirtualMachine updateVirtualMachine(final Integer vdcId, final Integer vappId,
        final Integer vmId, final VirtualMachineDto dto)
    {
        VirtualMachine old = getVirtualMachine(vdcId, vappId, vmId);
        return updateVirtualMachineFromDto(dto, old);
    }

    private VirtualMachine updateVirtualMachineFromDto(final VirtualMachineDto dto,
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
        updateVirtualMachine(old);
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

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteNotManagedVirtualMachines(final Hypervisor hypervisor)
    {
        repo.deleteNotManagedVirtualMachines(hypervisor);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteNotManagedVirtualMachines(final Hypervisor hypervisor, final boolean trace)
    {
        this.deleteNotManagedVirtualMachines(hypervisor);

        if (trace)
        {
            tracer.log(SeverityType.INFO, ComponentType.MACHINE,
                EventType.MACHINE_DELETE_VMS_NOTMANAGED,
                "Virtual Machines not managed by host from '" + hypervisor.getIp()
                    + "' have been deleted");
        }
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
     * Changes the state of the VirtualMachine to the state passed
     * 
     * @param vappId Virtual Appliance Id
     * @param vdcId VirtualDatacenter Id
     * @param state The state to which change
     * @throws Exception
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void changeVirtualMachineState(final Integer vmId, final Integer vappId,
        final Integer vdcId, final VirtualMachineState state)
    {
        VirtualMachine vm = getVirtualMachine(vdcId, vappId, vmId);
        userService.checkCurrentEnterpriseForPostMethods(vm.getEnterprise());
        // The change state applies on the hypervisor. Now there is a NOT_ALLOCATED to get rid of
        // the if(!hypervisor)
        if (VirtualMachineState.NOT_ALLOCATED.equals(vm.getState()))
        {
            addConflictErrors(APIError.VIRTUAL_MACHINE_UNALLOCATED_STATE);
            flushErrors();
        }
        if (sameState(vm, state))
        {
            // The state is the same, we warn the user and do nothing
            return;
        }
        // TODO revisar
        checkPauseAllowed(vm, state);

        VirtualMachineState old = vm.getState();

        validMachineStateChange(vm, state);

        blockVirtualMachine(vm);

        try
        {
            Integer datacenterId = vm.getHypervisor().getMachine().getDatacenter().getId();

            VirtualAppliance vapp = contanerVirtualAppliance(vm);
            EnvelopeType envelop = ovfService.createVirtualApplication(vapp);

            Document docEnvelope = OVFSerializer.getInstance().bindToDocument(envelop, false);

            RemoteService vf =
                remoteServiceService.getRemoteService(datacenterId,
                    RemoteServiceType.VIRTUAL_FACTORY);

            long timeout = Long.valueOf(System.getProperty("abiquo.server.timeout", "0"));

            Resource resource =
                ResourceFactory.create(vf.getUri(), RESOURCE_URI, timeout, docEnvelope,
                    ResourceFactory.LATEST);

            changeState(resource, envelop, state.toResourceState());
        }
        catch (Exception e)
        {
            restoreVirtualMachineState(vm, old);
            addConflictErrors(APIError.VIRTUAL_MACHINE_REMOTE_SERVICE_ERROR);
            flushErrors();

        }

    }

    private void restoreVirtualMachineState(final VirtualMachine vm, final VirtualMachineState old)
    {
        vm.setState(old);
        updateVirtualMachine(vm);
    }

    @Deprecated
    private VirtualAppliance contanerVirtualAppliance(final VirtualMachine vmachine)
    {

        VirtualDatacenter vdc =
            new VirtualDatacenter(vmachine.getEnterprise(),
                null,
                new Network("uuid"),
                HypervisorType.VMX_04,
                "name");

        // TODO do not set VDC network
        VirtualAppliance vapp =
            new VirtualAppliance(vmachine.getEnterprise(),
                vdc,
                "haVapp",
                VirtualApplianceState.NOT_DEPLOYED);

        NodeVirtualImage nvi =
            new NodeVirtualImage("haNodeVimage", vapp, vmachine.getVirtualImage(), vmachine);

        vapp.addToNodeVirtualImages(nvi);

        return vapp;
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

    public void changeState(final Resource resource, final EnvelopeType envelope,
        final String machineState) throws Exception
    {
        EnvelopeType envelopeRunning = ovfService.changeStateVirtualMachine(envelope, machineState);
        Document docEnvelopeRunning =
            OVFSerializer.getInstance().bindToDocument(envelopeRunning, false);

        resource.put(docEnvelopeRunning);
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
            addConflictErrors(APIError.VIRTUAL_MACHINE_INVALID_STATE_DELETE);
            flushErrors();
        }
        repo.deleteVirtualMachine(virtualMachine);
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
        Enterprise enterprise = enterpriseService.getEnterprise(enterpriseId);
        virtualMachine.setEnterprise(enterprise);

        VirtualAppliance virtualAppliance = checkVdcVappAndPrivilege(virtualMachine, vdcId, vappId);

        // We need the VirtualImage
        VirtualImage virtualImage = repo.getVirtualImage(vImageId);
        virtualMachine.setVirtualImage(virtualImage);

        // We need the Enterprise

        // We check for a suitable conversion (PREMIUM)
        attachVirtualImageConversion(virtualAppliance.getVirtualDatacenter(),
            virtualMachine.getVirtualImage(), virtualMachine);

        // The entity that defines the relation between a virtual machine, virtual applicance and
        // virtual image is VirtualImageNode
        createNodeVirtualImage(virtualMachine, virtualAppliance);

        // At this stage the virtual machine is not associated with any hypervisor
        virtualMachine.setState(VirtualMachineState.NOT_ALLOCATED);

        // TODO update the virtual appliance according to the rules. As the virtual appliance state
        // is the sum (pondered) of the states of its virtual machines

        return repo.createVirtualMachine(virtualMachine);
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
        nodeVirtualImageDAO.persist(nodeVirtualImage);
        logger.debug("Node virtual image created!");
    }

    /**
     * Prepares the virtual image, in premium it sets the conversion. Attachs the conversion if
     * premium to the {@link VirtualMachine}.
     * 
     * @param virtualDatacenter from where we retrieve the hypervisor type.
     * @param virtualImage to prepare.
     * @param virtualMachine virtual machine to persist.
     * @return VirtualImage in premium the conversion.
     */
    public void attachVirtualImageConversion(final VirtualDatacenter virtualDatacenter,
        final VirtualImage virtualImage, final VirtualMachine virtualMachine)
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
    public void deployVirtualMachine(final Integer vmId, final Integer vappId, final Integer vdcId,
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

        logger.debug("Mapping the external volumes");
        // We need to map all attached volumes if any
        initiatorMappings(virtualMachine);
        logger.debug("Mapping done!");

        logger.debug("Registering the machine VSM");
        // In order to be aware of the messages from the hypervisors we need to subscribe to VSM
        VirtualDatacenter virtualDatacenter = vdcService.getVirtualDatacenter(vdcId);
        VirtualAppliance virtualAppliance = vappService.getVirtualAppliance(vdcId, vappId);
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
        machineService.getVsm().monitor(vsmRS.getUri(), virtualMachine.getHypervisor().getIp(),
            virtualMachine.getHypervisor().getPort(),
            virtualMachine.getHypervisor().getType().name(),
            virtualMachine.getHypervisor().getUser(), virtualMachine.getHypervisor().getPassword());
        logger.debug("Machine registered!");

        logger.debug("Creating the DatacenterTask");

        // A datacenter task is a set of jobs and datacenter task. This is, the deploy of a
        // VirtualMachine is the definition of the VirtualMachine and the job, power on
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

        TarantinoRequestProducer producer =
            new TarantinoRequestProducer(virtualDatacenter.getDatacenter().getName());

        try
        {
            producer.openChannel();
            producer.publish(deployTask);
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

    private void closeProducerChannel(final TarantinoRequestProducer producer)
    {
        try
        {
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
            virtualMachine.getDatastore().getRootPath()
                + virtualMachine.getDatastore().getDirectory();

        Integer sequence = 1;
        for (DiskManagement imHard : hardDisks)
        {
            vmDesc.addSecondaryHardDisk(imHard.getSizeInMb() * 1048576, sequence, datastore);
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

    private HypervisorConnection hypervisorConnectionConfiguration(
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
     * In community there are no statful image.
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
        vmDesc.primaryDisk(DiskDescription.DiskFormatType.valueOf(virtualMachine.getVirtualImage()
            .getDiskFormatType().name()), virtualMachine.getVirtualImage().getDiskFileSize(),
            virtualMachine.getVirtualImage().getRepository().getUrl(), virtualMachine
                .getVirtualImage().getPathName(), datastore, repositoryManager.getUri());
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
            case ALLOCATED:
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
    public void undeployVirtualMachine(final Integer vmId, final Integer vappId,
        final Integer vdcId, final Boolean foreceEnterpriseSoftLimits)
    {
        logger.debug("Starting the undeploy of the virtual machine {}", vmId);
        // We need to operate with concrete and this also check that the VirtualMachine belongs to
        // those VirtualAppliance and VirtualDatacenter
        VirtualMachine virtualMachine = getVirtualMachine(vdcId, vappId, vmId);

        logger.debug("Check for permissions");
        // The user must have the proper permission
        userService.checkCurrentEnterpriseForPostMethods(virtualMachine.getEnterprise());
        logger.debug("Permission granted");

        logger.debug("Checking that the virtual machine id {} is in an appropriate state",
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
            producer.openChannel();
            producer.publish(deployTask);
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
        VirtualAppliance virtualAppliance = vappService.getVirtualAppliance(vdcId, vappId);
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
        machineService.getVsm().shutdownMonitor(vsmRS.getUri(),
            virtualMachine.getHypervisor().getIp(), virtualMachine.getHypervisor().getPort());
        logger.debug("Machine registered!");

        logger.debug("Creating the DatacenterTask");

        // A datacenter task is a set of jobs and datacenter task. This is, the deploy of a
        // VirtualMachine is the definition of the VirtualMachine and the job, power on
        DatacenterTasks deployTask = new DatacenterTasks();

        // The id identifies this job and is neede to create the ids of the items. It is hyerarchic
        // so Task 1 and its job would be 1.1, another 1.2
        deployTask.setId(virtualMachine.getUuid());

        // Tasks needs the definition of the virtual machine
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
            producer.openChannel();
            producer.publish(deployTask);
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
    private VirtualMachineDescriptionBuilder createVirtualMachineDefinitionBuilder(
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
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void applyVirtualMachineState(final Integer vmId, final Integer vappId,
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
            producer.openChannel();
            producer.publish(deployTask);
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
}
