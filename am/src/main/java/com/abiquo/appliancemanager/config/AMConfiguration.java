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

import com.abiquo.am.exceptions.AMError;
import com.abiquo.appliancemanager.exceptions.AMException;
import com.abiquo.ovfmanager.ovf.xml.OVFSerializer;

/**
 * Main configuration for the Appliance Manager artifact.
 * 
 * @author apuig
 */
public class AMConfiguration
{

    static
    {
        OVFSerializer.getInstance().setFormatOutput(true);
        OVFSerializer.getInstance().setValidateXML(false);
    }

    /** System property determine to use Proxy connections using this host. */
    private final static String SYSTEM_PROXY_HOST_ATTRIBUTE = "http.proxyHost";

    /** System property determine to use Proxy connections using this port. */
    private final static String SYSTEM_PROXY_PORT_ATTRIBUTE = "http.proxyPort";

    public final static Integer REPOSITORY_FILE_MARK_CHECK_TIMEOUT_SECONDS = Integer.valueOf(System
        .getProperty("abiquo.repository.timeoutSeconds", "10"));

    public final static String UPDATE_INTERVAL_DEFAULT = "5000";

    public final static String DEPLOY_TIMEOUT_DEFAULT = "60000";

    public final static int HTTP_CONNECTION_TIMEOUT = 60 * 1000; // a minute

    public final static int HTTP_IDLE_TIMEOUT = 10 * 60 * 1000; // ten minutes

    public final static int HTTP_REQUEST_TIMEOUT = 24 * 60 * 60 * 1000; // a day

    public final static int HTTP_MAX_CONNECTIONS = Integer.valueOf(System.getProperty(
        "abiquo.appliancemanager.downloads", "10"));

    /** milliseconds */
    public final static int DOWNLOADING_PUBLISH_INTERVAL = Integer.valueOf(System.getProperty(
        "abiquo.appliancemanager.downloadingPublishInterval", "5000"));

    /**
     * Where the ''repositoryLocation'' file system is mounted. Base path on the machine local file
     * system where the OVF packages files are stored.
     */
    private static String repositoryPath = System.getProperty(
        "abiquo.appliancemanager.localRepositoryPath", "/tmp/testrepo"); // TODO do not use default
                                                                         // values

    /**
     * Where the ''repositoryPath'' is exported. Usually a NFS location such
     * 'nsf-devel:/opt/vm_repository' .
     */
    private static String repositoryLocation = System.getProperty(
        "abiquo.appliancemanager.repositoryLocation", "nfs-test:/test/path"); // TODO do not use
                                                                              // default values

    /** proxy host server. if any. */
    private static String proxyHost = System.getProperty(SYSTEM_PROXY_HOST_ATTRIBUTE, null);

    /** proxy port server. if any. */
    private static Integer proxyPort = Integer.valueOf(System.getProperty(
        SYSTEM_PROXY_PORT_ATTRIBUTE, "80"));

    /** Milliseconds to wait before refresh the download progress. */
    private static Integer updateProgressInterval = Integer.parseInt(System.getProperty(
        "abiquo.appliancemanager.upload.progressInterval", UPDATE_INTERVAL_DEFAULT));

    /** Timeout for remote connection (during deploy). */
    private static Integer timeout = Integer.parseInt(System.getProperty(
        "abiquo.appliancemanager.deploy.timeout", DEPLOY_TIMEOUT_DEFAULT));

    /**
     * Timeout of of available ovf packages refresh on the filesystem (when timeout use cached
     * result)
     */
    private static Integer fstimeoutMs = Integer.parseInt(System.getProperty(
        "abiquo.appliancemanager.fstimeoutms", "7000"));

    public static Integer getFsTimeoutMs()
    {
        return fstimeoutMs;
    }

    public static String getRepositoryPath()
    {
        if (!repositoryPath.endsWith("/"))
        {
            repositoryPath += '/';
        }

        if (!isValidRepositoryPath(repositoryPath))
        {
            throw new AMException(AMError.CONFIG_REPOSITORY_PATH, repositoryPath);
        }

        return repositoryPath;
    }

    public static String getRepositoryLocation()
    {
        if (!isValidRepositoryLocation(repositoryLocation))
        {
            throw new AMException(AMError.CONFIG_REPOSITORY_LOCATION, repositoryLocation);
        }

        return repositoryLocation;
    }

    public static Integer getUpdateProgressInterval()
    {
        return updateProgressInterval;
    }

    public static Integer getTimeout()
    {
        return timeout;
    }

    public static String getProxyHost()
    {
        return proxyHost;
    }

    public static Integer getProxyPort()
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

    public static String printConfig()
    {
        return String.format("RepositoryConfiguation : [path:%s, loation:%s]", repositoryPath,
            repositoryLocation);
    }

    public static boolean isValidRepositoryPath(final String repositoryPath)
    {
        return !(repositoryPath == null || repositoryPath.isEmpty() || !repositoryPath
            .endsWith("/"));

    }

    public static boolean isValidRepositoryLocation(final String repositoryLocation)
    {
        return !(repositoryLocation == null || repositoryLocation.isEmpty() || !repositoryLocation
            .contains(":"));
    }
}
