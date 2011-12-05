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

package com.abiquo.scheduler;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.testng.Assert;

@Component
public class PopulateReader extends PopulateConstants
{

    @Autowired
    PopulateVirtualInfrastructure populateVirtualInfrastructure;

    @Autowired
    PopulateInfrastructure populateInfrastructure;

    @Autowired
    PopulateLimits populateLimits;

    @Autowired
    PopulateRules populateRules;

    @Autowired
    PopulateAction populateAction;

    public PopulateTestCase readModel(final List<String> decs)
    {
        PopulateTestCase tcase = new PopulateTestCase();
        tcase.actions = new LinkedList<AllocatorAction>();

        for (String line : decs)
        {
            line = cleanLine(line);
            if (line == null || line.isEmpty())
            {
                // commented line
            }
            else if (line.startsWith(TEST_NAME))
            {
                tcase.testName = line.substring(TEST_NAME.length());
            }
            else if (line.startsWith(TEST_DESCRIPTION))
            {
                tcase.testDescription = line.substring(TEST_DESCRIPTION.length());
            }
            else if (line.startsWith(DEC_DATACENTER))
            {
                populateInfrastructure.populateInfrastructure(line);
            }
            else if (line.startsWith(DEC_ENTERPRISE) || line.startsWith(DEC_VLAN))
            {
                populateVirtualInfrastructure.createVirtualInfrastructure(line);
            }
            else if (line.startsWith(DEC_RULE))
            {
                populateRules.createRule(line);
            }
            else if (line.startsWith(DEC_LIMIT))
            {
                populateLimits.createLimitRule(line);
            }
            else if (line.startsWith(DEC_ACTION))
            {
                AllocatorAction action = populateAction.readAction(line);
                tcase.actions.add(action);
            }
            else
            {
                throw new PopulateException("Invalid test declaration " + line);
            }
        }

        Assert.assertNotNull(tcase.testName, "Required test name");
        Assert.assertNotNull(tcase.testDescription, "Required test description");

        return tcase;
    }

    /**
     * clear comments from input (#)
     */
    private String cleanLine(final String line)
    {
        if (line.startsWith(COMMNET))
        {
            return null;
        }
        else if (line.contains(COMMNET))
        {
            return line.substring(0, line.indexOf(COMMNET)).trim();
        }
        else
        {
            return line.trim();
        }

    }

    public void removeVirtualMachine(final Integer virtualMachineId)
    {
        populateVirtualInfrastructure.removeVirtualMachine(virtualMachineId);
    }

    public void runningVirtualMachine(final Integer virtualMachineId)
    {
        populateVirtualInfrastructure.runningVirtualMachine(virtualMachineId);
    }
}
