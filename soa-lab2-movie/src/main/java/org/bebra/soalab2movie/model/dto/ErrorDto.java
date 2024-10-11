package org.bebra.soalab2movie.model.dto;

import lombok.AllArgsConstructor;

import java.time.LocalDate;

/**
 * @author max_pri
 */
@AllArgsConstructor
public class ErrorDto {
    private String message;
    private LocalDate date;
}
