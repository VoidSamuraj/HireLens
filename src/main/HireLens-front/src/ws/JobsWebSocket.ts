import SockJS from "sockjs-client";
import { Stomp } from "@stomp/stompjs";
import type { DataUpdateStatus, ChartsDto, WsPayload, WsCallbackMap } from "../utils/types.ts";

const API_URL =  import.meta.env.VITE_API_URL || "http://localhost:8080";
let stompClient: any = null;

export const connectWebSocket = (onConnected: () => void) => {
  const socket = new SockJS(API_URL+"/ws");
  stompClient = Stomp.over(socket);
  stompClient.connect({}, () => {
    console.log("Połączono z WebSocket");
    onConnected();
  });
};

export const subscribeToJob = (jobId: string, callbacks: WsCallbackMap) => {
  if (stompClient && stompClient.connected) {
    stompClient.subscribe(`/dataUpdate/${jobId}`, (message: any) => {
      const wsData: WsPayload<any> = JSON.parse(message.body);
      const cb = callbacks[wsData.type as keyof WsCallbackMap];
        console.log(wsData);
      if (wsData.type === "STATUS") {
          const data = wsData.payload;
          const status: DataUpdateStatus = {
            updatingDataWindowVisible: Boolean(data.updatingDataWindowVisible),
            downloadingData: Boolean(data.downloadingData),
            downloadedOffersNumber: Number(data.downloadedOffersNumber),
            remotiveCount: Number(data.remotiveCount),
            remoteOkCount: Number(data.remoteOkCount),
            adzunaCount: Number(data.adzunaCount),
            joinriseCount: Number(data.joinriseCount),
            processingByAI: Boolean(data.processingByAI),
            processedByAINumber: Number(data.processedByAINumber),
            savedToDatabase: Boolean(data.savedToDatabase),
            cancelled: Boolean(data.cancelled),
            errorMessage: data.errorMessage
          };
          if (cb) {
            cb(status);
          }
      }else if(wsData.type === "CHART_MAP"){
          const data = wsData.payload;
            const dto: ChartsDto = {
                    locations: {},
                    skills: {}
                };

                if (data.locations) {
                    Object.keys(data.locations).forEach(key => {
                        dto.locations[key] = Number(data.locations[key]);
                    });
                }

                if (data.skills) {
                  Object.keys(data.skills).forEach(category => {
                    const skillMap = data.skills[category];
                    dto.skills[category] = {};

                    Object.keys(skillMap).forEach(skill => {
                      dto.skills[category][skill] = Number(skillMap[skill]);
                    });
                  });
                }
                console.log(dto);
                if (cb) {
                    cb(dto);
                }
          }
    });
  }
};

export const disconnectWebSocket = () => {
  if (stompClient) {
    stompClient.disconnect(() => {
      console.log("Rozłączono z WebSocket");
    });
  }
};
