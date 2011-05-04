package com.abiquo.server.core.scheduler;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "rules")
public class RulesDto
{

    private FitPolicyRulesDto fitPolicyRules;

    private MachineLoadRulesDto machineLoadRules;

    private EnterpriseExclusionsRulesDto enterpriseExclusionRules;

    public FitPolicyRulesDto getFitPolicyRules()
    {
        return fitPolicyRules;
    }

    public void setFitPolicyRules(FitPolicyRulesDto fitPolicyRules)
    {
        this.fitPolicyRules = fitPolicyRules;
    }

    public MachineLoadRulesDto getMachineLoadRules()
    {
        return machineLoadRules;
    }

    public void setMachineLoadRules(MachineLoadRulesDto machineLoadRules)
    {
        this.machineLoadRules = machineLoadRules;
    }

    public EnterpriseExclusionsRulesDto getEnterpriseExclusionRules()
    {
        return enterpriseExclusionRules;
    }

    public void setEnterpriseExclusionRules(EnterpriseExclusionsRulesDto enterpriseExclusionRules)
    {
        this.enterpriseExclusionRules = enterpriseExclusionRules;
    }

}
