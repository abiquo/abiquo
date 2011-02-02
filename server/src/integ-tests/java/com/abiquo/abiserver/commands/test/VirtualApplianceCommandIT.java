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

package com.abiquo.abiserver.commands.test;

import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.abiquo.abiserver.commands.test.data.LoginProvider;
import com.abiquo.abiserver.commands.test.data.VirtualDataProvider;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.abiserver.pojo.virtualappliance.VirtualDataCenter;
import com.abiquo.abiserver.services.flex.VirtualApplianceService;

/**
 * The aim of this class is to test the VirtualDataCenter creation The steps are the following:<br>
 * 1-Login in to app<br>
 * 2- Create the virtual Datacenter<br>
 * 3- Modify this virtual Datacenter<br>
 * 4- Delete this virtual DataCenter<br>
 * 5- Logout<br>
 * This class require DB connection with the basic data provided by sql script (admin user, abiquo
 * enterprise, datacenter myDatacenter)
 * 
 * @author xfernandez
 */
public class VirtualApplianceCommandIT
{

    private static VirtualDataCenter vdc = null;

    private BasicResult basicResult = new BasicResult();

    private static VirtualApplianceService vaServices = null;

    /**
     * Log into app
     * 
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass()
    {
        LoginProvider.doLogin();
        vdc = VirtualDataProvider.createVirtualDataCenter();
        vaServices = new VirtualApplianceService();
    }

    /**
     * Logout
     * 
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass()
    {
        LoginProvider.doLogout();
    }

    /**
     * Test method for
     * {@link com.abiquo.abiserver.commands.VirtualApplianceCommand#createVirtualDataCenter(com.abiquo.abiserver.pojo.authentication.UserSession, com.abiquo.abiserver.pojo.virtualappliance.VirtualDataCenter)}
     * .
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testCreateVirtualDataCenter()
    {
        final DataResult<VirtualDataCenter> dataResult =
            (DataResult<VirtualDataCenter>) vaServices.createVirtualDataCenter(
                LoginProvider.getUser(),
                vdc);

        vdc = dataResult.getData();

        assertTrue("VCD created correctly", dataResult.getSuccess());
    }

    /**
     * Test method for
     * {@link com.abiquo.abiserver.commands.VirtualApplianceCommand#editVirtualDataCenter(com.abiquo.abiserver.pojo.authentication.UserSession, com.abiquo.abiserver.pojo.virtualappliance.VirtualDataCenter)}
     * .
     */
    @Test
    public void testEditVirtualDataCenter()
    {
        final VirtualDataCenter vdc2 = vdc;

        vdc2.setName("VirtualDataCenterModified");

        vdc2.setHyperType(VirtualDataProvider.getExistentHyperType());

        setBasicResult(vaServices.editVirtualDataCenter(LoginProvider.getUser(), vdc2));

        vdc = vdc2;

        assertTrue("VCD edited correctly", getBasicResult().getSuccess());
    }

    /**
     * Test method for
     * {@link com.abiquo.abiserver.commands.VirtualApplianceCommand#deleteVirtualDataCenter(com.abiquo.abiserver.pojo.authentication.UserSession, com.abiquo.abiserver.pojo.virtualappliance.VirtualDataCenter)}
     * .
     */
    @Test
    public void testDeleteVirtualDataCenter()
    {

        basicResult = vaServices.deleteVirtualDataCenter(LoginProvider.getUser(), vdc);

        assertTrue("VCD deleted correctly", getBasicResult().getSuccess());
    }

    private BasicResult getBasicResult()
    {
        return basicResult;
    }

    private void setBasicResult(final BasicResult basicResult)
    {
        this.basicResult = basicResult;
    }

}
