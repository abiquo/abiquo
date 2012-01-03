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
package com.abiquo.am.services;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Provides functionallity to manage Disk Files.
 * 
 * @author ibarrera
 */
public interface DiskFileService
{
    /**
     * Gets the path of a disk file in the repository.
     * 
     * @param path The path of the disk file to obtain.
     * @return The path of the requested disk file.
     * @throws FileNotFoundException If disk file does not exist.
     */
    public String get(String path);

    /**
     * Performs a disk file copy.
     * 
     * @param source The source disk file.
     * @param destination The destination disk file.
     * @throws FileNotFoundException If the disk file to be copied does not exist.
     * @throws IOException If the copy operation cannot be performed.
     */
    public void copy(String source, String destination);
}
