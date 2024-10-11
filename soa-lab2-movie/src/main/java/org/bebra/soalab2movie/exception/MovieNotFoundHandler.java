package org.bebra.soalab2movie.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * @author max_pri
 */
@Provider
public class MovieNotFoundHandler implements ExceptionMapper<MovieNotFoundException> {
    @Override
    public Response toResponse(MovieNotFoundException e) {
        return Response.status(404).entity(
                String.format("Movie with id %s not found", e.getId())
        ).build();
    }
}
