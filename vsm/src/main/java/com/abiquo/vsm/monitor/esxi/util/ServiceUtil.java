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

package com.abiquo.vsm.monitor.esxi.util;

import static com.vmware.vim25.mo.util.PropertyCollectorUtil.creatObjectSpec;
import static com.vmware.vim25.mo.util.PropertyCollectorUtil.createPropertySpec;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.vmware.vim25.DynamicProperty;
import com.vmware.vim25.LocalizedMethodFault;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.ObjectContent;
import com.vmware.vim25.ObjectSpec;
import com.vmware.vim25.ObjectUpdate;
import com.vmware.vim25.ObjectUpdateKind;
import com.vmware.vim25.PropertyChange;
import com.vmware.vim25.PropertyChangeOp;
import com.vmware.vim25.PropertyFilterSpec;
import com.vmware.vim25.PropertyFilterUpdate;
import com.vmware.vim25.PropertySpec;
import com.vmware.vim25.SelectionSpec;
import com.vmware.vim25.TaskInfo;
import com.vmware.vim25.TaskInfoState;
import com.vmware.vim25.UpdateSet;
import com.vmware.vim25.VimPortType;
import com.vmware.vim25.mo.util.PropertyCollectorUtil;

/**
 * Utility wrapper methods for the vimService methods
 */
public class ServiceUtil
{

    private AppUtil appUtil;

    public ServiceUtil()
    {
    }

    public static ServiceUtil CreateServiceUtil()
    {
        return new ServiceUtil();
    }

    public void init(AppUtil cb)
    {
        appUtil = cb;
    }

    static String[] meTree =
        {"ManagedEntity", "ComputeResource", "ClusterComputeResource", "Datacenter", "Folder",
        "HostSystem", "ResourcePool", "VirtualMachine"};

    static String[] crTree = {"ComputeResource", "ClusterComputeResource"};

    static String[] hcTree = {"HistoryCollector", "EventHistoryCollector", "TaskHistoryCollector"};

    boolean typeIsA(String searchType, String foundType)
    {
        if (searchType.equals(foundType))
        {
            return true;
        }
        else if (searchType.equals("ManagedEntity"))
        {
            for (int i = 0; i < meTree.length; ++i)
            {
                if (meTree[i].equals(foundType))
                {
                    return true;
                }
            }
        }
        else if (searchType.equals("ComputeResource"))
        {
            for (int i = 0; i < crTree.length; ++i)
            {
                if (crTree[i].equals(foundType))
                {
                    return true;
                }
            }
        }
        else if (searchType.equals("HistoryCollector"))
        {
            for (int i = 0; i < hcTree.length; ++i)
            {
                if (hcTree[i].equals(foundType))
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Get the ManagedObjectReference for an item under the specified root folder that has the type
     * and name specified.
     * 
     * @param root a root folder if available, or null for default
     * @param type type of the managed object
     * @param name name to match
     * @return First ManagedObjectReference of the type / name pair found
     */
    public ManagedObjectReference getDecendentMoRef(ManagedObjectReference root, String type,
        String name) throws Exception
    {
        if (name == null || name.length() == 0)
        {
            return null;
        }

        String[][] typeinfo = new String[][] {new String[] {type, "name",},};

        ObjectContent[] ocary = getContentsRecursively(null, root, typeinfo, true);

        if (ocary == null || ocary.length == 0)
        {
            return null;
        }

        ObjectContent oc = null;
        ManagedObjectReference mor = null;
        DynamicProperty[] propary = null;
        String propval = null;
        boolean found = false;
        for (int oci = 0; oci < ocary.length && !found; oci++)
        {
            oc = ocary[oci];
            mor = oc.getObj();
            propary = oc.getPropSet();

            propval = null;
            if (type == null || typeIsA(type, mor.getType()))
            {
                if (propary.length > 0)
                {
                    propval = (String) propary[0].getVal();
                }

                found = propval != null && name.equals(propval);
            }
        }

        if (!found)
        {
            mor = null;
        }

        return mor;
    }

    /**
     * Get the first ManagedObjectReference from a root of the specified type
     * 
     * @param root a root folder if available, or null for default
     * @param type the type of the entity - e.g. VirtualMachine
     * @return managed object reference available
     */
    public ManagedObjectReference getFirstDecendentMoRef(ManagedObjectReference root, String type)
        throws Exception
    {
        ArrayList morlist = getDecendentMoRefs(root, type);

        ManagedObjectReference mor = null;

        if (morlist.size() > 0)
        {
            mor = (ManagedObjectReference) morlist.get(0);
        }

        return mor;
    }

    /**
     * Retrieve all the ManagedObjectReferences of the type specified.
     * 
     * @param root a root folder if available, or null for default
     * @param type type of container refs to retrieve
     * @return List of MORefs
     */
    public ArrayList getDecendentMoRefs(ManagedObjectReference root, String type) throws Exception
    {
        ArrayList mors = getDecendentMoRefs(root, type, null);
        return mors;
    }

    public ArrayList getDecendentMoRefs(ManagedObjectReference root, String type, String[][] filter)
        throws Exception
    {
        String[][] typeinfo = new String[][] {new String[] {type, "name"},};

        ObjectContent[] ocary = getContentsRecursively(null, root, typeinfo, true);

        ArrayList refs = new ArrayList();

        if (ocary == null || ocary.length == 0)
        {
            return refs;
        }

        for (int oci = 0; oci < ocary.length; oci++)
        {
            refs.add(ocary[oci].getObj());
        }

        if (filter != null)
        {
            ArrayList filtermors = filterMOR(refs, filter);
            return filtermors;
        }
        else
        {
            return refs;
        }
    }

    private ArrayList filterMOR(ArrayList mors, String[][] filter) throws Exception
    {
        ArrayList filteredmors = new ArrayList();
        for (int i = 0; i < mors.size(); i++)
        {
            boolean flag = true;
            String guest = null;
            for (int k = 0; k < filter.length; k++)
            {
                String prop = filter[k][0];
                String reqVal = filter[k][1];
                String value = getProp(((ManagedObjectReference) mors.get(i)), prop);
                if (reqVal == null)
                {
                    continue;

                }

                if (value == null && reqVal == null)
                {
                    continue;

                }

                if (value == null && reqVal != null)
                {
                    flag = false;
                    k = filter.length + 1;

                }
                else if (value.equalsIgnoreCase(reqVal))
                {
                }
                else
                {
                    flag = false;
                    k = filter.length + 1;
                }
            }
            if (flag)
            {
                filteredmors.add(mors.get(i));
            }
        }
        return filteredmors;
    }

    private String getProp(ManagedObjectReference obj, String prop)
    {
        String propVal = null;
        try
        {
            propVal = (String) getDynamicProperty(obj, prop);
        }
        catch (Exception e)
        {
        }
        return propVal;
    }

    /**
     * Retrieve Container contents for all containers recursively from root
     * 
     * @return retrieved object contents
     */
    public ObjectContent[] getAllContainerContents() throws Exception
    {
        ObjectContent[] ocary = getContentsRecursively(null, true);

        return ocary;
    }

    /**
     * Retrieve container contents from specified root recursively if requested.
     * 
     * @param root a root folder if available, or null for default
     * @param recurse retrieve contents recursively from the root down
     * @return retrieved object contents
     */
    public ObjectContent[] getContentsRecursively(ManagedObjectReference root, boolean recurse)
        throws Exception
    {

        String[][] typeinfo = new String[][] {new String[] {"ManagedEntity",},};

        ObjectContent[] ocary = getContentsRecursively(null, root, typeinfo, recurse);

        return ocary;
    }

    /**
     * Retrieve content recursively with multiple properties. the typeinfo array contains typename +
     * properties to retrieve.
     * 
     * @param collector a property collector if available or null for default
     * @param root a root folder if available, or null for default
     * @param typeinfo 2D array of properties for each typename
     * @param recurse retrieve contents recursively from the root down
     * @return retrieved object contents
     */
    public ObjectContent[] getContentsRecursively(ManagedObjectReference collector,
        ManagedObjectReference root, String[][] typeinfo, boolean recurse) throws Exception
    {
        if (typeinfo == null || typeinfo.length == 0)
        {
            return null;
        }

        ManagedObjectReference usecoll = collector;
        if (usecoll == null)
        {
            usecoll = getPropertyCollector();
        }

        ManagedObjectReference useroot = root;
        if (useroot == null)
        {
            useroot = appUtil.getServiceInstance().getServiceContent().getRootFolder();
        }

        SelectionSpec[] selectionSpecs = null;
        if (recurse)
        {
            selectionSpecs = PropertyCollectorUtil.buildFullTraversal();
        }

        PropertySpec[] propspecary = buildPropertySpecArray(typeinfo);

        PropertyFilterSpec filterSpec = new PropertyFilterSpec();
        filterSpec.setPropSet(propspecary);
        filterSpec.setObjectSet(new ObjectSpec[] {PropertyCollectorUtil.creatObjectSpec(useroot,
            false, selectionSpecs)});

        ObjectContent[] retoc =
            getVimService().retrieveProperties(usecoll, new PropertyFilterSpec[] {filterSpec});

        return retoc;
    }

    /**
     * Get a MORef from the property returned.
     * 
     * @param objMor Object to get a reference property from
     * @param propName name of the property that is the MORef
     * @return ManagedObjectReference.
     */
    public ManagedObjectReference getMoRefProp(ManagedObjectReference objMor, String propName)
        throws Exception
    {
        Object props = getDynamicProperty(objMor, propName);
        ManagedObjectReference propmor = null;
        if (!props.getClass().isArray())
        {
            propmor = (ManagedObjectReference) props;
        }

        return propmor;
    }

    /**
     * Retrieve contents for a single object based on the property collector registered with the
     * service.
     * 
     * @param collector Property collector registered with service
     * @param mobj Managed Object Reference to get contents for
     * @param properties names of properties of object to retrieve
     * @return retrieved object contents
     */
    public ObjectContent[] getObjectProperties(ManagedObjectReference collector,
        ManagedObjectReference mobj, String[] properties) throws Exception
    {
        if (mobj == null)
        {
            return null;
        }

        ManagedObjectReference usecoll = collector;
        if (usecoll == null)
        {
            usecoll = getPropertyCollector();
        }

        PropertySpec propertySpec =
            createPropertySpec(mobj.getType(), new Boolean(properties == null
                || properties.length == 0), properties);

        PropertyFilterSpec spec = new PropertyFilterSpec();
        spec.setPropSet(new PropertySpec[] {propertySpec});

        ObjectSpec objectSpec = creatObjectSpec(mobj, false, new SelectionSpec[] {});
        spec.setObjectSet(new ObjectSpec[] {objectSpec});

        return getVimService().retrieveProperties(usecoll, new PropertyFilterSpec[] {spec});
    }

    /**
     * Retrieve a single object
     * 
     * @param mor Managed Object Reference to get contents for
     * @param propertyName of the object to retrieve
     * @return retrieved object
     */
    public Object getDynamicProperty(ManagedObjectReference mor, String propertyName)
        throws Exception
    {
        ObjectContent[] objContent = getObjectProperties(null, mor, new String[] {propertyName});

        Object propertyValue = null;
        if (objContent != null)
        {
            DynamicProperty[] dynamicProperty = objContent[0].getPropSet();
            if (dynamicProperty != null)
            {
                /*
                 * Check the dynamic propery for ArrayOfXXX object
                 */
                Object dynamicPropertyVal = dynamicProperty[0].getVal();
                String dynamicPropertyName = dynamicPropertyVal.getClass().getName();
                if (dynamicPropertyName.indexOf("ArrayOf") != -1)
                {
                    String methodName =
                        dynamicPropertyName.substring(dynamicPropertyName.indexOf("ArrayOf")
                            + "ArrayOf".length(), dynamicPropertyName.length());
                    /*
                     * If object is ArrayOfXXX object, then get the XXX[] by invoking getXXX() on
                     * the object. For Ex: ArrayOfManagedObjectReference.getManagedObjectReference()
                     * returns ManagedObjectReference[] array.
                     */
                    if (methodExists(dynamicPropertyVal, "get" + methodName, null))
                    {
                        methodName = "get" + methodName;
                    }
                    else
                    {
                        /*
                         * Construct methodName for ArrayOf primitive types Ex: For ArrayOfInt,
                         * methodName is get_int
                         */
                        methodName = "get_" + methodName.toLowerCase();
                    }
                    Method getMorMethod =
                        dynamicPropertyVal.getClass().getDeclaredMethod(methodName, (Class[]) null);
                    propertyValue = getMorMethod.invoke(dynamicPropertyVal, (Object[]) null);
                }
                else if (dynamicPropertyVal.getClass().isArray())
                {
                    /*
                     * Handle the case of an unwrapped array being deserialized.
                     */
                    propertyValue = dynamicPropertyVal;
                }
                else
                {
                    propertyValue = dynamicPropertyVal;
                }
            }
        }
        return propertyValue;
    }

    public String waitForTask(ManagedObjectReference taskmor) throws Exception
    {
        Object[] result =
            waitForValues(taskmor, new String[] {"info.state", "info.error"},
                new String[] {"state"}, new Object[][] {new Object[] {TaskInfoState.success,
                TaskInfoState.error}});
        if (result[0].equals(TaskInfoState.success))
        {
            return "success";
        }
        else
        {
            TaskInfo tinfo = (TaskInfo) getDynamicProperty(taskmor, "info");
            LocalizedMethodFault fault = tinfo.getError();
            String error = "Error Occured";
            if (fault != null)
            {
                error = fault.getFault().getFaultCause().getLocalizedMessage();
                System.out.println("Fault "
                    + fault.getFault().getFaultCause().getLocalizedMessage());
            }
            return error;
        }
    }

    /**
     * Handle Updates for a single object. waits till expected values of properties to check are
     * reached Destroys the ObjectFilter when done.
     * 
     * @param objmor MOR of the Object to wait for</param>
     * @param filterProps Properties list to filter
     * @param endWaitProps Properties list to check for expected values these be properties of a
     *            property in the filter properties list
     * @param expectedVals values for properties to end the wait
     * @return true indicating expected values were met, and false otherwise
     */
    public Object[] waitForValues(ManagedObjectReference objmor, String[] filterProps,
        String[] endWaitProps, Object[][] expectedVals) throws Exception
    {
        // version string is initially null
        String version = "";
        Object[] endVals = new Object[endWaitProps.length];
        Object[] filterVals = new Object[filterProps.length];

        PropertyFilterSpec spec = new PropertyFilterSpec();
        spec
            .setObjectSet(new ObjectSpec[] {creatObjectSpec(objmor, false, new SelectionSpec[] {})});

        spec
            .setPropSet(new PropertySpec[] {createPropertySpec(objmor.getType(), false, filterProps)});

        ManagedObjectReference filterSpecRef =
            getVimService().createFilter(getPropertyCollector(), spec, true);

        boolean reached = false;

        UpdateSet updateset = null;
        PropertyFilterUpdate[] filtupary = null;
        PropertyFilterUpdate filtup = null;
        ObjectUpdate[] objupary = null;
        ObjectUpdate objup = null;
        PropertyChange[] propchgary = null;
        PropertyChange propchg = null;
        while (!reached)
        {
            boolean retry = true;
            while (retry)
            {
                try
                {
                    updateset = getVimService().waitForUpdates(getPropertyCollector(), version);
                    retry = false;
                }
                catch (Exception e)
                {
                    if (e.getCause().getMessage().indexOf("java.net.SocketTimeoutException") != -1)
                    {
                        System.out.println("Retrying2........");
                        retry = true;
                    }
                    else
                    {
                        throw e;
                    }
                }
            }
            version = updateset.getVersion();
            if (updateset == null || updateset.getFilterSet() == null)
            {
                continue;
            }

            // Make this code more general purpose when PropCol changes later.
            filtupary = updateset.getFilterSet();
            filtup = null;
            for (int fi = 0; fi < filtupary.length; fi++)
            {
                filtup = filtupary[fi];
                objupary = filtup.getObjectSet();
                objup = null;
                propchgary = null;
                for (int oi = 0; oi < objupary.length; oi++)
                {
                    objup = objupary[oi];

                    // TODO: Handle all "kind"s of updates.
                    if (objup.getKind() == ObjectUpdateKind.modify
                        || objup.getKind() == ObjectUpdateKind.enter
                        || objup.getKind() == ObjectUpdateKind.leave)
                    {
                        propchgary = objup.getChangeSet();
                        for (int ci = 0; ci < propchgary.length; ci++)
                        {
                            propchg = propchgary[ci];
                            updateValues(endWaitProps, endVals, propchg);
                            updateValues(filterProps, filterVals, propchg);
                        }
                    }
                }
            }

            Object expctdval = null;
            // Check if the expected values have been reached and exit the loop if done.
            // Also exit the WaitForUpdates loop if this is the case.
            for (int chgi = 0; chgi < endVals.length && !reached; chgi++)
            {
                for (int vali = 0; vali < expectedVals[chgi].length && !reached; vali++)
                {
                    expctdval = expectedVals[chgi][vali];

                    reached = expctdval.equals(endVals[chgi]) || reached;
                }
            }
        }

        // Destroy the filter when we are done.
        getVimService().destroyPropertyFilter(filterSpecRef);

        return filterVals;
    }

    protected void updateValues(String[] props, Object[] vals, PropertyChange propchg)
    {
        for (int findi = 0; findi < props.length; findi++)
        {
            if (propchg.getName().lastIndexOf(props[findi]) >= 0)
            {
                if (propchg.getOp() == PropertyChangeOp.remove)
                {
                    vals[findi] = "";
                }
                else
                {
                    vals[findi] = propchg.getVal();
                    // System.out.println("Changed value : " + propchg.getVal());
                }
            }
        }
    }

    /**
     * This code takes an array of [typename, property, property, ...] and converts it into a
     * PropertySpec[]. handles case where multiple references to the same typename are specified.
     * 
     * @param typeinfo 2D array of type and properties to retrieve
     * @return Array of container filter specs
     */
    public PropertySpec[] buildPropertySpecArray(String[][] typeinfo)
    {
        // Eliminate duplicates
        HashMap tInfo = new HashMap();
        for (int ti = 0; ti < typeinfo.length; ++ti)
        {
            Set props = (Set) tInfo.get(typeinfo[ti][0]);
            if (props == null)
            {
                props = new HashSet();
                tInfo.put(typeinfo[ti][0], props);
            }
            boolean typeSkipped = false;
            for (int pi = 0; pi < typeinfo[ti].length; ++pi)
            {
                String prop = typeinfo[ti][pi];
                if (typeSkipped)
                {
                    props.add(prop);
                }
                else
                {
                    typeSkipped = true;
                }
            }
        }

        // Create PropertySpecs
        ArrayList pSpecs = new ArrayList();
        for (Iterator ki = tInfo.keySet().iterator(); ki.hasNext();)
        {
            String type = (String) ki.next();
            PropertySpec pSpec = new PropertySpec();
            Set<String> props = (Set<String>) tInfo.get(type);
            pSpec.setType(type);
            pSpec.setAll(props.isEmpty() ? Boolean.TRUE : Boolean.FALSE);
            pSpec.setPathSet(props.toArray(new String[props.size()]));

            pSpecs.add(pSpec);
        }

        return (PropertySpec[]) pSpecs.toArray(new PropertySpec[0]);
    }

    /**
     * Determines of a method 'methodName' exists for the Object 'obj'
     * 
     * @param obj The Object to check
     * @param methodName The method name
     * @param parameterTypes Array of Class objects for the parameter types
     * @return true if the method exists, false otherwise
     */
    boolean methodExists(Object obj, String methodName, Class[] parameterTypes)
    {
        boolean exists = false;
        try
        {
            Method method = obj.getClass().getMethod(methodName, parameterTypes);
            if (method != null)
            {
                exists = true;
            }
        }
        catch (Exception e)
        {
        }
        return exists;
    }

    private ManagedObjectReference getPropertyCollector()
    {
        return appUtil.getServiceInstance().getPropertyCollector().getMOR();
    }

    public VimPortType getVimService()
    {
        return appUtil.getServiceInstance().getServerConnection().getVimService();
    }
}
