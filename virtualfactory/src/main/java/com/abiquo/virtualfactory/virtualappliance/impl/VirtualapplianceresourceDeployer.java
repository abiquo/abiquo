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

package com.abiquo.virtualfactory.virtualappliance.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;

import org.apache.commons.lang.StringUtils;
import org.dmtf.schemas.ovf.envelope._1.ContentType;
import org.dmtf.schemas.ovf.envelope._1.EnvelopeType;
import org.dmtf.schemas.ovf.envelope._1.VSSDType;
import org.dmtf.schemas.ovf.envelope._1.VirtualHardwareSectionType;
import org.dmtf.schemas.ovf.envelope._1.VirtualSystemCollectionType;
import org.dmtf.schemas.ovf.envelope._1.VirtualSystemType;
import org.dmtf.schemas.wbem.wscim._1.common.CimString;
import org.dmtf.schemas.wbem.wsman._1.wsman.SelectorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlsoap.schemas.ws._2004._08.addressing.EndpointReferenceType;

import com.abiquo.ovfmanager.ovf.OVFEnvelopeUtils;
import com.abiquo.ovfmanager.ovf.exceptions.EmptyEnvelopeException;
import com.abiquo.ovfmanager.ovf.exceptions.IdAlreadyExistsException;
import com.abiquo.ovfmanager.ovf.exceptions.IdNotFoundException;
import com.abiquo.ovfmanager.ovf.exceptions.RequiredAttributeException;
import com.abiquo.ovfmanager.ovf.exceptions.SectionAlreadyPresentException;
import com.abiquo.ovfmanager.ovf.exceptions.SectionException;
import com.abiquo.ovfmanager.ovf.xml.OVFSerializer;
import com.abiquo.virtualfactory.exception.HypervisorException;
import com.abiquo.virtualfactory.exception.PluginException;
import com.abiquo.virtualfactory.exception.VirtualMachineException;
import com.abiquo.virtualfactory.model.AbsVirtualMachine;
import com.abiquo.virtualfactory.model.State;
import com.abiquo.virtualfactory.model.VirtualAppliance;
import com.abiquo.virtualfactory.model.VirtualApplianceModel;
import com.abiquo.virtualfactory.model.VirtualDisk;
import com.abiquo.virtualfactory.model.VirtualSystemModel;
import com.abiquo.virtualfactory.model.config.HypervisorConfiguration;
import com.abiquo.virtualfactory.model.config.VirtualMachineConfiguration;
import com.abiquo.virtualfactory.model.ovf.OVFModelConvertable;
import com.abiquo.virtualfactory.utils.AbicloudConstants;
import com.abiquo.virtualfactory.virtualappliance.VirtualapplianceresourceDeployable;
import com.sun.ws.management.InternalErrorFault;
import com.sun.ws.management.InvalidSelectorsFault;
import com.sun.ws.management.Management;
import com.sun.ws.management.UnsupportedFeatureFault;
import com.sun.ws.management.framework.Utilities;
import com.sun.ws.management.transfer.InvalidRepresentationFault;
import com.sun.ws.management.transfer.TransferExtensions;

/**
 * Specific implementations of VirtualApplianceResourceDeployer. This class handles the basic
 * operations (CRUD functionality)
 * 
 * @author abiquo
 */
public class VirtualapplianceresourceDeployer implements VirtualapplianceresourceDeployable
{

    private final static Logger logger = LoggerFactory
        .getLogger(VirtualapplianceresourceDeployer.class);

    protected static final QName QNAME_OVF_ENVELOPE =
        new QName("http://schemas.dmtf.org/ovf/envelope/1", "Envelope");

    protected OVFModelConvertable ovfconvert;

    /** Singleton which contains all the virtual appliances already deployed */
    protected VirtualApplianceModel virtualApplianceModel;

    /** Singleton which contains all the virtual machines already deployed */
    protected VirtualSystemModel virtualSystemFactory;

    /**
     * Default constructor
     */
    public VirtualapplianceresourceDeployer()
    {

    }

    /*
     * (non-Javadoc)
     * @seeorg.dmtf.schemas.ovf.envelope._1.virtualapplianceservice.virtualapplianceresource.
     * VirtualapplianceresourceDeployable#create(com.sun.ws.management.Management,
     * com.sun.ws.management.Management)
     */
    public void create(final Management request, final Management response)
    {
        VirtualAppliance virtualAppliance = null;
        try
        {
            TransferExtensions xferResponse = new TransferExtensions(response);

            // Selectors
            HashMap<String, String> selectors = new HashMap<String, String>();

            // Creating the virtual appliance
            try
            {
                EnvelopeType envelope = getEnvelope(request);

                logger.debug("[CREATE CALL] OVF: {}", OVFSerializer.getInstance()
                    .writeXML(envelope));

                ContentType content = OVFEnvelopeUtils.getTopLevelVirtualSystemContent(envelope);

                // Get the virtual application id
                String virtualAppID = content.getId();

                // create the virtualDisks
                Map<String, VirtualDisk> virtualDiskMap = ovfconvert.createVirtualDisks(envelope);

                virtualAppliance =
                    VirtualApplianceModel.getModel().createVirtualAppliance(virtualAppID);

                // create the virtual appliances
                virtualAppliance =
                    ovfconvert.createVirtualAppliance(envelope, virtualAppliance, virtualDiskMap);

                if (virtualAppliance.getState().compareTo(State.NOT_DEPLOYED) == 0)
                {
                    virtualAppliance.setState(State.DEPLOYED);
                }
                logger.info("Virtual Appliance : {} created succesfully",
                    virtualAppliance.getVirtualApplianceId());

            }
            catch (SectionException e)
            {
                throw new Exception("The virtual appliance can not be deployed since it has no virtual images added");
            }
            catch (IdNotFoundException e)
            {
                logger.error(e.getMessage());
            }

            logger.debug("Building the selector ID: {}", virtualAppliance.getVirtualApplianceId());
            selectors.put("id", virtualAppliance.getVirtualApplianceId());
            logger.debug("Building the end point reference");

            EndpointReferenceType epr =
                TransferExtensions.createEndpointReference(request.getTo(),
                    request.getResourceURI(), selectors);

            logger.debug("Building the response");
            xferResponse.setCreateResponse(epr);
        }
        catch (Exception e)
        {
            logger.error("An error is occurred when creating the VA ", e);
            try
            {
                logger.info("Rolling back the virtual appliance: {}",
                    virtualAppliance.getVirtualApplianceId());
                virtualApplianceModel.rollbackVirtualAppliance(virtualAppliance);
            }
            catch (VirtualMachineException e1)
            {
                logger.debug("Impossible to roll back the VA, cause: ", e1);
            }
            throw new InternalErrorFault(e);
        }
    }

    /**
     * Private helper to create an envelope OVF from the virtual appliance
     * 
     * @param virtualAppliance the virtual Appliance
     * @return the envelope
     * @throws RequiredAttributeException
     * @throws IdAlreadyExistsException
     * @throws SectionException
     * @throws SectionAlreadyPresentException
     */
    public EnvelopeType createEnvelopeType(final VirtualAppliance virtualAppliance)
        throws RequiredAttributeException, IdAlreadyExistsException,
        SectionAlreadyPresentException, SectionException
    {
        ContentType vsContent;

        // If there are more than one machine the OVF envelope contains a
        // VirtualSystemCollection
        if (virtualAppliance.getMachines().size() > 1)
        {
            VirtualSystemCollectionType virtualSystemCollection =
                OVFEnvelopeUtils.createVirtualSystemCollection(
                    "collection_" + virtualAppliance.getVirtualApplianceId(),
                    "Collection for a complex VirtualAppliance",
                    "collection to wrap a virtual appliance with more than one machine");

            for (AbsVirtualMachine machine : virtualAppliance.getMachines())
            {
                VirtualSystemType newVirtualSystem = ovfconvert.createVirtualSystem(machine);
                OVFEnvelopeUtils.addVirtualSystem(virtualSystemCollection, newVirtualSystem);
            }

            vsContent = virtualSystemCollection;
        }
        else
        {
            AbsVirtualMachine machine = virtualAppliance.getMachines().iterator().next();

            vsContent = ovfconvert.createVirtualSystem(machine);
        }

        EnvelopeType envelope = new EnvelopeType();

        OVFEnvelopeUtils.addVirtualSystem(envelope, vsContent);

        return envelope;
    }

    /*
     * (non-Javadoc)
     * @seeorg.dmtf.schemas.ovf.envelope._1.virtualapplianceservice.virtualapplianceresource.
     * VirtualapplianceresourceDeployable#delete(com.sun.ws.management.Management,
     * com.sun.ws.management.Management)
     */
    public void delete(final Management request, final Management response)
    {
        try
        {
            // Get the resource passed in the body
            EnvelopeType envelope = getEnvelope(request);

            logger.debug("[DELETE CALL] OVF: {}", OVFSerializer.getInstance().writeXML(envelope));

            try
            {
                deleteVirtualAppliance(envelope);

                TransferExtensions xferResponse = new TransferExtensions(response);

                xferResponse.setDeleteResponse();
                // Adding a body element in the delete response, since the invoke operation in the
                // client expects a body in the response
                xferResponse.getBody().addBodyElement(new QName("DeleteBodyElement"));
            }
            catch (SOAPException e)
            {
                throw new InternalErrorFault(e);
            }
        }
        catch (Exception e)
        {
            logger.error("An error occurred when deleting a virtual appliance: {}", e);
            throw new InternalErrorFault(e);
        }
    }

    protected void deleteVirtualAppliance(final EnvelopeType envelope)
        throws EmptyEnvelopeException, IdNotFoundException, SectionException,
        MalformedURLException, VirtualMachineException, PluginException, HypervisorException
    {
        ContentType contentInstance = OVFEnvelopeUtils.getTopLevelVirtualSystemContent(envelope);

        if (contentInstance instanceof VirtualSystemType)
        {
            String msg =
                "An attempt was made to delete a virtual system. A virtual system collection is expected";
            logger.info(msg);
            throw new InvalidSelectorsFault(InvalidSelectorsFault.Detail.TYPE_MISMATCH);
        }
        else if (contentInstance instanceof VirtualSystemCollectionType)
        {
            // Changing the state of the VirtualSystems contained in a VirtualSystemCollection
            for (ContentType subVirtualSystem : OVFEnvelopeUtils
                .getVirtualSystemsFromCollection((VirtualSystemCollectionType) contentInstance))
            {
                if (subVirtualSystem instanceof VirtualSystemType)
                {
                    String virtualSystemId = subVirtualSystem.getId();
                    logger.info("Checking the virtual Machine: " + virtualSystemId);

                    // create the virtualDisks
                    Map<String, VirtualDisk> virtualDiskMap =
                        ovfconvert.createVirtualDisks(envelope);
                    AbsVirtualMachine virtualMachineNew =
                        virtualSystemFactory.getMachine(ovfconvert
                            .getHypervisorConfigurationFromVirtualSystem(subVirtualSystem),
                            ovfconvert.getVirtualMachineConfigurationFromVirtualSystem(
                                (VirtualSystemType) subVirtualSystem, virtualDiskMap, envelope));

                    // If the machine does not exist, includes all the new machines
                    if (virtualMachineNew != null)
                    {
                        virtualMachineNew.deleteMachine();
                    }

                }// a virtual system
                else if (subVirtualSystem instanceof VirtualSystemCollectionType)
                {
                    logger
                        .error("Recursive deleting a virtual system collection inside a collection ");

                }// a virtual system collection

            } // for each sub virtual system on the collection
        }

    }

    /**
     * Private helper to delete just the virtual system collection that is passed in the body. The
     * others virtual systems will be recovered
     * 
     * @param contentInstance
     * @param envelope
     * @param id
     * @throws VirtualMachineException
     * @throws SectionException
     * @throws IdNotFoundException
     * @throws MalformedURLException
     * @throws HypervisorException
     * @throws PluginException
     */
    private void bundleVirtualSystemCollection(final VirtualSystemCollectionType contentInstance,
        final EnvelopeType envelope) throws IdNotFoundException, SectionException,
        VirtualMachineException, MalformedURLException, PluginException, HypervisorException
    {
        logger.info("Bundlelling the virtual system collection: {}", contentInstance.getId());
        ovfconvert.bundleVirtualSystemCollection(contentInstance, envelope);
    }

    /*
     * (non-Javadoc)
     * @seeorg.dmtf.schemas.ovf.envelope._1.virtualapplianceservice.virtualapplianceresource.
     * VirtualapplianceresourceDeployable#get(com.sun.ws.management.Management,
     * com.sun.ws.management.Management)
     */
    public void get(final Management request, final Management response)
    {
        String id = getIdSelector(request);

        logger.debug("GET ::: " + id);
        String msg = "The GET operation is not fully implemented";
        logger.warn(msg);

        throw new InternalErrorFault(msg);

    }

    /**
     * get the ovfconvert
     * 
     * @return the ovfconvert
     */
    public OVFModelConvertable getOvfconvert()
    {
        return ovfconvert;
    }

    /*
     * (non-Javadoc)
     * @seeorg.dmtf.schemas.ovf.envelope._1.virtualapplianceservice.virtualapplianceresource.
     * VirtualapplianceresourceDeployable#put(com.sun.ws.management.Management,
     * com.sun.ws.management.Management)
     */
    public void put(final Management request, final Management response)
    {
        // Use name selector to find the right virtualSystem
        String id = getIdSelector(request);

        AbsVirtualMachine virtualMachine = null;

        logger.info("Changing properties of the Virtual Appliance" + id);
        logger.debug("PUT call of the virtual appliance/virtual machine " + id);

        VirtualAppliance virtualAppliance = null;
        try
        {
            // Get the resource passed in the body
            EnvelopeType envelope = getEnvelope(request);

            logger.debug("[PUT CALL] OVF: {}", OVFSerializer.getInstance().writeXML(envelope));

            ContentType contentInstance =
                OVFEnvelopeUtils.getTopLevelVirtualSystemContent(envelope);

            if (contentInstance instanceof VirtualSystemType)
            {
                try
                {
                    Map<String, VirtualDisk> virtualDiskMap =
                        ovfconvert.createVirtualDisks(envelope);
                    HypervisorConfiguration hvConfig =
                        ovfconvert.getHypervisorConfigurationFromVirtualSystem(contentInstance);
                    VirtualMachineConfiguration vmConfig =
                        ovfconvert.getVirtualMachineConfigurationFromVirtualSystem(
                            (VirtualSystemType) contentInstance, virtualDiskMap, envelope);

                    virtualMachine = VirtualSystemModel.getModel().getMachine(hvConfig, vmConfig);
                    ovfconvert.reconfigureVirtualSystem(virtualMachine, contentInstance, hvConfig);

                }
                catch (Exception e)
                {
                    logger.info("There was an error when updating the Virtual machine" + e);
                    throw new InternalErrorFault(e);
                }
            }
            else if (contentInstance instanceof VirtualSystemCollectionType)
            {
                String VirtualApplianceId = id;

                virtualAppliance =
                    VirtualApplianceModel.getModel().createVirtualAppliance(VirtualApplianceId);

                String vappState =
                    ovfconvert.getVirtualAppState((VirtualSystemCollectionType) contentInstance);

                configureVirtualSystemCollection(vappState,
                    (VirtualSystemCollectionType) contentInstance, virtualAppliance, request,
                    envelope);
            }
            else
            {
                throw new InternalErrorFault("No virtual appliance to update");
            }

        }
        catch (Exception e)
        {
            if (virtualAppliance != null)
            {
                logger.error("An error is occurred when changing properties to the VA:", e);
                try
                {
                    logger.info("Rolling back the virtual appliance: {}",
                        virtualAppliance.getVirtualApplianceId());
                    virtualApplianceModel.rollbackVirtualAppliance(virtualAppliance);
                }
                catch (VirtualMachineException e1)
                {
                    logger.debug("Impossible to roll back the VA: ", e1);
                    throw new InternalErrorFault(e);
                }
            }
            throw new InternalErrorFault(e);
        }

    }

    public void bundleVirtualAppliance(final Management request, final Management response)
    {
        try
        {
            // Get the resource passed in the body
            EnvelopeType envelope = getEnvelope(request);

            logger.debug("[BUNDLE CALL] OVF: {}", OVFSerializer.getInstance().writeXML(envelope));

            // If an envelope with a virtual appliance is received then it needs to be bundled
            if (envelope != null)
            {
                ContentType contentInstance =
                    OVFEnvelopeUtils.getTopLevelVirtualSystemContent(envelope);

                if (contentInstance instanceof VirtualSystemType)
                {
                    String msg =
                        "An attempt was made to bundle a virtual system. A virtual system collection is expected";
                    logger.info(msg);
                    throw new InvalidSelectorsFault(InvalidSelectorsFault.Detail.TYPE_MISMATCH);
                }
                else if (contentInstance instanceof VirtualSystemCollectionType)
                {
                    // In the envelope will find the virtual systems chosen to be bundled
                    bundleVirtualSystemCollection((VirtualSystemCollectionType) contentInstance,
                        envelope);
                }
            }

            TransferExtensions xferResponse = new TransferExtensions(response);
            xferResponse.setDeleteResponse();
            // Adding a body element in the delete response, since the invoke operation in the
            // client expects a body in the response
            xferResponse.getBody().addBodyElement(new QName("BundleVirtualApplianceResponse"));
        }
        catch (Exception e)
        {
            logger.debug("An error occurred when bundlelling a virtual machine: {}", e);
            throw new InternalErrorFault(e);
        }
    }

    @Override
    public void checkVirtualSystem(final Management request, final Management response)
    {
        // Get the resource passed in the body
        EnvelopeType envelope = getEnvelope(request);

        logger.debug("[CHECK VIRTUAL SYSTEM CALL] OVF: {}",
            OVFSerializer.getInstance().writeXML(envelope));

        try
        {
            ContentType contentInstance =
                OVFEnvelopeUtils.getTopLevelVirtualSystemContent(envelope);
            // Changing the state of the VirtualSystems contained in a VirtualSystemCollection
            if (contentInstance instanceof VirtualSystemCollectionType)
            {
                for (ContentType subVirtualSystem : OVFEnvelopeUtils
                    .getVirtualSystemsFromCollection((VirtualSystemCollectionType) contentInstance))
                {
                    VirtualHardwareSectionType hardwareSection =
                        OVFEnvelopeUtils.getSection(subVirtualSystem,
                            VirtualHardwareSectionType.class);

                    // Getting the Virtual System type
                    VSSDType virtualSystemDataType = hardwareSection.getSystem();
                    CimString virtualSystemTypeCimString =
                        virtualSystemDataType.getVirtualSystemType();
                    String virtualSystemTypeString = virtualSystemTypeCimString.getValue();
                    logger.debug("The VirtualSystemTypeString is :" + virtualSystemTypeString);

                    String adminUser =
                        virtualSystemDataType.getOtherAttributes().get(
                            AbicloudConstants.ADMIN_USER_QNAME);
                    String adminPassword =
                        virtualSystemDataType.getOtherAttributes().get(
                            AbicloudConstants.ADMIN_USER_PASSWORD_QNAME);

                    // Getting the virtual system address
                    CimString virtualSystemIdCimString =
                        virtualSystemDataType.getVirtualSystemIdentifier();
                    String virtualSystemAddress = virtualSystemIdCimString.getValue();
                    logger.debug("The VirtualSystem Address is :" + virtualSystemAddress);

                    Map<String, VirtualDisk> virtualDiskMap =
                        ovfconvert.createVirtualDisks(envelope);
                    VirtualMachineConfiguration config =
                        ovfconvert.getVirtualMachineConfigurationFromVirtualSystem(
                            (VirtualSystemType) subVirtualSystem, virtualDiskMap, envelope);
                    AbsVirtualMachine vmachineTocheck =
                        VirtualSystemModel.getModel().createVirtualMachine(virtualSystemTypeString,
                            new URL(virtualSystemAddress), config, adminUser, adminPassword);
                    // Tries to deploy the virtual machine if it doesn't exist and fails is that the
                    // NOT_MANAGED virtual machine was deleted
                    vmachineTocheck.deployMachine();

                }

            }
            TransferExtensions xferResponse = new TransferExtensions(response);
            xferResponse.setDeleteResponse();
            // Adding a body element in the delete response, since the invoke operation in the
            // client expects a body in the response
            xferResponse.getBody().addBodyElement(new QName("CheckVirtualSystemHealthBodyElement"));
        }
        catch (Exception e)
        {
            throw new InternalErrorFault(e);
        }

        // Creating Check health response

    }

    @Override
    public void addVirtualSystem(final Management request, final Management response)
    {
        // Get the resource passed in the body
        EnvelopeType envelope = getEnvelope(request);

        logger.debug("[ADD VIRTUAL SYSTEM CALL] OVF: {}",
            OVFSerializer.getInstance().writeXML(envelope));

        try
        {
            ContentType contentInstance =
                OVFEnvelopeUtils.getTopLevelVirtualSystemContent(envelope);
            // Changing the state of the VirtualSystems contained in a VirtualSystemCollection
            if (contentInstance instanceof VirtualSystemCollectionType)
            {
                for (ContentType subVirtualSystem : OVFEnvelopeUtils
                    .getVirtualSystemsFromCollection((VirtualSystemCollectionType) contentInstance))
                {
                    VirtualHardwareSectionType hardwareSection =
                        OVFEnvelopeUtils.getSection(subVirtualSystem,
                            VirtualHardwareSectionType.class);

                    // Getting the Virtual System type
                    VSSDType virtualSystemDataType = hardwareSection.getSystem();
                    CimString virtualSystemTypeCimString =
                        virtualSystemDataType.getVirtualSystemType();
                    String virtualSystemTypeString = virtualSystemTypeCimString.getValue();
                    logger.debug("The VirtualSystemTypeString is :" + virtualSystemTypeString);

                    String adminUser =
                        virtualSystemDataType.getOtherAttributes().get(
                            AbicloudConstants.ADMIN_USER_QNAME);
                    String adminPassword =
                        virtualSystemDataType.getOtherAttributes().get(
                            AbicloudConstants.ADMIN_USER_PASSWORD_QNAME);

                    // Getting the virtual system address
                    CimString virtualSystemIdCimString =
                        virtualSystemDataType.getVirtualSystemIdentifier();
                    String virtualSystemAddress = virtualSystemIdCimString.getValue();
                    logger.debug("The VirtualSystem Address is :" + virtualSystemAddress);

                    Map<String, VirtualDisk> virtualDiskMap =
                        ovfconvert.createVirtualDisks(envelope);
                    VirtualMachineConfiguration config =
                        ovfconvert.getVirtualMachineConfigurationFromVirtualSystem(
                            (VirtualSystemType) subVirtualSystem, virtualDiskMap, envelope);
                    AbsVirtualMachine vmachineTocheck =
                        VirtualSystemModel.getModel().createVirtualMachine(virtualSystemTypeString,
                            new URL(virtualSystemAddress), config, adminUser, adminPassword);
                    vmachineTocheck.powerOnMachine();
                }

            }
            TransferExtensions xferResponse = new TransferExtensions(response);
            xferResponse.setDeleteResponse();
            // Adding a body element in the delete response, since the invoke operation in the
            // client expects a body in the response
            xferResponse.getBody().addBodyElement(new QName("AddVirtualSystemBodyElement"));
        }
        catch (Exception e)
        {
            throw new InternalErrorFault(e);
        }

    }

    @Override
    public void removeVirtualSystem(final Management request, final Management response)
    {
        // Get the resource passed in the body
        EnvelopeType envelope = getEnvelope(request);

        logger.debug("[REMOVE VIRTUAL SYSTEM CALL] OVF: {}",
            OVFSerializer.getInstance().writeXML(envelope));

        try
        {
            ContentType contentInstance =
                OVFEnvelopeUtils.getTopLevelVirtualSystemContent(envelope);
            // Changing the state of the VirtualSystems contained in a VirtualSystemCollection
            if (contentInstance instanceof VirtualSystemType)
            {
                VirtualHardwareSectionType hardwareSection =
                    OVFEnvelopeUtils.getSection(contentInstance, VirtualHardwareSectionType.class);

                // Getting the Virtual System type
                VSSDType virtualSystemDataType = hardwareSection.getSystem();
                CimString virtualSystemTypeCimString = virtualSystemDataType.getVirtualSystemType();
                String virtualSystemTypeString = virtualSystemTypeCimString.getValue();
                logger.debug("The VirtualSystemTypeString is :" + virtualSystemTypeString);

                String adminUser =
                    virtualSystemDataType.getOtherAttributes().get(
                        AbicloudConstants.ADMIN_USER_QNAME);
                String adminPassword =
                    virtualSystemDataType.getOtherAttributes().get(
                        AbicloudConstants.ADMIN_USER_PASSWORD_QNAME);

                // Getting the virtual system address
                CimString virtualSystemIdCimString =
                    virtualSystemDataType.getVirtualSystemIdentifier();
                String virtualSystemAddress = virtualSystemIdCimString.getValue();
                logger.debug("The VirtualSystem Address is :" + virtualSystemAddress);

                Map<String, VirtualDisk> virtualDiskMap = ovfconvert.createVirtualDisks(envelope);
                VirtualMachineConfiguration config =
                    ovfconvert.getVirtualMachineConfigurationFromVirtualSystem(
                        (VirtualSystemType) contentInstance, virtualDiskMap, envelope);
                AbsVirtualMachine vmachineToRemove =
                    VirtualSystemModel.getModel().createVirtualMachine(virtualSystemTypeString,
                        new URL(virtualSystemAddress), config, adminUser, adminPassword);
                vmachineToRemove.powerOffMachine();
                vmachineToRemove.deleteMachine();

            }
            TransferExtensions xferResponse = new TransferExtensions(response);
            xferResponse.setDeleteResponse();
            // Adding a body element in the delete response, since the invoke operation in the
            // client expects a body in the response
            xferResponse.getBody().addBodyElement(new QName("RemoveVirtualSystemBodyElement"));
        }
        catch (Exception e)
        {
            logger.error("An error was occurred when removing the virtual machine: ", e);
            throw new InternalErrorFault(e);
        }

    }

    /**
     * Set the ovfconvert
     * 
     * @param ovfconvert
     */
    public void setOvfconvert(final OVFModelConvertable ovfconvert)
    {
        this.ovfconvert = ovfconvert;
    }

    /**
     * reconfigure all the virtual systems inside the virtual system collection (also if collection
     * contains collections: RECURSIVE)
     * 
     * @param virtualAppliance
     * @param request
     * @param envelope
     * @throws Exception
     */
    protected void configureVirtualSystemCollection(final String vappState,
        final VirtualSystemCollectionType vscollection, final VirtualAppliance virtualAppliance,
        final Management request, final EnvelopeType envelope) throws Exception
    {
        // Setting the future state to avoid rolling back when powering off the virtual appliance
        virtualAppliance.setState(State.fromValue(vappState));

        // Changing the state of the VirtualSystems contained in a VirtualSystemCollection
        
        List<VirtualSystemType> virtualSystems = OVFEnvelopeUtils.getVirtualSystems(vscollection);        
            //XXX OVFEnvelopeUtils.getVirtualSystemsFromCollection(vscollection);
        
        Collections.sort(virtualSystems, new ContentTypeSequence());
        
        for (ContentType subVirtualSystem : virtualSystems)
        {
            if (subVirtualSystem instanceof VirtualSystemType)
            {
                String virtualSystemId = subVirtualSystem.getId();
                logger.info("Checking the virtual Machine: " + virtualSystemId);

                // create the virtualDisks
                Map<String, VirtualDisk> virtualDiskMap = ovfconvert.createVirtualDisks(envelope);
                HypervisorConfiguration hvConfig =
                    ovfconvert.getHypervisorConfigurationFromVirtualSystem(subVirtualSystem);
                VirtualMachineConfiguration vmConfig =
                    ovfconvert.getVirtualMachineConfigurationFromVirtualSystem(
                        (VirtualSystemType) subVirtualSystem, virtualDiskMap, envelope);

                AbsVirtualMachine virtualMachineNew =
                    virtualSystemFactory.getMachine(hvConfig, vmConfig);

                // If the machine does not exist, includes all the new machines
                if (virtualMachineNew == null)
                {
                    // If there are new machines the virtuap app is not completely deployed
                    logger.info("Detected a new virtual machine: {} to virtual appliance ",
                        virtualSystemId);

                    updateVirtualAppliance(virtualAppliance, request);

                    // Get the updated machine
                    virtualMachineNew =
                        virtualSystemFactory.getMachine(ovfconvert
                            .getHypervisorConfigurationFromVirtualSystem(subVirtualSystem),
                            ovfconvert.getVirtualMachineConfigurationFromVirtualSystem(
                                (VirtualSystemType) subVirtualSystem, virtualDiskMap, envelope));
                }

                virtualAppliance.getMachines().add(virtualMachineNew);

                try
                {
                    // using virtual system configuration
                    ovfconvert.reconfigureVirtualSystem(virtualMachineNew, subVirtualSystem,
                        hvConfig);

                    // TODO updateFromAnnotations
                    // TODO assure not only the annotation section can be accessed
                    // TODO assure the virtual machines can be reconfigurated
                }
                catch (SectionException se)
                {
                    logger
                        .warn(
                            "Required some section on the virtulal system reconfiguration not found, will use the virtual system collection to get its sections ",
                            se);

                    try
                    {
                        ovfconvert.reconfigureVirtualSystem(virtualMachineNew, vscollection,
                            hvConfig);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        throw new VirtualMachineException("Virtual appliance can not be updated (even using the virtual system collection sections), machine "
                            + virtualSystemId);
                    }
                }
                catch (Exception e)
                {
                    logger.error("An error was occurred when configuring the virtual machine: "
                        + virtualSystemId + " Exception: ", e);
                    throw e;
                }
            }// a virtual system
            else if (subVirtualSystem instanceof VirtualSystemCollectionType)
            {
                logger
                    .warn("Recursive reconfigurating a virtual system collection inside a collection ");

                configureVirtualSystemCollection(vappState,
                    (VirtualSystemCollectionType) subVirtualSystem, virtualAppliance, request,
                    envelope);
            }// a virtual system collection
            else
            {
                logger.error("Invalid ContentType inside a virtual system collection, its a "
                    + subVirtualSystem.getClass().getCanonicalName());
            }

        } // for each sub virtual system on the collection

    }// configure virtual system collection

    class ContentTypeSequence implements Comparator<ContentType>
    {
        @Override
        public int compare(final ContentType arg0, final ContentType arg1)
        {
            String info0 =
                arg0.getInfo() == null || StringUtils.isEmpty(arg0.getInfo().getValue())
                    ? "default" : arg0.getInfo().getValue();

            String info1 =
                arg1.getInfo() == null || StringUtils.isEmpty(arg1.getInfo().getValue())
                    ? "default" : arg1.getInfo().getValue();

            return String.CASE_INSENSITIVE_ORDER.compare(info0, info1);
        }
    }

    /**
     * Gets the envelope for a given request
     * 
     * @param request wiseman's client request
     * @return OVF's EnvelopeType
     */
    protected EnvelopeType getEnvelope(final Management request)
    {
        JAXBElement<EnvelopeType> resource = getResource(request);
        if (resource == null)
        {
            return null;
        }
        else
        {
            return resource.getValue();
        }
    }

    /**
     * Private helper to the get the selector from the request
     * 
     * @param request the request
     * @return returns the id
     * @throws InternalErrorFault
     */
    protected String getIdSelector(final Management request) throws InternalErrorFault
    {
        Set<SelectorType> selectors;
        try
        {
            selectors = request.getSelectors();
        }
        catch (JAXBException e)
        {
            throw new InternalErrorFault(e);
        }
        catch (SOAPException e)
        {
            throw new InternalErrorFault(e);
        }
        if (Utilities.getSelectorByName("id", selectors) == null)
        {
            throw new InvalidSelectorsFault(InvalidSelectorsFault.Detail.INSUFFICIENT_SELECTORS);
        }
        return (String) Utilities.getSelectorByName("id", selectors).getContent().get(0);
    }

    /**
     * Private helper to get the Resource from the request
     * 
     * @param request the request to get the resource from
     * @return the resource
     */
    @SuppressWarnings("unchecked")
    // the JAXBElement declaredType is checked
    protected JAXBElement<EnvelopeType> getResource(final Management request)
    {
        JAXBElement<EnvelopeType> envelopeElement = null;

        try
        {
            // Get JAXB Representation of Soap Body property document
            TransferExtensions transfer = new TransferExtensions(request);
            Object element = transfer.getResource(QNAME_OVF_ENVELOPE);
            if (element == null)
            {
                return null;
                // throw new
                // InvalidRepresentationFault(InvalidRepresentationFault.Detail.MISSING_VALUES);
            }
            if (element instanceof JAXBElement)
            {
                JAXBElement< ? > jaxElem = (JAXBElement< ? >) element;

                if (EnvelopeType.class.equals(jaxElem.getDeclaredType()))
                {
                    envelopeElement = (JAXBElement<EnvelopeType>) jaxElem;
                }
                else
                {
                    // XmlFragment only supported on Get
                    throw new UnsupportedFeatureFault(UnsupportedFeatureFault.Detail.FRAGMENT_LEVEL_ACCESS);
                }
            }
            else
            {
                throw new InvalidRepresentationFault(InvalidRepresentationFault.Detail.INVALID_VALUES);
            }
        }
        catch (SOAPException e)
        {
            throw new InternalErrorFault(e);
        }
        catch (JAXBException e)
        {
            throw new InternalErrorFault(e);
        }
        return envelopeElement;
    }

    /**
     * Private helper to update a virtual appliance with the new virtual machines from the request
     * 
     * @param virtualAppliance the virtual appliance to update
     * @param request the request
     * @throws SOAPException
     * @throws JAXBException
     * @throws MalformedURLException
     * @throws VirtualMachineException
     * @throws IdNotFoundException
     * @throws SectionException
     * @throws EmptyEnvelopeException
     * @throws RequiredAttributeException
     * @throws HypervisorException
     * @throws PluginException
     */
    protected void updateVirtualAppliance(final VirtualAppliance virtualAppliance,
        final Management request) throws SOAPException, JAXBException, MalformedURLException,
        VirtualMachineException, IdNotFoundException, SectionException, EmptyEnvelopeException,
        RequiredAttributeException, PluginException, HypervisorException
    {
        TransferExtensions xferRequest = new TransferExtensions(request);
        Object element = xferRequest.getResource(QNAME_OVF_ENVELOPE);

        Map<String, VirtualDisk> virtualDiskMap;

        if (element == null)
        {
            throw new SOAPException("Null element inside the TransferExtension");
        }

        logger.info("Checking Envelope object");
        EnvelopeType envelope = getEnvelope(request);

        virtualDiskMap = ovfconvert.createVirtualDisks(envelope);

        // Checking content element
        ContentType contentInstance = OVFEnvelopeUtils.getTopLevelVirtualSystemContent(envelope);

        if (contentInstance instanceof VirtualSystemType)
        {
            ovfconvert.addMachinesToVirtualAppliance(virtualAppliance,
                (VirtualSystemType) contentInstance, virtualDiskMap, envelope);

        }// a virtual system
        else if (contentInstance instanceof VirtualSystemCollectionType)
        {
            ovfconvert.updateVirtualSystemCollection((VirtualSystemCollectionType) contentInstance,
                virtualAppliance, virtualDiskMap, envelope);
        }
        else
        {
            logger.error("Invalid content type on the envelope, its a "
                + contentInstance.getClass().getCanonicalName());
        }

    }

    /**
     * @return the virtualApplianceModel
     */
    public VirtualApplianceModel getVirtualApplianceModel()
    {
        return virtualApplianceModel;
    }

    /**
     * @param virtualApplianceModel the virtualApplianceModel to set
     */
    public void setVirtualApplianceModel(final VirtualApplianceModel virtualApplianceModel)
    {
        this.virtualApplianceModel = virtualApplianceModel;
    }

    /**
     * @return the virtualSystemFactory
     */
    public VirtualSystemModel getVirtualSystemFactory()
    {
        return virtualSystemFactory;
    }

    /**
     * @param virtualSystemFactory the virtualSystemFactory to set
     */
    public void setVirtualSystemFactory(final VirtualSystemModel virtualSystemFactory)
    {
        this.virtualSystemFactory = virtualSystemFactory;
    }
}
