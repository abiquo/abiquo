package com.abiquo.api.resources.cloud;

import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

import org.apache.wink.common.annotations.Parent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 */
@Parent(VirtualDatacenterResource.class)
@Path(DisksResource.DISKS_PATH)
@Controller
public class DisksResource extends AbstractResource
{
    private static final Logger LOGGER = LoggerFactory.getLogger(DisksResource.class);

    public static final String DISKS_PATH = "disks";

    /** Autowired business logic service. */
    @Autowired
    protected StorageService service;

    /**
     * Exposes the method to query all the extra disks generated into a virtual datacenter.
     * 
     * @param vdcId identifeir of the virtual datacenter
     * @param restBuilder a Context-injected object to create the links of the Dto
     * @return a instance of {@link DisksManagementDto}. Is the wrapper list for
     *         {@link DiskManagementDto} object.
     * @throws Exception any thrown exception. Moved to HTTP status code in the
     *             {@link APIExceptionMapper} exception mapper.
     */
    @GET
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
     * @param vdcId identifier of the {@link VirtualDatacenter}
     * @param inputDto object {@link DiskManagementDto} to create.
     * @param restBuilder a Context-injected object to create the links of the Dto
     * @return the created Disk.
     * @throws Exception any thrown exception. Moved to HTTP status code in the
     *             {@link APIExceptionMapper} exception mapper.
     */
    @POST
    public DiskManagementDto createHardDisk(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) @NotNull @Min(1) final Integer vdcId,
        final DiskManagementDto inputDto, @Context final IRESTBuilder restBuilder) throws Exception
    {
        DiskManagement disk = service.createHardDisk(vdcId, inputDto.getSizeInMb());

        return DiskResource.createDiskTransferObject(disk, restBuilder);
    }

}
