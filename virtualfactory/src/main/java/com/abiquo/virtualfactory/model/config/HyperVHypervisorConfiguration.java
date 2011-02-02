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
package com.abiquo.virtualfactory.model.config;

/**
 * Configuration class for HyperV monitors.
 * 
 * @author ibarrera
 */
public class HyperVHypervisorConfiguration
{
    /**
     * destination repository path for not managed bundles
     */
    private String destinationRepositoryPath;

    /**
     * Gets the destination repository path
     * 
     * @return the destination repository path
     */
    public String getDestinationRepositoryPath()
    {
        return destinationRepositoryPath;
    }

    /**
     * Sets the destination repository path
     * 
     * @param destinationRepositoryPath
     */
    public void setDestinationRepositoryPath(final String destinationRepositoryPath)
    {
        this.destinationRepositoryPath = destinationRepositoryPath;
    }

}
