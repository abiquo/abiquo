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
import static com.abiquo.api.common.UriTestResolver.resolveIconURI;
import static com.abiquo.api.common.UriTestResolver.resolveVirtualImageURI;
import static com.abiquo.api.common.UriTestResolver.resolveEnterpriseURI;
import static com.abiquo.api.common.UriTestResolver.resolveDatacenterRepositoryURI;
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
import com.abiquo.api.resources.appslibrary.CategoryResource;
import com.abiquo.api.resources.appslibrary.DatacenterRepositoryResource;
import com.abiquo.api.resources.appslibrary.IconResource;
import com.abiquo.api.resources.cloud.VirtualDatacenterResource;
import com.abiquo.api.resources.cloud.VirtualDatacentersResource;
import com.abiquo.appliancemanager.util.URIResolver;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.appslibrary.Icon;
import com.abiquo.server.core.appslibrary.VirtualImage;
import com.abiquo.server.core.appslibrary.VirtualImageDto;
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

public class VirtualImageResourceIT extends AbstractJpaGeneratorIT
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
    public void testGetVirtualImageRaises409WhenNoDatacenterLimits()
    {
        ent = enterpriseGenerator.createUniqueInstance();
        VirtualImage virtualImage = virtualImageGenerator.createInstance(ent, repository);
        setup(ent, virtualImage.getCategory(), virtualImage);

        String uri = resolveVirtualImageURI(ent.getId(), datacenter.getId(), virtualImage.getId());
        ClientResponse response = get(uri, SYSADMIN, SYSADMIN);
        assertError(response, 409, APIError.ENTERPRISE_NOT_ALLOWED_DATACENTER);
    }

    @Test
    public void testGetVirtualImageRaises404WhenInvalidEnterprise()
    {
        VirtualImage virtualImage = virtualImageGenerator.createInstance(ent, repository);
        setup(virtualImage.getCategory(), virtualImage);

        String uri =
            resolveVirtualImageURI(ent.getId() + 100, datacenter.getId(), virtualImage.getId());
        ClientResponse response = get(uri, SYSADMIN, SYSADMIN);
        assertError(response, 404, APIError.NON_EXISTENT_ENTERPRISE);
    }

    @Test
    public void testGetVirtualImageRaises404WhenInvalidDatacenter()
    {
        VirtualImage virtualImage = virtualImageGenerator.createInstance(ent, repository);
        setup(virtualImage.getCategory(), virtualImage);

        String uri =
            resolveVirtualImageURI(ent.getId(), datacenter.getId() + 100, virtualImage.getId());
        ClientResponse response = get(uri, SYSADMIN, SYSADMIN);
        assertError(response, 404, APIError.NON_EXISTENT_DATACENTER);
    }

    @Test
    public void testGetVirtualImageRaises404WhenInvalidVirtualImage()
    {
        VirtualImage virtualImage = virtualImageGenerator.createInstance(ent, repository);
        setup(virtualImage.getCategory(), virtualImage);

        String uri =
            resolveVirtualImageURI(ent.getId(), datacenter.getId(), virtualImage.getId() + 100);
        ClientResponse response = get(uri, SYSADMIN, SYSADMIN);
        assertError(response, 404, APIError.NON_EXISTENT_VIRTUALIMAGE);
    }

    @Test
    public void getVirtualImage()
    {
        VirtualImage virtualImage = virtualImageGenerator.createInstance(ent, repository);
        setup(virtualImage.getCategory(), virtualImage);

        String uri = resolveVirtualImageURI(ent.getId(), datacenter.getId(), virtualImage.getId());
        ClientResponse response = get(uri, SYSADMIN, SYSADMIN);
        assertEquals(response.getStatusCode(), 200);

        VirtualImageDto dto = response.getEntity(VirtualImageDto.class);
        assertVirtualImageWithLinks(virtualImage, dto);
    }

    @Test
    public void getVirtualImageWithMaster()
    {
        VirtualImage master = virtualImageGenerator.createInstance(ent, repository);
        VirtualImage slave = virtualImageGenerator.createSlaveImage(master);

        setup(slave.getCategory(), master, slave);

        String uri = resolveVirtualImageURI(ent.getId(), datacenter.getId(), slave.getId());
        ClientResponse response = get(uri, SYSADMIN, SYSADMIN);
        assertEquals(response.getStatusCode(), 200);

        VirtualImageDto dto = response.getEntity(VirtualImageDto.class);
        assertVirtualImageWithLinks(slave, dto);
    }

    @Test
    public void getVirtualImageWithIcon()
    {
        VirtualImage virtualImage = virtualImageGenerator.createInstance(ent, repository);
        Icon icon = iconGenerator.createUniqueInstance();

        virtualImage.setIcon(icon);
        setup(virtualImage.getCategory(), icon, virtualImage);

        String uri = resolveVirtualImageURI(ent.getId(), datacenter.getId(), virtualImage.getId());
        ClientResponse response = get(uri, SYSADMIN, SYSADMIN);
        assertEquals(response.getStatusCode(), 200);

        VirtualImageDto dto = response.getEntity(VirtualImageDto.class);
        assertVirtualImageWithLinks(virtualImage, dto);
    }

    @Test
    public void getVirtualImageWithoutOVFId()
    {
        VirtualImage virtualImage = virtualImageGenerator.createInstance(ent, repository);

        virtualImage.setOvfid(null);
        setup(virtualImage.getCategory(), virtualImage);

        String uri = resolveVirtualImageURI(ent.getId(), datacenter.getId(), virtualImage.getId());
        ClientResponse response = get(uri, SYSADMIN, SYSADMIN);
        assertEquals(response.getStatusCode(), 200);

        VirtualImageDto dto = response.getEntity(VirtualImageDto.class);
        assertVirtualImageWithLinks(virtualImage, dto);
    }

    @Test
    public void editVirtualImage()
    {
        VirtualImage virtualImage = virtualImageGenerator.createInstance(ent, repository);

        virtualImage.setOvfid(null);
        setup(virtualImage.getCategory(), virtualImage);

        String uri = resolveVirtualImageURI(ent.getId(), datacenter.getId(), virtualImage.getId());
        ClientResponse response = get(uri, SYSADMIN, SYSADMIN);
        assertEquals(response.getStatusCode(), 200);

        VirtualImageDto dto = response.getEntity(VirtualImageDto.class);

        dto.setName("newName");
        dto.setPath("newPath");

        response = put(uri, dto, SYSADMIN, SYSADMIN);
        assertEquals(response.getStatusCode(), 200);

        VirtualImageDto modifiedDto = response.getEntity(VirtualImageDto.class);
        assertEquals(modifiedDto.getName(), dto.getName());
        assertEquals(modifiedDto.getPath(), dto.getPath());

    }

    @Test
    public void editVirtualImageChangeEnterpriseRises409()
    {
        VirtualImage virtualImage = virtualImageGenerator.createInstance(ent, repository);

        virtualImage.setOvfid(null);
        setup(virtualImage.getCategory(), virtualImage);

        String uri = resolveVirtualImageURI(ent.getId(), datacenter.getId(), virtualImage.getId());
        ClientResponse response = get(uri, SYSADMIN, SYSADMIN);
        assertEquals(response.getStatusCode(), 200);

        VirtualImageDto dto = response.getEntity(VirtualImageDto.class);

        Enterprise otherEnterprise = enterpriseGenerator.createUniqueInstance();
        setup(otherEnterprise);

        String enterpriseUri = resolveEnterpriseURI(otherEnterprise.getId());

        RESTLink enterpriseLink = dto.searchLink(EnterpriseResource.ENTERPRISE);
        enterpriseLink.setHref(enterpriseUri);
        dto.addLink(enterpriseLink);

        response = put(uri, dto, SYSADMIN, SYSADMIN);

        assertError(response, 409, APIError.VIMAGE_ENTERPRISE_CANNOT_BE_CHANGED);
    }

    @Test
    public void editVirtualImageChangeDataCenterRepoRises409()
    {
        VirtualImage virtualImage = virtualImageGenerator.createInstance(ent, repository);

        virtualImage.setOvfid(null);
        setup(virtualImage.getCategory(), virtualImage);

        String uri = resolveVirtualImageURI(ent.getId(), datacenter.getId(), virtualImage.getId());
        ClientResponse response = get(uri, SYSADMIN, SYSADMIN);
        assertEquals(response.getStatusCode(), 200);

        VirtualImageDto dto = response.getEntity(VirtualImageDto.class);

        Datacenter otherDatacenter = datacenterGenerator.createUniqueInstance();
        DatacenterLimits limits1 = datacenterLimitsGenerator.createInstance(ent, otherDatacenter);

        setup(limits1, otherDatacenter);

        String repoUri = resolveDatacenterRepositoryURI(ent.getId(), otherDatacenter.getId());

        RESTLink repoLink = dto.searchLink(DatacenterRepositoryResource.DATACENTER_REPOSITORY);
        repoLink.setHref(repoUri);
        dto.addLink(repoLink);

        response = put(uri, dto, SYSADMIN, SYSADMIN);

        assertError(response, 409, APIError.VIMAGE_DATACENTER_REPOSITORY_CANNOT_BE_CHANGED);
    }

    @Test
    public void editVirtualImageAllowSettingMasterNull()
    {
        VirtualImage virtualImage = virtualImageGenerator.createInstance(ent, repository);
        VirtualImage master = virtualImageGenerator.createInstance(ent, repository);

        virtualImage.setMaster(master);

        virtualImage.setOvfid(null);
        setup(master.getCategory(), master, virtualImage.getCategory(), virtualImage);

        String uri = resolveVirtualImageURI(ent.getId(), datacenter.getId(), virtualImage.getId());
        ClientResponse response = get(uri, SYSADMIN, SYSADMIN);
        assertEquals(response.getStatusCode(), 200);

        VirtualImageDto dto = response.getEntity(VirtualImageDto.class);

        RESTLink masterLink = dto.searchLink("master");

        dto.getLinks().remove(masterLink);

        response = put(uri, dto, SYSADMIN, SYSADMIN);

        assertEquals(response.getStatusCode(), 200);
    }

    @Test
    public void editVirtualImageSetNewMasterImageRises409()
    {
        VirtualImage virtualImage = virtualImageGenerator.createInstance(ent, repository);
        VirtualImage master = virtualImageGenerator.createInstance(ent, repository);

        virtualImage.setOvfid(null);
        setup(master.getCategory(), master, virtualImage.getCategory(), virtualImage);

        String uri = resolveVirtualImageURI(ent.getId(), datacenter.getId(), virtualImage.getId());
        ClientResponse response = get(uri, SYSADMIN, SYSADMIN);
        assertEquals(response.getStatusCode(), 200);

        VirtualImageDto dto = response.getEntity(VirtualImageDto.class);

        String masterUri = resolveVirtualImageURI(ent.getId(), datacenter.getId(), master.getId());

        RESTLink masterLink = new RESTLink("master", masterUri);
        dto.addLink(masterLink);

        response = put(uri, dto, SYSADMIN, SYSADMIN);

        assertError(response, 409, APIError.VIMAGE_MASTER_IMAGE_CANNOT_BE_CHANGED);
    }

    @Test
    public void deleteMasterImageRises409()
    {
        VirtualImage virtualImage = virtualImageGenerator.createInstance(ent, repository);
        VirtualImage master = virtualImageGenerator.createInstance(ent, repository);

        virtualImage.setMaster(master);

        virtualImage.setOvfid(null);
        setup(master.getCategory(), master, virtualImage.getCategory(), virtualImage);

        String uri = resolveVirtualImageURI(ent.getId(), datacenter.getId(), master.getId());

        ClientResponse response = get(uri, SYSADMIN, SYSADMIN);
        assertEquals(response.getStatusCode(), 200, response.getEntity(String.class));

        response = delete(uri, SYSADMIN, SYSADMIN);
        assertError(response, 409, APIError.VIMAGE_MASTER_IMAGE_CANNOT_BE_DELETED);
    }

    @Test
    public void deleteStatefulImageRises409()
    {

        Datacenter dc = datacenterGenerator.createUniqueInstance();

        VolumeManagement statefulVolume = volumeManagementGenerator.createStatefulInstance(dc);
        List<Object> entitiesToSetup = new ArrayList<Object>();
        volumeManagementGenerator.addAuxiliaryEntitiesToPersist(statefulVolume, entitiesToSetup);

        DatacenterLimits limitss =
            datacenterLimitsGenerator.createInstance(statefulVolume.getVirtualImage()
                .getEnterprise(), dc);
        RemoteService am =
            remoteServiceGenerator.createInstance(RemoteServiceType.APPLIANCE_MANAGER, dc);
        am.setUri(TestServerAndAMListener.AM_URI);

        setup(entitiesToSetup.toArray());
        setup(limitss, am);
        String uri =
            resolveVirtualImageURI(statefulVolume.getVirtualImage().getEnterprise().getId(),
                dc.getId(), statefulVolume.getVirtualImage().getId());

        ClientResponse response = get(uri, SYSADMIN, SYSADMIN);
        assertEquals(response.getStatusCode(), 200);

        response = delete(uri, SYSADMIN, SYSADMIN);
        assertError(response, 409, APIError.VIMAGE_STATEFUL_IMAGE_CANNOT_BE_DELETED);
    }

    public void deleteSharedImageFromOtherEnterpriseRises409()
    {

        Enterprise ent1 = enterpriseGenerator.createUniqueInstance();
        Datacenter datacenter1 = datacenterGenerator.createUniqueInstance();

        Repository rep = repositoryGenerator.createInstance(datacenter1);

        DatacenterLimits limits = datacenterLimitsGenerator.createInstance(ent1, datacenter1);

        VirtualImage virtualImage = virtualImageGenerator.createInstance(ent, repository);

        virtualImage.setShared(Boolean.TRUE);
        virtualImage.setEnterprise(ent1);

        virtualImage.setOvfid(null);
        setup(ent1, datacenter1, rep, limits, virtualImage.getCategory(), virtualImage);

        String uri = resolveVirtualImageURI(ent.getId(), datacenter.getId(), virtualImage.getId());

        ClientResponse response = delete(uri, SYSADMIN, SYSADMIN);
        assertError(response, 409, APIError.VIMAGE_SHARED_IMAGE_FROM_OTHER_ENTERPRISE);
    }

    // Do not make public to avoid TestNG run it as another test
    /* package */static void assertVirtualImageWithLinks(final VirtualImage vi,
        final VirtualImageDto dto)
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
        String edit = resolveVirtualImageURI(idEnterprise, idDatacenter, vi.getId());
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
                resolveVirtualImageURI(vi.getMaster().getEnterprise().getId(), idDatacenter, vi
                    .getMaster().getId());
            assertLinkExist(dto, master, "master");
        }

        if (vi.getOvfid() != null)
        {
            String amHref = amOVFPackageInstanceUrl(idEnterprise, vi.getOvfid());

            assertLinkExist(dto, vi.getOvfid(), "ovfpackage");
            assertLinkExist(dto, amHref, "ovfpackageinstance");
            assertLinkExist(dto, amHref + "?format=status", "ovfpackagestatus");
            assertLinkExist(dto, amHref + "?format=envelope", "ovfdocument");
            assertLinkExist(dto, amHref + "?format=diskFile", "imagefile");
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
        params.put("ovf", ovf);

        // Must use the URI resolver in the AM in order to encode the ovf parameter
        return URIResolver.resolveURI(TestServerAndAMListener.AM_URI, "erepos/{erepo}/ovfs/{ovf}",
            params);
    }

}
