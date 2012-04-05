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

package com.abiquo.model.transport;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang.StringUtils;

@Retention(RetentionPolicy.RUNTIME)
public @interface Link
{

    String href();

    String rel();

    String title();

    boolean required();

    String message() default "Link is missing";

    static class Validator implements ConstraintValidator<Link, String>
    {
        Link link;

        @Override
        public boolean isValid(final String value, final ConstraintValidatorContext context)
        {
            if (!link.required() && StringUtils.isEmpty(value))
            {
                return true;
            }
            else
            {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(link.message())
                    .addConstraintViolation();
            }

            return false;
        }

        @Override
        public void initialize(final Link constraintAnnotation)
        {
            this.link = constraintAnnotation;
        }
    }

}
