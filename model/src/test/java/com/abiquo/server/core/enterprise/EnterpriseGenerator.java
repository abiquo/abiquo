package com.abiquo.server.core.enterprise;

/**
 * Abiquo community edition
 * cloud management application for hybrid clouds
 * Copyright (C) 2008-2010 - Abiquo Holdings S.L.
 * 
 * This application is free software; you can redistribute it and/or
 * modify it under the terms of the GNU LESSER GENERAL PUBLIC
 * LICENSE as published by the Free Software Foundation under
 * version 3 of the License
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * LESSER GENERAL PUBLIC LICENSE v.3 for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA. */

import java.util.List;

import com.abiquo.server.core.common.DefaultEntityWithLimitsGenerator;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class EnterpriseGenerator extends DefaultEntityWithLimitsGenerator<Enterprise>
{

    public EnterpriseGenerator(final SeedGenerator seed)
    {
        super(seed);
    }

    @Override
    public void assertAllPropertiesEqual(final Enterprise ent1, final Enterprise ent2)
    {
        AssertEx.assertPropertiesEqualSilent(ent1, ent2, Enterprise.NAME_PROPERTY);

        if (ent1.getChefURL() != null)
        {
            AssertEx.assertPropertiesEqualSilent(ent1, ent2, Enterprise.CHEF_URL_PROPERTY,
                Enterprise.CHEF_CLIENT_PROPERTY, Enterprise.CHEF_VALIDATOR_PROPERTY,
                Enterprise.CHEF_CLIENT_CERT_PROPERTY, Enterprise.CHEF_VALIDATOR_CERT_PROPERTY);
        }
    }

    @Override
    public Enterprise createUniqueInstance()
    {
        final String name =
            newString(nextSeed(), Enterprise.NAME_LENGTH_MIN, Enterprise.NAME_LENGTH_MAX);
        return createInstance(name);
    }

    public Enterprise createInstanceNoLimits()
    {
        final String name =
            newString(nextSeed(), Enterprise.NAME_LENGTH_MIN, Enterprise.NAME_LENGTH_MAX);
        return createInstanceNoLimits(name);
    }

    public Enterprise createInstance(final String name)
    {
        int seed = nextSeed();

        final int ramSoftLimitInMb = seed;
        final int cpuCountSoftLimit = seed;
        final int hdSoftLimitInMb = seed;
        final int ramHardLimitInMb = seed;
        final int cpuCountHardLimit = seed;
        final int hdHardLimitInMb = seed;

        return new Enterprise(name,
            ramSoftLimitInMb,
            cpuCountSoftLimit,
            hdSoftLimitInMb,
            ramHardLimitInMb,
            cpuCountHardLimit,
            hdHardLimitInMb);
    }

    public Enterprise createInstanceNoLimits(final String name)
    {
        return new Enterprise(name, 0, 0, 0, 0, 0, 0);
    }

    public Enterprise createChefInstance()
    {
        Enterprise enterprise = createUniqueInstance();
        return addChefConfig(enterprise);
    }

    public Enterprise addChefConfig(final Enterprise enterprise)
    {
        String chefServerURL = "https://api.opscode.com/organizations/ent" + nextSeed();
        String clientName =
            newString(nextSeed(), Enterprise.CHEF_CLIENT_LENGTH_MIN,
                Enterprise.CHEF_CLIENT_LENGTH_MAX);
        String validatorName =
            newString(nextSeed(), Enterprise.CHEF_VALIDATOR_LENGTH_MIN,
                Enterprise.CHEF_VALIDATOR_LENGTH_MAX);
        String clientCert = newString(nextSeed(), 0, 100);
        String validationCert = newString(nextSeed(), 0, 100);

        enterprise.setChefURL(chefServerURL);
        enterprise.setChefClient(clientName);
        enterprise.setChefValidator(validatorName);
        enterprise.setChefValidatorCertificate(validationCert);
        enterprise.setChefClientCertificate(clientCert);

        return enterprise;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(final Enterprise entity,
        final List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);
    }

}
