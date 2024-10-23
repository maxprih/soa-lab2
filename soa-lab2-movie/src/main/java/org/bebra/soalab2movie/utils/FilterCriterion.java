package org.bebra.soalab2movie.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author max_pri
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilterCriterion {
    private String fieldName;
    private String filterMode;
    private String value;
}
