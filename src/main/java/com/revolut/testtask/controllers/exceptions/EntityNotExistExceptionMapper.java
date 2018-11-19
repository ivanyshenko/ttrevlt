package com.revolut.testtask.controllers.exceptions;

import com.revolut.testtask.services.exceptions.EntityNotExistException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class EntityNotExistExceptionMapper implements ExceptionMapper<EntityNotExistException> {
    @Override
    public Response toResponse(EntityNotExistException exception) {
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
