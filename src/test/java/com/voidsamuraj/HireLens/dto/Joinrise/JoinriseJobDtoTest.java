package com.voidsamuraj.HireLens.dto.Joinrise;

import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JoinriseJobDtoTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testJsonDeserialization() throws Exception {
        String json = """
            {
              "success": true,
              "legal": "API Terms of Service: Please link back...",
              "result": {
                "count": 10000,
                "jobs": [
                  {
                    "owner": {
                      "companyName": "GoFundMe"
                    },
                    "locationAddress": "Anywhere",
                    "title": "Renewals & Sales Support Specialist (Remote)",
                    "type": "Remote",
                    "url": "https://app.joinrise.co/jobs/gofundme-renewals-sales-support-specialist-remote-t23d",
                    "createdAt": "2025-07-03T17:30:54.528Z"
                  }
                ]
              }
            }
            """;

        JoinriseJobDto dto = objectMapper.readValue(json, JoinriseJobDto.class);

        assertNotNull(dto);
        assertTrue(dto.isSuccess());
        assertEquals("API Terms of Service: Please link back...", dto.getLegal());

        assertNotNull(dto.getResult());
        assertEquals(10000, dto.getResult().getCount());

        List<JoinriseJobDto.RiseJobDto> jobs = dto.getResult().getJobs();
        assertNotNull(jobs);
        assertEquals(1, jobs.size());

        JoinriseJobDto.RiseJobDto job = jobs.getFirst();
        assertEquals("Renewals & Sales Support Specialist (Remote)", job.getTitle());
        assertEquals("Remote", job.getType());
        assertEquals("Anywhere", job.getLocationAddress());
        assertEquals("https://app.joinrise.co/jobs/gofundme-renewals-sales-support-specialist-remote-t23d", job.getUrl());
        assertNotNull(job.getOwner());
        assertEquals("GoFundMe", job.getOwner().getCompanyName());
    }
}