package com.abiquo.server.core.scheduler;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.WrapperDto;

/**
 * Represent a collection of enterpriseExclusionRules
 */
@XmlRootElement(name = "fitPolicyRules")
public class FitPolicyRulesDto extends WrapperDto<FitPolicyRuleDto>
{

    @Override
    @XmlElement(name = "fitPolicyRule")
    public List<FitPolicyRuleDto> getCollection()
    {
        return collection;
    }

}
