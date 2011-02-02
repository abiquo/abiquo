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

package com.abiquo.abiserver.persistence.dao.workload.hibernate;

import junit.framework.Assert;

import org.hibernate.Session;
import org.testng.annotations.Test;

import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.DatacenterHB;
import com.abiquo.abiserver.business.hibernate.pojohb.workload.MachineLoadRuleHB;
import com.abiquo.abiserver.exception.PersistenceException;
import com.abiquo.abiserver.persistence.DataAccessTestBase;
import com.abiquo.abiserver.persistence.SessionUtils;
import com.abiquo.abiserver.persistence.TestDAOHelper;
import com.abiquo.abiserver.persistence.TestEntityGenerationUtils;
import com.abiquo.abiserver.persistence.dao.workload.MachineLoadRuleDAO;

@Test
public class MachineLoadRuleDAOTest extends DataAccessTestBase {
	/** This test makes completely sure we have everything well mapped */
	@Test
	public void test_save() throws PersistenceException {
		Session session = createSessionInTransaction();
		DatacenterHB datacenter = TestEntityGenerationUtils
				.createDatacenter("datacenter1");
		
		MachineLoadRuleHB rule = new MachineLoadRuleHB(datacenter, null, null, 90, 90);

		MachineLoadRuleDAO dao = TestDAOHelper
				.createMachineLoadRuleDAO(session);
		SessionUtils.saveAndFlush(session, datacenter);
		dao.makePersistent(rule);
		session.flush();

		Assert.assertTrue(SessionUtils.entityExists(session, MachineLoadRuleHB.class, rule.getId()));
	}
}
