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

package com.abiquo.api.service;

import java.util.Collection;
import java.util.LinkedHashSet;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.abiquo.appliancemanager.exceptions.AMException;
import com.abiquo.model.transport.error.ErrorDto;
import com.abiquo.model.transport.error.ErrorsDto;

public class DefaultApiService
{

    private Collection<AMException> unexpectedErrors;

    // Unexpected Errors
    private Collection<AMException> getUnexpectedErrors()
    {
        if (unexpectedErrors == null)
        {
            unexpectedErrors = new LinkedHashSet<AMException>();
        }
        return unexpectedErrors;
    }

    protected void addError(AMException e)
    {
        getUnexpectedErrors().add(e);
    }

    protected void flushErrors()
    {
        if (!getUnexpectedErrors().isEmpty())
        {
            ErrorsDto errors = new ErrorsDto();
            for (AMException e : getUnexpectedErrors())
            {

                ErrorDto error = new ErrorDto(e.getError().getCode(), e.getLocalizedMessage());

                errors.add(error);
            }

            unexpectedErrors.clear();
            throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR)
                .entity(errors).build());
        }
    }
}
