package org.bebra.soalab2movie.controller;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.bebra.soacommons.model.dto.PageDto;
import org.bebra.soacommons.model.enums.MovieGenre;
import org.bebra.soalab2movie.model.entity.Movie;
import org.bebra.soalab2movie.service.MovieService;

import java.util.List;

/**
 * @author max_pri
 */
@Path("/v1/movie")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MovieController {

    @Inject
    private MovieService movieService;

    @GET
    public Response getMovies(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size,
            @QueryParam("sort") List<String> sort) {
        PageDto<Movie> movies = movieService.findAll(page, size, sort);
        return Response.ok(movies).build();
    }

    @GET
    @Path("/{id}")
    public Response getMovie(@PathParam("id") Integer id) {
        Movie movie = movieService.findById(id);
        return Response.ok(movie).build();
    }

    @POST
    public Response uploadMovie(@Valid Movie movie) {
        movieService.save(movie);
        return Response.status(Response.Status.CREATED).build();
    }

    @PUT
    @Path("/{id}")
    public Response updateMovie(@PathParam("id") Integer id, @Valid Movie movie) {
        movieService.update(id, movie);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteMovie(@PathParam("id") Integer id) {
        movieService.delete(id);
        return Response.ok().build();
    }

    @GET
    @Path("/min-box-office")
    public Response getMovieWithMinUsaBoxOffice() {
        Movie movie = movieService.findMinimalUsaBoxOffice();
        return Response.ok(movie).build();
    }

    @GET
    @Path("/count-by-tagline")
    public Response countByTagline(@QueryParam("tagline") String tagline) {
        int count = movieService.countByTagline(tagline);
        return Response.ok(count).build();
    }

    @GET
    @Path("/genre-less-then")
    public Response findAllByGenreLessThan(@QueryParam("page") @DefaultValue("1") int page,
                                           @QueryParam("size") @DefaultValue("10") int size,
                                           @QueryParam("sort") List<String> sort,
                                           @QueryParam("movieGenre") MovieGenre movieGenre) {
        PageDto<Movie> movies = movieService.findAllByGenreLessThan(movieGenre, page, size, sort);
        return Response.ok(movies).build();
    }
}
