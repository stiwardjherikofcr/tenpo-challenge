package cl.tenpo.sjcr.percentage_calculator_service.infrastructure.adapter.in.rest.controller;

import cl.tenpo.sjcr.percentage_calculator_service.domain.port.in.CalculateUseCase;
import cl.tenpo.sjcr.percentage_calculator_service.domain.valueobject.CalculationRequest;
import cl.tenpo.sjcr.percentage_calculator_service.domain.valueobject.CalculationResult;
import cl.tenpo.sjcr.percentage_calculator_service.infrastructure.adapter.in.rest.dto.request.CalculationRequestDto;
import cl.tenpo.sjcr.percentage_calculator_service.infrastructure.adapter.in.rest.dto.response.CalculationResponseDto;
import cl.tenpo.sjcr.percentage_calculator_service.infrastructure.adapter.in.rest.mapper.CalculationDtoMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Calculation", description = "Calculation operations with dynamic percentage")
public class CalculationController {

    private static final Logger log = LoggerFactory.getLogger(CalculationController.class);

    private final CalculateUseCase calculateUseCase;
    private final CalculationDtoMapper mapper;

    public CalculationController(
            CalculateUseCase calculateUseCase,
            CalculationDtoMapper mapper
    ) {
        this.calculateUseCase = calculateUseCase;
        this.mapper = mapper;
    }

    @PostMapping(value = "/calculate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Calculate sum with dynamic percentage",
            description = "Receives two numbers, sums them, and applies a dynamic percentage from external service (with cache fallback)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Calculation successful",
                    content = @Content(schema = @Schema(implementation = CalculationResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input"
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Service unavailable (external service failed and no cached value)"
            )
    })
    public ResponseEntity<CalculationResponseDto> calculate(@Valid @RequestBody CalculationRequestDto requestDto) {
        log.info("Received calculation request: num1={}, num2={}", requestDto.getNum1(), requestDto.getNum2());

        CalculationRequest request = mapper.toDomain(requestDto);
        CalculationResult result = calculateUseCase.execute(request);
        CalculationResponseDto response = mapper.toDto(result);

        log.info("Calculation completed: result={}", response.getResult());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Simple health check endpoint")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }

}
