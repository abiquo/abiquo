package com.abiquo.virtualfactory.machine.test;

import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.fail;

import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.abiquo.virtualfactory.exception.VirtualMachineException;
import com.abiquo.virtualfactory.machine.impl.vcenter.DistrubutedPortGroupActions;
import com.abiquo.virtualfactory.network.VirtualNIC;
import com.vmware.vim25.mo.DistributedVirtualPortgroup;
import com.vmware.vim25.mo.ServiceInstance;

/**
 * Class that test the {@link DistrubutedPortGroupActions} class.
 * In order to check this class you need.
 * 
 * 1. Have a vCenter remote, with correct license and enough permissions.
 * 2. Create a 'dvSwitch' there.
 * 3. Remove all the @Test(enabled=false) here.
 * 4. Put it again because otherwise they always be executed.
 * 
 * @author jdevesa@abiquo.com
 */
public class VCenterVDSCreationTest
{

    private final String vcIPAddress = "10.60.1.90";

    private final String user = "root";

    private final String password = "temporal";
    
    // Name of the already-created dvSwitch in the remote vCenter.
    private final String dvSwitchName = "dvSwitch";
    private final String wrongSwitchName = "veintemillonedename";

    private ServiceInstance serviceInstance;

    private DistrubutedPortGroupActions vcenterDVS;

    /**
     * Open the connection
     */
    @Before
    public void setUp() throws Exception
    {
        System.setProperty("org.apache.axis.components.net.SecureSocketFactory",
            "org.apache.axis.components.net.SunFakeTrustSocketFactory");

        serviceInstance =
            new ServiceInstance(new URL("https://" + vcIPAddress + "/sdk"), user, password, true);

        vcenterDVS = new DistrubutedPortGroupActions(serviceInstance);
    }

    /**
     * Clean the connection.
     */
    @After
    public void tearDown() throws Exception
    {
        if (serviceInstance != null && serviceInstance.getServerConnection() != null)
        {
            serviceInstance.getServerConnection().logout();
        }
    }

    /**
     * Just execute the methods with the correct flow of the creation, get, and deletion of DVS.
     */
    @Test
    public void createGetAndDeleteDVS() throws Exception
    {
        String portGroupName = vcenterDVS.createPortGroupInDVS(dvSwitchName, "dvs_network", 34);
        Thread.sleep(1000);
        DistributedVirtualPortgroup portGroup = vcenterDVS.getPortGroup(dvSwitchName, portGroupName);
        Thread.sleep(1000);
        vcenterDVS.deletePortGroup(portGroup);
    }

    @Test(expected = VirtualMachineException.class)
    public void createPortGroupFailsWhenDVSwitchDoesNotExist() throws VirtualMachineException
    {
        vcenterDVS.createPortGroupInDVS(wrongSwitchName, "dvs_network", 34);
    }

    /**
     * Check we can not create two port groups with the same name.
     */
    @Test
    public void createPortGroupFailsWhenPortGroupNameDuplicated() throws Exception
    {
        String portGroupName = "";
        try
        {
            portGroupName = vcenterDVS.createPortGroupInDVS(dvSwitchName, "dvs_network", 34);
            Thread.sleep(1000);
        }
        catch (VirtualMachineException e)
        {
            // The first one should be created!
            fail("Failed because the first one has not been created.");
        }

        try
        {
            vcenterDVS.createPortGroupInDVS(dvSwitchName, "dvs_network", 34);
            Thread.sleep(1000);
        }
        catch (VirtualMachineException e)
        {
            // This catch is expected because we can not create two dvSwitch with the same name.
            // Even in different dvSwitches. Clean the port_group....
            DistributedVirtualPortgroup portGroup =
                vcenterDVS.getPortGroup("dvSwitch", portGroupName);
            Thread.sleep(2000);
            vcenterDVS.deletePortGroup(portGroup);
            return;
        }

        // The second one should not fail
        fail("Failed because the second one has been created!");
    }
    
    /**
     * Ensure the exception is controlled even if we have an invalid session
     */
    @Test(expected = VirtualMachineException.class)
    public void createPortGroupFailsWhenSessionInvalid() throws VirtualMachineException
    {
        serviceInstance = null;
        vcenterDVS.createPortGroupInDVS(dvSwitchName, "dvs_network", 34);
    }
        
    /**
     * Assert the get returns null when the port group does not exist.
     */
    @Test
    public void getPortGroupReturnsNullWhenPortGroupDoesNotExist() throws VirtualMachineException
    {
        DistributedVirtualPortgroup portGroup = vcenterDVS.getPortGroup(dvSwitchName, "dvs_network_34");
        assertNull(portGroup);
    }
    
    /**
     * When you provide a good port group name (it exists) but the switch name does not exist, 
     * throws an exception.
     * 
     * 1. Create a port group.
     * 2. Get the port group but with the incorrect dvSwitch name.
     * 3. Be sure the 
     * @throws VirtualMachineException 
     */
    @Test
    public void getPortGroupRaisesWhenDVSwitchWrong() throws InterruptedException, VirtualMachineException
    {
        String portGroupName = null;
        try
        {
            portGroupName = vcenterDVS.createPortGroupInDVS(dvSwitchName, "dvs_network", 34);
        }
        catch(VirtualMachineException e)
        {
            fail("Creation should not fail!");
        }
        try
        {
            // This is what we are testing!
            vcenterDVS.getPortGroup(wrongSwitchName, portGroupName);
        }
        catch (VirtualMachineException e)
        {
            // IT SHOULD THROW THIS EXCEPTION. Clean the port group.
            DistributedVirtualPortgroup portGroup = vcenterDVS.getPortGroup(dvSwitchName, portGroupName);
            Thread.sleep(1000);
            vcenterDVS.deletePortGroup(portGroup);
            return;
            
        }
        fail("Failed because it should raise an exception!");
    }
    
    @Test
    public void testAttachNicToVM() throws VirtualMachineException
    {
        VirtualNIC niic = new VirtualNIC(dvSwitchName, "00:50:56:a4:00:03", 2, "default_network", 1);
        vcenterDVS.attachVirtualMachineToPortGroup("vm_name", niic);
    }

}
