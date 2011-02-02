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

package com.abiquo.server.core.infrastructure.nodecollector;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Basic values to define a disk.
 * 
 * @author ibarrera
 */
@XmlRootElement(name = "disk")
public class DiskDto implements Serializable
{
    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** The amount of memory of the disk. */
    private Long hd;

    /** The path to the image which contains the disk. */
    private String imagePath;

    /** The kind of image used. */
    private String diskType;

    /** The format of image used. */
    private String diskFormatType;

    /**
     * Gets the hd.
     * 
     * @return the hd
     */
    @XmlElement(name = "hdValue")
    public Long getHd()
    {
        return hd;
    }

    /**
     * Sets the hd.
     * 
     * @param hd the hd to set
     */
    public void setHd(Long hd)
    {
        this.hd = hd;
    }

    /**
     * Gets the imagePath.
     * 
     * @return the imagePath
     */
    public String getImagePath()
    {
        return imagePath;
    }

    /**
     * Sets the imagePath.
     * 
     * @param imagePath the imagePath to set
     */
    public void setImagePath(String imagePath)
    {
        this.imagePath = imagePath;
    }

    /**
     * Gets the diskType.
     * 
     * @return the diskType
     */
    public String getDiskType()
    {
        return diskType;
    }

    /**
     * Sets the diskType.
     * 
     * @param diskType the diskType to set
     */
    public void setDiskType(String diskType)
    {
        this.diskType = diskType;
    }

    /**
     * Gets the diskFormatType.
     * 
     * @return the diskFormatType
     */
    public String getDiskFormatType()
    {
        return diskFormatType;
    }

    /**
     * Sets the diskFormatType.
     * 
     * @param diskFormatType the diskFormatType to set
     */
    public void setDiskFormatType(String diskFormatType)
    {
        this.diskFormatType = diskFormatType;
    }

}
