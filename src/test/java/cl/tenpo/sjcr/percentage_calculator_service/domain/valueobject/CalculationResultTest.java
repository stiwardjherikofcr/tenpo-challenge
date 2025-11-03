package cl.tenpo.sjcr.percentage_calculator_service.domain.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("CalculationResult Value Object Tests")
class CalculationResultTest {

    @Test
    @DisplayName("Should create CalculationResult with all fields")
    void shouldCreateCalculationResultWithAllFields() {
        BigDecimal sum = new BigDecimal("30.00");
        Percentage percentage = Percentage.of(15.0);
        BigDecimal percentageAmount = new BigDecimal("4.50");
        BigDecimal result = new BigDecimal("34.50");
        LocalDateTime timestamp = LocalDateTime.of(2024, 1, 15, 10, 30);

        CalculationResult calculationResult = CalculationResult.builder()
                .sum(sum)
                .appliedPercentage(percentage)
                .percentageAmount(percentageAmount)
                .result(result)
                .calculatedAt(timestamp)
                .usedCachedPercentage(false)
                .build();

        assertThat(calculationResult.getSum()).isEqualByComparingTo(sum);
        assertThat(calculationResult.getAppliedPercentage()).isEqualTo(percentage);
        assertThat(calculationResult.getPercentageAmount()).isEqualByComparingTo(percentageAmount);
        assertThat(calculationResult.getResult()).isEqualByComparingTo(result);
        assertThat(calculationResult.getCalculatedAt()).isEqualTo(timestamp);
        assertThat(calculationResult.isUsedCachedPercentage()).isFalse();
    }

    @Test
    @DisplayName("Should set calculatedAt to now when not provided")
    void shouldSetCalculatedAtToNowWhenNotProvided() {
        LocalDateTime before = LocalDateTime.now();

        CalculationResult calculationResult = CalculationResult.builder()
                .sum(new BigDecimal("30.00"))
                .appliedPercentage(Percentage.of(15.0))
                .percentageAmount(new BigDecimal("4.50"))
                .result(new BigDecimal("34.50"))
                .usedCachedPercentage(false)
                .build();

        LocalDateTime after = LocalDateTime.now();

        assertThat(calculationResult.getCalculatedAt()).isBetween(before, after);
    }

    @Test
    @DisplayName("Should throw exception when sum is null")
    void shouldThrowExceptionWhenSumIsNull() {
        assertThatThrownBy(() -> CalculationResult.builder()
                .appliedPercentage(Percentage.of(15.0))
                .percentageAmount(new BigDecimal("4.50"))
                .result(new BigDecimal("34.50"))
                .build())
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Sum cannot be null");
    }

    @Test
    @DisplayName("Should throw exception when appliedPercentage is null")
    void shouldThrowExceptionWhenAppliedPercentageIsNull() {
        assertThatThrownBy(() -> CalculationResult.builder()
                .sum(new BigDecimal("30.00"))
                .percentageAmount(new BigDecimal("4.50"))
                .result(new BigDecimal("34.50"))
                .build())
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Applied percentage cannot be null");
    }

    @Test
    @DisplayName("Should throw exception when percentageAmount is null")
    void shouldThrowExceptionWhenPercentageAmountIsNull() {
        assertThatThrownBy(() -> CalculationResult.builder()
                .sum(new BigDecimal("30.00"))
                .appliedPercentage(Percentage.of(15.0))
                .result(new BigDecimal("34.50"))
                .build())
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Percentage amount cannot be null");
    }

    @Test
    @DisplayName("Should throw exception when result is null")
    void shouldThrowExceptionWhenResultIsNull() {
        assertThatThrownBy(() -> CalculationResult.builder()
                .sum(new BigDecimal("30.00"))
                .appliedPercentage(Percentage.of(15.0))
                .percentageAmount(new BigDecimal("4.50"))
                .build())
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Final result cannot be null");
    }

    @Test
    @DisplayName("Should handle cached percentage flag")
    void shouldHandleCachedPercentageFlag() {
        CalculationResult cachedResult = CalculationResult.builder()
                .sum(new BigDecimal("30.00"))
                .appliedPercentage(Percentage.of(15.0))
                .percentageAmount(new BigDecimal("4.50"))
                .result(new BigDecimal("34.50"))
                .usedCachedPercentage(true)
                .build();

        assertThat(cachedResult.isUsedCachedPercentage()).isTrue();

        CalculationResult freshResult = CalculationResult.builder()
                .sum(new BigDecimal("30.00"))
                .appliedPercentage(Percentage.of(15.0))
                .percentageAmount(new BigDecimal("4.50"))
                .result(new BigDecimal("34.50"))
                .usedCachedPercentage(false)
                .build();

        assertThat(freshResult.isUsedCachedPercentage()).isFalse();
    }

    @Test
    @DisplayName("Should be equal when all fields match except timestamp")
    void shouldBeEqualWhenAllFieldsMatchExceptTimestamp() {
        CalculationResult result1 = CalculationResult.builder()
                .sum(new BigDecimal("30.00"))
                .appliedPercentage(Percentage.of(15.0))
                .percentageAmount(new BigDecimal("4.50"))
                .result(new BigDecimal("34.50"))
                .usedCachedPercentage(false)
                .calculatedAt(LocalDateTime.of(2024, 1, 15, 10, 30))
                .build();

        CalculationResult result2 = CalculationResult.builder()
                .sum(new BigDecimal("30.00"))
                .appliedPercentage(Percentage.of(15.0))
                .percentageAmount(new BigDecimal("4.50"))
                .result(new BigDecimal("34.50"))
                .usedCachedPercentage(false)
                .calculatedAt(LocalDateTime.of(2024, 12, 31, 23, 59))
                .build();

        assertThat(result1).isEqualTo(result2);
        assertThat(result1.hashCode()).isEqualTo(result2.hashCode());
    }

    @Test
    @DisplayName("Should not be equal when sum differs")
    void shouldNotBeEqualWhenSumDiffers() {
        CalculationResult result1 = createDefaultResult(new BigDecimal("30.00"));
        CalculationResult result2 = createDefaultResult(new BigDecimal("40.00"));

        assertThat(result1).isNotEqualTo(result2);
    }

    @Test
    @DisplayName("Should not be equal when percentage differs")
    void shouldNotBeEqualWhenPercentageDiffers() {
        CalculationResult result1 = CalculationResult.builder()
                .sum(new BigDecimal("30.00"))
                .appliedPercentage(Percentage.of(15.0))
                .percentageAmount(new BigDecimal("4.50"))
                .result(new BigDecimal("34.50"))
                .usedCachedPercentage(false)
                .build();

        CalculationResult result2 = CalculationResult.builder()
                .sum(new BigDecimal("30.00"))
                .appliedPercentage(Percentage.of(20.0))
                .percentageAmount(new BigDecimal("6.00"))
                .result(new BigDecimal("36.00"))
                .usedCachedPercentage(false)
                .build();

        assertThat(result1).isNotEqualTo(result2);
    }

    @Test
    @DisplayName("Should not be equal when cached flag differs")
    void shouldNotBeEqualWhenCachedFlagDiffers() {
        CalculationResult result1 = CalculationResult.builder()
                .sum(new BigDecimal("30.00"))
                .appliedPercentage(Percentage.of(15.0))
                .percentageAmount(new BigDecimal("4.50"))
                .result(new BigDecimal("34.50"))
                .usedCachedPercentage(true)
                .build();

        CalculationResult result2 = CalculationResult.builder()
                .sum(new BigDecimal("30.00"))
                .appliedPercentage(Percentage.of(15.0))
                .percentageAmount(new BigDecimal("4.50"))
                .result(new BigDecimal("34.50"))
                .usedCachedPercentage(false)
                .build();

        assertThat(result1).isNotEqualTo(result2);
    }

    @Test
    @DisplayName("toString should contain all relevant information")
    void toStringShouldContainAllRelevantInformation() {
        CalculationResult calculationResult = CalculationResult.builder()
                .sum(new BigDecimal("30.00"))
                .appliedPercentage(Percentage.of(15.0))
                .percentageAmount(new BigDecimal("4.50"))
                .result(new BigDecimal("34.50"))
                .usedCachedPercentage(false)
                .build();

        String toString = calculationResult.toString();

        assertThat(toString).contains("CalculationResult");
        assertThat(toString).contains("30");
        assertThat(toString).contains("15");
        assertThat(toString).contains("4.50");
        assertThat(toString).contains("34.50");
        assertThat(toString).contains("false");
    }

    @Test
    @DisplayName("Should be equal to itself")
    void shouldBeEqualToItself() {
        CalculationResult result = createDefaultResult(new BigDecimal("30.00"));

        assertThat(result).isEqualTo(result);
    }

    @Test
    @DisplayName("Should not be equal to null")
    void shouldNotBeEqualToNull() {
        CalculationResult result = createDefaultResult(new BigDecimal("30.00"));

        assertThat(result).isNotEqualTo(null);
    }

    @Test
    @DisplayName("Should not be equal to different class")
    void shouldNotBeEqualToDifferentClass() {
        CalculationResult result = createDefaultResult(new BigDecimal("30.00"));

        assertThat(result).isNotEqualTo("Not a CalculationResult");
    }

    @Test
    @DisplayName("Should handle equality with same values but different BigDecimal scale")
    void shouldHandleEqualityWithSameValuesButDifferentScale() {
        CalculationResult result1 = CalculationResult.builder()
                .sum(new BigDecimal("30.00"))
                .appliedPercentage(Percentage.of(15.0))
                .percentageAmount(new BigDecimal("4.50"))
                .result(new BigDecimal("34.50"))
                .usedCachedPercentage(false)
                .build();

        CalculationResult result2 = CalculationResult.builder()
                .sum(new BigDecimal("30.0"))
                .appliedPercentage(Percentage.of(15.0))
                .percentageAmount(new BigDecimal("4.5"))
                .result(new BigDecimal("34.5"))
                .usedCachedPercentage(false)
                .build();

        assertThat(result1).isEqualTo(result2);
    }

    @Test
    @DisplayName("Should not be equal when percentageAmount differs")
    void shouldNotBeEqualWhenPercentageAmountDiffers() {
        CalculationResult result1 = CalculationResult.builder()
                .sum(new BigDecimal("30.00"))
                .appliedPercentage(Percentage.of(15.0))
                .percentageAmount(new BigDecimal("4.50"))
                .result(new BigDecimal("34.50"))
                .usedCachedPercentage(false)
                .build();

        CalculationResult result2 = CalculationResult.builder()
                .sum(new BigDecimal("30.00"))
                .appliedPercentage(Percentage.of(15.0))
                .percentageAmount(new BigDecimal("5.00")) // Different percentageAmount
                .result(new BigDecimal("34.50"))
                .usedCachedPercentage(false)
                .build();

        assertThat(result1).isNotEqualTo(result2);
    }

    @Test
    @DisplayName("Should not be equal when result differs")
    void shouldNotBeEqualWhenResultDiffers() {
        CalculationResult result1 = CalculationResult.builder()
                .sum(new BigDecimal("30.00"))
                .appliedPercentage(Percentage.of(15.0))
                .percentageAmount(new BigDecimal("4.50"))
                .result(new BigDecimal("34.50"))
                .usedCachedPercentage(false)
                .build();

        CalculationResult result2 = CalculationResult.builder()
                .sum(new BigDecimal("30.00"))
                .appliedPercentage(Percentage.of(15.0))
                .percentageAmount(new BigDecimal("4.50"))
                .result(new BigDecimal("35.00")) // Different result
                .usedCachedPercentage(false)
                .build();

        assertThat(result1).isNotEqualTo(result2);
    }

    private CalculationResult createDefaultResult(BigDecimal sum) {
        return CalculationResult.builder()
                .sum(sum)
                .appliedPercentage(Percentage.of(15.0))
                .percentageAmount(new BigDecimal("4.50"))
                .result(new BigDecimal("34.50"))
                .usedCachedPercentage(false)
                .build();
    }
}
