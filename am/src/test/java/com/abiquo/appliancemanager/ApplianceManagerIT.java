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
import static com.abiquo.appliancemanager.transport.OVFStatusEnumType.DOWNLOAD;
import static com.abiquo.appliancemanager.transport.OVFStatusEnumType.DOWNLOADING;
import static com.abiquo.appliancemanager.transport.OVFStatusEnumType.ERROR;
import static com.abiquo.appliancemanager.transport.OVFStatusEnumType.NOT_DOWNLOAD;
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

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.appliancemanager.client.ApplianceManagerResourceStubImpl;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceDto;
import com.abiquo.appliancemanager.transport.OVFStatusEnumType;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClient.BoundRequestBuilder;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.FilePart;
import com.ning.http.client.StringPart;

@Test(groups = {AM_INTEGRATION_TESTS})
public class ApplianceManagerIT
{
    private final static Logger LOG = LoggerFactory.getLogger(ApplianceManagerIT.class);

    final static String snapshot = "000snap000";

    final static String idEnterprise = "1";

    protected ApplianceManagerResourceStubImpl client;

    protected ApplianceManagerAsserts asserts;

    @BeforeClass
    public void setUp() throws IOException
    {
        client = new ApplianceManagerResourceStubImpl(BASE_URI);
        asserts = new ApplianceManagerAsserts(client);
    }

    @BeforeMethod
    public void assertCleanPre()
    {
        asserts.ovfInstanceNoExist(ovfId);
        assertEventsEmpty();
    }

    @AfterMethod
    public void assertCleanPost()
    {
        asserts.ovfInstanceNoExist(ovfId);
        assertEventsEmpty();
    }

    // TODO config and repos !!

    @Test
    public void testCreate() throws Exception
    {
        asserts.installOvfAndWaitCompletion(ovfId);
        asserts.ovfInstanceExist(ovfId);

        asserts.clean(ovfId);

        expectedEvents(DOWNLOADING, DOWNLOAD, NOT_DOWNLOAD);
    }

    @Test
    public void testDeleteNotfound() throws Exception
    {
        client.delete(idEnterprise, ovfId);

        assertEventsEmpty();
    }

    @Test
    public void testCreateCancel() throws Exception
    {
        client.createOVFPackageInstance(idEnterprise, ovfId);
        client.delete(idEnterprise, ovfId);

        expectedEvents(DOWNLOADING, NOT_DOWNLOAD);
    }

    @Test
    public void testCreateDouble() throws Exception
    {
        asserts.ovfInstanceNoExist(ovfId);
        asserts.installOvfAndWaitCompletion(ovfId);
        asserts.ovfInstanceExist(ovfId);

        try
        {
            client.createOVFPackageInstance(idEnterprise, ovfId);
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

    @Test
    public void testErrorCreateInvalidOvfUrl()
    {
        client.createOVFPackageInstance(idEnterprise, ovf_invalidUrl);
        asserts.ovfStatus(ovf_invalidUrl, OVFStatusEnumType.ERROR);

        // client.delete(idEnterprise, ovf_invalidUrl);

        assertEventsEmpty();
    }

    @Test
    public void testErrorCreateOvfNotFound() throws Exception
    {
        client.createOVFPackageInstance(idEnterprise, ovf_notFound);

        asserts.waitUnitlExpected(ovf_notFound, ERROR);

        client.delete(idEnterprise, ovf_notFound);

        // no DOWNLOADING, nothing to put into the repository
        expectedEvents(ERROR, NOT_DOWNLOAD);
    }

    // TODO ovf_fileNotAllowed

    @Test
    public void testErrorCreateFileNotFound() throws Exception
    {
        client.createOVFPackageInstance(idEnterprise, ovf_fileNotFound);

        asserts.waitUnitlExpected(ovf_fileNotFound, ERROR);

        client.delete(idEnterprise, ovf_fileNotFound);

        expectedEvents(DOWNLOADING, ERROR, NOT_DOWNLOAD);
    }

    @Test
    public void testErrorCreateInvalidDiskFormat() throws Exception
    {
        client.createOVFPackageInstance(idEnterprise, ovf_invalidDiskFormat);

        asserts.waitUnitlExpected(ovf_invalidDiskFormat, ERROR);

        client.delete(idEnterprise, ovf_invalidDiskFormat);

        expectedEvents(DOWNLOADING, ERROR, NOT_DOWNLOAD);
    }

    @Test
    public void testErrorCreateMalformed() throws Exception
    {
        client.createOVFPackageInstance(idEnterprise, ovf_malformed);

        asserts.waitUnitlExpected(ovf_malformed, ERROR);

        client.delete(idEnterprise, ovf_malformed);

        // no DOWNLOADING, nothing to put into the repository
        expectedEvents(ERROR, NOT_DOWNLOAD);
    }

    @Test
    public void testErrorCreateMultidisk() throws Exception
    {
        client.createOVFPackageInstance(idEnterprise, ovf_multiDisk);

        asserts.waitUnitlExpected(ovf_multiDisk, ERROR);

        client.delete(idEnterprise, ovf_multiDisk);

        // no DOWNLOADING, nothing to put into the repository
        expectedEvents(ERROR, NOT_DOWNLOAD);
    }

    @Test(enabled = false)
    public void testBundle() throws Exception
    {
        testCreate();

        OVFPackageInstanceDto ovfDto = asserts.createTestDiskInfoBundle(ovfId, snapshot);

        asserts.createBundleDiskFile(ovfId, snapshot);

        client.bundleOVFPackage(idEnterprise, snapshot, ovfDto);// .bundleOVFPackage(BASE_URI,
        // idEnterprise, snapshot, ovfDto);
    }

    // TODO
    // public void testDelete()
    // {
    //
    // }
    // TODO
    // public void testDeleteBundle()
    // {
    //
    // }

}
