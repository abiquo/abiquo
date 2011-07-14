/**
 * 
 */
package com.abiquo.api.services.cloud;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.util.ArrayList;
import java.util.Random;

import javax.persistence.EntityManager;

import org.springframework.security.context.SecurityContextHolder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.api.common.AbstractGeneratorTest;
import com.abiquo.api.common.BasicUserAuthentication;
import com.abiquo.api.exceptions.BadRequestException;
import com.abiquo.api.exceptions.ConflictException;
import com.abiquo.api.exceptions.NotFoundException;
import com.abiquo.api.services.PrivateNetworkService;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.enterprise.DatacenterLimits;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.Role;
import com.abiquo.server.core.enterprise.User;
import com.abiquo.server.core.infrastructure.RemoteService;
import com.abiquo.server.core.infrastructure.network.IpPoolManagement;
import com.abiquo.server.core.infrastructure.network.NetworkConfiguration;
import com.abiquo.server.core.infrastructure.network.VLANNetwork;
import com.abiquo.server.core.util.network.IPAddress;
import com.abiquo.server.core.util.network.IPNetworkRang;

/**
 * @author jdevesa
 */
public class PrivateNetworkServiceTest extends AbstractGeneratorTest
{
    VirtualDatacenter vdc;

    VLANNetwork vlan;

    RemoteService rs;

    @BeforeMethod
    public void setupBasicUser()
    {
        Enterprise e = enterpriseGenerator.createUniqueInstance();
        Role r = roleGenerator.createInstance();
        User u = userGenerator.createInstance(e, r, "basicUser", "basicUser");
        setup(e, r, u);

        rs = remoteServiceGenerator.createInstance(RemoteServiceType.DHCP_SERVICE);
        vdc = vdcGenerator.createInstance(rs.getDatacenter());

        DatacenterLimits dclimit = new DatacenterLimits(vdc.getEnterprise(), vdc.getDatacenter());
        setup(vdc.getDatacenter(), rs, vdc.getEnterprise(), vdc.getNetwork(), vdc);
        vlan = vlanGenerator.createInstance(vdc.getNetwork(), rs, "255.255.255.0");
        vlan.setDefaultNetwork(Boolean.TRUE);
        vlan.setEnterprise(vdc.getEnterprise());
        setup(vlan.getConfiguration().getDhcp(), vlan.getConfiguration(), vlan, dclimit);

        SecurityContextHolder.getContext().setAuthentication(new BasicUserAuthentication());
    }

    /**
     * Perform an update test from the service.
     */
    @Test
    public void updateNetworkOKTest()
    {
        EntityManager em = getEntityManagerWithAnActiveTransaction();

        PrivateNetworkService service = new PrivateNetworkService(em);
        vlan.setName("newname");
        vlan.getConfiguration().setPrimaryDNS("45.45.45.0");
        vlan = service.updatePrivateNetwork(vdc.getId(), vlan.getId(), vlan);

        commitActiveTransaction(em);

        // Assert the values has changed.
        assertEquals(vlan.getName(), "newname");
        assertEquals(vlan.getConfiguration().getPrimaryDNS(), "45.45.45.0");
    }

    /**
     * Throws a not found exception when it does not found the virtual datacenter
     */
    @Test(expectedExceptions = {NotFoundException.class})
    public void updateNetworkRandomVDCTest()
    {
        EntityManager em = getEntityManagerWithAnActiveTransaction();
        PrivateNetworkService service = new PrivateNetworkService(em);
        service.updatePrivateNetwork(new Random().nextInt(1000), vlan.getId(), vlan);
    }

    /**
     * Throws a not found exception when it does not found the vLAN
     */
    @Test(expectedExceptions = {NotFoundException.class})
    public void updateNetworkRandomVlanTest()
    {
        EntityManager em = getEntityManagerWithAnActiveTransaction();
        PrivateNetworkService service = new PrivateNetworkService(em);
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
        PrivateNetworkService service = new PrivateNetworkService(em);
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
        PrivateNetworkService service = new PrivateNetworkService(em);
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
        PrivateNetworkService service = new PrivateNetworkService(em);
        service.updatePrivateNetwork(vdc.getId(), copy.getId(), copy);
    }
    
    /**
     * Throws a {@link ConflictException} when we try to change the mask of the VLAN, it should raise a
     * {@link ConflictException}
     */
    @Test(expectedExceptions = {ConflictException.class})
    public void updateNetworkMaskChanged()
    {
        EntityManager em = getEntityManagerWithAnActiveTransaction();
        VLANNetwork copy = performCopy(vlan);
        copy.getConfiguration().setMask(20);
        PrivateNetworkService service = new PrivateNetworkService(em);
        service.updatePrivateNetwork(vdc.getId(), copy.getId(), copy);
    }
    
    /**
     * Throws a {@link ConflictException} when we try to change the tag of the VLAN, it should raise a
     * {@link ConflictException}
     */
    @Test(expectedExceptions = {ConflictException.class})
    public void updateNetworkTagsChanged()
    {
        EntityManager em = getEntityManagerWithAnActiveTransaction();
        VLANNetwork copy = performCopy(vlan);
        copy.setTag(45);
        PrivateNetworkService service = new PrivateNetworkService(em);
        service.updatePrivateNetwork(vdc.getId(), copy.getId(), copy);
    }
    
    /**
     * Throws a {@link ConflictException} when we try to put the default vlan as false
     * {@link ConflictException}
     */
    @Test(expectedExceptions = {ConflictException.class})
    public void updateNetworkDefaultUnset()
    {
        EntityManager em = getEntityManagerWithAnActiveTransaction();
        VLANNetwork copy = performCopy(vlan);
        copy.setDefaultNetwork(Boolean.FALSE);
        PrivateNetworkService service = new PrivateNetworkService(em);
        service.updatePrivateNetwork(vdc.getId(), copy.getId(), copy);
    }
    
    /**
     * Only one VLAN can be set as DEFAULT in the same VDC. When one VLAN is modified as
     * the default one, the rest should be updated as non-default. In this test we check
     * this behavior is controlled in the updatePrivateNetwork process.
     * 
     * 1) The object 'vlan' is the default network in the VDC.
     * 2) We create a new {@link VLANNetwork} 'vlan2' inside the VDC which is set as non-default (by the generator).
     * 3) We update the 'vlan2' and put it as the default one.
     * 4) Retrive the 'vlan' object and check is not the default one anymore.
     */
    @Test
    public void updateNetworkSwitchVLANDefaultNetwork()
    {
        // Create the second one and assert is not default
        VLANNetwork vlan2 = vlanGenerator.createInstance(vdc.getNetwork(), rs, "255.255.255.0");
        vlan2.setEnterprise(vdc.getEnterprise());
        setup(vlan2.getConfiguration().getDhcp(), vlan2.getConfiguration(), vlan2);
        
        // Previous assertions
        assertFalse(vlan2.getDefaultNetwork());
        assertTrue(vlan.getDefaultNetwork());
        
        // Update the VLAN2 and put it as default.
        EntityManager em = getEntityManagerWithAnActiveTransaction();
        PrivateNetworkService service = new PrivateNetworkService(em);
        VLANNetwork copy = performCopy(vlan2);
        copy.setDefaultNetwork(Boolean.TRUE);
        service.updatePrivateNetwork(vdc.getId(), vlan2.getId(), copy);
        commitActiveTransaction(em);
        
        em = getEntityManagerWithAnActiveTransaction();
        service = new PrivateNetworkService(em);
        
        // Update the vlan object.
        vlan = service.getPrivateNetwork(vdc.getId(), vlan.getId());
        
        // Post assertions
        assertTrue(vlan2.getDefaultNetwork());
        assertFalse(vlan.getDefaultNetwork());
        
        commitActiveTransaction(em);
        
    }
    
    /**
     *  The {@link VLANNetworkGenerator} creates a network inside the range 192.168.1.0 
     *  and 192.168.1.255. And the default gateway is '192.168.1.1'.
     *  
     *  The 'gateway' field is an IP address that must be inside this range. This test checks that
     *  this process works ok.
     *
     */
    @Test
    public void updateNetworkGatewayOutsideTheRange()
    {
        EntityManager em = getEntityManagerWithAnActiveTransaction();
        PrivateNetworkService service = new PrivateNetworkService(em);
        
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
     *  Two VLANs can not be created with the same name inside the same VDC.
     *  But two VLANs with the same name can be created in VDC differents.
     *  
     *  This test checks this behaviour.
     */
    @Test
    public void updateNetworkDuplicatedName()
    {
        
        // Create the second VLANNetwork 'vlan2' in the same VDC than 'vlan' and check 
        // the process doesn't allow us to update it.
        VLANNetwork vlan2 = vlanGenerator.createInstance(vdc.getNetwork(), rs, "255.255.255.0");
        vlan2.setEnterprise(vdc.getEnterprise());
        setup(vlan2.getConfiguration().getDhcp(), vlan2.getConfiguration(), vlan2);
        
        // Create the third VLANNetwork 'vlan3' in other VDC than 'vlan'. The VLAN with the same name
        // should be allowed now.
        VirtualDatacenter vdc2 = vdcGenerator.createInstance(rs.getDatacenter(), vdc.getEnterprise());
        setup(vdc2.getNetwork(), vdc2);
        VLANNetwork vlan3 = vlanGenerator.createInstance(vdc2.getNetwork(), rs, "255.255.255.0");
        setup(vlan3.getConfiguration().getDhcp(), vlan3.getConfiguration(), vlan3);
        
        // STEP 1
        EntityManager em = getEntityManagerWithAnActiveTransaction();
        PrivateNetworkService service = new PrivateNetworkService(em);
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
        service = new PrivateNetworkService(em);

        // Set the same name and update vlan3
        VLANNetwork copy = performCopy(vlan3);
        copy.setName(vlan.getName());
        vlan3 = service.updatePrivateNetwork(vdc2.getId(), copy.getId(), copy);
        commitActiveTransaction(em);
        
        // assert the values
        assertEquals(vlan3.getName(), vlan.getName());
    }
    
    /**
     * Not all the fields of the VLAN can be changed. That was tested in previous test.
     * 
     * This method test all the fields in the VLANNetwork can be changed are actually changed.
     */
    @Test
    public void updateNetworkUdatesAllFields() 
    {
        VLANNetwork copy = performCopy(vlan);
        copy.getConfiguration().setGateway("192.168.1.44");
        copy.getConfiguration().setPrimaryDNS("8.4.4.8");
        copy.getConfiguration().setSecondaryDNS("56.56.56.56");
        copy.getConfiguration().setSufixDNS("bcn.test.test.com");
        copy.setName("newname");
        
        EntityManager em = getEntityManagerWithAnActiveTransaction();
        PrivateNetworkService service = new PrivateNetworkService(em);
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
     * Every virtual datacenter should have at least a VLAN defined there.
     * So this test will raise a ConflictException because it don't let to
     * delete it 
     */
    @Test(expectedExceptions = {ConflictException.class})
    public void deleteNetworkUniqueRaisesExceptionTest()
    {
        EntityManager em = getEntityManagerWithAnActiveTransaction();
        PrivateNetworkService service = new PrivateNetworkService(em);
        service.deletePrivateNetwork(vdc.getId(), vlan.getId());
    }
    
    /**
     * Every virtual datacenter should have at least a default VLAN defined there.
     * So this test will raise a ConflictException because it won't let
     * delete the default network.
     */
    @Test(expectedExceptions = {ConflictException.class})
    public void deleteNetworkDefaultRaisesExceptionTest()
    {
        EntityManager em = getEntityManagerWithAnActiveTransaction();
        PrivateNetworkService service = new PrivateNetworkService(em);
        
        // Create the second one
        VLANNetwork vlan2 = vlanGenerator.createInstance(vdc.getNetwork(), rs, "255.255.255.0");
        setup(vlan2.getConfiguration().getDhcp(), vlan2.getConfiguration(), vlan2);
        
        // Try to delete the first one.
        service.deletePrivateNetwork(vdc.getId(), vlan.getId());
    }
    
    
    /**
     * Performs a copy of a VLANNetwork object.
     * @param original original object to be copied.
     * @return a new instance of {@link VLANNetwork} object but with the same values
     */
    private VLANNetwork performCopy(VLANNetwork original)
    {
        VLANNetwork copy = new VLANNetwork();
        copy.setId(original.getId());
        copy.setName(original.getName());
        copy.setTag(original.getTag());
        copy.setDefaultNetwork(original.getDefaultNetwork());
        copy.setNetwork(original.getNetwork());
        copy.setConfiguration(new NetworkConfiguration(original.getConfiguration().getAddress(),
            original.getConfiguration().getMask(),
            IPNetworkRang.transformIntegerMaskToIPMask(original.getConfiguration().getMask()).toString(),
            original.getConfiguration().getGateway(),
            "bridge"));
        copy.getConfiguration().setPrimaryDNS(original.getConfiguration().getPrimaryDNS());
        copy.getConfiguration().setSecondaryDNS(original.getConfiguration().getSecondaryDNS());
        copy.getConfiguration().setSufixDNS(original.getConfiguration().getSufixDNS());

        return copy;
    }
}
