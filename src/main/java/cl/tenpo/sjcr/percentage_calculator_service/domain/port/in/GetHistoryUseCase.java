package cl.tenpo.sjcr.percentage_calculator_service.domain.port.in;

import cl.tenpo.sjcr.percentage_calculator_service.domain.model.CallHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.UUID;

public interface GetHistoryUseCase {

    CallHistory getCallById(UUID id);

    Page<CallHistory> getHistory(Pageable pageable);

    Page<CallHistory> getHistoryByDateRange(LocalDateTime from, LocalDateTime to, Pageable pageable);

    Page<CallHistory> getHistoryByEndpoint(String endpoint, Pageable pageable);

    Page<CallHistory> getHistoryBySuccessful(Pageable pageable);

    Page<CallHistory> getHistoryByUnsuccessful(Pageable pageable);

    long countTotalCalls();
}
