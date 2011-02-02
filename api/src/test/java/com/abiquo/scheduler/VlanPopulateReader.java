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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class VlanPopulateReader extends PopulateReader
{
    final static Logger LOGGER = LoggerFactory
    .getLogger(VlanPopulateReader.class);
    
    /**
     * Rack 1 is full, the virtual machine should be allocated in Rack2 
     * @return
     */
    public AllocatorAction rackFullModel()
    {
        AllocatorAction action = null;
        try
        {
            String hyper_type = "XEN_3";
            // Create rack 1, 1000 vlans permitted "dc1.r1:2,1002,2,10,[1]"
            String dc1 = "dc1";
            String r1 = "r1";
            String rack1Parameters = "2,11,1,10,[1]";
            populateInfrastructure.populateInfrastructure(dc1);
            populateInfrastructure.populateInfrastructure(dc1 + "."
                + r1 + ":" + rack1Parameters);

            // Create 10pm same capacity same hyper
            for (int i = 0; i < 10; i++)
            {
                populateInfrastructure.createMachine(dc1, r1, "m" + i
                    + ":" + hyper_type);
            }
            // Create rack 2
            String r2 = "r2";
            populateInfrastructure.populateInfrastructure(dc1 + "."
                + r2 + ":" + rack1Parameters);

            // Create 10pm same capacity same hyper
            for (int i = 1; i < 2; i++)
            {
                populateInfrastructure.createMachine(dc1, r2, "ma" + i
                    + ":" + "XEN_3");
            }

            // 1 vdc/1 vlan/ 1 vapp/ 1 vm/ 1 vnic/

            // "e1.vdc1:dc1,XEN_3";

            for (int j = 2; j < 11; j++)
            {
                populateVirtualInfrastructure
                .createVirtualInfrastructure(PopulateConstants.DEC_ENTERPRISE + j);
                populateVirtualInfrastructure
                    .createVirtualInfrastructure(PopulateConstants.DEC_ENTERPRISE + j
                        + "."
                        + PopulateConstants.DEC_VIRTUAL_DATACENTER + j
                        + ":" + dc1
                        + "," + hyper_type);
//                e1.vi1:dc1,1,1,2
                populateVirtualInfrastructure
                .createVirtualInfrastructure(PopulateConstants.DEC_ENTERPRISE + j + "." + PopulateConstants.DEC_VIRTUAL_IMAGE + j + ":" + dc1 + "," + "1,1,2");
                populateVirtualInfrastructure.createVirtualInfrastructure(PopulateConstants.DEC_VLAN
                    + j + ":"
                    + PopulateConstants.DEC_VIRTUAL_DATACENTER + j
                    + "," + r1);
                populateVirtualInfrastructure
                .createVirtualInfrastructure(PopulateConstants.DEC_ENTERPRISE + j + "." + PopulateConstants.DEC_VIRTUAL_DATACENTER + j + "." + PopulateConstants.DEC_VIRTUAL_APPLIANCE + j);
                populateVirtualInfrastructure
                    .createVirtualInfrastructure(PopulateConstants.DEC_ENTERPRISE + j + "." + PopulateConstants.DEC_VIRTUAL_DATACENTER + j + "." + PopulateConstants.DEC_VIRTUAL_APPLIANCE + j
                        + "." + PopulateConstants.DEC_VIRTUAL_MACHINE
                        + j + ":"
                        + PopulateConstants.DEC_VIRTUAL_IMAGE + j
                        + "," + "vnic" + j
                        + "," + PopulateConstants.DEC_VLAN + j);
                populateLimits.createLimitRule(PopulateConstants.DEC_LIMIT + "." + dc1 + "." +  PopulateConstants.DEC_ENTERPRISE + j);
            }
            populateVirtualInfrastructure
            .createVirtualInfrastructure(PopulateConstants.DEC_ENTERPRISE + 501);
            // Creation of the virtual machine to allocate
            populateVirtualInfrastructure.createVirtualInfrastructure(PopulateConstants.DEC_ENTERPRISE
                + 501 + "." + PopulateConstants.DEC_VIRTUAL_DATACENTER
                + 501 + ":" + dc1
                + "," + hyper_type);
            populateVirtualInfrastructure
            .createVirtualInfrastructure(PopulateConstants.DEC_ENTERPRISE + 501 + "." + PopulateConstants.DEC_VIRTUAL_IMAGE + 501 + ":" + dc1 + "," + "1,1,2");
            populateVirtualInfrastructure.createVirtualInfrastructure(PopulateConstants.DEC_VLAN
                + 501 + ":"
                + PopulateConstants.DEC_VIRTUAL_DATACENTER + 501);
            populateVirtualInfrastructure
            .createVirtualInfrastructure(PopulateConstants.DEC_ENTERPRISE + 501 + "." + PopulateConstants.DEC_VIRTUAL_DATACENTER + 501 + "." + PopulateConstants.DEC_VIRTUAL_APPLIANCE + 501);
            populateVirtualInfrastructure
            .createVirtualInfrastructure(PopulateConstants.DEC_ENTERPRISE + 501 + "." + PopulateConstants.DEC_VIRTUAL_DATACENTER + 501 + "." + PopulateConstants.DEC_VIRTUAL_APPLIANCE + 501
                + "." + PopulateConstants.DEC_VIRTUAL_MACHINE
                + 501 + ":"
                + PopulateConstants.DEC_VIRTUAL_IMAGE + 501
                + "," + "vnic" + 501
                + "," + PopulateConstants.DEC_VLAN + 501);
            populateLimits.createLimitRule(PopulateConstants.DEC_LIMIT + "." + dc1 + "." +  PopulateConstants.DEC_ENTERPRISE + 501);
            
            //Charging rule.fit.default:PROGRESSIVE
            
            populateRules.createRule("rule.fit.default:PROGRESSIVE");
            
            action = populateAction.readAction("action.allocate.vm501=m1");
        }
        catch (PopulateException e)
        {
            LOGGER.error(e.getMessage());
        }
        return action;
    }
    
    /**
     * Allocation of the VDC 10, in a rack overloading the NRSQ
     * @return
     */
    public AllocatorAction rackWithNRSQwarning()
    {
        AllocatorAction action = null;
        try
        {
            String hyper_type = "XEN_3";
            // Create rack 1, 10 VLANS permitted from 2-to 11
            String dc1 = "dc1";
            String r1 = "r1";
            String rack1Parameters = "2,11,1,10,[1]";
            populateInfrastructure.populateInfrastructure(dc1);
            populateInfrastructure.populateInfrastructure(dc1 + "."
                + r1 + ":" + rack1Parameters);

            // Create 10pm same capacity same hyper
            for (int i = 0; i < 10; i++)
            {
                populateInfrastructure.createMachine(dc1, r1, "m" + i
                    + ":" + hyper_type);
            }
            // 1 vdc/1 vlan/ 1 vapp/ 1 vm/ 1 vnic/

            // "e1.vdc1:dc1,XEN_3";

            //Creating 9 vdc, 9 vlans, 9 vapps
            for (int j = 2; j < 11; j++)
            {
                populateVirtualInfrastructure
                .createVirtualInfrastructure(PopulateConstants.DEC_ENTERPRISE + j);
                populateVirtualInfrastructure
                    .createVirtualInfrastructure(PopulateConstants.DEC_ENTERPRISE + j
                        + "."
                        + PopulateConstants.DEC_VIRTUAL_DATACENTER + j
                        + ":" + dc1
                        + "," + hyper_type);
                populateVirtualInfrastructure
                .createVirtualInfrastructure(PopulateConstants.DEC_ENTERPRISE + j + "." + PopulateConstants.DEC_VIRTUAL_IMAGE + j + ":" + dc1 + "," + "1,1,2");
                populateVirtualInfrastructure.createVirtualInfrastructure(PopulateConstants.DEC_VLAN
                    + j + ":"
                    + PopulateConstants.DEC_VIRTUAL_DATACENTER + j
                    + "," + r1);
                populateVirtualInfrastructure
                .createVirtualInfrastructure(PopulateConstants.DEC_ENTERPRISE + j + "." + PopulateConstants.DEC_VIRTUAL_DATACENTER + j + "." + PopulateConstants.DEC_VIRTUAL_APPLIANCE + j);
                populateVirtualInfrastructure
                    .createVirtualInfrastructure(PopulateConstants.DEC_ENTERPRISE + j + "." + PopulateConstants.DEC_VIRTUAL_DATACENTER + j + "." + PopulateConstants.DEC_VIRTUAL_APPLIANCE + j
                        + "." + PopulateConstants.DEC_VIRTUAL_MACHINE
                        + j + ":"
                        + PopulateConstants.DEC_VIRTUAL_IMAGE + j
                        + "," + "vnic" + j
                        + "," + PopulateConstants.DEC_VLAN + j);
                populateLimits.createLimitRule(PopulateConstants.DEC_LIMIT + "." + dc1 + "." +  PopulateConstants.DEC_ENTERPRISE + j);
            }
            // Creating of the virtual image 12
            populateVirtualInfrastructure
            .createVirtualInfrastructure(PopulateConstants.DEC_ENTERPRISE + 2 + "." + PopulateConstants.DEC_VIRTUAL_IMAGE + 12 + ":" + dc1 + "," + "1,1,2");
            //Creating the VLAN 12
            populateVirtualInfrastructure.createVirtualInfrastructure(PopulateConstants.DEC_VLAN
                + 12 + ":"
                + PopulateConstants.DEC_VIRTUAL_DATACENTER + 2);
            // Creating the virtual appliance 12
            populateVirtualInfrastructure
            .createVirtualInfrastructure(PopulateConstants.DEC_ENTERPRISE + 2 + "." + PopulateConstants.DEC_VIRTUAL_DATACENTER + 2 + "." + PopulateConstants.DEC_VIRTUAL_APPLIANCE + 12);
            //Creating the the virtual machine 12 with the virtual image 12
            populateVirtualInfrastructure
            .createVirtualInfrastructure(PopulateConstants.DEC_ENTERPRISE + 2 + "." + PopulateConstants.DEC_VIRTUAL_DATACENTER + 2 + "." + PopulateConstants.DEC_VIRTUAL_APPLIANCE + 12
                + "." + PopulateConstants.DEC_VIRTUAL_MACHINE
                + 12 + ":"
                + PopulateConstants.DEC_VIRTUAL_IMAGE + 12
                + "," + "vnic" + 12
                + "," + PopulateConstants.DEC_VLAN + 12);
            populateLimits.createLimitRule(PopulateConstants.DEC_LIMIT + "." + dc1 + "." +  PopulateConstants.DEC_ENTERPRISE + 2);
            
            //Charging rule.fit.default:PROGRESSIVE
            
            populateRules.createRule("rule.fit.default:PROGRESSIVE");
            
            action = populateAction.readAction("action.allocate.vm10=m1");
        }
        catch (PopulateException e)
        {
            LOGGER.error(e.getMessage());
        }
        return action;
    }
    
    private void createVirtualInfrastructure(String virtualName, String datacenterName, String hyperType, String rackName)
    {
        populateVirtualInfrastructure
        .createVirtualInfrastructure(PopulateConstants.DEC_ENTERPRISE + virtualName);
        populateVirtualInfrastructure
            .createVirtualInfrastructure(PopulateConstants.DEC_ENTERPRISE + virtualName
                + "."
                + PopulateConstants.DEC_VIRTUAL_DATACENTER + virtualName
                + ":" + PopulateConstants.DEC_DATACENTER + datacenterName
                + "," + hyperType);
        populateVirtualInfrastructure
        .createVirtualInfrastructure(PopulateConstants.DEC_ENTERPRISE + virtualName + "." + PopulateConstants.DEC_VIRTUAL_IMAGE + virtualName + ":" + datacenterName + "," + "1,1,2");
        populateVirtualInfrastructure.createVirtualInfrastructure(PopulateConstants.DEC_VLAN
            + virtualName + ":"
            + PopulateConstants.DEC_VIRTUAL_DATACENTER + virtualName
            + "," + PopulateConstants.DEC_RACK + rackName);
        populateVirtualInfrastructure
        .createVirtualInfrastructure(PopulateConstants.DEC_ENTERPRISE + virtualName + "." + PopulateConstants.DEC_VIRTUAL_DATACENTER + virtualName + "." + PopulateConstants.DEC_VIRTUAL_APPLIANCE + virtualName);
        populateVirtualInfrastructure
            .createVirtualInfrastructure(PopulateConstants.DEC_ENTERPRISE + virtualName + "." + PopulateConstants.DEC_VIRTUAL_DATACENTER + virtualName + "." + PopulateConstants.DEC_VIRTUAL_APPLIANCE + virtualName
                + "." + PopulateConstants.DEC_VIRTUAL_MACHINE
                + virtualName + ":"
                + PopulateConstants.DEC_VIRTUAL_IMAGE + virtualName
                + "," + "vnic" + virtualName
                + "," + PopulateConstants.DEC_VLAN + virtualName);
        populateLimits.createLimitRule(PopulateConstants.DEC_LIMIT + "." + datacenterName + "." +  PopulateConstants.DEC_ENTERPRISE + virtualName);
        
    }

}
