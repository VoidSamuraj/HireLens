import '../styles/infoOverlay.css'
import React , { useEffect, useState } from 'react'
import closeIcon from '../assets/icons/close.svg';
import { useTranslation } from "react-i18next";
import { useSelector, useDispatch } from "react-redux";
import { setIsOpen } from "../state/overlayLocalState";
import type { RootState } from "../state/store.ts"

interface LoadingBarProps {
  progress: number;
  maxProgress: number;
  t: (key: string) => string;
}

/**
 * InfoOverlay component displays a terminal-style overlay with job data update progress.
 *
 * It renders conditional info about downloading status, download count,
 * AI processing with a progress bar, saving status, and dashboard loading indicators.
 * Visibility of the overlay toggles based on global Redux state and local component state.
 *
 * Uses translations for labels and status messages.
 */
function InfoOverlay() {
  const { t } = useTranslation();
  const dispatch = useDispatch();
  const isOpenStateGlobal = useSelector((state: RootState) => state.searchState.updatingDataWindowVisible);
  const isOverlayOpen = useSelector((state: RootState) => state.overlayLocalState.isOpen);
  const [isOpenState, setIsOpenState] = useState(false);
  const isDownloadingData = useSelector((state: RootState) => state.searchState.downloadingData);
  const remotiveCount = useSelector((state: RootState) => state.searchState.remotiveCount);
  const remoteOkCount = useSelector((state: RootState) => state.searchState.remoteOkCount);
  const adzunaCount = useSelector((state: RootState) => state.searchState.adzunaCount);
  const joinriseCount = useSelector((state: RootState) => state.searchState.joinriseCount);
  const [isError, setIsError] = useState(false);
  const errorMessage = useSelector((state: RootState) => state.searchState.errorMessage);
  const [errorsArray, setErrorsArray] = useState<string[]>([]);
  const downloadedOffersNumber = useSelector((state: RootState) => state.searchState.downloadedOffersNumber);
  const isProcessingByAI = useSelector((state: RootState) => state.searchState.processingByAI);
  const processedByAINumber = useSelector((state: RootState) => state.searchState.processedByAINumber);
  const savedToDatabase = useSelector((state: RootState) => state.searchState.savedToDatabase);

  useEffect(() => {
    if (isOpenStateGlobal || isOverlayOpen)
      setIsOpenState(true);
  }, [isOpenStateGlobal, isOverlayOpen]);

  useEffect(() => {
        setErrorsArray(errorMessage.split(" ").filter(e => e.trim() !== ""));
        const exclude = ["fetchRemotiveError", "fetchRemoteOkError", "fetchAdzunaError", "fetchJoinriseError"];
        const filteredLen = errorsArray?.filter(item => item && !exclude.includes(item)).length ?? 0;
        setIsError(Boolean(filteredLen > 0));
  }, [errorMessage]);

  if (isOpenState)
    return (
      <div id="info-overlay">
        <div className={isError? "cancelled" : ""}>
          <button className={`close-overlay-button`} onClick={() => {setIsOpenState(false); dispatch(setIsOpen(false))}}>
            <img src={closeIcon} alt={t("back")} />
          </button>
          {isDownloadingData === true ? (<>
            <div className="line">
              {t("downloadingJobOffers")}
            </div>
            <div className="line">&nbsp;</div>
            {renderSourceLine("Remotive", remotiveCount, Boolean(errorMessage.includes("fetchRemotiveError")))}
            {renderSourceLine("RemoteOK", remoteOkCount, Boolean(errorMessage.includes("fetchRemoteOkError")))}
            {renderSourceLine("Adzuna", adzunaCount, Boolean(errorMessage.includes("fetchAdzunaError")))}
            {renderSourceLine("Joinrise", joinriseCount, Boolean(errorMessage.includes("fetchJoinriseError")))}

            <div className="line">&nbsp;</div>
            <div className="line">
              {t("downloadedTotal") + ": " + downloadedOffersNumber}
              {(isProcessingByAI !== true && !isError) && (
                <div className="terminal-loader-ascii" role="status"></div>
              )}
            </div>
            {isProcessingByAI && (
              <>
               <div className="line">&nbsp;</div>
                <div className="line">{t("aiAnalysis")}<LoadingBar progress={processedByAINumber} maxProgress={downloadedOffersNumber} t={t} /></div>
                {(downloadedOffersNumber == processedByAINumber || savedToDatabase) && (
                    <>
                    <div className="line">&nbsp;</div>
                  <div className="line">{t("savingData")}
                    {(!isError) && (
                        <div className="terminal-loader-ascii" role="status"></div>
                    )}
                  </div>
                  </>
                )}
                {
                    /*savedToDatabase && (
                  <div className="line">{t("loadingDashboard")}
                    {(!isError) && (
                        <div className="terminal-loader-ascii" role="status"></div>
                    )}
                  </div>
                )*/
            }
              </>
            )}
          </>
          ) : (
                <div className="line">{t("enterPromptToStart")}</div>
              )
          }
        </div>
      </div>
    );
  return ("");
}

/**
 * Renders a single terminal-style line for a job source.
 *
 * @param serviceName - The name of the job source (e.g., "Remotive").
 * @param count - Number of job offers retrieved from the source.
 * @param showUnavailable - If true, shows a warning line.
 *                          If false returns value.
 * @returns A JSX element representing the source status line.
 */
const renderSourceLine = (
  serviceName: string,
  count: number,
  showUnavailable: boolean = false
):  React.ReactElement | null => {

  const paddedName = serviceName.padEnd(10, " "); // for nice terminal-style alignment
  const message =
    showUnavailable
      ? `[⚠] ${paddedName} - źródło niedostępne`
      : `[✔] ${paddedName} - ${count} ${count === 1 ? "oferta" : "ofert"}`;

  return <div className="line">{message}</div>;
};
/**
 * LoadingBar component renders a simple textual progress bar in a Linux terminal style.
 *
 * Displays progress as a percentage and a bar composed of hash (#) and dot (.) characters,
 * visually representing progress within total fixed blocks (10 blocks).
 *
 * The 't' translation function is used to localize the "progress" label.
 */
function LoadingBar({ progress, maxProgress, t }: LoadingBarProps) {
  const totalBlocks = 20;

  const safeProgress = Math.min(
    Number(maxProgress) || 0,
    Math.max(0, Number(progress) || 0)
  );

  const hashesCount = Number(maxProgress) > 0
    ? Math.round((safeProgress / Number(maxProgress)) * totalBlocks)
    : 0;

  const dotsCount = totalBlocks - hashesCount;

  const progressValue = Number(maxProgress) > 0
    ? Math.round((safeProgress / Number(maxProgress)) * 100)
    : 0;

  const hashes = "#".repeat(hashesCount);
  const dots = ".".repeat(dotsCount);

  return (
    <div className="linux-progress" role="status">
      <span className="progress-text" role="status">{t("progress")}: [ {progressValue}% ]</span>
      <span className="progress-bar" role="status">[{hashes}{dots}]</span>
    </div>
  );
}

export default InfoOverlay