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

package com.abiquo.abiserver.model.ovf;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.dmtf.schemas.ovf.envelope._1.AbicloudNetworkType;
import org.dmtf.schemas.ovf.envelope._1.AnnotationSectionType;
import org.dmtf.schemas.ovf.envelope._1.ContentType;
import org.dmtf.schemas.ovf.envelope._1.EnvelopeType;
import org.dmtf.schemas.ovf.envelope._1.FileType;
import org.dmtf.schemas.ovf.envelope._1.IpPoolType;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.abiserver.abicloudws.AbiCloudConstants;
import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.StateEnum;
import com.abiquo.abiserver.business.hibernate.pojohb.networking.DHCPServiceHB;
import com.abiquo.abiserver.business.hibernate.pojohb.networking.IpPoolManagementHB;
import com.abiquo.abiserver.business.hibernate.pojohb.service.RemoteServiceHB;
import com.abiquo.abiserver.business.hibernate.pojohb.service.RemoteServiceType;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.NodeHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.NodeVirtualImageHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualappHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualmachineHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualhardware.ResourceAllocationSettingData;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualhardware.ResourceManagementHB;
import com.abiquo.abiserver.exception.PersistenceException;
import com.abiquo.abiserver.persistence.DAOFactory;
import com.abiquo.abiserver.persistence.dao.infrastructure.RemoteServiceDAO;
import com.abiquo.abiserver.persistence.dao.networking.VlanNetworkDAO;
import com.abiquo.abiserver.persistence.dao.virtualappliance.VirtualApplianceDAO;
import com.abiquo.abiserver.persistence.dao.virtualappliance.VirtualMachineDAO;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAOFactory;
import com.abiquo.abiserver.pojo.infrastructure.Datastore;
import com.abiquo.abiserver.pojo.infrastructure.HyperVisor;
import com.abiquo.abiserver.pojo.infrastructure.PhysicalMachine;
import com.abiquo.abiserver.pojo.infrastructure.Rack;
import com.abiquo.abiserver.pojo.infrastructure.State;
import com.abiquo.abiserver.pojo.infrastructure.VirtualMachine;
import com.abiquo.abiserver.pojo.virtualappliance.Node;
import com.abiquo.abiserver.pojo.virtualappliance.NodeVirtualImage;
import com.abiquo.abiserver.pojo.virtualappliance.VirtualAppliance;
import com.abiquo.abiserver.pojo.virtualimage.VirtualImage;
import com.abiquo.abiserver.pojo.virtualimage.VirtualImageConversions;
import com.abiquo.abiserver.pojo.virtualimage.VirtualImageDecorator;
import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.ovfmanager.cim.CIMResourceAllocationSettingDataUtils;
import com.abiquo.ovfmanager.cim.CIMTypesUtils.CIMResourceTypeEnum;
import com.abiquo.ovfmanager.cim.CIMVirtualSystemSettingDataUtils;
import com.abiquo.ovfmanager.ovf.OVFEnvelopeUtils;
import com.abiquo.ovfmanager.ovf.OVFReferenceUtils;
import com.abiquo.ovfmanager.ovf.exceptions.EmptyEnvelopeException;
import com.abiquo.ovfmanager.ovf.exceptions.IdAlreadyExistsException;
import com.abiquo.ovfmanager.ovf.exceptions.RequiredAttributeException;
import com.abiquo.ovfmanager.ovf.exceptions.SectionException;
import com.abiquo.ovfmanager.ovf.exceptions.SectionNotPresentException;
import com.abiquo.ovfmanager.ovf.section.DiskFormat;
import com.abiquo.ovfmanager.ovf.section.OVFDiskUtils;
import com.abiquo.ovfmanager.ovf.section.OVFNetworkUtils;
import com.abiquo.ovfmanager.ovf.section.OVFVirtualHadwareSectionUtils;

public class OVFModelFromVirtualAppliance
{

    private final static Logger logger = LoggerFactory
        .getLogger(OVFModelFromVirtualAppliance.class);

    // /////////// InfrastructureWS

    public EnvelopeType changeMachineState(final VirtualMachine virtualMachine,
        final String machineState, final List<ResourceAllocationSettingData> additionalRasds)
        throws Exception
    {
        EnvelopeType envelope = null;

        try
        {
            // Creates an OVF envelope from the virtual machine parameters
            envelope = constructEnvelopeType(virtualMachine, machineState, additionalRasds);
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
     * @param machineState TODO
     * @return an OVFEnvelope with the information contained on the virtualMachine
     * @throws Exception, if the virtualMachine can not be represented as an OVF document.
     */
    public EnvelopeType constructEnvelopeType(final VirtualMachine virtualMachine,
        final String machineState, final List<ResourceAllocationSettingData> additionalRasds)
        throws Exception
    {
        EnvelopeType envelope = new EnvelopeType();
        ReferencesType references = new ReferencesType();

        String instanceId = virtualMachine.getUUID();
        String machineName = virtualMachine.getName();
        VirtualImage virtualImage = virtualMachine.getVirtualImage();

        try
        {
            // Create NetworkSection and Network and add to Envelope
            NetworkSectionType netSection =
                OVFEnvelopeUtils.createSection(NetworkSectionType.class, null);
            Network net =
                OVFNetworkUtils.createNetwork(virtualMachine.getName() + "_network",
                    "Appliance Network identifier");
            OVFNetworkUtils.addNetwork(netSection, net);

            // Set the network section on the envelope
            OVFEnvelopeUtils.addSection(envelope, netSection);

            // Add the custom network. Can be null if we are deleting the node from the VirtualApp
            // with an update nodes operation
            AbicloudNetworkType customNetwork = createCustomNetwork(virtualMachine.getId());
            if (customNetwork != null)
            {
                OVFEnvelopeUtils.addSection(envelope, customNetwork);
            }

            // The Id of the virtualSystem is used for machine name
            VirtualSystemType virtualSystem =
                OVFEnvelopeUtils.createVirtualSystem(instanceId, machineName, null);

            // There is only one virtual base disk
            VirtualHardwareSectionType hardwareSection =
                createVirtualSystemSection(virtualMachine, virtualImage, null, 0, additionalRasds);

            // Configure AnnotationSection with the RD port
            AnnotationSectionType annotationSection =
                createVirtualSystemRDPortAnnotationSection(virtualMachine);
            if (machineState != null)
            {
                annotationSection.getOtherAttributes().put(AbiCloudConstants.machineStateQname,
                    machineState);
            }

            // OVFEnvelopeUtils.addSection(virtualSystem, productSection);
            OVFEnvelopeUtils.addSection(virtualSystem, hardwareSection);
            OVFEnvelopeUtils.addSection(virtualSystem, annotationSection);

            // Setting the virtual Disk package level element to the envelope
            VirtualDiskDescType virtualDisk =
                createDiskFromVirtualImage(virtualMachine, virtualImage, "0");
            OVFDiskUtils.addDisk(envelope, virtualDisk);

            OVFReferenceUtils.addFile(references,
                createFileFromVirtualImage(virtualImage, virtualMachine, false, false));

            // Setting the virtual system as envelope content
            OVFEnvelopeUtils.addVirtualSystem(envelope, virtualSystem);
            envelope.setReferences(references);
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
            return datastore.getUUID() + datastore.getDirectory();
        }
        else
        {
            return datastore.getUUID();
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
        String state = null;
        switch (virtualMachine.getState().toEnum())
        {
            case RUNNING:
                state = AbiCloudConstants.POWERUP_ACTION;
                break;
            case PAUSED:
                state = AbiCloudConstants.PAUSE_ACTION;
                break;
            case POWERED_OFF:
                state = AbiCloudConstants.POWERDOWN_ACTION;
                break;
            case REBOOTED:
                state = AbiCloudConstants.RESUME_ACTION;
                break;
        }
        return state;
    }

    private static String codifyRepositoryAndPath(final String imagePath, final String repository)
    {
        // TODO EBS when the path is formed by IP|IQN avoid using the repository and just the path
        String codify = null;

        if (imagePath.indexOf("|") != -1)
        {
            codify = imagePath;
        }
        else
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

        /**
         * TODO replace protocol form the repository
         */

        logger.info("Using repository [{}] and imagepath [{}]", repository, imagePath);

        logger.info("Codify ''{}''", codify);

        return codify;
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
        return createVirtualApplication(virtualAppliance, false);
    }

    public EnvelopeType createVirtualApplication(final VirtualAppliance virtualAppliance,
        final boolean bundling) throws Exception
    {
        return createVirtualApplication(virtualAppliance, bundling, false);
    }

    public EnvelopeType createVirtualApplicationHA(final VirtualAppliance virtualAppliance)
        throws Exception
    {
        return createVirtualApplication(virtualAppliance, false, true);
    }

    private EnvelopeType createVirtualApplication(final VirtualAppliance virtualAppliance,
        final boolean bundling, final boolean isHa) throws Exception
    {
        // Create an OVF envelope
        EnvelopeType envelope = new EnvelopeType();

        // Using the name as the virtual System Id
        String vscId = String.valueOf(virtualAppliance.getId());
        VirtualSystemCollectionType virtualSystemCollection =
            OVFEnvelopeUtils.createVirtualSystemCollection(vscId, null, null); // TODO info and name

        // Creating the references element
        ReferencesType references = new ReferencesType();

        // Creating the virtual Disk package level element
        // DiskSectionType diskSectionTypePackage =
        // OVFEnvelopeUtils.createSection(DiskSectionType.class, null);

        // Create NetworkSection and Network and add to Envelope
        NetworkSectionType netSection =
            OVFEnvelopeUtils.createSection(NetworkSectionType.class, null);
        Network net =
            OVFNetworkUtils.createNetwork(virtualAppliance.getName() + "_network",
                "Appliance Network identifier");
        OVFNetworkUtils.addNetwork(netSection, net);

        // set the network section on the envelope
        OVFEnvelopeUtils.addSection(envelope, netSection);

        // Add the custom network
        AbicloudNetworkType customNetwork = createCustomNetwork(virtualAppliance);
        OVFEnvelopeUtils.addSection(envelope, customNetwork);

        // Getting the all the virtual Machines
        for (Node node : virtualAppliance.getNodes())
        {
            if (node.isNodeTypeVirtualImage())
            {
                NodeVirtualImage nodeVirtualImage = (NodeVirtualImage) node;

                StateEnum vmState = nodeVirtualImage.getVirtualMachine().getState().toEnum();

                // Creates the virtual system inside the virtual system collection
                VirtualSystemType virtualSystem =
                    createVirtualSystem(nodeVirtualImage, virtualAppliance.getName());

                OVFEnvelopeUtils.addVirtualSystem(virtualSystemCollection, virtualSystem);

                // Setting the virtual Disk package level element to the envelope
                String diskId = String.valueOf(nodeVirtualImage.getId());
                VirtualDiskDescType virtualDisk =
                    createDiskFromVirtualImage(nodeVirtualImage.getVirtualMachine(),
                        nodeVirtualImage.getVirtualImage(), diskId);
                OVFDiskUtils.addDisk(envelope, virtualDisk);

                // Adding the virtual disks to references
                try
                {
                    OVFReferenceUtils.addFile(
                        references,
                        createFileFromVirtualImage(nodeVirtualImage.getVirtualImage(),
                            nodeVirtualImage.getVirtualMachine(), bundling, isHa));
                }
                catch (IdAlreadyExistsException e)
                {
                    // If there is already a disk reference of a file, that means two machine are
                    // pointing
                    // at the same disk, which reference is already inserted in the OVF. So, in this
                    // case,
                    // an 'IdAlreadyExistsException' is not problematic
                    logger.trace("File Reference already inserted");
                }

            }
            else
            // not nodevirtualimage
            {
                // TODO warn or error
            }

        }// for each node on the appliance

        // Adding the virtual System collection to the envelope
        OVFEnvelopeUtils.addVirtualSystem(envelope, virtualSystemCollection);

        // Adding the references to the envelope
        envelope.setReferences(references);

        return envelope;
    }

    private static AbicloudNetworkType createCustomNetwork(final Integer idVirtualMachine)
        throws Exception
    {
        DAOFactory factory = HibernateDAOFactory.instance();
        VirtualMachineDAO vmDAO = factory.getVirtualMachineDAO();
        VlanNetworkDAO vlanDAO = factory.getVlanNetworkDAO();

        factory.beginConnection();

        VirtualmachineHB vmHB = vmDAO.findById(idVirtualMachine);

        // [ABICLOUDPREMIUM-1731] If we are removing the VM with an update nodes operation, the VM
        // will not exist in DB. We return null to ignore the network section (it is not needed to
        // delete the node).
        if (vmHB == null)
        {
            return null;
        }

        Map<Integer, List<IpPoolManagementHB>> vlansConfig =
            new HashMap<Integer, List<IpPoolManagementHB>>();

        AbicloudNetworkType networkType = new AbicloudNetworkType();

        // Build the Map of vlans with the IPs used by the VM
        for (ResourceManagementHB rasman : vmHB.getResman())
        {
            if (rasman instanceof IpPoolManagementHB)
            {
                IpPoolManagementHB ipman = (IpPoolManagementHB) rasman;

                if (networkType.getUuid() == null)
                {
                    networkType.setUuid(ipman.getVirtualDataCenter().getNetwork().getUuid());
                }

                Integer vlanId = ipman.getVlanNetworkId();
                List<IpPoolManagementHB> vlanIPs = vlansConfig.get(vlanId);

                if (vlanIPs == null)
                {
                    vlanIPs = new LinkedList<IpPoolManagementHB>();
                }

                vlanIPs.add(ipman);
                vlansConfig.put(vlanId, vlanIPs);
            }
        }

        for (Map.Entry<Integer, List<IpPoolManagementHB>> vlanConfig : vlansConfig.entrySet())
        {
            Integer vlanId = vlanConfig.getKey();
            List<IpPoolManagementHB> ips = vlanConfig.getValue();

            Integer numberOfRules = 0;
            OrgNetworkType vlan = vlanDAO.findById(vlanId);
            DHCPServiceHB service = (DHCPServiceHB) vlan.getConfiguration().getDhcpService();
            if (service.getDhcpRemoteServiceId() != null)
            {
                RemoteServiceDAO rmDAO = factory.getRemoteServiceDAO();
                RemoteServiceHB remo = rmDAO.findById(service.getDhcpRemoteServiceId());

                service.setDhcpAddress(remo.getURI().getHost());
                service.setDhcpPort(remo.getURI().getPort());
            }

            // Pass all the IpPoolManagement to IpPoolType if the virtual machine is assigned.
            for (IpPoolManagementHB ip : ips)
            {
                IpPoolType rule = new IpPoolType();
                rule.setConfigureGateway(ip.getConfigureGateway());
                rule.setIp(ip.getIp());
                rule.setMac(ip.getMac());
                rule.setName(ip.getName());

                service.getStaticRules().add(rule);

                numberOfRules++;
            }

            if (numberOfRules > 0)
            {
                networkType.getNetworks().add(vlan);
            }
        }

        factory.endConnection();

        return networkType;
    }

    /**
     * Helper method to createVirtualApplication - Adds the CustomNetwork to the EnvelopeType
     * 
     * @param envelope a reference to an EnvelopeType object to which the CustomNetwork section will
     *            be added
     * @param virtualAppliance a VirtualAppliance object which may or may not have a network
     * @throws Exception
     */
    private static AbicloudNetworkType createCustomNetwork(final VirtualAppliance virtualAppliance)
        throws Exception
    {
        DAOFactory factory = HibernateDAOFactory.instance();

        factory.beginConnection();

        VirtualApplianceDAO vappDAO = factory.getVirtualApplianceDAO();
        VirtualappHB persistedVapp = vappDAO.findByIdNamedExtended(virtualAppliance.getId());

        factory.endConnection();

        AbicloudNetworkType networkType = new AbicloudNetworkType();

        for (NodeHB< ? > nodeHB : persistedVapp.getNodesHB())
        {
            NodeVirtualImageHB nvi = (NodeVirtualImageHB) nodeHB;
            AbicloudNetworkType vmNetwork =
                createCustomNetwork(nvi.getVirtualMachineHB().getIdVm());

            if (networkType.getUuid() == null)
            {
                networkType.setUuid(vmNetwork.getUuid());
            }

            networkType.getNetworks().addAll(vmNetwork.getNetworks());
        }

        return networkType;
    }

    private FileType createFileFromVirtualImage(final VirtualImage virtualImage,
        final VirtualMachine virtualMachine, final boolean bundling, final boolean isHa)
        throws RequiredAttributeException
    {
        String imagePath = null;

        String fileId = virtualImage.getName() + "." + virtualMachine.getId();

        if (virtualImage instanceof VirtualImageDecorator)
        {
            VirtualImageDecorator virtualImageDecorated = (VirtualImageDecorator) virtualImage;
            imagePath = virtualImageDecorated.getBasePath();
            fileId += "." + virtualImageDecorated.getPath();
        }
        else
        {
            imagePath = virtualImage.getPath();
        }

        // Combining the repository path + the virtual machine relative path (TODO AM)
        String imageRepository = null;
        if (virtualImage.getRepository() != null)
        {
            imageRepository = virtualImage.getRepository().getURL();
        }

        // TODO bundle fail!
        if (!bundling)
        {
            VirtualImageConversions conversion = virtualMachine.getConversion();
            if (conversion != null)
            {
                imagePath = conversion.getTargetPath();
            }
        }

        imagePath = processImagePath(virtualMachine, virtualImage, imagePath);

        String absolutePath = codifyRepositoryAndPath(imagePath, imageRepository);

        /*
         * nodeVirtualImage.getVirtualImage().getRepository().getURL() +
         * nodeVirtualImage.getVirtualImage().getPath();
         */

        BigInteger fileSize = null; // TODO required fileSize

        FileType virtualDiskImageFile =
            OVFReferenceUtils.createFileType(fileId, absolutePath, fileSize, null, null); // TODO
        insertTargetDataStore(virtualDiskImageFile, virtualMachine.getDatastore().getUUID()
            + virtualMachine.getDatastore().getDirectory());

        if (isHa)
        {
            setHA(virtualDiskImageFile);
        }

        // compression
        // chunk
        VirtualImageDecorator decorator =
            virtualImage instanceof VirtualImageDecorator ? (VirtualImageDecorator) virtualImage
                : VirtualImageDecorator.createDecorator(virtualImage);

        String path = null;
        String name = null;
        boolean isManaged = true;

        if (!virtualImage.isManaged())
        {

            name = decorator.getNotManagedBundleName();
            path = decorator.getNotManagedBundlePath() + "/" + name;
            virtualImage.setPath(path);
            virtualImage.setName(name);
            isManaged = false;
        }
        else
        {
            name = decorator.getName();
            path = decorator.getPath();
            isManaged = true;
        }

        virtualDiskImageFile.getOtherAttributes().put(new QName("destinationPath"), path);
        virtualDiskImageFile.getOtherAttributes().put(new QName("isManaged"),
            String.valueOf(isManaged));

        insertRepositoryManager(virtualDiskImageFile, virtualMachine);

        return virtualDiskImageFile;
    }

    /**
     * In case of HA create/delete operation a new custom parameter is set on the Disk Element to
     * indicate do not execute any operation to copy/remove the disk from the target datastore.
     */
    private static void setHA(final FileType virtualDiskImageFile)
    {
        virtualDiskImageFile.getOtherAttributes().put(AbiCloudConstants.HA_DISK,
            Boolean.TRUE.toString());
    }

    /**
     * Override to perform custom image path processing.
     * 
     * @param nodeVirtualImage The Virtual image being processed.
     * @param imagePath The computed path of the virtual image.
     * @throws RequiredAttributeException If an error occurs.
     */
    protected String processImagePath(final VirtualMachine virtualMachine,
        final VirtualImage virtualImage, final String imagePath) throws RequiredAttributeException
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
        virtualDiskImageFile.getOtherAttributes().put(AbiCloudConstants.DATASTORE_QNAME, dataStore);
    }

    private static VirtualDiskDescType createDiskFromVirtualImage(
        final VirtualMachine virtualMachine, final VirtualImage virtualImage, final String diskId)
    {
        String fileRef = virtualImage.getName() + "." + virtualMachine.getId();

        if (virtualImage instanceof VirtualImageDecorator)
        {
            VirtualImageDecorator decorator = (VirtualImageDecorator) virtualImage;
            fileRef += "." + decorator.getPath();
        }

        DiskFormat format;

        if (virtualMachine.getConversion() != null)
        {
            format =
                DiskFormat.fromValue(virtualMachine.getConversion().getDiskTargetFormatType()
                    .getUri());
        }
        else
        {
            format = DiskFormat.fromValue(virtualImage.getDiskFormatType().getUri());
        }

        Long capacity = virtualImage.getHdRequired();
        Long populate = virtualImage.getHdRequired(); // TODO required (using the fileSize + disk
        // format it can be computed)
        //
        // Adding the virtual Disks to the package level element
        VirtualDiskDescType virtualDescTypePackage =
            OVFDiskUtils.createDiskDescription("disk_" + diskId, fileRef, format, capacity, null,
                populate, null); // TODO unit, parent,

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

        // The Id of the virtualSystem is used for machine name
        String vsId = virtualMachine.getUUID(); // TODO Using the machine instance UUID as ID
        VirtualSystemType virtualSystem =
            OVFEnvelopeUtils.createVirtualSystem(vsId, virtualMachine.getName(),
                nodeVirtualImage.getName());

        // Create a productSection with the virtual system IP
        // ProductSectionType productSection =
        // createVirtualSystemMACOnProductSection(nodeVirtualImage);

        // Configure CPU, RAM and Network
        // NodeVirtualImage is a temporal attribute!!!
        VirtualHardwareSectionType hardwareSection =
            createVirtualSystemSection(virtualMachine, virtualImage, networkName,
                nodeVirtualImage.getId(), null);

        // Configure AnnotationSection with the RD port and password
        AnnotationSectionType annotationSection =
            createVirtualSystemRDPortAnnotationSection(virtualMachine);

        // OVFEnvelopeUtils.addSection(virtualSystem, productSection);
        OVFEnvelopeUtils.addSection(virtualSystem, hardwareSection);
        OVFEnvelopeUtils.addSection(virtualSystem, annotationSection);

        return virtualSystem;
    }

    private static AnnotationSectionType createVirtualSystemRDPortAnnotationSection(
        final VirtualMachine virtualMachine)
    {
        // TODO OVFEnvelopeUtils.createSection(AnnotationSectionType.class, null);
        AnnotationSectionType annotationSection = new AnnotationSectionType();

        Map<QName, String> otherAttributes = annotationSection.getOtherAttributes();

        String rdPort = String.valueOf(virtualMachine.getVdrpPort());
        otherAttributes.put(AbiCloudConstants.remoteDesktopPortQname, String.valueOf(rdPort));
        logger.debug("The remote desktop port included is: " + String.valueOf(rdPort));

        if (virtualMachine.getPassword() != null && !virtualMachine.getPassword().equals(""))
        {
            String rdPassword = virtualMachine.getPassword();
            otherAttributes.put(AbiCloudConstants.remoteDesktopPasswordQname, rdPassword);
            logger.debug("The remote desktop password is: " + rdPassword);
        }

        return annotationSection;
    }

    /**
     * hypervisor info
     * 
     * @throws RequiredAttributeException
     */
    private VirtualHardwareSectionType createVirtualSystemSection(
        final VirtualMachine virtualMachine, final VirtualImage virtualImage,
        final String networkName, final Integer nodeId,
        final List<ResourceAllocationSettingData> additionalRasds)
        throws RequiredAttributeException
    {
        HyperVisor hypervisor = (HyperVisor) virtualMachine.getAssignedTo();
        String hypervisorAddres = "http://" + hypervisor.getIp() + ":" + hypervisor.getPort() + "/";
        String vsystemType = hypervisor.getType().getName();
        String instanceID = virtualMachine.getUUID();

        // VIRTUAL SYSTEM SETTING DATA
        VSSDType vssd =
            CIMVirtualSystemSettingDataUtils.createVirtualSystemSettingData("Hypervisor",
                instanceID, null, null, null, null); // TODO

        insertUserAndPassword(vssd, hypervisor.getUser(), hypervisor.getPassword());

        // Setting the hypervisor address as VirtualSystemIdentifier element
        String virtualSystemIdentifier = hypervisorAddres;

        // Setting the hypervisor type
        CIMVirtualSystemSettingDataUtils.setVirtualSystemToVSSettingData(vssd,
            virtualSystemIdentifier, vsystemType);

        // Creating the VirtualHardware element
        VirtualHardwareSectionType hardwareSection =
            OVFVirtualHadwareSectionUtils.createVirtualHardwareSection(vssd, null, "transport"); // TODO
        // info
        // and
        // transport

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

        String virtualImageId = String.valueOf(nodeId);
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
        List<ResourceAllocationSettingData> rasdList =
            getResourceAllocationSettingDataList(virtualMachine,
                getPhysicalMachineIqn(virtualMachine), additionalRasds);

        for (ResourceAllocationSettingData rasd : rasdList)
        {
            RASDType rasdType = ResourceAllocationSettingData.toCIM_RASDType(rasd);
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

    private String getPhysicalMachineIqn(final VirtualMachine virtualMachine)
    {
        // Get Virtual Datacenter
        HyperVisor hv = (HyperVisor) virtualMachine.getAssignedTo();
        PhysicalMachine pm = (PhysicalMachine) hv.getAssignedTo();

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
        vssd.getOtherAttributes().put(AbiCloudConstants.ADMIN_USER_QNAME, user);
        vssd.getOtherAttributes().put(AbiCloudConstants.ADMIN_USER_PASSWORD_QNAME, password);
    }

    /**
     * Private helper to get the resources allocation setting data from the DB. This operation is
     * need since the virtual machine object got it doesn't store this information. When the pojos
     * for FLEX will be removed this operation won't be necessary
     * 
     * @param virtualMachine the virtual machine to get the RASD from
     * @return the resource allocation setting data list
     */
    protected List<ResourceAllocationSettingData> getResourceAllocationSettingDataList(
        final VirtualMachine virtualMachine, final String initiatorIqn,
        final List<ResourceAllocationSettingData> additionalRasds)
        throws RequiredAttributeException
    {
        List<ResourceAllocationSettingData> rads = new ArrayList<ResourceAllocationSettingData>();
        DAOFactory factory = HibernateDAOFactory.instance();

        try
        {
            VirtualMachineDAO vmDAO = factory.getVirtualMachineDAO();

            factory.beginConnection();
            VirtualmachineHB virtualMachineHB = vmDAO.findById(virtualMachine.getId());

            // [ABICLOUDPREMIUM-1731] If we are removing the VM with an update nodes operation, the
            // VM will not exist in DB. We return the empty list to ignore the rasd section (it is
            // not needed to delete the node).
            if (virtualMachineHB != null)
            {
                rads = virtualMachineHB.getRasds();
            }

            factory.endConnection();

        }
        catch (Exception e)
        {
            factory.rollbackConnection();
            logger.error(
                "An error was occurred when getting the rasds from the virtual machine: {}", e);

        }

        return rads;
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
                Collection<Node> nodes = virtualAppliance.getNodes();
                for (Node node : nodes)
                {
                    NodeVirtualImage nodeVi = (NodeVirtualImage) node;
                    VirtualMachine vm = nodeVi.getVirtualMachine();
                    String uuid = vm.getUUID();
                    if (uuid.equals(subVirtualSystem.getId()))
                    {
                        virtualSystemState = getStateToApply(vm.getState());
                    }

                }
                AnnotationSectionType annotationSection =
                    OVFEnvelopeUtils.getSection(subVirtualSystem, AnnotationSectionType.class);

                Map<QName, String> attributes = annotationSection.getOtherAttributes();
                attributes.put(AbiCloudConstants.machineStateQname, virtualSystemState);
            }
        }
        return envelope;
    }

    /**
     * Private helper to translate the Virtual machine state with the state to apply as parameter in
     * the Annotation Section of the Virtual System section
     * 
     * @param state the virtual machien state
     * @return
     */
    private static String getStateToApply(final State state)
    {
        String actionState = null;
        switch (state.toEnum())
        {
            case RUNNING:
                actionState = AbiCloudConstants.POWERUP_ACTION;
                break;
            case POWERED_OFF:
                actionState = AbiCloudConstants.POWERDOWN_ACTION;
                break;
            case PAUSED:
                actionState = AbiCloudConstants.PAUSE_ACTION;
                break;
            default:
                break;
        }
        return actionState;
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
            attributes.put(AbiCloudConstants.machineStateQname, newState);

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
                attributes.put(AbiCloudConstants.machineStateQname, newState);
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
    private static void insertRepositoryManager(final FileType virtualDiskImageFile,
        final VirtualMachine virtualMachine)
    {
        // XenServer virtual factory plugin needs to know the RepositoryManager address
        String repositoryManagerAddress = getRepositoryManagerAddress(virtualMachine);

        if (repositoryManagerAddress != null)
        {
            virtualDiskImageFile.getOtherAttributes().put(new QName("repositoryManager"),
                repositoryManagerAddress);
        }
    }

    private static String getRepositoryManagerAddress(final VirtualMachine virtualMachine)
    {
        // Get Virtual Datacenter
        HyperVisor hv = (HyperVisor) virtualMachine.getAssignedTo();
        PhysicalMachine pm = (PhysicalMachine) hv.getAssignedTo();
        Rack rack = (Rack) pm.getAssignedTo();
        Integer idDatacenter = rack.getDataCenter().getId();

        try
        {
            DAOFactory factory = HibernateDAOFactory.instance();
            RemoteServiceDAO rsDAO = factory.getRemoteServiceDAO();

            if (!factory.isTransactionActive())
            {
                factory.beginConnection();
            }

            List<RemoteServiceHB> remoteServices =
                rsDAO.getRemoteServicesByType(idDatacenter, RemoteServiceType.APPLIANCE_MANAGER);

            if (remoteServices == null || remoteServices.isEmpty())
            {
                logger.error("Could find the Remote Repository Remote Service");
                return null;
            }

            String repositoryManagerUri = remoteServices.iterator().next().getUri();

            factory.endConnection();

            return repositoryManagerUri;
        }
        catch (PersistenceException ex)
        {
            logger.error("Could not get Repository Manager Address", ex);
        }

        return null;
    }

}
