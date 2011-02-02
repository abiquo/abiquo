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

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.ovfmanager.ovf.xml.OVFSerializer;

/**
 * Maintains a singleton instance in order to reads and validate the AM XML configuration file.
 * 
 * @author apuig
 */
public class AMConfigurationManager
{
    private final static Logger logger = LoggerFactory.getLogger(AMConfigurationManager.class);

    /** The immutable AMConfiguration object. */
    private AMConfiguration configuration;

    /** Any initialization error cause. */
    private String configurationError = null;


    public final static String UPDATE_INTERVAL_DEFAULT = "5000";

    public final static String DEPLOY_TIMEOUT_DEFAULT = "60000";

    /** The singleton instance. */
    private static AMConfigurationManager singleton = null;

    /** Access the singleton instance. */
    public synchronized static AMConfigurationManager getInstance()
    {
        if (AMConfigurationManager.singleton == null)
        {
            AMConfigurationManager.singleton = new AMConfigurationManager();
        }
        return AMConfigurationManager.singleton;
    }

    /**
     * Singleton constructor, create the immutable AMConfiguration from XML configuration file. Any
     * configuration exception is reflected on the AMConfiguration ''error'' element.
     */
    private AMConfigurationManager()
    {
        try
        {
            configuration = loadConfiguration();

            check();
        }
        catch (Exception e)
        {
            logger.error("An ApplianceManager configuration error occurred", e);

            setConfigurationError(e.getMessage());
        }
    }

    /**
     * Loads the XML from the default configuration file location. And validate its elements.
     * 
     * @throws Exception it it can not load all the properties
     */
    private AMConfiguration loadConfiguration() throws Exception
    {
        String repositoryLocation =
            System.getProperty("abiquo.appliancemanager.repositoryLocation", "nfs-test:/test/repostiory"); // FIXME not default values
        
        String repositoryPath = System.getProperty("abiquo.appliancemanager.localRepositoryPath", "/tmp/testrepo"); // FIXME not default values
        

        if (repositoryLocation == null || repositoryLocation.isEmpty())
        {
            throw new Exception("Missing required configuration element 'Repository'.'Location.'");
        }

        if (repositoryPath == null || repositoryPath.isEmpty())
        {
            throw new Exception("Missing required configuration element 'Repository'.'Path'.");
        }

        // the user can miss the final / on this parameter
        if (!repositoryPath.endsWith("/"))
        {
            repositoryPath += '/';
        }

        if (!AMConfiguration.isValidRepositoryLocation(repositoryLocation))
        {
            final String cause =
                String.format("Configuration element 'Repository'.'Location'[%s] is invalid.",
                    repositoryLocation);
            throw new Exception(cause);
        }

        if (!AMConfiguration.isValidRepositoryPath(repositoryPath))
        {
            final String cause =
                String.format("Configuration element 'Repository'.'Path'[%s] is invalid.",
                    repositoryPath);
            throw new Exception(cause);
        }

        AMConfiguration configuration = new AMConfiguration(repositoryPath, repositoryLocation);

        // optional configuration elements or defaults
        configuration.setUpdateProgressInterval(Integer.parseInt(System.getProperty(
            "abiquo.appliancemanager.upload.progressInterval", UPDATE_INTERVAL_DEFAULT)));
        //configuration.setDeployBuffer(DEPLOY_BUFFER_DEFAULT);
        configuration.setTimeout(Integer.parseInt(System.getProperty(
            "abiquo.appliancemanager.deploy.timeout", DEPLOY_TIMEOUT_DEFAULT)));

        configuration = setProxyConfiguration(configuration);

        // set it on the OVFManager serialize utility class
        OVFSerializer.getInstance().setFormatOutput(true);
        OVFSerializer.getInstance().setValidateXML(false);

        return configuration;
    }

    private void check() throws Exception
    {
        checkCanWriteOnRepositoryPath();

        // TODO any other required check
    }

    private final static String ABIQUO_REPOSITROY_FILE_MARK = ".abiquo_repository";

    /***
     * Assure the configured ''repositoryPath'' can be written by the current user. Also check for
     * the ''.abiquo_repository'' file mark or create it.
     * 
     * @throw ConfiguraitonException if ''repositoryPath'' do not exist or can not be written.
     */
    private void checkCanWriteOnRepositoryPath() throws Exception
    {
        final String repoPath = configuration.getRepositoryPath();

        File repoFile = new File(repoPath);

        if (!repoFile.exists())
        {
            final String msg = String.format("''RepositoryPath'' at [%s] do not exist. ", repoPath);
            logger.warn(msg + "Try to create it.");

            if (!repoFile.mkdirs())
            {
                throw new Exception(msg + "And can not be created.");
            }
        }

        if (!repoFile.canWrite())
        {
            final String msg =
                String.format("Can not write on the ''RepositoryPath'' at [%s].", repoPath);
            throw new Exception(msg);
        }

        File abiquoRepoMark = new File(repoPath + ABIQUO_REPOSITROY_FILE_MARK);
        if (!abiquoRepoMark.exists())
        {
            try
            {
                if (!abiquoRepoMark.createNewFile())
                {
                    final String msg =
                        String.format("Can not create the abiquo repositroy file mark at [%s].",
                            abiquoRepoMark.getAbsolutePath());

                    throw new Exception(msg);
                }
            }
            catch (IOException e)
            {
                final String msg =
                    String.format("Can not create the abiquo repositroy file mark at [%s].",
                        abiquoRepoMark.getAbsolutePath());

                throw new Exception(msg, e);
            }
        }

        // [FATAL] ApplianceManager configuration error, caused by:
        // "Contact the system administrator"
    }

    /**
     * Gets the immutable AM configuration object if there isn't any configuration error. So, first
     * check if there ''validateAMConfiguration'' prior to call this method.
     * 
     * @return the AM configuration
     */
    public AMConfiguration getAMConfiguration()
    {
        assert validateAMConfiguration();

        // if (!validateAMConfiguration())
        // {
        // throw new Exception(getConfigurationError());
        // }

        return configuration;
    }

    /**
     * Checks if there are some repository configuration error.
     * 
     * @return true if ''getConfigurationError'' is not null.
     */
    public boolean validateAMConfiguration()
    {
        return configurationError == null;
    }

    /**
     * Returns the configuration error cause.
     * 
     * @return error cause (null if none).
     */
    public String getConfigurationError()
    {
        return configurationError;
    }

    public void setConfigurationError(final String configurationError)
    {
        this.configurationError = configurationError;
    }

    /**
     * appends to ...
     */
    public void addConfigurationError(final String configurationError)
    {
        if (this.configurationError != null)
        {
            this.configurationError.concat("\n").concat(configurationError);
        }
        else
        {
            this.configurationError = configurationError;
        }
    }

    /** System property determine to use Proxy connections using this host. */
    private final static String SYSTEM_PROXY_HOST_ATTRIBUTE = "http.proxyHost";

    /** System property determine to use Proxy connections using this port. */
    private final static String SYSTEM_PROXY_PORT_ATTRIBUTE = "http.proxyPort";

    private AMConfiguration setProxyConfiguration(AMConfiguration config)
    {
        String host;
        Integer port;
        host = System.getProperty(SYSTEM_PROXY_HOST_ATTRIBUTE, null);
        try
        {
            port = Integer.valueOf(System.getProperty(SYSTEM_PROXY_PORT_ATTRIBUTE, null));

        }
        catch (Exception e)
        {
            port = null;
        }

        config.setProxyHost(host);
        config.setProxyPort(port);

        return config;
    }

}
