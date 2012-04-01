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

package com.abiquo.api.resources.cloud;

import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.apache.wink.common.annotations.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.exceptions.mapper.APIExceptionMapper;
import com.abiquo.api.resources.AbstractResource;
import com.abiquo.api.services.StorageService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.server.core.infrastructure.storage.DiskManagement;
import com.abiquo.server.core.infrastructure.storage.DiskManagementDto;
import com.abiquo.server.core.infrastructure.storage.DisksManagementDto;

/**
 * <pre>
 * Resource that contains all the methods related to list of {@link DiskManagementDto}. Exposes all
 * the methods inside the URI
 * http://{host}/api/cloud/virtualdatacenters/{vdcid}/disks
 * </pre>
 * 
 * Extra disks are resources created here and used by virtual machines
 * 
 * @author jdevesa
 * @wiki Hard Disks is a new feature in 2.0. From now you can attach more than one disk to a virtual
 *       machine without the need of a Volume Device. However, this feature still have some
 *       restrictions: 1. It is only available in ESXi. 2. Once you have undeployed a Virtual
 *       Machine with multiple hard disks, all the data is lost.
 */
@Parent(VirtualDatacenterResource.class)
@Path(DisksResource.DISKS_PATH)
@Controller
public class DisksResource extends AbstractResource
{
    public static final String DISKS_PATH = "disks";

    public static final String FORCE = "force";

    /** Autowired business logic service. */
    @Autowired
    protected StorageService service;

    /**
     * Exposes the method to query all the extra disks generated into a virtual datacenter.
     * 
     * @title Retrieve all hard disks
     * @param vdcId identifier of the virtual datacenter
     * @param restBuilder a Context-injected object to create the links of the Dto
     * @return a instance of {@link DisksManagementDto}. Is the wrapper list for
     *         {@link DiskManagementDto} object.
     * @throws Exception any thrown exception. Moved to HTTP status code in the
     *             {@link APIExceptionMapper} exception mapper.
     */
    @GET
    @Produces(DisksManagementDto.MEDIA_TYPE)
    public DisksManagementDto getListOfHardDisks(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) @NotNull @Min(1) final Integer vdcId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        DisksManagementDto dtos = new DisksManagementDto();

        List<DiskManagement> disks = service.getListOfHardDisksByVirtualDatacenter(vdcId);

        for (DiskManagement disk : disks)
        {
            dtos.getCollection().add(DiskResource.createDiskTransferObject(disk, restBuilder));
        }
        return dtos;
    }

    /**
     * Expose the method to create a new Hard Disk.
     * 
     * @title Create a hard disk
     * @wiki This method creates a hard disk into a virtual datacenter available for use in any
     *       virtual machine.
     * @param vdcId identifier of the {@link VirtualDatacenter}
     * @param inputDto object {@link DiskManagementDto} to create.
     * @param restBuilder a Context-injected object to create the links of the Dto
     * @return the created Disk.
     * @throws Exception any thrown exception. Moved to HTTP status code in the
     *             {@link APIExceptionMapper} exception mapper.
     */
    @POST
    @Consumes(DiskManagementDto.MEDIA_TYPE)
    @Produces(DiskManagementDto.MEDIA_TYPE)
    public DiskManagementDto createHardDisk(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) @NotNull @Min(1) final Integer vdcId,
        final DiskManagementDto inputDto,
        @QueryParam(FORCE) @DefaultValue("false") final Boolean force,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        DiskManagement disk = service.createHardDisk(vdcId, inputDto.getSizeInMb(), force);

        return DiskResource.createDiskTransferObject(disk, restBuilder);
    }

}
