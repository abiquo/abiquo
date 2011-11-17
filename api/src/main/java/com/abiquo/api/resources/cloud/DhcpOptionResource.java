package com.abiquo.api.resources.cloud;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

import org.apache.wink.common.annotations.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.resources.AbstractResource;
import com.abiquo.api.services.NetworkService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.model.util.ModelTransformer;
import com.abiquo.server.core.infrastructure.network.DhcpOption;
import com.abiquo.server.core.infrastructure.network.DhcpOptionDto;

@Parent(DhcpOptionsResource.class)
@Path(DhcpOptionResource.DHCP_OPTION_PARAM)
@Controller
public class DhcpOptionResource extends AbstractResource
{
    public static final String DHCP_OPTION = "dhcpoption";

    public static final String DHCP_OPTION_PARAM = "{" + DHCP_OPTION + "}";

    @Autowired
    private NetworkService service;

    @GET
    public DhcpOptionDto getDhcpOption(@PathParam(DHCP_OPTION) final Integer id,
        @Context final IRESTBuilder restBuilder) throws Exception
    {

        DhcpOption option = service.getDhcpOption(id);

        return createTransferObject(option, restBuilder);
    }

    public static DhcpOptionDto createTransferObject(final DhcpOption systemProperty,
        final IRESTBuilder builder) throws Exception
    {
        DhcpOptionDto dto =
            ModelTransformer.transportFromPersistence(DhcpOptionDto.class, systemProperty);
        dto.addLinks(builder.buildDhcpOptionLink(dto));
        return dto;
    }
}
