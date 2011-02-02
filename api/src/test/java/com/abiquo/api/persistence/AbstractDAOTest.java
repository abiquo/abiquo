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

package com.abiquo.api.persistence;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.testng.annotations.Test;

public abstract class AbstractDAOTest extends AbstractJpaDBUnitTest
{
    protected abstract JpaDAO getDao();

    protected abstract Object getPersistentObject();

    protected abstract String compareMethod();

    @Test(enabled = false)
    public void listAll() throws Exception
    {
        assertEquals(true, getDao().findAll().size() > 0);
    }

    @Test(enabled = false)
    public void findOne() throws Exception
    {
        assertNotNull(getDao().findById(1));
    }

    @Test(enabled = false)
    public void persist() throws Exception
    {

        Object toPersist = getPersistentObject();

        Object persistent = getDao().makePersistent(toPersist);

        assertResourceEquals(toPersist, persistent);
    }

    @Test(enabled = false)
    public void remove() throws Exception
    {
        int originalSize = getDao().findAll().size();
        getDao().makeTransient(getDao().findById(1));
        assertEquals(true, getDao().findAll().size() == (originalSize - 1));
    }

    private void assertResourceEquals(Object toPersist, Object persistent) throws Exception
    {
        assertEquals(toPersist.getClass().getMethod(compareMethod(), new Class[0]).invoke(
            toPersist, new Object[0]), persistent.getClass().getMethod(compareMethod(),
            new Class[0]).invoke(persistent, new Object[0]));
    }

}
