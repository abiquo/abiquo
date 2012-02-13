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

package com.abiquo.server.core.infrastructure;

import java.util.Set;

import org.testng.annotations.Test;

import com.abiquo.model.transport.error.CommonError;
import com.abiquo.server.core.common.DefaultEntityTestBase;
import com.softwarementors.bzngine.entities.test.InstanceTester;

public class RackTest extends DefaultEntityTestBase<Rack>
{

    @Override
    protected InstanceTester<Rack> createEntityInstanceGenerator()
    {
        return new RackGenerator(getSeed());
    }

    @Test
    public void testVlanRange()
    {
        Rack rack = eg().createUniqueInstance();

        rack.setVlanIdMin(Rack.VLAN_ID_MIN_MIN - 1);
        assertFalse(rack.isValid());
        rack.setVlanIdMin(Rack.VLAN_ID_MIN_MIN);
        assertTrue(rack.isValid());

        rack.setVlanIdMax(Rack.VLAN_ID_MAX_MAX + 1);
        assertFalse(rack.isValid());
        rack.setVlanIdMax(Rack.VLAN_ID_MAX_MAX);
        assertTrue(rack.isValid());

        rack.setNrsq(Rack.NRSQ_MAX + 1);
        assertFalse(rack.isValid());
        rack.setNrsq(Rack.NRSQ_MAX);
        assertTrue(rack.isValid());

        rack.setVlanPerVdcReserved(Rack.VLAN_PER_VDC_EXPECTED_MIN - 1);
        assertFalse(rack.isValid());
        rack.setVlanPerVdcReserved(Rack.VLAN_PER_VDC_EXPECTED_MIN);
        assertTrue(rack.isValid());
    }

    @Test
    public void testVlanIdRange()
    {
        Rack rack = createUniqueEntity();
        rack.setVlanIdRange(Rack.VLAN_ID_MIN_DEFAULT_VALUE, Rack.VLAN_ID_MAX_DEFAULT_VALUE);
        assertTrue(rack.isValid());

        rack.setVlanIdRange(5, 3);

        Set<CommonError> errors = rack.getValidationErrors();
        assertFalse(errors.isEmpty());
        assertEquals(errors.iterator().next().getCode(), "CONSTR-VLANIDRANGE");
    }
}
