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

package com.abiquo.am.exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.wink.common.internal.ResponseImpl.ResponseBuilderImpl;

import redis.clients.jedis.exceptions.JedisException;

import com.abiquo.am.data.AMRedisDao;
import com.abiquo.model.transport.error.ErrorDto;
import com.abiquo.model.transport.error.ErrorsDto;

@Provider
public class AMRedisExceptionMapper implements ExceptionMapper<JedisException>
{

    @Override
    public Response toResponse(final JedisException exception)
    {
        ErrorsDto errors = new ErrorsDto();
        errors.add(createError(exception));

        ResponseBuilder builder = new ResponseBuilderImpl();
        builder.entity(errors);
        builder.status(Status.INTERNAL_SERVER_ERROR);

        resetRedisConnections();

        return builder.build();
    }

    private void resetRedisConnections()
    {
        AMRedisDao.REDIS_POOL.destroy();
        AMRedisDao.initPool();
    }

    private ErrorDto createError(final JedisException error)
    {
        ErrorDto errorDto = new ErrorDto();
        errorDto.setCode(AMError.AM_CHECK_REDIS.getCode());
        errorDto.setMessage(AMError.AM_CHECK_REDIS.getMessage() + "\n"
            + error.getLocalizedMessage());

        return errorDto;
    }

}
