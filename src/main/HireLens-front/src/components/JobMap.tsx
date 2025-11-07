import { useState, useEffect } from "react";
import { ResponsiveChoropleth } from '@nivo/geo'
import { useTranslation } from "react-i18next";
import type { LocationCount } from "../utils/types.ts";
import worldFeatures from '../data/world_countries.json';


type JobProps = {
  data: LocationCount[];                    // Array of location data objects with location name and counts
  style?: React.CSSProperties;              // Optional inline CSS styles for the container div
} & React.HTMLAttributes<HTMLDivElement>;   // Extend HTML attributes allowed for div elements

/**
 * JobMap component renders a responsive choropleth world map to visualize job counts by location.
 *
 * - Maps LocationCount data to the format needed by ResponsiveChoropleth.
 * - Dynamically adjusts map scale based on window width with event listener on resize.
 * - Applies color scaling and legends for job offer counts.
 * - Displays tooltips with location name and job offer count, localized with translation function.
 *
 * @param data array of location count objects containing location ID and job count value
 * @param style optional styles to apply to the map container div
 */
const JobMap = ({ data, style }: JobProps) => {
  const mapArray = data.map(item => ({
    id: item.location,
    value: item.count,
  }));
  const { t } = useTranslation();

  const [scale, setScale] = useState(150);

  useEffect(() => {
    const handleResize = () => {
      if (window.innerWidth < 480) setScale(80);
      else if (window.innerWidth < 1050) setScale(60);
      else if (window.innerWidth < 1200) setScale(70);
      else if (window.innerWidth < 1350) setScale(80);
      else if (window.innerWidth < 1450) setScale(90);
      else if (window.innerWidth < 1600) setScale(100);
      else if (window.innerWidth < 1700) setScale(110);
      else if (window.innerWidth < 1820) setScale(120);
      else if (window.innerWidth < 2100) setScale(130);
      else setScale(150);
    };

    handleResize();
    window.addEventListener("resize", handleResize);
    return () => window.removeEventListener("resize", handleResize);
  }, []);

  return (
    <div style={{ width: "100%", height: "100%", overflow: "hidden", ...style }}>
      <ResponsiveChoropleth
        data={mapArray}
        features={worldFeatures.features}
        margin={{ top: 0, right: 0, bottom: 0, left: 0 }}
        colors="RdYlGn"
        domain={[0, Math.max(...mapArray.map(d => d.value))]}
        unknownColor="#666666"
        label="properties.name"
        valueFormat=".2s"
        borderWidth={0.5}
        borderColor="#152538"
        projectionScale={scale}
        legends={[
          {
            anchor: 'bottom-left',
            direction: 'column',
            justify: true,
            translateX: 20,
            translateY: -100,
            itemsSpacing: 0,
            itemWidth: 94,
            itemHeight: 18,
            itemDirection: 'left-to-right',
            itemTextColor: '#ffffff',
            itemOpacity: 0.85,
            symbolSize: 18
          }
        ]}
        tooltip={({ feature }) => {
          if (feature.value == null || feature.value == undefined)
            return "";
          return (<div
            style={{
              padding: '8px 12px',
              background: 'rgba(0,0,0,0.8)',
              color: '#fff',
              borderRadius: '6px',
              fontSize: '18px',
              boxShadow: '0 2px 8px rgba(0,0,0,0.3)',
              width: '150px'
            }}
          >
            <strong>{feature.data.id}</strong>
            <br />
            {t("offerCount")}: {feature.data.value}
          </div>);
        }}
        theme={{
          legends: {
            text: {
              fontSize: 14
            }
          }
        }}

      />
    </div>)
}

export default JobMap