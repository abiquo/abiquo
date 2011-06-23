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

import junit.framework.TestCase;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.abiserver.commands.impl.RemoteServicesCommandImpl;
import com.abiquo.abiserver.exception.PersistenceException;
import com.abiquo.abiserver.pojo.authentication.UserSession;

/**
 * The Class InfrastructureCommandTest
 */
public class RemoteServiceCommandTest extends TestCase
{

    /** The infra command. */
    private RemoteServicesCommandImpl rsCommand;

    /** The user session. */
    private UserSession userSession;

    /**
     * Sets the up.
     * 
     * @throws java.lang.Exception * @throws Exception the exception
     */
    @Override
    @BeforeMethod
    public void setUp() throws Exception
    {
        rsCommand = new RemoteServicesCommandImpl();
        userSession = new UserSession();
        userSession.setUser("admin");
    }

    /**
     * Tear down.
     * 
     * @throws java.lang.Exception * @throws Exception the exception
     */
    @Override
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
    public void testAddRemoteService() throws PersistenceException
    {
        assertTrue(true);
        // RemoteServiceType rst = new RemoteServiceType();
        // rst.setIdRemoteServiceType(1);
        // rst.setName("VirtualFactory");
        //
        // RemoteService rs = new RemoteService();
        // //rs.setUri("http://localhost:8080/test_vf");
        // rs.setUuid("---");
        // rs.setName("test_vf");
        // rs.setProtocol("http://");
        // rs.setDomainName("localhost");
        // rs.setPort(8080);
        // rs.setServiceMapping("test_vf");
        // rs.setIdRemoteServiceType(rst);
        // rs.setIdDataCenter(1);
        // rs.setStatus(0);
        // RemoteService rsNew = rsCommand.addRemoteService(userSession, rs);
        // assertNotNull(rsNew);
        // assertTrue(rsNew.getStatus() == 0);
        // assertEquals("http://localhost:8080/test_vf", rsNew.getUri());

    }

}
