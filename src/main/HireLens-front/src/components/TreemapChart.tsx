import { ResponsiveTreeMap } from "@nivo/treemap";
import { linearGradientDef } from '@nivo/core'
import { useTranslation } from "react-i18next";
import '../styles/treeMap.css'

type TreemapChartProps = {
  jobCategories: { name: string; loc: number }[];
  onTextSend: (text: string) => void;
};

/**
 * TreemapChart component renders a responsive treemap visualization of job categories.
 *
 * - Accepts an array of job categories with name and numeric loc values.
 * - Sorts categories descending by loc count, maps to treemap data structure.
 * - Configures labels dynamically to avoid overflow based on node size.
 * - Customizes colors, borders, animations, tooltips, and theme styles.
 * - Localizes tooltip texts using translation hook.
 */
function TreemapChart({ jobCategories, onTextSend }: TreemapChartProps) {
  const { t } = useTranslation();

    if (!jobCategories || jobCategories.length === 0) {
      return (
        <div style={{ width: '100%', height: '100%', display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
          <div className="spinner"></div>
        </div>
      )
    }


  const treemapData = {
    name: "root",
    color: "transparent",
    children: jobCategories.sort((a, b) => b.loc - a.loc).map(cat => ({
      name: cat.name,
      loc: cat.loc
    }))
  };

  return (
    <div id="tree-map" style={{ width: '100%', height: '100%' }}>
      <ResponsiveTreeMap
        data={treemapData}
        identity="name"
        value="loc"
        innerPadding={6}
        outerPadding={6}
        margin={{ top: 10, right: 10, bottom: 10, left: 10 }}
        labelSkipSize={0}
        label={node => {
          const width = node.width;
          const height = node.height;
          const text = node.data.name;

          const approxCharWidth = 7;
          var maxChars;
          if (width >= height)
            maxChars = Math.floor(width / approxCharWidth);
          else
            maxChars = Math.floor(height / approxCharWidth);
          if (text.length > maxChars) return "";

          return text;
        }}

        labelTextColor="#FFFFFF"
        colors={{ scheme: 'tableau10' }}
        enableParentLabel={false}
        borderColor={node => node.data.color || "white"}
        borderWidth={1}
        animate={true}
        motionConfig="gentle"
        nodeOpacity={0.8}
        defs={[
          linearGradientDef('gradientA', [
            { offset: 0, color: 'inherit', opacity: 0 },
            { offset: 100, color: 'inherit', opacity: 0 },
          ])
        ]}
        onClick={(node) => {
                  onTextSend(node.data.name)
        }}
        tooltip={({ node }) => {
          if (node.data.name === "root") {
            // root – np. wcale nie pokazujemy
            return null
          }
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
            <strong>{node.data.name}</strong>
            <br />
            {t("offerCount")}: {node.value}
          </div>);
        }}
        theme={{
          labels: {
            text: {
              fontSize: 15,
              fontWeight: 500
            }
          },
        }}
        fill={[
          { match: { id: 'root' }, id: 'gradientA' }
        ]}
      />
    </div>
  );
};

export default TreemapChart;