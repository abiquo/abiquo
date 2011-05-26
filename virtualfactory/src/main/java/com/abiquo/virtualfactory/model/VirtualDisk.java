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

package com.abiquo.virtualfactory.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.ovfmanager.ovf.section.DiskFormat;

/**
 * This class represents a virtual disk.
 * 
 * @author pnavarro
 */
public class VirtualDisk
{

    /** The Constant logger. */
    private final static Logger logger = LoggerFactory.getLogger(VirtualDisk.class);

    /** The external disk attachment sequence (determine where the disk is mapped) */
    private int sequence;

    /** Absolute path location. */
    private String location;

    /** Virtual disk Id. */
    private String id;

    /** Virtual Disk capacity in bytes. */
    private long capacity;

    /** Virtual Disk Type. */
    private VirtualDiskType diskType;

    /** The repository. */
    private String repository;

    /** The image path. */
    private String imagePath;

    /** The target datastore where the disk deployed is stored */
    private String targetDatastore;

    /** The file reference **/
    private String fileRef;

    /** The image format **/
    private String format;

    /**
     * In case of HA create/delete operation a new custom parameter is set on the Disk Element to
     * indicate do not execute any operation to copy/remove the disk from the target datastore.
     */
    private boolean isha;

    /**
     * Instantiates a new virtual disk.
     */
    public VirtualDisk()
    {
        this.diskType = VirtualDiskType.STANDARD;
    }

    /**
     * Instantiates a new virtual disk.
     * 
     * @param id the id
     * @param location the location
     * @param capacity the capacity
     * @param targetDatastore the target datastore
     */
    public VirtualDisk(String id, String location, long capacity, String targetDatastore,
        String fileRef, String format)
    {
        // TODO check not null
        this.id = id;
        this.location = location;
        this.capacity = capacity;
        this.diskType = VirtualDiskType.STANDARD;
        decodeRepositoryAndPathFromLocation();
        this.targetDatastore = targetDatastore;
        this.fileRef = fileRef;
        this.format = convertDiskFormat(format);
    }

    /**
     * Instantiates a new virtual disk.
     * 
     * @param id the id
     * @param location the location
     * @param capacity the capacity
     * @param targetDatastore the target datastore
     * @param type, the disk type (standard or iSCSI)
     */
    public VirtualDisk(String id, String location, long capacity, VirtualDiskType type,
        String targetDatastore, String fileRef, String format)
    {
        // TODO check not null
        this.id = id;
        this.location = location;
        this.capacity = capacity;
        this.diskType = type;
        this.targetDatastore = targetDatastore;
        this.fileRef = fileRef;
        this.format = convertDiskFormat(format);
    }

    public int getSequence()
    {
        return sequence;
    }

    public void setSequence(int sequence)
    {
        this.sequence = sequence;
    }

    /**
     * Gets the virtual disk location.
     * 
     * @return the location
     */
    public String getLocation()
    {
        return location;
    }

    /**
     * Sets the virtual disk location.
     * 
     * @param location a virtual disk file path
     */
    public void setLocation(String location)
    {
        this.location = location;

        decodeRepositoryAndPathFromLocation();

    }

    /**
     * Gets the id.
     * 
     * @return the id
     */
    public String getId()
    {
        return id;
    }

    /**
     * Sets the Id.
     * 
     * @param id the id
     */
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * Ses the capacity of the virtual disk in bytes.
     * 
     * @param capacity the capacity to set
     */
    public void setCapacity(long capacity)
    {
        this.capacity = capacity;
    }

    /**
     * Gets the capacity of the virtual disk in bytes.
     * 
     * @return the capacity
     */
    public long getCapacity()
    {
        return capacity;
    }

    /**
     * Decode repository and path from location.
     */
    private void decodeRepositoryAndPathFromLocation()
    {
        if (this.diskType.compareTo(VirtualDiskType.STANDARD) == 0)
        {
            logger.debug("Disk location at [{}]", location);

            int indexFinRepository = location.indexOf("]");

            repository = location.substring(1, indexFinRepository);
            imagePath = location.substring(indexFinRepository + 1, location.length());

            int indexRepositoryPath = location.indexOf(":"); // This is not valid for imported Hyper-V machines : repository == "null" in this case

            if (indexRepositoryPath != -1 && repository.length() > indexRepositoryPath) // FIXES call when using an imported VMachine -> Windows unit i.e. C:\ is detected
            {
                repository = repository.substring(indexRepositoryPath, repository.length());
                logger.debug("Using imagePath [{}] at repository [{}]", imagePath, repository);
            }
            else
            {
                // imported images
                repository = null;
                logger.debug("Using imagePath [{}]", imagePath);
            }

        }
    }

    /**
     * Gets the repository.
     * 
     * @return the repository
     */
    public String getRepository()
    {
        if (!repository.endsWith("/"))
        {
            repository += "/";
        }
        return repository;
    }

    /**
     * Sets the repository.
     * 
     * @param repository the new repository
     */
    public void setRepository(String repository)
    {
        this.repository = repository;
    }

    /**
     * Gets the image path.
     * 
     * @return the image path
     */
    public String getImagePath()
    {
        return imagePath;
    }

    /**
     * Sets the image path.
     * 
     * @param imagePath the new image path
     */
    public void setImagePath(String imagePath)
    {
        this.imagePath = imagePath;
    }

    /**
     * Gets the disk Type
     * 
     * @return the diskType
     */
    public VirtualDiskType getDiskType()
    {
        return diskType;
    }

    /**
     * Sets the diskType
     * 
     * @param diskType the diskType to set
     */
    public void setDiskType(VirtualDiskType diskType)
    {
        this.diskType = diskType;
    }

    /**
     * Sets the datastore
     * 
     * @param targetDatastore the targetDatastore to set
     */
    public void setTargetDatastore(String targetDatastore)
    {
        this.targetDatastore = targetDatastore;
    }

    /**
     * Gets the datastore
     * 
     * @return the targetDatastore
     */
    public String getTargetDatastore()
    {
        return targetDatastore;
    }

    public String getFileRef()
    {
        return fileRef;
    }

    public void setFileRef(String fileRef)
    {
        this.fileRef = fileRef;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        boolean equal = true;

        if (obj instanceof VirtualDisk)
        {
            VirtualDisk vd = (VirtualDisk) obj;

            equal = equal && (diskType.value().equals(vd.getDiskType().value()));
            equal = equal && (location.equalsIgnoreCase(vd.getLocation()));
        }
        else
        {
            equal = false;
        }

        return equal;
    }

    public void setFormat(String format)
    {
        this.format = format;
    }

    public String getFormat()
    {
        return format;
    }

    /**
     * Converts DiskFormat in the OVF to the format description required by Libvirt Libvirt
     * Supported formats (from quemu-img man) Supported formats: cow qcow vmdk cloop dmg bochs vpc
     * vvfat qcow2 parallels nbd host_cdrom host_floppy host_device raw tftp ftps ftp https http
     * 
     * @param diskFormatUri
     * @return
     */
    protected String convertDiskFormat(String diskFormatUri)
    {
        String convertedFormat = new String();
        switch (DiskFormat.fromValue(diskFormatUri))
        {

            case UNKNOWN:
                convertedFormat = "raw";
                break;

            case INCOMPATIBLE:
                convertedFormat = "raw";
                break;

            case VMDK_STREAM_OPTIMIZED: // KVM uses VMDK_FLAT conversion, so qemu should expect a
                // "raw" disk
                convertedFormat = "raw";
                break;

            case VMDK_FLAT:
                convertedFormat = "raw";
                break;

            case VMDK_SPARSE:
                convertedFormat = "vmdk";
                break;

            case VDI_FLAT:
                convertedFormat = "raw";
                break;

            case VDI_SPARSE:
                convertedFormat = "raw";
                break;

            case QCOW2_FLAT:
                convertedFormat = "qcow2";
                break;

            case QCOW2_SPARSE:
                convertedFormat = "qcow2";
                break;

            default:
                convertedFormat = "raw";
                break;

        }
        logger.debug("Using Disk Type (diskFormatUri) [{}] : qemu should use [{}] format.",
            diskFormatUri, convertedFormat);
        return convertedFormat;

    }

    public void setHa()
    {
        isha = true;
    }

    public boolean isHa()
    {
        return isha;
    }
}
