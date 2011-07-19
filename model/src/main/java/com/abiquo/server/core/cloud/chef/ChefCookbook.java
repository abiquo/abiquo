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

import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.common.DefaultEntityBase;
import com.softwarementors.validation.constraints.LeadingOrTrailingWhitespace;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = ChefCookbook.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = ChefCookbook.TABLE_NAME)
public class ChefCookbook extends DefaultEntityBase
{
    public static final String TABLE_NAME = "chefcookbook";

    protected ChefCookbook()
    {
        super();
    }

    public ChefCookbook(final VirtualMachine virtualmachine, final String cookbook,
        final String version)
    {
        super();
        setVirtualmachine(virtualmachine);
        setCookbook(cookbook);
        setCookbook(version);
    }

    private final static String ID_COLUMN = "chefCookbookId";

    @Id
    @GeneratedValue
    @Column(name = ID_COLUMN, nullable = false)
    private Integer id;

    @Override
    public Integer getId()
    {
        return this.id;
    }

    public final static String VIRTUALMACHINE_PROPERTY = "virtualmachine";

    private final static boolean VIRTUALMACHINE_REQUIRED = false;

    private final static String VIRTUALMACHINE_ID_COLUMN = "idVM";

    @JoinColumn(name = VIRTUALMACHINE_ID_COLUMN)
    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_virtualmachine")
    private VirtualMachine virtualmachine;

    @Required(value = VIRTUALMACHINE_REQUIRED)
    public VirtualMachine getVirtualmachine()
    {
        return this.virtualmachine;
    }

    public void setVirtualmachine(final VirtualMachine virtualmachine)
    {
        this.virtualmachine = virtualmachine;
    }

    public final static String COOKBOOK_VERSION_PROPERTY = "cookbookVersion";

    private final static boolean COOKBOOK_VERSION_REQUIRED = true;

    private final static int COOKBOOK_VERSION_LENGTH_MIN = 0;

    private final static int COOKBOOK_VERSION_LENGTH_MAX = 20;

    private final static boolean COOKBOOK_VERSION_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String COOKBOOK_VERSION_COLUMN = "CookbookVersion";

    @Column(name = COOKBOOK_VERSION_COLUMN, nullable = !COOKBOOK_VERSION_REQUIRED, length = COOKBOOK_VERSION_LENGTH_MAX)
    private String cookbookVersion = "";

    @Required(value = COOKBOOK_VERSION_REQUIRED)
    @Length(min = COOKBOOK_VERSION_LENGTH_MIN, max = COOKBOOK_VERSION_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = COOKBOOK_VERSION_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getCookbookVersion()
    {
        return this.cookbookVersion;
    }

    public void setCookbookVersion(final String cookbookVersion)
    {
        this.cookbookVersion = cookbookVersion;
    }

    public final static String COOKBOOK_PROPERTY = "cookbook";

    private final static boolean COOKBOOK_REQUIRED = true;

    private final static int COOKBOOK_LENGTH_MIN = 0;

    private final static int COOKBOOK_LENGTH_MAX = 50;

    private final static boolean COOKBOOK_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String COOKBOOK_COLUMN = "Cookbook";

    @Column(name = COOKBOOK_COLUMN, nullable = !COOKBOOK_REQUIRED, length = COOKBOOK_LENGTH_MAX)
    private String cookbook;

    @Required(value = COOKBOOK_REQUIRED)
    @Length(min = COOKBOOK_LENGTH_MIN, max = COOKBOOK_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = COOKBOOK_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getCookbook()
    {
        return this.cookbook;
    }

    private void setCookbook(final String cookbook)
    {
        this.cookbook = cookbook;
    }

}
