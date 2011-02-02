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

import java.util.Enumeration;
import java.util.Properties;

import org.apache.velocity.VelocityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.mailman.velocity.VelocityManager;

/**
 * @author Diego Parrilla
 * @date 04-dic-2009
 * @version $Revision: 1.2 $
 */
public class RendererManager
{

    /** debug. */
    private static final Logger debug = LoggerFactory.getLogger(RendererManager.class);

    public static String generateBody(Properties properties, String template, String language,
        String brand)
    {
        String body = null;
        try
        {
            String name, value;
            VelocityContext c = new VelocityContext();
            Enumeration e = properties.propertyNames();
            while (e.hasMoreElements())
            {
                name = (String) e.nextElement();
                value = properties.getProperty(name);
                c.put(name, value);
            }
            body = VelocityManager.getInstance().merge(template, c, language, brand);
            debug.info("Body renderer:" + body);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return body;
    }

}
