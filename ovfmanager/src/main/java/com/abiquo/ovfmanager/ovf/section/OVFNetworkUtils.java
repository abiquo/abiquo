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

package com.abiquo.ovfmanager.ovf.section;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.dmtf.schemas.ovf.envelope._1.AbicloudNetworkType;
import org.dmtf.schemas.ovf.envelope._1.MsgType;
import org.dmtf.schemas.ovf.envelope._1.NetworkSectionType;
import org.dmtf.schemas.ovf.envelope._1.NetworkSectionType.Network;

import com.abiquo.ovfmanager.ovf.exceptions.IdAlreadyExistsException;
import com.abiquo.ovfmanager.ovf.exceptions.IdNotFoundException;
import com.abiquo.ovfmanager.ovf.exceptions.RequiredAttributeException;
import com.abiquo.ovfmanager.ovf.exceptions.SectionNotPresentException;

/**
 * 
 * @author abiquo
 *
 */
/**
 * @author abiquo
 */
public class OVFNetworkUtils
{

    /**
     * Adds a new Network to NetworkSection
     * 
     * @param netSection <NetworkSection> xml tag.
     * @param net new <Network> tag to add
     * @throws RequiredAttributeException if
     * @throws IdAlreadyExistsException
     */
    public static void addNetwork(NetworkSectionType netSection, Network net)
        throws RequiredAttributeException, IdAlreadyExistsException
    {
        if (net != null || netSection != null)
        {
            for (Network existingNetwork : netSection.getNetwork())
            {
                if (existingNetwork.getName().equalsIgnoreCase(net.getName()))
                {
                    throw new IdAlreadyExistsException("Id " + net.getName() + " already exists!");
                }
            }
            netSection.getNetwork().add(net);
        }
        else
        {
            throw new RequiredAttributeException("Required Network or NetworkSection cannot be null");
        }
    }

    /**
     * Create a <Network> tag with a given identifer string for ovf:name
     * 
     * @param identifier identifier of the network (required)
     * @param description description of the network (optional)
     * @return New Network instance;
     * @throws RequiredAttributeException thrown when identifier is null.
     */
    public static Network createNetwork(String identifier, String description)
        throws RequiredAttributeException
    {
        Network net = new Network();

        if (identifier != null)
        {
            net.setName(identifier);
            if (description != null)
            {
                MsgType infoNetwork = new MsgType();
                infoNetwork.setValue(description);

                net.setDescription(infoNetwork);
            }
        }
        else
        {
            throw new RequiredAttributeException("Required network ovf:name");
        }

        return net;
    }

    /**
     * Search inside the NetworkSection all the network interfaces.
     * 
     * @param netSection <NetworkSection> tag we look for all Networks
     * @return All Network interfaces defined in XML
     * @throws SectionNotPresentException
     * @throws RequiredAttributeException
     */
    public static List<Network> getAllNetworks(NetworkSectionType netSection)
        throws RequiredAttributeException
    {

        List<Network> networks = new ArrayList<Network>();

        if (netSection != null)
        {
            networks = netSection.getNetwork();
        }
        else
        {
            throw new RequiredAttributeException("NetworkSectionType cannot be null");
        }

        return networks;
    }

    /**
     * Search a Network with a given Id
     * 
     * @param netSection <NetworkSection> where we look for the <Network> interfaces
     * @param networkId identifier of the <Network> (ovf:name)
     * @return Network founded <Network>
     * @throws IdNotFoundException
     * @throws RequiredAttributeException
     */
    public static Network getNetwork(NetworkSectionType netSection, String networkId)
        throws IdNotFoundException, RequiredAttributeException
    {
        if (netSection == null || networkId == null)
        {
            throw new RequiredAttributeException("Some values are null!");
        }

        for (Network net : netSection.getNetwork())
        {
            if (networkId.equals(net.getName()))
            {
                return net;
            }
        }

        throw new IdNotFoundException("Network name " + networkId);
    }

    /**
     * Set other attributes to NetworkSection. As there is no xsd attributes for network features
     * such as Gateway, range, netmask.. It's mandatory to create a function that will insert this
     * values in an auxiliary OtherAttributes Map .
     * 
     * @param netSection the <NetworkSection> we work with
     * @param key Key of the Map
     * @param value value of the key
     * @throws RequiredAttributeException if key or netSection are null throws this method
     * @throws IdAlreadyExistsException if key already inserted
     */
    public static void addOtherAttributes(Network net, QName key, String value)
        throws RequiredAttributeException, IdAlreadyExistsException
    {
        if (net == null || key == null)
        {
            throw new RequiredAttributeException("Some values are null!");
        }
        if (net.getOtherAttributes().get(key) != null)
        {
            throw new IdAlreadyExistsException("Key already exists");
        }
        net.getOtherAttributes().put(key, value);

    }

    /**
     * Return an OtherAttributes value for a given key into the <NetworkSection>
     * 
     * @param netSection <Network
     * @param key
     * @return
     * @throws RequiredAttributeException
     * @throws IdNotFoundException
     */
    public static String getOtherAttribute(Network net, QName key)
        throws RequiredAttributeException, IdNotFoundException
    {
        if (net == null || key == null)
        {
            throw new RequiredAttributeException("Some values are null!");
        }

        String value = net.getOtherAttributes().get(key);

        if (value == null)
        {
            throw new IdNotFoundException("Key doesn't exist");
        }

        return value;
    }

    // TODO
    public static void addAbiquoNetwork(NetworkSectionType netSection, AbicloudNetworkType network)
        throws RequiredAttributeException
    {
        if (netSection != null && network != null)
        {
            netSection.getAny().add(network);
        }
        else
        {
            throw new RequiredAttributeException("");
        }
    }

    // TODO
    public static List<AbicloudNetworkType> getAllAbiquoNetworks(NetworkSectionType netSection)
        throws RequiredAttributeException
    {
        if (netSection != null)
        {
            List<Object> objects = netSection.getAny();
            List<AbicloudNetworkType> nws = new ArrayList<AbicloudNetworkType>();

            for (Object o : objects)
            {
                if (o instanceof AbicloudNetworkType)
                {
                    nws.add((AbicloudNetworkType) o);
                }
            }

            return nws;
        }
        else
        {
            throw new RequiredAttributeException("");
        }
    }
}