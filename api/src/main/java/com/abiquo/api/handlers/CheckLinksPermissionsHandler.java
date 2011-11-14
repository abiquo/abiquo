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

package com.abiquo.api.handlers;

import java.util.Collections;
import java.util.List;

import org.apache.wink.server.handlers.AbstractHandler;
import org.apache.wink.server.handlers.MessageContext;

import com.abiquo.api.spring.BeanLoader;
import com.abiquo.api.spring.security.URLAuthenticator;
import com.abiquo.model.enumerator.LinkOrder;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.model.transport.SingleResourceTransportDto;
import com.abiquo.model.transport.WrapperDto;
import com.abiquo.model.util.CompositeComparator;

public class CheckLinksPermissionsHandler extends AbstractHandler
{

    private URLAuthenticator urlAuthenticator;

    @SuppressWarnings("unchecked")
    @Override
    public void handleResponse(final MessageContext msgContext) throws Throwable
    {
        if (msgContext.getResponseEntity() != null
            && msgContext.getResponseEntity() instanceof SingleResourceTransportDto)
        {
            SingleResourceTransportDto srtDto =
                (SingleResourceTransportDto) msgContext.getResponseEntity();
            if (srtDto != null)
            {
                if (srtDto instanceof WrapperDto< ? >)
                {
                    for (Object obj : ((WrapperDto< ? >) srtDto).getCollection())
                    {
                        if (obj instanceof SingleResourceTransportDto)
                        {
                            SingleResourceTransportDto srt = ((SingleResourceTransportDto) obj);
                            srt.setLinks(checkLinks(srt.getLinks()));
                        }
                    }
                }
                else if (srtDto.getLinks() != null)
                {
                    srtDto.setLinks(checkLinks(srtDto.getLinks()));
                }
            }
        }
        super.handleResponse(msgContext);
    }

    @SuppressWarnings("unchecked")
    protected List<RESTLink> checkLinks(final List<RESTLink> links)
    {
        List<RESTLink> authLinks = null;

        if (links != null)
        {
            authLinks = getUrlAuthenticator().checkAuthLinks(links);
            Collections.sort(authLinks,
                CompositeComparator.build(LinkOrder.BY_REL, LinkOrder.BY_TITLE));
        }

        return authLinks;
    }

    protected URLAuthenticator getUrlAuthenticator()
    {
        if (urlAuthenticator == null)
        {
            urlAuthenticator = BeanLoader.getInstance().getBean(URLAuthenticator.class);
        }
        return urlAuthenticator;
    }
}
