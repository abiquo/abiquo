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

package com.abiquo.scheduler;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.abiquo.api.common.AbstractGeneratorTest;
import com.abiquo.api.common.Assert;
import com.abiquo.server.core.infrastructure.Rack;

public class ResourceUpgradeUseTest extends AbstractGeneratorTest
{
    ResourceUpgradeUse resourceUpgrade;

    @BeforeTest
    public void setUp() throws Exception
    {
        resourceUpgrade = new ResourceUpgradeUse();
    }

    @AfterTest
    public void tearDown()
    {
        resourceUpgrade = null;
    }

    @Test
    public void testGetFreeVLANFromUsedList()
    {
        List<Integer> vlanListUsed = new ArrayList<Integer>();
        int freeVlan = 0;
        Rack rack = rackGenerator.createUniqueInstance();
        rack.setVlanIdMin(2);
        rack.setVlanIdMax(10);
        
        rack.setVlansIdAvoided("");
        int[] arrayInt7 = {1, 2};
        vlanListUsed = new ArrayList<Integer>();
        arrayToList(arrayInt7, vlanListUsed);
        freeVlan = resourceUpgrade.getFreeVLANFromUsedList(vlanListUsed, rack);
        Assert.assertEquals(3, freeVlan);
        
        rack.setVlansIdAvoided("");
        vlanListUsed = new ArrayList<Integer>();
        int[] arrayInt = {3, 4, 5};
        arrayToList(arrayInt, vlanListUsed);
        freeVlan = resourceUpgrade.getFreeVLANFromUsedList(vlanListUsed, rack);
        Assert.assertEquals(2, freeVlan);

        rack.setVlansIdAvoided("");
        int[] arrayInt2 = {2, 4, 5};
        vlanListUsed = new ArrayList<Integer>();
        arrayToList(arrayInt2, vlanListUsed);
        freeVlan = resourceUpgrade.getFreeVLANFromUsedList(vlanListUsed, rack);
        Assert.assertEquals(3, freeVlan);

        rack.setVlansIdAvoided("2");
        int[] arrayInt3 = {4, 5};
        vlanListUsed = new ArrayList<Integer>();
        arrayToList(arrayInt3, vlanListUsed);
        freeVlan = resourceUpgrade.getFreeVLANFromUsedList(vlanListUsed, rack);
        Assert.assertEquals(3, freeVlan);

        rack.setVlansIdAvoided("2");
        int[] arrayInt4 = {2, 3, 4, 5};
        vlanListUsed = new ArrayList<Integer>();
        arrayToList(arrayInt4, vlanListUsed);
        freeVlan = resourceUpgrade.getFreeVLANFromUsedList(vlanListUsed, rack);
        Assert.assertEquals(6, freeVlan);

        rack.setVlansIdAvoided("2-8");
        int[] arrayInt5 = {};
        vlanListUsed = new ArrayList<Integer>();
        arrayToList(arrayInt5, vlanListUsed);
        freeVlan = resourceUpgrade.getFreeVLANFromUsedList(vlanListUsed, rack);
        Assert.assertEquals(9, freeVlan);

         rack.setVlansIdAvoided("2-8,9");
         int[] arrayInt6 = {};
         vlanListUsed = new ArrayList<Integer>();
         arrayToList(arrayInt6, vlanListUsed);
         freeVlan = resourceUpgrade.getFreeVLANFromUsedList(vlanListUsed, rack);
         Assert.assertEquals(10, freeVlan);
         

        // rack.setVlansIdAvoided("2-10");
        // int[] arrayInt7 = {};
        // vlanListUsed = new ArrayList<Integer>();
        // arrayToList(arrayInt7, vlanListUsed);
        // freeVlan = resourceUpgrade.getFreeVLANFromUsedList(vlanListUsed, rack);
        // Assert.assertEquals(10, freeVlan);

    }

    private void arrayToList(int[] arrayInt, List<Integer> listInt)
    {
        int[] array = arrayInt;
        for (int i = 0; i < array.length; i++)
        {
            int j = array[i];
            listInt.add(j);
        }

    }

}
