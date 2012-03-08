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

/**
 * Main configuration for the Appliance Manager artifact.
 * 
 * @author apuig
 */
public class AMConfiguration
{

    /** **** ***** REPOSITORY FILESYSTEM **** ******/

    /**
     * Where the ''repositoryPath'' is exported. <br>
     * Usually a NFS location such 'nsf-devel:/opt/vm_repository' .
     */
    private static String repositoryLocation = //
        System.getProperty("abiquo.appliancemanager.repositoryLocation", "nfs-test:/test/path");

    /** Where the ''repositoryLocation'' file system is mounted. */
    private static String repositoryPath = //
        System.getProperty("abiquo.appliancemanager.localRepositoryPath", "/tmp/testrepo");

    /** **** ***** TIMEOUT REPOSITORYN **** ******/

    /** Max time to check the .abiquo_repository file mark in the filesystem */
    public final static Integer REPOSITORY_FILE_MARK_CHECK_TIMEOUT_SECONDS = // 5 seconds
        Integer.parseInt(System.getProperty("abiquo.repository.timeoutSeconds", "5"));

    /** Max time to scan the enterprise repository filesystem folder finding new templates */
    public final static Integer ENTERPRISE_REPOSITORY_REFRESH_TIMEOUT = // repo check x10
        Integer.parseInt(System.getProperty("abiquo.appliancemanager.fstimeoutms",
            String.valueOf(REPOSITORY_FILE_MARK_CHECK_TIMEOUT_SECONDS * 10 * 1000)));

    /** **** ***** TIMEOUT CONNECTIONS **** ******/

    public final static Integer DOWNLOADING_PUBLISH_INTERVAL = Integer.valueOf(System.getProperty(
        "abiquo.appliancemanager.upload.progressIntervall", "5000"));

    public final static Integer HTTP_CONNECTION_TIMEOUT = // 2 minute
        Integer.parseInt(System.getProperty("abiquo.appliancemanager.deploy.connection", "120000"));

    public final static Integer HTTP_IDLE_TIMEOUT = // ten minutes
        Integer.parseInt(System.getProperty("abiquo.appliancemanager.deploy.idle", "600000"));

    public final static Integer HTTP_REQUEST_TIMEOUT = // a day
        Integer.parseInt(System.getProperty("abiquo.appliancemanager.deploy.totalTimeout",
            "86400000"));

    public final static Integer HTTP_MAX_CONNECTIONS = //
        Integer.parseInt(System.getProperty("abiquo.appliancemanager.downloads", "-1"));

    /** **** ***** PROXY **** ******/
    public final static String HTTP_PROXY_USER = //
        System.getProperty("abiquo.httpProxy.user");

    public final static String HTTP_PROXY_PASS = //
        System.getProperty("abiquo.httpProxy.password");

    public final static String HTTP_PROXY_HOST = //
        System.getProperty("abiquo.httpProxy.host");

    public final static Integer HTTP_PROXY_PORT = //
        Integer.parseInt(System.getProperty("abiquo.httpProxy.port", "80"));

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

    public static boolean isProxy()
    {
        return HTTP_PROXY_HOST != null;
    }

    /** Only serialize elements related to the repository (path and location) */
    public static String printConfig()
    {
        return String.format("Repository:\nexportLocation '%s'\nmountPoint '%s'",
            repositoryLocation, repositoryPath);
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
