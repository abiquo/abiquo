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

package com.abiquo.server.core.statistics;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import com.abiquo.server.core.common.DefaultEntityBase;
import com.softwarementors.validation.constraints.LeadingOrTrailingWhitespace;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = VirtualAppResources.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = VirtualAppResources.TABLE_NAME)
public class VirtualAppResources extends DefaultEntityBase
{
    public static final String TABLE_NAME = "vapp_enterprise_stats";

    protected VirtualAppResources()
    {
    }

    private final static String ID_COLUMN = "idVirtualApp";

    @Id
    @GeneratedValue
    @Column(name = ID_COLUMN, nullable = false)
    private Integer id;

    public Integer getId()
    {
        return this.id;
    }

    public final static String VDC_NAME_PROPERTY = "vdcName";

    private final static boolean VDC_NAME_REQUIRED = false;

    private final static int VDC_NAME_LENGTH_MIN = 0;

    private final static int VDC_NAME_LENGTH_MAX = 255;

    private final static boolean VDC_NAME_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String VDC_NAME_COLUMN = "vdcName";

    @Column(name = VDC_NAME_COLUMN, nullable = !VDC_NAME_REQUIRED, length = VDC_NAME_LENGTH_MAX)
    private String vdcName;

    @Required(value = VDC_NAME_REQUIRED)
    @Length(min = VDC_NAME_LENGTH_MIN, max = VDC_NAME_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = VDC_NAME_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getVdcName()
    {
        return this.vdcName;
    }

    public void setVdcName(String vdcName)
    {
        this.vdcName = vdcName;
    }

    public final static String VAPP_NAME_PROPERTY = "vappName";

    private final static boolean VAPP_NAME_REQUIRED = false;

    private final static int VAPP_NAME_LENGTH_MIN = 0;

    private final static int VAPP_NAME_LENGTH_MAX = 255;

    private final static boolean VAPP_NAME_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String VAPP_NAME_COLUMN = "vappName";

    @Column(name = VAPP_NAME_COLUMN, nullable = !VAPP_NAME_REQUIRED, length = VAPP_NAME_LENGTH_MAX)
    private String vappName;

    @Required(value = VAPP_NAME_REQUIRED)
    @Length(min = VAPP_NAME_LENGTH_MIN, max = VAPP_NAME_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = VAPP_NAME_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getVappName()
    {
        return this.vappName;
    }

    public void setVappName(String vappName)
    {
        this.vappName = vappName;
    }

    public final static String VOL_ATTACHED_PROPERTY = "volAttached";

    private final static String VOL_ATTACHED_COLUMN = "volAttached";

    private final static int VOL_ATTACHED_MIN = Integer.MIN_VALUE;

    private final static int VOL_ATTACHED_MAX = Integer.MAX_VALUE;

    @Column(name = VOL_ATTACHED_COLUMN, nullable = true)
    @Range(min = VOL_ATTACHED_MIN, max = VOL_ATTACHED_MAX)
    private int volAttached;

    public int getVolAttached()
    {
        return this.volAttached;
    }

    public void setVolAttached(int volAttached)
    {
        this.volAttached = volAttached;
    }

    public final static String VM_CREATED_PROPERTY = "vmCreated";

    private final static String VM_CREATED_COLUMN = "vmCreated";

    private final static int VM_CREATED_MIN = Integer.MIN_VALUE;

    private final static int VM_CREATED_MAX = Integer.MAX_VALUE;

    @Column(name = VM_CREATED_COLUMN, nullable = true)
    @Range(min = VM_CREATED_MIN, max = VM_CREATED_MAX)
    private int vmCreated;

    public int getVmCreated()
    {
        return this.vmCreated;
    }

    public void setVmCreated(int vmCreated)
    {
        this.vmCreated = vmCreated;
    }

    public final static String VOL_ASSOCIATED_PROPERTY = "volAssociated";

    private final static String VOL_ASSOCIATED_COLUMN = "volAssociated";

    private final static int VOL_ASSOCIATED_MIN = Integer.MIN_VALUE;

    private final static int VOL_ASSOCIATED_MAX = Integer.MAX_VALUE;

    @Column(name = VOL_ASSOCIATED_COLUMN, nullable = true)
    @Range(min = VOL_ASSOCIATED_MIN, max = VOL_ASSOCIATED_MAX)
    private int volAssociated;

    public int getVolAssociated()
    {
        return this.volAssociated;
    }

    public void setVolAssociated(int volAssociated)
    {
        this.volAssociated = volAssociated;
    }

    public final static String VM_ACTIVE_PROPERTY = "vmActive";

    private final static String VM_ACTIVE_COLUMN = "vmActive";

    private final static int VM_ACTIVE_MIN = Integer.MIN_VALUE;

    private final static int VM_ACTIVE_MAX = Integer.MAX_VALUE;

    @Column(name = VM_ACTIVE_COLUMN, nullable = true)
    @Range(min = VM_ACTIVE_MIN, max = VM_ACTIVE_MAX)
    private int vmActive;

    public int getVmActive()
    {
        return this.vmActive;
    }

    public void setVmActive(int vmActive)
    {
        this.vmActive = vmActive;
    }

    public final static String ID_ENTERPRISE_PROPERTY = "idEnterprise";

    private final static String ID_ENTERPRISE_COLUMN = "idEnterprise";

    private final static int ID_ENTERPRISE_MIN = Integer.MIN_VALUE;

    private final static int ID_ENTERPRISE_MAX = Integer.MAX_VALUE;

    @Column(name = ID_ENTERPRISE_COLUMN, nullable = true)
    @Range(min = ID_ENTERPRISE_MIN, max = ID_ENTERPRISE_MAX)
    private int idEnterprise;

    public int getIdEnterprise()
    {
        return this.idEnterprise;
    }

    public void setIdEnterprise(int idEnterprise)
    {
        this.idEnterprise = idEnterprise;
    }

}
