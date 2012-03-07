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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.abiserver.business.BusinessDelegateProxy;
import com.abiquo.abiserver.commands.ConfigurationCommand;
import com.abiquo.abiserver.commands.impl.ConfigurationCommandImpl;
import com.abiquo.abiserver.pojo.authentication.UserSession;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.heartbeat.shared.dto.RegisterDTO;

public class ConfigurationService
{

    private static final Logger logger = LoggerFactory.getLogger(ConfigurationService.class);

    private final ConfigurationCommand configurationCommand;

    public ConfigurationService()
    {
        configurationCommand = new ConfigurationCommandImpl();
    }

    protected ConfigurationCommand proxyCommand(final UserSession userSession)
    {
        return BusinessDelegateProxy.getInstance(userSession, configurationCommand,
            ConfigurationCommand.class);
    }

    // //////////////////////////////////////////////////////////////
    // REGISTRATION

    /**
     * Sets the Registration status as "Do not want to register"
     * 
     * @return a BasicResult object, with success = true, if the registration status was set
     *         successfully, or false otherwise
     */
    public BasicResult setRegistrationStatusNo(final UserSession userSession)
    {
        ConfigurationCommand command = proxyCommand(userSession);
        return command.setRegistrationStatusNo();
    }

    /**
     * Sets the Registration status as "Remind later"
     * 
     * @return a BasicResult object, with success = true, if the registration status was set
     *         successfully, or false otherwise
     */
    public BasicResult setRegistrationStatusLater(final UserSession userSession)
    {
        ConfigurationCommand command = proxyCommand(userSession);
        return command.setRegistrationStatusLater();
    }

    /**
     * Asks if client must show a window with the registration information
     * 
     * @return a DataResult<Boolean> object, with the asked information
     */
    public BasicResult mustShowRegistrationReminder(final UserSession userSession)
    {
        ConfigurationCommand command = proxyCommand(userSession);
        return command.mustShowRegistrationReminder();
    }

    /**
     * Method to retrieve the current stored Registration information
     * 
     * @return a DataResult<RegisterDTO> object, with the current Registration information
     */
    public BasicResult getRegistrationData(final UserSession userSession)
    {
        ConfigurationCommand command = proxyCommand(userSession);
        return command.getRegistrationData();
    }

    /**
     * Sets or updates the current Registration information
     * 
     * @param registrationData RegistrationDTO containing the Registration information.
     * @return a BasicResult object, with success=true if the registration information was set
     *         successfully, or false otherwise
     */
    public DataResult<RegisterDTO> setRegistrationData(final UserSession userSession,
        final RegisterDTO registrationData)
    {
        ConfigurationCommand command = proxyCommand(userSession);
        return command.setRegistrationData(registrationData);
    }

    // //////////////////////////////////////////////////////////////
    // HEARTBEAT

    /**
     * Asks if client must show a window with the Heartbeat information
     * 
     * @return a DataResult<Boolean> object, with the asked information
     */
    public BasicResult mustShowHeartbeatReminder(final UserSession userSession)
    {
        ConfigurationCommand command = proxyCommand(userSession);
        return command.mustShowHeartbeatReminder();
    }

    /**
     * Checks if the Heartbeat is enabled in this system
     * 
     * @return a DataResult<Boolean> object, with data = true if Hearbeat is enabled, or false
     *         otherwise
     */
    public BasicResult isHeartbeatEnabled(final UserSession userSession)
    {
        ConfigurationCommand command = proxyCommand(userSession);
        return command.isHeartbeatEnabled();
    }

    /**
     * Sets the Heartbeat status as "Do no want to activate"
     * 
     * @return a BasicResult object, with success = true, if the Heartbeat status was set
     *         successfully, or false otherwise
     */
    public BasicResult setHeartbeatStatusNo(final UserSession userSession)
    {
        ConfigurationCommand command = proxyCommand(userSession);
        return command.setHeartbeatStatusNo();
    }

    /**
     * Sets the Heartbeat status as "Remind later"
     * 
     * @return a BasicResult object, with success = true, if the Heartbeat status was set
     *         successfully, or false otherwise
     */
    public BasicResult setHeartbeatStatusLater(final UserSession userSession)
    {
        ConfigurationCommand command = proxyCommand(userSession);
        return command.setHeartbeatStatusLater();
    }

    /**
     * Sets the Heartbeat status to "Enabled"
     * 
     * @param userSession
     * @return a BasicResult object, with success = true, if the Heartbeat was enabled successfully
     */
    public BasicResult enableHeartbeat(final UserSession userSession)
    {
        ConfigurationCommand command = proxyCommand(userSession);
        return command.enableHeartbeat();
    }

    /**
     * Sets the Heartbeat status to "Disabled"
     * 
     * @param userSession
     * @return a BasicResult object, with success = true, if the Heartbeat was disabled successfully
     */
    public BasicResult disableHeartbeat(final UserSession userSession)
    {
        ConfigurationCommand command = proxyCommand(userSession);
        return command.disableHeartbeat();
    }

    /**
     * Retrieves the last Hearbeat information available
     * 
     * @return a DataResult<HeartbeatDTO> object containing the last Heartbeat information available
     *         for this system
     */
    public BasicResult getLastHeartbeat(final UserSession userSession)
    {
        ConfigurationCommand command = proxyCommand(userSession);
        return command.getLastHeartbeat();
    }

    /**
     * Retrieves a list containing the last Hearbeat entries
     * 
     * @param rows Number of entries to return
     * @return a DataResult<ArrayList<HeartbeatDTO>> object containing the last Heartbeat entries
     *         for this system
     */
    public BasicResult getLastHeartbeat(final UserSession userSession, final Integer rows)
    {
        ConfigurationCommand command = proxyCommand(userSession);
        return command.getLastHeartbeat(rows);
    }
}
