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

package com.abiquo.server.core.appslibrary;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;

import com.abiquo.model.enumerator.ConversionState;
import com.abiquo.model.enumerator.DiskFormatType;
import com.abiquo.server.core.common.DefaultEntityBase;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = VirtualImageConversion.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = VirtualImageConversion.TABLE_NAME)
public class VirtualImageConversion extends DefaultEntityBase
{
    public static final String TABLE_NAME = "virtualimage_conversions";

    protected VirtualImageConversion()
    {

    }

    protected VirtualImageConversion(final VirtualImage image, final DiskFormatType targetType,
        final String targetPath)
    {
        setImage(image);
        setTargetType(targetType);
        setTargetPath(targetPath);
        setTimestamp(new Date());
        setState(ConversionState.ENQUEUED);
    }

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private Integer id;

    @Override
    public Integer getId()
    {
        return id;
    }

    @JoinColumn(name = "idImage")
    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "FK_virtualimage_conversions_virtualimage")
    private VirtualImage image;

    @Required(true)
    public VirtualImage getImage()
    {
        return image;
    }

    public void setImage(final VirtualImage virtualImage)
    {
        this.image = virtualImage;
    }

    @Enumerated(value = javax.persistence.EnumType.STRING)
    @Column(nullable = true)
    private DiskFormatType sourceType;

    @Enumerated(value = javax.persistence.EnumType.STRING)
    @Column(nullable = false)
    private DiskFormatType targetType;

    @Column(nullable = true)
    private String sourcePath;

    @Column(nullable = false)
    private String targetPath;

    @Enumerated(value = javax.persistence.EnumType.STRING)
    @Column(nullable = false)
    private ConversionState state;

    @Column(nullable = false)
    private Date timestamp;

    @Column(nullable = true)
    private Long size;

    public DiskFormatType getSourceType()
    {
        return sourceType;
    }

    public void setSourceType(final DiskFormatType sourceType)
    {
        this.sourceType = sourceType;
    }

    public DiskFormatType getTargetType()
    {
        return targetType;
    }

    public void setTargetType(final DiskFormatType targetType)
    {
        this.targetType = targetType;
    }

    public String getSourcePath()
    {
        return sourcePath;
    }

    public void setSourcePath(final String sourcePath)
    {
        this.sourcePath = sourcePath;
    }

    public String getTargetPath()
    {
        return targetPath;
    }

    public void setTargetPath(final String targetPath)
    {
        this.targetPath = targetPath;
    }

    public ConversionState getState()
    {
        return state;
    }

    public void setState(final ConversionState enqueued)
    {
        this.state = enqueued;
    }

    public Date getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(final Date timestamp)
    {
        this.timestamp = timestamp;
    }

    public Long getSize()
    {
        return size;
    }

    public void setSize(final Long size)
    {
        this.size = size;
    }
}
