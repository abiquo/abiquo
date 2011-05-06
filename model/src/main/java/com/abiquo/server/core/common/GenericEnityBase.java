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

package com.abiquo.server.core.common;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.MappedSuperclass;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;

import com.abiquo.model.transport.error.CommonError;
import com.softwarementors.bzngine.entities.PersistentVersionedEntityBase;
import com.softwarementors.validation.ValidationManager;

@MappedSuperclass
public abstract class GenericEnityBase<T extends Serializable> extends
    PersistentVersionedEntityBase<T>
{
    protected GenericEnityBase()
    {
        super();
    }

    public boolean isValid()
    {
        return Validation.buildDefaultValidatorFactory().getValidator().validate(this).isEmpty();
    }

    public Set<CommonError> getValidationErrors()
    {
        Set<ConstraintViolation< ? extends Object>> constraintValidations =
            new LinkedHashSet<ConstraintViolation< ? extends Object>>(ValidationManager
                .getValidator().validate(this));
        
        Set<CommonError> setOfErrors = new LinkedHashSet<CommonError>();
        for (ConstraintViolation<? extends Object> cons : constraintValidations)
        {
            setOfErrors.add(new CommonError(cons.getPropertyPath().toString(), cons.getMessage()));
        }
        return setOfErrors;
    }
    
}
