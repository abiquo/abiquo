package com.abiquo.api.util.snapshot;

import java.util.UUID;

import org.apache.commons.io.FilenameUtils;

import com.abiquo.model.enumerator.DiskFormatType;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplate;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.VirtualMachineState;

public class SnapshotUtils
{
    public enum SnapshotType
    {
        /**
         * The {@link VirtualMachine} deployed disk has the same {@link DiskFormatType} than master
         * {@link VirtualMachineTemplate}.
         */
        FROM_ORIGINAL_DISK,

        /**
         * The {@link VirtualMachine} deployed disk is a conversion, then, the deployed disk has a
         * distinct {@link DiskFormatType} than master {@link VirtualMachineTemplate}.
         */
        FROM_DISK_CONVERSION,

        /**
         * The {@link VirtualMachine} deployed is not managed by abiquo.
         */
        FROM_NOT_MANAGED_VIRTUALMACHINE,

        /**
         * The {@link VirtualMachine} is a stateful one.
         */
        FROM_STATEFUL_DISK;

        /**
         * Returns the {@link SnapshotType} to do for a concrete {@link VirtualMachine}.
         * 
         * @param virtualMachine The {@link VirtualMachine} instance
         * @return The {@link SnapshotType} to do.
         */
        public static SnapshotType getSnapshotType(VirtualMachine virtualMachine)
        {
            if (!virtualMachine.isManaged())
            {
                return SnapshotType.FROM_NOT_MANAGED_VIRTUALMACHINE;
            }
            else if (virtualMachine.getVirtualImageConversion() != null)
            {
                return SnapshotType.FROM_DISK_CONVERSION;
            }
            else if (virtualMachine.isStateful())
            {
                return FROM_STATEFUL_DISK;
            }
            else
            {
                return SnapshotType.FROM_ORIGINAL_DISK;
            }
        }
    };

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

    /**
     * Indicates if a {@link VirtualMachine} must be powered off before snapshot.
     * 
     * @param virtualMachineState The actual {@link VirtualMachineState} of the
     *            {@link VirtualMachine}
     * @return True if must be powered off. Otherwise false;
     */
    public static boolean mustPowerOffToSnapshot(final VirtualMachineState virtualMachineState)
    {
        return virtualMachineState == VirtualMachineState.ON
            || virtualMachineState == VirtualMachineState.PAUSED;
    }
}
