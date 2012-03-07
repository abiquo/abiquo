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

package com.abiquo.model.enumerator;

import java.util.Comparator;

import com.abiquo.model.rest.RESTLink;

public enum LinkOrder implements Comparator<RESTLink>
{
    BY_REL
    {
        @Override
        public int compare(final RESTLink link0, final RESTLink link1)
        {
            if (link0.getRel() == null || link1.getRel() == null)
            {
                return 0;
            }

            return String.CASE_INSENSITIVE_ORDER.compare(link0.getRel(), link1.getRel());
        }
    },

    BY_TITLE
    {
        @Override
        public int compare(final RESTLink link0, final RESTLink link1)
        {
            if (link0.getTitle() == null || link1.getTitle() == null)
            {
                return 0;
            }

            return String.CASE_INSENSITIVE_ORDER.compare(link0.getTitle(), link1.getTitle());
        }
    }
}
