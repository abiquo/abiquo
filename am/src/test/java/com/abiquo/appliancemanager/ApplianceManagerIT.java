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

package com.abiquo.appliancemanager;

import static com.abiquo.appliancemanager.AMConsumerTestListener.assertEventsEmpty;
import static com.abiquo.appliancemanager.AMConsumerTestListener.expectedEvents;
import static com.abiquo.appliancemanager.ApplianceManagerAsserts.createBundleDiskFile;
import static com.abiquo.appliancemanager.ApplianceManagerAsserts.createTestDiskInfoBundle;
import static com.abiquo.appliancemanager.transport.TemplateStatusEnumType.DOWNLOAD;
import static com.abiquo.appliancemanager.transport.TemplateStatusEnumType.DOWNLOADING;
import static com.abiquo.appliancemanager.transport.TemplateStatusEnumType.ERROR;
import static com.abiquo.appliancemanager.transport.TemplateStatusEnumType.NOT_DOWNLOAD;
import static com.abiquo.testng.OVFRemoteRepositoryListener.ovfId;
import static com.abiquo.testng.OVFRemoteRepositoryListener.ovf_fileNotFound;
import static com.abiquo.testng.OVFRemoteRepositoryListener.ovf_invalidDiskFormat;
import static com.abiquo.testng.OVFRemoteRepositoryListener.ovf_invalidUrl;
import static com.abiquo.testng.OVFRemoteRepositoryListener.ovf_malformed;
import static com.abiquo.testng.OVFRemoteRepositoryListener.ovf_multiDisk;
import static com.abiquo.testng.OVFRemoteRepositoryListener.ovf_notFound;
import static com.abiquo.testng.TestConfig.AM_INTEGRATION_TESTS;
import static com.abiquo.testng.TestServerListener.BASE_URI;
import static org.testng.Assert.fail;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.appliancemanager.client.AMClient;
import com.abiquo.appliancemanager.client.AMClientException;
import com.abiquo.appliancemanager.transport.EnterpriseRepositoryDto;
import com.abiquo.appliancemanager.transport.RepositoryConfigurationDto;
import com.abiquo.appliancemanager.transport.TemplateDto;
import com.abiquo.appliancemanager.transport.TemplateStatusEnumType;

@Test(groups = {AM_INTEGRATION_TESTS})
public class ApplianceManagerIT
{
    final static Integer idEnterprise =  1;

    protected AMClient client;

    protected ApplianceManagerAsserts asserts;

    @BeforeClass
    public void setUp() throws IOException
    {
        client = new AMClient().initialize(BASE_URI, false);
        asserts = new ApplianceManagerAsserts(client);
    }

    @BeforeMethod
    public void assertCleanPre() throws AMClientException
    {
        asserts.ovfInstanceNoExist(ovfId);
        assertEventsEmpty();
    }

    @AfterMethod
    public void assertCleanPost()throws AMClientException
    {
        asserts.ovfInstanceNoExist(ovfId);
        assertEventsEmpty();
    }

    public void test_RepositoryConfig()throws AMClientException
    {
        client.checkService();

        RepositoryConfigurationDto config = client.getRepositoryConfiguration();
        Assert.assertFalse(StringUtils.isEmpty(config.getLocation()));
    }

    public void test_EnterpriseRepository() throws AMClientException
    {
        EnterpriseRepositoryDto erepo = client.getRepository(idEnterprise);

        Assert.assertFalse(StringUtils.isEmpty(erepo.getLocation()));
        Assert.assertTrue((erepo.getCapacityMb() > 0));
        Assert.assertTrue((erepo.getRemainingMb() > 0));
        Assert.assertTrue((erepo.getEnterpriseUsedMb() >= 0));

        // TODO check size after a create

        Assert.assertTrue((erepo.getEnterpriseUsedMb() < erepo.getCapacityMb()));
        Assert.assertTrue((erepo.getCapacityMb() >= erepo.getEnterpriseUsedMb()
            + erepo.getRemainingMb()));
    }

    public void test_Create() throws Exception
    {
        asserts.installOvfAndWaitCompletion(ovfId);
        asserts.ovfInstanceExist(ovfId);

        asserts.clean(ovfId);

        expectedEvents(DOWNLOADING, DOWNLOAD, NOT_DOWNLOAD);
    }

    public void test_DeleteNotfound() throws Exception
    {
        client.deleteTemplate(idEnterprise, ovfId);

        assertEventsEmpty();
    }

    // FIXME this test can generate the DOWNLOAD event prior to require cancel
    // public void test_CreateCancel() throws Exception
    // {
    // client.createOVFPackageInstance(idEnterprise, ovfId);
    // client.deleteTemplate(idEnterprise, ovfId);
    //
    // expectedEvents(DOWNLOADING, NOT_DOWNLOAD);
    // }

    public void test_CreateDouble() throws Exception
    {
        asserts.ovfInstanceNoExist(ovfId);
        asserts.installOvfAndWaitCompletion(ovfId);
        asserts.ovfInstanceExist(ovfId);

        try
        {
            client.installTemplateDefinition(idEnterprise, ovfId);
            fail("Duplicated ovf instance");
        }
        catch (Exception e)
        {

        }
        asserts.ovfInstanceExist(ovfId);
        asserts.clean(ovfId);

        // ERROR event not generated as request rejected (still download)
        expectedEvents(DOWNLOADING, DOWNLOAD, NOT_DOWNLOAD);
    }

    @Test(enabled = false)
    public void test_ErrorCreateInvalidOvfUrl() throws AMClientException
    {
        client.installTemplateDefinition(idEnterprise, ovf_invalidUrl);
        asserts.ovfStatus(ovf_invalidUrl, TemplateStatusEnumType.ERROR);

        // client.deleteTemplate(idEnterprise, ovf_invalidUrl);

        assertEventsEmpty();
    }

    @Test(enabled = false)
    public void test_ErrorCreateOvfNotFound() throws Exception
    {
        client.installTemplateDefinition(idEnterprise, ovf_notFound);

        asserts.waitUnitlExpected(ovf_notFound, ERROR);

        client.deleteTemplate(idEnterprise, ovf_notFound);

        // no DOWNLOADING, nothing to put into the repository
        expectedEvents(ERROR, NOT_DOWNLOAD);
    }

    // TODO ovf_fileNotAllowed
    @Test(enabled = false)
    public void test_ErrorCreateFileNotFound() throws Exception
    {
        client.installTemplateDefinition(idEnterprise, ovf_fileNotFound);

        asserts.waitUnitlExpected(ovf_fileNotFound, ERROR);

        client.deleteTemplate(idEnterprise, ovf_fileNotFound);

        expectedEvents(DOWNLOADING, ERROR, NOT_DOWNLOAD);
    }

    @Test(enabled = false)
    public void test_ErrorCreateInvalidDiskFormat() throws Exception
    {
        client.installTemplateDefinition(idEnterprise, ovf_invalidDiskFormat);

        asserts.waitUnitlExpected(ovf_invalidDiskFormat, ERROR);

        client.deleteTemplate(idEnterprise, ovf_invalidDiskFormat);

        expectedEvents(DOWNLOADING, ERROR, NOT_DOWNLOAD);
    }

    @Test(enabled = false)
    public void test_ErrorCreateMalformed() throws Exception
    {
        client.installTemplateDefinition(idEnterprise, ovf_malformed);

        asserts.waitUnitlExpected(ovf_malformed, ERROR);

        client.deleteTemplate(idEnterprise, ovf_malformed);

        // no DOWNLOADING, nothing to put into the repository
        expectedEvents(ERROR, NOT_DOWNLOAD);
    }

    @Test(enabled = false)
    public void test_ErrorCreateMultidisk() throws Exception
    {
        client.installTemplateDefinition(idEnterprise, ovf_multiDisk);

        asserts.waitUnitlExpected(ovf_multiDisk, ERROR);

        client.deleteTemplate(idEnterprise, ovf_multiDisk);

        // no DOWNLOADING, nothing to put into the repository
        expectedEvents(ERROR, NOT_DOWNLOAD);
    }

    @Test(enabled = false)
    public void test_CreateBundle() throws Exception
    {
        final String snapshot = "SnapshotUUID";

        asserts.installOvfAndWaitCompletion(ovfId);
        asserts.ovfInstanceExist(ovfId);

        TemplateDto ovfDto = createTestDiskInfoBundle(ovfId, snapshot);

        final String ovfBundle = ovfDto.getUrl();
        asserts.ovfInstanceNoExist(ovfBundle);

        // client.preBundleOVFPackage(idEnterprise, snapshot);

        createBundleDiskFile(ovfId, snapshot);
        client.bundleTemplate(idEnterprise, snapshot, ovfDto);

        asserts.ovfInstanceExist(ovfBundle);

        try
        {
            asserts.clean(ovfId);

            fail("cant delete if some bundle");
        }
        catch (Exception e)
        {
        }

        asserts.clean(ovfBundle);
        asserts.clean(ovfId);

        /**
         * TODO bundles SHOULD generate the DOWNLOAD event, not implemented as server doesn't expect
         * this.
         */
        expectedEvents(DOWNLOADING, DOWNLOAD, NOT_DOWNLOAD, NOT_DOWNLOAD);
    }

}
