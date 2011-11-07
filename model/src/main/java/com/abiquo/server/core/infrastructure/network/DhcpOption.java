package com.abiquo.server.core.infrastructure.network;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import com.abiquo.server.core.common.DefaultEntityBase;
import com.softwarementors.validation.constraints.LeadingOrTrailingWhitespace;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = DhcpOption.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = DhcpOption.TABLE_NAME)
public class DhcpOption extends DefaultEntityBase
{
    public static final String TABLE_NAME = "dhcpOption";

    // DO NOT ACCESS: present due to needs of infrastructure support. *NEVER* call from business
    // code
    protected DhcpOption()
    {
        // Just for JPA support
    }

    private final static String ID_COLUMN = "idDhcpOption";

    @Id
    @GeneratedValue
    @Column(name = ID_COLUMN, nullable = false)
    private Integer id;

    @Override
    public Integer getId()
    {
        return this.id;
    }

    public final static String OPTION_PROPERTY = "option";

    private final static boolean OPTION_REQUIRED = false;

    private final static String OPTION_COLUMN = "option";

    private final static int OPTION_MIN = Integer.MIN_VALUE;

    private final static int OPTION_MAX = Integer.MAX_VALUE;

    @Column(name = OPTION_COLUMN, nullable = !OPTION_REQUIRED)
    @Range(min = OPTION_MIN, max = OPTION_MAX)
    private Integer option;

    public Integer getOption()
    {
        return this.option;
    }

    private void setOption(final Integer option)
    {
        this.option = option;
    }

    public final static String DESCRIPTION_PROPERTY = "description";

    private final static boolean DESCRIPTION_REQUIRED = false;

    private final static int DESCRIPTION_LENGTH_MIN = 0;

    private final static int DESCRIPTION_LENGTH_MAX = 255;

    private final static boolean DESCRIPTION_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String DESCRIPTION_COLUMN = "description";

    @Column(name = DESCRIPTION_COLUMN, nullable = !DESCRIPTION_REQUIRED, length = DESCRIPTION_LENGTH_MAX)
    private String description;

    @Required(value = DESCRIPTION_REQUIRED)
    @Length(min = DESCRIPTION_LENGTH_MIN, max = DESCRIPTION_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = DESCRIPTION_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getDescription()
    {
        return this.description;
    }

    private void setDescription(final String description)
    {
        this.description = description;
    }

}
