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

package com.abiquo.mailman;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Calendar;
import java.util.Properties;

import org.junit.Test;

/**
 *
 */
public class SendRenderedEmailTest extends MailTestBase
{
    /**
     * Test method for
     * {@link com.abiquo.mailman.MailManager#send(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}
     * .
     */
    @Test
    public final void testSendRenderedEmail()
    {
        try
        {
            Properties properties = new Properties();
            properties.setProperty("username", "testusername");
            properties.setProperty("enterprise", "testenterprise");
            properties.setProperty("virtualdatacenter", "testvirtualdatacenter");
            properties.setProperty("virtualapp", "testvirtualapp");
            properties.setProperty("timestamp", Calendar.getInstance().getTime().toString());

            String body = RendererManager.generateBody(properties, "poweron", "en", "default");

            mail.send(mailUser, mailTo, mailCC, "Rendred mail :" + mailSubject, body);

            assertTrue("Message sent", true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("Exception raised:" + e.toString());
        }
    }

}
