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
package com.abiquo.virtualfactory.utils;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Utility functions to parse XML using Xpath.
 * 
 * @author ibarrera
 */
public final class XPathUtils
{
    /**
     * Evaluates the XPath expression against the <code>xml</code> and returns the selected value.
     * 
     * @param expression Expression to evaluate.
     * @param xml The xml to query.
     * @return The selected value.
     * @throws XPathExpressionException If an error occurs evaluating the expression.
     */
    public static String getValue(final String expression, final String xml)
        throws XPathExpressionException
    {
        final InputSource source = new InputSource(new StringReader(xml));
        final XPathFactory factory = XPathFactory.newInstance();
        final XPath xpath = factory.newXPath();

        return xpath.evaluate(expression, source);
    }

    /**
     * Evaluates the XPath expression against the <code>xml</code> and returns the selected values.
     * 
     * @param expression Expression to evaluate.
     * @param xml The xml to query.
     * @return The selected values.
     * @throws XPathExpressionException If an error occurs evaluating the expression.
     */
    public static List<String> getValues(final String expression, final String xml)
        throws XPathExpressionException
    {
        final InputSource source = new InputSource(new StringReader(xml));
        final XPathFactory factory = XPathFactory.newInstance();
        final XPath xpath = factory.newXPath();
        final NodeList xpathResults =
            (NodeList) xpath.evaluate(expression, source, XPathConstants.NODESET);
        final List<String> results = new ArrayList<String>(xpathResults.getLength());

        for (int i = 0; i < xpathResults.getLength(); i++)
        {
            results.add(xpathResults.item(i).getNodeValue());
        }

        return results;
    }

    /**
     * Default constructor.
     */
    private XPathUtils()
    {
        super();
    }

}
