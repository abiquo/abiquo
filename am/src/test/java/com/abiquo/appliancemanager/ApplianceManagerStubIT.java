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

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.apache.commons.io.FileUtils;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.DefaultHandler;
import org.mortbay.jetty.handler.HandlerList;
import org.mortbay.jetty.handler.ResourceHandler;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.abiquo.appliancemanager.client.ApplianceManagerResourceStubImpl;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceDto;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceStatusType;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClient.BoundRequestBuilder;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.FilePart;
import com.ning.http.client.ProxyServer;
import com.ning.http.client.StringPart;

public class ApplianceManagerStubIT
{

    protected final static String snapshot = "000snap000";

    public static final int RS_FILE_SERVER_PORT = 8282;

    protected final static String ovfId = String.format(
        "http://localhost:%d/testovf/description.ovf", RS_FILE_SERVER_PORT);

    protected final static String ovfIdInvalid =
        "http://localhost:8080/testovf/description-INVALID.ovf";

    protected final static String idEnterprise = "1";

    protected final static String baseUrl = getLocation();

    protected ApplianceManagerResourceStubImpl stub;

    protected ApplianceManagerStubTestUtils testUtils;

    protected static String REPO_PATH;

    // @Test
    // public void testDownload()
    // {
    // stub = new ApplianceManagerResourceStubImpl("http://localhost:80/am");
    //
    // OVFPackageInstanceStatusListDto statuslist =
    // stub.getOVFPackagInstanceStatusList(idEnterprise);
    //
    // for(OVFPackageInstanceStatusDto status : statuslist.getOvfPackageInstancesStatus())
    // {
    // OVFPackageInstanceDto inst = stub.getOVFPackageInstance(idEnterprise, status.getOvfId());
    // EnvelopeType envelope = stub.getOVFPackageInstanceEnvelope(idEnterprise, status.getOvfId());
    // }
    // }
    //
    /**
     * Clear the test configured repository.path folder.
     * 
     * @throws IOException
     */
    @BeforeClass
    // XXX BeforeMethod
    public void initializeRepositoryFileSystem() throws IOException
    {

        REPO_PATH = "/tmp/testrepo/";
        File vmrepo = new File(REPO_PATH);
        if (vmrepo.exists())
        {
            FileUtils.deleteDirectory(vmrepo);
        }

        vmrepo.mkdirs();
        new File(REPO_PATH + ".abiquo_repository").createNewFile();

    }

    @Test
    public void testDeploy() throws Exception
    {
        stub = new ApplianceManagerResourceStubImpl(baseUrl);
        testUtils = new ApplianceManagerStubTestUtils(stub);

        // The OVF is NOT_DOWNLOAD
        testUtils.ovfStatus(ovfId, OVFPackageInstanceStatusType.NOT_DOWNLOAD);

        // The OVF is not on the available list
        final Integer prevSize = testUtils.ovfAvailable(ovfId, false);

        // Install the package
        testUtils.installOvfAndWaitCompletion(ovfId);

        OVFPackageInstanceDto pi = stub.getOVFPackageInstance(idEnterprise, ovfId);
        String diskPath = pi.getDiskFilePath();
        File diskFile = new File(REPO_PATH + diskPath);
        Assert.assertTrue(diskFile.exists());

        // The OVF is contained on the available list
        final Integer actualSize = testUtils.ovfAvailable(ovfId, true);

        // There are one new available ovf.
        Assert.assertEquals((prevSize.intValue() + 1), actualSize.intValue());

        // The OVF is DOWNLOAD
        testUtils.ovfStatus(ovfId, OVFPackageInstanceStatusType.DOWNLOAD);
    }

    @Test(enabled = false)
    public void testDeployCancel() throws Exception
    {
        stub = new ApplianceManagerResourceStubImpl(baseUrl);
        testUtils = new ApplianceManagerStubTestUtils(stub);

        // The OVF is NOT_DOWNLOAD
        testUtils.ovfStatus(ovfId, OVFPackageInstanceStatusType.NOT_DOWNLOAD);

        // The OVF is not on the available list
        final Integer prevSize = testUtils.ovfAvailable(ovfId, false);

        // Install the package
        testUtils.installOvf(ovfId);

        // wait to start download
        Thread.sleep(10);

        // Require the cancel
        stub.delete(idEnterprise, ovfId);

        Thread.sleep(10);

        // The OVF is contained on the available list
        final Integer actualSize = testUtils.ovfAvailable(ovfId, false);

        // There are one new available ovf.
        Assert.assertEquals(prevSize.intValue(), actualSize.intValue());

        // The OVF is NOT_DOWNLOAD
        testUtils.ovfStatus(ovfId, OVFPackageInstanceStatusType.NOT_DOWNLOAD);
    }

    @Test(enabled = false)
    public void testDoubleDeploy() throws Exception
    {

        testDeploy();

        // The OVF is DOWNLOAD
        testUtils.ovfStatus(ovfId, OVFPackageInstanceStatusType.DOWNLOAD);

        // The OVF is on the available list
        final Integer prevSize = testUtils.ovfAvailable(ovfId, true);

        // Install the package
        testUtils.installOvfAndWaitCompletion(ovfId);

        // The OVF is contained on the available list
        final Integer actualSize = testUtils.ovfAvailable(ovfId, true);

        // There are one new available ovf.
        Assert.assertEquals(prevSize.intValue(), actualSize.intValue());

        // The OVF is DOWNLOAD
        testUtils.ovfStatus(ovfId, OVFPackageInstanceStatusType.DOWNLOAD);
    }

    @Test(enabled = false)
    public void testDeployInvalid()
    {
        stub = new ApplianceManagerResourceStubImpl(baseUrl);
        testUtils = new ApplianceManagerStubTestUtils(stub);

        // The OVF is NOT_DOWNLOAD
        testUtils.ovfStatus(ovfIdInvalid, OVFPackageInstanceStatusType.NOT_DOWNLOAD);

        // The OVF is not on the available list
        final Integer prevSize = testUtils.ovfAvailable(ovfIdInvalid, false);

        // Install the package
        try
        {
            // OVFPackageInstanceStatusDto statusInstall =
            stub.createOVFPackageInstance(idEnterprise, ovfIdInvalid);

            // TODO doesn't fail Assert.assertNotNull(null);
        }
        catch (Exception e)
        {
            Assert.assertNotNull(e);
        }

        // The OVF is not contained on the available list
        final Integer actualSize = testUtils.ovfAvailable(ovfIdInvalid, false);

        // There are the same availables packages
        Assert.assertEquals(prevSize.intValue(), actualSize.intValue());

        // The OVF is ERROR
        testUtils.ovfStatus(ovfIdInvalid, OVFPackageInstanceStatusType.ERROR);
    }

    @Test(enabled = false)
    public void testUploadStreaming() throws Exception
    {
        stub = new ApplianceManagerResourceStubImpl(baseUrl);
        testUtils = new ApplianceManagerStubTestUtils(stub);

        OVFPackageInstanceDto diskInfo = testUtils.createTestDiskInfoUpload();
        File upFile = testUtils.createUploadTempFile();

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
            httpClient.preparePost(baseUrl + "er/" + idEnterprise + "/ovfs/");

        request.addBodyPart(new StringPart("diskInfo", diskInfo.toString())); // TODO JSON
        request.addBodyPart(new FilePart("disk.vmkd", upFile, "octet-stream", "UTF-8"));

        Assert.assertTrue(request.execute().get().getStatusCode() == 201);
    }

    @Test(enabled = false)
    public void testBundle() throws Exception
    {
        testDeploy();

        OVFPackageInstanceDto ovfDto = testUtils.createTestDiskInfoBundle(ovfId, snapshot);

        testUtils.createBundleDiskFile(ovfId, snapshot);

        stub.bundleOVFPackage(idEnterprise, snapshot, ovfDto);// .bundleOVFPackage(baseUrl,
        // idEnterprise, snapshot, ovfDto);
    }

    public void testDelete()
    {

    }

    public void testDeleteBundle()
    {

    }

    /** Static file server for Test OVF Remote Repository */
    private static Server rsServer;

    // protected static String amContextConfigLocation = "classpath:springresources/am-cxf.xml";

    private final static String fileServerPath = "src/test/resources/";

    private final static String diskFilePath = "src/test/resources/testovf/diskFile.vmdk";

    /** Should be the same of on the References size on the ''src/test/resources/description.ovf'' */
    private final static Long diskFileSize = 1024 * 1024 * 10l;

    
    private static Server server;

    @BeforeClass
    protected void setupAm()
    {
        server = new Server(9008);

        WebAppContext webapp = new WebAppContext();
        webapp.setContextPath("/am");
        webapp.setWar("src/main/webapp");
        webapp.setServer(server);
        server.setHandler(webapp);

        try
        {
            server.start();
        }
        catch (Exception ex)
        {
            throw new RuntimeException("Could not start test server", ex);
        }
    }

    @AfterClass
    protected void teardownAm() throws Exception
    {
        server.stop();
    }

    @BeforeClass
    protected void configureFileServerTestResources() throws Exception
    {
        rsServer = new Server();
        SelectChannelConnector connector = new SelectChannelConnector();
        connector.setPort(RS_FILE_SERVER_PORT);
        rsServer.addConnector(connector);

        ResourceHandler resource_handler = new ResourceHandler();
        // resource_handler.setDirectoriesListed(true);
        // resource_handler.setWelcomeFiles(new String[] {"index.html"});
        resource_handler.setResourceBase(fileServerPath);

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] {resource_handler, new DefaultHandler()});
        rsServer.setHandler(handlers);
        rsServer.start();
        // rsServer.join();

        

        stub = new ApplianceManagerResourceStubImpl(baseUrl);
        testUtils = new ApplianceManagerStubTestUtils(stub);

        createDiskFile();
    }

    @AfterClass
    public static void tearDownFileServer() throws Exception
    {
        if (rsServer != null)
        {
            rsServer.stop();
        }
        deleteDiskFile();
        cleanupRepository();
    }

    protected static void cleanupRepository() throws IOException
    {
        File vmrepo = new File(REPO_PATH);
        if (vmrepo.exists())
        {
            FileUtils.deleteDirectory(vmrepo);
        }
    }

    protected static void createDiskFile() throws IOException
    {
        File diskFile = new File(diskFilePath);
        RandomAccessFile f = new RandomAccessFile(diskFile, "rw");
        f.setLength(diskFileSize);
    }

    // @AfterClass
    protected static void deleteDiskFile() throws Exception
    {
        File diskFile = new File(diskFilePath);

        final String errorCause =
            String.format("Can not delete the disk file at [%s]", diskFilePath);

        if (diskFile.exists())
        {
            if (!diskFile.delete())
            {
                throw new Exception(errorCause);
            }
        }
        else
        {
            throw new Exception(errorCause);
        }
    }

    protected static String getLocation()
    {
        return "http://localhost:9008/am"; 
        // TODO jetty port configured on pom.xml
        // return String.format("http://localhost:%s/am", AM_SERVICE_MAPPING_PORT);
    }

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
    // HttpPost httppost = new HttpPost(baseUrl + "er/" + idEnterprise + "/ovf/upload");
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
