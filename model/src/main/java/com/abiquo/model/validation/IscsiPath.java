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

package com.abiquo.model.validation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;

import org.apache.commons.lang.StringUtils;

import com.abiquo.model.util.AddressingUtils;

@Documented
@Constraint(validatedBy = IscsiPath.Validator.class)
@Target( {METHOD, FIELD, PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface IscsiPath
{
    public static final String ERROR_MESSAGE = "must be a valid iSCSI path";

    boolean required() default true;

    String message() default ERROR_MESSAGE;

    Class< ? >[] groups() default {};

    Class< ? extends Payload>[] payload() default {};

    static class Validator implements ConstraintValidator<IscsiPath, String>
    {
        IscsiPath iscsiPath;

        @Override
        public void initialize(final IscsiPath constraintAnnotation)
        {
            this.iscsiPath = constraintAnnotation;
        }

        @Override
        public boolean isValid(final String value, final ConstraintValidatorContext context)
        {
            if (!iscsiPath.required() && StringUtils.isEmpty(value))
            {
                return true;
            }

            boolean valid = !StringUtils.isEmpty(value) && AddressingUtils.isValidPath(value);

            if (!valid)
            {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(iscsiPath.message())
                    .addConstraintViolation();
            }

            return valid;
        }

    }
}
