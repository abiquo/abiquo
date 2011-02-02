/**
 * Abiquo community edition
 * cloud management application for hybrid clouds
 * Copyright (C) 2008-2010 - Abiquo Holdings S.L.
 *
 * This application is free software; you can redistribute it and/or
 * modify it under the terms of the GNU LESSER GENERAL PUBLIC
 * LICENSE as published by the Free Software Foundation under
 * version 3 of the License
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * LESSER GENERAL PUBLIC LICENSE v.3 for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

/**
 * 
 */
package com.abiquo.abiserver.pojo.networking;

/**
 * Defines the VLAN specific values specific of a rack
 * 
 * @author pnavarro
 */
public class VlanNetworkParameters
{

    /**
     * Identifer of the parameter.
     */
    private Integer vlan_network_parametersId;

    /**
     * The VLAN id min
     */
    private Integer vlan_id_min;

    /**
     * The VLAN id max
     */
    private Integer vlan_id_max;

    /**
     * The VLAN's id to avoid
     */
    private String vlans_id_avoided;

    /**
     * The VLAN's per VDC
     */
    private Integer vlan_per_vdc_expected;

    /**
     * The VLAN's networking resource security quotient
     */
    private Integer NRSQ;

    public VlanNetworkParameters()
    {
    }

    /**
     * Constructor for default parameters
     * 
     * @param vlan_id_min the VLAN id min
     * @param vlan_id_max the VLAN id max
     * @param vlans_id_avoided the VLAN avoided id's in CSV format
     * @param nrsq TODO
     * @param vlan_per_vdc_expected TODO
     */
    public VlanNetworkParameters(Integer vlan_id_min, Integer vlan_id_max, String vlans_id_avoided,
        Integer nrsq, Integer vlan_per_vdc_expected)
    {
        this.vlan_id_min = vlan_id_min;
        this.vlan_id_max = vlan_id_max;
        this.vlans_id_avoided = vlans_id_avoided;
        this.NRSQ = nrsq;
        this.vlan_per_vdc_expected = vlan_per_vdc_expected;
    }

    /**
     * @param vlan_network_parametersId the vlan_network_parametersId to set
     */
    public void setVlan_network_parametersId(Integer vlan_network_parametersId)
    {
        this.vlan_network_parametersId = vlan_network_parametersId;
    }

    /**
     * @return the vlan_network_parametersId
     */
    public Integer getVlan_network_parametersId()
    {
        return vlan_network_parametersId;
    }

    /**
     * @return the vlan_id_min
     */
    public Integer getVlan_id_min()
    {
        return vlan_id_min;
    }

    /**
     * @param vlanIdMin the vlan_id_min to set
     */
    public void setVlan_id_min(Integer vlanIdMin)
    {
        vlan_id_min = vlanIdMin;
    }

    /**
     * @return the vlan_id_max
     */
    public Integer getVlan_id_max()
    {
        return vlan_id_max;
    }

    /**
     * @param vlanIdMax the vlan_id_max to set
     */
    public void setVlan_id_max(Integer vlanIdMax)
    {
        vlan_id_max = vlanIdMax;
    }

    /**
     * @return the vlans_id_avoided
     */
    public String getVlans_id_avoided()
    {
        return vlans_id_avoided;
    }

    /**
     * @param vlansIdAvoided the vlans_id_avoided to set
     */
    public void setVlans_id_avoided(String vlansIdAvoided)
    {
        vlans_id_avoided = vlansIdAvoided;
    }

    public void setVlan_per_vdc_expected(Integer vlan_per_vdc_expected)
    {
        this.vlan_per_vdc_expected = vlan_per_vdc_expected;
    }

    public Integer getVlan_per_vdc_expected()
    {
        return vlan_per_vdc_expected;
    }

    public void setNRSQ(Integer nRSQ)
    {
        NRSQ = nRSQ;
    }

    public Integer getNRSQ()
    {
        return NRSQ;
    }

}
