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
import java.util.UUID;

import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.api.config.ConfigService;
import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.resources.cloud.PrivateNetworkResource;
import com.abiquo.api.services.DefaultApiService;
import com.abiquo.api.services.NetworkService;
import com.abiquo.api.services.UserService;
import com.abiquo.api.spring.security.SecurityService;
import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.model.transport.error.CommonError;
import com.abiquo.server.core.cloud.NodeVirtualImage;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualDatacenterDto;
import com.abiquo.server.core.cloud.VirtualDatacenterRep;
import com.abiquo.server.core.common.Limit;
import com.abiquo.server.core.enterprise.DatacenterLimits;
import com.abiquo.server.core.enterprise.DatacenterLimitsDAO;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.User;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.InfrastructureRep;
import com.abiquo.server.core.infrastructure.network.Network;
import com.abiquo.server.core.infrastructure.network.VLANNetwork;
import com.abiquo.server.core.infrastructure.network.VLANNetworkDto;
import com.abiquo.server.core.infrastructure.storage.VolumeManagement;

@Service
@Transactional(readOnly = true)
public class VirtualDatacenterService extends DefaultApiService
{

    public static final String FENCE_MODE = "bridge";

    // Used services
    @Autowired
    UserService userService;

    @Autowired
    InfrastructureRep datacenterRepo;

    // New repos and DAOs
    @Autowired
    VirtualDatacenterRep repo;

    @Autowired
    DatacenterLimitsDAO datacenterLimitsDao;

    @Autowired
    NetworkService networkService;

    @Autowired
    SecurityService securityService;

    public VirtualDatacenterService()
    {

    }

    // use this to initialize it for tests
    public VirtualDatacenterService(final EntityManager em)
    {
        repo = new VirtualDatacenterRep(em);
        datacenterRepo = new InfrastructureRep(em);
        datacenterLimitsDao = new DatacenterLimitsDAO(em);
        userService = new UserService(em);
        datacenterLimitsDao = new DatacenterLimitsDAO(em);
        securityService = new SecurityService();
        networkService = new NetworkService(em);
    }

    public Collection<VirtualDatacenter> getVirtualDatacenters(final Enterprise enterprise,
        final Datacenter datacenter)
    {
        User user = userService.getCurrentUser();
        return getVirtualDatacenters(enterprise, datacenter, user);
    }

    Collection<VirtualDatacenter> getVirtualDatacenters(Enterprise enterprise,
        final Datacenter datacenter, final User user)
    {
        // boolean findByUser =
        // user != null
        // && (user.getRole().getType() == Role.Type.USER && !StringUtils.isEmpty(user
        // .getAvailableVirtualDatacenters()));
        boolean findByUser =
            user != null && !securityService.canManageOtherEnterprises()
                && !securityService.canManageOtherUsers()
                && !StringUtils.isEmpty(user.getAvailableVirtualDatacenters());

        if (enterprise == null && user != null)
        {
            enterprise = user.getEnterprise();
        }

        if (findByUser)
        {
            return repo.findByEnterpriseAndDatacenter(enterprise, datacenter, user);
        }
        else
        {
            return repo.findByEnterpriseAndDatacenter(enterprise, datacenter);
        }
    }

    public VirtualDatacenter getVirtualDatacenter(final Integer id)
    {
        VirtualDatacenter vdc = repo.findById(id);
        if (vdc == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_VIRTUAL_DATACENTER);
            flushErrors();
        }
        return vdc;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public VirtualDatacenter createVirtualDatacenter(final VirtualDatacenterDto dto,
        final Datacenter datacenter, final Enterprise enterprise) throws Exception
    {
        if (!isValidEnterpriseDatacenter(enterprise, datacenter))
        {
            addForbiddenErrors(APIError.DATACENTER_NOT_ALLOWED);
            flushErrors();
        }

        Network network = createNetwork();
        VirtualDatacenter vdc = createVirtualDatacenter(dto, datacenter, enterprise, network);

        // set as default vlan (as it is the first one) and create it.
        VLANNetwork vlan =
            networkService.createPrivateNetwork(vdc.getId(), PrivateNetworkResource
                .createPersistenceObject(dto.getVlan()), false);

        // find the default vlan stablished by the enterprise-datacenter limits
        DatacenterLimits dcLimits =
            datacenterRepo.findDatacenterLimits(vdc.getEnterprise(), vdc.getDatacenter());
        if (dcLimits.getDefaultVlan() != null)
        {
            vdc.setDefaultVlan(dcLimits.getDefaultVlan());
        }
        else
        {
            vdc.setDefaultVlan(vlan);
        }
        repo.update(vdc);

        assignVirtualDatacenterToUser(vdc);

        return vdc;
    }

    private void assignVirtualDatacenterToUser(final VirtualDatacenter vdc)
    {
        User currentUser = userService.getCurrentUser();

        // if (currentUser.getRole().getType() == Role.Type.USER
        // && currentUser.getAvailableVirtualDatacenters() != null)
        if (!securityService.canManageOtherEnterprises()
            && !securityService.canManageOtherUsers()
            && org.springframework.util.StringUtils.hasText(currentUser
                .getAvailableVirtualDatacenters()))
        {
            String availableVirtualDatacenters =
                currentUser.getAvailableVirtualDatacenters() + "," + vdc.getId();
            currentUser.setAvailableVirtualDatacenters(availableVirtualDatacenters);

            userService.updateUser(currentUser);
        }
    }

    protected boolean isValidEnterpriseDatacenter(final Enterprise enterprise,
        final Datacenter datacenter)
    {
        return true;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public VirtualDatacenter updateVirtualDatacenter(final Integer id,
        final VirtualDatacenterDto dto)
    {
        VirtualDatacenter vdc = getVirtualDatacenter(id);
        userService.checkCurrentEnterpriseForPostMethods(vdc.getEnterprise());
        return updateVirtualDatacenter(vdc, dto);
    }

    protected VirtualDatacenter updateVirtualDatacenter(final VirtualDatacenter vdc,
        final VirtualDatacenterDto dto)
    {
        vdc.setName(dto.getName());
        setLimits(dto, vdc);

        if (!vdc.isValid())
        {
            addValidationErrors(vdc.getValidationErrors());
            flushErrors();
        }
        if (!isValidVlanHardLimitPerVdc(vdc.getVlanHard()))
        {
            String vlanXvdc = ConfigService.getSystemProperty(ConfigService.VLAN_PER_VDC);
            String errorMsg =
                APIError.LIMITS_INVALID_HARD_LIMIT_FOR_VLANS_PER_VDC.getMessage().replace("{0}",
                    vlanXvdc);
            CommonError error =
                new CommonError(APIError.LIMITS_INVALID_HARD_LIMIT_FOR_VLANS_PER_VDC.getCode(),
                    errorMsg);
            addConflictErrors(error);
            flushErrors();
        }

        repo.update(vdc);

        return vdc;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void deleteVirtualDatacenter(final Integer id)
    {
        VirtualDatacenter vdc = getVirtualDatacenter(id);
        userService.checkCurrentEnterpriseForPostMethods(vdc.getEnterprise());

        if (repo.containsVirtualAppliances(vdc))
        {
            addConflictErrors(APIError.VIRTUAL_DATACENTER_CONTAINS_VIRTUAL_APPLIANCES);
        }

        if (repo.containsResources(vdc, VolumeManagement.DISCRIMINATOR))
        {
            addConflictErrors(APIError.VIRTUAL_DATACENTER_CONTAINS_RESOURCES);
        }

        flushErrors();

        repo.delete(vdc);
    }

    private Network createNetwork()
    {
        Network network = new Network(UUID.randomUUID().toString());
        repo.insertNetwork(network);
        return network;
    }

    private VirtualDatacenter createVirtualDatacenter(final VirtualDatacenterDto dto,
        final Datacenter datacenter, final Enterprise enterprise, final Network network)
    {
        VirtualDatacenter vdc =
            new VirtualDatacenter(enterprise, datacenter, network, dto.getHypervisorType(), dto
                .getName());

        setLimits(dto, vdc);
        validateVirtualDatacenter(vdc, dto.getVlan(), datacenter);

        repo.insert(vdc);
        return vdc;
    }

    private void setLimits(final VirtualDatacenterDto dto, final VirtualDatacenter vdc)
    {
        vdc.setCpuCountLimits(new Limit((long) dto.getCpuCountSoftLimit(), (long) dto
            .getCpuCountHardLimit()));
        vdc.setHdLimitsInMb(new Limit(dto.getHdSoftLimitInMb(), dto.getHdHardLimitInMb()));
        vdc.setRamLimitsInMb(new Limit((long) dto.getRamSoftLimitInMb(), (long) dto
            .getRamHardLimitInMb()));
        vdc.setStorageLimits(new Limit(dto.getStorageSoft(), dto.getStorageHard()));
        vdc.setVlansLimits(new Limit(dto.getVlansSoft(), dto.getVlansHard()));
        vdc.setPublicIPLimits(new Limit(dto.getPublicIpsSoft(), dto.getPublicIpsHard()));
    }

    private void validateVirtualDatacenter(final VirtualDatacenter vdc, final VLANNetworkDto vlan,
        final Datacenter datacenter)
    {
        if (vlan == null)
        {
            addValidationErrors(APIError.NETWORK_INVALID_CONFIGURATION);
        }

        if (!vdc.isValid())
        {
            addValidationErrors(vdc.getValidationErrors());
        }
        else if (!isValidVlanHardLimitPerVdc(vdc.getVlanHard()))
        {

            String vlanXvdc = ConfigService.getSystemProperty(ConfigService.VLAN_PER_VDC);
            String errorMsg =
                APIError.LIMITS_INVALID_HARD_LIMIT_FOR_VLANS_PER_VDC.getMessage().replace("{0}",
                    vlanXvdc);
            CommonError error =
                new CommonError(APIError.LIMITS_INVALID_HARD_LIMIT_FOR_VLANS_PER_VDC.getCode(),
                    errorMsg);
            addValidationErrors(error);
        }

        if (vdc.getHypervisorType() != null
            && !isValidHypervisorForDatacenter(vdc.getHypervisorType(), datacenter))
        {
            addValidationErrors(APIError.VIRTUAL_DATACENTER_INVALID_HYPERVISOR_TYPE);
        }

        flushErrors();
    }

    private boolean isValidVlanHardLimitPerVdc(final long vlansHard)
    {
        String limitS = ConfigService.getVlanPerVdc();
        int limit = Integer.valueOf(limitS);

        return limit == 0 || limit >= vlansHard;
    }

    private boolean isValidHypervisorForDatacenter(final HypervisorType type,
        final Datacenter datacenter)
    {
        return datacenterRepo.findHypervisors(datacenter).contains(type);
    }

    public Collection<NodeVirtualImage> getNodeVirtualImageByEnterprise(final Enterprise enterprise)
    {
        return repo.findNodeVirtualImageByEnterprise(enterprise);
    }

}
