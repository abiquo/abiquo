package com.abiquo.server.core.infrastructure;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ucsrack")
public class UcsRackDto extends RackDto
{
    private static final long serialVersionUID = 1L;
    
    private Integer port;

    public Integer getPort()
    {
        return port;
    }

    public void setPort(Integer port)
    {
        this.port = port;
    }

    private String ip;

    public String getIp()
    {
        return ip;
    }

    public void setIp(String ip)
    {
        this.ip = ip;
    }

    private String password;

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    private String user;

    public String getUser()
    {
        return user;
    }

    public void setUser(String user)
    {
        this.user = user;
    }

}
