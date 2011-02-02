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
package com.abiquo.server.core.config;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.validator.constraints.Length;

import com.abiquo.server.core.common.DefaultEntityBase;
import com.softwarementors.validation.constraints.LeadingOrTrailingWhitespace;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = License.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = License.TABLE_NAME)
public class License extends DefaultEntityBase
{
    // *************************** Decrypted license field constants ***********************

    /** The date format used to store license dates. */
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /** The Customer ID license key. */
    public static final String CUSTOMER_ID_KEY = "license.customer.id";

    /** The Enabled IP license key. */
    public static final String ENABLED_IP_KEY = "license.enabled.ip";

    /** The Number of Cores license key. */
    public static final String NUMBER_OF_CORES_KEY = "license.cores.max";

    /** The Expiration Date license key. */
    public static final String EXPIRATION_DATE_KEY = "license.expires";

    // *************************** License model ***********************

    public static final String TABLE_NAME = "license";

    protected License()
    {
    }

    private final static String ID_COLUMN = "idLicense";

    @Id
    @GeneratedValue
    @Column(name = ID_COLUMN, nullable = false)
    private Integer id;

    public Integer getId()
    {
        return this.id;
    }

    public final static String DATA_PROPERTY = "data";

    private final static boolean DATA_REQUIRED = true;

    /* package */final static int DATA_LENGTH_MIN = 1;

    /* package */final static int DATA_LENGTH_MAX = 1000;

    private final static boolean DATA_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String DATA_COLUMN = "data";

    @Column(name = DATA_COLUMN, nullable = !DATA_REQUIRED, length = DATA_LENGTH_MAX)
    private String data;

    @Required(value = DATA_REQUIRED)
    @Length(min = DATA_LENGTH_MIN, max = DATA_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = DATA_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getData()
    {
        return this.data;
    }

    public void setData(String data)
    {
        this.data = data;
    }

    // *************************** Mandatory constructors ***********************

    public License(String data)
    {
        super();
        this.data = data;
    }

    // *************************** Helper methods ***********************

    public static boolean isActive(LicenseDto license)
    {
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
        try
        {
            Date expiration = format.parse(license.getExpiration());
            return expiration.getTime() >= System.currentTimeMillis();
        }
        catch (ParseException e)
        {
            throw new RuntimeException("The license expiration date cannot be parsed");
        }
    }

}
