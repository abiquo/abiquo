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

package com.abiquo.nodecollector.aim.impl;

import java.io.IOException;

import javax.xml.bind.JAXBException;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3._2003._05.soap_envelope.Fault;
import org.w3._2003._05.soap_envelope.Reasontext;

import com.abiquo.nodecollector.aim.WsmanCollector;
import com.abiquo.nodecollector.constants.MessageValues;
import com.abiquo.nodecollector.exception.libvirt.WsmanException;
import com.sun.ws.management.Management;
import com.sun.ws.management.ManagementMessageValues;
import com.sun.ws.management.addressing.Addressing;
import com.sun.ws.management.identify.Identify;
import com.sun.ws.management.transport.BasicAuthenticator;
import com.sun.ws.management.transport.HttpClient;

/**
 * {@link WsmanCollector} implementation.
 */
public class WsmanCollectorImpl implements WsmanCollector
{
    /** The constant logger object. */
    private static final Logger LOG = LoggerFactory.getLogger(WsmanCollectorImpl.class);

    /** Authentication user name to WS-MAN server. */
    private String wsmanUser;

    /** Authentication user password to WS-MAN server. */
    private String wsmanPassword;

    /** Port to all the WS-MAN servers. From configuration file */
    private Integer wsmanPort;

    /** The Demand information. */
    private Management demand;

    /** Values of the managem */
    protected static ManagementMessageValues values;

    protected static Addressing addressMessage;

    protected static Addressing response;

    /**
     * The wsman collector constructor.
     * 
     * @param wsmanUser wsman user name
     * @param wsmanPassword wsman password
     * @param wsmanPort wsman port
     */
    public WsmanCollectorImpl(final String wsmanUser, final String wsmanPassword,
        final Integer wsmanPort)
    {
        this.wsmanUser = wsmanUser;
        this.wsmanPassword = wsmanPassword;
        this.wsmanPort = wsmanPort;
        
        try
        {
            if (values == null)
            {
                values = ManagementMessageValues.newInstance();
            }
            if (addressMessage == null)
            {
                addressMessage = new Addressing();
            }
            if (response == null)
            {
                response = new Addressing();
            }

        }
        catch (SOAPException e)
        {
            LOG.error("Can not create subscription to WSMAN [{}]", e);
        }
    }

   

    @Override
    public void pingWsmanService(final String physicalMachineIp) throws WsmanException
    {
        Addressing responsecheck = null;
        try
        {
            // declare needed variables
            final String destination = createAIMURL(physicalMachineIp);
            final Identify identify = new Identify(); // TODO: static

            // authenticates to wsman
            authenticateHttp();

            // prepare the identify message
            identify.getEnvelope().addNamespaceDeclaration("wsmid",
                "http://schemas.dmtf.org/wbem/wsman/identity/1/wsmanidentity.xsd");
            identify.getBody().addChildElement("Identify", "wsmid");

            // get and check the response
            responsecheck = HttpClient.sendRequest(identify.getMessage(), destination);
            checkResponseFault(responsecheck);

            // checks SOAP message for WSMan service
            final SOAPElement[] idr =
                responsecheck.getChildren(responsecheck.getBody(), Identify.IDENTIFY_RESPONSE);
            if (idr.length == 0)
            {
                throw new WsmanException(MessageValues.WSMAN_NO_PING);
            }

        }
        catch (SOAPException e)
        {
            throw new WsmanException(MessageValues.WSMAN_NO_PING, e);
        }
        catch (IOException e)
        {
            throw new WsmanException(MessageValues.WSMAN_NO_PING, e);
        }
        catch (JAXBException e)
        {
            throw new WsmanException(MessageValues.WSMAN_NO_PING, e);
        }
        finally
        {
            responsecheck = null;
            response = null;
            System.clearProperty("wsman.user");
            System.clearProperty("wsman.password");
            HttpClient.setAuthenticator(new BasicAuthenticator());
        }

    }

    /**
     * Codify the WS-MAN location based on the target IP.
     * 
     * @param machineIp the physical machine IP.
     * @return the WS-MAN plugin location on the IP.
     */
    private String createAIMURL(final String machineIp)
    {
        return "http://" + machineIp + ":" + wsmanPort + "/wsman";
    }

    /**
     * Enable HTTP authentication (from configuration file) on the WS demand request.
     */
    private void authenticateHttp()
    {
        // Enable basic authentication for tests
        System.getProperties().put("wsman.user", wsmanUser);
        System.getProperties().put("wsman.password", wsmanPassword);
        HttpClient.setAuthenticator(new BasicAuthenticator());
    }

    /**
     * Checks if after a wsman call has been any response.
     * 
     * @param response response object to WSMAN call
     * @throws SOAPException if the fault is not parsed correctly
     * @throws JAXBException if the fault is not parsed correctly
     * @throws WsmanException if fault is not null
     */
    private void checkResponseFault(final Addressing response) throws JAXBException, SOAPException,
        WsmanException
    {
        final Fault fault = response.getFault();

        if (fault != null && fault.getReason() != null && fault.getReason().getText() != null)
        {
            final StringBuffer errorBuild = new StringBuffer();
            for (Reasontext rt : fault.getReason().getText())
            {
                errorBuild.append(rt.getValue()).append('\n');
            }

            throw new WsmanException(errorBuild.toString());
        }
    }

    /**
     * @return the wsmanUser
     */
    public String getWsmanUser()
    {
        return wsmanUser;
    }

    /**
     * @param wsmanUser the wsmanUser to set
     */
    public void setWsmanUser(final String wsmanUser)
    {
        this.wsmanUser = wsmanUser;
    }

    /**
     * @return the wsmanPassword
     */
    public String getWsmanPassword()
    {
        return wsmanPassword;
    }

    /**
     * @param wsmanPassword the wsmanPassword to set
     */
    public void setWsmanPassword(final String wsmanPassword)
    {
        this.wsmanPassword = wsmanPassword;
    }

    /**
     * @return the wsmanPort
     */
    public Integer getWsmanPort()
    {
        return wsmanPort;
    }

    /**
     * @param wsmanPort the wsmanPort to set
     */
    public void setWsmanPort(final Integer wsmanPort)
    {
        this.wsmanPort = wsmanPort;
    }

}
