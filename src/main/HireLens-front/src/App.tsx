import { useEffect, useState } from 'react'
import Navbar from './layouts/Navbar.tsx'
import Footer from './layouts/Footer.tsx'
import InfoOverlay from './layouts/InfoOverlay.tsx'
import Home from './pages/Home.tsx'
import JobList from './pages/JobList.tsx'
import ChartsPage from './pages/ChartsPage.tsx'
import { Routes, Route } from 'react-router-dom'
import { useDispatch } from "react-redux";
import { setChartsData } from "./state/chartsState";
import { fetchInitData } from './api/JobsApi'

function App() {
  const [marginTop, setMarginTop] = useState(100);
  const dispatch = useDispatch();

  useEffect(() => {

    const navbar = document.getElementById("navbar");
    let observer: MutationObserver | null = null;
    if (navbar){
        if (navbar.classList.contains("open")) {
          setMarginTop(165);
        } else {
          setMarginTop(100);
        }

        observer = new MutationObserver(() => {
          if (navbar.classList.contains("open")) {
            setMarginTop(165);
          } else {
            setMarginTop(100);
          }
        });

        observer.observe(navbar, { attributes: true, attributeFilter: ["class"] });
    };

    const storedJobId = localStorage.getItem("jobId");
    if (storedJobId) {

      fetchInitData(storedJobId,(data) =>{
          dispatch(setChartsData(data))
          } );
    }

    return () => observer?.disconnect();
  }, []);


  return (
    <>
      <Navbar />
      <div style={{
        marginTop: `${marginTop}px`, height: "100%",
        transition: 'margin-top 0.3s ease'
      }}>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/list" element={<JobList />} />
          <Route path="/charts" element={<ChartsPage />} />
        </Routes>
      </div>
      <InfoOverlay/>
      <Footer />
    </>
  )
}

export default App
