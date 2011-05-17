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

import com.abiquo.server.core.cloud.NodeVirtualImage;
import com.abiquo.server.core.cloud.VirtualImageConversion;
import com.abiquo.server.core.common.DefaultEntityBase;
import com.abiquo.server.core.infrastructure.storage.Tier;
import com.softwarementors.validation.constraints.LeadingOrTrailingWhitespace;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = NodeVirtualImageStatefulConversion.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = NodeVirtualImageStatefulConversion.TABLE_NAME)
public class NodeVirtualImageStatefulConversion extends DefaultEntityBase
{
    public static final String TABLE_NAME = "node_virtual_image_stateful_conversions";

    // DO NOT ACCESS: present due to needs of infrastructure support. *NEVER* call from business
    // code
    protected NodeVirtualImageStatefulConversion()
    {
        // Just for JPA support
    }

    public NodeVirtualImageStatefulConversion(final String newName,
        final VirtualApplianceStatefulConversion virtualApplianceStatefulConversion,
        final NodeVirtualImage nodeVirtualImage, final Tier tier)
    {
        setNewName(newName);
        setVirtualApplianceStatefulConversion(virtualApplianceStatefulConversion);
        setNodeVirtualImage(nodeVirtualImage);
        setTier(tier);
    }

    private final static String ID_COLUMN = "id";

    @Id
    @GeneratedValue
    @Column(name = ID_COLUMN, nullable = false)
    private Integer id;

    public Integer getId()
    {
        return this.id;
    }

    public final static String NODE_VIRTUAL_IMAGE_PROPERTY = "nodeVirtualImage";

    private final static boolean NODE_VIRTUAL_IMAGE_REQUIRED = true;

    private final static String NODE_VIRTUAL_IMAGE_ID_COLUMN = "idNodeVirtualImage";

    @JoinColumn(name = NODE_VIRTUAL_IMAGE_ID_COLUMN)
    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_nodeVirtualImage")
    private NodeVirtualImage nodeVirtualImage;

    @Required(value = NODE_VIRTUAL_IMAGE_REQUIRED)
    public NodeVirtualImage getNodeVirtualImage()
    {
        return this.nodeVirtualImage;
    }

    public void setNodeVirtualImage(final NodeVirtualImage nodeVirtualImage)
    {
        this.nodeVirtualImage = nodeVirtualImage;
    }

    public final static String NEW_NAME_PROPERTY = "newName";

    private final static boolean NEW_NAME_REQUIRED = true;

    /* package */final static int NEW_NAME_LENGTH_MIN = 0;

    /* package */final static int NEW_NAME_LENGTH_MAX = 255;

    private final static boolean NEW_NAME_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String NEW_NAME_COLUMN = "newName";

    @Column(name = NEW_NAME_COLUMN, nullable = !NEW_NAME_REQUIRED, length = NEW_NAME_LENGTH_MAX)
    private String newName;

    @Required(value = NEW_NAME_REQUIRED)
    @Length(min = NEW_NAME_LENGTH_MIN, max = NEW_NAME_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = NEW_NAME_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getNewName()
    {
        return this.newName;
    }

    private void setNewName(final String newName)
    {
        this.newName = newName;
    }

    public final static String DISK_STATEFUL_CONVERSION_PROPERTY = "diskStatefulConversion";

    private final static boolean DISK_STATEFUL_CONVERSION_REQUIRED = false;

    private final static String DISK_STATEFUL_CONVERSION_ID_COLUMN = "idDiskStatefulConversion";

    @JoinColumn(name = DISK_STATEFUL_CONVERSION_ID_COLUMN)
    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_diskStatefulConversion")
    private DiskStatefulConversion diskStatefulConversion;

    @Required(value = DISK_STATEFUL_CONVERSION_REQUIRED)
    public DiskStatefulConversion getDiskStatefulConversion()
    {
        return this.diskStatefulConversion;
    }

    public void setDiskStatefulConversion(final DiskStatefulConversion diskStatefulConversion)
    {
        this.diskStatefulConversion = diskStatefulConversion;
    }

    public final static String TIER_PROPERTY = "tier";

    private final static boolean TIER_REQUIRED = true;

    private final static String TIER_ID_COLUMN = "idTier";

    @JoinColumn(name = TIER_ID_COLUMN)
    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_tier")
    private Tier tier;

    @Required(value = TIER_REQUIRED)
    public Tier getTier()
    {
        return this.tier;
    }

    public void setTier(final Tier tier)
    {
        this.tier = tier;
    }

    public final static String VIRTUAL_APPLIANCE_STATEFUL_CONVERSION_PROPERTY =
        "virtualApplianceStatefulConversion";

    private final static boolean VIRTUAL_APPLIANCE_STATEFUL_CONVERSION_REQUIRED = true;

    private final static String VIRTUAL_APPLIANCE_STATEFUL_CONVERSION_ID_COLUMN =
        "idVirtualApplianceStatefulConversion";

    @JoinColumn(name = VIRTUAL_APPLIANCE_STATEFUL_CONVERSION_ID_COLUMN)
    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_virtualApplianceStatefulConversion")
    private VirtualApplianceStatefulConversion virtualApplianceStatefulConversion;

    @Required(value = VIRTUAL_APPLIANCE_STATEFUL_CONVERSION_REQUIRED)
    public VirtualApplianceStatefulConversion getVirtualApplianceStatefulConversion()
    {
        return this.virtualApplianceStatefulConversion;
    }

    public void setVirtualApplianceStatefulConversion(
        final VirtualApplianceStatefulConversion virtualApplianceStatefulConversion)
    {
        this.virtualApplianceStatefulConversion = virtualApplianceStatefulConversion;
    }

    public final static String VIRTUAL_IMAGE_CONVERSION_PROPERTY = "virtualImageConversion";

    private final static boolean VIRTUAL_IMAGE_CONVERSION_REQUIRED = false;

    private final static String VIRTUAL_IMAGE_CONVERSION_ID_COLUMN = "idVirtualImageConversion";

    @JoinColumn(name = VIRTUAL_IMAGE_CONVERSION_ID_COLUMN)
    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_virtualImageConversion")
    private VirtualImageConversion virtualImageConversion;

    @Required(value = VIRTUAL_IMAGE_CONVERSION_REQUIRED)
    public VirtualImageConversion getVirtualImageConversion()
    {
        return this.virtualImageConversion;
    }

    public void setVirtualImageConversion(final VirtualImageConversion virtualImageConversion)
    {
        this.virtualImageConversion = virtualImageConversion;
    }

}
