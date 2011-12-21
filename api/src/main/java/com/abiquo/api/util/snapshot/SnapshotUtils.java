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
