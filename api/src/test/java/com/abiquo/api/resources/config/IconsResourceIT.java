package com.abiquo.api.resources.config;

import static com.abiquo.api.common.UriTestResolver.resolveIconsURI;
import static com.abiquo.testng.TestConfig.ALL_INTEGRATION_TESTS;
import static com.abiquo.testng.TestConfig.BASIC_INTEGRATION_TESTS;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.apache.wink.client.ClientResponse;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import com.abiquo.api.resources.AbstractJpaGeneratorIT;
import com.abiquo.server.core.config.Icon;
import com.abiquo.server.core.config.IconDto;
import com.abiquo.server.core.config.IconsDto;

public class IconsResourceIT extends AbstractJpaGeneratorIT
{

    private String validURI;

    @Test(groups = {ALL_INTEGRATION_TESTS})
    public void addIcon()
    {
        IconDto iconDto = new IconDto();
        iconDto.setName("newName");
        iconDto.setPath("http://newPath.com/newlogo.jpg");

        ClientResponse response = post(resolveIconsURI(), iconDto);

        IconDto result = response.getEntity(IconDto.class);
        assertEquals(result.getPath(), "http://newPath.com/newlogo.jpg");
    }

    @Test(groups = {BASIC_INTEGRATION_TESTS})
    public void findIconByPath()
    {
        Icon icon = iconGenerator.createUniqueInstance();
        setup(icon);

        validURI = resolveIconsURI();// + "?path=" + icon.getPath();

        // StringBuilder URI = new StringBuilder(validURI + "?path=" + icon.getPath());

        ClientResponse response = get(validURI);

        IconsDto iconsDto = response.getEntity(IconsDto.class);
        assertNotNull(iconsDto);
        for (IconDto iconDto : iconsDto.getCollection())
        {
            assertEquals(iconDto.getId(), icon.getId());
        }
    }

    @AfterMethod
    @Test(groups = {BASIC_INTEGRATION_TESTS})
    public void tearDown()
    {
        super.tearDown();
    }
}
