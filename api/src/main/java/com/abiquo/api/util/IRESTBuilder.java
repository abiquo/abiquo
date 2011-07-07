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

package com.abiquo.api.util;

import java.util.List;

import org.apache.wink.server.utils.LinkBuilders;

import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.appslibrary.OVFPackageDto;
import com.abiquo.server.core.appslibrary.OVFPackageListDto;
import com.abiquo.server.core.cloud.VirtualApplianceDto;
import com.abiquo.server.core.cloud.VirtualDatacenterDto;
import com.abiquo.server.core.config.LicenseDto;
import com.abiquo.server.core.config.SystemPropertyDto;
import com.abiquo.server.core.enterprise.DatacenterLimitsDto;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.EnterpriseDto;
import com.abiquo.server.core.enterprise.PrivilegeDto;
import com.abiquo.server.core.enterprise.RoleDto;
import com.abiquo.server.core.enterprise.RoleLdapDto;
import com.abiquo.server.core.enterprise.UserDto;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.DatacenterDto;
import com.abiquo.server.core.infrastructure.Datastore;
import com.abiquo.server.core.infrastructure.MachineDto;
import com.abiquo.server.core.infrastructure.RackDto;
import com.abiquo.server.core.infrastructure.RemoteServiceDto;
import com.abiquo.server.core.infrastructure.management.RasdManagement;
import com.abiquo.server.core.infrastructure.network.IpPoolManagement;
import com.abiquo.server.core.infrastructure.network.IpPoolManagementDto;
import com.abiquo.server.core.infrastructure.network.VLANNetworkDto;
import com.abiquo.server.core.infrastructure.storage.VolumeManagement;
import com.abiquo.server.core.scheduler.EnterpriseExclusionRule;
import com.abiquo.server.core.scheduler.EnterpriseExclusionRuleDto;
import com.abiquo.server.core.scheduler.FitPolicyRule;
import com.abiquo.server.core.scheduler.FitPolicyRuleDto;
import com.abiquo.server.core.scheduler.MachineLoadRule;
import com.abiquo.server.core.scheduler.MachineLoadRuleDto;
import com.abiquo.server.core.util.PagedList;

public interface IRESTBuilder
{
    public RESTBuilder injectProcessor(LinkBuilders linkProcessor);

    public List<RESTLink> buildDatacenterLinks(DatacenterDto datacenter);

    public List<RESTLink> buildRackLinks(final Integer datacenterId, final RackDto rack);

    public List<RESTLink> buildMachineLinks(Integer datacenterId, Integer rackId, MachineDto machine);

    public List<RESTLink> buildRemoteServiceLinks(Integer datacenterId,
        RemoteServiceDto remoteService);

    public List<RESTLink> buildRoleLinks(RoleDto role);

    public List<RESTLink> buildRoleLinks(Integer enterpriseId, RoleDto role);

    public List<RESTLink> buildRoleLdapLinks(final Integer roleId, final RoleLdapDto roleLdap);

    public List<RESTLink> buildPrivilegeLink(final PrivilegeDto privilege);

    public List<RESTLink> buildEnterpriseLinks(EnterpriseDto enterprise);

    public List<RESTLink> buildUserLinks(Integer enterpriseId, Integer roleId, UserDto user);

    public List<RESTLink> buildOVFPackageListLinks(Integer datacenterId,
        OVFPackageListDto ovfPackageList);

    public List<RESTLink> buildOVFPackageLinks(Integer datacenterId, OVFPackageDto ovfPackage);

    public List<RESTLink> buildVirtualDatacenterLinks(VirtualDatacenterDto vdc,
        Integer datacenterId, Integer enterpriseId);

    public List<RESTLink> buildVirtualApplianceLinks(VirtualApplianceDto vapp, Integer vdcId,
        Integer enterpriseId);

    public List<RESTLink> buildPrivateNetworkLinks(Integer virtualDatacenterId,
        VLANNetworkDto network);

    public List<RESTLink> buildIPAddressLink(Integer vlanId, IpPoolManagementDto ip);

    /*
     * Premium methods
     */
    public List<RESTLink> buildStoragePoolLinks(Integer datacenterId, Integer deviceId,
        final Integer tierId, String poolId);

    public List<RESTLink> buildDatastoreLinks(Integer datacenterId, Integer rackId,
        Integer machineId, Datastore datastore);

    public List<RESTLink> buildVirtualMachineAdminLinks(Integer datacenterId, Integer rackId,
        Integer machineId, Integer enterpriseId, Integer userId);

    public List<RESTLink> buildVirtualMachineCloudLinks(Integer vdcId, Integer vappId, Integer vmId);

    public List<RESTLink> buildSystemPropertyLinks(SystemPropertyDto systemProperty);

    public List<RESTLink> buildPaggingLinks(String absolutePath, PagedList< ? > list);

    public RESTLink buildEnterpriseLink(Integer enterpriseId);

    public List<RESTLink> buildLicenseLinks(LicenseDto license);

    public List<RESTLink> buildLimitsLinks(Enterprise enterprise, Datacenter datacenter,
        DatacenterLimitsDto dto);

    public List<RESTLink> buildTierLinks(final Integer datacenterId, final Integer tierId);

    public List<RESTLink> buildStorageDeviceLinks(final Integer datacenterId, final Integer deviceId);

    public List<RESTLink> buildRasdLinks(RasdManagement ip);

    public List<RESTLink> buildIpRasdLinks(IpPoolManagement ip);

    public List<RESTLink> buildVolumeInfrastructureLinks(final VolumeManagement volume);

    public List<RESTLink> buildVolumeCloudLinks(final VolumeManagement volume);

    public List<RESTLink> buildVirtualMachineCloudAdminLinks(final Integer vdcId,
        final Integer vappId, final Integer vmId, final Integer datacenterId, final Integer rackId,
        final Integer machineId, final Integer enterpriseId, final Integer userId);
    
    public List<RESTLink> buildEnterpriseExclusionRuleLinks(
        final EnterpriseExclusionRuleDto enterpriseExclusionDto,
        EnterpriseExclusionRule enterpriseExclusion);

    public List<RESTLink> buildMachineLoadRuleLinks(final MachineLoadRuleDto mlrDto,
        final MachineLoadRule mlr);

    public List<RESTLink> buildFitPolicyRuleLinks(FitPolicyRuleDto fprDto, FitPolicyRule fpr);
}
