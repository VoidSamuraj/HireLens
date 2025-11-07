package com.voidsamuraj.HireLens.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * Wrapper DTO for sending a job offer request as JSON.
 * <p>
 * The payload will be serialized as a JSON object with a single field:
 * </p>
 * <pre>
 * {
 *   "text": "job offer description or content here"
 * }
 * </pre>
 * <p>
 * This class is intended to standardize the request format for APIs that
 * accept a textual job offer payload.
 * </p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobOfferRequest {

    /** Text content of the job offer. */
    private String text;
}