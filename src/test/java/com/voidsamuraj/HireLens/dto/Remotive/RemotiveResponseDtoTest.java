package com.voidsamuraj.HireLens.dto.Remotive;

import static org.junit.jupiter.api.Assertions.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class RemotiveResponseDtoTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testJsonDeserialization() throws Exception {
        String json = """
                {
                  "0-legal-notice": "Remotive API Legal Notice",
                  "job-count": 1,
                  "jobs": [
                    {
                      "id": 123,
                      "title": "Lead Developer",
                      "company_name": "Remotive"
                    }
                  ]
                }
                """;

        RemotiveResponseDto dto = objectMapper.readValue(json, RemotiveResponseDto.class);

        assertNotNull(dto);
        assertEquals("Remotive API Legal Notice", dto.getLegalNotice());
        assertEquals(1, dto.getJobCount());
        assertNotNull(dto.getJobs());
        assertEquals(1, dto.getJobs().size());

        assertEquals(123L, dto.getJobs().getFirst().getId());
        assertEquals("Lead Developer", dto.getJobs().getFirst().getTitle());
        assertEquals("Remotive", dto.getJobs().getFirst().getCompanyName());
    }
}
