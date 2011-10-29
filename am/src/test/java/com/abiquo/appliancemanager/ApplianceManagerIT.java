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

import static com.abiquo.testng.OVFRemoteRepositoryListener.ovfId;
import static com.abiquo.testng.OVFRemoteRepositoryListener.ovfIdInvalid;
import static com.abiquo.testng.TestConfig.AM_INTEGRATION_TESTS;
import static com.abiquo.testng.TestServerListener.BASE_URI;

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
import com.ning.http.client.ProxyServer;
import com.ning.http.client.StringPart;

import static org.testng.Assert.*;

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
    }
    
    @AfterMethod
    public void assertCleanPost()
    {
        asserts.ovfInstanceNoExist(ovfId);
    }

    @Test
    public void testDeploy() throws Exception
    {
        asserts.installOvfAndWaitCompletion(ovfId);
        asserts.ovfInstanceExist(ovfId);

        asserts.clean(ovfId);
    }

    @Test
    public void testDeployCancel() throws Exception
    {
        client.createOVFPackageInstance(idEnterprise, ovfId);
        client.delete(idEnterprise, ovfId);
    }

    @Test
    public void testDoubleDeploy() throws Exception
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
    }

    @Test(enabled = false)
    public void testDeployInvalid()
    {
        asserts.ovfStatus(ovfIdInvalid, OVFStatusEnumType.NOT_DOWNLOAD);

        // The OVF is not on the available list
        final Integer prevSize = asserts.ovfAvailable(ovfIdInvalid, false);

        // Install the package
        try
        {
            // OVFPackageInstanceStatusDto statusInstall =
            client.createOVFPackageInstance(idEnterprise, ovfIdInvalid);

            // TODO doesn't fail Assert.assertNotNull(null);
        }
        catch (Exception e)
        {
            Assert.assertNotNull(e);
        }

        // The OVF is not contained on the available list
        final Integer actualSize = asserts.ovfAvailable(ovfIdInvalid, false);

        // There are the same availables packages
        Assert.assertEquals(prevSize.intValue(), actualSize.intValue());

        // The OVF is ERROR
        asserts.ovfStatus(ovfIdInvalid, OVFStatusEnumType.ERROR);
    }

    @Test(enabled = false)
    public void testUploadStreaming() throws Exception
    {
        client = new ApplianceManagerResourceStubImpl(BASE_URI);
        asserts = new ApplianceManagerAsserts(client);

        OVFPackageInstanceDto diskInfo = asserts.createTestDiskInfoUpload();
        File upFile = asserts.createUploadTempFile();

        AsyncHttpClientConfig clientConf;
        AsyncHttpClient httpClient;
        int TIMEOUT = 10000;
        int CONNECTIONS = 10;

        // TODO read proxy info
        clientConf =
            new AsyncHttpClientConfig.Builder().setFollowRedirects(true)
                .setCompressionEnabled(true).setIdleConnectionTimeoutInMs(TIMEOUT)
                .setRequestTimeoutInMs(TIMEOUT).setMaximumConnectionsTotal(CONNECTIONS)
                .setProxyServer(new ProxyServer("127.0.0.1", 38080)).build();

        httpClient = new AsyncHttpClient(clientConf);

        BoundRequestBuilder request =
            httpClient.preparePost(BASE_URI + "er/" + idEnterprise + "/ovfs/");

        request.addBodyPart(new StringPart("diskInfo", diskInfo.toString())); // TODO JSON
        request.addBodyPart(new FilePart("disk.vmkd", upFile, "octet-stream", "UTF-8"));

        Assert.assertTrue(request.execute().get().getStatusCode() == 201);
    }

    @Test(enabled = false)
    public void testBundle() throws Exception
    {
        testDeploy();

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

    /**
     * using http://aruld.info/handling-multiparts-in-restful-applications-using-cxf
     * http://svn.apache .org/repos/asf/cxf/trunk/systests/jaxrs/src/test/java/org
     * /apache/cxf/systest/ jaxrs/MultipartStore.java <br/>
     **/
    // @Test(enabled = false)
    // public void testUploadStreaming() throws Exception
    // {
    // OVFPackageInstanceDto diskInfo = testUtils.createTestDiskInfoUpload();
    // File upFile = testUtils.createUploadTempFile();
    //
    // HttpClient httpclient = new DefaultHttpClient();
    // HttpPost httppost = new HttpPost(BASE_URI + "er/" + idEnterprise + "/ovf/upload");
    //
    // ContentBody bin = new FileBody(upFile);
    //
    // // XXX InputStreamBody bin =
    // // new InputStreamBody(new FileInputStream(upFile), "multipart/mixed",
    // // "testDiskUpload.file");
    // /*
    // * XXX
    // * @see
    // * http://radomirml.com/2009/02/13/file-upload-with-httpcomponents-successor-of-commons-
    // * httpclient to create an extension of InputStreamBody in order to determine the
    // * content-length of the attachment. *
    // */
    //
    // final String diskText = RSSerializerJACK.writeJSON(diskInfo);
    //
    // StringBody disk = new StringBody(diskText, "application/json", null);
    //
    // MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE); // XXX
    // reqEntity.addPart("diskInfo", disk);
    // reqEntity.addPart("diskFile", bin);
    //
    // httppost.setEntity(reqEntity);
    // httppost.setHeader("content-disposition", "2testDiskUpload.file");
    // // XXX --can not set-- httppost.addHeader("Content-Length",
    // // String.valueOf(upFile.length()));
    //
    // HttpResponse response = httpclient.execute(httppost);
    //
    // final Integer httpRespCode = response.getStatusLine().getStatusCode();
    // Assert.assertEquals(1, httpRespCode / 200);
    //
    // HttpEntity resEntity = response.getEntity();
    //
    // Assert.assertNotNull(resEntity);
    // }
}
