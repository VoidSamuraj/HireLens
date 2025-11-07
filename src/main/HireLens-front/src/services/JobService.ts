import type { Job } from '../utils/types.ts';

export class JobService {
  private jobs: Job[];

  constructor(jobs: Job[]) {
    this.jobs = jobs;
  }

  getJobs(query: string = "", pageSize: number = 5, pageNumber: number = 0): { total: number; data: Job[] } {
    const filtered = this.jobs.filter(
      job => job.title.toLowerCase().includes(query.toLowerCase()) ||
        job.companyName.toLowerCase().includes(query.toLowerCase())
    );

    const total = filtered.length;
    const start = pageNumber * pageSize;
    const end = start + pageSize;
    const data = filtered.slice(start, end);

    return { total, data };
  }

  getAllJobs(): Job[] {
    return this.jobs;
  }
}
