package com.abiquo.server.core.cloud;

import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.constraints.Length;

import com.abiquo.server.core.common.DefaultEntityBase;
import com.softwarementors.validation.constraints.LeadingOrTrailingWhitespace;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = ChefCookbook.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = ChefCookbook.TABLE_NAME)
public class ChefCookbook extends DefaultEntityBase
{
    public static final String TABLE_NAME = "chefcookbook";

    protected ChefCookbook()
    {
        super();
    }
    
    public ChefCookbook(VirtualMachine virtualmachine, String cookbook)
    {
        super();
        setVirtualmachine(virtualmachine);
        setCookbook(cookbook);
    }

    private final static String ID_COLUMN = "chefCookbookId";

    @Id
    @GeneratedValue
    @Column(name = ID_COLUMN, nullable = false)
    private Integer id;

    public Integer getId()
    {
        return this.id;
    }

    public final static String VIRTUALMACHINE_PROPERTY = "virtualmachine";

    private final static boolean VIRTUALMACHINE_REQUIRED = true;

    private final static String VIRTUALMACHINE_ID_COLUMN = "idVM";

    @JoinColumn(name = VIRTUALMACHINE_ID_COLUMN)
    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_virtualmachine")
    private VirtualMachine virtualmachine;

    @Required(value = VIRTUALMACHINE_REQUIRED)
    public VirtualMachine getVirtualmachine()
    {
        return this.virtualmachine;
    }

    public void setVirtualmachine(final VirtualMachine virtualmachine)
    {
        this.virtualmachine = virtualmachine;
    }

    public final static String COOKBOOK_PROPERTY = "cookbook";

    private final static boolean COOKBOOK_REQUIRED = true;

    private final static int COOKBOOK_LENGTH_MIN = 0;

    private final static int COOKBOOK_LENGTH_MAX = 255;

    private final static boolean COOKBOOK_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String COOKBOOK_COLUMN = "Cookbook";

    @Column(name = COOKBOOK_COLUMN, nullable = !COOKBOOK_REQUIRED, length = COOKBOOK_LENGTH_MAX)
    private String cookbook;

    @Required(value = COOKBOOK_REQUIRED)
    @Length(min = COOKBOOK_LENGTH_MIN, max = COOKBOOK_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = COOKBOOK_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getCookbook()
    {
        return this.cookbook;
    }

    private void setCookbook(final String cookbook)
    {
        this.cookbook = cookbook;
    }

}
