package com.voidsamuraj.HireLens.dto.Remotive;

import static org.junit.jupiter.api.Assertions.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class RemotiveJobDtoTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testJsonDeserialization() throws Exception {
        String json = """
                {
                  "id": 123,
                  "url": "https://remotive.com/remote-jobs/123",
                  "title": "Lead Developer",
                  "company_name": "Remotive",
                  "company_logo": "https://remotive.com/logo.png",
                  "category": "Software Development",
                  "job_type": "full_time",
                  "publication_date": "2025-09-05T12:34:56",
                  "candidate_required_location": "Worldwide",
                  "salary": "$40,000 - $50,000",
                  "description": "Full HTML job description here"
                }
                """;

        RemotiveJobDto jobDto = objectMapper.readValue(json, RemotiveJobDto.class);

        assertNotNull(jobDto);
        assertEquals(123L, jobDto.getId());
        assertEquals("https://remotive.com/remote-jobs/123", jobDto.getUrl());
        assertEquals("Lead Developer", jobDto.getTitle());
        assertEquals("Remotive", jobDto.getCompanyName());
        assertEquals("https://remotive.com/logo.png", jobDto.getCompanyLogo());
        assertEquals("Software Development", jobDto.getCategory());
        assertEquals("full_time", jobDto.getJobType());
        assertEquals("2025-09-05T12:34:56", jobDto.getPublicationDate());
        assertEquals("Worldwide", jobDto.getCandidateRequiredLocation());
        assertEquals("$40,000 - $50,000", jobDto.getSalary());
        assertEquals("Full HTML job description here", jobDto.getDescription());
    }
}
