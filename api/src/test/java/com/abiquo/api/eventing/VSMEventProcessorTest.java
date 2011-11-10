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

package com.abiquo.api.eventing;

import static com.abiquo.api.eventing.VirtualMachineStage.createVirtualMachineStage;
import static com.abiquo.server.core.cloud.VirtualMachineState.LOCKED;
import static com.abiquo.server.core.cloud.VirtualMachineState.NOT_ALLOCATED;
import static com.abiquo.server.core.cloud.VirtualMachineState.OFF;
import static com.abiquo.server.core.cloud.VirtualMachineState.ON;
import static com.abiquo.server.core.cloud.VirtualMachineState.PAUSED;
import static com.abiquo.testng.TestConfig.BASIC_UNIT_TESTS;
import static com.abiquo.vsm.events.VMEventType.CREATED;
import static com.abiquo.vsm.events.VMEventType.DESTROYED;
import static com.abiquo.vsm.events.VMEventType.MOVED;
import static com.abiquo.vsm.events.VMEventType.POWER_OFF;
import static com.abiquo.vsm.events.VMEventType.POWER_ON;
import static com.abiquo.vsm.events.VMEventType.RESUMED;
import static com.abiquo.vsm.events.VMEventType.SAVED;
import static com.abiquo.vsm.events.VMEventType.UNKNOWN;

import javax.persistence.EntityManager;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import com.abiquo.vsm.events.VMEventType;

/**
 * Test cases for {@link VSMEventProcessor} in community version.
 * 
 * @author eruiz
 */
@Test(groups = {BASIC_UNIT_TESTS})
public class VSMEventProcessorTest extends VSMEventProcessorTestBase
{
    protected static final String INVALID_EVENT = "INVALID_EVENT";

    @Test
    public void test_lockedToOn()
    {
        assertStage(createVirtualMachineStage().in(LOCKED).expecting(ON).onEvent(POWER_ON));
        assertStage(createVirtualMachineStage().in(LOCKED).expecting(ON).onEvent(RESUMED));
    }

    @Test
    public void test_lockedToOff()
    {
        assertStage(createVirtualMachineStage().in(LOCKED).expecting(OFF).onEvent(POWER_OFF));
    }

    @Test
    public void test_lockedToNotAllocated()
    {
        assertStageAndDestroyed(createVirtualMachineStage().in(LOCKED).expecting(NOT_ALLOCATED)
            .onEvent(DESTROYED));
    }

    @Test
    public void test_lockedToLock()
    {
        assertStage(createVirtualMachineStage().in(LOCKED).expecting(LOCKED).onEvent(MOVED));
    }

    @Test
    public void test_lockedToPaused()
    {
        assertStage(createVirtualMachineStage().in(LOCKED).expecting(PAUSED)
            .onEvent(VMEventType.PAUSED));
    }

    @Test
    public void test_ignoredEvents()
    {
        assertStage(createVirtualMachineStage().in(LOCKED).expecting(LOCKED).onEvent(INVALID_EVENT));
        assertStage(createVirtualMachineStage().in(LOCKED).expecting(LOCKED).onEvent(CREATED));
        assertStage(createVirtualMachineStage().in(LOCKED).expecting(LOCKED).onEvent(SAVED));
        assertStage(createVirtualMachineStage().in(LOCKED).expecting(LOCKED).onEvent(UNKNOWN));
    }

    @Override
    protected VSMEventProcessor getEventingProcessor(EntityManager em)
    {
        return new VSMEventProcessor(em);
    }

    @Override
    @AfterMethod(groups = {BASIC_UNIT_TESTS})
    public void tearDown()
    {
        super.tearDown();
    }
}
