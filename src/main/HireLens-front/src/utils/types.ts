/**
 * Represents a job category with a name and the corresponding count of job listings.
 */
export interface JobCategory {
  name: string;                    // The name of the job category (e.g., "Engineering", "Marketing")
  loc: number;                     // The count of job listings in this category
}

/**
 * Represents a single job listing with detailed information.
 */
export interface Job {
  id: number;
  title: string;
  companyName: string;
  companyLogo: string;
  url: string;
  category: string;
  jobType: string;
  publicationDate: string;
  candidateRequiredLocation: string;
  salary: string;
  description: string;
}

/**
 * Payload sent to initiate a new job data processing job.
 */
export interface StartJobPayload {
  query: string;
  level: JobLevel;                  // Seniority level filter (e.g., "JUNIOR", "ALL")
  includeUnknown: boolean;          // Whether to include jobs with unknown levels
  maxJobOffers: number;             // Max number of job offers to request/process
}

/**
 * Tracks the current status of job data downloading, processing, and saving.
 */
export interface DataUpdateStatus {
  updatingDataWindowVisible: boolean;
  downloadingData: boolean;
  downloadedOffersNumber: number;
  remotiveCount: number;
  remoteOkCount: number;
  adzunaCount: number;
  joinriseCount: number;
  processingByAI: boolean;
  processedByAINumber: number;
  savedToDatabase: boolean;
  cancelled: boolean;
  errorMessage: string;
}
/**
 * Aggregated data to fill charts
 */
export interface ChartsDto{
  locations: Record<string, number>;
  skills: Record<string, Record<string, number>>;
}

/**
 * Properties for the reusable Card UI component.
 */
export interface CardProps {
  children: React.ReactNode
  className?: string
  header?: string
  style?: React.CSSProperties,
  headerStyle?: React.CSSProperties
}

/**
 * Enum-like type representing job seniority levels.
 */
export type JobLevel = "INTERN" | "JUNIOR" | "MID" | "SENIOR" | "ALL";

/**
 * Represents the count of job offers grouped by a location category.
 */
export interface LocationCount {
  location: string;
  count: number;
}

export interface WsPayload<T> {
  type: "STATUS" | "CHART_MAP";
  payload: T;
}

export type WsCallbackMap = {
         STATUS?: (data: DataUpdateStatus) => void;
         CHART_MAP?: (data: ChartsDto) => void;
         TYPE_CHART1?: (data: any) => void;
       };
export interface OverlayLocalState {
  isOpen: boolean;
}
