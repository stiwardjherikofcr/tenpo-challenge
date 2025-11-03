package cl.tenpo.sjcr.percentage_calculator_service.infrastructure.adapter.out.persistence;

import cl.tenpo.sjcr.percentage_calculator_service.domain.model.CallHistory;
import cl.tenpo.sjcr.percentage_calculator_service.domain.port.out.CallHistoryRepositoryPort.PageResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@Transactional
@DisplayName("CallHistory JPA Adapter Integration Tests")
class CallHistoryJpaAdapterIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("test_db")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private CallHistoryJpaAdapter adapter;

    @Test
    @DisplayName("Should save call history successfully")
    void shouldSaveCallHistory() {

        CallHistory history = CallHistory.builder()
                .endpoint("/api/v1/calculate")
                .httpMethod("POST")
                .requestParameters("{\"num1\":10,\"num2\":20}")
                .response("{\"result\":34.5}")
                .httpStatusCode(200)
                .build();

        CallHistory saved = adapter.save(history);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getEndpoint()).isEqualTo("/api/v1/calculate");
        assertThat(saved.getHttpMethod()).isEqualTo("POST");
        assertThat(saved.isSuccessful()).isTrue();
        assertThat(saved.getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("Should retrieve paginated call history")
    void shouldRetrievePaginatedHistory() {

        for (int i = 0; i < 15; i++) {
            CallHistory history = CallHistory.builder()
                    .endpoint("/api/v1/calculate")
                    .httpMethod("POST")
                    .requestParameters("{\"num1\":" + i + ",\"num2\":" + i + "}")
                    .response("{\"result\":" + (i * 2) + "}")
                    .httpStatusCode(i % 2 == 0 ? 200 : 500) // Alternate success/failure
                    .errorMessage(i % 2 != 0 ? "Error " + i : null)
                    .build();
            adapter.save(history);
        }

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "timestamp"));
        PageResult<CallHistory> page = adapter.findAll(pageRequest);

        assertThat(page).isNotNull();
        assertThat(page.getTotalElements()).isGreaterThanOrEqualTo(15);
        assertThat(page.getContent()).hasSize(10);
        assertThat(page.getTotalPages()).isGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("Should save failed call history with error message")
    void shouldSaveFailedCallHistory() {

        CallHistory history = CallHistory.builder()
                .endpoint("/api/v1/calculate")
                .httpMethod("POST")
                .requestParameters("{\"num1\":10,\"num2\":20}")
                .errorMessage("Service unavailable")
                .httpStatusCode(503)
                .build();

        CallHistory saved = adapter.save(history);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.isSuccessful()).isFalse();
        assertThat(saved.getErrorMessage()).isEqualTo("Service unavailable");
        assertThat(saved.getResponse()).isNull();
    }
}
