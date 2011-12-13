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
    AM_CHECK_REDIS("AM-CHECK-REDIS", "No connection to the redis server"), //
    AM_NOTIFICATION("AM-NOTIFICATION", "Can not publish an outgoing status message"), //
    TEMPLATE_INVALID("TEMPLATE-INVALID", "Invalid OVF document"), //
    TEMPLATE_INVALID_LOCATION("TEMPLATE-INVALID-LOC", "Invalid OVF location"), //
    TEMPLATE_INVALID_MULTIPLE_FILES("TEMPLATE-INVALID-MULTIPLE-FILE",
        "OVF document contains more than a single Referenced File, one none."), TEMPLATE_NOT_FOUND(
        "TEMPLATE-NOT-FOUND", "OVF document not found in the repository"), //
    TEMPLATE_MALFORMED("TEMPLATE-MALFORMED", "OVF document can not be read"), //
    TEMPLATE_INSTALL("TEMPLATE-INSTALL", "Can't create the ovf package folder"), //
    TEMPLATE_INSTALL_ALREADY("TEMPLATE-INSTALL-ALREADY", "OVF already exist"), //
    TEMPLATE_DOWNLOAD("TEMPLATE-DOWNLOAD", "Can't download the ovf package"), //
    TEMPLATE_CANCEL("TEMPLATE-CANCEL", "Can't cancel the ovf package install"), //
    REPO_NO_SPACE("REPO-NO-SPACE", "No space left in the repository file system"), //
    TEMPLATE_BOUNDLE("TEMPLATE-BOUNDLE", "Can't create the instance"), //
    TEMPLATE_UPLOAD("TEMPLATE-UPLOAD", "Can't upload the ovf package"), //
    REPO_NOT_ACCESSIBLE("REPO-NOT-ACCESSIBLE",
        "Repository is not accessible. Check the exported location (propably NFS is stopped), "
            + "or havent write permision"), //
    REPO_TIMEOUT_REFRESH("REPO-TIMEOUT-REFRESH",
        "Timeout during repository file system synchronization."), //
    TEMPLATE_DELETE("OVFPI-DELETE", "Can't delete ovf package content"), //
    TEMPLATE_DELETE_INSTANCES("OVFPI-DELETE", "Can't delete ovf package content. It have instances"), //
    TEMPLATE_SNAPSHOT_ALREADY_EXIST("TEMPLATE-SNAPSHOT-ALREADY-EXIST",
        "Can't create snapshot, other with the same name exist"), //
    TEMPLATE_SNAPSHOT_CREATE("TEMPLATE-SNAPSHOT-CREATE",
        "Can't create snapshot, invalid ovf xml document"), //
    TEMPLATE_UNKNOW_STATUS("TEMPLATE-UNKNOW-STATUS", "Can't obtain the ovf package instance status"), //
    TEMPLATE_CHANGE_STATUS(
        "TEMPLATE-CHANGE-STATUS",
        "Can't update the ovf package instance status. Can't create the file mark in the repository file system"), //
    DISK_FILE_MOVE("DISK-FILE-MOVE", "Disk failed to move into the repository"), //
    TEMPLATE_SNAPSHOT_IMPORT_NOT_EXIST(
        "TEMPLATE-SNAPSHOT-IMPORT-NOT-EXIST",
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
