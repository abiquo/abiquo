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
import java.util.Collection;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPException;

import org.dmtf.schemas.ovf.envelope._1.EnvelopeType;
import org.dmtf.schemas.wbem.wsman._1.wsman.SelectorSetType;
import org.dmtf.schemas.wbem.wsman._1.wsman.SelectorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.StateEnum;
import com.abiquo.abiserver.config.AbiConfig;
import com.abiquo.abiserver.config.AbiConfigManager;
import com.abiquo.abiserver.eventing.EventingException;
import com.abiquo.abiserver.eventing.EventingSupport;
import com.abiquo.abiserver.exception.PersistenceException;
import com.abiquo.abiserver.exception.RemoteServiceException;
import com.abiquo.abiserver.exception.VirtualApplianceFaultException;
import com.abiquo.abiserver.exception.VirtualFactoryHealthException;
import com.abiquo.abiserver.model.ovf.OVFModelFactory;
import com.abiquo.abiserver.pojo.infrastructure.HyperVisor;
import com.abiquo.abiserver.pojo.infrastructure.PhysicalMachine;
import com.abiquo.abiserver.pojo.infrastructure.Rack;
import com.abiquo.abiserver.pojo.infrastructure.State;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.virtualappliance.Node;
import com.abiquo.abiserver.pojo.virtualappliance.NodeVirtualImage;
import com.abiquo.abiserver.pojo.virtualappliance.VirtualAppliance;
import com.abiquo.ovfmanager.ovf.exceptions.EmptyEnvelopeException;
import com.abiquo.ovfmanager.ovf.exceptions.SectionException;
import com.abiquo.ovfmanager.ovf.xml.OVFSerializer;
import com.abiquo.tracer.ComponentType;
import com.abiquo.tracer.Datacenter;
import com.abiquo.tracer.EventType;
import com.abiquo.tracer.Machine;
import com.abiquo.tracer.Platform;
import com.abiquo.tracer.SeverityType;
import com.abiquo.tracer.VirtualDatacenter;
import com.abiquo.tracer.client.TracerFactory;
import com.abiquo.util.ErrorManager;
import com.abiquo.util.resources.ResourceManager;
import com.sun.ws.management.client.Resource;
import com.sun.ws.management.client.ResourceFactory;
import com.sun.ws.management.client.exceptions.FaultException;
import com.sun.ws.management.transfer.Transfer;

/**
 * This class connects Virtual Appliance Command with AbiCloud Web Services
 * 
 * @author pnavarro
 */
public class VirtualApplianceWS implements IVirtualApplianceWS
{

    private final org.dmtf.schemas.wbem.wsman._1.wsman.ObjectFactory managementFactory =
        new org.dmtf.schemas.wbem.wsman._1.wsman.ObjectFactory();

    private final AbiConfig abiConfig = AbiConfigManager.getInstance().getAbiConfig();

    private static final ResourceManager resourceManager =
        new ResourceManager(VirtualApplianceWS.class);

    private final ErrorManager errorManager = ErrorManager
        .getInstance(AbiCloudConstants.ERROR_PREFIX);

    IInfrastructureWS infrastructureWS;

    /** The logger object */
    // private final static Logger logger =
    // LoggerFactory.getLogger(VirtualApplianceWS.class);
    private final static OVFSerializer ovfSerializer = OVFSerializer.getInstance();

    /** The logger object */
    final static Logger logger = LoggerFactory.getLogger(VirtualApplianceWS.class);

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

    public VirtualApplianceWS()
    {
        try
        {
            infrastructureWS =
                (IInfrastructureWS) Thread.currentThread().getContextClassLoader()
                    .loadClass("com.abiquo.abiserver.abicloudws.InfrastructureWSPremium")
                    .newInstance();
        }
        catch (Exception e)
        {
            infrastructureWS = new InfrastructureWS();
        }
    }

    /*
     * (non-Javadoc)
     * @see com.abiquo.abiserver.abicloudws.IVirtualApplianceWS#startVirtualAppliance
     * (com.abiquo.abiserver .pojo.virtualappliance.VirtualAppliance)
     */
    @Override
    public BasicResult startVirtualAppliance(final VirtualAppliance virtualAppliance)
        throws Exception
    {
        Resource resource;

        BasicResult result = new BasicResult(); // TODO throw Exception on
                                                // reportError

        try
        {
            EnvelopeType envelope =
                OVFModelFactory.createOVFModelFromVirtualAppliance().createVirtualApplication(
                    virtualAppliance);

            Document docEnvelope = ovfSerializer.bindToDocument(envelope, false); // TODO
            // notnamespaceaware

            String virtualSystemMonitor =
                RemoteServiceUtils.getVirtualSystemMonitorFromVA(virtualAppliance);

            if (virtualAppliance.getState().toEnum() == StateEnum.NOT_DEPLOYED)
            {

                String destination = RemoteServiceUtils.getVirtualFactoryFromVA(virtualAppliance);
                long timeout = abiConfig.getTimeout();

                Thread.sleep(bugTimeout);

                resource =
                    ResourceFactory.create(destination, AbiCloudConstants.RESOURCE_URI, timeout,
                        docEnvelope, ResourceFactory.LATEST);
            }
            else
            {
                resource = findResource(virtualAppliance);
            }

            if (resource != null)
            {
                // Subscribing to the virtual appliance states
                // Decomment this to test subscribing to all the events
                EventingSupport.subscribeToAllVA(virtualAppliance, virtualSystemMonitor);

                // Starting the virtual Appliance Changing the virtualSystems to
                // running
                result = changeState(resource, envelope, AbiCloudConstants.POWERUP_ACTION);
            }
            else
            {
                errorManager.reportError(VirtualApplianceWS.resourceManager, result,
                    "resourceNotFound", virtualAppliance.getName());
            }
        }
        catch (FaultException e)
        {
            encapsulateAndRethrowFault(virtualAppliance, e, EventType.VAPP_POWERON);
        }
        catch (RemoteServiceException e)
        {
            errorManager.reportError(VirtualApplianceWS.resourceManager, result,
                "virtualFactoryError", e, virtualAppliance.getName());
        }
        catch (IOException e)
        {
            errorManager.reportError(VirtualApplianceWS.resourceManager, result,
                "virtualFactoryError", e, virtualAppliance.getName());
        }

        return result;
    }

    /**
     * Performs a "Create" action in the Virtual Appliances
     * 
     * @param virtualAppliance
     * @param mustChangeState
     * @return a BasicResult
     * @deprecated
     * @throws Exception
     */
    @Deprecated
    BasicResult forceCreateVirtualAppliance(final VirtualAppliance virtualAppliance,
        final boolean mustChangeState) throws Exception
    {
        Resource resource;

        BasicResult result = null;

        try
        {
            EnvelopeType envelope =
                OVFModelFactory.createOVFModelFromVirtualAppliance().createVirtualApplication(
                    virtualAppliance);

            Document docEnvelope = ovfSerializer.bindToDocument(envelope, false); // TODO
            // notnamespaceaware

            String destination = RemoteServiceUtils.getVirtualFactoryFromVA(virtualAppliance);
            String virtualSystemMonitorAddress =
                RemoteServiceUtils.getVirtualSystemMonitorFromVA(virtualAppliance);
            long timeout = abiConfig.getTimeout();

            resource =
                ResourceFactory.create(destination, AbiCloudConstants.RESOURCE_URI, timeout,
                    docEnvelope, ResourceFactory.LATEST);

            result = new BasicResult();
            result.setSuccess(true);

            if (resource != null)
            {
                // Subscribing to the virtual appliance states
                // Decomment this to test subscribing to all the events
                EventingSupport.subscribeToAllVA(virtualAppliance, virtualSystemMonitorAddress);
                // Starting the virtual Appliance Changing the virtualSystems to
                // running
                if (mustChangeState)
                {
                    result = changeState(resource, envelope, virtualAppliance);
                }
            }
            else
            {
                errorManager.reportError(VirtualApplianceWS.resourceManager, result,
                    "resourceNotFound", virtualAppliance.getName());
            }
        }
        catch (FaultException e)
        {
            encapsulateAndRethrowFault(virtualAppliance, e, EventType.VAPP_REFRESH,
                "A problem was found when checking the virtual appliance state in the hypervisor");
        }
        catch (RemoteServiceException e)
        {
            errorManager.reportError(VirtualApplianceWS.resourceManager, result,
                "virtualFactoryError", e, virtualAppliance.getName());
        }
        catch (IOException e)
        {
            errorManager.reportError(VirtualApplianceWS.resourceManager, result,
                "virtualFactoryError", e, virtualAppliance.getName());
        }

        return result;
    }

    /**
     * Private helper to change the resource state
     * 
     * @param resource
     * @param envelope
     * @param machineState
     * @return
     * @throws ParserConfigurationException
     * @throws JAXBException
     * @throws SOAPException
     * @throws IOException
     * @throws FaultException
     * @throws DatatypeConfigurationException
     * @throws EmptyEnvelopeException
     * @throws SectionException
     */
    private BasicResult changeState(final Resource resource, final EnvelopeType envelope,
        final String machineState) throws ParserConfigurationException, JAXBException,
        SOAPException, IOException, FaultException, DatatypeConfigurationException,
        EmptyEnvelopeException, SectionException
    {
        BasicResult result = new BasicResult();

        EnvelopeType envelopeRunning =
            OVFModelFactory.createOVFModelFromVirtualAppliance().changeStateVirtualMachine(
                envelope, machineState);
        Document docEnvelopeRunning = ovfSerializer.bindToDocument(envelopeRunning, false); // TODO
        // notnamespaceaware

        resource.put(docEnvelopeRunning);
        result.setSuccess(true);

        return result;
    }

    /**
     * Private helper to change the resource state
     * 
     * @param resource
     * @param envelope
     * @param machineState
     * @return
     * @throws ParserConfigurationException
     * @throws JAXBException
     * @throws SOAPException
     * @throws IOException
     * @throws FaultException
     * @throws DatatypeConfigurationException
     * @throws EmptyEnvelopeException
     * @throws SectionException
     */
    private BasicResult changeState(final Resource resource, final EnvelopeType envelope,
        final VirtualAppliance virtualapp) throws ParserConfigurationException, JAXBException,
        SOAPException, IOException, FaultException, DatatypeConfigurationException,
        EmptyEnvelopeException, SectionException
    {
        BasicResult result = new BasicResult();

        EnvelopeType envelopeRunning =
            OVFModelFactory.createOVFModelFromVirtualAppliance().changeVirtualMachineStates(
                envelope, virtualapp);
        Document docEnvelopeRunning = ovfSerializer.bindToDocument(envelopeRunning, false); // TODO
        // notnamespaceaware

        resource.put(docEnvelopeRunning);
        result.setSuccess(true);

        return result;
    }

    /*
     * (non-Javadoc)
     * @see com.abiquo.abiserver.abicloudws.IVirtualApplianceWS#shutdownVirtualAppliance
     * (com.abiquo.abiserver .pojo.virtualappliance.VirtualAppliance, boolean)
     */
    @Override
    public BasicResult shutdownVirtualAppliance(final VirtualAppliance virtualAppliance)
        throws Exception
    {
        BasicResult result = null;
        result = new BasicResult();

        try
        {
            EnvelopeType envelope =
                OVFModelFactory.createOVFModelFromVirtualAppliance().createVirtualApplication(
                    virtualAppliance);

            logger.info("Checking the Virtual Appliance");

            Boolean resultCheck = checkVirtualAppliance(virtualAppliance);
            result.setSuccess(resultCheck);

            if (resultCheck)
            {
                Resource resource = findResource(virtualAppliance);

                if (resource != null)
                {
                    // Unsubscribing to every state in every Virtual appliance
                    // VM
                    // //Decomment this to test unsubscribing to all the events
                    result = changeState(resource, envelope, AbiCloudConstants.POWERDOWN_ACTION);
                    EventingSupport.unsubscribeToAllVA(virtualAppliance);
                }
                else
                {
                    errorManager.reportError(VirtualApplianceWS.resourceManager, result,
                        "resourceNotFound", virtualAppliance.getName());
                }
            }
            else
            {
                result.setMessage(virtualAppliance.getName() + ": Operation cannot be performed.");
                return result;
            }

        }
        catch (FaultException e)
        {
            encapsulateAndRethrowFault(virtualAppliance, e, EventType.VAPP_POWEROFF);
        }
        catch (RemoteServiceException e)
        {
            errorManager.reportError(VirtualApplianceWS.resourceManager, result,
                "virtualFactoryError", e, virtualAppliance.getName());
        }
        catch (IOException e)
        {
            errorManager.reportError(VirtualApplianceWS.resourceManager, result,
                "virtualFactoryError", e, virtualAppliance.getName());
        }

        return result;
    }

    @Override
    public BasicResult removeNodes(final List<Node> nodesToDelete) throws PersistenceException,
        JAXBException, ParserConfigurationException, SOAPException, IOException, FaultException,
        DatatypeConfigurationException, VirtualFactoryHealthException
    {
        BasicResult basicResult = new BasicResult();
        basicResult.setSuccess(true);

        Boolean checkOK = true;

        for (Node nodeToDelete : nodesToDelete)
        {
            if (nodeToDelete.isNodeTypeVirtualImage())
            {
                NodeVirtualImage nodeVi = (NodeVirtualImage) nodeToDelete;
                StateEnum vmState = nodeVi.getVirtualMachine().getState().toEnum();
                BasicResult removeResult =
                    infrastructureWS.removeVirtualSystem(nodeVi.getVirtualMachine());
                if (!removeResult.getSuccess())
                {
                    checkOK = false;
                }
            }
        }

        if (!checkOK)
        {
            throw new VirtualFactoryHealthException("The virtual machine health did not pass");
        }

        return basicResult;

    }

    /*
     * (non-Javadoc)
     * @see com.abiquo.abiserver.abicloudws.IVirtualApplianceWS#deleteVirtualAppliance
     * (com.abiquo.abiserver .pojo.virtualappliance.VirtualAppliance)
     */
    @Override
    public BasicResult deleteVirtualAppliance(final VirtualAppliance virtualAppliance)
        throws Exception
    {

        BasicResult result = new BasicResult();

        EnvelopeType envelope =
            OVFModelFactory.createOVFModelFromVirtualAppliance().createVirtualApplication(
                virtualAppliance, true);

        Document docEnvelope = ovfSerializer.bindToDocument(envelope, false);

        try
        {
            Resource resource = findResource(virtualAppliance);

            if (resource != null)
            {
                result.setSuccess(true);
                resource.invoke(Transfer.DELETE_ACTION_URI, docEnvelope);
            }
            else
            {
                errorManager.reportError(VirtualApplianceWS.resourceManager, result,
                    "resourceNotFound", virtualAppliance.getName());
            }
        }
        catch (FaultException e)
        {
            encapsulateAndRethrowFault(virtualAppliance, e, EventType.VAPP_INSTANCE);
        }
        catch (RemoteServiceException e)
        {
            errorManager.reportError(VirtualApplianceWS.resourceManager, result,
                "virtualFactoryError", e, virtualAppliance.getName());
        }
        catch (IOException e)
        {
            errorManager.reportError(VirtualApplianceWS.resourceManager, result,
                "virtualFactoryError", e, virtualAppliance.getName());
        }

        return result;

    }

    /**
     * Private helper to find a resource through the virtualAppliance name
     * 
     * @param virtualAppliance the virtualAppliance to find the resource from
     * @return the resource found
     * @throws SOAPException
     * @throws JAXBException
     * @throws IOException
     * @throws FaultException
     * @throws DatatypeConfigurationException
     * @throws PersistenceException
     * @throws RemoteServiceException
     */
    private Resource findResource(final VirtualAppliance virtualAppliance) throws SOAPException,
        JAXBException, IOException, FaultException, DatatypeConfigurationException,
        PersistenceException, RemoteServiceException
    {
        /**
         * TODO duplicated on InfrastructureWS
         */
        // Creating a selector passing as the id the machine name
        SelectorSetType selector = createSelectorId(String.valueOf(virtualAppliance.getId()));
        Resource[] resources =
            ResourceFactory.find(RemoteServiceUtils.getVirtualFactoryFromVA(virtualAppliance),
                AbiCloudConstants.RESOURCE_URI, abiConfig.getTimeout(), selector);
        Resource resource = resources[0];
        return resource;

    }

    /**
     * Private helper to create a selector id with the virtual application name
     * 
     * @param virtualApplianceName
     * @return
     */
    private SelectorSetType createSelectorId(final String virtualApplianceName)
    {
        /**
         * TODO duplicated on InfrastructureWS ??
         */

        // Creating a selector passing as the id the machine name
        SelectorType nameSelectorType = managementFactory.createSelectorType();
        nameSelectorType.setName("id");
        nameSelectorType.getContent().add(virtualApplianceName);
        SelectorSetType selector = new SelectorSetType();
        selector.getSelector().add(nameSelectorType);
        return selector;
    }

    /*
     * (non-Javadoc)
     * @see com.abiquo.abiserver.abicloudws.IVirtualApplianceWS#editVirtualAppliance
     * (com.abiquo.abiserver .pojo.virtualappliance.VirtualAppliance)
     */
    @Override
    public BasicResult editVirtualAppliance(final VirtualAppliance virtualAppliance)
        throws Exception
    {
        BasicResult result = new BasicResult();
        result.setSuccess(true);
        Collection<Node> nodesOld = virtualAppliance.getNodes();
        Collection<Node> nodesNew = new ArrayList<Node>();
        try
        {
            if (virtualAppliance.getState().toEnum() == StateEnum.APPLY_CHANGES_NEEDED)
            {
                Collection<Node> nodes = virtualAppliance.getNodes();

                for (Node node : nodes)
                {
                    if (node.isNodeTypeVirtualImage())
                    {
                        NodeVirtualImage nvi = (NodeVirtualImage) node;
                        if (nvi.getVirtualMachine().getState().toEnum()
                            .compareTo(StateEnum.IN_PROGRESS) == 0)
                        {
                            nodesNew.add(node);
                        }
                    }
                }

                virtualAppliance.setNodes(nodesNew);

                // When there are no nodes left, its no need to update
                // subscription.
                if (virtualAppliance.getNodes().size() > 0)
                {
                    virtualAppliance.setState(new State(StateEnum.NOT_DEPLOYED));
                    startVirtualAppliance(virtualAppliance);
                }
                virtualAppliance.setNodes(nodesOld);
                result.setSuccess(true);
            }
        }
        catch (FaultException e)
        {
            encapsulateAndRethrowFault(virtualAppliance, e, EventType.VAPP_MODIFY);
        }
        catch (RemoteServiceException e)
        {
            errorManager.reportError(VirtualApplianceWS.resourceManager, result,
                "virtualFactoryError", e, virtualAppliance.getName());
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * @see com.abiquo.abiserver.abicloudws.IVirtualApplianceWS#checkVirtualAppliance
     * (com.abiquo.abiserver .pojo.virtualappliance.VirtualAppliance, boolean)
     */
    @Override
    public Boolean checkVirtualAppliance(final VirtualAppliance virtualAppliance) throws Exception
    {
        logger.info("Checking the Virtual Appliance health in community version is not done");

        return true;
    }

    @Override
    public Boolean checkRemovedNodes(final VirtualAppliance virtualAppliance)
        throws VirtualFactoryHealthException
    {
        Collection<Node> nodes = virtualAppliance.getNodes();
        Boolean checkTotalResult = true;
        for (Node node : nodes)
        {
            if (node.isNodeTypeVirtualImage())
            {
                NodeVirtualImage nodeVi = (NodeVirtualImage) node;
                StateEnum vmState = nodeVi.getVirtualMachine().getState().toEnum();
                // The nodes crashed will not be rechecked
                if (nodeVi.getModified() == Node.NODE_ERASED)
                {
                    logger.info("Checking Virtual machine before the VM operation");
                    Boolean checkResult =
                        infrastructureWS.checkVirtualSystem(nodeVi.getVirtualMachine());
                    if (!checkResult)
                    {
                        checkTotalResult = false;
                    }
                }
            }
        }
        if (!checkTotalResult)
        {
            throw new VirtualFactoryHealthException("The nodes removed don't pass the health check");
        }

        return true;
    }

    /**
     * Invokes a pull subscribe to the virtual system monitor to forces the events refreshing
     * 
     * @param virtualAppliance the virtual appliance to be refreshed
     * @return a basicResult with the resulting operation
     */
    @Override
    public BasicResult forceRefreshVirtualApplianceState(final VirtualAppliance virtualAppliance)
    {
        logger.info("Refreshing the virtual appliance state: {}", virtualAppliance.getId());
        BasicResult result = new BasicResult();
        try
        {
            EventingSupport.subscribePullToAllVA(virtualAppliance);
        }
        catch (EventingException e)
        {
            logger
                .warn(
                    "An error was occurred when invokin a pulling subscribing to recover the VA events: {}",
                    e);
        }

        result.setSuccess(true);
        return result;
    }

    /**
     * Roll backs the event subscription to the virtual appliance
     * 
     * @param virtualAppliance
     */
    @Override
    public void rollbackEventSubscription(final VirtualAppliance virtualAppliance)
    {
        EventingSupport.unsubscribeToAllVA(virtualAppliance);
    }

    /*
     * (non-Javadoc)
     * @see com.abiquo.abiserver.abicloudws.IVirtualApplianceWS#bundleVirtualAppliance
     * (com.abiquo.abiserver .pojo.virtualappliance.VirtualAppliance)
     */
    @Override
    public BasicResult bundleVirtualAppliance(final VirtualAppliance virtualAppliance)
        throws Exception
    {
        BasicResult result = new BasicResult();

        try
        {
            logger.info("Checking the Virtual Appliance");

            Boolean resultCheck = checkVirtualAppliance(virtualAppliance);
            result.setSuccess(resultCheck);

            if (resultCheck)
            {
                EnvelopeType envelope =
                    OVFModelFactory.createOVFModelFromVirtualAppliance().createVirtualApplication(
                        virtualAppliance, true);

                Document docEnvelope = ovfSerializer.bindToDocument(envelope, false);
                Resource resource = findResource(virtualAppliance);

                if (resource != null)
                {
                    result.setSuccess(true);

                    Thread.sleep(bugTimeout);

                    resource.invoke(AbiCloudConstants.BUNDLE_VIRTUALAPPLIANCE, docEnvelope);
                }
                else
                {
                    errorManager.reportError(VirtualApplianceWS.resourceManager, result,
                        "resourceNotFound", virtualAppliance.getName());
                }
            }
            else
            {
                result.setMessage(virtualAppliance.getName() + ": Operation cannot be performed.");
                return result;
            }
        }
        catch (FaultException e)
        {
            encapsulateAndRethrowFault(virtualAppliance, e, EventType.VAPP_INSTANCE);
        }

        return result;
    }

    /**
     * Encapsulates and rethrows a Fault exception.
     * <p>
     * This method parses {@link FaultException} to provide human-readable information about the
     * failure.
     * 
     * @param va The Virtual Appliance data.
     * @param ex the exception to encapsulate.
     * @param event The event being handled.
     * @param message the message to append
     * @throws VirtualApplianceFaultException The encapsulated exception.
     */
    private void encapsulateAndRethrowFault(final VirtualAppliance va, final FaultException ex,
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

        // Log to tracer the original message
        traceLog(va, event, logMessage);

        // Rethrow encapsulated exception
        throw new VirtualApplianceFaultException(logMessage, ex);
    }

    /**
     * Encapsulates and rethrows a Fault exception.
     * <p>
     * This method parses {@link FaultException} to provide human-readable information about the
     * failure.
     * 
     * @param va The Virtual Appliance data.
     * @param ex the exception to encapsulate.
     * @param event The event being handled.
     * @throws VirtualApplianceFaultException The encapsulated exception.
     */
    private void encapsulateAndRethrowFault(final VirtualAppliance va, final FaultException ex,
        final EventType event) throws VirtualApplianceFaultException
    {
        encapsulateAndRethrowFault(va, ex, event, null);
    }

    /**
     * Traces a log to tracer.
     * 
     * @param va The Virtual Appliance information.
     * @param event The event to trace.
     * @param message The message to trace.
     */
    private void traceLog(final VirtualAppliance va, final EventType event, final String message)
    {
        NodeVirtualImage vi = (NodeVirtualImage) va.getNodes().iterator().next();
        HyperVisor hv = (HyperVisor) vi.getVirtualMachine().getAssignedTo();
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

        // Virtual datacenter Information

        VirtualDatacenter vdc =
            VirtualDatacenter.virtualDatacenter(va.getVirtualDataCenter().getName());
        vdc.setVirtualAppliance(com.abiquo.tracer.VirtualAppliance.virtualAppliance(va.getName()));
        platform.getEnterprise().setVirtualDatacenter(vdc);

        // Log to tracer the original message
        TracerFactory.getTracer().log(SeverityType.CRITICAL, ComponentType.VIRTUAL_APPLIANCE,
            event, message, platform);
    }
}
