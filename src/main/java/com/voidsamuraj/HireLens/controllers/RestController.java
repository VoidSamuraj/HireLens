package com.voidsamuraj.HireLens.controllers;

import com.voidsamuraj.HireLens.dto.aggregation.AggregatesDto;
import com.voidsamuraj.HireLens.dto.aggregation.StartJobPayload;
import com.voidsamuraj.HireLens.service.orchestrator.DownloadDataService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * REST Controller responsible for managing data download jobs.
 * <p>
 * Provides endpoints to start and stop jobs.
 * </p>
 */
@org.springframework.web.bind.annotation.RestController
@AllArgsConstructor
public class RestController {
    /**
     * Service responsible for the business logic of starting and stopping jobs.
     */
    private final DownloadDataService downloadDataService;

    /**
     * Starts a new data download job.
     *
     * @param payload Input data required to start the job (StartJobPayload).
     * @return Map containing the ID of the started job ("jobId").
     */
    @PostMapping("/api/startJob")
    public ResponseEntity<Map<String, String>> startJob(@RequestBody StartJobPayload payload) {
        UUID jobId = downloadDataService.startJob(payload);
        Map<String, String> response = new HashMap<>();
        response.put("jobId", jobId.toString());
        return ResponseEntity.ok(response);
    }
    /**
     * Stops a running job.
     *
     * @param jobId UUID of the job to be stopped.
     * @return ResponseEntity with HTTP status 200 OK if successful.
     */
    @PostMapping("/api/stopJob")
    public ResponseEntity<Void> stopJob(@RequestBody UUID jobId) {
        System.out.println("STOP /stopJob");
        downloadDataService.stopJob(jobId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    //TODO add data no query
    /**
     * Gets init data for charts.
     *
     * @return Init Data
     */
    @GetMapping("/init")
    public AggregatesDto getInitData(@RequestParam UUID jobId) {
        AggregatesDto data = downloadDataService.getRestartData(jobId);
        //if(data != null)

        return data;
    }
}
