/**
t * Abiquo community edition
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

package com.abiquo.api.services;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.services.cloud.VirtualDatacenterService;
import com.abiquo.api.tracer.TracerLogger;
import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.model.transport.SingleResourceTransportDto;
import com.abiquo.model.transport.error.ErrorDto;
import com.abiquo.model.transport.error.ErrorsDto;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.enterprise.DatacenterLimits;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.EnterpriseRep;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.InfrastructureRep;
import com.abiquo.server.core.infrastructure.Machine;
import com.abiquo.server.core.infrastructure.Rack;
import com.abiquo.server.core.infrastructure.RemoteService;
import com.abiquo.server.core.infrastructure.RemoteServiceDto;
import com.abiquo.server.core.infrastructure.RemoteServicesDto;
import com.abiquo.server.core.infrastructure.network.Network;
import com.abiquo.server.core.infrastructure.storage.StorageDevice;
import com.abiquo.server.core.infrastructure.storage.Tier;
import com.abiquo.server.core.pricing.PricingTemplate;
import com.abiquo.server.core.pricing.PricingTier;
import com.abiquo.tracer.ComponentType;
import com.abiquo.tracer.EventType;
import com.abiquo.tracer.SeverityType;

@Service
@Transactional(readOnly = true)
public class DatacenterService extends DefaultApiService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(DatacenterService.class);

    @Autowired
    InfrastructureRep repo;

    @Autowired
    InfrastructureService infrastructureService;

    @Autowired
    RemoteServiceService remoteServiceService;

    @Autowired
    VirtualDatacenterService virtualDatacenterService;

    @Autowired
    UserService userService;

    @Autowired
    EnterpriseRep enterpriseRep;

    public DatacenterService()
    {

    }

    public DatacenterService(final EntityManager em)
    {
        repo = new InfrastructureRep(em);
        infrastructureService = new InfrastructureService(em);
        remoteServiceService = new RemoteServiceService(em);
        virtualDatacenterService = new VirtualDatacenterService(em);
        userService = new UserService(em);
        tracer = new TracerLogger();
    }

    public Collection<Datacenter> getDatacenters(final Enterprise enterprise)
    {
        Collection<DatacenterLimits> dcLimits = repo.findDatacenterLimits(enterprise);
        Set<Datacenter> dcs = new HashSet<Datacenter>();
        for (DatacenterLimits dcl : dcLimits)
        {
            dcs.add(dcl.getDatacenter());
        }
        return dcs;
    }

    public Collection<Datacenter> getDatacenters()
    {
        return repo.findAll();
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public Datacenter addDatacenter(final Datacenter datacenter) throws Exception
    {
        if (repo.existsAnyDatacenterWithName(datacenter.getName()))
        {
            tracer.log(SeverityType.MINOR, ComponentType.DATACENTER, EventType.DC_CREATE,
                "Another datacenter with the name '" + datacenter.getName()
                    + "' already exists. Please choose a different name.");
            addConflictErrors(APIError.DATACENTER_DUPLICATED_NAME);
            flushErrors();
        }

        // Create the public network before the datacenter
        // This network will store all the public VLANs of the datacenter.
        Network network = new Network(UUID.randomUUID().toString());
        repo.insertNetwork(network);

        isValidDatacenter(datacenter);
        datacenter.setNetwork(network);
        repo.insert(datacenter);

        // Assign datacenter to the own enterprise
        DatacenterLimits dcLimits =
            new DatacenterLimits(userService.getCurrentUser().getEnterprise(), datacenter);
        enterpriseRep.insertLimit(dcLimits);

        List<PricingTemplate> pricingTemplateList = repo.getPricingTemplates();
        BigDecimal zero = new BigDecimal(0);

        // Add the default tiers
        for (int i = 1; i <= 4; i++)
        {
            Tier tier =
                new Tier("Default Tier " + i, "Description of the default tier " + i, datacenter);
            repo.insertTier(tier);

            if (!pricingTemplateList.isEmpty())
            {
                for (PricingTemplate pt : pricingTemplateList)
                {
                    PricingTier pricingTier = new PricingTier(zero, pt, tier);
                    repo.insertPricingTier(pricingTier);
                }

            }
        }

        // Log the event
        tracer.log(SeverityType.INFO, ComponentType.DATACENTER, EventType.DC_CREATE, "Datacenter '"
            + datacenter.getName() + "' has been created in " + datacenter.getLocation());

        return datacenter;
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public RemoteServicesDto addRemoteServices(final List<RemoteService> remoteServices,
        final Datacenter datacenter)
    {
        // Add the Remote Services in database in case are informed in the request
        RemoteServicesDto responseRemoteService = new RemoteServicesDto();
        if (remoteServices != null)
        {
            for (RemoteService rs : remoteServices)
            {

                SingleResourceTransportDto srtDto =
                    remoteServiceService.addRemoteService(rs, datacenter);

                if (srtDto instanceof RemoteServiceDto)
                {
                    RemoteServiceDto rsDto = (RemoteServiceDto) srtDto;
                    if (rsDto != null)
                    {
                        responseRemoteService.add(rsDto);
                        if (rsDto.getConfigurationErrors() != null
                            && !rsDto.getConfigurationErrors().isEmpty())
                        {
                            if (responseRemoteService.getConfigErrors() == null)
                            {
                                responseRemoteService.setConfigErrors(new ErrorsDto());
                            }
                            responseRemoteService.getConfigErrors().addAll(
                                rsDto.getConfigurationErrors());
                        }
                    }
                }
                else if (srtDto instanceof ErrorsDto)
                {
                    if (responseRemoteService.getConfigErrors() == null)
                    {
                        responseRemoteService.setConfigErrors(new ErrorsDto());
                    }
                    responseRemoteService.getConfigErrors().addAll((ErrorsDto) srtDto);
                }

            }
        }

        if (responseRemoteService.getConfigErrors() != null
            && !responseRemoteService.getConfigErrors().isEmpty())
        {
            // Log the event
            for (ErrorDto error : responseRemoteService.getConfigErrors().getCollection())
            {
                tracer.log(SeverityType.MAJOR, ComponentType.DATACENTER, EventType.DC_CREATE,
                    "Datacenter '" + datacenter.getName() + ": " + error.getMessage());
            }
        }
        else
        {
            // Log the event
            tracer.log(
                SeverityType.INFO,
                ComponentType.DATACENTER,
                EventType.DC_CREATE,
                "Datacenter '" + datacenter.getName() + "' has been created in "
                    + datacenter.getLocation());
        }

        if (responseRemoteService.getConfigErrors() != null
            && !responseRemoteService.getConfigErrors().isEmpty())
        {
            // Log the event
            tracer
                .log(
                    SeverityType.MAJOR,
                    ComponentType.DATACENTER,
                    EventType.DC_CREATE,
                    "Datacenter '"
                        + datacenter.getName()
                        + "' has been created but some Remote Services had configuration errors. Please check the events to fix the problems.");
        }
        else
        {
            // Log the event
            tracer.log(
                SeverityType.INFO,
                ComponentType.DATACENTER,
                EventType.DC_CREATE,
                "Datacenter '" + datacenter.getName() + "' has been created in "
                    + datacenter.getLocation());
        }

        return responseRemoteService;
    }

    public Datacenter getDatacenter(final Integer id)
    {
        Datacenter datacenter = repo.findById(id);

        if (datacenter == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_DATACENTER);
            flushErrors();
        }

        return datacenter;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Datacenter modifyDatacenter(final Integer datacenterId, final Datacenter datacenter)
    {
        Datacenter old = getDatacenter(datacenterId);

        if (repo.existsAnyOtherWithName(old, datacenter.getName()))
        {
            addConflictErrors(APIError.DATACENTER_DUPLICATED_NAME);
            flushErrors();
        }

        old.setName(datacenter.getName());
        old.setLocation(datacenter.getLocation());

        isValidDatacenter(old);

        repo.update(old);

        tracer.log(SeverityType.INFO, ComponentType.DATACENTER, EventType.DC_MODIFY, "Datacenter '"
            + old.getName() + "' has been modified [Name: " + datacenter.getName()
            + ", Situation: " + datacenter.getLocation() + "]");

        return old;
    }

    public Set<HypervisorType> getHypervisorTypes(final Datacenter datacenter)
    {
        return repo.findHypervisors(datacenter);
    }

    public List<Enterprise> findEnterprisesByDatacenterWithNetworks(final Datacenter datacenter,
        final Boolean network, final Integer firstElem, final Integer numElem)
    {
        return repo.findEnterprisesByDataCenter(datacenter, network, firstElem, numElem);

    }

    private void isValidDatacenter(final Datacenter datacenter)
    {
        if (!datacenter.isValid())
        {
            addValidationErrors(datacenter.getValidationErrors());
        }
        flushErrors();
    }

    public List<Rack> getRacks(final Datacenter datacenter)
    {
        return repo.findRacks(datacenter);
    }

    public List<Rack> getRacksWithHAEnabled(final Datacenter datacenter)
    {
        return repo.findRacksWithHAEnabled(datacenter);
    }

    public List<Machine> getEnabledMachines(final Rack rack)
    {
        return repo.findRackEnabledForHAMachines(rack);
    }

    public void removeDatacenter(final Integer id)
    {
        Datacenter datacenter = repo.findById(id);
        if (datacenter == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_DATACENTER);
            flushErrors();
        }

        // only delete the datacenter if it doesn't have any virtual datacenter and any storage
        // device associated
        Collection<VirtualDatacenter> vdcs =
            virtualDatacenterService.getVirtualDatacentersByDatacenter(datacenter);
        if (vdcs == null || vdcs.isEmpty())
        {
            List<StorageDevice> sDevices = getStorageDevices(datacenter);
            if (sDevices == null || sDevices.isEmpty())
            {

                // deleting datacenter
                deleteAllocationRules(datacenter);

                deleteNetwork(datacenter);

                deletePricingTiers(datacenter.getId());

                List<Rack> racks = getRacks(datacenter);
                if (racks != null)
                {
                    for (Rack rack : racks)
                    {
                        infrastructureService.removeRack(rack);
                    }
                }

                repo.delete(datacenter);

                LOGGER.debug("Deleting datacenter");

                tracer.log(SeverityType.INFO, ComponentType.DATACENTER, EventType.DC_DELETE,
                    "Datacenter " + datacenter.getName() + " deleted");
            }
            else
            {
                tracer.log(SeverityType.CRITICAL, ComponentType.DATACENTER, EventType.DC_DELETE,
                    "Cannot delete datacenter with storage devices associated");
                addConflictErrors(APIError.DATACENTER_DELETE_STORAGE);
                flushErrors();
            }
        }
        else
        {
            tracer.log(SeverityType.CRITICAL, ComponentType.DATACENTER, EventType.DC_DELETE,
                "Cannot delete datacenter with virtual datacenters associated");
            addConflictErrors(APIError.DATACENTER_DELETE_VIRTUAL_DATACENTERS);
            flushErrors();
        }

    }

    public boolean isAssignedTo(final Integer datacenterId, final RemoteServiceType type)
    {
        return infrastructureService.isAssignedTo(datacenterId, type);
    }

    // overrided on premium
    protected List<StorageDevice> getStorageDevices(final Datacenter datacenter)
    {
        return null;
    }

    // overrided on premium
    protected void deleteAllocationRules(final Datacenter datacenter)
    {
    }

    // overrided on premium
    protected void deleteNetwork(final Datacenter datacenter)
    {
    }

    // overrided on premium
    protected void deletePricingTiers(final Integer datacenterId)
    {
    }
}
