package cl.tenpo.sjcr.percentage_calculator_service.domain.valueobject;

import cl.tenpo.sjcr.percentage_calculator_service.domain.exception.InvalidInputException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Percentage Value Object Tests")
class PercentageTest {

    @Test
    @DisplayName("Should create valid percentage")
    void shouldCreateValidPercentage() {

        Percentage percentage = Percentage.of(new BigDecimal("15.5"));

        assertThat(percentage).isNotNull();
        assertThat(percentage.getValue()).isEqualByComparingTo("15.5");
    }

    @Test
    @DisplayName("Should throw exception for negative percentage")
    void shouldThrowForNegativePercentage() {

        assertThatThrownBy(() -> Percentage.of(new BigDecimal("-5")))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("Percentage cannot be negative");
    }

    @Test
    @DisplayName("Should throw exception for percentage > 100")
    void shouldThrowForPercentageGreaterThan100() {

        assertThatThrownBy(() -> Percentage.of(new BigDecimal("101")))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("Percentage cannot exceed 100%");
    }

    @Test
    @DisplayName("Should throw exception for null percentage")
    void shouldThrowForNullPercentage() {

        assertThatThrownBy(() -> Percentage.of(null))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("Percentage value cannot be null");
    }

    @Test
    @DisplayName("Should accept zero percentage")
    void shouldAcceptZeroPercentage() {

        Percentage percentage = Percentage.of(BigDecimal.ZERO);

        assertThat(percentage.getValue()).isEqualByComparingTo("0");
    }

    @Test
    @DisplayName("Should accept 100% percentage")
    void shouldAccept100Percentage() {

        Percentage percentage = Percentage.of(new BigDecimal("100"));

        assertThat(percentage.getValue()).isEqualByComparingTo("100");
    }

    @Test
    @DisplayName("Should convert to decimal correctly")
    void shouldConvertToDecimal() {

        Percentage percentage = Percentage.of(new BigDecimal("25"));

        BigDecimal decimal = percentage.asDecimal();

        assertThat(decimal).isEqualByComparingTo("0.25");
    }

    @Test
    @DisplayName("Should implement equals correctly")
    void shouldImplementEquals() {

        Percentage p1 = Percentage.of(new BigDecimal("15.0"));
        Percentage p2 = Percentage.of(new BigDecimal("15.0"));
        Percentage p3 = Percentage.of(new BigDecimal("20.0"));

        assertThat(p1).isEqualTo(p2);
        assertThat(p1).isNotEqualTo(p3);
    }

    @Test
    @DisplayName("Should not be equal to null")
    void shouldNotBeEqualToNull() {
        Percentage percentage = Percentage.of(new BigDecimal("15.0"));

        assertThat(percentage).isNotEqualTo(null);
    }

    @Test
    @DisplayName("Should not be equal to different class")
    void shouldNotBeEqualToDifferentClass() {
        Percentage percentage = Percentage.of(new BigDecimal("15.0"));

        assertThat(percentage).isNotEqualTo("15.0");
    }
}
