package org.bebra.soalab2movie.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Getter
public class MovieNotFoundException extends RuntimeException {
    private final Integer id;
}
