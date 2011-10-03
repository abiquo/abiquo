package com.abiquo.api.resources.appslibrary;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

import org.apache.wink.common.annotations.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.resources.AbstractResource;
import com.abiquo.api.resources.EnterpriseResource;
import com.abiquo.api.services.InfrastructureService;
import com.abiquo.api.services.appslibrary.VirtualImageService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.model.util.ModelTransformer;
import com.abiquo.server.core.cloud.VirtualImage;
import com.abiquo.server.core.cloud.VirtualImageDto;

@Parent(VirtualImagesResource.class)
@Path(VirtualImageResource.VIRTUAL_IMAGE_PATH)
@Controller
public class VirtualImageResource extends AbstractResource
{

    public final static String VIRTUAL_IMAGE_PATH = "virtualimage";

    @Autowired
    private VirtualImageService vimageService;

    @Autowired
    private InfrastructureService infService;

    /**
     * Return the virtual image if exists.
     */
    @GET
    public VirtualImageDto getVirtualImage(
        @PathParam(EnterpriseResource.ENTERPRISE) final Integer enterpId,
        @PathParam(DatacenterRepositoryResource.REMOTE_REPOSITORY_PATH) final Integer dcId,
        @PathParam(VirtualImageResource.VIRTUAL_IMAGE_PATH) final Integer vimageId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        // TODO check enterprise can use the datacenter
        VirtualImage vimage = vimageService.getVirtualImage(vimageId);

        final String amUri =
            infService.getRemoteService(dcId, RemoteServiceType.APPLIANCE_MANAGER).getUri();

        return createTransferObject(vimage, enterpId, dcId, amUri, restBuilder);
    }

    /**
     * Return the {@link VirtualImageDto}o object from the POJO {@link VirtualImage}
     */
    protected static VirtualImageDto createTransferObject(final VirtualImage vimage,
        final Integer enterpId, final Integer dcId, final String amUri, final IRESTBuilder builder)
        throws Exception
    {
        VirtualImageDto dto =
            ModelTransformer.transportFromPersistence(VirtualImageDto.class, vimage);

        dto = addLinks(builder, dto, enterpId, dcId, vimage.getId(), amUri);

        return dto;
    }

    private static VirtualImageDto addLinks(final IRESTBuilder builder, VirtualImageDto dto,
        final Integer enterpriseId, final Integer dcId, final Integer vimageId, final String amUri)
    {
        dto.setLinks(builder.buildVirtualImageLinks(enterpriseId, dcId, vimageId));

        dto.addLink(ovfpackageinstanceUri(amUri, vimageId, dto.getOvfid()));

        return dto;
    }

    private static RESTLink ovfpackageinstanceUri(final String amUri, final Integer enterpriseId,
        String ovfid)
    {

        if (ovfid.startsWith("http://"))
        {
            ovfid = ovfid.substring("http://".length());
        }

        final String href =
            String.format("%s/erepo/%s/ovf/%s", amUri, enterpriseId.toString(), ovfid);
        return new RESTLink("applianceManagerOVFPackageInstanceUri", href);
    }
}
