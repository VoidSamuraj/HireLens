import { useSelector, useDispatch } from "react-redux";
import type { RootState } from "../state/store";
import { setLevel, setMaxJobOffers, setIncludeUnknown } from "../state/restRequestParameters";
import "../styles/jobFilter.css";
import { useTranslation } from "react-i18next";
import type { JobLevel } from '../utils/types.ts';

/**
 * JobFilter component allows the user to filter job listings by seniority level, include unknown levels, and max job offers.
 *
 * It interacts with Redux state using selectors and dispatchers.
 * The component supports radio buttons for job levels, a checkbox to include jobs with unknown level,
 * and a numeric input to control max number of job offers to fetch.
 */
const JobFilter = () => {
  const { t } = useTranslation();
  const dispatch = useDispatch();
  const selectedLevel = useSelector((state: RootState) => state.restRequestParametersState.level);
  const maxJobOffers = useSelector((state: RootState) => state.restRequestParametersState.maxJobOffers);
  const includeUnknown = useSelector((state: RootState) => state.restRequestParametersState.includeUnknown);

  const handleLevelChange = (level: JobLevel) => {
    dispatch(setLevel(level));
  };

  const handleUndefinedChange = (checked: boolean) => {
    dispatch(setIncludeUnknown(checked));
  };

  return (
    <div className="job-filter">
      <div className="job-filter-levels">
        <span className="filter-label">{t("seniorityLevel")}:</span>
        <label>
          <input
            type="radio"
            name="level"
            value="INTERN"
            checked={selectedLevel === "INTERN"}
            onChange={() => handleLevelChange("INTERN")}
          />
          Intern
        </label>
        <label>
          <input
            type="radio"
            name="level"
            value="JUNIOR"
            checked={selectedLevel === "JUNIOR"}
            onChange={() => handleLevelChange("JUNIOR")}
          />
          Junior
        </label>
        <label>
          <input
            type="radio"
            name="level"
            value="MID"
            checked={selectedLevel === "MID"}
            onChange={() => handleLevelChange("MID")}
          />
          Mid
        </label>
        <label>
          <input
            type="radio"
            name="level"
            value="SENIOR"
            checked={selectedLevel === "SENIOR"}
            onChange={() => handleLevelChange("SENIOR")}
          />
          Senior
        </label>
        <label>
          <input
            type="radio"
            name="level"
            value="ALL"
            checked={selectedLevel === "ALL"}
            onChange={() => handleLevelChange("ALL")}
          />
          All
        </label>
      </div>

      <div className="job-filter-undefined">
        <label>
          <input
            type="checkbox"
            checked={includeUnknown}
            onChange={(e) => handleUndefinedChange(e.target.checked)}
          />
          {t("includeUndefined")}
        </label>
        <label id="jobs-per-api-label" title={t("jobsPerApiLabel")}>
          {t("jobsPerApi")}
          <input
            type="number"
            min={20}
            step={10}
            value={maxJobOffers}
            onChange={(e) =>
              dispatch(setMaxJobOffers(e.target.value === "" ? 100 : Number(e.target.value)))
            }
          />
        </label>
      </div>
    </div>
  );
};

export default JobFilter;