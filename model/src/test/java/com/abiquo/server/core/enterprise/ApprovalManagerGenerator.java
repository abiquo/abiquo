package com.abiquo.server.core.enterprise;

import java.util.List;

import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class ApprovalManagerGenerator extends DefaultEntityGenerator<ApprovalManager>
{

    EnterpriseGenerator enterpriseGenerator;

    UserGenerator userGenerator;

    public ApprovalManagerGenerator(final SeedGenerator seed)
    {
        super(seed);

        enterpriseGenerator = new EnterpriseGenerator(seed);

        userGenerator = new UserGenerator(seed);

    }

    @Override
    public void assertAllPropertiesEqual(final ApprovalManager obj1, final ApprovalManager obj2)
    {
        AssertEx.assertPropertiesEqualSilent(obj1, obj2, ApprovalManager.APPROVAL_MAIL_PROPERTY);
    }

    @Override
    public ApprovalManager createUniqueInstance()
    {
        // FIXME: Write here how to create the pojo

        ApprovalManager approvalManager = new ApprovalManager();

        // FIXME: Write here how to create the pojo
        Enterprise enterprise = enterpriseGenerator.createUniqueInstance();
        User user = userGenerator.createUniqueInstance();
        final String approvalMail = newString(nextSeed(), 1, 10);

        approvalManager.setEnterprise(enterprise);
        approvalManager.setUser(user);
        approvalManager.setApprovalMail(approvalMail);

        return approvalManager;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(final ApprovalManager entity,
        final List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

        Enterprise enterprise = entity.getEnterprise();
        enterpriseGenerator.addAuxiliaryEntitiesToPersist(enterprise, entitiesToPersist);
        entitiesToPersist.add(enterprise);

        User user = entity.getUser();
        userGenerator.addAuxiliaryEntitiesToPersist(user, entitiesToPersist);
        entitiesToPersist.add(user);

    }

}
