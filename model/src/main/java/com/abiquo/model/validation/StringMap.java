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
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = StringMap.Validator.class)
@Target( {METHOD, FIELD, PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface StringMap
{
    boolean required() default true;

    public int minKey();

    public int maxKey();

    public int maxValue();

    public int minValue();

    String message() default "is a required field";

    String genericMessage() default "is invalid. ";

    String messageMissingKey() default "The field 'key' is required";

    String messageMissingValue() default "The field 'value' is required but it may be empty";

    Class< ? >[] groups() default {};

    Class< ? extends Payload>[] payload() default {};

    static class Validator implements ConstraintValidator<StringMap, Map<String, String>>
    {
        StringMap map;

        @Override
        public void initialize(final StringMap constraintAnnotation)
        {
            this.map = constraintAnnotation;
        }

        @Override
        public boolean isValid(final Map<String, String> value,
            final ConstraintValidatorContext context)
        {
            if (!map.required() && value == null)
            {
                return true;
            }

            boolean valid =
                value != null && validKeys(value.keySet(), context)
                    && validValues(value.values(), context);

            return valid;
        }

        private boolean validKeys(final Set<String> keys, final ConstraintValidatorContext context)
        {
            boolean valid = true;
            String error = "";

            for (String key : keys)
            {
                if (key == null)
                {
                    valid = false;
                    error = map.genericMessage() + map.messageMissingKey();
                }
                else if (key.length() < map.minKey() || key.length() > map.maxKey())
                {
                    valid = false;
                    error =
                        map.genericMessage() + "The field 'key' must be between " + map.minKey()
                            + " and " + map.maxKey() + " characters long";
                }
            }

            if (valid == false)
            {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(error).addConstraintViolation();
            }

            return valid;
        }

        private boolean validValues(final Collection<String> values,
            final ConstraintValidatorContext context)
        {
            boolean valid = true;
            String error = "";

            for (String value : values)
            {
                if (value == null)
                {
                    valid = false;
                    error = map.genericMessage() + map.messageMissingValue();
                }
                else if (value.length() < map.minValue() || value.length() > map.maxValue())
                {
                    valid = false;
                    error =
                        map.genericMessage() + "The field 'value' must be between "
                            + map.minValue() + " and " + map.maxValue() + " characters long";
                }
            }

            if (valid == false)
            {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(error).addConstraintViolation();
            }

            return valid;
        }
    }
}
