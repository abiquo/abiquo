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

package com.abiquo.abiserver.model;

import java.util.List;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import com.abiquo.abiserver.config.AbiConfig;
import com.abiquo.abiserver.config.AbiConfigManager;

public class AbiCloudModelTest
{
    private AbiConfig abiConfig;

    @BeforeMethod
    public void setUp() throws Exception
    {
        abiConfig = AbiConfigManager.getInstance().getAbiConfig();
    }

    @AfterMethod
    public void tearDown() throws Exception
    {
    }

    /* @Test */
    /* public void testGetFreeVLANFromUsedList() throws NotEnoughResourcesException */
    /* { */
    /* int[] arrayInt = {3, 4, 5}; */
    /* List<Integer> vlanList = new ArrayList<Integer>(); */
    /* arrayToList(arrayInt, vlanList); */
    /* int freeVlan = AbiCloudModel.getFreeVLANFromUsedList(vlanList); */
    /* Assert.assertEquals("The VLAN expected is wrong", 6, freeVlan); */

    /* int[] arrayInt2 = {4, 5}; */
    /* vlanList = new ArrayList<Integer>(); */
    /* arrayToList(arrayInt2, vlanList); */
    /* freeVlan = AbiCloudModel.getFreeVLANFromUsedList(vlanList); */
    /* Assert.assertEquals("The VLAN expected is wrong", 3, freeVlan); */

    /* abiConfig.setMinVlanTag(2); */
    /* abiConfig.setPublicVlanTag(6); */
    /* int[] arrayInt3 = {4, 5}; */
    /* vlanList = new ArrayList<Integer>(); */
    /* arrayToList(arrayInt3, vlanList); */
    /* freeVlan = AbiCloudModel.getFreeVLANFromUsedList(vlanList); */
    /* Assert.assertEquals("The VLAN expected is wrong", 2, freeVlan); */

    /* abiConfig.setMinVlanTag(2); */
    /* abiConfig.setPublicVlanTag(6); */
    /* int[] arrayInt4 = {2, 3, 4, 5}; */
    /* vlanList = new ArrayList<Integer>(); */
    /* arrayToList(arrayInt4, vlanList); */
    /* freeVlan = AbiCloudModel.getFreeVLANFromUsedList(vlanList); */
    /* Assert.assertEquals("The VLAN expected is wrong", 7, freeVlan); */

    /* abiConfig.setMinVlanTag(2); */
    /* abiConfig.setPublicVlanTag(2); */
    /* int[] arrayInt5 = {4, 3, 3}; */
    /* vlanList = new ArrayList<Integer>(); */
    /* arrayToList(arrayInt5, vlanList); */
    /* freeVlan = AbiCloudModel.getFreeVLANFromUsedList(vlanList); */
    /* Assert.assertEquals("The VLAN expected is wrong", 5, freeVlan); */

    /* abiConfig.setMinVlanTag(2); */
    /* abiConfig.setPublicVlanTag(2); */
    /* int[] arrayInt6 = {9, 11, 2, 3}; */
    /* vlanList = new ArrayList<Integer>(); */
    /* arrayToList(arrayInt6, vlanList); */
    /* freeVlan = AbiCloudModel.getFreeVLANFromUsedList(vlanList); */
    /* Assert.assertEquals("The VLAN expected is wrong", 4, freeVlan); */

    /* } */

    private void arrayToList(final int[] arrayInt, final List<Integer> listInt)
    {
        int[] array = arrayInt;
        for (int i = 0; i < array.length; i++)
        {
            int j = array[i];
            listInt.add(j);
        }

    }

}
