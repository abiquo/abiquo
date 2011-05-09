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

import javax.validation.ConstraintViolation;
import javax.ws.rs.core.Response.Status;

import org.springframework.beans.factory.annotation.Autowired;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.tracer.TracerLogger;
import com.abiquo.api.exceptions.APIException;
import com.abiquo.api.exceptions.BadRequestException;
import com.abiquo.api.exceptions.ConflictException;
import com.abiquo.api.exceptions.ForbiddenException;
import com.abiquo.api.exceptions.InternalServerErrorException;
import com.abiquo.api.exceptions.NotFoundException;
import com.abiquo.model.transport.error.CommonError;
import com.abiquo.scheduler.limit.LimitExceededException;
import com.abiquo.server.core.common.GenericEnityBase;

public abstract class DefaultApiService
{
    private Collection<CommonError> conflictErrors;
    private Collection<CommonError> validationErrors;
    private Collection<CommonError> notfoundErrors;
    private Collection<CommonError> forbiddenErrors;
    private Collection<CommonError> unexpectedErrors;


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
    
    protected void addValidationErrors(Set<CommonError> errors)
    {
        getValidationErrors().addAll(errors);
    }
    
    protected void addValidationErrors(CommonError error)
    {
        getValidationErrors().add(error);
    }
    
    protected void addValidationErrors(APIError apiError)
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
    
    protected void addNotFoundErrors(Set<CommonError> errors)
    {
        getNotfoundErrors().addAll(errors);
    }
    
    protected void addNotFoundErrors(CommonError error)
    {
        getNotfoundErrors().add(error);
    }
    
    protected void addNotFoundErrors(APIError apiError)
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

    protected void addConflictErrors(Set<CommonError> errors)
    {
        getConflictErrors().addAll(errors);
    }
    
    protected void addConflictErrors(CommonError error)
    {
        getConflictErrors().add(error);
    }
    
    protected void addConflictErrors(APIError apiError)
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
    
    protected void addForbiddenErrors(Set<CommonError> errors)
    {
        getForbiddenErrors().addAll(errors);
    }
    
    protected void addForbiddenErrors(CommonError error)
    {
        getForbiddenErrors().add(error);
    }
    
    protected void addForbiddenErrors(APIError apiError)
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
    
    protected void addUnexpectedErrors(Set<CommonError> errors)
    {
        getUnexpectedErrors().addAll(errors);
    }
    
    protected void addUnexpectedErrors(CommonError error)
    {
        getUnexpectedErrors().add(error);
    }
    
    protected void addUnexpectedErrors(APIError apiError)
    {
        getUnexpectedErrors().add(addAPIError(apiError));
    }
    
    private CommonError addAPIError(APIError apiError)
    {
        return new CommonError(apiError.getCode(), apiError.getMessage());
    }
}
