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
package com.abiquo.api.spring;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContextException;

/**
 * Process all bean deifnitions to replace community beans by the premium ones.
 * 
 * @author pnavarro
 */
public class BeanReplacementProcessor implements BeanFactoryPostProcessor
{
    /** The logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(BeanReplacementProcessor.class);

    /** Map with the bean -> replacement for the community beans. */
    private Map<String, String> replacements;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
        throws BeansException
    {
        if (!(beanFactory instanceof BeanDefinitionRegistry))
        {
            throw new ApplicationContextException("Unsupported bean factory type: "
                + beanFactory.getClass().getName()
                + ". Bean factory must implement the BeanDefinitionRegistry interface");
        }

        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;

        for (String candidate : replacements.keySet())
        {
            String replacement = replacements.get(candidate);
            BeanDefinition replacementBeanDefinition = registry.getBeanDefinition(replacement);

            LOGGER.info("Replacing bean {} with class: {}", candidate,
                replacementBeanDefinition.getBeanClassName());

            registry.removeBeanDefinition(candidate);
            registry.registerBeanDefinition(candidate, replacementBeanDefinition);
            registry.removeBeanDefinition(replacement);
        }
    }

    public Map<String, String> getReplacements()
    {
        return replacements;
    }

    public void setReplacements(Map<String, String> replacements)
    {
        this.replacements = replacements;
    }

}
