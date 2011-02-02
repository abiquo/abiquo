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

package org.dmtf.schemas.ovf.envelope._1.virtualapplianceservice.virtualapplianceresource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.abiquo.virtualfactory.context.ApplicationContextProvider;
import com.abiquo.virtualfactory.virtualappliance.VirtualapplianceresourceDeployable;
import com.sun.ws.management.InternalErrorFault;
import com.sun.ws.management.Management;
import com.sun.ws.management.enumeration.Enumeration;
import com.sun.ws.management.eventing.Eventing;
import com.sun.ws.management.framework.handlers.ResourceHandler;
import com.sun.ws.management.server.EnumerationSupport;
import com.sun.ws.management.server.HandlerContext;

/**
 * VirtualapplianceresourceHandler delegate is responsible for processing enumeration actions.
 * 
 * @GENERATED
 */
public class VirtualapplianceresourceHandler extends ResourceHandler
{

    public static final String resourceURI =
        "http://schemas.dmtf.org/ovf/envelope/1/virtualApplianceService/virtualApplianceResource";

    // Log for logging messages
    @SuppressWarnings("unused")
    private final static Logger logger =
        LoggerFactory.getLogger(VirtualapplianceresourceHandler.class);

    // deployable class which calls all the resources
    private VirtualapplianceresourceDeployable deployer;

    public VirtualapplianceresourceHandler()
    {
        super();

        ApplicationContext ctx = ApplicationContextProvider.getApplicationContext();
        deployer = (VirtualapplianceresourceDeployable) ctx.getBean("deployer");

        try
        {
            // Register the IteratorFactory with EnumerationSupport
            EnumerationSupport
                .registerIteratorFactory(
                    "http://schemas.dmtf.org/ovf/envelope/1/virtualApplianceService/virtualApplianceResource",
                    new VirtualapplianceresourceIteratorFactory("http://schemas.dmtf.org/ovf/envelope/1/virtualApplianceService/virtualApplianceResource"));
        }
        catch (Exception e)
        {
            throw new InternalErrorFault(e);
        }
    }

    public void Get(HandlerContext context, Management request, Management response)
    {
        deployer.get(request, response);
    }

    public void RenewSubscriptionOp(HandlerContext context, Eventing request, Eventing response)
    {
        // TODO: For subscribe:
        // Call EventSupport.subscribe() && save the UUID returned (use
        // ContextListener to detect subscribe/unsubscribes)
        // Start sending events to EventSupport.sendEvent(uuid, event)

        // TODO: For unsubscribe:
        // Call EventSupport.unsubscribe()
        // Stop sending events for this UUID
        super.renewSubscription(context, request, response);
    }

    public void ReleaseOp(HandlerContext context, Enumeration request, Enumeration response)
    {
        super.release(context, request, response);
    }

    public void Delete(HandlerContext context, Management request, Management response)
    {
        deployer.delete(request, response);
    }

    public void SubscribeOp(HandlerContext context, Eventing request, Eventing response)
    {
        // TODO: For subscribe:
        // Call EventSupport.subscribe() && save the UUID returned (use
        // ContextListener to detect subscribe/unsubscribes)
        // Start sending events to EventSupport.sendEvent(uuid, event)

        // TODO: For unsubscribe:
        // Call EventSupport.unsubscribe()
        // Stop sending events for this UUID
        super.subscribe(context, request, response);
    }

    public void EnumerateOp(HandlerContext context, Enumeration request, Enumeration response)
    {
        super.enumerate(context, request, response);
    }

    public void Create(HandlerContext context, Management request, Management response)
    {
        // super.create(context, request, response);
        deployer.create(request, response);
    }

    public void UnsubscribeOp(HandlerContext context, Eventing request, Eventing response)
    {
        // TODO: For subscribe:
        // Call EventSupport.subscribe() && save the UUID returned (use
        // ContextListener to detect subscribe/unsubscribes)
        // Start sending events to EventSupport.sendEvent(uuid, event)

        // TODO: For unsubscribe:
        // Call EventSupport.unsubscribe()
        // Stop sending events for this UUID
        super.unsubscribe(context, request, response);
    }

    public void Put(HandlerContext context, Management request, Management response)
    {
        deployer.put(request, response);
    }

    public void PullOp(HandlerContext context, Enumeration request, Enumeration response)
    {
        super.pull(context, request, response);
    }

    public void GetStatusOp(HandlerContext context, Enumeration request, Enumeration response)
    {
        super.getStatus(context, request, response);
    }

    public void RenewOp(HandlerContext context, Enumeration request, Enumeration response)
    {
        super.renew(context, request, response);
    }

    public void GetSubscriptionStatusOp(HandlerContext context, Eventing request, Eventing response)
    {
        // TODO: For subscribe:
        // Call EventSupport.subscribe() && save the UUID returned (use
        // ContextListener to detect subscribe/unsubscribes)
        // Start sending events to EventSupport.sendEvent(uuid, event)

        // TODO: For unsubscribe:
        // Call EventSupport.unsubscribe()
        // Stop sending events for this UUID
        super.getSubscriptionStatus(context, request, response);
    }

    public void CheckVirtualSystem(HandlerContext context, Management request, Management response)
    {
        deployer.checkVirtualSystem(request, response);
    }

    public void BundleVirtualAppliance(HandlerContext context, Management request,
        Management response)
    {
        deployer.bundleVirtualAppliance(request, response);
    }

    public void AddVirtualSystem(HandlerContext context, Management request, Management response)
    {
        deployer.addVirtualSystem(request, response);
    }

    public void RemoteVirtualSystem(HandlerContext context, Management request, Management response)
    {
        deployer.removeVirtualSystem(request, response);
    }

    /**
     * @return
     */
    public VirtualapplianceresourceDeployable getDeployer()
    {
        return deployer;
    }

    public void setDeployer(VirtualapplianceresourceDeployable deployer)
    {
        this.deployer = deployer;
    }

}
