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

package com.abiquo.virtualfactory.hypervisor.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.util.ExtendedAppUtil;
import com.abiquo.util.ServiceUtil;
import com.abiquo.virtualfactory.constants.MessageValues;
import com.abiquo.virtualfactory.exception.HypervisorException;
import com.abiquo.virtualfactory.exception.VirtualMachineException;
import com.abiquo.virtualfactory.machine.impl.VmwareMachine;
import com.abiquo.virtualfactory.machine.impl.VmwareMachineUtils;
import com.abiquo.virtualfactory.model.AbiCloudModel;
import com.abiquo.virtualfactory.model.AbsVirtualMachine;
import com.abiquo.virtualfactory.model.IHypervisor;
import com.abiquo.virtualfactory.model.State;
import com.abiquo.virtualfactory.model.config.Configuration;
import com.abiquo.virtualfactory.model.config.VirtualMachineConfiguration;
import com.abiquo.virtualfactory.model.config.VmwareHypervisorConfiguration;
import com.vmware.vim25.DatastoreInfo;
import com.vmware.vim25.DuplicateName;
import com.vmware.vim25.HostNasVolume;
import com.vmware.vim25.HostNasVolumeSpec;
import com.vmware.vim25.KeyAnyValue;
import com.vmware.vim25.LicenseManagerLicenseInfo;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.NasDatastoreInfo;
import com.vmware.vim25.VimFault;
import com.vmware.vim25.mo.Datastore;
import com.vmware.vim25.mo.HostDatastoreSystem;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.util.OptionSpec;

public class VmwareHypervisor implements IHypervisor
{

    /** The Constant logger. */
    private final static Logger logger = LoggerFactory.getLogger(VmwareHypervisor.class.getName());

    private final HashMap<String, String> optsEntered = new HashMap<String, String>();

    private ExtendedAppUtil apputil;

    private ServiceUtil serviceUtil;

    private URL url;

    private String user;

    private String password;

    /**
     * It adds parameters from the configuration file
     * 
     * @param config
     * @param user TODO
     * @param password TODO
     */
    private void builtinOptionsEntered(final VmwareHypervisorConfiguration config,
        final String user, final String password)
    {
        optsEntered.put("username", user);
        optsEntered.put("password", password);
        this.user = user;
        this.password = password;

        optsEntered.put("ignorecert", config.getIgnorecert().toString());
        optsEntered.put("datacentername", config.getDatacenterName());
    }

    /**
     * It constructs the basic options needed to work
     * 
     * @return
     */
    private static OptionSpec[] constructOptions()
    {
        OptionSpec[] useroptions = new OptionSpec[7];
        useroptions[0] = new OptionSpec("vmname", "String", 1, "Name of the virtual machine", null);
        useroptions[1] =
            new OptionSpec("datacentername", "String", 1, "Name of the datacenter", null);
        useroptions[2] = new OptionSpec("hostname", "String", 0, "Name of the host", null);
        useroptions[3] =
            new OptionSpec("guestosid", "String", 0, "Type of Guest OS", "winXPProGuest");
        useroptions[4] = new OptionSpec("cpucount", "Integer", 0, "Total CPU Count", "1");
        useroptions[5] = new OptionSpec("disksize", "Integer", 0, "Size of the Disk", "64");
        useroptions[6] =
            new OptionSpec("memorysize",
                "Integer",
                0,
                "Size of the Memory in the blocks of 1024 MB",
                "1024");
        return useroptions;
    }

    /**
     * Gets the utility to connect the vmware VI
     * 
     * @return
     */
    public ExtendedAppUtil getAppUtil()
    {
        return apputil;
    }

    @Override
    public void connect(final URL url) throws HypervisorException
    {
        try
        {
            if (apputil != null && apputil.isConnected())
            {
                logout();
            }

            ServiceInstance serviceInstance = new ServiceInstance(this.url, user, password, true);
            apputil = ExtendedAppUtil.init(serviceInstance, constructOptions(), optsEntered);
            serviceUtil = ServiceUtil.CreateServiceUtil();
            serviceUtil.init(apputil);

            checkLicense();

            checkRepositoryDatastore();
        }
        catch (Exception e)
        {
            logger.debug("An error was occurred when connecting to the hypervisor", e);
            logout();
            throw new HypervisorException(MessageValues.CONN_EXCP_I, e);
        }
    }

    @Override
    public AbsVirtualMachine createMachine(final VirtualMachineConfiguration config)
        throws VirtualMachineException
    {
        return new VmwareMachine(config);
    }

    @Override
    public URL getAddress()
    {
        return url;
    }

    @Override
    public String getHypervisorType()
    {
        return HypervisorType.VMX_04.getValue();
    }

    @Override
    public void login(final String user, final String password)
    {
        optsEntered.put("username", user);
        optsEntered.put("password", password);
        this.user = user;
        this.password = password;

    }

    @Override
    public void logout()
    {
        try
        {
            this.getAppUtil().disConnect();
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
        }

    }

    @Override
    public void init(final URL url, final String user, final String password)
        throws HypervisorException
    {
        AbiCloudModel model = AbiCloudModel.getInstance();
        Configuration mainConfig = model.getConfigManager().getConfiguration();
        VmwareHypervisorConfiguration config = mainConfig.getVmwareHyperConfig();
        // Converting to https
        String ip = url.getHost();
        URL sdkUrl = null;
        try
        {
            sdkUrl = new URL("https://" + ip + "/sdk");
        }
        catch (MalformedURLException e1)
        {
            logger.error(e1.getMessage());
        }
        this.optsEntered.put("url", sdkUrl.toString());
        this.url = sdkUrl;
        // 1. Configure the default parameters from the config file
        builtinOptionsEntered(config, user, password);

        // try
        // {
        // ServiceInstance serviceInstance = new ServiceInstance(sdkUrl, user, password, true);
        // apputil = ExtendedAppUtil.init(serviceInstance, constructOptions(), optsEntered);
        // serviceUtil = ServiceUtil.CreateServiceUtil();
        // serviceUtil.init(apputil);
        // }
        // catch (Exception e)
        // {
        // logger.error(e.getMessage(), e);
        // }
    }

    /**
     * Private helper to check if vmware license is not FREE basic
     */
    private void checkLicense() throws HypervisorException
    {
        ManagedObjectReference licenseManager =
            apputil.getServiceInstance().getServiceContent().getLicenseManager();
        try
        {
            LicenseManagerLicenseInfo[] licenseInfo =
                (LicenseManagerLicenseInfo[]) serviceUtil.getDynamicProperty(licenseManager,
                    "licenses");
            for (LicenseManagerLicenseInfo licenseManagerLicenseInfo : licenseInfo)
            {
                if (licenseManagerLicenseInfo.getEditionKey().equals("esxBasic"))
                {
                    throw new HypervisorException("ESXi version is not supported");
                }

                KeyAnyValue[] properties = licenseManagerLicenseInfo.getProperties();

                Long expirationHours = new Long(0);
                Long expirationMinutes = new Long(0);
                boolean neverExpires = true;

                for (KeyAnyValue keyAnyValue : properties)
                {
                    if ("expirationHours".equals(keyAnyValue.getKey()))
                    {
                        expirationHours = (Long) keyAnyValue.getValue();
                        neverExpires = false;
                    }
                    else if ("expirationMinutes".equals(keyAnyValue.getKey()))
                    {
                        expirationMinutes = (Long) keyAnyValue.getValue();
                        neverExpires = false;
                    }
                }

                if (!neverExpires)
                {
                    if (expirationHours.intValue() == 0 || expirationMinutes.intValue() == 0)
                    {
                        throw new HypervisorException("ESXi version is not supported");
                    }
                }
            }
        }
        catch (Exception e)
        {
            throw new HypervisorException("ESXi version is not supported");
        }

    }

    private void checkRepositoryDatastore() throws HypervisorException
    {
        // init the datastore name
        String repositoryLocation =
            System.getProperty("abiquo.virtualfactory.vmware.repositoryLocation");

        String errorMessage =
            String.format("Can not configure the repository datastore [%s]", repositoryLocation);

        if (repositoryLocation == null || repositoryLocation.isEmpty()
            || !repositoryLocation.contains(":"))
        {
            throw new HypervisorException("Invalid or missing repository location attribute");
        }
        int pos = repositoryLocation.indexOf(':');
        String remoteHost = repositoryLocation.substring(0, pos);
        String remotePath = repositoryLocation.substring(pos + 1);

        if (remotePath.endsWith("/"))
        {
            remotePath = remotePath.substring(0, remotePath.length() - 1);
        }

        String datastoreName;
        try
        {
            datastoreName = obtainNAS(remoteHost, remotePath);
            AbiCloudModel.getInstance().getConfigManager().getConfiguration()
                .getVmwareHyperConfig().setDatastoreSanName(datastoreName);
        }
        catch (DuplicateName e)
        {
            logger.warn("Datastore already configured " + repositoryLocation + " \t at :"
                + e.getName());

            AbiCloudModel.getInstance().getConfigManager().getConfiguration()
                .getVmwareHyperConfig().setDatastoreSanName(e.getName());
        }
        catch (VimFault ev)
        {
            String errorDetail =
                ev.getFaultMessage() != null && ev.getFaultMessage().length > 0 ? ev
                    .getFaultMessage()[0].message : ev.getMessage();

            throw new HypervisorException(errorMessage + "\n" + errorDetail);
        }
        catch (Exception e)
        {
            throw new HypervisorException(errorMessage);
        }
    }

    /**
     * It prepares the vmware ESXi to work with abicloud infrastructure. It creates a NFS datastore
     * from the configuration file
     * 
     * @param hostName the hostname where the datastore will be created.
     * @throws Exception
     * @return the local name to be used to refere to the NAS datastore
     */
    private String obtainNAS(final String remoteHost, final String remotePath) throws Exception
    {
        VmwareMachineUtils utils = new VmwareMachineUtils(this);

        HostSystem host = utils.getHostSystem();

        Datastore[] datastores = host.getDatastores();

        if (datastores == null)
        {
            datastores = new Datastore[0];
        }

        for (Datastore datastore : datastores)
        {
            DatastoreInfo dsinfo = datastore.getInfo();

            // looking only on NAS based datastores
            if (dsinfo instanceof NasDatastoreInfo)
            {
                NasDatastoreInfo nasinfo = (NasDatastoreInfo) dsinfo;
                HostNasVolume nas = nasinfo.getNas();

                if (remoteHost.equalsIgnoreCase(nas.getRemoteHost())
                    && remotePath.equalsIgnoreCase(nas.getRemotePath()))
                {
                    return nas.getName();
                }
            }
        }

        // Datastore not found, creating it.
        final String localPath = String.format("ABQ_%s", UUID.randomUUID().toString());

        HostNasVolumeSpec spec = new HostNasVolumeSpec();
        spec.setRemoteHost(remoteHost);
        spec.setAccessMode("readWrite");// HostMountMode.readWrite.name());
        spec.setRemotePath(remotePath);
        spec.setLocalPath(localPath);
        // can configure username/password for 'type' CIFS

        HostDatastoreSystem hds = host.getHostDatastoreSystem();
        Datastore newDs = hds.createNasDatastore(spec);

        // ManagedObjectReference hostDatastoreSystemMOR = hds.getMOR();
        // ManagedObjectReference newdatastore =
        // utils.getService().createNasDatastore(hostDatastoreSystemMOR, spec);

        return newDs.getName();
    }

    // //
    private void copyDataStorefile() throws Exception
    {
        Configuration mainConfig =
            AbiCloudModel.getInstance().getConfigManager().getConfiguration();
        VmwareHypervisorConfiguration config = mainConfig.getVmwareHyperConfig();
        // String dcName = apputil.get_option("datacentername");
        String dcName = config.getDatacenterName();
        ManagedObjectReference dcmor = serviceUtil.getDecendentMoRef(null, "Datacenter", dcName);
        ManagedObjectReference fileManager =
            apputil.getServiceInstance().getServiceContent().getFileManager();
        ManagedObjectReference taskCopyMor =
            serviceUtil.getVimService().copyDatastoreFile_Task(fileManager,
                "[nfsrepository] ubuntu810desktop/ubuntu810desktop-flat.vmdk", dcmor,
                "[datastore1] test/test-flat.vmdk", dcmor, true);
        /*
         * ManagedObjectReference taskCopyMor =
         * serviceUtil.getService().copyDatastoreFile_Task(fileManager,
         * "[datastore1] testubuntu/testubuntu.vmdk", dcmor,
         * "[datastore1] 11b0b35e-4810-4aed-95c5-12b4dc06e80a/11b0b35e-4810-4aed-95c5-12b4dc06e80a.vmdk"
         * , dcmor, true);
         */
        // ManagedObjectReference taskCopyMor =
        // serviceUtil.getService().copyVirtualDisk_Task(virtualDiskManager,
        // "[nfsrepository] ubuntu/Ubuntu.8.10.Server.vmdk", dcmor,
        // "[datastore1] test/Nostalgia.vmdk", dcmor, null, true);
        String res = serviceUtil.waitForTask(taskCopyMor);
        if (res.equalsIgnoreCase("success"))
        {
            logger.info("Virtual Machine Created Sucessfully");
        }
        else
        {
            String message = "Virtual Machine could not be created. " + res;
            logger.error(message);
            throw new VirtualMachineException(message);
        }
    }

    /**
     * Initializes the hypervisor
     * 
     * @param address the hypervisor address
     * @throws HypervisorException
     */
    public void init(final URL address) throws HypervisorException
    {
        init(address, user, password);

    }

    @Override
    public void disconnect() throws HypervisorException
    {
        logout();
    }

    @Override
    public AbsVirtualMachine getMachine(final VirtualMachineConfiguration virtualMachineConfig)
        throws HypervisorException
    {
        // Gets the VMWare API main interface
        init(this.url, this.user, this.password);
        connect(this.url);
        VmwareMachineUtils utils = new VmwareMachineUtils(this);
        try
        {
            ManagedObjectReference machinemor =
                utils.getVmMor(virtualMachineConfig.getMachineName());
            ManagedObjectReference machinemoruuid =
                utils.getVmMor(virtualMachineConfig.getMachineId().toString());
            if (machinemor != null || machinemoruuid != null)
            {
                virtualMachineConfig.setHypervisor(this);
                AbsVirtualMachine vm = createMachine(virtualMachineConfig);
                vm.setState(State.DEPLOYED);
                return vm;
            }
            else
            {
                logger.debug(MessageValues.VM_NOT_FOUND + virtualMachineConfig.getMachineName());
                return null;
            }
        }
        catch (Exception e)
        {
            throw new HypervisorException(e);
        }
        finally
        {
            logout();
        }

    }

}
