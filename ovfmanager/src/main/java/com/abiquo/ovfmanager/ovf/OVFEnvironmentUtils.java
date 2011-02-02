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

package com.abiquo.ovfmanager.ovf;

import javax.xml.bind.JAXBElement;

import org.dmtf.schemas.ovf.environment._1.ObjectFactory;
import org.dmtf.schemas.ovf.environment._1.SectionType;
import org.dmtf.schemas.ovf.environment._1.PropertySectionType.Property;
import org.dmtf.schemas.ovf.environment._1.EntityType;
import org.dmtf.schemas.ovf.environment._1.EnvironmentType;
import org.dmtf.schemas.ovf.environment._1.PlatformSectionType;
import org.dmtf.schemas.ovf.environment._1.PropertySectionType;

import com.abiquo.ovfmanager.cim.CIMTypesUtils;
import com.abiquo.ovfmanager.ovf.exceptions.IdAlreadyExistsException;
import com.abiquo.ovfmanager.ovf.exceptions.RequiredAttributeException;
import com.abiquo.ovfmanager.ovf.exceptions.SectionAlreadyPresentException;
import com.abiquo.ovfmanager.ovf.exceptions.SectionNotPresentException;

public class OVFEnvironmentUtils
{

    private final static ObjectFactory envelopeFactory = new ObjectFactory();

    public static EnvironmentType createEnvironment(String id) throws RequiredAttributeException
    {
        EnvironmentType enviro = new EnvironmentType();

        if (id == null)
        {
            throw new RequiredAttributeException("Id on EnvironmentType");
        }

        enviro.setId(id);

        return enviro;
    }

    public static void addEntity(EnvironmentType environment, EntityType entity)
    {
        environment.getEntity().add(entity);
    }

    /**
     * platform or product section
     */
    public static void setSectionToEnvironment(EnvironmentType environment, SectionType section)
        throws SectionAlreadyPresentException
    {
        // TODO check withc sections can appear once ::: getSection(environment,
        // section.getClass());

        if (section instanceof PropertySectionType)
        {
            environment.getSection().add(
                envelopeFactory.createPropertySection((PropertySectionType) section));
        }
        else if (section instanceof PlatformSectionType)
        {
            environment.getSection().add(
                envelopeFactory.createPlatformSection((PlatformSectionType) section));
        }

        // TODO assert not exist
    }

    public static <T extends SectionType> T getSection(EnvironmentType environment,
        Class<T> sectionType) throws SectionNotPresentException
    {
        SectionType section;

        for (JAXBElement< ? extends SectionType> jxbsection : environment.getSection())
        {
            section = jxbsection.getValue();

            if (sectionType.isInstance(section))
            {
                return (T) section;
            }
        }

        throw new SectionNotPresentException("Section " + sectionType.getCanonicalName());
    }

    public static void setPropertySectionToEntity(EntityType entity,
        PropertySectionType propertySection)
    {
        // TODO assert not exist
        entity.getSection().add(envelopeFactory.createPropertySection(propertySection));
    }

    public static EntityType createEntity(String id) throws RequiredAttributeException
    {
        EntityType entity = new EntityType();

        if (id == null)
        {
            throw new RequiredAttributeException("Id for Environment.Entity");
        }

        entity.setId(id);

        return entity;
    }

    /**
     * Key/value pairs of assigned properties for an entity
     */
    static class PropertySectionUtils
    {

        public static PropertySectionType createPropertySection()
        {
            PropertySectionType psection = new PropertySectionType();

            return psection;
        }

        public static void addProperty(PropertySectionType psection, String key, String value)
            throws RequiredAttributeException, IdAlreadyExistsException
        {
            checkPropertyKey(psection, key);

            if (key == null || value == null)
            {
                throw new RequiredAttributeException("Id or value for Environment.PropretySection.Property");
            }

            Property property = new Property();
            property.setKey(key);
            property.setValue(value);

            psection.getProperty().add(property);
        }

        public static void checkPropertyKey(PropertySectionType psection, String propertyKey)
            throws IdAlreadyExistsException
        {
            for (Property property : psection.getProperty())
            {
                if (propertyKey.equals(property.getKey()))
                {
                    throw new IdAlreadyExistsException("PropertyKeys " + propertyKey);
                }
            }
        }

    }

    /**
     * Information about deployment platform.
     */
    static class PlatformSectionTypeUtils
    {

        /**
         * Creates a PlatformSection.
         * 
         * @param vendor, the optional Deployment platform vendor
         *@param version, the optional deployment platform version
         *@param kind, the optional deployment platform kind.
         *@param locale, the optional current locale
         *@param timeZone, the optional Current timezone offset in minutes from UTC. Time zones
         *            east of Greenwich are positive and time zones west of Greenwich are negative
         */
        public static PlatformSectionType createPlatformSection(String vendor, String version,
            String kind, String locale, Integer timeZone)
        {
            PlatformSectionType psection = new PlatformSectionType();

            psection.setKind(CIMTypesUtils.createString(kind));
            psection.setLocale(CIMTypesUtils.createString(locale));
            psection.setTimezone(timeZone);
            psection.setVendor(CIMTypesUtils.createString(vendor));
            psection.setVersion(CIMTypesUtils.createString(version));

            return null;
        }
    }

}
