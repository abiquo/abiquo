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
package com.abiquo.api.handlers;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.validation.Validation;
import javax.ws.rs.core.Response.Status;

import org.apache.wink.common.internal.registry.Injectable;
import org.apache.wink.server.handlers.HandlersChain;
import org.apache.wink.server.handlers.MessageContext;
import org.apache.wink.server.handlers.RequestHandler;
import org.apache.wink.server.internal.handlers.SearchResult;
import org.apache.wink.server.internal.registry.MethodRecord;
import org.apache.wink.server.internal.registry.ResourceInstance;
import org.apache.wink.server.internal.registry.ServerInjectableFactory.PathParamBinding;
import org.apache.wink.server.internal.registry.ServerInjectableFactory.QueryParamBinding;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.method.MethodConstraintViolation;
import org.hibernate.validator.method.MethodValidator;

import com.abiquo.api.exceptions.ExtendedAPIException;
import com.abiquo.api.exceptions.InvalidParameterConstraint;

/**
 * This class checks if the Path Params and Query Params annotated with constraints in the 
 * chosen resource to execute pass the constraints.
 * That avoids a lot of {if ... else ... } clauses in the Resource layer.
 * 
 * @author jdevesa@abiquo.com
 */
public class InputParamConstraintHandler implements RequestHandler
{

    MethodValidator validator;


    @Override
    public void init(Properties props)
    {
        // Instantiate the validator.
        validator =
            Validation.byProvider(HibernateValidator.class).configure().buildValidatorFactory()
                .getValidator().unwrap(MethodValidator.class);
    }

    @Override
    public void handleRequest(MessageContext context, HandlersChain chain) throws Throwable
    {
        SearchResult searchResult = context.getAttribute(SearchResult.class);

        // Get the resource, method and parameter metadata stored in internal Wink objects for the
        // chosen method to execute.
        MethodRecord mr = searchResult.getMethod();
        ResourceInstance rs = searchResult.getResource();
        List<Injectable> fp = mr.getMetadata().getFormalParameters();

        // Define the variables needed to iterate the parameters and check the errors.
        Set<MethodConstraintViolation<Object>> constraintViolations;
        Object rsInstance = rs.getInstance(context);
        Set<InvalidParameterConstraint> paramErrors = new LinkedHashSet<InvalidParameterConstraint>();
        
        // Iterate the paramters and convert constraint violations into InvalidParameterConstraint error code.
        for (int index = 0; index < fp.size(); index++)
        {
            Injectable inj = fp.get(index);
            Object value = new Object();
            String paramName = new String();
            
            // Check it only if it is a QueryParam or a PathParam (forget EntityParams aka DTOs!!)
            if (inj instanceof QueryParamBinding)
            {
                QueryParamBinding injQuery = (QueryParamBinding) inj;
                paramName = injQuery.getName();
                value = injQuery.getValue(context);
                constraintViolations =
                    validator.validateParameter(rsInstance, mr.getMetadata().getReflectionMethod(),
                        value, index);
            }
            else if (inj instanceof PathParamBinding)
            {
                PathParamBinding injPath = (PathParamBinding) inj;
                paramName = injPath.getName();
                value = injPath.getValue(context);
                constraintViolations =
                    validator.validateParameter(rsInstance, mr.getMetadata().getReflectionMethod(),
                        value, index);
            }
            else
            {
                constraintViolations = new LinkedHashSet<MethodConstraintViolation<Object>>();
            }

            
            // Build the error object
            for (MethodConstraintViolation<Object> constraintViolation : constraintViolations)
            {
                paramErrors.add(transformConstraintViolationToInputParamError(constraintViolation, String.valueOf(value), paramName));
            }
        }

        if (paramErrors.size() > 0)
        {
            // If there are param errors set the exception in the 'searchResult' object
            // and return back.
            ExtendedAPIException exception =
                new ExtendedAPIException(Status.BAD_REQUEST, paramErrors);

            searchResult.setError(exception);
            return;
        }
        else
        {
            // Go on.
            chain.doChain(context);
        }

    }

    /**
     * Build the object InvalidParameterConstraint from the MethodConstraintViolation object.
     * @param constraintViolation
     * @param value
     * @param paramName
     * @return
     */
    private InvalidParameterConstraint transformConstraintViolationToInputParamError(
        MethodConstraintViolation<Object> constraintViolation, String value, String paramName)
    {
        InvalidParameterConstraint inv = new InvalidParameterConstraint();
        
        inv.setAnnotation(constraintViolation.getConstraintDescriptor().getAnnotation());
        inv.setMessageError("Parameter " + paramName + " " + constraintViolation.getMessage() + " but value " + value + " was found");
        
        return inv;
    }

}
