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

package com.abiquo.abiserver.abicloudws;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPException;

import org.dmtf.schemas.ovf.envelope._1.EnvelopeType;
import org.dmtf.schemas.wbem.wsman._1.wsman.SelectorSetType;
import org.dmtf.schemas.wbem.wsman._1.wsman.SelectorType;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualappHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualhardware.ResourceAllocationSettingData;
import com.abiquo.abiserver.config.AbiConfig;
import com.abiquo.abiserver.config.AbiConfigManager;
import com.abiquo.abiserver.exception.PersistenceException;
import com.abiquo.abiserver.exception.RemoteServiceException;
import com.abiquo.abiserver.exception.VirtualApplianceFaultException;
import com.abiquo.abiserver.exception.VirtualFactoryHealthException;
import com.abiquo.abiserver.model.ovf.OVFModelFactory;
import com.abiquo.abiserver.persistence.hibernate.HibernateUtil;
import com.abiquo.abiserver.pojo.infrastructure.DataCenter;
import com.abiquo.abiserver.pojo.infrastructure.HyperVisor;
import com.abiquo.abiserver.pojo.infrastructure.PhysicalMachine;
import com.abiquo.abiserver.pojo.infrastructure.Rack;
import com.abiquo.abiserver.pojo.infrastructure.VirtualMachine;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.service.RemoteService;
import com.abiquo.abiserver.pojo.virtualappliance.VirtualAppliance;
import com.abiquo.ovfmanager.ovf.xml.OVFSerializer;
import com.abiquo.tracer.ComponentType;
import com.abiquo.tracer.Datacenter;
import com.abiquo.tracer.EventType;
import com.abiquo.tracer.Machine;
import com.abiquo.tracer.Platform;
import com.abiquo.tracer.SeverityType;
import com.abiquo.tracer.client.TracerFactory;
import com.abiquo.util.ErrorManager;
import com.abiquo.util.resources.ResourceManager;
import com.sun.ws.management.client.Resource;
import com.sun.ws.management.client.ResourceFactory;
import com.sun.ws.management.client.exceptions.FaultException;

/**
 * This class connects Infrastructure Command with AbiCloud Web Services
 * 
 * @author Oliver
 */
public class InfrastructureWS implements IInfrastructureWS
{
    private final static String IDVIRTUALAPP_SQL_BY_VM = "SELECT n.idVirtualApp "
        + "FROM node n, nodevirtualimage ni " + "WHERE n.idNode = ni.idNode and ni.idVM = :id";

    /** The logger object */
    private final static Logger logger = LoggerFactory.getLogger(InfrastructureWS.class);

    // private final static Logger logger = LoggerFactory.getLogger(InfrastructureWS.class);

    private final org.dmtf.schemas.wbem.wsman._1.wsman.ObjectFactory managementFactory =
        new org.dmtf.schemas.wbem.wsman._1.wsman.ObjectFactory();

    private final static OVFSerializer ovfSerializer = OVFSerializer.getInstance();

    private final AbiConfig abiConfig = AbiConfigManager.getInstance().getAbiConfig();

    static final ResourceManager resourceManager = new ResourceManager(InfrastructureWS.class);

    private final ErrorManager errorManager = ErrorManager
        .getInstance(AbiCloudConstants.ERROR_PREFIX);

    private static Integer bugTimeout;

    static
    {

        try
        {
            bugTimeout =
                Integer.valueOf(System.getProperty("abiquo.virtualfactory.sleepTimeout", "10000"));
        }
        catch (Exception e)
        {
            bugTimeout = 10000;
        }

        System.setProperty("wink.client.connectTimeout", String.valueOf(0));
        System.setProperty("wink.client.readTimeout", String.valueOf(0));
    }

    @Override
    public BasicResult setVirtualMachineState(final VirtualMachine virtualMachine,
        final String actionState) throws Exception
    {
        return setVirtualMachineState(virtualMachine, actionState, null);
    }

    /**
     * Update the VM configuration without changing the VM state.
     * 
     * @param virtualMachine The VM to update
     * @param additionalRasds The additionsl resources to consider
     * @return
     * @throws Exception
     */
    @Override
    public BasicResult updateVirtualMachineConfiguration(final VirtualMachine virtualMachine,
        final List<ResourceAllocationSettingData> additionalRasds) throws Exception
    {
        return setVirtualMachineState(virtualMachine, null, additionalRasds);
    }

    private BasicResult setVirtualMachineState(final VirtualMachine virtualMachine,
        final String actionState, final List<ResourceAllocationSettingData> additionalRasds)
        throws Exception
    {
        BasicResult result = new BasicResult();
        try
        {
            logger.info("Checking Virtual System before the VM operation");
            Boolean checkResult = checkVirtualSystem(virtualMachine);
            result.setSuccess(checkResult);

            if (checkResult)
            {
                Document doc = changeMachineState(virtualMachine, actionState, additionalRasds);
                Resource resource = findResource(virtualMachine);
                if (resource != null)
                {
                    result.setSuccess(true);
                }
                else
                {
                    errorManager.reportError(InfrastructureWS.resourceManager, result,
                        "resourceNotFound", virtualMachine.getName());
                }

                Thread.sleep(bugTimeout);

                resource.put(doc);
            }
            else
            {
                throw new VirtualFactoryHealthException("The virtual machine did not pass the health check.");
            }

        }
        catch (FaultException e)
        {
            encapsulateAndRethrowFault(virtualMachine, e, actionState);
        }
        return result;
    }

    /**
     * Creates a virtual machine in the target hypervisor
     * 
     * @param virtualMachine the virtual machine to create
     * @deprecated
     * @return a basic result
     */
    @Deprecated
    private BasicResult createVirtualMachine(final VirtualMachine virtualMachine)
    {
        BasicResult result = null;
        try
        {
            result = new BasicResult();
            // HyperVisor hypervisor = (HyperVisor) virtualMachine.getAssignedTo();
            Document envelope = createEnvelopeDocument(virtualMachine);
            Resource resource =
                ResourceFactory.create(getDestinationFromVM(virtualMachine),
                    AbiCloudConstants.RESOURCE_URI, abiConfig.getTimeout(), envelope,
                    ResourceFactory.LATEST);
            if (resource != null)
            {
                result.setSuccess(true);
            }
            else
            {
                errorManager.reportError(InfrastructureWS.resourceManager, result,
                    "resourceNotFound", virtualMachine.getName());
            }
        }
        catch (Exception e)
        {
            errorManager
                .reportError(InfrastructureWS.resourceManager, result, "operationFailed", e);
        }
        return result;
    }

    /**
     * Deletes the virtual machine
     * 
     * @param virtualMachine the virtual machine to delete
     * @return a basic result
     */
    @Override
    public BasicResult deleteVirtualMachine(final VirtualMachine virtualMachine)
    {
        BasicResult result = null;
        try
        {
            result = new BasicResult();
            Resource resource = findResource(virtualMachine);
            if (resource != null)
            {
                result.setSuccess(true);
            }
            else
            {
                errorManager.reportError(InfrastructureWS.resourceManager, result,
                    "resourceNotFound", virtualMachine.getName());
            }
            resource.delete();
        }
        catch (Exception e)
        {
            errorManager
                .reportError(InfrastructureWS.resourceManager, result, "operationFailed", e);
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.abicloudws.IInfrastructureWS#editVirtualMachine(com.abiquo.abiserver
     * .pojo.infrastructure.VirtualMachine)
     */
    @Override
    public BasicResult editVirtualMachine(final VirtualMachine virtualMachine)
    {
        BasicResult result = new BasicResult();
        try
        {
            logger.info("Checking Virtual machine before the VM operation");
            Boolean checkResult = checkVirtualSystem(virtualMachine);
            result.setSuccess(checkResult);

            if (checkResult)
            {
                Document doc = createEnvelopeDocument(virtualMachine); // TODO prev updateVS
                Resource resource = findResource(virtualMachine);
                if (resource != null)
                {
                    result.setSuccess(true);
                }
                else
                {
                    errorManager.reportError(InfrastructureWS.resourceManager, result,
                        "resourceNotFound", virtualMachine.getName());
                }
                resource.put(doc);
            }

        }
        catch (Exception e)
        {
            errorManager
                .reportError(InfrastructureWS.resourceManager, result, "operationFailed", e);

        }
        return result;
    }

    /**
     * Private helper to create a document with the OVF envelope. This envelope contains the
     * information with the virtualMachine creation
     * 
     * @param virtualMachine the virtual machine to get the parameters from
     * @return the Document with the new machine values
     * @throws JAXBException
     * @throws ParserConfigurationException, if the virtual machien can not be mapped to a OVF
     *             envelope document.
     */
    private Document createEnvelopeDocument(final VirtualMachine virtualMachine)
        throws JAXBException, ParserConfigurationException
    {
        EnvelopeType envelope;

        try
        {
            // Creates an OVF envelope from the virtual machine parameters
            // [ABICLOUDPREMIUM-1491] When editing a VM we do not perform a state change
            envelope =
                OVFModelFactory.createOVFModelFromVirtualAppliance().constructEnvelopeType(
                    virtualMachine, null, null);
            // OVFModelFactory.createOVFModelFromVirtualAppliance().getActualState(
            // virtualMachine), null);

            // envelope =
            // OVFModelFactory.createOVFModelFromVirtualAppliance().constructEnvelopeType(
            // virtualMachine,
            // OVFModelFactory.createOVFModelFromVirtualAppliance().getActualState(
            // virtualMachine), null);
        }
        catch (Exception e)
        {
            throw new ParserConfigurationException(e.toString());
        }

        // Updates the changed parameters, preparing for submision
        Document doc = ovfSerializer.bindToDocument(envelope, false); // TODO not namespaceaware

        return doc;
    }

    /**
     * Private helper to create a document with the information to perform the machine state change.
     * 
     * @param virtualMachine the virtualMachine
     * @param machineState the machine State
     * @return the document to submit
     * @throws Exception
     */
    private Document changeMachineState(final VirtualMachine virtualMachine,
        final String machineState, final List<ResourceAllocationSettingData> additionalRasds)
        throws Exception
    {

        EnvelopeType envelope =
            OVFModelFactory.createOVFModelFromVirtualAppliance().changeMachineState(virtualMachine,
                machineState, additionalRasds);

        Document doc = ovfSerializer.bindToDocument(envelope, false); // TODO not namespaceaware

        return doc;
    }

    /**
     * Private helper to create a selector id with the virtual machine name
     * 
     * @param machineName
     * @return
     */
    private SelectorSetType createSelectorId(final String machineName)
    {
        // Creating a selector passing as the id the machine name
        SelectorType nameSelectorType = managementFactory.createSelectorType();
        nameSelectorType.setName("id");
        nameSelectorType.getContent().add(machineName);
        SelectorSetType selector = new SelectorSetType();
        selector.getSelector().add(nameSelectorType);
        return selector;
    }

    /**
     * Private helper to find a resource through the virtualMachine name
     * 
     * @param virtualMachine the virtualMachine to find the resource from
     * @return the resource found
     * @throws SOAPException
     * @throws JAXBException
     * @throws IOException
     * @throws FaultException
     * @throws DatatypeConfigurationException
     * @throws PersistenceException
     */
    Resource findResource(final VirtualMachine virtualMachine) throws SOAPException, JAXBException,
        IOException, FaultException, DatatypeConfigurationException, PersistenceException
    {
        // Creating a selector passing as the id the machine name
        SelectorSetType selector = createSelectorId(virtualMachine.getUUID());
        String destination = getDestinationFromVM(virtualMachine);
        Resource[] resources =
            ResourceFactory.find(destination, AbiCloudConstants.RESOURCE_URI,
                abiConfig.getTimeout(), selector);
        Resource resource = resources[0];
        return resource;

    }

    /**
     * Private helper to get the virtual factory destination address from the virtual machine object
     * 
     * @param virtualMachine the virtual machine
     * @return the address destination
     * @throws PersistenceException
     */
    private String getDestinationFromVM(final VirtualMachine virtualMachine)
        throws PersistenceException
    {
        String destination = null;

        HyperVisor hypervisor = (HyperVisor) virtualMachine.getAssignedTo();
        PhysicalMachine physicalMachine = (PhysicalMachine) hypervisor.getAssignedTo();
        Rack rack = (Rack) physicalMachine.getAssignedTo();
        DataCenter dataCenter = rack.getDataCenter();
        ArrayList<RemoteService> remoteServices = dataCenter.getRemoteServices();
        for (RemoteService remoteService : remoteServices)
        {
            if (com.abiquo.abiserver.business.hibernate.pojohb.service.RemoteServiceType
                .valueOf(remoteService.getRemoteServiceType().getValueOf()) == com.abiquo.abiserver.business.hibernate.pojohb.service.RemoteServiceType.VIRTUAL_FACTORY)
            {
                destination = remoteService.getUri();
                break;
            }
        }
        return destination;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.abicloudws.IInfrastructureWS#forceRefreshVirtualMachineState(com.abiquo
     * .abiserver.pojo.infrastructure.VirtualMachine)
     */
    @Override
    public BasicResult forceRefreshVirtualMachineState(final VirtualMachine virtualMachine)
    {
        logger.info("Forcing refresh of the virtual machine state: {}", virtualMachine.getId());
        BasicResult result = new BasicResult();
        VirtualappHB virtualappHBPojo = null;
        Session session = null;
        Transaction transaction = null;
        try
        {
            session = HibernateUtil.getSession();
            transaction = session.beginTransaction();
            Query query = session.createSQLQuery(IDVIRTUALAPP_SQL_BY_VM);
            query.setString("id", virtualMachine.getId().toString());
            Integer virtualApplianceId = (Integer) query.uniqueResult();
            virtualappHBPojo =
                (VirtualappHB) session.get("VirtualappExtendedHB", virtualApplianceId);
            VirtualAppliance vapp = virtualappHBPojo.toPojo();
            String virtualSystemMonitorAddress =
                RemoteServiceUtils.getVirtualSystemMonitorFromVA(vapp);
            // EventingSupport.subscribePullEventToVM(virtualMachine, virtualSystemMonitorAddress);
        }
        catch (PersistenceException e)
        {
            logger.trace("An error occurred when retrieving the VirtualSystemMonitor",
                e.getStackTrace()[0]);
        }
        catch (RemoteServiceException e)
        {
            logger.trace("An error occurred when contacting the VirtualSystemMonitor",
                e.getStackTrace()[0]);
        }

        result.setSuccess(true);
        return result;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.abicloudws.IInfrastructureWS#checkVirtualSystem(com.abiquo.abiserver
     * .pojo.infrastructure.VirtualMachine)
     */
    @Override
    public Boolean checkVirtualSystem(final VirtualMachine virtualMachine)
    {
        return true;
    }

    /**
     * Encapsulates and rethrows a Fault exception.
     * <p>
     * This method parses {@link FaultException} to provide human-readable information about the
     * failure.
     * 
     * @param vm The virtual machine
     * @param ex the exception to encapsulate.
     * @param event The event being handled.
     * @param message the message to append
     * @throws VirtualApplianceFaultException The encapsulated exception.
     */
    private void encapsulateAndRethrowFault(final VirtualMachine vm, final FaultException ex,
        final EventType event, final String message) throws VirtualApplianceFaultException
    {
        String exceptionMessage = null;
        try
        {
            // Try to find the original exception details
            BufferedReader br = new BufferedReader(new StringReader(ex.getMessage()));
            String line = br.readLine();
            while (line != null)
            {
                int detailIndex = line.indexOf("Detail:");
                if (detailIndex != -1)
                {
                    int detailBegin = line.lastIndexOf("Exception:");
                    if (detailBegin != -1)
                    {
                        detailBegin += "Exception:".length();
                    }
                    else
                    {
                        detailBegin = detailIndex + "Detail:".length();
                    }

                    exceptionMessage = line.substring(detailBegin + 1, line.length());

                    break;
                }

                line = br.readLine();
            }
        }
        catch (IOException ioEx)
        {
            // Do nothing. Will log the original message.
        }

        if (exceptionMessage == null)
        {
            exceptionMessage = ex.getMessage();
        }

        String logMessage =
            message == null ? exceptionMessage : message + "(Caused by: " + exceptionMessage + ")";

        traceLog(vm, event, logMessage);

        // Rethrow encapsulated exception
        throw new VirtualApplianceFaultException(logMessage, ex);
    }

    /**
     * Encapsulates and rethrows a Fault exception.
     * <p>
     * This method parses {@link FaultException} to provide human-readable information about the
     * failure.
     * 
     * @param vm The virtual machine
     * @param ex the exception to encapsulate.
     * @param event The event being handled.
     * @throws VirtualApplianceFaultException The encapsulated exception.
     */
    private void encapsulateAndRethrowFault(final VirtualMachine vm, final FaultException ex,
        final String actionState) throws VirtualApplianceFaultException
    {
        encapsulateAndRethrowFault(vm, ex, translateActionToEvent(actionState), null);
    }

    /**
     * Traces a log to tracer.
     * 
     * @param vm the virtual machine information
     * @param event The event to trace.
     * @param message The message to trace.
     */
    private void traceLog(final VirtualMachine vm, final EventType event, final String message)
    {
        HyperVisor hv = (HyperVisor) vm.getAssignedTo();
        PhysicalMachine pm = (PhysicalMachine) hv.getAssignedTo();
        Rack rack = (Rack) pm.getAssignedTo();

        // Physical Machine information

        Machine machine = Machine.machine(pm.getName());

        com.abiquo.tracer.Rack tracerRack = com.abiquo.tracer.Rack.rack(rack.getName());
        tracerRack.setMachine(machine);

        Datacenter datacenter = Datacenter.datacenter(pm.getDataCenter().getName());
        datacenter.setRack(tracerRack);

        Platform platform = Platform.SYSTEM_PLATFORM;
        platform.setDatacenter(datacenter);

        // Log to tracer the original message
        TracerFactory.getTracer().log(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE, event,
            message, platform);
    }

    /**
     * Translate actions to VM to events to trace properly
     * 
     * @param actionState the VM action to translate
     * @return the trace Event
     */
    private EventType translateActionToEvent(final String actionState)
    {
        if (actionState == AbiCloudConstants.POWERUP_ACTION)
        {
            return EventType.VM_POWERON;
        }
        else if (actionState == AbiCloudConstants.POWERDOWN_ACTION)
        {
            return EventType.VM_POWEROFF;
        }
        else if (actionState == AbiCloudConstants.PAUSE_ACTION)
        {
            return EventType.VM_PAUSED;
        }
        else if (actionState == AbiCloudConstants.RESUME_ACTION)
        {
            return EventType.VM_RESUMED;
        }
        return null;

    }

    /**
     * @deprecated No longer used
     */
    @Override
    @Deprecated
    public BasicResult addVirtualSystem(final VirtualMachine virtualMachine) throws SOAPException,
        JAXBException, IOException, FaultException, DatatypeConfigurationException,
        ParserConfigurationException
    {
        BasicResult result = new BasicResult();
        Document doc = createEnvelopeDocument(virtualMachine);
        Resource resource = findResource(virtualMachine);
        if (resource != null)
        {
            result.setSuccess(true);
        }
        else
        {
            errorManager.reportError(InfrastructureWS.resourceManager, result, "resourceNotFound",
                virtualMachine.getName());
        }
        resource.invoke(AbiCloudConstants.ADD_VIRTUALSYSTEM_ACTION, doc);
        return result;
    }

    @Override
    public BasicResult removeVirtualSystem(final VirtualMachine virtualMachine)
        throws JAXBException, ParserConfigurationException, PersistenceException, SOAPException,
        IOException, FaultException, DatatypeConfigurationException
    {
        BasicResult result = new BasicResult();
        result.setSuccess(true);

        Document doc = createEnvelopeDocument(virtualMachine); // TODO prev updateVS
        Resource resource = findResource(virtualMachine);
        if (resource != null)
        {
            result.setSuccess(true);
        }
        else
        {
            errorManager.reportError(InfrastructureWS.resourceManager, result, "resourceNotFound",
                virtualMachine.getName());
        }
        resource.invoke(AbiCloudConstants.REMOVE_VIRTUALSYSTEM_ACTION, doc);
        return result;
    }
}
