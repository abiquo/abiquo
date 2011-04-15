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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.testng.annotations.Test;

import com.abiquo.model.enumerator.VolumeState;
import com.abiquo.server.core.common.DefaultEntityTestBase;
import com.softwarementors.bzngine.entities.test.InstanceTester;

public class VolumeManagementTest extends DefaultEntityTestBase<VolumeManagement>
{

    @Override
    protected InstanceTester<VolumeManagement> createEntityInstanceGenerator()
    {
        return new VolumeManagementGenerator(getSeed());
    }

    @Test
    public void testStateTransitions()
    {
        VolumeManagement volume = createUniqueEntity();
        assertEquals(volume.getState(), VolumeState.NOT_MOUNTED_NOT_RESERVED);

        // Volume mount
        volume.associate();
        assertEquals(volume.getState(), VolumeState.NOT_MOUNTED_RESERVED);

        volume.mount();
        assertEquals(volume.getState(), VolumeState.MOUNTED_RESERVED);

        // Volume unmount
        volume.unmount();
        assertEquals(volume.getState(), VolumeState.NOT_MOUNTED_RESERVED);

        volume.disassociate();
        assertEquals(volume.getState(), VolumeState.NOT_MOUNTED_NOT_RESERVED);
    }

    @Test
    public void testInvalidStateTransitions()
    {
        VolumeManagement volume = createUniqueEntity();
        assertEquals(volume.getState(), VolumeState.NOT_MOUNTED_NOT_RESERVED);

        // Unassociated
        checkInvalidStateTransition(volume, "mount");
        checkInvalidStateTransition(volume, "unmount");
        checkInvalidStateTransition(volume, "disassociate");

        // Associated
        volume.associate();
        assertEquals(volume.getState(), VolumeState.NOT_MOUNTED_RESERVED);
        checkInvalidStateTransition(volume, "associate");
        checkInvalidStateTransition(volume, "unmount");

        // Mounted
        volume.mount();
        assertEquals(volume.getState(), VolumeState.MOUNTED_RESERVED);
        checkInvalidStateTransition(volume, "associate");
        checkInvalidStateTransition(volume, "disassociate");
        checkInvalidStateTransition(volume, "mount");
    }

    private void checkInvalidStateTransition(final VolumeManagement volume, final String method)
    {
        try
        {
            Method m = volume.getClass().getMethod(method);
            m.invoke(volume);
        }
        catch (InvocationTargetException ex)
        {
            if (!(ex.getTargetException() instanceof IllegalStateException))
            {
                fail("An IllegalStateException was expected");
            }
        }
        catch (Exception ex)
        {
            fail("Could not execute the state change method");
        }
    }

}
