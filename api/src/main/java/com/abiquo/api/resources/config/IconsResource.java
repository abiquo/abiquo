package com.abiquo.api.resources.config;

import java.util.ArrayList;
import java.util.Collection;

import static com.abiquo.api.resources.config.IconResource.createTransferObject;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.apache.wink.common.annotations.Workspace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.resources.AbstractResource;
import com.abiquo.api.services.config.IconService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.server.core.config.Icon;
import com.abiquo.server.core.config.IconDto;
import com.abiquo.server.core.config.IconsDto;

@Path(IconsResource.ICONS_PATH)
@Controller
@Workspace(workspaceTitle = "Virtual Images Icons", collectionTitle = "Icons")
public class IconsResource extends AbstractResource
{

    public static final String ICONS_PATH = "config/icons";

    public static final String PATH = "path";

    @Autowired
    private IconService service;

    @GET
    public IconsDto getIcons(@QueryParam(PATH) final String path,
        @Context final IRESTBuilder restBuilder)
    {
        Collection<Icon> icons = new ArrayList<Icon>();
        if (path == null)
        {
            icons = service.getIcons();
        }
        else
        {
            Icon icon = service.getIconByPath(path);
            icons.add(icon);
        }
        IconsDto iconsDto = new IconsDto();
        for (Icon icon : icons)
        {
            IconDto icondto = createTransferObject(icon, restBuilder);
            iconsDto.add(icondto);
        }
        return iconsDto;

    }

    @POST
    public IconDto addIcon(final IconDto iconDto, @Context final IRESTBuilder restBuilder)
    {
        Icon icon = service.addIcon(iconDto, restBuilder);
        return createTransferObject(icon, restBuilder);

    }
}
