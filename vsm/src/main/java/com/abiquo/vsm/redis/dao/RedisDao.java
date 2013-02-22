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

import static java.lang.Integer.parseInt;
import static java.lang.String.valueOf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;

import com.abiquo.vsm.model.PhysicalMachine;
import com.abiquo.vsm.model.VirtualMachine;

/**
 * This DAO wraps the find and persistence JOhm functionalities for the specific VSM data model.
 * 
 * @author eruiz@abiquo.com
 */
public class RedisDao extends AbstractDao
{
    public RedisDao(final JedisPool redisPool)
    {
        super(redisPool);
    }

    // VirtualMachine related methods and constants ///////////////////////////////////////////////

    private static final String VirtualMachineNamespace = "VirtualMachine";

    private static final String NameNamespace = "name";

    private static final String NameKey = "name";

    private static final String LastKnownStateKey = "lastKnownState";

    private static final String HypervisorIdKey = "physicalMachine_id";

    private VirtualMachine findVirtualMachine(final String id, final Jedis redis)
    {
        if (!redis.exists(nest(VirtualMachineNamespace, id)))
        {
            return null;
        }

        Map<String, String> hash = redis.hgetAll(nest(VirtualMachineNamespace, id));

        VirtualMachine virtualMachine = new VirtualMachine();
        virtualMachine.setId(parseInt(id));
        virtualMachine.setPhysicalMachine(findPhysicalMachine(hash.get(HypervisorIdKey), redis));
        virtualMachine.setLastKnownState(nullToEmpty(hash.get(LastKnownStateKey)));
        virtualMachine.setName(hash.get(NameKey));

        return virtualMachine;
    }

    public VirtualMachine findVirtualMachineByName(final String name)
    {
        return execute(new Function<Jedis, VirtualMachine>()
        {
            @Override
            public VirtualMachine apply(final Jedis redis)
            {
                final String key = nest(VirtualMachineNamespace, NameNamespace, name);
                List<String> members = new ArrayList<String>(redis.smembers(key));

                if (!members.isEmpty())
                {
                    return findVirtualMachine(members.get(0), redis);
                }

                return null;
            }
        });
    }

    public VirtualMachine getVirtualMachine(final Integer id)
    {
        return execute(new Function<Jedis, VirtualMachine>()
        {
            @Override
            public VirtualMachine apply(final Jedis redis)
            {
                return findVirtualMachine(valueOf(id), redis);
            }
        });
    }

    public Set<VirtualMachine> findAllVirtualMachines()
    {
        return execute(new Function<Jedis, Set<VirtualMachine>>()
        {
            @Override
            public Set<VirtualMachine> apply(final Jedis redis)
            {
                Set<VirtualMachine> virtualMachines = new HashSet<VirtualMachine>();

                for (String id : redis.smembers(nest(VirtualMachineNamespace, AllNamespace)))
                {
                    VirtualMachine virtualMachine = findVirtualMachine(id, redis);

                    if (virtualMachine != null)
                    {
                        virtualMachines.add(virtualMachine);
                    }
                }

                return virtualMachines;
            }
        });
    }

    private TransactionBlock2 save(final VirtualMachine virtualMachine, final Jedis redis)
    {
        if (virtualMachine.getId() == null)
        {
            // It is new!
            virtualMachine.setId(generateUniqueId(VirtualMachineNamespace, redis));
        }

        final Map<String, String> hash = new HashMap<String, String>();
        hash.put(NameKey, nullToEmpty(virtualMachine.getName()));
        hash.put(LastKnownStateKey, nullToEmpty(virtualMachine.getLastKnownState()));

        if (virtualMachine.getPhysicalMachine() != null)
        {
            Integer hypervisorId = virtualMachine.getPhysicalMachine().getId();

            if (hypervisorId != null)
            {
                hash.put(HypervisorIdKey, valueOf(hypervisorId));
            }
        }

        TransactionBlock2 transactionBlock = new TransactionBlock2()
        {
            @Override
            public void execute() throws JedisException
            {
                String id = valueOf(virtualMachine.getId());

                sadd(nest(VirtualMachineNamespace, AllNamespace), id);
                sadd(nest(VirtualMachineNamespace, NameNamespace, virtualMachine.getName()), id);
                hmset(nest(VirtualMachineNamespace, id), hash);
            }
        };

        return transactionBlock;
    }

    public VirtualMachine save(final VirtualMachine virtualMachine)
    {
        executeTransactionBlockList(new Function<Jedis, TransactionBlockList>()
        {
            @Override
            public TransactionBlockList apply(final Jedis redis)
            {
                TransactionBlockList transactionBlockList = new TransactionBlockList();
                transactionBlockList.add(delete(virtualMachine, redis));
                transactionBlockList.add(save(virtualMachine, redis));
                return transactionBlockList;
            }
        });

        return findVirtualMachineByName(virtualMachine.getName());
    }

    private TransactionBlock2 delete(final VirtualMachine virtualMachine, final Jedis redis)
    {
        final String id = valueOf(virtualMachine.getId());

        if (findVirtualMachine(id, redis) != null)
        {
            return new TransactionBlock2()
            {
                @Override
                public void execute() throws JedisException
                {
                    srem(nest(VirtualMachineNamespace, AllNamespace), id);
                    srem(nest(VirtualMachineNamespace, NameNamespace, virtualMachine.getName()), id);
                    del(nest(VirtualMachineNamespace, id));
                }
            };
        }

        return null;
    }

    public void delete(final VirtualMachine virtualMachine)
    {
        executeTransactionBlock(new Function<Jedis, TransactionBlock2>()
        {
            @Override
            public TransactionBlock2 apply(final Jedis redis)
            {
                return delete(virtualMachine, redis);
            }
        });
    }

    // PhysicalMachine related methods and constants //////////////////////////////////////////////

    private static final String HypervisorNamespace = "PhysicalMachine";

    private static final String AddressNamespace = "address";

    private static final String AddressKey = "address";

    private static final String TypeKey = "type";

    private static final String UsernameKey = "username";

    private static final String PasswordKey = "password";

    private static final String VirtualMachinesCacheNamespace = "VirtualMachinesCache";

    private static final String CacheNamespace = "cache";

    private static final String VirtualMachinesCacheKey = "virtualMachines_id";

    private PhysicalMachine findPhysicalMachine(final String id, final Jedis redis)
    {
        if (!redis.exists(nest(HypervisorNamespace, id)))
        {
            return null;
        }

        Map<String, String> hash = redis.hgetAll(nest(HypervisorNamespace, id));
        String cacheId = hash.get(VirtualMachinesCacheKey);
        String cacheKey = nest(VirtualMachinesCacheNamespace, cacheId, CacheNamespace);

        PhysicalMachine physicalMachine = new PhysicalMachine();
        physicalMachine.setId(parseInt(id));
        physicalMachine.setAddress(hash.get(AddressKey));
        physicalMachine.setType(hash.get(TypeKey));
        physicalMachine.setUsername(hash.get(UsernameKey));
        physicalMachine.setPassword(hash.get(PasswordKey));
        physicalMachine.getVirtualMachines().getCache().addAll(redis.smembers(cacheKey));

        return physicalMachine;
    }

    public PhysicalMachine findPhysicalMachineByAddress(final String address)
    {
        return execute(new Function<Jedis, PhysicalMachine>()
        {
            @Override
            public PhysicalMachine apply(final Jedis redis)
            {
                final String key = nest(HypervisorNamespace, AddressNamespace, address);
                List<String> members = new ArrayList<String>(redis.smembers(key));

                if (!members.isEmpty())
                {
                    return findPhysicalMachine(members.get(0), redis);
                }

                return null;
            }
        });
    }

    public PhysicalMachine getPhysicalMachine(final Integer id)
    {
        return execute(new Function<Jedis, PhysicalMachine>()
        {
            @Override
            public PhysicalMachine apply(final Jedis redis)
            {
                return findPhysicalMachine(valueOf(id), redis);
            }
        });
    }

    public Set<PhysicalMachine> findAllPhysicalMachines()
    {
        return execute(new Function<Jedis, Set<PhysicalMachine>>()
        {
            @Override
            public Set<PhysicalMachine> apply(final Jedis redis)
            {
                Set<PhysicalMachine> physicalMachines = new HashSet<PhysicalMachine>();

                for (String id : redis.smembers(nest(HypervisorNamespace, AllNamespace)))
                {
                    PhysicalMachine physicalMachine = findPhysicalMachine(id, redis);

                    if (physicalMachine != null)
                    {
                        physicalMachines.add(physicalMachine);
                    }
                }

                return physicalMachines;
            }
        });
    }

    private TransactionBlock2 save(final PhysicalMachine physicalMachine, final Jedis redis)
    {
        String cacheId = "";

        if (physicalMachine.getId() == null)
        {
            // It is new!
            physicalMachine.setId(generateUniqueId(HypervisorNamespace, redis));
            cacheId = valueOf(generateUniqueId(VirtualMachinesCacheNamespace, redis));
        }
        else
        {
            cacheId =
                redis.hgetAll(nest(HypervisorNamespace, valueOf(physicalMachine.getId()))).get(
                    VirtualMachinesCacheKey);
        }

        final Map<String, String> hash = new HashMap<String, String>();
        hash.put(AddressKey, nullToEmpty(physicalMachine.getAddress()));
        hash.put(TypeKey, nullToEmpty(physicalMachine.getType()));
        hash.put(UsernameKey, nullToEmpty(physicalMachine.getUsername()));
        hash.put(PasswordKey, nullToEmpty(physicalMachine.getPassword()));
        hash.put(VirtualMachinesCacheKey, valueOf(cacheId));

        TransactionBlock2 transactionBlock = new TransactionBlock2()
        {
            @Override
            public void execute() throws JedisException
            {
                String id = valueOf(physicalMachine.getId());
                String cacheId = hash.get(VirtualMachinesCacheKey);
                String cacheKey = nest(VirtualMachinesCacheNamespace, cacheId, CacheNamespace);

                sadd(nest(HypervisorNamespace, AllNamespace), id);
                sadd(nest(HypervisorNamespace, AddressNamespace, physicalMachine.getAddress()), id);
                hmset(nest(HypervisorNamespace, id), hash);
                del(cacheKey);

                Set<String> cache = physicalMachine.getVirtualMachines().getCache();

                for (String name : cache)
                {
                    sadd(cacheKey, name);
                }
            }
        };

        return transactionBlock;
    }

    public PhysicalMachine save(final PhysicalMachine physicalMachine)
    {
        executeTransactionBlockList(new Function<Jedis, TransactionBlockList>()
        {
            @Override
            public TransactionBlockList apply(final Jedis redis)
            {
                TransactionBlockList transactionBlockList = new TransactionBlockList();
                transactionBlockList.add(delete(physicalMachine, redis));
                transactionBlockList.add(save(physicalMachine, redis));
                return transactionBlockList;
            }
        });

        return findPhysicalMachineByAddress(physicalMachine.getAddress());
    }

    private TransactionBlock2 delete(final PhysicalMachine physicalMachine, final Jedis redis)
    {
        final String id = valueOf(physicalMachine.getId());

        if (findPhysicalMachine(id, redis) != null)
        {
            final Map<String, String> hash = redis.hgetAll(nest(HypervisorNamespace, id));

            return new TransactionBlock2()
            {
                @Override
                public void execute() throws JedisException
                {
                    String cacheId = hash.get(VirtualMachinesCacheKey);

                    srem(nest(HypervisorNamespace, AllNamespace), id);
                    del(nest(HypervisorNamespace, AddressNamespace, physicalMachine.getAddress()));
                    del(nest(HypervisorNamespace, id));
                    del(nest(VirtualMachinesCacheNamespace, cacheId));
                    del(nest(VirtualMachinesCacheNamespace, cacheId, CacheNamespace));
                }
            };
        }

        return null;
    }

    public void delete(final PhysicalMachine physicalMachine)
    {
        executeTransactionBlock(new Function<Jedis, TransactionBlock2>()
        {
            @Override
            public TransactionBlock2 apply(final Jedis redis)
            {
                return delete(physicalMachine, redis);
            }
        });
    }
}
