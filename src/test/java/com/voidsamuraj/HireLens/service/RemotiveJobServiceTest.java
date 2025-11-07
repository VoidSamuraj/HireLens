package com.voidsamuraj.HireLens.service;

import com.voidsamuraj.HireLens.dto.Remotive.RemotiveJobDto;
import com.voidsamuraj.HireLens.dto.Remotive.RemotiveResponseDto;
import com.voidsamuraj.HireLens.service.api.RemotiveJobService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RemotiveJobServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private RemotiveJobService jobService;

    @Test
    void testFetchJobs() {
        RemotiveJobDto jobDto = new RemotiveJobDto();
        jobDto.setId(123L);
        jobDto.setTitle("Lead Developer");
        jobDto.setCompanyName("Remotive");

        RemotiveResponseDto responseDto = new RemotiveResponseDto();
        responseDto.setJobCount(1);
        responseDto.setJobs(List.of(jobDto));

        Mockito.when(restTemplate.getForObject(Mockito.anyString(), Mockito.eq(RemotiveResponseDto.class)))
                .thenReturn(responseDto);

        List<RemotiveJobDto> result = jobService.fetchJobs("https://remotive.com/api/remote-jobs?limit=1");
        assertNotNull(result); // <--- tu nie może być null
        assertEquals(1, result.size());
        assertEquals("Lead Developer", result.getFirst().getTitle());
    }
}
