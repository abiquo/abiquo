package com.abiquo.virtualfactory.machine.test;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.abiquo.virtualfactory.exception.VirtualMachineException;
import com.abiquo.virtualfactory.machine.impl.VCenterDVSCreation;
import com.vmware.vim25.mo.DistributedVirtualPortgroup;
import com.vmware.vim25.mo.ServiceInstance;

/**
 * @author jdevesa
 */
public class VCenterVDSCreationTest
{

    private String vcIPAddress = "10.60.21.204";

    private String user = "Administrator";

    private String password = "abiqu0!";

    private ServiceInstance serviceInstance;

    private VCenterDVSCreation vcenterDVS;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        System.setProperty("org.apache.axis.components.net.SecureSocketFactory",
            "org.apache.axis.components.net.SunFakeTrustSocketFactory");

        serviceInstance =
            new ServiceInstance(new URL("https://" + vcIPAddress + "/sdk"), user, password, true);

        vcenterDVS = new VCenterDVSCreation(serviceInstance);
    }

    /**
     * @throws java.lang.Exception
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
     * 
     * @throws Exception
     */
    @Test
    public void createGetAndDeleteDVS() throws Exception
    {
        String portGroupName = vcenterDVS.createPortGroupInDVS("dvSwitch", "dvs_network", 34);
        Thread.sleep(1000);
        DistributedVirtualPortgroup portGroup = vcenterDVS.getPortGroup("dvSwitch", portGroupName);
        Thread.sleep(1000);
        vcenterDVS.deletePortGroup(portGroup);
    }

    @Test(expected = VirtualMachineException.class)
    public void createPortGroupFailsWhenDVSwitchDoesNotExist() throws VirtualMachineException
    {
        vcenterDVS.createPortGroupInDVS("veintemillonedenave", "dvs_network", 34);
    }

    @Test
    public void createPortGroupFailsWhenPortGroupNameDuplicated() throws Exception
    {
        String portGroupName = "";
        try
        {
            portGroupName = vcenterDVS.createPortGroupInDVS("dvSwitch", "dvs_network", 34);
            Thread.sleep(1000);
        }
        catch (VirtualMachineException e)
        {
            // The first one should be created!
            TestCase.fail("Failed because the first one has not been created.");
        }

        try
        {
            vcenterDVS.createPortGroupInDVS("dvSwitch", "dvs_network", 34);
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
        }

        // The second one should not fail
        TestCase.fail("Failed because the second one has been created!");
    }

}
