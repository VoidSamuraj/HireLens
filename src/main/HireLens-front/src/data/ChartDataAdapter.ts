import type { ChartsDto, JobCategory, LocationCount } from "../utils/types.ts";

/**
 * Adapter for ChartsDto data (skills + locations).
 * Handles aggregation of nested skills and provides convenient access methods.
 */
export class ChartDataAdapter {
  private charts: ChartsDto = { locations: {}, skills: {} };
  private currentSkills: Record<string, number> = {};

  /** Load data from Redux ChartsDto */
public setData(dto?: ChartsDto | null) {
  if (!dto || !dto.skills || !dto.locations) {
    console.warn("ChartDataAdapter.setData called with invalid or empty ChartsDto, using empty defaults");
    this.charts = { locations: {}, skills: {} };
    this.currentSkills = {};
    return;
  }
    this.charts = dto;
    this.currentSkills = this.aggregateLevel1(dto.skills);
  }

  /** Aggregates nested skills for level 1 (category → total count) */
  private aggregateLevel1(skills: Record<string, Record<string, number>>): Record<string, number> {
    const aggregated: Record<string, number> = {};
    for (const [category, inner] of Object.entries(skills)) {
      aggregated[category] = Object.values(inner).reduce((a, b) => a + b, 0);
    }
    return aggregated;
  }

  /** Returns nested map for given category */
  private getNested(category: string): Record<string, number> | undefined {
    return this.charts.skills[category];
  }

  /** Set skills to aggregated level 1 view */
  public set1LevelOfSkills(): void {
    this.currentSkills = this.aggregateLevel1(this.charts.skills);
  }

  /** Set skills to nested category view */
  public setLevelOfSkills(category: string): void {
     const level1Categories = Object.keys(this.charts.skills);

        if (!level1Categories.includes(category)) {
            console.warn(`Category "${category}" is not in level 1. No change applied.`);
            return;
        }

    const nested = this.getNested(category);
    this.currentSkills = nested ?? {};
  }

  /** Get currently active skills data (aggregated or nested) */
  public getCurrentSkills(): Record<string, number> {
    return this.currentSkills;
  }

  /** Get full ChartsDto */
  public getCharts(): ChartsDto {
    return this.charts;
  }
  public  getFormattedSkills(): JobCategory[] {
      return Object.entries(this.currentSkills).map(([key, value]) => ({
        name: key,
        loc: value,
      }));
    }


  /** Get raw locations */
  public getLocations(): Record<string, number> {
    return this.charts.locations;
  }

  public getFormattedLocations(): { location: string; count: number }[] {
    return Object.entries(this.charts.locations).map(([location, count]) => ({
      location,
      count,
    }));
  }

 public getGroupedLocationsThreeCategories(): LocationCount[] {
    const grouped: Record<string, number> = { Remote: 0, Onsite: 0, Undefined: 0 };

    Object.entries(this.charts.locations).forEach(([location, count]) => {
      if (location !== undefined && location !== null) {
        const loc = location.toLowerCase();
        if (loc === "remote") grouped.Remote += count;
        else if (loc === "undefined") grouped.Undefined += count;
        else grouped.Onsite += count;
      } else {
        grouped.Undefined += count;
      }
    });

    return Object.entries(grouped).map(([location, count]) => ({ location, count }));
  }

  public getFilteredRemoteAndUndefined(): LocationCount[] {
    return Object.entries(this.charts.locations)
      .map(([location, count]) => ({ location, count }))
      .filter(item => {
        if (item.location === undefined || item.location === null) return false;
        const loc = item.location.toLowerCase();
        return loc !== "remote" && loc !== "undefined";
      });
  }

}

export const chartAdapter = new ChartDataAdapter();


/**
 * React hook that provides a ChartDataAdapter instance
 * populated with current Redux data.
 */
/*
export const useChartDataAdapter = (): ChartDataAdapter => {
  const charts = useSelector((state: RootState) => state.chartsState);
  const adapter = new ChartDataAdapter();
  adapter.setData(charts);
  return adapter;
};
*/

/**
 * Aggregates location data from ChartsDto into a list of { location, count } entries.
 *
 * @param charts - The ChartsDto object containing location mappings.
 * @returns An array of LocationEntry objects, one per unique location.
 */
/*
export const aggregateLocations = (charts: ChartsDto): LocationEntry[] => {
  return Object.entries(charts.locations).map(([location, count]) => ({
    location,
    count,
  }));
};
*/
/**
 * Aggregates skill data from ChartsDto into a list of { name, loc } entries.
 * The `loc` value represents the sum of all skill levels within the category.
 *
 * @param charts - The ChartsDto object containing nested skill mappings.
 * @returns An array of SkillEntry objects, one per skill category.
 */
/*
export const aggregateSkills = (charts: ChartsDto): SkillEntry[] => {
  return Object.entries(charts.skills).map(([category, skillsMap]) => ({
    name: category,
    loc: Object.values(skillsMap).reduce((acc, val) => acc + val, 0),
  }));
};

*/