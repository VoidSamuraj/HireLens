package com.voidsamuraj.HireLens.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.voidsamuraj.HireLens.entity.JobEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.Locale;
import java.util.MissingResourceException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * LocationMapper provides methods to convert arbitrary location strings or coordinates
 * into ISO3 country codes. Uses OpenStreetMap Nominatim free API for geocoding.
 * Implements simple caching to reduce number of API requests.
 */
@Slf4j
@Component
public class LocationMapper {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private String toISO3(String iso2) {
        if (iso2 == null || iso2.isBlank()) return null;
        try {
            Locale locale = new Locale.Builder().setRegion(iso2).build();
            return locale.getISO3Country();
        } catch (MissingResourceException e) {
            return iso2;
        }
    }

    // Simple cache for raw input -> ISO3 code
    private final Map<String, String> cache = new HashMap<>();
    private final Map<String, Instant> cacheTimestamps = new HashMap<>();
    private final long CACHE_TTL_SECONDS = 24 * 60 * 60; // 1 day

    /**
     * Map an arbitrary location string to an ISO3 country code.
     * Ignores invalid or non-country strings (like timezone names).
     *
     * @param raw Raw location string (city, country, region, etc.)
     * @return ISO3 country code, or null if cannot map
     */
    public String mapLocation(String raw) {
        if (raw == null || raw.isBlank()) return null;

        // Skip non-country or timezone strings
        if (raw.matches("(?i).*(CET|GMT|UTC|HOURS|ZONE).*")) return null;

        // Check cache
        if (cache.containsKey(raw)) {
            Instant added = cacheTimestamps.get(raw);
            if (added.plusSeconds(CACHE_TTL_SECONDS).isAfter(Instant.now())) {
                return cache.get(raw);
            } else {
                cache.remove(raw);
                cacheTimestamps.remove(raw);
            }
        }

        try {
            String query = URLEncoder.encode(raw, "UTF-8");
            String urlStr = "https://nominatim.openstreetmap.org/search?q=" + query
                    + "&format=json&addressdetails=1&limit=1";

            HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
            conn.setRequestProperty("User-Agent", "MyApp/1.0 (contact@myapp.com)");

            try (Scanner sc = new Scanner(conn.getInputStream())) {
                String response = sc.useDelimiter("\\A").next();
                JsonNode node = MAPPER.readTree(response);

                if (node.isArray() && node.size() > 0) {
                    JsonNode address = node.get(0).path("address");
                    String code2 = address.path("country_code").asText("").toUpperCase();
                    if (!code2.isEmpty()) {
                        String iso3 = toISO3(code2);
                        cache.put(raw, iso3);
                        cacheTimestamps.put(raw, Instant.now());
                        return iso3;
                    }
                }
            }
        } catch (IOException e) {
            log.error("Geocoding error: {}", e.getMessage());
        }

        return null;
    }

    /**
     * Map latitude and longitude coordinates to an ISO3 country code.
     *
     * @param lat Latitude
     * @param lon Longitude
     * @return ISO3 country code, or null if cannot map
     */
    public String mapCoordinates(double lat, double lon) {
        String key = lat + "," + lon;
        if (cache.containsKey(key)) {
            Instant added = cacheTimestamps.get(key);
            if (added.plusSeconds(CACHE_TTL_SECONDS).isAfter(Instant.now())) {
                return cache.get(key);
            } else {
                cache.remove(key);
                cacheTimestamps.remove(key);
            }
        }

        try {
            String urlStr = "https://nominatim.openstreetmap.org/reverse?lat=" + lat + "&lon=" + lon
                    + "&format=json&addressdetails=1";

            HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
            conn.setRequestProperty("User-Agent", "MyApp/1.0 (contact@myapp.com)");

            try (Scanner sc = new Scanner(conn.getInputStream())) {
                String response = sc.useDelimiter("\\A").next();
                JsonNode node = MAPPER.readTree(response);
                JsonNode address = node.path("address");
                String code2 = address.path("country_code").asText("").toUpperCase();
                if (!code2.isEmpty()) {
                    String iso3 = toISO3(code2);
                    cache.put(key, iso3);
                    cacheTimestamps.put(key, Instant.now());
                    return iso3;
                }
            }
        } catch (IOException e) {
            log.error("Reverse geocoding error: {}", e.getMessage());
        }

        return null;
    }

    /**
     * Maps all JobEntity.CandidateRequiredLocation strings into ISO3 country codes.
     * Modifies JobEntity in place.
     *
     * @param jobs list of JobEntity objects
     */
    public void normalizeLocations(List<JobEntity> jobs) {
        if (jobs == null || jobs.isEmpty()) return;

        for (JobEntity job : jobs) {
            String rawLocation = job.getCandidateRequiredLocation(); // original field
            if (rawLocation != null && !rawLocation.isBlank()) {
                String iso3 = mapLocation(rawLocation);
                if (iso3 != null) {
                    job.setCandidateRequiredLocation(iso3);
                } else {
                    job.setCandidateRequiredLocation("UNK");
                    log.warn("Cannot map location: {}", rawLocation);
                }
            } else {
                job.setCandidateRequiredLocation(null);
            }
        }
    }

}
