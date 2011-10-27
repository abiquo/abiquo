package com.abiquo.server.core.enterprise;

import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.SingleResourceTransportDto;

@XmlRootElement(name = "approvalmanager")
public class ApprovalManagerDto extends SingleResourceTransportDto
{
    private static final long serialVersionUID = 1L;

    private Integer idUser;

    private Integer idManager;

    public Integer getUserId()
    {
        return idUser;
    }

    public void setUserId(final Integer idUser)
    {
        this.idUser = idUser;
    }

    public void setManagerId(final Integer idManager)
    {
        this.idManager = idManager;
    }

    public Integer getManagerId()
    {
        return idManager;
    }
}
