package com.abiquo.server.core.scheduler;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.WrapperDto;

/**
 * Represent a collection of machineLoadRules
 */
@XmlRootElement(name = "machineLoadRules")
public class MachineLoadRulesDto extends WrapperDto<MachineLoadRuleDto>
{

    @Override
    @XmlElement(name = "machineLoadRule")
    public List<MachineLoadRuleDto> getCollection()
    {
        return collection;
    }

}
