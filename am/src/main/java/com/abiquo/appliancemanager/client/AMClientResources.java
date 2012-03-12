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

import static java.lang.String.valueOf;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.Status.Family;

import org.apache.wink.client.ClientConfig;
import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.appliancemanager.util.URIResolver;
import com.abiquo.model.transport.error.ErrorDto;

public class AMClientResources
{
    protected final static Logger LOGGER = LoggerFactory.getLogger(AMClient.class);

    // public static final String GET_IDS_ACTION = "action/getstates";

    protected String serviceUri;

    private RestClient client;

    private final static Integer CLIENT_TIMEOUT_MS = // 5 seconds
        Integer.parseInt(System.getProperty("abiquo.appliancemanager.timeout", "5000"));

    protected final static ClientConfig confTimeout;
    static
    {
        // WARNING: this property is intended to be setup in the ''remote services'', but we will
        // add a check in case of monolitic installs.
        final Integer REPOSITORY_FILE_MARK_CHECK_TIMEOUT_MS = //
            Integer.valueOf(System.getProperty("abiquo.repository.timeoutSeconds", "5")) * 1000;

        // Use the higher timeout
        final Integer EFFECTIVE_CLIENT_TIMEOUT =
            REPOSITORY_FILE_MARK_CHECK_TIMEOUT_MS > CLIENT_TIMEOUT_MS
                ? REPOSITORY_FILE_MARK_CHECK_TIMEOUT_MS : CLIENT_TIMEOUT_MS;

        confTimeout = new ClientConfig();
        confTimeout.readTimeout(EFFECTIVE_CLIENT_TIMEOUT);

        LOGGER.info("ApplianceManager client request timeout "
            + "for repository filesystem requests set to {}ms", EFFECTIVE_CLIENT_TIMEOUT);
    }

    /**
     * @param configTimeout, only for am request that will require some repository filesystem action
     */
    public void initializeClient(final String serviceUri, final boolean configTimeout)
    {
        this.serviceUri = serviceUri;
        if (configTimeout)
        {
            this.client = new RestClient(confTimeout);
        }
        else
        {
            this.client = new RestClient();
        }
    }

    public static String resolveTemplateUrl(final String serviceUri, final Integer idEnterprise,
        String ovfid)
    {
        if (ovfid.startsWith("http://"))
        {
            ovfid = ovfid.substring("http://".length());
        }

        Map<String, String> params;
        params = new HashMap<String, String>();
        params.put("erepo", valueOf(idEnterprise));
        // XXX calling from server encode the ''ovfid''
        // params.put(TEMPLATE_PATH, ovfid);

        return
        // XXX calling from server encode the ''ovfid''
        // URIResolver.resolveURI(serviceUri, "erepos/{erepo}/templates/{template}", params);
        URIResolver.resolveURI(serviceUri, "erepos/{erepo}/templates", params) // + '/'
            + decodedUrl(ovfid);

    }

    Resource template(final Integer idEnterprise, final String ovfid)
    {
        String url = resolveTemplateUrl(serviceUri, idEnterprise, ovfid);

        Resource resource = client.resource(url);

        return resource;
    }

    Resource templates(final Integer idEnterprise)
    {
        final String url =
            URIResolver.resolveURI(serviceUri, "erepos/{erepo}/templates",
                Collections.singletonMap("erepo", valueOf(idEnterprise)));

        return client.resource(url);
    }

    // Resource templates_GetIds(final Integer idEnterprise)
    // {
    // final String url =
    // URIResolver.resolveURI(serviceUri, "erepos/{erepo}/templates/",
    // Collections.singletonMap("erepo", valueOf(idEnterprise)));
    //
    // return client.resource(UriHelper.appendPathToBaseUri(url, GET_IDS_ACTION));
    // }

    Resource repository(final Integer idEnterprise)
    {
        final String url =
            URIResolver.resolveURI(serviceUri, "erepos/{erepo}",
                Collections.singletonMap("erepo", valueOf(idEnterprise)));

        return client.resource(url);
    }

    Resource repositories()
    {
        final String url = String.format("%s/%s", serviceUri, "erepos");

        Resource resource = client.resource(url);

        return resource;
    }

    Resource check()
    {
        final String url = String.format("%s/%s", serviceUri, "check");

        Resource resource = client.resource(url);

        return resource;
    }

    /**
     * Check each part of the url is properly encoded (uploading a template name with blanks)
     */
    private static String decodedUrl(final String url)
    {
        try
        {
            String[] parts = url.split("/");
            StringBuffer sb = new StringBuffer();
            for (String part : parts)
            {
                sb.append("/").append(java.net.URLEncoder.encode(part, "UTF-8"));
            }
            return sb.toString();
        }
        catch (UnsupportedEncodingException e)
        {
            throw new RuntimeException(url, e);
        }
    }

    protected void checkResponseErrors(final ClientResponse response) throws AMClientException
    {
        if (response.getStatusType().getFamily() != Family.SUCCESSFUL)
        {
            Status status = Status.fromStatusCode(response.getStatusCode());

            ErrorDto error = null;
            try
            {
                error = response.getEntity(ErrorDto.class);
            }
            catch (Exception e)
            {
                error = new ErrorDto("AM-COMM-UNEXPECTED", response.getMessage());
            }

            if (error != null)
            {
                throw new AMClientException(status, error.getMessage());
            }

            throw new AMClientException(status, "Appliance Manager not properly configured");
        }
    }

}
