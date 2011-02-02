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

package com.abiquo.abiserver.business.hibernate.pojohb.virtualimage;

import java.util.Date;

import com.abiquo.abiserver.business.hibernate.pojohb.IPojoHB;
import com.abiquo.abiserver.pojo.virtualimage.StateConversion;
import com.abiquo.abiserver.pojo.virtualimage.VirtualImageConversions;
import com.abiquo.server.core.enumerator.DiskFormatType;

public class VirtualImageConversionsHB implements java.io.Serializable,
    IPojoHB<VirtualImageConversions>
{
    private static final long serialVersionUID = -6775648600427932060L;

    private int id;

    private VirtualimageHB image;

    private Date timestamp;

    private String sourcePath;

    private String targetPath;

    private Long size;

    private StateConversionEnum state;

    private DiskFormatType sourceType;

    private DiskFormatType targetType;

    public VirtualImageConversionsHB()
    {
    }

    public VirtualImageConversionsHB(final VirtualimageHB image, final DiskFormatType sourceType,
        final DiskFormatType targetType, final StateConversionEnum state, final String sourcePath,
        final String targetPath)
    {
        this();
        this.setImage(image);
        this.setState(state);
        this.setSourcePath(sourcePath);
        this.setTargetPath(targetPath);
        this.setTimestamp(new Date());
        this.setSourceType(sourceType);
        this.setTargetType(targetType);
    }

    public VirtualImageConversions toPojo()
    {
        VirtualImageConversions imageConversion = new VirtualImageConversions();

        imageConversion.setId(id);
        imageConversion.setImage(image.toPojo());
        imageConversion.setTimestamp(timestamp);
        imageConversion.setState(new StateConversion(state));
        if (sourceType != null)
        {
            imageConversion
                .setDiskSourceFormatType(new com.abiquo.abiserver.pojo.virtualimage.DiskFormatType(sourceType));
        }
        imageConversion
            .setDiskTargetFormatType(new com.abiquo.abiserver.pojo.virtualimage.DiskFormatType(targetType));
        if (sourcePath != null)
        {
            imageConversion.setSourcePath(sourcePath);
        }
        imageConversion.setTargetPath(targetPath);
        imageConversion.setSize(size);

        return imageConversion;
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

    public VirtualimageHB getImageDecorated()
    {
        return image.toDecorator().toPojoHB();
    }

    /**
     * @return the image
     */
    public VirtualimageHB getImage()
    {
        return image;
    }

    /**
     * @param image the image to set
     */
    public void setImage(final VirtualimageHB image)
    {
        this.image = image;
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
    public StateConversionEnum getState()
    {
        return state;
    }

    /**
     * @param state the state to set
     */
    public void setState(final StateConversionEnum state)
    {
        this.state = state;
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

    public boolean isFailed()
    {
        return getState() == StateConversionEnum.FAILED;
    }

    public boolean isBundleConversion()
    {
        return sourceType != null;
    }

    public DiskFormatType getSourceType()
    {
        return sourceType;
    }

    public void setSourceType(DiskFormatType sourceType)
    {
        this.sourceType = sourceType;
    }

    public DiskFormatType getTargetType()
    {
        return targetType;
    }

    public void setTargetType(DiskFormatType targetType)
    {
        this.targetType = targetType;
    }
}
