package com.abiquo.api.services.appslibrary;

import static com.abiquo.tracer.Enterprise.enterprise;
import static com.abiquo.tracer.Platform.platform;
import static com.abiquo.tracer.User.user;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.services.DefaultApiService;
import com.abiquo.api.services.InfrastructureService;
import com.abiquo.api.services.appslibrary.event.OVFPackageInstanceToVirtualImage;
import com.abiquo.appliancemanager.client.ApplianceManagerResourceStubImpl;
import com.abiquo.appliancemanager.client.ApplianceManagerResourceStubImpl.ApplianceManagerStubException;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceDto;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceStatusDto;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceStatusListDto;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceStatusType;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.server.core.cloud.VirtualImage;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.Repository;
import com.abiquo.tracer.ComponentType;
import com.abiquo.tracer.Platform;
import com.abiquo.tracer.SeverityType;
import com.abiquo.tracer.UserInfo;
import com.abiquo.tracer.client.TracerFactory;

@Service
public class DatacenterRepositoryService extends DefaultApiService
{
    public static final Logger logger = LoggerFactory.getLogger(DatacenterRepositoryService.class);

    @Autowired
    private InfrastructureService infService;

    @Autowired
    private OVFPackageInstanceToVirtualImage toimage;

    /**
     * Request the DOWNLOAD {@link OVFPackageInstanceDto} available in the ApplianceManager and
     * update the {@link VirtualImage} repository with new images.
     */
    public void synchronizeDatacenterRepository(final Datacenter datacenter,
        final Enterprise enterprise)
    {
        ApplianceManagerResourceStubImpl amStub = getApplianceManagerClient(datacenter.getId());

        Repository repo = checkRepositoryLocation(datacenter, amStub);
        refreshRepository(enterprise.getId(), repo, amStub);

    }

    private ApplianceManagerResourceStubImpl getApplianceManagerClient(final Integer dcId)
    {
        final String amUri =
            infService.getRemoteService(dcId, RemoteServiceType.APPLIANCE_MANAGER).getUri();
        ApplianceManagerResourceStubImpl amStub = new ApplianceManagerResourceStubImpl(amUri);

        try
        {
            amStub.checkService();
        }
        catch (ApplianceManagerStubException e)
        {
            logger.error("ApplianceManager configuration error", e);

            // A user named "SYSTEM" is created
            UserInfo ui = new UserInfo("SYSTEM");
            Platform platform =
                platform("abicloud").enterprise(enterprise("abiCloud").user(user("SYSTEM")));
            TracerFactory.getTracer().log(SeverityType.CRITICAL, ComponentType.APPLIANCE_MANAGER,
                com.abiquo.tracer.EventType.UNKNOWN, e.getLocalizedMessage(), ui, platform);

            addConflictErrors(APIError.VIMAGE_AM_DOWN);
            flushErrors();
        }

        return amStub;
    }

    private Repository checkRepositoryLocation(final Datacenter datacenter,
        ApplianceManagerResourceStubImpl amStub)
    {
        final String repositoryLocation = amStub.getAMConfiguration().getRepositoryLocation();

        final Repository repo = infService.getRepository(datacenter);

        if (!repo.getUrl().equalsIgnoreCase(repositoryLocation))
        {
            addConflictErrors(APIError.VIMAGE_REPOSITORY_CHANGED);
            flushErrors();
        }

        return repo;
    }

    private void refreshRepository(final Integer idEnterprise, final Repository repo,
        final ApplianceManagerResourceStubImpl amStub)
    {

        List<OVFPackageInstanceDto> disks = new LinkedList<OVFPackageInstanceDto>();
        for (String ovfid : getAvailableOVFPackageInstance(idEnterprise, amStub))
        {
            try
            {
                OVFPackageInstanceDto packageInstance =
                    amStub.getOVFPackageInstance(String.valueOf(idEnterprise), ovfid);
                disks.add(packageInstance);
            }
            catch (ApplianceManagerStubException e)
            {
                logger.error("Can not initialize VirtualImage from ovf [{}]", ovfid);
            }
        }

        List<VirtualImage> insertedImages = toimage.insertVirtualImages(disks, repo);

        // Process existing images
        processExistingImages(insertedImages);
    }

    /**
     * Returns OVF ids of the DOWNLOADED {@link OVFPackageInstanceDto} in the enterprise repository
     */
    private List<String> getAvailableOVFPackageInstance(final Integer idEnterprise,
        ApplianceManagerResourceStubImpl amStub)
    {
        List<String> ovfids = new LinkedList<String>();

        try
        {
            OVFPackageInstanceStatusListDto list =
                amStub.getOVFPackagInstanceStatusList(idEnterprise.toString());

            for (OVFPackageInstanceStatusDto status : list.getOvfPackageInstancesStatus())
            {
                if (status.getOvfPackageStatus() == OVFPackageInstanceStatusType.DOWNLOAD)
                {
                    ovfids.add(status.getOvfId());
                }
            }
        }
        catch (ApplianceManagerStubException e)
        {
            addConflictErrors(APIError.VIMAGE_SYNCH_DC_REPO);
            flushErrors();
        }

        return ovfids;
    }

    /**
     * Post process AM existing images.
     * <p>
     * This method may be overriden in enterprise version to manage virtual image conversions.
     * 
     * @param images The existing images.
     */
    protected void processExistingImages(final Collection<VirtualImage> images)
    {
        // Do nothing
    }

}
