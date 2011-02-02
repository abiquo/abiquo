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

package com.abiquo.vsm.redis.dao;

import java.util.List;
import java.util.Set;

import redis.clients.johm.JOhm;

import com.abiquo.vsm.model.PhysicalMachine;
import com.abiquo.vsm.model.VirtualMachine;
import com.abiquo.vsm.model.VirtualMachinesCache;

/**
 * This DAO wraps the find and persistence JOhm functionalities for the specific VSM data model.
 * 
 * @author eruiz@abiquo.com
 */
public class RedisDao
{
    public PhysicalMachine getPhysicalMachine(final Integer id)
    {
        return JOhm.get(PhysicalMachine.class, id);
    }

    public VirtualMachine getVirtualMachine(final Integer id)
    {
        return JOhm.get(VirtualMachine.class, id);
    }

    public VirtualMachine findVirtualMachineByName(final String name)
    {
        return findUnique(VirtualMachine.class, "name", name);
    }

    public PhysicalMachine findPhysicalMachineByAddress(final String address)
    {
        return findUnique(PhysicalMachine.class, "address", address);
    }

    public Set<VirtualMachine> findAllVirtualMachines()
    {
        return JOhm.getAll(VirtualMachine.class);
    }

    public Set<PhysicalMachine> findAllPhysicalMachines()
    {
        return JOhm.getAll(PhysicalMachine.class);
    }

    public VirtualMachine save(VirtualMachine virtualMachine)
    {
        return saveUnique(VirtualMachine.class, virtualMachine, "name", virtualMachine.getName());
    }

    public PhysicalMachine save(PhysicalMachine physicalMachine)
    {
        return saveUnique(PhysicalMachine.class, physicalMachine, "address", physicalMachine
            .getAddress());
    }

    public VirtualMachinesCache save(VirtualMachinesCache cache)
    {
        return JOhm.save(cache, false);
    }

    public void delete(VirtualMachine virtualMachine)
    {
        JOhm.delete(VirtualMachine.class, virtualMachine.getId());
    }

    public void delete(PhysicalMachine physicalMachine)
    {
        JOhm.delete(PhysicalMachine.class, physicalMachine.getId());
    }

    private <T> T saveUnique(Class<T> clazz, T entity, final String fieldName,
        final Object fieldValue)
    {
        if (JOhm.isNew(entity))
        {
            T instance = findUnique(clazz, fieldName, fieldValue);

            if (instance != null)
            {
                throw new RuntimeException();
            }
        }

        return JOhm.<T> save(entity, true);
    }

    private <T> T findUnique(Class<T> clazz, final String fieldName, final Object fieldValue)
    {
        List<T> results = JOhm.find(clazz, fieldName, fieldValue);

        if (results.isEmpty())
        {
            return null;
        }

        if (results.size() > 1)
        {
            throw new RuntimeException();
        }

        return results.get(0);
    }
}
