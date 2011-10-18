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

package com.abiquo.nodecollector.domain.collectors;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.wink.common.internal.providers.entity.csv.CsvReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.virtualbox_4_0.DeviceType;
import org.virtualbox_4_0.IHost;
import org.virtualbox_4_0.IHostNetworkInterface;
import org.virtualbox_4_0.IMachine;
import org.virtualbox_4_0.IMedium;
import org.virtualbox_4_0.IMediumAttachment;
import org.virtualbox_4_0.IVirtualBox;
import org.virtualbox_4_0.MachineState;
import org.virtualbox_4_0.VirtualBoxManager;

import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.nodecollector.aim.AimCollector;
import com.abiquo.nodecollector.aim.impl.AimCollectorImpl;
import com.abiquo.nodecollector.constants.MessageValues;
import com.abiquo.nodecollector.domain.Collector;
import com.abiquo.nodecollector.exception.CollectorException;
import com.abiquo.nodecollector.exception.ConnectionException;
import com.abiquo.nodecollector.exception.NoManagedException;
import com.abiquo.nodecollector.exception.libvirt.AimException;
import com.abiquo.nodecollector.utils.ResourceComparator;
import com.abiquo.server.core.infrastructure.nodecollector.HostDto;
import com.abiquo.server.core.infrastructure.nodecollector.HostStatusEnumType;
import com.abiquo.server.core.infrastructure.nodecollector.ResourceEnumType;
import com.abiquo.server.core.infrastructure.nodecollector.ResourceType;
import com.abiquo.server.core.infrastructure.nodecollector.VirtualDiskEnumType;
import com.abiquo.server.core.infrastructure.nodecollector.VirtualSystemCollectionDto;
import com.abiquo.server.core.infrastructure.nodecollector.VirtualSystemDto;
import com.abiquo.server.core.infrastructure.nodecollector.VirtualSystemStatusEnumType;

/**
 * Special connection for VirtualBox Hypervisor.
 * 
 * @author jdevesa@abiquo.com
 */
@Collector(type = HypervisorType.VBOX, order = 3)
public class VirtualBoxCollector extends AbstractCollector
{
    /** The vbox. */
    private transient IVirtualBox vbox;

    /** The logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(VirtualBoxCollector.class);

    /** The web session manager object. The root of the vBox Hypervisor WService session. */
    private transient VirtualBoxManager mgr;

    // private long defaultDatastoreSize = 1073741824000L;

    String wsmanuser = "wsman";

    String wsmanpassword = "secret";

    Integer vboxPort = 18083;

    /**
     * WsmanCollector
     */
    protected AimCollector aimcollector;

    @Override
    public void connect(final String user, final String password) throws ConnectionException
    {
        wsmanuser = user;
        wsmanpassword = password;

        try
        {
            mgr = VirtualBoxManager.createInstance(null);
            mgr.connect("http://" + this.getIpAddress() + ":" + vboxPort, user, password);
            vbox = mgr.getVBox();
        }
        catch (Exception e)
        {
            throw new ConnectionException(MessageValues.CONN_EXCP_I, e);
        }

        try
        {
            aimcollector = new AimCollectorImpl(getIpAddress(), getAimPort());
        }
        catch (AimException e)
        {
            throw new ConnectionException(MessageValues.CONN_EXCP_IV, e);
        }

    }

    @Override
    public HostDto getHostInfo() throws CollectorException
    {

        final Integer MEGABYTE = 1048576;
        try
        {
            final IHost host = vbox.getHost();

            final HostDto info = new HostDto();

            info.setName("default_vBox_name");
            info.setCpu(host.getProcessorOnlineCount());
            info.setRam(host.getMemorySize() * MEGABYTE);
            info.setInitiatorIQN("iqn.2008-04.com.sun.virtualbox.initiator");

            // Getting interface resources

            List<IHostNetworkInterface> ifaces = host.getNetworkInterfaces();
            List<ResourceType> ifacesResource = new ArrayList<ResourceType>();

            for (IHostNetworkInterface iHostNetworkInterface : ifaces)
            {
                ResourceType nicResource = new ResourceType();
                nicResource.setAddress(iHostNetworkInterface.getHardwareAddress());
                nicResource.setElementName(iHostNetworkInterface.getName());
                nicResource.setResourceType(ResourceEnumType.NETWORK_INTERFACE);
                ifacesResource.add(nicResource);
            }

            // String diskFolder = vbox.getSystemProperties().getDefaultHardDiskFolder();
            // ResourceType diskResource = new ResourceType();
            // diskResource.setAddress(diskFolder);
            // diskResource.setElementName(diskFolder);
            // diskResource.setResourceType(ResourceEnumType.STORAGE_DISK);
            // diskResource.setUnits(new Long(defaultDatastoreSize));
            // diskResource.setAvailableUnits(new Long(defaultDatastoreSize));

            List<ResourceType> datastores = aimcollector.getDatastores();

            try
            {
                // [ABICLOUDPREMIUM-345] Node collector mustn't return datastores NFS equal to AM.
                // TODO Depends on [ABICLOUDPREMIUM-365]
                // datastores = removeRepositoryDatastore(datastores, repositoryLocation);

                info.getResources().addAll(datastores);

                checkPhysicalState();
                info.setStatus(HostStatusEnumType.MANAGED);
            }
            catch (NoManagedException e)
            {
                info.setStatus(HostStatusEnumType.NOT_MANAGED);
                info.setStatusInfo(e.getMessage());
            }

            Collections.sort(ifacesResource, new ResourceComparator());
            info.getResources().addAll(ifacesResource);
            info.setHypervisor(getHypervisorType().getValue());
            info.setVersion(vbox.getVersion());
            info.setStatus(HostStatusEnumType.MANAGED);

            return info;
        }
        catch (Throwable e)
        {
            throw new CollectorException(MessageValues.COLL_VER_NS, e);
        }

    }

    @Override
    public VirtualSystemCollectionDto getVirtualMachines() throws CollectorException
    {

        final Integer MEGABYTE = 1048576;

        VirtualSystemCollectionDto machines = new VirtualSystemCollectionDto();

        for (IMachine machine : vbox.getMachines())
        {
            VirtualSystemDto nextVMachine = new VirtualSystemDto();
            nextVMachine.setName(machine.getName());
            nextVMachine.setVport(getFreePortFromPortsList(machine.getVRDEServer().getVRDEProperty(
                "TCP/Ports")));
            nextVMachine.setCpu(machine.getCPUCount());
            nextVMachine.setUuid(machine.getId());
            nextVMachine.setRam(machine.getMemorySize() * MEGABYTE);
            MachineState state = machine.getState();

            if (state.compareTo(MachineState.Running) == 0)
            {
                nextVMachine.setStatus(VirtualSystemStatusEnumType.ON);
            }
            else if (state.compareTo(MachineState.Paused) == 0)
            {
                nextVMachine.setStatus(VirtualSystemStatusEnumType.PAUSED);
            }
            else
            {
                nextVMachine.setStatus(VirtualSystemStatusEnumType.OFF);
            }

            List<IMediumAttachment> mediumAttachments = machine.getMediumAttachments();

            for (IMediumAttachment iMediumAttachment : mediumAttachments)
            {
                ResourceType newResource = new ResourceType();

                if (iMediumAttachment.getType().compareTo(DeviceType.HardDisk) == 0)
                {
                    IMedium medium = iMediumAttachment.getMedium();
                    if (medium.getDeviceType().compareTo(DeviceType.HardDisk) == 0)
                    {
                        String formatName = medium.getFormat();
                        newResource.setResourceType(ResourceEnumType.STORAGE_DISK);
                        if (!"iSCSI".equals(formatName))
                        {
                            newResource.setAddress(medium.getLocation());
                            newResource.setUnits(medium.getSize().longValue());
                            newResource.setConnection(getDatastoreFromFile(medium.getLocation()));
                            newResource
                                .setResourceSubType(mediumFormatToVirtualDiskEnum(formatName)
                                    .value());
                        }
                        else
                        {
                            newResource.setAddress("unknown");
                            newResource.setUnits(new Long(0));
                            newResource.setResourceSubType(VirtualDiskEnumType.STATEFUL.value());
                            newResource.setConnection("unknown");
                        }
                    }

                }
                nextVMachine.getResources().add(newResource);
            }

            machines.getVirtualSystems().add(nextVMachine);
        }

        return machines;
    }

    /**
     * Parses the fileName to get the datastore name.
     * 
     * @param fileName the file name to parse
     * @return the datastore directory
     */
    private String getDatastoreFromFile(final String fileName)
    {
        int count = StringUtils.countMatches(fileName, "/");
        if (count == 1)
        {
            return "/";
        }
        int indexEndDirectory = fileName.lastIndexOf('/');
        return fileName.substring(0, indexEndDirectory);
    }

    @Override
    public void disconnect() throws CollectorException
    {
        try
        {
            mgr.disconnect();
            mgr.cleanup();
        }
        catch (Exception e)
        {
            throw new CollectorException(MessageValues.CONN_EXCP_III, e);
        }
    }

    private int getFreePortFromPortsList(final String csvPorts)
    {
        List<Integer> portList = new ArrayList<Integer>();
        try
        {
            CsvReader reader = new CsvReader(new StringReader(csvPorts));
            String[] line = reader.readLine();
            if (line != null)
            {
                for (String port : line)
                {
                    Integer portInt = Integer.valueOf(port);
                    portList.add(portInt);
                }
            }
        }
        catch (NumberFormatException e)
        {
            LOGGER.debug("Ignoring not integer port");
        }

        if (portList.isEmpty())
        {
            return -1;
        }
        else
        {
            return portList.get(0);
        }
    }

    private VirtualDiskEnumType mediumFormatToVirtualDiskEnum(final String formatId)
    {
        if ("raw".equals(formatId))
        {
            return VirtualDiskEnumType.RAW;
        }
        else if ("VMDK".equals(formatId))
        {
            return VirtualDiskEnumType.VMDK_FLAT;
        }
        else if ("VDI".equals(formatId))
        {
            return VirtualDiskEnumType.VDI_FLAT;
        }
        else if ("VHD".equals(formatId))
        {
            return VirtualDiskEnumType.VHD_FLAT;
        }
        else
        {
            return VirtualDiskEnumType.INCOMPATIBLE;
        }
    }

    protected void checkPhysicalState() throws NoManagedException
    {
        try
        {
            aimcollector.checkAIM();
        }
        catch (AimException e)
        {
            throw new NoManagedException(e.getMessage(), e);
        }
    }

}
