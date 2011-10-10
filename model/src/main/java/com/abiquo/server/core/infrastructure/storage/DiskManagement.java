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

import java.util.UUID;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.annotations.ForeignKey;

import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.infrastructure.Datastore;
import com.abiquo.server.core.infrastructure.management.Rasd;
import com.abiquo.server.core.infrastructure.management.RasdManagement;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = DiskManagement.TABLE_NAME)
@DiscriminatorValue(DiskManagement.DISCRIMINATOR)
public class DiskManagement extends RasdManagement
{
    public static final String DISCRIMINATOR = "17"; // CIMResourceTypeEnum.Disk_Drive

    public static final String ALLOCATION_UNITS = "MegaBytes";

    public static final String TABLE_NAME = "disk_management";

    public static final String DISK_DEVICE_NAME = "Disk Device";

    // DO NOT ACCESS: present due to needs of infrastructure support. *NEVER* call from business
    // code
    protected DiskManagement()
    {
        // Just for JPA support
    }

    public DiskManagement(final VirtualDatacenter virtualDatacenter,
        final VirtualAppliance virtualAppliance, final VirtualMachine virtualMachine,
        final Datastore datastore, final Long size)
    {
        super(DISCRIMINATOR);

        // RasdManagement properties
        Rasd rasd =
            new Rasd(UUID.randomUUID().toString(), "Disk Device", Integer.valueOf(DISCRIMINATOR));
        rasd.setAddress(datastore.getRootPath() + "/" + datastore.getDirectory());
        rasd.setAllocationUnits(ALLOCATION_UNITS);
        rasd.setLimit(size);
        rasd.setAutomaticAllocation(0);
        rasd.setAutomaticDeallocation(0);

        setRasd(rasd);
        setVirtualDatacenter(virtualDatacenter);
        setVirtualAppliance(virtualAppliance);
        setVirtualMachine(virtualMachine);

        // Disk properties
        setDatastore(datastore);
    }

    public final static String DATASTORE_PROPERTY = "datastore";

    private final static boolean DATASTORE_REQUIRED = false;

    private final static String DATASTORE_ID_COLUMN = "idDatastore";

    @JoinColumn(name = DATASTORE_ID_COLUMN)
    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_datastore")
    private Datastore datastore;

    @Required(value = DATASTORE_REQUIRED)
    public Datastore getDatastore()
    {
        return this.datastore;
    }

    public void setDatastore(final Datastore datastore)
    {
        this.datastore = datastore;
    }

    // ********************************** Others ********************************
    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
