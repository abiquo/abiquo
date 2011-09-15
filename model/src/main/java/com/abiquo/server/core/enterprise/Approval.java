package com.abiquo.server.core.enterprise;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.validator.constraints.Length;

import com.abiquo.server.core.common.DefaultEntityBase;
import com.softwarementors.validation.constraints.LeadingOrTrailingWhitespace;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = Approval.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = Approval.TABLE_NAME)
public class Approval extends DefaultEntityBase
{
    public static final String TABLE_NAME = "approval";

    // DO NOT ACCESS: present due to needs of infrastructure support. *NEVER* call from business
    // code
    protected Approval()
    {
        // Just for JPA support
    }

    public Approval(final String token, final ApprovalType type, final ApprovalState state,
        final Date timeRequested, final Date timeResponse, final String reason)
    {
        setToken(token);
        setApprovalType(type);
        setStatus(state);
        setTimeRequested(timeRequested);
        setTimeResponse(timeResponse);
        setReason(reason);
    }

    private final static String ID_COLUMN = "idApproval";

    @Id
    @GeneratedValue
    @Column(name = ID_COLUMN, nullable = false)
    private Integer id;

    public Integer getId()
    {
        return this.id;
    }

    public final static String TIME_REQUESTED_PROPERTY = "timeRequested";

    private final static boolean TIME_REQUESTED_REQUIRED = false;

    private final static String TIME_REQUESTED_COLUMN = "timeRequested";

    @Column(name = TIME_REQUESTED_COLUMN, nullable = !TIME_REQUESTED_REQUIRED)
    private Date timeRequested;

    @Required(value = TIME_REQUESTED_REQUIRED)
    public Date getTimeRequested()
    {
        return this.timeRequested;
    }

    public void setTimeRequested(final Date timeRequested)
    {
        this.timeRequested = timeRequested;
    }

    public final static String TOKEN_PROPERTY = "token";

    private final static boolean TOKEN_REQUIRED = false;

    /* package */final static int TOKEN_LENGTH_MIN = 0;

    /* package */final static int TOKEN_LENGTH_MAX = 255;

    private final static boolean TOKEN_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String TOKEN_COLUMN = "token";

    @Column(name = TOKEN_COLUMN, nullable = !TOKEN_REQUIRED, length = TOKEN_LENGTH_MAX)
    private String token;

    @Required(value = TOKEN_REQUIRED)
    @Length(min = TOKEN_LENGTH_MIN, max = TOKEN_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = TOKEN_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getToken()
    {
        return this.token;
    }

    public void setToken(final String token)
    {
        this.token = token;
    }

    public final static String TIME_RESPONSE_PROPERTY = "timeResponse";

    private final static boolean TIME_RESPONSE_REQUIRED = false;

    private final static String TIME_RESPONSE_COLUMN = "timeResponse";

    @Column(name = TIME_RESPONSE_COLUMN, nullable = !TIME_RESPONSE_REQUIRED)
    private Date timeResponse;

    @Required(value = TIME_RESPONSE_REQUIRED)
    public Date getTimeResponse()
    {
        return this.timeResponse;
    }

    public void setTimeResponse(final Date timeResponse)
    {
        this.timeResponse = timeResponse;
    }

    public final static String REASON_PROPERTY = "reason";

    private final static boolean REASON_REQUIRED = false;

    /* package */final static int REASON_LENGTH_MIN = 0;

    /* package */final static int REASON_LENGTH_MAX = 255;

    private final static boolean REASON_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String REASON_COLUMN = "reason";

    @Column(name = REASON_COLUMN, nullable = !REASON_REQUIRED, length = REASON_LENGTH_MAX)
    private String reason;

    @Required(value = REASON_REQUIRED)
    @Length(min = REASON_LENGTH_MIN, max = REASON_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = REASON_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getReason()
    {
        return this.reason;
    }

    public void setReason(final String reason)
    {
        this.reason = reason;
    }

    public final static String STATUS_PROPERTY = "status";

    private final static boolean STATUS_REQUIRED = true;

    private final static String STATUS_COLUMN = "status";

    @Enumerated(value = javax.persistence.EnumType.STRING)
    @Column(name = STATUS_COLUMN, nullable = !STATUS_REQUIRED)
    private ApprovalState status;

    @Required(value = STATUS_REQUIRED)
    public ApprovalState getStatus()
    {
        return this.status;
    }

    public void setStatus(final ApprovalState status)
    {
        this.status = status;
    }

    public final static String APPROVAL_TYPE_PROPERTY = "approvalType";

    private final static boolean APPROVAL_TYPE_REQUIRED = true;

    private final static String APPROVAL_TYPE_COLUMN = "approvalType";

    @Enumerated(value = javax.persistence.EnumType.STRING)
    @Column(name = APPROVAL_TYPE_COLUMN, nullable = !APPROVAL_TYPE_REQUIRED)
    private ApprovalType approvalType;

    @Required(value = APPROVAL_TYPE_REQUIRED)
    public ApprovalType getApprovalType()
    {
        return this.approvalType;
    }

    public void setApprovalType(final ApprovalType approvalType)
    {
        this.approvalType = approvalType;
    }
}
