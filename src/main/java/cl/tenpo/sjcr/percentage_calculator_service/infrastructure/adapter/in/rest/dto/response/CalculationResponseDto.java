package cl.tenpo.sjcr.percentage_calculator_service.infrastructure.adapter.in.rest.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response containing calculation result")
public class CalculationResponseDto {

    @Schema(description = "Final result (sum + percentage)", example = "35.44")
    private BigDecimal result;

    @Schema(description = "Original sum before percentage", example = "30.8")
    private BigDecimal originalSum;

    @Schema(description = "Applied percentage value", example = "15.0")
    private BigDecimal appliedPercentage;

    @Schema(description = "Timestamp of calculation")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
}
