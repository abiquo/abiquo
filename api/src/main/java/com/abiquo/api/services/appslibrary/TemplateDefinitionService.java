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
import com.abiquo.api.services.DefaultApiService;
import com.abiquo.api.services.EnterpriseService;
import com.abiquo.api.services.stub.AMServiceStub;
import com.abiquo.appliancemanager.client.ExternalHttpConnection;
import com.abiquo.appliancemanager.repositoryspace.OVFDescription;
import com.abiquo.appliancemanager.transport.TemplateStateDto;
import com.abiquo.model.enumerator.DiskFormatType;
import com.abiquo.ovfmanager.ovf.xml.OVFSerializer;
import com.abiquo.server.core.appslibrary.AppsLibraryDAO;
import com.abiquo.server.core.appslibrary.AppsLibraryRep;
import com.abiquo.server.core.appslibrary.Category;
import com.abiquo.server.core.appslibrary.TemplateDefinition;
import com.abiquo.server.core.appslibrary.TemplateDefinitionRep;
import com.abiquo.server.core.enterprise.Enterprise;

@Service
public class TemplateDefinitionService extends DefaultApiService
{
    private final static Logger LOGGER = LoggerFactory.getLogger(TemplateDefinitionService.class);

    @Autowired
    private TemplateDefinitionRep repo;

    @Autowired
    private AppsLibraryRep appslibraryRep;

    @Autowired
    private EnterpriseService enterpriseService;

    @Autowired
    private AMServiceStub amService;

    @Autowired
    protected AppsLibraryDAO appsLibraryDao;

    public TemplateDefinitionService()
    {

    }

    public TemplateDefinitionService(final EntityManager em)
    {
        repo = new TemplateDefinitionRep(em);
        appslibraryRep = new AppsLibraryRep(em);
        enterpriseService = new EnterpriseService(em);
        appsLibraryDao = new AppsLibraryDAO(em);
        amService = new AMServiceStub();
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public List<TemplateDefinition> getTemplateDefinitionsByEnterprise(final Integer idEnterprise)
    {
        enterpriseService.getEnterprise(idEnterprise); // check can view
        return repo.getTemplateDefinitionsByEnterprise(idEnterprise);
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public TemplateDefinition getTemplateDefinition(final Integer id, final Integer idEnterprise)
    {
        enterpriseService.getEnterprise(idEnterprise); // check can view
        TemplateDefinition ovfpackage = repo.getTemplateDefinition(id);
        if (ovfpackage == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_OVF_PACKAGE);
            flushErrors();
        }
        return ovfpackage;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public TemplateDefinition addTemplateDefinition(final TemplateDefinition templateDef,
        final Integer idEnterprise)
    {
        Enterprise ent = enterpriseService.getEnterprise(idEnterprise); // check can view
        templateDef.setAppsLibrary(appsLibraryDao.findByEnterpriseOrInitialize(ent));

        validate(templateDef);

        return repo.addTemplateDefinition(templateDef, ent);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public TemplateDefinition updateTemplateDefinition(final Integer templateDefId,
        final TemplateDefinition templateDef, final Integer idEnterprise)
    {
        Enterprise enterprise = enterpriseService.getEnterprise(idEnterprise); // check can view
        templateDef.setAppsLibrary(appsLibraryDao.findByEnterpriseOrInitialize(enterprise));

        validate(templateDef);

        return repo.updateTemplateDefinition(templateDefId, templateDef, enterprise);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void removeTemplateDefinition(final Integer id, final Integer idEnterprise)
    {
        enterpriseService.getEnterprise(idEnterprise); // check can view
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
    public TemplateStateDto getTemplateState(final Integer id, final Integer datacenterId,
        final Integer enterpriseId)
    {
        final String ovfUrl = getTemplateDefinition(id, enterpriseId).getUrl();
        return amService.getTemplateState(datacenterId, enterpriseId, ovfUrl);
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public void installTemplateDefinition(final Integer id, final Integer datacenterId,
        final Integer enterpriseId)
    {
        final String ovfUrl = getTemplateDefinition(id, enterpriseId).getUrl();
        amService.install(datacenterId, enterpriseId, ovfUrl);
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public void uninstallTemplateDefinition(final Integer id, final Integer datacenterId,
        final Integer enterpriseId)
    {
        final String ovfUrl = getTemplateDefinition(id, enterpriseId).getUrl();
        amService.delete(datacenterId, enterpriseId, ovfUrl);
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

        TemplateDefinition pack = new TemplateDefinition();
        pack.setName(name);
        // TODO data truncation
        pack.setProductName(name.length() > 45 ? name.substring(0, 44) : name);
        pack.setDescription(description.length() > 255 ? description.substring(0, 254)
            : description);

        /**
         * TODO product verison .... url ...
         */

        pack.setUrl(packageUrl);

        if (descr.getIcon() != null && descr.getIcon().size() == 1)
        {
            String iconPath = descr.getIcon().get(0).getFileRef();
            pack.setIconUrl(iconPath);
            // TODO start with http://
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
        ExternalHttpConnection connection = new ExternalHttpConnection();
        try
        {
            isovf = connection.openConnection(ovfid);

            EnvelopeType envelope = OVFSerializer.getInstance().readXMLEnvelope(isovf);

            Long accSize = 0l;

            for (FileType file : envelope.getReferences().getFile())
            {
                if (file.getSize() != null)
                {
                    accSize += file.getSize().longValue();
                }
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
            connection.releaseConnection();

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
