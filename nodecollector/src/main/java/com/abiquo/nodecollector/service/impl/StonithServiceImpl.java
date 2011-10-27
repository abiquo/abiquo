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

package com.abiquo.nodecollector.service.impl;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.abiquo.nodecollector.service.StonithService;

/**
 * @author eruiz@abiquo.com
 */
@Service("stonithService")
public class StonithServiceImpl implements StonithService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(StonithServiceImpl.class);

    @Override
    public boolean shootTheOtherNodeInTheHead(String host, Integer port, String user,
        String password)
    {
        CommandLine command = buildCommand(host, port, user, password);
        command.addArgument("power").addArgument("off");

        return executeCommand(command);
    }

    @Override
    public boolean isStonithUp(String host, Integer port, String user, String password)
    {
        CommandLine command = buildCommand(host, port, user, password);
        command.addArgument("status", true);

        return executeCommand(command);
    }

    private boolean executeCommand(CommandLine command)
    {
        try
        {
            LOGGER.debug(String.format("Executing '%s'", command.toString()));

            DefaultExecutor executor = new DefaultExecutor();

            return (executor.execute(command) == 0);
        }
        catch (Exception e)
        {
            LOGGER.error(String.format("While executing '%s'", command.toString()), e);
            return false;
        }
    }

    private CommandLine buildCommand(String ip, Integer port, String user, String password)
    {
        CommandLine command = new CommandLine("ipmitool");
        command.addArgument("-H").addArgument(ip, true);
        command.addArgument("-U").addArgument(user, true);
        command.addArgument("-P").addArgument(password, true);
        command.addArgument("chassis", true);

        if (port != null)
        {
            command.addArgument("-p").addArgument(port.toString(), true);
        }

        return command;
    }
}
