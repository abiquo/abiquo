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
package com.abiquo.server.core.infrastructure.storage;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import com.abiquo.model.enumerator.StorageTechnologyType;
import com.abiquo.server.core.common.GenericEnityBase;
import com.abiquo.server.core.infrastructure.RemoteService;
import com.softwarementors.validation.constraints.LeadingOrTrailingWhitespace;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = StoragePool.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = StoragePool.TABLE_NAME)
public class StoragePool extends GenericEnityBase<String>
{
    public static final String TABLE_NAME = "storage_pool";

    protected StoragePool()
    {
    }

    private final static String ID_COLUMN = "idStorage";

    /* package */final static int ID_LENGTH_MIN = 1;

    /* package */final static int ID_LENGTH_MAX = 40;

    private final static boolean ID_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    @Id
    @Column(name = ID_COLUMN, nullable = false, length = ID_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = ID_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    private String id;

    public String getId()
    {
        return this.id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

     public final static String HOST_PORT_PROPERTY = "hostPort";
	
	 private final static boolean HOST_PORT_REQUIRED = true;
	
	 private final static String HOST_PORT_COLUMN = "host_port";
	
	 private final static int HOST_PORT_MIN = Integer.MIN_VALUE;
	
	 private final static int HOST_PORT_MAX = Integer.MAX_VALUE;
	
//    @Column(name = HOST_PORT_COLUMN, nullable = !HOST_PORT_REQUIRED)
//    @Range(min = HOST_PORT_MIN, max = HOST_PORT_MAX)
//    private int hostPort;
//
//    public int getHostPort()
//    {
//        return this.hostPort;
//    }
//
//    public void setHostPort(int hostPort)
//    {
//        this.hostPort = hostPort;
//    }

    public final static String URL_MANAGEMENT_PROPERTY = "urlManagement";

    private final static boolean URL_MANAGEMENT_REQUIRED = true;

    /* package */final static int URL_MANAGEMENT_LENGTH_MIN = 0;

    /* package */final static int URL_MANAGEMENT_LENGTH_MAX = 255;

    private final static boolean URL_MANAGEMENT_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String URL_MANAGEMENT_COLUMN = "url_management";

//    @Column(name = URL_MANAGEMENT_COLUMN, nullable = !URL_MANAGEMENT_REQUIRED, length = URL_MANAGEMENT_LENGTH_MAX)
//    private String urlManagement;
//
//    @Required(value = URL_MANAGEMENT_REQUIRED)
//    @Length(min = URL_MANAGEMENT_LENGTH_MIN, max = URL_MANAGEMENT_LENGTH_MAX)
//    @LeadingOrTrailingWhitespace(allowed = URL_MANAGEMENT_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
//    public String getUrlManagement()
//    {
//        return this.urlManagement;
//    }
//
//    public void setUrlManagement(String urlManagement)
//    {
//        this.urlManagement = urlManagement;
//    }

    public final static String NAME_PROPERTY = "name";

    private final static boolean NAME_REQUIRED = true;

    /* package */final static int NAME_LENGTH_MIN = 0;

    /* package */final static int NAME_LENGTH_MAX = 255;

    private final static boolean NAME_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String NAME_COLUMN = "name";

    @Column(name = NAME_COLUMN, nullable = !NAME_REQUIRED, length = NAME_LENGTH_MAX)
    private String name;

    @Required(value = NAME_REQUIRED)
    @Length(min = NAME_LENGTH_MIN, max = NAME_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = NAME_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getName()
    {
        return this.name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

	public final static String TYPE_PROPERTY = "type";

	private final static boolean TYPE_REQUIRED = true;

	private final static String TYPE_COLUMN = "storage_technology";

//	@Enumerated(value = javax.persistence.EnumType.STRING)
//	@Column(name = TYPE_COLUMN, nullable = !TYPE_REQUIRED)
//	private StorageTechnologyType type;
//
//	@Required(value = TYPE_REQUIRED)
//	public StorageTechnologyType getType() {
//		return this.type;
//	}
//
//	public void setType(StorageTechnologyType type) {
//		this.type = type;
//	}

    public final static String HOST_IP_PROPERTY = "hostIp";

    private final static boolean HOST_IP_REQUIRED = true;

    /* package */final static int HOST_IP_LENGTH_MIN = 0;

    /* package */final static int HOST_IP_LENGTH_MAX = 255;

    private final static boolean HOST_IP_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String HOST_IP_COLUMN = "host_ip";
//
//    @Column(name = HOST_IP_COLUMN, nullable = !HOST_IP_REQUIRED, length = HOST_IP_LENGTH_MAX)
//    private String hostIp;
//
//    @Required(value = HOST_IP_REQUIRED)
//    @Length(min = HOST_IP_LENGTH_MIN, max = HOST_IP_LENGTH_MAX)
//    @LeadingOrTrailingWhitespace(allowed = HOST_IP_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
//    public String getHostIp()
//    {
//        return this.hostIp;
//    }
//
//    public void setHostIp(String hostIp)
//    {
//        this.hostIp = hostIp;
//    }

    public final static String REMOTE_SERVICE_PROPERTY = "remoteService";

    private final static boolean REMOTE_SERVICE_REQUIRED = true;

    private final static String REMOTE_SERVICE_ID_COLUMN = "idRemoteService";
//
//    @JoinColumn(name = REMOTE_SERVICE_ID_COLUMN)
//    @ManyToOne(fetch = FetchType.LAZY)
//    @ForeignKey(name = "FK_" + TABLE_NAME + "_remoteService")
//    private RemoteService remoteService;
//
//    @Required(value = REMOTE_SERVICE_REQUIRED)
//    public RemoteService getRemoteService()
//    {
//        return this.remoteService;
//    }
//
//    public void setRemoteService(RemoteService remoteService)
//    {
//        this.remoteService = remoteService;
//    }

    // *************************** Mandatory constructors ***********************

    @Column(name = "idCabin", nullable = false, length = 10)
    private Integer idCabin = 1;
    
    @Column(name = "idTier", nullable = false, length = 10)
    private Integer idTier = 1;
    
    public StoragePool(String name, String urlManagement, StorageTechnologyType type,
        String hostIp, int hostPort, RemoteService remoteService)
    {
        super();
        setName(name);
//		setUrlManagement(urlManagement);
//		setType(type);
//		setHostIp(hostIp);
//		setHostPort(hostPort);
//		setRemoteService(remoteService);
    }

}
