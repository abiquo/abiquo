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

package com.abiquo.server.core.infrastructure.network;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;

import com.abiquo.server.core.common.DefaultEntityBase;
import com.abiquo.server.core.infrastructure.RemoteService;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = Dhcp.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = Dhcp.TABLE_NAME)
public class Dhcp extends DefaultEntityBase
{
    public static final String TABLE_NAME = "dhcp_service";

    public Dhcp()
    {
    }

    public Dhcp(RemoteService remoteService)
    {
        setRemoteService(remoteService);
    }

    private final static String ID_COLUMN = "dhcp_service_id";

    @Id
    @GeneratedValue
    @Column(name = ID_COLUMN, nullable = false)
    private Integer id;

    public Integer getId()
    {
        return this.id;
    }

    public final static String REMOTE_SERVICE_PROPERTY = "remoteService";

    private final static boolean REMOTE_SERVICE_REQUIRED = false;

    private final static String REMOTE_SERVICE_ID_COLUMN = "dhcp_remote_service";

    @JoinColumn(name = REMOTE_SERVICE_ID_COLUMN)
    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_remoteService")
    private RemoteService remoteService;

    @Required(value = REMOTE_SERVICE_REQUIRED)
    public RemoteService getRemoteService()
    {
        return this.remoteService;
    }

    public void setRemoteService(RemoteService remoteService)
    {
        this.remoteService = remoteService;
    }
}
