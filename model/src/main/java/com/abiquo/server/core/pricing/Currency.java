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

    public Currency(final String name, final String simbol)
    {
        this.name = name;
        this.simbol = simbol;
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

    public final static String SIMBOL_PROPERTY = "simbol";

    private final static boolean SIMBOL_REQUIRED = false;

    private final static int SIMBOL_LENGTH_MIN = 0;

    private final static int SIMBOL_LENGTH_MAX = 255;

    private final static boolean SIMBOL_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String SIMBOL_COLUMN = "simbol";

    @Column(name = SIMBOL_COLUMN, nullable = !SIMBOL_REQUIRED, length = SIMBOL_LENGTH_MAX)
    private String simbol;

    @Required(value = SIMBOL_REQUIRED)
    @Length(min = SIMBOL_LENGTH_MIN, max = SIMBOL_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = SIMBOL_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getSimbol()
    {
        return this.simbol;
    }

    private void setSimbol(final String simbol)
    {
        this.simbol = simbol;
    }

}
