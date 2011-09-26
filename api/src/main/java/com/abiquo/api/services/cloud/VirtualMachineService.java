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

import org.dmtf.schemas.ovf.envelope._1.EnvelopeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.services.DefaultApiService;
import com.abiquo.api.services.InfrastructureService;
import com.abiquo.api.services.UserService;
import com.abiquo.api.services.ovf.OVFGeneratorService;
import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.ovfmanager.ovf.xml.OVFSerializer;
import com.abiquo.server.core.cloud.Hypervisor;
import com.abiquo.server.core.cloud.NodeVirtualImage;
import com.abiquo.server.core.cloud.State;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.VirtualMachineDto;
import com.abiquo.server.core.cloud.VirtualMachineRep;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.User;
import com.abiquo.server.core.infrastructure.RemoteService;
import com.abiquo.server.core.infrastructure.network.Network;
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
    InfrastructureService infrastructureService;

    @Autowired
    protected OVFGeneratorService ovfService;

    @Autowired
    UserService userService;

    public VirtualMachineService()
    {

    }

    public VirtualMachineService(final EntityManager em)
    {
        this.repo = new VirtualMachineRep(em);
        this.vappService = new VirtualApplianceService(em);
        this.userService = new UserService(em);
        this.infrastructureService = new InfrastructureService(em);
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
            addNotFoundErrors(APIError.NON_EXISTENT_VIRTUALMACHINE);
            flushErrors();
        }
        return vm;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public VirtualMachine updateVirtualMachine(final Integer vdcId, final Integer vappId,
        final Integer vmId, final VirtualMachineDto dto)
    {
        VirtualMachine old = getVirtualMachine(vdcId, vappId, vmId);

        old.setName(dto.getName());
        old.setDescription(dto.getDescription());
        old.setCpu(dto.getCpu());
        old.setRam(dto.getRam());
        old.setHdInBytes(dto.getHd());
        old.setHighDisponibility(dto.getHighDisponibility());

        if (dto.getPassword() != null && !dto.getPassword().equals(""))
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

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void deleteVirtualMachine(final VirtualMachine virtualMachine)
    {
        if (!virtualMachine.getState().equals(State.NOT_DEPLOYED)
            && !virtualMachine.getState().equals(State.UNKNOWN))
        {
            addConflictErrors(APIError.VIRTUAL_MACHINE_INVALID_STATE_DELETE);
            flushErrors();
        }
        repo.deleteVirtualMachine(virtualMachine);
    }
}
