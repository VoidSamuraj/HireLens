import { createSlice } from "@reduxjs/toolkit";
import type { PayloadAction } from '@reduxjs/toolkit';
import type { DataUpdateStatus } from '../utils/types';

// Initial state for controlling the status of the data update process
const initialState: DataUpdateStatus = {
    updatingDataWindowVisible: false,
    downloadingData: false,
    downloadedOffersNumber: 0,
    remotiveCount: 0,
    remoteOkCount: 0,
    adzunaCount: 0,
    joinriseCount: 0,
    processingByAI: false,
    processedByAINumber: 10,
    savedToDatabase: false,
    cancelled: false,
    errorMessage: ""
}

/**
 * Redux slice managing the state of job data update progress.
 *
 * Contains reducers to update individual status flags and counters as well as a bulk update reducer
 * that sets the entire state based on a provided DataUpdateStatus object.
 *
 * Supports:
 * - Visibility of the update window
 * - Downloading progress and count of downloaded offers
 * - AI processing status and processed offers count
 * - Database saving status
 * - Cancellation flag
 * - Error Message
 * - Resetting all states to initial defaults
 */
const searchStateSlice = createSlice({
    name: "searchState",
    initialState,
    reducers: {
        setSearchState: (state, action: PayloadAction<DataUpdateStatus>) => {
            state.updatingDataWindowVisible = action.payload.updatingDataWindowVisible;
            state.downloadingData = action.payload.downloadingData;
            state.downloadedOffersNumber = action.payload.downloadedOffersNumber;
            state.remotiveCount = action.payload.remotiveCount;
            state.remoteOkCount = action.payload.remoteOkCount;
            state.adzunaCount = action.payload.adzunaCount;
            state.joinriseCount = action.payload.joinriseCount;
            state.processingByAI = action.payload.processingByAI;
            state.processedByAINumber = action.payload.processedByAINumber;
            state.savedToDatabase = action.payload.savedToDatabase;
            state.cancelled = action.payload.cancelled;
            state.errorMessage = action.payload.errorMessage;
        },
        setIsUpdatingDataWindowVisible: (state, action: PayloadAction<boolean>) => {
            state.updatingDataWindowVisible = action.payload;
        },
        setIsDownloadingData: (state, action: PayloadAction<boolean>) => {
            state.downloadingData = action.payload;
        },
        setDownloadedOffersNumber: (state, action: PayloadAction<number>) => {
            state.downloadedOffersNumber = action.payload;
        },
        setRemotiveOffersNumber: (state, action: PayloadAction<number>) => {
            state.remotiveCount = action.payload;
        },
        setRemoteOkOffersNumber: (state, action: PayloadAction<number>) => {
            state.remoteOkCount = action.payload;
        },
        setAdzunaOffersNumber: (state, action: PayloadAction<number>) => {
            state.adzunaCount = action.payload;
        },
        setJoinriseOffersNumber: (state, action: PayloadAction<number>) => {
            state.joinriseCount = action.payload;
        },
        setIsProcessingByAI: (state, action: PayloadAction<boolean>) => {
            state.processingByAI = action.payload;
        },
        setProcessedByAINumber: (state, action: PayloadAction<number>) => {
            state.processedByAINumber = action.payload;
        },
        setSavedToDatabase: (state, action: PayloadAction<boolean>) => {
            state.savedToDatabase = action.payload;
        },
        setCancelled: (state, action: PayloadAction<boolean>) => {
            state.cancelled = action.payload;
        },
        setErrorMessage: (state, action: PayloadAction<string>) => {
            state.errorMessage = action.payload;
        },
        resetAllStates: (state) => {
            state.updatingDataWindowVisible = initialState.updatingDataWindowVisible;
            state.downloadingData = initialState.downloadingData;
            state.downloadedOffersNumber = initialState.downloadedOffersNumber;
            state.remotiveCount = initialState.remotiveCount;
            state.remoteOkCount = initialState.remoteOkCount;
            state.adzunaCount = initialState.adzunaCount;
            state.joinriseCount = initialState.joinriseCount;
            state.processingByAI = initialState.processingByAI;
            state.processedByAINumber = initialState.processedByAINumber;
            state.savedToDatabase = initialState.savedToDatabase;
            state.cancelled = initialState.cancelled;
            state.errorMessage = initialState.errorMessage;
        }
    }
});

export const { setSearchState, setIsUpdatingDataWindowVisible, setIsDownloadingData, setDownloadedOffersNumber, setRemotiveOffersNumber, setRemoteOkOffersNumber, setAdzunaOffersNumber, setJoinriseOffersNumber, setIsProcessingByAI, setProcessedByAINumber, setSavedToDatabase, setCancelled, setErrorMessage, resetAllStates } = searchStateSlice.actions;
export default searchStateSlice.reducer;