package com.voidsamuraj.HireLens.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.voidsamuraj.HireLens.client.remoteok.dto.Job;
import com.voidsamuraj.HireLens.dto.Adzuna.AdzunaJobDto;
import com.voidsamuraj.HireLens.dto.Joinrise.JoinriseJobDto;
import com.voidsamuraj.HireLens.dto.Remotive.RemotiveJobDto;
import com.voidsamuraj.HireLens.entity.JobEntity;
import com.voidsamuraj.HireLens.util.DateParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JobMapperTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    JobEntity job = JobEntity.builder()
            .id(1L)
            .title("Senior Java Developer")
            .companyName("TechCorp")
            .companyLogo("https://example.com/logo.png")
            .url("https://example.com/job/123")
            .category("IT, Software")
            .jobType("Full-time")
            .publicationDate("2025-09-08T00:00:00Z")
            .candidateRequiredLocation("Remote")
            .salary("8000-12000")
            .description("Exciting opportunity to work on enterprise software solutions with modern technologies.")
            .tsvEn("senior java developer techcorp remote")
            .build();


    @Test
    void toEntity() throws Exception{
        String json = """
                {
                  "id": 123,
                  "url": "https://remotive.com/remote-jobs/123",
                  "title": "Lead Developer",
                  "company_name": "Remotive",
                  "company_logo": "https://remotive.com/logo.png",
                  "category": "Software Development",
                  "job_type": "full_time",
                  "publication_date": "2025-09-05T12:34:56Z",
                  "candidate_required_location": "Worldwide",
                  "salary": "$40,000 - $50,000",
                  "description": "Full HTML job description here"
                }
                """;

        RemotiveJobDto jobDto = objectMapper.readValue(json, RemotiveJobDto.class);
        JobEntity entity = JobMapper.toEntity(jobDto);

        assertNotNull(entity);
        assertEquals(123L, entity.getId());
        assertEquals("https://remotive.com/remote-jobs/123", entity.getUrl());
        assertEquals("Lead Developer", entity.getTitle());
        assertEquals("Remotive", entity.getCompanyName());
        assertEquals("https://remotive.com/logo.png", entity.getCompanyLogo());
        assertEquals("Software Development", entity.getCategory());
        assertEquals("full_time", entity.getJobType());
        assertEquals("2025-09-05T12:34:56Z", entity.getPublicationDate());
        assertEquals("Worldwide", entity.getCandidateRequiredLocation());
        assertEquals("$40,000 - $50,000", entity.getSalary());
        assertEquals("Full HTML job description here", entity.getDescription());


    }

    @Test
    void toRemotiveJobDto() {
        RemotiveJobDto rjob = JobMapper.toRemotiveJobDto(job);
        assertNotNull(rjob);
        assertEquals(job.getId(), rjob.getId());
        assertEquals(job.getTitle(), rjob.getTitle());
        assertEquals(job.getCompanyName(), rjob.getCompanyName());
        assertEquals(job.getCompanyLogo(), rjob.getCompanyLogo());
        assertEquals(job.getUrl(), rjob.getUrl());
        assertEquals(job.getCategory(), rjob.getCategory());
        assertEquals(job.getJobType(), rjob.getJobType());
        assertEquals(job.getPublicationDate(), rjob.getPublicationDate());
        assertEquals(job.getCandidateRequiredLocation(), rjob.getCandidateRequiredLocation());
        assertEquals(job.getSalary(), rjob.getSalary());
        assertEquals(job.getDescription(), rjob.getDescription());
    }

    @Test
    void testToEntity() {
        JobEntity je = JobMapper.toEntity(JobMapper.toRemoteOkJobDto(job));
        assertNotNull(je);
        assertEquals(job.getId(), je.getId());
        assertEquals(job.getTitle(), je.getTitle());
        assertEquals(job.getCompanyName(), je.getCompanyName());
        assertEquals(job.getCompanyLogo(), je.getCompanyLogo());
        assertEquals(job.getUrl(), je.getUrl());
        assertEquals(job.getCategory(), je.getCategory());
        assertEquals(job.getJobType(), je.getJobType());
        assertEquals(job.getPublicationDate(), je.getPublicationDate());
        assertEquals(job.getCandidateRequiredLocation(), je.getCandidateRequiredLocation());
        assertEquals(job.getSalary(), je.getSalary());
        assertEquals(job.getDescription(), je.getDescription());
    }

    @Test
    void toRemoteOkJobDto() {
        Job rjob = JobMapper.toRemoteOkJobDto(job);
        assertNotNull(rjob);
        assertEquals(job.getId().toString(), rjob.getId());
        assertEquals(job.getTitle(), rjob.getSlug());
        assertEquals(job.getCompanyName(), rjob.getCompany());
        assertNotNull(rjob.getCompanyLogo());
        assertEquals(job.getCompanyLogo(), rjob.getCompanyLogo().toString());
        assertNotNull(rjob.getUrl());
        assertEquals(job.getUrl(), rjob.getUrl().toString());
        assertNotNull(rjob.getTags());
        assertTrue(job.getCategory().contains(rjob.getTags().getFirst()));
        assertEquals(job.getJobType(), rjob.getPosition());
        assertEquals(DateParser.parseToOffsetDateTime(job.getPublicationDate()), rjob.getDate());
        assertEquals(job.getCandidateRequiredLocation(), rjob.getLocation());
        assertNotNull(rjob.getSalaryMin());
        assertTrue(job.getSalary().contains(rjob.getSalaryMin().toString()));
        assertEquals(job.getDescription(), rjob.getDescription());
    }

    @Test
    void testToEntity1() throws Exception{
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
        JobEntity je = JobMapper.toEntity(jobDto);

        assertNotNull(je);
        assertNull( je.getId());
        assertEquals(jobDto.getTitle(), je.getTitle());
        assertEquals(jobDto.getCompanyName().getDisplay_name(), je.getCompanyName());
        assertEquals(jobDto.getRedirectUrl(), je.getUrl());
        assertEquals(jobDto.getCategory().getLabel(), je.getCategory());
        assertEquals(jobDto.getContractType(), je.getJobType());
        assertEquals(jobDto.getLocation().getDisplayName(), je.getCandidateRequiredLocation());
        assertTrue(je.getSalary().contains(jobDto.getSalaryMin().toString()));
        assertEquals(jobDto.getDescription(), je.getDescription());
    }

    @Test
    void toAdzunaJobDto() {
        AdzunaJobDto ajd = JobMapper.toAdzunaJobDto(job);
        assertNotNull(ajd);
        assertEquals(job.getId().toString(), ajd.getId());
        assertEquals(job.getTitle(), ajd.getTitle());
        assertEquals(job.getCompanyName(), ajd.getCompanyName().getDisplay_name());
        assertEquals(job.getUrl(), ajd.getRedirectUrl());
        assertTrue(job.getCategory().contains(ajd.getCategory().getLabel()));
        assertEquals(job.getJobType(), ajd.getContractType());
        assertEquals(job.getCandidateRequiredLocation(), ajd.getLocation().getDisplayName());
        assertTrue(job.getSalary().contains(ajd.getSalaryMin().toString()));
        assertEquals(job.getDescription(), ajd.getDescription());
    }

    @Test
    void testToEntity2() throws Exception{
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
        JobEntity je = JobMapper.toEntity(dto.getResult().getJobs().getFirst());
        assertNotNull(dto);
        assertNotNull(je);
        assertTrue(dto.isSuccess());
        assertEquals("API Terms of Service: Please link back...", dto.getLegal());

        assertNotNull(dto.getResult());
        assertEquals(10000, dto.getResult().getCount());


        assertEquals("Renewals & Sales Support Specialist (Remote)", je.getTitle());
        assertEquals("Remote", je.getJobType());
        assertEquals("Anywhere", je.getCandidateRequiredLocation());
        assertEquals("https://app.joinrise.co/jobs/gofundme-renewals-sales-support-specialist-remote-t23d", je.getUrl());
        assertEquals("GoFundMe", je.getCompanyName());
    }

    @Test
    void toJoinRiseJobDto() {
        JoinriseJobDto.RiseJobDto rjob = JobMapper.toJoinRiseJobDto(job);
        assertNotNull(rjob);
        assertEquals(job.getTitle(), rjob.getTitle());
        assertEquals(job.getCompanyName(), rjob.getOwner().getCompanyName());
        assertNotNull(rjob.getOwner().getPhoto());
        assertEquals(job.getCompanyLogo(), rjob.getOwner().getPhoto());
        assertNotNull(rjob.getUrl());
        assertEquals(job.getUrl(), rjob.getUrl());
        assertNotNull(rjob.getCategory());
        assertTrue(job.getCategory().contains(rjob.getCategory()));
        assertEquals(job.getJobType(), rjob.getType());
        assertEquals(job.getPublicationDate(), rjob.getCreatedAt());
        assertEquals(job.getCandidateRequiredLocation(), rjob.getLocationAddress());
        assertNotNull(rjob.getSalaryRangeMinYearly());
        assertTrue(job.getSalary().contains(rjob.getSalaryRangeMaxYearly().toString()));
    }
}