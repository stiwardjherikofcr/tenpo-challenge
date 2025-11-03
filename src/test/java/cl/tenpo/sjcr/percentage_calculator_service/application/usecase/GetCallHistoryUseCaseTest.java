package cl.tenpo.sjcr.percentage_calculator_service.application.usecase;

import cl.tenpo.sjcr.percentage_calculator_service.domain.model.CallHistory;
import cl.tenpo.sjcr.percentage_calculator_service.domain.port.out.CallHistoryRepositoryPort;
import cl.tenpo.sjcr.percentage_calculator_service.domain.port.out.CallHistoryRepositoryPort.PageResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetCallHistoryUseCase Tests")
class GetCallHistoryUseCaseTest {

    @Mock
    private CallHistoryRepositoryPort repository;

    private GetCallHistoryUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetCallHistoryUseCase(repository);
    }

    @Test
    @DisplayName("Should retrieve all history with pagination")
    void shouldRetrieveAllHistoryWithPagination() {

        Pageable pageable = PageRequest.of(0, 10);
        List<CallHistory> histories = List.of(
                createCallHistory("/api/calculate", "POST", 200),
                createCallHistory("/api/history", "GET", 200));
        PageResult<CallHistory> pageResult = createPageResult(histories, 0, 10, 2);

        when(repository.findAll(pageable)).thenReturn(pageResult);

        Page<CallHistory> result = useCase.getHistory(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        verify(repository).findAll(pageable);
    }

    @Test
    @DisplayName("Should retrieve history by date range")
    void shouldRetrieveHistoryByDateRange() {

        LocalDateTime from = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime to = LocalDateTime.of(2024, 12, 31, 23, 59);
        Pageable pageable = PageRequest.of(0, 10);
        List<CallHistory> histories = List.of(createCallHistory("/api/calculate", "POST", 200));
        PageResult<CallHistory> pageResult = createPageResult(histories, 0, 10, 1);

        when(repository.findByDateRange(from, to, pageable)).thenReturn(pageResult);

        Page<CallHistory> result = useCase.getHistoryByDateRange(from, to, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(repository).findByDateRange(from, to, pageable);
    }

    @Test
    @DisplayName("Should throw exception when from date is after to date")
    void shouldThrowExceptionWhenFromDateIsAfterToDate() {

        LocalDateTime from = LocalDateTime.of(2024, 12, 31, 23, 59);
        LocalDateTime to = LocalDateTime.of(2024, 1, 1, 0, 0);
        Pageable pageable = PageRequest.of(0, 10);

        assertThatThrownBy(() -> useCase.getHistoryByDateRange(from, to, pageable))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("'from' date must be before the 'to' date");
    }

    @Test
    @DisplayName("Should retrieve history by endpoint")
    void shouldRetrieveHistoryByEndpoint() {

        String endpoint = "/api/calculate";
        Pageable pageable = PageRequest.of(0, 10);
        List<CallHistory> histories = List.of(createCallHistory(endpoint, "POST", 200));
        PageResult<CallHistory> pageResult = createPageResult(histories, 0, 10, 1);

        when(repository.findByEndpoint(endpoint, pageable)).thenReturn(pageResult);

        Page<CallHistory> result = useCase.getHistoryByEndpoint(endpoint, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(repository).findByEndpoint(endpoint, pageable);
    }

    @Test
    @DisplayName("Should throw exception when endpoint is null")
    void shouldThrowExceptionWhenEndpointIsNull() {

        Pageable pageable = PageRequest.of(0, 10);

        assertThatThrownBy(() -> useCase.getHistoryByEndpoint(null, pageable))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Endpoint must not be null or blank");
    }

    @Test
    @DisplayName("Should throw exception when endpoint is blank")
    void shouldThrowExceptionWhenEndpointIsBlank() {

        Pageable pageable = PageRequest.of(0, 10);

        assertThatThrownBy(() -> useCase.getHistoryByEndpoint("  ", pageable))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Endpoint must not be null or blank");
    }

    @Test
    @DisplayName("Should retrieve successful calls")
    void shouldRetrieveSuccessfulCalls() {

        Pageable pageable = PageRequest.of(0, 10);
        List<CallHistory> histories = List.of(createCallHistory("/api/calculate", "POST", 200));
        PageResult<CallHistory> pageResult = createPageResult(histories, 0, 10, 1);

        when(repository.findSuccessfulCalls(pageable)).thenReturn(pageResult);

        Page<CallHistory> result = useCase.getHistoryBySuccessful(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(repository).findSuccessfulCalls(pageable);
    }

    @Test
    @DisplayName("Should retrieve unsuccessful calls")
    void shouldRetrieveUnsuccessfulCalls() {

        Pageable pageable = PageRequest.of(0, 10);
        List<CallHistory> histories = List.of(createCallHistory("/api/calculate", "POST", 500));
        PageResult<CallHistory> pageResult = createPageResult(histories, 0, 10, 1);

        when(repository.findFailedCalls(pageable)).thenReturn(pageResult);

        Page<CallHistory> result = useCase.getHistoryByUnsuccessful(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(repository).findFailedCalls(pageable);
    }

    @Test
    @DisplayName("Should count total calls")
    void shouldCountTotalCalls() {

        when(repository.count()).thenReturn(100L);

        long count = useCase.countTotalCalls();

        assertThat(count).isEqualTo(100L);
        verify(repository).count();
    }

    @Test
    @DisplayName("Should handle empty results")
    void shouldHandleEmptyResults() {

        Pageable pageable = PageRequest.of(0, 10);
        PageResult<CallHistory> emptyPageResult = createPageResult(Collections.emptyList(), 0, 10, 0);

        when(repository.findAll(pageable)).thenReturn(emptyPageResult);

        Page<CallHistory> result = useCase.getHistory(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
    }

    @Test
    @DisplayName("Should handle multiple pages")
    void shouldHandleMultiplePages() {

        Pageable pageable = PageRequest.of(1, 10);
        List<CallHistory> histories = List.of(createCallHistory("/api/calculate", "POST", 200));
        PageResult<CallHistory> pageResult = new PageResult<>() {
            @Override
            public List<CallHistory> getContent() {
                return histories;
            }

            @Override
            public int getTotalPages() {
                return 5;
            }

            @Override
            public long getTotalElements() {
                return 50;
            }

            @Override
            public int getPageNumber() {
                return 1;
            }

            @Override
            public int getPageSize() {
                return 10;
            }

            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public boolean hasPrevious() {
                return true;
            }

            @Override
            public boolean isFirst() {
                return false;
            }

            @Override
            public boolean isLast() {
                return false;
            }
        };

        when(repository.findAll(pageable)).thenReturn(pageResult);

        Page<CallHistory> result = useCase.getHistory(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getTotalPages()).isEqualTo(5);
        assertThat(result.getTotalElements()).isEqualTo(50);
        assertThat(result.getNumber()).isEqualTo(1);
        assertThat(result.hasNext()).isTrue();
        assertThat(result.hasPrevious()).isTrue();
        assertThat(result.isFirst()).isFalse();
        assertThat(result.isLast()).isFalse();
    }

    @Test
    @DisplayName("Should retrieve call history by ID successfully")
    void shouldRetrieveCallHistoryByIdSuccessfully() {

        UUID id = UUID.randomUUID();
        CallHistory.CallHistoryId callHistoryId = CallHistory.CallHistoryId.of(id);
        CallHistory callHistory = createCallHistory("/api/calculate", "POST", 200);

        when(repository.findById(callHistoryId)).thenReturn(Optional.of(callHistory));

        CallHistory result = useCase.getCallById(id);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(callHistory);
        verify(repository).findById(callHistoryId);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when call history ID not found")
    void shouldThrowExceptionWhenCallHistoryIdNotFound() {

        UUID id = UUID.randomUUID();
        CallHistory.CallHistoryId callHistoryId = CallHistory.CallHistoryId.of(id);

        when(repository.findById(callHistoryId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.getCallById(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Call history not found for ID: " + id);
    }

    @Test
    @DisplayName("Should throw NullPointerException when ID is null")
    void shouldThrowExceptionWhenIdIsNull() {

        assertThatThrownBy(() -> useCase.getCallById(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Call history ID cannot be null");
    }

    @Test
    @DisplayName("PageImpl should support iterator")
    void pageImplShouldSupportIterator() {

        Pageable pageable = PageRequest.of(0, 10);
        List<CallHistory> histories = List.of(
                createCallHistory("/api/calc1", "POST", 200),
                createCallHistory("/api/calc2", "POST", 200));
        PageResult<CallHistory> pageResult = createPageResult(histories, 0, 10, 2);

        when(repository.findAll(pageable)).thenReturn(pageResult);

        Page<CallHistory> result = useCase.getHistory(pageable);

        assertThat(result).isNotNull();
        Iterator<CallHistory> iterator = result.iterator();
        assertThat(iterator).isNotNull();
        assertThat(iterator.hasNext()).isTrue();

        List<CallHistory> iteratedItems = new ArrayList<>();
        result.forEach(iteratedItems::add);
        assertThat(iteratedItems).hasSize(2);
    }

    @Test
    @DisplayName("PageImpl should support map transformation")
    void pageImplShouldSupportMapTransformation() {

        Pageable pageable = PageRequest.of(0, 10);
        List<CallHistory> histories = List.of(createCallHistory("/api/calculate", "POST", 200));
        PageResult<CallHistory> pageResult = createPageResult(histories, 0, 10, 1);

        when(repository.findAll(pageable)).thenReturn(pageResult);

        Page<CallHistory> result = useCase.getHistory(pageable);
        Page<String> mappedResult = result.map(CallHistory::getEndpoint);

        assertThat(mappedResult).isNotNull();
        assertThat(mappedResult.getContent()).hasSize(1);
        assertThat(mappedResult.getContent().get(0)).isEqualTo("/api/calculate");
        assertThat(mappedResult.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("PageImpl should return correct pageable for next page")
    void pageImplShouldReturnCorrectPageableForNextPage() {

        Pageable pageable = PageRequest.of(0, 10);
        List<CallHistory> histories = List.of(createCallHistory("/api/calculate", "POST", 200));
        PageResult<CallHistory> pageResult = new PageResult<>() {
            @Override
            public List<CallHistory> getContent() {
                return histories;
            }

            @Override
            public int getTotalPages() {
                return 3;
            }

            @Override
            public long getTotalElements() {
                return 25;
            }

            @Override
            public int getPageNumber() {
                return 0;
            }

            @Override
            public int getPageSize() {
                return 10;
            }

            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public boolean hasPrevious() {
                return false;
            }

            @Override
            public boolean isFirst() {
                return true;
            }

            @Override
            public boolean isLast() {
                return false;
            }
        };

        when(repository.findAll(pageable)).thenReturn(pageResult);

        Page<CallHistory> result = useCase.getHistory(pageable);

        assertThat(result.hasNext()).isTrue();
        Pageable nextPageable = result.nextPageable();
        assertThat(nextPageable).isNotNull();
        assertThat(nextPageable.getPageNumber()).isEqualTo(1);
        assertThat(nextPageable.getPageSize()).isEqualTo(10);
    }

    @Test
    @DisplayName("PageImpl should return unpaged when no next page")
    void pageImplShouldReturnUnpagedWhenNoNextPage() {

        Pageable pageable = PageRequest.of(2, 10);
        List<CallHistory> histories = List.of(createCallHistory("/api/calculate", "POST", 200));
        PageResult<CallHistory> pageResult = new PageResult<>() {
            @Override
            public List<CallHistory> getContent() {
                return histories;
            }

            @Override
            public int getTotalPages() {
                return 3;
            }

            @Override
            public long getTotalElements() {
                return 25;
            }

            @Override
            public int getPageNumber() {
                return 2;
            }

            @Override
            public int getPageSize() {
                return 10;
            }

            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public boolean hasPrevious() {
                return true;
            }

            @Override
            public boolean isFirst() {
                return false;
            }

            @Override
            public boolean isLast() {
                return true;
            }
        };

        when(repository.findAll(pageable)).thenReturn(pageResult);

        Page<CallHistory> result = useCase.getHistory(pageable);

        assertThat(result.hasNext()).isFalse();
        assertThat(result.isLast()).isTrue();
        Pageable nextPageable = result.nextPageable();
        assertThat(nextPageable).isEqualTo(Pageable.unpaged());
    }

    @Test
    @DisplayName("PageImpl should return correct pageable for previous page")
    void pageImplShouldReturnCorrectPageableForPreviousPage() {

        Pageable pageable = PageRequest.of(1, 10);
        List<CallHistory> histories = List.of(createCallHistory("/api/calculate", "POST", 200));
        PageResult<CallHistory> pageResult = new PageResult<>() {
            @Override
            public List<CallHistory> getContent() {
                return histories;
            }

            @Override
            public int getTotalPages() {
                return 3;
            }

            @Override
            public long getTotalElements() {
                return 25;
            }

            @Override
            public int getPageNumber() {
                return 1;
            }

            @Override
            public int getPageSize() {
                return 10;
            }

            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public boolean hasPrevious() {
                return true;
            }

            @Override
            public boolean isFirst() {
                return false;
            }

            @Override
            public boolean isLast() {
                return false;
            }
        };

        when(repository.findAll(pageable)).thenReturn(pageResult);

        Page<CallHistory> result = useCase.getHistory(pageable);

        assertThat(result.hasPrevious()).isTrue();
        Pageable previousPageable = result.previousPageable();
        assertThat(previousPageable).isNotNull();
        assertThat(previousPageable.getPageNumber()).isEqualTo(0);
        assertThat(previousPageable.getPageSize()).isEqualTo(10);
    }

    @Test
    @DisplayName("PageImpl should return unpaged when no previous page")
    void pageImplShouldReturnUnpagedWhenNoPreviousPage() {

        Pageable pageable = PageRequest.of(0, 10);
        List<CallHistory> histories = List.of(createCallHistory("/api/calculate", "POST", 200));
        PageResult<CallHistory> pageResult = createPageResult(histories, 0, 10, 25);

        when(repository.findAll(pageable)).thenReturn(pageResult);

        Page<CallHistory> result = useCase.getHistory(pageable);

        assertThat(result.hasPrevious()).isFalse();
        assertThat(result.isFirst()).isTrue();
        Pageable previousPageable = result.previousPageable();
        assertThat(previousPageable).isEqualTo(Pageable.unpaged());
    }

    @Test
    @DisplayName("PageImpl should return correct number of elements")
    void pageImplShouldReturnCorrectNumberOfElements() {

        Pageable pageable = PageRequest.of(0, 10);
        List<CallHistory> histories = List.of(
                createCallHistory("/api/calc1", "POST", 200),
                createCallHistory("/api/calc2", "POST", 200),
                createCallHistory("/api/calc3", "POST", 200));
        PageResult<CallHistory> pageResult = createPageResult(histories, 0, 10, 3);

        when(repository.findAll(pageable)).thenReturn(pageResult);

        Page<CallHistory> result = useCase.getHistory(pageable);

        assertThat(result.getNumberOfElements()).isEqualTo(3);
        assertThat(result.getSize()).isEqualTo(10);
        assertThat(result.hasContent()).isTrue();
    }

    @Test
    @DisplayName("PageImpl should handle empty content correctly")
    void pageImplShouldHandleEmptyContentCorrectly() {

        Pageable pageable = PageRequest.of(0, 10);
        PageResult<CallHistory> emptyPageResult = createPageResult(Collections.emptyList(), 0, 10, 0);

        when(repository.findAll(pageable)).thenReturn(emptyPageResult);

        Page<CallHistory> result = useCase.getHistory(pageable);

        assertThat(result.hasContent()).isFalse();
        assertThat(result.getNumberOfElements()).isEqualTo(0);
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    @DisplayName("PageImpl should return unsorted Sort")
    void pageImplShouldReturnUnsortedSort() {

        Pageable pageable = PageRequest.of(0, 10);
        List<CallHistory> histories = List.of(createCallHistory("/api/calculate", "POST", 200));
        PageResult<CallHistory> pageResult = createPageResult(histories, 0, 10, 1);

        when(repository.findAll(pageable)).thenReturn(pageResult);

        Page<CallHistory> result = useCase.getHistory(pageable);

        assertThat(result.getSort()).isEqualTo(org.springframework.data.domain.Sort.unsorted());
    }

    private CallHistory createCallHistory(String endpoint, String method, int statusCode) {
        return CallHistory.builder()
                .endpoint(endpoint)
                .httpMethod(method)
                .httpStatusCode(statusCode)
                .timestamp(LocalDateTime.now())
                .build();
    }

    private PageResult<CallHistory> createPageResult(List<CallHistory> content, int pageNumber, int pageSize,
            long totalElements) {
        return new PageResult<>() {
            @Override
            public List<CallHistory> getContent() {
                return content;
            }

            @Override
            public int getTotalPages() {
                return (int) Math.ceil((double) totalElements / pageSize);
            }

            @Override
            public long getTotalElements() {
                return totalElements;
            }

            @Override
            public int getPageNumber() {
                return pageNumber;
            }

            @Override
            public int getPageSize() {
                return pageSize;
            }

            @Override
            public boolean hasNext() {
                return pageNumber < getTotalPages() - 1;
            }

            @Override
            public boolean hasPrevious() {
                return pageNumber > 0;
            }

            @Override
            public boolean isFirst() {
                return pageNumber == 0;
            }

            @Override
            public boolean isLast() {
                return pageNumber == getTotalPages() - 1;
            }
        };
    }
}
