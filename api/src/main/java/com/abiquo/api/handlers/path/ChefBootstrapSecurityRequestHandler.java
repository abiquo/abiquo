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

package com.abiquo.api.handlers.path;

import org.springframework.security.context.SecurityContextHolder;

import com.abiquo.api.resources.cloud.VirtualAppliancesResource;
import com.abiquo.api.resources.cloud.VirtualDatacentersResource;
import com.abiquo.api.resources.cloud.VirtualMachineResource;
import com.abiquo.api.resources.cloud.VirtualMachinesResource;
import com.abiquo.api.spring.security.onetimetoken.token.OneTimeTokenToken;

/**
 * Allows access to the virtual machine bootstrap configuration URI when the authentication has been
 * made using the one time token.
 * <p>
 * Since it is a particular authentication, we should avoid delegating to the
 * {@link CloudEnterpriseSecurityRequestHandler} because it will deny access to the resource.
 * 
 * @author Ignasi Barrera
 */
public class ChefBootstrapSecurityRequestHandler extends AbstractSecurityPathHandler
{
    private static String CHEF_BOOTSTRAP_PATH_REGEXP = pathPattern(
        VirtualDatacentersResource.VIRTUAL_DATACENTERS_PATH,
        VirtualAppliancesResource.VIRTUAL_APPLIANCES_PATH,
        VirtualMachinesResource.VIRTUAL_MACHINES_PATH,
        VirtualMachineResource.VIRTUAL_MACHINE_BOOTSTRAP_PATH);

    @Override
    public boolean appliesTo(final String path)
    {
        return path.matches(CHEF_BOOTSTRAP_PATH_REGEXP) && isOneTimeAuthentication();
    }

    /**
     * Check if the current request is authenticated using a one time token authentication.
     * 
     * @return Boolean indicating if the current request is authenticated using a one time token
     *         authentication.
     */
    private boolean isOneTimeAuthentication()
    {
        return SecurityContextHolder.getContext().getAuthentication() instanceof OneTimeTokenToken;
    }

    /**
     * Build a regular expression path pattern with the given path fragments.
     * 
     * @param fragments The path fragments.
     * @return The path pattern.
     */
    private static String pathPattern(final String... fragments)
    {
        StringBuffer sb = new StringBuffer("^");
        for (int i = 0; i < fragments.length; i++)
        {
            sb.append(fragments[i]);
            if (i < fragments.length - 1)
            {
                sb.append("/(\\d+)/");
            }
        }
        sb.append("$");
        return sb.toString();
    }

}
