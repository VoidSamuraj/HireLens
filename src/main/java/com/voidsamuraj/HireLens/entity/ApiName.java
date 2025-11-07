package com.voidsamuraj.HireLens.entity;

/**
 * Enum representing supported API names for job offer sources.
 * <p>
 * Each constant corresponds to a different external job API integrated in the system.
 * </p>
 *
 * <ul>
 *   <li>{@code ADZUNA} - Adzuna job listings API</li>
 *   <li>{@code JOINRISE} - JoinRise job listings API</li>
 *   <li>{@code REMOTEOK} - RemoteOK job listings API</li>
 *   <li>{@code REMOTIVE} - Remotive job listings API</li>
 * </ul>
 */
public enum ApiName {
    ADZUNA,
    JOINRISE,
    REMOTEOK,
    REMOTIVE
}