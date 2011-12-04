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

package com.abiquo.api.services.appslibrary;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import javax.persistence.EntityManager;

import org.dmtf.schemas.ovf.envelope._1.EnvelopeType;
import org.dmtf.schemas.ovf.envelope._1.FileType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.services.EnterpriseService;
import com.abiquo.appliancemanager.client.ApplianceManagerResourceStubImpl;
import com.abiquo.appliancemanager.repositoryspace.OVFDescription;
import com.abiquo.appliancemanager.transport.TemplateStateDto;
import com.abiquo.model.enumerator.DiskFormatType;
import com.abiquo.ovfmanager.ovf.xml.OVFSerializer;
import com.abiquo.server.core.appslibrary.AppsLibraryRep;
import com.abiquo.server.core.appslibrary.Category;
import com.abiquo.server.core.appslibrary.Icon;
import com.abiquo.server.core.appslibrary.TemplateDefinition;
import com.abiquo.server.core.appslibrary.TemplateDefinitionRep;
import com.abiquo.server.core.enterprise.Enterprise;

@Service
public class TemplateDefinitionService extends DefaultApiServiceWithApplianceManagerClient
{
    private final static Logger LOGGER = LoggerFactory.getLogger(TemplateDefinitionService.class);

    @Autowired
    private TemplateDefinitionRep repo;

    @Autowired
    private AppsLibraryRep appslibraryRep;

    public TemplateDefinitionService()
    {

    }

    public TemplateDefinitionService(final EntityManager em)
    {
        repo = new TemplateDefinitionRep(em);
        entService = new EnterpriseService(em);
        appslibraryRep = new AppsLibraryRep(em);
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public List<TemplateDefinition> getTemplateDefinitionsByEnterprise(final Integer idEnterprise)
    {
        return repo.getTemplateDefinitionsByEnterprise(idEnterprise);
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public TemplateDefinition getTemplateDefinition(final Integer id)
    {
        TemplateDefinition ovfpackage = repo.getTemplateDefinition(id);
        if (ovfpackage == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_OVF_PACKAGE);
            flushErrors();
        }
        return ovfpackage;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public TemplateDefinition addTemplateDefinition(final TemplateDefinition templateDef, final Integer idEnterprise)
    {
        Enterprise ent = entService.getEnterprise(idEnterprise);
        return repo.addTemplateDefinition(templateDef, ent);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public TemplateDefinition updateTemplateDefinition(final Integer templateDefId, final TemplateDefinition templateDef,
        final Integer idEnterprise)
    {
        Enterprise enterprise = entService.getEnterprise(idEnterprise);
        return repo.updateTemplateDefinition(templateDefId, templateDef, enterprise);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void removeTemplateDefinition(final Integer id)
    {
        TemplateDefinition templateDef = repo.getTemplateDefinition(id);
        if (templateDef == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_OVF_PACKAGE);
            flushErrors();
        }
        repo.removeTemplateDefinition(id);
    }

    /** #################### ApplianceManager communications #################### */
    /** #################### */

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public TemplateStateDto getTemplateState(final Integer id,
        final Integer datacenterId, final Integer enterpriseId)
    {
        checkEnterpriseAndDatacenter(enterpriseId, datacenterId);

        final String ovfUrl = getTemplateDefinition(id).getUrl();
        final ApplianceManagerResourceStubImpl amClient = getApplianceManagerClient(datacenterId);

        return amClient.getTemplateStatus(String.valueOf(enterpriseId), ovfUrl);
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public void installTemplateDefinition(final Integer id, final Integer datacenterId,
        final Integer enterpriseId)
    {
        checkEnterpriseAndDatacenter(enterpriseId, datacenterId);

        final String ovfUrl = getTemplateDefinition(id).getUrl();
        final ApplianceManagerResourceStubImpl amClient = getApplianceManagerClient(datacenterId);

        // checks the repository is writable
        amClient.getRepository(String.valueOf(enterpriseId), true);

        amClient.installTemplateDefinition(String.valueOf(enterpriseId), ovfUrl);
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public void uninstallTemplateDefinition(final Integer id, final Integer datacenterId,
        final Integer enterpriseId)
    {
        checkEnterpriseAndDatacenter(enterpriseId, datacenterId);

        final String ovfUrl = getTemplateDefinition(id).getUrl();
        final ApplianceManagerResourceStubImpl amClient = getApplianceManagerClient(datacenterId);

        amClient.delete(String.valueOf(enterpriseId), ovfUrl);
    }

    /** #################### ovfindex.xml #################### */
    /** #################### */

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public TemplateDefinition transformToTemplateDefinition(final OVFDescription descr,
        final String baseRepositorySpaceURL)
    {

        String packageUrl =
            descr.getOVFFile().startsWith("http://") ? descr.getOVFFile() : baseRepositorySpaceURL
                + "/" + descr.getOVFFile(); // TODO check not double //

        String name = descr.getProduct().getValue();
        String description = descr.getInfo().getValue();

        // TODO data truncation
        if (description.length() > 255)
        {
            description = description.substring(0, 254);
        }

        TemplateDefinition pack = new TemplateDefinition();
        pack.setDescription(description);
        pack.setName(name);
        pack.setProductName(name);

        /**
         * TODO product verison .... url ...
         */

        pack.setUrl(packageUrl);

        if (descr.getIcon() != null && descr.getIcon().size() == 1)
        {
            String iconPath = descr.getIcon().get(0).getFileRef();
            // TODO start with http://

            Icon icon = appslibraryRep.findByIconPathOrCreateNew(iconPath);
            pack.setIcon(icon);
        }

        DiskFormatType format = findByDiskFormatNameOrUnknow(descr.getDiskFormat());
        pack.setType(format);

        Category category = appslibraryRep.findByCategoryNameOrCreateNew(descr.getOVFCategories());
        pack.setCategory(category);

        Long diskSizeL = null;
        final String diskSize = descr.getDiskSize();
        if (diskSize != null && !diskSize.isEmpty())
        {
            diskSizeL = Long.parseLong(diskSize);
        }
        else
        {
            diskSizeL = getDiskFileSizeMbFromOvfId(packageUrl);
        }

        pack.setDiskFileSize(diskSizeL);

        return pack;
    }

    private Long getDiskFileSizeMbFromOvfId(final String ovfid)
    {
        InputStream isovf = null;
        try
        {
            URL ovfurl = new URL(ovfid);
            isovf = ovfurl.openStream();
            EnvelopeType envelope = OVFSerializer.getInstance().readXMLEnvelope(isovf);

            Long accSize = 0l;

            for (FileType file : envelope.getReferences().getFile())
            {
                accSize += file.getSize().longValue();
            }

            return accSize / (1024 * 1024);
        }
        catch (Exception e)
        {
            LOGGER.debug("Cannot retrieve the size of OvfPackage with id " + ovfid
                + " setting size 0");
            return 0L;
        }
        finally
        {
            if (isovf != null)
            {
                try
                {
                    isovf.close();
                }
                catch (IOException e)
                {
                    //
                }
            }
        }// finally
    }

    private DiskFormatType findByDiskFormatNameOrUnknow(final String diskFormatName)
    {
        DiskFormatType format;
        if (diskFormatName == null)
        {
            format = DiskFormatType.UNKNOWN;
        }
        else if ("STREAM_OPTIMIZED".equalsIgnoreCase(diskFormatName)) // FIXME
        {
            format = DiskFormatType.VMDK_STREAM_OPTIMIZED;
        }
        else
        {
            try
            {
                format = DiskFormatType.valueOf(diskFormatName);
            }
            catch (Exception e)
            {
                format = DiskFormatType.UNKNOWN;
            }
        }

        return format;
    }

}
