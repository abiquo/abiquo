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
import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.abiquo.abiserver.commands.test.data.InfrastructureDataProvider;
import com.abiquo.abiserver.commands.test.data.LoginProvider;
import com.abiquo.abiserver.pojo.infrastructure.DataCenter;
import com.abiquo.abiserver.pojo.infrastructure.HyperVisor;
import com.abiquo.abiserver.pojo.infrastructure.PhysicalMachine;
import com.abiquo.abiserver.pojo.infrastructure.PhysicalMachineCreation;
import com.abiquo.abiserver.pojo.infrastructure.Rack;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.abiserver.services.flex.InfrastructureService;

/**
 * Integration class that test the infrastructure manager section.
 * 
 * @author xfernandez
 */
public class InfrastructureCommandIT
{
    /**
     * DataCenter object used on the class.
     */
    private static DataCenter dataCenter = null;

    /**
     * Rack object used on this class;
     */
    private static Rack rack = null;

    /**
     * Physical Machine object used on this class;
     */
    private static PhysicalMachine pMachine = null;

    /**
     * BasicResult object.
     */
    private BasicResult basicResult = new BasicResult();

    /**
     * Infrastructure services
     */
    private static InfrastructureService infraService = null;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass()
    {
        LoginProvider.doLogin();
        dataCenter = InfrastructureDataProvider.createDataCenter();
        rack = InfrastructureDataProvider.createRack();
        pMachine = InfrastructureDataProvider.createPhysicalMachine();

        infraService = new InfrastructureService();
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass()
    {
        LoginProvider.doLogout();
    }

    /**
     * Test method for {@link com.abiquo.abiserver.commands.InfrastructureCommand#createDataCenter(com.abiquo.abiserver.pojo.authentication.UserSession, com.abiquo.abiserver.pojo.infrastructure.DataCenter)}.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testCreateDataCenter()
    {
        final DataResult<DataCenter> dataResult =
            (DataResult<DataCenter>) infraService.createDataCenter(
                LoginProvider.getUser(), dataCenter);

        dataCenter = dataResult.getData();

        assertTrue("dataCenter created correctly", dataResult.getSuccess());
    }

    /**
     * Test method for
     * {@link com.abiquo.abiserver.commands.InfrastructureCommand#createRack(com.abiquo.abiserver.pojo.authentication.UserSession, com.abiquo.abiserver.pojo.infrastructure.Rack)}
     * .
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testCreateRack()
    {
        rack.setDataCenter(dataCenter);
        final DataResult<Rack> dataResult =
            (DataResult<Rack>) infraService.createRack(LoginProvider.getUser(), rack);

        rack = dataResult.getData();

        assertTrue("Rack created correctly", dataResult.getSuccess());
    }

    /**
     * Test method for
     * {@link com.abiquo.abiserver.commands.InfrastructureCommand#createPhysicalMachine(com.abiquo.abiserver.pojo.authentication.UserSession, com.abiquo.abiserver.pojo.infrastructure.PhysicalMachineCreation)}
     * .
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testCreatePhysicalMachine()
    {
        pMachine.setAssignedTo(rack);
        pMachine.setDataCenter(dataCenter);

        final PhysicalMachineCreation pMachineCreation = new PhysicalMachineCreation();
        pMachineCreation.setPhysicalMachine(pMachine);
        pMachineCreation.setHypervisors(new ArrayList<HyperVisor>());

        final DataResult<PhysicalMachineCreation> dataResult =
            (DataResult<PhysicalMachineCreation>) infraService
.createPhysicalMachine(LoginProvider.getUser(),
                pMachineCreation);

        pMachine = ((PhysicalMachineCreation) dataResult.getData()).getPhysicalMachine();

        assertTrue("PhysicalMachine created correctly", dataResult.getSuccess());
    }

    /**
     * Test method for
     * {@link com.abiquo.abiserver.commands.InfrastructureCommand#editRack(com.abiquo.abiserver.pojo.authentication.UserSession, com.abiquo.abiserver.pojo.infrastructure.Rack)}
     * .
     */
    @Test
    public void testEditRack()
    {
        fail("test EditRack Not yet implemented");
    }

    /**
     * Test method for
     * {@link com.abiquo.abiserver.commands.InfrastructureCommand#editPhysicalMachine(com.abiquo.abiserver.pojo.authentication.UserSession, com.abiquo.abiserver.pojo.infrastructure.PhysicalMachine)}
     * .
     */
    @Test
    public void testEditPhysicalMachine()
    {
        fail("test EditPhysicalMachine Not yet implemented");
    }

    /**
     * Test method for
     * {@link com.abiquo.abiserver.commands.InfrastructureCommand#editDataCenter(com.abiquo.abiserver.pojo.authentication.UserSession, com.abiquo.abiserver.pojo.infrastructure.DataCenter)}
     * .
     */
    @Test
    public void testEditDataCenter()
    {
        final DataCenter dCenterModified = dataCenter;

        dCenterModified.setName("DataCenterModified");
        dCenterModified.setSituation("Gava");


        setBasicResult(infraService.editDataCenter(LoginProvider.getUser(), dCenterModified));

        dataCenter = dCenterModified;

        assertTrue("DataCenter edited correctly", getBasicResult().getSuccess());
    }
    
    /**
     * Test method for 
     * {@link com.abiquo.abiserver.commands.InfrastructureCommand#getHypervisorsTypeByDataCenter(com.abiquo.abiserver.pojo.authentication.UserSession, com.abiquo.abiserver.pojo.infrastructure.DataCenter)}
     * .
     */
    @Test
    public void getHypervisorsTypeByDataCenter()
    {
        final DataCenter myDataCenter = new DataCenter();

        myDataCenter.setId(1);
        myDataCenter.setName("myDataCenter");
        myDataCenter.setSituation("Barcelona");


        setBasicResult(infraService.getHypervisorsTypeByDataCenter(LoginProvider.getUser(), myDataCenter));

        assertTrue("DataCenter edited correctly", getBasicResult().getSuccess());
    }

    /**
     * Test method for {@link com.abiquo.abiserver.commands.InfrastructureCommand#deleteDataCenter(com.abiquo.abiserver.pojo.infrastructure.DataCenter)}.
     */
    @Test
    public void testDeleteDataCenter()
    {
        basicResult = infraService.deleteDataCenter(LoginProvider.getUser(), dataCenter);

        assertTrue("DataCenter deleted correctly", getBasicResult().getSuccess());
    }



    /**
     * Test method for {@link com.abiquo.abiserver.commands.InfrastructureCommand#deleteRack(com.abiquo.abiserver.pojo.infrastructure.Rack)}.
     */
    @Test
    public void testDeleteRack()
    {
        fail("test DeleteRack Not yet implemented");
    }


    /**
     * Test method for {@link com.abiquo.abiserver.commands.InfrastructureCommand#deletePhysicalMachine(com.abiquo.abiserver.pojo.infrastructure.PhysicalMachine)}.
     */
    @Test
    public void testDeletePhysicalMachine()
    {
        fail("test DeletePhysicalMachine Not yet implemented");
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
