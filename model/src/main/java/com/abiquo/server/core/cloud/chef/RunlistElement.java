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

package com.abiquo.server.core.cloud.chef;

import java.util.Comparator;

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

import com.abiquo.model.util.ChefUtils;
import com.abiquo.model.validation.RunlistItem;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.common.DefaultEntityBase;
import com.softwarementors.validation.constraints.LeadingOrTrailingWhitespace;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = RunlistElement.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = RunlistElement.TABLE_NAME)
public class RunlistElement extends DefaultEntityBase
{
    public static final String TABLE_NAME = "chef_runlist";

    protected RunlistElement()
    {
        super();
    }

    public RunlistElement(final String name, final String description, final int priority)
    {
        super();
        setName(name);
        setDescription(description);
        setPriority(priority);
    }

    private final static String ID_COLUMN = "id";

    @Id
    @GeneratedValue
    @Column(name = ID_COLUMN, nullable = false)
    private Integer id;

    @Override
    public Integer getId()
    {
        return this.id;
    }

    public void setId(final Integer id)
    {
        this.id = id;
    }

    public final static String VIRTUALMACHINE_PROPERTY = "virtualMachine";

    private final static boolean VIRTUALMACHINE_REQUIRED = true;

    private final static String VIRTUALMACHINE_ID_COLUMN = "idVM";

    @JoinColumn(name = VIRTUALMACHINE_ID_COLUMN)
    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_virtualmachine")
    private VirtualMachine virtualMachine;

    @Required(value = VIRTUALMACHINE_REQUIRED)
    public VirtualMachine getVirtualMachine()
    {
        return this.virtualMachine;
    }

    public void setVirtualMachine(final VirtualMachine virtualMachine)
    {
        this.virtualMachine = virtualMachine;
    }

    public final static String NAME_PROPERTY = "name";

    private final static boolean NAME_REQUIRED = true;

    /* package */final static int NAME_LENGTH_MIN = 1;

    /* package */final static int NAME_LENGTH_MAX = 100;

    private final static boolean NAME_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String NAME_COLUMN = "name";

    @Column(name = NAME_COLUMN, nullable = !NAME_REQUIRED, length = NAME_LENGTH_MAX)
    private String name;

    @Required(value = NAME_REQUIRED)
    @Length(min = NAME_LENGTH_MIN, max = NAME_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = NAME_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    @RunlistItem
    public String getName()
    {
        return this.name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public final static String DESCRIPTION_PROPERTY = "description";

    private final static boolean DESCRIPTION_REQUIRED = false;

    /* package */final static int DESCRIPTION_LENGTH_MIN = 0;

    /* package */final static int DESCRIPTION_LENGTH_MAX = 255;

    private final static boolean DESCRIPTION_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String DESCRIPTION_COLUMN = "description";

    @Column(name = DESCRIPTION_COLUMN, nullable = !DESCRIPTION_REQUIRED, length = DESCRIPTION_LENGTH_MAX)
    private String description;

    @Required(value = DESCRIPTION_REQUIRED)
    @Length(min = DESCRIPTION_LENGTH_MIN, max = DESCRIPTION_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = DESCRIPTION_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getDescription()
    {
        return this.description;
    }

    public void setDescription(final String description)
    {
        this.description = description;
    }

    public final static String PRIORITY_PROPERTY = "priority";

    final static String PRIORITY_COLUMN = "priority";

    /* package */final static int PRIORITY_MIN = 0;

    /* package */final static int PRIORITY_MAX = Integer.MAX_VALUE;

    final static boolean PRIORITY_REQUIRED = true;

    @Column(name = PRIORITY_COLUMN, nullable = false)
    @Range(min = PRIORITY_MIN, max = PRIORITY_MAX)
    private int priority;

    @Required(value = PRIORITY_REQUIRED)
    public int getPriority()
    {
        return this.priority;
    }

    public void setPriority(final int priority)
    {
        this.priority = priority;
    }

    /* ************ Utility methods ************ */

    public boolean isCookbook()
    {
        return isRecipe() && !getRecipeName().contains("::");
    }

    public boolean isRecipe()
    {
        return ChefUtils.isRecipe(name);
    }

    public boolean isRole()
    {
        return ChefUtils.isRole(name);
    }

    public String getRecipeName()
    {
        if (!isRecipe())
        {
            throw new IllegalArgumentException("The current runlist element is not a recipe");
        }
        return ChefUtils.getRecipeName(name);
    }

    public String getRoleName()
    {
        if (!isRole())
        {
            throw new IllegalArgumentException("The current runlist element is not a role");
        }
        return ChefUtils.getRoleName(name);
    }

    public Type getType()
    {
        return isRecipe() ? Type.RECIPE : Type.ROLE;
    }

    public static enum Type
    {
        RECIPE, ROLE
    }

    public static enum RunlistElementOrder implements Comparator<RunlistElement>
    {
        BY_NAME
        {
            @Override
            public int compare(final RunlistElement r1, final RunlistElement r2)
            {
                return String.CASE_INSENSITIVE_ORDER.compare(r1.getName(), r2.getName());
            }
        },

        BY_PRIORITY
        {
            @Override
            public int compare(final RunlistElement r1, final RunlistElement r2)
            {
                return r1.getPriority() - r2.getPriority();
            }
        }
    }

}
