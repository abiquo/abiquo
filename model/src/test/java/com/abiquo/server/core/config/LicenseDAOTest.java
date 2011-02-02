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
 * Boston, MA 02111-1307, USA.
 */
package com.abiquo.server.core.config;

import javax.persistence.EntityManager;
import javax.validation.ConstraintViolationException;

import org.testng.annotations.Test;

import com.abiquo.server.core.common.persistence.DefaultDAOTestBase;
import com.softwarementors.bzngine.entities.test.PersistentInstanceTester;

public class LicenseDAOTest extends DefaultDAOTestBase<LicenseDAO, License>
{

    @Override
    protected LicenseDAO createDao(EntityManager entityManager)
    {
        return new LicenseDAO(entityManager);
    }

    @Override
    protected PersistentInstanceTester<License> createEntityInstanceGenerator()
    {
        return new LicenseGenerator(getSeed());
    }

    @Test
    public void test_existsAnyWithData()
    {
        License license = createUniqueEntity();
        license.setData("license data");

        ds().persistAll(license);

        LicenseDAO dao = createDaoForRollbackTransaction();

        assertTrue(dao.existsAnyWithData("license data"));
        assertFalse(dao.existsAnyWithData("UNEXISTING_DATA"));
    }

    @Test
    public void test_existsAnyOtherWithData()
    {
        License license1 = createUniqueEntity();
        License license2 = createUniqueEntity();
        license1.setData("first license");
        license2.setData("second license");
        ds().persistAll(license1, license2);

        LicenseDAO dao = createDaoForRollbackTransaction();
        License license = dao.findById(license1.getId());

        assertFalse(dao.existsAnyOtherWithData(license, "UNEXISTING_NAME"));
        assertFalse(dao.existsAnyOtherWithData(license, "first license"));
        assertTrue(dao.existsAnyOtherWithData(license, "second license"));
    }

    @Test(expectedExceptions = ConstraintViolationException.class)
    public void test_addInvalidLicenseWithTrailingWhitespace()
    {
        License license = createUniqueEntity();
        license.setData("test data   ");
        ds().persistAll(license);
    }

    @Test(expectedExceptions = ConstraintViolationException.class)
    public void test_addInvalidLicenseWithLeadingWhitespace()
    {
        License license = createUniqueEntity();
        license.setData("  test data");
        ds().persistAll(license);
    }

    @Test(expectedExceptions = ConstraintViolationException.class)
    public void test_addInvalidLicenseEmpty()
    {
        License license = createUniqueEntity();
        license.setData("");
        ds().persistAll(license);
    }

    @Test(expectedExceptions = ConstraintViolationException.class)
    public void test_addInvalidLicenseOnlySpaces()
    {
        License license = createUniqueEntity();
        license.setData(" ");
        ds().persistAll(license);
    }

}
