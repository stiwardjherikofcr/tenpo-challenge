package cl.tenpo.sjcr.percentage_calculator_service.domain.service;

import cl.tenpo.sjcr.percentage_calculator_service.domain.valueobject.CalculationRequest;
import cl.tenpo.sjcr.percentage_calculator_service.domain.valueobject.CalculationResult;
import cl.tenpo.sjcr.percentage_calculator_service.domain.valueobject.Percentage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Calculation Domain Service Tests")
class CalculationDomainServiceTest {

    private CalculationDomainService service;

    @BeforeEach
    void setUp() {
        service = new CalculationDomainService();
    }

    @Test
    @DisplayName("Should calculate sum with percentage correctly")
    void shouldCalculateWithPercentage() {

        CalculationRequest request = CalculationRequest.builder()
                .num1(new BigDecimal("10"))
                .num2(new BigDecimal("20"))
                .build();
        Percentage percentage = Percentage.of(new BigDecimal("15"));

        CalculationResult result = service.calculate(request, percentage, false);

        assertThat(result).isNotNull();
        assertThat(result.getSum()).isEqualByComparingTo("30");

        assertThat(result.getResult()).isEqualByComparingTo("34.50");
        assertThat(result.getAppliedPercentage()).isEqualTo(percentage);
        assertThat(result.getCalculatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should handle zero percentage")
    void shouldHandleZeroPercentage() {

        CalculationRequest request = CalculationRequest.builder()
                .num1(new BigDecimal("15.5"))
                .num2(new BigDecimal("24.5"))
                .build();
        Percentage percentage = Percentage.of(BigDecimal.ZERO);

        CalculationResult result = service.calculate(request, percentage, false);

        assertThat(result.getSum()).isEqualByComparingTo("40");
        assertThat(result.getResult()).isEqualByComparingTo("40.00");
    }

    @Test
    @DisplayName("Should handle maximum percentage (100%)")
    void shouldHandleMaximumPercentage() {

        CalculationRequest request = CalculationRequest.builder()
                .num1(new BigDecimal("50"))
                .num2(new BigDecimal("50"))
                .build();
        Percentage percentage = Percentage.of(new BigDecimal("100"));

        CalculationResult result = service.calculate(request, percentage, false);

        assertThat(result.getSum()).isEqualByComparingTo("100");

        assertThat(result.getResult()).isEqualByComparingTo("200.00");
    }

    @Test
    @DisplayName("Should handle decimal numbers correctly")
    void shouldHandleDecimalNumbers() {

        CalculationRequest request = CalculationRequest.builder()
                .num1(new BigDecimal("10.567"))
                .num2(new BigDecimal("20.433"))
                .build();
        Percentage percentage = Percentage.of(new BigDecimal("12.5"));

        CalculationResult result = service.calculate(request, percentage, false);

        assertThat(result.getSum()).isEqualByComparingTo("31");

        assertThat(result.getResult()).isEqualByComparingTo("34.88");
    }

    @Test
    @DisplayName("Should round result to 2 decimal places")
    void shouldRoundToTwoDecimalPlaces() {

        CalculationRequest request = CalculationRequest.builder()
                .num1(new BigDecimal("10"))
                .num2(new BigDecimal("10"))
                .build();
        Percentage percentage = Percentage.of(new BigDecimal("33.33"));

        CalculationResult result = service.calculate(request, percentage, false);

        assertThat(result.getResult().scale()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should validate result successfully when values are valid")
    void shouldValidateResultSuccessfullyWhenValuesAreValid() {

        CalculationRequest request = CalculationRequest.builder()
                .num1(new BigDecimal("10"))
                .num2(new BigDecimal("20"))
                .build();
        Percentage percentage = Percentage.of(new BigDecimal("15"));

        CalculationResult result = service.calculate(request, percentage, true);

        assertThat(result).isNotNull();
        assertThat(result.getResult()).isPositive();
    }

    @Test
    @DisplayName("Should not validate when validation flag is false")
    void shouldNotValidateWhenValidationFlagIsFalse() {

        CalculationRequest request = CalculationRequest.builder()
                .num1(new BigDecimal("10"))
                .num2(new BigDecimal("20"))
                .build();
        Percentage percentage = Percentage.of(new BigDecimal("15"));

        CalculationResult result = service.calculate(request, percentage, false);

        assertThat(result).isNotNull();
    }
}
