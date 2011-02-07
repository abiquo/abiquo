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

  package com.abiquo.server.core.infrastructure;

  import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.server.core.common.DefaultEntityTestBase;
import com.softwarementors.bzngine.entities.test.InstanceTester;

  public class RemoteServiceTest extends DefaultEntityTestBase<RemoteService>
  {

      @Override
      protected InstanceTester<RemoteService> createEntityInstanceGenerator()
      {
          return new RemoteServiceGenerator(getSeed());
      }
      
      @DataProvider(name = "remoteServiceDP")
      public Iterator<Object[]> remoteServiceDataProvider()
      {
    	  Collection<Object[]> dp = new ArrayList<Object[]>() {{
    		  add(new Object[]{"http://10.60.1.30/am", true});
    		  add(new Object[]{"http://10.60.1.30:80/am", true});
    		  add(new Object[]{"http://10.60.1.30:-1/am", false});
    		  add(new Object[]{"http://10.60.1.30:80:80/am", false});
    	  }};
    	  
    	  return dp.iterator();
      }
      
      @Test(dataProvider = "remoteServiceDP")
      public void remoteServiceWithURI(String uri, boolean expected)
      {
    	  DatacenterGenerator datacenterGenerator = new DatacenterGenerator(getSeed());
    	  
    	  Datacenter d = datacenterGenerator.createUniqueInstance();
    	  RemoteService rs = new RemoteService(d, RemoteServiceType.NODE_COLLECTOR, uri, 0);
    	  
    	  Assert.assertEquals(rs.isValid(), expected);
    	  if (expected == false)
    	  {
    		  Assert.assertEquals(rs.getValidationErrors().iterator().next().getMessage(),
    				  "invalid uri: " + uri);
    	  }
      }
  }
