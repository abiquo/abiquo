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
package com.abiquo.am.resources;

import com.abiquo.appliancemanager.diskfile.DiskFile;


/**
 * Resource representation of the Disk Image files.
 * 
 * @author ibarrera
 */
public interface DiskFileResource
{

    /**
     * Gets the specified Disk File.
     * 
     * @param srcPath The path of the Disk File to get.
     * @return The Disk File.
     */
    public DiskFile getDiskFile(final String srcPath);


    /**
     * Creates a copy of the specified Disk File.
     * 
     * @param diskfile The configuration of the target Disk File.
     * @param src The path of the source Disk File.
     * @return The copied Disk File.
     */
    public DiskFile copyDiskFile(final DiskFile diskfile, final String src);

}
