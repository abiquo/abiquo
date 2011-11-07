package com.abiquo.server.core.infrastructure.network;

import static org.testng.Assert.assertEquals;

import java.util.List;

import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class DhcpOptionGenerator extends DefaultEntityGenerator<DhcpOption>
{

    

    public DhcpOptionGenerator(SeedGenerator seed)
    {
        super(seed);
        
    }

    @Override
    public void assertAllPropertiesEqual(DhcpOption obj1, DhcpOption obj2)
    {
      AssertEx.assertPropertiesEqualSilent(obj1, obj2, DhcpOption.OPTION_PROPERTY,DhcpOption.DESCRIPTION_PROPERTY);
    }

    @Override
    public DhcpOption createUniqueInstance()
    {
        // FIXME: Write here how to create the pojo

        DhcpOption dhcpOption = new DhcpOption();

        

        return dhcpOption;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(DhcpOption entity, List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);
        
        
    }

}
