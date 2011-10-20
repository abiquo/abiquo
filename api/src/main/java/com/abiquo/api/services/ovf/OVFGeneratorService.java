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

package com.abiquo.api.services.ovf;

import java.math.BigInteger;
import java.net.URI;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.dmtf.schemas.ovf.envelope._1.AbicloudNetworkType;
import org.dmtf.schemas.ovf.envelope._1.AnnotationSectionType;
import org.dmtf.schemas.ovf.envelope._1.ContentType;
import org.dmtf.schemas.ovf.envelope._1.DHCPServiceType;
import org.dmtf.schemas.ovf.envelope._1.DiskSectionType;
import org.dmtf.schemas.ovf.envelope._1.EnvelopeType;
import org.dmtf.schemas.ovf.envelope._1.FileType;
import org.dmtf.schemas.ovf.envelope._1.IpPoolType;
import org.dmtf.schemas.ovf.envelope._1.NetworkConfigurationType;
import org.dmtf.schemas.ovf.envelope._1.NetworkSectionType;
import org.dmtf.schemas.ovf.envelope._1.NetworkSectionType.Network;
import org.dmtf.schemas.ovf.envelope._1.OrgNetworkType;
import org.dmtf.schemas.ovf.envelope._1.RASDType;
import org.dmtf.schemas.ovf.envelope._1.ReferencesType;
import org.dmtf.schemas.ovf.envelope._1.VSSDType;
import org.dmtf.schemas.ovf.envelope._1.VirtualDiskDescType;
import org.dmtf.schemas.ovf.envelope._1.VirtualHardwareSectionType;
import org.dmtf.schemas.ovf.envelope._1.VirtualSystemCollectionType;
import org.dmtf.schemas.ovf.envelope._1.VirtualSystemType;
import org.dmtf.schemas.wbem.wscim._1.cim_schema._2.cim_resourceallocationsettingdata.CIMResourceAllocationSettingDataType;
import org.dmtf.schemas.wbem.wscim._1.cim_schema._2.cim_resourceallocationsettingdata.Caption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.ovfmanager.cim.CIMResourceAllocationSettingDataUtils;
import com.abiquo.ovfmanager.cim.CIMTypesUtils;
import com.abiquo.ovfmanager.cim.CIMTypesUtils.CIMResourceTypeEnum;
import com.abiquo.ovfmanager.cim.CIMTypesUtils.ChangeableTypeEnum;
import com.abiquo.ovfmanager.cim.CIMVirtualSystemSettingDataUtils;
import com.abiquo.ovfmanager.ovf.OVFEnvelopeUtils;
import com.abiquo.ovfmanager.ovf.OVFReferenceUtils;
import com.abiquo.ovfmanager.ovf.exceptions.EmptyEnvelopeException;
import com.abiquo.ovfmanager.ovf.exceptions.IdAlreadyExistsException;
import com.abiquo.ovfmanager.ovf.exceptions.RequiredAttributeException;
import com.abiquo.ovfmanager.ovf.exceptions.SectionException;
import com.abiquo.ovfmanager.ovf.exceptions.SectionNotPresentException;
import com.abiquo.ovfmanager.ovf.section.DiskFormat;
import com.abiquo.ovfmanager.ovf.section.OVFAnnotationUtils;
import com.abiquo.ovfmanager.ovf.section.OVFDiskUtils;
import com.abiquo.ovfmanager.ovf.section.OVFNetworkUtils;
import com.abiquo.ovfmanager.ovf.section.OVFVirtualHadwareSectionUtils;
import com.abiquo.server.core.cloud.Hypervisor;
import com.abiquo.server.core.cloud.NodeVirtualImage;
import com.abiquo.server.core.cloud.NodeVirtualImageDAO;
import com.abiquo.server.core.cloud.VirtualMachineState;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualDatacenterRep;
import com.abiquo.server.core.cloud.VirtualImage;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.VirtualMachineRep;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.Datastore;
import com.abiquo.server.core.infrastructure.InfrastructureRep;
import com.abiquo.server.core.infrastructure.Machine;
import com.abiquo.server.core.infrastructure.Rack;
import com.abiquo.server.core.infrastructure.RemoteService;
import com.abiquo.server.core.infrastructure.management.Rasd;
import com.abiquo.server.core.infrastructure.management.RasdManagement;
import com.abiquo.server.core.infrastructure.network.IpPoolManagement;
import com.abiquo.server.core.infrastructure.network.VLANNetwork;

@Service
@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
public class OVFGeneratorService
{
    @Autowired
    VirtualDatacenterRep vdcRepo;

    @Autowired
    InfrastructureRep datacenterRepo;

    @Autowired
    VirtualMachineRep vmRepo;

    private final static Logger logger = LoggerFactory.getLogger(OVFGeneratorService.class);

    public final static QName machineStateQname = new QName("machineStateAction");

    public final static QName remoteDesktopQname = new QName("remoteDesktopPort");

    public final static QName ADMIN_USER_QNAME = new QName("adminUser");

    public final static QName ADMIN_USER_PASSWORD_QNAME = new QName("adminPassword");

    public final static QName DATASTORE_QNAME = new QName("targetDatastore");

    public final static QName HA_DISK = new QName("ha");

    // /////////// InfrastructureWS

    public EnvelopeType changeMachineState(final VirtualMachine virtualMachine,
        final String machineState) throws Exception
    {
        EnvelopeType envelope = null;

        try
        {
            // Creates an OVF envelope from the virtual machine parameters
            envelope = constructEnvelopeType(virtualMachine, machineState);
        }
        catch (RequiredAttributeException e)
        {
            // virtual system creation (require id)
        }
        catch (SectionException e)
        {
            // addSections into virtual system (just created)
        }
        catch (IdAlreadyExistsException e)
        {
            // addVirtual system (envelope just created)
        }

        return envelope;
    }

    /**
     * Private helper to construct an OVF envelope form the basic parameters on the virtual machine
     * 
     * @param virtualMachine
     * @param machineState
     * @return an OVFEnvelope with the information contained on the virtualMachine
     * @throws Exception, if the virtualMachine can not be represented as an OVF document.
     */
    public EnvelopeType constructEnvelopeType(final VirtualMachine virtualMachine,
        final String machineState) throws Exception
    {
        EnvelopeType envelope;

        envelope = new EnvelopeType();

        String instanceId = virtualMachine.getUuid();
        String machineName = virtualMachine.getName();

        VirtualImage virtualImage = virtualMachine.getVirtualImage();

        try
        {
            // The Id of the virtualSystem is used for machine name
            VirtualSystemType virtualSystem =
                OVFEnvelopeUtils.createVirtualSystem(instanceId, machineName, null);// TODO null

            DiskSectionType diskSectionEnvelope = createEnvelopeDisk(virtualImage);

            Datastore targetDatastore = virtualMachine.getDatastore();
            ReferencesType diskReferences =
                createDiskFileReferences(virtualImage, targetDatastore.getRootPath()
                    + targetDatastore.getDirectory());

            // Creating the Annotation Type (machine state)
            AnnotationSectionType annotationSection =
                createAnnotationMachineStateAndRDPPort(machineState,
                    String.valueOf(virtualMachine.getVdrpPort()));

            // creating Virtual hardware section (containing hypervisor information)
            VirtualHardwareSectionType hardwareSection = createVirtualHardware(virtualMachine);

            // Setting the RAM and CPU from machine
            hardwareSection.getItem().add(createCPU(virtualMachine));
            hardwareSection.getItem().add(createMemory(virtualMachine));

            // adding virtual system sections
            // OVFEnvelopeUtils.addSection(virtualSystem, diskSectionSystem);
            OVFEnvelopeUtils.addSection(virtualSystem, hardwareSection);
            OVFEnvelopeUtils.addSection(virtualSystem, annotationSection);

            // adding envelope sections
            OVFEnvelopeUtils.addSection(envelope, diskSectionEnvelope);
            envelope.setReferences(diskReferences);

            // Setting the virtual system as envelope content
            OVFEnvelopeUtils.addVirtualSystem(envelope, virtualSystem);
        }
        catch (Exception e) // RequiredAttributeException(vs creation) and SectionException
        {

            String msg =
                String
                    .format(
                        "The envelope can not be created for the virtual machine [%s] (%s) : \n cause by : %s",
                        instanceId, machineName, e.toString());

            logger.error(msg);

            throw new Exception(msg, e);
        }

        return envelope;
    }

    /**
     * Gets the target datastore fully qualified path for deploying purposes
     * 
     * @param datastore the datastore to deploy
     * @param hypervisorType the hypervisor type
     * @return
     */
    public String getDatastoreForDeployedByHypervisorType(final Datastore datastore,
        final String hypervisorType)
    {
        if (HypervisorType.HYPERV_301.getValue().equalsIgnoreCase(hypervisorType)
            || HypervisorType.KVM.getValue().equalsIgnoreCase(hypervisorType)
            || HypervisorType.XEN_3.getValue().equalsIgnoreCase(hypervisorType))
        {
            return datastore.getName() + datastore.getDirectory();
        }
        else
        {
            return datastore.getName();
        }
    }

    /**
     * Private helper to get the virtual machine state
     * 
     * @param virtualMachine
     * @return
     */
    public static String getActualState(final VirtualMachine virtualMachine)
    {
        return virtualMachine.getState().toOVF();
    }

    private static DiskSectionType createEnvelopeDisk(final VirtualImage image)
    {
        // from the image
        String diskfileId = image.getName() + "." + image.getId();
        String diskId = String.valueOf(image.getId());
        Long capacity = image.getHdRequiredInBytes();// TODO set capacity !!! (using fileId? )

        DiskFormat format = DiskFormat.fromValue(image.getDiskFormatType().uri);

        // Setting the virtual Disk package level element
        DiskSectionType diskSection = new DiskSectionType();
        VirtualDiskDescType virtualDescType =
            OVFDiskUtils.createDiskDescription(diskId, diskfileId, format, capacity, null, null,
                null);

        diskSection.getDisk().add(virtualDescType);

        return diskSection;
    }

    private static String codifyRepositoryAndPath(final String imagePath, final String repository)
    {

        String codify = imagePath;

        if (imagePath.indexOf("|") == -1)
        {
            // Not managed images don't have repository
            if (repository == null)
            {
                if (imagePath.indexOf("[") != -1)
                {
                    codify = imagePath;
                }
                else
                {
                    codify = "[null]" + imagePath;
                }
            }
            else
            {
                codify = '[' + repository + ']' + imagePath;
            }
        }

        logger.info("Using repository [{}] and imagepath [{}]", repository, imagePath);

        logger.info("Codify ''{}''", codify);

        return codify;
    }

    /**
     * Setting the virtualDisk File reference
     * 
     * @param targetDatastore the targetDatastore
     */
    private static ReferencesType createDiskFileReferences(final VirtualImage virtualImage,
        final String targetDatastore)
    {
        ReferencesType references;
        FileType fileDisk;

        String imageRepository = null;
        if (virtualImage.isManaged())
        {
            imageRepository = virtualImage.getRepository().getUrl();
        }

        // from the image
        String href = codifyRepositoryAndPath(virtualImage.getPathName(), imageRepository);

        String fileId = virtualImage.getName() + "." + virtualImage.getId();
        BigInteger fileSize = null;

        fileDisk = OVFReferenceUtils.createFileType(fileId, href, fileSize, null, null);

        insertTargetDataStore(fileDisk, targetDatastore);

        references = new ReferencesType();
        references.getFile().add(fileDisk);

        return references;
    }

    private static RASDType createMemory(final VirtualMachine virtualMachine)
    {
        String elementName = "RAM";
        String instanceID = "2";
        CIMResourceTypeEnum resourceType = CIMResourceTypeEnum.Memory;
        Long virtualQuantity = new Long(virtualMachine.getRam());

        RASDType raMem;
        CIMResourceAllocationSettingDataType rasd;

        try
        {
            rasd =
                CIMResourceAllocationSettingDataUtils.createResourceAllocationSettingData(
                    elementName, instanceID, resourceType);

            CIMResourceAllocationSettingDataUtils.setAllocationToRASD(rasd, virtualQuantity);
            raMem = CIMResourceAllocationSettingDataUtils.createRASDTypeFromCIMRASD(rasd);
        }
        catch (RequiredAttributeException e)
        {
            // can not happen
            raMem = null;
        }

        return raMem;
    }

    private static RASDType createCPU(final VirtualMachine virtualMachine)
    {
        String elementName = "CPU";
        String instanceID = "1";
        CIMResourceTypeEnum resourceType = CIMResourceTypeEnum.Processor;
        Long virtualQuantity = new Long(virtualMachine.getCpu());

        RASDType raCpu;
        CIMResourceAllocationSettingDataType rasd;

        try
        {
            rasd =
                CIMResourceAllocationSettingDataUtils.createResourceAllocationSettingData(
                    elementName, instanceID, resourceType);

            CIMResourceAllocationSettingDataUtils.setAllocationToRASD(rasd, virtualQuantity);

            raCpu = CIMResourceAllocationSettingDataUtils.createRASDTypeFromCIMRASD(rasd);
        }
        catch (RequiredAttributeException e)
        {
            // can not happen
            raCpu = null;
        }

        return raCpu;
    }

    private static VirtualHardwareSectionType createVirtualHardware(
        final VirtualMachine virtualMachine)
    {
        VirtualHardwareSectionType hardwareSection;
        VSSDType vssd;

        // from the hypervisor
        Hypervisor hypervisor = virtualMachine.getHypervisor();
        String hypervisorAddress =
            "http://" + hypervisor.getIp() + ":" + hypervisor.getPort() + "/";
        String vsystemType = hypervisor.getType().getValue();
        try
        {
            // Setting the virtual machine ID
            String instanceIdString = virtualMachine.getUuid();
            String elementName = "Hypervisor";

            String description = null;
            Long generation = null;
            String caption = null;
            ChangeableTypeEnum changeableType = null;

            vssd =
                CIMVirtualSystemSettingDataUtils.createVirtualSystemSettingData(elementName,
                    instanceIdString, description, generation, caption, changeableType);

            insertUserAndPassword(vssd, hypervisor.getUser(), hypervisor.getPassword());

            // Setting the hypervisor address as VirtualSystemIdentifier element
            String virtualSystemIdentifier = hypervisorAddress;

            // Setting the hypervisor type
            CIMVirtualSystemSettingDataUtils.setVirtualSystemToVSSettingData(vssd,
                virtualSystemIdentifier, vsystemType);

            // Creating the VirtualHardware element
            String info = null;
            String transport = null;
            hardwareSection =
                OVFVirtualHadwareSectionUtils.createVirtualHardwareSection(vssd, info, transport);

        }
        catch (RequiredAttributeException e)
        {
            // if the virtual machine do not have UUID
            hardwareSection = null;
        }

        return hardwareSection;
    }

    private static AnnotationSectionType createAnnotationMachineStateAndRDPPort(
        final String machineState, final String rdpPort)
    {

        // Creating the Annotation Type
        AnnotationSectionType annotationSection =
            OVFAnnotationUtils.createAnnotationSection("Abiquo extension to store machine state",
                "see OtherAttributes: " + machineStateQname.toString());
        annotationSection.getOtherAttributes().put(remoteDesktopQname, rdpPort);

        annotationSection.getOtherAttributes().put(machineStateQname, machineState);

        return annotationSection;
    }

    /**************************
     * VirtualApplianceWS
     *********/

    /**
     * Private helper to create a virtual appliance
     * 
     * @param virtualAppliance
     * @return
     * @throws Exception
     */
    public EnvelopeType createVirtualApplication(final VirtualAppliance virtualAppliance)
        throws Exception
    {
        return createVirtualApplication(virtualAppliance, false, false);
    }

    public EnvelopeType createVirtualApplication(final VirtualAppliance virtualAppliance,
        final boolean bundling, final boolean ha) throws Exception
    {
        // Create an OVF envelope
        EnvelopeType envelope = new EnvelopeType();

        // Using the name as the virtual System Id
        String vscId = String.valueOf(virtualAppliance.getId());
        VirtualSystemCollectionType virtualSystemCollection =
            OVFEnvelopeUtils.createVirtualSystemCollection(vscId, null, null); // TODO info and name

        // Creating the references element
        ReferencesType references = new ReferencesType();

        // Create NetworkSection and Network and add to Envelope
        NetworkSectionType netSection =
            OVFEnvelopeUtils.createSection(NetworkSectionType.class, null);
        Network net =
            OVFNetworkUtils.createNetwork(virtualAppliance.getName() + "_network",
                "Appliance Network identifier");
        OVFNetworkUtils.addNetwork(netSection, net);

        // set the network section on the envelope
        OVFEnvelopeUtils.addSection(envelope, netSection);

        // Add the custom network;
        addCustomNetwork(envelope, virtualAppliance);

        // Getting the all the virtual Machines
        for (NodeVirtualImage node : virtualAppliance.getNodes())
        {
            NodeVirtualImage nodeVirtualImage = node;

            VirtualMachineState vmState = nodeVirtualImage.getVirtualMachine().getState();

            // Creates the virtual system inside the virtual system collection
            VirtualSystemType virtualSystem =
                createVirtualSystem(nodeVirtualImage, virtualAppliance.getName());
            OVFEnvelopeUtils.addVirtualSystem(virtualSystemCollection, virtualSystem);

            // Setting the virtual Disk package level element to the envelope
            final String id =
                nodeVirtualImage.getId() == null ? "10" : nodeVirtualImage.getId().toString();

            OVFDiskUtils.addDisk(envelope,
                createDiskFromVirtualImage(id, nodeVirtualImage.getVirtualImage()));

            OVFReferenceUtils.addFileOrIgnore(references,
                createFileFromVirtualImage(nodeVirtualImage, bundling, ha));
        }

        // Adding the virtual System collection to the envelope
        OVFEnvelopeUtils.addVirtualSystem(envelope, virtualSystemCollection);

        // Adding the references to the envelope
        envelope.setReferences(references);

        return envelope;
    }

    /**
     * Helper method to createVirtualApplication - Adds the CustomNetwork to the EnvelopeType
     * 
     * @param envelope a reference to an EnvelopeType object to which the CustomNetwork section will
     *            be added
     * @param virtualAppliance a VirtualAppliance object which may or may not have a network
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    private void addCustomNetwork(final EnvelopeType envelope,
        final VirtualAppliance virtualAppliance) throws Exception
    {
        VirtualDatacenter vdc = virtualAppliance.getVirtualDatacenter();

        com.abiquo.server.core.infrastructure.network.Network network = vdc.getNetwork();
        AbicloudNetworkType networkToDeploy = new AbicloudNetworkType();

        Collection<VLANNetwork> listOfVLANidentifiers = new LinkedHashSet<VLANNetwork>();
        for (NodeVirtualImage node : virtualAppliance.getNodes())
        {
            VirtualMachine vm = node.getVirtualMachine();
            Collection<IpPoolManagement> ips = vdcRepo.findIpsByVirtualMachine(vm);

            for (IpPoolManagement ip : ips)
            {
                listOfVLANidentifiers.add(ip.getVlanNetwork());
            }
        }
        networkToDeploy.setUuid(network.getUuid());
        for (VLANNetwork vlan : listOfVLANidentifiers)
        {
            Integer numberOfRules = 0;
            Collection<IpPoolManagement> ips = vdcRepo.findIpsByVLAN(vlan.getId(), 0, -1);

            RemoteService dhcpRemoteService = vlan.getConfiguration().getDhcp().getRemoteService();
            URI uri = new URI(dhcpRemoteService.getUri());

            DHCPServiceType dhcp = new DHCPServiceType();
            dhcp.setDhcpAddress(uri.getHost());
            dhcp.setDhcpPort(uri.getPort());

            // Pass all the IpPoolManagement to IpPoolType if the virtual machine is assigned.
            for (IpPoolManagement ip : ips)
            {
                if (ip.getVirtualMachine() != null)
                {
                    IpPoolType rule = new IpPoolType();
                    rule.setConfigureGateway(ip.getConfigureGateway());
                    rule.setIp(ip.getIp());
                    rule.setMac(ip.getMac());
                    rule.setName(ip.getName());

                    dhcp.getStaticRules().add(rule);

                    numberOfRules++;
                }
            }

            if (numberOfRules > 0)
            {
                OrgNetworkType networkType = new OrgNetworkType();
                networkType.setNetworkName(vlan.getName());
                networkType.setVlanTag(vlan.getTag());

                NetworkConfigurationType config = new NetworkConfigurationType();
                config.setFenceMode(vlan.getConfiguration().getFenceMode());
                config.setGateway(vlan.getConfiguration().getGateway());
                config.setMask(vlan.getConfiguration().getMask());
                config.setNetmask(vlan.getConfiguration().getNetMask());
                config.setNetworkAddress(vlan.getConfiguration().getAddress());
                config.setPrimaryDNS(vlan.getConfiguration().getPrimaryDNS());
                config.setSecondaryDNS(vlan.getConfiguration().getSecondaryDNS());
                config.setSufixDNS(vlan.getConfiguration().getSufixDNS());

                config.setDhcpService(dhcp);

                networkType.setConfiguration(config);

                networkToDeploy.getNetworks().add(networkType);
            }
        }

        OVFEnvelopeUtils.addSection(envelope, networkToDeploy);
    }

    private FileType createFileFromVirtualImage(final NodeVirtualImage nodeVirtualImage,
        final boolean bundling, final boolean ha) throws RequiredAttributeException
    {
        String imagePath = null;

        VirtualImage virtualImage = nodeVirtualImage.getVirtualImage();
        String fileId = virtualImage.getName() + "." + nodeVirtualImage.getVirtualImage().getId();

        imagePath = virtualImage.getPathName();

        String imageRepository = null;
        if (virtualImage.isManaged())
        {
            imageRepository = virtualImage.getRepository().getUrl();
        }

        // if (!bundling)
        // {
        // VirtualImageConversions conversion =
        // nodeVirtualImage.getVirtualMachine().getConversion();
        //
        // if (conversion != null)
        // {
        // imagePath = conversion.getTargetPath();
        // }
        // }

        imagePath = processImagePath(nodeVirtualImage, imagePath);

        String absolutePath = codifyRepositoryAndPath(imagePath, imageRepository);

        BigInteger fileSize = null;

        FileType virtualDiskImageFile =
            OVFReferenceUtils.createFileType(fileId, absolutePath, fileSize, null, null);

        Datastore targetDatastore = nodeVirtualImage.getVirtualMachine().getDatastore();

        insertTargetDataStore(virtualDiskImageFile,
            targetDatastore.getRootPath() + targetDatastore.getDirectory());

        if (ha)
        {
            setHA(virtualDiskImageFile);
        }

        String path = null;
        if (!virtualImage.isManaged())
        {
            path = virtualImage.getNotManagedBundlePath();
            virtualImage.setPathName(path);
            virtualImage.setName(virtualImage.getNotManagedBundleName());
        }
        else
        {
            path = virtualImage.getPathName();
        }

        virtualDiskImageFile.getOtherAttributes().put(new QName("destinationPath"), path);
        virtualDiskImageFile.getOtherAttributes().put(new QName("isManaged"),
            String.valueOf(virtualImage.isManaged()));

        insertRepositoryManager(virtualDiskImageFile, nodeVirtualImage);

        return virtualDiskImageFile;
    }

    /**
     * Override to perform custom image path processing.
     * 
     * @param nodeVirtualImage The Virtual image being processed.
     * @param imagePath The computed path of the virtual image.
     * @throws RequiredAttributeException If an error occurs.
     */
    protected String processImagePath(final NodeVirtualImage nodeVirtualImage,
        final String imagePath) throws RequiredAttributeException
    {
        return imagePath;
    }

    /**
     * Private helper to insert in the FileType the dataStore attribute
     * 
     * @param virtualDiskImageFile the file type ovf section
     * @param dataStore the datastore attribute to insert
     */
    private static void insertTargetDataStore(final FileType virtualDiskImageFile,
        final String dataStore)
    {
        virtualDiskImageFile.getOtherAttributes().put(DATASTORE_QNAME, dataStore);
    }

    /**
     * In case of HA create/delete operation a new custom parameter is set on the Disk Element to
     * indicate do not execute any operation to copy/remove the disk from the target datastore.
     */
    private static void setHA(final FileType virtualDiskImageFile)
    {
        virtualDiskImageFile.getOtherAttributes().put(HA_DISK, Boolean.TRUE.toString());
    }

    private static VirtualDiskDescType createDiskFromVirtualImage(final String diskId,
        final VirtualImage virtualImage)
    {
        Long capacity = virtualImage.getHdRequiredInBytes();
        Long populate = virtualImage.getHdRequiredInBytes();

        DiskFormat format = null;

        format = DiskFormat.fromValue(virtualImage.getDiskFormatType().uri);

        // Adding the virtual Disks to the package level element
        VirtualDiskDescType virtualDescTypePackage =
            OVFDiskUtils.createDiskDescription("disk_" + diskId, virtualImage.getFileRef(), format,
                capacity, null, populate, null);

        return virtualDescTypePackage;
    }

    /**
     * Private helper to create a Virtual System
     * 
     * @param virtualMachine the virtual machine to create the virtual system from
     * @param virtualImage ther virtual image to create the virtual system from
     * @return
     * @throws Exception
     */
    public VirtualSystemType createVirtualSystem(final NodeVirtualImage nodeVirtualImage,
        final String virtualApplianceName) throws Exception
    {
        VirtualMachine virtualMachine = nodeVirtualImage.getVirtualMachine();
        VirtualImage virtualImage = nodeVirtualImage.getVirtualImage();

        // setting the network name the name of the virtualAppliance
        String networkName = virtualApplianceName + "_network";

        int rdPort = virtualMachine.getVdrpPort();

        // The Id of the virtualSystem is used for machine name
        String vsId = virtualMachine.getUuid();
        VirtualSystemType virtualSystem =
            OVFEnvelopeUtils.createVirtualSystem(vsId, virtualMachine.getName(), null);

        VirtualHardwareSectionType hardwareSection =
            createVirtualSystemSection(virtualMachine, virtualImage, networkName, nodeVirtualImage);

        // Configure AnnotationSection with the RD port
        AnnotationSectionType annotationSection =
            createVirtualSystemRDPortAnnotationSection(rdPort);

        // OVFEnvelopeUtils.addSection(virtualSystem, productSection);
        OVFEnvelopeUtils.addSection(virtualSystem, hardwareSection);
        OVFEnvelopeUtils.addSection(virtualSystem, annotationSection);

        return virtualSystem;
    }

    private static AnnotationSectionType createVirtualSystemRDPortAnnotationSection(final int rdPort)
    {

        AnnotationSectionType annotationSection;

        annotationSection = new AnnotationSectionType(); // TODO
        // OVFEnvelopeUtils.createSection(AnnotationSectionType.class,
        // null);

        Map<QName, String> otherAttributes = annotationSection.getOtherAttributes();

        otherAttributes.put(remoteDesktopQname, String.valueOf(rdPort));
        logger.debug("The remote desktop port included is: " + String.valueOf(rdPort));

        return annotationSection;
    }

    /**
     * hypervisor info
     * 
     * @throws RequiredAttributeException
     */
    private VirtualHardwareSectionType createVirtualSystemSection(
        final VirtualMachine virtualMachine, final VirtualImage virtualImage,
        final String networkName, final NodeVirtualImage node) throws RequiredAttributeException
    {
        Hypervisor hypervisor = virtualMachine.getHypervisor();
        String hypervisorAddres = "http://" + hypervisor.getIp() + ":" + hypervisor.getPort() + "/";
        String vsystemType = hypervisor.getType().getValue();
        String instanceID = virtualMachine.getUuid();

        // VIRTUAL SYSTEM SETTING DATA
        VSSDType vssd =
            CIMVirtualSystemSettingDataUtils.createVirtualSystemSettingData("Hypervisor",
                instanceID, null, null, null, null);

        insertUserAndPassword(vssd, hypervisor.getUser(), hypervisor.getPassword());

        // Setting the hypervisor address as VirtualSystemIdentifier element
        String virtualSystemIdentifier = hypervisorAddres;

        // Setting the hypervisor type
        CIMVirtualSystemSettingDataUtils.setVirtualSystemToVSSettingData(vssd,
            virtualSystemIdentifier, vsystemType);

        // Creating the VirtualHardware element
        VirtualHardwareSectionType hardwareSection =
            OVFVirtualHadwareSectionUtils.createVirtualHardwareSection(vssd, null, "transport");

        // Setting RAM
        CIMResourceAllocationSettingDataType cimRam =
            CIMResourceAllocationSettingDataUtils.createResourceAllocationSettingData("RAM", "2",
                CIMResourceTypeEnum.Memory);

        CIMResourceAllocationSettingDataUtils.setAllocationToRASD(cimRam,
            new Long(virtualMachine.getRam()));

        // Setting CPU
        CIMResourceAllocationSettingDataType cimCpu =
            CIMResourceAllocationSettingDataUtils.createResourceAllocationSettingData("CPU", "1",
                CIMResourceTypeEnum.Processor);

        CIMResourceAllocationSettingDataUtils.setAllocationToRASD(cimCpu,
            new Long(virtualMachine.getCpu()));

        String virtualImageId = node.getId() == null ? "10" : node.getId().toString();

        String diskId = "disk_" + virtualImageId;
        CIMResourceAllocationSettingDataType cimDisk =
            CIMResourceAllocationSettingDataUtils.createResourceAllocationSettingData("Harddisk"
                + virtualImageId + "'", virtualImageId, CIMResourceTypeEnum.Disk_Drive);
        CIMResourceAllocationSettingDataUtils.addHostResourceToRASD(cimDisk,
            OVFVirtualHadwareSectionUtils.OVF_DISK_URI + diskId);

        // All the above resource will be refactored to follow the CIM Resource Allocation model
        // So far we will receive iscsi resources - This information is now here
        // ResourceAllocationSettingData related to networking is taken care of in the for loop that
        // follows ...
        Collection<Rasd> rasdList =
            getResourceAllocationSettingDataList(virtualMachine, getPhysicalMachineIqn(node));

        for (Rasd rasd : rasdList)
        {
            RASDType rasdType = toCIM_RASDType(rasd);
            OVFVirtualHadwareSectionUtils.addRASD(hardwareSection, rasdType);
        }

        RASDType raCpu = CIMResourceAllocationSettingDataUtils.createRASDTypeFromCIMRASD(cimCpu);
        RASDType raRam = CIMResourceAllocationSettingDataUtils.createRASDTypeFromCIMRASD(cimRam);
        RASDType raDisk = CIMResourceAllocationSettingDataUtils.createRASDTypeFromCIMRASD(cimDisk);

        OVFVirtualHadwareSectionUtils.addRASD(hardwareSection, raCpu);
        OVFVirtualHadwareSectionUtils.addRASD(hardwareSection, raRam);
        OVFVirtualHadwareSectionUtils.addRASD(hardwareSection, raDisk);

        return hardwareSection;

    }// vhs

    private String getPhysicalMachineIqn(final NodeVirtualImage nvi)
    {
        // Get Virtual Datacenter
        Hypervisor hypervisor = nvi.getVirtualMachine().getHypervisor();
        Machine pm = hypervisor.getMachine();

        return pm.getInitiatorIQN();
    }

    /**
     * Adds user and password in the Virtual System Type
     * 
     * @param vssd the virtual system type
     * @param user the user
     * @param password the password
     */
    private static void insertUserAndPassword(final VSSDType vssd, final String user,
        final String password)
    {
        vssd.getOtherAttributes().put(ADMIN_USER_QNAME, user);
        vssd.getOtherAttributes().put(ADMIN_USER_PASSWORD_QNAME, password);

    }

    /**
     * Private helper to get the resources allocation setting data from the DB. This operation is
     * need since the virtual machine object got it doesn't store this information. When the pojos
     * for FLEX will be removed this operation won't be necessary
     * 
     * @param virtualMachine the virtual machine to get the RASD from
     * @return the resource allocation setting data list
     */
    protected Collection<Rasd> getResourceAllocationSettingDataList(
        final VirtualMachine virtualMachine, final String initiatorIqn)
        throws RequiredAttributeException
    {
        Collection<RasdManagement> management =
            vmRepo.findRasdManagementByVirtualMachine(virtualMachine);

        Collection<Rasd> rasd = new LinkedHashSet<Rasd>();
        if (management != null && !management.isEmpty())
        {
            for (RasdManagement r : management)
            {
                rasd.add(r.getRasd());
            }
        }

        return rasd;
    }

    /**
     * Private helper to assign the virtual system state with the present state of the virtual
     * machine
     * 
     * @param enveloe
     * @param virtualAppliance the virtual appliance where to get the states of the virtual machine
     * @return
     * @throws EmptyEnvelopeException
     * @throws SectionException
     * @throws SectionNotPresentException
     */
    public EnvelopeType changeVirtualMachineStates(final EnvelopeType envelope,
        final VirtualAppliance virtualAppliance) throws EmptyEnvelopeException, SectionException
    {
        ContentType entityInstance = OVFEnvelopeUtils.getTopLevelVirtualSystemContent(envelope);

        if (entityInstance instanceof VirtualSystemCollectionType)
        {
            VirtualSystemCollectionType virtualSystemCollectionType =
                (VirtualSystemCollectionType) entityInstance;

            for (ContentType subVirtualSystem : OVFEnvelopeUtils
                .getVirtualSystemsFromCollection(virtualSystemCollectionType))
            {
                String virtualSystemState = null;
                for (NodeVirtualImage node : virtualAppliance.getNodes())
                {
                    NodeVirtualImage nodeVi = node;
                    VirtualMachine vm = nodeVi.getVirtualMachine();
                    String uuid = vm.getUuid();
                    if (uuid.equals(subVirtualSystem.getId()))
                    {
                        virtualSystemState = vm.getState().toOVF();
                    }

                }
                AnnotationSectionType annotationSection =
                    OVFEnvelopeUtils.getSection(subVirtualSystem, AnnotationSectionType.class);

                Map<QName, String> attributes = annotationSection.getOtherAttributes();
                attributes.put(machineStateQname, virtualSystemState);
            }
        }
        return envelope;
    }

    /**
     * Private helper to change the state
     * 
     * @param enveloe
     * @return
     * @throws EmptyEnvelopeException
     * @throws SectionException
     * @throws SectionNotPresentException
     */
    public EnvelopeType changeStateVirtualMachine(final EnvelopeType envelope, final String newState)
        throws EmptyEnvelopeException, SectionException
    {
        ContentType entityInstance = OVFEnvelopeUtils.getTopLevelVirtualSystemContent(envelope);

        if (entityInstance instanceof VirtualSystemType)
        {
            // Getting state property
            AnnotationSectionType annotationSection =
                OVFEnvelopeUtils.getSection(entityInstance, AnnotationSectionType.class);

            Map<QName, String> attributes = annotationSection.getOtherAttributes();
            attributes.put(machineStateQname, newState);

        }
        else if (entityInstance instanceof VirtualSystemCollectionType)
        {
            VirtualSystemCollectionType virtualSystemCollectionType =
                (VirtualSystemCollectionType) entityInstance;

            for (ContentType subVirtualSystem : OVFEnvelopeUtils
                .getVirtualSystemsFromCollection(virtualSystemCollectionType))
            {
                AnnotationSectionType annotationSection =
                    OVFEnvelopeUtils.getSection(subVirtualSystem, AnnotationSectionType.class);

                Map<QName, String> attributes = annotationSection.getOtherAttributes();
                attributes.put(machineStateQname, newState);
            }
        }

        return envelope;
    }

    /**
     * Adds the Repository Manager to the FileType.
     * <p>
     * The Repository Manager is used by the XenServer plugin in the copy operation.
     * 
     * @param virtualDiskImageFile The FileType.
     * @param nvi The NodeVirtualImage.
     */
    private void insertRepositoryManager(final FileType virtualDiskImageFile,
        final NodeVirtualImage nvi)
    {
        // XenServer virtual factory plugin needs to know the RepositoryManager address
        String repositoryManagerAddress = getRepositoryManagerAddress(nvi);

        if (repositoryManagerAddress != null)
        {
            virtualDiskImageFile.getOtherAttributes().put(new QName("repositoryManager"),
                repositoryManagerAddress);
        }

    }

    private String getRepositoryManagerAddress(NodeVirtualImage nvi)
    {
        VirtualMachine vmachine = vmRepo.findVirtualMachineById(nvi.getVirtualMachine().getId());

        // Get Virtual Datacenter
        Hypervisor hypervisor = vmachine.getHypervisor();
        Machine pm = hypervisor.getMachine();

        Integer datacenterId = pm.getDatacenter().getId();

        Datacenter dc = datacenterRepo.findById(datacenterId);

        List<RemoteService> am =
            datacenterRepo.findRemoteServiceWithTypeInDatacenter(dc,
                RemoteServiceType.APPLIANCE_MANAGER);

        if (am == null || am.isEmpty())
        {
            logger.error("Could find the Remote Repository Remote Service");
            return null;
        }

        return am.iterator().next().getUri();
    }

    public static RASDType toCIM_RASDType(final Rasd rasdIn)
    {
        RASDType rasdOut = new RASDType();

        rasdOut.setAddress(CIMTypesUtils.createString(rasdIn.getAddress()));
        rasdOut.setAddressOnParent(CIMTypesUtils.createString(rasdIn.getAddressOnParent()));
        rasdOut.setAllocationUnits(CIMTypesUtils.createString(rasdIn.getAllocationUnits()));
        // TODO We convert a string to a list here! Keep in mind!
        rasdOut.getConnection().add(CIMTypesUtils.createString(rasdIn.getConnection()));
        rasdOut.setAutomaticAllocation(CIMTypesUtils.createBooleanFromInt(rasdIn
            .getAutomaticAllocation()));
        rasdOut.setAutomaticDeallocation(CIMTypesUtils.createBooleanFromInt(rasdIn
            .getAutomaticDeallocation()));
        rasdOut.setCaption((Caption) CIMTypesUtils.createString(rasdIn.getCaption()));

        // rasdOut.setChangeableType(CIMTypesUtils.createChangeableTypeFromInteger(rasdIn
        // .getChangeableType()));

        rasdOut.setConfigurationName(CIMTypesUtils.createString(rasdIn.getConfigurationName()));
        rasdOut.setConsumerVisibility(CIMTypesUtils.createConsumerVisibilityFromInteger(rasdIn
            .getConsumerVisibility()));
        rasdOut.setDescription(CIMTypesUtils.createString(rasdIn.getDescription()));
        rasdOut.setElementName(CIMTypesUtils.createString(rasdIn.getElementName()));
        rasdOut.setGeneration(CIMTypesUtils.createUnsignedLong(rasdIn.getGeneration()));
        rasdOut.setInstanceID(CIMTypesUtils.createString(rasdIn.getId()));
        rasdOut.setLimit(CIMTypesUtils.createUnsignedLong(rasdIn.getLimit()));
        rasdOut.setMappingBehavior(CIMTypesUtils.createMappingBehaviorFromInteger(rasdIn
            .getMappingBehaviour()));
        rasdOut.setOtherResourceType(CIMTypesUtils.createString(rasdIn.getOtherResourceType()));
        rasdOut.setParent(CIMTypesUtils.createString(rasdIn.getParent()));
        rasdOut.setPoolID(CIMTypesUtils.createString(rasdIn.getPoolId()));
        rasdOut.setReservation(CIMTypesUtils.createUnsignedLong(rasdIn.getReservation()));
        rasdOut.setResourceSubType(CIMTypesUtils.createString(rasdIn.getResourceSubType()));
        // TODO We convert a string to a list here! Keep in mind!
        rasdOut.getHostResource().add(CIMTypesUtils.createString(rasdIn.getHostResource()));
        rasdOut.setResourceType(CIMTypesUtils.createResourceTypeFromInteger(rasdIn
            .getResourceType()));
        rasdOut.setVirtualQuantity(CIMTypesUtils.createUnsignedLong(rasdIn.getVirtualQuantity()));
        rasdOut.setWeight(CIMTypesUtils.createUnsignedInt(rasdIn.getWeight()));

        return rasdOut;
    }
}
