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
import com.abiquo.api.exceptions.NotFoundException;
import com.abiquo.api.resources.DatacenterResource;
import com.abiquo.api.resources.DatacentersResource;
import com.abiquo.api.util.URIResolver;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualDatacenterRep;
import com.abiquo.server.core.common.Limit;
import com.abiquo.server.core.enterprise.DatacenterLimits;
import com.abiquo.server.core.enterprise.DatacenterLimitsDto;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.EnterpriseDto;
import com.abiquo.server.core.enterprise.EnterpriseRep;
import com.abiquo.server.core.enterprise.Role;
import com.abiquo.server.core.enterprise.User;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.Machine;
import com.abiquo.server.core.infrastructure.MachineDto;

@Service
@Transactional(readOnly = true)
public class EnterpriseService extends DefaultApiService
{
    @Autowired
    EnterpriseRep repo;

    @Autowired
    VirtualDatacenterRep vdcRepo;

    @Autowired
    MachineService machineService;

    @Autowired
    UserService userService;

    @Autowired
    DatacenterService datacenterService;

    public EnterpriseService()
    {

    }

    public EnterpriseService(EntityManager em)
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

    public Collection<Enterprise> getEnterprises(String filterName, Integer offset,
        Integer numResults)
    {
        User user = userService.getCurrentUser();
        if (user.getRole().getType() == Role.Type.ENTERPRISE_ADMIN)
        {
            return Collections.singletonList(user.getEnterprise());
        }

        if (!StringUtils.isEmpty(filterName))
        {
            return repo.findByNameAnywhere(filterName);
        }

        return repo.findAll(offset, numResults);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Enterprise addEnterprise(EnterpriseDto dto)
    {
        if (repo.existsAnyWithName(dto.getName()))
        {
            errors.add(APIError.ENTERPRISE_DUPLICATED_NAME);
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

    public Enterprise getEnterprise(Integer id)
    {
        Enterprise enterprise = repo.findById(id);
        if (enterprise == null)
        {
            throw new NotFoundException(APIError.NON_EXISTENT_ENTERPRISE);
        }

        // userService.checkEnterpriseAdminCredentials(enterprise);
        userService.checkCurrentEnterprise(enterprise);
        return enterprise;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Enterprise modifyEnterprise(Integer enterpriseId, EnterpriseDto dto)
    {
        Enterprise old = repo.findById(enterpriseId);
        if (old == null)
        {
            throw new NotFoundException(APIError.NON_EXISTENT_ENTERPRISE);
        }

        userService.checkEnterpriseAdminCredentials(old);

        if (dto.getName().isEmpty())
        {
            errors.add(APIError.ENTERPRISE_EMPTY_NAME);
            flushErrors();
        }

        if (repo.existsAnyOtherWithName(old, dto.getName()))
        {
            errors.add(APIError.ENTERPRISE_DUPLICATED_NAME);
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

        repo.update(old);
        return old;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void removeEnterprise(Integer id)
    {
        Enterprise enterprise = getEnterprise(id);
        User user = userService.getCurrentUser();

        if (user.getEnterprise().equals(enterprise))
        {
            errors.add(APIError.ENTERPRISE_DELETE_OWN_ENTERPRISE);
            flushErrors();
        }

        Collection<VirtualDatacenter> vdcs = vdcRepo.findByEnterprise(enterprise);
        if (!vdcs.isEmpty())
        {
            errors.add(APIError.ENTERPRISE_DELETE_ERROR_WITH_VDCS);
            flushErrors();
        }

        repo.delete(enterprise);
    }

    public List<Machine> findReservedMachines(Integer enterpriseId)
    {
        return repo.findReservedMachines(getEnterprise(enterpriseId));
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Machine reserveMachine(MachineDto machineDto, Integer enterpriseId)
    {
        return reserveMachine(machineDto.getId(), enterpriseId);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Machine reserveMachine(Integer machineId, Integer enterpriseId)
    {
        Machine machine = machineService.getMachine(machineId);
        Enterprise enterprise = getEnterprise(enterpriseId);
        repo.reserveMachine(machine, enterprise);

        return machine;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void releaseMachine(Integer machineId, Integer enterpriseId)
    {
        Enterprise enterprise = getEnterprise(enterpriseId);
        Machine machine = repo.findReservedMachine(enterprise, machineId);
        if (machine == null)
        {
            throw new NotFoundException(APIError.NON_EXISTENT_MACHINE);
        }

        repo.releaseMachine(machine);
    }

    public DatacenterLimits findLimitsByEnterpriseAndIdentifier(Integer enterpriseId,
        Integer limitId)
    {
        Enterprise enterprise = getEnterprise(enterpriseId);

        return findLimitsByEnterpriseAndIdentifier(enterprise, limitId);
    }

    private DatacenterLimits findLimitsByEnterpriseAndIdentifier(Enterprise enterprise,
        Integer limitId)
    {
        DatacenterLimits limit = repo.findLimitsByEnterpriseAndIdentifier(enterprise, limitId);

        if (limit == null)
        {
            throw new NotFoundException(APIError.LIMITS_NOT_EXIST);
        }

        return limit;
    }

    public Collection<DatacenterLimits> findLimitsByEnterprise(Integer enterpriseId)
    {
        Enterprise enterprise = getEnterprise(enterpriseId);

        return repo.findLimitsByEnterprise(enterprise);
    }

    public DatacenterLimits findLimitsByEnterpriseAndDatacenter(Integer enterpriseId,
        Integer datacenterId)
    {
        Enterprise enterprise = getEnterprise(enterpriseId);
        Datacenter datacenter = datacenterService.getDatacenter(datacenterId);

        return repo.findLimitsByEnterpriseAndDatacenter(enterprise, datacenter);
    }

    public Collection<DatacenterLimits> findLimitsByDatacenter(Integer datacenterId)
    {
        Datacenter datacenter = datacenterService.getDatacenter(datacenterId);

        return repo.findLimitsByDatacenter(datacenter);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public DatacenterLimits createDatacenterLimits(Integer enterpriseId, Integer datacenterId,
        DatacenterLimitsDto dto)
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
            errors.add(APIError.LIMITS_DUPLICATED);
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
    public DatacenterLimits updateDatacenterLimits(Integer enterpriseId, Integer limitId,
        DatacenterLimitsDto dto)
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

    protected void isValidDatacenterLimit(DatacenterLimits dcLimits)
    {
        // community dummy impl (no limit check)
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void deleteDatacenterLimits(Integer enterpriseId, Integer limitId)
    {
        Enterprise enterprise = getEnterprise(enterpriseId);

        DatacenterLimits limit = findLimitsByEnterpriseAndIdentifier(enterprise, limitId);

        repo.deleteLimit(limit);
    }

    private Datacenter getDatacenter(DatacenterLimitsDto dto)
    {
        RESTLink datacenterLink = dto.searchLink("datacenter");

        if (datacenterLink == null)
        {
            throw new NotFoundException(APIError.NON_EXISTENT_DATACENTER);
        }

        String buildPath =
            buildPath(DatacentersResource.DATACENTERS_PATH, DatacenterResource.DATACENTER_PARAM);
        MultivaluedMap<String, String> values =
            URIResolver.resolveFromURI(buildPath, datacenterLink.getHref());

        if (values.isEmpty())
        {
            throw new NotFoundException(APIError.NON_EXISTENT_DATACENTER);
        }

        Integer datacenterId = Integer.valueOf(values.getFirst(DatacenterResource.DATACENTER));

        return datacenterService.getDatacenter(datacenterId);
    }

    protected void isValidEnterprise(Enterprise enterprise)
    {
        if (!enterprise.isValid())
        {
            validationErrors.addAll(enterprise.getValidationErrors());
        }

        flushErrors();
    }

    protected void isValidEnterpriseLimit(Enterprise old)
    {
        // community dummy impl (no limit check)

    }
}
