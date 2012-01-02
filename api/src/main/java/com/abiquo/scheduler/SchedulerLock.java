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
