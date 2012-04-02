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

package com.abiquo.am.exceptions;

import java.util.Arrays;
import java.util.Comparator;

public enum AMError
{

    AM_CHECK("AM-CHECK",
        "Appliance Manager is not properly configured. Check the repository file system"), //
    AM_CHECK_REDIS("AM-CHECK-REDIS", "No connection to the Redis Server"), //
    AM_NOTIFICATION("AM-NOTIFICATION",
        "Cannot update Template status because cannot publish download status message in RabbitMQ"), //
    TEMPLATE_INVALID("TEMPLATE-INVALID", "Invalid OVF Document"), //
    TEMPLATE_INVALID_LOCATION("TEMPLATE-INVALID-LOC", "Invalid OVF URL"), //
    TEMPLATE_INVALID_MULTIPLE_DISKS("TEMPLATE-INVALID-MULTIPLE-DISK",
        "Invalid number of disks. The OVF document can only contain ONE referenced disk."), TEMPLATE_INVALID_MULTIPLE_FILES(
        "TEMPLATE-INVALID-MULTIPLE-FILE",
        "Invalid number of referenced files. The OVF document can only contain ONE referenced file."), TEMPLATE_INVALID_DISK_REFERENCE(
        "TEMPLATE-INVALID-DISK-REFRENCE",
        "Virtual Hardware Section contains no reference to the disk."), TEMPLATE_NOT_FOUND(
        "TEMPLATE-NOT-FOUND", "OVF Document not found in the Template Repository"), //
    TEMPLATE_MALFORMED("TEMPLATE-MALFORMED", "OVF Document cannot be read"), //
    TEMPLATE_INSTALL("TEMPLATE-INSTALL",
        "Cannot create the template folder on the NFS Repository file system"), //
    TEMPLATE_INSTALL_ALREADY("TEMPLATE-INSTALL-ALREADY", "Template already exists"), //
    TEMPLATE_DOWNLOAD("TEMPLATE-DOWNLOAD", "Cannot download the template"), //
    TEMPLATE_CANCEL("TEMPLATE-CANCEL", "Cannot cancel the template download"), //
    REPO_NO_SPACE("REPO-NO-SPACE", "No space left on the NFS Repository file system"), //
    TEMPLATE_BOUNDLE("TEMPLATE-BOUNDLE", "Cannot create the template instance"), //
    TEMPLATE_UPLOAD("TEMPLATE-UPLOAD", "Cannot upload the template"), //
    REPO_NOT_ACCESSIBLE(
        "REPO-NOT-ACCESSIBLE",
        "NFS Repository may not be available or accessible to Abiquo Remote Services. Check the repositoryLocation in abiquo.properties."), //

    REPO_TIMEOUT_REFRESH("REPO-TIMEOUT-REFRESH",
        "Timeout during NFS Repository file system refresh."), //
    TEMPLATE_DELETE("OVFPI-DELETE", "Cannot delete template folder"), //
    TEMPLATE_DELETE_INSTANCES("OVFPI-DELETE",
        "Cannot delete template folder content because there are instances in the folder"), //
    TEMPLATE_SNAPSHOT_ALREADY_EXIST("TEMPLATE-SNAPSHOT-ALREADY-EXIST",
        "Cannot create template instance because another one with the same name already exists"), //
    TEMPLATE_SNAPSHOT_CREATE("TEMPLATE-SNAPSHOT-CREATE",
        "Cannot create template instance because the master OVF Document is invalid"), //
    TEMPLATE_UNKNOW_STATUS("TEMPLATE-UNKNOW-STATUS", "Cannot obtain the template status"), //
    TEMPLATE_CHANGE_STATUS("TEMPLATE-CHANGE-STATUS",
        "Cannot update template status because cannot create file marker on NFS Repository file system"), //
    DISK_FILE_MOVE("DISK-FILE-MOVE",
        "Failed to move disk from the XenServer folder to the NFS Repository for instance"), //
    TEMPLATE_SNAPSHOT_IMPORT_NOT_EXIST("TEMPLATE-SNAPSHOT-IMPORT-NOT-EXIST",
        "Imported template instance not found. Should be an instance of an imported virtual machine."), //
    DISK_FILE_NOT_FOUND("DISK-NOT-FOUND",
        "Template disk file requested for XenServer deployment does not exist in the current NFS Repository"), //
    DISK_FILE_COPY_ERROR("DISK-FILE-COPY",
        "An error occurred during copy of disk from NFS Repository to XenServer folder for deployment"), //
    DISK_FILE_ALREADY_EXIST(
        "DISK-ALREADY-EXIST",
        "The destination path of the copy already exists in the XenServer folder on the NFS Repository file system"), //

    MOUNT_FILE_NOT_FOUND("MOUNT-0", "Cannot find ''/etc/mtab'' to check mounted repositories."), //
    MOUNT_FILE_READ_ERROR("MOUNT-1", "Cannot read ''/etc/mtab'' to check mounted repositories."), //
    MOUNT_INVALID_REPOSITORY("MOUNT-2",
        "The ''abiquo.appliancemanager.repositoryLocation'' (NFS export location) "
            + "is not mounted at ''abiquo.appliancemanager.localRepositoryPath''"), //
    CONFIG_REPOSITORY_LOCATION("CONF-0",
        "Invalid ''abiquo.appliancemanager.repositoryLocation'' configuration element"), //
    CONFIG_REPOSITORY_PATH("CONF-1",
        "Invalid ''abiquo.appliancemanager.localRepositoryPath'' configuration element"), //
    CONFIG_REPOSITORY_MARK("CONF-2", "Repository is mounted with the expected mount point, "
        + "but the ''.abiquo_repository'' file marker is not found and cannot be created")//
    ;

    /**
     * Internal error code
     */
    String code;

    /**
     * Description message
     */
    String message;

    String cause;

    private AMError(final String code, final String message)
    {
        this.code = code;
        this.message = message;
    }

    public String getCode()
    {
        return String.valueOf(this.code);
    }

    public String getMessage()
    {
        return this.message;
    }

    public void addCause(final String cause)
    {
        this.cause = cause;
    }

    public static void main(final String[] args)
    {
        AMError[] errors = AMError.values();
        Arrays.sort(errors, new Comparator<AMError>()
        {
            @Override
            public int compare(final AMError err1, final AMError err2)
            {
                return String.CASE_INSENSITIVE_ORDER.compare(err1.code, err2.code);
            }

        });

        // Outputs all errors in wiki table format
        for (AMError error : errors)
        {
            System.out.println(String.format("| %s | %s | %s |", error.code, error.message,
                error.name()));
        }
    }

}
