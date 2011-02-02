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

package com.abiquo.abiserver.services.flex;

import com.abiquo.abiserver.business.BusinessDelegateProxy;
import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.DatastoreHB;
import com.abiquo.abiserver.commands.DatastoreCommand;
import com.abiquo.abiserver.commands.impl.DatastoreCommandImpl;
import com.abiquo.abiserver.pojo.authentication.UserSession;
import com.abiquo.abiserver.pojo.infrastructure.Datastore;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.result.DataResult;

/**
 * Service class to communicate with flex interface.
 * 
 * @author jdevesa@abiquo.com
 */
public class DatastoreService
{

    /**
     * Business logic of the server.
     */
    DatastoreCommand datastoreCommand;

    /**
     * Default constructor.
     */
    public DatastoreService()
    {
        datastoreCommand = new DatastoreCommandImpl();
    }

    protected DatastoreCommand proxyCommand(UserSession userSession)
    {
        return BusinessDelegateProxy.getInstance(userSession, datastoreCommand,
            DatastoreCommand.class);
    }

    /**
     * Service to create a new Datastore.
     * 
     * @param userSession UserSession object.
     * @param datastore datastore to create.
     * @return a DataResult containing the Datastore created.
     */
    public BasicResult createDatastore(UserSession userSession, Datastore datastore, Integer idPhysicalMachine)
    {
        DataResult<Datastore> dataResult = new DataResult<Datastore>();
        
        DatastoreCommand command = proxyCommand(userSession);

        DatastoreHB datastoreResult;

        try
        {
            datastoreResult =
                command.createDatastore(userSession, datastore.toPojoHB(), idPhysicalMachine);
            dataResult.setSuccess(Boolean.TRUE);
            dataResult.setData(datastoreResult.toPojo());
        }
        catch (Exception e)
        {
            dataResult.setSuccess(false);
            dataResult.setMessage(e.getMessage());
        }
        
        
        return dataResult;
    }
    
    /**
     * Service to edit a new Datastore.
     * 
     * @param userSession UserSession object.
     * @param datastoreId datastore identifier to modify
     * @param datastore datastore to edit.
     * @return a DataResult containing the Datastore created.
     */
    public BasicResult editDatastore(UserSession userSession, Datastore datastore)
    {
        DataResult<Datastore> dataResult = new DataResult<Datastore>();
        
        DatastoreCommand command = proxyCommand(userSession);
        DatastoreHB datastoreResult;

        try
        {
            datastoreResult = command.editDatastore(userSession, datastore.toPojoHB());
            dataResult.setSuccess(Boolean.TRUE);
            dataResult.setData(datastoreResult.toPojo());
        }
        catch (Exception e)
        {
            dataResult.setSuccess(false);
            dataResult.setMessage(e.getMessage());
        }
        
        
        return dataResult;
    }
    
}
