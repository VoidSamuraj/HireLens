import { useState, useEffect } from "react";
import { useSelector, useDispatch } from "react-redux";
import type { RootState } from "../state/store";
import { setQuery } from "../state/restRequestParameters";
import { setSearchState, resetAllStates } from "../state/searchState";
import { setIsOpen } from "../state/overlayLocalState";
import { Link } from "react-router-dom";
import '../styles/navbar.css'
import { useTranslation } from "react-i18next";
import '../i18n.ts';
import searchIcon from '../assets/icons/search.svg';
import arrowUpIcon from '../assets/icons/arrow-up.svg';
import sunIcon from '../assets/icons/sun.svg';
import moonIcon from '../assets/icons/moon.svg';
import serverIcon from '../assets/icons/server.svg';
import JobFilter from '../components/JobFilter.tsx'
import type { StartJobPayload } from '../utils/types.ts';
import { useJobWebSocket } from '../hooks/useJobWebSocket'
import { startJob } from "../api/JobsApi";

/**
 * Navbar component provides navigation links, search input, language and theme toggles,
 * and integrates WebSocket-driven job search status updates via Redux.
 *
 * - Uses i18next translation hooks for language localization and toggle.
 * - Manages search input visibility and job query state with Redux.
 * - Uses custom useJobWebSocket hook to handle a WebSocket connection for live job status.
 * - Dispatches state updates to Redux store on WebSocket messages.
 * - Executes async job start request passing current search parameters,
 *   then initiates WebSocket connection stream for status updates.
 * - Handles dark/light theme toggling with persistence in localStorage.
 */
function Navbar() {
  const { t, i18n } = useTranslation();
  const { status, startWebSocket } = useJobWebSocket();
  const dispatch = useDispatch();

  const toggleLanguage = () => {
    i18n.changeLanguage(i18n.language === "pl" ? "en" : "pl");
  };
  const [searchOpen, setSearchOpen] = useState(false);
  //TODO add includeUnknown handling in backend
  const query = useSelector((state: RootState) => state.restRequestParametersState.query);
  const selectedLevel = useSelector((state: RootState) => state.restRequestParametersState.level);
  const maxJobOffers = useSelector((state: RootState) => state.restRequestParametersState.maxJobOffers);
  const includeUnknown = useSelector((state: RootState) => state.restRequestParametersState.includeUnknown);

  const [theme, setTheme] = useState(() => {
    return localStorage.getItem("theme") || "dark";
  });

  useEffect(() => {
    if (status !== null)
      dispatch(setSearchState(status));
    else
      dispatch(resetAllStates());
  }, [status]);



// Starts a new job with current search parameters and opens WebSocket connection
  const performSearchRequest = async () => {
    const payload: StartJobPayload = {
      query: query,
      level: selectedLevel,
      includeUnknown: includeUnknown,
      maxJobOffers: maxJobOffers
    };
    try {
      const result = await startJob(payload, (data) => {
        localStorage.setItem("jobId", data.jobId);
        startWebSocket(data.jobId);
      });
      console.log("Job wystartował:", result.jobId);
    } catch (err) {
      console.error("Błąd przy starcie joba:", err);
    }


  };


  // Synchronize theme attribute with localStorage on change
  useEffect(() => {
    document.documentElement.setAttribute("data-theme", theme);
    localStorage.setItem("theme", theme);
  }, [theme]);

  const toggleTheme = () => {
    setTheme(prev => (prev === "dark" ? "light" : "dark"));
  };


  return (
    <>
      <div id="navbar" className={searchOpen ? "open" : ""}>
        <div id="flip-card" className={searchOpen ? "flipped" : ""}>
          <div id="nav-buttons">
            <Link to="/">{t("dashboard")}</Link>
            <Link to="/list">{t("list")}</Link>
            <Link to="/charts">{t("charts")}</Link>
          </div>
          <div id="search">
            <input type="text" placeholder={t("search")} maxLength={80} value={query} onChange={(e) => dispatch(setQuery(e.target.value))} />
            <div id="searchInputBox">
              <button className="input-icon" onClick={performSearchRequest} >
                <img src={searchIcon} alt={t("search")} />
              </button>
            </div>
          </div>
        </div>
        <div id="side-buttons">
          <button className="image-button-toggle" onClick={() => setSearchOpen(prev => !prev)}>
            <img src={searchIcon} alt={t("search")} className={`icon ${searchOpen ? "hidden" : "visible"}`} />
            <img src={arrowUpIcon} alt={t("back")} className={`icon ${searchOpen ? "visible" : "hidden"}`} />
          </button>
          <button id="server-status" onClick={() => dispatch(setIsOpen(true))}>
             <img src={serverIcon} alt={t("server")} className={`icon`} />
          </button>
          <button id="language-button" onClick={toggleLanguage}>{(i18n.language ?? "pl").toUpperCase()}</button>
          <button className="image-button-toggle" onClick={toggleTheme} style={{ marginRight: '20px' }}>
            <img src={sunIcon} alt={t("light")} className={`icon ${theme === "dark" ? "hidden" : "visible"}`} />
            <img src={moonIcon} alt={t("dark")} className={`icon ${theme === "dark" ? "visible" : "hidden"}`} />
          </button>
        </div>
        <JobFilter />
      </div>
    </>
  )
}

export default Navbar