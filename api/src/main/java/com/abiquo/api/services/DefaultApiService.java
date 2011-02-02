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

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.exceptions.ExtendedAPIException;
import com.abiquo.scheduler.limit.LimitExceededException;
import com.abiquo.server.core.common.DefaultEntityBase;

public abstract class DefaultApiService
{
    protected Collection<LimitExceededException> limitExceptions =
        new LinkedHashSet<LimitExceededException>();

    protected Collection<APIError> errors = new LinkedHashSet<APIError>();

    protected Collection<ConstraintViolation< ? >> validationErrors =
        new LinkedHashSet<ConstraintViolation< ? >>();

    protected void flushErrors()
    {
        if (!errors.isEmpty() || !validationErrors.isEmpty() || !limitExceptions.isEmpty())
        {
            Collection<APIError> dup = new LinkedHashSet<APIError>(errors);
            Collection<ConstraintViolation< ? >> dupValidation =
                new LinkedHashSet<ConstraintViolation< ? >>(validationErrors);

            Collection<LimitExceededException> dupLimit =
                new LinkedHashSet<LimitExceededException>(limitExceptions);

            errors.clear();
            validationErrors.clear();
            limitExceptions.clear();

            throw new ExtendedAPIException(Status.BAD_REQUEST, dup, dupValidation, dupLimit);
        }
    }

    protected void addValidationErrors(Set<ConstraintViolation< ? >> errors)
    {
        validationErrors.addAll(errors);
    }

    protected void addValidationErrors(DefaultEntityBase entity)
    {
        addValidationErrors(entity.getValidationErrors());
    }

    protected void raiseValidationErrors(DefaultEntityBase entity)
    {
        addValidationErrors(entity);
        flushErrors();
    }
}
