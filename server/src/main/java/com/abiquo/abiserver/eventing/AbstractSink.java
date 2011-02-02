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

package com.abiquo.abiserver.eventing;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServlet;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPException;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.sun.ws.management.Management;

/**
 * Abstract class representing an event sink.
 * 
 * @author dcalavera
 */
public abstract class AbstractSink extends HttpServlet
{

    /**
     * 
     */
    private static final long serialVersionUID = -1012848931629594658L;

    protected Node getNode(InputStream input) throws IOException, SOAPException, EventingException
    {

        Management man = new Management(input);
        SOAPBody body = man.getBody();
        Node eventNode = body.getFirstChild();
        if (eventNode == null || body.hasFault())
        {
            throw new EventingException("An unknown event was received and is not being treated");
        }
        return eventNode;
    }

    protected String getAttributeString(NamedNodeMap eventAttributes, String prefix, String value)
    {
        String retu = null;

        Node node = eventAttributes.getNamedItem(prefix + ":" + value);

        if (node == null)
        {
            Node namedItem = eventAttributes.getNamedItem(value);
            if (namedItem != null)
            {
                retu = namedItem.getNodeValue();
            }
        }
        else
        {
            retu = node.getNodeValue();
        }

        return retu;
    }
}
