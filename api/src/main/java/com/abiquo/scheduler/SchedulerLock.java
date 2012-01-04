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

package com.abiquo.scheduler;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.exceptions.ServiceUnavailableException;

/**
 * Synchronize physical machines resources modification
 */
public class SchedulerLock
{
    private final static Logger LOG = LoggerFactory.getLogger(SchedulerLock.class);

    private final static ReentrantLock THE_LOCK = new ReentrantLock();

    private final static long TIMEOUT = Long.parseLong(System.getProperty(
        "com.abiquo.schedulerlock.timeout", "30000"));

    /**
     * Gain access to modify resources in the physical infrastructure.
     * 
     * @param msg, cause of the lock
     * @throws ServiceUnavailableException if can't acquire the lock in the configured timeout.
     */
    public static void acquire(final String msg)
    {
        final long start = System.currentTimeMillis();

        LOG.debug("Wait to adquire lock - {}", msg);

        try
        {
            if (!THE_LOCK.tryLock(TIMEOUT, TimeUnit.MILLISECONDS))
            {
                LOG.error("Can't acquire lock after {}ms - {}", TIMEOUT, msg);
                throw new ServiceUnavailableException(APIError.SERVICE_UNAVAILABLE_ERROR);
            }
        }
        catch (InterruptedException e)
        {
            LOG.error("Lock interrupted - {}", msg, e);
            throw new ServiceUnavailableException(APIError.SERVICE_UNAVAILABLE_ERROR);
        }

        LOG.debug("Adquired lock after {}ms - {}", System.currentTimeMillis() - start, msg);
    }

    /**
     * Release the lock (or do nothing if not owned by the current thread)
     * 
     * @param msg, cause of the lock
     */
    public static void release(final String msg)
    {
        if (THE_LOCK.isHeldByCurrentThread())
        {
            THE_LOCK.unlock();
            LOG.debug("Released - {}", msg);
        }
        else
        {
            LOG.warn("Not adquired - {}", msg);
        }
    }
}
