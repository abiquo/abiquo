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

package com.abiquo.server.core.infrastructure;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.server.core.common.DefaultEntityBase;
import com.softwarementors.validation.constraints.LeadingOrTrailingWhitespace;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = RemoteService.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = RemoteService.TABLE_NAME)
public class RemoteService extends DefaultEntityBase
{
    public static final String TABLE_NAME = "remote_service";

    public static final int STATUS_ERROR = 0;

    public static final int STATUS_SUCCESS = 1;

    protected RemoteService()
    {
    }

    public RemoteService(Datacenter datacenter, RemoteServiceType type, String uri, int status)
    {
        setDatacenter(datacenter);
        setType(type);
        setUri(uri);
        setStatus(status);
    }

    private final static String ID_COLUMN = "idRemoteService";

    @Id
    @GeneratedValue
    @Column(name = ID_COLUMN, nullable = false)
    private Integer id;

    public Integer getId()
    {
        return this.id;
    }

    public final static String URI_PROPERTY = "uri";

    private final static boolean URI_REQUIRED = false;

    private final static int URI_LENGTH_MIN = 0;

    private final static int URI_LENGTH_MAX = 255;

    private final static boolean URI_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String URI_COLUMN = "Uri";

    @Column(name = URI_COLUMN, nullable = !URI_REQUIRED, length = URI_LENGTH_MAX)
    private String uri;

    @Required(value = URI_REQUIRED)
    @Length(min = URI_LENGTH_MIN, max = URI_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = URI_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getUri()
    {
        return this.uri;
    }

    public void setUri(String uri)
    {
        this.uri = uri;
    }

    public final static String DATACENTER_PROPERTY = "datacenter";

    private final static boolean DATACENTER_REQUIRED = true;

    private final static String DATACENTER_ID_COLUMN = "idDataCenter";

    @JoinColumn(name = DATACENTER_ID_COLUMN)
    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_datacenter")
    private Datacenter datacenter;

    @Required(value = DATACENTER_REQUIRED)
    public Datacenter getDatacenter()
    {
        return this.datacenter;
    }

    public void setDatacenter(Datacenter datacenter)
    {
        this.datacenter = datacenter;
    }

    public final static String TYPE_PROPERTY = "type";

    private final static boolean TYPE_REQUIRED = true;

    private final static String TYPE_COLUMN = "remoteServiceType";

    private final static int TYPE_COLUMN_LENGTH = 50;

    @Enumerated(value = javax.persistence.EnumType.STRING)
    @Column(name = TYPE_COLUMN, nullable = !TYPE_REQUIRED, length = TYPE_COLUMN_LENGTH)
    private RemoteServiceType type;

    @Required(value = TYPE_REQUIRED)
    public RemoteServiceType getType()
    {
        return this.type;
    }

    public void setType(RemoteServiceType type)
    {
        this.type = type;
    }

    public final static String STATUS_PROPERTY = "status";

    private final static String STATUS_COLUMN = "status";

    private final static int STATUS_MIN = Integer.MIN_VALUE;

    private final static int STATUS_MAX = Integer.MAX_VALUE;

    @Column(name = STATUS_COLUMN, nullable = true)
    @Range(min = STATUS_MIN, max = STATUS_MAX)
    private int status;

    public int getStatus()
    {
        return this.status;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }
}
