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

package com.abiquo.ovfmanager.cim;

import java.util.List;

import org.dmtf.schemas.ovf.envelope._1.RASDType;
import org.dmtf.schemas.wbem.wscim._1.cim_schema._2.cim_resourceallocationsettingdata.CIMResourceAllocationSettingDataType;
import org.dmtf.schemas.wbem.wscim._1.cim_schema._2.cim_resourceallocationsettingdata.ConsumerVisibility;
import org.dmtf.schemas.wbem.wscim._1.cim_schema._2.cim_resourceallocationsettingdata.MappingBehavior;

import com.abiquo.ovfmanager.cim.CIMTypesUtils.CIMResourceTypeEnum;
import com.abiquo.ovfmanager.cim.CIMTypesUtils.ChangeableTypeEnum;
import com.abiquo.ovfmanager.cim.CIMTypesUtils.ConsumerVisibilityEnum;
import com.abiquo.ovfmanager.ovf.exceptions.RequiredAttributeException;

/**
 * TODO using descriptions from
 * http://www.vmware.com/support/developer/cim-sdk/smash/u3/ga/apirefdoc
 * /CIM_ResourceAllocationSettingData.html TODO documentation, first do the CIM_RASD the obtain the
 * RASDT
 */
public class CIMResourceAllocationSettingDataUtils
{

    // TODO todo optional
    public static RASDType createRASDTypeFromCIMRASD(
        final CIMResourceAllocationSettingDataType rasd, final Boolean required,
        final String configuration, final String bound)
    {
        RASDType rasdT;

        rasdT = createRASDTypeFromCIMRASD(rasd);

        rasdT.setRequired(required);
        rasdT.setConfiguration(configuration);
        rasdT.setBound(bound);

        return rasdT;
    }

    public static RASDType createRASDTypeFromCIMRASD(final CIMResourceAllocationSettingDataType rasd)
    {
        RASDType rasdT = new RASDType();

        rasdT.setAddress(rasd.getAddress());
        rasdT.setAddressOnParent(rasd.getAddressOnParent());
        rasdT.setAllocationUnits(rasd.getAllocationUnits());
        rasdT.setAutomaticAllocation(rasd.getAutomaticAllocation());
        rasdT.setAutomaticDeallocation(rasd.getAutomaticDeallocation());
        rasdT.setCaption(rasd.getCaption());
        rasdT.setChangeableType(rasd.getChangeableType());
        rasdT.setConfigurationName(rasd.getConfigurationName());
        rasdT.setConsumerVisibility(rasd.getConsumerVisibility());
        rasdT.setDescription(rasd.getDescription());
        rasdT.setElementName(rasd.getElementName());
        rasdT.getHostResource().addAll(rasd.getHostResource());
        rasdT.getConnection().addAll(rasd.getConnection());
        rasdT.setGeneration(rasd.getGeneration());
        rasdT.setInstanceID(rasd.getInstanceID());
        rasdT.setLimit(rasd.getLimit());
        rasdT.setMappingBehavior(rasd.getMappingBehavior());
        rasdT.setOtherResourceType(rasd.getOtherResourceType());
        rasdT.setParent(rasd.getParent());
        rasdT.setPoolID(rasd.getPoolID());
        rasdT.setReservation(rasd.getReservation());
        rasdT.setResourceSubType(rasd.getResourceSubType());
        rasdT.setResourceType(rasd.getResourceType());
        rasdT.setVirtualQuantity(rasd.getVirtualQuantity());
        rasdT.setWeight(rasd.getWeight());

        return rasdT;
    }

    public static CIMResourceAllocationSettingDataType createResourceAllocationSettingData(
        final String elementName, final String instanceID, final CIMResourceTypeEnum resourceType,
        final String resourceSubType, final String otherResourceType, final String parent,
        final String description, final String caption, final Long generation)
        throws RequiredAttributeException
    {
        CIMResourceAllocationSettingDataType rasd = new CIMResourceAllocationSettingDataType();

        if (elementName == null)
        {
            throw new RequiredAttributeException("VirtualSystemSettingData elementName");
        }
        if (instanceID == null)
        {
            throw new RequiredAttributeException("VirtualSystemSettingData instanceID");
        }

        rasd.setElementName(CIMTypesUtils.createString(elementName));
        rasd.setInstanceID(CIMTypesUtils.createString(instanceID));

        rasd.setResourceType(CIMTypesUtils.createResourceType(resourceType));
        rasd.setResourceSubType(CIMTypesUtils.createString(resourceSubType));
        rasd.setOtherResourceType(CIMTypesUtils.createString(otherResourceType));
        rasd.setParent(CIMTypesUtils.createString(parent));
        rasd.setDescription(CIMTypesUtils.createString(description));
        rasd.setCaption(CIMTypesUtils.createCaptionRASD(caption));
        rasd.setGeneration(CIMTypesUtils.createUnsignedLong(generation));

        return rasd;
    }

    public static CIMResourceAllocationSettingDataType createResourceAllocationSettingData(
        final String elementName, final String instanceID, final CIMResourceTypeEnum resourceType)
        throws RequiredAttributeException
    {
        CIMResourceAllocationSettingDataType rasd = new CIMResourceAllocationSettingDataType();

        if (elementName == null)
        {
            throw new RequiredAttributeException("VirtualSystemSettingData elementName");
        }
        if (instanceID == null)
        {
            throw new RequiredAttributeException("VirtualSystemSettingData instanceID");
        }

        rasd.setElementName(CIMTypesUtils.createString(elementName));
        rasd.setInstanceID(CIMTypesUtils.createString(instanceID));

        rasd.setResourceType(CIMTypesUtils.createResourceType(resourceType));

        return rasd;
    }

    public static CIMResourceAllocationSettingDataType createResourceAllocationSettingData(
        final String elementName, final String instanceID, final CIMResourceTypeEnum resourceType,
        final long virtualQuantity, final String units) throws RequiredAttributeException
    {
        CIMResourceAllocationSettingDataType rasd =
            createResourceAllocationSettingData(elementName, instanceID, resourceType);
        if (virtualQuantity != 0)
        {
            rasd.setVirtualQuantity(CIMTypesUtils.createUnsignedLong(virtualQuantity));
        }
        if (units != null)
        {
            rasd.setAllocationUnits(CIMTypesUtils.createString(units));
        }

        return rasd;
    }

    public static void setAddressToRASD(final CIMResourceAllocationSettingDataType rasd,
        final String address, final String addressOnParen)
    {
        rasd.setAddress(CIMTypesUtils.createString(address));
        rasd.setAddressOnParent(CIMTypesUtils.createString(addressOnParen));
    }

    public static void setPoolPropertiesToRASD(final CIMResourceAllocationSettingDataType rasd,
        final String poolID, final int weight, final List<String> hostResource,
        final String mappingBehavior)
    {
        // TODO assert weight is positive
        // TODO almost poolId is not null

        rasd.setPoolID(CIMTypesUtils.createString(poolID));
        rasd.setWeight(CIMTypesUtils.createUnsignedInt((long) weight));

        MappingBehavior mapi = new MappingBehavior();
        mapi.setValue(mappingBehavior);

        // TODO remove previous rasd.getHostResource(). ??
        if (hostResource != null)
        {
            for (String host : hostResource)
            {
                rasd.getHostResource().add(CIMTypesUtils.createString(host));
            }
        }
    }

    public static void setAllocationToRASD(final CIMResourceAllocationSettingDataType rasd,
        final Long virtualQuantity, final String allocationUnits, final Long reservation,
        final Long limit, final Boolean automaticAllocation, final Boolean automaticDeallocation,
        final ChangeableTypeEnum changeableType)
    {
        rasd.setVirtualQuantity(CIMTypesUtils.createUnsignedLong(virtualQuantity));
        rasd.setAllocationUnits(CIMTypesUtils.createString(allocationUnits));
        rasd.setReservation(CIMTypesUtils.createUnsignedLong(reservation));
        rasd.setLimit(CIMTypesUtils.createUnsignedLong(limit));
        rasd.setAutomaticAllocation(CIMTypesUtils.createBoolean(automaticAllocation));
        rasd.setAutomaticDeallocation(CIMTypesUtils.createBoolean(automaticDeallocation));
        rasd.setChangeableType(CIMTypesUtils.createChangeableTypeRASD(changeableType));
    }

    public static void setAllocationToRASD(final CIMResourceAllocationSettingDataType rasd,
        final Long virtualQuantity)
    {
        rasd.setVirtualQuantity(CIMTypesUtils.createUnsignedLong(virtualQuantity));
    }

    public static void addHostResourceToRASD(final CIMResourceAllocationSettingDataType rasd,
        final String hostResource)
    {
        rasd.getHostResource().add(CIMTypesUtils.createString(hostResource));
    }

    public static void setAddressToRASD(final CIMResourceAllocationSettingDataType rasd,
        final String address)
    {
        rasd.setAddress(CIMTypesUtils.createString(address));
    }

    public static void setConfigurationIdToRASD(final RASDType rasd,
        final String configurationName, final ConsumerVisibilityEnum consumerVisisbility)
    {
        rasd.setConfigurationName(CIMTypesUtils.createString(configurationName));

        ConsumerVisibility visio = new ConsumerVisibility();
        visio.setValue(String.valueOf(consumerVisisbility.getNumericConsumerVisibilityType()));

        rasd.setConsumerVisibility(visio);
    }

    public static void addConnectionToRASD(final CIMResourceAllocationSettingDataType rasd,
        final String newConnection)
    {
        rasd.getConnection().add(CIMTypesUtils.createString(newConnection));
    }

}
