import { useEffect, useState } from "react";
import "../styles/jobList.css";
import { JobService } from "../services/JobService.ts";
import { testJobs } from "../data/testJobs.ts";
import type { Job } from '../utils/types.ts';

type Props = {
  query?: string;
  initialPageSize?: number;
  isSettingsDisplayed?: boolean;
  style?: React.CSSProperties;
};

/**
 * JobList component renders a paginated, searchable list of jobs with optional control display.
 *
 * Fetches jobs from JobService based on query, current page, and page size.
 * Shows job details including company logo or placeholder, job title, company info, location,
 * job type, category, salary, publish date, and description. Provides pagination controls
 * and page size selector if enabled.
 *
 * @param query Search keyword to filter jobs (default: "")
 * @param initialPageSize Initial number of job items per page (default: 10)
 * @param isSettingsDisplayed Whether pagination and page size options are displayed (default: true)
 * @param style CSS styles applied to outer container (default: margin "1rem")
 */
function JobList({
  query = "",
  initialPageSize = 10,
  isSettingsDisplayed = true,
  style = { margin: "1rem" },
}: Props) {
  const [jobs, setJobs] = useState<Job[]>([]);
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(initialPageSize);
  const [total, setTotal] = useState(0);


  const jobService = new JobService(testJobs);

  useEffect(() => {
    const result = jobService.getJobs(query, pageSize, page);
    setJobs(result.data);
    setTotal(result.total);
  }, [query, page, pageSize]);

  const totalPages = Math.ceil(total / pageSize);

  return (
    <div className="job-list-container" style={style}>


      <ul className="job-list">
        {jobs.map(job => (
          <li key={job.id} className="job-item">
            <div className="job-left">
              <div className="job-header">
                <div className="job-logo">
                  {job.companyLogo ? (
                    <img src={job.companyLogo} alt={job.companyName} />
                  ) : (
                    <div className="no-logo">{job.companyName[0]}</div>
                  )}
                </div>
                <h3 className="job-title">{job.title}</h3>
              </div>
              <div className="job-meta">
                <div className="meta-left">
                  <p><strong>Company:</strong> {job.companyName}</p>
                  <p><strong>Location:</strong> {job.candidateRequiredLocation}</p>
                </div>
                <div className="meta-center">
                  <p><strong>Type:</strong> {job.jobType}</p>
                  <p><strong>Category:</strong> {job.category}</p>

                </div>
                <div className="meta-right">
                  <p><strong>Salary:</strong> {job.salary}</p>
                  <p><strong>Published:</strong> {job.publicationDate}</p>
                </div>
              </div>
              {job.url && <a href={job.url} target="_blank" rel="noopener noreferrer">View Job</a>}
            </div>
            <div className="job-description">
              <p>{job.description}</p>
            </div>
          </li>
        ))}
      </ul>
      {isSettingsDisplayed && (
        <div className="list-settings">
          <div className="pagination">
            <button onClick={() => setPage(p => Math.max(p - 1, 0))} disabled={page === 0}>
              Prev
            </button>
            {[...Array(totalPages).keys()].map(p => (
              <button key={p} className={p === page ? "active" : ""} onClick={() => setPage(p)}>
                {p + 1}
              </button>
            ))}
            <button onClick={() => setPage(p => Math.min(p + 1, totalPages - 1))} disabled={page + 1 >= totalPages}>
              Next
            </button>
          </div>
          <div className="page-size-selector">
            <label>Items per page: </label>
            <select value={pageSize} onChange={e => setPageSize(Number(e.target.value))}>
              <option value={2}>2</option>
              <option value={3}>3</option>
              <option value={5}>5</option>
              <option value={10}>10</option>
              <option value={20}>20</option>
              <option value={50}>50</option>
              <option value={100}>100</option>
            </select>
          </div>

        </div>
      )}
    </div>
  );
}

export default JobList;
