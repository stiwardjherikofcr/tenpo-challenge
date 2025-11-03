package cl.tenpo.sjcr.percentage_calculator_service.infrastructure.adapter.in.rest.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonRawValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Call history record")
public class CallHistoryResponseDto {

    @Schema(description = "Record ID (UUID)", example = "550e8400-e29b-41d4-a716-446655440000")
    private String id;

    @Schema(description = "Timestamp of the call")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    @Schema(description = "API endpoint called", example = "/api/v1/calculate")
    private String endpoint;

    @Schema(description = "HTTP method", example = "POST")
    private String method;

    @Schema(description = "Request parameters as JSON")
    @JsonRawValue
    private String requestParams;

    @Schema(description = "Response as JSON")
    @JsonRawValue
    private String response;

    @Schema(description = "Error message if failed")
    private String errorMessage;

    @Schema(description = "Whether the call was successful", example = "true")
    private boolean success;
}
