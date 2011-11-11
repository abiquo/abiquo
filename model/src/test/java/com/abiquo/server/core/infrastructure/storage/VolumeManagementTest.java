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

import org.testng.annotations.Test;

import com.abiquo.model.enumerator.VolumeState;
import com.abiquo.server.core.appslibrary.VirtualImage;
import com.abiquo.server.core.appslibrary.VirtualImageGenerator;
import com.abiquo.server.core.common.DefaultEntityTestBase;
import com.softwarementors.bzngine.entities.test.InstanceTester;

public class VolumeManagementTest extends DefaultEntityTestBase<VolumeManagement>
{

    private VirtualImageGenerator virtualImageGenerator;

    @Override
    protected InstanceTester<VolumeManagement> createEntityInstanceGenerator()
    {
        return new VolumeManagementGenerator(getSeed());
    }

    @Test
    public void testSizeValues()
    {
        VolumeManagement volume = createUniqueEntity();
        volume.getRasd().setLimit(null);
        volume.getRasd().setReservation(null);

        assertEquals(volume.getSizeInMB(), 0L);
        assertEquals(volume.getUsedSizeInMB(), 0L);
        assertEquals(volume.getAvailableSizeInMB(), 0L);

        volume.setSizeInMB(-1L);
        assertEquals(volume.getSizeInMB(), 0L);
        volume.setSizeInMB(0L);
        assertEquals(volume.getSizeInMB(), 0L);
        volume.setSizeInMB(1L);
        assertEquals(volume.getSizeInMB(), 1L);

        volume.setUsedSizeInMB(-1L);
        assertEquals(volume.getUsedSizeInMB(), 0L);
        volume.setUsedSizeInMB(0L);
        assertEquals(volume.getUsedSizeInMB(), 0L);
        volume.setUsedSizeInMB(1L);
        assertEquals(volume.getUsedSizeInMB(), 1L);

        volume.setAvailableSizeInMB(-1L);
        assertEquals(volume.getAvailableSizeInMB(), 0L);
        volume.setAvailableSizeInMB(0L);
        assertEquals(volume.getAvailableSizeInMB(), 0L);
        volume.setAvailableSizeInMB(1L);
        assertEquals(volume.getAvailableSizeInMB(), 1L);
    }

    @Test
    public void testNullPropertyAccess()
    {
        VolumeManagement volume = createUniqueEntity();
        volume.getRasd().setLimit(null);
        volume.getRasd().setReservation(null);

        assertNull(volume.getRasd().getLimit());
        assertNull(volume.getRasd().getReservation());
        assertEquals(volume.getSizeInMB(), 0L);
        assertEquals(volume.getAvailableSizeInMB(), 0L);
    }

    @Test
    public void testStateTransitions()
    {
        VolumeManagement volume = createUniqueEntity();
        assertEquals(volume.getState(), VolumeState.DETACHED);

        volume.associate();
        assertEquals(volume.getState(), VolumeState.ATTACHED);

        volume.disassociate();
        assertEquals(volume.getState(), VolumeState.DETACHED);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testInvalidAssociate()
    {
        VolumeManagement volume = createUniqueEntity();
        assertEquals(volume.getState(), VolumeState.DETACHED);

        volume.associate();
        assertEquals(volume.getState(), VolumeState.ATTACHED);

        volume.associate(); // This one must fail
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testInvalidDisassociate()
    {
        VolumeManagement volume = createUniqueEntity();
        assertEquals(volume.getState(), VolumeState.DETACHED);

        volume.disassociate();
    }

    @Test
    public void testIsStateful()
    {
        VolumeManagement volume = createUniqueEntity();
        volume.setVirtualImage(null);
        assertFalse(volume.isStateful());

        virtualImageGenerator = new VirtualImageGenerator(getSeed());
        VirtualImage virtualImage = virtualImageGenerator.createUniqueInstance();
        volume.setVirtualImage(virtualImage);
        assertTrue(volume.isStateful());
    }
}
