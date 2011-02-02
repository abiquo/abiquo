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

package com.abiquo.abiserver.pojo.virtualappliance;

import java.util.Date;

import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.LogHB;
import com.abiquo.abiserver.pojo.IPojo;

/**
 * This class represents a Log entry for Virtual Appliances
 * 
 * @author Oliver
 */

public class Log implements IPojo<LogHB>
{

    /* ------------- Public attributes ------------- */

    /**
     * The log identification
     */
    private int idLog;

    /**
     * The ID of the Virtual Appliance to which this log belongs We do not use the entire Virtual
     * Appliance object here, since a VirtualAppliance object already contains a list of logs
     */
    private int idVirtualAppliance;

    /**
     * Log description
     */
    private String description;

    /**
     * Log date
     */
    private Date logDate;

    private int deleted;

    /**
     * /** Basic constructor.
     */
    public Log()
    {
    }

    public int getIdLog()
    {
        return idLog;
    }

    public void setIdLog(int idLog)
    {
        this.idLog = idLog;
    }

    public int getIdVirtualAppliance()
    {
        return idVirtualAppliance;
    }

    public void setIdVirtualAppliance(int idVirtualAppliance)
    {
        this.idVirtualAppliance = idVirtualAppliance;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public Date getLogDate()
    {
        return logDate;
    }

    public void setLogDate(Date logDate)
    {
        this.logDate = logDate;
    }

    public int getDeleted()
    {
        return deleted;
    }

    public void setDeleted(int deleted)
    {
        this.deleted = deleted;
    }

    /**
     * This method transform to Hibernate pojo object.
     */
    public LogHB toPojoHB()
    {
        LogHB log = new LogHB();
        log.setIdLog(idLog);
        log.setDescription(description);
        log.setIdVirtualAppliance(idVirtualAppliance);
        log.setLogDate(logDate);
        log.setDeleted(deleted);

        return log;
    }

}
