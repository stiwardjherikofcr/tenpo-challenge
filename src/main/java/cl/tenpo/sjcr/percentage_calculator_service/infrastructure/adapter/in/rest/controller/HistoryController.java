package cl.tenpo.sjcr.percentage_calculator_service.infrastructure.adapter.in.rest.controller;

import cl.tenpo.sjcr.percentage_calculator_service.domain.model.CallHistory;
import cl.tenpo.sjcr.percentage_calculator_service.domain.port.in.GetHistoryUseCase;
import cl.tenpo.sjcr.percentage_calculator_service.infrastructure.adapter.in.rest.dto.request.PaginationRequestDto;
import cl.tenpo.sjcr.percentage_calculator_service.infrastructure.adapter.in.rest.dto.response.CallHistoryResponseDto;
import cl.tenpo.sjcr.percentage_calculator_service.infrastructure.adapter.in.rest.dto.response.PageResultResponseDto;
import cl.tenpo.sjcr.percentage_calculator_service.infrastructure.adapter.in.rest.mapper.CallHistoryDtoMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/history")
@Tag(name = "History", description = "Call history operations")
@Validated
public class HistoryController {

        private static final Logger log = LoggerFactory.getLogger(HistoryController.class);

        private final GetHistoryUseCase getHistoryUseCase;
        private final CallHistoryDtoMapper mapper;

        public HistoryController(
                        GetHistoryUseCase getHistoryUseCase,
                        CallHistoryDtoMapper mapper) {
                this.getHistoryUseCase = getHistoryUseCase;
                this.mapper = mapper;
        }

        private <T> PageResultResponseDto<T> toPageResult(Page<T> page) {
                return new PageResultResponseDto<>(
                                page.getContent(),
                                page.isFirst(),
                                page.isLast(),
                                page.getSize(),
                                page.getNumber(),
                                page.getTotalElements(),
                                page.getTotalPages());
        }

        @GetMapping(value = { "", "/" }, produces = MediaType.APPLICATION_JSON_VALUE)
        @Operation(summary = "Get call history", description = "Retrieves paginated call history with date, endpoint, parameters, and response/error information")
        @ApiResponse(responseCode = "200", description = "History retrieved successfully", content = @Content(schema = @Schema(implementation = PageResultResponseDto.class)))
        public ResponseEntity<PageResultResponseDto<CallHistoryResponseDto>> getHistory(
                        @Parameter(description = "Page number (0-indexed)", example = "0") @RequestParam(defaultValue = "0") @Min(0) int page,

                        @Parameter(description = "Page size (max 100)", example = "10") @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,

                        @Parameter(description = "Sort field", example = "timestamp") @RequestParam(defaultValue = "timestamp") String sortBy,

                        @Parameter(description = "Sort direction", example = "DESC") @RequestParam(defaultValue = "DESC") String sortDirection) {
                log.info("Retrieving call history: page={}, size={}, sortBy={}, sortDirection={}",
                                page, size, sortBy, sortDirection);

                Sort.Direction direction = Sort.Direction.fromString(sortDirection);
                Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

                Page<CallHistory> historyPage = getHistoryUseCase.getHistory(pageable);
                Page<CallHistoryResponseDto> responsePage = historyPage.map(mapper::toDto);

                log.info("Retrieved {} history records", historyPage.getTotalElements());
                return ResponseEntity.ok(toPageResult(responsePage));
        }

        @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
        @Operation(summary = "Get call by ID", description = "Retrieves a specific call history record by its unique identifier")
        @ApiResponse(responseCode = "200", description = "Call history record retrieved successfully", content = @Content(schema = @Schema(implementation = CallHistoryResponseDto.class)))
        @ApiResponse(responseCode = "404", description = "Call history record not found")
        public ResponseEntity<CallHistoryResponseDto> getCallById(
                        @Parameter(description = "Call history unique identifier", example = "123e4567-e89b-12d3-a456-426614174000") @PathVariable UUID id) {
                log.info("Retrieving call history by id: {}", id);

                CallHistory callHistory = getHistoryUseCase.getCallById(id);
                CallHistoryResponseDto response = mapper.toDto(callHistory);

                log.info("Retrieved call history for id: {}", id);
                return ResponseEntity.ok(response);
        }

        @GetMapping(value = "/date-range", produces = MediaType.APPLICATION_JSON_VALUE)
        @Operation(summary = "Get call history by date range", description = "Retrieves paginated call history filtered by a specific date range")
        @ApiResponse(responseCode = "200", description = "History retrieved successfully", content = @Content(schema = @Schema(implementation = PageResultResponseDto.class)))
        @ApiResponse(responseCode = "400", description = "Invalid date range (from date must be before to date)")
        public ResponseEntity<PageResultResponseDto<CallHistoryResponseDto>> getHistoryByDateRange(
                        @Parameter(description = "Start date and time (ISO 8601 format)", example = "2024-01-01T00:00:00") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,

                        @Parameter(description = "End date and time (ISO 8601 format)", example = "2024-12-31T23:59:59") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,

                        @Parameter(description = "Page number (0-indexed)", example = "0") @RequestParam(defaultValue = "0") @Min(0) int page,

                        @Parameter(description = "Page size (max 100)", example = "10") @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,

                        @Parameter(description = "Sort field", example = "timestamp") @RequestParam(defaultValue = "timestamp") String sortBy,

                        @Parameter(description = "Sort direction", example = "DESC") @RequestParam(defaultValue = "DESC") String sortDirection) {
                Sort.Direction direction = Sort.Direction.fromString(sortDirection);
                Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

                Page<CallHistory> historyPage = getHistoryUseCase.getHistoryByDateRange(from, to, pageable);
                Page<CallHistoryResponseDto> responsePage = historyPage.map(mapper::toDto);

                return ResponseEntity.ok(toPageResult(responsePage));
        }

        @GetMapping(value = "/by-endpoint", produces = MediaType.APPLICATION_JSON_VALUE)
        @Operation(summary = "Get call history by endpoint", description = "Retrieves paginated call history filtered by a specific endpoint path")
        @ApiResponse(responseCode = "200", description = "History retrieved successfully", content = @Content(schema = @Schema(implementation = PageResultResponseDto.class)))
        @ApiResponse(responseCode = "400", description = "Invalid endpoint parameter (cannot be null or blank)")
        public ResponseEntity<PageResultResponseDto<CallHistoryResponseDto>> getHistoryByEndpoint(
                        @Parameter(description = "Endpoint path to filter by", example = "/api/v1/calculate") @RequestParam String endpoint,

                        @Parameter(description = "Page number (0-indexed)", example = "0") @RequestParam(defaultValue = "0") @Min(0) int page,

                        @Parameter(description = "Page size (max 100)", example = "10") @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,

                        @Parameter(description = "Sort field", example = "timestamp") @RequestParam(defaultValue = "timestamp") String sortBy,

                        @Parameter(description = "Sort direction", example = "DESC") @RequestParam(defaultValue = "DESC") String sortDirection) {
                log.info("Retrieving call history by endpoint: {}, page={}, size={}",
                                endpoint, page, size);

                Sort.Direction direction = Sort.Direction.fromString(sortDirection);
                Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

                Page<CallHistory> historyPage = getHistoryUseCase.getHistoryByEndpoint(endpoint, pageable);
                Page<CallHistoryResponseDto> responsePage = historyPage.map(mapper::toDto);

                log.info("Retrieved {} history records for endpoint: {}", historyPage.getTotalElements(), endpoint);
                return ResponseEntity.ok(toPageResult(responsePage));
        }

        @GetMapping(value = "/successful", produces = MediaType.APPLICATION_JSON_VALUE)
        @Operation(summary = "Get successful calls", description = "Retrieves paginated call history for successful calls (HTTP 2xx status codes)")
        @ApiResponse(responseCode = "200", description = "Successful calls history retrieved successfully", content = @Content(schema = @Schema(implementation = PageResultResponseDto.class)))
        public ResponseEntity<PageResultResponseDto<CallHistoryResponseDto>> getHistoryBySuccessful(
                        @Parameter(description = "Page number (0-indexed)", example = "0") @RequestParam(defaultValue = "0") @Min(0) int page,

                        @Parameter(description = "Page size (max 100)", example = "10") @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,

                        @Parameter(description = "Sort field", example = "timestamp") @RequestParam(defaultValue = "timestamp") String sortBy,

                        @Parameter(description = "Sort direction", example = "DESC") @RequestParam(defaultValue = "DESC") String sortDirection) {
                log.info("Retrieving successful call history: page={}, size={}", page, size);

                Sort.Direction direction = Sort.Direction.fromString(sortDirection);
                Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

                Page<CallHistory> historyPage = getHistoryUseCase.getHistoryBySuccessful(pageable);
                Page<CallHistoryResponseDto> responsePage = historyPage.map(mapper::toDto);

                log.info("Retrieved {} successful call history records", historyPage.getTotalElements());
                return ResponseEntity.ok(toPageResult(responsePage));
        }

        @GetMapping(value = "/failed", produces = MediaType.APPLICATION_JSON_VALUE)
        @Operation(summary = "Get failed calls", description = "Retrieves paginated call history for failed calls (HTTP 4xx/5xx status codes or with error messages)")
        @ApiResponse(responseCode = "200", description = "Failed calls history retrieved successfully", content = @Content(schema = @Schema(implementation = PageResultResponseDto.class)))
        public ResponseEntity<PageResultResponseDto<CallHistoryResponseDto>> getHistoryByUnsuccessful(
                        @Parameter(description = "Page number (0-indexed)", example = "0") @RequestParam(defaultValue = "0") @Min(0) int page,

                        @Parameter(description = "Page size (max 100)", example = "10") @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,

                        @Parameter(description = "Sort field", example = "timestamp") @RequestParam(defaultValue = "timestamp") String sortBy,

                        @Parameter(description = "Sort direction", example = "DESC") @RequestParam(defaultValue = "DESC") String sortDirection) {
                log.info("Retrieving failed call history: page={}, size={}", page, size);

                Sort.Direction direction = Sort.Direction.fromString(sortDirection);
                Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

                Page<CallHistory> historyPage = getHistoryUseCase.getHistoryByUnsuccessful(pageable);
                Page<CallHistoryResponseDto> responsePage = historyPage.map(mapper::toDto);

                log.info("Retrieved {} failed call history records", historyPage.getTotalElements());
                return ResponseEntity.ok(toPageResult(responsePage));
        }

        @GetMapping(value = "/count", produces = MediaType.APPLICATION_JSON_VALUE)
        @Operation(summary = "Count total calls", description = "Returns the total count of all calls in the history")
        @ApiResponse(responseCode = "200", description = "Total count retrieved successfully", content = @Content(schema = @Schema(implementation = Map.class)))
        public ResponseEntity<Map<String, Long>> countTotalCalls() {
                log.info("Retrieving total call count");

                long totalCalls = getHistoryUseCase.countTotalCalls();

                log.info("Total calls in history: {}", totalCalls);
                return ResponseEntity.ok(Map.of("totalCalls", totalCalls));
        }

}
