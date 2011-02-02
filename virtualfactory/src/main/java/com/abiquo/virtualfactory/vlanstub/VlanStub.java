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

package com.abiquo.virtualfactory.vlanstub;

import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.aimstub.TTransportProxy;
import com.abiquo.aimstub.VLanException;
import com.abiquo.aimstub.Aim.Iface;
import com.abiquo.virtualfactory.model.AbiCloudModel;
import com.abiquo.virtualfactory.model.config.Configuration;
import com.abiquo.virtualfactory.vlan.VLANCreate;
import com.abiquo.virtualfactory.vlan.VLANDelete;
import com.abiquo.virtualfactory.vlan.VLANGet;
import com.abiquo.virtualfactory.vlan.VLANPut;

public class VlanStub
{

    /** The constant logger object. */
    private final static Logger logger = LoggerFactory.getLogger(VlanStub.class);

    public static void createVlan(final URL physicalMachineIp, final String vlan_id,
        String externalInterface, String bridgeName) throws VLANException
    {
        Iface aimStub =
            TTransportProxy.getInstance(physicalMachineIp.getHost(), physicalMachineIp.getPort());

        try
        {
            aimStub.createVLAN(Integer.parseInt(vlan_id), externalInterface, bridgeName);
        }
        catch (NumberFormatException e)
        {
            throw new VLANException(e.getMessage());
        }
        catch (VLanException e)
        {
            throw new VLANException(e.description);
        }
        catch (TException e)
        {
            throw new VLANException(e.getMessage());
        }
    }

    public static void deleteVlan(final URL physicalMachineIp, final String vlan_id,
        String externalInterface, String bridgeName) throws VLANException
    {
        Iface aimStub =
            TTransportProxy.getInstance(physicalMachineIp.getHost(), physicalMachineIp.getPort());

        try
        {
            aimStub.deleteVLAN(Integer.parseInt(vlan_id), externalInterface, bridgeName);
        }
        catch (NumberFormatException e)
        {
            throw new VLANException(e.getMessage());
        }
        catch (VLanException e)
        {
            throw new VLANException(e.description);
        }
        catch (TException e)
        {
            throw new VLANException(e.getMessage());
        }
    }

}
