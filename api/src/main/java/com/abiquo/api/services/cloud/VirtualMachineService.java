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
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.exceptions.NotFoundException;
import com.abiquo.api.exceptions.PreconditionFailedException;
import com.abiquo.api.services.RemoteServiceService;
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
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.RemoteService;
import com.abiquo.server.core.infrastructure.network.Network;
import com.sun.ws.management.client.Resource;
import com.sun.ws.management.client.ResourceFactory;

@Service
@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
public class VirtualMachineService
{
    private static final String RESOURCE_URI =
        "http://schemas.dmtf.org/ovf/envelope/1/virtualApplianceService/virtualApplianceResource";

    @Autowired
    VirtualMachineRep repo;

    @Autowired
    VirtualApplianceService vappService;

    @Autowired
    RemoteServiceService remoteService;

    @Autowired
    OVFGeneratorService ovfService;

    public VirtualMachineService()
    {

    }

    public VirtualMachineService(EntityManager em)
    {
        this.repo = new VirtualMachineRep(em);
        this.vappService = new VirtualApplianceService(em);
    }

    public Collection<VirtualMachine> findByHypervisor(Hypervisor hypervisor)
    {
        assert hypervisor != null;
        return repo.findByHypervisor(hypervisor);
    }

    public Collection<VirtualMachine> findByEnterprise(Enterprise enterprise)
    {
        assert enterprise != null;
        return repo.findByEnterprise(enterprise);
    }

    public Collection<VirtualMachine> findVirtualMachinesByUser(Enterprise enterprise, User user)
    {
        return repo.findVirtualMachinesByUser(enterprise, user);
    }

    public List<VirtualMachine> findByVirtualAppliance(VirtualAppliance vapp)
    {
        return repo.findVirtualMachinesByVirtualAppliance(vapp.getId());
    }

    public VirtualMachine getVirtualMachine(Integer vdcId, Integer vappId, Integer vmId)
    {
        VirtualMachine vm = repo.findVirtualMachineById(vmId);

        VirtualAppliance vapp = vappService.getVirtualAppliance(vdcId, vappId);

        if (vm == null || !isAssignedTo(vmId, vapp.getId()))
        {
            throw new NotFoundException(APIError.NON_EXISTENT_VIRTUALMACHINE);
        }
        return vm;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public VirtualMachine updateVirtualMachine(Integer vdcId, Integer vappId, Integer vmId,
        VirtualMachineDto dto)
    {
        VirtualMachine old = getVirtualMachine(vdcId, vappId, vmId);

        old.setName(dto.getName());

        updateVirtualMachine(old);

        return old;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateVirtualMachine(VirtualMachine vm)
    {
        repo.update(vm);
    }

    /**
     * Check if a virtual machine is defined into a virtual appliance.
     * 
     * @param vmId identifier of the virtual machine
     * @param vappId identifier of the virtual appliance
     * @return True if it is, false otherwise.
     */
    public boolean isAssignedTo(Integer vmId, Integer vappId)
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
    public void deleteNotManagedVirtualMachines(Hypervisor hypervisor)
    {
        repo.deleteNotManagedVirtualMachines(hypervisor);
    }

    /**
     * Block the virtual by changing its state to IN_PROGRESS
     * 
     * @param vm VirtualMachine to be blocked
     */
    public void blockVirtualMachine(VirtualMachine vm)
    {
        if (vm.getState() == State.IN_PROGRESS)
        {
            throw new PreconditionFailedException(APIError.VIRTUAL_MACHINE_ALREADY_IN_PROGRESS);
        }

        vm.setState(State.IN_PROGRESS);
        updateVirtualMachine(vm);
    }

    /**
     * Changes the state of the VirtualMachine to the state passed
     * 
     * @param vappId Virtual Appliance Id
     * @param vdcId VirtualDatacenter Id
     * @param state The state to which change
     * @throws Exception
     */
    public void changeVirtualMachineState(Integer vmId, Integer vappId, Integer vdcId, State state)
        throws Exception
    {
        VirtualAppliance virtualAppliance = vappService.getVirtualAppliance(vdcId, vappId);
        Datacenter datacenter = virtualAppliance.getVirtualDatacenter().getDatacenter();
        VirtualMachine vm = getVirtualMachine(vdcId, vappId, vmId);
        VirtualAppliance vapp = contanerVirtualAppliance(vm); 
        EnvelopeType envelop = ovfService.createVirtualApplication(vapp);

        Document docEnvelope = OVFSerializer.getInstance().bindToDocument(envelop, false);

        RemoteService vf =
            remoteService.getRemoteService(datacenter.getId(), RemoteServiceType.VIRTUAL_FACTORY);

        long timeout = Long.valueOf(System.getProperty("abiquo.server.timeout", "0"));

        Resource resource =
            ResourceFactory.create(vf.getUri(), RESOURCE_URI, timeout, docEnvelope,
                ResourceFactory.LATEST);

        changeState(resource, envelop, state.toResourceState());

    }

    @Deprecated
    private VirtualAppliance contanerVirtualAppliance(VirtualMachine vmachine)
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
    public Boolean sameState(VirtualMachine vm, State state)
    {
        String actual = vm.getState().toOVF();// OVFGeneratorService.getActualState(vm);
        return state.toOVF().equalsIgnoreCase(actual);
    }

    private void changeState(final Resource resource, final EnvelopeType envelope,
        final String machineState) throws Exception
    {
        EnvelopeType envelopeRunning = ovfService.changeStateVirtualMachine(envelope, machineState);
        Document docEnvelopeRunning =
            OVFSerializer.getInstance().bindToDocument(envelopeRunning, false);

        resource.put(docEnvelopeRunning);
    }
}
