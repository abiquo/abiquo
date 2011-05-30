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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.ws.WebServiceException;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.virtualbox_4_0.AccessMode;
import org.virtualbox_4_0.CleanupMode;
import org.virtualbox_4_0.DeviceType;
import org.virtualbox_4_0.IMachine;
import org.virtualbox_4_0.IMedium;
import org.virtualbox_4_0.INetworkAdapter;
import org.virtualbox_4_0.IProgress;
import org.virtualbox_4_0.ISession;
import org.virtualbox_4_0.IStorageController;
import org.virtualbox_4_0.IVRDEServer;
import org.virtualbox_4_0.IVirtualBox;
import org.virtualbox_4_0.LockType;
import org.virtualbox_4_0.MachineState;
import org.virtualbox_4_0.NetworkAdapterType;
import org.virtualbox_4_0.SessionState;
import org.virtualbox_4_0.StorageBus;

import com.abiquo.aimstub.Aim.Iface;
import com.abiquo.aimstub.Datastore;
import com.abiquo.aimstub.RimpException;
import com.abiquo.aimstub.TTransportProxy;
import com.abiquo.util.AddressingUtils;
import com.abiquo.virtualfactory.exception.VirtualMachineException;
import com.abiquo.virtualfactory.hypervisor.impl.VirtualBoxHypervisor;
import com.abiquo.virtualfactory.model.AbiCloudModel;
import com.abiquo.virtualfactory.model.AbsVirtualMachine;
import com.abiquo.virtualfactory.model.State;
import com.abiquo.virtualfactory.model.VirtualDisk;
import com.abiquo.virtualfactory.model.VirtualDiskType;
import com.abiquo.virtualfactory.model.config.VirtualMachineConfiguration;
import com.abiquo.virtualfactory.network.VirtualNIC;
import com.abiquo.virtualfactory.vlanstub.VLANException;
import com.abiquo.virtualfactory.vlanstub.VlanStub;

/**
 * The virtualBox machine representation.
 * 
 * @author pnavarro
 */
public class VirtualBoxMachine extends AbsVirtualMachine
{

    /** The logger. */
    private static final Logger logger = LoggerFactory.getLogger(VirtualBoxMachine.class);

    /** The v box hyper. */
    private VirtualBoxHypervisor vBoxHyper;

    /** The machine. */
    private IMachine machine;

    /** The machine name. */
    private final String machineName;

    /** the memory ram in Mbytes. */
    private final long memoryRam;

    /** The number of cpus. */
    private final int cpuNumbers;

    /** The machine id. */
    private final String machineId;

    /** The remote desktop port. */
    private final int rdpPort;

    /** The cloned VDI disk. */
    private IMedium newVDI;

    /** The Virtual NIC list */
    private final List<VirtualNIC> vnicList;

    /** Timeout to wait for a virtual machine state transition. */
    private final static Integer OPERATION_TIMEOUT = 2 * 60000; // 2 minutes

    /*
     * The cloned image name. private String clonedImageName;
     */

    /** The storage controller name. */
    private final String sataStorageControllerName;

    /** The storage controller name. */
    private final String scsiStorageControllerName;

    /** The last storage controller port. */
    private int lastControllerPortUsed;

    /** Maximum devices per port count. */
    private long maxDevicesPerPortCount;

    /**
     * Instantiates a new virtual box machine.
     * 
     * @param config the config
     * @throws VirtualMachineException the virtual machine exception
     */
    public VirtualBoxMachine(VirtualMachineConfiguration config) throws VirtualMachineException
    {
        super(config);

        if (config.isSetHypervisor() && config.getHyper() instanceof VirtualBoxHypervisor)
        {
            vBoxHyper = (VirtualBoxHypervisor) config.getHyper();
        }
        else
        {
            throw new VirtualMachineException("VirtualBoxMachiner requires a VirtualBoxHypervisor "
                + "on VirtualMachineConfiguration, not a "
                + config.getHyper().getClass().getCanonicalName());
        }

        machineName = config.getMachineName();
        machineId = config.getMachineId().toString();
        rdpPort = config.getRdPort();
        cpuNumbers = config.getCpuNumber();//
        memoryRam = config.getMemoryRAM() / (1024 * 1024);
        sataStorageControllerName = machineId.concat("SATASController");
        scsiStorageControllerName = machineId.concat("SCSISController");
        lastControllerPortUsed = 0;
        vnicList = config.getVnicList();
    }

    /**
     * Deploys the machine.
     * 
     * @throws VirtualMachineException the exception
     */
    @Override
    public void deployMachine() throws VirtualMachineException
    {

        try
        {
            vBoxHyper.reconnect();

            if (!isVMAlreadyCreated())
            {
                // Create the virtual machine
                createVirtualMachine();

                VirtualDisk diskBase = config.getVirtualDiskBase();

                if (diskBase.getDiskType() == VirtualDiskType.STANDARD)
                {
                    // Just clones the image if the virtual disk is standard
                    // Cloning the virtual disk

                    cloneVirtualDisk();
                }

                // Configures the virtual machine
                configureVirtualMachine();
            }

            checkIsCancelled();
        }
        catch (Exception e)
        {
            logger.error("Failed to deploy machine :{}", e);
            // The roll back in the virtual machine is done in top level when
            // rolling back the
            // virtual appliance
            rollBackVirtualMachine();
            state = State.CANCELLED;
            throw new VirtualMachineException(e);
        }

        logger.info("Created virtualbox machine name:" + config.getMachineName() + "\t ID:"
            + config.getMachineId().toString() + "\t " + "using hypervisor connection at "
            + config.getHyper().getAddress().toString());

        state = State.DEPLOYED;

    }

    /**
     * Private helper to create a virtual machine template from the open virtualization format
     * parameters.
     * 
     * @throws Exception
     */
    private void createVirtualMachine()
    {
        // Getting the virtualBox hypervisor
        IVirtualBox vbox = vBoxHyper.getVirtualBox();
        // Creating the machine implies 4 steps
        // 1. Creating the mutable machine, or opening if it was created
        logger.info("Creating the virtual machine in the hypervisor");

        machine =
            vbox.createMachine(null, config.getMachineName(), "Other", config.getMachineId()
                .toString(), Boolean.TRUE);

        logger.info("VirtualBox machine created succesfully");
    }

    /**
     * Private helper to configure VirtualMachine.
     * 
     * @throws VirtualMachineException
     */
    private void configureVirtualMachine() throws VirtualMachineException
    {
        // Getting the session
        ISession oSession = vBoxHyper.getSession();
        try
        {

            configureBasicResources();

            configureVirtualDiskResources();

            configureNetwork();

            machine.saveSettings();

            vBoxHyper.getSession().unlockMachine();

        }
        catch (WebServiceException e)
        {
            throw new VirtualMachineException(e);
        }
        finally
        {
            // Closing the sessions
            if (machine != null)
            {
                machine.releaseRemote();
            }
            if (oSession != null)
            {
                oSession.releaseRemote();
            }
        }
    }

    /**
     * Configures the Virtual disk resources
     * 
     * @throws VirtualMachineException
     */
    private void configureVirtualDiskResources() throws VirtualMachineException
    {
        logger.debug("Configuring Virtual disks resources");

        // Getting the virtualBox hypervisor
        IVirtualBox vbox = vBoxHyper.getVirtualBox();

        // Using SATA driver for attaching extended disks
        IStorageController controller =
            machine.addStorageController(sataStorageControllerName, StorageBus.SATA);

        // Adding the scsci controller

        /**
         * Setting the I/O cache prevents to the virtual machine process to hang when power off.
         * <p>
         * http://www.virtualbox.org/ticket/8276
         * </p>
         * (12:13:44 PM) klaus-vb: apuig: the workaround is disabling async I/O for the controller,
         * by ticking "use host I/O cache" for the scsi controller in the VM config.
         */
        machine.addStorageController(scsiStorageControllerName, StorageBus.SCSI).setUseHostIOCache(
            true);

        if (config.getVirtualDiskBase().getDiskType() == VirtualDiskType.STANDARD)
        {
            // Attaching base disks
            logger.debug("Attaching the harddisk with the id: {}", newVDI.getId());

            machine.attachDevice(scsiStorageControllerName, 0, 0, DeviceType.HardDisk, newVDI);

            maxDevicesPerPortCount = controller.getPortCount().longValue();
        }
        else if (config.getVirtualDiskBase().getDiskType() == VirtualDiskType.ISCSI)
        {
            // If the virtual disk is an statefull one
            attachIscsiDisk(config.getVirtualDiskBase(), vbox, machine, scsiStorageControllerName);
        }

        // Attaching extended disks
        attachExtendedDisks(config.getExtendedVirtualDiskList(), vbox, machine);

    }

    /**
     * Configures the networking
     * 
     * @throws VirtualMachineException
     */
    private void configureNetwork() throws VirtualMachineException
    {
        logger.debug("Configuring network");

        // Attaching the bridge interfaces
        attachBridgeInterfaces(vnicList);

    }

    /**
     * Private helper to configure the basic resources, like RAM and CPU
     */
    private void configureBasicResources()
    {
        logger.debug("Configuring Basic resources");
        ISession oSession = null;
        // Getting the virtualBox hypervisor
        IVirtualBox vbox = vBoxHyper.getVirtualBox();
        // 3. Saving settings ¿? We do it below
        // targetMachine.saveSettings();
        // 4. Registering machine
        vbox.registerMachine(machine);
        // Getting the session
        oSession = vBoxHyper.getSession();
        machine.lockMachine(oSession, LockType.Write);
        machine = oSession.getMachine();

        // Definining RAM and CPU number
        machine.setMemorySize(memoryRam);
        machine.setCPUCount(new Long(cpuNumbers));
    }

    /**
     * Private helper to attach bridge interfaces.
     * 
     * @param vnicList the mac list with the interface bridge list to attach
     * @throws VirtualMachineException
     */
    private void attachBridgeInterfaces(List<VirtualNIC> vnicList) throws VirtualMachineException
    {
        try
        {
            String abiquoPrefix =
                AbiCloudModel.getInstance().getConfigManager().getConfiguration().getBridgePrefix();
            for (VirtualNIC virtualNIC : vnicList)
            {
                String bridgeName = abiquoPrefix + "_" + virtualNIC.getVlanTag();

                // Creating the VLAN
                URL phymach_ip = vBoxHyper.getAddress();

                URL aimURL =
                    new URL(phymach_ip.getProtocol(),
                        phymach_ip.getHost(),
                        8889,
                        phymach_ip.getFile());

                VlanStub.createVlan(aimURL, String.valueOf(virtualNIC.getVlanTag()),
                    virtualNIC.getVSwitchName(), bridgeName);

                attachNetworkAdapter(machine, virtualNIC.getMacAddress(), abiquoPrefix + "_"
                    + virtualNIC.getVlanTag(), vnicList.indexOf(virtualNIC));
            }
        }
        catch (VLANException e)
        {
            throw new VirtualMachineException("An error was occurred when configuring the networking for virtual machine :"
                + machineName,
                e);
        }
        catch (MalformedURLException e)
        {
            throw new VirtualMachineException("An error was occurred when configuring the networking for virtual machine :"
                + machineName,
                e);
        }
    }

    /**
     * Private helper to detach bridge interfaces.
     * 
     * @param vnicList the mac list with the interface bridge list to attach
     * @throws VirtualMachineException
     */
    private void detachBridgeInterfaces(List<VirtualNIC> vnicList) throws VirtualMachineException
    {
        try
        {
            String abiquoPrefix =
                AbiCloudModel.getInstance().getConfigManager().getConfiguration().getBridgePrefix();
            for (VirtualNIC virtualNIC : vnicList)
            {
                String bridgeName = abiquoPrefix + "_" + virtualNIC.getVlanTag();

                // Creating the VLAN
                URL phymach_ip = vBoxHyper.getAddress();

                URL aimURL =
                    new URL(phymach_ip.getProtocol(),
                        phymach_ip.getHost(),
                        8889,
                        phymach_ip.getFile());

                if (mustDeleteVLAN(bridgeName))
                {
                    VlanStub.deleteVlan(aimURL, String.valueOf(virtualNIC.getVlanTag()),
                        virtualNIC.getVSwitchName(), bridgeName);

                }
            }
        }
        catch (VLANException e)
        {
            throw new VirtualMachineException("An error was occurred when configuring the networking for virtual machine :"
                + machineName,
                e);
        }
        catch (MalformedURLException e)
        {
            throw new VirtualMachineException("An error was occurred when configuring the networking for virtual machine :"
                + machineName,
                e);
        }
    }

    /**
     * Checks in all virtual machines in the hypervisor to check if the bridge name is in used. If
     * is in used the bridge name tagged with the VLAN tag cann't be deleted
     * 
     * @param bridgeName the bridge name
     * @return true if the bridge name can be deleted, false if not.
     */
    private boolean mustDeleteVLAN(String bridgeName)
    {
        List<IMachine> machines = vBoxHyper.getVirtualBox().getMachines();
        machines.remove(machine);
        long networkAdapterCount =
            vBoxHyper.getVirtualBox().getSystemProperties().getNetworkAdapterCount();
        for (IMachine iMachine : machines)
        {
            for (long i = 0; i < networkAdapterCount - 1; i++)
            {
                INetworkAdapter networkAdapter = iMachine.getNetworkAdapter(i);
                if (bridgeName.equals(networkAdapter.getHostInterface()))
                {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Private helper to attach the network adapter with the MAC object information.
     * 
     * @param targetMachine the target machine
     * @param macAddress the mac address
     * @param bridgeName the bridge name
     * @param slot
     */
    private void attachNetworkAdapter(IMachine targetMachine, String macAddress, String bridgeName,
        int slot)
    {
        // Attaching the network adapter
        INetworkAdapter networkAdapter = machine.getNetworkAdapter(new Long(slot));
        networkAdapter.setAdapterType(NetworkAdapterType.I82543GC);
        networkAdapter.setEnabled(true);
        networkAdapter.attachToBridgedInterface();
        networkAdapter.setHostInterface(bridgeName);
        // networkAdapter.attachToBridgedInterface();
        networkAdapter.setMACAddress(macAddress);

    }

    /**
     * Private helper to attach the virtual extended disks from a virtual machine configuration.
     * 
     * @param list the virtual machine configuration
     * @param vbox the vbox
     * @param machine the machine to attache the disks
     * @throws VirtualMachineException
     */
    private void attachExtendedDisks(List<VirtualDisk> list, IVirtualBox vbox, IMachine machine)
        throws VirtualMachineException
    {
        if (!list.isEmpty())
        {
            for (VirtualDisk vdisk : list)
            {
                // TODO Attaching other STANDARD extended disks
                if (vdisk.getDiskType().compareTo(VirtualDiskType.ISCSI) == 0)
                {
                    if (lastControllerPortUsed <= maxDevicesPerPortCount)
                    {
                        attachIscsiDisk(vdisk, vbox, machine, sataStorageControllerName);
                    }
                    else
                    {
                        throw new VirtualMachineException("The maximum devices to attach was reached. Impossible to add more extended disks");
                    }
                }
            }
        }

    }

    /**
     * Private helper to attach an ISCSI disk to a virtual machine.
     * 
     * @param vdisk the virtual disk to attach
     * @param vbox the {@link IVirtualBox} object used to create the hard disk
     * @param machine the machine to attach the hard disk
     * @param controllerName the controller name
     */
    private void attachIscsiDisk(VirtualDisk vdisk, IVirtualBox vbox, IMachine machine,
        String controllerName)
    {
        String location = vdisk.getLocation();
        IMedium iscsidiskVDI = vbox.createHardDisk("iSCSI", location);
        int index = location.indexOf("|");
        String ip = location.substring(0, index);
        String iscsiPath = location.substring(index + 1);
        String iqn = AddressingUtils.getIQN(iscsiPath);
        String lunId = AddressingUtils.getLUN(iscsiPath);
        iscsidiskVDI.setProperty("InitiatorName", "iqn.2008-04.com.sun.virtualbox.initiator");
        iscsidiskVDI.setProperty("TargetAddress", ip);
        iscsidiskVDI.setProperty("TargetName", iqn);
        iscsidiskVDI.setProperty("LUN", lunId);
        machine.attachDevice(controllerName, lastControllerPortUsed, 0, DeviceType.HardDisk,
            iscsidiskVDI);
        lastControllerPortUsed++;
    }

    /**
     * Mounts the repository, attachment throw API.
     * 
     * @throws Exception the exception
     */
    protected void cloneVirtualDisk() throws Exception
    {
        VirtualDisk diskBase = config.getVirtualDiskBase();

        String repository = extractRepository(diskBase.getLocation());

        if (repository == null)
        {
            throw new Exception("Not valid repository " + diskBase.getLocation());
        }

        String datastorePath = getDatastorePathFromRepository(repository);

        if (datastorePath == null)
        {
            throw new Exception("Not valid datastore path " + diskBase.getLocation());
        }

        String imagePath = diskBase.getImagePath();
        String sourcePath = datastorePath + imagePath;
        String destinationRepository = getDatastore(diskBase);

        logger.info("Assigning the virtual disk [{}] from repository[{}]", sourcePath,
            destinationRepository);

        String clonedImagePath = destinationRepository + machineName;

        if (config.getVirtualDiskBase().isHa())
        {
            newVDI =
                vBoxHyper.getVirtualBox().openMedium(clonedImagePath, DeviceType.HardDisk,
                    AccessMode.ReadWrite);
            
            // newVDI = vBoxHyper.getVirtualBox().createHardDisk(diskVDI.getFormat(),
            // clonedImagePath);
        }
        else
        {
            cloneThroughAPI(sourcePath, clonedImagePath);
        }

        logger.debug("Image cloned at [{}]", clonedImagePath);
    }

    private String getDatastorePathFromRepository(final String repository) throws RimpException,
        TException
    {
        String aimLocation = vBoxHyper.getAddress().getHost();
        int aimPort = vBoxHyper.getAddress().getPort();

        Iface aim = TTransportProxy.getInstance(aimLocation, aimPort);
        List<Datastore> datastores = aim.getDatastores();
        String path = null;

        for (Datastore datastore : datastores)
        {
            if (datastore.device.contains(repository))
            {
                path = datastore.path;
            }
        }

        if (path == null)
        {
            return null;
        }
        return path.endsWith("/") ? path : path + "/";
    }

    private String extractRepository(final String input)
    {
        Pattern pattern = Pattern.compile("\\[(.*?)\\]");
        Matcher matcher = pattern.matcher(input);
        String repo = null;

        if (matcher.find())
        {
            repo = new String(matcher.group(1));
        }

        if (repo == null)
        {
            return null;
        }

        if (repo.endsWith("/"))
        {
            repo = repo.substring(0, repo.length() - 1);
        }

        return repo.equalsIgnoreCase("null") ? null : repo;
    }

    private String getDatastore(final VirtualDisk disk)
    {
        String datastore = disk.getTargetDatastore();
        if (!datastore.endsWith("/"))
        {
            datastore += "/";
        }
        return datastore;
    }

    /**
     * Clones the disk though virtual box api calls.
     * 
     * @param imageRemotePath the image remote path
     * @param clonedImagePath the cloned image path
     * @throws VirtualMachineException the virtual machine exception
     */
    public void cloneThroughAPI(String imageRemotePath, String clonedImagePath)
        throws VirtualMachineException
    {
        IMedium diskVDI = null;

        IVirtualBox vbox = vBoxHyper.getVirtualBox();

        logger.debug(
            "Cloning image through API using imageRepositoryPath[{}] and clonedImagePath[{}]",
            imageRemotePath, clonedImagePath);

        try
        {
            // Maybe to change the UUID parent is not necessary
            diskVDI = vbox.openMedium(imageRemotePath, DeviceType.HardDisk, AccessMode.ReadWrite);

        }
        catch (javax.xml.ws.WebServiceException wse)
        {
            String msg =
                "Can not open the HardDisk on [{}] (now try to find on media registry) caused by [{}]";
            logger.warn(msg, imageRemotePath, wse);

            try
            {
                diskVDI = vbox.findMedium(imageRemotePath, DeviceType.HardDisk);
            }
            catch (javax.xml.ws.WebServiceException wse2)
            {
                String msg2 =
                    "Can not open of find on media registry the HardDisk [" + imageRemotePath + "]";
                throw new VirtualMachineException(msg2, wse2);
            }
        }

        newVDI = vBoxHyper.getVirtualBox().createHardDisk(diskVDI.getFormat(), clonedImagePath);
        IProgress progress = diskVDI.cloneTo(newVDI, diskVDI.getVariant(), null);

        waitOperation(progress, 15 * 60000); // 15 minutes

        diskVDI.close();

        logger.info("Cloning success, at [{}] ", clonedImagePath);
    }

    /**
     * Opens a remote session.
     * 
     * @throws VirtualMachineException
     */
    private void openRemoteSession() throws VirtualMachineException
    {

        String sessionType = "headless";
        String env = "DISPLAY=:0.0";

        logger.info("Opening the remote session");

        // openExistingSession();
        ISession oSession = vBoxHyper.getSession();

        IProgress oProgress = machine.launchVMProcess(oSession, sessionType, env);
        logger.info("Session for VM " + config.getMachineId().toString() + " is opened...");

        waitOperation(oProgress, 10000); // 10 seconds

        // This hacks are necesarry since state synchronization does not work
        // well in Vbox
        try
        {
            Thread.sleep(5000);
        }
        catch (InterruptedException e)
        {
            logger.error("An error waiting the session to be closed was occurred: "
                + e.getMessage());
        }

        oSession.unlockMachine();
    }

    /**
     * Starts the virtual machine execution.
     */
    @Override
    public void powerOnMachine() throws VirtualMachineException
    {
        if (!checkState(State.POWER_UP))
        {
            vBoxHyper.reconnect();
            // openRemoteSession();
            ISession oSession = vBoxHyper.getSession();
            machine.lockMachine(oSession, LockType.Write);
            IMachine machine = vBoxHyper.getConsole().getMachine();
            IVRDEServer vrdpserver = machine.getVRDEServer();

            if (AddressingUtils.isValidPort(String.valueOf(rdpPort)))
            {
                vrdpserver.setEnabled(true);
                logger.debug("Activating the VRDP port: " + rdpPort);
                vrdpserver.setVRDEProperty("TCP/Ports", String.valueOf(rdpPort));
            }
            else
            {
                vrdpserver.setEnabled(false);
            }

            machine.saveSettings();
            oSession.unlockMachine();
            openRemoteSession();
        }
    }

    /**
     * Stops the virtual machine execution.
     */
    @Override
    public void powerOffMachine() throws VirtualMachineException
    {
        if (!checkState(State.POWER_OFF))
        {
            vBoxHyper.reconnect();
            machine.lockMachine(vBoxHyper.getSession(), LockType.Shared);

            IProgress oProgress = vBoxHyper.getConsole().powerDown();

            waitOperation(oProgress, 10000);

            if (vBoxHyper.getSession().getState() == SessionState.Locked)
            {
                vBoxHyper.getSession().unlockMachine();
            }
        }
    }

    /**
     * Check every 5 seconds if the operation ended.
     */
    private void waitOperation(IProgress progress, long totalms) throws VirtualMachineException
    {
        for (long current = 0; current < totalms; current = current + 1000)
        {
            try
            {
                progress.waitForCompletion(500);

                if (progress.getCompleted())
                {
                    if (progress.getResultCode() != 0)
                    {
                        throw new VirtualMachineException(progress.getErrorInfo().getText());
                    }

                    return;
                }
            }
            catch (Exception e)
            {
                // timeout
                e.printStackTrace();
            }

            try
            {
                Thread.sleep(500);
            }
            catch (InterruptedException e)
            {
                throw new VirtualMachineException(e);
            }
            logger.debug("Vbox op %s at %d", progress.getOperationDescription(),
                progress.getOperationPercent());
        }

        throw new VirtualMachineException(String.format("Timeout [%s] it waits %d seconds",
            progress.getDescription(), totalms / 1000));

    }

    /**
     * Pauses the virtual machine execution.
     */
    @Override
    public void pauseMachine() throws VirtualMachineException
    {
        if (!checkState(State.PAUSE))
        {
            vBoxHyper.reconnect();
            machine.lockMachine(vBoxHyper.getSession(), LockType.Shared);
            vBoxHyper.getSession().getConsole().pause();
            vBoxHyper.getSession().unlockMachine();
        }
    }

    /**
     * Resumes the virtual machine execution.
     */
    @Override
    public void resumeMachine() throws VirtualMachineException
    {
        if (!checkState(State.RESUME))
        {
            // openSession().resume();
            vBoxHyper.reconnect();
            machine.lockMachine(vBoxHyper.getSession(), LockType.Shared);
            vBoxHyper.getConsole().resume();
            vBoxHyper.getSession().unlockMachine();
        }
    }

    /**
     * Resets the virtual machine.
     */
    @Override
    public void resetMachine() throws VirtualMachineException
    {
        if (!checkState(State.POWER_UP))
        {
            vBoxHyper.reconnect();
            machine.lockMachine(vBoxHyper.getSession(), LockType.Shared);
            vBoxHyper.getConsole().reset();
            vBoxHyper.getSession().unlockMachine();
        }
    }

    /*
     * (non-Javadoc)
     * @see com.abiquo.abicloud.model.AbsVirtualMachine#deleteMachine()
     */
    @Override
    public void deleteMachine() throws VirtualMachineException
    {
        vBoxHyper.reconnect();

        try
        {
            detachBridgeInterfaces(vnicList);
        }
        catch (Exception e1)
        {
            logger.error("An error was occurred then deconfiguring the networking resources: {}",
                e1);
        }
        // Getting the virtualBox hypervisor
        IVirtualBox vbox = vBoxHyper.getVirtualBox();
        // Detaching Hard disk
        IMachine machine;
        // Getting the session
        ISession oSession = vBoxHyper.getSession();
        try
        {
            machine = vbox.findMachine(machineName);
        }
        catch (Exception e1)
        {
            machine = vbox.findMachine(config.getMachineId().toString());
        }
        // TODO Waits for a seconds to be sure that the session is in the right
        // state
        try
        {
            Thread.sleep(3000);
        }
        catch (InterruptedException e)
        {
            logger.error("An error waiting the session to be closed was occurred: "
                + e.getMessage());
        }
        // machine.lockMachine(oSession, LockType.Write);
        // // Detaching hard disk
        // machine = oSession.getMachine();
        // detachDisks(machine);
        // // Detaching extended disks
        // detachExtendedDisks(machine);
        // machine.saveSettings();
        // oSession.unlockMachine();
        //
        // if (config.getVirtualDiskBase().getDiskType() ==
        // VirtualDiskType.STANDARD)
        // {
        // // Deleting from the rimp
        // // removeImage();
        // }
        //

        // Deregistering machine
        List<IMedium> mediumsToDelete =
            machine.unregister(CleanupMode.DetachAllReturnHardDisksOnly);

        // Deleting mediums
        if (!config.getVirtualDiskBase().isHa())
        {
            IProgress oProgress = machine.delete(mediumsToDelete);

            try
            {
                waitOperation(oProgress, OPERATION_TIMEOUT);
            }
            catch (VirtualMachineException e)
            {
                logger
                    .error("An error was found when deleting the hard disk from the virtual machine"
                        + oProgress.getErrorInfo());
            }
        }

        // Closing the sessions
        vBoxHyper.logout();
    }

    @Override
    public State getStateInHypervisor()
    {
        try
        {
            State state = null;
            // Getting the virtualBox hypervisor
            vBoxHyper.reconnect();
            if (!isVMAlreadyCreated())
            {
                return State.NOT_DEPLOYED;
            }
            // Getting the virtualBox hypervisor
            IVirtualBox vbox = vBoxHyper.getVirtualBox();
            try
            {
                machine = vbox.findMachine(machineName);
            }
            catch (Exception e1)
            {
                machine = vbox.findMachine(config.getMachineId().toString());
            }
            MachineState actualState = machine.getState();
            switch (actualState)
            {
                case Aborted:
                    state = State.POWER_OFF;
                    break;
                case Paused:
                    state = State.PAUSE;
                    break;
                case Running:
                    state = State.POWER_UP;
                    break;
                case PoweredOff:
                    state = State.POWER_OFF;
                    break;
                case Restoring:
                    state = State.RESUME;
                    break;
                case Starting:
                    state = State.POWER_UP;
                    break;
                case Stopping:
                    state = State.POWER_OFF;
                    break;
                default:
                    state = State.POWER_OFF;
                    break;
            }
            return state;
        }
        catch (VirtualMachineException e)
        {
            logger.error("An error was occurred when getting the virtual machine state", e);
            return State.UNKNOWN;
        }
    }

    /*
     * (non-Javadoc)
     * @seecom.abiquo.abicloud.model.AbsVirtualMachine#reconfigVM(com.abiquo. abicloud.model.config.
     * VirtualMachineConfiguration)
     */
    @Override
    public void reconfigVM(VirtualMachineConfiguration newConfiguration)
        throws VirtualMachineException
    {
        vBoxHyper.reconnect();
        IVirtualBox vbox = vBoxHyper.getVirtualBox();
        ISession oSession = vBoxHyper.getSession();
        if (oSession.getState().compareTo(SessionState.Unlocked) == 0)
        {
            machine.lockMachine(oSession, LockType.Write);
            machine = oSession.getMachine();
            IMachine machine = vBoxHyper.getConsole().getMachine();
            // Setting the new Ram value
            logger.info("Reconfiguring The Virtual Machine For Memory Update " + this.machineId);
            if (newConfiguration.isRam_set())
            {
                machine.setMemorySize(newConfiguration.getMemoryRAM() / (1024 * 1024));
            }
            logger.info("Reconfiguring The Virtual Machine For CPU Update " + this.machineId);
            // Setting the number cpu value
            if (newConfiguration.isCpu_number_set())
            {
                machine.setCPUCount(new Long(newConfiguration.getCpuNumber()));
            }
            // Closing the sessions
            if (machine != null)
            {
                machine.releaseRemote();
            }
            if (oSession != null)
            {
                oSession.releaseRemote();
                oSession.unlockMachine();
            }
            // reconfigDisks(newConfiguration, config);
        }
        else
        {
            logger
                .warn("The reconfiguration could not be done since the virtual machine must be powered off");
        }
    }

    @Override
    public boolean isVMAlreadyCreated() throws VirtualMachineException
    {
        // Getting the session
        vBoxHyper.reconnect();
        // Getting the virtualBox hypervisor
        IVirtualBox vbox = vBoxHyper.getVirtualBox();
        try
        {
            vbox.findMachine(machineName);
        }
        catch (Exception e1)
        {
            try
            {
                vbox.findMachine(config.getMachineId().toString());
            }
            catch (Exception e)
            {
                logger.debug("The Virtual machine: {} does not exist", config.getMachineId()
                    .toString());
                return false;
            }
        }
        return true;
    }

    /**
     * Private helper to check the real state of the virtual machine.
     * 
     * @param stateToCheck the state to check
     * @return true if the state in the hypervisors equals to the state as parameter, false if
     *         contrary
     */
    private boolean checkState(State stateToCheck) throws VirtualMachineException
    {
        State actualState = getStateInHypervisor();
        if (actualState.compareTo(stateToCheck) == 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public void bundleVirtualMachine(String sourcePath, String destinationPath,
        String snapshotName, boolean isManaged) throws VirtualMachineException
    {

        logger.debug("Creating instance of image [{}] in clonedImagePath[{}]", sourcePath,
            destinationPath);
        logger.info("Creating an instance of the virtual machine: {}", config.getMachineName());
        try
        {
            String hypervisorLocation = vBoxHyper.getAddress().getHost();
            String sourceFolder = null;
            String imagePath;

            if (isManaged)
            {
                sourceFolder = getDatastore(config.getVirtualDiskBase());
                imagePath = sourceFolder;
            }
            else
            {
                imagePath = config.getVirtualDiskBase().getImagePath();
                int indexEndImagePath = imagePath.lastIndexOf('/');
                imagePath = imagePath.substring(0, indexEndImagePath);
                sourceFolder = imagePath;

            }

            Iface aimclient =
                TTransportProxy.getInstance(hypervisorLocation, vBoxHyper.getAddress().getPort());
            aimclient.copyFromDatastoreToRepository(machineName, snapshotName, destinationPath,
                sourceFolder);

            logger.info("Creating an instance of the virtual machine: {} DONE",
                config.getMachineName());
        }
        catch (Exception e)
        {
            String errorMessage = "Failed to bundle the virtual machine: {}, :{}";
            logger.error(errorMessage, machineId, e);
            throw new VirtualMachineException("Fail to bundle the virtual machine", e);
        }

    }

}
