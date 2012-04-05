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

package com.abiquo.api.handlers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.wink.server.handlers.HandlersChain;
import org.apache.wink.server.handlers.MessageContext;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.exceptions.NotFoundException;
import com.abiquo.api.resources.cloud.VirtualDatacentersResource;

/**
 * Request handler to check permissions of the logged user to use the requested virtual datacenter.
 * This means that all request to uris who depend on "cloud/virtualdatacenters/{id}" will be checked
 * by this handler.
 * 
 * @author scastro
 */
public class CloudEnterpriseSecurityRequestHandler extends SecurityPathRequestHandler
{

    private static String VIRTUAL_DATACENTER_ID_REGEX =
        VirtualDatacentersResource.VIRTUAL_DATACENTERS_PATH + "/(\\d+)";

    private static String VIRTUAL_DATACENTER_PATH_REGEX = VIRTUAL_DATACENTER_ID_REGEX + "[/]?.*$";

    @Override
    public boolean matches(final String path)
    {
        return path.matches(VIRTUAL_DATACENTER_PATH_REGEX);
    }

    @Override
    public void handleRequest(final MessageContext context, final HandlersChain chain)
        throws Throwable
    {
        // check if path maches with 'cloud/virtualdatacenter/{id}*'
        String path = context.getUriInfo().getPath();
        // 1. get user from context [userName, authType, privileges list]
        Object[] userprorps = getCurrentLoginInfo();

        // 3. get virtualdatacenter id from path
        Pattern p = Pattern.compile(VIRTUAL_DATACENTER_ID_REGEX);
        Matcher m = p.matcher(path);
        // matcher ALLWAYS must find the vdc id in the second group (remember that group 0 is
        // the original string)
        m.find();
        Integer idVdc = new Integer(m.group(1));

        boolean isAllowed =
            getUserService().isUserAllowedToUseVirtualDatacenter((String) userprorps[0],
                (String) userprorps[1], (String[]) userprorps[2], idVdc);

        if (!isAllowed)
        {
            // throw not found if is not allowed
            throw new NotFoundException(APIError.NON_EXISTENT_VIRTUAL_DATACENTER);
        }

        // finally
        chain.doChain(context);
    }

}
