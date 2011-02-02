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

package com.abiquo.server.core.scheduler;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.abiquo.server.core.scheduler.EnterpriseExclusionRule;
import com.abiquo.server.core.scheduler.FitPolicyRule;
import com.abiquo.server.core.scheduler.MachineLoadRule;
import com.abiquo.server.core.scheduler.PersistentRule;

/**
 * Utility methods to manipulate Workload Rules.
 * 
 * @author ibarrera
 */
public class RuleUtils
{
    /**
     * Sorts the given rule list.
     * 
     * @param rules The rule list to sort.
     */
    public static void sort(final List< ? extends PersistentRule> rules)
    {
        Collections.sort(rules, new RuleComparator());
    }

    /**
     * Comparator used to sort a set of Workload rules.
     * 
     * @author ibarrera
     */
    private static class RuleComparator implements Comparator<PersistentRule>, Serializable
    {
        /** Serial UID. */
        private static final long serialVersionUID = 1L;

        @Override
        public int compare(final PersistentRule rule0, final PersistentRule rule1)
        {
            return getWeight(rule0) - getWeight(rule1);
        }

        private int getWeight(final PersistentRule rule)
        {
            if (rule instanceof FitPolicyRule)
            {
                return 0;
            }
            else if (rule instanceof EnterpriseExclusionRule)
            {
                return 1;
            }
            else if (rule instanceof MachineLoadRule)
            {
                return 2;
            }
            else
            {
                return 3;
            }
        }
    }
}
