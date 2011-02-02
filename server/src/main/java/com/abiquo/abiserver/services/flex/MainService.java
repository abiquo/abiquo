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
import com.abiquo.abiserver.commands.MainCommand;
import com.abiquo.abiserver.commands.impl.MainCommandImpl;
import com.abiquo.abiserver.pojo.authentication.UserSession;
import com.abiquo.abiserver.pojo.result.BasicResult;

/**
 * This class defines all services related to the main application
 * 
 * @author Oliver
 */

public class MainService
{

    private final MainCommand mainCommand;

    public MainService()
    {
        mainCommand = new MainCommandImpl();
    }

    protected MainCommand proxyCommand(UserSession userSession)
    {
        return BusinessDelegateProxy.getInstance(userSession, mainCommand, MainCommand.class);
    }

    /**
     * Collects all the common information that the application needs to load
     * 
     * @param session
     * @return
     */
    public BasicResult getCommonInformation(UserSession session)
    {

        MainCommand command = proxyCommand(session);

        return command.getCommonInformation(session);
    }
}
