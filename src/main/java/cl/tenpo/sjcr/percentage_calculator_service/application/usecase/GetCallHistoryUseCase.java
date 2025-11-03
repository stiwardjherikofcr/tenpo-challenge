package cl.tenpo.sjcr.percentage_calculator_service.application.usecase;

import cl.tenpo.sjcr.percentage_calculator_service.domain.model.CallHistory;
import cl.tenpo.sjcr.percentage_calculator_service.domain.port.in.GetHistoryUseCase;
import cl.tenpo.sjcr.percentage_calculator_service.domain.port.out.CallHistoryRepositoryPort;
import cl.tenpo.sjcr.percentage_calculator_service.domain.port.out.CallHistoryRepositoryPort.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class GetCallHistoryUseCase implements GetHistoryUseCase {

    private static final Logger log = LoggerFactory.getLogger(GetCallHistoryUseCase.class);

    private final CallHistoryRepositoryPort repository;

    public GetCallHistoryUseCase(CallHistoryRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public CallHistory getCallById(UUID id) {
        Objects.requireNonNull(id, "Call history ID cannot be null");

        log.debug("Retrieving call history by ID: {}", id);
        CallHistory.CallHistoryId callHistoryId = CallHistory.CallHistoryId.of(id);
        CallHistory callHistory = repository.findById(callHistoryId)
                .orElseThrow(() -> new IllegalArgumentException("Call history not found for ID: " + id));

        log.info("Retrieved call history for ID: {}", id);
        return callHistory;
    }

    @Override
    public Page<CallHistory> getHistory(Pageable pageable) {
        log.debug("Retrieving call history with pagination: {}", pageable);
        PageResult<CallHistory> history = repository.findAll(pageable);

        log.info("Retrieved {} call history records", history.getTotalElements());
        return new PageImpl<>(history);
    }

    @Override
    public Page<CallHistory> getHistoryByDateRange(LocalDateTime from, LocalDateTime to, Pageable pageable) {
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("The 'from' date must be before the 'to' date.");
        }

        log.debug("Retrieving call history from {} to {} with pagination: {}", from, to, pageable);
        PageResult<CallHistory> history = repository.findByDateRange(from, to, pageable);

        log.info("Retrieved {} call history records from {} to {}", history.getTotalElements(), from, to);
        return new PageImpl<>(history);
    }

    @Override
    public Page<CallHistory> getHistoryByEndpoint(String endpoint, Pageable pageable) {
        if (endpoint == null || endpoint.isBlank()) {
            throw new IllegalArgumentException("Endpoint must not be null or blank.");
        }

        log.debug("Retrieving call history for endpoint '{}' with pagination: {}", endpoint, pageable);
        PageResult<CallHistory> history = repository.findByEndpoint(endpoint, pageable);

        log.info("Retrieved {} call history records for endpoint '{}'", history.getTotalElements(), endpoint);
        return new PageImpl<>(history);
    }

    @Override
    public Page<CallHistory> getHistoryBySuccessful(Pageable pageable) {
        log.debug("Retrieving successful call history with pagination: {}", pageable);
        PageResult<CallHistory> history = repository.findSuccessfulCalls(pageable);

        log.info("Retrieved {} successful call history records", history.getTotalElements());
        return new PageImpl<>(history);
    }

    @Override
    public Page<CallHistory> getHistoryByUnsuccessful(Pageable pageable) {
        log.debug("Retrieving unsuccessful call history with pagination: {}", pageable);
        PageResult<CallHistory> history = repository.findFailedCalls(pageable);

        log.info("Retrieved {} unsuccessful call history records", history.getTotalElements());
        return new PageImpl<>(history);
    }

    @Override
    public long countTotalCalls() {
        log.debug("Counting total call history records");
        long count = repository.count();
        log.info("Total call history records: {}", count);
        return count;
    }

    private record PageImpl<T>(PageResult<T> pageResult) implements Page<T> {

        private PageImpl(PageResult<T> pageResult) {
            this.pageResult = Objects.requireNonNull(pageResult,
                    "Page result cannot be null");
        }

        @Override
        @NonNull
        public List<T> getContent() {
            return pageResult.getContent();
        }

        @Override
        public boolean hasContent() {
            return !pageResult.getContent().isEmpty();
        }

        @Override
        @NonNull
        public Sort getSort() {
            return Sort.unsorted();
        }

        @Override
        public int getTotalPages() {
            return pageResult.getTotalPages();
        }

        @Override
        public long getTotalElements() {
            return pageResult.getTotalElements();
        }

        @Override
        @NonNull
        public <U> Page<U> map(@NonNull Function<? super T, ? extends U> converter) {
            List<U> convertedContent = pageResult.getContent().stream()
                    .map(converter)
                    .collect(Collectors.toList());

            PageResult<U> convertedPageResult = new PageResult<>() {
                @Override
                public List<U> getContent() {
                    return convertedContent;
                }

                @Override
                public int getPageNumber() {
                    return pageResult.getPageNumber();
                }

                @Override
                public int getPageSize() {
                    return pageResult.getPageSize();
                }

                @Override
                public long getTotalElements() {
                    return pageResult.getTotalElements();
                }

                @Override
                public int getTotalPages() {
                    return pageResult.getTotalPages();
                }

                @Override
                public boolean isFirst() {
                    return pageResult.isFirst();
                }

                @Override
                public boolean isLast() {
                    return pageResult.isLast();
                }

                @Override
                public boolean hasNext() {
                    return pageResult.hasNext();
                }

                @Override
                public boolean hasPrevious() {
                    return pageResult.hasPrevious();
                }
            };

            return new PageImpl<>(convertedPageResult);
        }

        @Override
        public int getNumber() {
            return pageResult.getPageNumber();
        }

        @Override
        public int getSize() {
            return pageResult.getPageSize();
        }

        @Override
        public int getNumberOfElements() {
            return pageResult.getContent().size();
        }

        @Override
        public boolean hasNext() {
            return pageResult.hasNext();
        }

        @Override
        public boolean hasPrevious() {
            return pageResult.hasPrevious();
        }

        @Override
        @NonNull
        public Pageable nextPageable() {
            return pageResult.hasNext() ? PageRequest.of(getNumber() + 1, getSize()) : Pageable.unpaged();
        }

        @Override
        @NonNull
        public Pageable previousPageable() {
            return pageResult.hasPrevious() ? PageRequest.of(getNumber() - 1, getSize()) : Pageable.unpaged();
        }

        @Override
        public boolean isFirst() {
            return pageResult.isFirst();
        }

        @Override
        public boolean isLast() {
            return pageResult.isLast();
        }

        @Override
        @NonNull
        public Iterator<T> iterator() {
            return pageResult.getContent().iterator();
        }
    }
}
