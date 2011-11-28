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
