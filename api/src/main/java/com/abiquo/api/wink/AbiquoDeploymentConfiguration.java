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

/**
 * 
 */
package com.abiquo.api.wink;

import java.util.ArrayList;

import javax.ws.rs.core.Application;

import org.apache.wink.common.internal.application.ApplicationValidator;
import org.apache.wink.common.internal.registry.ProvidersRegistry;
import org.apache.wink.server.internal.DeploymentConfiguration;
import org.apache.wink.server.internal.application.ApplicationProcessor;
import org.apache.wink.server.internal.registry.ResourceRegistry;

/**
 * @author jaume
 *
 */
public class AbiquoDeploymentConfiguration extends DeploymentConfiguration
{
    private AbiquoResourceRegistry    resourceRegistry;
    private ProvidersRegistry         providersRegistry;
    
    /**
     * Initializes registries. Usually there should be no need to override this
     * method. When creating Resources or Providers registry, ensure that they
     * use the same instance of the ApplicationValidator.
     */
    @Override
    protected void initRegistries() {
        super.initRegistries();
        ApplicationValidator applicationValidator = new ApplicationValidator();
        providersRegistry = new ProvidersRegistry(getOfFactoryRegistry(), applicationValidator);
        resourceRegistry = new AbiquoResourceRegistry(getOfFactoryRegistry(), applicationValidator);
    }
    
    @Override
    public void addApplication(Application application, boolean isSystemApplication) {
        super.addApplication(application, isSystemApplication);
        getApplications().clear();
        new ApplicationProcessor(application, getResourceRegistry(), getProvidersRegistry(),
                                 isSystemApplication).process();
        getApplications().add(application);
    }
    
    @Override
    public ProvidersRegistry getProvidersRegistry() {
        return providersRegistry;
    }

    @Override
    public ResourceRegistry getResourceRegistry() {
        return resourceRegistry;
    }  
}
