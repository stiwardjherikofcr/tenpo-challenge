package cl.tenpo.sjcr.percentage_calculator_service.domain.port.out;

import cl.tenpo.sjcr.percentage_calculator_service.domain.model.CallHistory;
import cl.tenpo.sjcr.percentage_calculator_service.domain.model.CallHistory.CallHistoryId;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CallHistoryRepositoryPort {

    CallHistory save(CallHistory callHistory);

    Optional<CallHistory> findById(CallHistoryId id);

    PageResult<CallHistory> findAll(Pageable pageable);

    PageResult<CallHistory> findByDateRange(LocalDateTime from, LocalDateTime to, Pageable pageable);

    PageResult<CallHistory> findByEndpoint(String endpoint, Pageable pageable);

    PageResult<CallHistory> findSuccessfulCalls(Pageable pageable);

    PageResult<CallHistory> findFailedCalls(Pageable pageable);

    long count();

    interface PageResult<T> {
        List<T> getContent();

        int getTotalPages();

        long getTotalElements();

        int getPageNumber();

        int getPageSize();

        boolean hasNext();

        boolean hasPrevious();

        boolean isFirst();

        boolean isLast();
    }
}
