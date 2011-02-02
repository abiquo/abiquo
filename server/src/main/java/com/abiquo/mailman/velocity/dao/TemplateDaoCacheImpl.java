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

import com.abiquo.mailman.dao.AbstractCache;
import com.abiquo.mailman.dao.CacheDispatcher;
import com.abiquo.mailman.dao.CacheReceiver;

/**
 * Database Template Dao Cache.
 * 
 * @author Diego Parrilla
 * @date 04-dic-2009
 * @version $Revision: 1.2 $
 **/
public class TemplateDaoCacheImpl extends AbstractCache implements TemplateDao
{
    /**
     * @param dispatcher
     * @param receiver
     * @param dao
     */
    public TemplateDaoCacheImpl(CacheDispatcher dispatcher, CacheReceiver receiver, Object dao)
    {
        super(dispatcher, receiver, dao);
    }

    /*
     * (non-Javadoc)
     * @see TemplateDao#getTemplate(java.lang.String, java.lang.String, java.lang.String)
     */
    public InputStream getTemplate(String templateName, String langId, String brandId)
        throws Exception
    {
        return null;
    }
}
