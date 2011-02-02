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

package com.abiquo.abiserver.pojo.service;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.lang.StringUtils;
import org.apache.wink.common.internal.utils.UriHelper;

import com.abiquo.abiserver.business.hibernate.pojohb.service.RemoteServiceHB;
import com.abiquo.abiserver.pojo.IPojo;
import com.abiquo.server.core.infrastructure.RemoteServiceDto;

/**
 * The Class RemoteService represents a remotely manageable service
 */
public class RemoteService implements IPojo<RemoteServiceHB>
{

    /** The id remote service. */
    private int idRemoteService;

    /** The remote service Type. */
    private RemoteServiceType remoteServiceType;

    /** The id data center. */
    private int idDataCenter;

    /** The uri. */
    private String uri;

    /** Status: service availability */
    private Integer status;

    /** URI's protocol */
    private String protocol;

    /** URI's domain name or IP */
    private String domainName;

    /** URI's port number */
    private Integer port;

    /** URI's service mapping */
    private String serviceMapping;

    public Integer getStatus()
    {
        return status;
    }

    public void setStatus(final Integer status)
    {
        this.status = status;
    }

    public String getProtocol()
    {
        return protocol;
    }

    public void setProtocol(final String protocol)
    {
        this.protocol = protocol;
    }

    public String getDomainName()
    {
        return domainName;
    }

    public void setDomainName(final String domainName)
    {
        this.domainName = domainName;
    }

    public Integer getPort()
    {
        return port;
    }

    public void setPort(final Integer port)
    {
        this.port = port;
    }

    public String getServiceMapping()
    {
        return serviceMapping;
    }

    public void setServiceMapping(final String serviceMapping)
    {
        this.serviceMapping = serviceMapping;
    }

    /**
     * Instantiates a new remote service.
     */
    public RemoteService()
    {
        this.idRemoteService = 0;
        this.idDataCenter = 0;
        this.remoteServiceType = new RemoteServiceType();
        this.uri = "";
        this.protocol = "http://";
        this.domainName = "";
        this.port = 0;
        this.serviceMapping = "";
        this.status = 0;
    }

    /**
     * Instantiates a new remote service.
     * 
     * @param remoteService the remote service
     */
    public RemoteService(final RemoteService remoteService)
    {
        idRemoteService = remoteService.getIdRemoteService();
        idDataCenter = remoteService.getIdDataCenter();
        remoteServiceType = remoteService.getRemoteServiceType();
        uri = remoteService.getUri();
        protocol = remoteService.getProtocol();
        domainName = remoteService.getDomainName();
        port = remoteService.getPort();
        serviceMapping = remoteService.getServiceMapping();
        status = remoteService.getStatus();
    }

    /**
     * Gets the id data center.
     * 
     * @return the id data center
     */
    public int getIdDataCenter()
    {
        return idDataCenter;
    }

    /**
     * Sets the id data center.
     * 
     * @param idDataCenter the new id data center
     */
    public void setIdDataCenter(final int idDataCenter)
    {
        this.idDataCenter = idDataCenter;
    }

    /**
     * Gets the id remote service.
     * 
     * @return the id remote service
     */
    public int getIdRemoteService()
    {
        return idRemoteService;
    }

    /**
     * Sets the id remote service.
     * 
     * @param idRemoteService the new id remote service
     */
    public void setIdRemoteService(final int idRemoteService)
    {
        this.idRemoteService = idRemoteService;
    }

    /**
     * Gets the remote service type.
     * 
     * @return the remote service type
     */
    public RemoteServiceType getRemoteServiceType()
    {
        return remoteServiceType;
    }

    /**
     * Sets the remote service type.
     * 
     * @param remoteServiceType the new remote service type
     */
    public void setRemoteServiceType(final RemoteServiceType remoteServiceType)
    {
        this.remoteServiceType = remoteServiceType;
    }

    /**
     * Remote service URI must always end with a /
     */
    public String getUri()
    {
        if (StringUtils.isEmpty(uri) || modifiedUri())
        {
            uri = getFullUri(protocol, domainName, port, serviceMapping);
        }

        return uri;
    }

    /**
     * Sets the uri.
     * 
     * @param uri the new uri
     */
    public void setUri(String uri)
    {
        this.uri = uri;
        try
        {
            URI u = new URI(uri);

            this.protocol = fixProtocol(u.getScheme());
            this.domainName = u.getHost();
            this.port = u.getPort();
            this.serviceMapping = u.getPath();
            if (serviceMapping.startsWith("/"))
                serviceMapping = serviceMapping.replaceFirst("/", "");
        }
        catch (URISyntaxException e)
        {

        }
    }

    public static String getFullUri(String protocol, String domainName, Integer port,
        String serviceMapping)
    {
        String domainHost = domainName + (port != null ? ":" + port : "");

        String fullURL = StringUtils.join(new String[] {fixProtocol(protocol), domainHost});

        if (!StringUtils.isEmpty(serviceMapping))
        {
            fullURL = UriHelper.appendPathToBaseUri(fullURL, serviceMapping);
        }

        return fullURL;
    }

    /*
     * (non-Javadoc)
     * @see com.abiquo.abiserver.pojo.IPojo#toPojoHB()
     */
    @Override
    public RemoteServiceHB toPojoHB()
    {
        RemoteServiceHB remoteServiceHB = new RemoteServiceHB();
        remoteServiceHB.setIdDataCenter(this.getIdDataCenter());
        remoteServiceHB.setIdRemoteService(this.getIdRemoteService());
        remoteServiceHB.setStatus(this.getStatus());
        remoteServiceHB.setUri(this.getUri());
        remoteServiceHB.setRemoteServiceType(remoteServiceType.toEnum());
        return remoteServiceHB;
    }

    private static String fixProtocol(String protocol)
    {
        if (!protocol.endsWith("://"))
        {
            protocol += "://";
        }
        return protocol;
    }

    private boolean modifiedUri()
    {
        try
        {
            URI u = new URI(uri);

            return !fixProtocol(u.getScheme()).equals(fixProtocol(protocol))
                || !u.getHost().equals(domainName)
                || u.getPort() != port
                || (!StringUtils.isEmpty(u.getPath()) && !u.getPath().replaceFirst("/", "").equals(
                    serviceMapping));
        }
        catch (URISyntaxException e)
        {
            return true;
        }
    }

    public static RemoteService create(RemoteServiceDto dto, int datacenterId)
    {
        RemoteService remoteService = new RemoteService();
        remoteService.setIdRemoteService(dto.getId());
        remoteService.setIdDataCenter(datacenterId);
        remoteService.setUri(dto.getUri());

        remoteService.setStatus(dto.getStatus());

        remoteService
            .setRemoteServiceType(new com.abiquo.abiserver.pojo.service.RemoteServiceType(dto
                .getType()));

        return remoteService;
    }

}
