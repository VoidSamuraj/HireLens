package com.voidsamuraj.HireLens.dto.aggregation;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Generic wrapper for sending WebSocket messages with type information.
 * <p>
 * Type parameter:
 * <ul>
 *     <li><code>T</code>: the type of the payload being sent (e.g., {@link DataType}, {@link DataUpdateStatus}).</li>
 * </ul>
 * </p>
 *
 * <p>Fields:</p>
 * <ul>
 *     <li><b>type</b>: indicates the kind of data contained in the payload.
 *         <ul>
 *             <li>{@code STATUS} - status update of a background job</li>
 *             <li>{@code CHART_MAP} - chart or aggregated data</li>
 *         </ul>
 *     </li>
 *     <li><b>payload</b>: the actual data being sent</li>
 * </ul>
 *
 * <p>Example usage:</p>
 * <pre>
 * WsPayload&lt;DataUpdateStatus&gt; statusMessage =
 *     new WsPayload<>(WsPayload.DataType.STATUS, new DataUpdateStatus());
 *
 * WsPayload&lt;AggregatesDto&gt; chartMessage =
 *     new WsPayload<>(WsPayload.DataType.CHART_MAP, aggregatesDto);
 * </pre>
 *
 * @param <T> type of the payload
 */
@Data
@AllArgsConstructor
public class WsPayload<T> {

    /** Enum representing the type of data in the payload. */
    public enum DataType {
        /** Status update of a background job. */
        STATUS,

        /** Chart or aggregated data. */
        CHART_MAP
    }

    /** The type of the data being sent. */
    private DataType type;

    /** The actual payload data. */
    private T payload;
}
