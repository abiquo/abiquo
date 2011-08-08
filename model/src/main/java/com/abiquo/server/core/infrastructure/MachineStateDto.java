package com.abiquo.server.core.infrastructure;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.SingleResourceTransportDto;
import com.abiquo.server.core.infrastructure.Machine.State;

@XmlRootElement(name = "MachineState")
public class MachineStateDto extends SingleResourceTransportDto
{
    private static final long serialVersionUID = -1283420076908929678L;

    private State state;

    @XmlElement(name = "state")
    public State getState()
    {
        return state;
    }

    public void setState(final State state)
    {
        this.state = state;
    }

}
