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
package com.abiquo.nodecollector.domain.collectors;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.lang.StringUtils;

import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.nodecollector.domain.Collector;
import com.abiquo.nodecollector.domain.HypervisorCollector;

/**
 * Base class for all {@link HypervisorCollector} that implements common functionality.
 * 
 * @author ibarrera
 */
public abstract class AbstractCollector implements HypervisorCollector
{
    /** IP address of the hypervisor. */
    private String ipAddress;
    
    /** AIM port **/
    private Integer aimPort;
    
    @Override
    public HypervisorType getHypervisorType()
    {
        return this.getClass().getAnnotation(Collector.class).type();
    }

    /**
     * @return the ipAddress
     */
    public String getIpAddress()
    {
        return ipAddress;
    }

    /**
     * @param ipAddress the ipAddress to set
     */
    public void setIpAddress(final String ipAddress)
    {
        this.ipAddress = ipAddress;
    }
    
    /**
     * @return the aimPort
     */
    public Integer getAimPort()
    {
        return aimPort;
    }

    /**
     * @param aimPort the aimPort to set
     */
    public void setAimPort(Integer aimPort)
    {
        this.aimPort = aimPort;
    }
    
    /**
     * Check if two repositories point to the same mount point. It is a helper method that all the
     * Hypervisors can use. It is more complicated than an equals function because one parameter can
     * be informed with the host name of the NFS and the other one with the IP address of the same
     * machine. It also checks if the actualRepository param is accessible.
     * 
     * @param definedRepository NFS string mount point we look for to determine if the host is
     *            Managed.
     * @param actualRepository found NFS repository mounted in the machine.
     * @return true if all the values are actually the same.
     */
    protected Boolean checkNFS(String definedRepository, String actualRepository)
    {

        if (StringUtils.isEmpty(definedRepository))
        {
            return false;
        }

        definedRepository = removeTrailingSlash(definedRepository);
        actualRepository = removeTrailingSlash(actualRepository);

        // The value 'host' of the repository NFS, defined by host:path can be actually
        // an IP address or a Host name
        // And the same for the mounted NFS, can be an IP address or a host name.
        // In order to compare if the defined NFS is the mounted NFS, we always will use
        // the IP address. So, maybe we have
        // to convert the host name to and IP address.
        // Get the IP address of the defined NFS.
        String definedHostOrIp = definedRepository.substring(0, definedRepository.indexOf(":"));
        String definedPath = definedRepository.substring(definedRepository.indexOf(":") + 1);
        String definedNFSIPAddress;

        // convert it to String InetAddress IP
        try
        {
            definedNFSIPAddress = InetAddress.getByName(definedHostOrIp).getHostAddress();
        }
        catch (UnknownHostException e)
        {
            return Boolean.FALSE;
        }

        String actualHostOrIp = actualRepository.substring(0, actualRepository.indexOf(":"));
        String actualPath = actualRepository.substring(actualRepository.indexOf(":") + 1);
        String actualNFSIPAddress;
        try
        {
            actualNFSIPAddress = InetAddress.getByName(actualHostOrIp).getHostAddress();
        }
        catch (UnknownHostException e)
        {
            return false;
        }

        if (definedNFSIPAddress.equalsIgnoreCase(actualNFSIPAddress)
            && definedPath.equalsIgnoreCase(actualPath))
        {
            return Boolean.TRUE;
        }
        else
        {
            return Boolean.FALSE;
        }
    }

    /**
     * Removes the trailing slash from the given String.
     * 
     * @param text The String to modify.
     * @return The String without the trailing slash.
     */
    protected static String removeTrailingSlash(final String text)
    {
        if (text.endsWith("/"))
        {
            return text.substring(0, text.length() - 1);
        }
        else
        {
            return text;
        }
    }

}
