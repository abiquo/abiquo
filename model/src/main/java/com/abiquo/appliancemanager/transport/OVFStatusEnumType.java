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

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

import com.abiquo.server.core.appslibrary.TemplateDefinition;

/**
 * status of a {@link TemplateDefinition} in a {@link EnterpriseRepositoriesDto}.
 */
@XmlType(name = "ovfInstanceStatus")
@XmlEnum
public enum OVFStatusEnumType
{
    /** ovf package not found in the enterprise repository */
    NOT_DOWNLOAD,
    /** currently being created **/
    DOWNLOADING,
    /** ovf package installed in the current enterprise repository. */
    DOWNLOAD,
    /** error during the creation */
    ERROR;

    public String value()
    {
        return name();
    }

    public static OVFStatusEnumType fromValue(String v)
    {
        return valueOf(v);
    }
}
