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

import org.testng.Assert;
import org.testng.annotations.Test;

import com.abiquo.commons.amqp.domain.QueuableString;
import com.abiquo.commons.amqp.impl.bpm.BPMConsumer;
import com.abiquo.commons.amqp.impl.bpm.BPMProducer;

public class BPMTest
{
    public static void main(String[] args) throws IOException
    {
        new BPMTest().basic();
    }

    @Test(enabled = false)
    public void basic() throws IOException
    {
        int messages = 10;

        BPMProducer p = createProducer();
        p.openChannel();

        for (int i = 0; i < messages; i++)
        {
            p.publish(new QueuableString(String.format("blbla %d", i)));
        }

        // p.disconnect();

        // Instance a consumer
        // BPMConsumer c = createConsumer();
        //
        // c.addCallback(new BPMCallback()
        // {
        // @Override
        // public void onMessage(String message)
        // {
        // System.out.println(message);
        // }
        // });
        //
        // for (int i = 0; i < messages; i++)
        // {
        // c.consume();
        // }

        Assert.assertTrue(true);
    }

    protected BPMProducer createProducer() throws IOException
    {
        return new BPMProducer();
    }

    protected BPMConsumer createConsumer() throws IOException
    {
        return new BPMConsumer();
    }
}
