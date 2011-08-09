package com.abiquo.api.resources.config;

import javax.ws.rs.Path;

import org.apache.wink.common.annotations.Parent;
import org.springframework.stereotype.Controller;

import com.abiquo.api.resources.AbstractResource;

@Parent(PricingTemplatesResource.class)
@Path(PricingTemplateResource.PRICING_TEMPLATE_PARAM)
@Controller
public class PricingTemplateResource extends AbstractResource
{
    public static final String PRICING_TEMPLATE = "template";

    public static final String PRICING_TEMPLATE_PARAM = "{" + PRICING_TEMPLATE + "}";

}
