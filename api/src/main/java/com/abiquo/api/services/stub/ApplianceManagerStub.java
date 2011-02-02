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

package com.abiquo.api.services.stub;

import com.abiquo.appliancemanager.transport.EnterpriseRepositoryDto;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceDto;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceStatusDto;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceStatusListDto;
import com.abiquo.appliancemanager.transport.OVFPackageInstancesDto;
import com.abiquo.server.core.enterprise.EnterprisesDto;

public interface ApplianceManagerStub
{
    public OVFPackageInstanceStatusListDto getOVFPackagInstances(final String serviceUri,
        final String idEnterprise);

    /**
     * TODO: Could we change this name to getOVFPackageInstanceStatus ??
     * 
     * @param serviceUri
     * @param idEnterprise
     * @param ovfUrl
     * @return
     */
    public OVFPackageInstanceStatusDto getOVFPackageStatus(final String serviceUri,
        final String idEnterprise, final String ovfUrl);

    /**
     * Downloads and installs a OVFPackage, building an OVFPackageInstance available for this DC and
     * Enterprise
     * 
     * @param serviceUri
     * @param idEnterprise
     * @param ovfUrl
     * @return information on status of this OVFPackage installation
     */
    public OVFPackageInstanceStatusDto installOVFPackage(final String serviceUri,
        final String idEnterprise, final String ovfUrl);

    /**
     * Lists all OVFPackageInstances available for this DC
     * 
     * @param serviceUri
     * @param idEnterprise
     * @return
     */
    public OVFPackageInstancesDto getOVFPackageInstancesList(final String serviceUri,
        final String idEnterprise);

    /**
     * Finds OVFPackageInstance a given enterprise by ovfUrl
     * 
     * @param serviceUri
     * @param idEnterprise
     * @param ovfUrl
     * @return
     */
    public OVFPackageInstanceDto getOVFPackageInstance(final String serviceUri,
        final String idEnterprise, final String ovfUrl);

    /**
     * Builds a Bundle for an OVFPackageInstance identified by 'ovfUrl' at a given enterprise
     * 
     * @param serviceUri
     * @param idEnterprise
     * @param ovfUrl
     * @return
     */
    public OVFPackageInstanceDto bundleOVFPackage(final String serviceUri,
        final String idEnterprise, final String ovfUrl);

    /**
     * Lists all Enterprise repositories, one for each enterprise using this DC
     * 
     * @param serviceUri
     * @param idEnterprise
     * @param ovfUrl
     * @return
     */
    public EnterprisesDto getEnterpriseRepositoriesByDC(final String uri);

    /**
     * @param uri
     * @param idEnterprise
     * @return
     */
    public EnterpriseRepositoryDto getEnterpriseRepository(String uri, String idEnterprise);

    /**
     * Deletes OVFPackageInstance from this Enterprise. Should also delete bundled images?
     * 
     * @param uri
     * @param idEnterprise
     * @param ovfUrl
     * @return OVFPackageInstanceStatus.NOT_DOWNLOAD if OK
     */
    public OVFPackageInstanceStatusDto deleteOVFPackageInstance(String uri, String idEnterprise,
        String ovfUrl);

}
