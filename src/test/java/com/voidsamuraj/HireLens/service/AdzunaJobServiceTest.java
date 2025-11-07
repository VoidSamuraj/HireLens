package com.voidsamuraj.HireLens.service;

import com.voidsamuraj.HireLens.dto.Adzuna.*;
import com.voidsamuraj.HireLens.service.api.AdzunaJobService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AdzunaJobServiceTest {
    @Mock
    private RestTemplate restTemplate;

    private AdzunaJobService service;
    private AutoCloseable mocks;


    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        service = new AdzunaJobService(restTemplate);
    }
    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    public void testFetchJobs() {
        AdzunaJobDto jobDto = new AdzunaJobDto();
        jobDto.setId("abc123");
        jobDto.setTitle("Java Developer");
        jobDto.setCompanyName(new AdzunaCompanyDto("TechCorp"));
        jobDto.setRedirectUrl("https://adzuna.com/jobs/abc123");

        // Opcjonalnie ustaw kategorie i lokalizacje
        AdzunaLocationDto location = new AdzunaLocationDto();
        location.setDisplayName("London, UK");
        jobDto.setLocation(location);
        AdzunaCategoryDto category =  new AdzunaCategoryDto();
        category.setLabel("IT / Software");
        jobDto.setCategory(category);

        // Przygotowanie "wrappera" – jeśli Twój serwis oczekuje obiektu zawierającego listę jobów
        AdzunaResponseDto responseDto = new AdzunaResponseDto();
        responseDto.setResults(List.of(jobDto));

        // Mock RestTemplate, zwracający przygotowany obiekt
        Mockito.when(restTemplate.getForObject(Mockito.anyString(), Mockito.eq(AdzunaResponseDto.class)))
                .thenReturn(responseDto);

        // Wywołanie serwisu
        List<AdzunaJobDto> result = service.fetchJobs("https://api.adzuna.com/v1/api/jobs");

        // Assercje
        assertNotNull(result);               // nie może być null
        assertEquals(1, result.size());      // powinna być jedna pozycja
        assertEquals("Java Developer", result.getFirst().getTitle());
        assertEquals("TechCorp", result.getFirst().getCompanyName().getDisplay_name());
        assertEquals("https://adzuna.com/jobs/abc123", result.getFirst().getRedirectUrl());
        assertEquals("IT / Software", result.getFirst().getCategory().getLabel());
        assertEquals("London, UK", result.getFirst().getLocation().getDisplayName());
    }

}