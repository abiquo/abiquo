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

import static com.abiquo.api.common.UriTestResolver.resolveOVFPackageURI;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.io.File;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.Resource;
import org.apache.wink.common.internal.utils.UriHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import com.abiquo.api.resources.AbstractResourcePlusAMIT;
import com.abiquo.api.services.stub.ApplianceManagerStub;
import com.abiquo.appliancemanager.transport.MemorySizeUnit;
import com.abiquo.appliancemanager.transport.OVFPackageDiskFormat;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceDto;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceStatusDto;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceStatusType;

/**
 * Requires: - /tmp/testvmrepo directory created - access to
 * http://rs.bcn.abiquo.com/m0n0wall/description.ovf
 * 
 * @author destevez
 */

public class InstallOVFPackageIT extends AbstractResourcePlusAMIT
{

    /** Used to consume the remote service Appliance Manager. */
    @Autowired
    ApplianceManagerStub amStub;

    private static final String AM_SERVICE_MAPPING = "http://localhost:9009/am";

    private static final String OVF_PACKAGE_LOCATION =
        "http://rs.bcn.abiquo.com/m0n0wall/description.ovf";

    private static final int ENTERPRISE_ID = 1;

    private static final int OVF_ID = 1;

    private static final int TARGET_DATACENTER_ID = 1;

    private static final int QUERY_DOWNLOAD_PROGRESS_TIME = 200;

    @Override
    protected List data()
    {
        // TODO Auto-generated method stub
        return Collections.singletonList("/data/installAM.xml");
    }

    @Test
    public void installOVFPackage()
    {
        String installPackageAction =
            UriHelper.appendPathToBaseUri(resolveOVFPackageURI(ENTERPRISE_ID, OVF_ID),
                OVFPackageResource.INSTALL_ACTION);

        // System.out.println("installPackageAction: " + installPackageAction);

        Resource resource =
            client.resource(installPackageAction).accept(MediaType.APPLICATION_XML)
                .queryParam(OVFPackageResource.INSTALL_TARGET_QUERY_PARAM, TARGET_DATACENTER_ID);

        ClientResponse response = resource.post(null);
        assertEquals(response.getStatusCode(), 202);

        OVFPackageInstanceStatusDto stat =
            stat =
                amStub.getOVFPackageStatus(AM_SERVICE_MAPPING, String.valueOf(1),
                    OVF_PACKAGE_LOCATION);

        // System.out.println("stat.getProgress(): " + stat.getProgress());
        // System.out.println("stat.getOvfPackageStatus().toString(): "
        // + stat.getOvfPackageStatus().toString());

        response = null;
        while (stat.getOvfPackageStatus().equals(OVFPackageInstanceStatusType.DOWNLOADING))
        {
            try
            {
                assertTrue(stat.getProgress() > 0);
                assertTrue(stat.getOvfPackageStatus().equals(
                    OVFPackageInstanceStatusType.DOWNLOADING));

                stat =
                    amStub.getOVFPackageStatus(AM_SERVICE_MAPPING, String.valueOf(OVF_ID),
                        OVF_PACKAGE_LOCATION);
                // System.out.println("stat.getProgress(): " + stat.getProgress());
                // System.out.println("stat.getOvfPackageStatus().toString(): "
                // + stat.getOvfPackageStatus().toString());

                Thread.sleep(QUERY_DOWNLOAD_PROGRESS_TIME);

            }
            catch (InterruptedException e)
            {
                // e.printStackTrace();
                fail("Thread execution problems getting donwload progress", e);
            }
        }

        // check file exists
        File file = new File("/tmp/testvmrepo/1/rs.bcn.abiquo.com/m0n0wall/deploy.error");
        assertTrue(!file.exists());

        // New deploy causes HTTP 400: Bad Request
        // resource =
        // client.resource(installPackageAction).accept(MediaType.APPLICATION_XML).queryParam(
        // OVFPackageResource.INSTALL_TARGET_QUERY_PARAM, 1);
        //
        // response = resource.post(null);
        // assertEquals(response.getStatusCode(), 400);

    }

    // TODO: Test BUNDLE !!!!
    private OVFPackageInstanceDto createTestDiskInfoBundle(final String ovfId, final String snapshot)
    {
        final String bundleOVFid =
            ovfId.substring(0, ovfId.lastIndexOf('.')) + "-snapshot-" + snapshot + ".ovf";

        OVFPackageInstanceDto di = new OVFPackageInstanceDto();
        di.setName("theBundleDiskName");
        di.setDescription("theBundleDiskDescription");

        di.setCpu(1);
        di.setHd(new Long(1024 * 1024 * 10));
        di.setRam(512l);
        di.setHdSizeUnit(MemorySizeUnit.BYTE);
        di.setRamSizeUnit(MemorySizeUnit.BYTE);

        di.setIconPath("thiIconPath");
        di.setDiskFileFormat(OVFPackageDiskFormat.VMDK_FLAT);

        // di.setImageSize(121212); // XXX not use
        di.setDiskFilePath("XXXXXXXXX do not used XXXXXXXXXXX"); // XXX not use
        di.setOvfUrl(bundleOVFid);

        di.setIdEnterprise(Integer.valueOf(ENTERPRISE_ID));
        di.setIdUser(2);
        di.setCategoryName("new test category");

        return di;
    }

}
