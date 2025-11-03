package cl.tenpo.sjcr.percentage_calculator_service.infrastructure.adapter.in.rest.controller;

import cl.tenpo.sjcr.percentage_calculator_service.infrastructure.adapter.in.rest.dto.request.CalculationRequestDto;
import cl.tenpo.sjcr.percentage_calculator_service.infrastructure.adapter.in.rest.dto.response.CalculationResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@DisplayName("Calculation Controller Integration Tests")
class CalculationControllerIntegrationTest {

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
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should calculate sum with percentage successfully")
    void shouldCalculateSuccessfully() throws Exception {

        CalculationRequestDto request = CalculationRequestDto.builder()
                .num1(new BigDecimal("10"))
                .num2(new BigDecimal("20"))
                .build();

        MvcResult result = mockMvc.perform(post("/api/v1/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.result").exists())
                .andExpect(jsonPath("$.originalSum").value(30))
                .andExpect(jsonPath("$.appliedPercentage").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andReturn();

        CalculationResponseDto response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CalculationResponseDto.class
        );

        assertThat(response.getResult()).isNotNull();
        assertThat(response.getOriginalSum()).isEqualByComparingTo("30");
        assertThat(response.getAppliedPercentage()).isNotNull();
    }

    @Test
    @DisplayName("Should return 400 for invalid input (null num1)")
    void shouldReturn400ForNullNum1() throws Exception {

        CalculationRequestDto request = CalculationRequestDto.builder()
                .num1(null)
                .num2(new BigDecimal("20"))
                .build();

        mockMvc.perform(post("/api/v1/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("Should return 400 for invalid input (null num2)")
    void shouldReturn400ForNullNum2() throws Exception {

        CalculationRequestDto request = CalculationRequestDto.builder()
                .num1(new BigDecimal("10"))
                .num2(null)
                .build();

        mockMvc.perform(post("/api/v1/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle negative numbers")
    void shouldHandleNegativeNumbers() throws Exception {

        CalculationRequestDto request = CalculationRequestDto.builder()
                .num1(new BigDecimal("-10"))
                .num2(new BigDecimal("20"))
                .build();

        mockMvc.perform(post("/api/v1/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.originalSum").value(10));
    }
}
