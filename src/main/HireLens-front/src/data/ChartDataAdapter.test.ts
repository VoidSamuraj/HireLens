import { ChartDataAdapter } from "./ChartDataAdapter";
import type { ChartsDto } from "../utils/types";

describe("ChartDataAdapter", () => {
  const mockCharts: ChartsDto= {
    locations: { Canada: 5, Poland: 10 },
    skills: {
      Frontend: { React: 50, TypeScript: 70, CSS: 30 },
      Backend: { Spring: 80, Ktor: 60 },
    },
  };

  let adapter: ChartDataAdapter;

  beforeEach(() => {
    adapter = new ChartDataAdapter();
    adapter.setData(mockCharts);
  });

  test("should aggregate skills correctly for level 1", () => {
    adapter.set1LevelOfSkills();
    const result = adapter.getCurrentSkills();

    expect(result).toEqual({
      Frontend: 150,
      Backend: 140,
    });
  });

  test("should return nested skills for a specific category", () => {
    adapter.setLevelOfSkills("Frontend");
    const result = adapter.getCurrentSkills();

    expect(result).toEqual({
      React: 50,
      TypeScript: 70,
      CSS: 30,
    });
  });

  test("should return empty object for invalid category", () => {
    adapter.setLevelOfSkills("NonExisting");
    const result = adapter.getCurrentSkills();

    expect(result).toEqual({});
  });

  test("should keep locations intact", () => {
    const locations = adapter.getLocations();
    expect(locations).toEqual({ Canada: 5, Poland: 10 });
  });

  test("should return full ChartsDto structure", () => {
    const full = adapter.getCharts();
    expect(full.skills).toBeDefined();
    expect(full.locations).toBeDefined();
  });
});
