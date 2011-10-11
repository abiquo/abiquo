package com.abiquo.api.exceptions;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.core.Response.Status;

import com.abiquo.model.transport.error.CommonError;
import com.sun.istack.NotNull;

public class UnsupportedMediaException extends APIException
{
    private static final long serialVersionUID = 3461842821323389935L;

    public UnsupportedMediaException(final APIError apiError)
    {
        super(apiError);
    }

    public UnsupportedMediaException(final CommonError error)
    {
        super(error);
    }

    public UnsupportedMediaException(final Set<CommonError> errors)
    {
        super(errors);
    }

    public UnsupportedMediaException(@NotNull final String contentType,
        final List<String> expectedContentTypes)
    {
        super(new HashSet<CommonError>());
        String code =
            Status.UNSUPPORTED_MEDIA_TYPE.getStatusCode() + "-"
                + Status.UNSUPPORTED_MEDIA_TYPE.getReasonPhrase().toUpperCase();
        String msg =
            "Unsupported Content-Type: " + contentType + " Supported ones are: "
                + expectedContentTypes;
        CommonError error = new CommonError(code, msg);
        this.getErrors().add(error);
    }

}
