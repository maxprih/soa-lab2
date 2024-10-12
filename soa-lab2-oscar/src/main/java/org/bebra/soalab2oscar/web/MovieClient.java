package org.bebra.soalab2oscar.web;

import org.bebra.soacommons.model.dto.MovieDto;
import org.bebra.soacommons.model.dto.PageDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;



@FeignClient(value = "movieClient", url = "${app.movie-service.url}")
public interface MovieClient {
    @GetMapping("/movie")
    ResponseEntity<PageDto<MovieDto>> getMovies(@RequestParam("page") int page, @RequestParam("size") int size);

    @PutMapping("/movie/{id}")
    ResponseEntity<Void> updateMovie(@PathVariable("id") Integer id, @RequestBody MovieDto movieDto);
}
