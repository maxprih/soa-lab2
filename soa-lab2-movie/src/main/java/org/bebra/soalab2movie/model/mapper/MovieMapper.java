package org.bebra.soalab2movie.model.mapper;

import org.bebra.soalab2movie.model.entity.Movie;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "cdi")
public interface MovieMapper {
    @Mapping(target = "id", ignore = true)
    Movie updateFields(@MappingTarget Movie existingMovie, Movie newMovie);
}

