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

package com.abiquo.server.core.enterprise;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;

import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class UserGenerator extends DefaultEntityGenerator<User>
{

    EnterpriseGenerator enterpriseGenerator;

    RoleGenerator roleGenerator;

    public UserGenerator(SeedGenerator seed)
    {
        super(seed);

        enterpriseGenerator = new EnterpriseGenerator(seed);

        roleGenerator = new RoleGenerator(seed);

    }

    @Override
    public void assertAllPropertiesEqual(User obj1, User obj2)
    {
        AssertEx.assertPropertiesEqualSilent(obj1, obj2, User.NAME_PROPERTY, User.NICK_PROPERTY,
            User.LOCALE_PROPERTY, User.PASSWORD_PROPERTY, User.SURNAME_PROPERTY,
            User.ACTIVE_PROPERTY, User.EMAIL_PROPERTY);
    }

    @Override
    public User createUniqueInstance()
    {
        Enterprise enterprise = enterpriseGenerator.createUniqueInstance();
        return createInstance(enterprise);
    }

    public User createInstance(Enterprise enterprise)
    {
        Role role = roleGenerator.createUniqueInstance();

        return createInstance(enterprise, role);
    }

    public User createInstance(Role role)
    {
        Enterprise enterprise = enterpriseGenerator.createUniqueInstance();

        return createInstance(enterprise, role);
    }

    public User createInstance(Enterprise enterprise, Role role)
    {
        String password = newString(nextSeed(), 0, 255);

        return createInstance(enterprise, role, password);
    }

    public User createInstance(Enterprise enterprise, Role role, String password)
    {
        String name = newString(nextSeed(), 0, 255);
        String surname = newString(nextSeed(), 0, 255);
        String email = "abc@example.com";
        String nick = newString(nextSeed(), 0, 255);
        return createInstance(enterprise, role, password, name, surname, email, nick);
    }

    public User createInstance(Enterprise enterprise, Role role, String nick, String password)
    {
        String name = newString(nextSeed(), 0, 255);
        String surname = newString(nextSeed(), 0, 255);
        String email = "abc@example.com";
        return createInstance(enterprise, role, password, name, surname, email, nick);
    }

    public User createInstance(Enterprise enterprise, Role role, String password, String name,
        String surname, String email, String nick)
    {
        String locale = newString(nextSeed(), 0, 255);

        User user =
            new User(enterprise,
                role,
                name,
                surname,
                email,
                nick,
                DigestUtils.md5Hex(password),
                locale);

        user.setActive(1);
        return user;
    }

    public User createUserWithSession()
    {
        User user = createUniqueInstance();
        String key = newString(nextSeed(), 0, 255);

        Calendar cal = GregorianCalendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 5);

        Date expireDate = cal.getTime();

        Session session = new Session(user, key, expireDate);
        user.addSession(session);

        return user;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(User entity, List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

        Enterprise enterprise = entity.getEnterprise();
        enterpriseGenerator.addAuxiliaryEntitiesToPersist(enterprise, entitiesToPersist);
        entitiesToPersist.add(enterprise);

        Role role = entity.getRole();
        roleGenerator.addAuxiliaryEntitiesToPersist(role, entitiesToPersist);
        entitiesToPersist.add(role);

    }

}
