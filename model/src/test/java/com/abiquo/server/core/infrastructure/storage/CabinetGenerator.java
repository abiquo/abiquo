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

import java.util.List;

import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.DatacenterGenerator;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class CabinetGenerator extends DefaultEntityGenerator<Cabinet>
{

    
      DatacenterGenerator datacenterGenerator;
    

    public CabinetGenerator(SeedGenerator seed)
    {
        super(seed);
        
          datacenterGenerator = new DatacenterGenerator(seed);
        
    }

    @Override
    public void assertAllPropertiesEqual(Cabinet obj1, Cabinet obj2)
    {
      AssertEx.assertPropertiesEqualSilent(obj1, obj2, Cabinet.MANAGEMENT_PORT_PROPERTY,Cabinet.NAME_PROPERTY,Cabinet.ISCSI_IP_PROPERTY,Cabinet.STORAGE_TECHNOLOGY_PROPERTY,Cabinet.MANAGEMENT_IP_PROPERTY,Cabinet.ISCSI_PORT_PROPERTY);
    }

    @Override
    public Cabinet createUniqueInstance()
    {
        // FIXME: Write here how to create the pojo

        Cabinet cabinet = new Cabinet();

        
        Datacenter datacenter = datacenterGenerator.createUniqueInstance();
        cabinet.setDatacenter(datacenter);
        cabinet.setIscsiIp("192.168.1.1");
        cabinet.setIscsiPort(80);
        cabinet.setManagementIp("102.168.1.2");
        cabinet.setManagementPort(8080);
        cabinet.setName("LoPutoCabinet");        

        return cabinet;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(Cabinet entity, List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);
        
        
          Datacenter datacenter = entity.getDatacenter();
          datacenterGenerator.addAuxiliaryEntitiesToPersist(datacenter, entitiesToPersist);
          entitiesToPersist.add(datacenter);
        
    }

}
