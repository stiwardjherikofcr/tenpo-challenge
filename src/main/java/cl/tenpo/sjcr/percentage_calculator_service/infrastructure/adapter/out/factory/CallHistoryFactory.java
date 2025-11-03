package cl.tenpo.sjcr.percentage_calculator_service.infrastructure.adapter.out.factory;

import cl.tenpo.sjcr.percentage_calculator_service.domain.model.CallHistory;
import cl.tenpo.sjcr.percentage_calculator_service.domain.valueobject.CalculationRequest;
import cl.tenpo.sjcr.percentage_calculator_service.domain.valueobject.CalculationResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

public class CallHistoryFactory {

    private static final Logger log = LoggerFactory.getLogger(CallHistoryFactory.class);
    private static final int SUCCESS_HTTP_STATUS = 200;

    private final ObjectMapper objectMapper;

    public CallHistoryFactory(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public CallHistory createFromSuccess(
            CalculationRequest request,
            CalculationResult result,
            String endpoint,
            String httpMethod,
            LocalDateTime timestamp
    ) {
        String requestJson = serializeToJson(request, "request");
        String responseJson = serializeToJson(result, "response");

        return CallHistory.builder()
                .endpoint(endpoint)
                .httpMethod(httpMethod)
                .httpStatusCode(SUCCESS_HTTP_STATUS)
                .requestParameters(requestJson)
                .response(responseJson)
                .timestamp(timestamp)
                .build();
    }

    public CallHistory createFromFailure(
            CalculationRequest request,
            String errorMessage,
            int httpStatusCode,
            String endpoint,
            String httpMethod,
            LocalDateTime timestamp
    ) {
        String requestJson = serializeToJson(request, "request");

        return CallHistory.builder()
                .endpoint(endpoint)
                .httpMethod(httpMethod)
                .httpStatusCode(httpStatusCode)
                .requestParameters(requestJson)
                .errorMessage(errorMessage)
                .timestamp(timestamp)
                .build();
    }

    private String serializeToJson(Object object, String objectType) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.warn("Failed to serialize {} to JSON, using toString() fallback", objectType, e);
            return object.toString();
        }
    }
}