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

package com.abiquo.abiserver.commands.test.data;

import com.abiquo.abiserver.pojo.infrastructure.HyperVisorType;
import com.abiquo.abiserver.pojo.user.Enterprise;
import com.abiquo.abiserver.pojo.virtualappliance.VirtualDataCenter;
import com.abiquo.abiserver.pojo.virtualhardware.ResourceAllocationLimit;

/**
 * Data for virtual Objects.
 * 
 * @author xfernandez
 */
public final class VirtualDataProvider extends DataProvider
{

    /**
     * Private class constructor
     */
    private VirtualDataProvider()
    {
        // Private Constructor
        super();
    }

    /**
     * Basic Method to create an Standard VirtualDataCenter like the GUI
     */
    public static VirtualDataCenter createVirtualDataCenter()
    {

        final VirtualDataCenter vDataCenter = new VirtualDataCenter();
        vDataCenter.setName("VirtualDataCenter");
        vDataCenter.setIdDataCenter(1);
        final HyperVisorType htype = new HyperVisorType();
        htype.setId(1);
        htype.setName("KVM");
        vDataCenter.setHyperType(htype);
        final Enterprise enterprise = new Enterprise();
        enterprise.setId(1);
        enterprise.setName("abiquo");
        enterprise.setDeleted(false);
        enterprise.setResourceAllocationLimit(new ResourceAllocationLimit());
        vDataCenter.setEnterprise(enterprise);

        return vDataCenter;

    }

    /**
     * This method creates an HyperType of a HyperVisor existent in database
     */
    public static HyperVisorType getExistentHyperType()
    {
        final HyperVisorType htype = new HyperVisorType();
        htype.setId(2);
        htype.setName("VBOX");

        return htype;
    }
}
