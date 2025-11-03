package cl.tenpo.sjcr.percentage_calculator_service.domain.valueobject;

import cl.tenpo.sjcr.percentage_calculator_service.domain.exception.InvalidInputException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("CalculationRequest Value Object Tests")
class CalculationRequestTest {

    @Test
    @DisplayName("Should create CalculationRequest with BigDecimal values")
    void shouldCreateCalculationRequestWithBigDecimalValues() {
        BigDecimal num1 = new BigDecimal("10.5");
        BigDecimal num2 = new BigDecimal("20.3");

        CalculationRequest request = CalculationRequest.of(num1, num2);

        assertThat(request.getNum1()).isEqualByComparingTo(num1);
        assertThat(request.getNum2()).isEqualByComparingTo(num2);
    }

    @Test
    @DisplayName("Should create CalculationRequest with double values")
    void shouldCreateCalculationRequestWithDoubleValues() {
        CalculationRequest request = CalculationRequest.of(15.75, 24.25);

        assertThat(request.getNum1()).isEqualByComparingTo("15.75");
        assertThat(request.getNum2()).isEqualByComparingTo("24.25");
    }

    @Test
    @DisplayName("Should create CalculationRequest using builder with BigDecimal")
    void shouldCreateCalculationRequestUsingBuilderWithBigDecimal() {
        BigDecimal num1 = new BigDecimal("100.50");
        BigDecimal num2 = new BigDecimal("200.75");

        CalculationRequest request = CalculationRequest.builder()
                .num1(num1)
                .num2(num2)
                .build();

        assertThat(request.getNum1()).isEqualByComparingTo(num1);
        assertThat(request.getNum2()).isEqualByComparingTo(num2);
    }

    @Test
    @DisplayName("Should create CalculationRequest using builder with Double")
    void shouldCreateCalculationRequestUsingBuilderWithDouble() {
        CalculationRequest request = CalculationRequest.builder()
                .num1(50.25)
                .num2(75.50)
                .build();

        assertThat(request.getNum1()).isEqualByComparingTo("50.25");
        assertThat(request.getNum2()).isEqualByComparingTo("75.50");
    }

    @Test
    @DisplayName("Should calculate sum correctly")
    void shouldCalculateSumCorrectly() {
        CalculationRequest request = CalculationRequest.of(10.5, 20.3);

        BigDecimal sum = request.calculateSum();

        assertThat(sum).isEqualByComparingTo("30.8");
    }

    @Test
    @DisplayName("Should throw exception when num1 is null")
    void shouldThrowExceptionWhenNum1IsNull() {
        assertThatThrownBy(() -> CalculationRequest.of(null, BigDecimal.TEN))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("num1 cannot be null");
    }

    @Test
    @DisplayName("Should throw exception when num2 is null")
    void shouldThrowExceptionWhenNum2IsNull() {
        assertThatThrownBy(() -> CalculationRequest.of(BigDecimal.TEN, null))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("num2 cannot be null");
    }

    @Test
    @DisplayName("Should throw exception when builder num1 is null")
    void shouldThrowExceptionWhenBuilderNum1IsNull() {
        assertThatThrownBy(() -> CalculationRequest.builder()
                .num2(BigDecimal.TEN)
                .build())
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("num1 cannot be null");
    }

    @Test
    @DisplayName("Should throw exception when builder num2 is null")
    void shouldThrowExceptionWhenBuilderNum2IsNull() {
        assertThatThrownBy(() -> CalculationRequest.builder()
                .num1(BigDecimal.TEN)
                .build())
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("num2 cannot be null");
    }

    @Test
    @DisplayName("Should handle negative numbers")
    void shouldHandleNegativeNumbers() {
        CalculationRequest request = CalculationRequest.of(-10.5, 20.3);

        assertThat(request.getNum1()).isEqualByComparingTo("-10.5");
        assertThat(request.getNum2()).isEqualByComparingTo("20.3");
        assertThat(request.calculateSum()).isEqualByComparingTo("9.8");
    }

    @Test
    @DisplayName("Should handle zero values")
    void shouldHandleZeroValues() {
        CalculationRequest request = CalculationRequest.of(0.0, 0.0);

        assertThat(request.getNum1()).isEqualByComparingTo("0.0");
        assertThat(request.getNum2()).isEqualByComparingTo("0.0");
        assertThat(request.calculateSum()).isEqualByComparingTo("0.0");
    }

    @Test
    @DisplayName("Should handle very large numbers")
    void shouldHandleVeryLargeNumbers() {
        BigDecimal largeNum1 = new BigDecimal("999999999999999.99");
        BigDecimal largeNum2 = new BigDecimal("888888888888888.88");

        CalculationRequest request = CalculationRequest.of(largeNum1, largeNum2);

        assertThat(request.calculateSum()).isEqualByComparingTo("1888888888888888.87");
    }

    @Test
    @DisplayName("Should be equal when numbers are the same")
    void shouldBeEqualWhenNumbersAreTheSame() {
        CalculationRequest request1 = CalculationRequest.of(10.5, 20.3);
        CalculationRequest request2 = CalculationRequest.of(10.5, 20.3);

        assertThat(request1).isEqualTo(request2);
        assertThat(request1.hashCode()).isEqualTo(request2.hashCode());
    }

    @Test
    @DisplayName("Should not be equal when numbers are different")
    void shouldNotBeEqualWhenNumbersAreDifferent() {
        CalculationRequest request1 = CalculationRequest.of(10.5, 20.3);
        CalculationRequest request2 = CalculationRequest.of(15.5, 25.3);

        assertThat(request1).isNotEqualTo(request2);
    }

    @Test
    @DisplayName("Should not be equal when only num2 differs")
    void shouldNotBeEqualWhenOnlyNum2Differs() {
        CalculationRequest request1 = CalculationRequest.of(10.5, 20.3);
        CalculationRequest request2 = CalculationRequest.of(10.5, 25.3);

        assertThat(request1).isNotEqualTo(request2);
    }

    @Test
    @DisplayName("Should handle equality with same values but different BigDecimal scale")
    void shouldHandleEqualityWithSameValuesButDifferentScale() {
        CalculationRequest request1 = CalculationRequest.of(new BigDecimal("10.50"), new BigDecimal("20.30"));
        CalculationRequest request2 = CalculationRequest.of(new BigDecimal("10.5"), new BigDecimal("20.3"));

        assertThat(request1).isEqualTo(request2);
    }

    @Test
    @DisplayName("toString should contain values")
    void toStringShouldContainValues() {
        CalculationRequest request = CalculationRequest.of(10.5, 20.3);

        String toString = request.toString();

        assertThat(toString).contains("CalculationRequest");
        assertThat(toString).contains("10.5");
        assertThat(toString).contains("20.3");
    }

    @Test
    @DisplayName("Should be equal to itself")
    void shouldBeEqualToItself() {
        CalculationRequest request = CalculationRequest.of(10.5, 20.3);

        assertThat(request).isEqualTo(request);
    }

    @Test
    @DisplayName("Should not be equal to null")
    void shouldNotBeEqualToNull() {
        CalculationRequest request = CalculationRequest.of(10.5, 20.3);

        assertThat(request).isNotEqualTo(null);
    }

    @Test
    @DisplayName("Should not be equal to different class")
    void shouldNotBeEqualToDifferentClass() {
        CalculationRequest request = CalculationRequest.of(10.5, 20.3);

        assertThat(request).isNotEqualTo("Not a CalculationRequest");
    }
}
