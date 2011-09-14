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

package com.abiquo.server.core.pricing;

import java.util.List;

import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class CurrencyGenerator extends DefaultEntityGenerator<Currency>
{

    public CurrencyGenerator(final SeedGenerator seed)
    {
        super(seed);

    }

    @Override
    public void assertAllPropertiesEqual(final Currency obj1, final Currency obj2)
    {
        AssertEx.assertPropertiesEqualSilent(obj1, obj2, Currency.NAME_PROPERTY,
            Currency.SYMBOL_PROPERTY);
    }

    @Override
    public Currency createUniqueInstance()
    {
        String name = newString(nextSeed(), 0, 255);
        String symbol = newString(nextSeed(), 0, 5);

        Currency currency = new Currency(name, symbol, 2);

        return currency;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(final Currency entity,
        final List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

    }

}
