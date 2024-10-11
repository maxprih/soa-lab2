package org.bebra.soalab2oscar.service;

import lombok.RequiredArgsConstructor;
import org.bebra.soacommons.model.dto.MovieDto;
import org.bebra.soacommons.model.dto.PersonDto;
import org.bebra.soacommons.model.enums.MovieGenre;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author max_pri
 */
@Service
@RequiredArgsConstructor
public class OscarService {
    private final MovieService movieService;

    public List<PersonDto> getLoosers() {
        List<MovieDto> allMovies = movieService.getAllMovies();
        Map<Long, List<MovieDto>> moviesByScreenwriterId = allMovies.stream()
                .collect(Collectors.groupingBy(movie -> movie.getScreenwriter().getId()));

        return moviesByScreenwriterId.values().stream()
                .filter(movieDtos -> movieDtos.stream().allMatch(movie -> movie.getOscarsCount() == 0))
                .map(movieDtos -> movieDtos.getFirst().getScreenwriter())
                .toList();
    }

    public void rewardThriller() {
        List<MovieDto> allMovies = movieService.getAllMovies();
        allMovies.stream()
                .filter(movie -> movie.getGenre().equals(MovieGenre.THRILLER))
                .forEach(movie -> {
                    movie.setOscarsCount(movie.getOscarsCount() + 1);
                    movieService.updateMovie(movie.getId(), movie);
                });
    }
}
