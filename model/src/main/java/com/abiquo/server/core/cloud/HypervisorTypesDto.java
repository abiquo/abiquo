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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.model.transport.WrapperDto;

/**
 * Represent a collection of HypervisorTypes
 */
@XmlRootElement(name = "hypervisorstype")
public class HypervisorTypesDto extends WrapperDto<HypervisorTypeDto>
{
    /**
     * 
     */
    private static final long serialVersionUID = -4460166562066326383L;

    @Override
    @XmlElement(name = "hypervisortype")
    public List<HypervisorTypeDto> getCollection()
    {
        return collection;
    }

    public void setCollection(final ArrayList<HypervisorType> arrayList)
    {
        // TODO Auto-generated method stub

        for (HypervisorType ht : arrayList)
        {
            HypervisorTypeDto aux = new HypervisorTypeDto();
            aux.setBaseFormat(ht.baseFormat);
            aux.setCompatibilityTable(ht.compatibilityTable);
            aux.setDefaultPort(ht.defaultPort);
            aux.setId(ht.id());
            collection.add(aux);
        }

    }
}
