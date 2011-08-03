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
package com.abiquo.model.util;

import java.util.Comparator;

/**
 * Compares an object by a list of properties.
 * 
 * @author ibarrera
 */
public class CompositeComparator
{
    /**
     * Builds a comparator that delegates on a list of comparators.
     * 
     * @param <T> The type of the objects to compare.
     * @param comparators The list of comparators to use.
     * @return The result of the comparison.
     */
    public static <T> Comparator<T> build(final Comparator<T>... comparators)
    {
        return new Comparator<T>()
        {
            @Override
            public int compare(final T o1, final T o2)
            {
                for (Comparator<T> comparator : comparators)
                {
                    int result = comparator.compare(o1, o2);
                    if (result != 0)
                    {
                        return result;
                    }
                }

                return 0;
            }
        };
    }

}
