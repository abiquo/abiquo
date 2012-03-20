package com.abiquo.model.transport;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang.StringUtils;

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
