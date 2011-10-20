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

package com.abiquo.server.core.pricing;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Min;

import org.hibernate.validator.constraints.Length;

import com.abiquo.server.core.common.DefaultEntityBase;
import com.softwarementors.validation.constraints.LeadingOrTrailingWhitespace;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = Currency.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = Currency.TABLE_NAME)
public class Currency extends DefaultEntityBase
{
    public static final String TABLE_NAME = "currency";

    // DO NOT ACCESS: present due to needs of infrastructure support. *NEVER* call from business
    // code
    protected Currency()
    {
        // Just for JPA support
    }

    public Currency(final String name, final String symbol, final int digits)
    {
        this.setName(name);
        this.setSymbol(symbol);
        this.setDigits(digits);
    }

    private final static String ID_COLUMN = "idCurrency";

    @Id
    @GeneratedValue
    @Column(name = ID_COLUMN, nullable = false)
    private Integer id;

    @Override
    public Integer getId()
    {
        return this.id;
    }

    public final static String NAME_PROPERTY = "name";

    private final static boolean NAME_REQUIRED = false;

    private final static int NAME_LENGTH_MIN = 0;

    public final static int NAME_LENGTH_MAX = 20;

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

    public final static String SYMBOL_PROPERTY = "symbol";

    private final static boolean SYMBOL_REQUIRED = true;

    private final static int SYMBOL_LENGTH_MIN = 0;

    public final static int SYMBOL_LENGTH_MAX = 10;

    private final static boolean SYMBOL_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String SYMBOL_COLUMN = "symbol";

    @Column(name = SYMBOL_COLUMN, nullable = !SYMBOL_REQUIRED, length = SYMBOL_LENGTH_MAX)
    private String symbol;

    @Required(value = SYMBOL_REQUIRED)
    @Length(min = SYMBOL_LENGTH_MIN, max = SYMBOL_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = SYMBOL_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getSymbol()
    {
        return this.symbol;
    }

    public void setSymbol(final String symbol)
    {
        this.symbol = symbol;
    }

    public final static String DIGITS_PROPERTY = "digits";

    private final static boolean DIGITS_REQUIRED = true;

    private final static String DIGITS_COLUMN = "digits";

    public final static int DIGITS_MAX = 9;

    @Column(name = DIGITS_COLUMN, nullable = !DIGITS_REQUIRED)
    private int digits;

    @Required(value = DIGITS_REQUIRED)
    @Min(value = 0)
    public int getDigits()
    {
        return digits;
    }

    public void setDigits(final int digits)
    {
        this.digits = digits;
    }

    @OneToMany(targetEntity = CostCodeCurrency.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "currency")
    private List<CostCodeCurrency> currencyCostCode = new ArrayList<CostCodeCurrency>();

}
