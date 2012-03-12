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

import static com.abiquo.api.common.Assert.assertLinkExist;
import static com.abiquo.api.common.UriTestResolver.resolveDatacenterRepositoryURI;
import static com.abiquo.api.common.UriTestResolver.resolveDatacenterURI;
import static com.abiquo.api.common.UriTestResolver.resolveEnterpriseURI;
import static com.abiquo.api.common.UriTestResolver.resolveVirtualMachineTemplateURI;
import static com.abiquo.api.common.UriTestResolver.resolveVirtualMachineTemplatesURI;
import static com.abiquo.api.resources.RemoteServiceResource.createPersistenceObject;
import static com.abiquo.testng.TestConfig.AM_INTEGRATION_TESTS;
import static com.abiquo.testng.TestConfig.getParameter;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.wink.client.ClientResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.api.resources.AbstractJpaGeneratorIT;
import com.abiquo.api.resources.DatacenterResource;
import com.abiquo.api.resources.EnterpriseResource;
import com.abiquo.api.services.InfrastructureService;
import com.abiquo.appliancemanager.client.AMClient;
import com.abiquo.appliancemanager.client.AMClientException;
import com.abiquo.appliancemanager.transport.EnterpriseRepositoryDto;
import com.abiquo.appliancemanager.transport.TemplateStateDto;
import com.abiquo.appliancemanager.transport.TemplateStatusEnumType;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.appslibrary.DatacenterRepositoryDto;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplateDto;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplatesDto;
import com.abiquo.server.core.enterprise.DatacenterLimits;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.Privilege;
import com.abiquo.server.core.enterprise.Role;
import com.abiquo.server.core.enterprise.User;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.RemoteService;
import com.abiquo.server.core.infrastructure.RemoteServiceDto;
import com.abiquo.testng.TestServerAndAMListener;

@Test(groups = {AM_INTEGRATION_TESTS})
public class ApplianceManagerResourceIT extends AbstractJpaGeneratorIT
{
    private final static Logger LOG = LoggerFactory.getLogger(ApplianceManagerResourceIT.class);

    // to add the am properly
    @Autowired
    private InfrastructureService service;

    private AMClient amclient;

    private static final String SYSADMIN = "sysadmin";

    // TODO use configured port in OVFPackageRepositoryListener
    private static final String DEFAULT_OVF = "http://localhost:7979/testovf/description.ovf";

    // "http://rs.bcn.abiquo.com/m0n0wall/description.ovf";
    // "http://abiquo-repository.abiquo.com/m0n0wall/m0n0wall-1.3b18-i386-monolithicFlat.1.5.ovf";

    protected final static String REPO_PATH = getParameter(
        "abiquo.appliancemanager.localRepositoryPath", "/tmp/testrepo");

    private RemoteServiceDto amDto()
    {
        RemoteServiceDto am = new RemoteServiceDto();
        am.setType(RemoteServiceType.APPLIANCE_MANAGER);
        am.setUri(TestServerAndAMListener.AM_URI);

        return am;
    }

    private Enterprise ent;

    private Datacenter datacenter;

    @BeforeMethod
    public void setUpDatacenterRepository() throws Exception
    {
        ent = enterpriseGenerator.createUniqueInstance();
        datacenter = datacenterGenerator.createUniqueInstance();
        DatacenterLimits limits = datacenterLimitsGenerator.createInstance(ent, datacenter);

        setup(ent, datacenter, limits);

        amclient = new AMClient().initialize(TestServerAndAMListener.AM_URI,false);

        setUpUser();
        setUpApplianceManagerInDatacenter();

        cleanupRepository();
    }

    private static void cleanupRepository()
    {
        try
        {
            File vmrepo = new File(REPO_PATH);
            if (vmrepo.exists())
            {
                FileUtils.deleteDirectory(vmrepo);

            }

            vmrepo = new File(REPO_PATH);
            vmrepo.mkdirs();

            new File(FilenameUtils.concat(REPO_PATH, ".abiquo_repository")).createNewFile();
        }
        catch (Exception e)
        {
            throw new RuntimeException("Can't clean up " + REPO_PATH, e);
        }
    }

    private void setUpUser()
    {
        Role r = roleGenerator.createInstanceSysAdmin("sysRole");
        User u = userGenerator.createInstance(ent, r, SYSADMIN, SYSADMIN);

        List<Object> entitiesToSetup = new ArrayList<Object>();

        for (Privilege p : r.getPrivileges())
        {
            entitiesToSetup.add(p);
        }
        entitiesToSetup.add(r);
        entitiesToSetup.add(u);

        setup(entitiesToSetup.toArray());
    }

    private void setUpApplianceManagerInDatacenter() throws Exception
    {
        amclient.checkService();

        RemoteService am = createPersistenceObject(amDto());
        service.addRemoteService(am, datacenter.getId());
    }

    @Test(groups = {AM_INTEGRATION_TESTS})
    public void deleteVirtualMachineTemplate() throws AMClientException
    {

        amclient.installTemplateDefinition(ent.getId(), DEFAULT_OVF);

        boolean isdownloaded = false;
        while (!isdownloaded)
        {
            TemplateStateDto status =
                amclient.getTemplateStatus(ent.getId(), DEFAULT_OVF);

            if (status.getStatus() == TemplateStatusEnumType.ERROR)
            {
                isdownloaded = true;
                assertNull(status.getErrorCause());
            }
            else if (status.getStatus() == TemplateStatusEnumType.DOWNLOAD)
            {
                LOG.info("Download {}", DEFAULT_OVF);
                isdownloaded = true;
            }
            else if (status.getStatus() == TemplateStatusEnumType.DOWNLOADING)
            {
                LOG.info("{} Installing {}", status.getDownloadingProgress().toString(),
                    DEFAULT_OVF);
            }

            try
            {
                Thread.sleep(100);
            }
            catch (InterruptedException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        try
        {
            Thread.sleep(2000);
        }
        catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String uri = resolveVirtualMachineTemplatesURI(ent.getId(), datacenter.getId());
        ClientResponse response = get(uri, SYSADMIN, SYSADMIN);
        VirtualMachineTemplatesDto dto = response.getEntity(VirtualMachineTemplatesDto.class);

        for (VirtualMachineTemplateDto vmtemplate : dto.getCollection())
        {
            String uriVmtemplate =
                resolveVirtualMachineTemplateURI(ent.getId(), datacenter.getId(),
                    vmtemplate.getId());
            ClientResponse resp = get(uriVmtemplate, SYSADMIN, SYSADMIN);
            assertEquals(response.getStatusCode(), 200);

            resp = delete(uriVmtemplate, SYSADMIN, SYSADMIN);
            assertEquals(resp.getStatusCode(), 200);

            resp = get(uri, SYSADMIN, SYSADMIN);
            assertEquals(resp.getStatusCode(), 404);

        }

    }

    @Test(groups = {AM_INTEGRATION_TESTS})
    public void deleteSharedVMTemplate() throws AMClientException
    {
        amclient.installTemplateDefinition(ent.getId(), DEFAULT_OVF);

        boolean isdownloaded = false;
        while (!isdownloaded)
        {
            TemplateStateDto status =
                amclient.getTemplateStatus(ent.getId(), DEFAULT_OVF);

            if (status.getStatus() == TemplateStatusEnumType.ERROR)
            {
                isdownloaded = true;
                assertNull(status.getErrorCause());
            }
            else if (status.getStatus() == TemplateStatusEnumType.DOWNLOAD)
            {
                LOG.info("Download {}", DEFAULT_OVF);
                isdownloaded = true;
            }
            else if (status.getStatus() == TemplateStatusEnumType.DOWNLOADING)
            {
                LOG.info("{} Installing {}", status.getDownloadingProgress().toString(),
                    DEFAULT_OVF);
            }

            try
            {
                Thread.sleep(100);
            }
            catch (InterruptedException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        try
        {
            Thread.sleep(2000);
        }
        catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String uri = resolveVirtualMachineTemplatesURI(ent.getId(), datacenter.getId());
        ClientResponse response = get(uri, SYSADMIN, SYSADMIN);
        VirtualMachineTemplatesDto dto = response.getEntity(VirtualMachineTemplatesDto.class);

        for (VirtualMachineTemplateDto vmtmplate : dto.getCollection())
        {
            String uriVmtemplate =
                resolveVirtualMachineTemplateURI(ent.getId(), datacenter.getId(), vmtmplate.getId());
            ClientResponse resp = get(uriVmtemplate, SYSADMIN, SYSADMIN);
            assertEquals(response.getStatusCode(), 200);

            VirtualMachineTemplateDto vmtemplateDto =
                resp.getEntity(VirtualMachineTemplateDto.class);
            vmtemplateDto.setShared(Boolean.TRUE);

            resp = put(uriVmtemplate, vmtemplateDto, SYSADMIN, SYSADMIN);
            assertEquals(resp.getStatusCode(), 200);

            resp = delete(uriVmtemplate, SYSADMIN, SYSADMIN);
            assertEquals(resp.getStatusCode(), 200);

            resp = get(uri, SYSADMIN, SYSADMIN);
            assertEquals(resp.getStatusCode(), 404);

        }
    }

    @Test
    public void datacenterRepositoryIsCreated()
    {
        final Integer enterpriseId = ent.getId();
        final Integer datacenterId = datacenter.getId();
        final String amRepoUrl = amEnterpriseRepositoryUrl(enterpriseId);

        // Check for vapp1
        ClientResponse response =
            get(resolveDatacenterRepositoryURI(enterpriseId, datacenterId), SYSADMIN, SYSADMIN);
        assertEquals(response.getStatusCode(), Status.OK.getStatusCode());

        DatacenterRepositoryDto repodto = response.getEntity(DatacenterRepositoryDto.class);
        assertNotNull(repodto);
        assertNotNull(repodto.getRepositoryLocation());

        assertLinkExist(repodto, resolveEnterpriseURI(enterpriseId), EnterpriseResource.ENTERPRISE);
        assertLinkExist(repodto, resolveDatacenterURI(datacenterId), DatacenterResource.DATACENTER);
        assertLinkExist(repodto, amRepoUrl, "applianceManagerRepositoryUri");

        ClientResponse amresponse = get(amRepoUrl, DatacenterRepositoryDto.MEDIA_TYPE);
        assertEquals(amresponse.getStatusCode(), Status.OK.getStatusCode());

        EnterpriseRepositoryDto amrepo = amresponse.getEntity(EnterpriseRepositoryDto.class);
        assertNotNull(amrepo);
        assertEquals(amrepo.getId(), enterpriseId);

        assertNotNull(amrepo.getCapacityMb());
        assertNotNull(amrepo.getEnterpriseUsedMb());
        assertNotNull(amrepo.getRemainingMb());

        assertTrue(amrepo.getCapacityMb() > amrepo.getEnterpriseUsedMb());
        assertTrue(amrepo.getCapacityMb() >= amrepo.getRemainingMb());
    }

    @Test
    public void createOVFandWaitUntilVirtualMachineTemplateCreated() throws AMClientException
    {
        final Integer enterpriseId = ent.getId();
        amclient.installTemplateDefinition(enterpriseId, DEFAULT_OVF);

        boolean isdown = false;
        while (!isdown)
        {
            TemplateStateDto status =
                amclient.getTemplateStatus(enterpriseId, DEFAULT_OVF);

            if (status.getStatus() == TemplateStatusEnumType.ERROR)
            {
                isdown = true;
                assertNull(status.getErrorCause());
            }
            else if (status.getStatus() == TemplateStatusEnumType.DOWNLOAD)
            {
                LOG.info("Download {}", DEFAULT_OVF);
                isdown = true;
            }
            else if (status.getStatus() == TemplateStatusEnumType.DOWNLOADING)
            {
                LOG.info("{} Installing {}", status.getDownloadingProgress().toString(),
                    DEFAULT_OVF);
            }

            try
            {
                Thread.sleep(100);
            }
            catch (InterruptedException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        try
        {
            // wait for OVFpackageInstanceStatusEvent to create the VirtualImage
            Thread.sleep(2000);
        }
        catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String uri = resolveVirtualMachineTemplatesURI(ent.getId(), datacenter.getId());
        ClientResponse response = get(uri, SYSADMIN, SYSADMIN);
        VirtualMachineTemplatesDto dto = response.getEntity(VirtualMachineTemplatesDto.class);

        assertVirtualMachineTemplateExist(dto.getCollection(), DEFAULT_OVF);
    }

    private static void assertVirtualMachineTemplateExist(
        final List<VirtualMachineTemplateDto> vmtemplates, final String ovfurl)
    {
        for (VirtualMachineTemplateDto dto : vmtemplates)
        {
            RESTLink ovfpackage = dto.searchLink("templatedefinition");
            if (ovfpackage.getHref().equalsIgnoreCase(ovfurl))
            {
                return;
            }
        }

        fail("virtual machine template not found " + ovfurl);
    }

    private static String amEnterpriseRepositoryUrl(final Integer enterpriseId)
    {
        return String.format("%s/erepos/%s", TestServerAndAMListener.AM_URI,
            enterpriseId.toString());
    }

}
