package cl.tenpo.sjcr.percentage_calculator_service.domain.valueobject;

import cl.tenpo.sjcr.percentage_calculator_service.domain.exception.InvalidInputException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public final class Percentage {

    private static final BigDecimal MIN_VALUE = BigDecimal.ZERO;
    private static final BigDecimal MAX_VALUE = new BigDecimal("100");

    private final BigDecimal value;

    private Percentage(BigDecimal value) {
        validate(value);
        this.value = value;
    }

    public static Percentage of(BigDecimal value) {
        return new Percentage(value);
    }

    public static Percentage of(double value) {
        return new Percentage(BigDecimal.valueOf(value));
    }

    private void validate(BigDecimal value) {
        if (value == null) {
            throw new InvalidInputException("Percentage value cannot be null");
        }
        if (value.compareTo(MIN_VALUE) < 0) {
            throw new InvalidInputException("Percentage cannot be negative");
        }
        if (value.compareTo(MAX_VALUE) > 0) {
            throw new InvalidInputException("Percentage cannot exceed 100%");
        }
    }

    public BigDecimal applyTo(BigDecimal amount) {
        return amount.multiply(asDecimal());
    }

    public BigDecimal getValue() {
        return value;
    }

    public BigDecimal asDecimal() {
        return value.divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Percentage that = (Percentage) o;
        return value.compareTo(that.value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value.toString() + "%";
    }
}
