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

import java.util.HashMap;
import java.util.List;

import com.abiquo.abiserver.business.hibernate.pojohb.metering.MeterHB;
import com.abiquo.abiserver.exception.MeterCommandException;
import com.abiquo.abiserver.pojo.authentication.UserSession;

/**
 * All the business logic for metering section in AbiCloud
 * 
 * @author jdevesa@abiquo.com
 */
public interface MeterCommand
{
    /**
     * Get the list of the meters matching with the filters specified
     * 
     * @param user user that queries the meter list
     * @param filters HashMap containing relation "column"-"restriction". Correct 'column' values
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
     *            "component"<br>
     *            "firstdate" <br>
     *            "lastdate" <br>
     *            "numrows" <br>
     *            "datefrom" <br>
     *            "dateto" <br>
     * @param numrows the number of results
     * @return list of meters
     */
    List<MeterHB> getMeters(UserSession user, HashMap<String, String> filters, Integer numrows)
        throws MeterCommandException;

    /**
     * Transform the class {@link com.abiquo.tracer.EventType} to a list of Strings
     * 
     * @return List of Strings containing the values of the EventType enumeration
     */
    List<String> getListOfSeverityTypes();

    /**
     * Transform the class {@link com.abiquo.tracer.SeverityType} to a list of Strings
     * 
     * @return List of Strings containing the values of the SeverityType enumeration
     */
    List<String> getListOfEventTypes();
}
