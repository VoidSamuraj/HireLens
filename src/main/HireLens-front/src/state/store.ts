import { configureStore } from "@reduxjs/toolkit";
import searchStateReducer from "./searchState.ts"
import restRequestParametersReducer from "./restRequestParameters.ts"
import overlayLocalStateReducer from "./overlayLocalState.ts"
import chartsStateReducer from "./chartsState.ts";

/**
 * Configures the Redux store for the application using Redux Toolkit's configureStore.
 *
 * Combines multiple slice reducers into one root reducer by passing an object:
 * - `searchState` slice to manage the UI state related to data update progress.
 * - `restRequestParametersState` slice to hold REST API request parameters.
 *
 * This modular approach allows independent state management for different slices of the app.
 *
 * Exported store can be provided to the React application via <Provider> and used with hooks like useSelector/useDispatch.
 */
export const store = configureStore({
    reducer: {
        searchState: searchStateReducer,
        overlayLocalState: overlayLocalStateReducer,
        restRequestParametersState: restRequestParametersReducer,
        chartsState: chartsStateReducer
    }
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;