package com.abiquo.server.core.appslibrary;

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
@Table(name = Icon.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = Icon.TABLE_NAME)
public class Icon extends DefaultEntityBase
{
    public static final String TABLE_NAME = "icon";

    // DO NOT ACCESS: present due to needs of infrastructure support. *NEVER* call from business
    // code
    public Icon()
    {
        // Just for JPA support
    }

    public Icon(final String iconPath)
    {
        setPath(iconPath);
    }

    private final static String ID_COLUMN = "idIcon";

    @Id
    @GeneratedValue
    @Column(name = ID_COLUMN, nullable = false)
    private Integer id;

    public Integer getId()
    {
        return this.id;
    }

    public final static String NAME_PROPERTY = "name";

    private final static boolean NAME_REQUIRED = true;

    private final static int NAME_LENGTH_MIN = 0;

    private final static int NAME_LENGTH_MAX = 20;

    private final static boolean NAME_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String NAME_COLUMN = "name";

    @Column(name = NAME_COLUMN, nullable = !NAME_REQUIRED, length = NAME_LENGTH_MAX)
    private String name = "icon";

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

    public final static String PATH_PROPERTY = "path";

    private final static boolean PATH_REQUIRED = false;

    private final static int PATH_LENGTH_MIN = 0;

    private final static int PATH_LENGTH_MAX = 20;

    private final static boolean PATH_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String PATH_COLUMN = "path";

    @Column(name = PATH_COLUMN, nullable = !PATH_REQUIRED, length = PATH_LENGTH_MAX)
    private String path = "";

    @Required(value = PATH_REQUIRED)
    @Length(min = PATH_LENGTH_MIN, max = PATH_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = PATH_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getPath()
    {
        return this.path;
    }

    public void setPath(final String path)
    {
        this.path = path;
    }

}
