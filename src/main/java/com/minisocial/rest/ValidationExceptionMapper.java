package com.minisocial.rest;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {
    @Override
    public Response toResponse(ConstraintViolationException exception) {
        String message = exception.getConstraintViolations().iterator().next().getMessage();
        return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\": \"" + message + "\"}")
                .build();
    }
}