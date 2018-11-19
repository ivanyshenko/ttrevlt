package com.revolut.testtask.controllers.exceptions;

import com.revolut.testtask.services.exceptions.InvalidOperationException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class InvalidOperationExceptionMapper implements ExceptionMapper<InvalidOperationException> {
    @Override
    public Response toResponse(InvalidOperationException exception) {
        return Response.status(Response.Status.BAD_REQUEST).build();
    }
}
