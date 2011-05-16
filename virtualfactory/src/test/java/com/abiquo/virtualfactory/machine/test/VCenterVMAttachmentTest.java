package com.abiquo.virtualfactory.machine.test;

import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.abiquo.virtualfactory.exception.VirtualMachineException;
import com.abiquo.virtualfactory.machine.impl.vcenter.VCenterDVSCreation;
import com.abiquo.virtualfactory.machine.impl.vcenter.VCenterVMAttachment;
import com.abiquo.virtualfactory.network.VirtualNIC;
import com.vmware.vim25.mo.ServiceInstance;


public class VCenterVMAttachmentTest
{
    private final String vcIPAddress = "10.60.21.204";

    private final String user = "Administrator";

    private final String password = "abiqu0!";
    
    // Name of the already-created dvSwitch in the remote vCenter.
    private final String vmName = "vm_devesa_1";
    private final String dvSwitch = "dvSwitch";
    private final String wrongVmName = "veintemillonedename";
    private final String network_name = "default_network_2";
    
    private VCenterVMAttachment vcenterAtt;
    
    private ServiceInstance serviceInstance;
    
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

        vcenterAtt = new VCenterVMAttachment(serviceInstance);
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
    
    @Test
    public void testAttachNicToVM() throws VirtualMachineException
    {
        VirtualNIC niic = new VirtualNIC(dvSwitch, "00:50:56:a4:00:00", 2, "default_network", 1);
        vcenterAtt.attachVirtualMachine(vmName, niic);
    }
}
