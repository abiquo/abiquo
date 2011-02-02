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

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring context for the {@link SpringReplacementsTest}.
 * 
 * @author ibarrera
 */
@Configuration
public class SpringReplacementsTestContext
{
    static final String ORIGINAL_STRING = "Dummy bean";

    static final String REPLACED_STRING = "Dummy bean replaced";

    @Bean
    public BeanReplacementProcessor replacementProcessor()
    {
        Map<String, String> replacements = new HashMap<String, String>();
        replacements.put("replacedBean", "replacementBean");

        BeanReplacementProcessor processor = new BeanReplacementProcessor();
        processor.setReplacements(replacements);
        return processor;
    }

    @Bean
    public String replacedBean()
    {
        return ORIGINAL_STRING;
    }

    @Bean
    public String replacementBean()
    {
        return REPLACED_STRING;
    }
}
