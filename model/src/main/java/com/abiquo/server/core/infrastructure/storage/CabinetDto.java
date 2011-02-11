  package com.abiquo.server.core.infrastructure.storage;

  import javax.xml.bind.annotation.XmlRootElement;

  import com.abiquo.model.transport.SingleResourceTransportDto;

  @XmlRootElement(name = "")
  public class CabinetDto extends SingleResourceTransportDto
  {
      private Integer id;
      public Integer getId()
      {
          return id;
      }

      public void setId(Integer id)
      {
          this.id = id;
      }

      private int managementPort;

public int getManagementPort()
{
    return managementPort;
}

public void setManagementPort(int managementPort)
{
    this.managementPort = managementPort;
}

private String name;

public String getName()
{
    return name;
}

public void setName(String name)
{
    this.name = name;
}

private String iscsiIp;

public String getIscsiIp()
{
    return iscsiIp;
}

public void setIscsiIp(String iscsiIp)
{
    this.iscsiIp = iscsiIp;
}

private String storageTechnology;

public String getStorageTechnology()
{
    return storageTechnology;
}

public void setStorageTechnology(String storageTechnology)
{
    this.storageTechnology = storageTechnology;
}

private String managementIp;

public String getManagementIp()
{
    return managementIp;
}

public void setManagementIp(String managementIp)
{
    this.managementIp = managementIp;
}

private int iscsiPort;

public int getIscsiPort()
{
    return iscsiPort;
}

public void setIscsiPort(int iscsiPort)
{
    this.iscsiPort = iscsiPort;
}

  }
