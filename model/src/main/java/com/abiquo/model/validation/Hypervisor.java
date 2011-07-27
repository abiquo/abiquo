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

/**
 * 
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

import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.server.core.util.network.IPAddress;

/**
 * @author jdevesa
 *
 */
@Documented
@Constraint(validatedBy = Hypervisor.Validator.class)
@Target( {METHOD, FIELD, PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Hypervisor
{
    boolean required() default true;

    String message() default "must be one of the following values {vbox, kvm, xen-3, vmx-04, hyperv-301, xenserver}";

    Class< ? >[] groups() default {};

    Class< ? extends Payload>[] payload() default {};

    static class Validator implements ConstraintValidator<Hypervisor, String>
    {
        Hypervisor hyp;

        @Override
        public void initialize(final Hypervisor constraintAnnotation)
        {
            this.hyp = constraintAnnotation;
        }

        @Override
        public boolean isValid(final String value, final ConstraintValidatorContext context)
        {
            if (!hyp.required() && StringUtils.isEmpty(value))
            {
                return true;
            }

            boolean valid = false;
            if (!StringUtils.isEmpty(value))
            {                
                for (HypervisorType currentHyp : HypervisorType.values())
                {
                    if (currentHyp.getValue().equalsIgnoreCase(value))
                    {
                        valid = true;
                        break;
                    }
                }
            }

            if (!valid)
            {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(hyp.message()).addConstraintViolation();
            }

            return valid;
        }

    }
}
