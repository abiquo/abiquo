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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.testng.Assert;

import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualDatacenterRep;
import com.abiquo.server.core.common.DefaultEntityWithLimits;
import com.abiquo.server.core.common.Limit;
import com.abiquo.server.core.enterprise.DatacenterLimits;
import com.abiquo.server.core.enterprise.DatacenterLimitsDAO;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.EnterpriseRep;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.InfrastructureRep;

@Component
@Transactional
public class PopulateLimits extends PopulateConstants
{

    @Autowired
    EnterpriseRep enterRep;

    @Autowired
    VirtualDatacenterRep vdcRep;

    @Autowired
    InfrastructureRep dcRep;

    @Autowired
    DatacenterLimitsDAO dcLimitDao;

    /**
     * <ul>
     * <li>limit.e1: -- limit
     * <li>limit.vd1:-- limit
     * <li>limit.d1.e1: -- limit
     * </ul>
     */
    public void createLimitRule(String limitDec)
    {
        String limit;
        String fragments[] = limitDec.split(DELIMITER_ENTITIES);

        Assert.assertTrue(fragments.length == 2 || fragments.length == 3,
            "Invalid limit declaration " + limitDec);
        Assert.assertTrue(fragments[0].equalsIgnoreCase(DEC_LIMIT), "Invalid limit declaration "
            + limitDec);

        switch (fragments.length)
        {
            case 2:

                fragments = fragments[1].split(DELIMITER_DEFINITION);

                String entity = fragments[0];
                String limits = fragments.length == 2 ? fragments[1] : null;

                if (entity.startsWith(DEC_ENTERPRISE))
                {
                    setEnterpriseLimits(entity, limits);
                }
                else if (entity.startsWith(DEC_VIRTUAL_DATACENTER))
                {
                    setVirtualDatacenterLimits(entity, limits);
                }
                else
                {
                    throw new PopulateException("Invalid declaration " + limitDec);
                }

                break;

            case 3:
                String datacenter = fragments[1];

                fragments = fragments[2].split(DELIMITER_DEFINITION);

                String enterprise = fragments[0];
                limit = fragments.length == 2 ? fragments[1] : null;

                setDatacenterLimit(datacenter, enterprise, limit);

                break;

            default:
                throw new PopulateException("Invalid declaration " + limitDec);
        }

    }

    private void setEnterpriseLimits(String enter, String limitDec)
    {
        Enterprise enterprise = enterRep.findByName(enter);

        Assert.assertNotNull(enterprise, "Enterprise not found " + enter);

        setLimits(enterprise, limitDec);

        enterRep.update(enterprise);
    }

    private void setVirtualDatacenterLimits(String virtualDc, String limitDec)
    {
        VirtualDatacenter vdc = vdcRep.findByName(virtualDc);

        Assert.assertNotNull(vdc, "Virtual datacenter not found " + virtualDc);

        setLimits(vdc, limitDec);

        vdcRep.update(vdc);
    }

    private void setDatacenterLimit(String dataCenter, String enter, String limitDec)
    {
        Enterprise enterprise = enterRep.findByName(enter);

        Assert.assertNotNull(enterprise, "Enterprise not found " + enter);

        Datacenter dc = dcRep.findByName(dataCenter);

        Assert.assertNotNull(dc, "Datacenter not found " + dataCenter);

        DatacenterLimits dcLimit = dcLimitDao.findByEnterpriseAndDatacenter(enterprise, dc);

        if (dcLimit == null)
        {
            dcLimit = new DatacenterLimits(enterprise, dc);
            setLimits(dcLimit, limitDec);
            dcLimitDao.persist(dcLimit);
        }
        else
        {
            setLimits(dcLimit, limitDec);
            dcLimitDao.flush();
        }

        // throw new PopulateException("Not implemented limit by datacenters");
    }

    /**
     * @param limitDeclaration, vlan[10,20];ram[10,20]
     */
    private void setLimits(DefaultEntityWithLimits entity, String limitDeclaration)
    {
        if (limitDeclaration == null)
        {
            return;
        }

        String frg[] = limitDeclaration.split(DELIMITER_LIMIT);

        for (String limit : frg)
        {
            if (limit.startsWith("cpu") || limit.startsWith("CPU"))
            {
                entity.setCpuCountLimits(createLimit(limit, false));
            }
            else if (limit.startsWith("ram") || limit.startsWith("RAM"))
            {

                entity.setRamLimitsInMb(createLimit(limit, true));
            }
            else if (limit.startsWith("hd") || limit.startsWith("HD"))
            {

                entity.setHdLimitsInMb(createLimit(limit, true));
            }
            else if (limit.startsWith("storage"))
            {

                entity.setStorageLimits(createLimit(limit, false));
            }
            else if (limit.startsWith("vlan") || limit.startsWith("VLAN"))
            {

                entity.setVlansLimits(createLimit(limit, false));
            }
            else if (limit.startsWith("publicIp") || limit.startsWith("publicip")
                || limit.startsWith("publicIP"))
            {

                entity.setPublicIPLimits(createLimit(limit, false));
            }
            else
            {
                throw new PopulateException("Invalid limit declaration " + limitDeclaration);
            }
        }
    }

    /**
     * hard,soft
     */
    private Limit createLimit(String limitFr, boolean gbToMb)
    {
        limitFr = limitFr.substring(limitFr.indexOf('[') + 1, limitFr.indexOf(']'));

        String frg[] = limitFr.split(DELIMITER_ATTRIBUTES);

        Assert.assertTrue(frg.length == 2, "Invalid limit decalration " + limitFr);

        Long soft = Long.parseLong(frg[0]);
        Long hard = Long.parseLong(frg[1]);

        return new Limit(gbToMb ? soft * GB_TO_MB : soft, gbToMb ? hard * GB_TO_MB : hard);
    }
}
