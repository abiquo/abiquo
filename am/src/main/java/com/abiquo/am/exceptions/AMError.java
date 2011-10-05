package com.abiquo.am.exceptions;

import java.util.Arrays;
import java.util.Comparator;

public enum AMError
{

    // STATUSCODES
    STATUS_BAD_REQUEST("400-BAD REQUEST", "Request not valid"), STATUS_UNAUTHORIZED(
        "401-UNAUTHORIZED", "This requests requires user authentication"), STATUS_FORBIDDEN(
        "403-FORBIDDEN", "Access is denied"), STATUS_NOT_FOUND("404-NOT FOUND",
        "The Resource requested does not exist"), STATUS_METHOD_NOT_ALLOWED(
        "405-METHOD NOT ALLOWED", "The resource doesn't expose this method"), STATUS_CONFLICT(
        "409-CONFLICT", "Conflict"), STATUS_UNSUPPORTED_MEDIA_TYPE("415-UNSUPPORTED MEDIA TYPE",
        "Abiquo API currently only supports application/xml Media Type"), STATUS_INTERNAL_SERVER_ERROR(
        "500-INTERNAL SERVER ERROR", "Unexpected exception"), STATUS_UNPROVISIONED(
        "412 - Unprovisioned", "Unprovisioned exception"),

    REPO_NOT_ACCESSIBLE("REPO-NOT-ACCESSIBLE",
        "Repository is not accessible. Check the exported location (propably NFS is stopped)"), REPO_NOT_WRITABLE(
        "REPO-NOT-WRITABLE", "Repository is read only. Check the mount parameters."),

    REPO_TIMEOUT_REFRESH("REPO-TIMEOUT-REFRESH",
        "Timeout during repository file system synchronization."),
        
        OVFPI_DELETE("OVFPI-DELETE", "Can't delete ovf package content"),
        OVFPI_DELETE_INSTANCES("OVFPI-DELETE", "Can't delete ovf package content. It have instances"),
        
        OVFPI_SNAPSHOT_NOT_EXIST("OVF-SNAPSHOT-NOT-EXIST","Snapshot not found. Should be a bundle of an imported virtual machine."),

    DISK_FILE_NOT_FOUND("DISK-NOT-FOUND", "Requested disk doesn't exist in the current repository"), DISK_FILE_COPY_ERROR(
        "DISK-FILE-COPY", "Some error during disk file copy"), DISK_FILE_ALREADY_EXIST(
        "DISK-ALREADY-EXIST",
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
