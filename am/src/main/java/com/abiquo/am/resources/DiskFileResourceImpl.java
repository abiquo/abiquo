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
package com.abiquo.am.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.wink.common.annotations.Workspace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;

import com.abiquo.am.services.DiskFileService;
import com.abiquo.appliancemanager.diskfile.DiskFile;

/**
 * Resource representation of the Disk Image files.
 * 
 * @author ibarrera
 */

@Controller("diskFileResource")
@Path(DiskFileResourceImpl.DISK_FILE_PATH)
@Workspace(workspaceTitle = "Appliance Manager disks", collectionTitle = "rawdisks")
public class DiskFileResourceImpl implements DiskFileResource, InitializingBean
{
    /** The logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DiskFileResourceImpl.class);

    /** The resource name. */
    public static final String DISK_FILE = "diskfile";

    /**
     * The resource parameter matching configuration.
     * <p>
     * Must override default regular expression in order to be able to match complete URIs as the
     * Disk File identifier.
     */
    public static final String DISK_FILE_PARAM = "{" + DISK_FILE + ": .*}"; // FIXME take care of .*

    /** The resource path. */
    public static final String DISK_FILE_PATH = ApplianceManagerPaths.DISK_FILE_PATH + "/"
        + DISK_FILE_PARAM;

    /** The Disk File Manager Service */
    @Autowired
    private DiskFileService diskFileService;

    @Override
    public void afterPropertiesSet() throws Exception
    {
        Assert.notNull(diskFileService, "diskFileService must not be null");
    }

    @Override
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.TEXT_XML})
    public DiskFile getDiskFile(@PathParam(DISK_FILE) final String srcPath)
    {
        DiskFile diskFile = new DiskFile();

        diskFile.setLocation(diskFileService.get(srcPath));

        return diskFile;
    }

    @Override
    @PUT
    @Consumes({MediaType.APPLICATION_XML, MediaType.TEXT_XML})
    @Produces({MediaType.APPLICATION_XML, MediaType.TEXT_XML})
    public DiskFile copyDiskFile(final DiskFile diskfile, @PathParam(DISK_FILE) final String src)
    {
        LOGGER.debug("Copy opperation requested for Disk File {}", src);

        diskFileService.copy(src, diskfile.getLocation());

        return diskfile;
    }

    /**
     * Gets the diskFileService.
     * 
     * @return the diskFileService
     */
    public DiskFileService getDiskFileService()
    {
        return diskFileService;
    }

    /**
     * Sets the diskFileService.
     * 
     * @param diskFileService the diskFileService to set
     */
    public void setDiskFileService(DiskFileService diskFileService)
    {
        this.diskFileService = diskFileService;
    }

}
