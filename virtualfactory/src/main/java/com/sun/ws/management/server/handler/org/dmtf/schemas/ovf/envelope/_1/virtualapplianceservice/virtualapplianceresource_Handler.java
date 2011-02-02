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

package com.sun.ws.management.server.handler.org.dmtf.schemas.ovf.envelope._1.virtualapplianceservice;

import org.dmtf.schemas.ovf.envelope._1.virtualapplianceservice.virtualapplianceresource.VirtualapplianceresourceHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.ws.management.Management;
import com.sun.ws.management.enumeration.Enumeration;
import com.sun.ws.management.eventing.Eventing;
import com.sun.ws.management.framework.handlers.DelegatingHandler;
import com.sun.ws.management.server.HandlerContext;

/**
 * This Handler delegates to the org.dmtf.schemas.ovf.envelope._1.virtualapplianceservice
 * .virtualapplianceresource.VirtualapplianceresourceHandler class. There is typically nothing to
 * implement in this class.
 * 
 * @GENERATED
 */
public class virtualapplianceresource_Handler extends DelegatingHandler
{
    // Log for logging messages
    @SuppressWarnings("unused")
    private final static Logger loggger =
        LoggerFactory.getLogger(virtualapplianceresource_Handler.class);

    private static final VirtualapplianceresourceHandler delegate =
        new VirtualapplianceresourceHandler();

    /** The Ws-management action to add a virtual system */
    public static final String ADD_VIRTUALSYSTEM_ACTION = "http://abiquo.com/virtualSystem/add";

    /** The Ws-management action to remove a virtual system */
    public static final String REMOVE_VIRTUALSYSTEM_ACTION =
        "http://abiquo.com/virtualSystem/remove";

    /** The Ws-management action to check the health check of a virtual system */
    public static final String BUNDLE_VIRTUALAPPLIANCE =
        "http://abiquo.com/VirtualAppliance/bundle";

    /** The Ws-management action to check the health check of a virtual system */
    public static final String CHECK_VIRTUALSYSTEM_ACTION =
        "http://abiquo.com/healthcheck/checkVirtualSystem";

    /**
     * Handler constructor.
     */
    public virtualapplianceresource_Handler()
    {
        super(delegate);
    }

    /**
     * Overridden handle operation to support the custom operation name mapping to wsa:Action uri
     * for SPEC Action URIs
     */
    @Override
    public void handle(String action, String resourceURI, HandlerContext context,
        Management request, Management response) throws Exception
    {
        if ("http://schemas.xmlsoap.org/ws/2004/09/transfer/Get".equals(action))
        {
            response.setAction("http://schemas.xmlsoap.org/ws/2004/09/transfer/GetResponse");
            delegate.Get(context, request, response);
            return;
        }
        if ("http://schemas.xmlsoap.org/ws/2004/08/eventing/Renew".equals(action))
        {
            response.setAction("http://schemas.xmlsoap.org/ws/2004/08/eventing/RenewResponse");
            delegate.RenewSubscriptionOp(context, new Eventing(request), new Eventing(response));
            return;
        }
        if ("http://schemas.xmlsoap.org/ws/2004/09/enumeration/Release".equals(action))
        {
            response.setAction("http://schemas.xmlsoap.org/ws/2004/09/enumeration/ReleaseResponse");
            delegate.ReleaseOp(context, new Enumeration(request), new Enumeration(response));
            return;
        }
        if ("http://schemas.xmlsoap.org/ws/2004/09/transfer/Delete".equals(action))
        {
            response.setAction("http://schemas.xmlsoap.org/ws/2004/09/transfer/DeleteResponse");
            delegate.Delete(context, request, response);
            return;
        }
        if ("http://schemas.xmlsoap.org/ws/2004/08/eventing/Subscribe".equals(action))
        {
            response.setAction("http://schemas.xmlsoap.org/ws/2004/08/eventing/SubscribeResponse");
            delegate.SubscribeOp(context, new Eventing(request), new Eventing(response));
            return;
        }
        if ("http://schemas.xmlsoap.org/ws/2004/09/enumeration/Enumerate".equals(action))
        {
            response
                .setAction("http://schemas.xmlsoap.org/ws/2004/09/enumeration/EnumerateResponse");
            delegate.EnumerateOp(context, new Enumeration(request), new Enumeration(response));
            return;
        }
        if ("http://schemas.xmlsoap.org/ws/2004/09/transfer/Create".equals(action))
        {
            response.setAction("http://schemas.xmlsoap.org/ws/2004/09/transfer/CreateResponse");
            delegate.Create(context, request, response);
            return;
        }
        if ("http://schemas.xmlsoap.org/ws/2004/08/eventing/Unsubscribe".equals(action))
        {
            response
                .setAction("http://schemas.xmlsoap.org/ws/2004/08/eventing/UnsubscribeResponse");
            delegate.UnsubscribeOp(context, new Eventing(request), new Eventing(response));
            return;
        }
        if ("http://schemas.xmlsoap.org/ws/2004/09/transfer/Put".equals(action))
        {
            response.setAction("http://schemas.xmlsoap.org/ws/2004/09/transfer/PutResponse");
            delegate.Put(context, request, response);
            return;
        }
        if ("http://schemas.xmlsoap.org/ws/2004/09/enumeration/Pull".equals(action))
        {
            response.setAction("http://schemas.xmlsoap.org/ws/2004/09/enumeration/PullResponse");
            delegate.PullOp(context, new Enumeration(request), new Enumeration(response));
            return;
        }
        if ("http://schemas.xmlsoap.org/ws/2004/09/enumeration/GetStatus".equals(action))
        {
            response
                .setAction("http://schemas.xmlsoap.org/ws/2004/09/enumeration/GetStatusResponse");
            delegate.GetStatusOp(context, new Enumeration(request), new Enumeration(response));
            return;
        }
        if ("http://schemas.xmlsoap.org/ws/2004/09/enumeration/Renew".equals(action))
        {
            response.setAction("http://schemas.xmlsoap.org/ws/2004/09/enumeration/RenewResponse");
            delegate.RenewOp(context, new Enumeration(request), new Enumeration(response));
            return;
        }
        if ("http://schemas.xmlsoap.org/ws/2004/08/eventing/GetStatus".equals(action))
        {
            response.setAction("http://schemas.xmlsoap.org/ws/2004/08/eventing/GetStatusResponse");
            delegate
                .GetSubscriptionStatusOp(context, new Eventing(request), new Eventing(response));
            return;
        }
        if (CHECK_VIRTUALSYSTEM_ACTION.equals(action))
        {
            response.setAction(CHECK_VIRTUALSYSTEM_ACTION + "Response");
            delegate.CheckVirtualSystem(context, request, response);
            return;
        }
        if (BUNDLE_VIRTUALAPPLIANCE.equals(action))
        {
            response.setAction(BUNDLE_VIRTUALAPPLIANCE + "Response");
            delegate.BundleVirtualAppliance(context, request, response);
            return;
        }
        if (ADD_VIRTUALSYSTEM_ACTION.equals(action))
        {
            response.setAction(ADD_VIRTUALSYSTEM_ACTION + "Response");
            delegate.AddVirtualSystem(context, request, response);
            return;
        }
        if (REMOVE_VIRTUALSYSTEM_ACTION.equals(action))
        {
            response.setAction(REMOVE_VIRTUALSYSTEM_ACTION + "Response");
            delegate.RemoteVirtualSystem(context, request, response);
            return;
        }
        // be sure to call to super to ensure all operations are handled.
        super.handle(action, resourceURI, context, request, response);
    }
}
