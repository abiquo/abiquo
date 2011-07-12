package com.abiquo.server.core.cloud;

import static org.testng.Assert.assertEquals;

import java.util.List;

import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class ChefCookbookGenerator extends DefaultEntityGenerator<ChefCookbook>
{

    VirtualMachineGenerator virtualMachineGenerator;

    public ChefCookbookGenerator(SeedGenerator seed)
    {
        super(seed);

        virtualMachineGenerator = new VirtualMachineGenerator(seed);

    }

    @Override
    public void assertAllPropertiesEqual(ChefCookbook obj1, ChefCookbook obj2)
    {
        AssertEx.assertPropertiesEqualSilent(obj1, obj2, ChefCookbook.COOKBOOK_PROPERTY);
    }

    @Override
    public ChefCookbook createUniqueInstance()
    {
        // FIXME: Write here how to create the pojo
        VirtualMachine virtualMachine = virtualMachineGenerator.createUniqueInstance();

        String cookbook = newString(nextSeed(), 0, 255);
        ChefCookbook chefCookbook = new ChefCookbook(virtualMachine, cookbook);

        return chefCookbook;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(ChefCookbook entity, List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

        VirtualMachine virtualMachine = entity.getVirtualmachine();
        virtualMachineGenerator.addAuxiliaryEntitiesToPersist(virtualMachine, entitiesToPersist);
        entitiesToPersist.add(virtualMachine);

    }

}
