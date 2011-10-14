package com.abiquo.api.resources.config;

import org.apache.wink.client.ClientResponse;
import org.testng.annotations.Test;

import com.abiquo.api.resources.AbstractJpaGeneratorIT;
import com.abiquo.server.core.config.Icon;
import com.abiquo.server.core.config.IconDto;

import static com.abiquo.testng.TestConfig.ALL_INTEGRATION_TESTS;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static com.abiquo.api.common.UriTestResolver.resolveIconURI;

public class IconResourceIT extends AbstractJpaGeneratorIT
{
    private String validURI;

    @Test(groups = {ALL_INTEGRATION_TESTS})
    public void getIcon()
    {
        Icon icon = iconGenerator.createUniqueInstance();
        setup(icon);

        validURI = resolveIconURI(icon.getId());

        ClientResponse response = get(validURI);

        IconDto iconDto = response.getEntity(IconDto.class);
        assertNotNull(iconDto);
        assertEquals(icon.getPath(), iconDto.getPath());
    }

    @Test(groups = {ALL_INTEGRATION_TESTS})
    public void updateIcon()
    {
        Icon icon = iconGenerator.createUniqueInstance();
        setup(icon);

        validURI = resolveIconURI(icon.getId());

        IconDto iconDto = new IconDto();
        iconDto.setId(icon.getId());
        iconDto.setName("newName");
        iconDto.setPath("http://newPath.com/image.jpg");

        ClientResponse response = put(validURI, iconDto);

        response = get(validURI);

        IconDto newiconDto = response.getEntity(IconDto.class);
        assertEquals(newiconDto.getPath(), "http://newPath.com/image.jpg");

    }

}
