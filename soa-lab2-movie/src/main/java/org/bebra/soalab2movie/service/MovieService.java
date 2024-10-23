package org.bebra.soalab2movie.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.bebra.soacommons.model.dto.PageDto;
import org.bebra.soacommons.model.dto.PageMetadata;
import org.bebra.soacommons.model.enums.MovieGenre;
import org.bebra.soalab2movie.exception.MinimalUsaBoxOfficeMovieNotFound;
import org.bebra.soalab2movie.exception.MovieNotFoundException;
import org.bebra.soalab2movie.model.entity.Movie;
import org.bebra.soalab2movie.model.mapper.MovieMapper;
import org.bebra.soalab2movie.repository.MovieRepository;

import java.util.List;



@ApplicationScoped
public class MovieService {
    @Inject
    private MovieRepository movieRepository;
    @Inject
    private MovieMapper mapper;

    public PageDto<Movie> findAll(int page, int size, List<String> sortList) {
        List<Movie> movies = movieRepository.findAll(page, size, sortList);
        long totalElements = movieRepository.countTotalMovies();

        return constructPage(movies, page, size, totalElements);
    }

    public Movie findById(Integer id) {
        return movieRepository.findById(id).orElseThrow(() -> new MovieNotFoundException(id));
    }

    @Transactional
    public void save(Movie movie) {
        movieRepository.save(movie);
    }

    @Transactional
    public void update(Integer id, Movie newMovie) {
        Movie existingMovie = findById(id);
        Movie updatedMovie = mapper.updateFields(existingMovie, newMovie);
        movieRepository.save(updatedMovie);
    }

    @Transactional
    public void delete(Integer id) {
        Movie movie = findById(id);
        movieRepository.delete(movie);
    }

    public Movie findMinimalUsaBoxOffice() {
        return movieRepository.findTopByOrderByUsaBoxOfficeAsc().orElseThrow(MinimalUsaBoxOfficeMovieNotFound::new);
    }

    public int countByTagline(String tagline) {
        return movieRepository.countAllByTagline(tagline);
    }

    public PageDto<Movie> findAllByGenreLessThan(MovieGenre genre, int page, int size, List<String> sortList) {
        List<Movie> movies = movieRepository.findByGenreLessThan(genre, page, size, sortList);
        long totalElements = movieRepository.countTotalMoviesGenreLessThen(genre);
        return constructPage(movies, page, size, totalElements);
    }

    public PageDto<Movie> constructPage(List<Movie> movies, int page, int size, long totalElements) {
        int totalPages = (int) Math.ceil((double) totalElements / size);
        return new PageDto<>(movies, new PageMetadata(size, page, totalElements, totalPages));
    }

    @Transactional
    public void rewardGenre(MovieGenre movieGenre) {
        movieRepository.rewardGenre(movieGenre);
    }
}
