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

package com.abiquo.api.resources.cloud;

import static com.abiquo.api.common.Assert.assertErrors;
import static com.abiquo.api.common.UriTestResolver.resolveDatacenterURI;
import static com.abiquo.api.common.UriTestResolver.resolveEnterpriseURI;
import static com.abiquo.api.common.UriTestResolver.resolveVirtualDatacentersURI;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.wink.client.ClientResponse;
import org.apache.wink.common.internal.utils.UriHelper;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.resources.AbstractJpaGeneratorIT;
import com.abiquo.api.resources.DatacenterResource;
import com.abiquo.api.resources.EnterpriseResource;
import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.cloud.Hypervisor;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualDatacenterDto;
import com.abiquo.server.core.cloud.VirtualDatacentersDto;
import com.abiquo.server.core.enterprise.DatacenterLimits;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.Role;
import com.abiquo.server.core.enterprise.User;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.Machine;
import com.abiquo.server.core.infrastructure.RemoteService;
import com.abiquo.server.core.infrastructure.network.VLANNetworkDto;

@Test
public class VirtualDatacentersResourceIT extends AbstractJpaGeneratorIT
{
    Enterprise sysEnterprise;

    @BeforeMethod
    public void setupSysadmin()
    {
        sysEnterprise = enterpriseGenerator.createUniqueInstance();
        Role r = roleGenerator.createInstance(Role.Type.SYS_ADMIN);

        User u = userGenerator.createInstance(sysEnterprise, r, "sysadmin", "sysadmin");
        setup(sysEnterprise, r, u);
    }

    @Test
    public void getVirtualDatacentersFilteredByUser()
    {
        Enterprise enterprise = enterpriseGenerator.createUniqueInstance();
        Datacenter datacenter = datacenterGenerator.createUniqueInstance();

        Role role = roleGenerator.createInstance(Role.Type.USER);
        User user = userGenerator.createInstance(enterprise, role, "foo");

        VirtualDatacenter vdc1 = vdcGenerator.createInstance(datacenter, enterprise);
        VirtualDatacenter vdc2 = vdcGenerator.createInstance(datacenter, enterprise);
        VirtualDatacenter vdc3 = vdcGenerator.createInstance(datacenter, enterprise);

        setup(enterprise, datacenter, vdc1, vdc2, vdc3,
            new DatacenterLimits(enterprise, datacenter));

        String ids = vdc1.getId() + "," + vdc2.getId();

        user.setAvailableVirtualDatacenters(ids);
        setup(user.getRole(), user);

        ClientResponse response = get(resolveVirtualDatacentersURI(), user.getNick(), "foo");
        assertEquals(response.getStatusCode(), 200);

        VirtualDatacentersDto entity = response.getEntity(VirtualDatacentersDto.class);
        assertNotNull(entity);
        assertNotNull(entity.getCollection());
        assertEquals(entity.getCollection().size(), 2);
    }

    @Test
    public void getVirtualDatacentersList() throws Exception
    {
        VirtualDatacenter vdc = vdcGenerator.createInstance(sysEnterprise);
        setup(vdc.getDatacenter(), vdc.getNetwork(), vdc, new DatacenterLimits(sysEnterprise, vdc
            .getDatacenter()));

        ClientResponse response = get(resolveVirtualDatacentersURI(), "sysadmin", "sysadmin");
        assertEquals(response.getStatusCode(), 200);

        VirtualDatacentersDto entity = response.getEntity(VirtualDatacentersDto.class);
        assertNotNull(entity);
        assertNotNull(entity.getCollection());
        assertEquals(entity.getCollection().size(), 1);
    }

    @Test
    public void getVirtualDatacentersFilteredByEnterprise()
    {
        VirtualDatacenter vdc = vdcGenerator.createUniqueInstance();
        VirtualDatacenter vdc2 = vdcGenerator.createInstance(vdc.getEnterprise());
        VirtualDatacenter vdc3 = vdcGenerator.createInstance(vdc.getDatacenter());

        DatacenterLimits dcl1 = new DatacenterLimits(vdc.getEnterprise(), vdc.getDatacenter());
        DatacenterLimits dcl2 = new DatacenterLimits(vdc2.getEnterprise(), vdc2.getDatacenter());
        DatacenterLimits dcl3 = new DatacenterLimits(vdc3.getEnterprise(), vdc3.getDatacenter());

        setup(vdc.getDatacenter(), vdc2.getDatacenter(), vdc.getEnterprise(), vdc3.getEnterprise(),
            vdc.getNetwork(), vdc2.getNetwork(), vdc3.getNetwork(), vdc, vdc2, vdc3, dcl1, dcl2,
            dcl3);

        String uri = resolveVirtualDatacentersURI();
        uri =
            UriHelper.appendQueryParamsToPath(uri, Collections.singletonMap(
                EnterpriseResource.ENTERPRISE,
                new String[] {vdc.getEnterprise().getId().toString()}), false);

        ClientResponse response = get(uri, "sysadmin", "sysadmin");
        VirtualDatacentersDto entity = response.getEntity(VirtualDatacentersDto.class);
        assertNotNull(entity);
        assertNotNull(entity.getCollection());
        assertEquals(entity.getCollection().size(), 2);
    }

    @Test
    public void getVirtualDatacentersFilteredByDatacenter()
    {
        VirtualDatacenter vdc = vdcGenerator.createInstance(sysEnterprise);
        VirtualDatacenter vdc2 = vdcGenerator.createInstance(sysEnterprise);
        VirtualDatacenter vdc3 = vdcGenerator.createInstance(vdc.getDatacenter(), sysEnterprise);

        DatacenterLimits dcl1 = new DatacenterLimits(vdc.getEnterprise(), vdc.getDatacenter());
        DatacenterLimits dcl2 = new DatacenterLimits(vdc2.getEnterprise(), vdc2.getDatacenter());
        DatacenterLimits dcl3 = new DatacenterLimits(vdc3.getEnterprise(), vdc3.getDatacenter());

        setup(vdc.getDatacenter(), vdc2.getDatacenter(), vdc.getNetwork(), vdc2.getNetwork(), vdc3
            .getNetwork(), vdc, vdc2, vdc3, dcl1, dcl2, dcl3);

        String uri = resolveVirtualDatacentersURI();
        uri =
            UriHelper.appendQueryParamsToPath(uri, Collections.singletonMap(
                DatacenterResource.DATACENTER,
                new String[] {vdc.getDatacenter().getId().toString()}), false);

        ClientResponse response = get(uri, "sysadmin", "sysadmin");
        VirtualDatacentersDto entity = response.getEntity(VirtualDatacentersDto.class);
        assertNotNull(entity);
        assertNotNull(entity.getCollection());
        assertEquals(entity.getCollection().size(), 2);
    }

    @Test
    public void getVirtualDatacentersFilteredByEnterpriseAndDatacenter()
    {
        VirtualDatacenter vdc = vdcGenerator.createUniqueInstance();
        VirtualDatacenter vdc2 = vdcGenerator.createInstance(vdc.getEnterprise());
        VirtualDatacenter vdc3 = vdcGenerator.createInstance(vdc.getDatacenter());

        DatacenterLimits dcl1 = new DatacenterLimits(vdc.getEnterprise(), vdc.getDatacenter());
        DatacenterLimits dcl2 = new DatacenterLimits(vdc2.getEnterprise(), vdc2.getDatacenter());
        DatacenterLimits dcl3 = new DatacenterLimits(vdc3.getEnterprise(), vdc3.getDatacenter());

        setup(vdc.getDatacenter(), vdc2.getDatacenter(), vdc.getEnterprise(), vdc3.getEnterprise(),
            vdc.getNetwork(), vdc2.getNetwork(), vdc3.getNetwork(), vdc, vdc2, vdc3, dcl1, dcl2,
            dcl3);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();
        queryParams.put(DatacenterResource.DATACENTER, new String[] {vdc.getDatacenter().getId()
            .toString()});
        queryParams.put(EnterpriseResource.ENTERPRISE, new String[] {vdc.getEnterprise().getId()
            .toString()});

        String uri = resolveVirtualDatacentersURI();
        uri = UriHelper.appendQueryParamsToPath(uri, queryParams, false);

        ClientResponse response = get(uri, "sysadmin", "sysadmin");
        VirtualDatacentersDto entity = response.getEntity(VirtualDatacentersDto.class);
        assertNotNull(entity);
        assertNotNull(entity.getCollection());
        assertEquals(entity.getCollection().size(), 1);
    }

    @Test
    public void createVirtualDatacenterRaisesErrorWhenDatacenterDoesNotExist()
    {
        VirtualDatacenterDto dto = new VirtualDatacenterDto();
        dto.setHypervisorType(HypervisorType.KVM);

        ClientResponse response = post(resolveVirtualDatacentersURI(), dto);
        assertEquals(response.getStatusCode(), 404);
        assertErrors(response, 404, APIError.NON_EXISTENT_DATACENTER.getCode());

        RESTLink datacenterLink = new RESTLink("datacenter", "http://localhost:8080/api/foo");
        dto.addLink(datacenterLink);

        response = post(resolveVirtualDatacentersURI(), dto, "sysadmin", "sysadmin");
        assertEquals(response.getStatusCode(), 404);
        assertErrors(response, 404, APIError.NON_EXISTENT_DATACENTER.getCode());

        dto = new VirtualDatacenterDto();
        dto.setHypervisorType(HypervisorType.KVM);
        datacenterLink = new RESTLink("datacenter", resolveDatacenterURI(1234455));
        dto.addLink(datacenterLink);

        response = post(resolveVirtualDatacentersURI(), dto, "sysadmin", "sysadmin");
        assertEquals(response.getStatusCode(), 404);
        assertErrors(response, 404, APIError.NON_EXISTENT_DATACENTER.getCode());
    }

    @Test
    public void createVirtualDatacenterRaiseErrorWhenEnterpriseDoesNotExist()
    {
        Datacenter dc = datacenterGenerator.createUniqueInstance();
        setup(dc);

        VirtualDatacenterDto dto = new VirtualDatacenterDto();
        dto.setHypervisorType(HypervisorType.KVM);
        RESTLink datacenterLink = new RESTLink("datacenter", resolveDatacenterURI(dc.getId()));
        dto.addLink(datacenterLink);

        ClientResponse response = post(resolveVirtualDatacentersURI(), dto, "sysadmin", "sysadmin");
        assertEquals(response.getStatusCode(), 404);
        assertErrors(response, 404, APIError.NON_EXISTENT_ENTERPRISE.getCode());

        RESTLink enterpriseLink = new RESTLink("enterprise", "http://localhost:8080/api/foo");
        dto.addLink(enterpriseLink);

        response = post(resolveVirtualDatacentersURI(), dto, "sysadmin", "sysadmin");
        assertEquals(response.getStatusCode(), 404);
        assertErrors(response, 404, APIError.NON_EXISTENT_ENTERPRISE.getCode());

        dto = new VirtualDatacenterDto();
        dto.setHypervisorType(HypervisorType.KVM);
        dto.addLink(datacenterLink);
        enterpriseLink = new RESTLink("enterprise", resolveEnterpriseURI(1234455));
        dto.addLink(enterpriseLink);

        response = post(resolveVirtualDatacentersURI(), dto);
        assertEquals(response.getStatusCode(), 404);
        assertErrors(response, 404, APIError.NON_EXISTENT_ENTERPRISE.getCode());
    }

    @Test
    public void createVirtualDatacenterRaiseErrorWhenHypervisorTypeIsNotInDatacenter()
    {
        RemoteService rs = remoteServiceGenerator.createInstance(RemoteServiceType.DHCP_SERVICE);
        Enterprise e = enterpriseGenerator.createUniqueInstance();
        Datacenter dc = rs.getDatacenter();

        Machine machine = machineGenerator.createMachine(dc);
        Hypervisor hypervisor =
            hypervisorGenerator.createInstance(machine, HypervisorType.HYPERV_301);

        setup(dc, machine, hypervisor, e, rs, new DatacenterLimits(e, dc));

        VirtualDatacenterDto dto = getValidVdc();
        RESTLink datacenterLink = new RESTLink("datacenter", resolveDatacenterURI(dc.getId()));
        dto.addLink(datacenterLink);
        RESTLink enterpriseLink = new RESTLink("enterprise", resolveEnterpriseURI(e.getId()));
        dto.addLink(enterpriseLink);

        dto.setName("vdc_test_create");
        dto.setHypervisorType(HypervisorType.KVM);

        ClientResponse response = post(resolveVirtualDatacentersURI(), dto, "sysadmin", "sysadmin");
        assertErrors(response, APIError.VIRTUAL_DATACENTER_INVALID_HYPERVISOR_TYPE);
    }

    @Test
    public void createVirtualDatacenter()
    {
        VirtualDatacenterDto dto = getValidVdc();

        ClientResponse response = post(resolveVirtualDatacentersURI(), dto, "sysadmin", "sysadmin");

        assertEquals(response.getStatusCode(), 201);
    }

    /**
     * Test that checks the LIMITS_INVALID_HARD_LIMIT_FOR_VLANS_PER_VDC, that means the number of
     * VLANs is greater than the allowed by properties. You can change the property
     * 'abiquo.server.networking.vlanPerVDC' in the POM.xml
     */
    @Test
    public void createVirtualDatacenterFailsIfTheVlanPerVDCIsNotValid()
    {
        VirtualDatacenterDto dto = getValidVdc();
        dto.setVlansSoft(1);
        dto.setVlansHard(100);

        ClientResponse response = post(resolveVirtualDatacentersURI(), dto, "sysadmin", "sysadmin");

        assertEquals(response.getStatusCode(), 400);

        assertErrors(response, APIError.LIMITS_INVALID_HARD_LIMIT_FOR_VLANS_PER_VDC);
    }

    @Test
    public void createVirtualDatacenterWithLimits()
    {
        VirtualDatacenterDto dto = getValidVdc();
        dto.setVlansLimits(1, 2);
        dto.setCpuCountLimits(1, 2);
        dto.setPublicIPLimits(1, 2);
        dto.setHdLimitsInMb(1, 2);
        dto.setRamLimitsInMb(1, 2);
        dto.setStorageLimits(1, 2);

        ClientResponse response = post(resolveVirtualDatacentersURI(), dto, "sysadmin", "sysadmin");

        assertEquals(response.getStatusCode(), 201);
    }

    @Test
    public void createVirtualDatacenterWithLimitsFailsIfSoftIsZeroAndHardIsNot()
    {
        VirtualDatacenterDto dto = getValidVdc();
        dto.setVlansLimits(0, 2);
        dto.setCpuCountLimits(0, 2);
        dto.setPublicIPLimits(0, 2);
        dto.setHdLimitsInMb(0, 2);
        dto.setRamLimitsInMb(0, 2);
        dto.setStorageLimits(0, 2);

        ClientResponse response = post(resolveVirtualDatacentersURI(), dto, "sysadmin", "sysadmin");

        assertEquals(response.getStatusCode(), 400);
    }

    @Test
    public void createVirtualDatacenterWithLimitsPassIfHardIsZeroAndSoftIsNot()
    {
        VirtualDatacenterDto dto = getValidVdc();
        dto.setVlansLimits(1, 0);
        dto.setCpuCountLimits(1, 0);
        dto.setPublicIPLimits(1, 0);
        dto.setHdLimitsInMb(1, 0);
        dto.setRamLimitsInMb(1, 0);
        dto.setStorageLimits(1, 0);

        ClientResponse response = post(resolveVirtualDatacentersURI(), dto, "sysadmin", "sysadmin");

        assertEquals(response.getStatusCode(), 201);
    }

    @Test
    public void createVirtualDatacenterWithLimitsPassIfSoftAndHardAreEqual()
    {
        VirtualDatacenterDto dto = getValidVdc();
        dto.setVlansLimits(1, 1);
        dto.setCpuCountLimits(1, 1);
        dto.setPublicIPLimits(1, 1);
        dto.setHdLimitsInMb(1, 1);
        dto.setRamLimitsInMb(1, 1);
        dto.setStorageLimits(1, 1);

        ClientResponse response = post(resolveVirtualDatacentersURI(), dto, "sysadmin", "sysadmin");

        assertEquals(response.getStatusCode(), 201);
    }

    @Test
    public void createVirtualDatacenterWithLimitsFailIfSoftIsGreated()
    {
        VirtualDatacenterDto dto = getValidVdc();
        dto.setVlansLimits(2, 1);
        dto.setCpuCountLimits(2, 1);
        dto.setPublicIPLimits(2, 1);
        dto.setHdLimitsInMb(2, 1);
        dto.setRamLimitsInMb(2, 1);
        dto.setStorageLimits(2, 1);

        ClientResponse response = post(resolveVirtualDatacentersURI(), dto, "sysadmin", "sysadmin");

        assertEquals(response.getStatusCode(), 400);
    }

    public void createVirtualDatacenterFailsWithInvalidIps()
    {
        VirtualDatacenterDto dto = getValidVdc();
        dto.getVlan().setAddress("foo");

        ClientResponse response = post(resolveVirtualDatacentersURI(), dto, "sysadmin", "sysadmin");
        assertEquals(response.getStatusCode(), 400);
    }

    public void createVirtualDatacenterPassWithOptionalDns()
    {
        VirtualDatacenterDto dto = getValidVdc();
        dto.getVlan().setPrimaryDNS(null);
        dto.getVlan().setSecondaryDNS(null);

        ClientResponse response = post(resolveVirtualDatacentersURI(), dto, "sysadmin", "sysadmin");
        assertEquals(response.getStatusCode(), 201);
    }

    private VirtualDatacenterDto getValidVdc()
    {
        RemoteService rs = remoteServiceGenerator.createInstance(RemoteServiceType.DHCP_SERVICE);
        Enterprise e = enterpriseGenerator.createUniqueInstance();

        Machine machine = machineGenerator.createMachine(rs.getDatacenter());
        Hypervisor hypervisor = hypervisorGenerator.createInstance(machine);

        setup(rs.getDatacenter(), machine, hypervisor, e, rs, new DatacenterLimits(e, rs
            .getDatacenter()));

        VirtualDatacenterDto dto = new VirtualDatacenterDto();
        RESTLink datacenterLink =
            new RESTLink("datacenter", resolveDatacenterURI(rs.getDatacenter().getId()));

        dto.addLink(datacenterLink);
        RESTLink enterpriseLink = new RESTLink("enterprise", resolveEnterpriseURI(e.getId()));
        dto.addLink(enterpriseLink);

        dto.setName("vdc_test_create");
        dto.setHypervisorType(hypervisor.getType());

        VLANNetworkDto networkDto = new VLANNetworkDto();
        networkDto.setName("Default Network");
        networkDto.setDefaultNetwork(Boolean.TRUE);
        networkDto.setAddress("192.168.0.0");
        networkDto.setGateway("192.168.0.1");
        networkDto.setMask(24);
        networkDto.setPrimaryDNS("10.0.0.1");
        networkDto.setSecondaryDNS("10.0.0.1");

        dto.setVlan(networkDto);

        return dto;
    }
}
