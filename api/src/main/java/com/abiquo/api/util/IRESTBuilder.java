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

import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.appslibrary.Category;
import com.abiquo.server.core.appslibrary.CategoryDto;
import com.abiquo.server.core.appslibrary.TemplateDefinitionDto;
import com.abiquo.server.core.appslibrary.TemplateDefinitionListDto;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplate;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualApplianceDto;
import com.abiquo.server.core.cloud.VirtualApplianceStateDto;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.config.LicenseDto;
import com.abiquo.server.core.config.SystemPropertyDto;
import com.abiquo.server.core.enterprise.DatacenterLimitsDto;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.EnterpriseDto;
import com.abiquo.server.core.enterprise.EnterprisePropertiesDto;
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
import com.abiquo.server.core.infrastructure.network.VLANNetwork;
import com.abiquo.server.core.infrastructure.network.VLANNetworkDto;
import com.abiquo.server.core.infrastructure.network.VMNetworkConfiguration;
import com.abiquo.server.core.infrastructure.storage.DiskManagement;
import com.abiquo.server.core.infrastructure.storage.Tier;
import com.abiquo.server.core.infrastructure.storage.VolumeManagement;
import com.abiquo.server.core.pricing.CostCode;
import com.abiquo.server.core.pricing.CostCodeCurrencyDto;
import com.abiquo.server.core.pricing.Currency;
import com.abiquo.server.core.pricing.CurrencyDto;
import com.abiquo.server.core.pricing.PricingCostCodeDto;
import com.abiquo.server.core.pricing.PricingTemplate;
import com.abiquo.server.core.pricing.PricingTemplateDto;
import com.abiquo.server.core.pricing.PricingTierDto;
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

    public List<RESTLink> buildMachineLinks(Integer datacenterId, Integer rackId,
        Boolean managedRack, Enterprise enterprise, MachineDto machine);

    public List<RESTLink> buildRemoteServiceLinks(Integer datacenterId,
        RemoteServiceDto remoteService);

    public List<RESTLink> buildRoleLinks(RoleDto role);

    public List<RESTLink> buildRoleLinks(Integer enterpriseId, RoleDto role);

    public List<RESTLink> buildRoleLdapLinks(final Integer roleId, final RoleLdapDto roleLdap);

    public List<RESTLink> buildPrivilegeLink(final PrivilegeDto privilege);

    public List<RESTLink> buildPrivilegeListLink(PrivilegeDto privilege);

    public List<RESTLink> buildEnterpriseLinks(EnterpriseDto enterprise);

    public List<RESTLink> buildEnterprisePropertiesLinks(final Integer enterpriseId,
        final EnterprisePropertiesDto enterpriseProperties);

    public List<RESTLink> buildUserLinks(Integer enterpriseId, Integer roleId, UserDto user);

    public List<RESTLink> buildTemplateDefinitionListLinks(Integer datacenterId,
        TemplateDefinitionListDto templateDefinitionList);

    public List<RESTLink> buildTemplateDefinitionLinks(Integer enterpriseId,
        TemplateDefinitionDto templateDefinition, Category category);

    public List<RESTLink> buildVirtualDatacenterLinks(VirtualDatacenter vdc, Integer datacenterId,
        Integer enterpriseId);

    public List<RESTLink> buildVirtualApplianceLinks(VirtualApplianceDto vapp, Integer vdcId,
        Integer enterpriseId);

    public List<RESTLink> buildPrivateNetworkLinks(Integer virtualDatacenterId,
        VLANNetworkDto network);

    public List<RESTLink> buildPublicNetworkLinks(final Integer datacenterId,
        final VLANNetwork network);

    public List<RESTLink> buildDatacenterRepositoryLinks(final Integer enterpriseId,
        final Integer dcId, final String dcName, final Integer repoId);

    public List<RESTLink> buildVirtualMachineTemplateLinks(final Integer enterpriseId,
        final Integer dcId, final VirtualMachineTemplate template,
        final VirtualMachineTemplate master);

    public RESTLink buildVirtualMachineTemplateLink(final Integer enterpriseId, final Integer dcId,
        final Integer virtualImageId);

    /*
     * Premium methods
     */
    public List<RESTLink> buildStoragePoolLinks(Integer datacenterId, Integer deviceId,
        final Integer tierId, String poolId);

    public List<RESTLink> buildDatastoreLinks(Integer datacenterId, Integer rackId,
        Integer machineId, Datastore datastore);

    public List<RESTLink> buildVirtualMachineAdminLinks(Integer datacenterId, Integer rackId,
        Integer machineId, Integer enterpriseId, Integer userId, HypervisorType machineType,
        VirtualAppliance vapp, Integer vmId);

    public List<RESTLink> buildVirtualMachineCloudLinks(Integer vdcId, Integer vappId,
        VirtualMachine vm, boolean chefEnabled, final Integer[] volumeIds, final Integer[] diskIds,
        final List<IpPoolManagement> ips);

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
        final Integer vappId, final VirtualMachine vm, final Integer datacenterId,
        final Integer rackId, final Integer machineId, final Integer enterpriseId,
        final Integer userId, boolean chefEnabled, Integer[] volumeIds, Integer[] diksIds,
        final List<IpPoolManagement> ips, final HypervisorType vdcType, final VirtualAppliance vapp);

    public List<RESTLink> buildEnterpriseExclusionRuleLinks(
        final EnterpriseExclusionRuleDto enterpriseExclusionDto,
        EnterpriseExclusionRule enterpriseExclusion);

    public List<RESTLink> buildMachineLoadRuleLinks(final MachineLoadRuleDto mlrDto,
        final MachineLoadRule mlr);

    public List<RESTLink> buildFitPolicyRuleLinks(FitPolicyRuleDto fprDto, FitPolicyRule fpr);

    public List<RESTLink> buildVirtualApplianceStateLinks(VirtualApplianceStateDto dto, Integer id,
        Integer vdcId);

    public List<RESTLink> buildVirtualMachineStateLinks(Integer vappId, Integer vdcId, Integer vmId);

    public List<RESTLink> buildCurrencyLinks(CurrencyDto currencyDto, Currency currency);

    public List<RESTLink> buildPricingTemplateLinks(final Integer currencyId,
        final PricingTemplateDto pricingTemplate);

    public List<RESTLink> buildCostCodeLinks(final Integer costCodeId);

    public List<RESTLink> buildCostCodeCurrencyLinks(CostCode costCode, Currency currency,
        CostCodeCurrencyDto dto);

    public List<RESTLink> buildPricingCostCodeLinks(CostCode costCode,
        PricingTemplate pricingTemplate, PricingCostCodeDto dto);

    public List<RESTLink> buildPricingTierLinks(Tier tier, PricingTemplate pricingTemplate,
        PricingTierDto dto);

    public List<RESTLink> buildPublicNetworksLinks(Integer datacenterId);

    public List<RESTLink> buildPublicIpLinks(final Integer datacenterId, final IpPoolManagement ip);

    public List<RESTLink> buildPublicIpRasdLinks(final Integer vdcId, IpPoolManagement ip);

    public List<RESTLink> buildVMNetworkConfigurationLinks(final Integer vdcId,
        final Integer vappId, final Integer vmId, VMNetworkConfiguration config);

    public List<RESTLink> buildNICLinks(IpPoolManagement ip);

    public List<RESTLink> buildExternalNetworkLinks(Integer enterpriseId, VLANNetworkDto dto);

    public List<RESTLink> buildExternalNetworkByDatacenterLinks(Integer enterpriseId,
        Integer limitId, VLANNetwork network);

    public List<RESTLink> buildExternalNetworksByDatacenterLinks(Integer enterpriseId,
        Integer limitId);

    public List<RESTLink> buildExternalIpRasdLinks(final Integer entId, final Integer limitId,
        IpPoolManagement ip);

    public List<RESTLink> buildDiskLinks(final DiskManagement disk, final Integer vdcId,
        final Integer vappId);

    public List<RESTLink> buildCategoryLinks(final Category category);

    public List<RESTLink> buildVirtualDatacenterDiskLinks(DiskManagement disk);

    public RESTLink buildUserLink(Integer enterpriseId, Integer userId);

    public List<RESTLink> buildVirtualDatacenterTierLinks(Integer virtualDatacenterId, Integer id);

    public RESTLink buildMovedVolumeLinks(VolumeManagement movedVolume);

    public RESTLink buildVirtualMachineLink(Integer vdc, Integer vapp, Integer vm);

    public RESTLink buildVirtualMachineTaskLink(Integer vdc, Integer vapp, Integer vm, String taskId);

}
