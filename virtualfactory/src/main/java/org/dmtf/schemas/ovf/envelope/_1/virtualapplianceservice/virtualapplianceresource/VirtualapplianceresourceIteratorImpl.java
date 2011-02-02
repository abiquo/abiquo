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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import org.dmtf.schemas.ovf.envelope._1.EnvelopeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlsoap.schemas.ws._2004._08.addressing.EndpointReferenceType;

import com.abiquo.ovfmanager.ovf.xml.OVFSerializer;
import com.abiquo.virtualfactory.model.VirtualAppliance;
import com.abiquo.virtualfactory.model.VirtualApplianceModel;
import com.abiquo.virtualfactory.virtualappliance.impl.VirtualapplianceresourceDeployer;
import com.sun.ws.management.InternalErrorFault;
import com.sun.ws.management.framework.transfer.TransferSupport;
import com.sun.ws.management.server.EnumerationItem;

/**
 * Specific implementations of VirtualApplianceResourceIterator The class to be presented by a data
 * source that would like to be enumerated.
 * 
 * @see com.sun.ws.management.server.EnumerationIterator
 */
public class VirtualapplianceresourceIteratorImpl
{
    private static final String WSMAN_VIRTUALAPPLIANCE_RESOURCE =
        "http://schemas.dmtf.org/ovf/envelope/1/virtualApplianceService/virtualApplianceResource";

    private static final Logger loggger =
        LoggerFactory.getLogger(VirtualapplianceresourceIteratorImpl.class);

    private int length;

    private final String address;

    private final boolean includeEPR;

    private Iterator<VirtualAppliance> virtualAppliances;

    /**
     * Standard constructor
     * 
     * @param address the addres
     * @param includeEPR a flag to include EPR
     */
    public VirtualapplianceresourceIteratorImpl(String address, boolean includeEPR)
    {
        this.address = address;
        this.includeEPR = includeEPR;

        loggger.info("Created VirtualapplianceresourceIteratorImpl to address " + address);

        release();
    }

    /**
     * Supplys the next element of the iteration. This is invoked to satisfy a
     * {@link org.xmlsoap.schemas.ws._2004._09.enumeration.Pull Pull} request. The operation must
     * return within the {@link org.xmlsoap.schemas.ws._2004._09.enumeration.Pull#getMaxTime
     * timeout} specified in the {@link org.xmlsoap.schemas.ws._2004._09.enumeration.Pull Pull}
     * request, otherwise {@link #release release} will be invoked and the current thread
     * interrupted. When cancelled, the implementation can return the result currently accumulated
     * (in which case no {@link com.sun.ws.management.soap.Fault Fault} is generated) or it can
     * return {@code null} in which case a {@link com.sun.ws.management.enumeration.TimedOutFault
     * TimedOutFault} is returned.
     * 
     * @return an {@link EnumerationElement Element} that is used to construct a proper response for
     *         a {@link org.xmlsoap.schemas.ws._2004._09.enumeration.PullResponse PullResponse}.
     */
    public EnumerationItem next()
    {
        // Get the next VirtualAppliance
        VirtualAppliance virtualAppliance = virtualAppliances.next();
        Map<String, String> selectors = new HashMap<String, String>();

        selectors.put("id", virtualAppliance.getVirtualApplianceId());

        try
        {
            final EndpointReferenceType epr;

            if (includeEPR)
            {
                epr =
                    TransferSupport.createEpr(address, WSMAN_VIRTUALAPPLIANCE_RESOURCE, selectors);
            }
            else
            {
                epr = null;
            }

            // Create the EnumerationItem and return it
            VirtualapplianceresourceDeployer deployer = new VirtualapplianceresourceDeployer();
            EnvelopeType envelope = deployer.createEnvelopeType(virtualAppliance);

            JAXBElement<EnvelopeType> vSystemElement =
                OVFSerializer.getInstance().toJAXBElement(envelope);

            // TODO think about filtering and including items
            /*
             * // Always return item if filtering is done by EnumerationSupport if
             * ((this.includeItem) || (this.isFiltered == false)) { ee = new EnumerationItem(item,
             * epr); } else { ee = new EnumerationItem(null, epr); }
             */
            return new EnumerationItem(vSystemElement, epr);
        }
        catch (JAXBException e)
        {
            throw new InternalErrorFault(e.getMessage());
        }
        catch (Exception e) // RequiredAttributeException IdAlreadyExistsException
                            // SectionAlreadyPresentException SectionException
        {
            // TODO Auto-generated catch block
            throw new InternalErrorFault(e);
        }

    }

    /**
     * Estimates the total number of elements available.
     * 
     * @return an estimate of the total number of elements available in the enumeration. Return a
     *         negative number if an estimate is not available.
     */
    public int estimateTotalItems()
    {
        return length;
    }

    /**
     * Indicates if there are more elements remaining in the iteration.
     * 
     * @return {@code true} if there are more elements in the iteration, {@code false} otherwise.
     */
    public boolean hasNext()
    {
        return virtualAppliances.hasNext();
    }

    /**
     * Releases any resources being used by the iterator. Calls to other methods of this iterator
     * instance will exhibit undefined behaviour, after this method completes.
     */
    public void release()
    {
        this.length = VirtualApplianceModel.getModel().getVirtualAppliances().size();
        this.virtualAppliances = VirtualApplianceModel.getModel().getVirtualAppliances().iterator();
    }
}
