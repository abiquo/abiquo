/**
 * 
 */
package com.abiquo.api.wink;

import org.apache.wink.server.internal.DeploymentConfiguration;
import org.apache.wink.server.internal.RequestProcessor;

/**
 * @author jaume
 *
 */
public class AbiquoRequestProcessor extends RequestProcessor
{
    private AbiquoDeploymentConfiguration configuration;
    
    public AbiquoRequestProcessor(AbiquoDeploymentConfiguration configuration) {
        super(configuration);
    }
    
    public DeploymentConfiguration getConfiguration() {
        return configuration;
    }

}
