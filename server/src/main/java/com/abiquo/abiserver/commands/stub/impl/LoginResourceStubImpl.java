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
package com.abiquo.abiserver.commands.stub.impl;

import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.handlers.BasicAuthSecurityHandler;

import com.abiquo.abiserver.commands.stub.AbstractAPIStub;
import com.abiquo.abiserver.commands.stub.LoginResourceStub;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.server.core.enterprise.UserDto;

public class LoginResourceStubImpl extends AbstractAPIStub implements LoginResourceStub
{

    /**
     * @see com.abiquo.abiserver.commands.stub.EnterprisesResourceStub#getUserByName(java.lang.String,
     *      java.lang.String)
     */
    @Override
    public DataResult<UserDto> getUserByName(String user, String password,
        BasicAuthSecurityHandler basicAuthSecurityHandler)
    {
        ClientResponse response = get(createLoginLink(), user, password, basicAuthSecurityHandler);

        UserDto userDto = response.getEntity(UserDto.class);

        DataResult<UserDto> data = new DataResult<UserDto>();
        data.setData(userDto);

        return data;
    }

}
