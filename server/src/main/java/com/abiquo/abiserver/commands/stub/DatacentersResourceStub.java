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

import java.util.ArrayList;

import com.abiquo.abiserver.pojo.infrastructure.DataCenter;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.result.DataResult;

public interface DatacentersResourceStub
{
    public DataResult<DataCenter> createDatacenter(DataCenter datacenter);

    public DataResult<ArrayList<DataCenter>> getDatacenters();

    public DataResult<ArrayList<DataCenter>> getDatacenters(final Integer enterpriseId);

    public DataResult<DataCenter> modifyDatacenter(DataCenter datacenter);

    public DataResult<DataCenter> deleteDatacenter(DataCenter datacenter);

    public DataResult<DataCenter> getDatacenter(Integer datacenterId);

    public BasicResult updateUsedResources(Integer datacenterId);
}
