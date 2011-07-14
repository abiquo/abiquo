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

/**
 * abiCloud  community version
 * cloud management application for hybrid clouds
 * Copyright (C) 2008-2010 - Soluciones Grid SL
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

/**
 * 
 */
package com.abiquo.nodecollector.resource.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * The test suite group all the end-to-end test classes to execute. This test suite needs all the
 * Hypervisors running in the development or QA environment and the IP address must be the same than
 * {@link PluginCollectorTesteable} interface values. If you don't need to execute one of the
 * tests because the environment is not set, just comment the "*Test.class" into the
 * '@Suite.SuiteClasses' annotation corresponding with the Hypervisor that's not ready.
 * 
 * @author jdevesa@abiquo.com
 */
@RunWith(Suite.class)
@Suite.SuiteClasses( {
   // HyperVCollectorTest.class, 
   // XenServerCollectorTest.class,
   // ESXiCollectorTest.class
   // XenCollectorTest.class
   // KVMCollectorTest.class,
  // VirtualBoxCollectorTest.class
})
public class NodeResourceSuiteIT
{

}
