/**
 * 
 */
package com.abiquo.api.exceptions;

import java.lang.annotation.Annotation;

/**
 * @author jdevesa
 *
 */
public class InvalidParameterConstraint
{
    private Annotation annotation;
    
    private String messageError;
    
    public void setMessageError(String messageError)
    {
        this.messageError = messageError;
    }
    public String getMessageError()
    {
        return messageError;
    }
    
    public void setAnnotation(Annotation annotation)
    {
        this.annotation = annotation;
    }
    public Annotation getAnnotation()
    {
        return annotation;
    }
}
