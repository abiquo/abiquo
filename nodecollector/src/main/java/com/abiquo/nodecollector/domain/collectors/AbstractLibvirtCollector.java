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

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.io.FilenameUtils;
import org.libvirt.Domain;
import org.libvirt.DomainInfo;
import org.libvirt.LibvirtException;
import org.libvirt.NodeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.abiquo.nodecollector.aim.AimCollector;
import com.abiquo.nodecollector.constants.MessageValues;
import com.abiquo.nodecollector.domain.collectors.libvirt.LeaksFreeConnect;
import com.abiquo.nodecollector.exception.CollectorException;
import com.abiquo.nodecollector.exception.NoManagedException;
import com.abiquo.nodecollector.exception.libvirt.AimException;
import com.abiquo.nodecollector.utils.XPathUtils;
import com.abiquo.server.core.infrastructure.nodecollector.HostDto;
import com.abiquo.server.core.infrastructure.nodecollector.HostStatusEnumType;
import com.abiquo.server.core.infrastructure.nodecollector.ResourceEnumType;
import com.abiquo.server.core.infrastructure.nodecollector.ResourceType;
import com.abiquo.server.core.infrastructure.nodecollector.VirtualDiskEnumType;
import com.abiquo.server.core.infrastructure.nodecollector.VirtualSystemCollectionDto;
import com.abiquo.server.core.infrastructure.nodecollector.VirtualSystemDto;
import com.abiquo.server.core.infrastructure.nodecollector.VirtualSystemStatusEnumType;

/**
 * Abstract class which provides common information collection for Libvirt-supported Hypervisors (In
 * our case KVM and XEN).
 * 
 * @author jdevesa
 */
public abstract class AbstractLibvirtCollector extends AbstractCollector
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractLibvirtCollector.class);

    private LeaksFreeConnect connection;

    protected AimCollector aimcollector;

    // private LeaksFreeConnect login() throws CollectorException
    // {
    // try
    // {
    // return new LeaksFreeConnect(getConnectionURL());
    // }
    // catch (LibvirtException e)
    // {
    // throw new CollectorException(MessageValues.CONN_EXCP_I, e);
    // }
    // }
    //
    // private void logout(LeaksFreeConnect conn)
    // {
    // try
    // {
    // conn.close();
    // }
    // catch (LibvirtException e)
    // {
    // e.printStackTrace();
    // }
    // }

    @Override
    public HostDto getHostInfo() throws CollectorException
    {
        final int KBYTE = 1024;
        final HostDto hostInfo = new HostDto();

        // LeaksFreeConnect conn = login();

        try
        {
            final NodeInfo nodeInfo = connection.nodeInfo();
            hostInfo.setName(connection.getHostName());
            hostInfo.setCpu(Long.valueOf(nodeInfo.cores));
            hostInfo.setRam(nodeInfo.memory * KBYTE);
            hostInfo.setHypervisor(getHypervisorType().getValue());
            hostInfo.setVersion(String.valueOf(connection.getVersion()));

            List<ResourceType> datastores = aimcollector.getDatastores();

            hostInfo.getResources().addAll(aimcollector.getNetInterfaces());
            hostInfo.setInitiatorIQN(aimcollector.getInitiatorIQN());

            try
            {
                // [ABICLOUDPREMIUM-345] Node collector mustn't return datastores NFS equal to AM.
                // TODO Depends on [ABICLOUDPREMIUM-365]
                // datastores = removeRepositoryDatastore(datastores, repositoryLocation);

                hostInfo.getResources().addAll(datastores);

                checkPhysicalState();
                hostInfo.setStatus(HostStatusEnumType.MANAGED);
            }
            catch (NoManagedException e)
            {
                hostInfo.setStatus(HostStatusEnumType.NOT_MANAGED);
                hostInfo.setStatusInfo(e.getMessage());
            }

        }
        catch (LibvirtException e)
        {
            LOGGER.error("Unhandled exception :", e);
            throw new CollectorException(MessageValues.COLL_EXCP_PH, e);
        }
        catch (AimException e)
        {
            LOGGER.error("Unhandled exception :", e);
            throw new CollectorException(e.getMessage(), e);
        }
        // finally
        // {
        // logout(conn);
        // }

        return hostInfo;
    }

    @Override
    public VirtualSystemCollectionDto getVirtualMachines() throws CollectorException
    {
        final VirtualSystemCollectionDto vmc = new VirtualSystemCollectionDto();
        final List<Domain> listOfDomains = new ArrayList<Domain>();

        try
        {
            // Defined domains are the closed ones!
            for (String domainValue : connection.listDefinedDomains())
            {
                if (domainValue != null) // Why null domains are returned?
                {
                    listOfDomains.add(connection.domainLookupByName(domainValue));
                }
            }
            // Domains are the started ones
            for (int domainInt : connection.listDomains())
            {
                listOfDomains.add(connection.domainLookupByID(domainInt));
            }

            // Create the list of Virtual Systems from the recovered domains
            for (Domain domain : listOfDomains)
            {
                if (!isDomain0(domain))
                {
                    vmc.getVirtualSystems().add(createVirtualSystemFromDomain(domain));
                }
            }
        }
        catch (LibvirtException e1)
        {
            LOGGER.error("Unhandled exception :", e1);
            throw new CollectorException(e1.getMessage(), e1);
        }
        catch (XPathExpressionException e)
        {
            LOGGER.error("Unhandled exception :", e);
            throw new CollectorException(e.getMessage(), e);
        }

        return vmc;
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

    @Override
    public void disconnect() throws CollectorException
    {
        try
        {
            if (getConnection() != null)
            {
                getConnection().close();
            }
        }
        catch (LibvirtException e)
        {
            LOGGER.error("Unhandled exception when disconnect:", e);
            throw new CollectorException(MessageValues.CONN_EXCP_III, e);
        }
    }

    /**
     * @param domain a domain defined by libvirt.
     * @return a {@link VirtualSystem} object
     * @throws LibvirtException can be thrown when connecting to libvirt
     * @throws XPathExpressionException can be thrown parsing the libvirt response
     */
    private VirtualSystemDto createVirtualSystemFromDomain(final Domain domain)
        throws LibvirtException, XPathExpressionException
    {

        final int KBYTE = 1024;
        final DomainInfo domainInfo = domain.getInfo();
        final String domainXML = domain.getXMLDesc(0);

        final VirtualSystemDto vSys = new VirtualSystemDto();
        vSys.setRam(domainInfo.memory * KBYTE);
        vSys.setCpu(Long.valueOf(domainInfo.nrVirtCpu));
        vSys.setName(domain.getName());
        vSys.setUuid(domain.getUUIDString());

        // If autoport is enabled, set port to -1 to indicate that remote access will not be
        // available
        String autoport = XPathUtils.getValue("//graphics/@autoport", domainXML);
        if (autoport != null && autoport.equalsIgnoreCase("yes"))
        {
            vSys.setVport(-1L);
        }
        else
        {
            // Evaluate the libvirt XML desc of the domain to get the remote display port.
            String vPortString = XPathUtils.getValue("//graphics/@port", domainXML);
            if (vPortString == null || vPortString.isEmpty())
            {
                vSys.setVport(-1L);
            }
            else
            {
                Long vPort = Long.valueOf(vPortString);
                vSys.setVport(vPort <= 0 ? -1 : vPort);
            }
        }

        // Evaluate the libvirt XML desc of the domain to get the image files
        // using the XPath features

        // final List<String> imageValues = XPathUtils.getValues("//disk/source/@file",
        // domainXML);
        // for (String imageValue : imageValues)
        // {
        // vSys.getResources().add(createDiskFromImagePath(imageValue));
        // }

        NodeList systemdisk = XPathUtils.getNodes("//disk[target[@dev='hda']]/source", domainXML);
        if (systemdisk.item(0) != null)
        {
            Node image = systemdisk.item(0).getAttributes().getNamedItem("file");
            if (image != null)
            {
                vSys.getResources().add(createDiskFromImagePath(image.getNodeValue()));
            }
            else
            {
                image = systemdisk.item(0).getAttributes().getNamedItem("dev");
                vSys.getResources().add(createDiskFromVolumePath(image.getNodeValue()));
            }
        }

        // Homogenize the status
        switch (domainInfo.state)
        {
            case VIR_DOMAIN_RUNNING:
            case VIR_DOMAIN_BLOCKED:
                vSys.setStatus(VirtualSystemStatusEnumType.ON);
                break;

            case VIR_DOMAIN_PAUSED:
                vSys.setStatus(VirtualSystemStatusEnumType.PAUSED);
                break;

            default:
                vSys.setStatus(VirtualSystemStatusEnumType.OFF);
                break;
        }

        return vSys;
    }

    /**
     * Create a {@link Disk} objet from an image value.
     * 
     * @param imagePath image where the disk is stored
     * @return a Disk object filled with the information
     */
    private ResourceType createDiskFromImagePath(final String imagePath)
    {

        final ResourceType currentHardDisk = new ResourceType();
        currentHardDisk.setResourceType(ResourceEnumType.HARD_DISK);
        currentHardDisk.setAddress(imagePath);
        try
        {

            long diskSize = aimcollector.getDiskFileSize(imagePath);
            currentHardDisk.setUnits(diskSize);

        }
        catch (AimException e)
        {
            currentHardDisk.setUnits(0L);
        }
        currentHardDisk.setResourceSubType(VirtualDiskEnumType.UNKNOWN.value());
        currentHardDisk.setConnection(FilenameUtils.getFullPathNoEndSeparator(imagePath));
        return currentHardDisk;

    }

    /**
     * Create a {@link Disk} objet from an image value.
     * 
     * @param imagePath image where the disk is stored
     * @return a Disk object filled with the information
     */
    private ResourceType createDiskFromVolumePath(final String imagePath)
    {

        final ResourceType currentHardDisk = new ResourceType();
        currentHardDisk.setResourceType(ResourceEnumType.VOLUME_DISK);
        currentHardDisk.setAddress(""); // Datastore directory
        try
        {

            long diskSize = aimcollector.getDiskFileSize(imagePath);
            currentHardDisk.setUnits(diskSize);

        }
        catch (AimException e)
        {
            currentHardDisk.setUnits(0L);
        }
        currentHardDisk.setResourceSubType(VirtualDiskEnumType.STATEFUL.value());
        currentHardDisk.setConnection(""); // Datastore root path
        return currentHardDisk;

    }

    /**
     * Returns true if given domain is "Domain-0"
     * 
     * @param domain to evaluate
     * @return true if given domain is "Domain-0"
     * @throws LibvirtException exception
     */
    protected abstract boolean isDomain0(Domain domain) throws LibvirtException;

    public LeaksFreeConnect getConnection()
    {
        return connection;
    }

    public void setConnection(final LeaksFreeConnect conn)
    {
        this.connection = conn;
    }
}
