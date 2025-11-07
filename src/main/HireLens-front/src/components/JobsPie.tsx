import React from "react"
import { ResponsivePie } from '@nivo/pie'
import { useTranslation } from "react-i18next";
import type { LocationCount } from "../utils/types.ts";

type JobProps = {
  data: LocationCount[];
  style?: React.CSSProperties;
} & React.HTMLAttributes<HTMLDivElement>;

const JobsPie = ({ data, style }: JobProps) => {

    if (!data || data.length === 0) {
      return (
        <div style={{ width: '100%', display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
          <div className="spinner"></div>
        </div>
      )
    }

  const { t } = useTranslation();
  const pieData = data.map(loc => ({
    id: loc.location,
    value: loc.count
  }));


  return (
    <div style={{ width: "100%", height: "100%", ...style }}>
      <ResponsivePie
        data={pieData}
        margin={{ top: 40, right: 100, bottom: 40, left: 100 }}
        innerRadius={0.5}
        padAngle={0.6}
        cornerRadius={2}
        activeOuterRadiusOffset={8}
        borderColor={{ from: 'color', modifiers: [['darker', 0]] }}
        arcLinkLabelsSkipAngle={10}
        arcLinkLabelsThickness={3}
        arcLinkLabelsColor={{ from: 'color' }}
        arcLinkLabelsTextColor="#ffffff"
        arcLabelsTextColor="#ffffff"
        arcLabelsSkipAngle={10}
        tooltip={({ datum }) => {
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
            <strong>{datum.id}</strong>
            <br />
            {t("offerCount")}: {datum.value}
          </div>);
        }}
        theme={{
          labels: {
            text: {
              fontSize: 14
            }
          }
        }}
      />
    </div>
  );
}


export default JobsPie