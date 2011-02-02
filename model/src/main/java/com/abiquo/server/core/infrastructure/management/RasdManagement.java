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

package com.abiquo.server.core.infrastructure.management;

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

import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.common.DefaultEntityBase;
import com.softwarementors.validation.constraints.LeadingOrTrailingWhitespace;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = RasdManagement.TABLE_NAME)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "idResourceType", discriminatorType = DiscriminatorType.STRING)
public class RasdManagement extends DefaultEntityBase
{
    public static final String TABLE_NAME = "rasd_management";

    protected RasdManagement()
    {
    }

    protected RasdManagement(String idResourceType)
    {
        // setVirtualDatacenter(vdc);
        setIdResourceType(idResourceType);
        // setVirtualAppliance(virtualAppliance);
    }

    private final static String ID_COLUMN = "idManagement";

    @Id
    @GeneratedValue
    @Column(name = ID_COLUMN, nullable = false)
    private Integer id;

    @Override
    public Integer getId()
    {
        return this.id;
    }

    // public final static String ID_RESOURCE_PROPERTY = "idResource";
    //
    // private final static boolean ID_RESOURCE_REQUIRED = false;
    //
    // private final static int ID_RESOURCE_LENGTH_MIN = 0;
    //
    // private final static int ID_RESOURCE_LENGTH_MAX = 255;
    //
    // private final static boolean ID_RESOURCE_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;
    //
    // private final static String ID_RESOURCE_COLUMN = "idResource";
    //
    // @Column(name = ID_RESOURCE_COLUMN, nullable = !ID_RESOURCE_REQUIRED, length =
    // ID_RESOURCE_LENGTH_MAX)
    // private String idResource;
    //
    // @Required(value = ID_RESOURCE_REQUIRED)
    // @Length(min = ID_RESOURCE_LENGTH_MIN, max = ID_RESOURCE_LENGTH_MAX)
    // @LeadingOrTrailingWhitespace(allowed = ID_RESOURCE_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    // public String getIdResource()
    // {
    // return this.idResource;
    // }
    //
    // protected void setIdResource(String idResource)
    // {
    // this.idResource = idResource;
    // }

    //
    public final static String VIRTUAL_APPLIANCE_PROPERTY = "virtualAppliance";

    private final static boolean VIRTUAL_APPLIANCE_REQUIRED = false;

    private final static String VIRTUAL_APPLIANCE_ID_COLUMN = "idVirtualApp";

    @JoinColumn(name = VIRTUAL_APPLIANCE_ID_COLUMN)
    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_virtualAppliance")
    private VirtualAppliance virtualAppliance;

    @Required(value = VIRTUAL_APPLIANCE_REQUIRED)
    public VirtualAppliance getVirtualAppliance()
    {
        return this.virtualAppliance;
    }

    public void setVirtualAppliance(VirtualAppliance virtualAppliance)
    {
        this.virtualAppliance = virtualAppliance;
    }

    //
    public final static String VIRTUAL_DATACENTER_PROPERTY = "virtualDatacenter";

    private final static boolean VIRTUAL_DATACENTER_REQUIRED = false;

    private final static String VIRTUAL_DATACENTER_ID_COLUMN = "idVirtualDataCenter";

    @JoinColumn(name = VIRTUAL_DATACENTER_ID_COLUMN)
    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_virtualDatacenter")
    private VirtualDatacenter virtualDatacenter;

    @Required(value = VIRTUAL_DATACENTER_REQUIRED)
    public VirtualDatacenter getVirtualDatacenter()
    {
        return this.virtualDatacenter;
    }

    public void setVirtualDatacenter(VirtualDatacenter virtualDatacenter)
    {
        this.virtualDatacenter = virtualDatacenter;
    }

    public final static String ID_VM_PROPERTY = "idVm";

    private final static String ID_VM_COLUMN = "idVM";

    private final static int ID_VM_MIN = Integer.MIN_VALUE;

    private final static int ID_VM_MAX = Integer.MAX_VALUE;

    @Column(name = ID_VM_COLUMN, nullable = true)
    @Range(min = ID_VM_MIN, max = ID_VM_MAX)
    private Integer idVm;

    public Integer getIdVm()
    {
        return this.idVm;
    }

    protected void setIdVm(Integer idVm)
    {
        this.idVm = idVm;
    }

    public final static String ID_RESOURCE_TYPE_PROPERTY = "idResourceType";

    private final static boolean ID_RESOURCE_TYPE_REQUIRED = true;

    private final static int ID_RESOURCE_TYPE_LENGTH_MIN = 0;

    private final static int ID_RESOURCE_TYPE_LENGTH_MAX = 255;

    private final static boolean ID_RESOURCE_TYPE_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String ID_RESOURCE_TYPE_COLUMN = "idResourceType";

    @Column(name = ID_RESOURCE_TYPE_COLUMN, nullable = !ID_RESOURCE_TYPE_REQUIRED, length = ID_RESOURCE_TYPE_LENGTH_MAX)
    private String idResourceType;

    @Required(value = ID_RESOURCE_TYPE_REQUIRED)
    @Length(min = ID_RESOURCE_TYPE_LENGTH_MIN, max = ID_RESOURCE_TYPE_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = ID_RESOURCE_TYPE_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getIdResourceType()
    {
        return this.idResourceType;
    }

    protected void setIdResourceType(String idResourceType)
    {
        this.idResourceType = idResourceType;
    }

    //
    public final static String RASDRAW_PROPERTY = "rasdRaw";

    private final static boolean RASDRAW_REQUIRED = false;

    private final static String RASDRAW_ID_COLUMN = "idResource";

    @JoinColumn(name = RASDRAW_ID_COLUMN)
    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_rasdraw")
    private Rasd rasdRaw;

    @Required(value = RASDRAW_REQUIRED)
    public Rasd getRasdRaw()
    {
        return this.rasdRaw;
    }

    public void setRasdRaw(Rasd rasdRaw)
    {
        this.rasdRaw = rasdRaw;
    }
}
