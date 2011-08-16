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
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.resources.DatacenterResource;
import com.abiquo.api.resources.DatacentersResource;
import com.abiquo.api.resources.config.PricingTemplateResource;
import com.abiquo.api.resources.config.PricingTemplatesResource;
import com.abiquo.api.spring.security.SecurityService;
import com.abiquo.api.util.URIResolver;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.model.transport.error.CommonError;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualDatacenterRep;
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
import com.abiquo.server.core.infrastructure.Machine;
import com.abiquo.server.core.infrastructure.MachineDto;
import com.abiquo.server.core.pricing.PricingRep;
import com.abiquo.server.core.pricing.PricingTemplate;

@Service
@Transactional(readOnly = true)
public class EnterpriseService extends DefaultApiService
{
    @Autowired
    EnterpriseRep repo;

    @Autowired
    VirtualDatacenterRep vdcRepo;

    @Autowired
    PricingRep pricingRep;

    @Autowired
    MachineService machineService;

    @Autowired
    UserService userService;

    @Autowired
    DatacenterService datacenterService;

    @Autowired
    SecurityService securityService;

    public EnterpriseService()
    {

    }

    public EnterpriseService(final EntityManager em)
    {
        repo = new EnterpriseRep(em);
        vdcRepo = new VirtualDatacenterRep(em);
        machineService = new MachineService(em);
        userService = new UserService(em);
        datacenterService = new DatacenterService(em);
    }

    /**
     * Based on the spring authentication context.
     * 
     * @see SecurityContextHolder
     */
    // public Enterprise getCurrentEnterprise()
    // {
    // // AbiquoUserDetails currentUserInfo = (AbiquoUserDetails)
    // SecurityContextHolder.getContext().getAuthentication();
    //
    // User user = userService.getCurrentUser();
    //
    // return user.getEnterprise();
    //
    // // Enterprise enterprise = repo.findById(id);
    // // if (enterprise == null)
    // // {
    // // throw new NotFoundException(APIError.NON_EXISTENT_ENTERPRISE);
    // // }
    //
    // }

    public Enterprise getCurrentEnterprise()
    {
        return userService.getCurrentUser().getEnterprise();
    }

    public Collection<Enterprise> getEnterprises(final int idPricingTempl, final boolean included,
        final String filterName, final Integer offset, final Integer numResults)
    {
        User user = userService.getCurrentUser();
        // if (user.getRole().getType() == Role.Type.ENTERPRISE_ADMIN)
        if (securityService.isEnterpriseAdmin())
        {
            return Collections.singletonList(user.getEnterprise());
        }

        if (!StringUtils.isEmpty(filterName))
        {
            return repo.findByNameAnywhere(filterName);
        }

        PricingTemplate pt = null;
        if (idPricingTempl != 0)
        {
            pt = findPricingTemplate(idPricingTempl);
            return repo.findByPricingTemplate(pt, included, filterName, offset, numResults);
        }

        return repo.findAll(offset, numResults);
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

        isValidEnterprise(enterprise);

        repo.insert(enterprise);
        return enterprise;
    }

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

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Enterprise modifyEnterprise(final Integer enterpriseId, final EnterpriseDto dto)
    {
        Enterprise old = repo.findById(enterpriseId);
        if (old == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_ENTERPRISE);
            flushErrors();
        }

        userService.checkEnterpriseAdminCredentials(old);

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

        //
        if (dto.searchLink("template") != null)
        {

            PricingTemplate pricingTemplate = findPricingTemplate(getPricingTemplateId(dto));
            old.setPricingTemplate(pricingTemplate);
        }

        // if we are in community the Pricingtemplate id is not informed, is null
        // in this case we don't overwrite the old value.
        else if (dto.getIdPricingTemplate() != null)
        {
            if (dto.getIdPricingTemplate() == 0)
            {
                old.setPricingTemplate(null);
            }
            else
            {
                PricingTemplate pricingTemplate = findPricingTemplate(dto.getIdPricingTemplate());
                old.setPricingTemplate(pricingTemplate);
            }

        }

        //
        repo.update(old);
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

        repo.delete(enterprise);
    }

    protected void deleteRole(final Role role)
    {
        repo.deleteRole(role);
    }

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
        Machine machine = machineService.getMachine(machineId);
        Enterprise enterprise = getEnterprise(enterpriseId);
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
            addNotFoundErrors(APIError.NON_EXISTENT_MACHINE);
            flushErrors();
        }

        repo.releaseMachine(machine);
    }

    public DatacenterLimits findLimitsByEnterpriseAndIdentifier(final Integer enterpriseId,
        final Integer limitId)
    {
        Enterprise enterprise = getEnterprise(enterpriseId);

        return findLimitsByEnterpriseAndIdentifier(enterprise, limitId);
    }

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

    public Collection<DatacenterLimits> findLimitsByEnterprise(final Integer enterpriseId)
    {
        Enterprise enterprise = getEnterprise(enterpriseId);

        return repo.findLimitsByEnterprise(enterprise);
    }

    public DatacenterLimits findLimitsByEnterpriseAndDatacenter(final Integer enterpriseId,
        final Integer datacenterId)
    {
        Enterprise enterprise = getEnterprise(enterpriseId);
        Datacenter datacenter = datacenterService.getDatacenter(datacenterId);

        return repo.findLimitsByEnterpriseAndDatacenter(enterprise, datacenter);
    }

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
            new DatacenterLimits(enterprise,
                datacenter,
                dto.getRamSoftLimitInMb(),
                dto.getCpuCountSoftLimit(),
                dto.getHdSoftLimitInMb(),
                dto.getRamHardLimitInMb(),
                dto.getCpuCountHardLimit(),
                dto.getHdHardLimitInMb(),
                dto.getStorageSoft(),
                dto.getStorageHard(),
                dto.getPublicIpsSoft(),
                dto.getPublicIpsHard(),
                dto.getVlansSoft(),
                dto.getVlansHard());

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
            vdcRepo.findByEnterpriseAndDatacenter(enterprise, limit.getDatacenter());

        if (vdcs != null && !vdcs.isEmpty())
        {
            addConflictErrors(APIError.DATACENTER_LIMIT_DELETE_VDCS);
            flushErrors();
        }

        repo.deleteLimit(limit);
    }

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

    public Collection<Privilege> findAllPrivileges()
    {
        return repo.findAllPrivileges();
    }

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

    public Collection<Privilege> getAllPrivileges()
    {
        return repo.findAllPrivileges();
    }

    public RoleLdap getRoleLdap(final String role_ldap)
    {
        List<RoleLdap> list = repo.findRoleLdapByRoleLdap(role_ldap);
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
}
