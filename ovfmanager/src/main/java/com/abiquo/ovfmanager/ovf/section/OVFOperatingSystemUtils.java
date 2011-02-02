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

import org.dmtf.schemas.ovf.envelope._1.OperatingSystemSectionType;

import com.abiquo.ovfmanager.cim.CIMTypesUtils;
import com.abiquo.ovfmanager.cim.CIMTypesUtils.OperatingSystemTypeEnum;
import com.abiquo.ovfmanager.ovf.exceptions.RequiredAttributeException;

public class OVFOperatingSystemUtils
{
    
    /**
     * 
     * VERSION: A string describing the Operating System's version number. The format of the version information is as follows: <Major Number>.<Minor Number>.<Revision> or <Major Number>.<Minor Number>.<Revision Letter>.
     * */
    public static OperatingSystemSectionType createOperatingSystemSection(OperatingSystemTypeEnum osType, String version, String description, String info) throws RequiredAttributeException
    {
        // TODO required
        
        OperatingSystemSectionType ossection = new OperatingSystemSectionType();
     
        
        if(osType == null)
        {
            throw new RequiredAttributeException("Operating System Id for OperatingSystemSection");
        }
        
        ossection.setInfo(CIMTypesUtils.createMsg(info, null)); // TODO info message id
        
        ossection.setId(osType.getNumericOSType());
        ossection.setDescription(CIMTypesUtils.createMsg(description, null)); // TODO message id
        // TODO if description == null set the enumeration name               
        ossection.setVersion(version); // TODO validate version format ??
        
        return ossection;
    }
    

}
