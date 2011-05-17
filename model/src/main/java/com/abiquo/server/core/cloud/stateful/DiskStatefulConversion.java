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

package com.abiquo.server.core.cloud.stateful;

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
import org.hibernate.validator.constraints.Length;

import com.abiquo.model.enumerator.ConversionState;
import com.abiquo.server.core.common.DefaultEntityBase;
import com.abiquo.server.core.infrastructure.storage.VolumeManagement;
import com.softwarementors.validation.constraints.LeadingOrTrailingWhitespace;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = DiskStatefulConversion.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = DiskStatefulConversion.TABLE_NAME)
public class DiskStatefulConversion extends DefaultEntityBase
{
    public static final String TABLE_NAME = "diskstateful_conversions";

    // DO NOT ACCESS: present due to needs of infrastructure support. *NEVER* call from business
    // code
    protected DiskStatefulConversion()
    {
        // Just for JPA support
    }

    public DiskStatefulConversion(final String imagePath, final VolumeManagement volume,
        final ConversionState state, final Date timestamp)
    {
        setImagePath(imagePath);
        setVolume(volume);
        setState(state);
        // setTimestamp(timestamp);
    }

    private final static String ID_COLUMN = "id";

    @Id
    @Column(name = ID_COLUMN, nullable = false)
    @GeneratedValue
    private Integer id;

    @Override
    public Integer getId()
    {
        return this.id;
    }

    // public final static String TIMESTAMP_PROPERTY = "timestamp";
    //
    // private final static boolean TIMESTAMP_REQUIRED = true;
    //
    // private final static int TIMESTAMP_LENGTH_MIN = 0;
    //
    // private final static int TIMESTAMP_LENGTH_MAX = 255;
    //
    // private final static boolean TIMESTAMP_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;
    //
    // private final static String TIMESTAMP_COLUMN = "convertionTimestamp";
    //
    // @Column(name = TIMESTAMP_COLUMN, nullable = !TIMESTAMP_REQUIRED, length =
    // TIMESTAMP_LENGTH_MAX)
    // private Date timestamp;
    //
    // @Required(value = TIMESTAMP_REQUIRED)
    // @Length(min = TIMESTAMP_LENGTH_MIN, max = TIMESTAMP_LENGTH_MAX)
    // @LeadingOrTrailingWhitespace(allowed = TIMESTAMP_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    // public Date getTimestamp()
    // {
    // return this.timestamp;
    // }
    //
    // private void setTimestamp(final Date timestamp)
    // {
    // this.timestamp = timestamp;
    // }

    public final static String IMAGE_PATH_PROPERTY = "imagePath";

    private final static boolean IMAGE_PATH_REQUIRED = true;

    /* package */final static int IMAGE_PATH_LENGTH_MIN = 0;

    /* package */final static int IMAGE_PATH_LENGTH_MAX = 255;

    private final static boolean IMAGE_PATH_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String IMAGE_PATH_COLUMN = "imagePath";

    @Column(name = IMAGE_PATH_COLUMN, nullable = !IMAGE_PATH_REQUIRED, length = IMAGE_PATH_LENGTH_MAX)
    private String imagePath;

    @Required(value = IMAGE_PATH_REQUIRED)
    @Length(min = IMAGE_PATH_LENGTH_MIN, max = IMAGE_PATH_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = IMAGE_PATH_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getImagePath()
    {
        return this.imagePath;
    }

    public void setImagePath(final String imagePath)
    {
        this.imagePath = imagePath;
    }

    public final static String STATE_PROPERTY = "state";

    private final static boolean STATE_REQUIRED = true;

    private final static String STATE_COLUMN = "state";

    @Enumerated(value = javax.persistence.EnumType.STRING)
    @Column(name = STATE_COLUMN, nullable = !STATE_REQUIRED)
    private ConversionState state;

    public ConversionState getState()
    {
        return this.state;
    }

    public void setState(final ConversionState state)
    {
        this.state = state;
    }

    public final static String VOLUME_PROPERTY = "volume";

    private final static boolean VOLUME_REQUIRED = true;

    private final static String VOLUME_ID_COLUMN = "idManagement";

    @JoinColumn(name = VOLUME_ID_COLUMN)
    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_volume")
    private VolumeManagement volume;

    @Required(value = VOLUME_REQUIRED)
    public VolumeManagement getVolume()
    {
        return this.volume;
    }

    public void setVolume(final VolumeManagement volume)
    {
        this.volume = volume;
    }

}
