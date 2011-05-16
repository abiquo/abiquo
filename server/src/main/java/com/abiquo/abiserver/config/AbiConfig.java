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

package com.abiquo.abiserver.config;

import com.abiquo.abiserver.business.hibernate.pojohb.virtualhardware.LimitHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualhardware.ResourceAllocationLimitHB;

/**
 * The Class AbiConfig.
 */
public class AbiConfig
{

    public String getDefaultRepositorySpace()
    {
        return System.getProperty("abiquo.server.remoteSpace.default");
    }

    public String getAbiquoVersion()
    {
        return System.getProperty("abiquo.version");
    }

    public String getAbiquoDistribution()
    {
        return System.getProperty("abiquo.distribution");
    }

    public ResourceAllocationLimitHB getResourceReservationLimits()
    {
        ResourceAllocationLimitHB limits = new ResourceAllocationLimitHB();
        limits.setCpu(createLimit("cpu"));
        limits.setRam(createLimit("ram"));
        limits.setHd(createLimit("hd"));
        limits.setCpu(createLimit("storage"));
        limits.setRam(createLimit("repository"));
        limits.setHd(createLimit("repository"));
        limits.setHd(createLimit("publicIP"));
        return limits;
    }

    public int getVirtualCpuPerCore()
    {
        return Integer.valueOf(System.getProperty("abiquo.server.virtualCpuPerCore", "0"));
    }

    public long getTimeout()
    {
        return Long.valueOf(System.getProperty("abiquo.server.timeout", "0"));
    }

    public int getSessionTimeout()
    {
        return Integer.valueOf(System.getProperty("abiquo.server.sessionTimeout", "0"));
    }

    public String getEventSinkAddress()
    {
        return System.getProperty("abiquo.server.eventSinkAddress");
    }

    public String getMailServer()
    {
        return System.getProperty("abiquo.server.mail.server");
    }

    public String getMailUser()
    {
        return System.getProperty("abiquo.server.mail.user");
    }

    public String getMailPassword()
    {
        return System.getProperty("abiquo.server.mail.password");
    }

    public Integer getVlanPerVDC()
    {
        return Integer.valueOf(System.getProperty("abiquo.server.networking.vlanPerVdc"));
    }

    public String getApiLocation()
    {
        return System.getProperty("abiquo.server.api.location");
    }

    public String getCostCode()
    {
        return System.getProperty("abiquo.server.costCode");
    }

    private LimitHB createLimit(String type)
    {
        long hard =
            Long.valueOf(System.getProperty("abiquo.server.resourcelimits." + type + ".hard", "0"));
        long soft =
            Long.valueOf(System.getProperty("abiquo.server.resourcelimits." + type + ".soft", "0"));

        return new LimitHB(hard, soft);
    }
}
