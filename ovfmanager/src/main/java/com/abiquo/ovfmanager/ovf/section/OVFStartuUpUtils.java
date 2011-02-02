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

package com.abiquo.ovfmanager.ovf.section;

import org.dmtf.schemas.ovf.envelope._1.StartupSectionType;
import org.dmtf.schemas.ovf.envelope._1.StartupSectionType.Item;

import com.abiquo.ovfmanager.ovf.exceptions.RequiredAttributeException;

public class OVFStartuUpUtils
{

    public static Item createStartUpItem(String virtualSystemId, Integer order,
        Boolean waitForHost, String startAction, String stopAction, Integer startDelay,
        Integer stopDelay) throws RequiredAttributeException
    {
        Item ite = new Item();

        if (virtualSystemId == null || order == null)
        {
            throw new RequiredAttributeException("Id or oreder on StartUpSection.Item");

        }

        ite.setId(virtualSystemId);
        ite.setOrder(order);

        ite.setWaitingForGuest(waitForHost);
        ite.setStartAction(startAction);
        ite.setStopAction(stopAction);
        ite.setStartDelay(startDelay);
        ite.setStopDelay(stopDelay);

        return ite;
    }

    public static void addStartUpItem(StartupSectionType susection, Item startItem)
    {
        // TODO check id already exist exception
        // TODO check order is valid

        susection.getItem().add(startItem);
    }

}
