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

package com.abiquo.mailman.velocity.dao;

import java.io.InputStream;

/**
 * 
 *
 */
public class FileTemplateDaoImpl implements TemplateDao
{
    // private static final Logger debug =
    // LoggerFactory.getLogger(FileTemplateDaoImpl.class.getName());

    /**
     * 
     */
    public FileTemplateDaoImpl()
    {
        super();
    }

    /*
     * (non-Javadoc)
     * @see es.amplia.marco.mdb.renderer.velocity.dao.TemplateDao#getTemplate(java.lang.String,
     * java.lang.String, java.lang.String)
     */
    public InputStream getTemplate(String templateName, String langId, String brandId)
        throws Exception
    {

        InputStream result = null;
        result =
            Thread
                .currentThread()
                .getContextClassLoader()
                .getResourceAsStream(
                    "./templates/" + templateName + "_" + brandId + "_" + langId + ".vm");
        return result;
    }

}
