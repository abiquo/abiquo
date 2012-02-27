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

package com.abiquo.api.resources;

import static com.abiquo.api.common.Assert.assertErrors;
import static com.abiquo.api.common.UriTestResolver.resolveEnterprisesURI;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.Resource;
import org.apache.wink.common.internal.utils.UriHelper;
import org.testng.annotations.Test;

import com.abiquo.api.common.Assert;
import com.abiquo.api.exceptions.APIError;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.EnterpriseDto;
import com.abiquo.server.core.enterprise.EnterprisesDto;
import com.abiquo.server.core.enterprise.Privilege;
import com.abiquo.server.core.enterprise.Role;
import com.abiquo.server.core.enterprise.User;
import com.abiquo.server.core.pricing.PricingTemplate;

public class EnterprisesResourceIT extends AbstractJpaGeneratorIT
{

    private String enterprisesURI = resolveEnterprisesURI();

    @Test
    public void getEnterpriseList()
    {
        Enterprise e1 = enterpriseGenerator.createUniqueInstance();
        Enterprise e2 = enterpriseGenerator.createUniqueInstance();

        Role r1 = roleGenerator.createInstanceSysAdmin();
        Role r2 = roleGenerator.createInstanceEnterpriseAdmin();

        User u1 = userGenerator.createInstance(e1, r1, "foo");
        User u2 = userGenerator.createInstance(e2, r2, "bar");

        List<Object> entitiesToSetup = new ArrayList<Object>();

        entitiesToSetup.add(e1);
        entitiesToSetup.add(e2);

        for (Privilege p : r1.getPrivileges())
        {
            entitiesToSetup.add(p);
        }
        for (Privilege p : r2.getPrivileges())
        {
            entitiesToSetup.add(p);
        }
        entitiesToSetup.add(r1);
        entitiesToSetup.add(r2);
        entitiesToSetup.add(u1);
        entitiesToSetup.add(u2);

        setup(entitiesToSetup.toArray());

        ClientResponse response = get(enterprisesURI, u1.getNick(), "foo", EnterprisesDto.MEDIA_TYPE);
        assertEquals(response.getStatusCode(), 200);

        EnterprisesDto entity = response.getEntity(EnterprisesDto.class);
        Assert.assertSize(entity.getCollection(), 2);

        response = get(enterprisesURI, u2.getNick(), "bar", EnterprisesDto.MEDIA_TYPE);
        assertEquals(response.getStatusCode(), 200);

        entity = response.getEntity(EnterprisesDto.class);
        Assert.assertSize(entity.getCollection(), 1);
    }

    @Test
    public void getEnterpriseListFilteredByName()
    {
        Enterprise e1 = enterpriseGenerator.createInstance("enterprise_foo_enterprise");
        Enterprise e2 = enterpriseGenerator.createInstance("enterprise_bar_enterprise");

        Role r1 = roleGenerator.createInstance();
        User u1 = userGenerator.createInstance(e1, r1, "foo");

        setup(e1, e2, r1, u1);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();
        queryParams.put("filter", new String[] {"bar"});

        String uri = UriHelper.appendQueryParamsToPath(enterprisesURI, queryParams, false);

        ClientResponse response = get(uri, u1.getNick(), "foo", EnterprisesDto.MEDIA_TYPE);
        assertEquals(response.getStatusCode(), 200);

        EnterprisesDto entity = response.getEntity(EnterprisesDto.class);
        Assert.assertSize(entity.getCollection(), 1);
    }

    @Test
    public void getEnterpriseWithPricingTemplate() throws Exception
    {
        Enterprise e1 = enterpriseGenerator.createUniqueInstance();
        Enterprise e2 = enterpriseGenerator.createUniqueInstance();
        Enterprise e3 = enterpriseGenerator.createUniqueInstance();
        Enterprise e4 = enterpriseGenerator.createUniqueInstance();
        Enterprise e5 = enterpriseGenerator.createUniqueInstance();

        PricingTemplate pt = pricingTemplateGenerator.createUniqueInstance();
        PricingTemplate pt1 = pricingTemplateGenerator.createUniqueInstance();

        e1.setPricingTemplate(pt);
        e2.setPricingTemplate(pt1);
        e3.setPricingTemplate(pt);
        e4.setPricingTemplate(pt);
        Role r1 = roleGenerator.createInstanceSysAdmin();
        User u1 = userGenerator.createInstance(e1, r1, "foo");

        List<Object> entitiesToSetup = new ArrayList<Object>();

        entitiesToSetup.add(pt.getCurrency());
        entitiesToSetup.add(pt1.getCurrency());
        entitiesToSetup.add(pt);
        entitiesToSetup.add(pt1);
        entitiesToSetup.add(e1);
        entitiesToSetup.add(e2);
        entitiesToSetup.add(e3);
        entitiesToSetup.add(e4);
        entitiesToSetup.add(e5);

        for (Privilege p : r1.getPrivileges())
        {
            entitiesToSetup.add(p);
        }
        entitiesToSetup.add(r1);
        entitiesToSetup.add(u1);

        setup(entitiesToSetup.toArray());

        // Enterprise associated to the pricing template
        Map<String, String[]> queryParams = new HashMap<String, String[]>();
        queryParams.put("idPricingTemplate", new String[] {Integer.toString(pt.getId())});
        queryParams.put("included", new String[] {"true"});

        String uri = UriHelper.appendQueryParamsToPath(enterprisesURI, queryParams, false);

        ClientResponse response = get(uri, u1.getNick(), "foo", EnterprisesDto.MEDIA_TYPE);
        assertEquals(response.getStatusCode(), Status.OK.getStatusCode());

        EnterprisesDto entity = response.getEntity(EnterprisesDto.class);
        Assert.assertSize(entity.getCollection(), 3);

        // Enterprise not associated to the pricing template
        Map<String, String[]> queryParams2 = new HashMap<String, String[]>();
        queryParams2.put("idPricingTemplate", new String[] {Integer.toString(pt.getId())});
        queryParams2.put("included", new String[] {"false"});

        uri = UriHelper.appendQueryParamsToPath(enterprisesURI, queryParams2, false);

        response = get(uri, u1.getNick(), "foo", EnterprisesDto.MEDIA_TYPE);
        assertEquals(response.getStatusCode(), Status.OK.getStatusCode());
        entity = response.getEntity(EnterprisesDto.class);
        Assert.assertSize(entity.getCollection(), 2);
    }

    @Test
    public void createNewEnterprise() throws Exception
    {
        EnterpriseDto e = validEnterprise();

        ClientResponse response = post(enterprisesURI, e);
        assertEquals(response.getStatusCode(), Status.CREATED.getStatusCode());

        EnterpriseDto entityPost = response.getEntity(EnterpriseDto.class);
        assertNotNull(entityPost);
        assertEquals(e.getName(), entityPost.getName());
        assertEquals(e.getCpuCountHardLimit(), entityPost.getCpuCountHardLimit());
        assertEquals(e.getCpuCountSoftLimit(), entityPost.getCpuCountSoftLimit());
        assertEquals(e.getHdHardLimitInMb(), entityPost.getHdHardLimitInMb());
        assertEquals(e.getHdSoftLimitInMb(), entityPost.getHdSoftLimitInMb());
        assertEquals(e.getRamHardLimitInMb(), entityPost.getRamHardLimitInMb());
        assertEquals(e.getRamSoftLimitInMb(), entityPost.getRamSoftLimitInMb());
    }

    @Test
    public void postWithDuplicatedName()
    {
        EnterpriseDto e = validEnterprise();

        ClientResponse response = post(enterprisesURI, e);
        assertEquals(response.getStatusCode(), Status.CREATED.getStatusCode());

        response = post(enterprisesURI, e);
        assertErrors(response, Status.CONFLICT.getStatusCode(), APIError.ENTERPRISE_DUPLICATED_NAME.getCode());
    }

    @Test
    public void postEnterpriseWithInvalidRepositoryLimits()
    {
        EnterpriseDto e = validEnterprise();
        e.setRepositoryLimits(0, 1);

        ClientResponse response = post(enterprisesURI, e);
        assertEquals(response.getStatusCode(), Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    public void postEnterpriseWithInvalidVlansLimits()
    {
        EnterpriseDto e = validEnterprise();
        e.setVlansLimits(0, 1);

        ClientResponse response = post(enterprisesURI, e);
        assertEquals(response.getStatusCode(), Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    public void postWithInvalidCpuRange()
    {
        EnterpriseDto e = validEnterprise();
        e.setCpuCountSoftLimit(-1);

        ClientResponse response = post(enterprisesURI, e);
        assertEquals(response.getStatusCode(), Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    public void postWithInvalidHdRange()
    {
        EnterpriseDto e = validEnterprise();
        e.setHdHardLimitInMb(-1);

        ClientResponse response = post(enterprisesURI, e);
        assertEquals(response.getStatusCode(), Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    public void postWithInvalidRamRange()
    {
        EnterpriseDto e = validEnterprise();
        e.setRamHardLimitInMb(-1);

        ClientResponse response = post(enterprisesURI, e);
        assertEquals(response.getStatusCode(), Status.BAD_REQUEST.getStatusCode());
    }

    private EnterpriseDto validEnterprise()
    {
        EnterpriseDto e = new EnterpriseDto();

        e.setName("enterprise_4");
        e.setCpuCountSoftLimit(1);
        e.setCpuCountHardLimit(2);
        e.setHdSoftLimitInMb(1);
        e.setHdHardLimitInMb(2);
        e.setRamSoftLimitInMb(1);
        e.setRamHardLimitInMb(2);

        return e;
    }
}
