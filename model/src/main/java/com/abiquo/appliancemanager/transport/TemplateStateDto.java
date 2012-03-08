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

package com.abiquo.appliancemanager.transport;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.abiquo.model.transport.SingleResourceTransportDto;
import com.abiquo.server.core.appslibrary.TemplateDefinition;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.DatacenterDto;

/**
 * The materialization state of an {@link TemplateDefinition} of a given {@link Datacenter} and
 * {@link Enterprise}
 */
@XmlType
@XmlRootElement(name = "templateState")
public class TemplateStateDto extends SingleResourceTransportDto
{
    private static final long serialVersionUID = -4115162963051770344L;

    public static final String BASE_MEDIA_TYPE = "application/vnd.abiquo.templatestate+xml";
    public static final String MEDIA_TYPE = BASE_MEDIA_TYPE + "; version=" + API_VERSION;

    /**
     * Original location of the {@link TemplateDefinition}. Identify the entity combined with the
     * {@link Enterprise} identifier (id of the ApplianceManager EnterpriseRepository). Datacenter
     * identifier is implicit in the ApplianceManager context.
     */
    protected String ovfId;

    /** Current status in the enterprise repository */

    // @XmlElement(name = "status")
    protected TemplateStatusEnumType status;

    /**
     * If status == DOWNLOADING reports the current creation progress (based on bytes lefts to read
     * from the remote repository)
     */
    protected Double downloadingProgress;

    /**
     * If status == ERROR reports the creation error cause (content of ''deploy.error'' file)
     */
    protected String errorCause;

    protected String masterOvf;

    public String getOvfId()
    {
        return ovfId;
    }

    public void setOvfId(final String ovfId)
    {
        this.ovfId = ovfId;
    }

    public TemplateStatusEnumType getStatus()
    {
        return status;
    }

    public void setStatus(final TemplateStatusEnumType status)
    {
        this.status = status;
    }

    public Double getDownloadingProgress()
    {
        return downloadingProgress;
    }

    public void setDownloadingProgress(final Double downloadingProgress)
    {
        this.downloadingProgress = downloadingProgress;
    }

    public String getErrorCause()
    {
        return errorCause;
    }

    public void setErrorCause(final String errorCause)
    {
        this.errorCause = errorCause;
    }

    public String getMasterOvf()
    {
        return masterOvf;
    }

    public void setMasterOvf(final String masterOvf)
    {
        this.masterOvf = masterOvf;
    }
    
    @Override
    public String getMediaType()
    {
        return MEDIA_TYPE;
    }
    
    @Override
    public String getBaseMediaType()
    {
        return BASE_MEDIA_TYPE;
    }

}
