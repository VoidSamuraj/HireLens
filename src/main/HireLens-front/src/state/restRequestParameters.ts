import { createSlice } from "@reduxjs/toolkit";
import type { PayloadAction } from '@reduxjs/toolkit';
import type { StartJobPayload, JobLevel } from '../utils/types.ts'

// Initial state for the Redux slice managing the REST request parameters for job search
const initialState: StartJobPayload = {
    query: "",
    level: "ALL",
    includeUnknown: true,
    maxJobOffers: 100
}

/**
 * Redux slice created with Redux Toolkit's createSlice utility.
 * Manages the state of REST request parameters related to job search queries.
 *
 * Contains reducer methods for updating individual parameters within the state:
 * - setQuery: updates the job search query string
 * - setLevel: sets the job experience level filter using the JobLevel enum
 * - setIncludeUnknown: toggles whether to include jobs with unknown experience levels
 * - setMaxJobOffers: sets the maximum number of job offers to request
 *
 * Each reducer receives a payload of the corresponding type and updates the state immutably.
 * The slice exports action creators corresponding to these reducers for dispatching updates.
 * The reducer is the default export to be integrated into the Redux store.
 */
const restRequestParametersSlice = createSlice({
    name: "restRequestParametersState",
    initialState,
    reducers: {
        setQuery: (state, action: PayloadAction<string>) => {
            state.query = action.payload;
        },
        setLevel: (state, action: PayloadAction<JobLevel>) => {
            state.level = action.payload;
        },
        setIncludeUnknown: (state, action: PayloadAction<boolean>) => {
            state.includeUnknown = action.payload;
        },
        setMaxJobOffers: (state, action: PayloadAction<number>) => {
            state.maxJobOffers = action.payload;
        },
    }
});

export const { setQuery, setLevel, setIncludeUnknown, setMaxJobOffers } = restRequestParametersSlice.actions;
export default restRequestParametersSlice.reducer;