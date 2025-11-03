package cl.tenpo.sjcr.percentage_calculator_service.infrastructure.adapter.in.rest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request for calculation with two numbers")
public class CalculationRequestDto {

    @NotNull(message = "num1 is required")
    @Schema(description = "First number", example = "10.5", required = true)
    private BigDecimal num1;

    @NotNull(message = "num2 is required")
    @Schema(description = "Second number", example = "20.3", required = true)
    private BigDecimal num2;
}
