package com.voidsamuraj.HireLens.mapper;

import com.voidsamuraj.HireLens.client.remoteok.dto.Job;
import com.voidsamuraj.HireLens.dto.Adzuna.AdzunaCategoryDto;
import com.voidsamuraj.HireLens.dto.Adzuna.AdzunaCompanyDto;
import com.voidsamuraj.HireLens.dto.Adzuna.AdzunaJobDto;
import com.voidsamuraj.HireLens.dto.Adzuna.AdzunaLocationDto;
import com.voidsamuraj.HireLens.dto.Joinrise.JoinriseJobDto;
import com.voidsamuraj.HireLens.dto.aggregation.JobDto;
import com.voidsamuraj.HireLens.entity.ApiName;
import com.voidsamuraj.HireLens.entity.JobEntity;
import com.voidsamuraj.HireLens.dto.Remotive.RemotiveJobDto;
import com.voidsamuraj.HireLens.entity.JobLevel;
import com.voidsamuraj.HireLens.util.DateParser;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Mapper component responsible for converting between various job DTOs and {@link JobEntity}.
 * <p>
 * Supports mapping for multiple external job APIs, including:
 * <ul>
 *   <li>Remotive</li>
 *   <li>RemoteOK</li>
 *   <li>Adzuna</li>
 *   <li>Joinrise</li>
 * </ul>
 * as well as a generic web job representation {@link JobDto}.
 * </p>
 */
public class JobMapper {
    public static JobDto toJobDto(final JobEntity entity) {
        return JobDto.builder()
                .id(entity.getId().intValue())
                .title(entity.getTitle())
                .companyName(entity.getCompanyName())
                .companyLogo(entity.getCompanyLogo())
                .url(entity.getUrl())
                .category(entity.getCategory())
                .jobType(entity.getJobType())
                .publicationDate(entity.getPublicationDate())
                .candidateRequiredLocation(entity.getCandidateRequiredLocation())
                .salary(entity.getSalary())
                .description(entity.getDescription())
                .build();
    }

    public static JobEntity toEntity(final RemotiveJobDto dto) {
        return JobEntity.builder()
                .apiId(dto.getId().toString())
                .apiName(ApiName.REMOTIVE)
                .title(dto.getTitle())
                .companyLogo(dto.getCompanyLogo())
                .companyName(dto.getCompanyName())
                .url(dto.getUrl())
                .category(dto.getCategory())
                .jobType(dto.getJobType())
                .publicationDate(DateParser.format(dto.getPublicationDate()))
                .candidateRequiredLocation(dto.getCandidateRequiredLocation())
                .salary(dto.getSalary())
                .description(dto.getDescription())
                .build();
    }

    public static RemotiveJobDto toRemotiveJobDto(final JobEntity entity) {
        RemotiveJobDto dto = new RemotiveJobDto();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setCompanyLogo(entity.getCompanyLogo());
        dto.setCompanyName(entity.getCompanyName());
        dto.setUrl(entity.getUrl());
        dto.setCategory(entity.getCategory());
        dto.setJobType(entity.getJobType());
        dto.setPublicationDate(DateParser.format(entity.getPublicationDate()));
        dto.setCandidateRequiredLocation(entity.getCandidateRequiredLocation());
        dto.setSalary(entity.getSalary());
        dto.setDescription(entity.getDescription());
        return dto;
    }

    public static JobEntity toEntity(final Job dto) {
        String url = dto.getUrl() != null ? dto.getUrl().toString() : "";
        String dateStr = dto.getDate() != null ? DateParser.format(dto.getDate()) : "";
        String id = dto.getId() !=null? dto.getId():"";
        String logo = dto.getCompanyLogo() != null? dto.getCompanyLogo().toString():null;
        return JobEntity.builder()
                .apiId(id)
                .apiName(ApiName.REMOTEOK)
                .title(dto.getSlug())
                .companyName(dto.getCompany())
                .url(url)
                .companyLogo(logo)
                .category(String.join(", ", Optional.ofNullable(dto.getTags()).orElse(List.of())))
                .publicationDate(dateStr)
                .candidateRequiredLocation(dto.getLocation())
                .salary(String.format("%d-%d",dto.getSalaryMin(), dto.getSalaryMax()))
                .description(dto.getDescription())
                .build();
    }

    public static Job toRemoteOkJobDto(final JobEntity entity) {
        String salary = entity.getSalary();
        Integer minSalary = null;
        Integer maxSalary = null;

        if (salary != null && !salary.isBlank()) {
            String[] parts = Arrays.stream(salary.split("-"))
                    .filter(s -> !s.isBlank())
                    .toArray(String[]::new);
            if (parts.length == 2) {
                minSalary = Integer.valueOf(parts[0].trim());
                maxSalary = Integer.valueOf(parts[1].trim());
            } else if (parts.length == 1) {
                minSalary = Integer.valueOf(parts[0].trim());
                maxSalary = Integer.valueOf(parts[0].trim());
            }
        }
        Job dto = new Job();
        dto.setId(entity.getId().toString());
        dto.setSlug(entity.getTitle());
        dto.setCompany(entity.getCompanyName());
        dto.setCompanyLogo(URI.create(entity.getCompanyLogo()));
        dto.setUrl(URI.create(entity.getUrl()));
        dto.setTags(Arrays.stream(entity.getCategory().split(",\\s*"))
                .map(String::trim)
                .toList());
        dto.setPosition(entity.getJobType());
        dto.setDate(DateParser.parseToOffsetDateTime(entity.getPublicationDate()));
        dto.setLocation(entity.getCandidateRequiredLocation());
        dto.setSalaryMin(minSalary);
        dto.setSalaryMax(maxSalary);
        dto.setDescription(entity.getDescription());
        return dto;
    }
    public static JobEntity toEntity(final AdzunaJobDto dto) {
        String category = Stream.of(dto.getCategory().getLabel(), dto.getCategory().getTag())
                .filter(Objects::nonNull)
                .collect(Collectors.joining(", "));
        String location = dto.getLocation() != null ?dto.getLocation().getDisplayName():"";
        String id = dto.getId() !=null? dto.getId():"";

        return JobEntity.builder()
                .apiId(id)
                .apiName(ApiName.ADZUNA)
                .title(dto.getTitle())
                .companyName(dto.getCompanyName().getDisplay_name())
                .url(dto.getRedirectUrl())
                .category(category)
                .jobType(dto.getContractType())
                .publicationDate("")
                .candidateRequiredLocation(location)
                .salary(String.format("%d - %d",dto.getSalaryMin(), dto.getSalaryMax()))
                .description(dto.getDescription())
                .build();
    }

    public static AdzunaJobDto toAdzunaJobDto(final JobEntity entity) {
        String salary = entity.getSalary();
        Integer minSalary = null;
        Integer maxSalary = null;

        if (salary != null && !salary.isBlank()) {
            String[] parts = Arrays.stream(salary.split("-"))
                    .filter(s -> !s.isBlank())
                    .toArray(String[]::new);
            if (parts.length == 2) {
                minSalary = Integer.valueOf(parts[0].trim());
                maxSalary = Integer.valueOf(parts[1].trim());
            } else if (parts.length == 1) {
                minSalary = Integer.valueOf(parts[0].trim());
                maxSalary = Integer.valueOf(parts[0].trim());
            }
        }
        AdzunaJobDto dto = new AdzunaJobDto();
        dto.setId(entity.getId().toString());
        dto.setTitle(entity.getTitle());
        dto.setCompanyName(new AdzunaCompanyDto(entity.getCompanyName()));
        dto.setRedirectUrl(entity.getUrl());
        List<String> category = Arrays.stream(entity.getCategory().split(",\\s*"))
                .map(String::trim)
                .toList();
        dto.setCategory(new AdzunaCategoryDto());
        dto.getCategory().setLabel(category.getFirst());
        dto.getCategory().setTag(category.getLast());
        dto.setContractType(entity.getJobType());
        dto.setLocation(new AdzunaLocationDto());
        dto.getLocation().setDisplayName(entity.getCandidateRequiredLocation());
        dto.setSalaryMin(minSalary);
        dto.setSalaryMin(maxSalary);
        dto.setDescription(entity.getDescription());
        return dto;
    }

    public static JobEntity toEntity(final JoinriseJobDto.RiseJobDto dto) {

        System.out.println("JOINRISE Type "+dto.getType());
        System.out.println("JOINRISE WorkModel "+dto.getDescriptionBreakdown().getWorkModel());
        System.out.println("JOINRISE employmentType "+dto.getDescriptionBreakdown().getEmploymentType());
        System.out.println("JOINRISE getOneSentenceJobSummary "+dto.getDescriptionBreakdown().getOneSentenceJobSummary());
        System.out.println("JOINRISE keywords "+dto.getDescriptionBreakdown().getKeywords());
        System.out.println("JOINRISE skillrequirements ");
        dto.getDescriptionBreakdown().getSkillRequirements().forEach(System.out::println);


        return JobEntity.builder()
                .apiId(dto.getId())
                .apiName(ApiName.JOINRISE)
                .title(dto.getTitle())
                .experienceLevel(JobLevel.fromString(dto.getSeniority()))
                .companyLogo(Optional.ofNullable(dto.getOwner()).map(JoinriseJobDto.RiseJobDto.Owner::getPhoto).orElse(""))
                .companyName(Optional.ofNullable(dto.getOwner()).map(JoinriseJobDto.RiseJobDto.Owner::getCompanyName).orElse(""))
                .url(dto.getUrl())
                .category(dto.getCategory())
                .jobType(dto.getType())
                .publicationDate(DateParser.format(dto.getCreatedAt()))
                .candidateRequiredLocation(dto.getLocationAddress())
                .salary((dto.getSalaryRangeMinYearly()/12) +" - "+(dto.getSalaryRangeMaxYearly()/12) )
                .description("")
                .build();
    }

    public static JoinriseJobDto.RiseJobDto toJoinRiseJobDto(final JobEntity entity) {
        JoinriseJobDto.RiseJobDto dto = new JoinriseJobDto.RiseJobDto();
        dto.setId(entity.getApiId());
        dto.setTitle(entity.getTitle());
        dto.setSeniority(entity.getExperienceLevel().name());
        dto.setOwner(new JoinriseJobDto.RiseJobDto.Owner());
        dto.getOwner().setCompanyName(entity.getCompanyName());
        dto.getOwner().setPhoto(entity.getCompanyLogo());
        dto.setUrl(entity.getUrl());
        dto.setCategory(entity.getCategory());
        dto.setType(entity.getJobType());
        dto.setCreatedAt(DateParser.format(entity.getPublicationDate()));
        dto.setLocationAddress(entity.getCandidateRequiredLocation());
        try {
            dto.setSalaryRangeMinYearly(Integer.parseInt(entity.getSalary().split(" - ")[0]));
            dto.setSalaryRangeMaxYearly(Integer.parseInt(entity.getSalary().split(" - ")[1]));
        }catch(Exception ignored){}
        return dto;
    }

}