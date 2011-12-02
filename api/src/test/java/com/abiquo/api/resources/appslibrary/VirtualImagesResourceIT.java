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
import static com.abiquo.api.common.UriTestResolver.resolveStatefulVirtualImagesURI;
import static com.abiquo.api.common.UriTestResolver.resolveStatefulVirtualImagesURIWithCategory;
import static com.abiquo.api.common.UriTestResolver.resolveVirtualImagesURI;
import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.apache.wink.client.ClientResponse;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.resources.AbstractJpaGeneratorIT;
import com.abiquo.model.enumerator.ConversionState;
import com.abiquo.model.enumerator.DiskFormatType;
import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.server.core.appslibrary.Category;
import com.abiquo.server.core.appslibrary.VirtualImage;
import com.abiquo.server.core.appslibrary.VirtualImageConversion;
import com.abiquo.server.core.appslibrary.VirtualImagesDto;
import com.abiquo.server.core.enterprise.DatacenterLimits;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.Privilege;
import com.abiquo.server.core.enterprise.Role;
import com.abiquo.server.core.enterprise.User;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.RemoteService;
import com.abiquo.server.core.infrastructure.Repository;
import com.abiquo.server.core.infrastructure.storage.VolumeManagement;

@Test
public class VirtualImagesResourceIT extends AbstractJpaGeneratorIT
{
    private static final String SYSADMIN = "sysadmin";

    private final static String AM_BASE_URI = "http://localhost:"
        + String.valueOf(getEmbededServerPort()) + "/am";

    private Enterprise ent;

    private Datacenter datacenter;

    private Repository repository;

    // Only used in stateful tests
    private VolumeManagement volume;

    @BeforeMethod
    public void setUpDatacenterRepository()
    {
        ent = enterpriseGenerator.createUniqueInstance();
        datacenter = datacenterGenerator.createUniqueInstance();
        repository = repositoryGenerator.createInstance(datacenter);
        volume = volumeManagementGenerator.createInstance(datacenter, ent);

        RemoteService am =
            remoteServiceGenerator.createInstance(RemoteServiceType.APPLIANCE_MANAGER, datacenter);
        am.setUri(AM_BASE_URI);

        Role role = roleGenerator.createInstanceSysAdmin();
        User user = userGenerator.createInstance(ent, role, SYSADMIN, SYSADMIN);

        List<Object> entitiesToSetup = new ArrayList<Object>();
        entitiesToSetup.add(ent);
        entitiesToSetup.add(datacenter);
        entitiesToSetup.add(volume.getStoragePool().getDevice());
        entitiesToSetup.add(volume.getStoragePool().getTier());
        entitiesToSetup.add(volume.getStoragePool());
        entitiesToSetup.add(volume.getRasd());
        entitiesToSetup.add(volume.getVirtualDatacenter());
        entitiesToSetup.add(volume);
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
    public void testGetVirtualImagesRaises409WhenNoDatacenterLimits()
    {
        String uri = resolveVirtualImagesURI(ent.getId(), datacenter.getId());
        ClientResponse response = get(uri, SYSADMIN, SYSADMIN);
        assertError(response, 409, APIError.ENTERPRISE_NOT_ALLOWED_DATACENTER);
    }

    @Test
    public void testGetVirtualImagesRaises404WhenInvalidEnterprise()
    {
        String uri = resolveVirtualImagesURI(ent.getId() + 100, datacenter.getId());
        ClientResponse response = get(uri, SYSADMIN, SYSADMIN);
        assertError(response, 404, APIError.NON_EXISTENT_ENTERPRISE);
    }

    @Test
    public void testGetVirtualImagesRaises404WhenInvalidDatacenter()
    {
        String uri = resolveVirtualImagesURI(ent.getId(), datacenter.getId() + 100);
        ClientResponse response = get(uri, SYSADMIN, SYSADMIN);
        assertError(response, 404, APIError.NON_EXISTENT_DATACENTER);
    }

    @Test
    public void testGetStatefulVirtualImagesRaises404WhenInvalidDatacenter()
    {
        String uri = resolveStatefulVirtualImagesURI(ent.getId(), datacenter.getId() + 100);
        ClientResponse response = get(uri, SYSADMIN, SYSADMIN);
        assertError(response, 404, APIError.NON_EXISTENT_DATACENTER);
    }

    @Test
    public void testGetStatefulVirtualImagesRaises404WhenInvalidEnterprise()
    {
        String uri = resolveStatefulVirtualImagesURI(ent.getId() + 100, datacenter.getId());
        ClientResponse response = get(uri, SYSADMIN, SYSADMIN);
        assertError(response, 404, APIError.NON_EXISTENT_ENTERPRISE);
    }

    @Test
    public void testGetStatefulVirtualImagesRaises404WhenNoDatacenterLimits()
    {
        String uri = resolveStatefulVirtualImagesURI(ent.getId(), datacenter.getId());
        ClientResponse response = get(uri, SYSADMIN, SYSADMIN);
        assertError(response, 409, APIError.ENTERPRISE_NOT_ALLOWED_DATACENTER);
    }

    @Test
    public void testGetStatefulVirtualImagesRaises404WhenInvalidCategory()
    {
        DatacenterLimits limits = datacenterLimitsGenerator.createInstance(ent, datacenter);
        setup(limits);

        String uri =
            resolveStatefulVirtualImagesURIWithCategory(ent.getId(), datacenter.getId(), 50);
        ClientResponse response = get(uri, SYSADMIN, SYSADMIN);
        assertError(response, 404, APIError.NON_EXISTENT_CATEGORY);
    }

    @Test
    public void testGetVirtualImages()
    {
        VirtualImage vi1 = virtualImageGenerator.createInstance(ent, repository);
        VirtualImage vi2 = virtualImageGenerator.createInstance(ent, repository);
        DatacenterLimits limits = datacenterLimitsGenerator.createInstance(ent, datacenter);

        setup(limits, vi1.getCategory(), vi2.getCategory(), vi1, vi2);

        String uri = resolveVirtualImagesURI(ent.getId(), datacenter.getId());
        ClientResponse response = get(uri, SYSADMIN, SYSADMIN);
        assertEquals(response.getStatusCode(), 200);

        VirtualImagesDto dto = response.getEntity(VirtualImagesDto.class);
        assertEquals(dto.getCollection().size(), 2);
    }

    @Test
    public void testGetStatefulVirtualImagesWithoutResults()
    {
        VirtualImage vi1 = virtualImageGenerator.createInstance(ent, repository);
        DatacenterLimits limits = datacenterLimitsGenerator.createInstance(ent, datacenter);

        setup(limits, vi1.getCategory(), vi1);

        String uri = resolveStatefulVirtualImagesURI(ent.getId(), datacenter.getId());
        ClientResponse response = get(uri, SYSADMIN, SYSADMIN);
        assertEquals(response.getStatusCode(), 200);

        VirtualImagesDto dto = response.getEntity(VirtualImagesDto.class);
        assertEquals(dto.getCollection().size(), 0);
    }

    @Test
    public void testGetStatefulVirtualImages()
    {
        VirtualImage image = virtualImageGenerator.createInstance(ent, repository);
        DatacenterLimits limits = datacenterLimitsGenerator.createInstance(ent, datacenter);
        setup(limits, image.getCategory(), image);

        volume.setVirtualImage(image);
        update(volume, image);

        String uri = resolveStatefulVirtualImagesURI(ent.getId(), datacenter.getId());
        ClientResponse response = get(uri, SYSADMIN, SYSADMIN);
        assertEquals(response.getStatusCode(), 200);

        VirtualImagesDto dto = response.getEntity(VirtualImagesDto.class);
        assertEquals(dto.getCollection().size(), 1);
    }

    @Test
    public void testGetStatefulVirtualImagesByCategory()
    {
        VirtualImage image = virtualImageGenerator.createInstance(ent, repository);
        DatacenterLimits limits = datacenterLimitsGenerator.createInstance(ent, datacenter);
        setup(limits, image.getCategory(), image);

        volume.setVirtualImage(image);
        update(volume, image);

        String uri =
            resolveStatefulVirtualImagesURIWithCategory(ent.getId(), datacenter.getId(), image
                .getCategory().getId());
        ClientResponse response = get(uri, SYSADMIN, SYSADMIN);
        assertEquals(response.getStatusCode(), 200);

        VirtualImagesDto dto = response.getEntity(VirtualImagesDto.class);
        assertEquals(dto.getCollection().size(), 1);
    }

    @Test
    public void testGetStatefulVirtualImagesByCategoryWithoutResults()
    {
        Category anotherCategory = categoryGenerator.createUniqueInstance();

        VirtualImage image = virtualImageGenerator.createInstance(ent, repository);
        DatacenterLimits limits = datacenterLimitsGenerator.createInstance(ent, datacenter);
        setup(limits, image.getCategory(), image, anotherCategory);

        volume.setVirtualImage(image);
        update(volume, image);

        String uri =
            resolveStatefulVirtualImagesURIWithCategory(ent.getId(), datacenter.getId(),
                anotherCategory.getId());
        ClientResponse response = get(uri, SYSADMIN, SYSADMIN);
        assertEquals(response.getStatusCode(), 200);

        VirtualImagesDto dto = response.getEntity(VirtualImagesDto.class);
        assertEquals(dto.getCollection().size(), 0);
    }

    @Test
    public void testGetVirtualImagesCompatibles_compatibleConversionNoCompatible()
    {
        DatacenterLimits limits = datacenterLimitsGenerator.createInstance(ent, datacenter);

        VirtualImage vi1 =
            virtualImageGenerator.createInstance(ent, repository, DiskFormatType.VDI_FLAT,
                "compatible-vbox");
        VirtualImage vi2 =
            virtualImageGenerator.createInstance(ent, repository,
                DiskFormatType.VMDK_STREAM_OPTIMIZED, "No-compatible-vbox");

        VirtualImageConversion conversion1 =
            conversionGenerator.createInstance(vi1, DiskFormatType.VMDK_STREAM_OPTIMIZED);

        setup(limits, vi1.getCategory(), vi1, vi2.getCategory(), vi2, conversion1);

        String uri = resolveVirtualImagesURI(ent.getId(), datacenter.getId());
        ClientResponse response =
            resource(uri, SYSADMIN, SYSADMIN).queryParam("hypervisorTypeName",
                String.valueOf(HypervisorType.VBOX.id())).get();

        assertEquals(response.getStatusCode(), 200);

        VirtualImagesDto dto = response.getEntity(VirtualImagesDto.class);
        assertEquals(dto.getCollection().size(), 1);
    }

    @Test
    public void testGetVirtualImagesCompatibles_NoCompatibleConversionCompatible()
    {
        DatacenterLimits limits = datacenterLimitsGenerator.createInstance(ent, datacenter);

        VirtualImage vi1 =
            virtualImageGenerator.createInstance(ent, repository,
                DiskFormatType.VMDK_STREAM_OPTIMIZED, "compatible-vbox");
        VirtualImage vi2 =
            virtualImageGenerator.createInstance(ent, repository,
                DiskFormatType.VMDK_STREAM_OPTIMIZED, "No-compatible-vbox");

        VirtualImageConversion conversion1 =
            conversionGenerator.createInstance(vi1, DiskFormatType.VDI_FLAT);
        conversion1.setState(ConversionState.FINISHED);

        setup(limits, vi1.getCategory(), vi1, vi2.getCategory(), vi2, conversion1);

        String uri = resolveVirtualImagesURI(ent.getId(), datacenter.getId());
        ClientResponse response =
            resource(uri, SYSADMIN, SYSADMIN).queryParam("hypervisorTypeName",
                String.valueOf(HypervisorType.VBOX.id())).get();

        assertEquals(response.getStatusCode(), 200);

        VirtualImagesDto dto = response.getEntity(VirtualImagesDto.class);
        assertEquals(dto.getCollection().size(), 1);
    }

}
