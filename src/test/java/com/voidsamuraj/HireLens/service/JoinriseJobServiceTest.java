package com.voidsamuraj.HireLens.service;

import com.voidsamuraj.HireLens.dto.Joinrise.JoinriseJobDto;
import com.voidsamuraj.HireLens.service.api.JoinriseJobService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class  JoinriseJobServiceTest {
    @Mock
    private RestTemplate restTemplate;

    private JoinriseJobService service;
    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        service = new JoinriseJobService(restTemplate);
    }
    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }
    @Test
    void fetchJobs_withQuery_filtersCorrectly() {
        // przygotowanie danych
        JoinriseJobDto.RiseJobDto job1 = new JoinriseJobDto.RiseJobDto();
        job1.setTitle("Backend Engineer");
        job1.setOwner(new JoinriseJobDto.RiseJobDto.Owner());
        job1.getOwner().setCompanyName("TechCorp");
        job1.setCategory("Engineering");
        job1.setType("Full-Time");

        JoinriseJobDto.RiseJobDto job2 = new JoinriseJobDto.RiseJobDto();
        job2.setTitle("Marketing Manager");
        job2.setOwner(new JoinriseJobDto.RiseJobDto.Owner());
        job2.getOwner().setCompanyName("MarketInc");
        job2.setCategory("Marketing");
        job2.setType("Part-Time");

        JoinriseJobDto.Result result = new JoinriseJobDto.Result();
        result.setJobs(List.of(job1, job2));

        JoinriseJobDto responseDto = new JoinriseJobDto();
        responseDto.setResult(result);

        when(restTemplate.getForObject(anyString(), eq(JoinriseJobDto.class)))
                .thenReturn(responseDto);

        // test
        List<JoinriseJobDto.RiseJobDto> filtered = service.fetchJobs("backend techcorp");

        assertEquals(1, filtered.size());
        assertEquals("Backend Engineer", filtered.get(0).getTitle());
    }

    @Test
    void fetchJobs_emptyQuery_returnsAll() {
        JoinriseJobDto.RiseJobDto job1 = new JoinriseJobDto.RiseJobDto();
        job1.setTitle("Backend Engineer");

        JoinriseJobDto.RiseJobDto job2 = new JoinriseJobDto.RiseJobDto();
        job2.setTitle("Marketing Manager");

        JoinriseJobDto.Result result = new JoinriseJobDto.Result();
        result.setJobs(List.of(job1, job2));

        JoinriseJobDto responseDto = new JoinriseJobDto();
        responseDto.setResult(result);

        when(restTemplate.getForObject(anyString(), eq(JoinriseJobDto.class)))
                .thenReturn(responseDto);

        List<JoinriseJobDto.RiseJobDto> allJobs = service.fetchJobs("");

        assertEquals(2, allJobs.size());
    }
}