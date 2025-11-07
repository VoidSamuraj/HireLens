package com.voidsamuraj.HireLens.dto.Adzuna;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Data Transfer Object representing a category from Adzuna job offers.
 * Contains a category label and associated tag.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdzunaCategoryDto {
    private String label;
    private String tag;
}