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
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPException;

import com.abiquo.abiserver.business.hibernate.pojohb.virtualhardware.ResourceAllocationSettingData;
import com.abiquo.abiserver.exception.PersistenceException;
import com.abiquo.abiserver.pojo.infrastructure.VirtualMachine;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.sun.ws.management.client.exceptions.FaultException;

public interface IInfrastructureWS
{

    /**
     * Performs the action in Abicloud associated with the attribute "state" in the virtual machine
     * 
     * @param virtualMachine the virtual machine to perform the state change
     * @param actionState the action state to perform
     * @return a basic result
     * @throws Exception
     */
    public BasicResult setVirtualMachineState(VirtualMachine virtualMachine, String actionState)
        throws Exception;

    /**
     * Update the VM configuration without changing the VM state.
     * 
     * @param virtualMachine The VM to update
     * @param additionalRasds The additionsl resources to consider
     * @return
     * @throws Exception
     */
    public BasicResult updateVirtualMachineConfiguration(final VirtualMachine virtualMachine,
        final List<ResourceAllocationSettingData> additionalRasds) throws Exception;

    /**
     * Edits the virtualMachine with a new configuration
     * 
     * @param virtualMachine the virtual machine to edit
     * @return
     */
    public BasicResult editVirtualMachine(VirtualMachine virtualMachine);

    /**
     * Invokes a pull subscribe to the virtual system monitor to forces the events refreshing
     * 
     * @param virtualMachine the virtual virtualMachine to be refreshed
     * @return a basicResult with the resulting operation
     */
    public BasicResult forceRefreshVirtualMachineState(VirtualMachine virtualMachine);

    /**
     * Deletes the virtual machine
     * 
     * @param virtualMachine the virtual machine to delete
     * @return a basic result
     */
    public BasicResult deleteVirtualMachine(VirtualMachine virtualMachine);

    /**
     * Checks the virtual machine health
     * 
     * @param virtualMachine the virtual machine to check
     * @return TODO
     */
    public Boolean checkVirtualSystem(VirtualMachine virtualMachine);

    /**
     * Adds a virtual System
     * 
     * @param virtualMachine the virtual machine to add
     * @return
     * @throws DatatypeConfigurationException
     * @throws FaultException
     * @throws IOException
     * @throws JAXBException
     * @throws SOAPException
     * @throws ParserConfigurationException
     */
    public BasicResult addVirtualSystem(VirtualMachine virtualMachine) throws SOAPException,
        JAXBException, IOException, FaultException, DatatypeConfigurationException,
        ParserConfigurationException;

    /**
     * Removes a virtual system
     * 
     * @param virtualMachine the virtual system to remove
     * @return
     * @throws ParserConfigurationException
     * @throws JAXBException
     * @throws DatatypeConfigurationException
     * @throws FaultException
     * @throws IOException
     * @throws SOAPException
     * @throws PersistenceException
     */
    public BasicResult removeVirtualSystem(VirtualMachine virtualMachine) throws JAXBException,
        ParserConfigurationException, PersistenceException, SOAPException, IOException,
        FaultException, DatatypeConfigurationException;
}
