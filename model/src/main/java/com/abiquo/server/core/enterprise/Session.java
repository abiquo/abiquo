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

package com.abiquo.server.core.enterprise;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;

import com.abiquo.server.core.common.DefaultEntityBase;

@Entity
@Table(name = Session.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = Session.TABLE_NAME)
public class Session extends DefaultEntityBase
{
    public static final String TABLE_NAME = "session";

    protected Session()
    {
    }

    protected Session(User user, String key, Date expireDate, String authType)
    {
        setUser(user);
        setNick(user.getNick());
        setKey(key);
        setExpireDate(expireDate);
        setAuthType(authType);
    }

    protected Session(User user, String key, Date expireDate)
    {
        this(user, key, expireDate, User.AuthType.ABIQUO.name());
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

    @Column(name = "user", nullable = false)
    private String nick;

    public String getNick()
    {
        return nick;
    }

    private void setNick(String nick)
    {
        this.nick = nick;
    }

    @Column(name = "`key`", nullable = false)
    private String key;

    public String getKey()
    {
        return key;
    }

    private void setKey(String key)
    {
        this.key = key;
    }

    @Column(name = "expireDate", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date expireDate;

    public Date getExpireDate()
    {
        return expireDate;
    }

    private void setExpireDate(Date expireDate)
    {
        this.expireDate = expireDate;
    }

    @JoinColumn(name = "idUser")
    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "FK_user_user")
    private User user;

    public User getUser()
    {
        return user;
    }

    private void setUser(User user)
    {
        this.user = user;
    }

    @Column(name = "authType", nullable = false)
    private String authType;

    public String getAuthType()
    {
        return authType;
    }

    private void setAuthType(String authType)
    {
        this.authType = authType;
    }
}
