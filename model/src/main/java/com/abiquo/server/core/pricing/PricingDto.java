  package com.abiquo.server.core.pricing;

  import javax.xml.bind.annotation.XmlRootElement;

  import com.abiquo.model.transport.SingleResourceTransportDto;

  @XmlRootElement(name = "")
  public class PricingDto extends SingleResourceTransportDto
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

      private int standingChargePeriod;

public int getStandingChargePeriod()
{
    return standingChargePeriod;
}

public void setStandingChargePeriod(int standingChargePeriod)
{
    this.standingChargePeriod = standingChargePeriod;
}

private int limitMaximumDeployedCharged;

public int getLimitMaximumDeployedCharged()
{
    return limitMaximumDeployedCharged;
}

public void setLimitMaximumDeployedCharged(int limitMaximumDeployedCharged)
{
    this.limitMaximumDeployedCharged = limitMaximumDeployedCharged;
}

private String vlan;

public String getVlan()
{
    return vlan;
}

public void setVlan(String vlan)
{
    this.vlan = vlan;
}

private boolean showMinimumCharge;

public boolean getShowMinimumCharge()
{
    return showMinimumCharge;
}

public void setShowMinimumCharge(boolean showMinimumCharge)
{
    this.showMinimumCharge = showMinimumCharge;
}

private int chargingPeriod;

public int getChargingPeriod()
{
    return chargingPeriod;
}

public void setChargingPeriod(int chargingPeriod)
{
    this.chargingPeriod = chargingPeriod;
}

private int minimumChargePeriod;

public int getMinimumChargePeriod()
{
    return minimumChargePeriod;
}

public void setMinimumChargePeriod(int minimumChargePeriod)
{
    this.minimumChargePeriod = minimumChargePeriod;
}

private int minimumCharge;

public int getMinimumCharge()
{
    return minimumCharge;
}

public void setMinimumCharge(int minimumCharge)
{
    this.minimumCharge = minimumCharge;
}

private boolean showChangesBeforeDeployement;

public boolean getShowChangesBeforeDeployement()
{
    return showChangesBeforeDeployement;
}

public void setShowChangesBeforeDeployement(boolean showChangesBeforeDeployement)
{
    this.showChangesBeforeDeployement = showChangesBeforeDeployement;
}

private String publicIp;

public String getPublicIp()
{
    return publicIp;
}

public void setPublicIp(String publicIp)
{
    this.publicIp = publicIp;
}

private int vCpu;

public int getVCpu()
{
    return vCpu;
}

public void setVCpu(int vCpu)
{
    this.vCpu = vCpu;
}

private int memoryMb;

public int getMemoryMB()
{
    return memoryMb;
}

public void setMemoryMB(int memoryMb)
{
    this.memoryMb = memoryMb;
}

  }
