package com.abiquo.server.core.enterprise;

import java.util.List;

import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class PrivilegeGenerator extends DefaultEntityGenerator<Privilege>
{

    private RoleGenerator roleGenerator;

    public PrivilegeGenerator(final SeedGenerator seed)
    {
        super(seed);
        roleGenerator = new RoleGenerator(seed);
    }

    @Override
    public void assertAllPropertiesEqual(final Privilege obj1, final Privilege obj2)
    {
        AssertEx.assertPropertiesEqualSilent(obj1, obj2, Privilege.NAME_PROPERTY);
    }

    @Override
    public Privilege createUniqueInstance()
    {
        String name = newString(nextSeed(), Privilege.NAME_LENGTH_MIN, Privilege.NAME_LENGTH_MAX);

        Privilege privilege = new Privilege(name);

        return privilege;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(final Privilege entity,
        final List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

    }

    public Role createRoleUniqueInstance()
    {
        Role role = roleGenerator.createUniqueInstance();
        // role.set
        return role;
    }

}
