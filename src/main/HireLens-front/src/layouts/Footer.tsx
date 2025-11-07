import '../styles/footer.css'
import { useTranslation } from "react-i18next";

/**
 * Footer component displays footer information with external API and icon references.
 * Uses translation hook 't' for localization of section labels.
 * Renders lists with links opening in new tabs, secured with noopener noreferrer.
 */
function Footer() {
  const { t } = useTranslation();

  return (
    <footer className="footer">
      <div>
        <div></div>
        <div>
          <p>Api:</p>
          <ul>
            <li>
              Adzuna – <a href="https://www.adzuna.com" target="_blank" rel="noopener noreferrer">https://www.adzuna.com</a>
            </li>
            <li>
              Remote OK Jobs – <a href="https://remoteok.com" target="_blank" rel="noopener noreferrer">https://remoteok.com</a>
            </li>
            <li>
              JoinRise – <a href="https://app.joinrise.co" target="_blank" rel="noopener noreferrer">https://app.joinrise.co</a>
            </li>
            <li>
              Remotive – <a href="https://remotive.com" target="_blank" rel="noopener noreferrer">https://remotive.com</a>
            </li>
          </ul>
        </div>
        <div>
          <p>{t("icons")}:</p>
          <ul>
            <li>
              Boxicons – <a href="https://boxicons.com" target="_blank" rel="noopener noreferrer">https://boxicons.com</a>
            </li>
          </ul>

        </div>

      </div>
      <p>© 2025 Karol Robak</p>
    </footer>
  );
}

export default Footer;
