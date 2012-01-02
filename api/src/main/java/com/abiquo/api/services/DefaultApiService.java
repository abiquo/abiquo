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

package com.abiquo.api.services;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.exceptions.BadRequestException;
import com.abiquo.api.exceptions.ConflictException;
import com.abiquo.api.exceptions.ForbiddenException;
import com.abiquo.api.exceptions.InternalServerErrorException;
import com.abiquo.api.exceptions.NotFoundException;
import com.abiquo.api.exceptions.ServiceUnavailableException;
import com.abiquo.api.tracer.TracerLogger;
import com.abiquo.model.transport.error.CommonError;
import com.abiquo.server.core.common.GenericEnityBase;

public abstract class DefaultApiService
{
    private Collection<CommonError> conflictErrors;

    private Collection<CommonError> validationErrors;

    private Collection<CommonError> notfoundErrors;

    private Collection<CommonError> forbiddenErrors;

    private Collection<CommonError> unexpectedErrors;

    private Collection<CommonError> serviceUnavailableErrors;

    @Autowired
    protected TracerLogger tracer;

    protected void flushErrors()
    {
        Set<CommonError> errors = new LinkedHashSet<CommonError>();
        if (!getUnexpectedErrors().isEmpty())
        {
            errors.addAll(unexpectedErrors);
            unexpectedErrors.clear();
            throw new InternalServerErrorException(errors);
        }
        if (!getForbiddenErrors().isEmpty())
        {
            errors.addAll(forbiddenErrors);
            forbiddenErrors.clear();
            throw new ForbiddenException(errors);
        }
        if (!getNotfoundErrors().isEmpty())
        {
            errors.addAll(notfoundErrors);
            notfoundErrors.clear();
            throw new NotFoundException(errors);
        }
        if (!getValidationErrors().isEmpty())
        {
            errors.addAll(validationErrors);
            validationErrors.clear();
            throw new BadRequestException(errors);
        }
        if (!getConflictErrors().isEmpty())
        {
            errors.addAll(conflictErrors);
            conflictErrors.clear();
            throw new ConflictException(errors);
        }
        if (!getServiceUnavailableErrors().isEmpty())
        {
            errors.addAll(serviceUnavailableErrors);
            serviceUnavailableErrors.clear();
            throw new ServiceUnavailableException(errors);
        }
    }

    protected <T extends GenericEnityBase< ? >> void validate(final T entity)
    {
        if (!entity.isValid())
        {
            addValidationErrors(entity.getValidationErrors());
            flushErrors();
        }
    }

    // ValidationErrors
    private Collection<CommonError> getValidationErrors()
    {
        if (validationErrors == null)
        {
            validationErrors = new LinkedHashSet<CommonError>();
        }
        return validationErrors;
    }

    protected void addValidationErrors(final Set<CommonError> errors)
    {
        getValidationErrors().addAll(errors);
    }

    protected void addValidationErrors(final CommonError error)
    {
        getValidationErrors().add(error);
    }

    protected void addValidationErrors(final APIError apiError)
    {
        getValidationErrors().add(addAPIError(apiError));
    }

    // NotFoundErrors
    private Collection<CommonError> getNotfoundErrors()
    {
        if (notfoundErrors == null)
        {
            notfoundErrors = new LinkedHashSet<CommonError>();
        }
        return notfoundErrors;
    }

    protected void addNotFoundErrors(final Set<CommonError> errors)
    {
        getNotfoundErrors().addAll(errors);
    }

    protected void addNotFoundErrors(final CommonError error)
    {
        getNotfoundErrors().add(error);
    }

    protected void addNotFoundErrors(final APIError apiError)
    {
        getNotfoundErrors().add(addAPIError(apiError));
    }

    // ConflictErrors
    private Collection<CommonError> getConflictErrors()
    {
        if (conflictErrors == null)
        {
            conflictErrors = new LinkedHashSet<CommonError>();
        }
        return conflictErrors;
    }

    protected void addConflictErrors(final Set<CommonError> errors)
    {
        getConflictErrors().addAll(errors);
    }

    protected void addConflictErrors(final CommonError error)
    {
        getConflictErrors().add(error);
    }

    protected void addConflictErrors(final APIError apiError)
    {
        getConflictErrors().add(addAPIError(apiError));
    }

    // Security Errors
    private Collection<CommonError> getForbiddenErrors()
    {
        if (forbiddenErrors == null)
        {
            forbiddenErrors = new LinkedHashSet<CommonError>();
        }
        return forbiddenErrors;
    }

    protected void addForbiddenErrors(final Set<CommonError> errors)
    {
        getForbiddenErrors().addAll(errors);
    }

    protected void addForbiddenErrors(final CommonError error)
    {
        getForbiddenErrors().add(error);
    }

    protected void addForbiddenErrors(final APIError apiError)
    {
        getForbiddenErrors().add(addAPIError(apiError));
    }

    // Unexpected Errors
    private Collection<CommonError> getUnexpectedErrors()
    {
        if (unexpectedErrors == null)
        {
            unexpectedErrors = new LinkedHashSet<CommonError>();
        }
        return unexpectedErrors;
    }

    protected void addUnexpectedErrors(final Set<CommonError> errors)
    {
        getUnexpectedErrors().addAll(errors);
    }

    protected void addUnexpectedErrors(final CommonError error)
    {
        getUnexpectedErrors().add(error);
    }

    protected void addUnexpectedErrors(final APIError apiError)
    {
        getUnexpectedErrors().add(addAPIError(apiError));
    }

    // Service Unavailabe Errors
    private Collection<CommonError> getServiceUnavailableErrors()
    {
        if (serviceUnavailableErrors == null)
        {
            serviceUnavailableErrors = new LinkedHashSet<CommonError>();
        }
        return serviceUnavailableErrors;
    }

    protected void addServiceUnavailableErrors(final Set<CommonError> errors)
    {
        getServiceUnavailableErrors().addAll(errors);
    }

    protected void addServiceUnavailableErrors(final CommonError error)
    {
        getServiceUnavailableErrors().add(error);
    }

    protected void addServiceUnavailableErrors(final APIError apiError)
    {
        getServiceUnavailableErrors().add(addAPIError(apiError));
    }

    private CommonError addAPIError(final APIError apiError)
    {
        return new CommonError(apiError.getCode(), apiError.getMessage());
    }

}
