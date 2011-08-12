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
package com.abiquo.api.resources.config;

import static com.abiquo.api.resources.config.PrivilegeResource.createTransferObject;

import java.util.Collection;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

import org.apache.wink.common.annotations.Workspace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.resources.AbstractResource;
import com.abiquo.api.services.EnterpriseService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.model.util.ModelTransformer;
import com.abiquo.server.core.enterprise.Privilege;
import com.abiquo.server.core.enterprise.PrivilegeDto;
import com.abiquo.server.core.enterprise.PrivilegesDto;

@Path(PrivilegesResource.PRIVILEGES_PATH)
@Controller
@Workspace(workspaceTitle = "Abiquo configuration workspace", collectionTitle = "Privileges")
public class PrivilegesResource extends AbstractResource
{
    public static final String PRIVILEGES_PATH = "config/privileges";

    @Autowired
    private EnterpriseService service;

    @GET
    public PrivilegesDto getPrivileges(@Context final IRESTBuilder restBuilder) throws Exception
    {
        Collection<Privilege> ps = service.findAllPrivileges();

        PrivilegesDto privileges = new PrivilegesDto();
        for (Privilege p : ps)
        {
            privileges.add(createTransferObject(p, restBuilder));
        }

        return privileges;
    }

    public static PrivilegesDto createAdminTransferObjects(final Collection<Privilege> privs,
        final IRESTBuilder restBuilder) throws Exception
    {
        PrivilegesDto privileges = new PrivilegesDto();
        for (Privilege privilege : privs)
        {
            PrivilegeDto pDto =
                ModelTransformer.transportFromPersistence(PrivilegeDto.class, privilege);

            privileges.add(pDto);
        }

        return privileges;
    }

}
