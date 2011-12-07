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

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.FilterDefs;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.common.DefaultEntityBase;
import com.softwarementors.validation.constraints.LeadingOrTrailingWhitespace;
import com.softwarementors.validation.constraints.Required;

@Entity
@FilterDefs({@FilterDef(name = RasdManagement.NOT_TEMP),
    @FilterDef(name = RasdManagement.ONLY_TEMP)})
@Filters({@Filter(name = RasdManagement.NOT_TEMP, condition = "temporal is null"),
    @Filter(name = RasdManagement.ONLY_TEMP, condition = "temporal is not null")})
@Table(name = RasdManagement.TABLE_NAME)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "idResourceType", discriminatorType = DiscriminatorType.STRING)
public class RasdManagement extends DefaultEntityBase
{
    /** The first attachment sequence number to be used. */
    public static final int FIRST_ATTACHMENT_SEQUENCE = 1;

    public static final String TABLE_NAME = "rasd_management";

    public static final String NOT_TEMP = "rasdmanagement_not_temp";
    public static final String ONLY_TEMP = "rasdmanagement_only_temp";
    // DO NOT ACCESS: present due to needs of infrastructure support. *NEVER* call from business
    // code
    protected RasdManagement()
    {
        // Just for JPA support
    }

    protected RasdManagement(final String idResourceType)
    {
        setIdResourceType(idResourceType);
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

    public void setId(final Integer id)
    {
        this.id = id;
    }

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

    public void setVirtualAppliance(final VirtualAppliance virtualAppliance)
    {
        this.virtualAppliance = virtualAppliance;
    }

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

    public void setVirtualDatacenter(final VirtualDatacenter virtualDatacenter)
    {
        this.virtualDatacenter = virtualDatacenter;
    }

    public final static String VIRTUAL_MACHINE_PROPERTY = "virtualMachine";

    private final static boolean VIRTUAL_MACHINE_REQUIRED = false;

    private final static String VIRTUAL_MACHINE_ID_COLUMN = "idVM";

    @JoinColumn(name = VIRTUAL_MACHINE_ID_COLUMN)
    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_virtualMAchine")
    private VirtualMachine virtualMachine;

    public void setVirtualMachine(final VirtualMachine virtualMachine)
    {
        this.virtualMachine = virtualMachine;
    }

    @Required(value = VIRTUAL_MACHINE_REQUIRED)
    public VirtualMachine getVirtualMachine()
    {
        return virtualMachine;
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

    public void setIdResourceType(final String idResourceType)
    {
        this.idResourceType = idResourceType;
    }

    public final static String RASD_PROPERTY = "rasd";

    private final static boolean RASD_REQUIRED = false;

    private final static String RASD_ID_COLUMN = "idResource";

    @JoinColumn(name = RASD_ID_COLUMN)
    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_rasd")
    private Rasd rasd;

    @Required(value = RASD_REQUIRED)
    public Rasd getRasd()
    {
        return this.rasd;
    }

    public void setRasd(final Rasd rasd)
    {
        this.rasd = rasd;
    }

    public final static String TEMPORAL_PROPERTY = "temporal";

    private final static String TEMPORAL_COLUMN = "temporal";

    private final static int TEMPORAL_MIN = 1;

    private final static int TEMPORAL_MAX = Integer.MAX_VALUE;

    @Column(name = TEMPORAL_COLUMN, nullable = true)
    @Range(min = TEMPORAL_MIN, max = TEMPORAL_MAX)
    private Integer temporal;

    public Integer getTemporal()
    {
        return this.temporal;
    }

    public void setTemporal(final Integer temporal)
    {
        this.temporal = temporal;
    }
    
    // **************************** Rasd delegating methods ***************************

    public String getDescription()
    {
        return getRasd().getDescription();
    }

    public void setDescription(final String description)
    {
        getRasd().setDescription(description);
    }

    public long getAttachmentOrder()
    {
        Long generation = getRasd().getGeneration();
        return generation == null ? 0L : generation;
    }

    public void setAttachmentOrder(final long order)
    {
        if (order < FIRST_ATTACHMENT_SEQUENCE)
        {
            throw new IllegalArgumentException("Attachment order should be greater or equal to "
                + FIRST_ATTACHMENT_SEQUENCE);
        }

        getRasd().setGeneration(order < 0 ? 0L : order);
    }
}
