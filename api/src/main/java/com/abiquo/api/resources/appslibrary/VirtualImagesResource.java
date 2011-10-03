package com.abiquo.api.resources.appslibrary;

import static com.abiquo.api.resources.appslibrary.VirtualImageResource.createTransferObject;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

import org.apache.wink.common.annotations.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.resources.EnterpriseResource;
import com.abiquo.api.services.InfrastructureService;
import com.abiquo.api.services.appslibrary.VirtualImageService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.server.core.cloud.VirtualImage;
import com.abiquo.server.core.cloud.VirtualImagesDto;
import com.abiquo.api.resources.AbstractResource;

@Parent(DatacenterRepositoryResource.class)
@Path(VirtualImagesResource.VIRTUAL_IMAGES_PATH)
@Controller
public class VirtualImagesResource extends AbstractResource
{

    public final static String VIRTUAL_IMAGES_PATH = "virtualimages";

    @Autowired
    private VirtualImageService service;

    @Autowired
    private InfrastructureService infService;

    @GET
    public VirtualImagesDto getVirtualImages(
        @PathParam(EnterpriseResource.ENTERPRISE) final Integer enterpId,
        @PathParam(DatacenterRepositoryResource.REMOTE_REPOSITORY_PATH) final Integer dcId,
        @Context IRESTBuilder restBuilder) throws Exception
    {
        VirtualImagesDto reposDto = new VirtualImagesDto();

        final String amUri =
            infService.getRemoteService(dcId, RemoteServiceType.APPLIANCE_MANAGER).getUri();

        List<VirtualImage> all = service.getVirtualImages(enterpId, dcId);

        if (all != null && !all.isEmpty())
        {
            for (VirtualImage vimage : all)
            {
                reposDto.add(createTransferObject(vimage, enterpId, dcId, amUri, restBuilder));
            }
        }

        return reposDto;
    }
}
