package com.voidsamuraj.HireLens.service.orchestrator;

import com.voidsamuraj.HireLens.dto.aggregation.*;
import com.voidsamuraj.HireLens.dto.ai.AnalysisResult;
import com.voidsamuraj.HireLens.entity.Groups;
import com.voidsamuraj.HireLens.entity.JobEntity;
import com.voidsamuraj.HireLens.entity.JobLevel;
import com.voidsamuraj.HireLens.mapper.JobMapper;
import com.voidsamuraj.HireLens.mapper.LocationMapper;
import com.voidsamuraj.HireLens.repository.GroupRepository;
import com.voidsamuraj.HireLens.repository.JobRepository;
import com.voidsamuraj.HireLens.service.ai.AiClientService;
import com.voidsamuraj.HireLens.service.api.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * Service responsible for orchestrating job data downloads from multiple job APIs,
 * processing, and saving aggregated results into the database.
 *
 * Supports asynchronous execution tracked by a unique job UUID, with status updates
 * sent over WebSocket through Spring's {@link SimpMessagingTemplate}.
 *
 * The service downloads data from Remotive, RemoteOK, Adzuna, and Joinrise job APIs,
 * maps received DTOs to internal {@link JobEntity} objects, aggregates and saves them.
 *
 * Throughout the process, publishes the current state of data download, processing, and persistence operations.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DownloadDataService {

    private final RemotiveJobService remotiveJobService;
    private final RemoteOkService remoteOkJobService;
    private final AdzunaJobService adzunaJobService;
    private final JoinriseJobService joinriseJobService;
    private final JobRepository jobRepository;
    private final GroupRepository groupRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final AiClientService aiClientService;
    private final UserJobDataService userJobDataService;
    private final LocationMapper locationMapper;


    private final ConcurrentHashMap<UUID, Future<?>> activeJobs = new ConcurrentHashMap<>();
    /**
     * Starts a new asynchronous job to download and process remote job data
     * according to the given payload parameters.
     *
     * @param payload the parameters controlling query string and max jobs fetched
     * @return a UUID uniquely identifying this asynchronous job execution
     */
    public UUID startJob(StartJobPayload payload) {
        UUID jobId = UUID.randomUUID();
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            try {
                userJobDataService.init(jobId);
                downloadData(jobId, payload);
            } catch (Exception e) {
                log.error("Error starting Job", e);
                sendCancelled(jobId, "Error starting Job");
                Thread.currentThread().interrupt();
            } finally {
                resetStatus(jobId);
                activeJobs.remove(jobId);
            }
        });
        activeJobs.put(jobId, future);
        return jobId;
    }

    /**
     * Returns the current aggregates data for the given job ID,
     * unless the job has been interrupted, in which case it returns null.
     *
     * @param id the UUID of the job to retrieve the aggregates for
     * @return the current {@link AggregatesDto} if the job is active; {@code null} if the job has been interrupted
     */
    public AggregatesDto getRestartData(UUID id){
        if(checkIfInterruptedAndReact(id))
            return null;
        else
            return userJobDataService.getAggregates();
    }

    /**
     * Attempts to stop the asynchronous job associated with the given {@code jobId}.
     * <p>
     * The method looks up the running job in the {@code activeJobs} map.
     * If found, it cancels the associated {@link Future} by invoking {@link Future#cancel(boolean)}
     * with {@code true}, which interrupts the executing thread if it is currently running.
     * <p>
     * Once cancelled, the job is removed from the active job registry and a WebSocket message
     * is sent to all subscribed clients to notify them that the job has been cancelled.
     *
     * @param jobId the unique identifier of the job to cancel
     */
    public void stopJob(UUID jobId) {
        Future<?> job = activeJobs.get(jobId);
        if (job != null) {
            job.cancel(true);
            activeJobs.remove(jobId);
            sendCancelled(jobId);
        }
    }

    /**
     * Sends a cancellation notification to all WebSocket clients subscribed to the
     * {@code /dataUpdate/{jobId}} topic.
     * <p>
     * The message payload is a {@link DataUpdateStatus} object with its
     * {@code cancelled} flag set to {@code true}, allowing the frontend
     * to react appropriately (e.g. hide progress bars, display a cancelled state, etc.).
     *
     * @param jobId the identifier of the job that was cancelled
     */
    private void sendCancelled(UUID jobId) {
        DataUpdateStatus status = new DataUpdateStatus();
        status.setCancelled(true);
        WsPayload<DataUpdateStatus> cancelled = new WsPayload<>(
                WsPayload.DataType.STATUS,
                status
        );
        messagingTemplate.convertAndSend("/dataUpdate/" + jobId, cancelled);
    }
    /**
     * Sends a cancellation notification to all WebSocket clients subscribed to the
     * {@code /dataUpdate/{jobId}} topic.
     * <p>
     * The message payload is a {@link DataUpdateStatus} object with its
     * {@code cancelled} flag set to {@code true}, allowing the frontend
     * to react appropriately (e.g. hide progress bars, display a cancelled state, etc.).
     *
     * @param jobId the identifier of the job that was cancelled
     * @param error the error that caused cancellation
     */
    private void sendCancelled(UUID jobId, String error) {
        DataUpdateStatus status = new DataUpdateStatus();
        status.setCancelled(true);
        status.setErrorMessage(error);
        WsPayload<DataUpdateStatus> cancelled = new WsPayload<>(
                WsPayload.DataType.STATUS,
                status
        );
        messagingTemplate.convertAndSend("/dataUpdate/" + jobId, cancelled);
    }
    /**
     * Executes the main data download, aggregation, processing, and saving logic asynchronously.
     * Updates clients on progress via WebSocket messages.
     *
     * @param jobId the unique id for tracking job progress and status
     * @param payload the input payload containing query and pagination parameters
     */
    private void downloadData(UUID jobId, StartJobPayload payload) {

        resetStatus(jobId);
        List<JobEntity> newJobs = new ArrayList<>();
        int downloadedCount;
        List<String> errors = new ArrayList<>();
        // Initialize download count
        setDownloadingNumber(jobId, 0, 0, 0, 0, 0, "");
        int pageNumber = 1;
        int allJobsSize=0;
        int remotiveCount, remoteOkCount, adzunaCount, joinriseCount;
        boolean wasEmpty;

        Map<String, Integer> savedLocations  = jobRepository.getLocationCounts(payload.getQuery()).stream()
                .collect(Collectors.toMap(JobRepository.SkillCount::skill, sc -> sc.total().intValue()));
        Map<String, SkillData> savedSkills  = jobRepository.getSkillSums(payload.getQuery()).stream()
                .collect(Collectors.toMap(
                        JobRepository.SkillProjection::getSkill,
                        s -> new SkillData(s.getGroupName(), s.getTotalLevel())
                ));

        userJobDataService.setLocations(savedLocations);
        userJobDataService.setSkills(savedSkills);

        // TODO: re-evaluate pagination handling for API calls if needed
        do {
            newJobs.clear();
            errors.clear();
            wasEmpty = true;
            remotiveCount = 0;
            remoteOkCount = 0;
            adzunaCount = 0;
            joinriseCount = 0;

            try {
                List<JobEntity> entities = fetchJobsFromService(remotiveJobService, JobMapper::toEntity, payload, pageNumber, errors);
                if (!entities.isEmpty())
                    wasEmpty = false;
                entities = filterJobs(entities);
                remotiveCount = entities.size();
                newJobs.addAll(entities);
            } catch (Exception e) {
                errors.add("fetchRemotiveError");
                log.error("Error fetching Remotive jobs", e);
            }
            downloadedCount = allJobsSize + newJobs.size();
            setDownloadingNumber(jobId, downloadedCount, remotiveCount, remoteOkCount, adzunaCount, joinriseCount, String.join(" ", errors));
            if (checkIfInterruptedAndReact(jobId))
                return;
            try {
                List<JobEntity> entities = fetchJobsFromService(remoteOkJobService, JobMapper::toEntity, payload, pageNumber, errors);
                if (!entities.isEmpty())
                    wasEmpty = false;
                entities = filterJobs(entities);
                remoteOkCount = entities.size();
                newJobs.addAll(entities);
            } catch (Exception e) {
                errors.add("fetchRemoteOkError");
                log.error("Error fetching RemoteOk jobs", e);
            }
            downloadedCount = allJobsSize + newJobs.size();
            setDownloadingNumber(jobId, downloadedCount, remotiveCount, remoteOkCount, adzunaCount, joinriseCount, String.join(" ", errors));
            if (checkIfInterruptedAndReact(jobId))
                return;
            try {
                List<JobEntity> entities = fetchJobsFromService(adzunaJobService, JobMapper::toEntity, payload, pageNumber, errors);
                if (!entities.isEmpty())
                    wasEmpty = false;
                entities = filterJobs(entities);
                adzunaCount = entities.size();
                newJobs.addAll(entities);
            } catch (Exception e) {
                errors.add("fetchAdzunaError");
                log.error("Error fetching Adzuna jobs", e);
            }
            downloadedCount = allJobsSize + newJobs.size();
            setDownloadingNumber(jobId, downloadedCount, remotiveCount, remoteOkCount, adzunaCount, joinriseCount, String.join(" ", errors));
            if (checkIfInterruptedAndReact(jobId))
                return;
            try {
                List<JobEntity> entities = fetchJobsFromService(joinriseJobService, JobMapper::toEntity, payload, pageNumber, errors);
                if (!entities.isEmpty())
                    wasEmpty = false;
                entities = filterJobs(entities);
                joinriseCount = entities.size();
                newJobs.addAll(entities);
            } catch (Exception e) {
                errors.add("fetchJoinriseError");
                log.error("Error fetching Joinrise jobs", e);
            }
            downloadedCount = allJobsSize + newJobs.size();
            setDownloadingNumber(jobId, downloadedCount, remotiveCount, remoteOkCount, adzunaCount, joinriseCount, String.join(" ", errors));
            if (checkIfInterruptedAndReact(jobId))
                return;

            newJobs.forEach(it-> it.setDescription(
                    Jsoup.parse(it.getDescription()).text()
                            .replaceAll("\\s{2,}", " ").trim()
            ));

            try {
                for (int i = 0; i < newJobs.size(); i++) {
                    if (checkIfInterruptedAndReact(jobId))
                        return;
                    JobEntity job = newJobs.get(i);
                    String data = String.join(" ",
                            job.getExperienceLevel() != null ? "Seniority: "+job.getExperienceLevel().name() : "",
                            job.getTitle(),
                            job.getDescription()
                    ).trim();
                    AnalysisResult result = aiClientService.analyzeJob(data);
                    job.setSkills(result.getSkills());
                    job.setExperienceLevel(JobLevel.fromString(result.getSeniority()));
                    updateStatus(jobId, true, true, downloadedCount, remotiveCount, remoteOkCount, adzunaCount, joinriseCount, true, allJobsSize + i + 1, false, String.join(" ", errors));
                }
            } catch (Exception e) {
                errors.add("aiProcessingError");
                log.error("Error AI processing jobs", e);
                return;
            }

            //Get list of all skills from newJobs
            List<String> skills = newJobs.stream()
                    .flatMap(job -> job.getSkills().stream())
                    .map(s -> s.getGroup().getSkill().toLowerCase())
                    .toList();

            //Search for existing entries in database
            Map<String, Groups> existingGroupsMap = groupRepository.findExistingGroups(skills).stream()
                    .collect(Collectors.toMap(
                            g -> g.getSkill().toLowerCase(),
                            g -> g
                    ));

            //get not existing groups
            List<String> missingSkills = skills.stream()
                    .filter(skill -> !existingGroupsMap.containsKey(skill.toLowerCase()))
                    .distinct()
                    .toList();

            //assign groups to new skills and save in database
            Map<String, String> newGroups = missingSkills.isEmpty()
                    ? Collections.emptyMap()
                    : aiClientService.groupSkills(missingSkills);

            if (!newGroups.isEmpty()) {
                List<Groups> groupsToSave = newGroups.entrySet().stream()
                        .map(e -> new Groups(e.getKey(), e.getValue()))
                        .toList();
                groupRepository.saveAll(groupsToSave);

                newGroups.forEach((skill, groupName) ->
                        existingGroupsMap.put(skill.toLowerCase(), new Groups(skill, groupName))
                );
            }

            //update group data in system(locally)
            newJobs.forEach(job ->
                    job.getSkills().forEach(skill -> {
                        Groups group = existingGroupsMap.get(skill.getGroup().getSkill().toLowerCase());
                        if (group != null) {
                            skill.setGroup(group);
                        }
                    })
            );

            //Saving data
            if (!newJobs.isEmpty()) {
                updateData(jobId, newJobs);
                try {
                    locationMapper.normalizeLocations(newJobs);
                    jobRepository.saveAll(newJobs);
                    updateStatus(jobId, true, true, downloadedCount, remotiveCount, remoteOkCount, adzunaCount, joinriseCount, true, downloadedCount, true, String.join(" ", errors));
                } catch (Exception e) {
                    errors.add("databaseError");
                    log.error("Error saving jobs in db", e);
                }
            }
            ++pageNumber;
            allJobsSize+= newJobs.size();
        }while(!wasEmpty);
        updateStatus(jobId, false, false,  0, 0, 0, 0, 0, false, 0, false, "");
    }

    private <T>  List<JobEntity> fetchJobsFromService(JobService<T> service, Function<T, JobEntity> mapper,
                                                      StartJobPayload payload,
                                                      int pageNumber, List<String> errors) {
        try {
            return  service.fetchJobs(payload.getQuery(), payload.getMaxJobOffers(), pageNumber)
                    .stream()
                    .map(mapper)
                    .toList();
        } catch (Exception e) {
            errors.add(service.getClass().getSimpleName() + "Error");
            log.error("Error fetching jobs from " + service.getClass().getSimpleName(), e);
        }
        return  new ArrayList<>();
    }
    /**
     *  Filter out jobs which are already in database
     * @param allJobs - list off jobs to check
     * @return List of new jobs
     */
    private List<JobEntity> filterJobs(List<JobEntity> allJobs){
        List<String> keysToCheck = allJobs.stream()
                .map(job -> job.getApiName() + ":" + job.getApiId())
                .toList();

        Set<String> existingKeys = jobRepository.findKeysIn(keysToCheck);

        return allJobs.stream()
                .filter(job -> !existingKeys.contains(job.getApiName() + ":" + job.getApiId()))
                .toList();
    }
    /**
     * Updates clients with the current download progress by sending WebSocket messages.
     *
     * @param jobId the job identifier
     * @param downloadedCount the current number of downloaded job offers
     * @param errorsCodes whether error appeared, then provide keys of error
     */
    private void setDownloadingNumber(UUID jobId, int downloadedCount, int remotiveCount, int remoteOkCount, int adzunaCount, int joinriseCount, String errorsCodes){
        updateStatus(jobId, true, true, downloadedCount, remotiveCount, remoteOkCount, adzunaCount, joinriseCount,false,0,false, errorsCodes);

    }

    /**
     * Sends a detailed status update message to the clients listening on the WebSocket channel.
     *
     * @param jobId the job identifier
     * @param isUpdatingDataWindowVisible controls visibility of the data update window in UI
     * @param isDownloadingData whether data is being downloaded currently
     * @param downloadedOffersNumber number of job offers downloaded so far
     * @param isProcessingByAI whether AI processing is active
     * @param processedByAINumber number of job offers processed by AI
     * @param savedToDatabase whether data has been saved to the database
     * @param errorsCodes whether error appeared, then provide keys of error
     */
    private void updateStatus(
            UUID jobId,
            boolean isUpdatingDataWindowVisible,
            boolean isDownloadingData,
            int downloadedOffersNumber,
            int remotiveCount,
            int remoteOkCount,
            int adzunaCount,
            int joinriseCount,
            boolean isProcessingByAI,
            int processedByAINumber,
            boolean savedToDatabase,
            String errorsCodes
    ) {
        WsPayload<DataUpdateStatus> jobStatus = new WsPayload<>(
                WsPayload.DataType.STATUS,
                new DataUpdateStatus(
                        isUpdatingDataWindowVisible,
                        isDownloadingData,
                        downloadedOffersNumber,
                        remotiveCount,
                        remoteOkCount,
                        adzunaCount,
                        joinriseCount,
                        isProcessingByAI,
                        processedByAINumber,
                        savedToDatabase,
                        false,
                        errorsCodes)
        );

        messagingTemplate.convertAndSend("/dataUpdate/" + jobId, jobStatus);
    }
    private void updateData(
            UUID jobId,
            List<JobEntity> jobs
    ) {
        userJobDataService.addJobs(jobs);
        updateData(jobId);
    }
    private void updateData(UUID jobId) {
        AggregatesDto aDto = userJobDataService.getAggregates();
        WsPayload<AggregatesDto> jobStatus = new WsPayload<>(WsPayload.DataType.CHART_MAP, aDto);

        messagingTemplate.convertAndSend("/dataUpdate/" + jobId, jobStatus);
    }


    /**
     * Resets the status indicators to initial/default values by notifying clients.
     *
     * @param jobId the job identifier
     */
    private void resetStatus(UUID jobId) {
        WsPayload<DataUpdateStatus> jobStatus = new WsPayload<>(
                WsPayload.DataType.STATUS,
                new DataUpdateStatus()
        );
        messagingTemplate.convertAndSend("/dataUpdate/" + jobId, jobStatus);
    }

    /**
     * Checks whether the current thread has been interrupted and reacts by sending
     * a cancellation event via WebSocket if so.
     *
     * <p>This method is intended to be called periodically during long-running
     * operations to allow cooperative cancellation of asynchronous jobs.</p>
     *
     * @param jobId the UUID of the job being executed, used to notify clients
     *              about cancellation
     * @return {@code true} if the thread was interrupted and cancellation was sent,
     *         {@code false} otherwise
     */
    private boolean checkIfInterruptedAndReact(UUID jobId){
        if (Thread.currentThread().isInterrupted()) {
            sendCancelled(jobId);
            return true;
        }
        return false;
    }

}
