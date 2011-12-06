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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

import org.apache.wink.common.annotations.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.resources.AbstractResource;
import com.abiquo.api.services.EnterpriseService;
import com.abiquo.api.services.UserService;
import com.abiquo.api.spring.security.SecurityService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.model.enumerator.Privileges;
import com.abiquo.server.core.enterprise.Privilege;
import com.abiquo.server.core.enterprise.PrivilegeDto;
import com.abiquo.server.core.enterprise.User;

@Parent(PrivilegesResource.class)
@Path(PrivilegeResource.PRIVILEGE_PARAM)
@Controller
public class PrivilegeResource extends AbstractResource
{
    public static final String PRIVILEGE = "privilege";

    public static final String PRIVILEGE_PARAM = "{" + PRIVILEGE + "}";

    @Autowired
    private EnterpriseService service;

    @Autowired
    UserService userService;

    @Autowired
    SecurityService securityService;

    @GET
    public PrivilegeDto getPrivilege(@PathParam(PRIVILEGE) final Integer privilegeId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        if (!securityService.hasPrivilege(Privileges.USERS_VIEW_PRIVILEGES)
            && !securityService.hasPrivilege(Privileges.USERS_VIEW))
        {
            User currentUser = userService.getCurrentUser();
            if (currentUser.getRole().getPrivileges() != null)
            {
                for (Privilege p : currentUser.getRole().getPrivileges())
                {
                    if (p.getId().equals(privilegeId))
                    {
                        Privilege privilege = service.getPrivilege(privilegeId);
                        return createTransferObject(privilege, restBuilder);
                    }
                }
            }
            else
            {
                // throws access denied exception
                securityService.requirePrivilege(Privileges.USERS_VIEW_PRIVILEGES);
            }

        }

        Privilege privilege = service.getPrivilege(privilegeId);

        return createTransferObject(privilege, restBuilder);
    }

    public static PrivilegeDto createTransferObject(final Privilege systemProperty,
        final IRESTBuilder builder) throws Exception
    {
        PrivilegeDto dto = new PrivilegeDto();
        dto.setName(systemProperty.getName());
        dto.setId(systemProperty.getId());

        dto.setLinks(builder.buildPrivilegeLink(dto));
        return dto;
    }

}
