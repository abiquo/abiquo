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

package com.abiquo.abiserver.commands.impl.test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Set;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.DatacenterHB;
import com.abiquo.abiserver.business.hibernate.pojohb.service.RemoteServiceHB;
import com.abiquo.abiserver.commands.InfrastructureCommand;
import com.abiquo.abiserver.commands.impl.InfrastructureCommandImpl;
import com.abiquo.abiserver.exception.PersistenceException;
import com.abiquo.abiserver.persistence.DAOFactory;
import com.abiquo.abiserver.persistence.dao.infrastructure.DataCenterDAO;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAOFactory;
import com.abiquo.abiserver.pojo.authentication.UserSession;
import com.abiquo.abiserver.pojo.infrastructure.DataCenter;
import com.abiquo.abiserver.pojo.service.RemoteService;
import com.abiquo.abiserver.pojo.service.RemoteServiceType;

/**
 * The Class InfrastructureCommandTest
 */
public class InfrastructureCommandImplTest
{

    /** The infra command. */
    private InfrastructureCommand infraCommand;

    /** The data center. */
    private DataCenter dataCenter;

    /** The DAO factory */
    private DAOFactory factorytest;

    /** The user session. */
    private UserSession userSession;

    /** The virtual appliance deployer. */
    private RemoteService virtualApplianceDeployer;

    /**
     * Sets the up.
     * 
     * @throws java.lang.Exception * @throws Exception the exception
     */
    @BeforeMethod
    public void setUp() throws Exception
    {
        infraCommand = new InfrastructureCommandImpl();
        dataCenter = new DataCenter();
        dataCenter.setId(1);
        virtualApplianceDeployer = new RemoteService();
        virtualApplianceDeployer.setIdRemoteService(1);
        RemoteServiceType remoteServiceType =
            new RemoteServiceType(com.abiquo.abiserver.business.hibernate.pojohb.service.RemoteServiceType.VIRTUAL_FACTORY);
        remoteServiceType.setName("VappDeployer");
        virtualApplianceDeployer.setRemoteServiceType(remoteServiceType);
        virtualApplianceDeployer.setUri("http://localhost:8080/virtualfactory/");
        virtualApplianceDeployer.setIdDataCenter(dataCenter.getId());
        ArrayList<RemoteService> remoteServices = new ArrayList<RemoteService>();
        remoteServices.add(virtualApplianceDeployer);
        dataCenter.setName("TestDC");
        dataCenter.setRemoteServices(remoteServices);
        dataCenter.setSituation("Mislata");
        userSession = new UserSession();
        userSession.setUser("admin");
    }

    /**
     * Tear down.
     * 
     * @throws java.lang.Exception * @throws Exception the exception
     */
    @AfterMethod
    public void tearDown() throws Exception
    {
    }

    /**
     * Test create data center.
     * 
     * @throws PersistenceException the persistence exception
     */
    @Test
    public void testCreateDataCenter() throws PersistenceException
    {
        factorytest = HibernateDAOFactory.instance();

        infraCommand.createDataCenter(userSession, dataCenter);

        DataCenterDAO datacenterDAO = factorytest.getDataCenterDAO();
        DatacenterHB myDatacenter = datacenterDAO.findById(dataCenter.getId());
        Set<RemoteServiceHB> remoteServices = myDatacenter.getRemoteServicesHB();
        assertFalse(remoteServices.isEmpty());
    }

    /**
     * Test edit data center.
     * 
     * @throws PersistenceException the persistence exception
     */
    @Test
    public void testEditDataCenter() throws PersistenceException, URISyntaxException
    {
        String newUri = "http://localhost:9090/abicloud_WS/";
        factorytest = HibernateDAOFactory.instance();

        virtualApplianceDeployer.setUri(newUri);
        infraCommand.editDataCenter(userSession, dataCenter);
        DataCenterDAO datacenterDAO = factorytest.getDataCenterDAO();
        DatacenterHB myDatacenter = datacenterDAO.findById(dataCenter.getId());
        Set<RemoteServiceHB> remoteServices = myDatacenter.getRemoteServicesHB();
        for (RemoteServiceHB remoteServiceHB : remoteServices)
        {
            assertEquals("The remote service uri couln't be changed", newUri,
                remoteServiceHB.getUri());
        }
    }
}
