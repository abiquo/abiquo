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

package com.abiquo.api.services;

import static com.abiquo.api.util.URIResolver.buildPath;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.resources.DatacenterResource;
import com.abiquo.api.resources.DatacentersResource;
import com.abiquo.api.resources.config.PricingTemplateResource;
import com.abiquo.api.resources.config.PricingTemplatesResource;
import com.abiquo.api.services.appslibrary.CategoryService;
import com.abiquo.api.spring.security.SecurityService;
import com.abiquo.api.util.URIResolver;
import com.abiquo.model.enumerator.Privileges;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.model.transport.error.CommonError;
import com.abiquo.server.core.appslibrary.Category;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualDatacenterRep;
import com.abiquo.server.core.cloud.VirtualMachineRep;
import com.abiquo.server.core.common.Limit;
import com.abiquo.server.core.enterprise.DatacenterLimits;
import com.abiquo.server.core.enterprise.DatacenterLimitsDto;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.EnterpriseDto;
import com.abiquo.server.core.enterprise.EnterpriseRep;
import com.abiquo.server.core.enterprise.Privilege;
import com.abiquo.server.core.enterprise.Role;
import com.abiquo.server.core.enterprise.RoleLdap;
import com.abiquo.server.core.enterprise.User;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.InfrastructureRep;
import com.abiquo.server.core.infrastructure.Machine;
import com.abiquo.server.core.infrastructure.MachineDto;
import com.abiquo.server.core.infrastructure.network.VLANNetwork;
import com.abiquo.server.core.pricing.PricingRep;
import com.abiquo.server.core.pricing.PricingTemplate;
import com.abiquo.tracer.ComponentType;
import com.abiquo.tracer.EventType;
import com.abiquo.tracer.SeverityType;

@Service
public class EnterpriseService extends DefaultApiService
{
    @Autowired
    protected EnterpriseRep repo;

    @Autowired
    private VirtualDatacenterRep vdcRepo;

    @Autowired
    private InfrastructureRep infraRep;

    @Autowired
    PricingRep pricingRep;

    @Autowired
    private UserService userService;

    @Autowired
    private DatacenterService datacenterService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    protected VirtualMachineRep virtualMachineRep;

    @Autowired
    private CategoryService categoryService;

    /** Autowired Virtual Infrastructure DAO repository. */
    @Autowired
    protected VirtualDatacenterRep virtualDatacenterRep;

    public EnterpriseService()
    {

    }

    public EnterpriseService(final EntityManager em)
    {
        repo = new EnterpriseRep(em);
        vdcRepo = new VirtualDatacenterRep(em);
        infraRep = new InfrastructureRep(em);
        userService = new UserService(em);
        datacenterService = new DatacenterService(em);
    }

    @Transactional(readOnly = true)
    public Enterprise getCurrentEnterprise()
    {
        return userService.getCurrentUser().getEnterprise();
    }

    @Transactional(readOnly = true)
    public Collection<Enterprise> getEnterprises(final Integer startwith, final int idPricingTempl,
        final boolean included, final String filterName, final Integer numResults)
    {
        User user = userService.getCurrentUser();

        PricingTemplate pt = null;

        // id pricing -1
        if (idPricingTempl != -1)
        {
            if (idPricingTempl != 0)
            {
                pt = findPricingTemplate(idPricingTempl);
            }
        }

        // if (user.getRole().getType() == Role.Type.ENTERPRISE_ADMIN)
        if (!securityService.hasPrivilege(Privileges.ENTERPRISE_ENUMERATE)
            && !securityService.hasPrivilege(Privileges.USERS_MANAGE_OTHER_ENTERPRISES)
            && !securityService.hasPrivilege(Privileges.ENTERPRISE_ADMINISTER_ALL))
        {
            if (idPricingTempl != -1)
            {
                return repo.findByPricingTemplate(startwith, pt, included, filterName, numResults,
                    user.getEnterprise().getId());
            }

            return Collections.singletonList(user.getEnterprise());
        }

        if (idPricingTempl != -1)
        {
            if (idPricingTempl != 0)
            {
                pt = findPricingTemplate(idPricingTempl);
            }
            return repo
                .findByPricingTemplate(startwith, pt, included, filterName, numResults, null);
        }

        if (!StringUtils.isEmpty(filterName))
        {
            return repo.findByNameAnywhere(filterName);
        }

        return repo.findAll(startwith, numResults);

    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Enterprise addEnterprise(final EnterpriseDto dto)
    {
        if (repo.existsAnyWithName(dto.getName()))
        {
            addConflictErrors(APIError.ENTERPRISE_DUPLICATED_NAME);
            flushErrors();
        }

        Enterprise enterprise =
            new Enterprise(dto.getName(),
                dto.getRamSoftLimitInMb(),
                dto.getCpuCountSoftLimit(),
                dto.getHdSoftLimitInMb(),
                dto.getRamHardLimitInMb(),
                dto.getCpuCountHardLimit(),
                dto.getHdHardLimitInMb());

        enterprise.setIsReservationRestricted(dto.getIsReservationRestricted());
        enterprise.setStorageLimits(new Limit(dto.getStorageSoft(), dto.getStorageHard()));
        enterprise.setRepositoryLimits(new Limit(dto.getRepositorySoft(), dto.getRepositoryHard()));
        enterprise.setVlansLimits(new Limit(dto.getVlansSoft(), dto.getVlansHard()));
        enterprise.setPublicIPLimits(new Limit(dto.getPublicIpsSoft(), dto.getPublicIpsHard()));

        // if we are in community the Pricingtemplate id is not informed, is null
        // in this case we don't overwrite the old value.
        if (securityService.hasPrivilege(Privileges.PRICING_MANAGE))
        {
            if (dto.searchLink(PricingTemplateResource.PRICING_TEMPLATE) != null)
            {
                int idPricing = getPricingTemplateId(dto);
                if (idPricing == 0)
                {
                    enterprise.setPricingTemplate(null);
                }
                else
                {

                    PricingTemplate pricingTemplate = findPricingTemplate(idPricing);
                    enterprise.setPricingTemplate(pricingTemplate);
                }
            }
        }
        isValidEnterprise(enterprise);

        repo.insert(enterprise);
        tracer.log(SeverityType.INFO, ComponentType.ENTERPRISE, EventType.ENTERPRISE_CREATE,
            "enterprise.created", enterprise.getName());
        return enterprise;
    }

    @Transactional(readOnly = true)
    public PricingTemplate getPricingTemplate(final Integer id)
    {
        PricingTemplate pt = pricingRep.findPricingTemplateById(id);
        if (pt == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_PRICING_TEMPLATE);
            flushErrors();
        }
        return pt;
    }

    @Transactional(readOnly = true)
    public Enterprise getEnterprise(final Integer id)
    {
        Enterprise enterprise = repo.findById(id);
        if (enterprise == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_ENTERPRISE);
            flushErrors();
        }

        // userService.checkEnterpriseAdminCredentials(enterprise);
        userService.checkCurrentEnterprise(enterprise);
        return enterprise;
    }

    /**
     * This method does not enforce same enterprise. But does check for privs.
     * 
     * @param id
     * @return Enterprise
     */
    @Transactional(readOnly = true)
    public Enterprise getAnyEnterprise(final Integer id)
    {
        Enterprise enterprise = repo.findById(id);
        if (enterprise == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_ENTERPRISE);
            flushErrors();
        }

        return enterprise;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Enterprise modifyEnterprise(final Integer enterpriseId, final EnterpriseDto dto)
    {
        Enterprise old = repo.findById(enterpriseId);
        if (old == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_ENTERPRISE);
            flushErrors();
        }

        Integer userEnt = userService.getCurrentUser().getEnterprise().getId();
        if (!securityService.hasPrivilege(Privileges.USERS_MANAGE_OTHER_ENTERPRISES)
            && !userEnt.equals(dto.getId()))
        {
            throw new AccessDeniedException("");
        }

        if (dto.getName().isEmpty())
        {
            addValidationErrors(APIError.ENTERPRISE_EMPTY_NAME);
            flushErrors();
        }

        if (repo.existsAnyOtherWithName(old, dto.getName()))
        {
            addConflictErrors(APIError.ENTERPRISE_DUPLICATED_NAME);
            flushErrors();
        }

        old.setName(dto.getName());
        old.setIsReservationRestricted(dto.getIsReservationRestricted());
        old.setRamLimitsInMb(new Limit((long) dto.getRamSoftLimitInMb(), (long) dto
            .getRamHardLimitInMb()));
        old.setCpuCountLimits(new Limit((long) dto.getCpuCountSoftLimit(), (long) dto
            .getCpuCountHardLimit()));
        old.setHdLimitsInMb(new Limit(dto.getHdSoftLimitInMb(), dto.getHdHardLimitInMb()));
        old.setStorageLimits(new Limit(dto.getStorageSoft(), dto.getStorageHard()));
        old.setRepositoryLimits(new Limit(dto.getRepositorySoft(), dto.getRepositoryHard()));
        old.setVlansLimits(new Limit(dto.getVlansSoft(), dto.getVlansHard()));
        old.setPublicIPLimits(new Limit(dto.getPublicIpsSoft(), dto.getPublicIpsHard()));
        isValidEnterprise(old);
        isValidEnterpriseLimit(old);

        // if we are in community the Pricingtemplate id is not informed, is null
        // in this case we don't overwrite the old value.
        if (securityService.hasPrivilege(Privileges.PRICING_MANAGE))
        {
            if (dto.searchLink(PricingTemplateResource.PRICING_TEMPLATE) != null)
            {
                int idPricing = getPricingTemplateId(dto);
                if (idPricing == 0)
                {
                    old.setPricingTemplate(null);
                }
                else
                {

                    PricingTemplate pricingTemplate = findPricingTemplate(idPricing);
                    old.setPricingTemplate(pricingTemplate);
                    tracer.log(SeverityType.INFO, ComponentType.ENTERPRISE,
                        EventType.PRICING_TEMPLATE_ASSIGNED, "pricingtemplate.assigned",
                        pricingTemplate.getName(), old.getName());
                }
            }
        }

        repo.update(old);
        tracer.log(SeverityType.INFO, ComponentType.ENTERPRISE, EventType.ENTERPRISE_MODIFY,
            "enterprise.modified", old.getName());
        return old;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void removeEnterprise(final Integer id)
    {
        Enterprise enterprise = getEnterprise(id);
        User user = userService.getCurrentUser();

        if (user.getEnterprise().equals(enterprise))
        {
            addConflictErrors(APIError.ENTERPRISE_DELETE_OWN_ENTERPRISE);
            flushErrors();
        }

        Collection<VirtualDatacenter> vdcs = vdcRepo.findByEnterprise(enterprise);
        if (!vdcs.isEmpty())
        {
            addConflictErrors(APIError.ENTERPRISE_DELETE_ERROR_WITH_VDCS);
            flushErrors();
        }

        // Release reserved machines
        List<Machine> reservedMachines = findReservedMachines(id);
        if (reservedMachines != null && !reservedMachines.isEmpty())
        {
            for (Machine m : reservedMachines)
            {
                releaseMachine(m.getId(), id);
            }
        }

        if (!userService.enterpriseWithBlockedRoles(enterprise).isEmpty())
        {
            String message =
                "Cannot delete enterprise because some users have roles with super privileges ("
                    + userService.enterpriseWithBlockedRoles(enterprise)
                    + "), please change their enterprise before continuing";
            addConflictErrors(new CommonError(APIError.ENTERPRISE_WITH_BLOCKED_USER.getCode(),
                message));
            flushErrors();
        }

        // user with blocked role is checked before
        Collection<Role> roles =
            repo.findRolesByEnterpriseNotNull(enterprise, null, null, false, 0, 1000);
        if (roles != null)
        {
            for (Role r : roles)
            {
                Collection<User> users = repo.findUsersByRole(r);
                if (users != null)
                {
                    for (User u : users)
                    {
                        repo.removeUser(u);
                    }
                }
                deleteRole(r);
            }
        }
        Collection<Category> categories = categoryService.getCategories(enterprise.getId(), true);

        if (categories != null)
        {
            for (Category c : categories)
            {
                categoryService.removeCategory(c.getId());
            }
        }
        Collection<VLANNetwork> vlans =
            virtualDatacenterRep.findExternalVlansByEnterprise(enterprise);

        if (vlans != null)
        {
            for (VLANNetwork v : vlans)
            {
                virtualDatacenterRep.deleteVLAN(v);
            }
        }

        removeEnterpriseProperties(enterprise);
        repo.delete(enterprise);
        tracer.log(SeverityType.INFO, ComponentType.ENTERPRISE, EventType.ENTERPRISE_DELETE,
            "enterprise.deleted", enterprise.getName());

    }

    protected void deleteRole(final Role role)
    {
        repo.deleteRole(role);
    }

    @Transactional(readOnly = true)
    public List<Machine> findReservedMachines(final Integer enterpriseId)
    {
        return repo.findReservedMachines(getEnterprise(enterpriseId));
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Machine reserveMachine(final MachineDto machineDto, final Integer enterpriseId)
    {
        return reserveMachine(machineDto.getId(), enterpriseId);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Machine reserveMachine(final Integer machineId, final Integer enterpriseId)
    {
        Machine machine = infraRep.findMachineById(machineId);
        if (machine == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_MACHINE);
            flushErrors();
        }

        Enterprise enterprise = getEnterprise(enterpriseId);

        if (machine.getEnterprise() != null)
        {
            if (machine.getEnterprise().getId() != enterpriseId)
            {
                addConflictErrors(APIError.MACHINE_RESERVED_ENTERPRISE);
                flushErrors();
            }
            else
            {
                addConflictErrors(APIError.MACHINE_ALREADY_RESERVED);
                flushErrors();
            }

        }

        repo.reserveMachine(machine, enterprise);

        return machine;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void releaseMachine(final Integer machineId, final Integer enterpriseId)
    {
        Enterprise enterprise = getEnterprise(enterpriseId);
        Machine machine = repo.findReservedMachine(enterprise, machineId);
        if (machine == null)
        {
            addNotFoundErrors(APIError.MACHINE_NOT_RESERVED);
            flushErrors();
        }

        repo.releaseMachine(machine);
    }

    @Transactional(readOnly = true)
    public DatacenterLimits findLimitsByEnterpriseAndIdentifier(final Integer enterpriseId,
        final Integer limitId)
    {
        Enterprise enterprise = getEnterprise(enterpriseId);

        return findLimitsByEnterpriseAndIdentifier(enterprise, limitId);
    }

    @Transactional(readOnly = true)
    private DatacenterLimits findLimitsByEnterpriseAndIdentifier(final Enterprise enterprise,
        final Integer limitId)
    {
        DatacenterLimits limit = repo.findLimitsByEnterpriseAndIdentifier(enterprise, limitId);

        if (limit == null)
        {
            addNotFoundErrors(APIError.LIMITS_NOT_EXIST);
            flushErrors();
        }

        return limit;
    }

    @Transactional(readOnly = true)
    public Collection<DatacenterLimits> findLimitsByEnterprise(final Integer enterpriseId)
    {
        Enterprise enterprise = getEnterprise(enterpriseId);

        return repo.findLimitsByEnterprise(enterprise);
    }

    /**
     * Checks enterprise and datacenter exists and have a limits relation (datacenter allowed by
     * enterprise).
     */
    @Transactional(readOnly = true)
    public DatacenterLimits findLimitsByEnterpriseAndDatacenter(final Integer enterpriseId,
        final Integer datacenterId)
    {
        Enterprise enterprise = getEnterprise(enterpriseId);
        Datacenter datacenter = datacenterService.getDatacenter(datacenterId);

        return repo.findLimitsByEnterpriseAndDatacenter(enterprise, datacenter);
    }

    /**
     * Checks enterprise and datacenter exists and have a limits relation (datacenter allowed by
     * enterprise).
     */
    @Transactional(readOnly = true)
    public DatacenterLimits findLimitsByEnterpriseVMTShared(final Integer enterpriseId,
        final Integer datacenterId)
    {
        Enterprise enterprise = getAnyEnterprise(enterpriseId);
        Datacenter datacenter = datacenterService.getDatacenter(datacenterId);

        return repo.findLimitsByEnterpriseAndDatacenter(enterprise, datacenter);
    }

    @Transactional(readOnly = true)
    public Collection<DatacenterLimits> findLimitsByDatacenter(final Integer datacenterId)
    {
        Datacenter datacenter = datacenterService.getDatacenter(datacenterId);

        return repo.findLimitsByDatacenter(datacenter);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public DatacenterLimits createDatacenterLimits(final Integer enterpriseId,
        final Integer datacenterId, final DatacenterLimitsDto dto)
    {
        Enterprise enterprise = getEnterprise(enterpriseId);
        Datacenter datacenter = null;
        if (datacenterId != null)
        {
            datacenter = datacenterService.getDatacenter(datacenterId);
        }
        else
        {
            datacenter = getDatacenter(dto);
        }

        if (repo.findLimitsByEnterpriseAndDatacenter(enterprise, datacenter) != null)
        {
            addConflictErrors(APIError.LIMITS_DUPLICATED);
            flushErrors();
        }

        DatacenterLimits limit =
            new DatacenterLimits(enterprise, datacenter, dto.getRamSoftLimitInMb(), dto
                .getCpuCountSoftLimit(), dto.getHdSoftLimitInMb(), dto.getRamHardLimitInMb(), dto
                .getCpuCountHardLimit(), dto.getHdHardLimitInMb(), dto.getStorageSoft(), dto
                .getStorageHard(), dto.getPublicIpsSoft(), dto.getPublicIpsHard(), dto
                .getVlansSoft(), dto.getVlansHard());

        if (!limit.isValid())
        {
            addValidationErrors(limit.getValidationErrors());
            flushErrors();
        }

        isValidDatacenterLimit(limit);

        repo.insertLimit(limit);

        return limit;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public DatacenterLimits updateDatacenterLimits(final Integer enterpriseId,
        final Integer limitId, final DatacenterLimitsDto dto)
    {
        Enterprise enterprise = getEnterprise(enterpriseId);
        DatacenterLimits old = findLimitsByEnterpriseAndIdentifier(enterprise, limitId);

        old.setRamLimitsInMb(new Limit((long) dto.getRamSoftLimitInMb(), (long) dto
            .getRamHardLimitInMb()));
        old.setCpuCountLimits(new Limit((long) dto.getCpuCountSoftLimit(), (long) dto
            .getCpuCountHardLimit()));
        old.setHdLimitsInMb(new Limit(dto.getHdSoftLimitInMb(), dto.getHdHardLimitInMb()));
        old.setRepositoryLimits(new Limit(dto.getRepositorySoftLimitsInMb(), dto
            .getRepositoryHardLimitsInMb()));
        old.setStorageLimits(new Limit(dto.getStorageSoft(), dto.getStorageHard()));
        old.setPublicIPLimits(new Limit(dto.getPublicIpsSoft(), dto.getPublicIpsHard()));
        old.setVlansLimits(new Limit(dto.getVlansSoft(), dto.getVlansHard()));

        if (!old.isValid())
        {
            addValidationErrors(old.getValidationErrors());
            flushErrors();
        }

        isValidDatacenterLimit(old);

        repo.updateLimit(old);

        return old;
    }

    protected void isValidDatacenterLimit(final DatacenterLimits dcLimits)
    {
        // community dummy impl (no limit check)
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void deleteDatacenterLimits(final Integer enterpriseId, final Integer limitId)
    {
        Enterprise enterprise = getEnterprise(enterpriseId);

        DatacenterLimits limit = findLimitsByEnterpriseAndIdentifier(enterprise, limitId);

        Collection<VirtualDatacenter> vdcs =
            vdcRepo.findByEnterpriseAndDatacenter(enterprise, limit.getDatacenter(), 0, 0, "",
                VirtualDatacenter.OrderByEnum.NAME, true);

        if (vdcs != null && !vdcs.isEmpty())
        {
            addConflictErrors(APIError.DATACENTER_LIMIT_DELETE_VDCS);
            flushErrors();
        }

        repo.deleteLimit(limit);
    }

    @Transactional(readOnly = true)
    private Datacenter getDatacenter(final DatacenterLimitsDto dto)
    {
        RESTLink datacenterLink = dto.searchLink("datacenter");

        if (datacenterLink == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_DATACENTER);
            flushErrors();
        }

        String buildPath =
            buildPath(DatacentersResource.DATACENTERS_PATH, DatacenterResource.DATACENTER_PARAM);
        MultivaluedMap<String, String> values =
            URIResolver.resolveFromURI(buildPath, datacenterLink.getHref());

        if (values.isEmpty())
        {
            addNotFoundErrors(APIError.NON_EXISTENT_DATACENTER);
            flushErrors();
        }

        Integer datacenterId = Integer.valueOf(values.getFirst(DatacenterResource.DATACENTER));

        return datacenterService.getDatacenter(datacenterId);
    }

    protected void isValidEnterprise(final Enterprise enterprise)
    {
        if (!enterprise.isValid())
        {
            addValidationErrors(enterprise.getValidationErrors());
        }

        flushErrors();
    }

    @Transactional(readOnly = true)
    public Collection<Privilege> findAllPrivileges()
    {
        return repo.findAllPrivileges();
    }

    @Transactional(readOnly = true)
    public Privilege getPrivilege(final Integer id)
    {
        Privilege privilege = repo.findPrivilegeById(id);
        if (privilege == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_PRIVILEGE);
            flushErrors();
        }

        return privilege;
    }

    @Transactional(readOnly = true)
    public Collection<Privilege> getAllPrivileges()
    {
        return repo.findAllPrivileges();
    }

    @Transactional(readOnly = true)
    public RoleLdap getRoleLdap(final String roleLdap)
    {
        List<RoleLdap> list = repo.findRoleLdapByRoleLdap(roleLdap);
        if (list == null || list.isEmpty())
        {
            addNotFoundErrors(APIError.NON_EXISTENT_ROLELDAP);
            flushErrors();
        }
        else if (list.size() > 1)
        {
            addConflictErrors(APIError.MULTIPLE_ENTRIES_ROLELDAP);
            flushErrors();
        }
        return list.get(0);
    }

    @Transactional(readOnly = true)
    public RoleLdap getRoleLdapOrNull(final String roleLdap)
    {
        List<RoleLdap> list = repo.findRoleLdapByRoleLdap(roleLdap);
        if (list.size() > 1)
        {
            addConflictErrors(APIError.MULTIPLE_ENTRIES_ROLELDAP);
            flushErrors();
        }

        return list == null || list.isEmpty() ? null : list.get(0);
    }

    @Transactional(readOnly = true)
    public Role findRoleById(final Integer id)
    {
        return repo.findRoleById(id);
    }

    @Transactional(readOnly = true)
    public List<Privilege> findPrivilegesByRole(final Role role)
    {
        return repo.findPrivilegesByRole(role);
    }

    protected void isValidEnterpriseLimit(final Enterprise old)
    {
        // community dummy impl (no limit check)

    }

    private PricingTemplate findPricingTemplate(final Integer id)
    {
        PricingTemplate pt = pricingRep.findPricingTemplateById(id);
        if (pt == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_PRICING_TEMPLATE);
            flushErrors();
        }
        return pt;
    }

    private Integer getPricingTemplateId(final EnterpriseDto dto)
    {
        RESTLink pt = dto.searchLink(PricingTemplateResource.PRICING_TEMPLATE);

        if (pt == null)
        {
            addValidationErrors(APIError.MISSING_PRICING_TEMPLATE_LINK);
            flushErrors();
        }

        String buildPath =
            buildPath(PricingTemplatesResource.PRICING_TEMPLATES_PATH,
                PricingTemplateResource.PRICING_TEMPLATE_PARAM);
        MultivaluedMap<String, String> values = URIResolver.resolveFromURI(buildPath, pt.getHref());

        if (values == null || !values.containsKey(PricingTemplateResource.PRICING_TEMPLATE))
        {
            addNotFoundErrors(APIError.PRICING_TEMPLATE_PARAM_NOT_FOUND);
            flushErrors();
        }

        Integer pricingTemplateId =
            Integer.valueOf(values.getFirst(PricingTemplateResource.PRICING_TEMPLATE));
        return pricingTemplateId;
    }

    protected void removeEnterpriseProperties(final Enterprise enterprise)
    {
        // PREMIUM
    }
}
