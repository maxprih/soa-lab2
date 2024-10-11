package org.bebra.soalab2movie.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bebra.soacommons.model.enums.MovieGenre;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.time.LocalDate;

/**
 * @author max_pri
 */
@ToString
@Getter
@Setter
@Entity
@Table(name = "movie")
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @NotBlank
    private String name;

    @NotNull
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "coordinates_id", referencedColumnName = "id")
    private Coordinates coordinates;

    @NotNull
    @Column(name = "creation_date")
    private LocalDate creationDate = LocalDate.now();

    @Min(0)
    @Column(name = "oscars_count")
    private long oscarsCount;

    @NotNull
    @Min(0)
    @Column(name = "usa_box_office")
    private Long usaBoxOffice;

    @NotNull
    private String tagline;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "genre", columnDefinition = "movie_genre")
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private MovieGenre genre;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "person_id", referencedColumnName = "id")
    private Person screenwriter;
}
