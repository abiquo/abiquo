/**
 * 
 */
package com.abiquo.model.validation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.abiquo.model.transport.SingleResourceTransportDto;

/**
 * This annotation just marks the field is only-output one. Used by all the classes that extend the
 * {@link SingleResourceTransportDto}
 * 
 * @author jdevesa@abiquo.com
 */
@Target({METHOD, FIELD, PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Output
{

}
