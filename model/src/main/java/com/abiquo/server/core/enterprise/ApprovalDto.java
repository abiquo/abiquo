package com.abiquo.server.core.enterprise;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.abiquo.model.transport.SingleResourceTransportDto;

@XmlRootElement(name = "approval")
@XmlType(propOrder = {"id", "token", "approvalType", "status", "timeRequested", "timeResponse",
"reason"})
public class ApprovalDto extends SingleResourceTransportDto
{
    private static final long serialVersionUID = 6985079950355525566L;

    private Integer id;

    public Integer getId()
    {
        return id;
    }

    public void setId(final Integer id)
    {
        this.id = id;
    }

    private Date timeRequested;

    public Date getTimeRequested()
    {
        return timeRequested;
    }

    public void setTimeRequested(final Date timeRequested)
    {
        this.timeRequested = timeRequested;
    }

    private String token;

    public String getToken()
    {
        return token;
    }

    public void setToken(final String token)
    {
        this.token = token;
    }

    private Date timeResponse;

    public Date getTimeResponse()
    {
        return timeResponse;
    }

    public void setTimeResponse(final Date timeResponse)
    {
        this.timeResponse = timeResponse;
    }

    private String reason;

    public String getReason()
    {
        return reason;
    }

    public void setReason(final String reason)
    {
        this.reason = reason;
    }

    private ApprovalState status;

    public ApprovalState getStatus()
    {
        return status;
    }

    public void setStatus(final ApprovalState status)
    {
        this.status = status;
    }

    private ApprovalType approvalType;

    public ApprovalType getApprovalType()
    {
        return approvalType;
    }

    public void setApprovalType(final ApprovalType approvalType)
    {
        this.approvalType = approvalType;
    }

    private byte[] request;

    public void setRequest(final byte[] request)
    {
        this.request = request;
    }

    public byte[] getRequest()
    {
        return request;
    }
}
