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

import java.util.HashMap;
import java.util.Map;

import com.abiquo.mailman.velocity.bean.TemplateCacheBean;

/**
 * Caches the templates from the database
 **/
public class VelocityCacheManager
{
    // private static final Logger debug = LoggerFactory.getLogger(VelocityCacheManager.class);

    private static final VelocityCacheManager INSTANCE = new VelocityCacheManager();

    /*
     * cache for sites information.
     */
    private static Map<String, TemplateCacheBean> templates_;

    protected VelocityCacheManager()
    {
        templates_ = new HashMap<String, TemplateCacheBean>();
    }

    /**
     * Method getInstance.
     * 
     * @return SiteCacheManager
     */
    public static VelocityCacheManager getInstance()
    {
        return INSTANCE;
    }

    public void setTemplate(String templateId, String brandId, String langId,
        TemplateCacheBean value)
    {
        templates_.put(templateId + brandId + langId, value);
    }

    public TemplateCacheBean getTemplate(String templateId, String brandId, String langId)
    {
        return templates_.get(templateId + brandId + langId);
    }

    public void resetTemplateCache()
    {
        synchronized (templates_)
        {
            templates_.clear();
        }
    }

    public void resetTemplateCache(String templateId, String brandId, String langId)
    {
        synchronized (templates_)
        {
            if (templates_.containsKey(templateId + brandId + langId))
            {
                templates_.remove(templateId + brandId + langId);
            }
        }
    }
}
