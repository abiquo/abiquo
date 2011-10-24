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
 * 
 */
package com.abiquo.abiserver.persistence.dao.metering;

import java.util.HashMap;
import java.util.List;

import com.abiquo.abiserver.business.hibernate.pojohb.metering.MeterHB;
import com.abiquo.abiserver.business.hibernate.pojohb.user.UserHB;
import com.abiquo.abiserver.exception.PersistenceException;
import com.abiquo.abiserver.persistence.DAO;

/**
 * Specific interface to work with the
 * {@link com.abiquo.abiserver.business.hibernate.pojohb.metering.MeterHB} Exposes all the methods
 * that this entity will need to interact with the data source
 * 
 * @author jdevesa@abiquo.com
 */
public interface MeterDAO extends DAO<MeterHB, Long>
{
    /**
     * Return all the metering entries filtered by some fields (or not)
     * 
     * @param filter HashMap containing relation "column"-"restriction". Correct 'column' values
     *            are:<br>
     *            "datacenter"<br>
     *            "rack" <br>
     *            "physicalmachine" <br>
     *            "storagesystem" <br>
     *            "storagepool" <br>
     *            "volume" <br>
     *            "network" <br>
     *            "subnet" <br>
     *            "enterprise" <br>
     *            "user" <br>
     *            "virtualdatacenter" <br>
     *            "virtualapp" <br>
     *            "virtualmachine" <br>
     *            "severity" <br>
     *            "actionperformed" <br>
     *            "component" <br>
     *            "datefrom" <br>
     *            "dateto" <br>
     * @param performedbyList the list of users we can watch its movements
     * @param numrows set the max rows to return
     * @return List of matching entries if "performedby" is a key. Null otherwise.
     */
    public List<MeterHB> findAllByFilter(HashMap<String, String> filter,
        List<String> performedbyList, Integer numrows, UserHB user) throws PersistenceException;
}
