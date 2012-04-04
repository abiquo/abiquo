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
import org.springframework.security.AccessDeniedException;

import com.abiquo.api.resources.EnterprisesResource;

/**
 * Request handler to check permissions of the logged user to use the requested virtual datacenter.
 * This means that all request to uris who depend on "cloud/virtualdatacenters/{id}" will be checked
 * by this handler.
 * 
 * @author scastro
 */
public class AdminEnterpriseSecurityRequestHandler extends SecurityPathRequestHandler
{

    /**
     * in this case must be a <code>\w</code> and not a <code>\d</code>
     */
    private static String ENTERPRISE_ID_REGEX = EnterprisesResource.ENTERPRISES_PATH + "/(\\w+)";

    private static String ENTERPRISES_PATH_REGEX = ENTERPRISE_ID_REGEX + "[/]?.*$";

    @Override
    public boolean matches(final String path)
    {
        return path.matches(ENTERPRISES_PATH_REGEX);
    }

    @Override
    public void handleRequest(final MessageContext context, final HandlersChain chain)
        throws Throwable
    {
        // check if path maches with 'admin/enterprises/{id}*'
        String path = context.getUriInfo().getPath();
        // 1. get user from context [userName, authType, privileges list]
        Object[] userprorps = getCurrentLoginInfo();

        // 3. get enterprise id from path
        Pattern p = Pattern.compile(ENTERPRISE_ID_REGEX);
        Matcher m = p.matcher(path);
        // matcher ALLWAYS must find the enterprise id in the second group (remember that group 0 is
        // the original string)
        m.find();
        String gr = m.group(1);
        if (!gr.equals("_"))
        {
            Integer idEnt = new Integer(gr);

            boolean isAllowed =
                getUserService().isUserAllowedToEnterprise((String) userprorps[0],
                    (String) userprorps[1], (String[]) userprorps[2], idEnt);

            if (!isAllowed)
            {
                // throw forbidden if is not allowed
                throw new AccessDeniedException("Missing privilege to get info from other enterprises");
            }
        }

        // finally
        chain.doChain(context);
    }
}
