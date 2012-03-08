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

package com.abiquo.ovfmanager.ovf.xml;

import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.bind.Binder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.dmtf.schemas.ovf.envelope._1.AbicloudNetworkType;
import org.dmtf.schemas.ovf.envelope._1.AnnotationSectionType;
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
import org.dmtf.schemas.ovf.envelope._1.VirtualHardwareSectionType;
import org.dmtf.schemas.ovf.envelope._1.VirtualSystemType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.abiquo.ovfmanager.ovf.exceptions.XMLException;

/**
 * Use JAXB to bind standard OVF-envelope and OVFIndex objects into/from XML documents.<br/>
 * 
 * @see Stax2Factory, where Woodstox is used as StAX implementation of the underlying XML parser.
 * @see ANT build file "jaxb" target, where the binding classes are generated.
 * @TODO XMLStreamWriter can specify the encoding ... useful ?
 * @TODO close writers even when an exception occurs.
 */
public class OVFSerializer
{
    private final static Logger logger = LoggerFactory.getLogger(OVFSerializer.class);

    /** Define the allowed objects to be binded from/into the OVF-envelope schema definition. */
    private final JAXBContext contextEnvelope;

    /** Generated factory to create XML elements in OVF-envelope name space. */
    private final org.dmtf.schemas.ovf.envelope._1.ObjectFactory factoryEnvelop;

    /** Determines if the marshalling process to format the XML document. */
    private boolean formatOutput = true;

    /** The singleton instance. */
    private static OVFSerializer instance;

    /** Used to bind an envelope into a DOM document. **/
    private static DocumentBuilderFactory docBuilderFact = DocumentBuilderFactory.newInstance();

    // ** Define the allowed objects to be binded form/into the OVFIndex schema definition. */
    // private final JAXBContext contextIndex;

    // /** Generated factory to create XML elements on OVFIndex name space. */
    // private final com.abiquo.repositoryspace.ObjectFactory factoryIndex;

    /**
     * Get the OVFSerializer singelton instance.
     * 
     * @return the OVFSerializer instance or null if it can not be created.
     */
    public static OVFSerializer getInstance()
    {
        if (instance == null)
        {
            try
            {
                instance = new OVFSerializer();
            }
            catch (JAXBException e)
            {
                logger.error("OVFSerializer instance can not be created ", e);
            }
        }

        return instance;
    }

    /**
     * Instantiate a new OVFSerializer.
     * 
     * @throws JAXBException, if some JAXB context can not be created.
     */
    private OVFSerializer() throws JAXBException
    {
        contextEnvelope = JAXBContext.newInstance(EnvelopeType.class);
        factoryEnvelop = new org.dmtf.schemas.ovf.envelope._1.ObjectFactory();
    }

    /**
     * Wrap into an DOM document the provided OVF envelope.
     * 
     * @param envelope, the OVF envelope the be wrapped
     * @param isNamespaceAware, determine if the created document is XML name space aware.
     * @return a DOM document containing the provided OVF envelope. TODO rename to "toDocument"
     */
    public Document bindToDocument(final EnvelopeType envelope, final boolean isNamespaceAware)
        throws ParserConfigurationException, JAXBException
    {
        // Now serialize the Java Content tree back to XML data
        docBuilderFact.setNamespaceAware(isNamespaceAware);

        DocumentBuilder docBuilder = docBuilderFact.newDocumentBuilder();
        Document doc = docBuilder.newDocument();

        Binder<Node> binder = contextEnvelope.createBinder();

        binder.marshal(toJAXBElement(envelope), doc);

        return doc;
    }

    /**
     * Read an expected OVF-envelope form the provided source.
     * 
     * @param is, the input stream source where read XML documents.
     * @return the OVF-envelope read from source.
     * @throws XMLException, if it is not an envelope type or any XML problem.
     */
    public EnvelopeType readXMLEnvelope(final InputStream is) throws XMLException
    {
        XMLStreamReader reader = null;
        Unmarshaller unmarshall;
        JAXBElement<EnvelopeType> jaxbEnvelope;

        try
        {
            reader = Stax2Factory.getStreamReaderFactory().createXMLStreamReader(is);
            unmarshall = contextEnvelope.createUnmarshaller();
            unmarshall.setSchema(null); // never try to validate an OVF

            jaxbEnvelope = unmarshall.unmarshal(reader, EnvelopeType.class);

        }
        catch (JAXBException ea)
        {
            throw new XMLException(ea);
        }
        catch (XMLStreamException ex)
        {
            throw new XMLException(ex);
        }
        finally
        {
            try
            {
                if (reader != null)
                {
                    reader.close();
                }
            }
            catch (XMLStreamException e)
            {
                e.printStackTrace();
            }
        }

        return jaxbEnvelope.getValue();
    }

    /** Wrap into an JAXBElement the provided OVF envelope. **/
    public JAXBElement<EnvelopeType> toJAXBElement(final EnvelopeType envelope)
    {
        return factoryEnvelop.createEnvelope(envelope);
    }

    /** Wrap into an JAXBElement the provided OVF envelope section. **/
    @SuppressWarnings("unchecked")
    public <T extends SectionType> JAXBElement<T> toJAXBElement(final T section)
    {
        JAXBElement<T> jaxB;

        if (section instanceof DeploymentOptionSectionType)
        {
            jaxB =
                (JAXBElement<T>) factoryEnvelop
                    .createDeploymentOptionSection((DeploymentOptionSectionType) section);
        }
        else if (section instanceof StartupSectionType)
        {
            jaxB =
                (JAXBElement<T>) factoryEnvelop.createStartupSection((StartupSectionType) section);
        }
        else if (section instanceof InstallSectionType)
        {
            jaxB =
                (JAXBElement<T>) factoryEnvelop.createInstallSection((InstallSectionType) section);
        }
        else if (section instanceof OperatingSystemSectionType)
        {
            jaxB =
                (JAXBElement<T>) factoryEnvelop
                    .createOperatingSystemSection((OperatingSystemSectionType) section);
        }
        else if (section instanceof AnnotationSectionType)
        {
            jaxB =
                (JAXBElement<T>) factoryEnvelop
                    .createAnnotationSection((AnnotationSectionType) section);
        }
        else if (section instanceof NetworkSectionType)
        {
            jaxB =
                (JAXBElement<T>) factoryEnvelop.createNetworkSection((NetworkSectionType) section);
        }
        else if (section instanceof EulaSectionType)
        {
            jaxB = (JAXBElement<T>) factoryEnvelop.createEulaSection((EulaSectionType) section);
        }
        else if (section instanceof ProductSectionType)
        {
            jaxB =
                (JAXBElement<T>) factoryEnvelop.createProductSection((ProductSectionType) section);
        }
        else if (section instanceof VirtualHardwareSectionType)
        {
            jaxB =
                (JAXBElement<T>) factoryEnvelop
                    .createVirtualHardwareSection((VirtualHardwareSectionType) section);
        }
        else if (section instanceof ResourceAllocationSectionType)
        {
            jaxB =
                (JAXBElement<T>) factoryEnvelop
                    .createResourceAllocationSection((ResourceAllocationSectionType) section);
        }
        else if (section instanceof DiskSectionType)
        {
            jaxB = (JAXBElement<T>) factoryEnvelop.createDiskSection((DiskSectionType) section);
        }
        else if (section instanceof AbicloudNetworkType)
        {
            jaxB =
                (JAXBElement<T>) factoryEnvelop
                    .createCustomNetworkSection((AbicloudNetworkType) section);
        }
        else
        {
            // TODO throws an exception for invalid section
            jaxB = null;
        }

        return jaxB;
    }

    /**
     * Creates an XML document representing the provided OVF-envelop and write it to output stream.
     * 
     * @param envelope, the object to be binded into an XML document.
     * @param os, the destination of the XML document.
     * @throws XMLException, any XML problem.
     */
    public void writeXML(final EnvelopeType envelope, final OutputStream os) throws XMLException
    {
        XMLStreamWriter writer = null;
        Marshaller marshall;

        try
        {
            writer = Stax2Factory.getStreamWriterFactory().createXMLStreamWriter(os);
            marshall = contextEnvelope.createMarshaller();

            if (formatOutput)
            {
                marshall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            }

            JAXBElement<EnvelopeType> jaxbElem = factoryEnvelop.createEnvelope(envelope);
            marshall.marshal(jaxbElem, writer);

        }
        catch (JAXBException ea)
        {
            throw new XMLException(ea);
        }
        catch (XMLStreamException ex)
        {
            throw new XMLException(ex);
        }
        finally
        {
            if (writer != null)
            {
                try
                {
                    writer.close();
                }
                catch (XMLStreamException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Creates an XML document representing the provided OVF-section and write it to output stream.
     * 
     * @param envelope, the object to be binded into an XML document.
     * @param os, the destination of the XML document.
     * @throws XMLException, any XML problem.
     */
    public void writeXML(final SectionType section, final OutputStream os) throws XMLException
    {
        XMLStreamWriter writer;
        Marshaller marshall;

        try
        {
            writer = Stax2Factory.getStreamWriterFactory().createXMLStreamWriter(os);
            marshall = contextEnvelope.createMarshaller();

            if (formatOutput)
            {
                marshall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            }

            marshall.marshal(toJAXBElement(section), writer);

            writer.close();
        }
        catch (JAXBException ea)
        {
            throw new XMLException(ea);
        }
        catch (XMLStreamException ex)
        {
            throw new XMLException(ex);
        }
    }

    /**
     * Creates an XML document representing an OVF-envelop with the provided virtual system and
     * write it to output stream.
     * 
     * @param virtualSystem, the object inside the envelope to be binded into an XML document.
     * @param os, the destination of the XML document.
     * @throws XMLException, any XML problem.
     */
    public void writeXML(final VirtualSystemType virtualSystem, final OutputStream os)
        throws XMLException
    {
        EnvelopeType envelope;

        envelope = factoryEnvelop.createEnvelopeType();
        envelope.setContent(factoryEnvelop.createVirtualSystem(virtualSystem));

        writeXML(envelope, os);
    }

}
