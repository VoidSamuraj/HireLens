package com.voidsamuraj.HireLens.dto.Adzuna;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object representing a company from Adzuna job offers.
 * Contains name.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdzunaCompanyDto {
    private String display_name;
}
