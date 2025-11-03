package cl.tenpo.sjcr.percentage_calculator_service.domain.valueobject;

import cl.tenpo.sjcr.percentage_calculator_service.domain.exception.InvalidInputException;

import java.math.BigDecimal;
import java.util.Objects;

public final class CalculationRequest {

    private final BigDecimal num1;
    private final BigDecimal num2;

    private CalculationRequest(BigDecimal num1, BigDecimal num2) {
        validate(num1, num2);
        this.num1 = num1;
        this.num2 = num2;
    }

    public static CalculationRequest of(BigDecimal num1, BigDecimal num2) {
        validate(num1, num2);
        return new CalculationRequest(num1, num2);
    }

    public static CalculationRequest of(double num1, double num2) {
        return of(BigDecimal.valueOf(num1), BigDecimal.valueOf(num2));
    }

    private static void validate(BigDecimal num1, BigDecimal num2) {
        if (num1 == null) {
            throw new InvalidInputException("num1 cannot be null");
        }

        if (num2 == null) {
            throw new InvalidInputException("num2 cannot be null");
        }

        try {
            num1.add(num2);
        } catch (ArithmeticException e) {
            throw new InvalidInputException("Invalid numbers for addition");
        }
    }

    public BigDecimal calculateSum() {
        return num1.add(num2);
    }

    public BigDecimal getNum1() {
        return num1;
    }

    public BigDecimal getNum2() {
        return num2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CalculationRequest that = (CalculationRequest) o;
        return num1.compareTo(that.num1) == 0 &&
                num2.compareTo(that.num2) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(num1, num2);
    }

    @Override
    public String toString() {
        return String.format("CalculationRequest{num1=%s, num2=%s}", num1.toPlainString(), num2.toPlainString());
    }

    public static class Builder {
        private BigDecimal num1;
        private BigDecimal num2;

        public Builder num1(BigDecimal num1) {
            this.num1 = num1;
            return this;
        }

        public Builder num1(Double num1) {
            this.num1 = BigDecimal.valueOf(num1);
            return this;
        }

        public Builder num2(BigDecimal num2) {
            this.num2 = num2;
            return this;
        }

        public Builder num2(Double num2) {
            this.num2 = BigDecimal.valueOf(num2);
            return this;
        }

        public CalculationRequest build() {
            return new CalculationRequest(num1, num2);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
