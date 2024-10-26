package org.bebra.soalab2movie.exception;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.core.Response;
import jakarta.ejb.EJBTransactionRolledbackException;
import jakarta.ws.rs.ext.Provider;

import java.util.Set;

@Provider
public class ConstraintViolationExceptionHandler implements ExceptionMapper<EJBTransactionRolledbackException> {

    @Override
    public Response toResponse(EJBTransactionRolledbackException ex) {
        Throwable cause = ex.getCause();
        if (cause instanceof ConstraintViolationException constraintEx) {

            Set<ConstraintViolation<?>> constraintViolations = constraintEx.getConstraintViolations();
            var jsonObject = Json.createObjectBuilder().add("title", "Validation Errors");
            var jsonArray = Json.createArrayBuilder();

            for (var constraint : constraintViolations) {
                JsonObject jsonError = Json.createObjectBuilder()
                        .add("class", constraint.getLeafBean().toString().split("@")[0])
                        .add("field", constraint.getPropertyPath().toString())
                        .add("violationMessage", constraint.getMessage())
                        .build();
                jsonArray.add(jsonError);
            }

            JsonObject errorJsonEntity = jsonObject.add("errors", jsonArray.build()).build();
            return Response.status(Response.Status.BAD_REQUEST).entity(errorJsonEntity).build();
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Internal Server Error").build();
    }
}
