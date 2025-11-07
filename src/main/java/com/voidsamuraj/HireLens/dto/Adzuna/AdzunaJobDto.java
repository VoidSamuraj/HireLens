package com.voidsamuraj.HireLens.dto.Adzuna;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

/**
 * DTO representing a job offer from Adzuna.
 * Includes all job details such as ID, title, description, salary range, contract type,
 * company, category, location, and geographic coordinates.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdzunaJobDto {

    private String id;

    private String title;

    private String description;

    @JsonProperty("redirect_url")
    private String redirectUrl;

    @JsonProperty("salary_min")
    private Integer salaryMin;

    @JsonProperty("salary_max")
    private Integer salaryMax;

    @JsonProperty("salary_is_predicted")
    private String  salaryIsPredicted;

    public Boolean getSalaryIsPredictedAsBoolean() {
        return "1".equals(salaryIsPredicted);
    }
    @JsonProperty("contract_type")
    private String contractType;

    @JsonProperty("company")
    private AdzunaCompanyDto companyName;

    private AdzunaCategoryDto category;

    private AdzunaLocationDto location;

    private Double latitude;

    private Double longitude;
}
