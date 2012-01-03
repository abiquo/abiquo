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

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.nodecollector.constants.MessageValues;
import com.abiquo.nodecollector.domain.Collector;
import com.abiquo.nodecollector.domain.collectors.xenserver.SRType;
import com.abiquo.nodecollector.exception.CollectorException;
import com.abiquo.nodecollector.exception.ConnectionException;
import com.abiquo.nodecollector.exception.LoginException;
import com.abiquo.nodecollector.exception.NoManagedException;
import com.abiquo.nodecollector.utils.ResourceComparator;
import com.abiquo.server.core.infrastructure.nodecollector.HostDto;
import com.abiquo.server.core.infrastructure.nodecollector.HostStatusEnumType;
import com.abiquo.server.core.infrastructure.nodecollector.ResourceEnumType;
import com.abiquo.server.core.infrastructure.nodecollector.ResourceType;
import com.abiquo.server.core.infrastructure.nodecollector.VirtualDiskEnumType;
import com.abiquo.server.core.infrastructure.nodecollector.VirtualSystemCollectionDto;
import com.abiquo.server.core.infrastructure.nodecollector.VirtualSystemDto;
import com.abiquo.server.core.infrastructure.nodecollector.VirtualSystemStatusEnumType;
import com.xensource.xenapi.APIVersion;
import com.xensource.xenapi.Connection;
import com.xensource.xenapi.Host;
import com.xensource.xenapi.PBD;
import com.xensource.xenapi.PIF;
import com.xensource.xenapi.SR;
import com.xensource.xenapi.Session;
import com.xensource.xenapi.Types.SessionAuthenticationFailed;
import com.xensource.xenapi.Types.SessionInvalid;
import com.xensource.xenapi.Types.VbdType;
import com.xensource.xenapi.Types.VmPowerState;
import com.xensource.xenapi.VBD;
import com.xensource.xenapi.VDI;
import com.xensource.xenapi.VM;

/**
 * XenServer collector plugin.
 * 
 * @author destevez
 */
@Collector(type = HypervisorType.XENSERVER, order = 1)
public class XenServerCollector extends AbstractCollector
{
    /** The logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(XenServerCollector.class);

    /** XenServer connection. */
    private Connection connection;

    @Override
    public void connect(final String user, final String password) throws ConnectionException,
        LoginException
    {
        try
        {
            connection = new Connection(new URL("http://" + getIpAddress()));

            Session session =
                Session.loginWithPassword(connection, user, password, APIVersion.latest()
                    .toString());

            LOGGER.debug("Session started with UUID {}", session.getUuid(connection));
        }
        catch (SessionAuthenticationFailed e)
        {
            LOGGER.warn("Invalid credentials for hypervisor {} at cloud node ", this
                .getHypervisorType().name(), getIpAddress());
            throw new LoginException(MessageValues.LOG_EXCP, e);
        }
        catch (Exception e)
        {
            LOGGER.warn("Could not connect at hypervisor {} at cloud node {}", this
                .getHypervisorType().name(), getIpAddress());
            throw new ConnectionException(MessageValues.CONN_EXCP_I, e);
        }
    }

    @Override
    public void disconnect() throws CollectorException
    {
        LOGGER.info("Disconnecting...");

        try
        {
            if (connection.getSessionReference() != null)
            {
                Session.logout(connection);
                connection.dispose();
            }
        }
        catch (SessionInvalid e)
        {
            // Do nothing. It means the previous connection was non-authenticated.
            return;
        }
        catch (Exception e)
        {
            throw new CollectorException(MessageValues.CONN_EXCP_III, e);
        }
    }

    @Override
    public HostDto getHostInfo() throws CollectorException
    {
        LOGGER.info("Getting physical information in XenServer collector...");

        HostDto info = new HostDto();

        try
        {
            // Currently only one host is supported
            Map<Host, Host.Record> hosts = Host.getAllRecords(connection);
            Host host = hosts.keySet().iterator().next();
            Host.Record hostRecord = hosts.get(host);
            String repositoryLocation =
                System.getProperty("abiquo.appliancemanager.repositoryLocation");

            info.setCpu(Long.valueOf(hostRecord.hostCPUs.size()));
            info.setName(hostRecord.nameLabel);
            info.setRam(host.getMetrics(connection).getMemoryTotal(connection));
            info.setHypervisor(HypervisorType.XENSERVER.getValue());
            info.setVersion(StringUtils.join(new String[] {
            hostRecord.softwareVersion.get("product_brand"), " v",
            hostRecord.softwareVersion.get("product_version"), " build",
            hostRecord.softwareVersion.get("build_number")}));
            info.setInitiatorIQN(hostRecord.otherConfig.get("iscsi_iqn"));
            info.setHypervisor(HypervisorType.XENSERVER.getValue());
            info.getResources().addAll(getHostResources(hostRecord, repositoryLocation));

            logHostDetails(hostRecord);

            try
            {
                checkPhysicalState(hostRecord, repositoryLocation);
                info.setStatus(HostStatusEnumType.MANAGED);
            }
            catch (NoManagedException e)
            {
                info.setStatus(HostStatusEnumType.NOT_MANAGED);
                info.setStatusInfo(e.getMessage());
            }
        }
        catch (Exception e)
        {
            throw new CollectorException(MessageValues.COLL_EXCP_PH, e);
        }

        return info;
    }

    /**
     * Returns all the virtual systems except the one named <code>Domain-0</code>. That virtual
     * system is the hypervisor itself and must not be returned as a virtual system.
     * 
     * @return The list of virtual systems in the hypervisor.
     * @throws CollectorException if any problem occurs.
     */
    @Override
    public VirtualSystemCollectionDto getVirtualMachines() throws CollectorException
    {
        LOGGER.info("Getting virtual machine information in XenServer collector...");

        try
        {
            VirtualSystemCollectionDto virtualSystems = new VirtualSystemCollectionDto();
            Map<VM, VM.Record> retrievedVMs = VM.getAllRecords(getConnection());

            for (VM.Record vm : retrievedVMs.values())
            {
                if (!vm.isControlDomain && !vm.isATemplate)
                {
                    LOGGER.debug("Found virtual machine: {}. Getting info...", vm.nameLabel);

                    VirtualSystemDto virtualSystem = getVirtualMachineInfo(vm);
                    virtualSystems.getVirtualSystems().add(virtualSystem);
                }
            }

            return virtualSystems;
        }
        catch (Exception e)
        {
            throw new CollectorException(MessageValues.COLL_EXCP_VM, e);
        }
    }

    /**
     * Checks if the Physical machine is properly configured.
     * 
     * @param hostRecord The Host information.
     * @param repositoryLocation The location of the Appliance Library Storage Repository.
     * @throws NoManagedException If the physical machine is not properly configured.
     */
    private void checkPhysicalState(final Host.Record hostRecord, final String repositoryLocation)
        throws NoManagedException
    {
        LOGGER.debug("Checking Repository configuration...");

        try
        {
            // Find the Appliance Library Repository
            Map<PBD, PBD.Record> pbds = PBD.getAllRecords(connection);

            for (PBD.Record pbd : pbds.values())
            {
                SR.Record srRecord = pbd.SR.getRecord(connection);

                if (isApplianceLibraryRepository(srRecord, pbd, repositoryLocation))
                {
                    // Fail if it is not attached, success otherwise
                    if (!pbd.currentlyAttached)
                    {
                        LOGGER.debug(MessageValues.NOMAN_NFS_VI);
                        throw new NoManagedException(MessageValues.NOMAN_NFS_VI);
                    }

                    // Appliance Repository found; there is nothing else to check
                    break;
                }
            }

            // If no Appliance Repository is found, return a MANAGED state, since Virtual Factory
            // will create it when deploying a Virtual Appliance
        }
        catch (Exception e)
        {
            LOGGER.debug(MessageValues.NOMAN_NFS_VI);
            throw new NoManagedException(MessageValues.NOMAN_NFS_VI, e);
        }

        // Check if Linux Guest support package is installed
        LOGGER.debug("Checking Linux Guest support installation...");

        String linuxInstallStatus = hostRecord.softwareVersion.get("package-linux");
        String linuxDetails = hostRecord.softwareVersion.get("xs:linux");

        if (linuxInstallStatus == null || linuxDetails == null)
        {
            LOGGER.debug(MessageValues.NOMAN_XEN_SERVER_I);
            throw new NoManagedException(MessageValues.NOMAN_XEN_SERVER_I);
        }

        if (!linuxInstallStatus.equalsIgnoreCase("installed"))
        {
            LOGGER.debug(MessageValues.NOMAN_XEN_SERVER_I);
            throw new NoManagedException(MessageValues.NOMAN_XEN_SERVER_I);
        }
    }

    /**
     * Gets all resources from the specified host.
     * 
     * @param hostRecord The host.
     * @param repositoryLocation The location of the Appliance Library Storage Repository.
     * @return The list of the host resources.
     * @throws Exception If host resources cannot be retrieved.
     */
    protected List<ResourceType> getHostResources(final Host.Record hostRecord,
        final String repositoryLocation) throws Exception
    {
        List<ResourceType> resources = new ArrayList<ResourceType>();

        resources.addAll(getNetworkInterfaces(hostRecord));
        resources.addAll(getStorageRepositories(repositoryLocation));

        Collections.sort(resources, new ResourceComparator());

        return resources;
    }

    /**
     * Get information of all network interfaces in a given host.
     * 
     * @param host The target Host.
     * @return A list containing the information of all network interfaces.
     * @throws Exception If information of all network interfaces cannot be retrieved.
     */
    protected List<ResourceType> getNetworkInterfaces(final Host.Record host) throws Exception
    {
        List<ResourceType> resources = new ArrayList<ResourceType>();

        for (PIF pif : host.PIFs)
        {
            PIF.Record pifRecord = pif.getRecord(connection);

            if (pifRecord.VLAN == -1) // Ignore VLAN specific PIF
            {
                LOGGER.debug("Found Network Device {} - MAC {}", pifRecord.device, pifRecord.MAC);

                ResourceType resource = new ResourceType();
                resource.setResourceType(ResourceEnumType.NETWORK_INTERFACE);
                resource.setAddress(pifRecord.MAC);
                resource.setElementName(pifRecord.device);
                resources.add(resource);
            }
        }

        return resources;
    }

    /**
     * Gets information of Storage Repositories where Virtual Appliances will be deployed.
     * 
     * @param repositoryLocation The location of the Appliance Library Storage Repository.
     * @return A list containing all Storage Repositories where Virtual Appliances will be deployed.
     * @throws Exception If Storage Repository information cannot be retrieved.
     */
    protected List<ResourceType> getStorageRepositories(final String repositoryLocation)
        throws Exception
    {
        List<ResourceType> resources = new ArrayList<ResourceType>();

        Map<PBD, PBD.Record> pbds = PBD.getAllRecords(connection);

        for (PBD.Record pbd : pbds.values())
        {
            SR.Record srRecord = pbd.SR.getRecord(connection);
            SRType type = SRType.fromValue(srRecord.type);

            // Return only valid SRs and ignore Appliance Library repository
            if (type.isValidForDeployment()
                && !isApplianceLibraryRepository(srRecord, pbd, repositoryLocation))
            {
                LOGGER.debug("Found Storage Repository {}", srRecord.nameLabel);

                ResourceType resource = new ResourceType();
                resource.setResourceType(ResourceEnumType.HARD_DISK);
                resource.setResourceSubType(type.name());
                resource.setAddress(srRecord.uuid);
                resource.setElementName(srRecord.nameLabel);
                resource.setUnits(srRecord.physicalSize);
                resource.setAvailableUnits(srRecord.physicalSize - srRecord.physicalUtilisation);
                resources.add(resource);
            }

        }

        return resources;
    }

    /**
     * Get the information of the given Virtual Machine.
     * 
     * @param vm The Virtual Machine.
     * @return The Virtual Machine information.
     */
    protected VirtualSystemDto getVirtualMachineInfo(final VM.Record vm)
    {
        VirtualSystemDto vSystem = new VirtualSystemDto();

        vSystem.setName(vm.nameLabel);
        vSystem.setStatus(getState(vm.powerState));
        vSystem.setUuid(vm.uuid);
        vSystem.setCpu(vm.VCPUsAtStartup);
        vSystem.setRam(vm.memoryStaticMax);
        vSystem.setVport(0L); // Remote access is not supported by XenServer
        vSystem.getResources().addAll(getVirtualDisks(vm));

        return vSystem;
    }

    /**
     * Converts a {@link VmPowerState} to a {@link VirtualSystemStatusEnumType}.
     * 
     * @param state The <code>VmPowerState</code>.
     * @return The <code>VirtualSystemStatusEnumType</code>.
     */

    private VirtualSystemStatusEnumType getState(final VmPowerState state)
    {
        switch (state)
        {
            case HALTED:
                return VirtualSystemStatusEnumType.OFF;
            case RUNNING:
                return VirtualSystemStatusEnumType.ON;
            case SUSPENDED:
                return VirtualSystemStatusEnumType.OFF;
            case PAUSED:
                return VirtualSystemStatusEnumType.PAUSED;
            case UNRECOGNIZED:
            default:
                return VirtualSystemStatusEnumType.OFF;
        }
    }

    /**
     * Gets the virtual disks of the given Virtual Machine.
     * <p>
     * The way in which the virtual disk image is represented on physical storage depends on the
     * type of the Storage Repository in which the created VDI resides. For example, if the SR is of
     * type <code>lvm</code> the VIRTUAL disk image will be rendered as an LVM volume. if the SR is
     * of type <code>nfs</code> then the new disk image will be a sparse VHD file created on an NFS
     * filer.
     * <p>
     * The Storage Repository type can be retrieved API using the SR.get_type() API call.
     * 
     * @param vm The Virtual Machine.
     * @return A list with all the Virtual Disks.
     */
    private List<ResourceType> getVirtualDisks(final VM.Record vm)
    {
        List<ResourceType> diskList = new ArrayList<ResourceType>();

        for (VBD vbd : vm.VBDs)
        {
            try
            {
                if (vbd.getType(connection).equals(VbdType.DISK))
                {
                    VDI vdi = vbd.getVDI(connection);
                    VDI.Record vdiRecord = vdi.getRecord(connection);
                    ResourceType disk = new ResourceType();

                    disk.setUnits(vdiRecord.virtualSize);
                    disk.setAddress(vdiRecord.location);
                    disk.setResourceType(ResourceEnumType.HARD_DISK);
                    disk.setConnection(vdiRecord.SR.getUuid(connection));
                    disk.setElementName(vdiRecord.nameLabel);

                    // TODO: How to determine if it is sparse or flat ?
                    disk.setResourceSubType(VirtualDiskEnumType.VHD_SPARSE.value());

                    diskList.add(disk);
                }
            }
            catch (Exception e)
            {
                LOGGER.error(e.getMessage(), e);
            }
        }

        return diskList;
    }

    /**
     * Get the Repository Location of a {@link SRType#NFS} Storage Repository.
     * <p>
     * XenServer API does not provide a way to retrieve the device-config attibute of the SR, so the
     * repository location must be searched in another attribute.
     * <p>
     * By default, XenCenter and Abiquo store this information in the <b>description</b> field, so
     * we will assume there will be present the repository location.
     * 
     * @param sr The Storage Repository.
     * @param pbd The Physical Block Device associated with the Storage Repository.
     * @param repositoryLocation The location of the Appliance Library repository.
     * @return A boolean indicating if the SR corresponds to the Appliance Library repository.
     * @throws If Repository information cannot be retrieved.
     */
    private boolean isApplianceLibraryRepository(final SR.Record sr, final PBD.Record pbd,
        final String repositoryLocation) throws Exception
    {
        if (repositoryLocation == null)
        {
            return false;
        }

        // First of all check Storage Repository type
        SRType type = SRType.fromValue(sr.type);
        if (type != SRType.NFS)
        {
            return false;
        }

        // Check if NFS repository points to Appliance Library
        String pbdLocation =
            pbd.deviceConfig.get("server") + ":" + pbd.deviceConfig.get("serverpath");

        return removeTrailingSlash(pbdLocation).equalsIgnoreCase(
            removeTrailingSlash(repositoryLocation));
    }

    /**
     * Logs host details.
     * 
     * @param hostRecord The host information
     */
    private void logHostDetails(final Host.Record hostRecord)
    {
        // Do not perform XenServer API calls if debug is disabled
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("Host details for {}:", hostRecord.nameLabel);

            for (String key : hostRecord.softwareVersion.keySet())
            {
                String value = hostRecord.softwareVersion.get(key);
                LOGGER.debug("  {}: {}", key, value);
            }
        }
    }

    /**
     * Gets the connection.
     * 
     * @return the connection.
     */
    public Connection getConnection()
    {
        return connection;
    }

    /**
     * Sets the connection.
     * 
     * @param connection the connection to set.
     */
    public void setConnection(final Connection connection)
    {
        this.connection = connection;
    }

}
