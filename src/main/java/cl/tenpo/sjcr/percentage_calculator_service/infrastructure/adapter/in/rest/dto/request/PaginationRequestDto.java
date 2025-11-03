package cl.tenpo.sjcr.percentage_calculator_service.infrastructure.adapter.in.rest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "Pagination parameters")
public class PaginationRequestDto {

    @Min(value = 0, message = "Page number must be non-negative")
    @Schema(description = "Page number (0-indexed)", example = "0", defaultValue = "0")
    private final Integer page;

    @Min(value = 1, message = "Page size must be at least 1")
    @Max(value = 100, message = "Page size cannot exceed 100")
    @Schema(description = "Number of items per page", example = "10", defaultValue = "10", minimum = "1", maximum = "100")
    private final Integer size;

    public int getPageOrDefault() {
        return page != null ? page : 0;
    }

    public int getSizeOrDefault() {
        return size != null ? size : 10;
    }
}
