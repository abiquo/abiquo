package com.abiquo.server.core.enterprise;

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
@Table(name = ApprovalManager.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = ApprovalManager.TABLE_NAME)
public class ApprovalManager extends DefaultEntityBase
{
    public static final String TABLE_NAME = "approval_manager";

    // DO NOT ACCESS: present due to needs of infrastructure support. *NEVER* call from business
    // code
    public ApprovalManager()
    {
        // Just for JPA support
    }

    public ApprovalManager(final String approvalMail)
    {
        setApprovalMail(approvalMail);
    }

    private final static String ID_COLUMN = "idApprovalManager";

    @Id
    @GeneratedValue
    @Column(name = ID_COLUMN, nullable = false)
    private Integer id;

    @Override
    public Integer getId()
    {
        return this.id;
    }

    public final static String ENTERPRISE_PROPERTY = "enterprise";

    private final static boolean ENTERPRISE_REQUIRED = true;

    private final static String ENTERPRISE_ID_COLUMN = "idEnterprise";

    @JoinColumn(name = ENTERPRISE_ID_COLUMN)
    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_enterprise")
    private Enterprise enterprise;

    @Required(value = ENTERPRISE_REQUIRED)
    public Enterprise getEnterprise()
    {
        return this.enterprise;
    }

    public void setEnterprise(final Enterprise enterprise)
    {
        this.enterprise = enterprise;
    }

    public final static String APPROVAL_MAIL_PROPERTY = "approvalMail";

    private final static boolean APPROVAL_MAIL_REQUIRED = false;

    private final static int APPROVAL_MAIL_LENGTH_MIN = 0;

    private final static int APPROVAL_MAIL_LENGTH_MAX = 255;

    private final static boolean APPROVAL_MAIL_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String APPROVAL_MAIL_COLUMN = "approvalMail";

    @Column(name = APPROVAL_MAIL_COLUMN, nullable = !APPROVAL_MAIL_REQUIRED, length = APPROVAL_MAIL_LENGTH_MAX)
    private String approvalMail;

    @Required(value = APPROVAL_MAIL_REQUIRED)
    @Length(min = APPROVAL_MAIL_LENGTH_MIN, max = APPROVAL_MAIL_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = APPROVAL_MAIL_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getApprovalMail()
    {
        return this.approvalMail;
    }

    public void setApprovalMail(final String approvalMail)
    {
        this.approvalMail = approvalMail;
    }

    public final static String USER_PROPERTY = "user";

    private final static boolean USER_REQUIRED = true;

    private final static String USER_ID_COLUMN = "idUser";

    @JoinColumn(name = USER_ID_COLUMN)
    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_user")
    private User user;

    @Required(value = USER_REQUIRED)
    public User getUser()
    {
        return this.user;
    }

    public void setUser(final User user)
    {
        this.user = user;
    }

}
