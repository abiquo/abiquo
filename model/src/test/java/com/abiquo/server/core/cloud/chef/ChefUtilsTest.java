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
package com.abiquo.server.core.cloud.chef;

import static com.abiquo.model.util.ChefUtils.getRecipeName;
import static com.abiquo.model.util.ChefUtils.getRoleName;
import static com.abiquo.model.util.ChefUtils.isRecipe;
import static com.abiquo.model.util.ChefUtils.isRole;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import com.abiquo.model.util.ChefUtils;

/**
 * Unit tests for the {@link ChefUtils} class.
 * 
 * @author ibarrera
 */
public class ChefUtilsTest
{

    @Test
    public void testIsRecipe()
    {
        assertTrue(isRecipe("recipe[test]"));

        assertFalse(isRecipe(null));
        assertFalse(isRecipe(""));
        assertFalse(isRecipe("recipe[]"));
        assertFalse(isRecipe("role[test]"));
        assertFalse(isRecipe("recipe"));
    }

    @Test
    public void testIsRole()
    {
        assertTrue(isRole("role[test]"));

        assertFalse(isRole(null));
        assertFalse(isRole(""));
        assertFalse(isRole("role[]"));
        assertFalse(isRole("recipe[test]"));
        assertFalse(isRole("role"));
    }

    @Test
    public void testGetRecipeName()
    {
        assertEquals(getRecipeName("recipe[test]"), "test");
    }

    @Test
    public void testGetRoleName()
    {
        assertEquals(getRoleName("role[test]"), "test");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testGetNullRecipeName()
    {
        getRecipeName(null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testGetInvalidRoleName()
    {
        getRoleName("recipe[test]");
    }
}
