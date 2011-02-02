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

package com.abiquo.mailman.velocity;

import java.io.InputStream;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.mailman.dao.DynamicDaoFactory;
import com.abiquo.mailman.velocity.dao.FileTemplateDaoImpl;
import com.abiquo.mailman.velocity.dao.TemplateDao;

/**
 * Database Template Loader.
 **/
public class DBResourceLoader extends ResourceLoader
{

    private static final Logger debug = LoggerFactory.getLogger(DBResourceLoader.class.getName());



    /**
     * Default constructor
     */
    public DBResourceLoader()
    {
    }

    /*
     * (non-Javadoc)
     * @see
     * org.apache.velocity.runtime.resource.loader.ResourceLoader#getResourceStream(java.lang.String
     * )
     */
    public InputStream getResourceStream(String source) throws ResourceNotFoundException
    {
        TemplateDao dao = null;
        InputStream daoReturn = null;

        try
        {
            dao = (TemplateDao) DynamicDaoFactory.getInstance().getDao(FileTemplateDaoImpl.class);
            daoReturn =
                dao.getTemplate(source, (String) super.rsvc.getApplicationAttribute(VelocityMailTemplate.APP_LANG),
                    (String) super.rsvc.getApplicationAttribute(VelocityMailTemplate.APP_BRAND));
        }
        catch (ResourceNotFoundException e)
        {
            String msg = "DataSourceResourceLoader Error: could not find resource.";
            debug.error(msg, e);
            throw e;
        }
        catch (Exception e)
        {
            String msg = "Error getting template.";
            debug.error(msg, e);
            throw new ResourceNotFoundException(msg);
        }
        return daoReturn;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.apache.velocity.runtime.resource.loader.ResourceLoader#isSourceModified(org.apache.velocity
     * .runtime.resource.Resource)
     */
    public boolean isSourceModified(Resource resource)
    {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.apache.velocity.runtime.resource.loader.ResourceLoader#getLastModified(org.apache.velocity
     * .runtime.resource.Resource)
     */
    public long getLastModified(Resource resource)
    {
        return 0L;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.apache.velocity.runtime.resource.loader.ResourceLoader#init(org.apache.commons.collections
     * .ExtendedProperties)
     */
    public void init(ExtendedProperties configuration)
    {
        debug.info("Resource Loader Initalized.");
    }
}
