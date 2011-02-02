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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;

import javax.validation.ConstraintViolation;
import javax.ws.rs.core.Response.Status;

import com.abiquo.scheduler.limit.LimitExceededException;

public class ExtendedAPIException extends APIException
{
    private static final long serialVersionUID = -6140840253539726342L;

    Collection<APIError> errors;

    Collection<ConstraintViolation< ? >> validationErrors =
        new LinkedHashSet<ConstraintViolation< ? >>();

    Collection<LimitExceededException> limitExceptions =
        new LinkedHashSet<LimitExceededException>();

    public Collection<LimitExceededException> getLimitExceededExceptions()
    {
        return limitExceptions;
    }

    public void addValidationError(ConstraintViolation< ? > error)
    {
        validationErrors.add(error);
    }

    public Collection<ConstraintViolation< ? >> getValidationErrors()
    {
        return validationErrors;
    }

    public Collection<APIError> getErrors()
    {
        return errors;
    }

    public ExtendedAPIException(Status httpStatus, APIError error)
    {
        this(httpStatus, Collections.singletonList(error),
            new LinkedHashSet<ConstraintViolation< ? >>());
    }

    @Deprecated
    public ExtendedAPIException(Status httpStatus, Collection<APIError> errors,
        Collection<ConstraintViolation< ? >> validationErrors)
    {
        super(httpStatus);
        this.errors = errors;
        this.validationErrors = validationErrors;
    }

    public ExtendedAPIException(Status httpStatus, Collection<APIError> errors,
        Collection<ConstraintViolation< ? >> validationErrors,
        Collection<LimitExceededException> limitExceptions)
    {
        super(httpStatus);
        this.errors = errors;
        this.validationErrors = validationErrors;
        this.limitExceptions = limitExceptions;

    }
}
