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

package com.abiquo.server.core.cloud;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import com.abiquo.server.core.common.DefaultEntityBase;
import com.abiquo.server.core.enumerator.HypervisorType;
import com.abiquo.server.core.infrastructure.Machine;
import com.softwarementors.validation.constraints.LeadingOrTrailingWhitespace;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = Hypervisor.TABLE_NAME, uniqueConstraints = {})
// TODO: specify unique constraints
@org.hibernate.annotations.Table(appliesTo = Hypervisor.TABLE_NAME, indexes = {})
// TODO: specify indexes
public class Hypervisor extends DefaultEntityBase
{

    // ****************************** JPA support *******************************
    public static final String TABLE_NAME = "hypervisor";

    // DO NOT ACCESS: present due to needs of infrastructure support. *NEVER* call from business
    // code
    protected Hypervisor()
    {
        // Just for JPA support
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

    // ************************** Validation support ****************************
    // ******************************* Properties *******************************

    public final static String IP_PROPERTY = "ip";

    private final static boolean IP_REQUIRED = true;

    private final static int IP_LENGTH_MIN = 0;

    private final static int IP_LENGTH_MAX = 39;

    private final static boolean IP_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String IP_COLUMN = "ip";

    @Column(name = IP_COLUMN, nullable = !IP_REQUIRED, length = IP_LENGTH_MAX)
    private String ip;

    @Required(value = IP_REQUIRED)
    @Length(min = IP_LENGTH_MIN, max = IP_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = IP_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getIp()
    {
        return this.ip;
    }

    private void setIp(String ip)
    {
        this.ip = ip;
    }

    public final static String IP_SERVICE_PROPERTY = "ipService";

    private final static boolean IP_SERVICE_REQUIRED = true;

    private final static int IP_SERVICE_LENGTH_MIN = 0;

    private final static int IP_SERVICE_LENGTH_MAX = 39;

    private final static boolean IP_SERVICE_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String IP_SERVICE_COLUMN = "ipService";

    @Column(name = IP_SERVICE_COLUMN, nullable = !IP_SERVICE_REQUIRED, length = IP_SERVICE_LENGTH_MAX)
    private String ipService;

    @Required(value = IP_SERVICE_REQUIRED)
    @Length(min = IP_SERVICE_LENGTH_MIN, max = IP_SERVICE_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = IP_SERVICE_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getIpService()
    {
        return this.ipService;
    }

    private void setIpService(String ipService)
    {
        this.ipService = ipService;
    }

    public final static String PORT_PROPERTY = "port";

    private final static String PORT_COLUMN = PORT_PROPERTY;

    private final static long PORT_MIN = Integer.MIN_VALUE;

    private final static long PORT_MAX = Integer.MAX_VALUE;

    @Column(name = PORT_COLUMN, nullable = false)
    @Range(min = PORT_MIN, max = PORT_MAX)
    private int port;

    public int getPort()
    {
        return this.port;
    }

    private void setPort(int port)
    {
        this.port = port;
    }

    public final static String TYPE_PROPERTY = "type";

    private final static boolean TYPE_REQUIRED = true;

    private final static String TYPE_COLUMN = "type";

    private final static int TYPE_COLUMN_LENGTH = 20;

    @Enumerated(value = javax.persistence.EnumType.STRING)
    @Column(name = TYPE_COLUMN, nullable = !TYPE_REQUIRED, length = TYPE_COLUMN_LENGTH)
    private HypervisorType type;

    @Required(value = TYPE_REQUIRED)
    public HypervisorType getType()
    {
        return this.type;
    }

    private void setType(HypervisorType type)
    {
        this.type = type;
    }

    public final static String MACHINE_PROPERTY = "machine";

    private final static boolean MACHINE_REQUIRED = true;

    private final static String MACHINE_ID_COLUMN = "idPhysicalMachine";

    @JoinColumn(name = MACHINE_ID_COLUMN)
    @OneToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "hypervisor_machine_fk")
    private Machine machine;

    @Required(value = MACHINE_REQUIRED)
    public Machine getMachine()
    {
        return this.machine;
    }

    public void setMachine(Machine machine)
    {
        this.machine = machine;
    }

    public final static String USER_PROPERTY = "user";

    private final static int USER_LENGTH_MIN = 0;

    private final static int USER_LENGTH_MAX = 255;

    @Column(name = USER_PROPERTY, nullable = false, length = USER_LENGTH_MAX)
    private String user;

    @Required(value = true)
    @Length(min = USER_LENGTH_MIN, max = USER_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = false)
    public String getUser()
    {
        return this.user;
    }

    private void setUser(String user)
    {
        this.user = user;
    }

    public final static String PASSWORD_PROPERTY = "password";

    private final static int PASSWORD_LENGTH_MIN = 0;

    private final static int PASSWORD_LENGTH_MAX = 255;

    @Column(name = PASSWORD_PROPERTY, nullable = false, length = PASSWORD_LENGTH_MAX)
    private String password;

    @Required(value = true)
    @Length(min = PASSWORD_LENGTH_MIN, max = PASSWORD_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = false)
    public String getPassword()
    {
        return this.password;
    }

    private void setPassword(String password)
    {
        this.password = password;
    }

    // @OneToMany(mappedBy = "hypervisor")
    // private VirtualMachine virtualMachines;

    // ****************************** Associations ******************************

    // *************************** Mandatory constructors ***********************
    // TODO: define mandatory constructors
    public Hypervisor(Machine machine, HypervisorType type, String ip,
        String ipService, int port, String user, String password)
    {
        setMachine(machine);
        setType(type);
        setIp(ip);
        setIpService(ipService);
        setPort(port);
        setUser(user);
        setPassword(password);
    }

    // *************************** Business methods ***********************
    // TODO: define business methods

    // ********************************** Others ********************************
    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

}
