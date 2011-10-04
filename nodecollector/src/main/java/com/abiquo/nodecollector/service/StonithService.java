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

package com.abiquo.nodecollector.service;

/**
 * This class exposes the business logic of the stonith service.
 * 
 * @author eruiz@abiquo.com
 */
public interface StonithService
{
    /**
     * Changes power state of chassis to power off.
     * 
     * @param host Remote host name for LAN interface
     * @param port Remote RMCP port [default=623]
     * @param user Remote session username
     * @param password Remote session password
     * @return True if the chassis are succesfully down. Otherwise false.
     */
    public boolean shootTheOtherNodeInTheHead(final String host, final Integer port,
        final String user, final String password);

    /**
     * Checks if the stonith service is available.
     * 
     * @param host Remote host name for LAN interface
     * @param port Remote RMCP port [default=623]
     * @param user Remote session username
     * @param password Remote session password
     * @return True if the ipmitool is working. Otherwise false.
     */
    public boolean isStonithUp(final String host, final Integer port, final String user,
        final String password);
}
