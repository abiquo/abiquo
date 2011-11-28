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

package com.abiquo.abiserver.abicloudws;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPException;

import com.abiquo.abiserver.exception.PersistenceException;
import com.abiquo.abiserver.exception.VirtualFactoryHealthException;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.virtualappliance.Node;
import com.abiquo.abiserver.pojo.virtualappliance.NodeVirtualImage;
import com.abiquo.abiserver.pojo.virtualappliance.VirtualAppliance;
import com.sun.ws.management.client.exceptions.FaultException;

public interface IVirtualApplianceWS
{

    /**
     * Performs a "Start" action in the Virtual Machine
     * 
     * @param virtualAppliance
     * @return a BasicResult
     * @throws Exception
     */
    public BasicResult startVirtualAppliance(final VirtualAppliance virtualAppliance)
        throws Exception;

    /**
     * Performs a "Shutdown" action in the Virtual Machine
     * 
     * @param virtualAppliance
     * @return a BasicResult
     * @throws Exception
     */
    public BasicResult shutdownVirtualAppliance(final VirtualAppliance virtualAppliance)
        throws Exception;

    /**
     * Deletes a VirtualAppliance that exists in the Data Base
     * 
     * @param virtualAppliance
     * @return a BasicResult object, containing success = true if the deletion was successful
     * @throws Exception
     */
    public BasicResult deleteVirtualAppliance(final VirtualAppliance virtualAppliance)
        throws Exception;

    /**
     * Modifies the information of a VirtualAppliance that already exists in the Data Base
     * 
     * @param virtualAppliance the new virtual appliance with the new changes to update
     * @return A DataResult object, containing a list of nodes modified
     * @throws Exception
     */
    public BasicResult editVirtualAppliance(final VirtualAppliance virtualAppliance)
        throws Exception;

    /**
     * Helper to refresh the virtual appliance in the virtualfactory
     * 
     * @param virtualAppliance the virtual factory to refresh
     * @throws Exception
     */
    public Boolean checkVirtualAppliance(final VirtualAppliance virtualAppliance) throws Exception;

    /**
     * It bundles the virtualAppliance. The Virtual Appliance contains the nodes to be bundled
     * 
     * @param virtualAppliance the virtual Appliance to bundle
     * @return a baiscResult with the resulting operation
     * @throws Exception
     */
    public BasicResult bundleVirtualAppliance(final VirtualAppliance virtualAppliance)
        throws Exception;

    /**
     * Invokes a pull subscribe to the virtual system monitor to forces the events refreshing
     * 
     * @param virtualAppliance the virtual appliance to be refreshed
     * @return a basicResult with the resulting operation
     */
    public BasicResult forceRefreshVirtualApplianceState(final VirtualAppliance virtualAppliance);

    /**
     * Roll backs the event subscription to the virtual appliance
     * 
     * @param virtualAppliance
     */
    public void rollbackEventSubscription(final VirtualAppliance virtualAppliance);

    public BasicResult removeNodes(List<Node> nodesToDelete) throws PersistenceException,
        JAXBException, ParserConfigurationException, SOAPException, IOException, FaultException,
        DatatypeConfigurationException, VirtualFactoryHealthException;

    public Boolean checkRemovedNodes(final VirtualAppliance virtualAppliance)
        throws VirtualFactoryHealthException;

}
