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
import static com.abiquo.api.common.UriTestResolver.resolveStatefulVirtualMachineTemplatesURI;
import static com.abiquo.api.common.UriTestResolver.resolveStatefulVirtualMachineTemplatesURIWithCategory;
import static com.abiquo.api.common.UriTestResolver.resolveVirtualMachineTemplatesURI;
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
import com.abiquo.server.core.appslibrary.VirtualMachineTemplate;
import com.abiquo.server.core.appslibrary.VirtualImageConversion;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplatesDto;
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
public class VirtualMachineTemplatesResourceIT extends AbstractJpaGeneratorIT
{
    private static final String SYSADMIN = "sysadmin";

    private final static String AM_BASE_URI =
        "http://localhost:" + String.valueOf(getEmbededServerPort()) + "/am";

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
    public void testGetVirtualMachineTemplatesRaises409WhenNoDatacenterLimits()
    {
        String uri = resolveVirtualMachineTemplatesURI(ent.getId(), datacenter.getId());
        ClientResponse response = get(uri, SYSADMIN, SYSADMIN);
        assertError(response, 409, APIError.ENTERPRISE_NOT_ALLOWED_DATACENTER);
    }

    @Test
    public void testGetVirtualMachineTemplatesRaises404WhenInvalidEnterprise()
    {
        String uri = resolveVirtualMachineTemplatesURI(ent.getId() + 100, datacenter.getId());
        ClientResponse response = get(uri, SYSADMIN, SYSADMIN);
        assertError(response, 404, APIError.NON_EXISTENT_ENTERPRISE);
    }

    /*
     * @Test public void testGetVirtualImagesRaises404WhenInvalidDatacenter() { String uri =
     * resolveVirtualImagesURI(ent.getId(), datacenter.getId() + 100); ClientResponse response =
     * get(uri, SYSADMIN, SYSADMIN); assertError(response, 404, APIError.NON_EXISTENT_DATACENTER); }
     */

    @Test
    public void testGetVirtualMachineTemplatesRaises400WhenInvalidHypervisorType()
    {
        DatacenterLimits limits = datacenterLimitsGenerator.createInstance(ent, datacenter);
        setup(limits);

        String uri = resolveVirtualMachineTemplatesURI(ent.getId(), datacenter.getId());
        uri += "?hypervisorTypeName=INVALID";
        ClientResponse response = get(uri, SYSADMIN, SYSADMIN);
        assertEquals(response.getStatusCode(), 400);

        // TODO: Specific APiError
        // assertError(response, 400);
    }

    @Test
    public void testGetStatefulVirtualMachineTemplatesRaises404WhenInvalidDatacenter()
    {
        String uri = resolveStatefulVirtualMachineTemplatesURI(ent.getId(), datacenter.getId() + 100);
        ClientResponse response = get(uri, SYSADMIN, SYSADMIN);
        assertError(response, 404, APIError.NON_EXISTENT_DATACENTER);
    }

    @Test
    public void testGetStatefulVirtualMachineTemplatesRaises404WhenInvalidEnterprise()
    {
        String uri = resolveStatefulVirtualMachineTemplatesURI(ent.getId() + 100, datacenter.getId());
        ClientResponse response = get(uri, SYSADMIN, SYSADMIN);
        assertError(response, 404, APIError.NON_EXISTENT_ENTERPRISE);
    }

    @Test
    public void testGetStatefulVirtualMachineTemplatesRaises404WhenNoDatacenterLimits()
    {
        String uri = resolveStatefulVirtualMachineTemplatesURI(ent.getId(), datacenter.getId());
        ClientResponse response = get(uri, SYSADMIN, SYSADMIN);
        assertError(response, 409, APIError.ENTERPRISE_NOT_ALLOWED_DATACENTER);
    }

    @Test
    public void testGetStatefulVirtualMachineTemplatesRaises404WhenInvalidCategory()
    {
        DatacenterLimits limits = datacenterLimitsGenerator.createInstance(ent, datacenter);
        setup(limits);

        String uri =
            resolveStatefulVirtualMachineTemplatesURIWithCategory(ent.getId(), datacenter.getId(),
                "nonexisting");
        ClientResponse response = get(uri, SYSADMIN, SYSADMIN);
        assertError(response, 404, APIError.NON_EXISTENT_CATEGORY);
    }

    @Test
    public void testGetVirtualMachineTemplates()
    {
        VirtualMachineTemplate vi1 = virtualMachineTemplateGenerator.createInstance(ent, repository);
        VirtualMachineTemplate vi2 = virtualMachineTemplateGenerator.createInstance(ent, repository);
        DatacenterLimits limits = datacenterLimitsGenerator.createInstance(ent, datacenter);

        setup(limits, vi1.getCategory(), vi2.getCategory(), vi1, vi2);

        String uri = resolveVirtualMachineTemplatesURI(ent.getId(), datacenter.getId());
        ClientResponse response = get(uri, SYSADMIN, SYSADMIN);
        assertEquals(response.getStatusCode(), 200);

        VirtualMachineTemplatesDto dto = response.getEntity(VirtualMachineTemplatesDto.class);
        assertEquals(dto.getCollection().size(), 2);
    }

    @Test
    public void testGetStatefulVirtualMachineTemplatesWithoutResults()
    {
        VirtualMachineTemplate vi1 = virtualMachineTemplateGenerator.createInstance(ent, repository);
        DatacenterLimits limits = datacenterLimitsGenerator.createInstance(ent, datacenter);

        setup(limits, vi1.getCategory(), vi1);

        String uri = resolveStatefulVirtualMachineTemplatesURI(ent.getId(), datacenter.getId());
        ClientResponse response = get(uri, SYSADMIN, SYSADMIN);
        assertEquals(response.getStatusCode(), 200);

        VirtualMachineTemplatesDto dto = response.getEntity(VirtualMachineTemplatesDto.class);
        assertEquals(dto.getCollection().size(), 0);
    }

    @Test
    public void testGetStatefulVirtualMachineTemplates()
    {
        VirtualMachineTemplate vmtemplate = virtualMachineTemplateGenerator.createInstance(ent, repository);
        DatacenterLimits limits = datacenterLimitsGenerator.createInstance(ent, datacenter);
        setup(limits, vmtemplate.getCategory(), vmtemplate);

        volume.setVirtualMachineTemplate(vmtemplate);
        update(volume, vmtemplate);

        String uri = resolveStatefulVirtualMachineTemplatesURI(ent.getId(), datacenter.getId());
        ClientResponse response = get(uri, SYSADMIN, SYSADMIN);
        assertEquals(response.getStatusCode(), 200);

        VirtualMachineTemplatesDto dto = response.getEntity(VirtualMachineTemplatesDto.class);
        assertEquals(dto.getCollection().size(), 1);
    }

    @Test
    public void testGetStatefulVirtualMachineTemplatesByCategory()
    {
        VirtualMachineTemplate vmtemplate = virtualMachineTemplateGenerator.createInstance(ent, repository);
        DatacenterLimits limits = datacenterLimitsGenerator.createInstance(ent, datacenter);
        setup(limits, vmtemplate.getCategory(), vmtemplate);

        volume.setVirtualMachineTemplate(vmtemplate);
        update(volume, vmtemplate);

        String uri =
            resolveStatefulVirtualMachineTemplatesURIWithCategory(ent.getId(), datacenter.getId(), vmtemplate
                .getCategory().getName());
        ClientResponse response = get(uri, SYSADMIN, SYSADMIN);
        assertEquals(response.getStatusCode(), 200);

        VirtualMachineTemplatesDto dto = response.getEntity(VirtualMachineTemplatesDto.class);
        assertEquals(dto.getCollection().size(), 1);
    }

    @Test
    public void testGetStatefulVirtualMachineTemplatesByCategoryWithoutResults()
    {
        Category anotherCategory = categoryGenerator.createUniqueInstance();

        VirtualMachineTemplate template = virtualMachineTemplateGenerator.createInstance(ent, repository);
        DatacenterLimits limits = datacenterLimitsGenerator.createInstance(ent, datacenter);
        setup(limits, template.getCategory(), template, anotherCategory);

        volume.setVirtualMachineTemplate(template);
        update(volume, template);

        String uri =
            resolveStatefulVirtualMachineTemplatesURIWithCategory(ent.getId(), datacenter.getId(),
                anotherCategory.getName());
        ClientResponse response = get(uri, SYSADMIN, SYSADMIN);
        assertEquals(response.getStatusCode(), 200);

        VirtualMachineTemplatesDto dto = response.getEntity(VirtualMachineTemplatesDto.class);
        assertEquals(dto.getCollection().size(), 0);
    }

    @Test
    public void testGetVirtualMachineTemplatesCompatibles_compatibleConversionNoCompatible()
    {
        DatacenterLimits limits = datacenterLimitsGenerator.createInstance(ent, datacenter);

        VirtualMachineTemplate vi1 =
            virtualMachineTemplateGenerator.createInstance(ent, repository, DiskFormatType.VDI_FLAT,
                "compatible-vbox");
        VirtualMachineTemplate vi2 =
            virtualMachineTemplateGenerator.createInstance(ent, repository,
                DiskFormatType.VMDK_STREAM_OPTIMIZED, "No-compatible-vbox");

        VirtualImageConversion conversion1 =
            conversionGenerator.createInstance(vi1, DiskFormatType.VMDK_STREAM_OPTIMIZED);

        setup(limits, vi1.getCategory(), vi1, vi2.getCategory(), vi2, conversion1);

        String uri = resolveVirtualMachineTemplatesURI(ent.getId(), datacenter.getId());
        ClientResponse response =
            resource(uri, SYSADMIN, SYSADMIN).queryParam("hypervisorTypeName",
                HypervisorType.VBOX.name()).get();

        assertEquals(response.getStatusCode(), 200);

        VirtualMachineTemplatesDto dto = response.getEntity(VirtualMachineTemplatesDto.class);
        assertEquals(dto.getCollection().size(), 1);
    }

    @Test
    public void testGetVirtualMachineTemplatesCompatibles_NoCompatibleConversionCompatible()
    {
        DatacenterLimits limits = datacenterLimitsGenerator.createInstance(ent, datacenter);

        VirtualMachineTemplate vi1 =
            virtualMachineTemplateGenerator.createInstance(ent, repository,
                DiskFormatType.VMDK_STREAM_OPTIMIZED, "compatible-vbox");
        VirtualMachineTemplate vi2 =
            virtualMachineTemplateGenerator.createInstance(ent, repository,
                DiskFormatType.VMDK_STREAM_OPTIMIZED, "No-compatible-vbox");

        VirtualImageConversion conversion1 =
            conversionGenerator.createInstance(vi1, DiskFormatType.VDI_FLAT);
        conversion1.setState(ConversionState.FINISHED);

        setup(limits, vi1.getCategory(), vi1, vi2.getCategory(), vi2, conversion1);

        String uri = resolveVirtualMachineTemplatesURI(ent.getId(), datacenter.getId());
        ClientResponse response =
            resource(uri, SYSADMIN, SYSADMIN).queryParam("hypervisorTypeName",
                HypervisorType.VBOX.name()).get();

        assertEquals(response.getStatusCode(), 200);

        VirtualMachineTemplatesDto dto = response.getEntity(VirtualMachineTemplatesDto.class);
        assertEquals(dto.getCollection().size(), 1);
    }

}
