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

package com.abiquo.commons.ampq.vsm;

import java.io.IOException;

import org.testng.annotations.Test;

import com.abiquo.commons.amqp.impl.vsm.VSMCallback;
import com.abiquo.commons.amqp.impl.vsm.VSMConfiguration;
import com.abiquo.commons.amqp.impl.vsm.VSMConsumer;
import com.abiquo.commons.amqp.impl.vsm.VSMProducer;
import com.abiquo.commons.amqp.impl.vsm.domain.VirtualSystemEvent;

public class VSMTest
{
    public static void main(String[] args) throws IOException
    {
        new VSMTest().basic();
    }

    @Test(enabled = false)
    public void basic() throws IOException
    {
        VSMConsumer consumer = new VSMConsumer(VSMConfiguration.EVENT_SYNK_QUEUE);

        consumer.addCallback(new VSMCallback()
        {
            @Override
            public void onEvent(VirtualSystemEvent message)
            {
                System.out.println(message.toString());
            }
        });

        consumer.start();

        VSMProducer p = createProducer();
        p.openChannel();

        for (int i = 0; i < 10; i++)
        {
            p.publish(new VirtualSystemEvent("", "vmx-04", "@", "POWERED_ON"));

            try
            {
                Thread.sleep(1000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

        p.closeChannel();
        consumer.stop();
    }

    @Test(enabled = false)
    public void single() throws IOException
    {
        VSMProducer p = createProducer();
        p.openChannel();
        p.publish(null); // new VirtualSystemEvent("", "vmx-04", "@", "POWERED_ON"));
        p.closeChannel();
    }

    protected VSMProducer createProducer() throws IOException
    {
        return new VSMProducer();
    }

    protected VSMConsumer createConsumer(String queue) throws IOException
    {
        return new VSMConsumer(queue);
    }
}
