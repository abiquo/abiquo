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
import static com.abiquo.api.common.UriTestResolver.resolveVirtualImageURI;
import static com.abiquo.testng.TestConfig.AM_INTEGRATION_TESTS;
import static com.abiquo.testng.TestConfig.getParameter;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.abiquo.api.services.appslibrary.VirtualImageService;
import com.abiquo.appliancemanager.client.ApplianceManagerResourceStubImpl;
import com.abiquo.appliancemanager.transport.EnterpriseRepositoryDto;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceStatusDto;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceStatusType;
import com.abiquo.appliancemanager.util.URIResolver;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.server.core.appslibrary.DatacenterRepositoryDto;
import com.abiquo.server.core.cloud.VirtualImage;
import com.abiquo.server.core.cloud.VirtualImageDto;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.Privilege;
import com.abiquo.server.core.enterprise.Role;
import com.abiquo.server.core.enterprise.User;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.RemoteServiceDto;

@Test(groups = {AM_INTEGRATION_TESTS})
public class VirtualImageResourceIT extends AbstractJpaGeneratorIT
{
    private final static Logger LOG = LoggerFactory.getLogger(VirtualImageResourceIT.class);

    private final static String AM_BASE_URI = "http://localhost:"
        + String.valueOf(getEmbededServerPort()) + "/am";

    // to add the am properly
    @Autowired
    private InfrastructureService service;

    private ApplianceManagerResourceStubImpl amclient;

    private static final String SYSADMIN = "sysadmin";

    private static final String DEFAULT_OVF = "http://localhost:8282/testovf/description.ovf";

    // "http://rs.bcn.abiquo.com/m0n0wall/description.ovf";
    // "http://abiquo-repository.abiquo.com/m0n0wall/m0n0wall-1.3b18-i386-monolithicFlat.1.5.ovf";

    protected final static String REPO_PATH = getParameter(
        "abiquo.appliancemanager.localRepositoryPath", "/tmp/testrepo");

    private RemoteServiceDto amDto()
    {
        RemoteServiceDto am = new RemoteServiceDto();
        am.setType(RemoteServiceType.APPLIANCE_MANAGER);
        am.setUri(AM_BASE_URI);

        return am;
    }

    private Enterprise ent;

    private Datacenter datacenter;

    private VirtualImage virtualImage;

    @BeforeMethod
    public void setUpDatacenterRepository()
    {
        ent = enterpriseGenerator.createUniqueInstance();
        datacenter = datacenterGenerator.createUniqueInstance();

        setup(ent, datacenter);

        amclient = new ApplianceManagerResourceStubImpl(AM_BASE_URI);

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

    private void setUpApplianceManagerInDatacenter()
    {
        amclient.checkService();

        service.addRemoteService(amDto(), datacenter.getId());
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

        ClientResponse amresponse = get(amRepoUrl);
        assertEquals(amresponse.getStatusCode(), Status.OK.getStatusCode());

        EnterpriseRepositoryDto amrepo = amresponse.getEntity(EnterpriseRepositoryDto.class);
        assertNotNull(amrepo);
        assertEquals(amrepo.getId(), enterpriseId);
        assertNotNull(amrepo.getName());

        assertNotNull(amrepo.getRepositoryCapacityMb());
        assertNotNull(amrepo.getRepositoryEnterpriseUsedMb());
        assertNotNull(amrepo.getRepositoryRemainingMb());

        assertTrue(amrepo.getRepositoryCapacityMb() > amrepo.getRepositoryEnterpriseUsedMb());
        assertTrue(amrepo.getRepositoryCapacityMb() >= amrepo.getRepositoryRemainingMb());
    }

    @Test
    public void createOVFandWaitUntilVirtualImageCreated()
    {
        final Integer datacenterId = datacenter.getId();
        final Integer enterpriseId = ent.getId();
        amclient.createOVFPackageInstance(enterpriseId.toString(), DEFAULT_OVF);

        boolean isdown = false;
        while (!isdown)
        {
            OVFPackageInstanceStatusDto status =
                amclient.getOVFPackageInstanceStatus(enterpriseId.toString(), DEFAULT_OVF);

            if (status.getOvfPackageStatus() == OVFPackageInstanceStatusType.ERROR)
            {
                isdown = true;
                assertNull(status.getErrorCause());
            }
            else if (status.getOvfPackageStatus() == OVFPackageInstanceStatusType.DOWNLOAD)
            {
                LOG.info("Download {}", DEFAULT_OVF);
                isdown = true;
            }
            else if (status.getOvfPackageStatus() == OVFPackageInstanceStatusType.DOWNLOADING)
            {
                LOG.info("{} Installing {}", status.getProgress().toString(), DEFAULT_OVF);
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

        List<VirtualImage> images = vimageService.getVirtualImages(enterpriseId, datacenterId);
        virtualImage = assertVirtualImageExist(images, DEFAULT_OVF);
    }

    /**
     * TODO fix test
     * java.lang.IllegalArgumentException: variable 'virtualimage' was not supplied a value
    at com.abiquo.api.common.UriTestResolver.resolveVirtualImageURI(UriTestResolver.java:734)
    at com.abiquo.api.resources.appslibrary.VirtualImageResourceIT.testGetVirtualImage(VirtualImageResourceIT.java:276)
     * */
    @Test(enabled=false)
    private void getVirtualImage()
    {
        // TODO: Change this to create the VI in the DB when we have the category, icon and other
        // mandatory objects in the model.
        createOVFandWaitUntilVirtualImageCreated();
        assertNotNull(virtualImage);

        String uri = resolveVirtualImageURI(ent.getId(), datacenter.getId(), virtualImage.getId());
        ClientResponse response = get(uri);
        assertEquals(response.getStatusCode(), 200);

        VirtualImageDto dto = response.getEntity(VirtualImageDto.class);
        String href = amOVFPackageInstanceUrl(ent.getId(), dto.getOvfid());

        assertLinkExist(dto, href, "ovfpackageinstance");
        assertLinkExist(dto, href + "?format=status", "ovfpackagestatus");
        assertLinkExist(dto, href + "?format=envelope", "ovfdocument");
        assertLinkExist(dto, href + "?format=diskFile", "imagefile");
    }

    // TODO: Make test for VirtualImage with master. Test link existance

    private static VirtualImage assertVirtualImageExist(final List<VirtualImage> vimages,
        final String ovfurl)
    {
        for (VirtualImage vimage : vimages)
        {
            if (vimage.getOvfid().equalsIgnoreCase(ovfurl))
            {
                return vimage;
            }
        }

        fail("virtual image not found " + ovfurl);
        return null;
    }

    @Autowired
    VirtualImageService vimageService;

    private static String amEnterpriseRepositoryUrl(final Integer enterpriseId)
    {
        return String.format("%s/erepos/%s", AM_BASE_URI, enterpriseId.toString());
    }

    private static String amOVFPackageInstanceUrl(final Integer enterpriseId, final String ovf)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("erepo", enterpriseId.toString());
        params.put("ovf", ovf);

        // Must use the URI resolver in the AM in order to encode the ovf parameter
        return URIResolver.resolveURI(AM_BASE_URI, "erepos/{erepo}/ovfs/{ovf}", params);
    }

}
