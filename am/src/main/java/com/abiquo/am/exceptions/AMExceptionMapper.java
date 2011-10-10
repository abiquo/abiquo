package com.abiquo.am.exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.wink.common.internal.ResponseImpl.ResponseBuilderImpl;

import com.abiquo.appliancemanager.exceptions.AMException;
import com.abiquo.model.transport.error.ErrorDto;
import com.abiquo.model.transport.error.ErrorsDto;

@Provider
public class AMExceptionMapper implements ExceptionMapper<AMException>
{

    @Override
    public Response toResponse(AMException exception)
    {
        ErrorsDto errors = new ErrorsDto();
        errors.add(createError(exception));

        ResponseBuilder builder = new ResponseBuilderImpl();
        builder.entity(errors);
        builder.status(Status.INTERNAL_SERVER_ERROR);
        return builder.build();
    }

    private ErrorDto createError(AMException error)
    {
        ErrorDto errorDto = new ErrorDto();
        errorDto.setCode(error.getError().getCode());
        errorDto.setMessage(error.getMessage());
        return errorDto;
    }

}
