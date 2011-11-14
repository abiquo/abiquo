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

/**
 * 
 */
package com.abiquo.api.services.cloud;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;

import org.dmtf.schemas.ovf.envelope._1.EnvelopeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;

import com.abiquo.api.config.ConfigService;
import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.exceptions.ConflictException;
import com.abiquo.api.services.DefaultApiService;
import com.abiquo.api.services.RemoteServiceService;
import com.abiquo.api.services.UserService;
import com.abiquo.api.services.VirtualMachineAllocatorService;
import com.abiquo.api.services.ovf.OVFGeneratorService;
import com.abiquo.api.util.EventingSupport;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.model.transport.error.CommonError;
import com.abiquo.ovfmanager.ovf.xml.OVFSerializer;
import com.abiquo.server.core.cloud.NodeVirtualImage;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualApplianceDto;
import com.abiquo.server.core.cloud.VirtualApplianceRep;
import com.abiquo.server.core.cloud.VirtualApplianceState;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualDatacenterRep;
import com.abiquo.server.core.cloud.VirtualImageDto;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.VirtualMachineChangeStateResultDto;
import com.abiquo.server.core.cloud.VirtualMachineState;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.RemoteService;
import com.abiquo.tracer.ComponentType;
import com.abiquo.tracer.EventType;
import com.abiquo.tracer.SeverityType;
import com.sun.ws.management.client.Resource;
import com.sun.ws.management.client.ResourceFactory;

/**
 * Implements the business logic of the class {@link VirtualAppliance}
 * 
 * @author jdevesa@abiquo.com
 */
@Service
@Transactional(readOnly = true)
public class VirtualApplianceService extends DefaultApiService
{

    private static final String RESOURCE_URI =
        "http://schemas.dmtf.org/ovf/envelope/1/virtualApplianceService/virtualApplianceResource";

    @Autowired
    VirtualDatacenterRep repo;

    @Autowired
    VirtualDatacenterService vdcService;

    @Autowired
    OVFGeneratorService ovfService;

    @Autowired
    RemoteServiceService remoteServiceService;

    @Autowired
    VirtualMachineAllocatorService allocatorService;

    @Autowired
    UserService userService;

    @Autowired
    VirtualApplianceRep virtualApplianceRepo;

    @Autowired
    VirtualMachineService vmService;

    /** The logger object **/
    private final static Logger logger = LoggerFactory.getLogger(VirtualMachineService.class);

    public VirtualApplianceService()
    {

    }

    public VirtualApplianceService(final EntityManager em)
    {
        this.repo = new VirtualDatacenterRep(em);
        this.virtualApplianceRepo = new VirtualApplianceRep(em);
        this.vdcService = new VirtualDatacenterService(em);
        this.remoteServiceService = new RemoteServiceService(em);
        this.userService = new UserService(em);
    }

    /**
     * Retrieves the list of virtual appliances by an unique virtual datacenter
     * 
     * @param vdcId identifier of the virtualdatacenter.
     * @return the list of {@link VirtualAppliance} pojo
     */
    public List<VirtualAppliance> getVirtualAppliancesByVirtualDatacenter(final Integer vdcId)
    {
        VirtualDatacenter vdc = vdcService.getVirtualDatacenter(vdcId);
        return (List<VirtualAppliance>) repo.findVirtualAppliancesByVirtualDatacenter(vdc);
    }

    public VirtualAppliance getVirtualApplianceByVirtualMachine(final VirtualMachine virtualMachine)
    {
        return virtualApplianceRepo.findVirtualApplianceByVirtualMachine(virtualMachine);
    }

    /**
     * Returns the virtual appliance identi
     * 
     * @param vdcId i
     * @param vappId
     * @return
     */
    public VirtualAppliance getVirtualAppliance(final Integer vdcId, final Integer vappId)
    {

        VirtualDatacenter vdc = repo.findById(vdcId);
        if (vdc == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_VIRTUAL_DATACENTER);
            flushErrors();
        }
        if (vappId == 0)
        {
            addValidationErrors(APIError.INVALID_ID);
            flushErrors();
        }

        VirtualAppliance vapp = repo.findVirtualApplianceById(vappId);
        if (vapp == null || !vapp.getVirtualDatacenter().getId().equals(vdcId))
        {
            addNotFoundErrors(APIError.NON_EXISTENT_VIRTUALAPPLIANCE);
            flushErrors();
        }
        return vapp;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void startVirtualAppliance(final Integer vdcId, final Integer vappId)
    {
        VirtualAppliance virtualAppliance = getVirtualAppliance(vdcId, vappId);
        Datacenter datacenter = virtualAppliance.getVirtualDatacenter().getDatacenter();

        try
        {
            if (virtualAppliance.getState() == VirtualApplianceState.DEPLOYED)
            {
                allocate(virtualAppliance);

                virtualAppliance.setState(VirtualApplianceState.DEPLOYED);
                repo.updateVirtualAppliance(virtualAppliance);

                EnvelopeType envelop = ovfService.createVirtualApplication(virtualAppliance);

                Document docEnvelope = OVFSerializer.getInstance().bindToDocument(envelop, false);

                RemoteService vsm =
                    remoteServiceService.getRemoteService(datacenter.getId(),
                        RemoteServiceType.VIRTUAL_SYSTEM_MONITOR);

                RemoteService vf =
                    remoteServiceService.getRemoteService(datacenter.getId(),
                        RemoteServiceType.VIRTUAL_FACTORY);

                long timeout = Long.valueOf(ConfigService.getServerTimeout());

                Resource resource =
                    ResourceFactory.create(vf.getUri(), RESOURCE_URI, timeout, docEnvelope,
                        ResourceFactory.LATEST);

                EventingSupport.subscribeToAllVA(virtualAppliance, vsm.getUri());

                changeState(resource, envelop, VirtualMachineState.ON.toResourceState());
            }
        }
        catch (Exception e)
        {
            // XXX
        }
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void addImage(final Integer virtualDatacenterId, final Integer virtualApplianceId,
        final VirtualImageDto image)
    {

    }

    private void allocate(final VirtualAppliance virtualAppliance)
    {
        for (NodeVirtualImage node : virtualAppliance.getNodes())
        {
            allocatorService.allocateVirtualMachine(node.getVirtualMachine().getId(),
                virtualAppliance.getId(), false);
        }
    }

    private void changeState(final Resource resource, final EnvelopeType envelope,
        final String machineState) throws Exception
    {
        EnvelopeType envelopeRunning = ovfService.changeStateVirtualMachine(envelope, machineState);
        Document docEnvelopeRunning =
            OVFSerializer.getInstance().bindToDocument(envelopeRunning, false);

        resource.put(docEnvelopeRunning);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public VirtualAppliance createVirtualAppliance(final Integer vdcId,
        final VirtualApplianceDto dto)
    {
        VirtualDatacenter vdc = vdcService.getVirtualDatacenter(vdcId);
        userService.checkCurrentEnterpriseForPostMethods(vdc.getEnterprise());

        logger.debug("Create virtual appliance with name {}", dto.getName());
        // Only empty virtual appliances can be created
        VirtualAppliance vapp =
            new VirtualAppliance(vdc.getEnterprise(),
                vdc,
                dto.getName(),
                VirtualApplianceState.NOT_DEPLOYED);

        vapp.setHighDisponibility(dto.getHighDisponibility());
        vapp.setPublicApp(dto.getPublicApp());
        vapp.setNodeconnections(dto.getNodecollections());

        if (!vapp.isValid())
        {
            StringBuilder sb = extractErrorsInAString(vapp);
            logger.error(
                "Error create virtual appliance with name {} due to validation errors: {}",
                dto.getName(), sb.toString());
            tracer
                .log(SeverityType.CRITICAL, ComponentType.VIRTUAL_APPLIANCE, EventType.VAPP_CREATE,
                    "Delete of the virtual appliance with name " + dto.getName());
            tracer.systemError(SeverityType.CRITICAL, ComponentType.VIRTUAL_APPLIANCE,
                EventType.VAPP_CREATE,
                "Delete of the virtual appliance with name " + dto.getName(),
                new Exception(sb.toString()));
            addValidationErrors(vapp.getValidationErrors());
            flushErrors();
        }
        logger.debug("Add virtual appliance to Abiquo with name {}", dto.getName());
        repo.insertVirtualAppliance(vapp);
        logger.debug("Created virtual appliance with name {} !", dto.getName());
        return vapp;
    }

    private StringBuilder extractErrorsInAString(VirtualAppliance vapp)
    {
        Set<CommonError> errors = vapp.getValidationErrors();
        StringBuilder sb = new StringBuilder();
        for (CommonError e : errors)
        {
            sb.append("Error code: ").append(e.getCode()).append(", Message: ")
                .append(e.getMessage());
        }
        return sb;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public VirtualAppliance updateVirtualAppliance(final Integer vdcId, final Integer vappId,
        final VirtualApplianceDto dto)
    {
        VirtualAppliance vapp = getVirtualAppliance(vdcId, vappId);
        userService.checkCurrentEnterpriseForPostMethods(vapp.getEnterprise());

        vapp.setName(dto.getName());

        repo.updateVirtualAppliance(vapp);

        return vapp;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public List<VirtualMachineChangeStateResultDto> changeVirtualAppMachinesState(
        final Integer vdcId, final Integer vappId, final VirtualMachineState state)
    {
        VirtualAppliance vapp = getVirtualAppliance(vdcId, vappId);
        if (vapp.getState().equals(VirtualMachineState.NOT_ALLOCATED))
        {
            addConflictErrors(APIError.VIRTUALAPPLIANCE_NOT_DEPLOYED);
            flushErrors();
        }
        if (!vapp.getState().equals(VirtualMachineState.ALLOCATED))
        {
            addConflictErrors(APIError.VIRTUALAPPLIANCE_NOT_RUNNING);
            flushErrors();
        }
        List<VirtualMachine> vmachines = vmService.findByVirtualAppliance(vapp);
        List<VirtualMachineChangeStateResultDto> results =
            new ArrayList<VirtualMachineChangeStateResultDto>();
        for (VirtualMachine vm : vmachines)
        {
            try
            {
                if (!vmService.sameState(vm, state))
                {
                    vmService.changeVirtualMachineState(vm.getId(), vappId, vdcId, state);
                }
                VirtualMachineChangeStateResultDto result =
                    new VirtualMachineChangeStateResultDto();
                result.setId(vm.getId());
                result.setName(vm.getName());
                result.setSuccess(true);
                results.add(result);
            }
            catch (ConflictException e)
            {
                VirtualMachineChangeStateResultDto result =
                    new VirtualMachineChangeStateResultDto();
                result.setId(vm.getId());
                result.setName(vm.getName());
                result.setSuccess(false);
                for (CommonError er : e.getErrors())
                {
                    result.setMessage(er.getMessage());
                }
                results.add(result);
            }
        }
        return results;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public List<String> deployVirtualAppliance(final Integer vdcId, final Integer vappId)
    {
        VirtualAppliance virtualAppliance = getVirtualAppliance(vdcId, vappId);

        List<String> dto = new ArrayList<String>();
        try
        {
            for (NodeVirtualImage machine : virtualAppliance.getNodes())
            {
                String link =
                    vmService.deployVirtualMachine(machine.getVirtualMachine().getId(), vappId,
                        vdcId, false);
                dto.add(link);
            }
        }
        catch (Exception e)
        {
            // The virtual appliance is in an unknown state
        }
        return dto;
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public VirtualApplianceState getVirtualApplianceState(final Integer vdcId, final Integer vappId)
    {
        VirtualAppliance virtualAppliance = getVirtualAppliance(vdcId, vappId);
        return virtualAppliance.getState();
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public List<String> undeployVirtualAppliance(final Integer vdcId, final Integer vappId)
    {
        VirtualAppliance virtualAppliance = getVirtualAppliance(vdcId, vappId);

        List<String> dto = new ArrayList<String>();
        try
        {
            for (NodeVirtualImage machine : virtualAppliance.getNodes())
            {
                String link =
                    vmService.undeployVirtualMachine(machine.getVirtualMachine().getId(), vappId,
                        vdcId);
                dto.add(link);
            }
        }
        catch (Exception e)
        {
            // The virtual appliance is in an unknown state
        }
        return dto;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void deleteVirtualAppliance(final Integer vdcId, final Integer vappId)
    {
        VirtualAppliance virtualAppliance = getVirtualAppliance(vdcId, vappId);
        userService.checkCurrentEnterpriseForPostMethods(virtualAppliance.getEnterprise());
        logger.debug("Deleting the virtual appliance name {} ", virtualAppliance.getName());
        if (!virtualApplianceStateAllowsDelete(virtualAppliance))
        {
            logger.error(
                "Delete virtual appliance error, the State must be NOT_DEPLOYED but was {}",
                virtualAppliance.getState().name());
            tracer.log(SeverityType.CRITICAL, ComponentType.VIRTUAL_APPLIANCE,
                EventType.VAPP_DELETE, "Delete of the virtual appliance with name "
                    + virtualAppliance.getName()
                    + " failed with due to an invalid state. Should be NOT_DEPLOYED, but was "
                    + virtualAppliance.getState().name());
            tracer
                .systemError(
                    SeverityType.CRITICAL,
                    ComponentType.VIRTUAL_APPLIANCE,
                    EventType.VAPP_CREATE,
                    "Delete of the virtual appliance with name " + virtualAppliance.getName()
                        + " failed with due to an invalid state. Should be NOT_DEPLOYED, but was "
                        + virtualAppliance.getState().name(),
                    new Exception(" failed with due to an invalid state. Should be NOT_DEPLOYED, but was "
                        + virtualAppliance.getState().name()));
            addConflictErrors(APIError.VIRTUALAPPLIANCE_NOT_RUNNING);
            flushErrors();
        }

        // We must delete all of its virtual machines
        for (NodeVirtualImage n : virtualAppliance.getNodes())
        {
            logger.trace("Deleting the virtual machine with name {}", n.getVirtualMachine()
                .getName());
            vmService.deleteVirtualMachine(n.getVirtualMachine().getId(), virtualAppliance.getId(),
                n.getVirtualAppliance().getVirtualDatacenter().getId());
            logger.trace("Deleting the virtual machine with name {}", n.getVirtualMachine()
                .getName() + " successful!");
        }
        virtualApplianceRepo.deleteVirtualAppliance(virtualAppliance);
        logger.debug("Deleting the virtual appliance name {} ok!", virtualAppliance.getName());
        tracer.log(SeverityType.INFO, ComponentType.VIRTUAL_APPLIANCE, EventType.VAPP_DELETE,
            "Delete of the virtual appliance with name " + virtualAppliance.getName()
                + " succeeded");
    }

    private boolean virtualApplianceStateAllowsDelete(final VirtualAppliance virtualAppliance)
    {
        switch (virtualAppliance.getState())
        {
            case LOCKED:
            case UNKNOWN:
            case DEPLOYED:
            case NEEDS_SYNC:
            {
                return false;
            }
            default:
            {
                return true;
            }
        }
    }
}
