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

package com.abiquo.appliancemanager;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.ISuite;
import org.testng.ISuiteListener;

import com.abiquo.appliancemanager.transport.OVFStatusEnumType;
import com.abiquo.commons.amqp.impl.am.AMCallback;
import com.abiquo.commons.amqp.impl.am.AMConsumer;
import com.abiquo.commons.amqp.impl.am.domain.OVFPackageInstanceStatusEvent;

/**
 * Consumes AM events {@link OVFPackageInstanceStatusEvent} and check them arrives in the expected
 * order.
 * <p>
 * TODO logs not working
 */
public class AMConsumerTestListener implements ISuiteListener, AMCallback
{
    private final static Logger LOG = LoggerFactory.getLogger(AMConsumerTestListener.class);

    private AMConsumer consumer;

    private static Queue<OVFStatusEnumType> EVENTS = new ConcurrentLinkedQueue<OVFStatusEnumType>();

    private final static int TIMEOUT = 3;

    public static void expectedEvents(final OVFStatusEnumType... events)
    {
        for (OVFStatusEnumType event : events)
        {
            pollWithTimeoutAndCompare(event);
        }
    }

    private static void pollWithTimeoutAndCompare(final OVFStatusEnumType expected)
    {
        OVFStatusEnumType actual = null;

        for (int t = 0; t < TIMEOUT && actual == null; t++)
        {
            actual = EVENTS.poll();
            if (actual != null)
            {
                Assert.assertEquals(actual, expected, "Event sequence");
                return;
            }
            try
            {
                Thread.sleep(1000);
            }
            catch (InterruptedException e)
            {
                Assert.fail("timeout event " + expected);
            }
        }

        Assert.fail("missing event " + expected);
    }

    public static void assertEventsEmpty()
    {
        OVFStatusEnumType noEvent = EVENTS.poll();
        Assert.assertNull(noEvent, "expected no event" + noEvent);
    }

    @Override
    public void onStart(ISuite suite)
    {
        consumer = new AMConsumer();
        consumer.addCallback(this);
        try
        {
            consumer.start();
            LOG.info("test AMConsumer started");

            Thread.sleep(1000); // FIXME wait for lost events to consume it
            EVENTS.clear();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onFinish(ISuite suite)
    {
        try
        {
            Thread.sleep(1000); // FIXME wait for lost events to consume it
            EVENTS.clear();

            consumer.stop();
            LOG.info("test AMConsumer stoped");
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * ##### AMCallbackImpl #####
     */

    @Override
    public void onDownload(OVFPackageInstanceStatusEvent event)
    {
        EVENTS.add(OVFStatusEnumType.fromValue(event.getStatus()));
    }

    @Override
    public void onNotDownload(OVFPackageInstanceStatusEvent event)
    {
        EVENTS.add(OVFStatusEnumType.fromValue(event.getStatus()));
    }

    @Override
    public void onError(OVFPackageInstanceStatusEvent event)
    {
        EVENTS.add(OVFStatusEnumType.fromValue(event.getStatus()));
    }

    @Override
    public void onDownloading(OVFPackageInstanceStatusEvent event)
    {
        EVENTS.add(OVFStatusEnumType.fromValue(event.getStatus()));
    }

}
