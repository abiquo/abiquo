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
import static com.abiquo.appliancemanager.transport.OVFStatusEnumType.NOT_DOWNLOAD;
import static com.abiquo.testng.TestConfig.AM_INTEGRATION_TESTS;
import static com.abiquo.testng.TestServerListener.BASE_URI;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.ws.rs.core.MediaType;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.appliancemanager.client.ApplianceManagerResourceStubImpl;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceDto;
import com.abiquo.testng.TestServerListener;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.FilePart;
import com.ning.http.client.ListenableFuture;
import com.ning.http.client.RequestBuilder;
import com.ning.http.client.StringPart;

@Test(groups = {AM_INTEGRATION_TESTS})
public class ApplianceManagerUploadIT
{
    final static String ovfId = "http://127.0.0.1/upload/testUpload/envelope.ovf";

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

    public void test_CreateUpload() throws Exception
    {

        OVFPackageInstanceDto info = ApplianceManagerAsserts.createTestDiskInfoUpload(ovfId);
        File file = ApplianceManagerAsserts.createUploadTempFile();

        uploadOVFPackageInstance("1", info, file, true);

        asserts.ovfInstanceExist(ovfId);

        client.delete(idEnterprise, ovfId);

        expectedEvents(DOWNLOAD, NOT_DOWNLOAD);

    }

    private void uploadOVFPackageInstance(final String idEnterprise,
        final OVFPackageInstanceDto diskInfo, final File diskFile, final boolean blocking)
        throws IOException, InterruptedException, ExecutionException
    {

        final String ovfsposturl =
            String.format("%s/erepos/%s/ovfs", TestServerListener.BASE_URI, idEnterprise);

        AsyncHttpClient httpClient = uploadClient();
        ListenableFuture<com.ning.http.client.Response> resFuture =
            httpClient.executeRequest(uploadParts(ovfsposturl, diskInfo, diskFile));

        if (blocking)
        {
            com.ning.http.client.Response res = resFuture.get();

            if (res.getStatusCode() / 200 != 1)
            {
                throw new RuntimeException("Can't upload " + res.getStatusText());
            }
        }
        // TODO add callbacks
    }

    private com.ning.http.client.Request uploadParts(final String ovfsPostUrl,
        final OVFPackageInstanceDto diskInfo, final File diskFile)
    {

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        JacksonJaxbJsonProvider jaxbjson = new JacksonJaxbJsonProvider();
        try
        {
            jaxbjson.writeTo(diskInfo, OVFPackageInstanceDto.class, null, null,
                MediaType.APPLICATION_JSON_TYPE, null, output);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        RequestBuilder builder = new RequestBuilder("POST");
        builder.setUrl(ovfsPostUrl);

        final String jsonString =
            new String("{ovfInstance: " + new String(output.toByteArray()) + " }"); // FIXME
        builder.addBodyPart(new StringPart("diskInfo", jsonString));
        builder.addBodyPart(new FilePart("diskFile", diskFile, "application/octet-stream", null));
        // TODO doesn't work as serialization in amqp difers for rest providers (missing ovfinstance
        // root element) final String jsonString = new String(JSONUtils.serialize(trik));
        // builder.addBodyPart(new FilePart("diskFile", diskFile, "octet-stream", "UTF-8"));
        // "application/x-gzip", null));
        // builder.addBodyPart(new ByteArrayPart("diskInfo", "diskInfo",
        // JSONUtils.serialize(diskInfo), MediaType.APPLICATION_JSON, null));

        return builder.build();
    }

    private AsyncHttpClient uploadClient()
    {
        AsyncHttpClientConfig.Builder builder =
            new AsyncHttpClientConfig.Builder().setFollowRedirects(true) //
                .setCompressionEnabled(true) //
                .setConnectionTimeoutInMs(20 * 1000)//
                .setRequestTimeoutInMs(20 * 1000);

        return new AsyncHttpClient(builder.build());
    }

}
