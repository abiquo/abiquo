package com.abiquo.model.transport;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Unit tests for the {@link AcceptedRequestDto} marshalling and unmarshalling.
 * 
 * @author Ignasi Barrera
 */
@Test(groups = "model")
public class AcceptedRequestDtoTest
{
    private JAXBContext context;

    @BeforeMethod
    public void setup() throws JAXBException
    {
        context = JAXBContext.newInstance(AcceptedRequestDto.class);
    }

    public void testMarshall() throws Exception
    {
        AcceptedRequestDto<String> dto = new AcceptedRequestDto<String>();
        dto.setEntity("Dummy value");
        dto.setStatusUrlLink("http://foo/bar");

        String xml = marshal(dto);
        checkDocument(xml);
    }

    public void testUnmarshall() throws Exception
    {
        AcceptedRequestDto<String> dto = unmarshal("dto/accepted-request.xml");

        assertNotNull(dto);
        assertNotNull(dto.getStatusLink());
        assertEquals(dto.getStatusLink().getHref(), "http://foo/bar");
    }

    private void checkDocument(final String document) throws Exception
    {
        InputStream in = new ByteArrayInputStream(document.getBytes());
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(in);

        // Verify status link exists
        NodeList links = doc.getElementsByTagName("link");
        assertEquals(links.getLength(), 1);

        NamedNodeMap attributes = links.item(0).getAttributes();
        Node href = attributes.getNamedItem("href");
        Node rel = attributes.getNamedItem("rel");

        assertNotNull(href);
        assertNotNull(rel);
        assertEquals(href.getNodeValue(), "http://foo/bar");
        assertEquals(rel.getNodeValue(), "status");
    }

    private String marshal(final AcceptedRequestDto< ? > obj) throws JAXBException
    {
        StringWriter writer = new StringWriter();
        Marshaller m = context.createMarshaller();
        m.marshal(obj, writer);
        return writer.toString();
    }

    @SuppressWarnings("unchecked")
    private AcceptedRequestDto<String> unmarshal(final String location) throws JAXBException,
        IOException
    {
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(location);
        Unmarshaller um = context.createUnmarshaller();
        AcceptedRequestDto<String> dto = (AcceptedRequestDto<String>) um.unmarshal(in);
        in.close();
        return dto;
    }
}
