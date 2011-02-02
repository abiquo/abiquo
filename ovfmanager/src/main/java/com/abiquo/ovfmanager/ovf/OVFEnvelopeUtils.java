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

/**
 * This project aims to be OVF1.0.0 DSP8023(Envelope) and DSP8027(Environment) complaint @see DMTF
 * page for details {@link http://schemas.dmtf.org/ovf/}, but for now VMWare only can deploy OVF
 * packages form the 0.9, so compatibility is take into order.
 * <p>
 * Manipulate objects on the OVF-envelope name space. Hide from other classes the use of JAXB.
 * <p>
 * It intends to be an utility similar to ''open-ovf'', see the complete feature list for this
 * project on:
 * <li>{@link http://open-ovf.wiki.sourceforge.net/open-ovf+command+line+interface}</li><br>
 * also the virtualBox OVF code is checked<br>
 * <li>{@link http ://www.virtualbox.org/browser/trunk/src/VBox/Main/ApplianceImpl.cpp?rev=16306}</li>
 * <p>
 * References to check:
 * <li>{@link http://server.dzone.com/news/a-review-ovf-a-systems-managem}</li>
 * <li>{@link http ://stage.vambenepe.com/archives/382}</li>
 * <li>{@http://grantmcwilliams.com/index.php/virtualization/blog/}</li>
 * <p>
 */
package com.abiquo.ovfmanager.ovf;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBElement;

import org.dmtf.schemas.ovf.envelope._1.AbicloudNetworkType;
import org.dmtf.schemas.ovf.envelope._1.AnnotationSectionType;
import org.dmtf.schemas.ovf.envelope._1.ContentType;
import org.dmtf.schemas.ovf.envelope._1.DeploymentOptionSectionType;
import org.dmtf.schemas.ovf.envelope._1.DiskSectionType;
import org.dmtf.schemas.ovf.envelope._1.EnvelopeType;
import org.dmtf.schemas.ovf.envelope._1.EulaSectionType;
import org.dmtf.schemas.ovf.envelope._1.InstallSectionType;
import org.dmtf.schemas.ovf.envelope._1.NetworkSectionType;
import org.dmtf.schemas.ovf.envelope._1.OperatingSystemSectionType;
import org.dmtf.schemas.ovf.envelope._1.ProductSectionType;
import org.dmtf.schemas.ovf.envelope._1.ResourceAllocationSectionType;
import org.dmtf.schemas.ovf.envelope._1.SectionType;
import org.dmtf.schemas.ovf.envelope._1.StartupSectionType;
import org.dmtf.schemas.ovf.envelope._1.VirtualDiskDescType;
import org.dmtf.schemas.ovf.envelope._1.VirtualHardwareSectionType;
import org.dmtf.schemas.ovf.envelope._1.VirtualSystemCollectionType;
import org.dmtf.schemas.ovf.envelope._1.VirtualSystemType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.ovfmanager.cim.CIMTypesUtils;
import com.abiquo.ovfmanager.ovf.exceptions.EmptyEnvelopeException;
import com.abiquo.ovfmanager.ovf.exceptions.IdAlreadyExistsException;
import com.abiquo.ovfmanager.ovf.exceptions.IdNotFoundException;
import com.abiquo.ovfmanager.ovf.exceptions.InvalidSectionException;
import com.abiquo.ovfmanager.ovf.exceptions.RequiredAttributeException;
import com.abiquo.ovfmanager.ovf.exceptions.SectionAlreadyPresentException;
import com.abiquo.ovfmanager.ovf.exceptions.SectionException;
import com.abiquo.ovfmanager.ovf.exceptions.SectionNotPresentException;
import com.abiquo.ovfmanager.ovf.section.OVFCustomNetworkUtils;
import com.abiquo.ovfmanager.ovf.section.OVFDiskUtils;
import com.abiquo.ovfmanager.ovf.section.OVFNetworkUtils;
import com.abiquo.ovfmanager.ovf.section.OVFVirtualHadwareSectionUtils;

/**
 * This utility provide basic operation on the highest level OVF envelope entities, providing
 * methods to get/create Section on the Envelope or Content (VirtualSystem and
 * VirtualSystemCollection). <br>
 * <li>Creates Envelope, VirtualSystem an VirtualSystemCollection</li><br>
 * <li>Attach sections into Envelope, VirtualSystem or VirtualSystemCollection</li><br>
 * TODO add ''final'' modifier on all the input parameters
 */
public class OVFEnvelopeUtils
{
    private final static Logger log = LoggerFactory.getLogger(OVFEnvelopeUtils.class);

    /** Generated factory to create XML OVF-elements in OVF name space . */
    private final static org.dmtf.schemas.ovf.envelope._1.ObjectFactory envelopFactory =
        new org.dmtf.schemas.ovf.envelope._1.ObjectFactory();

    /** Utility methods for the Reference element. */
    public final static OVFReferenceUtils fileReference = new OVFReferenceUtils();

    /** Utility methods for the Disk section element. */
    public final static OVFDiskUtils diskSection = new OVFDiskUtils();

    /** Utility methods for the Network section element. */
    public final static OVFNetworkUtils networkSection = new OVFNetworkUtils();

    /** Utility methods for the CustomNetwork section element. */
    public final static OVFCustomNetworkUtils customNetworkSection = new OVFCustomNetworkUtils();

    /** Utility methods for the Virtual Hardware section element. */
    public final static OVFVirtualHadwareSectionUtils hardwareSection =
        new OVFVirtualHadwareSectionUtils();

    /**
     * Using the Core Metadata Section table from the OVF specification determine when a specific
     * section is allowed on the OVF envelope element.
     * 
     * @param sectionType, a section class to be checked if is complain to appear on the envelope.
     * @throws InvalidSectionException, if the section type is not allowed on the envelope.
     */
    public static <T extends SectionType> void checkEnvelopeSection(Class<T> sectionType)
        throws InvalidSectionException
    {
        if (!DiskSectionType.class.equals(sectionType)
            && !NetworkSectionType.class.equals(sectionType)
            && !DeploymentOptionSectionType.class.equals(sectionType)
            && !VirtualHardwareSectionType.class.equals(sectionType)
            && (!AbicloudNetworkType.class.equals(sectionType) && !AbicloudNetworkType.class
                .equals(sectionType.getSuperclass())))
        {
            throw new InvalidSectionException("Envelope", sectionType);
        }
    }

    /**
     * Using the Core Metadata Section table from the OVF specification determine when a specific
     * section is allowed on the content element (could be VirtualSystem or
     * VirtualSystemCollection).
     * 
     * @param vsystem, a VirtualSystem or a VirtualSystemCollection class.
     * @param sectionType, a section class to be checked if is complain to appear on the given
     *            Content (virtual system or collection).
     * @throws InvalidSectionException, if the section type is not allowed on the content element.
     */
    public static <T extends SectionType> void checkContentSection(
        Class< ? extends ContentType> vsystem, Class<T> sectionType) throws InvalidSectionException
    {
        if (VirtualSystemCollectionType.class.equals(vsystem))
        {
            if (!StartupSectionType.class.equals(sectionType)
                && !EulaSectionType.class.equals(sectionType)
                && !ProductSectionType.class.equals(sectionType)
                && !AnnotationSectionType.class.equals(sectionType)
                && !ResourceAllocationSectionType.class.equals(sectionType)
                && !VirtualHardwareSectionType.class.equals(sectionType)// TODO remove: break the
            // standard
            )
            {
                throw new InvalidSectionException("VirtualSystemCollection", sectionType);
            }

        }
        else
        // is an VirtualSystemType
        {

            if (!VirtualHardwareSectionType.class.equals(sectionType)
                && !InstallSectionType.class.equals(sectionType)
                && !OperatingSystemSectionType.class.equals(sectionType)
                && !EulaSectionType.class.equals(sectionType)
                && !ProductSectionType.class.equals(sectionType)
                && !AnnotationSectionType.class.equals(sectionType)
                && !VirtualHardwareSectionType.class.equals(sectionType))
            {
                throw new InvalidSectionException("VirtualSystem", sectionType);
            }

        }

    }

    /**
     * Instantiate the desired implementation of a section type.
     * 
     * @param sectionType, the section class to be instantiated.
     * @param msg, a message content to be attached on the instantiated section
     * @return the required section object.
     */
    public static <T extends SectionType> T createSection(Class<T> sectionType, String msg)
        throws SectionException
    {
        T section;
        try
        {
            section = sectionType.newInstance();

            section.setInfo(CIMTypesUtils.createMsg(msg, null));
            // section.setRequired(---) TODO set Required (but it change if the secction if for
            // envelope/vs or vsc ...)
        }
        catch (Exception e)
        {
            throw new SectionException("Section " + sectionType.getName()
                + " can not be instantiated ");
        }

        return section;
    }

    /**
     * Gets the specified section form the provided OVFEnvelope.
     * 
     * @param envelope, the OVF envelope to be inspected
     * @param sectionType, the desired section to be extracted form the envelope.
     * @return the section if present on the highest level envelope content.
     * @throws SectionNotPresentException, when there is not the desired section defined.
     */
    @SuppressWarnings("unchecked")
    public static <T extends SectionType> T getSection(EnvelopeType envelope, Class<T> sectionType)
        throws SectionNotPresentException, InvalidSectionException
    {
        SectionType section;

        checkEnvelopeSection(sectionType);

        for (JAXBElement< ? extends SectionType> jxbsection : envelope.getSection())
        {
            section = jxbsection.getValue();

            if (sectionType.isInstance(section))
            {
                return (T) section;
            }
        }

        throw new SectionNotPresentException(sectionType);
    }

    /**
     * Gets the specified section form provided VirtualSystem.
     * 
     * @param vsystem, the virtual system or virtual system collection to be inspected
     * @param sectionType, the desired section to be extracted form the virtual system or virtual
     *            system collection.
     * @return the section if present on the virtual system or virtual system collection.
     * @throws SectionNotPresentException, when there is not the desired section defined.
     */
    @SuppressWarnings("unchecked")
    public static <T extends SectionType> T getSection(ContentType vsystem, Class<T> sectionType)
        throws SectionNotPresentException, InvalidSectionException
    {
        SectionType section;

        checkContentSection(vsystem.getClass(), sectionType);

        for (JAXBElement< ? extends SectionType> jxbsection : vsystem.getSection())
        {
            section = jxbsection.getValue();

            if (sectionType.isInstance(section))
            {
                return (T) section;
            }
        }

        throw new SectionNotPresentException(sectionType);
    }

    /**
     * Adds the specified section on the provided OVFEnvelope.
     * 
     * @param envelope, the OVF envelope to be modified.
     * @param sectionType, the desired section to be added on the envelope.
     * @throws SectionAlreadyPresentException, when a sectionType is already defined on the envelope
     *             level.
     */
    public static <T extends SectionType> void addSection(EnvelopeType envelope, T section)
        throws SectionAlreadyPresentException, InvalidSectionException
    {

        try
        {
            getSection(envelope, section.getClass());

            throw new SectionAlreadyPresentException(section.getClass());
        }
        catch (SectionNotPresentException e1)
        {
            // so lets add it
        }

        try
        {
            envelope.getSection().add(envelopFactory.createSection(section));
        }
        catch (Exception e) // InstantiationException or IllegalAccessException
        {
            log.error("The class " + section.getClass().getCanonicalName()
                + " can not be instantiated");
        }
    }

    /**
     * Add the specified section form the provided virtual system.
     * 
     * @param vsystem, the virtual system or virtual system collection to be modified
     * @param sectionType, the desired section to be added on the virtual system or virtual system
     *            collection.
     * @throws SectionAlreadyPresentException, when a sectionType is already defined on the virtual
     *             system or collection (note: only VirtualHardware, Product and Eula sections can
     *             appear more than once).
     */
    public static void addSection(ContentType vsystem, SectionType section)
        throws SectionAlreadyPresentException, InvalidSectionException
    {

        try
        {
            getSection(vsystem, section.getClass());

            // note: only VirtualHardware, Product and Eula sections can appear more than once
            if (!ProductSectionType.class.equals(section.getClass())
                && !EulaSectionType.class.equals(section.getClass())
                && !VirtualHardwareSectionType.class.equals(section.getClass())

            )
            {
                throw new SectionAlreadyPresentException(section.getClass());
            }
        }
        catch (SectionNotPresentException e1)
        {
            // so lets create it
        }

        try
        {
            vsystem.getSection().add(envelopFactory.createSection(section));
        }
        catch (Exception e) // InstantiationException or IllegalAccessException
        {
            log.error("The class " + section.getClass().getCanonicalName()
                + " can not be instantiated");
        }
    }

    /*******************************************************************************
     * VITTUAL SYSTEM / COLLECTION CONTENT
     *******************************************************************************/

    /**
     * Creates an empty virtual system.
     * 
     * @param vsId, the required virtual system identifier.
     * @param name, the optional name for the virtual system.
     * @param info, the optional attached information.
     * @return an empty (any section on it) virtual system with the provided Id, name and
     *         information message.
     */
    public static VirtualSystemType createVirtualSystem(String vsId, String name, String info)
        throws RequiredAttributeException
    {
        if (vsId == null)
        {
            throw new RequiredAttributeException("VirtualSystem attribute ID");
        }

        VirtualSystemType vs = new VirtualSystemType();
        vs.setId(vsId);

        vs.setInfo(CIMTypesUtils.createMsg(info, null));
        vs.setName(CIMTypesUtils.createMsg(name, null));

        return vs;
    }

    /**
     * Creates an empty virtual system collection.
     * 
     * @param vsId, the required virtual system identifier.
     * @param name, the optional name for the virtual system collection.
     * @param info, the optional attached information
     * @return an empty (any section on it) virtual system with the provided Id, name and
     *         information message.
     */
    public static VirtualSystemCollectionType createVirtualSystemCollection(String vsId,
        String name, String info) throws RequiredAttributeException
    {
        if (vsId == null)
        {
            throw new RequiredAttributeException("VirtualSystem attribute ID");
        }

        VirtualSystemCollectionType vsc = new VirtualSystemCollectionType();
        vsc.setId(vsId);

        vsc.setInfo(CIMTypesUtils.createMsg(info, null));
        vsc.setName(CIMTypesUtils.createMsg(name, null));

        return vsc;
    }

    /**
     * Check if the given envelope contains more than one virtual system. (if there is a virtual
     * system collection)
     * 
     * @return true if there is only one virtual system on the provided OVF envelope content.
     * @throws EmptyEnvelopeException it the envelope do not contain any virtual system or virtual
     *             system collection
     */
    public static boolean isOneVirtualSystem(EnvelopeType envelope) throws EmptyEnvelopeException
    {
        boolean isVirtualSystem;

        ContentType entity = envelope.getContent().getValue();

        if (entity == null)
        {
            throw new EmptyEnvelopeException();
        }

        if (entity instanceof VirtualSystemType)
        {
            isVirtualSystem = true;
        }
        else if (entity instanceof VirtualSystemCollectionType)
        {
            isVirtualSystem = false;
        }
        else
        {
            throw new EmptyEnvelopeException("Invalid envelope, it do not contains virtualsytem or virtualsystemcollections its a "
                + entity.getClass().getCanonicalName());
        }

        return isVirtualSystem;
    }

    /**
     * Gets the virtual system collection presents on the OVF envelope.
     * 
     * @return the highest level virtual system collection or virtual system collection
     * @throws EmptyEnvelopeException if the provided OVF envelope do not contain any content
     */
    public static ContentType getTopLevelVirtualSystemContent(EnvelopeType envelope)
        throws EmptyEnvelopeException
    {
        ContentType vs;
        JAXBElement< ? extends ContentType> content = envelope.getContent();

        if (content != null)
        {
            vs = content.getValue();

            if (vs == null)
            {
                throw new EmptyEnvelopeException();
            }
        }
        else
        {
            throw new EmptyEnvelopeException();
        }

        return vs;
    }

    /**
     * Extract all the virtual systems (and virtual system collections) form a collection.
     * 
     * @return all the virtual system and collections contained on the provided virtual system
     *         collection.
     */
    public static Set<ContentType> getVirtualSystemsFromCollection(
        VirtualSystemCollectionType vsCollection)
    {
        Set<ContentType> systems = new HashSet<ContentType>();

        List<JAXBElement< ? extends ContentType>> vsCollectContent = vsCollection.getContent();

        for (JAXBElement< ? extends ContentType> elem : vsCollectContent)
        {
            ContentType entity = elem.getValue();

            systems.add(entity);
        }

        return systems;
    }

    /**
     * Extract all the virtual systems (and virtual system collections) form a collection.
     * 
     * @return all the virtual system and collections contained on the provided virtual system
     *         collection.
     */
    public static List<VirtualSystemType> getVirtualSystems(VirtualSystemCollectionType vsCollection)
    {
        List<VirtualSystemType> systems = new LinkedList<VirtualSystemType>();

        List<JAXBElement< ? extends ContentType>> vsCollectContent = vsCollection.getContent();

        for (JAXBElement< ? extends ContentType> elem : vsCollectContent)
        {
            ContentType entity = elem.getValue();

            if (entity instanceof VirtualSystemType)
            {
                systems.add((VirtualSystemType) entity);
            }
            else
            // collection
            {
                systems.addAll(getVirtualSystems((VirtualSystemCollectionType) entity));
            }

        }

        return systems;
    }

    /**
     * Adds a new virtual system on the virtual system collection.
     * 
     * @param collection, the virtual system collection to be changed.
     * @param system, the virtual system or virtual system collection to add.
     */
    public static <T extends ContentType> void addVirtualSystem(
        VirtualSystemCollectionType collection, T system) throws IdAlreadyExistsException
    {
        log.debug("Adding virtual system id:" + system.getId() + " to collection "
            + collection.getId());

        checkVirtualSystemId(collection, system.getId());

        if (system instanceof VirtualSystemType)
        {
            collection.getContent().add(
                envelopFactory.createVirtualSystem((VirtualSystemType) system));
        }
        else if (system instanceof VirtualSystemCollectionType)
        {
            VirtualSystemCollectionType addCollection = (VirtualSystemCollectionType) system;

            collection.getContent()
                .add(envelopFactory.createVirtualSystemCollection(addCollection));
        }
        else
        {
            log.error(
                "The provided content type is not a virtual system or vs collection, its a {}",
                system.getClass().getCanonicalName());
        }
    }

    /**
     * Check if there is some other Content (virtual system or system collection) with the same if
     * on the given virtual system collection.
     * 
     * @param vscollection, a virtual system collection to be check.
     * @param vsId, the identifier to be assert it is unique.
     * @throws IdAlreadyExists it there is some other virtual system with the same id.
     */
    public static void checkVirtualSystemId(VirtualSystemCollectionType vscollection,
        final String vsId) throws IdAlreadyExistsException
    {
        ContentType content;

        for (JAXBElement< ? extends ContentType> jxbcontent : vscollection.getContent())
        {
            content = jxbcontent.getValue();

            if (vsId.equals(content.getId()))
            {
                throw new IdAlreadyExistsException("Virtual system id " + vsId + " on collectino "
                    + vscollection.getId());
            }
        }
    }

    /***
     * TODO doc : call with vs and vsc
     * 
     * @throws IdAlreadyExistsException
     */
    public static <T extends ContentType> void addVirtualSystem(EnvelopeType envelope, T contentType)
        throws IdAlreadyExistsException
    {
        if (VirtualSystemType.class.isInstance(contentType))
        {
            addVirtualSystem(envelope, (VirtualSystemType) contentType);
        }
        else if (VirtualSystemCollectionType.class.isInstance(contentType))
        {
            addVirtualSystemCollection(envelope, (VirtualSystemCollectionType) contentType);
        }
        else
        {
            // TODO throw invalid content exception
        }
    }

    /**
     * Adds a new virtual system on the OVF envelope. If there is a virtual system collection is
     * added there (highest level collection), if there is a previous virtual system a new virtual
     * system collection is created to wrap both, if there is any virtual system/collection simply
     * added.
     * 
     * @param envelope, the OVFenvelope to be changed.
     * @param system, the virtual system to add.
     * @throws IdAlreadyExists
     */
    public static void addVirtualSystem(EnvelopeType envelope, VirtualSystemType system)
        throws IdAlreadyExistsException
    {
        log.debug("Adding virtual system id:" + system.getId());

        JAXBElement<VirtualSystemType> elementSystem = envelopFactory.createVirtualSystem(system);

        if (envelope.getContent() == null) // the envelope do not contain any previous VirtualSystem
        {
            envelope.setContent(elementSystem);
        }
        else
        // envelope already contains some virtual systems (or collection)
        {
            ContentType entity = envelope.getContent().getValue();

            if (entity instanceof VirtualSystemType)
            {
                final String msg =
                    "Adding virtual system on an envelope witch already habe a virtual system,"
                        + " a new VirtualSystemCollection will be created to wrap both";
                log.warn(msg);

                VirtualSystemType entityPrevRoot = (VirtualSystemType) entity;
                VirtualSystemCollectionType collection = new VirtualSystemCollectionType();

                // TODO require some Section to be moved ??? (think on product/eula ....)
                collection.setInfo(CIMTypesUtils.createMsg(msg, null));
                collection.setId("wrap_" + entityPrevRoot.getId() + "_" + system.getId());

                collection.getContent().add(envelopFactory.createVirtualSystem(entityPrevRoot));// previous
                collection.getContent().add(envelopFactory.createVirtualSystem(system));// actual

                envelope.setContent(envelopFactory.createVirtualSystemCollection(collection));
            }
            else if (entity instanceof VirtualSystemCollectionType)
            {
                final String msg = "There is a virtual system collection already";
                log.debug(msg);

                checkVirtualSystemId((VirtualSystemCollectionType) entity, system.getId());

                VirtualSystemCollectionType collection = (VirtualSystemCollectionType) entity;
                collection.getContent().add(elementSystem);
            }
            else
            {
                log
                    .error("Invalid envelope, it do not contains virtualsytem or virtualsystemcollections its a "
                        + entity.getClass().getCanonicalName());
            }
        }// some previous content
    }

    /**
     * Check if the provided virtual system collection is or contains the given collection Id.
     * 
     * @param collection, the virtual system collection to be check and inspected its elements.
     * @param collecitonId, the desired collection Id.
     * @return the virtual system collection (provided or nested on) with Id equals to provided.
     * @throws IdNotFoundException if the collection and any of its nested elements have the desired
     *             Id.
     */
    private static VirtualSystemCollectionType getVirtualSystemCollection(
        VirtualSystemCollectionType collection, final String collectionId)
        throws IdNotFoundException
    {
        for (JAXBElement< ? extends ContentType> system : collection.getContent())
        {
            ContentType entitySystem = system.getValue();

            if (entitySystem instanceof VirtualSystemCollectionType)
            {
                try
                {
                    return getVirtualSystemCollection((VirtualSystemCollectionType) entitySystem,
                        collectionId);
                }
                catch (IdNotFoundException e)
                {
                    // check if there is more virtual systems collections nested the given
                    // collection.
                }
            }
        }// subcollections

        throw new IdNotFoundException("Virtual system collection " + collectionId + " not found");
    }

    /**
     * Adds a new virtual system collection on the OVF envelope. If there is a virtual system
     * collection is added there (highest level collection), if there is a previous virtual system
     * this is added on the collection, if there is any virtual system/collection simply added.
     * 
     * @param envelope, the OVFenvelope to be changed.
     * @param collection, the virtual system collection to add. TODO throw except if id already on
     *            the present collection
     * @throws IdAlreadyExists
     */
    public static void addVirtualSystemCollection(EnvelopeType envelope,
        VirtualSystemCollectionType collection) throws IdAlreadyExistsException
    {
        log.debug("Adding virtual system collection id:" + collection.getId());

        JAXBElement<VirtualSystemCollectionType> elementCollection =
            envelopFactory.createVirtualSystemCollection(collection);

        if (envelope.getContent() == null) // the envelope do not contain any previous VirtualSystem
        {
            envelope.setContent(elementCollection);
        }
        else
        // envelope already contains some virtual systems (or collection)
        {
            ContentType entity = envelope.getContent().getValue();

            if (entity instanceof VirtualSystemType)
            {
                final String msg =
                    "Adding virtual system collection on an envelope witch already habe a virtual system,"
                        + " previous virtual system added to collection";
                // TODO perhaps better to have collection{vs, vsc}
                log.warn(msg);

                // TODO performance: use prev pointer
                collection.getContent().add(
                    envelopFactory.createVirtualSystem((VirtualSystemType) entity));

                envelope.setContent(envelopFactory.createVirtualSystemCollection(collection));
                // TODO perfomance : can i use elementCollection ?
            }
            else if (entity instanceof VirtualSystemCollectionType)
            {
                final String msg = "There is a virtual system collection already";
                log.debug(msg);

                VirtualSystemCollectionType prevCollection = (VirtualSystemCollectionType) entity;

                checkVirtualSystemId(prevCollection, elementCollection.getValue().getId());

                prevCollection.getContent().add(elementCollection);
            }
            else
            {
                log
                    .error("Invalid envelope, it do not contains virtualsytem or virtualsystemcollections its a "
                        + entity.getClass().getCanonicalName());

                // TODO throw or remove
            }
        }// some previous content
    }

    /**
     * Adds a virtual system collection on the given virtual system collection.
     * 
     * @param envelope, the OVFenvelope to be changed.
     * @param collection, the virtual system collection to add.
     * @param collectionID, the virtual system collection identifier on the envelope.
     * @throws IdNotFoundException if the provided envelope do not contain a virtual system
     *             collection with provided ID.
     */
    public static void addVirtualSystemCollection(EnvelopeType envelope,
        VirtualSystemCollectionType collection, String collectionId) throws IdNotFoundException
    {

        log.debug("Adding virtual system collection id:" + collection.getId() + " to collection: "
            + collectionId);

        JAXBElement<VirtualSystemCollectionType> elementCollection =
            envelopFactory.createVirtualSystemCollection(collection);

        if (envelope.getContent() == null) // the envelope do not contain any previous VirtualSystem
        {
            final String msg =
                "The provided envelope do not conatain any virtual system or collection";
            throw new IdNotFoundException(msg);
        }
        else
        // envelope already contains some virtual systems (or collection)
        {
            ContentType entity = envelope.getContent().getValue();

            if (entity instanceof VirtualSystemType)
            {
                final String msg = "The provided envelope only contains a virtual system";
                throw new IdNotFoundException(msg);
            }
            else if (entity instanceof VirtualSystemCollectionType)
            {
                VirtualSystemCollectionType targetCollection =
                    getVirtualSystemCollection((VirtualSystemCollectionType) entity, collectionId);

                targetCollection.getContent().add(elementCollection);
                // TODO check id not exist
            }
            else
            {
                log
                    .error("Invalid envelope, it do not contains virtualsytem or virtualsystemcollections its a "
                        + entity.getClass().getCanonicalName());

                // TODO throw or remove
            }
        }// some previous content
    }

    /**
     * Gets all the virtual disk descriptions on the disk section for the provided OVF envelope.
     * 
     * @param envelope, the OVFenvelope to be inspected.
     * @return all the defined disk descriptions on the OVF envelope.
     */
    public static Set<VirtualDiskDescType> getAllVirtualDiskDescription(EnvelopeType envelope)
        throws SectionNotPresentException
    {
        DiskSectionType sectionDisk = null;
        Set<VirtualDiskDescType> disks = new HashSet<VirtualDiskDescType>();

        try
        {
            sectionDisk = getSection(envelope, DiskSectionType.class);
        }
        catch (InvalidSectionException e)
        {
            // network is allowed on the envelope
        }

        disks.addAll(sectionDisk.getDisk());

        return disks;
    }

}
