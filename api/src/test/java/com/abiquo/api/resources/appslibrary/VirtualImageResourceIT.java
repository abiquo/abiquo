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

package com.abiquo.api.resources.appslibrary;

import static com.abiquo.testng.TestConfig.AM_INTEGRATION_TESTS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.api.resources.AbstractJpaGeneratorIT;
import com.abiquo.api.resources.RemoteServicesResource;
import com.abiquo.api.services.InfrastructureService;
import com.abiquo.appliancemanager.client.ApplianceManagerResourceStubImpl;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.RemoteServiceDto;

@Test(groups = {AM_INTEGRATION_TESTS})
public class VirtualImageResourceIT extends AbstractJpaGeneratorIT
{
    private final static String AM_BASE_URI = "http://localhost:"
        + String.valueOf(getEmbededServerPort()) + "/am";

    // to add the am properly
    @Autowired
    private InfrastructureService service;

    private ApplianceManagerResourceStubImpl amclient;

    private RemoteServiceDto amDto()
    {
        RemoteServiceDto am = new RemoteServiceDto();
        am.setType(RemoteServiceType.APPLIANCE_MANAGER);
        am.setUri(AM_BASE_URI);

        return am;
    }

    private Enterprise ent;

    private Datacenter datacenter;

    @BeforeMethod
    public void setUpDatacenterRepository()
    {
        ent = enterpriseGenerator.createUniqueInstance();
        datacenter = datacenterGenerator.createUniqueInstance();

        setup(ent, datacenter);

        service.addRemoteService(amDto(), datacenter.getId());

        amclient = new ApplianceManagerResourceStubImpl(AM_BASE_URI);
    }

    @Test//(enabled=false)
    public void testCreateVirtualImage() throws InterruptedException
    {
        amclient.checkService();
        amclient.createOVFPackageInstance(ent.getId().toString(), "http://cccc.com/nore.ovf");

        Thread.sleep(5000);
    }
}
