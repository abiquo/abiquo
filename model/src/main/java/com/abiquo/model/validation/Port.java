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

@Documented
@Constraint(validatedBy = Port.Validator.class)
@Target( {METHOD, FIELD, PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Port
{
    boolean required() default true;

    String message() default "invalid port :";

    Class< ? >[] groups() default {};

    Class< ? extends Payload>[] payload() default {};

    static class Validator implements ConstraintValidator<Port, Integer>
    {
        Port port;

        @Override
        public void initialize(Port constraintAnnotation)
        {
            this.port = constraintAnnotation;
        }

        @Override
        public boolean isValid(Integer value, ConstraintValidatorContext context)
        {
            if (!port.required() && value == null)
            {
                return true;
            }

            Boolean valid = value >= 0 && value <= 65535;
            
            if (!valid)
            {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(port.message() + value)
                    .addConstraintViolation();
            }
            
            return valid;
        }

    }
}
