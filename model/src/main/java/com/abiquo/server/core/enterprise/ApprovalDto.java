package com.abiquo.server.core.enterprise;

import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.SingleResourceTransportDto;

@XmlRootElement(name = "approval")
public class ApprovalDto extends SingleResourceTransportDto
{
    private static final long serialVersionUID = 9064569677642171693L;

    private Integer id;

    private String token;

    private ApprovalType approvalType;

    private ApprovalState approvalState;

    private String reason;

    public void setId(final Integer id)
    {
        this.id = id;
    }

    public Integer getId()
    {
        return id;
    }

    public void setToken(final String token)
    {
        this.token = token;
    }

    public String getToken()
    {
        return token;
    }

    public void setApprovalType(final ApprovalType approvalType)
    {
        this.approvalType = approvalType;
    }

    public ApprovalType getApprovalType()
    {
        return approvalType;
    }

    public void setApprovalState(final ApprovalState approvalState)
    {
        this.approvalState = approvalState;
    }

    public ApprovalState getApprovalState()
    {
        return approvalState;
    }

    public void setReason(final String reason)
    {
        this.reason = reason;
    }

    public String getReason()
    {
        return reason;
    }
}
