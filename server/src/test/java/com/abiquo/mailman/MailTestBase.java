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

import org.junit.After;
import org.junit.Before;

public class MailTestBase
{
    protected MailManager mail = null;

    private final static String defaultEncoding = "UTF-8";

    protected String mailServer = "smtp.gmail.com";

    protected String mailUser = "testing@abiquo.com";

    protected String mailPass = "madalenes";

    protected String mailTo = mailUser;

    protected String mailCC = mailUser;

    protected String mailSubject = "This is a test";

    protected String mailBody = "If you get it, then its a test.";

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        mail = new MailManager(mailServer, mailUser, mailPass, defaultEncoding, true, true);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {

    }
}
