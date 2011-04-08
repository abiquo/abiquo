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

import static com.abiquo.api.common.UriTestResolver.resolveDatastoresURI;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.ws.rs.core.MediaType;

import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.Resource;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.abiquo.api.common.Assert;
import com.abiquo.api.exceptions.APIError;
import com.abiquo.model.transport.error.ErrorsDto;
import com.abiquo.server.core.infrastructure.DatastoreDto;
import com.abiquo.server.core.infrastructure.Machine;

public class DatastoresResourceIT extends AbstractJpaGeneratorIT
{
    @DataProvider(name = "createDatastoreProvider")
    @SuppressWarnings("unused")
    private Iterator<Object[]> createDatastoreProvider()
    {
        Collection<Object[]> provider = new ArrayList<Object[]>();

        DatastoreDto dto1 = createDatastoreDto("name", "uuid", "directory", true, false);
        provider.add(new Object[] {dto1});

        DatastoreDto dto2 = createDatastoreDto("dsname2", "dsuuid2", "dsdirectory2", false, false);
        provider.add(new Object[] {dto2});

        return provider.iterator();
    }

    @Test(dataProvider = "createDatastoreProvider")
    public void postDatastore(DatastoreDto dto)
    {
        ClientResponse response = createDatastore(dto);

        assertEquals(response.getStatusCode(), 201);

        DatastoreDto entityPost = response.getEntity(DatastoreDto.class);
        assertNotNull(entityPost);
        assertEquals(entityPost.getName(), dto.getName());
        assertEquals(entityPost.isEnabled(), dto.isEnabled());
    }

    @DataProvider(name = "createDatastoreProviderWithDuplicatedField")
    @SuppressWarnings("unused")
    private Iterator<Object[]> createDatastoreProviderWithDuplicatedField()
    {
        Collection<Object[]> provider = new ArrayList<Object[]>();

        DatastoreDto dto1 = createDatastoreDto("dsname", "dsuuid", "dsdirectory", true, false);
        DatastoreDto dto2 = createDatastoreDto("dsname", "dsuuid2", "dsdirectory2", false, false);
        provider.add(new Object[] {dto1, dto2, APIError.DATASTORE_DUPLICATED_NAME.getCode()});

        DatastoreDto dto5 = createDatastoreDto("dsname", "dsuuid", "dsdirectory", true, false);
        DatastoreDto dto6 = createDatastoreDto("dsname2", "dsuuid2", "dsdirectory", false, false);
        provider.add(new Object[] {dto5, dto6, APIError.DATASTORE_DUPLICATED_DIRECTORY.getCode()});

        return provider.iterator();
    }

    @Test(dataProvider = "createDatastoreProviderWithDuplicatedField")
    public void createDatastoreWithDuplicatedField(DatastoreDto dto1, DatastoreDto dto2,
        String errorCode)
    {
        createDatastore(dto1);
        ClientResponse response = createDatastore(dto2);

        assertEquals(response.getStatusCode(), 400);

        ErrorsDto errors = response.getEntity(ErrorsDto.class);
        Assert.assertError(errors, errorCode);
    }

    @Test
    public void getMachineDatastores() throws Exception
    {
        ClientResponse response =
            createDatastore(createDatastoreDto("dsnameM", "dsuuidM", "dsdirectoryM", true, false));

        assertEquals(response.getStatusCode(), 201);
    }

    private ClientResponse createDatastore(DatastoreDto dto)
    {
        Machine machine = machineGenerator.createMachineIntoRack();
        setup(machine.getDatacenter(), machine.getRack(), machine);

        String uri =
            resolveDatastoresURI(machine.getDatacenter().getId(), machine.getRack().getId(),
                machine.getId());

        return createDatastore(uri, dto);
    }

    private ClientResponse createDatastore(String datastoresUri, DatastoreDto dto)
    {
        Resource resource = client.resource(datastoresUri).accept(MediaType.APPLICATION_XML);
        return resource.contentType(MediaType.APPLICATION_XML).post(dto);
    }

    private DatastoreDto createDatastoreDto(String name, String rootPath, String directory,
        boolean enabled, boolean shared)
    {
        DatastoreDto dto = new DatastoreDto();
        dto.setName(name);
        dto.setRootPath(rootPath);
        dto.setDirectory(directory);
        dto.setShared(shared);
        dto.setEnabled(enabled);

        return dto;
    }
}
