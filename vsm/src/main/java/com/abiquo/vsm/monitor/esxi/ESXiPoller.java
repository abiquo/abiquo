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

package com.abiquo.vsm.monitor.esxi;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.vsm.events.VMEvent;
import com.abiquo.vsm.events.VMEventType;
import com.abiquo.vsm.model.PhysicalMachine;
import com.abiquo.vsm.monitor.AbstractMonitor;
import com.abiquo.vsm.monitor.esxi.util.ExtendedAppUtil;
import com.abiquo.vsm.redis.dao.RedisDao;
import com.abiquo.vsm.redis.dao.RedisDaoFactory;
import com.vmware.vim25.ArrayOfEvent;
import com.vmware.vim25.Event;
import com.vmware.vim25.EventFilterSpec;
import com.vmware.vim25.EventFilterSpecByEntity;
import com.vmware.vim25.EventFilterSpecRecursionOption;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.ObjectSpec;
import com.vmware.vim25.ObjectUpdate;
import com.vmware.vim25.PropertyChange;
import com.vmware.vim25.PropertyChangeOp;
import com.vmware.vim25.PropertyFilterSpec;
import com.vmware.vim25.PropertyFilterUpdate;
import com.vmware.vim25.PropertySpec;
import com.vmware.vim25.SelectionSpec;
import com.vmware.vim25.ServiceContent;
import com.vmware.vim25.UpdateSet;
import com.vmware.vim25.VimPortType;
import com.vmware.vim25.VmEvent;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.util.OptionSpec;

/**
 * This class performs the polling towards the Vmware ESXi hypervisor
 */
public class ESXiPoller implements Callable<Integer>
{

    /** The logger object. */
    private final static Logger logger = LoggerFactory.getLogger(ESXiPoller.class);

    /** The event translation. */
    private Hashtable<String, VMEventType> eventTranslation;

    private AbstractMonitor monitor;

    // VMWare dependent
    /** The opts entered. */
    private HashMap<String, String> optsEntered;

    /** The prop coll. */
    ManagedObjectReference propColl;

    private static OptionSpec[] optionSpec;

    /** The Redis dao. */
    private RedisDao dao;

    static
    {
        optionSpec = constructOptions();
    }

    /**
     * Instantiates a new eS xi poller.
     */
    public ESXiPoller(AbstractMonitor monitor)
    {
        eventTranslation = new Hashtable<String, VMEventType>();
        this.monitor = monitor;

        eventTranslation.put("VmRegisteredEvent", VMEventType.CREATED);
        eventTranslation.put("VmPoweredOnEvent", VMEventType.POWER_ON);
        eventTranslation.put("VmPoweredOffEvent", VMEventType.POWER_OFF);
        eventTranslation.put("VmSuspendedEvent", VMEventType.PAUSED);
        eventTranslation.put("VmResumingEvent", VMEventType.RESUMED);
        eventTranslation.put("VmRemovedEvent", VMEventType.DESTROYED);

        // VMWare dependent
        optsEntered = new HashMap<String, String>();

        dao = RedisDaoFactory.getInstance();
    }

    /**
     * Initializes the login configuration
     * 
     * @param user the admin user
     * @param password admin password
     */
    public void init(String user, String password)
    {
        optsEntered.put("username", user);
        optsEntered.put("password", password);

    }

    /**
     * It adds parameters from the configuration file.
     * 
     * @param config the config
     */
    private void builtinOptionsEntered()
    {
        optsEntered.put("ignorecert", "true");
        optsEntered.put("datacentername", "ha-datacenter");

    }

    /**
     * Creates the event history collector.
     * 
     * @param url the url
     * @param apputil the apputil
     * @return the managed object reference
     * @throws Exception the exception
     */
    private ManagedObjectReference createEventHistoryCollector(ExtendedAppUtil apputil)
        throws Exception
    {
        // Create an Entity Event Filter Spec to
        // specify the MoRef of the VM to be get events filtered for
        ServiceContent sic = apputil.getServiceInstance().getServiceContent();
        EventFilterSpecByEntity entitySpec = new EventFilterSpecByEntity();
        ManagedObjectReference rootFolder = sic.getRootFolder();
        entitySpec.setEntity(rootFolder);
        entitySpec.setRecursion(EventFilterSpecRecursionOption.children);

        // set the entity spec in the EventFilter
        EventFilterSpec eventFilter = new EventFilterSpec();
        eventFilter.setEntity(entitySpec);

        // we are only interested in getting events for the VM.
        // Add as many events you want to track relating to vm.
        // Refer to API Data Object vmEvent and see the extends class list for elaborate list of
        // vmEvents

        /*
         * String[] arrayString = new String[filters.get(url).getFilter().size()];
         * filters.get(url).getFilter().toArray(arrayString);
         */

        String[] arrayString = new String[] {};

        eventFilter.setType(arrayString);
        /*
         * eventFilter.setType( new String[] {"VmPoweredOffEvent", "VmPoweredOnEvent",
         * "VmSuspendedEvent","VmRenamedEvent"} );
         */
        eventFilter.setType(new String[] {"VmPoweredOffEvent", "VmPoweredOnEvent",
        "VmSuspendedEvent", "VmRenamedEvent", "VmRemovedEvent", "VmRegisteredEvent",
        "VmResumingEvent"});
        // create the EventHistoryCollector to monitor events for a VM
        // and get the ManagedObjectReference of the EventHistoryCollector returned
        VimPortType service = apputil.getServiceUtil().getVimService();
        ManagedObjectReference eventManager = sic.getEventManager();
        ManagedObjectReference eventHistoryCollector =
            service.createCollectorForEvents(eventManager, eventFilter);
        return eventHistoryCollector;
    }

    /**
     * Creates the event filter spec.
     * 
     * @param eventHistoryCollector the event history collector
     * @return the property filter spec
     */
    private PropertyFilterSpec createEventFilterSpec(ManagedObjectReference eventHistoryCollector)
    {
        // Set up a PropertySpec to use the latestPage attribute
        // of the EventHistoryCollector

        PropertySpec propSpec = new PropertySpec();
        propSpec.setAll(new Boolean(false));
        propSpec.setPathSet(new String[] {"latestPage"});
        propSpec.setType(eventHistoryCollector.getType());

        // PropertySpecs are wrapped in a PropertySpec array
        PropertySpec[] propSpecAry = new PropertySpec[] {propSpec};

        // Set up an ObjectSpec with the above PropertySpec for the
        // EventHistoryCollector we just created
        // as the Root or Starting Object to get Attributes for.

        ObjectSpec objSpec = new ObjectSpec();
        objSpec.setObj(eventHistoryCollector);
        objSpec.setSkip(new Boolean(false));

        // Get Event objects in "latestPage" from "EventHistoryCollector"
        // and no "traversl" further, so, no SelectionSpec is specified
        objSpec.setSelectSet(new SelectionSpec[] {});

        // ObjectSpecs are wrapped in an ObjectSpec array
        ObjectSpec[] objSpecAry = new ObjectSpec[] {objSpec};

        PropertyFilterSpec spec = new PropertyFilterSpec();
        spec.setPropSet(propSpecAry);
        spec.setObjectSet(objSpecAry);
        return spec;
    }

    /**
     * Handle update.
     * 
     * @param update the update
     * @return the hashtable< string, list< vm event>>
     */
    private Hashtable<String, List<VmEvent>> handleUpdate(UpdateSet update)
    {
        Hashtable<String, List<VmEvent>> events = new Hashtable<String, List<VmEvent>>();
        ArrayList<ObjectUpdate> vmUpdates = new ArrayList<ObjectUpdate>();
        PropertyFilterUpdate[] pfus = update.getFilterSet();
        for (PropertyFilterUpdate pfui : pfus)
        {
            ObjectUpdate[] ous = pfui.getObjectSet();
            for (ObjectUpdate oui : ous)
            {
                if (oui.getObj().getType().equals("EventHistoryCollector"))
                {
                    vmUpdates.add(oui);
                }
            }
        }
        if (vmUpdates.size() > 0)
        {
            logger.trace("Virtual Machine updates:");
            for (ObjectUpdate vmi : vmUpdates)
            {
                handleObjectUpdate(events, vmi);
            }
        }
        return events;
    }

    /**
     * Handle object update.
     * 
     * @param events the events
     * @param oUpdate the o update
     */
    void handleObjectUpdate(Hashtable<String, List<VmEvent>> events, ObjectUpdate oUpdate)
    {
        PropertyChange[] pc = oUpdate.getChangeSet();

        logger.trace(" {} Data:", oUpdate.getKind().toString());
        handleChanges(events, pc);
    }

    /**
     * Handle changes.
     * 
     * @param events the events
     * @param changes the changes
     */
    private void handleChanges(Hashtable<String, List<VmEvent>> events, PropertyChange[] changes)
    {

        for (int pci = 0; pci < changes.length; ++pci)
        {
            Object value = changes[pci].getVal();
            PropertyChangeOp op = changes[pci].getOp();

            if (value != null && !op.name().equalsIgnoreCase("remove"))
            {
                if (value instanceof ArrayOfEvent)
                {
                    ArrayOfEvent aoe = (ArrayOfEvent) value;
                    Event[] evts = aoe.getEvent();

                    for (Event event : evts)
                    {
                        handleVMEvent(event, events, "[ARRAY] Event received: {} Time Stamp: {}");
                    }
                }
                else
                {
                    handleVMEvent(value, events, "Event received: {} Time Stamp: {}");
                }
            }
        }
    }

    private void handleVMEvent(Object event, Hashtable<String, List<VmEvent>> events, String log)
    {
        if (event instanceof VmEvent)
        {
            VmEvent vmEvent = (VmEvent) event;
            if (eventTranslation.containsKey(vmEvent.getClass().getSimpleName()))
            {
                String UUID = vmEvent.getVm().getName();

                logger.debug(log, vmEvent.getClass().getSimpleName(), vmEvent.getCreatedTime()
                    .getTime().toString());

                logger.debug("Wiht id: " + vmEvent.getChainId());
                if (!events.containsKey(UUID))
                {
                    events.put(UUID, new ArrayList<VmEvent>());
                }

                events.get(UUID).add(vmEvent);
            }
        }
    }

    /**
     * It constructs the basic options needed to work.
     * 
     * @return the option spec[]
     */
    private static OptionSpec[] constructOptions()
    {
        OptionSpec[] useroptions = new OptionSpec[8];
        useroptions[0] = new OptionSpec("vmname", "String", 1, "Name of the virtual machine", null);
        useroptions[1] =
            new OptionSpec("datacentername", "String", 1, "Name of the datacenter", null);
        useroptions[2] = new OptionSpec("hostname", "String", 0, "Name of the host", null);
        useroptions[3] =
            new OptionSpec("guestosid", "String", 0, "Type of Guest OS", "winXPProGuest");
        useroptions[4] = new OptionSpec("cpucount", "Integer", 0, "Total CPU Count", "1");
        useroptions[5] = new OptionSpec("disksize", "Integer", 0, "Size of the Disk", "64");
        useroptions[6] =
            new OptionSpec("memorysize",
                "Integer",
                0,
                "Size of the Memory in the blocks of 1024 MB",
                "1024");
        useroptions[7] =
            new OptionSpec("datastorename", "String", 0, "Name of the datastore", null);

        return useroptions;
    }

    /*
     * (non-Javadoc)
     * @see java.util.concurrent.Callable#call()
     */
    @Override
    public Integer call() throws Exception
    {
        ExtendedAppUtil apputil = null;

        builtinOptionsEntered();

        Map<String, String> credentialCache = new HashMap<String, String>();

        while (true)
        {
            List<String> monitoredMachines = monitor.getMonitoredMachines();
            List<String> copy;

            // It is important to synchronize the copy of monitoredMachines list to avoid errors if
            // a machine is added or removed while iterating the list.
            synchronized (monitoredMachines)
            {
                copy = new ArrayList<String>(monitoredMachines);
            }

            for (String url : copy)
            {
                try
                {
                    logger.trace("Monitoring the VMWARE ESXI located in: {}", url);

                    URL tempUrl = new URL(url);
                    String connectionUrl = "https://" + tempUrl.getHost() + ":443/sdk";
                    this.optsEntered.put("url", connectionUrl);

                    if (!credentialCache.containsKey(url))
                    {
                        PhysicalMachine pm = dao.findPhysicalMachineByAddress(url);

                        if (pm == null)
                        {
                            logger.error("Unable to retrieve physical machine " + url
                                + " from redis. Skipping machine.");
                            continue;
                        }

                        credentialCache.put(url, pm.getUsername() + "#" + url + "#"
                            + pm.getPassword());
                        init(pm.getUsername(), pm.getPassword());
                    }
                    else
                    {
                        String credentialStr = credentialCache.get(url);
                        String[] credentials = credentialStr.split("#" + url + "#");
                        init(credentials[0], credentials[1]);
                    }

                    ServiceInstance serviceInstance =
                        new ServiceInstance(new URL(connectionUrl),
                            optsEntered.get("username"),
                            optsEntered.get("password"),
                            true);

                    apputil =
                        ExtendedAppUtil.init(serviceInstance, constructOptions(), optsEntered);
                    
                    // Filters configuration
                    ManagedObjectReference eventHistoryCollector =
                        createEventHistoryCollector(apputil);
                    propColl = apputil.getServiceInstance().getPropertyCollector().getMOR();
                    PropertyFilterSpec eventFilterSpec =
                        createEventFilterSpec(eventHistoryCollector);

                    VimPortType service = apputil.getServiceUtil().getVimService();

                    // Creates the filter
                    ManagedObjectReference propFilter =
                        service.createFilter(propColl, eventFilterSpec, true);

                    String version = "";

                    UpdateSet update = service.waitForUpdates(propColl, version);

                    if (update != null && update.getFilterSet() != null)
                    {
                        Hashtable<String, List<VmEvent>> events = this.handleUpdate(update);

                        version = update.getVersion();

                        if (events.size() > 0)
                        {
                            filterAndSend(events, url);
                        }
                    }

                    service.cancelWaitForUpdates(propColl);

                    // Destroying the property poller
                    service.destroyPropertyFilter(propFilter);

                    
                }
                catch (Exception e)
                {
                    logger.warn(e.getMessage() + " ignoring it.", e);
                }
                finally 
                {
                    apputil.disConnect();
                    logger.trace("Disconnected from the VMWARE ESXI located in: {}", url);
                }
            }
        }
    }

    /**
     * It filters the event and sends it
     * 
     * @param physicalMachineAddress the physical machine address
     * @param events the events
     */
    private void filterAndSend(Hashtable<String, List<VmEvent>> events,
        String physicalMachineAddress)
    {
        // logger.debug("Calling check and send demands: {} events: {}", list.size(),
        // events.size());

        for (String machine : events.keySet())
        {
            for (VmEvent event : events.get(machine))
            {
                // If it is an event supported and the event has not been notified
                if (eventTranslation.containsKey(event.getClass().getSimpleName()))
                {
                    VMEventType state = eventTranslation.get(event.getClass().getSimpleName());
                    VMEvent eventToNotify = new VMEvent(state, physicalMachineAddress, machine);
                    monitor.notify(eventToNotify);
                }
            }
        }
    }
}
