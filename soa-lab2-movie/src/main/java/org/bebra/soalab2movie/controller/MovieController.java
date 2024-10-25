package org.bebra.soalab2movie.controller;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.bebra.soacommons.model.dto.PageDto;
import org.bebra.soalab2movie.model.entity.Movie;
import org.bebra.soalab2movie.service.MovieService;

import java.util.List;


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
            @QueryParam("sort") List<String> sort,
            @QueryParam("name") String nameValue,
            @QueryParam("name-filter") String nameFilter,
            @QueryParam("id") String idValue,
            @QueryParam("id-filter") String idFilter,
            @QueryParam("tagline") String taglineValue,
            @QueryParam("tagline-filter") String taglineFilter,
            @QueryParam("creationDate") String creationDateValue,
            @QueryParam("creationDate-filter") String creationDateFilter,
            @QueryParam("oscarsCount") String oscarsCountValue,
            @QueryParam("oscarsCount-filter") String oscarsCountFilter,
            @QueryParam("usaBoxOffice") String usaBoxOfficeValue,
            @QueryParam("usaBoxOffice-filter") String usaBoxOfficeFilter,
            @QueryParam("genre") String genreValue,
            @QueryParam("genre-filter") String genreFilter,
            @QueryParam("screenwriter.name") String screenwriterNameValue,
            @QueryParam("screenwriter.name-filter") String screenwriterNameFilter,
            @QueryParam("coordinates.x") String coordinatesXValue,
            @QueryParam("coordinates.x-filter") String coordinatesXFilter,
            @QueryParam("coordinates.y") String coordinatesYValue,
            @QueryParam("coordinates.y-filter") String coordinatesYFilter) {
        PageDto<Movie> movies = movieService.findAll(page, size, sort,
                nameValue, nameFilter,
                idValue, idFilter,
                taglineValue, taglineFilter,
                creationDateValue, creationDateFilter,
                oscarsCountValue, oscarsCountFilter,
                usaBoxOfficeValue, usaBoxOfficeFilter,
                genreValue, genreFilter,
                screenwriterNameValue, screenwriterNameFilter,
                coordinatesXValue, coordinatesXFilter,
                coordinatesYValue, coordinatesYFilter);
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
    public Response findAllByGenreLessThan(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size,
            @QueryParam("sort") List<String> sort,
            @QueryParam("name") String nameValue,
            @QueryParam("name-filter") String nameFilter,
            @QueryParam("id") String idValue,
            @QueryParam("id-filter") String idFilter,
            @QueryParam("tagline") String taglineValue,
            @QueryParam("tagline-filter") String taglineFilter,
            @QueryParam("creationDate") String creationDateValue,
            @QueryParam("creationDate-filter") String creationDateFilter,
            @QueryParam("oscarsCount") String oscarsCountValue,
            @QueryParam("oscarsCount-filter") String oscarsCountFilter,
            @QueryParam("usaBoxOffice") String usaBoxOfficeValue,
            @QueryParam("usaBoxOffice-filter") String usaBoxOfficeFilter,
            @QueryParam("genre") String genreValue,
            @QueryParam("screenwriter.name") String screenwriterNameValue,
            @QueryParam("screenwriter.name-filter") String screenwriterNameFilter,
            @QueryParam("coordinates.x") String coordinatesXValue,
            @QueryParam("coordinates.x-filter") String coordinatesXFilter,
            @QueryParam("coordinates.y") String coordinatesYValue,
            @QueryParam("coordinates.y-filter") String coordinatesYFilter) {
        PageDto<Movie> movies = movieService.findAllByGenreLessThan(page, size, sort,
                nameValue, nameFilter,
                idValue, idFilter,
                taglineValue, taglineFilter,
                creationDateValue, creationDateFilter,
                oscarsCountValue, oscarsCountFilter,
                usaBoxOfficeValue, usaBoxOfficeFilter,
                genreValue,
                screenwriterNameValue, screenwriterNameFilter,
                coordinatesXValue, coordinatesXFilter,
                coordinatesYValue, coordinatesYFilter);
        return Response.ok(movies).build();
    }
}
