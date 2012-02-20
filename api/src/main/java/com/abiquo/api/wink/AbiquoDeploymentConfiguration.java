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
