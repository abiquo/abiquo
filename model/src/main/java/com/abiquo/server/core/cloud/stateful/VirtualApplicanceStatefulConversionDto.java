package com.abiquo.server.core.cloud.stateful;

import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.SingleResourceTransportDto;
import com.abiquo.server.core.cloud.State;
import com.abiquo.server.core.cloud.VirtualAppliance;

@XmlRootElement(name = "")
public class VirtualApplicanceStatefulConversionDto extends SingleResourceTransportDto
{
    private static final long serialVersionUID = 9214453688265809192L;

    private Integer id;

    public Integer getId()
    {
        return id;
    }

    public void setId(final Integer id)
    {
        this.id = id;
    }

    private int idUser;

    public int getIdUser()
    {
        return idUser;
    }

    public void setIdUser(final int idUser)
    {
        this.idUser = idUser;
    }

    private VirtualAppliance virtualAppliance;

    public VirtualAppliance getVirtualAppliance()
    {
        return virtualAppliance;
    }

    public void setVirtualAppliance(final VirtualAppliance vapp)
    {
        this.virtualAppliance = vapp;
    }

    private State subState;

    public State getSubState()
    {
        return subState;
    }

    public void setSubState(final State subState)
    {
        this.subState = subState;
    }

    private State state;

    public State getState()
    {
        return state;
    }

    public void setState(final State state)
    {
        this.state = state;
    }

}
