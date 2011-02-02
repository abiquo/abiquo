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

package com.abiquo.abiserver.pojo.virtualimage;

import java.util.Date;

import com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.VirtualImageConversionsHB;
import com.abiquo.abiserver.pojo.IPojo;

public class VirtualImageConversions implements IPojo<VirtualImageConversionsHB>
{
    private int id;

    private VirtualImage image;

    private DiskFormatType diskSourceFormatType;

    private DiskFormatType diskTargetFormatType;

    private Date timestamp;

    private String sourcePath;

    private String targetPath;

    private Long size;

    private StateConversion state;

    public VirtualImageConversions()
    {
        id = 0;
        sourcePath = "";
        targetPath = "";
    }

    public VirtualImageConversionsHB toPojoHB()
    {
        VirtualImageConversionsHB imageConversionHB = new VirtualImageConversionsHB();

        imageConversionHB.setId(id);
        imageConversionHB.setTimestamp(timestamp);
        imageConversionHB.setState(state.toEnum());
        imageConversionHB.setImage(image.toPojoHB());
        if (diskSourceFormatType != null)
        {
            imageConversionHB.setSourceType(diskSourceFormatType.toEnum());
        }
        imageConversionHB.setTargetType(diskTargetFormatType.toEnum());
        if (sourcePath != null)
        {
            imageConversionHB.setSourcePath(sourcePath);
        }
        imageConversionHB.setTargetPath(targetPath);
        imageConversionHB.setSize(size);

        return imageConversionHB;
    }

    /**
     * @return the id
     */
    public int getId()
    {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(final int id)
    {
        this.id = id;
    }

    /**
     * @return the dateTime
     */
    public Date getTimestamp()
    {
        return timestamp;
    }

    /**
     * @param dateTime the dateTime to set
     */
    public void setTimestamp(final Date timestamp)
    {
        this.timestamp = timestamp;
    }

    /**
     * @return the state
     */
    public StateConversion getState()
    {
        return state;
    }

    /**
     * @param state the state to set
     */
    public void setState(final StateConversion state)
    {
        this.state = state;
    }

    /**
     * @return the image
     */
    public VirtualImage getImage()
    {
        return image;
    }

    /**
     * @param image the image to set
     */
    public void setImage(final VirtualImage image)
    {
        this.image = image;
    }

    /**
     * @return the diskSourceFormatType
     */
    public DiskFormatType getDiskSourceFormatType()
    {
        return diskSourceFormatType;
    }

    /**
     * @param diskSourceFormatType the diskSourceFormatType to set
     */
    public void setDiskSourceFormatType(final DiskFormatType diskSourceFormatType)
    {
        this.diskSourceFormatType = diskSourceFormatType;
    }

    /**
     * @return the diskTargetFormatType
     */
    public DiskFormatType getDiskTargetFormatType()
    {
        return diskTargetFormatType;
    }

    /**
     * @param diskTargetFormatType the diskTargetFormatType to set
     */
    public void setDiskTargetFormatType(final DiskFormatType diskTargetFormatType)
    {
        this.diskTargetFormatType = diskTargetFormatType;
    }

    /**
     * @return the sourcePath
     */
    public String getSourcePath()
    {
        return sourcePath;
    }

    /**
     * @param sourcePath the sourcePath to set
     */
    public void setSourcePath(final String sourcePath)
    {
        this.sourcePath = sourcePath;
    }

    /**
     * @return the targetPath
     */
    public String getTargetPath()
    {
        return targetPath;
    }

    /**
     * @param targetPath the targetPath to set
     */
    public void setTargetPath(final String targetPath)
    {
        this.targetPath = targetPath;
    }

    /**
     * @return the size
     */
    public Long getSize()
    {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(final Long size)
    {
        this.size = size;
    }
}
