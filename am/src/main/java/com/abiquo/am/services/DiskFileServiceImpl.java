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
package com.abiquo.am.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.abiquo.am.services.DiskFileService;
import com.abiquo.appliancemanager.config.AMConfiguration;
import com.abiquo.appliancemanager.config.AMConfigurationManager;

/**
 * Default implementation of the {@link DiskFileService}.
 * 
 * @author ibarrera
 */
@Service
public class DiskFileServiceImpl extends OVFPackageConventions implements DiskFileService
{
    /** The logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DiskFileServiceImpl.class);

    /** The module configuration. */
    private AMConfiguration config;

    /** The path to the local repository. */
    private String repositoryPath;

    /**
     * Creates the service.
     */
    public DiskFileServiceImpl()
    {
        this.config = AMConfigurationManager.getInstance().getAMConfiguration();
        this.repositoryPath = this.config.getRepositoryPath();

        if (!this.repositoryPath.endsWith("/"))
        {
            this.repositoryPath += "/";
        }
    }

    @Override
    public String get(final String path) throws FileNotFoundException
    {
        File file = new File(repositoryPath + path);

        if (!file.exists())
        {
            throw new FileNotFoundException();
        }

        return file.getPath();
    }

    @Override
    public void copy(String source, final String destination) throws FileNotFoundException,
        IOException
    {
        source = customEncode(source); // source is automatic decoded ( :9000 -> %3A9000)

        LOGGER.info("Copying disk file from [{}] to [{}]", source, destination);

        FileUtils.copyFile(new File(repositoryPath + source),
            new File(repositoryPath + destination));

        LOGGER.info("Copy process finished");
    }

    /**
     * Gets the config.
     * 
     * @return the config
     */
    public AMConfiguration getConfig()
    {
        return config;
    }

}
