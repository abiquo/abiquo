package com.abiquo.server.core.enterprise;

import java.util.List;

import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class ApprovalManagerGenerator extends DefaultEntityGenerator<ApprovalManager>
{
    public ApprovalManagerGenerator(final SeedGenerator seed)
    {
        super(seed);
    }

    @Override
    public void assertAllPropertiesEqual(final ApprovalManager obj1, final ApprovalManager obj2)
    {
        AssertEx.assertPropertiesEqualSilent(obj1, obj2);
    }

    @Override
    public ApprovalManager createUniqueInstance()
    {
        ApprovalManager approvalManager = new ApprovalManager();

        return approvalManager;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(final ApprovalManager entity,
        final List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);
    }
}
