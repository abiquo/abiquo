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

package com.abiquo.virtualfactory.model.ovf;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.sound.midi.SysexMessage;
import javax.xml.namespace.QName;

import org.dmtf.schemas.ovf.envelope._1.AbicloudNetworkType;
import org.dmtf.schemas.ovf.envelope._1.AnnotationSectionType;
import org.dmtf.schemas.ovf.envelope._1.ContentType;
import org.dmtf.schemas.ovf.envelope._1.DiskSectionType;
import org.dmtf.schemas.ovf.envelope._1.EnvelopeType;
import org.dmtf.schemas.ovf.envelope._1.FileType;
import org.dmtf.schemas.ovf.envelope._1.IpPoolType;
import org.dmtf.schemas.ovf.envelope._1.OrgNetworkType;
import org.dmtf.schemas.ovf.envelope._1.RASDType;
import org.dmtf.schemas.ovf.envelope._1.ReferencesType;
import org.dmtf.schemas.ovf.envelope._1.VSSDType;
import org.dmtf.schemas.ovf.envelope._1.VirtualDiskDescType;
import org.dmtf.schemas.ovf.envelope._1.VirtualHardwareSectionType;
import org.dmtf.schemas.ovf.envelope._1.VirtualSystemCollectionType;
import org.dmtf.schemas.ovf.envelope._1.VirtualSystemType;
import org.dmtf.schemas.wbem.wscim._1.common.CimString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.ovfmanager.cim.CIMTypesUtils.CIMResourceTypeEnum;
import com.abiquo.ovfmanager.cim.CIMVirtualSystemSettingDataUtils;
import com.abiquo.ovfmanager.ovf.OVFEnvelopeUtils;
import com.abiquo.ovfmanager.ovf.exceptions.EmptyEnvelopeException;
import com.abiquo.ovfmanager.ovf.exceptions.IdNotFoundException;
import com.abiquo.ovfmanager.ovf.exceptions.InvalidSectionException;
import com.abiquo.ovfmanager.ovf.exceptions.RequiredAttributeException;
import com.abiquo.ovfmanager.ovf.exceptions.SectionAlreadyPresentException;
import com.abiquo.ovfmanager.ovf.exceptions.SectionException;
import com.abiquo.ovfmanager.ovf.exceptions.SectionNotPresentException;
import com.abiquo.ovfmanager.ovf.section.OVFAnnotationUtils;
import com.abiquo.ovfmanager.ovf.section.OVFVirtualHadwareSectionUtils;
import com.abiquo.virtualfactory.exception.HypervisorException;
import com.abiquo.virtualfactory.exception.PluginException;
import com.abiquo.virtualfactory.exception.VirtualMachineException;
import com.abiquo.virtualfactory.exception.VirtualNetworkException;
import com.abiquo.virtualfactory.model.AbsVirtualMachine;
import com.abiquo.virtualfactory.model.State;
import com.abiquo.virtualfactory.model.VirtualAppliance;
import com.abiquo.virtualfactory.model.VirtualDisk;
import com.abiquo.virtualfactory.model.VirtualDiskType;
import com.abiquo.virtualfactory.model.VirtualSystemModel;
import com.abiquo.virtualfactory.model.config.BootstrapConfiguration;
import com.abiquo.virtualfactory.model.config.HypervisorConfiguration;
import com.abiquo.virtualfactory.model.config.VirtualMachineConfiguration;
import com.abiquo.virtualfactory.network.VirtualNIC;
import com.abiquo.virtualfactory.utils.AbicloudConstants;

/**
 * @author jdevesa@abiquo.com
 */
public class OVFModelToVirtualAppliance implements OVFModelConvertable
{

    private final static Logger logger = LoggerFactory.getLogger(OVFModelToVirtualAppliance.class);

    final static Integer applyStateDelayMs = Integer.valueOf(System.getProperty(
        "abiquo.virtualfactory.applyStateDelayMs", "0"));

    /**
     * Default constructor
     */
    public OVFModelToVirtualAppliance()
    {

    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.virtualfactory.model.ovf.OVFModelConvertable#addMachinesToVirtualAppliance(com
     * .abiquo.virtualfactory.model.VirtualAppliance,
     * org.dmtf.schemas.ovf.envelope._1.VirtualSystemType, java.util.Map, java.lang.String,
     * java.lang.String, boolean[])
     */
    @Override
    public void addMachinesToVirtualAppliance(final VirtualAppliance virtualAppliance,
        final VirtualSystemType virtualSystemInstance,
        final Map<String, VirtualDisk> virtualDiskMap, final EnvelopeType envelope)
        throws MalformedURLException, VirtualMachineException, SectionNotPresentException,
        SectionException, RequiredAttributeException, IdNotFoundException, PluginException,
        HypervisorException
    {
        // VIRTUAL HARDWARE
        // TODO Control more than one VirtualHardwareSectionType instances
        VirtualHardwareSectionType hardwareSection =
            OVFEnvelopeUtils.getSection(virtualSystemInstance, VirtualHardwareSectionType.class);

        // Getting the Virtual System type
        VSSDType virtualSystemDataType = hardwareSection.getSystem();
        CimString virtualSystemTypeCimString = virtualSystemDataType.getVirtualSystemType();
        String virtualSystemTypeString = virtualSystemTypeCimString.getValue();
        logger.debug("The VirtualSystemTypeString is :" + virtualSystemTypeString);
        String adminUser =
            virtualSystemDataType.getOtherAttributes().get(AbicloudConstants.ADMIN_USER_QNAME);
        String adminPassword =
            virtualSystemDataType.getOtherAttributes().get(
                AbicloudConstants.ADMIN_USER_PASSWORD_QNAME);

        // Getting the virtual system instance ID
        CimString instanceIdCimString = virtualSystemDataType.getInstanceID();
        String virtualSystemId = instanceIdCimString.getValue();
        logger.debug("The VirtualSystemId is :" + virtualSystemId);

        // Getting the virtual system address
        CimString virtualSystemIdCimString = virtualSystemDataType.getVirtualSystemIdentifier();
        String virtualSystemAddress = virtualSystemIdCimString.getValue();
        logger.debug("The VirtualSystem Address is :" + virtualSystemAddress);

        VirtualMachineConfiguration virtualConfig =
            getVirtualMachineConfigurationFromVirtualSystem(virtualSystemInstance, virtualDiskMap,
                envelope);

        // Adding the virtualMachine if it doesn't exist
        AbsVirtualMachine virtualmachine =
            VirtualSystemModel.getModel().getMachine(
                getHypervisorConfigurationFromVirtualSystem(virtualSystemInstance), virtualConfig);

        // If is not created or is not deployed, create it
        if (virtualmachine == null
            || virtualmachine.getStateInHypervisor().compareTo(State.NOT_DEPLOYED) == 0)
        {
            AbsVirtualMachine newVirtualMachine =
                virtualAppliance.addMachine(virtualSystemTypeString, new URL(virtualSystemAddress),
                    virtualConfig, adminUser, adminPassword);
            newVirtualMachine.deployMachine();

        }

        logger.info("FINISHED ADDING VIRTUAL MACHINES ");
    }

    /**
     * Sort the volumes using the Generation attribute (determined by the attachment sequence to the
     * virtual machine on the server)
     */
    class VirtualDiskSequence implements Comparator<VirtualDisk>
    {
        @Override
        public int compare(final VirtualDisk arg0, final VirtualDisk arg1)
        {
            if (arg0.getSequence() > arg1.getSequence())
            {
                return 1;
            }
            return 0;
        }
    }

    // Define the inner class to sort the vnicList
    class VNICSequence implements Comparator<VirtualNIC>
    {
        @Override
        public int compare(final VirtualNIC vn1, final VirtualNIC vn2)
        {
            return vn1.getOrder() - vn2.getOrder();
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.virtualfactory.model.ovf.OVFModelConvertable#configureVirtualSystem(com.abiquo
     * .virtualfactory.model.AbsVirtualMachine, org.dmtf.schemas.ovf.envelope._1.ContentType,
     * boolean)
     */
    @Override
    public void reconfigureVirtualSystem(final AbsVirtualMachine virtualMachine,
        final ContentType virtualSystem, final HypervisorConfiguration hvConfig)
        throws VirtualMachineException, SectionException, Exception
    {
        final String poweron = "PowerUp";

        // We will change the state or reconfigure the machine depending on the state field value
        String machineState = getMachineStateFromAnnotation(virtualSystem);

        if (machineState != null)
        {
            // Apply the new state to the virtual machine
            virtualMachine.applyState(State.fromValue(machineState));

            if (machineState.equalsIgnoreCase(poweron) && applyStateDelayMs != 0)
            {
                try
                {
                    Thread.sleep(applyStateDelayMs);
                }
                catch (Exception e)
                {
                    logger.error("Error while apply state delay", e);
                }
            }

        }
        else
        {
            // Get the new virtual machine configuration
            VirtualMachineConfiguration newConfig =
                buildUpdateConfiguration(virtualMachine.getConfiguration(), virtualSystem);

            // AbsVirtualMachine newVirtualMachine =
            // VirtualSystemModel.getModel().getMachine(hvConfig, newConfig);

            newConfig = removeStatefulFromExtendedList(newConfig);
            
            // Apply the new configuration to the machine in the hypervisor
            virtualMachine.reconfigVM(newConfig);
        }
    }
    
    /**
     * Due ABICLOUDPREMIUM-2129 an stateful virtual disk is also present in the list of auxiliary disks
     * */
    private VirtualMachineConfiguration removeStatefulFromExtendedList(VirtualMachineConfiguration conf)
    {
        VirtualDisk primary = conf.getVirtualDiskBase();
        
        if(primary.getDiskType() == VirtualDiskType.STANDARD)
        {
            return conf;
        }
        
        final String primaryLocation = primary.getLocation();
        
        List<VirtualDisk> newList = new LinkedList<VirtualDisk>();
        for(VirtualDisk vd : conf.getExtendedVirtualDiskList())
        {
            final String loc = vd.getLocation();
            if(!primaryLocation.equalsIgnoreCase(loc))
            {
                newList.add(vd);
            }
        }
        
        conf.getExtendedVirtualDiskList().clear();
        conf.getExtendedVirtualDiskList().addAll(newList);
        
        return conf;
    }

    
    private VirtualMachineConfiguration buildUpdateConfiguration(
        final VirtualMachineConfiguration vmConfig, final ContentType virtualSystem)
        throws SectionException
    {
        // Here we will have always a VirtualSystemType instance
        VirtualSystemType virtualSystemInstance = (VirtualSystemType) virtualSystem;

        String rdPassword =
            getAttributeFromAnnotation(virtualSystemInstance,
                VirtualMachineConfiguration.remoteDesktopPasswordQname);

        // TODO the default value should be 0, but to avoid errors 256MB is assigned
        long newRam = 256 * 1024 * 1024;
        int newCPUNumber = 1;
        List<VirtualDisk> newDisks = new ArrayList<VirtualDisk>();

        VirtualMachineConfiguration newConfig = new VirtualMachineConfiguration(vmConfig);
        newConfig.setHypervisor(vmConfig.getHyper());
        newConfig.setRdPassword(rdPassword);

        VirtualHardwareSectionType hardwareSection =
            OVFEnvelopeUtils.getSection(virtualSystem, VirtualHardwareSectionType.class);

        for (RASDType item : hardwareSection.getItem())
        {
            int resourceType = Integer.valueOf(item.getResourceType().getValue());

            // Only add the new volumes
            if (CIMResourceTypeEnum.Memory.getNumericResourceType() == resourceType)
            {
                newRam = item.getVirtualQuantity().getValue().longValue() * 1024 * 1024;
            }
            else if (CIMResourceTypeEnum.Processor.getNumericResourceType() == resourceType)
            {
                newCPUNumber = item.getVirtualQuantity().getValue().intValue();
            }
            else if (CIMResourceTypeEnum.iSCSI_HBA.getNumericResourceType() == resourceType
                && changingStorageAndIsType(item, "NEW"))
            {
                String location;
                location =
                    item.getAddress().getValue() + "|" + item.getConnection().get(0).getValue();
                // Creating the iscsi virtual disk
                VirtualDisk iscsiVirtualDisk = new VirtualDisk();
                iscsiVirtualDisk.setId(item.getInstanceID().getValue());
                iscsiVirtualDisk.setDiskType(VirtualDiskType.ISCSI);
                iscsiVirtualDisk.setLocation(location);

                // Attachement sequence
                int gen = 0;
                if (item.getGeneration() != null && item.getGeneration().getValue() != null)
                {
                    gen = item.getGeneration().getValue().intValue();
                }

                iscsiVirtualDisk.setSequence(gen);
                newDisks.add(iscsiVirtualDisk);
            }
        }

        newConfig.setCpuNumber(newCPUNumber);
        newConfig.setMemoryRam(newRam);
        newConfig.getExtendedVirtualDiskList().clear();
        newConfig.getExtendedVirtualDiskList().addAll(newDisks);

        return newConfig;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.virtualfactory.model.ovf.OVFModelConvertable#createVirtualAppliance(org.dmtf.schemas
     * .ovf.envelope._1.EnvelopeType, boolean[])
     */
    @Override
    public VirtualAppliance createVirtualAppliance(final EnvelopeType envelope,
        final VirtualAppliance virtualAppliance, final Map<String, VirtualDisk> virtualDiskMap)
        throws MalformedURLException, VirtualMachineException, IdNotFoundException,
        EmptyEnvelopeException, SectionException, RequiredAttributeException,
        VirtualNetworkException, PluginException, HypervisorException
    {
        ContentType content = OVFEnvelopeUtils.getTopLevelVirtualSystemContent(envelope);

        // Checking content element
        if (content instanceof VirtualSystemType)
        {
            logger.trace("Checking a Virtual System");
            VirtualSystemType virtualSystem = (VirtualSystemType) content;

            // String virtualApplianceId = virtualSystemInstance.getId();

            addMachinesToVirtualAppliance(virtualAppliance, virtualSystem, virtualDiskMap, envelope);

        }// virtual system
        else if (content instanceof VirtualSystemCollectionType)
        {
            logger.trace("Checking a Virtual System collection");

            VirtualSystemCollectionType virtualSystemCollection =
                (VirtualSystemCollectionType) content;

            for (ContentType subVirtualSystem : OVFEnvelopeUtils
                .getVirtualSystemsFromCollection(virtualSystemCollection))
            {
                if (subVirtualSystem instanceof VirtualSystemType)
                {
                    addMachinesToVirtualAppliance(virtualAppliance,
                        (VirtualSystemType) subVirtualSystem, virtualDiskMap, envelope);
                }
                else if (subVirtualSystem instanceof VirtualSystemCollectionType)
                {
                    updateVirtualSystemCollection((VirtualSystemCollectionType) subVirtualSystem,
                        virtualAppliance, virtualDiskMap, envelope);
                }
            }// each sub virtual system on the collection

        }// virtual system collection
        else
        {
            throw new EmptyEnvelopeException("There is not any virtual system or virtual system collection");
        }

        return virtualAppliance;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.virtualfactory.model.ovf.OVFModelConvertable#createVirtualDisks(org.dmtf.schemas
     * .ovf.envelope._1.EnvelopeType)
     */
    @Override
    public Map<String, VirtualDisk> createVirtualDisks(final EnvelopeType envelope)
        throws IdNotFoundException, SectionException
    {
        // Map of file references and locationDisk
        Map<String, VirtualDisk> virtualDiskMap = new HashMap<String, VirtualDisk>();

        // Map with all the reference Files
        Map<String, FileType> referenceFiles = new HashMap<String, FileType>();

        // Indexing reference files
        ReferencesType references = envelope.getReferences();

        DiskSectionType diskSection = OVFEnvelopeUtils.getSection(envelope, DiskSectionType.class);
        logger.trace("Checking Disk Section");

        for (FileType f : references.getFile())
        {
            logger.debug("Registering the file id: " + f.getId() + " href:" + f.getHref());
            referenceFiles.put(f.getId(), f);
        }

        for (VirtualDiskDescType d : diskSection.getDisk())
        {
            if (referenceFiles.containsKey(d.getFileRef()))
            {
                FileType fileRef = referenceFiles.get(d.getFileRef());
                String path = fileRef.getHref();

                // Disks can be deployed to different Physical Machines.
                // The target datastore must be determined for each disk
                String targetDatastore =
                    fileRef.getOtherAttributes().get(AbicloudConstants.DATASTORE_QNAME);

                boolean isha = false;
                if (fileRef.getOtherAttributes().containsKey(AbicloudConstants.HA_DISK))
                {
                    logger.debug("Its a HA disk (do not copy or remove)");
                    isha = true;
                }

                logger.debug("Registering the virtual disk location:" + path);

                VirtualDisk virtualDisk;

                if (path.startsWith("["))
                {
                    virtualDisk =
                        new VirtualDisk(d.getDiskId(),
                            path,
                            new Long(d.getCapacity()),
                            targetDatastore,
                            d.getFileRef(),
                            d.getFormat());
                }
                else
                {
                    // EBS Then the path contains an IQN where the image must be booted
                    virtualDisk =
                        new VirtualDisk(d.getDiskId(),
                            path,
                            new Long(d.getCapacity()),
                            VirtualDiskType.ISCSI,
                            targetDatastore,
                            d.getFileRef(),
                            d.getFormat());
                }

                if (isha)
                {
                    virtualDisk.setHa();
                }

                virtualDiskMap.put(d.getDiskId(), virtualDisk);
            }
            else
            {
                throw new IdNotFoundException("The Disk fileRef: " + d.getFileRef()
                    + " should be included in the References file");
            }
        }

        return virtualDiskMap;
    }// create virtual disk

    /*
     * (non-Javadoc)
     * @seecom.abiquo.virtualfactory.model.ovf.OVFModelConvertable#createVirtualSystem(com.abiquo.
     * virtualfactory.model.AbsVirtualMachine)
     */
    @Override
    public VirtualSystemType createVirtualSystem(final AbsVirtualMachine machine)
        throws RequiredAttributeException, SectionAlreadyPresentException, SectionException
    {
        // Create a new VirtualSystem

        String vsId = machine.getConfiguration().getMachineId().toString(); // TODO getId
        String name = "VirtualSystem for a virtual machine ";
        String info = null;

        VirtualSystemType virtualSystem = OVFEnvelopeUtils.createVirtualSystem(vsId, name, info);

        // Creating the VirtualHardwareType
        String hypervisorType = machine.getConfiguration().getHyper().getHypervisorType();
        VSSDType vssd =
            CIMVirtualSystemSettingDataUtils.createVirtualSystemSettingData("elementName",
                "instanceID", null, null, null, null);
        // TODO required elementName
        // TODO required instanceID
        CIMVirtualSystemSettingDataUtils.setVirtualSystemToVSSettingData(vssd,
            "virtualSystemIdentifier", hypervisorType);

        // TODO required transport (also set some info)
        VirtualHardwareSectionType virtualHardwareSection =
            OVFVirtualHadwareSectionUtils.createVirtualHardwareSection(vssd, null, "transport");

        // Creating VirtualDiskType
        VirtualDisk virtualdisk = machine.getConfiguration().getVirtualDiskBase();

        DiskSectionType diskSection = new DiskSectionType();

        VirtualDiskDescType diskUnit = new VirtualDiskDescType(); // TODO create constructor on
        // OVFDiskUtils
        diskUnit.setFileRef(virtualdisk.getLocation());
        diskUnit.setDiskId(virtualdisk.getId());

        diskSection.getDisk().add(diskUnit);

        // Creating the Annotation Type
        AnnotationSectionType annotationSection =
            OVFAnnotationUtils.createAnnotationSection("Abiquo extension to store machine state",
                "see OtherAttributes: " + State.machineStateQname.toString());
        annotationSection.getOtherAttributes().put(State.machineStateQname,
            machine.getStateInHypervisor().value());

        // Adding section elements
        OVFEnvelopeUtils.addSection(virtualSystem, virtualHardwareSection);
        OVFEnvelopeUtils.addSection(virtualSystem, diskSection);
        OVFEnvelopeUtils.addSection(virtualSystem, annotationSection);

        return virtualSystem;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.virtualfactory.model.ovf.OVFModelConvertable#getMachineStateFromAnnotation(org
     * .dmtf.schemas.ovf.envelope._1.ContentType)
     */
    @Override
    public String getMachineStateFromAnnotation(final ContentType virtualSystem)
        throws SectionException
    {
        AnnotationSectionType annotationSection =
            OVFEnvelopeUtils.getSection(virtualSystem, AnnotationSectionType.class);

        Map<QName, String> attributes = annotationSection.getOtherAttributes();
        String machineStateValue = attributes.get(State.machineStateQname);

        // TODO if machienStateValue == null throws an exception

        return machineStateValue;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.virtualfactory.model.ovf.OVFModelConvertable#getVSSDInstanceId(org.dmtf.schemas
     * .ovf.envelope._1.ContentType)
     */
    @Override
    public String getVSSDInstanceId(final ContentType virtualSystem) throws SectionException
    {
        String virtualSystemId;

        // VIRTUAL HARDWARE
        // TODO Control more than one VirtualHardwareSectionType instances
        VirtualHardwareSectionType hardwareSection =
            OVFEnvelopeUtils.getSection(virtualSystem, VirtualHardwareSectionType.class);

        // Getting the Virtual System type

        VSSDType virtualSystemDataType = hardwareSection.getSystem();

        // Getting the virtual system instance ID
        virtualSystemId = virtualSystemDataType.getInstanceID().getValue();

        return virtualSystemId;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.virtualfactory.model.ovf.OVFModelConvertable#updateVirtualSystemCollection(org
     * .dmtf.schemas.ovf.envelope._1.VirtualSystemCollectionType,
     * com.abiquo.virtualfactory.model.VirtualAppliance, java.util.Map, boolean[])
     */
    @Override
    public void updateVirtualSystemCollection(
        final VirtualSystemCollectionType virtualSystemCollection,
        final VirtualAppliance virtualAppliance, final Map<String, VirtualDisk> virtualDiskMap,
        final EnvelopeType envelope) throws MalformedURLException, VirtualMachineException,
        SectionException, IdNotFoundException, RequiredAttributeException, PluginException,
        HypervisorException
    {

        logger.info("Update Virtual System Collection");

        for (ContentType subVirtualSystem : OVFEnvelopeUtils
            .getVirtualSystemsFromCollection(virtualSystemCollection))
        {
            if (subVirtualSystem instanceof VirtualSystemType)
            {
                addMachinesToVirtualAppliance(virtualAppliance,
                    (VirtualSystemType) subVirtualSystem, virtualDiskMap, envelope);
            }
            else if (subVirtualSystem instanceof VirtualSystemCollectionType)
            {
                updateVirtualSystemCollection((VirtualSystemCollectionType) subVirtualSystem,
                    virtualAppliance, virtualDiskMap, envelope);
            }
            else
            {
                logger.error("Invalid content type on the collection , its a "
                    + subVirtualSystem.getClass().getCanonicalName());
            }
        }// for each subVS
    }

    /**
     * From a given virtual system, get its MAC address
     * 
     * @param virtualSystem <VirtualSystemType> to look for inside
     * @return String with the MAC address
     * @throws InvalidSectionException
     * @throws SectionNotPresentException
     */
    protected String getVirtualSystemMac(final VirtualSystemType virtualSystem)
        throws SectionNotPresentException, InvalidSectionException
    {
        String macAddress = "";

        VirtualHardwareSectionType hardwareSection =
            OVFEnvelopeUtils.getSection(virtualSystem, VirtualHardwareSectionType.class);
        for (RASDType item : hardwareSection.getItem())
        {
            int resourceType = new Integer(item.getResourceType().getValue());
            if (CIMResourceTypeEnum.Ethernet_Adapter.getNumericResourceType() == resourceType)
            {
                macAddress = item.getAddress().getValue();
            }

        }// for each rasd

        return macAddress;
    }

    private String getAttributeFromAnnotation(final VirtualSystemType virtualSystem,
        final QName attribute) throws SectionException
    {
        AnnotationSectionType annotationSection =
            OVFEnvelopeUtils.getSection(virtualSystem, AnnotationSectionType.class);
        return annotationSection.getOtherAttributes().get(attribute);
    }

    @Override
    public String getVirtualAppState(final VirtualSystemCollectionType contentInstance)
        throws SectionException
    {
        String vappState = null;
        for (ContentType subVirtualSystem : OVFEnvelopeUtils
            .getVirtualSystemsFromCollection(contentInstance))
        {
            if (subVirtualSystem instanceof VirtualSystemType)
            {
                vappState = getMachineStateFromAnnotation(subVirtualSystem);
                break;
            }
        }
        return vappState;
    }

    @Override
    public void bundleVirtualSystemCollection(
        final VirtualSystemCollectionType virtualSystemCollection, final EnvelopeType envelope)
        throws IdNotFoundException, SectionException, VirtualMachineException,
        MalformedURLException, PluginException, HypervisorException
    {
        Map<String, VirtualDisk> virtualDiskMap = createVirtualDisks(envelope);

        for (ContentType subVirtualSystem : OVFEnvelopeUtils
            .getVirtualSystemsFromCollection(virtualSystemCollection))
        {
            if (subVirtualSystem instanceof VirtualSystemType)
            {
                bundleVirtualSystem((VirtualSystemType) subVirtualSystem, virtualDiskMap, envelope);
            }
            else if (subVirtualSystem instanceof VirtualSystemCollectionType)
            {
                bundleVirtualSystemCollection((VirtualSystemCollectionType) subVirtualSystem,
                    envelope);
            }
            else
            {
                // throw new
                // EmptyEnvelopeException("There is not any virtual system or virtual system collection");
            }

        }// each sub virtual system on the collection

    }

    /**
     * Private helper to bundle a virtual system
     * 
     * @param subVirtualSystem
     * @param virtualDiskMap
     * @throws VirtualMachineException
     * @throws SectionException
     * @throws MalformedURLException
     * @throws HypervisorException
     * @throws PluginException
     */
    private void bundleVirtualSystem(final VirtualSystemType subVirtualSystem,
        final Map<String, VirtualDisk> virtualDiskMap, final EnvelopeType envelope)
        throws VirtualMachineException, MalformedURLException, SectionException, PluginException,
        HypervisorException
    {
        logger.debug("Bundlelling the virtual system: {}", subVirtualSystem.getId());

        VirtualDisk virtualDisk = null;

        final List<FileType> files = envelope.getReferences().getFile();

        // TODO Control more than one VirtualHardwareSectionType instances
        VirtualHardwareSectionType hardwareSection =
            OVFEnvelopeUtils.getSection(subVirtualSystem, VirtualHardwareSectionType.class);
        // Getting the Virtual System type
        VSSDType virtualSystemDataType = hardwareSection.getSystem();

        // Getting the virtual system instance ID
        CimString instanceIdCimString = virtualSystemDataType.getInstanceID();
        String virtualSystemId = instanceIdCimString.getValue();
        logger.debug("The VirtualSystemId is :" + virtualSystemId);

        for (RASDType item : hardwareSection.getItem())
        {
            int resourceType = new Integer(item.getResourceType().getValue());

            if (CIMResourceTypeEnum.Disk_Drive.getNumericResourceType() == resourceType)
            {
                for (CimString hostResource : item.getHostResource())
                {
                    String hostResourceString = hostResource.getValue();
                    String diskId =
                        hostResourceString.replace(OVFVirtualHadwareSectionUtils.OVF_DISK_URI, "");
                    virtualDisk = virtualDiskMap.get(diskId);
                }
            }

        }// for each rasd

        String sourcePath = virtualDisk.getImagePath().trim();

        // Adding the virtualMachine if it doesn't exist
        AbsVirtualMachine virtualmachine =
            VirtualSystemModel.getModel().getMachine(
                getHypervisorConfigurationFromVirtualSystem(subVirtualSystem),
                getVirtualMachineConfigurationFromVirtualSystem(subVirtualSystem, virtualDiskMap,
                    envelope));

        String destinationPath = null;
        String snapshotName = null;
        boolean isManaged = true;

        if (files != null && !files.isEmpty())
        {
            QName qnamePath = new QName("destinationPath");
            QName isMaganedPath = new QName("isManaged");

            for (FileType file : files)
            {
                if (file.getId().equalsIgnoreCase(virtualDisk.getFileRef()))
                {
                    if (file.getOtherAttributes().containsKey(qnamePath))
                    {
                        String destinationPathTemp = file.getOtherAttributes().get(qnamePath);

                        int indexFinRepository = destinationPathTemp.lastIndexOf('/');
                        snapshotName =
                            destinationPathTemp.substring(indexFinRepository + 1,
                                destinationPathTemp.length());
                        destinationPath = destinationPathTemp.substring(0, indexFinRepository);
                        isManaged = Boolean.valueOf(file.getOtherAttributes().get(isMaganedPath));
                    }
                    break;
                }
            }
        }

        if (virtualmachine != null)
        {
            virtualmachine.bundleVirtualMachine(sourcePath, destinationPath, snapshotName,
                isManaged);
        }
    }

    @Override
    public VirtualMachineConfiguration getVirtualMachineConfigurationFromVirtualSystem(
        final VirtualSystemType virtualSystemInstance,
        final Map<String, VirtualDisk> virtualDiskMap, final EnvelopeType envelope)
        throws MalformedURLException, VirtualMachineException, SectionNotPresentException,
        SectionException
    {
        String rdPort =
            getAttributeFromAnnotation(virtualSystemInstance,
                VirtualMachineConfiguration.remoteDesktopPortQname);
        String rdPassword =
            getAttributeFromAnnotation(virtualSystemInstance,
                VirtualMachineConfiguration.remoteDesktopPasswordQname);

        String virtualSystemName = virtualSystemInstance.getName().getValue();
        logger.trace("Creating a virtual machine from a Virtual System {}", virtualSystemName);

        String virtualSystemId = null;
        String virtualSystemTypeString = null;
        String virtualSystemAddress = null;
        List<VirtualDisk> virtualDiskBaseList = new ArrayList<VirtualDisk>();
        List<VirtualDisk> extendedDiskList = new ArrayList<VirtualDisk>();
        List<VirtualNIC> vnicList = new ArrayList<VirtualNIC>();

        // TODO the default value should be 0, but to avoid errors 256MB is assigned
        long memoryRam = 256 * 1024 * 1024;
        int cpuNumber = 1;

        // VIRTUAL HARDWARE
        // TODO Control more than one VirtualHardwareSectionType instances
        VirtualHardwareSectionType hardwareSection =
            OVFEnvelopeUtils.getSection(virtualSystemInstance, VirtualHardwareSectionType.class);

        // Getting the Virtual System type
        VSSDType virtualSystemDataType = hardwareSection.getSystem();
        CimString virtualSystemTypeCimString = virtualSystemDataType.getVirtualSystemType();
        virtualSystemTypeString = virtualSystemTypeCimString.getValue();
        logger.debug("The VirtualSystemTypeString is :" + virtualSystemTypeString);

        // Getting the virtual system address
        CimString virtualSystemIdCimString = virtualSystemDataType.getVirtualSystemIdentifier();
        virtualSystemAddress = virtualSystemIdCimString.getValue();
        logger.debug("The VirtualSystem Address is :" + virtualSystemAddress);

        // Getting the virtual system instance ID
        CimString instanceIdCimString = virtualSystemDataType.getInstanceID();
        virtualSystemId = instanceIdCimString.getValue();
        logger.debug("The VirtualSystemId is :" + virtualSystemId);

        for (RASDType item : hardwareSection.getItem())
        {
            int resourceType = new Integer(item.getResourceType().getValue());

            if (CIMResourceTypeEnum.Memory.getNumericResourceType() == resourceType)
            {
                memoryRam = item.getVirtualQuantity().getValue().longValue() * 1024 * 1024;
            }
            else if (CIMResourceTypeEnum.Processor.getNumericResourceType() == resourceType)
            {
                cpuNumber = item.getVirtualQuantity().getValue().intValue();
            }
            else if (CIMResourceTypeEnum.Ethernet_Adapter.getNumericResourceType() == resourceType)
            {
                String macAddress = item.getAddress().getValue();
                String vswitchName = item.getConnection().get(0).getValue();
                int vlanTag = Integer.parseInt(item.getAllocationUnits().getValue());
                String networkName = item.getParent().getValue();
                Integer order = Integer.valueOf(item.getConfigurationName().getValue());
                VirtualNIC vnic =
                    new VirtualNIC(vswitchName, macAddress, vlanTag, networkName, order);
                vnicList.add(vnic);
            }
            else if (CIMResourceTypeEnum.Disk_Drive.getNumericResourceType() == resourceType)
            {
                for (CimString hostResource : item.getHostResource())
                {
                    String hostResourceString = hostResource.getValue();
                    String diskId =
                        hostResourceString.replace(OVFVirtualHadwareSectionUtils.OVF_DISK_URI, "");
                    virtualDiskBaseList.add(virtualDiskMap.get(diskId));
                }
            }
            else if (CIMResourceTypeEnum.iSCSI_HBA.getNumericResourceType() == resourceType
                && changingStorageAndIsType(item, "OLD"))
            {
                String location;
                location =
                    item.getAddress().getValue() + "|" + item.getConnection().get(0).getValue();
                // Creating the iscsi virtual disk
                VirtualDisk iscsiVirtualDisk = new VirtualDisk();
                iscsiVirtualDisk.setId(item.getInstanceID().getValue());
                iscsiVirtualDisk.setDiskType(VirtualDiskType.ISCSI);
                iscsiVirtualDisk.setLocation(location);

                // Attachement sequence
                int gen = 0;
                if (item.getGeneration() != null && item.getGeneration().getValue() != null)
                {
                    gen = item.getGeneration().getValue().intValue();
                }

                iscsiVirtualDisk.setSequence(gen);

                extendedDiskList.add(iscsiVirtualDisk);
            }

        }// for each rasd

        processStatefulDisks(virtualDiskBaseList, extendedDiskList);

        // Sort the disks
        Collections.sort(extendedDiskList, new VirtualDiskSequence());

        for (VirtualDisk vd : extendedDiskList)
        {
            logger.debug("Attaching disk [{}] generation sequence [{}]", vd.getLocation(),
                vd.getSequence());
        }
        // Sort the vnicList
        Collections.sort(vnicList, new VNICSequence());
        for (VirtualNIC vn : vnicList)
        {
            logger.debug("Attaching NIC [{}] generation sequence [{}]", vn.getMacAddress(),
                vn.getOrder());
        }

        logger.debug("The remote desktop port is : " + rdPort);

        VirtualMachineConfiguration virtualConfig =
            new VirtualMachineConfiguration(UUID.fromString(virtualSystemId),
                virtualSystemName,
                virtualDiskBaseList,
                Integer.parseInt(rdPort),
                rdPassword,
                memoryRam,
                cpuNumber,
                vnicList);
        // Adding the extended disks list
        virtualConfig.getExtendedVirtualDiskList().addAll(extendedDiskList);

        // Set the Appliance Manager RemoteService address
        String repositoryManagerAddress = getRepositoryManagerAddress(envelope);
        virtualConfig.setRepositoryManagerAddress(repositoryManagerAddress);

        addBootstrapConfiguration(virtualConfig, envelope);

        return virtualConfig;
    }

    /**
     * [ABICLOUDPREMIUM-1491] Check if the disk being processed is a new disk (in a deploy
     * operation) or a disk already attached to the machine (put operation). This is needed because
     * in the PUT operation volumes are managed in a different way. The OVF will have the old
     * volumes and the new volumes, so the virtualfactory can know which volumes to add and which
     * volumes to remove.
     * 
     * @param item The item being processed.
     * @return
     */
    private static boolean changingStorageAndIsType(final RASDType item, final String value)
    {
        // The volume must be included depending on what we are doing.
        // The plugins will reconfigure the disks based on the disks they are provided in the
        // current config and the new config.

        // When changing storage: current config and newconfig have different disks and the plugins
        // will apply the difference

        // When changing the state or reconfiguring ram, current config and new config must have the
        // same disks so the plugins do not try to reconfigure the storage

        boolean changingStorage =
            item.getConfigurationName() != null && item.getConfigurationName().getValue() != null
                && item.getConfigurationName().getValue().length() > 0;

        return !changingStorage || item.getConfigurationName().getValue().equals(value);
    }

    /**
     * Process the Stateful Virtual Disks in order to remove them from <code>extendedDiskList</code>
     * and configure them with the apropiate InstanceID.
     * 
     * @param virtualDiskBaseList The virtualDiskBaseList.
     * @param extendedDiskList The extendedDiskList.
     */
    private void processStatefulDisks(final List<VirtualDisk> virtualDiskBaseList,
        final List<VirtualDisk> extendedDiskList)
    {
        for (VirtualDisk baseDisk : virtualDiskBaseList)
        {
            // If it is a stateful disk
            if (isExternalDisk(baseDisk))
            {
                // Find the disk in the extendedDiskList
                for (Iterator<VirtualDisk> it = extendedDiskList.iterator(); it.hasNext();)
                {
                    VirtualDisk extendedDisk = it.next();
                    if (baseDisk.getLocation().equals(extendedDisk.getLocation()))
                    {
                        // Update Base disk ID and remove it from extendedDiskList
                        baseDisk.setId(extendedDisk.getId());
                        it.remove();
                    }

                }
            }
        }
    }

    /**
     * Check if a disk is an external disk.
     * 
     * @param virtualDisk The disk to check.
     * @return Boolean indicating if the disk is an external disk.
     */
    private static boolean isExternalDisk(final VirtualDisk virtualDisk)
    {
        return !virtualDisk.getLocation().startsWith("[");
    }

    private String getRepositoryManagerAddress(final EnvelopeType envelope)
    {
        // All files will contain the same value, so get the value from the first one
        QName repositoryManagerPath = new QName("repositoryManager");
        List<FileType> files = envelope.getReferences().getFile();
        FileType file = files.get(0);

        return file.getOtherAttributes().get(repositoryManagerPath);
    }

    private void addBootstrapConfiguration(final VirtualMachineConfiguration virtualConfig,
        final EnvelopeType envelope) throws SectionException
    {
        AbicloudNetworkType abiquoNetwork =
            OVFEnvelopeUtils.getSection(envelope, AbicloudNetworkType.class);

        for (OrgNetworkType network : abiquoNetwork.getNetworks())
        {
            List<IpPoolType> rules = network.getConfiguration().getDhcpService().getStaticRules();
            for (IpPoolType rule : rules)
            {
                // There is only one rule with the configure gateway flag in ALL rules from ALL
                // networks
                if (rule.isConfigureGateway())
                {
                    if (rule.getBootstrapConfigURI() != null)
                    {
                        // We assume bootstrapAuth can be null if the bootstrapURI does not require
                        // authentication
                        BootstrapConfiguration bootstrapConfig = new BootstrapConfiguration();
                        bootstrapConfig.setConfigURI(rule.getBootstrapConfigURI());
                        bootstrapConfig.setAuth(rule.getBootstrapConfigAuth());

                        virtualConfig.setBootstrapConfig(bootstrapConfig);
                    }

                    // We're done
                    return;
                }
            }
        }
    }

    @Override
    public HypervisorConfiguration getHypervisorConfigurationFromVirtualSystem(
        final ContentType virtualSystemInstance) throws SectionNotPresentException,
        InvalidSectionException
    {
        VirtualHardwareSectionType hardwareSection =
            OVFEnvelopeUtils.getSection(virtualSystemInstance, VirtualHardwareSectionType.class);

        // Getting the Virtual System type
        VSSDType virtualSystemDataType = hardwareSection.getSystem();
        CimString virtualSystemTypeCimString = virtualSystemDataType.getVirtualSystemType();
        String virtualSystemTypeString = virtualSystemTypeCimString.getValue();

        String adminUser =
            virtualSystemDataType.getOtherAttributes().get(AbicloudConstants.ADMIN_USER_QNAME);
        String adminPassword =
            virtualSystemDataType.getOtherAttributes().get(
                AbicloudConstants.ADMIN_USER_PASSWORD_QNAME);

        // Getting the virtual system address
        CimString virtualSystemIdCimString = virtualSystemDataType.getVirtualSystemIdentifier();
        String virtualSystemAddress = virtualSystemIdCimString.getValue();
        return new HypervisorConfiguration(adminUser,
            adminPassword,
            virtualSystemAddress,
            virtualSystemTypeString);
    }

}
