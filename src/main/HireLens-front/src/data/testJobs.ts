import type { Job, LocationCount, JobCategory } from '../utils/types.ts';


export function groupLocationsThreeCategories(data: LocationCount[]): LocationCount[] {
  const grouped: Record<string, number> = { Remote: 0, Onsite: 0, Undefined: 0 };

  data.forEach(item => {
    if (item.location != undefined) {
      const loc = item.location.toLowerCase();
      if (loc === "remote") grouped.Remote += item.count;
      else if (loc === "undefined") grouped.Undefined += item.count;
      else grouped.Onsite += item.count;
    } else {
      grouped.Undefined += item.count;
    }
  });

  return Object.entries(grouped).map(([location, count]) => ({ location, count }));
}

export function filterRemoteAndUndefined(data: LocationCount[]): LocationCount[] {
  return data.filter(item => {
    if (item.location == undefined)
      return false;
    const loc = item.location.toLowerCase();
    return loc !== "remote" && loc !== "undefined";
  });
}



export const testJobs: Job[] = [
  { id: 1, title: "Senior Backend Developer", companyName: "Acme Corp", companyLogo: "", url: "https://acme.com/jobs/1", category: "Engineering", jobType: "Full-time", publicationDate: "2025-09-01", candidateRequiredLocation: "Remote", salary: "$80k-$100k", description: "Work on backend systems and APIs..." },
  { id: 2, title: "Frontend Engineer", companyName: "Tech Solutions", companyLogo: "", url: "https://techsolutions.com/jobs/2", category: "Engineering", jobType: "Contract", publicationDate: "2025-09-02", candidateRequiredLocation: "Remote", salary: "$60k-$80k", description: "Build responsive frontend apps..." },
  { id: 3, title: "Product Manager", companyName: "Startup Hub", companyLogo: "", url: "https://startuphub.com/jobs/3", category: "Product", jobType: "Full-time", publicationDate: "2025-09-03", candidateRequiredLocation: "UK", salary: "$70k-$90k", description: "Lead product development..." },
  { id: 4, title: "Data Scientist", companyName: "DataCorp", companyLogo: "", url: "https://datacorp.com/jobs/4", category: "Data", jobType: "Full-time", publicationDate: "2025-09-04", candidateRequiredLocation: "Remote", salary: "$90k-$120k", description: "Analyze data and build ML models..." },
  { id: 5, title: "DevOps Engineer", companyName: "CloudWorks", companyLogo: "", url: "https://cloudworks.com/jobs/5", category: "Engineering", jobType: "Full-time", publicationDate: "2025-09-05", candidateRequiredLocation: "Remote", salary: "$85k-$105k", description: "Maintain CI/CD pipelines..." },
  { id: 6, title: "UX Designer", companyName: "DesignStudio", companyLogo: "", url: "https://designstudio.com/jobs/6", category: "Design", jobType: "Contract", publicationDate: "2025-09-06", candidateRequiredLocation: "Remote", salary: "$50k-$70k", description: "Create user-centered designs..." },
  { id: 7, title: "Mobile Developer", companyName: "AppWorks", companyLogo: "", url: "https://appworks.com/jobs/7", category: "Engineering", jobType: "Full-time", publicationDate: "2025-09-07", candidateRequiredLocation: "Remote", salary: "$75k-$95k", description: "Develop mobile applications..." },
  { id: 8, title: "QA Engineer", companyName: "QualitySoft", companyLogo: "", url: "https://qualitysoft.com/jobs/8", category: "Engineering", jobType: "Full-time", publicationDate: "2025-09-08", candidateRequiredLocation: "US", salary: "$65k-$85k", description: "Test software products..." },
  { id: 9, title: "Marketing Specialist", companyName: "MarketPro", companyLogo: "", url: "https://marketpro.com/jobs/9", category: "Marketing", jobType: "Full-time", publicationDate: "2025-09-09", candidateRequiredLocation: "Remote", salary: "$55k-$75k", description: "Plan and execute marketing campaigns..." },
  { id: 10, title: "Customer Support", companyName: "HelpDesk Co", companyLogo: "", url: "https://helpdesk.com/jobs/10", category: "Support", jobType: "Part-time", publicationDate: "2025-09-10", candidateRequiredLocation: "Remote", salary: "$30k-$40k", description: "Assist customers with inquiries..." },
  { id: 11, title: "Technical Writer", companyName: "DocuTech", companyLogo: "", url: "https://docutech.com/jobs/11", category: "Documentation", jobType: "Full-time", publicationDate: "2025-09-11", candidateRequiredLocation: "Remote", salary: "$50k-$70k", description: "Create technical documentation..." },
  { id: 12, title: "Project Coordinator", companyName: "BuildIt", companyLogo: "", url: "https://buildit.com/jobs/12", category: "Management", jobType: "Full-time", publicationDate: "2025-09-12", candidateRequiredLocation: "Remote", salary: "$60k-$80k", description: "Coordinate project tasks..." },
  { id: 13, title: "Security Analyst", companyName: "SecureTech", companyLogo: "", url: "https://securetech.com/jobs/13", category: "Security", jobType: "Full-time", publicationDate: "2025-09-13", candidateRequiredLocation: "US", salary: "$90k-$110k", description: "Monitor and secure systems..." },
  { id: 14, title: "Business Analyst", companyName: "BizInsights", companyLogo: "", url: "https://bizinsights.com/jobs/14", category: "Business", jobType: "Full-time", publicationDate: "2025-09-14", candidateRequiredLocation: "UK", salary: "$70k-$90k", description: "Analyze business processes..." },
  { id: 15, title: "AI Engineer", companyName: "FutureAI", companyLogo: "", url: "https://futureai.com/jobs/15", category: "Data", jobType: "Full-time", publicationDate: "2025-09-15", candidateRequiredLocation: "Remote", salary: "$95k-$130k", description: "Develop AI models..." },
  { id: 16, title: "HR Manager", companyName: "PeopleCorp", companyLogo: "", url: "https://peoplecorp.com/jobs/16", category: "HR", jobType: "Full-time", publicationDate: "2025-09-16", candidateRequiredLocation: "Remote", salary: "$60k-$80k", description: "Manage HR processes..." },
  { id: 17, title: "Cloud Architect", companyName: "Cloudify", companyLogo: "", url: "https://cloudify.com/jobs/17", category: "Engineering", jobType: "Full-time", publicationDate: "2025-09-17", candidateRequiredLocation: "Remote", salary: "$100k-$150k", description: "Design cloud infrastructure..." },
  { id: 18, title: "SEO Specialist", companyName: "SearchMax", companyLogo: "", url: "https://searchmax.com/jobs/18", category: "Marketing", jobType: "Contract", publicationDate: "2025-09-18", candidateRequiredLocation: "Remote", salary: "$45k-$65k", description: "Optimize websites for search engines..." },
  { id: 19, title: "Fullstack Developer", companyName: "DevFactory", companyLogo: "", url: "https://devfactory.com/jobs/19", category: "Engineering", jobType: "Full-time", publicationDate: "2025-09-19", candidateRequiredLocation: "Remote", salary: "$80k-$110k", description: "Develop fullstack applications..." },
  { id: 20, title: "Content Writer", companyName: "WordSmiths", companyLogo: "", url: "https://wordsmiths.com/jobs/20", category: "Content", jobType: "Part-time", publicationDate: "2025-09-20", candidateRequiredLocation: "Remote", salary: "$35k-$50k", description: "Write articles and blog posts..." }
];
export const locationCounts = [
  { location: "Remote", count: 7 },
  { location: "US", count: 3 },
  { location: "UK", count: 2 },
  { location: "Germany", count: 1 },
  { location: "France", count: 1 },
  { location: "Canada", count: 1 },
  { location: "Australia", count: 1 },
  { location: "Spain", count: 1 },
  { location: "Italy", count: 1 },
  { location: "Undefined", count: 2 },
];

export const jobCategories: JobCategory[] = [
  { name: "Frontend", loc: 15 },
  { name: "Backend", loc: 18 },
  { name: "Fullstack", loc: 12 },
  { name: "DevOps", loc: 8 },
  { name: "Data Science", loc: 10 },
  { name: "Machine Learning", loc: 6 },
  { name: "Mobile (iOS/Android)", loc: 9 },
  { name: "QA / Testing", loc: 7 },
  { name: "Product Management", loc: 5 },
  { name: "UX/UI Design", loc: 11 },
  { name: "Cybersecurity", loc: 4 },
  { name: "Cloud Architecture", loc: 6 },
  { name: "Database Administration", loc: 5 },
  { name: "IT Support", loc: 7 },
  { name: "Blockchain", loc: 3 },
  { name: "Embedded Systems", loc: 2 },
  { name: "Game Development", loc: 4 },
  { name: "AR/VR", loc: 2 },
  { name: "AI Research", loc: 3 },
  { name: "Technical Writing", loc: 5 },
  { name: "Networking", loc: 4 },
  { name: "Systems Engineering", loc: 3 },
  { name: "IT Project Management", loc: 6 },
  { name: "Robotics", loc: 2 },
  { name: "Big Data", loc: 7 }
];

export const testLocations: LocationCount[] = [
  { location: "AFG", count: 800936 },
  { location: "AGO", count: 122626 },
  { location: "ALB", count: 230000 },
  { location: "DZA", count: 410000 },
  { location: "ARG", count: 520000 },
  { location: "ARM", count: 90000 },
  { location: "AUS", count: 750000 },
  { location: "AUT", count: 350000 },
  { location: "AZE", count: 120000 },
  { location: "BHS", count: 50000 },
  { location: "BHR", count: 60000 },
  { location: "BGD", count: 950000 },
  { location: "BRB", count: 30000 },
  { location: "BEL", count: 400000 },
  { location: "BLZ", count: 25000 },
  { location: "BEN", count: 130000 },
  { location: "BTN", count: 10000 },
  { location: "BOL", count: 70000 },
  { location: "BIH", count: 80000 },
  { location: "BWA", count: 50000 },
  { location: "BRA", count: 1000000 },
  { location: "BRN", count: 20000 },
  { location: "BGR", count: 180000 },
  { location: "BFA", count: 90000 },
  { location: "BDI", count: 30000 }
];

