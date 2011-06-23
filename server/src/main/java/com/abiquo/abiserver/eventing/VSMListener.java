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

package com.abiquo.abiserver.eventing;

import java.util.Collection;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.DatacenterHB;
import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.HypervisorHB;
import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.RackHB;
import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.StateEnum;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.NodeVirtualImageHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualappHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualmachineHB;
import com.abiquo.abiserver.commands.VirtualApplianceCommand;
import com.abiquo.abiserver.commands.impl.VirtualApplianceCommandImpl;
import com.abiquo.abiserver.persistence.hibernate.HibernateUtil;
import com.abiquo.abiserver.pojo.virtualappliance.NodeVirtualImage;
import com.abiquo.abiserver.pojo.virtualappliance.VirtualAppliance;
import com.abiquo.commons.amqp.impl.vsm.VSMCallback;
import com.abiquo.commons.amqp.impl.vsm.domain.VirtualSystemEvent;
import com.abiquo.tracer.ComponentType;
import com.abiquo.tracer.Datacenter;
import com.abiquo.tracer.Machine;
import com.abiquo.tracer.Platform;
import com.abiquo.tracer.Rack;
import com.abiquo.tracer.SeverityType;
import com.abiquo.tracer.VirtualDatacenter;
import com.abiquo.tracer.VirtualMachine;
import com.abiquo.tracer.client.TracerFactory;
import com.abiquo.vsm.events.VMEventType;

/**
 * Servlet implementation class eventSink. It receives the events from the Virtual System Monitor
 * and updates the database
 */
public class VSMListener implements VSMCallback
{
    private final static Logger logger = LoggerFactory.getLogger(VSMListener.class);

    private static final long serialVersionUID = 1L;

    protected final static String IDVIRTUALAPP_SQL_BY_VM =
        "SELECT n.idVirtualApp " + "FROM node n, nodevirtualimage ni "
            + "WHERE n.idNode = ni.idNode and ni.idVM = :id";

    private final static String VM_BY_UUID =
        "Select vm "
            + "from com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualmachineHB as vm "
            + "where vm.name = :uuid";

    private final static String HY_BY_VM =
        "Select hy "
            + "from com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.HypervisorHB as hy "
            + "inner join hy.virtualmachines as vm " + "where vm.idVm = :idVM";

    private final static String ALL_VM =
        "from com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualmachineHB as vm";

    @Override
    public void onEvent(VirtualSystemEvent event)
    {
        try
        {
            updateEventOnDb(event);
        }
        catch (EventingException e)
        {
            logger.debug("Event not recognized, just ignoring it", e);
        }
        catch (Exception e)
        {
            logger.error("An exception was occurred when receiving an event", e);
        }
    }

    /**
     * Updates the event on database
     * 
     * @param eventType
     * @throws Exception
     */
    private void updateEventOnDb(final VirtualSystemEvent event) throws Exception
    {
        Session session = null;
        Transaction transaction = null;
        try
        {
            session = HibernateUtil.getSession();
            transaction = session.beginTransaction();
            if (logger.isTraceEnabled())
            {
                logger.trace("Showing VM states");
                Query allVmQuery = session.createQuery(ALL_VM);
                List results = allVmQuery.list();
                for (Object object : results)
                {
                    VirtualmachineHB vmHB = (VirtualmachineHB) object;
                    logger.trace("The VM ID is: {}, The VM state is : {}", vmHB.getIdVm(), vmHB
                        .getState());
                }
            }

            Query query = session.createQuery(VM_BY_UUID);
            query.setString("uuid", event.getVirtualSystemId());
            

            // HORRIBLE HACK for ABICLOUDPREMIUM-1846
            VirtualmachineHB virtualMachineAux = (VirtualmachineHB) query.uniqueResult();
            VirtualmachineHB virtualMachine = null;

            if (virtualMachineAux != null
                && (virtualMachineAux.getHypervisor() == null || virtualMachineAux.getHypervisor()
                    .getPhysicalMachine() == null))
            {
                // Sometimes when the event is received, Hibernate session object is incomplete and Hypervisor information is not associated.
                // If this is the case we try to wait and force Hibernate to use another transaction to recover the full info.
                // Hopefully this will be fixed properly by replacing the EventSink by using JPA.
                logger
                    .error("WARNING-> virtualMachineAux.getHypervisor() IS NULL. Forcing Hibernate to restore complete VirtualMachine entity...");

                transaction.commit();

                Thread.sleep(2000);

                Session session2 = HibernateUtil.getSession();

                transaction = session2.beginTransaction();

                Query query2 = session2.createQuery(HY_BY_VM);
                query2.setInteger("idVM", virtualMachineAux.getIdVm());

                HypervisorHB hypervFound = (HypervisorHB) query2.uniqueResult();

                virtualMachine = virtualMachineAux;

                virtualMachine.setHypervisor(hypervFound);

                transaction.commit();

                session = HibernateUtil.getSession();

                transaction = session.beginTransaction();

                logger
                    .error("Hibernate was forced to restore complete VirtualMachine: Hypervisor is now "
                        + virtualMachine.getHypervisor());

            }
            else
            {
                virtualMachine = virtualMachineAux;
            }
            // HORRIBLE HACK - end.

            // We must ignore events coming from PhysicalMachines in 5 - HA_IN_PROGRESS or 6 -
            // DISABLED_FOR_HA states
            if (virtualMachine.getState() == StateEnum.HA_IN_PROGRESS)
            {
                logger
                    .trace(
                        "Ignoring event from VM ID is: {} with VM state : {}, its Physical Machine is currently disabled or in progress by HA process",
                        virtualMachine.getIdVm(), virtualMachine.getState());
                return;
            }

            // Checking if the VM is not null since the VM that we are receiving
            // the event was already deleted
            // If hypervisor is null, the Virtual Machine belongs to an
            // undeployed Virtual Appliance
            // and its state is NOT_DEPLOYED. Must not modify this state.
            if (virtualMachine != null && virtualMachine.getHypervisor() != null)
            {
                VMEventType eventType = VMEventType.valueOf(event.getEventType());

                switch (eventType)
                {
                    case MOVED:
                        // VM must be deleted from system and Vapp Updated
                        onVMDestroyedEvent(session, virtualMachine.getIdVm());
                        // Physicalmachine destination must be updated
                        // VApp is also updated
                        onVMMovedEvent(session, virtualMachine, event.getVirtualSystemAddress(),
                            event.getVirtualSystemType());
                        break;

                    case DESTROYED:
                        // VM must be deleted from system and Vapp Updated
                        onVMDestroyedEvent(session, virtualMachine.getIdVm());
                        break;

                    default:
                        // State has changed
                        virtualMachine.setState(convertEventTypeToState(eventType));
                        session.update(virtualMachine);
                        break;
                }
                transaction.commit();

                // Log the event, depending on the event type
                Platform platform = Platform.SYSTEM_PLATFORM;

                // Set a Datacenter object, with name and rack
                DatacenterHB dcHB =
                    virtualMachine.getHypervisor().getPhysicalMachine().getDataCenter();
                RackHB rackHB = virtualMachine.getHypervisor().getPhysicalMachine().getRack();

                Datacenter dc = Datacenter.datacenter(dcHB.getName());
                Rack rack = new Rack(rackHB.getName());
                VirtualMachine vm = VirtualMachine.virtualMachine(virtualMachine.getName());
                Machine machine = new Machine(rackHB.getName() + "_machine");

                machine.setVirtualMachine(vm);
                rack.setMachine(machine);
                dc.setRack(rack);

                platform.setDatacenter(dc);

                switch (eventType)
                {
                    case POWER_ON:
                        TracerFactory.getTracer().log(SeverityType.INFO,
                            ComponentType.VIRTUAL_MACHINE, com.abiquo.tracer.EventType.VM_POWERON,
                            platform);
                        break;

                    case POWER_OFF:
                        TracerFactory.getTracer().log(SeverityType.INFO,
                            ComponentType.VIRTUAL_MACHINE, com.abiquo.tracer.EventType.VM_POWEROFF,
                            platform);
                        break;

                    case PAUSED:
                        TracerFactory.getTracer().log(SeverityType.INFO,
                            ComponentType.VIRTUAL_MACHINE, com.abiquo.tracer.EventType.VM_PAUSED,
                            platform);
                        break;

                    case RESUMED:
                        TracerFactory.getTracer().log(SeverityType.INFO,
                            ComponentType.VIRTUAL_MACHINE, com.abiquo.tracer.EventType.VM_RESUMED,
                            platform);
                        break;

                    case DESTROYED:
                        TracerFactory.getTracer().log(SeverityType.MAJOR,
                            ComponentType.VIRTUAL_MACHINE, com.abiquo.tracer.EventType.VM_DESTROY,
                            platform);
                        return;

                    case MOVED:
                        TracerFactory.getTracer().log(SeverityType.INFO,
                            ComponentType.VIRTUAL_MACHINE, com.abiquo.tracer.EventType.VM_MOVED,
                            platform);
                        return;

                    default:
                        break;
                }

                checkAndUpdateVirtualAppliance(virtualMachine.getIdVm());
            }
            else
            {
                if (transaction != null && transaction.isActive())
                {
                    transaction.rollback();
                }

                logger.warn("The virtual machine related to the event no longer exists");
            }

        }
        catch (HibernateException e)
        {
            if (transaction != null && transaction.isActive())
            {
                transaction.rollback();
            }
            logger.error("An error was occurred when updating the virtual machine state", e);
        }

    }

    /**
     * Free resources associated to this VM on Node Delete detection. To be overridden at Premium
     * 
     * @param Session
     * @param nviHB
     */
    protected void onDeleteNode(Session session, NodeVirtualImageHB nviHB)
    {
        VirtualApplianceCommand vaCommand = new VirtualApplianceCommandImpl();
        vaCommand.beforeDeletingNode(session, nviHB);
        vaCommand.deleteRasdFromNode(session, nviHB);

    }

    /**
     * Checks the Virtual machine states of the VirtualAppliance where are located and updates the
     * VA state
     * 
     * @param idVm the virtual machine where the state has changed
     */
    private void checkAndUpdateVirtualAppliance(final Integer idVm)
    {
        Session session = null;
        Transaction transaction = null;

        try
        {
            session = HibernateUtil.getSession();
            transaction = session.beginTransaction();
            Query query = session.createSQLQuery(IDVIRTUALAPP_SQL_BY_VM);
            query.setString("id", idVm.toString());
            Integer virtualApplianceId = (Integer) query.uniqueResult();
            VirtualappHB vappHB =
                (VirtualappHB) session.get("VirtualappExtendedHB", virtualApplianceId);
            VirtualAppliance vapp = vappHB.toPojo();
            transaction.commit();

            if (vapp.getState().toEnum() == StateEnum.IN_PROGRESS)
            {
                Collection<com.abiquo.abiserver.pojo.virtualappliance.Node> nodes = vapp.getNodes();
                boolean allVMrunning = true;

                for (com.abiquo.abiserver.pojo.virtualappliance.Node node : nodes)
                {
                    if (node.isNodeTypeVirtualImage())
                    {
                        NodeVirtualImage nodeVi = (NodeVirtualImage) node;
                        if (nodeVi.getVirtualMachine().getState().toEnum() != StateEnum.RUNNING)
                        {
                            logger
                                .debug(
                                    "As a virtual machine state with non running state was detected, the virtual app {} remains IN PROGRESS ",
                                    virtualApplianceId);
                            allVMrunning = false;
                            break;
                        }
                    }
                }

                // Bundle logic handles the state of the virtual appliance
                allVMrunning = allVMrunning && !isBundling(vapp);

                if (allVMrunning)
                {
                    session = HibernateUtil.getSession();
                    transaction = session.beginTransaction();
                    logger
                        .debug(
                            "All the virtual machines were running, changing the state of the virtual app: {} to RUNNING",
                            virtualApplianceId);
                    vappHB = (VirtualappHB) session.get("VirtualappExtendedHB", virtualApplianceId);
                    vappHB.setState(StateEnum.RUNNING);
                    vappHB.setSubState(StateEnum.RUNNING);
                    session.update(vappHB);
                    transaction.commit();

                    // Log the event

                    // Set a virtualdatacenter object, with name and
                    // VirtualAppliance
                    VirtualDatacenter vdc =
                        VirtualDatacenter.virtualDatacenter(vappHB.getVirtualDataCenterHB()
                            .getName());
                    com.abiquo.tracer.VirtualAppliance vApp =
                        com.abiquo.tracer.VirtualAppliance.virtualAppliance(vappHB.getName());
                    vdc.setVirtualAppliance(vApp);

                    Platform platform = Platform.SYSTEM_PLATFORM;
                    platform.getEnterprise().setVirtualDatacenter(vdc);

                    TracerFactory.getTracer().log(SeverityType.INFO,
                        ComponentType.VIRTUAL_APPLIANCE, com.abiquo.tracer.EventType.VAPP_RUNNING,
                        platform);
                }
            }
        }
        catch (HibernateException e)
        {

            if (transaction != null && transaction.isActive())
            {
                transaction.rollback();
            }
            logger.error("An error was occurred when updating the virtual application state", e);
        }
    }

    private boolean isBundling(final VirtualAppliance virtualAppliance)
    {
        return (virtualAppliance.getState().toEnum() == StateEnum.IN_PROGRESS && virtualAppliance
            .getSubState().toEnum() == StateEnum.BUNDLING);
    }

    /**
     * Reacts to a VM movement between Physical Machines
     * 
     * @param session
     * @param vm virtual machine being moved
     * @param destinationPhysicalMachineAddress : physical machine destination address
     * @param destinationPhysicalMachineType : physical machine destination hypervisorType
     */
    protected void onVMMovedEvent(Session session, VirtualmachineHB vm,
        String destinationPhysicalMachineAddress, String destinationPhysicalMachineType)
    {
        // * Implemented as a Enterprise Edition feature

    }

    /**
     * Fires on Virtual Machine Destroyed event detection. Set VM to NOT_DEPLOYED and Delete all
     * references in this Vapp Changes Vapp state to NOT_DEPLOYED or APPLY_CHANGES depending on its
     * included VMachines. Resources are freed.
     * 
     * @param idVm
     */
    protected void onVMDestroyedEvent(final Session session, final Integer idVm)
    {
        // * Implemented as a Enterprise Edition

    }

    /**
     * Converts the Event type from the event to the virtual machine state
     * 
     * @param eventType the event type coming from the event notification
     * @return the state hibernate pojo ready to be inserted into the database
     * @throws EventingException
     */
    private StateEnum convertEventTypeToState(final VMEventType eventType) throws EventingException
    {
        StateEnum state;
        switch (eventType)
        {
            case POWER_OFF:
                state = StateEnum.POWERED_OFF;
                break;

            case POWER_ON:
                state = StateEnum.RUNNING;
                break;

            case PAUSED:
                state = StateEnum.PAUSED;
                break;

            case RESUMED:
                state = StateEnum.RUNNING;
                break;

            case DESTROYED:
                state = StateEnum.NOT_DEPLOYED;
                break;

            default:
                throw new EventingException("An unknown event was received");
        }
        return state;
    }
}
