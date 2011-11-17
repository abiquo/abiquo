package com.abiquo.api.resources.cloud;

import static com.abiquo.api.resources.cloud.DhcpOptionResource.createTransferObject;

import java.util.Collection;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

import org.apache.wink.common.annotations.Workspace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.resources.AbstractResource;
import com.abiquo.api.services.NetworkService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.model.util.ModelTransformer;
import com.abiquo.server.core.infrastructure.network.DhcpOption;
import com.abiquo.server.core.infrastructure.network.DhcpOptionDto;
import com.abiquo.server.core.infrastructure.network.DhcpOptionsDto;

//@Parent(PublicNetworkResource.class)
@Path(DhcpOptionsResource.DHCP_OPTIONS_PATH)
@Controller
@Workspace(workspaceTitle = "Abiquo administration workspace", collectionTitle = "Dhcpoptions")
public class DhcpOptionsResource extends AbstractResource
{
    public static final String DHCP_OPTIONS_PATH = "admin/dhcpoptions";

    @Autowired
    private NetworkService service;

    @GET
    public DhcpOptionsDto getDhcpOptions(@Context final IRESTBuilder restBuilder) throws Exception
    {
        Collection<DhcpOption> options = service.findAllDhcpOptions();

        DhcpOptionsDto dhcpOptions = new DhcpOptionsDto();
        for (DhcpOption d : options)
        {
            dhcpOptions.add(createTransferObject(d, restBuilder));
        }

        return dhcpOptions;
    }

    @POST
    public DhcpOptionDto post(final DhcpOptionDto dto, @Context final IRESTBuilder restBuilder)
        throws Exception
    {

        DhcpOption opt = service.addDhcpOption(dto);

        return createTransferObject(opt, restBuilder);
    }

    public static DhcpOptionsDto createAdminTransferObjects(final Collection<DhcpOption> opts,
        final IRESTBuilder restBuilder) throws Exception
    {
        DhcpOptionsDto dhcpOptions = new DhcpOptionsDto();
        for (DhcpOption dhcpOption : opts)
        {
            DhcpOptionDto dto =
                ModelTransformer.transportFromPersistence(DhcpOptionDto.class, dhcpOption);

            dhcpOptions.add(dto);
        }

        return dhcpOptions;
    }

}
