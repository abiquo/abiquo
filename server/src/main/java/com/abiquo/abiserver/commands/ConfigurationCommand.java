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

import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.heartbeat.shared.dto.RegisterDTO;

public interface ConfigurationCommand
{

    /**
     * Sets the Registration status as "Do not want to register"
     * 
     * @return a BasicResult object, with success = true, if the registration status was set
     *         successfully, or false otherwise
     */
    public abstract BasicResult setRegistrationStatusNo();

    /**
     * Sets the Registration status as "Remind later"
     * 
     * @return a BasicResult object, with success = true, if the registration status was set
     *         successfully, or false otherwise
     */
    public abstract BasicResult setRegistrationStatusLater();

    /**
     * Asks if client must show a window with the registration information
     * 
     * @return a DataResult<Boolean> object, with the asked information
     */
    public abstract BasicResult mustShowRegistrationReminder();

    /**
     * Method to retrieve the current stored Registration information
     * 
     * @return a DataResult<RegisterDTO> object, with the current Registration information
     */
    public abstract BasicResult getRegistrationData();

    /**
     * Sets or updates the current Registration information
     * 
     * @param registrationData RegistrationDTO containing the Registration information.
     * @return a BasicResult object, with success=true if the registration information was set
     *         successfully, or false otherwise
     */
    public abstract DataResult<RegisterDTO> setRegistrationData(RegisterDTO registrationData);

    /**
     * Asks if client must show a window with the Heartbeat information
     * 
     * @return a DataResult<Boolean> object, with the asked information
     */
    public abstract BasicResult mustShowHeartbeatReminder();

    /**
     * Checks if the Heartbeat is enabled in this system
     * 
     * @return a DataResult<Boolean> object, with data = true if Hearbeat is enabled, or false
     *         otherwise
     */
    public abstract BasicResult isHeartbeatEnabled();

    /**
     * Sets the Heartbeat status as "Do no want to activate"
     * 
     * @return a BasicResult object, with success = true, if the Heartbeat status was set
     *         successfully, or false otherwise
     */
    public abstract BasicResult setHeartbeatStatusNo();

    /**
     * Sets the Heartbeat status as "Remind later"
     * 
     * @return a BasicResult object, with success = true, if the Heartbeat status was set
     *         successfully, or false otherwise
     */
    public abstract BasicResult setHeartbeatStatusLater();

    /**
     * Sets the Heartbeat status to "Enabled"
     * 
     * @param userSession
     * @return a BasicResult object, with success = true, if the Heartbeat was enabled successfully
     */
    public abstract BasicResult enableHeartbeat();

    /**
     * Sets the Heartbeat status to "Disabled"
     * 
     * @param userSession
     * @return a BasicResult object, with success = true, if the Heartbeat was disabled successfully
     */
    public abstract BasicResult disableHeartbeat();

    /**
     * Retrieves the last Hearbeat information available
     * 
     * @return a DataResult<HeartbeatDTO> object containing the last Heartbeat information available
     *         for this system
     */
    public abstract BasicResult getLastHeartbeat();

    /**
     * Retrieves a list containing the last Hearbeat entries
     * 
     * @param rows Number of entries to return
     * @return a DataResult<ArrayList<HeartbeatDTO>> object containing the last Heartbeat entries
     *         for this system
     */
    public abstract BasicResult getLastHeartbeat(Integer rows);

}
