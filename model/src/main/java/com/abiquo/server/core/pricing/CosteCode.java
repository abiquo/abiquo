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
@Table(name = CosteCode.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = CosteCode.TABLE_NAME)
public class CosteCode extends DefaultEntityBase
{
    public static final String TABLE_NAME = "costeCode";

    // DO NOT ACCESS: present due to needs of infrastructure support. *NEVER* call from business
    // code
    protected CosteCode()
    {
        // Just for JPA support
    }

    /* package */final static String ID_COLUMN = "idCosteCode";

    @Id
    @GeneratedValue
    @Column(name = ID_COLUMN, nullable = false)
    private Integer id;

    @Override
    public Integer getId()
    {
        return this.id;
    }

    public final static String VARIABLE_PROPERTY = "variable";

    private final static boolean VARIABLE_REQUIRED = false;

    private final static int VARIABLE_LENGTH_MIN = 0;

    private final static int VARIABLE_LENGTH_MAX = 255;

    private final static boolean VARIABLE_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String VARIABLE_COLUMN = "variable";

    @Column(name = VARIABLE_COLUMN, nullable = !VARIABLE_REQUIRED, length = VARIABLE_LENGTH_MAX)
    private String variable;

    @Required(value = VARIABLE_REQUIRED)
    @Length(min = VARIABLE_LENGTH_MIN, max = VARIABLE_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = VARIABLE_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getVariable()
    {
        return this.variable;
    }

    private void setVariable(final String variable)
    {
        this.variable = variable;
    }

}
