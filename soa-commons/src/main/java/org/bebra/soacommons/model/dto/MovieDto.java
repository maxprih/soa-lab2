package org.bebra.soacommons.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bebra.soacommons.model.enums.MovieGenre;

import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieDto {
    private Integer id;
    private String name;
    private CoordinatesDto coordinates;
    private LocalDate creationDate;
    private long oscarsCount;
    private Long usaBoxOffice;
    private String tagline;
    private MovieGenre genre;
    private PersonDto screenwriter;
}
