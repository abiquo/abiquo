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
        "Appliance Manager is not well configured. Check the repository filesystem"), //
    OVF_INVALID("OVF-INVALID", "Invalid OVF document"), //
    OVF_NOT_FOUND("OVF-NOT-FOUND", "OVF document not found in the repository"), //
    OVF_MALFORMED("OVF-MALFORMED", "OVF document can not be read"), //
    OVF_INSTALL("OVF-INSTALL", "Can't create the ovf package folder"), //
    OVF_DOWNLOAD("OVF-DOWNLOAD", "Can't download the ovf package"), //
    OVF_CANCEL("OVF-CANCEL", "Can't cancel the ovf package install"), //
    REPO_NO_SPACE("REPO-NO-SPACE", "No space left in the repository file system"), //
    OVF_BOUNDLE("OVF-BOUNDLE", "Can't create the instance"), //
    OVF_UPLOAD("OVF-UPLOAD", "Can't upload the ovf package"), //
    REPO_NOT_ACCESSIBLE("REPO-NOT-ACCESSIBLE",
        "Repository is not accessible. Check the exported location (propably NFS is stopped)"), //
    REPO_NOT_WRITABLE("REPO-NOT-WRITABLE", "Repository is read only. Check the mount parameters."), //
    REPO_TIMEOUT_REFRESH("REPO-TIMEOUT-REFRESH",
        "Timeout during repository file system synchronization."), //
    OVFPI_DELETE("OVFPI-DELETE", "Can't delete ovf package content"), //
    OVFPI_DELETE_INSTANCES("OVFPI-DELETE", "Can't delete ovf package content. It have instances"), //
    OVFPI_SNAPSHOT_ALREADY_EXIST("OVF-SNAPSHOT-ALREADY-EXIST",
        "Can't create snapshot, other with the same name exist"), //
    OVFPI_SNAPSHOT_CREATE("OVF-SNAPSHOT-CREATE", "Can't create snapshot, invalid ovf xml document"), //
    OVFPI_UNKNOW_STATUS("OVF-UNKNOW-STATUS", "Can't obtain the ovf package instance status"), //
    OVFPI_CHANGE_STATUS(
        "OVF-CHANGE-STATUS",
        "Can't update the ovf package instance status. Can't create the file mark in the repository file system"), //
    DISK_FILE_MOVE("DISK-FILE-MOVE", "Disk failed to move into the repository"), //
    OVFPI_SNAPSHOT_IMPORT_NOT_EXIST(
        "OVF-SNAPSHOT-IMPORT-NOT-EXIST",
        "Imported snapshot not found. Should be a bundle of an imported virtual machine or isn't a directory"), //
    DISK_FILE_NOT_FOUND("DISK-NOT-FOUND", "Requested disk doesn't exist in the current repository"), //
    DISK_FILE_COPY_ERROR("DISK-FILE-COPY", "Some error during disk file copy"), //
    DISK_FILE_ALREADY_EXIST("DISK-ALREADY-EXIST",
        "The destination path of the copy already exist in the current repository");

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
