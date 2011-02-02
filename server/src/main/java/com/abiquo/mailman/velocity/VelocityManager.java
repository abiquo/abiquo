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

import java.io.StringWriter;
import java.io.Writer;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

import com.abiquo.mailman.velocity.bean.TemplateCacheBean;
import com.abiquo.mailman.velocity.constant.VelocityConstants;

/**
 * Velocity utility class
 */

public class VelocityManager
{

    private static VelocityManager instance_;

    /** Use of velocity in a separate instant model. */
    private static VelocityEngine ve_;

    public static VelocityManager getInstance() throws Exception
    {

        if (instance_ == null)
        {
            ve_ = new VelocityEngine();
            Properties velocityProps = new Properties();
            velocityProps.put(VelocityConstants.RESOURCE_LOADER,
                VelocityConstants.RESOURCE_LOADER_VALUE);
            velocityProps.put(VelocityConstants.RESOURCE_LOADER_DESCRIPTION,
                VelocityConstants.RESOURCE_LOADER_DESCRIPTION_VALUE);
            velocityProps.put(VelocityConstants.RESOURCE_LOADER_CLASS,
                VelocityConstants.RESOURCE_LOADER_CLASS_VALUE);
            velocityProps.put(VelocityConstants.RESOURCE_LOADER_CACHE,
                VelocityConstants.RESOURCE_LOADER_CACHE_VALUE);
            velocityProps.put(VelocityConstants.RESOURCE_LOADER_CHECK_CACHE,
                VelocityConstants.RESOURCE_LOADER_CHECK_CACHE_VALUE);
            ve_.init(velocityProps);
            instance_ = new VelocityManager();
        }
        return instance_;

    }

    /**
     * @throws Exception
     */
    private VelocityManager()
    {
    }

    private synchronized TemplateCacheBean getNewTemplate(String template, String langId,
        String brandId) throws ResourceNotFoundException, ParseErrorException, Exception
    {
        Template velTemplate = null;

        ve_.setApplicationAttribute(VelocityMailTemplate.APP_BRAND, brandId);
        ve_.setApplicationAttribute(VelocityMailTemplate.APP_LANG, langId);

        TemplateCacheBean cache = new TemplateCacheBean();
        velTemplate = ve_.getTemplate(template, VelocityMailTemplate.DEFAULT_ENCODING);
        cache.setTemplate(velTemplate);

        return cache;
    }

    /**
     * Get the template by primary key id-langid-brandid. Usefull when the merge is made by the
     * caller.
     * 
     * @param template
     * @param langId
     * @param brandId
     * @return Template
     * @throws ResourceNotFoundException
     * @throws ParseErrorException
     * @throws Exception
     */
    private TemplateCacheBean getTemplate(String template, String langId, String brandId)
        throws ResourceNotFoundException, ParseErrorException, Exception
    {
        // get from cache
        TemplateCacheBean cache =
            VelocityCacheManager.getInstance().getTemplate(template, brandId, langId);
        if (cache == null)
        {
            cache = getNewTemplate(template, langId, brandId);
            VelocityCacheManager.getInstance().setTemplate(template, langId, brandId, cache);
        }
        return cache;
    }

    /**
     * Merge the template with the context param.
     * 
     * @param templateName
     * @param context
     * @param langId
     * @param brandId
     * @return String - merged template.
     * @throws ResourceNotFoundException
     * @throws ParseErrorException
     * @throws MethodInvocationException
     * @throws Exception
     */
    public String merge(String templateName, Context context, String langId, String brandId)
        throws ResourceNotFoundException, ParseErrorException, MethodInvocationException, Exception
    {
        Writer result = new StringWriter();

        TemplateCacheBean cache = getTemplate(templateName, langId, brandId);

        Template template = cache.getTemplate();

        // merge the template with the context.
        template.merge(context, result);

        return result.toString();
    }
}
