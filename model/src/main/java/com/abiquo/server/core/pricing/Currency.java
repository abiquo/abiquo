package com.abiquo.server.core.pricing;

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

    public Currency(final String name, final String symbol)
    {
        this.setName(name);
        this.setSymbol(symbol);
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

    private final static int NAME_LENGTH_MAX = 255;

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

    private void setName(final String name)
    {
        this.name = name;
    }

    public final static String SYMBOL_PROPERTY = "symbol";

    private final static boolean SYMBOL_REQUIRED = true;

    private final static int SYMBOL_LENGTH_MIN = 0;

    private final static int SYMBOL_LENGTH_MAX = 255;

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

    private void setSymbol(final String symbol)
    {
        this.symbol = symbol;
    }

    public final static String BLOCKED_PROPERTY = "blocked";

    private final static String BLOCKED_COLUMN = "blocked";

    private final static boolean BLOCKED_REQUIRED = true;

    @Column(name = BLOCKED_COLUMN, nullable = !BLOCKED_REQUIRED)
    private boolean blocked;

    @Required(value = BLOCKED_REQUIRED)
    public boolean isBlocked()
    {
        return blocked;
    }

    public void setBlocked(final boolean blocked)
    {
        this.blocked = blocked;
    }
}
