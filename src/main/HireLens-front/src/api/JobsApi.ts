import axios from "axios";
import type { Job, StartJobPayload, ChartsDto } from "../utils/types.ts";


const API_URL =  import.meta.env.VITE_API_URL || "http://localhost:8080";
/**
 * Starts a new job by posting the start payload to the backend API.
 * Optionally executes a callback with the response data containing jobId.
 *
 * @param payload - The parameters to start the job with.
 * @param onResponse - Optional callback function called with response data.
 * @returns A promise resolving with the jobId returned from backend.
 */
export const startJob = async (
  payload: StartJobPayload,
  onResponse?: (data: { jobId: string}) => void
): Promise<{ jobId: string}> => {
  const res = await axios.post(`${API_URL}/api/startJob`, payload);

  if (onResponse) {
    onResponse(res.data);
  }
  return res.data;
};

/**
 * Sends a request to stop a running job by jobId.
 *
 * @param jobId - The jobId object identifying the job to stop.
 * @returns A promise resolving with the HTTP response status.
 */
export const stopJob = async (jobId: { jobId: string }) => {
  const res = await axios.post(`${API_URL}/api/stopJob`, jobId);
  return res.status;
};

/**
 * TODO
 * Fetches the list of all jobs from the backend API.
 *
 * @returns A promise resolving with an array of Job objects.
 */
export const fetchJobs = async (): Promise<Job[]> => {
  const res = await axios.get<Job[]>(`${API_URL}/api/jobs`);
  return res.data;
};

/**
 *
 * Fetches the aggregated jobs downloaded so far.
 *
 * @returns A promise.
 */
export const fetchInitData = async (jobId: string, onData: (data: ChartsDto) => void) => {
  const res = await axios.get<ChartsDto>(`${API_URL}/init`, {params: { jobId }});
  onData(res.data);
};

