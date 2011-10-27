package com.abiquo.server.core.enterprise;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.SingleResourceTransportDto;

@XmlRootElement(name = "approval")
public class ApprovalWithUserDto extends SingleResourceTransportDto
{
    private static final long serialVersionUID = 6985079950355525566L;

    private Integer id;

    private Date timeRequested;

    private String token;

    private Date timeResponse;

    private String reason;

    private ApprovalState status;

    private ApprovalType approvalType;

    private byte[] request;

    private User user;

    public Integer getId()
    {
        return id;
    }

    public void setId(final Integer id)
    {
        this.id = id;
    }

    public Date getTimeRequested()
    {
        return timeRequested;
    }

    public void setTimeRequested(final Date timeRequested)
    {
        this.timeRequested = timeRequested;
    }

    public String getToken()
    {
        return token;
    }

    public void setToken(final String token)
    {
        this.token = token;
    }

    public Date getTimeResponse()
    {
        return timeResponse;
    }

    public void setTimeResponse(final Date timeResponse)
    {
        this.timeResponse = timeResponse;
    }

    public String getReason()
    {
        return reason;
    }

    public void setReason(final String reason)
    {
        this.reason = reason;
    }

    public ApprovalState getStatus()
    {
        return status;
    }

    public void setStatus(final ApprovalState status)
    {
        this.status = status;
    }

    public ApprovalType getApprovalType()
    {
        return approvalType;
    }

    public void setApprovalType(final ApprovalType approvalType)
    {
        this.approvalType = approvalType;
    }

    public void setRequest(final byte[] request)
    {
        this.request = request;
    }

    public byte[] getRequest()
    {
        return request;
    }

    public void setUser(final User user)
    {
        this.user = user;
    }

    public User getUser()
    {
        return user;
    }
}
