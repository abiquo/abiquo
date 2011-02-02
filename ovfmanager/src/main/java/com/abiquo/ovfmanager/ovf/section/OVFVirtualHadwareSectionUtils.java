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

package com.abiquo.ovfmanager.ovf.section;

import org.dmtf.schemas.ovf.envelope._1.RASDType;
import org.dmtf.schemas.ovf.envelope._1.VSSDType;
import org.dmtf.schemas.ovf.envelope._1.VirtualHardwareSectionType;

public class OVFVirtualHadwareSectionUtils
{
	public static String OVF_DISK_URI = "ovf:/disk/";
	public static String OVF_FILE_URI = "ovf:/file/";

    public static VirtualHardwareSectionType createVirtualHardwareSection(VSSDType vssd,String info, String transport)
    {   
        // TODO required ?
        
        // TODO create a Transport enumeration ????
        VirtualHardwareSectionType vhsection = new VirtualHardwareSectionType();
        
        if(vssd != null)
        {           
            vhsection.setSystem(vssd);
        }
        if(transport != null)
        {
            vhsection.setTransport(transport);
        }
        
        return vhsection;
        
    }

    
    public static void addRASD(VirtualHardwareSectionType vhsection, RASDType rasd)
    {
        vhsection.getItem().add(rasd);
    }

    
}
