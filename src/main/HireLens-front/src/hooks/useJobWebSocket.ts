import { useState, useCallback } from "react";
import { connectWebSocket, subscribeToJob, disconnectWebSocket } from "../ws/JobsWebSocket";
import type { DataUpdateStatus } from "../utils/types.ts";
import { useDispatch } from "react-redux";
import { setChartsData } from "../state/chartsState";

export const useJobWebSocket = () => {
  const [status, setStatus] = useState<DataUpdateStatus | null>(null);
  const dispatch = useDispatch();

  const startWebSocket = useCallback((jobId: string) => {
    setStatus(null);

    connectWebSocket(() => {
      subscribeToJob(jobId, {
        STATUS: (status) => {
          setStatus(status);
          if(status.cancelled)
            stopWebSocketAncCleanStatus();

            let errorsArray = status.errorMessage.split(" ").filter(e => e.trim() !== "");
            const exclude = ["fetchRemotiveError", "fetchRemoteOkError", "fetchAdzunaError", "fetchJoinriseError"];
            let filteredLen = errorsArray?.filter(item => item && !exclude.includes(item)).length ?? 0;
            if (Boolean(filteredLen > 0))
                stopWebSocket();
        },
        CHART_MAP: (data) => {
             console.log(data);
              dispatch(setChartsData(data));
        },
        TYPE_CHART1: () => { }

    });
  });
}, []);

const stopWebSocket = useCallback(() => {
  disconnectWebSocket();
}, []);

const stopWebSocketAncCleanStatus = useCallback(() => {
  disconnectWebSocket();
  setStatus(null);
}, []);

return { status, startWebSocket, stopWebSocketAncCleanStatus, stopWebSocket };
};
