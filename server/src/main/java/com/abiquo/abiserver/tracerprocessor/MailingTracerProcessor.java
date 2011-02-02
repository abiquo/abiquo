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

package com.abiquo.abiserver.tracerprocessor;

import java.util.Calendar;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.abiserver.persistence.DAOFactory;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAOFactory;
import com.abiquo.mailman.MailManager;
import com.abiquo.mailman.RendererManager;
import com.abiquo.tracer.TracerTo;
import com.abiquo.tracer.server.TracerCollectorException;
import com.abiquo.tracer.server.TracerProcessor;

/**
 * This class sends emails with the events received
 * 
 * @author Diego Parrilla
 */
public class MailingTracerProcessor implements TracerProcessor
{
    private final static Logger log = LoggerFactory.getLogger(MailingTracerProcessor.class);

    /** Access to ORM layer. */
    private DAOFactory factory = HibernateDAOFactory.instance();

    /** Sends an email using the configured SMTP account. */
    private MailManager mail = MailManager.instance();
    
    

    @Override
    public void process(TracerTo tracer) throws TracerCollectorException
    {
        switch (tracer.getEvent())
        {
            case USER_CREATE:
                sendLoginEmail(tracer);
                break;
        }
    }
    
    @Override
    public void destroy() throws TracerCollectorException
    {
        // nothing to do there
    }

    private void sendLoginEmail(TracerTo tracer)
    {
        
        // XXX WIP
//        
//        log.debug("User " + tracer.getUser().getUsername() + "is logging in.");
//        Properties properties = new Properties();
//        properties.setProperty("username", tracer.getUser().getUsername());
//        properties.setProperty("enterprise", tracer.getUser().getEnterprise());
//        properties.setProperty("timestamp", Calendar.getInstance().getTime().toString());
//        String body = RendererManager.generateBody(properties, "userlogin", "en", "default");
//        try
//        {
//            mail.send("testing@abiquo.com", "testing@abiquo.com", "testing@abiquo.com",
//                "User Login", body);
//        }
//        catch (Exception e)
//        {
//            log.error("Cannot send email", e);
//        }

    }
}
