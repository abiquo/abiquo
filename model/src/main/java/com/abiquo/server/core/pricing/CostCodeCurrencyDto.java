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

package com.abiquo.server.core.pricing;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.SingleResourceTransportDto;

@XmlRootElement(name = "costeCodeCurrency")
public class CostCodeCurrencyDto extends SingleResourceTransportDto
{
    /**
     * 
     */
    private static final long serialVersionUID = -5240459003907673943L;

    public CostCodeCurrencyDto()
    {
    }

    public CostCodeCurrencyDto(final BigDecimal price)
    {
        this.price = price;

    }

    private Integer id;

    public Integer getId()
    {
        return id;
    }

    public void setId(final Integer id)
    {
        this.id = id;
    }

    private BigDecimal price;

    public BigDecimal getPrice()
    {
        return price;
    }

    public void setPrice(final BigDecimal price)
    {
        this.price = price;
    }

    private Integer idCurrency;

    public Integer getIdCurrency()
    {
        return idCurrency;
    }

    public void setIdCurrency(final Integer idCurrency)
    {
        this.idCurrency = idCurrency;
    }

    private Integer idCostCode;

    public Integer getIdCostCode()
    {
        return idCostCode;
    }

    public void setIdCostCode(final Integer idCostCode)
    {
        this.idCostCode = idCostCode;
    }

}
