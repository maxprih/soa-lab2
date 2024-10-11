package org.bebra.soalab2movie.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author max_pri
 */
@RequiredArgsConstructor
@Getter
public class MovieNotFoundException extends RuntimeException {
    private final Integer id;
}
