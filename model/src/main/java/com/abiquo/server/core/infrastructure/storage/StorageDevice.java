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
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import com.abiquo.model.enumerator.StorageTechnologyType;
import com.abiquo.model.validation.Ip;
import com.abiquo.model.validation.Port;
import com.abiquo.server.core.common.DefaultEntityBase;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.softwarementors.validation.constraints.LeadingOrTrailingWhitespace;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = StorageDevice.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = StorageDevice.TABLE_NAME)
public class StorageDevice extends DefaultEntityBase
{
    public static final String TABLE_NAME = "storage_device";

    protected StorageDevice()
    {
    }

    public StorageDevice(final String name, final String managementIP,
        final Integer managementPort, final String iscsiIP, final Integer iscsiPort,
        final StorageTechnologyType storageTechnology, final String username,
        final String password, final Datacenter dc)
    {
        this.setName(name);
        this.setManagementIp(managementIP);
        this.setManagementPort(managementPort);
        this.setIscsiIp(iscsiIP);
        this.setIscsiPort(iscsiPort);
        this.setStorageTechnology(storageTechnology);
        this.setUsername(username);
        this.setPassword(password);
        this.setDatacenter(dc);
    }

    private final static String ID_COLUMN = "id";

    @Id
    @GeneratedValue
    @Column(name = ID_COLUMN, nullable = false)
    private Integer id;

    public Integer getId()
    {
        return this.id;
    }

    public final static String MANAGEMENT_PORT_PROPERTY = "managementPort";

    private final static boolean MANAGEMENT_PORT_REQUIRED = true;

    private final static String MANAGEMENT_PORT_COLUMN = "management_port";

    private final static int MANAGEMENT_PORT_MIN = Integer.MIN_VALUE;

    private final static int MANAGEMENT_PORT_MAX = Integer.MAX_VALUE;

    @Column(name = MANAGEMENT_PORT_COLUMN, nullable = !MANAGEMENT_PORT_REQUIRED)
    @Range(min = MANAGEMENT_PORT_MIN, max = MANAGEMENT_PORT_MAX)
    private Integer managementPort;

    @Port
    public Integer getManagementPort()
    {
        return this.managementPort;
    }

    public void setManagementPort(final Integer managementPort)
    {
        this.managementPort = managementPort;
    }

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

    public void setName(final String name)
    {
        this.name = name;
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

    public void setDatacenter(final Datacenter datacenter)
    {
        this.datacenter = datacenter;
    }

    public final static String ISCSI_IP_PROPERTY = "iscsiIp";

    private final static boolean ISCSI_IP_REQUIRED = true;

    private final static int ISCSI_IP_LENGTH_MIN = 0;

    private final static int ISCSI_IP_LENGTH_MAX = 255;

    private final static boolean ISCSI_IP_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String ISCSI_IP_COLUMN = "iscsi_ip";

    @Column(name = ISCSI_IP_COLUMN, nullable = !ISCSI_IP_REQUIRED, length = ISCSI_IP_LENGTH_MAX)
    private String iscsiIp;

    @Required(value = ISCSI_IP_REQUIRED)
    @Length(min = ISCSI_IP_LENGTH_MIN, max = ISCSI_IP_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = ISCSI_IP_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    @Ip
    public String getIscsiIp()
    {
        return this.iscsiIp;
    }

    public void setIscsiIp(final String iscsiIp)
    {
        this.iscsiIp = iscsiIp;
    }

    public final static String STORAGE_TECHNOLOGY_PROPERTY = "storageTechnology";

    private final static boolean STORAGE_TECHNOLOGY_REQUIRED = true;

    private final static int STORAGE_TECHNOLOGY_LENGTH_MAX = 255;

    private final static String STORAGE_TECHNOLOGY_COLUMN = "storage_technology";

    @Enumerated(value = javax.persistence.EnumType.STRING)
    @Column(name = STORAGE_TECHNOLOGY_COLUMN, nullable = !STORAGE_TECHNOLOGY_REQUIRED, length = STORAGE_TECHNOLOGY_LENGTH_MAX)
    private StorageTechnologyType storageTechnology;

    @Required(value = STORAGE_TECHNOLOGY_REQUIRED)
    public StorageTechnologyType getStorageTechnology()
    {
        return this.storageTechnology;
    }

    public void setStorageTechnology(final StorageTechnologyType storageTechnology)
    {
        this.storageTechnology = storageTechnology;
    }

    public final static String MANAGEMENT_IP_PROPERTY = "managementIp";

    private final static boolean MANAGEMENT_IP_REQUIRED = true;

    private final static int MANAGEMENT_IP_LENGTH_MIN = 0;

    private final static int MANAGEMENT_IP_LENGTH_MAX = 255;

    private final static boolean MANAGEMENT_IP_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String MANAGEMENT_IP_COLUMN = "management_ip";

    @Column(name = MANAGEMENT_IP_COLUMN, nullable = !MANAGEMENT_IP_REQUIRED, length = MANAGEMENT_IP_LENGTH_MAX)
    private String managementIp;

    @Required(value = MANAGEMENT_IP_REQUIRED)
    @Length(min = MANAGEMENT_IP_LENGTH_MIN, max = MANAGEMENT_IP_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = MANAGEMENT_IP_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    @Ip
    public String getManagementIp()
    {
        return this.managementIp;
    }

    public void setManagementIp(final String managementIp)
    {
        this.managementIp = managementIp;
    }

    public final static String ISCSI_PORT_PROPERTY = "iscsiPort";

    private final static boolean ISCSI_PORT_REQUIRED = true;

    private final static String ISCSI_PORT_COLUMN = "iscsi_port";

    private final static int ISCSI_PORT_MIN = Integer.MIN_VALUE;

    private final static int ISCSI_PORT_MAX = Integer.MAX_VALUE;

    @Column(name = ISCSI_PORT_COLUMN, nullable = !ISCSI_PORT_REQUIRED)
    @Range(min = ISCSI_PORT_MIN, max = ISCSI_PORT_MAX)
    private int iscsiPort;

    @Port
    public int getIscsiPort()
    {
        return this.iscsiPort;
    }

    public void setIscsiPort(final int iscsiPort)
    {
        this.iscsiPort = iscsiPort;
    }

    public final static String USERNAME_PROPERTY = "username";

    private final static boolean USERNAME_REQUIRED = false;

    /* package */final static int USERNAME_LENGTH_MIN = 0;

    /* package */final static int USERNAME_LENGTH_MAX = 255;

    private final static boolean USERNAME_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String USERNAME_COLUMN = "username";

    @Column(name = USERNAME_COLUMN, nullable = !USERNAME_REQUIRED, length = USERNAME_LENGTH_MAX)
    private String username;

    @Required(value = USERNAME_REQUIRED)
    @Length(min = USERNAME_LENGTH_MIN, max = USERNAME_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = USERNAME_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getUsername()
    {
        return this.username;
    }

    public void setUsername(final String username)
    {
        this.username = username;
    }

    public final static String PASSWORD_PROPERTY = "password";

    private final static boolean PASSWORD_REQUIRED = false;

    /* package */final static int PASSWORD_LENGTH_MIN = 0;

    /* package */final static int PASSWORD_LENGTH_MAX = 255;

    private final static boolean PASSWORD_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String PASSWORD_COLUMN = "password";

    @Column(name = PASSWORD_COLUMN, nullable = !PASSWORD_REQUIRED, length = PASSWORD_LENGTH_MAX)
    private String password;

    @Required(value = PASSWORD_REQUIRED)
    @Length(min = PASSWORD_LENGTH_MIN, max = PASSWORD_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = PASSWORD_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getPassword()
    {
        return this.password;
    }

    public void setPassword(final String password)
    {
        this.password = password;
    }

}
