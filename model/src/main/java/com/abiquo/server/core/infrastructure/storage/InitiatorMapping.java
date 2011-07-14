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

package com.abiquo.server.core.infrastructure.storage;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import com.abiquo.server.core.common.DefaultEntityBase;
import com.softwarementors.validation.constraints.LeadingOrTrailingWhitespace;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = InitiatorMapping.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = InitiatorMapping.TABLE_NAME)
public class InitiatorMapping extends DefaultEntityBase
{
    public static final String TABLE_NAME = "initiator_mapping";

    protected InitiatorMapping()
    {
    }

    public InitiatorMapping(final String initiatorIQN, VolumeManagement volumeManagementProperty,
        final String targetIQN, final int targetLUN)
    {
        this.setInitiatorIqn(initiatorIQN);
        this.setTargetIqn(targetIQN);
        this.setTargetLun(targetLUN);
        this.setVolumeManagement(volumeManagementProperty);

    }

    private final static String ID_COLUMN = "idInitiatorMapping";

    @Id
    @GeneratedValue
    @Column(name = ID_COLUMN, nullable = false)
    private Integer id;

    public Integer getId()
    {
        return this.id;
    }

    public final static String TARGET_LUN_PROPERTY = "targetLun";

    private final static boolean TARGET_LUN_REQUIRED = true;

   // private final static boolean TARGET_LUN_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String TARGET_LUN_COLUMN = "targetLun";

    @Column(name = TARGET_LUN_COLUMN, nullable = !TARGET_LUN_REQUIRED)
    @Range(min = TARGET_IQN_LENGTH_MIN, max = TARGET_IQN_LENGTH_MAX)
    private Integer targetLun;
//
//    @Required(value = TARGET_LUN_REQUIRED)
//    @Length(min = TARGET_LUN_LENGTH_MIN, max = TARGET_LUN_LENGTH_MAX)
   // @LeadingOrTrailingWhitespace(allowed = TARGET_LUN_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public Integer getTargetLun()
    {
        return this.targetLun;
    }

    private void setTargetLun(int targetLun)
    {
        this.targetLun = targetLun;
    }

    public final static String TARGET_IQN_PROPERTY = "targetIqn";

    private final static boolean TARGET_IQN_REQUIRED = true;

    private final static int TARGET_IQN_LENGTH_MIN = 0;

    private final static int TARGET_IQN_LENGTH_MAX = 255;

    private final static boolean TARGET_IQN_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String TARGET_IQN_COLUMN = "targetIqn";

    @Column(name = TARGET_IQN_COLUMN, nullable = !TARGET_IQN_REQUIRED, length = TARGET_IQN_LENGTH_MAX)
    private String targetIqn;

    @Required(value = TARGET_IQN_REQUIRED)
    @Length(min = TARGET_IQN_LENGTH_MIN, max = TARGET_IQN_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = TARGET_IQN_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getTargetIqn()
    {
        return this.targetIqn;
    }

    private void setTargetIqn(String targetIqn)
    {
        this.targetIqn = targetIqn;
    }

    public final static String INITIATOR_IQN_PROPERTY = "initiatorIqn";

    private final static boolean INITIATOR_IQN_REQUIRED = true;

    private final static int INITIATOR_IQN_LENGTH_MIN = 0;

    private final static int INITIATOR_IQN_LENGTH_MAX = 255;

    private final static boolean INITIATOR_IQN_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String INITIATOR_IQN_COLUMN = "initiatorIqn";

    @Column(name = INITIATOR_IQN_COLUMN, nullable = !INITIATOR_IQN_REQUIRED, length = INITIATOR_IQN_LENGTH_MAX)
    private String initiatorIqn;

    @Required(value = INITIATOR_IQN_REQUIRED)
    @Length(min = INITIATOR_IQN_LENGTH_MIN, max = INITIATOR_IQN_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = INITIATOR_IQN_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getInitiatorIqn()
    {
        return this.initiatorIqn;
    }

    private void setInitiatorIqn(String initiatorIqn)
    {
        this.initiatorIqn = initiatorIqn;
    }

    public final static String VOLUME_MANAGEMENT_PROPERTY = "volumeManagement";

    private final static boolean VOLUME_MANAGEMENT_REQUIRED = true;

    private final static String VOLUME_MANAGEMENT_ID_COLUMN = "idManagement";

    @JoinColumn(name = VOLUME_MANAGEMENT_ID_COLUMN)
    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_volumeManagement")
    private VolumeManagement volume;

    @Required(value = VOLUME_MANAGEMENT_REQUIRED)
    public VolumeManagement getVolumeManagement()
    {
        return this.volume;
    }

    public void setVolumeManagement(VolumeManagement volumeManagement)
    {
        this.volume = volumeManagement;
    }

}
