package com.voidsamuraj.HireLens.entity;

/**
 * Enum representing common job experience levels.
 * <p>
 * Levels typically used to classify job offers and candidate experience.
 * </p>
 *
 * <ul>
 *   <li>{@code INTERN} - Internship or entry-level positions</li>
 *   <li>{@code JUNIOR} - Junior-level positions</li>
 *   <li>{@code MID} - Mid-level positions (default if unknown)</li>
 *   <li>{@code SENIOR} - Senior-level positions</li>
 *   <li>{@code ALL} - Represents all levels</li>
 * </ul>
 */
public enum JobLevel {
    INTERN,
    JUNIOR,
    MID,
    SENIOR,
    ALL;

    /**
     * Converts a string to the corresponding {@link JobLevel}.
     * <p>
     * If the input is {@code null} or unrecognized, returns {@link #MID} as default.
     * </p>
     *
     * @param str the string representation of the job level
     * @return corresponding {@link JobLevel} enum
     */
    public static JobLevel fromString(String str) {
        if (str == null) return MID;
        return switch (str.toLowerCase()) {
            case "intern" -> INTERN;
            case "junior" -> JUNIOR;
            case "senior" -> SENIOR;
            default -> MID;
        };
    }
}
