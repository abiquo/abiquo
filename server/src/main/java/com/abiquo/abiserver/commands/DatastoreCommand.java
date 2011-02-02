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

package com.abiquo.abiserver.commands;

import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.DatastoreHB;
import com.abiquo.abiserver.exception.DatastoreCommandException;
import com.abiquo.abiserver.pojo.authentication.UserSession;

/**
 * This command collects all Datastore actions.
 * 
 * @author jdevesa@abiquo.com
 */
public interface DatastoreCommand
{
    /**
     * Persist a new {@link DatastoreHB} entity.
     * 
     * @param userSession user session object
     * @param newDatastore datastore to be created.
     * @param physicalMachineId physicalmachine where the datastore is placed.
     * @return a recently created object.
     */
    public DatastoreHB createDatastore(UserSession userSession, DatastoreHB newDatastore,
        Integer physicalMachineId) throws DatastoreCommandException;

    /**
     * Edit a created {@link DatastoreHB}. The edit parameters are only the 'defaultDatastore' and
     * the 'directory' value.
     * 
     * @param userSession user session object
     * @param datastore new datastore values.
     * @return the modified object.
     */
    public DatastoreHB editDatastore(UserSession userSession, DatastoreHB datastore)
        throws DatastoreCommandException;

}
