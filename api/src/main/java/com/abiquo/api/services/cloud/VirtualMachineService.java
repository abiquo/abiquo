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
import com.abiquo.api.services.UserService;
import com.abiquo.api.services.VirtualMachineAllocatorService;
import com.abiquo.api.services.ovf.OVFGeneratorService;
import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.model.transport.error.ErrorDto;
import com.abiquo.model.transport.error.ErrorsDto;
import com.abiquo.ovfmanager.ovf.xml.OVFSerializer;
import com.abiquo.server.core.cloud.Hypervisor;
import com.abiquo.server.core.cloud.NodeVirtualImage;
import com.abiquo.server.core.cloud.NodeVirtualImageDAO;
import com.abiquo.server.core.cloud.State;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualImage;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.VirtualMachineDto;
import com.abiquo.server.core.cloud.VirtualMachineRep;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.User;
import com.abiquo.server.core.infrastructure.RemoteService;
import com.abiquo.server.core.infrastructure.network.Network;
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
    protected VirtualApplianceService vappService;

    @Autowired
    protected InfrastructureService infrastructureService;

    @Autowired
    protected OVFGeneratorService ovfService;

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

    /** The logger object **/
    private final static Logger logger = LoggerFactory.getLogger(VirtualMachineService.class);

    public VirtualMachineService()
    {

    }

    public VirtualMachineService(final EntityManager em)
    {
        this.repo = new VirtualMachineRep(em);
        this.vappService = new VirtualApplianceService(em);
        this.userService = new UserService(em);
        this.infrastructureService = new InfrastructureService(em);
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

    /**
     * Block the virtual by changing its state to IN_PROGRESS
     * 
     * @param vm VirtualMachine to be blocked
     */
    public void blockVirtualMachine(final VirtualMachine vm)
    {
        if (vm.getState() == State.IN_PROGRESS)
        {
            addConflictErrors(APIError.VIRTUAL_MACHINE_ALREADY_IN_PROGRESS);
            flushErrors();
        }

        vm.setState(State.IN_PROGRESS);
        updateVirtualMachine(vm);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void validMachineStateChange(final State oldState, final State newState)
    {
        if (oldState == State.NOT_DEPLOYED)
        {
            addConflictErrors(APIError.VIRTUAL_MACHINE_NOT_DEPLOYED);
            flushErrors();
        }
        if (oldState == State.POWERED_OFF && newState != State.RUNNING || oldState == State.PAUSED
            && newState != State.REBOOTED || oldState == State.RUNNING
            && newState == State.REBOOTED)
        {
            addConflictErrors(APIError.VIRTUAL_MACHINE_STATE_CHANGE_ERROR);
            flushErrors();
        }
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
        final Integer vdcId, final State state)
    {
        VirtualMachine vm = getVirtualMachine(vdcId, vappId, vmId);
        userService.checkCurrentEnterpriseForPostMethods(vm.getEnterprise());
        // The change state applies on the hypervisor. Now there is a NOT_ALLOCATED to get rid of
        // the if(!hypervisor)
        if (State.NOT_ALLOCATED.equals(vm.getState()))
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

        State old = vm.getState();

        validMachineStateChange(old, state);

        blockVirtualMachine(vm);

        try
        {
            Integer datacenterId = vm.getHypervisor().getMachine().getDatacenter().getId();

            VirtualAppliance vapp = contanerVirtualAppliance(vm);
            EnvelopeType envelop = ovfService.createVirtualApplication(vapp);

            Document docEnvelope = OVFSerializer.getInstance().bindToDocument(envelop, false);

            RemoteService vf =
                infrastructureService.getRemoteService(datacenterId,
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

    private void restoreVirtualMachineState(final VirtualMachine vm, final State old)
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
                com.abiquo.server.core.cloud.State.NOT_DEPLOYED,
                com.abiquo.server.core.cloud.State.NOT_DEPLOYED);

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
    public Boolean sameState(final VirtualMachine vm, final State state)
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

    public void checkPauseAllowed(final VirtualMachine vm, final State state)
    {
        if (vm.getHypervisor().getType() == HypervisorType.XEN_3 && state == State.PAUSED)
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

        if (!virtualMachine.getState().equals(State.NOT_DEPLOYED)
            && !virtualMachine.getState().equals(State.UNKNOWN))
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
        VirtualAppliance virtualAppliance = checkVdcVappAndPrivilege(virtualMachine, vdcId, vappId);

        // We need the VirtualImage
        VirtualImage virtualImage = repo.getVirtualImage(vImageId);
        virtualMachine.setVirtualImage(virtualImage);

        // We need the Enterprise
        Enterprise enterprise = enterpriseService.getEnterprise(enterpriseId);
        virtualMachine.setEnterprise(enterprise);

        // We check for a suitable conversion (PREMIUM)
        attachVirtualImageConversion(virtualMachine.getVirtualImage(), virtualMachine);

        // The entity that defines the relation between a virtual machine, virtual applicance and
        // virtual image is VirtualImageNode
        createNodeVirtualImage(virtualMachine, virtualAppliance);

        // At this stage the virtual machine is not associated with any hypervisor
        virtualMachine.setState(State.NOT_ALLOCATED);

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
     * @param virtualImage to prepare.
     * @param virtualMachine virtual machine to persist.
     * @return VirtualImage in premium the conversion.
     */
    public void attachVirtualImageConversion(final VirtualImage virtualImage,
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
        machineService.getVsm().monitor(virtualMachine.getHypervisor().getIpService(),
            virtualMachine.getHypervisor().getIp(), virtualMachine.getHypervisor().getPort(),
            virtualMachine.getHypervisor().getType().name(),
            virtualMachine.getHypervisor().getUser(), virtualMachine.getHypervisor().getPassword());
        logger.debug("Machine registered!");
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
            infrastructureService.checkRemoteServiceStatusByDatacenter(virtualDatacenter
                .getDatacenter().getId());
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
     * The {@link State} allowed for deploy are: <br>
     * <ul>
     * <li>NOT_ALLOCATED</li>
     * <li>NOT_DEPLOYED</li>
     * </ul>
     * 
     * @param virtualMachine with a state void
     */
    private void checkVirtualMachineStateAllowsDeploy(final VirtualMachine virtualMachine)
    {
        if (!State.NOT_ALLOCATED.equals(virtualMachine.getState())
            && !State.NOT_DEPLOYED.equals(virtualMachine.getState()))
        {
            tracer.log(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE, EventType.VM_DEPLOY,
                APIError.VIRTUAL_MACHINE_INVALID_STATE.getMessage());
            tracer.systemLog(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE,
                EventType.VM_DEPLOY, "The Virtual Machine is already deployed or Allocated.");
            addConflictErrors(APIError.VIRTUAL_MACHINE_INVALID_STATE);
            flushErrors();
        }
        logger.debug("The virtual machine is in state {}" + virtualMachine.getState().name());
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

        logger.debug("The virtual machine state to NOT_DEPLOYED");
        virtualMachine.setState(State.NOT_DEPLOYED);
        // Hypervisor == null in order to delete the relation between
        // virtualMachine
        // and physicalMachine
        Hypervisor hyper = virtualMachine.getHypervisor();
        virtualMachine.setHypervisor(null);
        // Datastore == null in order to delete the relation virtualmachine
        // datastore
        virtualMachine.setDatastore(null);
        logger.debug("The state is valid  undeploy");

        logger.debug("Deallocating");
        /*
         * Free the resources and recalculate.
         */
        vmAllocatorService.deallocateVirtualMachine(vmId);
        logger.debug("Deallocated!");

        logger.debug("Check remote services");
        // The remote services must be up for this Datacenter if we are to deploy
        checkRemoteServicesByVirtualDatacenter(vdcId);
        logger.debug("Remote services are ok!");

        logger.debug("Detaching the external volumes");
        // We need to de attached volumes if any
        detachVolumes(virtualMachine);
        logger.debug("Detach done!");

        logger.debug("Registering the machine VSM");
        // We don't need the messages from the hypervisors we need to unsubscribe to VSM
        machineService.getVsm().shutdownMonitor(hyper.getIpService(), hyper.getIp(),
            hyper.getPort());
        logger.debug("Machine registered!");
    }

    /**
     * Properly documented in Premium.
     * 
     * @param virtualMachine void
     */
    protected void detachVolumes(final VirtualMachine virtualMachine)
    {
        // PREMIUM
        logger.debug("detachVolumes community edition");
    }
}
