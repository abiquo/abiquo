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

package com.abiquo.virtualfactory.model.ovf;

import java.net.MalformedURLException;
import java.util.Map;

import org.dmtf.schemas.ovf.envelope._1.ContentType;
import org.dmtf.schemas.ovf.envelope._1.EnvelopeType;
import org.dmtf.schemas.ovf.envelope._1.VirtualSystemCollectionType;
import org.dmtf.schemas.ovf.envelope._1.VirtualSystemType;

import com.abiquo.ovfmanager.ovf.exceptions.EmptyEnvelopeException;
import com.abiquo.ovfmanager.ovf.exceptions.IdNotFoundException;
import com.abiquo.ovfmanager.ovf.exceptions.InvalidSectionException;
import com.abiquo.ovfmanager.ovf.exceptions.RequiredAttributeException;
import com.abiquo.ovfmanager.ovf.exceptions.SectionAlreadyPresentException;
import com.abiquo.ovfmanager.ovf.exceptions.SectionException;
import com.abiquo.ovfmanager.ovf.exceptions.SectionNotPresentException;
import com.abiquo.virtualfactory.exception.HypervisorException;
import com.abiquo.virtualfactory.exception.PluginException;
import com.abiquo.virtualfactory.exception.VirtualMachineException;
import com.abiquo.virtualfactory.exception.VirtualNetworkException;
import com.abiquo.virtualfactory.model.AbsVirtualMachine;
import com.abiquo.virtualfactory.model.VirtualAppliance;
import com.abiquo.virtualfactory.model.VirtualDisk;
import com.abiquo.virtualfactory.model.config.HypervisorConfiguration;
import com.abiquo.virtualfactory.model.config.VirtualMachineConfiguration;

/**
 * @author jdevesa@abiquo.com
 */
public interface OVFModelConvertable
{
    /**
     * @param virtualAppliance
     * @param virtualSystemInstance
     * @param virtualDiskMap
     * @param envelope
     * @throws MalformedURLException
     * @throws VirtualMachineException
     * @throws SectionNotPresentException
     * @throws SectionException
     * @throws IdNotFoundException
     * @throws RequiredAttributeException
     * @throws HypervisorException
     * @throws PluginException
     */
    public void addMachinesToVirtualAppliance(VirtualAppliance virtualAppliance,
        VirtualSystemType virtualSystemInstance, Map<String, VirtualDisk> virtualDiskMap,
        EnvelopeType envelope) throws MalformedURLException, VirtualMachineException,
        SectionNotPresentException, SectionException, RequiredAttributeException,
        IdNotFoundException, PluginException, HypervisorException;

    /**
     * @param virtualMachine
     * @param virtualSystem
     * @param configureDisks
     * @throws VirtualMachineException
     * @throws SectionException
     * @throws Exception
     */
    public void reconfigureVirtualSystem(final AbsVirtualMachine virtualMachine,
        final ContentType virtualSystem, final HypervisorConfiguration hvConfig)
        throws VirtualMachineException, SectionException, Exception;

    /**
     * NOTE: In the future certain measures would have to be taken if the Hypervisor crashes and all
     * information is lost, in which case the Virtual Machines will have to be created all over
     * again. For the moment, the boolean writeToDB is used as analogous to deploying the virtual
     * machine i.e when writeToDB is false (when a retrieval from the BBDD is being done), a virtual
     * machine doesn't need to be deployed either
     * 
     * @param envelope EnvelopeType envelope section
     * @param virtualApp virtual appliance to deploy
     * @param virtualDisks all the virtual disks related to the virtual appliance
     * @return
     * @throws MalformedURLException
     * @throws VirtualMachineException
     * @throws IdNotFoundException
     * @throws EmptyEnvelopeException
     * @throws SectionException
     * @throws RequiredAttributeException
     * @throws VirtualNetworkException
     * @throws HypervisorException
     * @throws PluginException
     */
    public VirtualAppliance createVirtualAppliance(EnvelopeType envelope,
        VirtualAppliance virtualApp, Map<String, VirtualDisk> virtualDiskMap)
        throws MalformedURLException, VirtualMachineException, IdNotFoundException,
        EmptyEnvelopeException, SectionException, RequiredAttributeException,
        VirtualNetworkException, PluginException, HypervisorException;

    /**
     * Return a virtual disk set with the properly file location (using References on the Envelope)
     * 
     * @param envelope
     * @return
     * @throws IdNotFoundException
     * @throws SectionException
     */
    public Map<String, VirtualDisk> createVirtualDisks(EnvelopeType envelope)
        throws IdNotFoundException, SectionException;

    /**
     * Helper to create a VirtualSystem from a machine description
     * 
     * @param machine
     * @return
     * @throws RequiredAttributeException
     * @throws SectionAlreadyPresentException
     * @throws SectionException
     */
    public VirtualSystemType createVirtualSystem(AbsVirtualMachine machine)
        throws RequiredAttributeException, SectionAlreadyPresentException, SectionException;

    /**
     * @param virtualSystem
     * @return
     * @throws SectionException
     */
    public String getMachineStateFromAnnotation(ContentType virtualSystem) throws SectionException;

    /**
     * Gets the hypervisor identifier (equals to machienId)
     * 
     * @param virtualSystem
     * @return
     * @throws SectionException
     */
    public String getVSSDInstanceId(ContentType virtualSystem) throws SectionException;

    /**
     * @param virtualSystemCollection
     * @param virtualAppliance
     * @param virtualDiskMap
     * @param envelope
     * @throws MalformedURLException
     * @throws VirtualMachineException
     * @throws SectionException
     * @throws IdNotFoundException
     * @throws RequiredAttributeException
     * @throws HypervisorException
     * @throws PluginException
     */
    public void updateVirtualSystemCollection(VirtualSystemCollectionType virtualSystemCollection,
        VirtualAppliance virtualAppliance, Map<String, VirtualDisk> virtualDiskMap,
        EnvelopeType envelope) throws MalformedURLException, VirtualMachineException,
        SectionException, IdNotFoundException, RequiredAttributeException, PluginException,
        HypervisorException;

    /**
     * Gets the virtual app state to perform the DHCP to change
     * 
     * @param contentInstance
     * @return
     * @throws SectionException
     */
    public String getVirtualAppState(VirtualSystemCollectionType contentInstance)
        throws SectionException;

    /**
     * It bundles the virtual systems list contained in the virtual system collection
     * 
     * @param contentInstance the collection of virtual systems to bundle
     * @param envelope the OVF envelope
     * @throws SectionException
     * @throws IdNotFoundException
     * @throws VirtualMachineException
     * @throws MalformedURLException
     * @throws HypervisorException
     * @throws PluginException
     */
    public void bundleVirtualSystemCollection(VirtualSystemCollectionType contentInstance,
        EnvelopeType envelope) throws IdNotFoundException, SectionException,
        VirtualMachineException, MalformedURLException, PluginException, HypervisorException;

    /**
     * Extracts the virtual machine configuration from a virtual system instance
     * 
     * @param virtualSystemInstance
     * @param virtualDiskMap
     * @param envelope
     * @return
     * @throws MalformedURLException
     * @throws VirtualMachineException
     * @throws SectionNotPresentException
     * @throws SectionException
     */
    public VirtualMachineConfiguration getVirtualMachineConfigurationFromVirtualSystem(
        VirtualSystemType virtualSystemInstance, Map<String, VirtualDisk> virtualDiskMap,
        EnvelopeType envelope) throws MalformedURLException, VirtualMachineException,
        SectionNotPresentException, SectionException;

    /**
     * Gets the hypervisor configuration from a virtual system
     * 
     * @param virtualSystemInstance
     * @return
     * @throws InvalidSectionException
     * @throws SectionNotPresentException
     */
    public HypervisorConfiguration getHypervisorConfigurationFromVirtualSystem(
        ContentType virtualSystemInstance) throws SectionNotPresentException,
        InvalidSectionException;

}
