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

/**
 * 
 */
package com.abiquo.api.services.cloud;

import static com.abiquo.testng.TestConfig.BASIC_UNIT_TESTS;
import static com.abiquo.testng.TestConfig.NETWORK_UNIT_TESTS;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.util.Random;

import javax.persistence.EntityManager;

import org.springframework.security.context.SecurityContextHolder;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.api.common.AbstractUnitTest;
import com.abiquo.api.common.BasicUserAuthentication;
import com.abiquo.api.exceptions.BadRequestException;
import com.abiquo.api.exceptions.ConflictException;
import com.abiquo.api.exceptions.NotFoundException;
import com.abiquo.api.services.NetworkService;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.enterprise.DatacenterLimits;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.Role;
import com.abiquo.server.core.enterprise.User;
import com.abiquo.server.core.infrastructure.RemoteService;
import com.abiquo.server.core.infrastructure.network.NetworkConfiguration;
import com.abiquo.server.core.infrastructure.network.VLANNetwork;
import com.abiquo.server.core.infrastructure.network.VLANNetworkGenerator;
import com.abiquo.server.core.util.network.IPNetworkRang;

/**
 * @author jdevesa
 */
@Test(groups = {NETWORK_UNIT_TESTS})
public class PrivateNetworkServiceTest extends AbstractUnitTest
{
    VirtualDatacenter vdc;

    VLANNetwork vlan;

    RemoteService rs;

    @BeforeMethod(groups = {BASIC_UNIT_TESTS, NETWORK_UNIT_TESTS})
    public void setupBasicUser()
    {
        Enterprise e = enterpriseGenerator.createUniqueInstance();
        Role r = roleGenerator.createInstance();
        User u = userGenerator.createInstance(e, r, "basicUser", "basicUser");
        setup(e, r, u);

        rs = remoteServiceGenerator.createInstance(RemoteServiceType.DHCP_SERVICE);
        vdc = vdcGenerator.createInstance(rs.getDatacenter(), e);

        DatacenterLimits dclimit = new DatacenterLimits(vdc.getEnterprise(), vdc.getDatacenter());
        vlan = vlanGenerator.createInstance(vdc.getNetwork(), rs, "255.255.255.0");
        vlan.setEnterprise(vdc.getEnterprise());
        vdc.setDefaultVlan(vlan);
        setup(vdc.getDatacenter(), rs, vdc.getNetwork(), vlan.getConfiguration(), vlan, vdc,
            dclimit);

        SecurityContextHolder.getContext().setAuthentication(new BasicUserAuthentication());
    }

    @Override
    @AfterMethod(groups = {BASIC_UNIT_TESTS, NETWORK_UNIT_TESTS})
    public void tearDown()
    {
        super.tearDown();
    }

    /**
     * Throws a not found exception when it does not found the virtual datacenter
     */
    @Test(expectedExceptions = {NotFoundException.class})
    public void updateNetworkRandomVDCTest()
    {
        EntityManager em = getEntityManagerWithAnActiveTransaction();
        NetworkService service = new NetworkService(em);
        service.updatePrivateNetwork(new Random().nextInt(1000), vlan.getId(), vlan);
    }

    /**
     * Throws a not found exception when it does not found the vLAN
     */
    @Test(expectedExceptions = {NotFoundException.class})
    public void updateNetworkRandomVlanTest()
    {
        EntityManager em = getEntityManagerWithAnActiveTransaction();
        NetworkService service = new NetworkService(em);
        service.updatePrivateNetwork(vdc.getId(), new Random().nextInt(1000), vlan);
    }

    /**
     * Throws a not found exception when it does not found the vLAN with a known vdc but that does
     * not belong to it
     */
    @Test(expectedExceptions = {NotFoundException.class})
    public void updateNetworkInvalidTupleVDCVlanTest()
    {
        VirtualDatacenter vdc2 = vdcGenerator.createInstance(rs.getDatacenter());
        setup(vdc2.getEnterprise(), vdc2.getNetwork(), vdc2);

        EntityManager em = getEntityManagerWithAnActiveTransaction();
        NetworkService service = new NetworkService(em);
        service.updatePrivateNetwork(vdc2.getId(), vlan.getId(), vlan);
    }

    /**
     * Throws a {@link BadRequestException} when the ip of the path is different from the IP of the
     * VLAN.
     */
    @Test(expectedExceptions = {BadRequestException.class})
    public void updateNetworkIncoherentIDs()
    {
        EntityManager em = getEntityManagerWithAnActiveTransaction();
        VLANNetwork copy = performCopy(vlan);
        copy.setId(new Random().nextInt(1000));
        NetworkService service = new NetworkService(em);
        service.updatePrivateNetwork(vdc.getId(), vlan.getId(), copy);

    }

    /**
     * Throws a ConflictException when we try to change the address of the VLAN, it should raise a
     * {@link ConflictException}
     */
    @Test(expectedExceptions = {ConflictException.class})
    public void updateNetworkAddressChanged()
    {
        EntityManager em = getEntityManagerWithAnActiveTransaction();
        VLANNetwork copy = performCopy(vlan);
        copy.getConfiguration().setAddress("12.12.12.12");
        NetworkService service = new NetworkService(em);
        service.updatePrivateNetwork(vdc.getId(), copy.getId(), copy);
    }

    /**
     * Throws a {@link ConflictException} when we try to change the mask of the VLAN, it should
     * raise a {@link ConflictException}
     */
    @Test(expectedExceptions = {ConflictException.class})
    public void updateNetworkMaskChanged()
    {
        EntityManager em = getEntityManagerWithAnActiveTransaction();
        VLANNetwork copy = performCopy(vlan);
        copy.getConfiguration().setMask(20);
        NetworkService service = new NetworkService(em);
        service.updatePrivateNetwork(vdc.getId(), copy.getId(), copy);
    }

    /**
     * Throws a {@link ConflictException} when we try to change the tag of the VLAN, it should raise
     * a {@link ConflictException}
     */
    @Test(expectedExceptions = {ConflictException.class})
    public void updateNetworkTagsChanged()
    {
        EntityManager em = getEntityManagerWithAnActiveTransaction();
        VLANNetwork copy = performCopy(vlan);
        copy.setTag(45);
        NetworkService service = new NetworkService(em);
        service.updatePrivateNetwork(vdc.getId(), copy.getId(), copy);
    }

    /**
     * The {@link VLANNetworkGenerator} creates a network inside the range 192.168.1.0 and
     * 192.168.1.255. And the default gateway is '192.168.1.1'. The 'gateway' field is an IP address
     * that must be inside this range. This test checks that this process works ok.
     */
    @Test
    public void updateNetworkGatewayOutsideTheRange()
    {
        EntityManager em = getEntityManagerWithAnActiveTransaction();
        NetworkService service = new NetworkService(em);

        // Put an invalid gateway (out of range) and check a ConflictException is raised.
        VLANNetwork copy = performCopy(vlan);
        copy.getConfiguration().setGateway("24.24.24.0");
        try
        {
            service.updatePrivateNetwork(vdc.getId(), copy.getId(), copy);
            fail();
        }
        catch (ConflictException ce)
        {
            // do nothing. It is the desired behaviour
        }

        // Put a valid gateway and check the services updates it ok.
        copy.getConfiguration().setGateway("192.168.1.45");
        vlan = service.updatePrivateNetwork(vdc.getId(), copy.getId(), copy);
        assertEquals(vlan.getConfiguration().getGateway(), copy.getConfiguration().getGateway());

        commitActiveTransaction(em);
    }

    /**
     * Two VLANs can not be created with the same name inside the same VDC. But two VLANs with the
     * same name can be created in VDC differents. This test checks this behaviour.
     */
    @Test
    public void updateNetworkDuplicatedName()
    {

        // Create the second VLANNetwork 'vlan2' in the same VDC than 'vlan' and check
        // the process doesn't allow us to update it.
        VLANNetwork vlan2 = vlanGenerator.createInstance(vdc.getNetwork(), rs, "255.255.255.0");
        vlan2.setEnterprise(vdc.getEnterprise());
        setup(vlan2.getConfiguration(), vlan2);

        // Create the third VLANNetwork 'vlan3' in other VDC than 'vlan'. The VLAN with the same
        // name
        // should be allowed now.
        VirtualDatacenter vdc2 =
            vdcGenerator.createInstance(rs.getDatacenter(), vdc.getEnterprise());
        setup(vdc2.getNetwork(), vdc2);
        VLANNetwork vlan3 = vlanGenerator.createInstance(vdc2.getNetwork(), rs, "255.255.255.0");
        setup(vlan3.getConfiguration(), vlan3);

        // STEP 1
        EntityManager em = getEntityManagerWithAnActiveTransaction();
        NetworkService service = new NetworkService(em);
        try
        {
            // Set the same name.
            vlan2.setName(vlan.getName());
            service.updatePrivateNetwork(vdc.getId(), vlan2.getId(), vlan2);
            fail();
        }
        catch (ConflictException ce)
        {
            // The conflict exception is the desired behavior, go on
        }

        // STEP 2
        service = new NetworkService(em);

        // Set the same name and update vlan3
        VLANNetwork copy = performCopy(vlan3);
        copy.setName(vlan.getName());
        vlan3 = service.updatePrivateNetwork(vdc2.getId(), copy.getId(), copy);
        commitActiveTransaction(em);

        // assert the values
        assertEquals(vlan3.getName(), vlan.getName());
    }

    /**
     * Not all the fields of the VLAN can be changed. That was tested in previous test. This method
     * test all the fields in the VLANNetwork can be changed are actually changed.
     */
    @Test(groups = {BASIC_UNIT_TESTS})
    public void updateNetworkUpdatesAllFields()
    {
        VLANNetwork copy = performCopy(vlan);
        copy.getConfiguration().setGateway("192.168.1.44");
        copy.getConfiguration().setPrimaryDNS("8.4.4.8");
        copy.getConfiguration().setSecondaryDNS("56.56.56.56");
        copy.getConfiguration().setSufixDNS("bcn.test.test.com");
        copy.setName("newname");

        EntityManager em = getEntityManagerWithAnActiveTransaction();
        NetworkService service = new NetworkService(em);
        vlan = service.updatePrivateNetwork(vdc.getId(), copy.getId(), copy);
        commitActiveTransaction(em);

        assertEquals(vlan.getConfiguration().getGateway(), "192.168.1.44");
        assertEquals(vlan.getConfiguration().getPrimaryDNS(), "8.4.4.8");
        assertEquals(vlan.getConfiguration().getSecondaryDNS(), "56.56.56.56");
        assertEquals(vlan.getConfiguration().getSufixDNS(), "bcn.test.test.com");
        assertEquals(vlan.getName(), "newname");
    }

    // DELETE-related methods.
    /**
     * Every virtual datacenter should have at least a VLAN defined there. So this test will raise a
     * ConflictException because it don't let to delete it
     */
    @Test(expectedExceptions = {ConflictException.class})
    public void deleteNetworkUniqueRaisesExceptionTest()
    {
        EntityManager em = getEntityManagerWithAnActiveTransaction();
        NetworkService service = new NetworkService(em);
        service.deletePrivateNetwork(vdc.getId(), vlan.getId());
    }

    /**
     * Every virtual datacenter should have at least a default VLAN defined there. So this test will
     * raise a ConflictException because it won't let delete the default network.
     */
    @Test(expectedExceptions = {ConflictException.class}, enabled = false)
    public void deleteNetworkDefaultRaisesExceptionTest()
    {
        EntityManager em = getEntityManagerWithAnActiveTransaction();
        NetworkService service = new NetworkService(em);

        // Create the second one
        VLANNetwork vlan2 = vlanGenerator.createInstance(vdc.getNetwork(), rs, "255.255.255.0");
        setup(vlan2.getConfiguration(), vlan2);

        // Try to delete the first one.
        service.deletePrivateNetwork(vdc.getId(), vlan.getId());
    }

    /**
     * Test when we try to delete a VLAN it works.
     */
    @Test(groups = {BASIC_UNIT_TESTS})
    public void deleteNetworkTest()
    {
        // Create the second one
        VLANNetwork vlan2 = vlanGenerator.createInstance(vdc.getNetwork(), rs, "255.255.255.0");
        vlan2.setEnterprise(vdc.getEnterprise());
        setup(vlan2.getConfiguration(), vlan2);

        EntityManager em = getEntityManagerWithAnActiveTransaction();
        NetworkService service = new NetworkService(em);

        // Assert here we have two vlans.
        assertEquals(service.getPrivateNetworks(vdc.getId()).size(), 2);

        // Try to delete the second one.
        service.deletePrivateNetwork(vdc.getId(), vlan2.getId());
        // Assert here we have delete one of them.
        assertEquals(service.getPrivateNetworks(vdc.getId()).size(), 1);

        commitActiveTransaction(em);
    }

    /**
     * Performs a copy of a VLANNetwork object.
     * 
     * @param original original object to be copied.
     * @return a new instance of {@link VLANNetwork} object but with the same values
     */
    private VLANNetwork performCopy(final VLANNetwork original)
    {
        VLANNetwork copy = new VLANNetwork();
        copy.setId(original.getId());
        copy.setName(original.getName());
        copy.setTag(original.getTag());
        copy.setNetwork(original.getNetwork());
        copy.setConfiguration(new NetworkConfiguration(original.getConfiguration().getAddress(),
            original.getConfiguration().getMask(),
            IPNetworkRang.transformIntegerMaskToIPMask(original.getConfiguration().getMask())
                .toString(), original.getConfiguration().getGateway(), "bridge"));
        copy.getConfiguration().setPrimaryDNS(original.getConfiguration().getPrimaryDNS());
        copy.getConfiguration().setSecondaryDNS(original.getConfiguration().getSecondaryDNS());
        copy.getConfiguration().setSufixDNS(original.getConfiguration().getSufixDNS());

        return copy;
    }
}
