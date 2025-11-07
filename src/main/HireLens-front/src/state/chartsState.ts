import { createSlice } from "@reduxjs/toolkit";
import type { PayloadAction } from "@reduxjs/toolkit";
import type { ChartsDto } from "../utils/types"

/**
 * Initial Redux state for Charts data.
 * Default: empty locations and skills maps.
 */
const initialState: ChartsDto = {
  locations: {},
  skills: {},
};


/**
 * Redux slice for managing chart data received from backend (e.g. WebSocket).
 *
 * State:
 * - `locations`: mapping of location → job count.
 * - `skills`: nested mapping of skillCategory → skill → weight/count.
 *
 * Reducers:
 * - `setChartsData`: replaces the full dataset.
 * - `clearChartsData`: resets to an empty structure.
 */
const chartsStateSlice = createSlice({
  name: "chartsState",
  initialState,
  reducers: {
    setChartsData: (state, action: PayloadAction<ChartsDto>) => {
        state.locations = action.payload.locations;
        state.skills = action.payload.skills;
    },
    clearChartsData: (state) => {
             state.locations = initialState.locations;
             state.skills = initialState.skills;
    },
  },
});

export const { setChartsData,  clearChartsData } = chartsStateSlice.actions;
export default chartsStateSlice.reducer;