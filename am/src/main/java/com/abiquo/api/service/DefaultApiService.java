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
