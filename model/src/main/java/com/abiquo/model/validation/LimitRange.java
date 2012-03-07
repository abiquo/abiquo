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

import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.OverridesAttribute;
import javax.validation.Payload;

import com.abiquo.server.core.common.Limit;

@Documented
@Constraint(validatedBy = LimitRange.Validator.class)
@Target({METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LimitRange
{
    @OverridesAttribute(constraint = LimitRange.class, name = "type")
    String type();

    String message() default "invalid limit range for {type}";

    Class< ? >[] groups() default {};

    Class< ? extends Payload>[] payload() default {};

    static class Validator implements ConstraintValidator<LimitRange, Limit>
    {
        public static final long NO_LIMIT = 0;

        LimitRange anno;

        @Override
        public void initialize(LimitRange anno)
        {
            this.anno = anno;
        }

        @Override
        public boolean isValid(Limit limit, ConstraintValidatorContext context)
        {
            boolean valid = false;

            if (limit.soft < 0 || limit.hard < 0)
            {
                valid = false;
            }
            else
            {
                valid =
                    (limit.soft == NO_LIMIT && limit.hard == NO_LIMIT)
                        || (limit.soft >= NO_LIMIT && limit.hard == NO_LIMIT)
                        || (limit.soft != NO_LIMIT && limit.soft <= limit.hard);
            }

            if (!valid)
            {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                    "invalid limit range for " + anno.type() + ": soft = " + limit.soft
                        + "; hard = " + limit.hard).addConstraintViolation();
            }

            return valid;
        }
    }
}
