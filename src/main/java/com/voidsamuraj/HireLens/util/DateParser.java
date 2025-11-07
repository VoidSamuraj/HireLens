package com.voidsamuraj.HireLens.util;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Utility class for parsing and formatting date and time strings.
 *
 * This class provides methods to parse date/time strings in multiple ISO formats
 * into {@link OffsetDateTime} objects, assuming UTC offset where necessary.
 * It also provides formatting of {@link OffsetDateTime} objects back to strings
 * using the ISO offset date-time standard.
 *
 * Supported input formats:
 * - ISO_OFFSET_DATE_TIME (e.g., "2025-09-08T12:30:00Z")
 * - ISO_LOCAL_DATE_TIME (e.g., "2025-09-08T12:30:00")
 * - ISO_LOCAL_DATE (e.g., "2025-09-08")
 *
 * For local date/time inputs without timezone info, UTC offset is applied by default.
 *
 * Usage:
 * - parseToOffsetDateTime(String input): Attempts to parse a string into an OffsetDateTime.
 * - format(OffsetDateTime dateTime): Formats an OffsetDateTime into a String.
 * - format(String date): Convenience method to parse then format a date string.
 */

public class DateParser {

    private static final DateTimeFormatter OUTPUT_FORMAT =
            DateTimeFormatter.ISO_OFFSET_DATE_TIME;//ofPattern("yyyy-MM-dd HH:mm:ssXXX"); // XXX = offset e.g. +00:00

    private static final List<DateTimeFormatter> INPUT_FORMATS = List.of(
            DateTimeFormatter.ISO_OFFSET_DATE_TIME, // e.g. 2025-09-08T12:30:00Z
            DateTimeFormatter.ISO_LOCAL_DATE_TIME,  // e.g. 2025-09-08T12:30:00
            DateTimeFormatter.ISO_LOCAL_DATE        // e.g. 2025-09-08
    );


    /**
     * Parses a date/time string in one of the supported ISO formats into an OffsetDateTime.
     * If the input lacks an offset, UTC (+00:00) is assumed.
     *
     * @param input the date/time string to parse; may be null or blank
     * @return the parsed OffsetDateTime, or null if input is null or blank
     * @throws IllegalArgumentException if the input does not match any supported format
     */
    public static OffsetDateTime parseToOffsetDateTime(String input) {
        if (input == null || input.isBlank()) {
            return null;
        }

        for (DateTimeFormatter formatter : INPUT_FORMATS) {
            try {
                if (formatter == DateTimeFormatter.ISO_OFFSET_DATE_TIME) {
                    return OffsetDateTime.parse(input, formatter);
                }
                if (formatter == DateTimeFormatter.ISO_LOCAL_DATE_TIME) {
                    return LocalDateTime.parse(input, formatter).atOffset(ZoneOffset.UTC);
                }
                if (formatter == DateTimeFormatter.ISO_LOCAL_DATE) {
                    return LocalDate.parse(input, formatter).atStartOfDay().atOffset(ZoneOffset.UTC);
                }
            } catch (DateTimeParseException ignored) {
                // continue trying other formats
            }
        }

        throw new IllegalArgumentException("Unsupported date format: " + input);
    }

    /**
     * Formats an OffsetDateTime into a standardized ISO 8601 string with offset.
     *
     * @param dateTime the OffsetDateTime to format; may be null
     * @return the formatted date/time string, or null if dateTime is null
     */
    public static String format(OffsetDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(OUTPUT_FORMAT);
    }

    /**
     * Convenience method that parses the input string and then formats the resulting OffsetDateTime.
     *
     * @param date the date/time string to parse and format
     * @return the formatted date/time string
     * @throws IllegalArgumentException if the input cannot be parsed successfully
     */
    public static String format(String date) {
        return format(parseToOffsetDateTime(date));
    }
}
