package cl.tenpo.sjcr.percentage_calculator_service.infrastructure.adapter.in.rest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Paginated response")
public record PageResultResponseDto<T>(
        @Schema(description = "List of items in current page") List<T> content,

        @Schema(description = "Whether this is the first page") boolean first,

        @Schema(description = "Whether this is the last page") boolean last,

        @Schema(description = "Page size (items per page)") int pageSize,

        @Schema(description = "Current page number (0-based)") int pageNumber,

        @Schema(description = "Total number of elements across all pages") long totalElements,

        @Schema(description = "Total number of pages") int totalPages) {
}
