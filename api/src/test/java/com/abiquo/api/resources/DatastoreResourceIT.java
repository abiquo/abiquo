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

package com.abiquo.api.resources;

import static com.abiquo.api.common.Assert.assertError;
import static com.abiquo.api.common.UriTestResolver.resolveDatastoreURI;
import static org.testng.Assert.assertEquals;

import org.apache.wink.client.ClientResponse;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.model.transport.error.ErrorsDto;
import com.abiquo.server.core.infrastructure.Datastore;
import com.abiquo.server.core.infrastructure.DatastoreDto;
import com.abiquo.server.core.infrastructure.Machine;

public class DatastoreResourceIT extends AbstractJpaGeneratorIT
{

    @Test
    public void getDatastore()
    {
        Datastore ds = datastoreGenerator.createUniqueInstance();
        Machine machine = ds.getMachines().get(0);
        setup(machine.getDatacenter(), machine.getRack(), machine, ds);

        String uri =
            resolveDatastoreURI(machine.getDatacenter().getId(), machine.getRack().getId(),
                machine.getId(), ds.getId());

        ClientResponse response = get(uri);

        DatastoreDto dto = response.getEntity(DatastoreDto.class);
        Assert.assertNotNull(dto);
        Assert.assertEquals(dto.getEditLink().getHref(), uri);
    }

    @Test
    public void updateDatastore()
    {
        Datastore ds = datastoreGenerator.createUniqueInstance();
        Machine machine = ds.getMachines().get(0);
        setup(machine.getDatacenter(), machine.getRack(), machine, ds);

        String uri =
            resolveDatastoreURI(machine.getDatacenter().getId(), machine.getRack().getId(),
                machine.getId(), ds.getId());

        ClientResponse response = get(uri);
        DatastoreDto dto = response.getEntity(DatastoreDto.class);

        dto.setName("updatedDatastoreName");

        response = put(uri, dto);

        assertEquals(response.getEntity(DatastoreDto.class).getName(), dto.getName());
    }

    @Test
    public void updateDatastoreWithDuplicatedName()
    {
        Datastore ds = datastoreGenerator.createUniqueInstance();
        Machine machine = ds.getMachines().get(0);
        Datastore ds2 = datastoreGenerator.createInstance(machine);
        setup(machine.getDatacenter(), machine.getRack(), machine, ds, ds2);

        String uri =
            resolveDatastoreURI(machine.getDatacenter().getId(), machine.getRack().getId(),
                machine.getId(), ds.getId());
        String uri2 =
            resolveDatastoreURI(machine.getDatacenter().getId(), machine.getRack().getId(),
                machine.getId(), ds2.getId());

        ClientResponse response = get(uri);
        DatastoreDto dto1 = response.getEntity(DatastoreDto.class);

        response = get(uri2);
        DatastoreDto dto2 = response.getEntity(DatastoreDto.class);

        dto2.setName(dto1.getName());

        response = put(uri2, dto2);

        assertError(response.getEntity(ErrorsDto.class),
            APIError.DATASTORE_DUPLICATED_NAME.getCode());
    }

    @Test
    public void updateDatastoreWithDuplicatedDirectory()
    {
        Datastore ds = datastoreGenerator.createUniqueInstance();
        Machine machine = ds.getMachines().get(0);
        Datastore ds2 = datastoreGenerator.createInstance(machine);
        setup(machine.getDatacenter(), machine.getRack(), machine, ds, ds2);

        String uri =
            resolveDatastoreURI(machine.getDatacenter().getId(), machine.getRack().getId(),
                machine.getId(), ds.getId());
        String uri2 =
            resolveDatastoreURI(machine.getDatacenter().getId(), machine.getRack().getId(),
                machine.getId(), ds2.getId());

        ClientResponse response = get(uri);
        DatastoreDto dto1 = response.getEntity(DatastoreDto.class);

        response = get(uri2);
        DatastoreDto dto2 = response.getEntity(DatastoreDto.class);

        dto2.setDirectory(dto1.getDirectory());

        response = put(uri2, dto2);

        assertError(response.getEntity(ErrorsDto.class),
            APIError.DATASTORE_DUPLICATED_DIRECTORY.getCode());
    }

    @Test
    public void updateNonExistentDatastore()
    {
        Datastore ds = datastoreGenerator.createUniqueInstance();
        Machine machine = ds.getMachines().get(0);
        setup(machine.getDatacenter(), machine.getRack(), machine, ds);

        String uri =
            resolveDatastoreURI(machine.getDatacenter().getId(), machine.getRack().getId(),
                machine.getId(), ds.getId());
        ClientResponse response = get(uri);
        DatastoreDto dto1 = response.getEntity(DatastoreDto.class);

        response =
            put(resolveDatastoreURI(machine.getDatacenter().getId(), machine.getRack().getId(),
                machine.getId(), ds.getId() + 1234), dto1);

        assertEquals(response.getStatusCode(), 404);
        assertError(response.getEntity(ErrorsDto.class),
            APIError.DATASTORE_NOT_ASSIGNED_TO_MACHINE.getCode());
    }
}
