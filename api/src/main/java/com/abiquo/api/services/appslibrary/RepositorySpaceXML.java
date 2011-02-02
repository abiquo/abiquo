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

package com.abiquo.api.services.appslibrary;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.appliancemanager.repositoryspace.RepositorySpace;
import com.abiquo.ovfmanager.ovf.exceptions.XMLException;
import com.abiquo.ovfmanager.ovf.xml.Stax2Factory;

/**
 * Responsible of RepositorySpace serialization into/from XML (used to read ''ovfindex.xml'').
 */
public class RepositorySpaceXML
{
    private final static Logger logger = LoggerFactory.getLogger(RepositorySpaceXML.class);

    private final static Boolean JAXB_FORMATTED_OUTPUT = true;

    /** Define the allowed objects to be binded form/into the OVFIndex schema definition. */
    private final JAXBContext contextIndex;

    /** Generated factory to create XML elements on OVFIndex name space. */
    private final com.abiquo.appliancemanager.repositoryspace.ObjectFactory factoryIndex;

    /** The singleton instance. */
    private static RepositorySpaceXML instance;

    /**
     * Get the OVFSerializer singelton instance.
     * 
     * @return the OVFSerializer instance or null if it can not be created.
     */
    public static synchronized RepositorySpaceXML getInstance()
    {
        if (instance == null)
        {
            try
            {
                instance = new RepositorySpaceXML();
            }
            catch (JAXBException e)
            {
                logger.error("OVFSerializer instance can not be created ", e);
                throw new RuntimeException("OVFSerializer instance can not be created ", e);
                // TODO runtime exception
            }
        }

        return instance;
    }

    /**
     * @throws JAXBException, if some JAXB context can not be created.
     */
    private RepositorySpaceXML() throws JAXBException
    {
        contextIndex = JAXBContext.newInstance(new Class[] {RepositorySpace.class});
        factoryIndex = new com.abiquo.appliancemanager.repositoryspace.ObjectFactory();
    }

    /**
     * Creates an XML document representing the provided repository space based object and write it
     * to output stream. The provided stream is closed.
     * 
     * @param repoSpace, the object to be binded into an XML document.
     * @param os, the destination of the XML document.
     * @throws OVFSchemaException, any XML problem.
     */
    public void writeAsXML(RepositorySpace rs, OutputStream os) throws XMLException
    {
        XMLStreamWriter writer = null;
        Marshaller marshall;

        try
        {
            writer = Stax2Factory.createXMLStreamWriter(os);

            marshall = contextIndex.createMarshaller();
            marshall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
                new Boolean(JAXB_FORMATTED_OUTPUT));

            marshall.marshal(factoryIndex.createRepositorySpace(rs), writer);
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
                writer.close();
                os.close();
            }
            catch (Exception e)
            {
                logger.error("Can not close the output source, writing a RepositorySpace", e);
            }
        }
    }

    /**
     * @param is, the input stream source where read XML documents.
     * @param cl Represents the class that should be created from the XML structure in the in the
     *            InputStream
     * @return the RepositorySpace read from source.
     * @throws XMLException, if it is not a RepositorySpace type or any XML problem.
     */
    public RepositorySpace readAsXML(InputStream is) throws XMLException
    {
        XMLStreamReader reader = null;

        try
        {
            reader = Stax2Factory.createXMLStreamReader(is);
            Unmarshaller unmarshall = contextIndex.createUnmarshaller();

            JAXBElement<RepositorySpace> jaxb = unmarshall.unmarshal(reader, RepositorySpace.class);

            return jaxb.getValue();
        }
        catch (XMLStreamException e)
        {
            throw new XMLException(e);
        }
        catch (JAXBException e)
        {
            throw new XMLException(e);
        }
        finally
        {
            try
            {
                reader.close();
                is.close();
            }
            catch (Exception e)
            {
                logger.error("Can not close the input source, reading a RepositorySpace", e);
            }
        }
    }

    /**
     * XXX
     */
    public RepositorySpace obtainRepositorySpace(final String repositorySpaceURL) throws XMLException
    {
        RepositorySpace repo;

        try
        {
            URL rsUrl = new URL(repositorySpaceURL);
            InputStream isRs = rsUrl.openStream(); 
            
            repo = readAsXML(isRs);
        }
        catch (XMLException e) // XMLStreamException or JAXBException
        {
            final String msg = "Invalid OVFIndex.xml on RepositorySpace: " + repositorySpaceURL;
            throw new XMLException(msg, e);
        }
        catch (MalformedURLException e)
        {
            final String msg = "Invalid repository space identifier : " + repositorySpaceURL;
            throw new XMLException(msg, e);
        }
        catch (IOException e)
        {
            final String msg = "Can not open a connection to : " + repositorySpaceURL;
            throw new XMLException(msg, e);
        }
        
        repo.setRepositoryURI(repositorySpaceURL); // XXX

        return repo;
    }

}
