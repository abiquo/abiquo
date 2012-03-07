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

package com.abiquo.abiserver.commands.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.abiserver.commands.BasicCommand;
import com.abiquo.abiserver.commands.ConfigurationCommand;
import com.abiquo.abiserver.listener.ProxyContextLoaderListener;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.heartbeat.client.services.HeartbeatClient;
import com.abiquo.heartbeat.client.services.RegistrationClient;
import com.abiquo.heartbeat.client.services.impl.HeartbeatClientImpl;
import com.abiquo.heartbeat.client.services.impl.RegistrationClientImpl;
import com.abiquo.heartbeat.shared.dto.HeartbeatDTO;
import com.abiquo.heartbeat.shared.dto.RegisterDTO;
import com.abiquo.heartbeat.shared.dto.RegisterResponse;
import com.abiquo.heartbeat.shared.exceptions.HeartbeatException;

public class ConfigurationCommandImpl extends BasicCommand implements ConfigurationCommand
{
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationCommandImpl.class);

    private final HeartbeatClient heartbeat;

    private final RegistrationClient register;

    public ConfigurationCommandImpl()
    {
        heartbeat = ProxyContextLoaderListener.getCtx().getBean(HeartbeatClientImpl.class);
        register = ProxyContextLoaderListener.getCtx().getBean(RegistrationClientImpl.class);
    }

    // //////////////////////////////////////////////////////////////
    // REGISTRATION

    /*
     * (non-Javadoc)
     * @see com.abiquo.abiserver.commands.ConfigurationComman#setRegistrationStatusNo()
     */
    @Override
    public BasicResult setRegistrationStatusNo()
    {
        register.setStatusNo();

        BasicResult basicResult = new BasicResult();
        basicResult.setSuccess(true);

        return basicResult;
    }

    /*
     * (non-Javadoc)
     * @see com.abiquo.abiserver.commands.ConfigurationComman#setRegistrationStatusLater()
     */
    @Override
    public BasicResult setRegistrationStatusLater()
    {
        register.setStatusLater();

        BasicResult basicResult = new BasicResult();
        basicResult.setSuccess(true);

        return basicResult;
    }

    /*
     * (non-Javadoc)
     * @see com.abiquo.abiserver.commands.ConfigurationComman#mustShowRegistrationReminder()
     */
    @Override
    public BasicResult mustShowRegistrationReminder()
    {
        boolean showRegister = register.isRegistrationReminderTimeout();

        DataResult<Boolean> dataResult = new DataResult<Boolean>();

        dataResult.setData(showRegister);
        dataResult.setSuccess(true);

        return dataResult;
    }

    /*
     * (non-Javadoc)
     * @see com.abiquo.abiserver.commands.ConfigurationComman#getRegistrationData()
     */
    @Override
    public BasicResult getRegistrationData()
    {
        DataResult<RegisterDTO> dataResult = new DataResult<RegisterDTO>();

        RegisterDTO data = null;

        try
        {
            data = register.getData();
        }
        catch (Exception e)
        {
            logger.warn("error retrieving registration data", e);
        }

        dataResult.setData(data);
        dataResult.setSuccess(true);

        return dataResult;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.ConfigurationComman#setRegistrationData(com.abiquo.heartbeat
     * .shared.dto.RegisterDTO)
     */
    @Override
    public DataResult<RegisterDTO> setRegistrationData(final RegisterDTO registrationData)
    {
        DataResult<RegisterDTO> dataResult = new DataResult<RegisterDTO>();

        try
        {
            RegisterResponse registerResponse = register.send(registrationData);
            dataResult.setData(registerResponse.getRegisterDTO());
            dataResult.setSuccess(true);
        }
        catch (Exception e)
        {
            dataResult.setSuccess(false);
            dataResult
                .setMessage("Connection refused: unabled to connect to the configuration server");
            logger.error("error sending the registration data", e);
        }

        return dataResult;
    }

    // //////////////////////////////////////////////////////////////
    // HEARTBEAT

    /*
     * (non-Javadoc)
     * @see com.abiquo.abiserver.commands.ConfigurationComman#mustShowHeartbeatReminder()
     */
    @Override
    public BasicResult mustShowHeartbeatReminder()
    {
        DataResult<Boolean> dataResult = new DataResult<Boolean>();

        boolean showHeartbeat = heartbeat.isConfigurationTimeout();
        dataResult.setData(showHeartbeat);
        dataResult.setSuccess(true);

        return dataResult;
    }

    /*
     * (non-Javadoc)
     * @see com.abiquo.abiserver.commands.ConfigurationComman#isHeartbeatEnabled()
     */
    @Override
    public BasicResult isHeartbeatEnabled()
    {
        DataResult<Boolean> dataResult = new DataResult<Boolean>();

        dataResult.setData(heartbeat.isConfigured());
        dataResult.setSuccess(true);

        return dataResult;
    }

    /*
     * (non-Javadoc)
     * @see com.abiquo.abiserver.commands.ConfigurationComman#setHeartbeatStatusNo()
     */
    @Override
    public BasicResult setHeartbeatStatusNo()
    {
        BasicResult basicResult = new BasicResult();

        heartbeat.setStatusNo();
        basicResult.setSuccess(true);

        return basicResult;
    }

    /*
     * (non-Javadoc)
     * @see com.abiquo.abiserver.commands.ConfigurationComman#setHeartbeatStatusLater()
     */
    @Override
    public BasicResult setHeartbeatStatusLater()
    {
        BasicResult basicResult = new BasicResult();

        heartbeat.setStatusLater();
        basicResult.setSuccess(true);

        return basicResult;
    }

    /*
     * (non-Javadoc)
     * @see com.abiquo.abiserver.commands.ConfigurationComman#enableHeartbeat()
     */
    @Override
    public BasicResult enableHeartbeat()
    {
        BasicResult basicResult = new BasicResult();

        try
        {
            heartbeat.setStatusYes();
            basicResult = getLastHeartbeat();
        }
        catch (Exception e)
        {
            basicResult.setSuccess(false);
            basicResult
                .setMessage("Connection refused: unabled to connect to the configuration server");
            logger.error("error activating the heartbeat", e);
        }

        return basicResult;
    }

    /*
     * (non-Javadoc)
     * @see com.abiquo.abiserver.commands.ConfigurationComman#disableHeartbeat()
     */
    @Override
    public BasicResult disableHeartbeat()
    {
        BasicResult basicResult = new BasicResult();

        try
        {
            heartbeat.setStatusNo();
            heartbeat.stopHeartbeat();
            basicResult.setSuccess(true);
        }
        catch (Exception e)
        {
            basicResult.setSuccess(false);
            basicResult
                .setMessage("Connection refused: unabled to connect to the configuration server");
            logger.error("error setting the heartbeating data", e);
        }

        return basicResult;
    }

    /*
     * (non-Javadoc)
     * @see com.abiquo.abiserver.commands.ConfigurationComman#getLastHeartbeat()
     */
    @Override
    public BasicResult getLastHeartbeat()
    {
        DataResult<HeartbeatDTO> basicResult = new DataResult<HeartbeatDTO>();

        try
        {
            if (!heartbeat.isConfigured())
            {
                basicResult.setSuccess(false);
                basicResult.setMessage("Connection refused: Heartbeat is not configured");
                logger.error("error getting the heartbeating data");
            }
            else
            {
                heartbeat.send();
                HeartbeatDTO data = heartbeat.getLast();

                basicResult.setSuccess(true);
                basicResult.setData(data);
            }
        }
        catch (HeartbeatException e)
        {
            basicResult.setSuccess(false);
            basicResult
                .setMessage("Connection refused: unabled to connect to the configuration server");
            logger.error("error sending the heartbeating data", "Heartbeat not enabled");
        }

        return basicResult;
    }

    /*
     * (non-Javadoc)
     * @see com.abiquo.abiserver.commands.ConfigurationComman#getLastHeartbeat(java.lang.Integer)
     */
    @Override
    public BasicResult getLastHeartbeat(final Integer rows)
    {
        DataResult<List<HeartbeatDTO>> dataResult = new DataResult<List<HeartbeatDTO>>();

        try
        {
            if (!heartbeat.isConfigured())
            {
                dataResult.setSuccess(false);
                dataResult.setMessage("Connection refused: Heartbeat is not configured");
                logger.error("error getting the heartbeating data", "Heartbeat not enabled");
            }
            else
            {
                List<HeartbeatDTO> data = heartbeat.getLast(rows);
                if (data != null && !data.isEmpty())
                {
                    dataResult.setData(data);
                    dataResult.setSuccess(true);
                }
            }
        }
        catch (Exception e)
        {
            dataResult.setSuccess(false);
            dataResult
                .setMessage("Connection refused: unabled to connect to the configuration server");
            logger.error("error getting the heartbeating data", e);
        }

        return dataResult;
    }
}
