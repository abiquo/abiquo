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

package com.abiquo.nodecollector.domain.collectors.libvirt;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.libvirt.Connect;
import org.libvirt.Device;
import org.libvirt.Domain;
import org.libvirt.Interface;
import org.libvirt.LibvirtException;
import org.libvirt.Network;
import org.libvirt.NetworkFilter;
import org.libvirt.Secret;
import org.libvirt.StoragePool;
import org.libvirt.StorageVol;
import org.libvirt.Stream;

/**
 * For a world without libvirt connection leaks. From libvirt documentation (see url bellow) "Also
 * note that every other object associated with a connection (virDomainPtr, virNetworkPtr, etc) will
 * also hold a reference on the connection. To avoid leaking a connection object, applications must
 * ensure all associated objects are also freed."
 * 
 * @see http://libvirt.org/guide/html/Application_Development_Guide-Connections.html
 * @author eruiz@abiquo.com
 */
public class LeaksFreeConnect extends Connect
{
    private Set<Object> objectsToFree;

    public LeaksFreeConnect(String url) throws LibvirtException
    {
        super(url);

        objectsToFree = new HashSet<Object>();
    }

    protected <T> T addObjectToFree(T object)
    {
        objectsToFree.add(object);
        return object;
    }

    @Override
    public Domain domainCreateLinux(String arg0, int arg1) throws LibvirtException
    {
        Domain domain = super.domainCreateLinux(arg0, arg1);
        return addObjectToFree(domain);
    }

    @Override
    public Domain domainCreateXML(String arg0, int arg1) throws LibvirtException
    {
        Domain domain = super.domainCreateXML(arg0, arg1);
        return addObjectToFree(domain);
    }

    @Override
    public Domain domainDefineXML(String arg0) throws LibvirtException
    {
        Domain domain = super.domainDefineXML(arg0);
        return addObjectToFree(domain);
    }

    @Override
    public Domain domainLookupByID(int arg0) throws LibvirtException
    {
        Domain domain = super.domainLookupByID(arg0);
        return addObjectToFree(domain);
    }

    @Override
    public Domain domainLookupByName(String arg0) throws LibvirtException
    {
        Domain domain = super.domainLookupByName(arg0);
        return addObjectToFree(domain);
    }

    @Override
    public Domain domainLookupByUUID(int[] arg0) throws LibvirtException
    {
        Domain domain = super.domainLookupByUUID(arg0);
        return addObjectToFree(domain);
    }

    @Override
    public Domain domainLookupByUUID(UUID arg0) throws LibvirtException
    {
        Domain domain = super.domainLookupByUUID(arg0);
        return addObjectToFree(domain);
    }

    @Override
    public Domain domainLookupByUUIDString(String arg0) throws LibvirtException
    {
        Domain domain = super.domainLookupByUUIDString(arg0);
        return addObjectToFree(domain);
    }

    @Override
    public Device deviceCreateXML(String arg0) throws LibvirtException
    {
        Device device = super.deviceCreateXML(arg0);
        return addObjectToFree(device);
    }

    @Override
    public Device deviceLookupByName(String arg0) throws LibvirtException
    {
        Device device = super.deviceLookupByName(arg0);
        return addObjectToFree(device);
    }

    @Override
    public Interface interfaceDefineXML(String arg0) throws LibvirtException
    {
        Interface iface = super.interfaceDefineXML(arg0);
        return addObjectToFree(iface);
    }

    @Override
    public Interface interfaceLookupByMACString(String arg0) throws LibvirtException
    {
        Interface iface = super.interfaceLookupByMACString(arg0);
        return addObjectToFree(iface);
    }

    @Override
    public Interface interfaceLookupByName(String arg0) throws LibvirtException
    {
        Interface iface = super.interfaceLookupByName(arg0);
        return addObjectToFree(iface);
    }

    @Override
    public Network networkCreateXML(String arg0) throws LibvirtException
    {
        Network network = super.networkCreateXML(arg0);
        return addObjectToFree(network);
    }

    @Override
    public Network networkDefineXML(String arg0) throws LibvirtException
    {
        Network network = super.networkDefineXML(arg0);
        return addObjectToFree(network);
    }

    @Override
    public Network networkLookupByName(String arg0) throws LibvirtException
    {
        Network network = super.networkLookupByName(arg0);
        return addObjectToFree(network);
    }

    @Override
    @Deprecated
    public Network networkLookupByUUID(int[] arg0) throws LibvirtException
    {
        Network network = super.networkLookupByUUID(arg0);
        return addObjectToFree(network);
    }

    @Override
    public Network networkLookupByUUID(UUID arg0) throws LibvirtException
    {
        Network network = super.networkLookupByUUID(arg0);
        return addObjectToFree(network);
    }

    @Override
    public Network networkLookupByUUIDString(String arg0) throws LibvirtException
    {
        Network network = super.networkLookupByUUIDString(arg0);
        return addObjectToFree(network);
    }

    @Override
    public NetworkFilter networkFilterDefineXML(String arg0) throws LibvirtException
    {
        NetworkFilter networkFilter = super.networkFilterDefineXML(arg0);
        return addObjectToFree(networkFilter);
    }

    @Override
    public NetworkFilter networkFilterLookupByName(String arg0) throws LibvirtException
    {
        NetworkFilter networkFilter = super.networkFilterLookupByName(arg0);
        return addObjectToFree(networkFilter);
    }

    @Override
    public NetworkFilter networkFilterLookupByUUID(int[] arg0) throws LibvirtException
    {
        NetworkFilter networkFilter = super.networkFilterLookupByUUID(arg0);
        return addObjectToFree(networkFilter);
    }

    @Override
    public NetworkFilter networkFilterLookupByUUID(UUID arg0) throws LibvirtException
    {
        NetworkFilter networkFilter = super.networkFilterLookupByUUID(arg0);
        return addObjectToFree(networkFilter);
    }

    @Override
    public NetworkFilter networkFilterLookupByUUIDString(String arg0) throws LibvirtException
    {
        NetworkFilter networkFilter = super.networkFilterLookupByUUIDString(arg0);
        return addObjectToFree(networkFilter);
    }

    @Override
    public Secret secretLookupByUUIDString(String arg0) throws LibvirtException
    {
        Secret secret = super.secretLookupByUUIDString(arg0);
        return addObjectToFree(secret);
    }

    @Override
    public Secret secretDefineXML(String arg0) throws LibvirtException
    {
        Secret secret = super.secretDefineXML(arg0);
        return addObjectToFree(secret);
    }

    @Override
    public Secret secretLookupByUUID(int[] arg0) throws LibvirtException
    {
        Secret secret = super.secretLookupByUUID(arg0);
        return addObjectToFree(secret);
    }

    @Override
    public Secret secretLookupByUUID(UUID arg0) throws LibvirtException
    {
        Secret secret = super.secretLookupByUUID(arg0);
        return addObjectToFree(secret);
    }

    @Override
    public StoragePool storagePoolCreateXML(String arg0, int arg1) throws LibvirtException
    {
        StoragePool pool = super.storagePoolCreateXML(arg0, arg1);
        return addObjectToFree(pool);
    }

    @Override
    public StoragePool storagePoolDefineXML(String arg0, int arg1) throws LibvirtException
    {
        StoragePool pool = super.storagePoolDefineXML(arg0, arg1);
        return addObjectToFree(pool);
    }

    @Override
    public StoragePool storagePoolLookupByName(String arg0) throws LibvirtException
    {
        StoragePool pool = super.storagePoolLookupByName(arg0);
        return addObjectToFree(pool);
    }

    @Override
    @Deprecated
    public StoragePool storagePoolLookupByUUID(int[] arg0) throws LibvirtException
    {
        StoragePool pool = super.storagePoolLookupByUUID(arg0);
        return addObjectToFree(pool);
    }

    @Override
    public StoragePool storagePoolLookupByUUID(UUID arg0) throws LibvirtException
    {
        StoragePool pool = super.storagePoolLookupByUUID(arg0);
        return addObjectToFree(pool);
    }

    @Override
    public StoragePool storagePoolLookupByUUIDString(String arg0) throws LibvirtException
    {
        StoragePool pool = super.storagePoolLookupByUUIDString(arg0);
        return addObjectToFree(pool);
    }

    @Override
    public StorageVol storageVolLookupByKey(String arg0) throws LibvirtException
    {
        StorageVol vol = super.storageVolLookupByKey(arg0);
        return addObjectToFree(vol);
    }

    @Override
    public StorageVol storageVolLookupByPath(String arg0) throws LibvirtException
    {
        StorageVol vol = super.storageVolLookupByPath(arg0);
        return addObjectToFree(vol);
    }

    @Override
    public Stream streamNew(int arg0) throws LibvirtException
    {
        Stream stream = super.streamNew(arg0);
        return addObjectToFree(stream);
    }

    @Override
    public int close() throws LibvirtException
    {
        for (Object object : objectsToFree)
        {
            invokeFreeMethod(object.getClass(), object);
        }

        return super.close();
    }

    private void invokeFreeMethod(Class< ? > clazz, Object object)
    {
        try
        {
            Method method = clazz.getMethod("free", null);
            method.invoke(object, null);
        }
        catch (Exception e)
        {
            // Ignore.
        }
    }
}
