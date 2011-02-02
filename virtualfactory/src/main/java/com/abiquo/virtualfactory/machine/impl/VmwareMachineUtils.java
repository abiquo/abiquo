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

package com.abiquo.virtualfactory.machine.impl;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.util.ExtendedAppUtil;
import com.abiquo.util.ServiceUtil;
import com.abiquo.virtualfactory.exception.HypervisorException;
import com.abiquo.virtualfactory.exception.VirtualMachineException;
import com.abiquo.virtualfactory.hypervisor.impl.VmwareHypervisor;
import com.vmware.vim25.ConfigTarget;
import com.vmware.vim25.DynamicProperty;
import com.vmware.vim25.InvalidProperty;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.NetworkSummary;
import com.vmware.vim25.ObjectContent;
import com.vmware.vim25.ObjectSpec;
import com.vmware.vim25.PropertyFilterSpec;
import com.vmware.vim25.PropertySpec;
import com.vmware.vim25.ResourceAllocationInfo;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.ServiceContent;
import com.vmware.vim25.SharesInfo;
import com.vmware.vim25.SharesLevel;
import com.vmware.vim25.TaskInfo;
import com.vmware.vim25.VimPortType;
import com.vmware.vim25.VirtualMachineNetworkInfo;
import com.vmware.vim25.VirtualMachinePowerState;
import com.vmware.vim25.VirtualMachineRuntimeInfo;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.VirtualMachine;

/**
 * Wrap the main interface of the VMWare SDK (ExtendedAppUtil) to provide additional utilities. TODO
 * use the same method to retrieve properties.
 */
public class VmwareMachineUtils
{
    /** The logger object. */
    private final static Logger logger = LoggerFactory.getLogger(VmwareMachineUtils.class);

    /** Main interface from the VMWare SDK. */
    private ExtendedAppUtil apputil;

    /** VMWare hypervisor instance. */
    private VmwareHypervisor vmwareHyper;

    public ServiceUtil getServiceUtils()
    {
        return apputil.getServiceUtil();
    }

    /**
     * Default constructor. Gets the main SDK interface (apputil).
     * 
     * @param vmwareHyper, the VMWare hypervisor instance.
     */
    public VmwareMachineUtils(VmwareHypervisor vmwareHyper)
    {
        this.vmwareHyper = vmwareHyper;
        apputil = vmwareHyper.getAppUtil();

    }

    /**
     * Default constructor. Gets the main SDK interface (apputil).
     * 
     * @param vmwareHyper, the VMWare hypervisor instance.
     */
    public VmwareMachineUtils(ExtendedAppUtil apputil)
    {
        this.apputil = apputil;

    }

    /**
     * Gets the main VMWare SDK interface.
     */
    public ExtendedAppUtil getAppUtil()
    {
        return apputil;
    }

    /**
     * Gets the VMWare service content handle the managed object references (MOR).
     */
    public ServiceContent getServiceContent()
    {
        return apputil.getServiceInstance().getServiceContent();
    }

    /**
     * Gets the VMWare service utils to navigate the managed object references (MOR).
     */
    public ServiceUtil getServiceUtil()
    {
        ServiceUtil su = ServiceUtil.CreateServiceUtil();
        su.init(apputil);
        return su;
    }

    /**
     * Gets the VMWare service content require actions to the managed object references (MOR).
     */
    public VimPortType getService()
    {
        return apputil.getServiceInstance().getServerConnection().getVimService();
    }

    public HostSystem getHostSystem() throws InvalidProperty, RuntimeFault, RemoteException
    {
        ManagedEntity[] mes =
            new InventoryNavigator(apputil.getServiceInstance().getRootFolder())
                .searchManagedEntities("HostSystem");

        HostSystem host = (HostSystem) mes[0];

        return host;
    }

    /**
     * Gets the option from the VMWare session.
     * 
     * @param key, the name of the option to get.
     * @return the option value, null if not provided.
     */
    public String getOption(String key)
    {
        return apputil.get_option(key);
    }

    /**
     * Gets the host system on the current datacenter.
     */
    public ManagedObjectReference getHostSystemMOR() throws VirtualMachineException
    {
        ManagedObjectReference dcmor;
        ManagedObjectReference hfmor;

        String dcName = apputil.get_option("datacentername");

        try
        {
            dcmor = getServiceUtil().getDecendentMoRef(null, "Datacenter", dcName);

            if (dcmor == null)
            {
                throw new Exception();
            }
        }
        catch (Exception e)
        {
            String msg = "Datacenter [" + dcName + "] not found.";
            logger.error(msg);
            throw new VirtualMachineException(msg);
        }

        try
        {
            hfmor = getServiceUtil().getMoRefProp(dcmor, "hostFolder");
        }
        catch (Exception e)
        {
            String msg = "Datacenter " + dcName + " not found.";
            logger.error(msg);
            throw new VirtualMachineException(msg);
        }

        // crmors = getServiceUtil().getDecendentMoRefs(hfmor, "ComputeResource");

        return getHostSystemMor(dcmor, hfmor);
    }

    /**
     * Gets the host system from a given datacenter and host folder. If not specified the
     * ''hostname'' option use the first decendent on the datacenter.
     */
    public ManagedObjectReference getHostSystemMor(ManagedObjectReference dcmor,
        ManagedObjectReference hfmor) throws VirtualMachineException
    {
        ManagedObjectReference hostmor;
        String hostName = apputil.get_option("hostname");

        if (hostName != null)
        {
            try
            {
                hostmor = getServiceUtil().getDecendentMoRef(hfmor, "HostSystem", hostName);

                if (hostmor == null)
                {
                    throw new Exception();
                }
            }
            catch (Exception e)
            {
                String message = "Host " + hostName + " not found";
                logger.error(message);
                throw new VirtualMachineException(message);
            }
        }
        else
        {
            try
            {
                hostmor = getServiceUtil().getFirstDecendentMoRef(dcmor, "HostSystem");
            }
            catch (Exception e)
            {
                String message = "Host " + hostName + " not found using ''FirstDecendent''";
                logger.error(message);
                throw new VirtualMachineException(message);
            }
        }

        return hostmor;
    }

    /**
     * Gets the reference to the network with provided name on the data center.
     * 
     * @param networkName, the name of the desired network.
     * @return the reference to the network named networkName, if exists.
     * @throws VirtualMachineException if some error or the given name is not any network on the
     *             datacenter.
     */
    public ManagedObjectReference getNetwork(String networkName) throws VirtualMachineException
    {

        String dcName; // datacenter name
        ManagedObjectReference dcmor; // datacenter
        ManagedObjectReference hfmor; // host folder
        ManagedObjectReference hostmor;// host
        ArrayList<ManagedObjectReference> crmors;// all computer resources on host folder
        ManagedObjectReference crmor; // computer resource
        ManagedObjectReference netMor = null;

        try
        {

            dcName = getOption("datacentername");
            dcmor = getServiceUtil().getDecendentMoRef(null, "Datacenter", dcName);

            if (dcmor == null)
            {
                String message = "Datacenter " + dcName + " not found.";
                logger.error(message);
                throw new VirtualMachineException(message);
            }

            hfmor = getServiceUtil().getMoRefProp(dcmor, "hostFolder");

            hostmor = getHostSystemMor(dcmor, hfmor);

            crmors = getServiceUtil().getDecendentMoRefs(hfmor, "ComputeResource");

            crmor = getComputerResourceFromHost(crmors, hostmor);

            VMUtils vmutils = new VMUtils(apputil);
            ConfigTarget configTarget = vmutils.getConfigTargetForHost(crmor, hostmor);

            boolean flag = false;
            for (int i = 0; i < configTarget.getNetwork().length; i++)
            {
                VirtualMachineNetworkInfo networkInfo = configTarget.getNetwork()[i];
                NetworkSummary networkSummary = networkInfo.getNetwork();

                if (networkSummary.getName().equals(networkName))
                {
                    flag = true;
                    if (networkSummary.isAccessible())
                    {
                        networkName = networkSummary.getName();
                        netMor = networkSummary.getNetwork();
                    }
                    else
                    {
                        throw new Exception("Specified Network is not accessible");
                    }
                    break;
                }
            }

            if (!flag || netMor == null)
            {
                return null;
            }

        }
        catch (Exception e)
        {
            throw new VirtualMachineException("Can not get the Network ", e);
        }

        return netMor;
    }

    /**
     * Gets the the Virtual machines list using the network (port group) passed as parameter
     * 
     * @param networkName the network name
     * @return the virtual machine list using this network
     * @throws VirtualMachineException
     */
    public ManagedObjectReference[] getVmsFromNetworkName(String networkName)
        throws VirtualMachineException
    {

        try
        {
            ManagedObjectReference network = getNetwork(networkName);
            ManagedObjectReference[] vms =
                (ManagedObjectReference[]) getServiceUtil().getDynamicProperty(network, "vm");
            if (vms == null)
            {
                vms = new ManagedObjectReference[0];
            }
            return vms;
        }
        catch (Exception e)
        {
            throw new VirtualMachineException("An error was found when getting the virtual machine used by this network: "
                + networkName,
                e);
        }
    }

    /**
     * @deprecated should return only one instance. Private helper to choose the virtual machine
     *             managed object reference
     * @param vmname, the VM name.
     * @return a list of virtual machine managed object references retalted to the
     * @throws Exception
     */
    public ArrayList<ManagedObjectReference> getVms(String vmname) throws VirtualMachineException
    {
        // String vmname = null;
        // String operation = null;
        String host = null;
        String folder = null;
        String datacenter = null;
        String pool = null;
        String guestid = null;
        String ipaddress = null;
        String[][] filter = null;

        // ExtendedAppUtil cb = vmwareHyper.getAppUtil();
        ArrayList<ManagedObjectReference> vmList = new ArrayList<ManagedObjectReference>();

        if (apputil.option_is_set("host"))
        {
            host = apputil.get_option("host");
        }
        if (apputil.option_is_set("folder"))
        {
            folder = apputil.get_option("folder");
        }
        if (apputil.option_is_set("datacenter"))
        {
            datacenter = apputil.get_option("datacenter");
        }
        if (apputil.option_is_set("vmname"))
        {
            vmname = apputil.get_option("vmname"); // XXX
        }
        if (apputil.option_is_set("pool"))
        {
            pool = apputil.get_option("pool");
        }
        if (apputil.option_is_set("ipaddress"))
        {
            ipaddress = apputil.get_option("ipaddress");
        }
        if (apputil.option_is_set("guestid"))
        {
            guestid = apputil.get_option("guestid");
        }
        // filter = new String[][] { new String[] { "summary.config.guestId", "winXPProGuest",},};
        // vmname = this.machineName;

        filter =
            new String[][] {new String[] {"guest.ipAddress", ipaddress,},
            new String[] {"summary.config.guestId", guestid,}};

        vmList = getVMs("VirtualMachine", datacenter, folder, pool, vmname, host, filter);

        return vmList;
    }

    /**
     * Private helper to get a list of virtual machine managed object references
     * 
     * @param entity
     * @param datacenter
     * @param folder
     * @param pool
     * @param vmname
     * @param host
     * @param filter
     * @return a list of virtual machine managed object references
     * @throws Exception
     */
    public ArrayList<ManagedObjectReference> getVMs(String entity, String datacenter,
        String folder, String pool, String vmname, String host, String[][] filter)
        throws VirtualMachineException
    {
        ManagedObjectReference dsMOR = null;
        ManagedObjectReference hostMOR = null;
        ManagedObjectReference poolMOR = null;
        // ManagedObjectReference vmMOR = null;
        ManagedObjectReference folderMOR = null;
        ManagedObjectReference tempMOR = null;
        ArrayList<ManagedObjectReference> vmList = new ArrayList<ManagedObjectReference>();
        String[][] filterData = null;

        if (datacenter != null)
        {
            try
            {
                dsMOR = getServiceUtil().getDecendentMoRef(null, "Datacenter", datacenter);

                if (dsMOR == null)
                {
                    throw new Exception();
                }
            }
            catch (Exception e)
            {
                final String msg = "Can get ''Datacenter'' :" + datacenter;
                throw new VirtualMachineException(msg, e);
            }

            tempMOR = dsMOR;
        }

        if (folder != null)
        {
            try
            {
                folderMOR = getServiceUtil().getDecendentMoRef(tempMOR, "Folder", folder);

                if (folderMOR == null)
                {
                    throw new Exception();
                }
            }
            catch (Exception e)
            {
                final String msg = "Can get ''Folder'' :" + folder;
                throw new VirtualMachineException(msg, e);
            }

            tempMOR = folderMOR;
        }

        if (pool != null)
        {
            try
            {
                poolMOR = getServiceUtil().getDecendentMoRef(tempMOR, "ResourcePool", pool);
                if (poolMOR == null)
                {
                    throw new Exception();
                }
            }
            catch (Exception e)
            {
                final String msg = "Can get ''ResourcePool'' :" + pool;
                throw new VirtualMachineException(msg, e);
            }

            tempMOR = poolMOR;
        }

        if (host != null)
        {
            try
            {
                hostMOR = getServiceUtil().getDecendentMoRef(tempMOR, "HostSystem", host);

                if (hostMOR == null)
                {
                    throw new Exception();
                }
            }
            catch (Exception e)
            {
                final String msg = "Can get ''HostSystem'' :" + host;
                throw new VirtualMachineException(msg, e);
            }

            tempMOR = hostMOR;
        }

        if (vmname != null)
        {
            int i = 0;
            filterData = new String[filter.length + 1][2];
            for (i = 0; i < filter.length; i++)
            {
                filterData[i][0] = filter[i][0];
                filterData[i][1] = filter[i][1];
            }
            // Adding the vmname in the filter
            filterData[i][0] = "name";
            filterData[i][1] = vmname;
        }
        else if (vmname == null)
        {
            int i = 0;
            filterData = new String[filter.length + 1][2];
            for (i = 0; i < filter.length; i++)
            {
                filterData[i][0] = filter[i][0];
                filterData[i][1] = filter[i][1];
            }
        }

        try
        {
            vmList = getVM(tempMOR, filterData);

            if ((vmList == null) || (vmList.size() == 0))
            {
                throw new Exception();
            }
        }
        catch (Exception e)
        {
            final String msg =
                "The virtual machine : " + vmname + "couldn't be found in the hypervisor";
            throw new VirtualMachineException(msg, e);
        }

        return vmList;
    }

    @SuppressWarnings("unchecked")
    private ArrayList<ManagedObjectReference> getVM(ManagedObjectReference tempMOR,
        String[][] filterData) throws Exception
    {
        return (ArrayList<ManagedObjectReference>) getServiceUtil().getDecendentMoRefs(tempMOR,
            "VirtualMachine", filterData);
    }

    @SuppressWarnings("unchecked")
    private ArrayList<ManagedObjectReference> getNetwork(ManagedObjectReference tempMOR,
        String[][] filterData) throws Exception
    {
        return (ArrayList<ManagedObjectReference>) getServiceUtil().getDecendentMoRefs(tempMOR,
            "Network", filterData);
    }

    /**
     * Private helper to get the virtual machine power state
     * 
     * @param vmmor the MOR to get the info from
     * @return the virtual machine state
     * @throws Exception if there is any error
     */
    public VirtualMachinePowerState getVMState(ManagedObjectReference vmmor) throws Exception
    {
        DynamicProperty[] virtualMachineRuntimeInfoProperty;
        VirtualMachineRuntimeInfo runtimeInfo;
        virtualMachineRuntimeInfoProperty = getDynamicProarray(vmmor, "runtime");
        runtimeInfo = ((VirtualMachineRuntimeInfo) (virtualMachineRuntimeInfoProperty[0]).getVal());
        return runtimeInfo.getPowerState();
    }

    /**
     * Private helper to check if the task has been success executed.
     * 
     * @param taskmor the MOR to get the info from
     * @throws VirtualMachineException it there is any error.
     */
    public void checkTaskState(ManagedObjectReference taskmor) throws VirtualMachineException
    {
        DynamicProperty[] taskInfoProperty;
        TaskInfo tinfo;
        String taskResult;

        try
        {
            taskInfoProperty = getDynamicProarray(taskmor, "info");
            tinfo = ((TaskInfo) (taskInfoProperty[0]).getVal()); // TODO only the first relevant ??
        }
        catch (Exception e)
        {
            final String msg =
                "Can not get the Dynamic property ''info'' for task " + taskmor.get_value();
            throw new VirtualMachineException(msg, e);
        }

        // wait task completion

        try
        {
            taskResult = getServiceUtil().waitForTask(taskmor);
        }
        catch (Exception e)
        {
            final String msg = "Exception while waiting task completion " + taskmor.get_value();
            throw new VirtualMachineException(msg, e);
        }

        if (taskResult.equalsIgnoreCase("success")) 
        {
            return; // any exception
        }
        else
        {
            throw new VirtualMachineException("Task " + taskmor.get_value() + " FAIL :" + taskResult);            
        }
    }

    /**
     * Private helper to get an array dinamic property
     * 
     * @param MOR the Managed Object Referented
     * @param pName the property name
     * @return the array dinamy property
     * @throws Exception
     */
    public DynamicProperty[] getDynamicProarray(ManagedObjectReference MOR, String pName)
        throws Exception
    {
        ObjectContent[] objContent =
            getServiceUtil().getObjectProperties(null, MOR, new String[] {pName});
        ObjectContent contentObj = objContent[0];
        DynamicProperty[] objArr = contentObj.getPropSet();

        return objArr;
    }

    /**
     * Private helper to monitor the task launched
     * 
     * @param tmor the task managed object reference
     * @throws Exception
     */
    public void monitorTask(ManagedObjectReference tmor) throws Exception
    {
        if (tmor != null)
        {
            String result = getServiceUtil().waitForTask(tmor);
            if (result.equalsIgnoreCase("success"))
            {
                logger.info("Task Completed Sucessfully");
            }
            else
            {
                logger.error("Failure " + result);
                throw new Exception("The task could not be performed");
            }
        }
    }

    /**
     * Gets the management object reference from the virtual machine name
     * 
     * @param vmName the virtual machine name
     * @throws Exception
     */
    public ManagedObjectReference getVmMor(String vmName) throws Exception
    {
        ServiceInstance si = apputil.getServiceInstance();

        Folder rootFolder = si.getRootFolder();
        VirtualMachine vm =
            (VirtualMachine) new InventoryNavigator(rootFolder).searchManagedEntity(
                "VirtualMachine", vmName);
        if (vm != null)
        {
            return vm.getMOR();
        }
        else
        {
            return null;
        }
    }

    // Retrieve properties from a single MoRef
    public Object[] getProperties(ManagedObjectReference moRef, String[] properties)
        throws RuntimeFault, RemoteException
    {
        ServiceContent content = apputil.getServiceInstance().getServiceContent();
        PropertySpec pSpec = new PropertySpec();
        pSpec.setType(moRef.getType());
        pSpec.setPathSet(properties);

        ObjectSpec oSpec = new ObjectSpec();
        // Set the starting object
        oSpec.setObj(moRef);
        PropertyFilterSpec pfSpec = new PropertyFilterSpec();
        pfSpec.setPropSet(new PropertySpec[] {pSpec});
        pfSpec.setObjectSet(new ObjectSpec[] {oSpec});
        ObjectContent[] ocs =
            apputil.getServiceInstance().getServerConnection().getVimService().retrieveProperties(
                content.getPropertyCollector(), new PropertyFilterSpec[] {pfSpec});

        Object[] ret = new Object[properties.length];

        if (ocs != null)
        {
            for (int i = 0; i < ocs.length; ++i)
            {
                ObjectContent oc = ocs[i];
                DynamicProperty[] dps = oc.getPropSet();
                if (dps != null)
                {
                    for (int j = 0; j < dps.length; ++j)
                    {
                        DynamicProperty dp = dps[j];
                        for (int p = 0; p < ret.length; ++p)
                        {
                            if (properties[p].equals(dp.getName()))
                            {
                                logger.debug("property named [{}] value [{}]", dp.getName(), dp
                                    .getVal().toString());
                                ret[p] = dp.getVal();
                            }
                        }
                    }
                }
            }
        }
        return ret;
    }

    /**
     * Get the computer resource associated to the given host.
     * 
     * @param crmors, all the computer resource on the data center.
     * @param hostmor, reference to the host related to the current VM.
     */
    public ManagedObjectReference getComputerResourceFromHost(
        ArrayList<ManagedObjectReference> crmors, ManagedObjectReference hostmor)
        throws VirtualMachineException
    {

        ManagedObjectReference crmor = null;
        String hostName;

        try
        {
            hostName = (String) getServiceUtil().getDynamicProperty(hostmor, "name");
        }
        catch (Exception e)
        {
            throw new VirtualMachineException("Can not get ''name'' property for the given HostMOR");
        }

        for (int i = 0; i < crmors.size(); i++)
        {
            try
            {
                ManagedObjectReference[] hrmors =
                    (ManagedObjectReference[]) getServiceUtil().getDynamicProperty(
                        (ManagedObjectReference) crmors.get(i), "host");

                if (hrmors != null && hrmors.length > 0)
                {
                    for (int j = 0; j < hrmors.length; j++)
                    {
                        String hname =
                            (String) getServiceUtil().getDynamicProperty(hrmors[j], "name");
                        if (hname.equalsIgnoreCase(hostName))
                        {
                            crmor = (ManagedObjectReference) crmors.get(i);
                            i = crmors.size() + 1;
                            j = hrmors.length + 1;
                        }
                    }
                }
            }
            catch (Exception e)
            {
                final String msg = "Can not get host on the computer resource ";
                throw new VirtualMachineException(msg, e);
            }
        }

        if (crmor == null)
        {
            String message = "No Compute Resource Found On Specified Host";
            logger.error(message);
            throw new VirtualMachineException(message);
        }

        return crmor;
    }

    /**
     * Gets the resource allocation information from the resource value
     * 
     * @param value the value
     * @return the resource allocation information
     * @throws Exception
     */
    public ResourceAllocationInfo getShares(String value) throws Exception
    {
        ResourceAllocationInfo raInfo = new ResourceAllocationInfo();
        SharesInfo sharesInfo = new SharesInfo();

        if (value.equalsIgnoreCase(SharesLevel.high.name()))
        {
            sharesInfo.setLevel(SharesLevel.high);
        }
        else if (value.equalsIgnoreCase(SharesLevel.normal.name()))
        {
            sharesInfo.setLevel(SharesLevel.normal);
        }
        else if (value.equalsIgnoreCase(SharesLevel.low.name()))
        {
            sharesInfo.setLevel(SharesLevel.low);
        }
        else
        {
            sharesInfo.setLevel(SharesLevel.custom);
            sharesInfo.setShares(Integer.parseInt(value));
        }
        raInfo.setShares(sharesInfo);
        return raInfo;
    }

    /**
     * Open a session to the VMWare ESXi
     * 
     * @throws VirtualMachineException
     * @throws HypervisorException
     */
    public void reconnect() throws VirtualMachineException
    {
        URL address = null;
        try
        {
            address = vmwareHyper.getAddress();

            vmwareHyper.init(address);
            vmwareHyper.connect(address);
            apputil = vmwareHyper.getAppUtil();
        }
        catch (HypervisorException e)
        {
            logger.error("An error was occurred when reconnecting to the hypervisor: {}", address
                .toExternalForm());
            throw new VirtualMachineException(e);

        }
    }

    /**
     * Close the current session.
     */
    public void logout()
    {
        vmwareHyper.logout();
    }

}
