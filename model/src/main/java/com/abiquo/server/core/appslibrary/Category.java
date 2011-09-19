package com.abiquo.server.core.appslibrary;

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
@Table(name = Category.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = Category.TABLE_NAME)
public class Category extends DefaultEntityBase
{
    public static final String TABLE_NAME = "category";

    // DO NOT ACCESS: present due to needs of infrastructure support. *NEVER* call from business
    // code
    public Category()
    {
        // Just for JPA support
    }

    public Category(final String name)
    {
        setName(name);
    }

    private final static String ID_COLUMN = "idCategory";

    @Id
    @GeneratedValue
    @Column(name = ID_COLUMN, nullable = false)
    private Integer id;

    public Integer getId()
    {
        return this.id;
    }

    public void setId(final Integer id)
    {
        this.id = id;
    }

    public final static String NAME_PROPERTY = "name";

    private final static boolean NAME_REQUIRED = true;

    private final static int NAME_LENGTH_MIN = 0;

    private final static int NAME_LENGTH_MAX = 255;

    private final static boolean NAME_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String NAME_COLUMN = "name";

    @Column(name = NAME_COLUMN, nullable = !NAME_REQUIRED, length = NAME_LENGTH_MAX)
    private String name = "";

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

    public final static String IS_DEFAULT_PROPERTY = "isDefault";

    private final static boolean IS_DEFAULT_REQUIRED = true;

    private final static String IS_DEFAULT_COLUMN = "isDefault";

    private final static int IS_DEFAULT_MIN = Integer.MIN_VALUE;

    private final static int IS_DEFAULT_MAX = Integer.MAX_VALUE;

    @Column(name = IS_DEFAULT_COLUMN, nullable = !IS_DEFAULT_REQUIRED)
    @Range(min = IS_DEFAULT_MIN, max = IS_DEFAULT_MAX)
    private Integer isDefault;

    public Integer getIsDefault()
    {
        return this.isDefault;
    }

    public void setIsDefault(final Integer isDefault)
    {
        this.isDefault = isDefault;
    }

    public final static String IS_ERASABLE_PROPERTY = "isErasable";

    private final static boolean IS_ERASABLE_REQUIRED = false;

    private final static String IS_ERASABLE_COLUMN = "isErasable";

    private final static int IS_ERASABLE_MIN = Integer.MIN_VALUE;

    private final static int IS_ERASABLE_MAX = Integer.MAX_VALUE;

    @Column(name = IS_ERASABLE_COLUMN, nullable = !IS_ERASABLE_REQUIRED)
    @Range(min = IS_ERASABLE_MIN, max = IS_ERASABLE_MAX)
    private Integer isErasable;

    public Integer getIsErasable()
    {
        return this.isErasable;
    }

    public void setIsErasable(final Integer isErasable)
    {
        this.isErasable = isErasable;
    }

}
