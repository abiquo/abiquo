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

import static com.abiquo.api.util.URIResolver.buildPath;

import java.util.LinkedList;
import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.wink.common.annotations.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.exceptions.BadRequestException;
import com.abiquo.api.exceptions.mapper.APIExceptionMapper;
import com.abiquo.api.resources.AbstractResource;
import com.abiquo.api.services.StorageService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.api.util.URIResolver;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.model.transport.AcceptedRequestDto;
import com.abiquo.model.transport.LinksDto;
import com.abiquo.model.util.ModelTransformer;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.infrastructure.storage.DiskManagement;
import com.abiquo.server.core.infrastructure.storage.DiskManagementDto;
import com.abiquo.server.core.infrastructure.storage.DisksManagementDto;
import com.abiquo.server.core.infrastructure.storage.VolumeManagement;

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
     * Attaches Hard Disks to be used by a Virtual Machine.
     * 
     * @param vdcId identifier of the Virtual Datacenter.
     * @param vappId identifier of the Virtual Appliance.
     * @param vmId identifier of the Virtual Machine.
     * @param hdRefs A list of links to the volumes to attach.
     * @param restBuilder a Context-injected object to create the links of the Dto
     * @return the {@link DiskManagementDto} object that contains all the {@link DiskManagementDto}
     * @throws Exception any thrown exception. Moved to HTTP status code in the
     *             {@link APIExceptionMapper} exception mapper.
     */
    @POST
    @Path(DISKS_PATH)
    public AcceptedRequestDto< ? > attachHardDisks(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) @NotNull @Min(1) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) @NotNull @Min(1) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) @NotNull @Min(1) final Integer vmId,
        final LinksDto hdRefs, @Context final IRESTBuilder restBuilder) throws Exception
    {

        Object result = service.attachHardDisks(vdcId, vappId, vmId, hdRefs);

        // The attach method may return a Tarantino task identifier if the operation requires a
        // reconfigure. Otherwise it will return null.
        if (result != null)
        {
            AcceptedRequestDto<Object> response = new AcceptedRequestDto<Object>();
            response.setStatusUrlLink("http://status");
            response.setEntity(result);
            return response;
        }

        return null;
    }

    /**
     * Detach all hard disks from the virtual machine.
     * 
     * @param vdcId The id of the virtual datacenter where the virtual machine belongs to.
     * @param vappId The id of the virtual appliance of the virtual machine.
     * @param vmId The id of the virtual machine.
     * @param restBuilder The rest builder used to generate resource links.
     * @return The identifier of the attachment task, if the virtual machine is deployed,
     *         <code>null</code> otherwise.
     * @throws Exception If an error occurs. Exception will be automatically transformed to the
     *             appropriate HTTP errors by the {@link APIExceptionMapper}.
     */
    @DELETE
    @Path(DISKS_PATH)
    public AcceptedRequestDto< ? > detachHardDisks(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) @NotNull @Min(1) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) @NotNull @Min(1) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) @NotNull @Min(1) final Integer vmId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {

        Object result = service.detachHardDisks(vdcId, vappId, vmId);

        // The attach method may return a Tarantino task identifier if the operation requires a
        // reconfigure. Otherwise it will return null.
        if (result != null)
        {
            AcceptedRequestDto<Object> response = new AcceptedRequestDto<Object>();
            response.setStatusUrlLink("http://status");
            response.setEntity(result);
            return response;
        }

        return null;
    }

    /**
     * Modify the hard disks of the virtual machine.
     * 
     * @param vdcId The id of the virtual datacenter where the virtual machine belongs to.
     * @param vappId The id of the virtual appliance of the virtual machine.
     * @param vmId The id of the virtual machine.
     * @param hdRefs A list of links to the volumes for the virtual machine.
     * @param restBuilder The rest builder used to generate resource links.
     * @return The identifier of the attachment task, if the virtual machine is deployed,
     *         <code>null</code> otherwise.
     * @throws Exception If an error occurs. Exception will be automatically transformed to the
     *             appropriate HTTP errors by the {@link APIExceptionMapper}.
     */
    @PUT
    @Path(DISKS_PATH)
    public AcceptedRequestDto< ? > changeHardDisks(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) @NotNull @Min(1) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) @NotNull @Min(1) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) @NotNull @Min(1) final Integer vmId,
        @NotNull final LinksDto hdRefs, @Context final IRESTBuilder restBuilder) throws Exception
    {
        // TODO: apply Albert configuration changes.
        return null;
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
     * Detach a hard disk from the virtual machine.
     * 
     * @param vdcId The id of the virtual datacenter where the virtual machine belongs to.
     * @param vappId The id of the virtual appliance of the virtual machine.
     * @param vmId The id of the virtual machine.
     * @param diskId The id of the volume to detach.
     * @param restBuilder The rest builder used to generate resource links.
     * @return The identifier of the attachment task, if the virtual machine is deployed,
     *         <code>null</code> otherwise.
     * @throws Exception If an error occurs. Exception will be automatically transformed to the
     *             appropriate HTTP errors by the {@link APIExceptionMapper}.
     */
    @DELETE
    @Path(DISKS_PATH + "/" + DISK_PARAM)
    public AcceptedRequestDto< ? > detachHardDisk(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) @NotNull @Min(1) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) @NotNull @Min(1) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) @NotNull @Min(1) final Integer vmId,
        @PathParam(DISK) @NotNull @Min(0) final Integer diskId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        // TODO : apply Albert configuration changes.
        return null;
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
