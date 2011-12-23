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

package com.abiquo.api.util.snapshot;

import java.util.UUID;

import org.apache.commons.io.FilenameUtils;

import com.abiquo.server.core.appslibrary.VirtualMachineTemplate;

public class SnapshotUtils
{
    /**
     * Returns the destination path where a snapshot of a certain {@link VirtualMachineTemplate}
     * must be stored.
     * 
     * @param template The {@link VirtualMachineTemplate} to consider
     * @return The destination path
     */
    public static String formatSnapshotPath(final VirtualMachineTemplate template)
    {
        String filename = template.getPath();

        if (!template.isMaster())
        {
            filename = template.getMaster().getPath();
        }

        return FilenameUtils.getFullPath(filename);
    }

    /**
     * Generates a snapshot filename of a certain {@link VirtualMachineTemplate}.
     * 
     * @param template The {@link VirtualMachineTemplate} to consider
     * @return The snapshot filename
     */
    public static String formatSnapshotName(final VirtualMachineTemplate template)
    {
        String name = FilenameUtils.getName(template.getPath());

        if (!template.isMaster())
        {
            name = FilenameUtils.getName(template.getMaster().getPath());
        }

        return String.format("%s-snapshot-%s", UUID.randomUUID().toString(), name);
    }
}
