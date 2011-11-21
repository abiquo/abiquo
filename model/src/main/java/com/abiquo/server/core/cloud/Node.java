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

package com.abiquo.server.core.cloud;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
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
@Table(name = Node.TABLE_NAME)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
class Node extends DefaultEntityBase
{

    public static final String TABLE_NAME = "node";

    protected Node()
    {
        setType(NodeVirtualImage.DISCRIMINATOR); // node virtual image
    }

    public Node(final String type)
    {
        setType(type);
    }

    private final static String ID_COLUMN = "idNode";

    @Id
    @GeneratedValue
    @Column(name = ID_COLUMN, nullable = false)
    private Integer id;

    @Override
    public Integer getId()
    {
        return this.id;
    }

    public final static String MODIFIED_PROPERTY = "modified";

    private final static String MODIFIED_COLUMN = "modified";

    private final static boolean MODIFIED_REQUIRED = true;

    private final static int MODIFIED_DEFAULT = 0;

    private final static int MODIFIED_MIN = Integer.MIN_VALUE;

    private final static int MODIFIED_MAX = Integer.MAX_VALUE;

    @Column(name = MODIFIED_COLUMN, nullable = !MODIFIED_REQUIRED)
    @Range(min = MODIFIED_MIN, max = MODIFIED_MAX)
    private int modified = MODIFIED_DEFAULT;

    public int getModified()
    {
        return this.modified;
    }

    protected void setModified(final int modified)
    {
        this.modified = modified;
    }

    //
    public final static String TYPE_PROPERTY = "type";

    private final static String TYPE_COLUMN = "type";

    private final static boolean TYPE_REQUIRED = true;

    private final static String TYPE_DEFAULT = NodeVirtualImage.DISCRIMINATOR; // node virtual image

    private final static int TYPE_LENGTH_MIN = 1;

    private final static int TYPE_LENGTH_MAX = 100;

    @Column(name = TYPE_COLUMN, nullable = !TYPE_REQUIRED)
    @Length(min = TYPE_LENGTH_MIN, max = TYPE_LENGTH_MAX)
    private String type = TYPE_DEFAULT;

    public String getType()
    {
        return type;
    }

    protected void setType(final String type)
    {
        this.type = type;
    }

    public final static String NAME_PROPERTY = "name";

    private final static boolean NAME_REQUIRED = false;

    /* package */final static int NAME_LENGTH_MIN = 0;

    /* package */final static int NAME_LENGTH_MAX = 255;

    private final static boolean NAME_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String NAME_COLUMN = "name";

    @Column(name = NAME_COLUMN, nullable = !NAME_REQUIRED, length = NAME_LENGTH_MAX)
    private String name;

    @Required(value = NAME_REQUIRED)
    @Length(min = NAME_LENGTH_MIN, max = NAME_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = NAME_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getName()
    {
        return this.name;
    }

    protected void setName(final String name)
    {
        this.name = name;
    }

    public final static String VIRTUAL_APPLIANCE_PROPERTY = "virtualAppliance";

    protected final static boolean VIRTUAL_APPLIANCE_REQUIRED = true;

    protected final static String VIRTUAL_APPLIANCE_ID_COLUMN = "idVirtualApp";

    @JoinColumn(name = VIRTUAL_APPLIANCE_ID_COLUMN)
    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_virtualAppliance")
    protected VirtualAppliance virtualAppliance;

    @Required(value = VIRTUAL_APPLIANCE_REQUIRED)
    public VirtualAppliance getVirtualAppliance()
    {
        return this.virtualAppliance;
    }

    public void setVirtualAppliance(final VirtualAppliance virtualAppliance)
    {
        this.virtualAppliance = virtualAppliance;
    }

    public final static String X_PROPERTY = "x";

    private final static String X_COLUMN = "posX";

    private final static boolean X_REQUIRED = true;

    private final static int X_DEFAULT = 0;

    private final static int X_MIN = Integer.MIN_VALUE;

    private final static int X_MAX = Integer.MAX_VALUE;

    @Column(name = X_COLUMN, nullable = !X_REQUIRED)
    @Range(min = X_MIN, max = X_MAX)
    private int x = X_DEFAULT;

    public int getX()
    {
        return this.x;
    }

    protected void setX(final int x)
    {
        this.x = x;
    }

    public final static String Y_PROPERTY = "y";

    private final static String Y_COLUMN = "posY";

    private final static boolean Y_REQUIRED = true;

    private final static int Y_DEFAULT = 0;

    private final static int Y_MIN = Integer.MIN_VALUE;

    private final static int Y_MAX = Integer.MAX_VALUE;

    @Column(name = Y_COLUMN, nullable = !Y_REQUIRED)
    @Range(min = Y_MIN, max = Y_MAX)
    private int y = Y_DEFAULT;

    public int getY()
    {
        return this.y;
    }

    protected void setY(final int y)
    {
        this.y = y;
    }
}
