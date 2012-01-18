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

package com.abiquo.appliancemanager.config;

/**
 * Main configuration for the Appliance Manager artifact.
 * 
 * @author apuig
 */
public class AMConfiguration
{
    public final static Integer REPOSITORY_FILE_MARK_CHECK_TIMEOUT_SECONDS = Integer.valueOf(System
        .getProperty("abiquo.repository.timeoutSeconds", "10"));

    public final static int HTTP_CONNECTION_TIMEOUT = 60 * 1000; // a minute

    public final static int HTTP_IDLE_TIMEOUT = 10 * 60 * 1000; // ten minutes

    public final static int HTTP_REQUEST_TIMEOUT = 24 * 60 * 60 * 1000; // a day

    public final static int HTTP_MAX_CONNECTIONS = Integer.valueOf(System.getProperty(
        "abiquo.appliancemanager.downloads", "10"));

    /** milliseconds */
    public final static int DOWNLOADING_PUBLISH_INTERVAL = Integer.valueOf(System.getProperty(
        "abiquo.appliancemanager.downloadingPublishInterval", "1500"));

    /**
     * Where the ''repositoryLocation'' file system is mounted. Base path on the machine local file
     * system where the OVF packages files are stored.
     */
    private String repositoryPath;

    /**
     * Where the ''repositoryPath'' is exported. Usually a NFS location such
     * 'nsf-devel:/opt/vm_repository' .
     */
    private String repositoryLocation;

    /** proxy host server. if any. */
    private String proxyHost;

    /** proxy port server. if any. */
    private Integer proxyPort;

    /** Milliseconds to wait before refresh the download progress. */
    private Integer updateProgressInterval;

    /** Bytes to be downloaded from the RepositorySpace before flushing to the . */
    private Integer deployBuffer;

    /** Timeout for remote connection (during deploy). */
    private Integer timeout;

    /**
     * Timeout of of available ovf packages refresh on the filesystem (when timeout use cached
     * result)
     */
    private Integer fstimeoutMs;

    public AMConfiguration(final String repositoryPath, final String repositoryLocation)
    {
        assert isValidRepositoryLocation(repositoryLocation);
        assert isValidRepositoryPath(repositoryPath);

        this.repositoryPath = repositoryPath;
        this.repositoryLocation = repositoryLocation;
    }

    public static boolean isValidRepositoryPath(final String repositoryPath)
    {
        return !(repositoryPath == null || repositoryPath.isEmpty() || !repositoryPath
            .endsWith("/"));
        // TODO should the ''exist and can write'' check be there ?
    }

    public static boolean isValidRepositoryLocation(final String repositoryLocation)
    {
        return !(repositoryLocation == null || repositoryLocation.isEmpty() || !repositoryLocation
            .contains(":"));
    }

    public Integer getFsTimeoutMs()
    {
        return fstimeoutMs;
    }

    public void setFsTimeoutMs(final Integer fstimeoutMs)
    {
        this.fstimeoutMs = fstimeoutMs;
    }

    public String getRepositoryPath()
    {
        return repositoryPath;
    }

    public String getRepositoryLocation()
    {
        return repositoryLocation;
    }

    public Integer getUpdateProgressInterval()
    {
        return updateProgressInterval;
    }

    public Integer getTimeout()
    {
        return timeout;
    }

    public Integer getDeployBuffer()
    {
        return deployBuffer;
    }

    protected void setRepositoryPath(final String repositoryPath)
    {
        assert isValidRepositoryPath(repositoryPath);

        this.repositoryPath = repositoryPath;
    }

    protected void setRepositoryLocation(final String repositoryLocation)
    {
        assert isValidRepositoryLocation(repositoryLocation);

        this.repositoryLocation = repositoryLocation;
    }

    protected void setUpdateProgressInterval(final Integer updateProgressInterval)
    {
        assert updateProgressInterval > 0; // XXX

        this.updateProgressInterval = updateProgressInterval;
    }

    protected void setDeployBuffer(final Integer deployBuffer)
    {
        assert deployBuffer > 32; // XXX

        this.deployBuffer = deployBuffer;
    }

    protected void setTimeout(final Integer timeout)
    {
        assert timeout >= 0; // XXX

        this.timeout = timeout;
    }

    public void setProxyHost(final String proxyHost)
    {
        if (proxyHost != null && proxyHost.isEmpty())
        {
            this.proxyHost = null;
        }
        else
        {
            this.proxyHost = proxyHost;
        }
    }

    public String getProxyHost()
    {
        return proxyHost;
    }

    public void setProxyPort(final Integer proxyPort)
    {

        if (proxyPort != null && proxyPort == 0)
        {
            this.proxyPort = null;
        }
        else
        {
            this.proxyPort = proxyPort;
        }
    }

    public Integer getProxyPort()
    {
        return proxyPort;
    }

    public boolean isProxy()
    {
        return proxyHost != null && proxyPort != null;
    }

    /**
     * Only serializa elements related to the repository (path and location)
     */
    @Override
    public String toString()
    {
        return String.format("RepositoryConfiguation : [path:%s, loation:%s]", repositoryPath,
            repositoryLocation);
    }

}
