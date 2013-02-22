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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import redis.clients.jedis.Response;
import redis.clients.jedis.TransactionBlock;
import redis.clients.jedis.exceptions.JedisException;

public class TransactionBlockList extends TransactionBlock
{
    private Queue<Response< ? >> responses = new LinkedList<Response< ? >>();

    private List<TransactionBlock2> transactionBlocks;

    public TransactionBlockList()
    {
        transactionBlocks = new ArrayList<TransactionBlock2>();
    }

    @Override
    public void execute() throws JedisException
    {
        for (TransactionBlock2 transactionBlock : transactionBlocks)
        {
            transactionBlock.setClient(client);
            transactionBlock.execute();
            responses.addAll(transactionBlock.getResponses());
        }
    }

    public void add(final TransactionBlock2 transactionBlock)
    {
        if (transactionBlock != null)
        {
            transactionBlocks.add(transactionBlock);
        }
    }

    @Override
    protected Response< ? > generateResponse(final Object data)
    {
        Response< ? > response = responses.poll();

        if (response != null)
        {
            response.set(data);
        }

        return response;
    }
}
