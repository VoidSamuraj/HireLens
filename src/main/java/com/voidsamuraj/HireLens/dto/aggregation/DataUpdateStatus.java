package com.voidsamuraj.HireLens.dto.aggregation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the status of a data update process, including UI visibility,
 * download and processing flags, and counts of processed and saved items.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataUpdateStatus {

    /** Indicates whether the data update UI window should be visible(invoked). */
    boolean updatingDataWindowVisible = false;

    /** Indicates whether data is currently being downloaded. */
    boolean downloadingData = false;

    /** Number of offers downloaded so far. */
    int downloadedOffersNumber = 0;

    /** Number of items downloaded from Remotive. */
    int remotiveCount = 0;

    /** Number of items downloaded from RemoteOK. */
    int remoteOkCount = 0;

    /** Number of items downloaded from Adzuna. */
    int adzunaCount = 0;

    /** Number of items downloaded from JoinRise. */
    int joinriseCount = 0;

    /** Indicates whether data is currently being processed by AI. */
    boolean processingByAI = false;

    /** Number of items processed by AI so far. */
    int processedByAINumber = 0;

    /** Indicates whether the processed data has been saved to the database. */
    boolean savedToDatabase = false;

    /** Indicates whether the process was cancelled. */
    boolean cancelled = false;

    /** Error message (tag names), if any occurred during the process. */
    String errorMessage = "";
}
