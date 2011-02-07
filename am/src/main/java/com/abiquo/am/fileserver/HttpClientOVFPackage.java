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

package com.abiquo.am.fileserver;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.abiquo.am.fileserver.info.FileInfo;
import com.abiquo.am.fileserver.info.PackageInfo;
import com.abiquo.appliancemanager.config.AMConfiguration;
import com.abiquo.appliancemanager.config.AMConfigurationManager;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClient.BoundRequestBuilder;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.ProxyServer;

@Component(value = "httpClientOVFPackage")
public class HttpClientOVFPackage
{
    private final static Logger LOGGER = LoggerFactory.getLogger(HttpClientOVFPackage.class);

    private static AsyncHttpClientConfig clientConf;

    private AsyncHttpClient httpClient;

    private final static int CONNECTION_TIMEOUT = 60 * 1000; // a minute

    private final static int IDLE_TIMEOUT = 10 * 60 * 1000; // ten minutes

    private final static int REQUEST_TIMEOUT = 24 * 60 * 60 * 1000; // a day

    // TODO private final static int CONNECTIONS = 10;

    static
    {
        AMConfiguration amconf = AMConfigurationManager.getInstance().getAMConfiguration();

        AsyncHttpClientConfig.Builder builder =
            new AsyncHttpClientConfig.Builder().setFollowRedirects(true)
                .setCompressionEnabled(true).setIdleConnectionTimeoutInMs(IDLE_TIMEOUT)
                .setConnectionTimeoutInMs(CONNECTION_TIMEOUT)
                .setRequestTimeoutInMs(REQUEST_TIMEOUT);
        // .setMaximumConnectionsTotal(CONNECTIONS)

        if (amconf.getProxyHost() != null && amconf.getProxyPort() != null)
        {
            LOGGER.info("Configure HTTP connections to use the proxy [{}] [{}]", amconf.getProxyHost(),
                amconf.getProxyPort());

            ProxyServer proxy = new ProxyServer(amconf.getProxyHost(), amconf.getProxyPort());
            builder = builder.setProxyServer(proxy);
        }

        clientConf = builder.build();
    }

    public HttpClientOVFPackage()
    {
        httpClient = new AsyncHttpClient(clientConf);
    }

    public void addDownload(PackageInfo pack)
    {
        try
        {
            for (FileInfo file : pack.files)
            {
                if (!file.isAlreadyBeingDownload)
                {
                    addDownload(file);
                }
            }
        }
        catch (IOException e)
        {
            pack.onError(e.toString());
        }
    }

    protected void addDownload(FileInfo file) throws IOException
    {
        BoundRequestBuilder request = httpClient.prepareGet(file.fileUrl);
        // request.addBodyPart(arg0);
        // request.addHeader(arg0, arg1);

        file.execution = request.execute(file);
        // try
        // {
        //
        // // Response r= request.execute().get();
        //
        // //file.execution.get();
        // }
        // catch (InterruptedException e)
        // {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        // catch (ExecutionException e)
        // {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
    }

}
