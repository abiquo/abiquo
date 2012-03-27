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
package com.abiquo.appliancemanager.web.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.am.exceptions.AMExceptionMapper;
import com.abiquo.appliancemanager.config.AMConfiguration;
import com.abiquo.appliancemanager.exceptions.AMException;
import com.abiquo.model.transport.error.ErrorDto;
import com.abiquo.ovfmanager.ovf.xml.Stax2Factory;

/**
 * Base implementation of the Check Servlet.
 * <p>
 * Each Remote Service in the platform must implement its own <code>CheckServlet</code> to let
 * consumers test its availability.
 * 
 * @author ibarrera
 */
public abstract class AbstractCheckServlet extends HttpServlet
{
    /** The logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCheckServlet.class);

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    static JAXBContext contextError;

    static
    {
        try
        {
            contextError = JAXBContext.newInstance(new Class[] {ErrorDto.class});
        }
        catch (JAXBException e)
        {
            e.printStackTrace();
            LOGGER.error("Can't initialize ErrorDto serializer");
        }
    }

    /**
     * Performs a check to validate Remote Service status.
     * 
     * @return A boolean indicating the status of the Remote Service.
     * @throws Exception If check operation fails or the Remote Service is not available.
     */
    protected abstract boolean check() throws Exception;

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
        throws ServletException, IOException
    {
        try
        {
            if (check())
            {
                success(resp, AMConfiguration.getRepositoryLocation());
            }
            else
            {
                fail(resp, "Can't check Appliance Manager");
            }
        }
        catch (AMException ex)
        {
            LOGGER.warn("Check operation failed {}", ex.getError().getMessage());
            fail(resp, toString(AMExceptionMapper.createError(ex)));
        }
        catch (Exception e)
        {
            fail(resp, e.getMessage());
        }
    }

    final private static QName errorsQname = new QName("error");

    private String toString(final ErrorDto error)
    {
        ByteArrayOutputStream boutput = new ByteArrayOutputStream();
        XMLStreamWriter writer = null;

        try
        {
            writer = Stax2Factory.createXMLStreamWriter(boutput);
            contextError.createMarshaller().marshal(
                new JAXBElement<ErrorDto>(errorsQname, ErrorDto.class, null, error)//
                , writer);

            return boutput.toString();
        }
        catch (JAXBException e)
        {
            throw new RuntimeException(e);
        }
        catch (XMLStreamException e)
        {
            throw new RuntimeException(e);
        }
        finally
        {

            try
            {
                if (writer != null)
                {
                    writer.close();
                }
            }
            catch (XMLStreamException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * Returns a {@link HttpServletResponse#SC_OK} HTTP code indicating that the Remote Service is
     * available.
     * 
     * @param resp The Response.
     * @throws IOException
     */
    protected void success(final HttpServletResponse resp, final String bodyContent)
        throws IOException
    {
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(bodyContent);
    }

    /**
     * Returns a {@link HttpServletResponse#SC_SERVICE_UNAVAILABLE} HTTP code indicating that the
     * Remote Service is not available.
     * 
     * @param resp The Response.
     * @param msg The details of the check failure.
     * @throws If error code cannot be sent.
     */
    protected void fail(final HttpServletResponse resp, final String msg) throws IOException
    {
        resp.setContentType(MediaType.TEXT_PLAIN);
        resp.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        resp.getWriter().write(msg);
    }

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp)
        throws ServletException, IOException
    {
        doGet(req, resp);
    }
}
