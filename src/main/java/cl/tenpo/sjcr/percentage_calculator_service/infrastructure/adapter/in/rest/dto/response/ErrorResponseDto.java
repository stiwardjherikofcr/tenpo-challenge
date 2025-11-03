package cl.tenpo.sjcr.percentage_calculator_service.infrastructure.adapter.in.rest.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
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
@Schema(description = "Error response")
public class ErrorResponseDto {

    @Schema(description = "Error message", example = "Percentage service unavailable")
    private String message;

    @Schema(description = "Error details", example = "External service timeout")
    private String details;

    @Schema(description = "HTTP status code", example = "503")
    private int status;

    @Schema(description = "Timestamp of error")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    @Schema(description = "Request path", example = "/api/v1/calculate")
    private String path;
}
