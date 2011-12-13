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

package com.abiquo.api.resources.appslibrary;

import static com.abiquo.api.common.Assert.assertError;
import static com.abiquo.api.common.Assert.assertLinkExist;
import static com.abiquo.api.common.UriTestResolver.resolveCategoryURI;
import static com.abiquo.api.common.UriTestResolver.resolveDatacenterRepositoryURI;
import static com.abiquo.api.common.UriTestResolver.resolveEnterpriseURI;
import static com.abiquo.api.common.UriTestResolver.resolveIconURI;
import static com.abiquo.api.common.UriTestResolver.resolveVirtualMachineTemplateURI;
import static com.abiquo.api.util.URIResolver.buildPath;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wink.client.ClientResponse;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.api.common.UriTestResolver;
import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.resources.AbstractJpaGeneratorIT;
import com.abiquo.api.resources.EnterpriseResource;
import com.abiquo.api.resources.cloud.VirtualDatacenterResource;
import com.abiquo.api.resources.cloud.VirtualDatacentersResource;
import com.abiquo.appliancemanager.util.URIResolver;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.appslibrary.Icon;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplate;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplateDto;
import com.abiquo.server.core.enterprise.DatacenterLimits;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.Privilege;
import com.abiquo.server.core.enterprise.Role;
import com.abiquo.server.core.enterprise.User;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.RemoteService;
import com.abiquo.server.core.infrastructure.Repository;
import com.abiquo.server.core.infrastructure.storage.VolumeManagement;
import com.abiquo.testng.TestServerAndAMListener;

public class VirtualMachineTemplateResourceIT extends AbstractJpaGeneratorIT
{
    private static final String SYSADMIN = "sysadmin";

    private Enterprise ent;

    private Datacenter datacenter;

    private Repository repository;

    @BeforeMethod
    public void setUpDatacenterRepository()
    {
        ent = enterpriseGenerator.createUniqueInstance();
        datacenter = datacenterGenerator.createUniqueInstance();
        repository = repositoryGenerator.createInstance(datacenter);

        DatacenterLimits limits = datacenterLimitsGenerator.createInstance(ent, datacenter);

        RemoteService am =
            remoteServiceGenerator.createInstance(RemoteServiceType.APPLIANCE_MANAGER, datacenter);
        am.setUri(TestServerAndAMListener.AM_URI);

        Role role = roleGenerator.createInstanceSysAdmin();
        User user = userGenerator.createInstance(ent, role, SYSADMIN, SYSADMIN);

        List<Object> entitiesToSetup = new ArrayList<Object>();
        entitiesToSetup.add(ent);
        entitiesToSetup.add(datacenter);
        entitiesToSetup.add(limits);
        entitiesToSetup.add(repository);
        entitiesToSetup.add(am);

        for (Privilege p : role.getPrivileges())
        {
            entitiesToSetup.add(p);
        }
        entitiesToSetup.add(role);
        entitiesToSetup.add(user);

        setup(entitiesToSetup.toArray());

    }

    @Test
    public void testGetVirtualMachineTemplateRaises409WhenNoDatacenterLimits()
    {
        ent = enterpriseGenerator.createUniqueInstance();
        VirtualMachineTemplate vmtemplate = virtualMachineTemplateGenerator.createInstance(ent, repository);
        setup(ent, vmtemplate.getCategory(), vmtemplate);

        String uri = resolveVirtualMachineTemplateURI(ent.getId(), datacenter.getId(), vmtemplate.getId());
        ClientResponse response = get(uri, SYSADMIN, SYSADMIN);
        assertError(response, 409, APIError.ENTERPRISE_NOT_ALLOWED_DATACENTER);
    }

    @Test
    public void testGetVirtualMachineTemplateRaises404WhenInvalidEnterprise()
    {
        VirtualMachineTemplate vmtemplate = virtualMachineTemplateGenerator.createInstance(ent, repository);
        setup(vmtemplate.getCategory(), vmtemplate);

        String uri =
            resolveVirtualMachineTemplateURI(ent.getId() + 100, datacenter.getId(), vmtemplate.getId());
        ClientResponse response = get(uri, SYSADMIN, SYSADMIN);
        assertError(response, 404, APIError.NON_EXISTENT_ENTERPRISE);
    }

    @Test
    public void testGetVirtualMachineTemplateRaises404WhenInvalidDatacenter()
    {
        VirtualMachineTemplate vmtemplate = virtualMachineTemplateGenerator.createInstance(ent, repository);
        setup(vmtemplate.getCategory(), vmtemplate);

        String uri =
            resolveVirtualMachineTemplateURI(ent.getId(), datacenter.getId() + 100, vmtemplate.getId());
        ClientResponse response = get(uri, SYSADMIN, SYSADMIN);
        assertError(response, 404, APIError.NON_EXISTENT_DATACENTER);
    }

    @Test
    public void testGetVirtualMachineTemplateRaises404WhenInvalidVirtualMachineTemplate()
    {
        VirtualMachineTemplate vmtemplate = virtualMachineTemplateGenerator.createInstance(ent, repository);
        setup(vmtemplate.getCategory(), vmtemplate);

        String uri =
            resolveVirtualMachineTemplateURI(ent.getId(), datacenter.getId(), vmtemplate.getId() + 100);
        ClientResponse response = get(uri, SYSADMIN, SYSADMIN);
        assertError(response, 404, APIError.NON_EXISTENT_VIRTUAL_MACHINE_TEMPLATE);
    }

    @Test
    public void getVirtualMachineTemplate()
    {
        VirtualMachineTemplate vmtemplate = virtualMachineTemplateGenerator.createInstance(ent, repository);
        setup(vmtemplate.getCategory(), vmtemplate);

        String uri = resolveVirtualMachineTemplateURI(ent.getId(), datacenter.getId(), vmtemplate.getId());
        ClientResponse response = get(uri, SYSADMIN, SYSADMIN);
        assertEquals(response.getStatusCode(), 200);

        VirtualMachineTemplateDto dto = response.getEntity(VirtualMachineTemplateDto.class);
        assertVirtualMachineTemplateWithLinks(vmtemplate, dto);
    }

    @Test
    public void getVirtualMachineTemplateWithMaster()
    {
        VirtualMachineTemplate master = virtualMachineTemplateGenerator.createInstance(ent, repository);
        VirtualMachineTemplate slave = virtualMachineTemplateGenerator.createSlaveVirtualMachineTemplate(master);

        setup(slave.getCategory(), master, slave);

        String uri = resolveVirtualMachineTemplateURI(ent.getId(), datacenter.getId(), slave.getId());
        ClientResponse response = get(uri, SYSADMIN, SYSADMIN);
        assertEquals(response.getStatusCode(), 200);

        VirtualMachineTemplateDto dto = response.getEntity(VirtualMachineTemplateDto.class);
        assertVirtualMachineTemplateWithLinks(slave, dto);
    }

    @Test
    public void getVirtualMachineTemplateWithIcon()
    {
        VirtualMachineTemplate vmtemplate = virtualMachineTemplateGenerator.createInstance(ent, repository);
        Icon icon = iconGenerator.createUniqueInstance();

        vmtemplate.setIcon(icon);
        setup(vmtemplate.getCategory(), icon, vmtemplate);

        String uri = resolveVirtualMachineTemplateURI(ent.getId(), datacenter.getId(), vmtemplate.getId());
        ClientResponse response = get(uri, SYSADMIN, SYSADMIN);
        assertEquals(response.getStatusCode(), 200);

        VirtualMachineTemplateDto dto = response.getEntity(VirtualMachineTemplateDto.class);
        assertVirtualMachineTemplateWithLinks(vmtemplate, dto);
    }

    @Test
    public void getVirtualMachineTemplateWithoutTemplateDefinitionId()
    {
        VirtualMachineTemplate vmtemplate = virtualMachineTemplateGenerator.createInstance(ent, repository);

        vmtemplate.setOvfid(null);
        setup(vmtemplate.getCategory(), vmtemplate);

        String uri = resolveVirtualMachineTemplateURI(ent.getId(), datacenter.getId(), vmtemplate.getId());
        ClientResponse response = get(uri, SYSADMIN, SYSADMIN);
        assertEquals(response.getStatusCode(), 200);

        VirtualMachineTemplateDto dto = response.getEntity(VirtualMachineTemplateDto.class);
        assertVirtualMachineTemplateWithLinks(vmtemplate, dto);
    }

    @Test
    public void editVirtualMachineTemplate()
    {
        VirtualMachineTemplate vmtemplate = virtualMachineTemplateGenerator.createInstance(ent, repository);

        vmtemplate.setOvfid(null);
        setup(vmtemplate.getCategory(), vmtemplate);

        String uri = resolveVirtualMachineTemplateURI(ent.getId(), datacenter.getId(), vmtemplate.getId());
        ClientResponse response = get(uri, SYSADMIN, SYSADMIN);
        assertEquals(response.getStatusCode(), 200);

        VirtualMachineTemplateDto dto = response.getEntity(VirtualMachineTemplateDto.class);

        dto.setName("newName");
        dto.setPath("newPath");

        response = put(uri, dto, SYSADMIN, SYSADMIN);
        assertEquals(response.getStatusCode(), 200);

        VirtualMachineTemplateDto modifiedDto = response.getEntity(VirtualMachineTemplateDto.class);
        assertEquals(modifiedDto.getName(), dto.getName());
        assertEquals(modifiedDto.getPath(), dto.getPath());

    }

    @Test
    public void editVirtualMachineTemplateChangeEnterpriseRises409()
    {
        VirtualMachineTemplate vmtemplate = virtualMachineTemplateGenerator.createInstance(ent, repository);

        vmtemplate.setOvfid(null);
        setup(vmtemplate.getCategory(), vmtemplate);

        String uri = resolveVirtualMachineTemplateURI(ent.getId(), datacenter.getId(), vmtemplate.getId());
        ClientResponse response = get(uri, SYSADMIN, SYSADMIN);
        assertEquals(response.getStatusCode(), 200);

        VirtualMachineTemplateDto dto = response.getEntity(VirtualMachineTemplateDto.class);

        Enterprise otherEnterprise = enterpriseGenerator.createUniqueInstance();
        setup(otherEnterprise);

        String enterpriseUri = resolveEnterpriseURI(otherEnterprise.getId());

        RESTLink enterpriseLink = dto.searchLink(EnterpriseResource.ENTERPRISE);
        enterpriseLink.setHref(enterpriseUri);
        dto.addLink(enterpriseLink);

        response = put(uri, dto, SYSADMIN, SYSADMIN);

        assertError(response, 409, APIError.VMTEMPLATE_ENTERPRISE_CANNOT_BE_CHANGED);
    }

    @Test
    public void editVirtualMachineTemplateChangeDataCenterRepoRises409()
    {
        VirtualMachineTemplate vmtemplate = virtualMachineTemplateGenerator.createInstance(ent, repository);

        vmtemplate.setOvfid(null);
        setup(vmtemplate.getCategory(), vmtemplate);

        String uri = resolveVirtualMachineTemplateURI(ent.getId(), datacenter.getId(), vmtemplate.getId());
        ClientResponse response = get(uri, SYSADMIN, SYSADMIN);
        assertEquals(response.getStatusCode(), 200);

        VirtualMachineTemplateDto dto = response.getEntity(VirtualMachineTemplateDto.class);

        Datacenter otherDatacenter = datacenterGenerator.createUniqueInstance();
        DatacenterLimits limits1 = datacenterLimitsGenerator.createInstance(ent, otherDatacenter);

        setup(limits1, otherDatacenter);

        String repoUri = resolveDatacenterRepositoryURI(ent.getId(), otherDatacenter.getId());

        RESTLink repoLink = dto.searchLink(DatacenterRepositoryResource.DATACENTER_REPOSITORY);
        repoLink.setHref(repoUri);
        dto.addLink(repoLink);

        response = put(uri, dto, SYSADMIN, SYSADMIN);

        assertError(response, 409, APIError.VMTEMPLATE_DATACENTER_REPOSITORY_CANNOT_BE_CHANGED);
    }

    @Test
    public void editVirtualMachineTemplateAllowSettingMasterNull()
    {
        VirtualMachineTemplate vmtemplate = virtualMachineTemplateGenerator.createInstance(ent, repository);
        VirtualMachineTemplate master = virtualMachineTemplateGenerator.createInstance(ent, repository);

        vmtemplate.setMaster(master);

        vmtemplate.setOvfid(null);
        setup(master.getCategory(), master, vmtemplate.getCategory(), vmtemplate);

        String uri = resolveVirtualMachineTemplateURI(ent.getId(), datacenter.getId(), vmtemplate.getId());
        ClientResponse response = get(uri, SYSADMIN, SYSADMIN);
        assertEquals(response.getStatusCode(), 200);

        VirtualMachineTemplateDto dto = response.getEntity(VirtualMachineTemplateDto.class);

        RESTLink masterLink = dto.searchLink("master");

        dto.getLinks().remove(masterLink);

        response = put(uri, dto, SYSADMIN, SYSADMIN);

        assertEquals(response.getStatusCode(), 200);
    }

    @Test
    public void editVirtualMachineTemplateSetNewMasterMachineTemplateRises409()
    {
        VirtualMachineTemplate vmtemplate = virtualMachineTemplateGenerator.createInstance(ent, repository);
        VirtualMachineTemplate master = virtualMachineTemplateGenerator.createInstance(ent, repository);

        vmtemplate.setOvfid(null);
        setup(master.getCategory(), master, vmtemplate.getCategory(), vmtemplate);

        String uri = resolveVirtualMachineTemplateURI(ent.getId(), datacenter.getId(), vmtemplate.getId());
        ClientResponse response = get(uri, SYSADMIN, SYSADMIN);
        assertEquals(response.getStatusCode(), 200);

        VirtualMachineTemplateDto dto = response.getEntity(VirtualMachineTemplateDto.class);

        String masterUri = resolveVirtualMachineTemplateURI(ent.getId(), datacenter.getId(), master.getId());

        RESTLink masterLink = new RESTLink("master", masterUri);
        dto.addLink(masterLink);

        response = put(uri, dto, SYSADMIN, SYSADMIN);

        assertError(response, 409, APIError.VMTEMPLATE_MASTER_TEMPLATE_CANNOT_BE_CHANGED);
    }

    @Test
    public void deleteMasterMachineTemplateRises409()
    {
        VirtualMachineTemplate vmtemplate = virtualMachineTemplateGenerator.createInstance(ent, repository);
        VirtualMachineTemplate master = virtualMachineTemplateGenerator.createInstance(ent, repository);

        vmtemplate.setMaster(master);

        vmtemplate.setOvfid(null);
        setup(master.getCategory(), master, vmtemplate.getCategory(), vmtemplate);

        String uri = resolveVirtualMachineTemplateURI(ent.getId(), datacenter.getId(), master.getId());

        ClientResponse response = get(uri, SYSADMIN, SYSADMIN);
        assertEquals(response.getStatusCode(), 200, response.getEntity(String.class));

        response = delete(uri, SYSADMIN, SYSADMIN);
        assertError(response, 409, APIError.VMTEMPLATE_MASTER_TEMPLATE_CANNOT_BE_DELETED);
    }

    @Test
    public void deleteStatefulMachineTemplateRises409()
    {

        Datacenter dc = datacenterGenerator.createUniqueInstance();

        VolumeManagement statefulVolume = volumeManagementGenerator.createStatefulInstance(dc);
        List<Object> entitiesToSetup = new ArrayList<Object>();
        volumeManagementGenerator.addAuxiliaryEntitiesToPersist(statefulVolume, entitiesToSetup);

        DatacenterLimits limitss =
            datacenterLimitsGenerator.createInstance(statefulVolume.getVirtualMachineTemplate()
                .getEnterprise(), dc);
        RemoteService am =
            remoteServiceGenerator.createInstance(RemoteServiceType.APPLIANCE_MANAGER, dc);
        am.setUri(TestServerAndAMListener.AM_URI);

        setup(entitiesToSetup.toArray());
        setup(limitss, am);
        String uri =
            resolveVirtualMachineTemplateURI(statefulVolume.getVirtualMachineTemplate().getEnterprise().getId(),
                dc.getId(), statefulVolume.getVirtualMachineTemplate().getId());

        ClientResponse response = get(uri, SYSADMIN, SYSADMIN);
        assertEquals(response.getStatusCode(), 200);

        response = delete(uri, SYSADMIN, SYSADMIN);
        assertError(response, 409, APIError.VMTEMPLATE_STATEFUL_TEMPLATE_CANNOT_BE_DELETED);
    }

    public void deleteSharedMachineTemplateFromOtherEnterpriseRises409()
    {

        Enterprise ent1 = enterpriseGenerator.createUniqueInstance();
        Datacenter datacenter1 = datacenterGenerator.createUniqueInstance();

        Repository rep = repositoryGenerator.createInstance(datacenter1);

        DatacenterLimits limits = datacenterLimitsGenerator.createInstance(ent1, datacenter1);

        VirtualMachineTemplate vmtemplate = virtualMachineTemplateGenerator.createInstance(ent, repository);

        vmtemplate.setShared(Boolean.TRUE);
        vmtemplate.setEnterprise(ent1);

        vmtemplate.setOvfid(null);
        setup(ent1, datacenter1, rep, limits, vmtemplate.getCategory(), vmtemplate);

        String uri = resolveVirtualMachineTemplateURI(ent.getId(), datacenter.getId(), vmtemplate.getId());

        ClientResponse response = delete(uri, SYSADMIN, SYSADMIN);
        assertError(response, 409, APIError.VMTEMPLATE_SHARED_TEMPLATE_FROM_OTHER_ENTERPRISE);
    }

    // Do not make public to avoid TestNG run it as another test
    /* package */static void assertVirtualMachineTemplateWithLinks(final VirtualMachineTemplate vi,
        final VirtualMachineTemplateDto dto)
    {
        Integer idEnterprise = vi.getEnterprise().getId();
        Integer idDatacenter = vi.getRepository().getDatacenter().getId();

        // Required fields
        assertNotNull(dto.getId());
        assertNotNull(dto.getName());
        assertNotNull(dto.getPath());
        assertNotNull(dto.getDiskFormatType());
        assertNotNull(dto.getDiskFileSize());

        // Required links
        String edit = resolveVirtualMachineTemplateURI(idEnterprise, idDatacenter, vi.getId());
        String category = resolveCategoryURI(vi.getCategory().getId());
        assertLinkExist(dto, edit, "edit");
        assertLinkExist(dto, category, CategoryResource.CATEGORY);

        // Optional links
        if (vi.getIcon() != null)
        {
            String icon = resolveIconURI(vi.getIcon().getId());
            assertLinkExist(dto, icon, IconResource.ICON);
        }

        if (vi.getMaster() != null)
        {
            String master =
                resolveVirtualMachineTemplateURI(vi.getMaster().getEnterprise().getId(), idDatacenter, vi
                    .getMaster().getId());
            assertLinkExist(dto, master, "master");
        }

        if (vi.getOvfid() != null)
        {
            String amHref = amOVFPackageInstanceUrl(idEnterprise, vi.getOvfid());

            assertLinkExist(dto, vi.getOvfid(), "templatedefinition");
            assertLinkExist(dto, amHref, "template");
            assertLinkExist(dto, amHref + "?format=status", "templatestatus");
            assertLinkExist(dto, amHref + "?format=envelope", "ovfdocument");
            assertLinkExist(dto, amHref + "?format=diskFile", "diskfile");
        }

        if (vi.isStateful())
        {
            String template =
                buildPath(VirtualDatacentersResource.VIRTUAL_DATACENTERS_PATH,
                    VirtualDatacenterResource.VIRTUAL_DATACENTER_PARAM, "volumes", "{volume}");
            Map<String, String> values = new HashMap<String, String>();
            values.put(VirtualDatacenterResource.VIRTUAL_DATACENTER, vi.getVolume()
                .getVirtualDatacenter().getId().toString());
            values.put("volume", vi.getVolume().getId().toString());

            String volumeHref = UriTestResolver.resolveURI(template, values);
            assertLinkExist(dto, volumeHref, "volume");
        }
    }

    private static String amOVFPackageInstanceUrl(final Integer enterpriseId, final String ovf)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("erepo", enterpriseId.toString());
        params.put("template", ovf);

        // Must use the URI resolver in the AM in order to encode the ovf parameter
        return URIResolver.resolveURI(TestServerAndAMListener.AM_URI, "erepos/{erepo}/templates/{template}",
            params);
    }

}
