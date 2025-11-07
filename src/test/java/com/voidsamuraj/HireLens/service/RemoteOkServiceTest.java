package com.voidsamuraj.HireLens.service;

import com.voidsamuraj.HireLens.client.remoteok.api.DefaultApi;
import com.voidsamuraj.HireLens.client.remoteok.dto.Job;
import com.voidsamuraj.HireLens.service.api.RemoteOkService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class RemoteOkServiceTest {

    @Mock
    private DefaultApi apiClient;

    private RemoteOkService service;
    private AutoCloseable mocks;
    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        service = new RemoteOkService(apiClient);
    }
    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }
    @Test
    void fetchJobs_noQuery_returnsAll() {
        Job job1 = new Job().slug("java-developer").description("Spring developer").tags(List.of("java","spring"));
        Job job2 = new Job().slug("python-developer").description("Django developer").tags(List.of("python","django"));

        when(apiClient.getJobs()).thenReturn(Flux.just(job1, job2));

        List<Job> result = service.fetchJobs(null);
        assertEquals(2, result.size());

        result = service.fetchAllJobs();
        assertEquals(2, result.size());
    }

    @Test
    void fetchJobs_withQuery_filtersCorrectly() {
        Job job1 = new Job().slug("java-developer").description("Spring developer").tags(List.of("java","spring"));
        Job job2 = new Job().slug("python-developer").description("Django developer").tags(List.of("python","django"));
        Job job3 = new Job().slug("senior-java").description("Backend engineer").tags(List.of("java","backend"));

        when(apiClient.getJobs()).thenReturn(Flux.just(job1, job2, job3));

        List<Job> result = service.fetchJobs("java spring");
        assertEquals(1, result.size());
        assertEquals("java-developer", result.getFirst().getSlug());

        List<Job> result2 = service.fetchJobs("java");
        assertEquals(2, result2.size()); // job1 i job3

        List<Job> result3 = service.fetchJobs("java engineer");
        assertEquals(1, result3.size()); // job1 i job3
    }
}