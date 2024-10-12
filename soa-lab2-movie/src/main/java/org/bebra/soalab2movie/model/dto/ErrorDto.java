package org.bebra.soalab2movie.model.dto;

import lombok.AllArgsConstructor;

import java.time.LocalDate;


@AllArgsConstructor
public class ErrorDto {
    private String message;
    private LocalDate date;
}
