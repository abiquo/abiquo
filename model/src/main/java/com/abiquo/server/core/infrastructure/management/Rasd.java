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

package com.abiquo.server.core.infrastructure.management;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import com.abiquo.server.core.common.GenericEnityBase;
import com.softwarementors.validation.constraints.LeadingOrTrailingWhitespace;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = Rasd.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = Rasd.TABLE_NAME)
public class Rasd extends GenericEnityBase<String>
{
    public static final String TABLE_NAME = "rasd";

    // DO NOT ACCESS: present due to needs of infrastructure support. *NEVER* call from business
    // code
    protected Rasd()
    {
        // Just for JPA support
    }

    public Rasd(final String id, final String elementName, final int resourceType)
    {
        this.id = id;
        setElementName(elementName);
        setResourceType(resourceType);
    }

    private final static String ID_COLUMN = "instanceID";

    /* package */final static int ID_LENGTH_MIN = 1;

    /* package */final static int ID_LENGTH_MAX = 40;

    private final static boolean ID_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    @Id
    @Column(name = ID_COLUMN, nullable = false, length = ID_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = ID_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    private String id;

    @Override
    public String getId()
    {
        return this.id;
    }

    public final static String ADDRESS_ON_PARENT_PROPERTY = "addressOnParent";

    private final static boolean ADDRESS_ON_PARENT_REQUIRED = false;

    private final static int ADDRESS_ON_PARENT_LENGTH_MIN = 0;

    private final static int ADDRESS_ON_PARENT_LENGTH_MAX = 255;

    private final static boolean ADDRESS_ON_PARENT_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String ADDRESS_ON_PARENT_COLUMN = "addressOnParent";

    @Column(name = ADDRESS_ON_PARENT_COLUMN, nullable = !ADDRESS_ON_PARENT_REQUIRED, length = ADDRESS_ON_PARENT_LENGTH_MAX)
    private String addressOnParent;

    @Required(value = ADDRESS_ON_PARENT_REQUIRED)
    @Length(min = ADDRESS_ON_PARENT_LENGTH_MIN, max = ADDRESS_ON_PARENT_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = ADDRESS_ON_PARENT_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getAddressOnParent()
    {
        return this.addressOnParent;
    }

    public void setAddressOnParent(final String addressOnParent)
    {
        this.addressOnParent = addressOnParent;
    }

    public final static String ADDRESS_PROPERTY = "address";

    private final static boolean ADDRESS_REQUIRED = false;

    private final static int ADDRESS_LENGTH_MIN = 0;

    private final static int ADDRESS_LENGTH_MAX = 255;

    private final static boolean ADDRESS_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String ADDRESS_COLUMN = "address";

    @Column(name = ADDRESS_COLUMN, nullable = !ADDRESS_REQUIRED, length = ADDRESS_LENGTH_MAX)
    private String address;

    @Required(value = ADDRESS_REQUIRED)
    @Length(min = ADDRESS_LENGTH_MIN, max = ADDRESS_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = ADDRESS_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getAddress()
    {
        return this.address;
    }

    public void setAddress(final String address)
    {
        this.address = address;
    }

    public final static String PARENT_PROPERTY = "parent";

    private final static boolean PARENT_REQUIRED = false;

    private final static int PARENT_LENGTH_MIN = 0;

    private final static int PARENT_LENGTH_MAX = 255;

    private final static boolean PARENT_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String PARENT_COLUMN = "parent";

    @Column(name = PARENT_COLUMN, nullable = !PARENT_REQUIRED, length = PARENT_LENGTH_MAX)
    private String parent;

    @Required(value = PARENT_REQUIRED)
    @Length(min = PARENT_LENGTH_MIN, max = PARENT_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = PARENT_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getParent()
    {
        return this.parent;
    }

    public void setParent(final String parent)
    {
        this.parent = parent;
    }

    public final static String VIRTUAL_QUANTITY_PROPERTY = "virtualQuantity";

    private final static String VIRTUAL_QUANTITY_COLUMN = "virtualQuantity";

    private final static long VIRTUAL_QUANTITY_MIN = Integer.MIN_VALUE;

    private final static long VIRTUAL_QUANTITY_MAX = Integer.MAX_VALUE;

    @Column(name = VIRTUAL_QUANTITY_COLUMN, nullable = true)
    @Range(min = VIRTUAL_QUANTITY_MIN, max = VIRTUAL_QUANTITY_MAX)
    private Integer virtualQuantity;

    public Integer getVirtualQuantity()
    {
        return this.virtualQuantity;
    }

    public void setVirtualQuantity(final Integer virtualQuantity)
    {
        this.virtualQuantity = virtualQuantity;
    }

    public final static String HOST_RESOURCE_PROPERTY = "hostResource";

    private final static boolean HOST_RESOURCE_REQUIRED = false;

    private final static int HOST_RESOURCE_LENGTH_MIN = 0;

    private final static int HOST_RESOURCE_LENGTH_MAX = 255;

    private final static boolean HOST_RESOURCE_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String HOST_RESOURCE_COLUMN = "hostResource";

    @Column(name = HOST_RESOURCE_COLUMN, nullable = !HOST_RESOURCE_REQUIRED, length = HOST_RESOURCE_LENGTH_MAX)
    private String hostResource;

    @Required(value = HOST_RESOURCE_REQUIRED)
    @Length(min = HOST_RESOURCE_LENGTH_MIN, max = HOST_RESOURCE_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = HOST_RESOURCE_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getHostResource()
    {
        return this.hostResource;
    }

    public void setHostResource(final String hostResource)
    {
        this.hostResource = hostResource;
    }

    public final static String GENERATION_PROPERTY = "generation";

    private final static String GENERATION_COLUMN = "generation";

    private final static long GENERATION_MIN = Long.MIN_VALUE;

    private final static long GENERATION_MAX = Long.MAX_VALUE;

    @Column(name = GENERATION_COLUMN, nullable = true)
    @Range(min = GENERATION_MIN, max = GENERATION_MAX)
    private Long generation;

    public Long getGeneration()
    {
        return this.generation;
    }

    public void setGeneration(final Long generation)
    {
        this.generation = generation;
    }

    public final static String CHANGEABLE_TYPE_PROPERTY = "changeableType";

    private final static String CHANGEABLE_TYPE_COLUMN = "changeableType";

    private final static int CHANGEABLE_TYPE_MIN = Integer.MIN_VALUE;

    private final static int CHANGEABLE_TYPE_MAX = Integer.MAX_VALUE;

    @Column(name = CHANGEABLE_TYPE_COLUMN, nullable = true)
    @Range(min = CHANGEABLE_TYPE_MIN, max = CHANGEABLE_TYPE_MAX)
    private Integer changeableType;

    public Integer getChangeableType()
    {
        return this.changeableType;
    }

    public void setChangeableType(final Integer changeableType)
    {
        this.changeableType = changeableType;
    }

    public final static String AUTOMATIC_ALLOCATION_PROPERTY = "automaticAllocation";

    private final static String AUTOMATIC_ALLOCATION_COLUMN = "automaticAllocation";

    private final static int AUTOMATIC_ALLOCATION_MIN = Integer.MIN_VALUE;

    private final static int AUTOMATIC_ALLOCATION_MAX = Integer.MAX_VALUE;

    @Column(name = AUTOMATIC_ALLOCATION_COLUMN, nullable = true)
    @Range(min = AUTOMATIC_ALLOCATION_MIN, max = AUTOMATIC_ALLOCATION_MAX)
    private Integer automaticAllocation;

    public Integer getAutomaticAllocation()
    {
        return this.automaticAllocation;
    }

    public void setAutomaticAllocation(final Integer automaticAllocation)
    {
        this.automaticAllocation = automaticAllocation;
    }

    public final static String RESOURCE_SUB_TYPE_PROPERTY = "resourceSubType";

    private final static boolean RESOURCE_SUB_TYPE_REQUIRED = false;

    private final static int RESOURCE_SUB_TYPE_LENGTH_MIN = 0;

    private final static int RESOURCE_SUB_TYPE_LENGTH_MAX = 255;

    private final static boolean RESOURCE_SUB_TYPE_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String RESOURCE_SUB_TYPE_COLUMN = "resourceSubType";

    @Column(name = RESOURCE_SUB_TYPE_COLUMN, nullable = !RESOURCE_SUB_TYPE_REQUIRED, length = RESOURCE_SUB_TYPE_LENGTH_MAX)
    private String resourceSubType;

    @Required(value = RESOURCE_SUB_TYPE_REQUIRED)
    @Length(min = RESOURCE_SUB_TYPE_LENGTH_MIN, max = RESOURCE_SUB_TYPE_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = RESOURCE_SUB_TYPE_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getResourceSubType()
    {
        return this.resourceSubType;
    }

    public void setResourceSubType(final String resourceSubType)
    {
        this.resourceSubType = resourceSubType;
    }

    public final static String RESERVATION_PROPERTY = "reservation";

    private final static String RESERVATION_COLUMN = "reservation";

    private final static long RESERVATION_MIN = Long.MIN_VALUE;

    private final static long RESERVATION_MAX = Long.MAX_VALUE;

    @Column(name = RESERVATION_COLUMN, nullable = true)
    @Range(min = RESERVATION_MIN, max = RESERVATION_MAX)
    private Long reservation;

    public Long getReservation()
    {
        return this.reservation;
    }

    public void setReservation(final Long reservation)
    {
        this.reservation = reservation;
    }

    public final static String POOL_ID_PROPERTY = "poolId";

    private final static boolean POOL_ID_REQUIRED = false;

    private final static int POOL_ID_LENGTH_MIN = 0;

    private final static int POOL_ID_LENGTH_MAX = 255;

    private final static boolean POOL_ID_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String POOL_ID_COLUMN = "poolID";

    @Column(name = POOL_ID_COLUMN, nullable = !POOL_ID_REQUIRED, length = POOL_ID_LENGTH_MAX)
    private String poolId;

    @Required(value = POOL_ID_REQUIRED)
    @Length(min = POOL_ID_LENGTH_MIN, max = POOL_ID_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = POOL_ID_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getPoolId()
    {
        return this.poolId;
    }

    public void setPoolId(final String poolId)
    {
        this.poolId = poolId;
    }

    public final static String CONNECTION_PROPERTY = "connection";

    private final static boolean CONNECTION_REQUIRED = false;

    private final static int CONNECTION_LENGTH_MIN = 0;

    private final static int CONNECTION_LENGTH_MAX = 255;

    private final static boolean CONNECTION_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String CONNECTION_COLUMN = "connectionResource";

    @Column(name = CONNECTION_COLUMN, nullable = !CONNECTION_REQUIRED, length = CONNECTION_LENGTH_MAX)
    private String connection;

    @Required(value = CONNECTION_REQUIRED)
    @Length(min = CONNECTION_LENGTH_MIN, max = CONNECTION_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = CONNECTION_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getConnection()
    {
        return this.connection;
    }

    public void setConnection(final String connection)
    {
        this.connection = connection;
    }

    public final static String CONFIGURATION_NAME_PROPERTY = "configurationName";

    private final static boolean CONFIGURATION_NAME_REQUIRED = false;

    private final static int CONFIGURATION_NAME_LENGTH_MIN = 0;

    private final static int CONFIGURATION_NAME_LENGTH_MAX = 255;

    private final static boolean CONFIGURATION_NAME_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String CONFIGURATION_NAME_COLUMN = "configurationName";

    @Column(name = CONFIGURATION_NAME_COLUMN, nullable = !CONFIGURATION_NAME_REQUIRED, length = CONFIGURATION_NAME_LENGTH_MAX)
    private String configurationName;

    @Required(value = CONFIGURATION_NAME_REQUIRED)
    @Length(min = CONFIGURATION_NAME_LENGTH_MIN, max = CONFIGURATION_NAME_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = CONFIGURATION_NAME_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getConfigurationName()
    {
        return this.configurationName;
    }

    public void setConfigurationName(final String configurationName)
    {
        this.configurationName = configurationName;
    }

    private final static String WEIGHT_COLUMN = "weight";

    public final static String WEIGHT_PROPERTY = "weight";

    private final static boolean WEIGHT_REQUIRED = false;

    private final static long WEIGHT_LENGTH_MIN = Integer.MIN_VALUE;

    private final static long WEIGHT_LENGTH_MAX = Integer.MAX_VALUE;

    @Column(name = WEIGHT_COLUMN, nullable = !WEIGHT_REQUIRED)
    @Range(min = WEIGHT_LENGTH_MIN, max = WEIGHT_LENGTH_MAX)
    private Integer weight;

    @Required(value = WEIGHT_REQUIRED)
    public Integer getWeight()
    {
        return this.weight;
    }

    public void setWeight(final Integer weight)
    {
        this.weight = weight;
    }

    public final static String OTHER_RESOURCE_TYPE_PROPERTY = "otherResourceType";

    private final static boolean OTHER_RESOURCE_TYPE_REQUIRED = false;

    private final static int OTHER_RESOURCE_TYPE_LENGTH_MIN = 0;

    private final static int OTHER_RESOURCE_TYPE_LENGTH_MAX = 255;

    private final static boolean OTHER_RESOURCE_TYPE_LEADING_OR_TRAILING_WHITESPACES_ALLOWED =
        false;

    private final static String OTHER_RESOURCE_TYPE_COLUMN = "otherResourceType";

    @Column(name = OTHER_RESOURCE_TYPE_COLUMN, nullable = !OTHER_RESOURCE_TYPE_REQUIRED, length = OTHER_RESOURCE_TYPE_LENGTH_MAX)
    private String otherResourceType;

    @Required(value = OTHER_RESOURCE_TYPE_REQUIRED)
    @Length(min = OTHER_RESOURCE_TYPE_LENGTH_MIN, max = OTHER_RESOURCE_TYPE_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = OTHER_RESOURCE_TYPE_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getOtherResourceType()
    {
        return this.otherResourceType;
    }

    public void setOtherResourceType(final String otherResourceType)
    {
        this.otherResourceType = otherResourceType;
    }

    public final static String MAPPING_BEHAVIOUR_PROPERTY = "mappingBehaviour";

    private final static String MAPPING_BEHAVIOUR_COLUMN = "mappingBehaviour";

    private final static int MAPPING_BEHAVIOUR_MIN = Integer.MIN_VALUE;

    private final static int MAPPING_BEHAVIOUR_MAX = Integer.MAX_VALUE;

    @Column(name = MAPPING_BEHAVIOUR_COLUMN, nullable = true)
    @Range(min = MAPPING_BEHAVIOUR_MIN, max = MAPPING_BEHAVIOUR_MAX)
    private Integer mappingBehaviour;

    public Integer getMappingBehaviour()
    {
        return this.mappingBehaviour;
    }

    public void setMappingBehaviour(final Integer mappingBehaviour)
    {
        this.mappingBehaviour = mappingBehaviour;
    }

    public final static String AUTOMATIC_DEALLOCATION_PROPERTY = "automaticDeallocation";

    private final static String AUTOMATIC_DEALLOCATION_COLUMN = "automaticDeallocation";

    private final static int AUTOMATIC_DEALLOCATION_MIN = Integer.MIN_VALUE;

    private final static int AUTOMATIC_DEALLOCATION_MAX = Integer.MAX_VALUE;

    @Column(name = AUTOMATIC_DEALLOCATION_COLUMN, nullable = true)
    @Range(min = AUTOMATIC_DEALLOCATION_MIN, max = AUTOMATIC_DEALLOCATION_MAX)
    private Integer automaticDeallocation;

    public Integer getAutomaticDeallocation()
    {
        return this.automaticDeallocation;
    }

    public void setAutomaticDeallocation(final Integer automaticDeallocation)
    {
        this.automaticDeallocation = automaticDeallocation;
    }

    public final static String CAPTION_PROPERTY = "caption";

    private final static boolean CAPTION_REQUIRED = false;

    private final static int CAPTION_LENGTH_MIN = 0;

    private final static int CAPTION_LENGTH_MAX = 255;

    private final static boolean CAPTION_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String CAPTION_COLUMN = "caption";

    @Column(name = CAPTION_COLUMN, nullable = !CAPTION_REQUIRED, length = CAPTION_LENGTH_MAX)
    private String caption;

    @Required(value = CAPTION_REQUIRED)
    @Length(min = CAPTION_LENGTH_MIN, max = CAPTION_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = CAPTION_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getCaption()
    {
        return this.caption;
    }

    public void setCaption(final String caption)
    {
        this.caption = caption;
    }

    public final static String ALLOCATION_UNITS_PROPERTY = "allocationUnits";

    private final static boolean ALLOCATION_UNITS_REQUIRED = false;

    private final static int ALLOCATION_UNITS_LENGTH_MIN = 0;

    private final static int ALLOCATION_UNITS_LENGTH_MAX = 255;

    private final static boolean ALLOCATION_UNITS_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String ALLOCATION_UNITS_COLUMN = "allocationUnits";

    @Column(name = ALLOCATION_UNITS_COLUMN, nullable = !ALLOCATION_UNITS_REQUIRED, length = ALLOCATION_UNITS_LENGTH_MAX)
    private String allocationUnits;

    @Required(value = ALLOCATION_UNITS_REQUIRED)
    @Length(min = ALLOCATION_UNITS_LENGTH_MIN, max = ALLOCATION_UNITS_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = ALLOCATION_UNITS_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getAllocationUnits()
    {
        return this.allocationUnits;
    }

    public void setAllocationUnits(final String allocationUnits)
    {
        this.allocationUnits = allocationUnits;
    }

    public final static String ELEMENT_NAME_PROPERTY = "elementName";

    private final static boolean ELEMENT_NAME_REQUIRED = true;

    public final static int ELEMENT_NAME_LENGTH_MIN = 1;

    public final static int ELEMENT_NAME_LENGTH_MAX = 255;

    private final static boolean ELEMENT_NAME_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String ELEMENT_NAME_COLUMN = "elementName";

    @Column(name = ELEMENT_NAME_COLUMN, nullable = !ELEMENT_NAME_REQUIRED, length = ELEMENT_NAME_LENGTH_MAX)
    private String elementName;

    @Required(value = ELEMENT_NAME_REQUIRED)
    @Length(min = ELEMENT_NAME_LENGTH_MIN, max = ELEMENT_NAME_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = ELEMENT_NAME_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getElementName()
    {
        return this.elementName;
    }

    public void setElementName(final String elementName)
    {
        this.elementName = elementName;
    }

    public final static String DESCRIPTION_PROPERTY = "description";

    private final static boolean DESCRIPTION_REQUIRED = false;

    private final static int DESCRIPTION_LENGTH_MIN = 1;

    private final static int DESCRIPTION_LENGTH_MAX = 255;

    private final static boolean DESCRIPTION_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String DESCRIPTION_COLUMN = "description";

    @Column(name = DESCRIPTION_COLUMN, nullable = !DESCRIPTION_REQUIRED, length = DESCRIPTION_LENGTH_MAX)
    private String description;

    @Required(value = DESCRIPTION_REQUIRED)
    @Length(min = DESCRIPTION_LENGTH_MIN, max = DESCRIPTION_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = DESCRIPTION_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getDescription()
    {
        return this.description;
    }

    public void setDescription(final String description)
    {
        this.description = description;
    }

    public final static String CONSUMER_VISIBILITY_PROPERTY = "consumerVisibility";

    private final static String CONSUMER_VISIBILITY_COLUMN = "consumerVisibility";

    private final static int CONSUMER_VISIBILITY_MIN = Integer.MIN_VALUE;

    private final static int CONSUMER_VISIBILITY_MAX = Integer.MAX_VALUE;

    @Column(name = CONSUMER_VISIBILITY_COLUMN, nullable = true)
    @Range(min = CONSUMER_VISIBILITY_MIN, max = CONSUMER_VISIBILITY_MAX)
    private Integer consumerVisibility;

    public Integer getConsumerVisibility()
    {
        return this.consumerVisibility;
    }

    public void setConsumerVisibility(final Integer consumerVisibility)
    {
        this.consumerVisibility = consumerVisibility;
    }

    public final static String LIMIT_PROPERTY = "limit";

    private final static String LIMIT_COLUMN = "limitResource";

    private final static long LIMIT_MIN = Integer.MIN_VALUE;

    private final static long LIMIT_MAX = Integer.MAX_VALUE;

    @Column(name = LIMIT_COLUMN, nullable = true)
    @Range(min = LIMIT_MIN, max = LIMIT_MAX)
    private Long limit;

    public Long getLimit()
    {
        return this.limit;
    }

    public void setLimit(final Long limit)
    {
        this.limit = limit;
    }

    public final static String RESOURCE_TYPE_PROPERTY = "resourceType";

    private final static String RESOURCE_TYPE_COLUMN = "resourceType";

    private final static boolean RESOURCE_TYPE_REQUIRED = true;

    final static int RESOURCE_TYPE_MIN = Integer.MIN_VALUE;

    final static int RESOURCE_TYPE_MAX = Integer.MAX_VALUE;

    @Required(value = RESOURCE_TYPE_REQUIRED)
    @Column(name = RESOURCE_TYPE_COLUMN, nullable = !RESOURCE_TYPE_REQUIRED)
    @Range(min = RESOURCE_TYPE_MIN, max = RESOURCE_TYPE_MAX)
    private Integer resourceType;

    public Integer getResourceType()
    {
        return this.resourceType;
    }

    public void setResourceType(final Integer resourceType)
    {
        this.resourceType = resourceType;
    }

}
