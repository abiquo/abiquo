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

package com.abiquo.abiserver.persistence;

import org.hibernate.Session;

import com.abiquo.abiserver.business.hibernate.pojohb.workload.EnterpriseExclusionRuleHB;
import com.abiquo.abiserver.business.hibernate.pojohb.workload.MachineLoadRuleHB;
import com.abiquo.abiserver.persistence.dao.workload.EnterpriseExclusionRuleDAO;
import com.abiquo.abiserver.persistence.dao.workload.MachineLoadRuleDAO;
import com.abiquo.abiserver.persistence.dao.workload.hibernate.EnterpriseExclusionRuleDAOHibernate;
import com.abiquo.abiserver.persistence.dao.workload.hibernate.MachineLoadRuleDAOHibernate;

public class TestDAOHelper
{
    public static EnterpriseExclusionRuleDAO createEnterpriseExclusionRuleDAO(Session session)
    {
        assert session != null;

        EnterpriseExclusionRuleDAOHibernate dao = new EnterpriseExclusionRuleDAOHibernate();
        dao.setSession(session);
        dao.setPersistentClass(EnterpriseExclusionRuleHB.class);
        return dao;
    }

    public static MachineLoadRuleDAO createMachineLoadRuleDAO(Session session)
    {
        assert session != null;

        MachineLoadRuleDAOHibernate dao = new MachineLoadRuleDAOHibernate();
        dao.setSession(session);
        dao.setPersistentClass(MachineLoadRuleHB.class);
        return dao;
    }
}
