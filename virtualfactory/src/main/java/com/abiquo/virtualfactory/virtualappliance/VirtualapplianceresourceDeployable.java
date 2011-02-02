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

package com.abiquo.virtualfactory.virtualappliance;

import com.sun.ws.management.InternalErrorFault;
import com.sun.ws.management.Management;

/**
 * This interface declares all the methods of the virtualfactory needed to deploy remote virtual
 * applications. It works as a facade of the project, a layer between the service contract and the
 * virtualfactory's implementation
 * 
 * @author jdevesa@abiquo.com
 */
public interface VirtualapplianceresourceDeployable
{
    /**
     * Create a virtual application
     * 
     * @param request the management created request
     * @param response the generated referenced response
     * @throws InternalErrorFault if any problem occurs
     */
    public void create(Management request, Management response) throws InternalErrorFault;

    /**
     * Delete a virtual application
     * 
     * @param request the management request
     * @param response the generated referenced response
     * @throws InternalErrorFault if any problem occurs
     */
    public void delete(Management request, Management response) throws InternalErrorFault;

    /**
     * Get the virtual application
     * 
     * @param request the request
     * @param response the generated response
     * @throws InternalErrorFault if any problem occurs
     */
    public void get(Management request, Management response) throws InternalErrorFault;

    /**
     * Put the virtual application
     * 
     * @param request the request
     * @param response the generated response
     * @throws InternalErrorFault if any problem occurs
     */
    public void put(Management request, Management response) throws InternalErrorFault;

    /**
     * Checks the health of a virtual system
     * 
     * @param request
     * @param response
     */
    public void checkVirtualSystem(Management request, Management response);

    /**
     * Adds a virtual System
     * 
     * @param request
     * @param response
     */
    public void addVirtualSystem(Management request, Management response);

    /**
     * Removes a virtual system
     * 
     * @param request
     * @param response
     */
    public void removeVirtualSystem(Management request, Management response);

    /**
     * Bundles a virtual appliance
     * 
     * @param request
     * @param response
     */
    public void bundleVirtualAppliance(Management request, Management response);
}
