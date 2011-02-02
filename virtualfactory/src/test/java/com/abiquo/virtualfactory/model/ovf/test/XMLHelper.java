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

package com.abiquo.virtualfactory.model.ovf.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This class deals with DOM documents and Node. It keeps a link to a factory, builder and document
 * so that those objects are instanciated only once (and only once) per thread. It means that THIS
 * HELPER IS THREAD-SAFE thought the underlying objects are not supposed to be. It also provides
 * some convenient functions to manipulate DOM more easily without requiring the use of a
 * third-party library.
 * 
 * @see org.w3c.dom
 * @see javax.xml.parsers
 * @see javax.xml.transform
 * @author abiquo
 */
public class XMLHelper
{

    /** ThreadLocal object to store one XMLHelper instance per thread. */
    private static ThreadLocal<Object> instance = new ThreadLocal<Object>()
    {
        protected synchronized Object initialValue()
        {
            return new XMLHelper();
        }
    };

    /** The logger object. */
    private static final Logger logger = LoggerFactory.getLogger(XMLHelper.class);

    /**
     * schema cache - Schema objects are thread-safe and can be stored in a static map.
     */
    private static List<String> schemaFiles = new ArrayList<String>();

    /** The resource resolver object. */
    private static LSResourceResolver schemaResourceResolver;

    /** Document builder object. */
    private DocumentBuilder builder;

    /** Document object. */
    private Document document;

    /** default factory, builder and document - NOT thread-safe */
    private DocumentBuilderFactory factory;

    /** Transformer object- */
    private Transformer transformer;

    /** factory and transformer, for XML transformations - NOT thread-safe. */
    private TransformerFactory transformerFactory;

    /**
     * Returns the helper class. Implements the singleton pattern for each thread. This function is
     * thread-safe.
     * 
     * @return the helper instance
     */
    public static XMLHelper getInstance()
    {
        return (XMLHelper) instance.get();
    }

    /**
     * Registers a new path for resolving resources when loading schemas (XML Schemas, DTD, etc).
     */
    public static void registerSchema(String schemaFile) throws Exception
    {
        if (schemaResourceResolver != null)
        {
            throw new IllegalArgumentException("Cannot register new schema files when the schema system is already in use");
        }

        schemaFiles.add(schemaFile);
    }

    /**
     * Private constructor.
     */
    protected XMLHelper()
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Instanciating XML objects for this thread...");
        }

        // DOM factory, builder and document
        factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(false); // validation must be requested
        try
        {
            builder = factory.newDocumentBuilder();
            document = builder.newDocument();
        }
        catch (ParserConfigurationException e)
        {
            throw new RuntimeException("Error while loading DOM document builder", e);
        }

        // XML tranformer (and factory)
        transformerFactory = TransformerFactory.newInstance();
        try
        {
            transformer = transformerFactory.newTransformer();
        }
        catch (TransformerConfigurationException e)
        {
            throw new RuntimeException("error while loading XML transformer");
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("Instanciating XML objects done.");
        }
    }

    /**
     * Internal recursive method for
     * {@link #importElementChangePrefix(Document, Element, String, String)}
     */
    private Element __importElementChangePrefix(Document doc, Element elt, String prefix,
        String namespaceURI)
    {

        // creating new element.
        Element newElement;
        if (namespaceURI.equals(elt.getNamespaceURI()))
        {
            // target namespace, change prefix
            newElement = createElementNS(doc, namespaceURI, prefix + ":" + getLocalName(elt));
        }
        else
        {
            // not the target namespace, do not change anything
            newElement = createElementNS(doc, elt.getNamespaceURI(), elt.getTagName());
        }

        // import attributes
        NamedNodeMap attributes = elt.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++)
        {
            Attr attr = (Attr) attributes.item(i);

            // skip default namespace attribute
            if (attr.getName().equals("xmlns"))
                continue;

            // import attribute node
            newElement.setAttributeNodeNS((Attr) doc.importNode(attr, true));
        }

        // import all child nodes
        NodeList children = elt.getChildNodes();
        for (int i = 0; i < children.getLength(); i++)
        {
            Node node = children.item(i);
            switch (node.getNodeType())
            {
                case Node.ELEMENT_NODE:
                    newElement.appendChild(__importElementChangePrefix(doc, (Element) node, prefix,
                        namespaceURI));
                    break;

                case Node.TEXT_NODE:
                case Node.COMMENT_NODE:
                case Node.CDATA_SECTION_NODE:
                    newElement.appendChild(doc.importNode(node, true));
                    break;

                default:
                    throw new RuntimeException("invalid node type (" + node.getNodeType()
                        + ") for node: " + node.getNodeName());
            }
        }

        return newElement;
    }

    /**
     * Appends a node to another node. This method transparently manages the different case when the
     * child to append is from another document.
     * 
     * @param parent the parent node
     * @param child the node to append to the parent node
     */
    public void append(Element parent, Element child)
    {

        if (parent.getOwnerDocument() == child.getOwnerDocument())
        {
            parent.appendChild(child);

        }
        else
        {
            parent.appendChild(parent.getOwnerDocument().importNode(child, true /* deep */));
        }

    }

    /**
     * Appends all the child nodes of the second elements to the first element. Useful to gather
     * together configuration files that were splitted for the sake of readability.
     * 
     * @param first The first element
     * @param second The second element. Must have the same type as the first element.
     */
    public void appendAll(Element first, Element second) throws Exception
    {

        // first node null, error
        if (first == null)
        {
            throw new Exception("cannot append on a NULL node");
        }

        // no second node, no merge
        if (second == null)
            return;

        // check that the two elements are of the same type
        // (same namespace, same node name)
        if (!((first.getNodeName().equals(second.getNodeName())) && ((first.getNamespaceURI() == null && second
            .getNamespaceURI() == null) || first.getNamespaceURI().equals(second.getNamespaceURI()))))
        {
            throw new Exception("the tags should be of the same type: " + "found {"
                + first.getNamespaceURI() + "}:" + first.getNodeName() + " and {"
                + second.getNamespaceURI() + "}:" + second.getNodeName());
        }

        // adding nodes from of second element to the first one
        NodeList nodes = second.getChildNodes();
        if (first.getOwnerDocument() == second.getOwnerDocument())
        {
            for (int i = 0; i < nodes.getLength(); i++)
            {
                first.appendChild(nodes.item(i).cloneNode(true));
            }
        }
        else
        {
            Document doc = first.getOwnerDocument();
            for (int i = 0; i < nodes.getLength(); i++)
            {
                first.appendChild(doc.importNode(nodes.item(i), true));
            }
        }

    }

    /**
     * Checks if the node is an element and matches the expected name and namespace.
     * 
     * @param description description of the element, for the purpose of error messages
     * @param namespace the expected namespace
     * @param elementName the expected element name
     * @throws Exception if the node does not match
     */
    public void checkCorrectElementNameAndNamespace(String description, Node node,
        String namespace, String elementName) throws Exception
    {

        // check if the node is an element
        if (node == null)
            throw new Exception("NULL node");
        if (node.getNodeType() != Node.ELEMENT_NODE)
            throw new Exception("an element node is expected");

        // check namespace
        if (!namespace.equals(node.getNamespaceURI()))
        {
            throw new Exception("Invalid " + description + " (expected namespace: " + namespace
                + ", got: " + node.getNamespaceURI());
        }

        // check node name
        if (!elementName.equals(getLocalName((Element) node)))
        {
            throw new Exception("Invalid " + description + " (expected node: " + elementName
                + ", got: " + node.getNodeName());
        }
    }

    /**
     * Creates an element node with a simple value. The document to use is given as the third
     * parameter.
     * 
     * @param tagName The name of the tag for this element
     * @param value The simple value of this element
     * @param doc The document to use
     * @return The <code>Element</code> corresponding to the element.
     */
    public Element createElement(Document doc, String tagName, String value)
    {
        Element elementNode = doc.createElement(tagName);
        elementNode.appendChild(doc.createTextNode(value));
        return elementNode;
    }

    /**
     * Creates a new node using the default document
     * 
     * @return The corresponding <code>Element</code> object.
     */
    public Element createElement(String tagName)
    {
        return document.createElement(tagName);
    }

    /**
     * Creates an element node with a simple value. The created node is owned by the default
     * document.
     * 
     * @param tagName The name of the tag for this element
     * @param value The simple value of this element
     * @return The <code>Element</code> corresponding to the element.
     */
    public Element createElement(String tagName, String value)
    {
        return createElement(document, tagName, value);
    }

    /**
     * Creates a new element node, qualifies it using the given namespace. It uses the document
     * given as the last parameter to create nodes.
     * 
     * @param namespaceURI The URI of the namespace for this element
     * @param tagName Tag name (including prefix)
     * @param doc The document to use
     * @return The corresponding <code>Element</code> object.
     */
    public Element createElementNS(Document doc, String namespaceURI, String tagName)
    {
        Element elementNode = doc.createElementNS(namespaceURI, tagName);
        return elementNode;
    }

    /**
     * Creates a new element node, qualifies it using the given namespace, and gives a simple value
     * to it. It uses the document given as the last parameter to create nodes.
     * 
     * @param namespaceURI The URI of the namespace for this element
     * @param tagName Tag name (including prefix)
     * @param value The simple value for this element
     * @param doc The document to use
     * @return The corresponding <code>Element</code> object.
     */
    public Element createElementNS(Document doc, String namespaceURI, String tagName, String value)
    {
        Element elementNode = doc.createElementNS(namespaceURI, tagName);
        elementNode.appendChild(doc.createTextNode(value));
        return elementNode;
    }

    /**
     * Creates a new element node from the default document and qualifies it using the given
     * namespace
     * 
     * @param namespaceURI The URI of the namespace for this element
     * @param tagName Tag name (including prefix)
     * @return The corresponding <code>Element</code> object.
     */
    public Element createElementNS(String namespaceURI, String tagName)
    {
        return document.createElementNS(namespaceURI, tagName);
    }

    /**
     * Creates a new element node from the default document, qualifies it using the given namespace,
     * and gives a simple value to it.
     * 
     * @param namespaceURI The URI of the namespace for this element
     * @param tagName Tag name (including prefix)
     * @param value The simple value for this element
     * @return The corresponding <code>Element</code> object.
     */
    public Element createElementNS(String namespaceURI, String tagName, String value)
    {
        return createElementNS(document, namespaceURI, tagName, value);
    }

    /**
     * Fills an element node with sub-elements found in the files of a specified directory.
     * 
     * @param elt The element to fill
     * @param namespace Expected namespace for sub-elements
     * @param elementName Expected element name for sub-elements
     * @param directory The directory to explore
     * @throws Exception if the directory could not be read or if one of the files does not match
     *             the criteria
     */
    public void fillElementUsingDirectory(Element elt, String namespace, String elementName,
        File directory) throws Exception
    {

        // check the directory exists
        if (!directory.exists())
            throw new Exception("directory not found: " + directory);
        if (!directory.isDirectory())
            throw new Exception("expected a directory, got a file: " + directory);

        // iterate through the files of this directory
        String[] fileList = directory.list();
        for (int i = 0; i < fileList.length; i++)
        {
            File file = new File(fileList[i]);
            if (file.isDirectory())
            {
                // recursive call
                fillElementUsingDirectory(elt, namespace, elementName, file);
            }
            else
            {
                // append the root node of the file to the element
                elt.appendChild(getRootElement(file, namespace, elementName));
            }
        }
    }

    /**
     * Returns the default document instance used in this helper.
     * 
     * @return A <code>Document</code> instance.
     */
    public Document getDefaultDocument()
    {
        return document;
    }

    /**
     * Returns the default document builder instance.
     * 
     * @return A <code>DocumentBuilder</code> instance.
     */
    public DocumentBuilder getDocumentBuilder()
    {
        return builder;
    }

    /**
     * Returns the default <code>DocumentBuilder</code> factory instance.
     * 
     * @return A <code>DocumentBuilderFactory</code> instance.
     */
    public DocumentBuilderFactory getDocumentBuilderFactory()
    {
        return factory;
    }

    /**
     * Wrapper for {@link #getElementAsXML(Element, boolean)} (second parameter to FALSE).
     */
    public String getElementAsXML(Element elt)
    {
        return this.getElementAsXML(elt, false);
    }

    /**
     * Returns the XML corresponding to a DOM element. (Note: it uses the default document for
     * generating the XML).
     * 
     * @param elt The element
     * @param addXmlDeclaration Whether the XML declaration must be added
     * @throws TransformerException if any error occurs while transforming the node into a string.
     * @return The XML representation of this node as a string
     */
    public String getElementAsXML(Element elt, boolean addXmlDeclaration)
    {

        // the use of a XML transformation is a
        // common way of doing the job: see
        // http://java.sun.com/webservices/jaxp/dist/1.1/docs/tutorial/xslt/2_write.html

        Source source = new DOMSource(elt);
        Writer writer = new StringWriter();
        Result result = new StreamResult(writer);
        try
        {
            if (!addXmlDeclaration)
            {
                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            }
            transformer.transform(source, result);
        }
        catch (TransformerException e)
        {
            throw new RuntimeException("error while transforming dom node to xml", e);
        }
        return writer.toString();
    }

    /**
     * Gets the unique Element corresponding to this local name.
     * 
     * @param localName The local name of the element to look for.
     * @param elt The parent element where to search for the element.
     * @return The corresponding element or null if none found.
     */
    public Element getElementByLocalName(String localName, Element elt)
    {
        if (elt == null)
            return null;
        NodeList nodes = elt.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++)
        {
            if (nodes.item(i).getNodeType() != Node.ELEMENT_NODE)
                continue;
            Element eltNode = (Element) nodes.item(i);
            if (getLocalName(eltNode).equals(localName))
            {
                return eltNode;
            }
        }
        return null;
    }

    /**
     * Gets the unique Element corresponding to this tag name
     * 
     * @param elt The parent element where to search for the element.
     * @param tagName The tag name of the element to look for.
     * @return The corresponding element or null if none found.
     */
    public Element getElementByTagName(String tagName, Element elt)
    {
        // TODO Why not to use Element.getElementsByTagName, NodeList vs Element
        // returned
        if (elt == null)
            return null;
        NodeList nodes = elt.getElementsByTagName(tagName);
        return (Element) nodes.item(0);
    }

    /**
     * Returns the first child Element of a DOM Element.
     */
    public Element getFirstChildElement(Element elt)
    {
        NodeList nodes = elt.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++)
        {
            if (nodes.item(i).getNodeType() != Node.ELEMENT_NODE)
                continue;
            return (Element) nodes.item(i);
        }
        return null;
    }

    /**
     * Gets the local name of a tag, whatever it has a namespace of not. This function is useful
     * because the {@link org.w3c.dom.Node#getLocalName()} method does return NULL when a node has
     * been created using DOM level 1 methods, such as
     * {@link org.w3c.dom.Document#createElement(String)}.
     * 
     * @param elt The element (NOT null required)
     * @return the local name of the element
     */
    public String getLocalName(Element elt)
    {
        if (elt.getNamespaceURI() == null)
        {
            return elt.getTagName();
        }
        else
        {
            return elt.getLocalName();
        }
    }

    /**
     * Returns the root node of a document, checking root node name and namespace.
     * 
     * @see #getRootElement(InputStream, String, String)
     */
    public Element getRootElement(File file, String namespace, String elementName) throws Exception
    {
        return getRootElement(new FileInputStream(file), namespace, elementName);
    }

    /**
     * Returns the root node of a document.
     * 
     * @param input the document, as an <code>InputStream</code>
     * @return the root node of the document
     */
    public Element getRootElement(InputStream input) throws Exception
    {
        Document doc = builder.parse(input);
        return doc.getDocumentElement();
    }

    /**
     * Retrieves the root element of a document and checks it matches the expected name and
     * namespace.
     * 
     * @param input the document (input stream)
     * @param namespace the expected namespace
     * @param elementName the expected element name
     * @throws Exception if any error occurs
     * @return the root element of the document, if it matches expected namespace and name.
     *         Otherwise, an exception is thrown.
     */
    public Element getRootElement(InputStream input, String namespace, String elementName)
        throws Exception
    {

        Element elt = getRootElement(input);
        checkCorrectElementNameAndNamespace("root element", elt, namespace, elementName);
        return elt;
    }

    /**
     * Returns the root node of a document stored in a local file
     * 
     * @param filename the path of the document
     * @return the root node of the document
     */
    public Element getRootElement(String filename) throws Exception
    {
        return getRootElement(new FileInputStream(filename));
    }

    /**
     * Returns the default
     */
    public Transformer getTransformer()
    {
        return transformer;
    }

    /**
     * Returns the default <code>TransformerFactory</code>
     * 
     * @return The default instance of <code>TransformerFactory</code>
     * @see javax.xml.transform
     */
    public TransformerFactory getTransformerFactory()
    {
        return transformerFactory;
    }

    /**
     * Imports an element and changes the prefix for all the elements associated with the given
     * namespace.
     * 
     * @param doc The document to use for import
     * @param elt The element to import
     * @param prefix The new prefix for this namespace
     * @param namespaceURI The namespace
     * @return a copy (import) of the element, with prefixes changed for all the elements that are
     *         in the given namespace.
     */
    public Element importElementChangePrefix(Document doc, Element elt, String prefix,
        String namespaceURI) throws Exception
    {

        if (!namespaceURI.equals(elt.getNamespaceURI()))
        {
            throw new Exception("Invalid namespace: expecting '" + namespaceURI + "', got: "
                + elt.getNamespaceURI());
        }

        return __importElementChangePrefix(doc, elt, prefix, namespaceURI);
    }

    /**
     * Creates a new DOM document using the default builder.
     * 
     * @return A new <code>Document</code> object.
     */
    public Document newDocument()
    {
        return builder.newDocument();
    }

    /**
     * Returns the DOM element from its XML-encoded string representation.
     * 
     * @param xml A string containing the XML representation of the element
     * @throws SAXException if any parse error occur
     * @return the DOM <code>Element</code>
     */
    public Element parseStringToElement(String xml) throws SAXException
    {
        try
        {
            Document doc = builder.parse(new InputSource(new StringReader(xml)));
            return doc.getDocumentElement();
        }
        catch (IOException e)
        {
            // should never happen because the source is a string
            // thus raise a runtime error
            throw new RuntimeException("I/O exception while parsing xml string", e);
        }

    }

    /**
     * Private Helper to remove prefix
     * 
     * @param node the node to remove prefix
     */
    public void prefixRemover(Node node)
    {
        node.setPrefix(null);
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++)
        {
            if (nodeList.item(i).getNodeType() == Node.ELEMENT_NODE)
            {
                if (nodeList.item(i).hasChildNodes())
                {
                    prefixRemover(nodeList.item(i));
                }
                nodeList.item(i).setPrefix(null);
            }
        }

    }

}
