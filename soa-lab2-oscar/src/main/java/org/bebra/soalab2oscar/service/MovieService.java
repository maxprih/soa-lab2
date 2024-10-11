package org.bebra.soalab2oscar.service;

import lombok.RequiredArgsConstructor;
import org.bebra.soacommons.model.dto.MovieDto;
import org.bebra.soacommons.model.dto.PageDto;
import org.bebra.soalab2oscar.web.MovieClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author max_pri
 */
@Service
@RequiredArgsConstructor
public class MovieService {
    private final MovieClient movieClient;

    public List<MovieDto> getAllMovies() {
        int pageNumber = 0;
        int pageSize = 500;
        List<MovieDto> allMovies = new ArrayList<>();
        PageDto<MovieDto> page;

        do {
            page = movieClient.getMovies(pageNumber, pageSize).getBody();
            allMovies.addAll(page.getContent());
            pageNumber++;
        } while (pageNumber < page.getPage().getTotalPages());

        return allMovies;
    }

    public void updateMovie(Integer id, MovieDto updatedMovie) {
        movieClient.updateMovie(id, updatedMovie);
    }
}
