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
