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

    String message() default "must be a String map and the Strings must be in the defined range";

    public int minKey();

    public int maxKey();

    public int maxValue();

    public int minValue();

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
                value != null && validKeys(value.keySet()) && validValues(value.values());

            if (!valid)
            {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(map.message())
                    .addConstraintViolation();
            }

            return valid;
        }

        private boolean validKeys(Set<String> keys)
        {
            for (String key : keys)
            {
                if (key == null)
                {
                    return false;
                }
                if (key.length() < map.minKey() || key.length() > map.maxKey())
                {
                    return false;
                }
            }

            return true;
        }

        private boolean validValues(Collection<String> values)
        {
            for (String value : values)
            {
                if (value == null)
                {
                    return false;
                }

                if (value.length() < map.minValue() || value.length() > map.maxValue())
                {
                    return false;
                }
            }

            return true;
        }
    }
}
