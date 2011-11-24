/**
 * 
 */
package com.abiquo.api.resources.cloud;

import org.springframework.stereotype.Controller;

import com.abiquo.api.exceptions.mapper.APIExceptionMapper;
import com.abiquo.api.resources.AbstractResource;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.model.util.ModelTransformer;
import com.abiquo.server.core.infrastructure.storage.DiskManagement;
import com.abiquo.server.core.infrastructure.storage.DiskManagementDto;

/**
 * @author jdevesa
 */
@Controller
public class DiskResource extends AbstractResource
{

    public static String DISK = "disk";

    public static String DISK_PARAM = "{" + DISK + "}";

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
        final IRESTBuilder restBuilder) throws Exception
    {
        DiskManagementDto dto =
            ModelTransformer.transportFromPersistence(DiskManagementDto.class, disk);
        dto.addLinks(restBuilder.buildVirtualDatacenterDiskLinks(disk));

        return dto;

    }
}
