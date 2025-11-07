package com.voidsamuraj.HireLens.dto.Joinrise;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/*
    TODO: implement usage of:
       private String workModel; ? check
            private String oneSentenceJobSummary;
            private List<String> keywords;
            private String employmentType;
            private List<String> skillRequirements;
 */
/**
 * DTO representing the Joinrise API response for job listings.
 * Includes success status, legal info, and result containing job details.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JoinriseJobDto {
    private boolean success;
    private String legal;

    @JsonProperty("result")
    private Result result;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Result {
        private int count;
        private List<RiseJobDto> jobs;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RiseJobDto {
        @JsonProperty("_id")
        private String id;
        private String seniority;
        private Integer salaryRangeMinYearly;
        private Integer salaryRangeMaxYearly;
        private String title;
        private String url;
        private String type; // Remote / Onsite
        private String createdAt;
        private String locationAddress;
        private String category;
        //private String salary;
        @JsonProperty("owner")
        private Owner owner;
        private DescriptionBreakdown descriptionBreakdown;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Owner {
            private String companyName;
            private String photo;
        }
        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class DescriptionBreakdown {
            private String workModel;
            private String oneSentenceJobSummary;
            private List<String> keywords;
            private String employmentType;
            private List<String> skillRequirements;
        }
    }
}
