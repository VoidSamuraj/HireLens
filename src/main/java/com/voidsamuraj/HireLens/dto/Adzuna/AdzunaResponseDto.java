package com.voidsamuraj.HireLens.dto.Adzuna;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO representing the response from the Adzuna job search API.
 * Contains a list of job results.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdzunaResponseDto {
    private List<AdzunaJobDto> results;
}