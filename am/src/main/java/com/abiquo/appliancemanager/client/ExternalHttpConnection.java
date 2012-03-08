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

package com.abiquo.appliancemanager.client;

import static com.abiquo.appliancemanager.config.AMConfiguration.HTTP_CONNECTION_TIMEOUT;
import static com.abiquo.appliancemanager.config.AMConfiguration.HTTP_IDLE_TIMEOUT;
import static com.abiquo.appliancemanager.config.AMConfiguration.HTTP_MAX_CONNECTIONS;
import static com.abiquo.appliancemanager.config.AMConfiguration.HTTP_PROXY_HOST;
import static com.abiquo.appliancemanager.config.AMConfiguration.HTTP_PROXY_PASS;
import static com.abiquo.appliancemanager.config.AMConfiguration.HTTP_PROXY_PORT;
import static com.abiquo.appliancemanager.config.AMConfiguration.HTTP_PROXY_USER;
import static com.abiquo.appliancemanager.config.AMConfiguration.HTTP_REQUEST_TIMEOUT;
import static com.abiquo.appliancemanager.config.AMConfiguration.isProxy;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.ProxyServer;

/**
 * Configure external abiquo HTTP communications (proxy and requests timeouts)
 */
public class ExternalHttpConnection
{
    private final static Logger LOG = LoggerFactory.getLogger(ExternalHttpConnection.class);

    public static AsyncHttpClientConfig createHttpClientConf()
    {

        AsyncHttpClientConfig.Builder builder = new AsyncHttpClientConfig.Builder(). //
            setFollowRedirects(true).//
            setCompressionEnabled(true).//
            setIdleConnectionInPoolTimeoutInMs(HTTP_IDLE_TIMEOUT).//
            setIdleConnectionTimeoutInMs(HTTP_IDLE_TIMEOUT). //
            setConnectionTimeoutInMs(HTTP_CONNECTION_TIMEOUT).//
            setRequestTimeoutInMs(HTTP_REQUEST_TIMEOUT).//
            setMaximumConnectionsTotal(HTTP_MAX_CONNECTIONS).//
            setFollowRedirects(true).setMaximumNumberOfRedirects(6);

        if (isProxy())
        {
            LOG.debug("Configure HTTP connections to use the proxy [{}:{}]", //
                HTTP_PROXY_HOST, HTTP_PROXY_PORT);

            ProxyServer proxy;
            if (HTTP_PROXY_USER != null)
            {
                proxy =
                    new ProxyServer(HTTP_PROXY_HOST,
                        HTTP_PROXY_PORT,
                        HTTP_PROXY_USER,
                        HTTP_PROXY_PASS);
            }
            else
            {
                proxy = new ProxyServer(HTTP_PROXY_HOST, HTTP_PROXY_PORT);
            }

            // not use default java system properties (httpProxy)
            builder = builder.setUseProxyProperties(false).setProxyServer(proxy);
        }

        return builder.build();
    }

    private final AsyncHttpClient httpClient = new AsyncHttpClient(createHttpClientConf());

    public InputStream openConnection(final String url) throws IOException
    {
        try
        {
            // block
            return httpClient.prepareGet(url).execute().get().getResponseBodyAsStream();
        }
        catch (Exception e)
        {
            throw new IOException("Can't open InputStream to " + url, e);
        }
    }

    public void releaseConnection()
    {
        httpClient.close();
    }

    // it also release the connection
    public Long headFile(final String url) throws IOException
    {
        try
        {
            return Long.parseLong(httpClient.prepareHead(url).execute().get()
                .getHeader("Content-Length"));
        }
        catch (Exception e)
        {
            throw new IOException("Can't open InputStream to " + url, e);
        }
        finally
        {
            httpClient.close();
        }
    }

}
