import '../styles/card.css'
import type { CardProps } from '../utils/types.ts'

/**
 * Card component provides a styled container with a header and children content area.
 *
 * - Displays a header at the top with customizable inline styles.
 * - Renders children inside the card content area.
 * - Allows extending styles via optional className prop.
 *
 * @param children React nodes to be rendered inside the card body
 * @param className optional CSS class(es) to customize styling of the card container
 * @param header text to display as the card's header/title
 * @param headerStyle optional inline CSS styles for the header
 */
function Card({ children, className, header, headerStyle }: CardProps) {
  return (
    <div className={`card-container ${className}`}>
      <p className="card-title" style={headerStyle}>{header}</p>
      <div className={`card-content`}>
        {children}
      </div>
    </div>
  );
}

export default Card