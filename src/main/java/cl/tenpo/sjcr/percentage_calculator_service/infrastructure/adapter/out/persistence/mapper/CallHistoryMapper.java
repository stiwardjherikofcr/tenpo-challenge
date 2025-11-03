package cl.tenpo.sjcr.percentage_calculator_service.infrastructure.adapter.out.persistence.mapper;

import cl.tenpo.sjcr.percentage_calculator_service.domain.model.CallHistory;
import cl.tenpo.sjcr.percentage_calculator_service.infrastructure.adapter.out.persistence.entity.CallHistoryEntity;
import org.springframework.stereotype.Component;

@Component
public class CallHistoryMapper {

    public CallHistoryEntity toEntity(CallHistory domain) {
        if (domain == null) {
            return null;
        }

        return CallHistoryEntity.builder()
                .id(null)
                .timestamp(domain.getTimestamp())
                .endpoint(domain.getEndpoint())
                .method(domain.getHttpMethod())
                .requestParams(domain.getRequestParameters())
                .response(domain.getResponse())
                .errorMessage(domain.getErrorMessage())
                .success(domain.isSuccessful())
                .build();
    }

    public CallHistory toDomain(CallHistoryEntity entity) {
        if (entity == null) {
            return null;
        }

        return CallHistory.builder()
                .id(CallHistory.CallHistoryId.of(entity.getId()))
                .timestamp(entity.getTimestamp())
                .endpoint(entity.getEndpoint())
                .httpMethod(entity.getMethod())
                .requestParameters(entity.getRequestParams())
                .response(entity.getResponse())
                .httpStatusCode(entity.isSuccess() ? 200 : 500)
                .errorMessage(entity.getErrorMessage())
                .build();
    }
}
