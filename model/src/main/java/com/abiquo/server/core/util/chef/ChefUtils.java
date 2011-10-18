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
package com.abiquo.server.core.util.chef;

import static com.google.common.collect.Lists.transform;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.chef.RunlistElement;
import com.google.common.base.Function;

/**
 * Utility methods to work with Chef servers.
 * 
 * @author ibarrera
 */
public class ChefUtils
{
    /** Template for recipe elements in a node run list. */
    private static final String RECIPE_TEMPLATE = "^recipe\\[(.+)\\]$";

    /** Template for role elements in a node run list. */
    private static final String ROLE_TEMPLATE = "^role\\[(.+)\\]$";

    /**
     * Check if the given element of a node run list is a recipe.
     * 
     * @param element The element to check.
     * @return Boolean indicating if the given element of a node run list is a recipe.
     */
    public static boolean isRecipe(final String element)
    {
        if (element == null)
        {
            return false;
        }

        return element.matches(RECIPE_TEMPLATE);
    }

    /**
     * Check if the given element of a node run list is a role.
     * 
     * @param element The element to check.
     * @return Boolean indicating if the given element of a node run list is a role.
     */
    public static boolean isRole(final String element)
    {
        if (element == null)
        {
            return false;
        }

        return element.matches(ROLE_TEMPLATE);
    }

    /**
     * Get the name of the recipe from a node run list element.
     * 
     * @param element The element.
     * @return The name of the recipe from a node run list element.
     */
    public static String getRecipeName(final String element)
    {
        if (element == null)
        {
            throw new IllegalArgumentException("The given parameter is not a recipe");
        }

        Pattern p = Pattern.compile(RECIPE_TEMPLATE);
        Matcher m = p.matcher(element);

        if (!m.matches())
        {
            throw new IllegalArgumentException("The given parameter is not a recipe");
        }

        return m.group(1);
    }

    /**
     * Get the name of the role from a node run list element.
     * 
     * @param element The element.
     * @return The name of the role from a node run list element.
     */
    public static String getRoleName(final String element)
    {
        if (element == null)
        {
            throw new IllegalArgumentException("The given parameter is not a role");
        }

        Pattern p = Pattern.compile(ROLE_TEMPLATE);
        Matcher m = p.matcher(element);

        if (!m.matches())
        {
            throw new IllegalArgumentException("The given parameter is not a role");
        }

        return m.group(1);
    }

    /**
     * Convert the given text in a valid recipe for a runlist.
     * 
     * @param element The text to convert.
     * @return The recipe for the runlist.
     */
    public static String toRecipe(final String element)
    {
        return "recipe[" + element + "]";
    }

    /**
     * Convert the given text in a valid role for a runlist.
     * 
     * @param element The text to convert.
     * @return The role for the runlist.
     */
    public static String toRole(final String element)
    {
        return "role[" + element + "]";
    }

    /**
     * Get a valid node name for the given virtual machine.
     * <p>
     * The valid node names should be valid hostnames.
     * 
     * @param virtualMachine The virtual machine.
     * @return A valid node name.
     */
    public static String validNodeName(final VirtualMachine virtualMachine)
    {
        return virtualMachine.getName().replaceAll("_", "-");
    }

    /**
     * Return a list with the names of the given runlist elements.
     * 
     * @param elements The runlist elements.
     * @return A list with the names of the given runlist elements.
     */
    public static List<String> getElementNames(final List<RunlistElement> elements)
    {
        return transform(elements, new Function<RunlistElement, String>()
        {
            @Override
            public String apply(final RunlistElement input)
            {
                return input.getName();
            }
        });
    }
}
