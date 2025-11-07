import { createSlice } from "@reduxjs/toolkit";
import type { PayloadAction } from '@reduxjs/toolkit';
import type { OverlayLocalState } from '../utils/types.ts'

// Initial state for the Redux slice managing the REST request parameters for overlay state
const initialState: OverlayLocalState= {
    isOpen: false
}

/**
 * Manages local UI state for InfoOverlay visibility.
 *
 * State:
 * - `isOpen`: whether the overlay is currently visible.
 *
 * Reducers:
 * - `setIsOpen`: toggles overlay visibility.
 *
 * Used to manually open/close InfoOverlay independent of global backend state.
 */
const overlayLocalStateSlice = createSlice({
  name: "overlayLocalState",
  initialState,
  reducers: {
    setIsOpen: (state, action: PayloadAction<boolean>) => {
      state.isOpen = action.payload;
    },
  },
});

export const { setIsOpen } = overlayLocalStateSlice.actions;
export default overlayLocalStateSlice.reducer;