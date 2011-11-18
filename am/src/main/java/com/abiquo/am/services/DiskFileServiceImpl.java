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

import static com.abiquo.am.services.OVFPackageConventions.customEncode;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.abiquo.am.exceptions.AMError;
import com.abiquo.appliancemanager.config.AMConfigurationManager;
import com.abiquo.appliancemanager.exceptions.AMException;

/**
 * Default implementation of the {@link DiskFileService}.
 * 
 * @author ibarrera
 */
@Service
public class DiskFileServiceImpl implements DiskFileService
{
    /** The logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DiskFileServiceImpl.class);

    /** The path to the local repository. */
    private final String repositoryPath = AMConfigurationManager.getInstance().getAMConfiguration()
        .getRepositoryPath();

    /**
     * Creates the service.
     */
    public DiskFileServiceImpl()
    {
    }

    @Override
    public String get(final String path)
    {
        return getFile(path).getPath();
    }

    @Override
    public void copy(String source, final String destination)
    {
        source = customEncode(source); // source is automatic decoded ( :9000 -> %3A9000)

        LOGGER.info("Copying disk file from [{}] to [{}]", source, destination);

        final File sourceFile = getFile(source);
        final File destinationFile = new File(repositoryPath + destination);

        if (destinationFile.exists())
        {
            throw new AMException(AMError.DISK_FILE_ALREADY_EXIST, destination);
        }

        try
        {
            FileUtils.copyFile(sourceFile, destinationFile);
        }
        catch (IOException e)
        {
            throw new AMException(AMError.DISK_FILE_COPY_ERROR, e);
        }

        LOGGER.info("Copy process finished");
    }

    /**
     * Or return DISK_FILE_NOT_FOUND
     */
    private File getFile(final String path)
    {
        File file = new File(FilenameUtils.concat(repositoryPath, path));

        if (!file.exists())
        {
            throw new AMException(AMError.DISK_FILE_NOT_FOUND, path);
        }

        return file;
    }
}
