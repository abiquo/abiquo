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

package com.abiquo.api.exceptions;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.core.Response.Status;

import com.abiquo.model.transport.error.CommonError;
import com.sun.istack.NotNull;

public class UnsupportedMediaException extends APIException
{
    private static final long serialVersionUID = 3461842821323389935L;

    public UnsupportedMediaException(final APIError apiError)
    {
        super(apiError);
    }

    public UnsupportedMediaException(final CommonError error)
    {
        super(error);
    }

    public UnsupportedMediaException(final Set<CommonError> errors)
    {
        super(errors);
    }

    public UnsupportedMediaException(@NotNull final String contentType,
        final List<String> expectedContentTypes)
    {
        super(new HashSet<CommonError>());
        String code =
            Status.UNSUPPORTED_MEDIA_TYPE.getStatusCode() + "-"
                + Status.UNSUPPORTED_MEDIA_TYPE.getReasonPhrase().toUpperCase();
        String msg =
            "Unsupported Content-Type: " + contentType + " Supported ones are: "
                + expectedContentTypes;
        CommonError error = new CommonError(code, msg);
        this.getErrors().add(error);
    }

}
