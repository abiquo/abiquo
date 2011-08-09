package com.abiquo.api.resources.config;

import javax.ws.rs.Path;

import org.apache.wink.common.annotations.Workspace;
import org.springframework.stereotype.Controller;

import com.abiquo.api.resources.AbstractResource;

@Path(PricingTemplatesResource.PRICING_TEMPLATES_PATH)
@Controller
@Workspace(workspaceTitle = "Abiquo configuration workspace", collectionTitle = "PricingTemplates")
public class PricingTemplatesResource extends AbstractResource
{

    public static final String PRICING_TEMPLATES_PATH = "config/pricingTemplates";

}
