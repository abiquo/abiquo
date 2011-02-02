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

package com.abiquo.abiserver.business.hibernate.pojohb.virtualhardware;

import java.io.Serializable;

import org.dmtf.schemas.ovf.envelope._1.RASDType;
import org.dmtf.schemas.wbem.wscim._1.cim_schema._2.cim_resourceallocationsettingdata.CIMResourceAllocationSettingDataType;
import org.dmtf.schemas.wbem.wscim._1.cim_schema._2.cim_resourceallocationsettingdata.Caption;

import com.abiquo.ovfmanager.cim.CIMTypesUtils;

/**
 * According to CIM System Virtualization Model, a ResourceAllocationSettingData are the 'Settings
 * that define the resource allocation. These settings are used by the host system to manage the
 * allocated resource and its relationship to the host resources and/or the resource pool from which
 * it was allocated'. In other words, they are the values that representing the characteristics of a
 * virtual resource. You can find the definitions of the data in the following VMWare document:
 * http://www.vmware.com/support/developer/cim-sdk/smash/u3/ga/apirefdoc/
 * CIM_ResourceAllocationSettingData.html
 * 
 * @author Abiquo
 */
public class ResourceAllocationSettingData implements Serializable// , IPojoHB There's no need to
// move the pojo to Flex POJO
{

    /**
     * Serial ID of the serializable element
     */
    private static final long serialVersionUID = 3210023891043132534L;

    /**
     * The address of the resource.For example, the MAC address of a Ethernet port.
     */
    protected String address;

    /**
     * Unknown utility
     */
    protected String addressOnParent;

    /**
     * This property specifies the units of allocation used by the Reservation and Limit properties.
     * For example, when ResourceType=Processor, AllocationUnits may be set to hertz*10^6 or
     * percent. When ResourceType=Memory, AllocationUnits may be set to bytes*10^3. The value of
     * this property shall be a legal value of the Programmatic Units qualifier as defined in
     * Appendix C.1 of DSP0004 V2.4 or later.
     */
    protected String allocationUnits;

    /**
     * This property specifies if the resource will be automatically allocated. For example when set
     * to true, when the consuming virtual computer system is powered on, this resource would be
     * allocated. A value of false indicates the resource must be explicitly allocated. For example,
     * the setting may represent removable media (cdrom, floppy, etc.) where at power on time, the
     * media isnot present. An explicit operation is required to allocate the resource. Integer to
     * Boolean value: 1- true, 0- false
     */
    protected Integer automaticAllocation;

    /**
     * This property specifies if the resource will be automatically de-allocated. For example, when
     * set to true, when the consuming virtual computer system is powered off, this resource would
     * be de-allocated. When set to false, the resource will remain allocated and must be explicitly
     * de-allocated. Integer to Boolean value: 1- true, 0- false
     */
    protected Integer automaticDeallocation;

    /**
     * The Caption property is a short textual description (one- line string) of the object.
     */
    protected String caption;

    /**
     * Enumeration indicating the type of setting. <br>
     * 0 "Not Changeable - Persistent" indicates the instance of SettingData represents primordial
     * settings and shall not be modifiable.<br>
     * 1 "Changeable - Transient" indicates the SettingData represents modifiable settings that are
     * not persisted. Establishing persistent settings from transient settings may be supported.<br>
     * 2 "Changeable - Persistent" indicates the SettingData represents a persistent configuration
     * that may be modified.<br>
     * 3 "Not Changeable - Transient" indicates the SettingData represents a snapshot of the
     * settings of the associated ManagedElement and is not persistent.
     */
    protected Integer changeableType;

    /**
     * An instance of CIM_SettingData may correspond to a well-known configuration that exists for
     * an associated CIM_ManagedElement. If the ConfigurationName property is non-NULL, the instance
     * of CIM_SettingData shall correspond to a well-known configuration for a Managed Element, the
     * value of the ConfigurationName property shall be the name of the configuration, and the
     * ChangeableType property shall have the value 0 or 2. A value of NULL for the
     * ConfigurationName property shall mean that the instance of CIM_SettingData does not
     * correspond to a well-known configuration for a Managed Element or that this information is
     * unknown.
     */
    protected String configurationName;

    /**
     * The thing to which this resource is connected. For example, a named network or switch port.
     */
    protected String connection;

    /**
     * Describes the consumers visibility to the allocated resource. A value of "Passed-Through"
     * indicates the underlying or host resource is utilized and passed through to the consumer,
     * possibly using partitioning. At least one item shall be present in the HostResource property.
     * A value of "Virtualized" indicates the resource is virtualized and may not map directly to an
     * underlying/host resource. Some implementations may support specific assignment for
     * virtualized resources, in which case the host resource(s) are exposed using the HostResource
     * property. A value of "Not represented" indicates a representation of the resource does not
     * exist within the context of the resource consumer.
     */
    protected Integer consumerVisibility;

    /**
     * The Description property provides a textual description of the object.
     */
    protected String description;

    /**
     * The user-friendly name for this instance of SettingData. In addition, the user-friendly name
     * can be used as an index property for a search or query. (Note: The name does not have to be
     * unique within a namespace.)
     */
    protected String elementName;

    /**
     * Unknown utility.
     */
    protected Long generation;

    /**
     * This property exposes specific assignment to host or underlying resources. The embedded
     * instances shall contain only key properties and be treated as Object Paths. If the virtual
     * resource may be scheduled on a number of underlying resources, this property shall be left
     * NULL. In that case, the DeviceAllocatedFromPool or ResourceAllocationFromPool associations
     * may be used to determine the pool of host resources this virtual resource may be scheduled
     * on. If specific assignment is utilized, all underlying resources used by this virtual
     * resource shall be listed in this array. Typically the array will contain one item, however
     * for aggregate allocations, such as multiple processors, multiple host resources may be
     * specified.
     */
    protected String hostResource;

    /**
     * Within the scope of the instantiating Namespace, InstanceID opaquely and uniquely identifies
     * an instance of this class. To ensure uniqueness within the NameSpace, the value of InstanceID
     * should be constructed using the following "preferred" algorithm: <OrgID>:<LocalID> Where
     * <OrgID> and <LocalID> are separated by a colon (:), and where <OrgID> must include a
     * copyrighted, trademarked, or otherwise unique name that is owned by the business entity that
     * is creating or defining the InstanceID or that is a registered ID assigned to the business
     * entity by a recognized global authority. (This requirement is similar to the <Schema
     * Name>_<Class Name> structure of Schema class names.) In addition, to ensure uniqueness,
     * <OrgID> must not contain a colon (:). When using this algorithm, the first colon to appear in
     * InstanceID must appear between <OrgID> and <LocalID>. <LocalID> is chosen by the business
     * entity and should not be reused to identify different underlying (real-world) elements. If
     * the above "preferred" algorithm is not used, the defining entity must assure that the
     * resulting InstanceID is not reused across any InstanceIDs produced by this or other providers
     * for the NameSpace of this instance. For DMTF-defined instances, the "preferred" algorithm
     * must be used with the <OrgID> set to CIM.
     */
    protected String instanceID;

    /**
     * This property specifies the upper bound, or maximum amount of resource that will be granted
     * for this allocation. For example, a system which supports memory paging may support setting
     * the Limit of a Memory allocation below that of the VirtualQuantity, thus forcing paging to
     * occur for this allocation.
     */
    protected Long limit;

    /**
     * Specifies how this resource maps to underlying resourcesIf the HostResource array contains
     * any entries, this property reflects how the resource maps to those specific resources.
     */
    protected Integer mappingBehaviour;

    /**
     * A string that describes the resource type when a well defined value is not available and
     * ResourceType has the value "Other".
     */
    protected String otherResourceType;

    /**
     * The Parent of the resource.For example, a controller for the current allocation
     */
    protected String parent;

    /**
     * This property specifies which ResourcePool the resource is currently allocated from, or which
     * ResourcePool the resource will be allocated from when the allocation occurs.
     */
    protected String poolID;

    /**
     * This property specifies the amount of resource guaranteed to be available for this
     * allocation. On system which support over-commitment of resources, this value is typically
     * used for admission control to prevent an an allocation from being accepted thus preventing
     * starvation.
     */
    protected Long reservation;

    /**
     * A string describing an implementation specific sub-type for this resource. For example, this
     * may be used to distinguish different models of the same resource type.
     */
    protected String resourceSubType;

    /**
     * The type of resource this allocation setting represents.
     */
    protected Integer resourceType;

    /**
     * This property specifies the quantity of resources presented to the consumer. For example,
     * when ResourceType=Processor, this property would reflect the number of discrete Processors
     * presented to the virtual computer system. When ResourceType=Memory, this property could
     * reflect the number of MB reported to the virtual computer system.
     */
    protected Long virtualQuantity;

    /**
     * This property specifies a relative priority for this allocation in relation to other
     * allocations from the same ResourcePool. This property has no unit of measure, and is only
     * relevant when compared to other allocations vying for the same host resources.
     */
    protected Long weight;

    /**
     * @return the address
     */
    public String getAddress()
    {
        return address;
    }

    /**
     * @param address the address to set
     */
    public void setAddress(String address)
    {
        this.address = address;
    }

    /**
     * @return the addressOnParent
     */
    public String getAddressOnParent()
    {
        return addressOnParent;
    }

    /**
     * @param addressOnParent the addressOnParent to set
     */
    public void setAddressOnParent(String addressOnParent)
    {
        this.addressOnParent = addressOnParent;
    }

    /**
     * @return the allocationUnits
     */
    public String getAllocationUnits()
    {
        return allocationUnits;
    }

    /**
     * @param allocationUnits the allocationUnits to set
     */
    public void setAllocationUnits(String allocationUnits)
    {
        this.allocationUnits = allocationUnits;
    }

    /**
     * @return the automaticAllocation
     */
    public Integer getAutomaticAllocation()
    {
        return automaticAllocation;
    }

    /**
     * @param automaticAllocation the automaticAllocation to set
     */
    public void setAutomaticAllocation(Integer automaticAllocation)
    {
        this.automaticAllocation = automaticAllocation;
    }

    /**
     * @return the automaticDeallocation
     */
    public Integer getAutomaticDeallocation()
    {
        return automaticDeallocation;
    }

    /**
     * @param automaticDeallocation the automaticDeallocation to set
     */
    public void setAutomaticDeallocation(Integer automaticDeallocation)
    {
        this.automaticDeallocation = automaticDeallocation;
    }

    /**
     * @return the caption
     */
    public String getCaption()
    {
        return caption;
    }

    /**
     * @param caption the caption to set
     */
    public void setCaption(String caption)
    {
        this.caption = caption;
    }

    /**
     * @return the changeableType
     */
    public Integer getChangeableType()
    {
        return changeableType;
    }

    /**
     * @param changeableType the changeableType to set
     */
    public void setChangeableType(Integer changeableType)
    {
        this.changeableType = changeableType;
    }

    /**
     * @return the configurationName
     */
    public String getConfigurationName()
    {
        return configurationName;
    }

    /**
     * @param configurationName the configurationName to set
     */
    public void setConfigurationName(String configurationName)
    {
        this.configurationName = configurationName;
    }

    /**
     * @return the connection
     */
    public String getConnection()
    {
        return connection;
    }

    /**
     * @param connection the connection to set
     */
    public void setConnection(String connection)
    {
        this.connection = connection;
    }

    /**
     * @return the consumerVisibility
     */
    public Integer getConsumerVisibility()
    {
        return consumerVisibility;
    }

    /**
     * @param consumerVisibility the consumerVisibility to set
     */
    public void setConsumerVisibility(Integer consumerVisibility)
    {
        this.consumerVisibility = consumerVisibility;
    }

    /**
     * @return the description
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
     * @return the elementName
     */
    public String getElementName()
    {
        return elementName;
    }

    /**
     * @param elementName the elementName to set
     */
    public void setElementName(String elementName)
    {
        this.elementName = elementName;
    }

    /**
     * @return the generation
     */
    public Long getGeneration()
    {
        return generation;
    }

    /**
     * @param generation the generation to set
     */
    public void setGeneration(Long generation)
    {
        this.generation = generation;
    }

    /**
     * @return the hostResource
     */
    public String getHostResource()
    {
        return hostResource;
    }

    /**
     * @param hostResource the hostResource to set
     */
    public void setHostResource(String hostResource)
    {
        this.hostResource = hostResource;
    }

    /**
     * @return the instanceID
     */
    public String getInstanceID()
    {
        return instanceID;
    }

    /**
     * @param instanceID the instanceID to set
     */
    public void setInstanceID(String instanceID)
    {
        this.instanceID = instanceID;
    }

    /**
     * @return the limit
     */
    public Long getLimit()
    {
        return limit;
    }

    /**
     * @param limit the limit to set
     */
    public void setLimit(Long limit)
    {
        this.limit = limit;
    }

    /**
     * @return the mappingBehavior
     */
    public Integer getMappingBehaviour()
    {
        return mappingBehaviour;
    }

    /**
     * @param mappingBehavior the mappingBehavior to set
     */
    public void setMappingBehaviour(Integer mappingBehaviour)
    {
        this.mappingBehaviour = mappingBehaviour;
    }

    /**
     * @return the otherResourceType
     */
    public String getOtherResourceType()
    {
        return otherResourceType;
    }

    /**
     * @param otherResourceType the otherResourceType to set
     */
    public void setOtherResourceType(String otherResourceType)
    {
        this.otherResourceType = otherResourceType;
    }

    /**
     * @return the parent
     */
    public String getParent()
    {
        return parent;
    }

    /**
     * @param parent the parent to set
     */
    public void setParent(String parent)
    {
        this.parent = parent;
    }

    /**
     * @return the poolID
     */
    public String getPoolID()
    {
        return poolID;
    }

    /**
     * @param poolID the poolID to set
     */
    public void setPoolID(String poolID)
    {
        this.poolID = poolID;
    }

    /**
     * @return the reservation
     */
    public Long getReservation()
    {
        return reservation;
    }

    /**
     * @param reservation the reservation to set
     */
    public void setReservation(Long reservation)
    {
        this.reservation = reservation;
    }

    /**
     * @return the resourceSubType
     */
    public String getResourceSubType()
    {
        return resourceSubType;
    }

    /**
     * @param resourceSubType the resourceSubType to set
     */
    public void setResourceSubType(String resourceSubType)
    {
        this.resourceSubType = resourceSubType;
    }

    /**
     * @return the resourceType
     */
    public Integer getResourceType()
    {
        return resourceType;
    }

    /**
     * @param resourceType the resourceType to set
     */
    public void setResourceType(Integer resourceType)
    {
        this.resourceType = resourceType;
    }

    /**
     * @return the virtualQuantity
     */
    public Long getVirtualQuantity()
    {
        return virtualQuantity;
    }

    /**
     * @param virtualQuantity the virtualQuantity to set
     */
    public void setVirtualQuantity(Long virtualQuantity)
    {
        this.virtualQuantity = virtualQuantity;
    }

    /**
     * @return the weight
     */
    public Long getWeight()
    {
        return weight;
    }

    /**
     * @param weight the weight to set
     */
    public void setWeight(Long weight)
    {
        this.weight = weight;
    }

    /**
     * This method parses the OVF type CIMResourceAllocationSettingDataType to abicloud server's
     * ResourceAllocationSettingData
     * 
     * @param rasdIn
     * @return
     */
    public static ResourceAllocationSettingData fromCIM_RASDType(
        CIMResourceAllocationSettingDataType rasdIn)
    {
        ResourceAllocationSettingData rasdOut = new ResourceAllocationSettingData();

        rasdOut.setAddress(rasdIn.getAddress().getValue());
        rasdOut.setAddressOnParent(rasdIn.getAddressOnParent().getValue());
        rasdOut.setAllocationUnits(rasdIn.getAllocationUnits().getValue());

        // C++ int-boolean logic
        if (rasdIn.getAutomaticAllocation().isValue())
        {
            rasdOut.setAutomaticAllocation(1);
        }
        else
        {
            rasdOut.setAutomaticAllocation(0);
        }
        // C++ int-boolean logic
        if (rasdIn.getAutomaticDeallocation().isValue())
        {
            rasdOut.setAutomaticDeallocation(1);
        }
        else
        {
            rasdOut.setAutomaticDeallocation(0);
        }

        rasdOut.setCaption(rasdIn.getCaption().getValue());
        rasdOut.setChangeableType(rasdIn.getChangeableType().getValue());
        // NOTE: gets numeric types of mapping behaviour, not its descriptions
        rasdOut.setConfigurationName(rasdIn.getConfigurationName().getValue());
        // TODO: non-OVF! Our connection is an String and OVF is a List
        rasdOut.setConnection(rasdIn.getConnection().get(0).getValue());
        rasdOut.setConsumerVisibility(Integer.parseInt(rasdIn.getConsumerVisibility().getValue()));
        rasdOut.setDescription(rasdIn.getDescription().getValue());
        rasdOut.setElementName(rasdIn.getElementName().getValue());
        rasdOut.setGeneration(rasdIn.getGeneration().getValue().longValue());
        // TODO: non-OVF! Our connection is an String and OVF is a List
        rasdOut.setHostResource(rasdIn.getHostResource().get(0).getValue());
        rasdOut.setInstanceID(rasdIn.getInstanceID().getValue());
        rasdOut.setLimit(rasdIn.getLimit().getValue().longValue());
        // NOTE: gets numeric types of mapping behaviour, not its descriptions
        rasdOut.setMappingBehaviour(Integer.parseInt(rasdIn.getMappingBehavior().getValue()));
        rasdOut.setOtherResourceType(rasdIn.getOtherResourceType().getValue());
        rasdOut.setParent(rasdIn.getParent().getValue());
        rasdOut.setPoolID(rasdIn.getPoolID().getValue());
        rasdOut.setReservation(rasdIn.getReservation().getValue().longValue());
        rasdOut.setResourceSubType(rasdIn.getResourceSubType().getValue());
        // NOTE: gets numeric types of mapping behaviour, not its descriptions
        rasdOut.setResourceType(Integer.parseInt(rasdIn.getResourceType().getValue()));
        rasdOut.setVirtualQuantity(rasdIn.getVirtualQuantity().getValue().longValue());
        rasdOut.setWeight(rasdIn.getWeight().getValue());

        return rasdOut;
    }

    /**
     * This method parses from the abicloud server's ResourceAllocationSettingData to OVF's type
     * CIMResourceAllocationSettingDataType
     */
    public static RASDType toCIM_RASDType(ResourceAllocationSettingData rasdIn)
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
        rasdOut.setInstanceID(CIMTypesUtils.createString(rasdIn.getInstanceID()));
        rasdOut.setLimit(CIMTypesUtils.createUnsignedLong(rasdIn.getLimit()));
        rasdOut.setMappingBehavior(CIMTypesUtils.createMappingBehaviorFromInteger(rasdIn
            .getMappingBehaviour()));
        rasdOut.setOtherResourceType(CIMTypesUtils.createString(rasdIn.getOtherResourceType()));
        rasdOut.setParent(CIMTypesUtils.createString(rasdIn.getParent()));
        rasdOut.setPoolID(CIMTypesUtils.createString(rasdIn.getPoolID()));
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
