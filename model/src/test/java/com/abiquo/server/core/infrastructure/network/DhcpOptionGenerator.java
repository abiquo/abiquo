package com.abiquo.server.core.infrastructure.network;

import java.util.List;

import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.softwarementors.bzngine.entities.PersistentEntity;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class DhcpOptionGenerator extends DefaultEntityGenerator<DhcpOption>
{

    public DhcpOptionGenerator(final SeedGenerator seed)
    {
        super(seed);

    }

    @Override
    public void assertAllPropertiesEqual(final DhcpOption obj1, final DhcpOption obj2)
    {
        AssertEx.assertPropertiesEqualSilent(obj1, obj2, PersistentEntity.ID_PROPERTY);
    }

    @Override
    public DhcpOption createUniqueInstance()
    {

        String address = "192.168.1.0";
        String gateway = "192.168.1.1";
        Integer mask = 24;
        String netmask = "255.255.255.0";
        Integer option = 121;

        DhcpOption dhcpOption = new DhcpOption(option, address, mask, netmask, gateway, netmask);

        return dhcpOption;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(final DhcpOption entity,
        final List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

    }

}
