import { useState, useEffect } from "react";
import Card from '../layouts/Card.tsx'
import JobList from '../pages/JobList.tsx'
import TreemapChart from '../components/TreemapChart.tsx'
import JobsPie from '../components/JobsPie.tsx'
import JobMap from '../components/JobMap.tsx'
import '../styles/home.css'
import { useTranslation } from "react-i18next";
import { useSelector } from "react-redux";
import type { RootState } from "../state/store.ts"
import { testLocations } from '../data/testJobs.ts';
import {ChartDataAdapter, chartAdapter} from '../data/ChartDataAdapter'
import type { ChartsDto , JobCategory, LocationCount} from "../utils/types.ts";
//TODO dodać filtrowanie do mapy jak w filterRemoteAndUndefined i ujednolicenie danych

/**
 * Home component composes the main dashboard layout using a CSS grid.
 *
 * It arranges multiple Card components into a grid with different sizes and content,
 * including charts, maps, filtered job lists, and placeholders.
 *
 * Each Card receives a localized header and custom styles or child components.
 */
function Home() {
  const { t } = useTranslation();

    const chartData: ChartDataAdapter = chartAdapter;

    const skillsData = useSelector((state: RootState) => state.chartsState.skills);
    const locationsData = useSelector((state: RootState) => state.chartsState.locations);

    const [category, setCategory] = useState<string>("undefined");
    const [jobsPieChartData1, setJobsPieChartData1] = useState<LocationCount[]>([]);
    const [jobsPieChartData2, setJobsPieChartData2] = useState<LocationCount[]>([]);
    const [jobCategories, setJobCategories] = useState<JobCategory[]>([]);
   // const [currentSkills, setCurrentSkills] = useState<Skills>({});

    const changeChartScope = (text: string) => {
        setCategory(text);
    };

useEffect(() => {
  if (!skillsData || !locationsData) return;

  const dto: ChartsDto = { locations: locationsData, skills: skillsData };
  chartData.setData(dto);

  const activeCategory = category || Object.keys(skillsData)[0] || "";
  setCategory(activeCategory);
          if(activeCategory == "undefined")
              chartData.set1LevelOfSkills();
          else
              chartData.setLevelOfSkills(category);
  setJobCategories(chartData.getFormattedSkills());
  setJobsPieChartData1(chartData.getGroupedLocationsThreeCategories());
  setJobsPieChartData2(chartData.getFilteredRemoteAndUndefined());
}, [skillsData, locationsData, category]);

  return (
    <>
      <div className="grid-layout">
        <Card className="item item1" header={t("frameworksAndSkillsMap")} headerStyle={{ margin: "0px" }}>
          <TreemapChart jobCategories={jobCategories} onTextSend={changeChartScope}/>
        </Card>
        <Card className="item item2" header={t("jobsByLocation")}>
          <JobsPie data={jobsPieChartData1} style={{ height: "49%" }} />
          <JobsPie data={jobsPieChartData2} style={{ height: "49%" }} />
        </Card>
        <Card className="item item3" header={t("jobDistribution")} >
          <JobMap data={testLocations} />
        </Card>
        <Card className="item">4</Card>
        <Card className="item">5</Card>
        <Card className="item">6</Card>
        <Card className="item item7" header={t("offerList")}>
          <JobList isSettingsDisplayed={false} style={{ width: "calc(100% - 1rem)" }} />
        </Card>
      </div>
    </>
  )
}

export default Home