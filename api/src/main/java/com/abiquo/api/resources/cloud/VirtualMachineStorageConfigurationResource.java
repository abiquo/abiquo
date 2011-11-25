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

/**
 * Abiquo premium edition
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

/**
 * 
 */
package com.abiquo.api.resources.cloud;

import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

import org.apache.wink.common.annotations.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.exceptions.mapper.APIExceptionMapper;
import com.abiquo.api.resources.AbstractResource;
import com.abiquo.api.services.StorageService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.model.util.ModelTransformer;
import com.abiquo.server.core.infrastructure.storage.DiskManagement;
import com.abiquo.server.core.infrastructure.storage.DiskManagementDto;
import com.abiquo.server.core.infrastructure.storage.DisksManagementDto;

/**
 * <pre>
 * Resource that contains all the methods related to a Virtual Machine storage configuration. Exposes all
 * the methods inside the URI
 * http://{host}/api/cloud/virtualdatacenters/{vdcid}/virtualappliances/{vappids
 * }/virtualmachines/{vmids}/storage/disks
 * </pre>
 * 
 * @author jdevesa@abiquo.com
 */
@Parent(VirtualMachineResource.class)
@Controller
@Path(VirtualMachineStorageConfigurationResource.STORAGE)
public class VirtualMachineStorageConfigurationResource extends AbstractResource
{
    /** General REST path of the resource */
    public static final String STORAGE = "storage";

    /** Path to access to 'disks' section. */
    public static final String DISKS_PATH = "disks";

    /** 'Rel' link to acces to 'disks' section' */
    public static final String DISK = "disk";

    /** Parameter to map the input values related to Disks. */
    public static final String DISK_PARAM = "{" + DISK + "}";

    /** Autowired business logic service. */
    @Autowired
    protected StorageService service;

    /**
     * Returns all the defined disks in the virtual machine.
     * 
     * @param vdcId identifier of the Virtual Datacenter.
     * @param vappId identifier of the Virtual Appliance.
     * @param vmId identifier of the Virtual Machine.
     * @param restBuilder a Context-injected object to create the links of the Dto
     * @return the {@link DisksManagementDto} object that contains all the
     *         {@link DisksManagementDto}
     * @throws Exception any thrown exception. Moved to HTTP status code in the
     *             {@link APIExceptionMapper} exception mapper.
     */
    @GET
    @Path(DISKS_PATH)
    public DisksManagementDto getListOfHardDisks(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) @NotNull @Min(1) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) @NotNull @Min(1) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) @NotNull @Min(1) final Integer vmId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        DisksManagementDto dtos = new DisksManagementDto();

        List<DiskManagement> disks = service.getListOfHardDisksByVM(vdcId, vappId, vmId);

        for (DiskManagement disk : disks)
        {
            dtos.getCollection().add(createDiskTransferObject(disk, vdcId, vappId, restBuilder));
        }
        return dtos;
    }

    /**
     * Creates a new Hard Disk to be used by a Virtual Machine.
     * 
     * @param vdcId identifier of the Virtual Datacenter.
     * @param vappId identifier of the Virtual Appliance.
     * @param vmId identifier of the Virtual Machine.
     * @param inputDto {@link DiskManagementDto} with the attributes to create a new HardDisk inside
     *            the machine.
     * @param restBuilder a Context-injected object to create the links of the Dto
     * @return the {@link DiskManagementDto} object that contains all the {@link DiskManagementDto}
     * @throws Exception any thrown exception. Moved to HTTP status code in the
     *             {@link APIExceptionMapper} exception mapper.
     */
    @POST
    @Path(DISKS_PATH)
    public DiskManagementDto createHardDisk(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) @NotNull @Min(1) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) @NotNull @Min(1) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) @NotNull @Min(1) final Integer vmId,
        final DiskManagementDto inputDto, @Context final IRESTBuilder restBuilder) throws Exception
    {
        DiskManagement disk = service.attachHardDiskIntoVM(vdcId, vappId, vmId, 0);

        return createDiskTransferObject(disk, vdcId, vappId, restBuilder);
    }

    /**
     * Returns a single disk according on its order in Virtual Machine
     * 
     * @param vdcId identifier of the Virtual Datacenter.
     * @param vappId identifier of the Virtual Appliance.
     * @param vmId identifier of the Virtual Machine.
     * @param diskOrder identifier of the hard disk inside the virtual machine
     * @param restBuilder a Context-injected object to create the links of the Dto
     * @return the {@link DiskManagementDto} object that contains all the {@link DiskManagementDto}
     * @throws Exception any thrown exception. Moved to HTTP status code in the
     *             {@link APIExceptionMapper} exception mapper.
     */
    @GET
    @Path(DISKS_PATH + "/" + DISK_PARAM)
    public DiskManagementDto getHardDisk(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) @NotNull @Min(1) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) @NotNull @Min(1) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) @NotNull @Min(1) final Integer vmId,
        @PathParam(DISK) @NotNull @Min(0) final Integer diskOrder,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        DiskManagement disk = service.getHardDiskByVM(vdcId, vappId, vmId, diskOrder);

        return createDiskTransferObject(disk, vdcId, vappId, restBuilder);
    }

    /**
     * Deleting a single disk identified by its order in Virtual Machine
     * 
     * @param vdcId identifier of the Virtual Datacenter.
     * @param vappId identifier of the Virtual Appliance.
     * @param vmId identifier of the Virtual Machine.
     * @param diskOrder identifier of the hard disk inside the virtual machine
     * @param restBuilder a Context-injected object to create the links of the Dto
     * @return the {@link DiskManagementDto} object that contains all the {@link DiskManagementDto}
     * @throws Exception any thrown exception. Moved to HTTP status code in the
     *             {@link APIExceptionMapper} exception mapper.
     */
    @DELETE
    @Path(DISKS_PATH + "/" + DISK_PARAM)
    public void detachHardDisk(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) @NotNull @Min(1) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) @NotNull @Min(1) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) @NotNull @Min(1) final Integer vmId,
        @PathParam(DISK) @NotNull @Min(0) final Integer diskOrder,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        service.detachHardDisk(vdcId, vappId, vmId, diskOrder);
    }

    /**
     * Creates the DTO {@link DiskManagementDto} from the pojo object {@link DiskManagement} and
     * sets its related links.
     * 
     * @param disk input {@link DiskManagement} object.
     * @param restBuilder a Context-injected object to create the links of the Dto
     * @return the output {@link DiskManagementDto} object.
     * @throws Exception any thrown exception. Moved to HTTP status code in the
     *             {@link APIExceptionMapper} exception mapper.
     */
    public static DiskManagementDto createDiskTransferObject(final DiskManagement disk,
        final Integer vdcId, final Integer vappId, final IRESTBuilder restBuilder) throws Exception
    {
        DiskManagementDto dto =
            ModelTransformer.transportFromPersistence(DiskManagementDto.class, disk);
        dto.addLinks(restBuilder.buildDiskLinks(disk, vdcId, vappId));

        return dto;

    }

    /**
     * Creates the pojo {@link DiskManagement} from the DTO object {@link DiskManagementDto}.
     * 
     * @param disk input {@link DiskManagementDto} object.
     * @return the output {@link DiskManagement} object.
     * @throws Exception any thrown exception. Moved to HTTP status code in the
     *             {@link APIExceptionMapper} exception mapper.
     */
    public static DiskManagement createDiskPersistenceObject(final DiskManagementDto inputDto)
        throws Exception
    {
        return ModelTransformer.persistenceFromTransport(DiskManagement.class, inputDto);
    }
}
