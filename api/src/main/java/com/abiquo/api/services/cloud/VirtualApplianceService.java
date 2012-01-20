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

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.exceptions.APIException;
import com.abiquo.api.services.DefaultApiService;
import com.abiquo.api.services.UserService;
import com.abiquo.api.services.VirtualMachineAllocatorService;
import com.abiquo.api.services.stub.TarantinoService;
import com.abiquo.model.transport.error.CommonError;
import com.abiquo.scheduler.VirtualMachineRequirementsFactory;
import com.abiquo.scheduler.limit.VirtualMachinePrice;
import com.abiquo.scheduler.limit.VirtualMachinePrice.PricingModelVariables;
import com.abiquo.scheduler.limit.VirtualMachinePrice.VirtualMachineCost;
import com.abiquo.server.core.cloud.NodeVirtualImage;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualApplianceDto;
import com.abiquo.server.core.cloud.VirtualAppliancePriceDto;
import com.abiquo.server.core.cloud.VirtualApplianceRep;
import com.abiquo.server.core.cloud.VirtualApplianceState;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualDatacenterRep;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.VirtualMachineState;
import com.abiquo.server.core.infrastructure.management.RasdManagement;
import com.abiquo.server.core.infrastructure.management.RasdManagementDAO;
import com.abiquo.server.core.infrastructure.storage.Tier;
import com.abiquo.server.core.infrastructure.storage.VolumeManagement;
import com.abiquo.server.core.pricing.CostCode;
import com.abiquo.server.core.pricing.PricingCostCode;
import com.abiquo.server.core.pricing.PricingRep;
import com.abiquo.server.core.pricing.PricingTemplate;
import com.abiquo.server.core.pricing.PricingTier;
import com.abiquo.server.core.scheduler.VirtualMachineRequirements;
import com.abiquo.tracer.ComponentType;
import com.abiquo.tracer.EventType;
import com.abiquo.tracer.SeverityType;

/**
 * Implements the business logic of the class {@link VirtualAppliance}
 * 
 * @author jdevesa@abiquo.com
 */
@Service
public class VirtualApplianceService extends DefaultApiService
{
    /** The logger object **/
    private final static Logger logger = LoggerFactory.getLogger(VirtualMachineService.class);

    @Autowired
    TarantinoService tarantino;

    @Autowired
    private VirtualDatacenterRep repo;

    @Autowired
    private VirtualDatacenterService vdcService;

    @Autowired
    private UserService userService;

    @Autowired
    private VirtualApplianceRep virtualApplianceRepo;

    @Autowired
    private PricingRep pricingRep;

    @Autowired
    private RasdManagementDAO rasdManDao;

    @Autowired
    private VirtualMachineService vmService;

    @Autowired
    private VirtualMachineRequirementsFactory requirements;

    @Autowired
    private VirtualMachineAllocatorService vmallocator;

    public VirtualApplianceService()
    {

    }

    public VirtualApplianceService(final EntityManager em)
    {
        this.repo = new VirtualDatacenterRep(em);
        this.vdcService = new VirtualDatacenterService(em);
        this.userService = new UserService(em);
        this.virtualApplianceRepo = new VirtualApplianceRep(em);
        this.pricingRep = new PricingRep(em);
        this.rasdManDao = new RasdManagementDAO(em);
        this.vmService = new VirtualMachineService(em);
        this.vmallocator = new VirtualMachineAllocatorService(em);
        this.requirements = new VirtualMachineRequirementsFactory();
    }

    /**
     * Retrieves the list of virtual appliances by an unique virtual datacenter
     * 
     * @param vdcId identifier of the virtualdatacenter.
     * @return the list of {@link VirtualAppliance} pojo
     */

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public List<VirtualAppliance> getVirtualAppliancesByVirtualDatacenter(final Integer vdcId)
    {
        VirtualDatacenter vdc = vdcService.getVirtualDatacenter(vdcId);
        return (List<VirtualAppliance>) repo.findVirtualAppliancesByVirtualDatacenter(vdc);
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
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
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
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
            tracer.log(SeverityType.CRITICAL, ComponentType.VIRTUAL_APPLIANCE,
                EventType.VAPP_CREATE, "virtualAppliance.deleteErrorValidations", dto.getName());
            addValidationErrors(vapp.getValidationErrors());
            flushErrors();
        }
        logger.debug("Add virtual appliance to Abiquo with name {}", dto.getName());
        repo.insertVirtualAppliance(vapp);
        logger.debug("Created virtual appliance with name {} !", dto.getName());
        return vapp;
    }

    private StringBuilder extractErrorsInAString(final VirtualAppliance vapp)
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

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public String getPriceVirtualApplianceText(final Integer vdcId, final Integer vappId)
    {
        String price = "";
        VirtualAppliance virtualAppliance = getVirtualAppliance(vdcId, vappId);
        // if enterprise has pt associated
        PricingTemplate pricingTemplate = virtualAppliance.getEnterprise().getPricingTemplate();
        if (pricingTemplate != null && pricingTemplate.isShowChangesBefore())
        {
            VirtualAppliancePriceDto priceDto =
                getPriceVirtualAppliance(virtualAppliance, pricingTemplate);
            price = pricingTemplate.getDescription();
            price =
                price.replace(PricingModelVariables.CHARGE.getText(), priceDto.getTotalCost() + " "
                    + pricingTemplate.getCurrency().getSymbol());
            price =
                price.replace(PricingModelVariables.CHARGE_PERIOD.getText(), pricingTemplate
                    .getChargingPeriod().name());
            price =
                price.replace(PricingModelVariables.MIN_CHARGE.getText(),
                    priceDto.getMinimumChargePeriod() + " "
                        + pricingTemplate.getCurrency().getSymbol());
            price =
                price.replace(PricingModelVariables.MIN_PERIOD.getText(), pricingTemplate
                    .getMinimumCharge().name());

        }
        if (!price.equals(""))
        {
            price = price + "\n";
        }
        return price;// + "\n";
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public VirtualAppliancePriceDto getPriceVirtualAppliance(
        final VirtualAppliance virtualAppliance, final PricingTemplate pricingTemplate)
    {
        BigDecimal cost = new BigDecimal(0);
        Map<VirtualMachineCost, BigDecimal> virtualMachinesCost =
            new HashMap<VirtualMachinePrice.VirtualMachineCost, BigDecimal>();
        virtualMachinesCost.put(VirtualMachineCost.COMPUTE, cost);
        virtualMachinesCost.put(VirtualMachineCost.COST_CODE, cost);
        virtualMachinesCost.put(VirtualMachineCost.NETWORK, cost);
        virtualMachinesCost.put(VirtualMachineCost.ADDITIONAL_VOLUME, cost);
        virtualMachinesCost.put(VirtualMachineCost.STORAGE, cost);
        virtualMachinesCost.put(VirtualMachineCost.STANDING_CHARGE, cost);
        virtualMachinesCost.put(VirtualMachineCost.TOTAL, cost);

        VirtualAppliancePriceDto dto =
            new VirtualAppliancePriceDto(cost, cost, cost, cost, cost, cost);

        int significantDigits = pricingTemplate.getCurrency().getDigits();

        for (NodeVirtualImage node : virtualAppliance.getNodes())
        {
            final VirtualMachine vmachine = node.getVirtualMachine();
            VirtualMachineRequirements virtualMachineRequirements =
                requirements.createVirtualMachineRequirements(vmachine);

            virtualMachinesCost =
                addVirtualMachineCost(virtualMachinesCost, node.getVirtualMachine(),
                    virtualMachineRequirements, pricingTemplate);
        }
        dto.setAdditionalVolumCost(rounded(significantDigits,
            virtualMachinesCost.get(VirtualMachineCost.ADDITIONAL_VOLUME)));
        dto.setCostCodeCost(rounded(significantDigits,
            virtualMachinesCost.get(VirtualMachineCost.COST_CODE)));
        dto.setComputeCost(rounded(significantDigits,
            virtualMachinesCost.get(VirtualMachineCost.COMPUTE)));
        dto.setStorageCost(rounded(significantDigits,
            virtualMachinesCost.get(VirtualMachineCost.STORAGE)));
        dto.setNetworkCost(rounded(significantDigits,
            virtualMachinesCost.get(VirtualMachineCost.NETWORK)));
        dto.setStandingCharge(rounded(significantDigits, pricingTemplate.getStandingChargePeriod()));
        dto.setMinimumCharge(pricingTemplate.getMinimumCharge().ordinal());
        dto.setMinimumChargePeriod(rounded(significantDigits,
            pricingTemplate.getMinimumChargePeriod()));
        dto.setTotalCost(rounded(significantDigits,
            virtualMachinesCost.get(VirtualMachineCost.TOTAL)));
        // It is for enterprise so we don't have to add to the price
        // .add( pricingTemplate.getStandingChargePeriod())

        return dto;
    }

    private BigDecimal rounded(final int significantDigits, final BigDecimal aNumber)
    {
        return aNumber.setScale(significantDigits, BigDecimal.ROUND_UP);
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    private Map<VirtualMachineCost, BigDecimal> addVirtualMachineCost(
        final Map<VirtualMachineCost, BigDecimal> virtualMachinesCost,
        final VirtualMachine virtualMachine,
        final VirtualMachineRequirements virtualMachineRequirements,
        final PricingTemplate pricingTemplate)
    {
        BigDecimal BYTES_TO_GB = new BigDecimal(1024l * 1024l * 1024l);

        getCostCodeCost(virtualMachinesCost, virtualMachine, pricingTemplate);

        Collection<RasdManagement> resources = rasdManDao.findByVirtualMachine(virtualMachine);
        getAdditionalStorageCost(virtualMachinesCost, resources, pricingTemplate);

        virtualMachinesCost.put(
            VirtualMachineCost.COMPUTE,
            virtualMachinesCost.get(VirtualMachineCost.COMPUTE).add(
                pricingTemplate.getVcpu().multiply(
                    new BigDecimal(virtualMachineRequirements.getCpu()))));
        virtualMachinesCost.put(
            VirtualMachineCost.COMPUTE,
            virtualMachinesCost.get(VirtualMachineCost.COMPUTE).add(
                pricingTemplate.getMemoryMB().multiply(
                    new BigDecimal(virtualMachineRequirements.getRam()))));

        virtualMachinesCost.put(
            VirtualMachineCost.STORAGE,
            virtualMachinesCost.get(VirtualMachineCost.STORAGE).add(
                pricingTemplate.getHdGB().multiply(
                    new BigDecimal(virtualMachineRequirements.getHd()).divide(BYTES_TO_GB, 2,
                        BigDecimal.ROUND_HALF_EVEN))));

        virtualMachinesCost.put(
            VirtualMachineCost.NETWORK,
            virtualMachinesCost.get(VirtualMachineCost.NETWORK).add(
                pricingTemplate.getPublicIp().multiply(
                    new BigDecimal(virtualMachineRequirements.getPublicIP()))));

        virtualMachinesCost.put(
            VirtualMachineCost.TOTAL,
            virtualMachinesCost.get(VirtualMachineCost.TOTAL).add(
                virtualMachinesCost.get(VirtualMachineCost.COST_CODE).add(
                    virtualMachinesCost.get(VirtualMachineCost.COMPUTE).add(
                        virtualMachinesCost.get(VirtualMachineCost.STORAGE).add(
                            virtualMachinesCost.get(VirtualMachineCost.ADDITIONAL_VOLUME).add(
                                virtualMachinesCost.get(VirtualMachineCost.NETWORK)))))));
        return virtualMachinesCost;
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    private void getCostCodeCost(final Map<VirtualMachineCost, BigDecimal> virtualMachinesCost,
        final VirtualMachine virtualMachine, final PricingTemplate pricing)
    {
        CostCode cc =
            pricingRep.findCostCodeById(virtualMachine.getVirtualMachineTemplate().getCostCode());
        PricingCostCode pricingCostCode = pricingRep.findPricingCostCode(cc, pricing);
        if (pricingCostCode != null)
        {
            virtualMachinesCost.put(
                VirtualMachineCost.COST_CODE,
                virtualMachinesCost.get(VirtualMachineCost.COST_CODE).add(
                    pricingCostCode.getPrice()));
        }

    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    private void getAdditionalStorageCost(
        final Map<VirtualMachineCost, BigDecimal> virtualMachinesCost,
        final Collection<RasdManagement> resources, final PricingTemplate pricing)
    {

        for (final RasdManagement resource : resources)
        {
            if (resource instanceof VolumeManagement)
            {
                final VolumeManagement volman = (VolumeManagement) resource;
                // accum += volman.getSizeInMB();
                Tier tier = pricingRep.findTierById(volman.getStoragePool().getTier().getId());
                PricingTier pricingTier = pricingRep.findPricingTier(tier, pricing);
                if (pricingTier != null)
                {
                    virtualMachinesCost.put(
                        VirtualMachineCost.ADDITIONAL_VOLUME,
                        virtualMachinesCost.get(VirtualMachineCost.ADDITIONAL_VOLUME).add(
                            pricingTier.getPrice()));
                }
            }
        }
    }

    private void allocateVirtualAppliance(final VirtualAppliance vapp,
        final boolean foreceEnterpriseSoftLimits)
    {

        VirtualMachineRequirements required = requirements.createVirtualMachineRequirements(vapp);
        vmallocator.checkLimist(vapp, required, foreceEnterpriseSoftLimits);

        try
        {
            for (NodeVirtualImage nvi : vapp.getNodes())
            {
                final VirtualMachine virtualMachine = nvi.getVirtualMachine();

                // XXX duplicated limit checker
                vmService.allocate(virtualMachine, vapp, foreceEnterpriseSoftLimits);

            }
            // XXX consider (try to) having the SchechedulerLock by each VM
            // for (Integer vmid : nviDao.findVirtualMachineIdsByVirtualAppliance(vapp))
            // { final String msg = String.format("Allocate %d", vmid); try {
            // SchedulerLock.acquire(msg); final VirtualMachine virtualMachine =
            // vmService.getVirtualMachine(vmid); vmService.allocate(virtualMachine, vapp,
            // foreceEnterpriseSoftLimits); } finally { SchedulerLock.release(msg); } }
        }
        catch (APIException e)
        {
            for (NodeVirtualImage nvi : vapp.getNodes())
            {

                VirtualMachine vm = nvi.getVirtualMachine();
                if (vm.getState() == VirtualMachineState.ALLOCATED)
                {
                    vmallocator.deallocateVirtualMachine(vm);

                    // TODO consider moving the set NOT_ALLOCATED in deallocateVM
                    vm.setState(VirtualMachineState.NOT_ALLOCATED);
                    vmService.updateVirtualMachineBySystem(vm);
                }
            }
            throw e;
        }
        catch (RuntimeException e)
        {
            for (NodeVirtualImage nvi : vapp.getNodes())
            {

                VirtualMachine vm = nvi.getVirtualMachine();
                if (vm.getState() == VirtualMachineState.ALLOCATED)
                {
                    vmallocator.deallocateVirtualMachine(vm);

                    // TODO consider moving the set NOT_ALLOCATED in deallocateVM
                    vm.setState(VirtualMachineState.NOT_ALLOCATED);
                    vmService.updateVirtualMachineBySystem(vm);
                }
            }
            addUnexpectedErrors(APIError.STATUS_INTERNAL_SERVER_ERROR);
            flushErrors();
        }

    }

    /**
     * Deploys all of the {@link VirtualMachine} belonging to this {@link VirtualAppliance}
     * <p>
     * TODO force TRUE
     * 
     * @param vdcId {@link VirtualDatacenter}
     * @param vappId {@link VirtualAppliance}
     * @return List<String>
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Map<Integer, String> deployVirtualAppliance(final Integer vdcId, final Integer vappId,
        final boolean forceLimits)
    {
        VirtualAppliance virtualAppliance = getVirtualAppliance(vdcId, vappId);

        allocateVirtualAppliance(virtualAppliance, forceLimits);

        Map<Integer, String> dto = new HashMap<Integer, String>();
        for (NodeVirtualImage nodevi : virtualAppliance.getNodes())
        {
            VirtualMachine vmachine = nodevi.getVirtualMachine();

            try
            {
                String link = vmService.sendDeploy(vmachine, virtualAppliance);

                dto.put(vmachine.getId(), link);
            }
            catch (Exception e)
            {
                logger
                    .error(
                        "Error already loggegd in the sendDeploy deploying virtual appliance name {}. {}",
                        virtualAppliance.getName(), e.toString());
            }
        }
        return dto;
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public VirtualApplianceState getVirtualApplianceState(final Integer vdcId, final Integer vappId)
    {
        VirtualAppliance virtualAppliance = getVirtualAppliance(vdcId, vappId);
        return virtualAppliance.getState();
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Map<Integer, String> undeployVirtualAppliance(final Integer vdcId, final Integer vappId,
        final Boolean forceUndeploy, final Map<Integer, VirtualMachineState> originalStates)
    {
        VirtualAppliance virtualAppliance = getVirtualAppliance(vdcId, vappId);

        // first check if there is any imported virtualmachine.
        if (!forceUndeploy)
        {
            for (NodeVirtualImage node : virtualAppliance.getNodes())
            {
                if (node.getVirtualMachine().isCaptured())
                {
                    addConflictErrors(APIError.VIRTUAL_MACHINE_IMPORTED_WILL_BE_DELETED);
                    flushErrors();
                }
            }
        }

        Map<Integer, String> dto = new HashMap<Integer, String>();
        for (NodeVirtualImage machine : virtualAppliance.getNodes())
        {
            Integer vmId = machine.getVirtualMachine().getId();
            try
            {
                String link =
                    vmService.undeployVirtualMachine(vmId, vappId, vdcId, forceUndeploy,
                        originalStates.get(vmId));
                dto.put(machine.getVirtualMachine().getId(), link);
            }
            catch (Exception e)
            {
                // The virtual appliance is in an unknown state
                tracer.log(SeverityType.CRITICAL, ComponentType.VIRTUAL_APPLIANCE,
                    EventType.VM_UNDEPLOY, APIError.GENERIC_OPERATION_ERROR.getMessage());

                // For the Admin to know all errors
                tracer.systemLog(SeverityType.CRITICAL, ComponentType.VIRTUAL_APPLIANCE,
                    EventType.VM_UNDEPLOY, "virtualAppliance.deployError", e.toString(),
                    machine.getName(), e.getMessage());
                logger
                    .error(
                        "Error undeploying virtual appliance name {}. But we continue with next virtual machine: {}",
                        virtualAppliance.getName(), e.toString());
            }
        }
        return dto;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void deleteVirtualAppliance(final Integer vdcId, final Integer vappId,
        final Map<Integer, VirtualMachineState> originalStates)
    {
        VirtualAppliance virtualAppliance = getVirtualAppliance(vdcId, vappId);
        userService.checkCurrentEnterpriseForPostMethods(virtualAppliance.getEnterprise());
        logger.debug("Deleting the virtual appliance name {} ", virtualAppliance.getName());

        // We must delete all of its virtual machines
        for (NodeVirtualImage n : virtualAppliance.getNodes())
        {
            Integer vmId = n.getVirtualMachine().getId();
            logger.trace("Deleting the virtual machine with name {}", n.getVirtualMachine()
                .getName());
            vmService.deleteVirtualMachine(vmId, virtualAppliance.getId(), n.getVirtualAppliance()
                .getVirtualDatacenter().getId(), originalStates.get(vmId));
            logger.trace("Deleting the virtual machine with name {}", n.getVirtualMachine()
                .getName() + " successful!");
        }
        virtualApplianceRepo.deleteVirtualAppliance(virtualAppliance);
        logger.debug("Deleting the virtual appliance name {} ok!", virtualAppliance.getName());
        tracer.log(SeverityType.INFO, ComponentType.VIRTUAL_APPLIANCE, EventType.VAPP_DELETE,
            "virtualAppliance.deleted", virtualAppliance.getName());
    }

}
