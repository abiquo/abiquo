package com.abiquo.server.core.enterprise;

import java.util.Date;
import java.util.List;
import java.util.Random;

import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class ApprovalGenerator extends DefaultEntityGenerator<Approval>
{
    UserGenerator userGenerator;

    public ApprovalGenerator(final SeedGenerator seed)
    {
        super(seed);

        userGenerator = new UserGenerator(seed);
    }

    @Override
    public void assertAllPropertiesEqual(final Approval obj1, final Approval obj2)
    {
        AssertEx.assertPropertiesEqualSilent(obj1, obj2, Approval.TIME_REQUESTED_PROPERTY,
            Approval.TOKEN_PROPERTY, Approval.TIME_RESPONSE_PROPERTY, Approval.REASON_PROPERTY,
            Approval.STATUS_PROPERTY, Approval.APPROVAL_TYPE_PROPERTY, Approval.REQUEST_PROPERTY);
    }

    @Override
    public Approval createUniqueInstance()
    {
        String token = newString(nextSeed(), Approval.TOKEN_LENGTH_MIN, Approval.TOKEN_LENGTH_MAX);
        ApprovalType approvalType = newEnum(ApprovalType.class, nextSeed());
        ApprovalState approvalState = newEnum(ApprovalState.class, nextSeed());
        Date timeRequested = newDateTime(nextSeed()).toDate();
        Date timeResponse = newDateTime(nextSeed()).toDate();
        User user = userGenerator.createUniqueInstance();

        byte[] request = new byte[5];
        new Random().nextBytes(request);

        return new Approval(token,
            approvalType,
            approvalState,
            timeRequested,
            timeResponse,
            request,
            user);
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(final Approval entity,
        final List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

        User user = entity.getUser();
        userGenerator.addAuxiliaryEntitiesToPersist(user, entitiesToPersist);
        entitiesToPersist.add(user);
    }

}
