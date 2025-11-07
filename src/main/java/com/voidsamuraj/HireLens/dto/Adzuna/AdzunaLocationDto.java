package com.voidsamuraj.HireLens.dto.Adzuna;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO representing a location in Adzuna job offers.
 * Contains a list of area strings and a display name.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdzunaLocationDto {
    private List<String> area;

    @JsonProperty("display_name")
    private String displayName;
}
