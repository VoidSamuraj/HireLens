package com.voidsamuraj.HireLens.dto.Adzuna;

import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;

public class AdzunaJobDtoTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testJsonDeserialization() throws Exception {
        String json = """
                {
                  "id": "abc123",
                  "title": "Java Developer",
                  "description": "Full stack developer role",
                  "redirect_url": "https://adzuna.com/jobs/abc123",
                  "salary_min": 40000,
                  "salary_max": 50000,
                  "salary_is_predicted": true,
                  "contract_type": "full_time",
                  "company": "TechCorp",
                  "category": { "label": "Software / IT" },
                  "location": { "display_name": "London, UK" },
                  "latitude": 51.5074,
                  "longitude": -0.1278
                }
                """;

        AdzunaJobDto jobDto = objectMapper.readValue(json, AdzunaJobDto.class);

        assertNotNull(jobDto);
        assertEquals("abc123", jobDto.getId());
        assertEquals("Java Developer", jobDto.getTitle());
        assertEquals("Full stack developer role", jobDto.getDescription());
        assertEquals("https://adzuna.com/jobs/abc123", jobDto.getRedirectUrl());
        assertEquals(40000, jobDto.getSalaryMin());
        assertEquals(50000, jobDto.getSalaryMax());
        assertTrue(Boolean.parseBoolean(jobDto.getSalaryIsPredicted()));
        assertEquals("full_time", jobDto.getContractType());
        assertEquals("TechCorp", jobDto.getCompanyName());

        assertNotNull(jobDto.getCategory());
        assertEquals("Software / IT", jobDto.getCategory().getLabel());

        assertNotNull(jobDto.getLocation());
        assertEquals("London, UK", jobDto.getLocation().getDisplayName());

        assertEquals(51.5074, jobDto.getLatitude());
        assertEquals(-0.1278, jobDto.getLongitude());
    }
}