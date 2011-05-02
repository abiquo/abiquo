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
package com.abiquo.abiserver.commands.stub;

import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.abiserver.pojo.result.ListRequest;
import com.abiquo.abiserver.pojo.user.Enterprise;
import com.abiquo.abiserver.pojo.user.EnterpriseListResult;
import com.abiquo.server.core.enterprise.UserDto;

public interface EnterprisesResourceStub
{
    public DataResult<Enterprise> createEnterprise(Enterprise enterprise);

    public DataResult<Enterprise> editEnterprise(Enterprise enterprise);

    public BasicResult deleteEnterprise(Integer enterpriseId);

    public DataResult<EnterpriseListResult> getEnterprises(ListRequest enterpriseListOptions);

    public DataResult<Enterprise> getEnterprise(Integer enterpriseId);

    /**
     * This function returns the currently logged user at the time call. Since this method in the
     * API Rest is secure user must be logged with valid credentials to reach it. Then we can keep
     * going with the old login method. <br />
     * Since this method is an ad-hoc implementation should be deprecated until we get rid of the
     * <b>server</b>.
     * 
     * @param user username.
     * @param password password.
     * @return {@link UserDto} of the user currently authenticated.
     */
    public DataResult<UserDto> getUserByName(String user, String password);
}
