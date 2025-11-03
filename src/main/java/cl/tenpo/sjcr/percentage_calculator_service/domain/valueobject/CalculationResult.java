package cl.tenpo.sjcr.percentage_calculator_service.domain.valueobject;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public final class CalculationResult {

    private final BigDecimal sum;
    private final Percentage appliedPercentage;
    private final BigDecimal percentageAmount;
    private final BigDecimal result;
    private final boolean usedCachedPercentage;
    private final LocalDateTime calculatedAt;

    private CalculationResult(Builder builder) {
        this.sum = builder.sum;
        this.appliedPercentage = builder.appliedPercentage;
        this.percentageAmount = builder.percentageAmount;
        this.result = builder.result;
        this.calculatedAt = builder.calculatedAt != null ? builder.calculatedAt : LocalDateTime.now();
        this.usedCachedPercentage = builder.usedCachedPercentage;
    }

    public BigDecimal getSum() {
        return sum;
    }

    public Percentage getAppliedPercentage() {
        return appliedPercentage;
    }

    public BigDecimal getPercentageAmount() {
        return percentageAmount;
    }

    public BigDecimal getResult() {
        return result;
    }

    public LocalDateTime getCalculatedAt() {
        return calculatedAt;
    }

    public boolean isUsedCachedPercentage() {
        return usedCachedPercentage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        CalculationResult that = (CalculationResult) o;
        return usedCachedPercentage == that.usedCachedPercentage &&
                sum.compareTo(that.sum) == 0 &&
                Objects.equals(appliedPercentage, that.appliedPercentage) &&
                percentageAmount.compareTo(that.percentageAmount) == 0 &&
                result.compareTo(that.result) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                sum,
                appliedPercentage,
                percentageAmount,
                result,
                usedCachedPercentage);
    }

    @Override
    public String toString() {
        return "CalculationResult{" +
                "sum=" + sum +
                ", appliedPercentage=" + appliedPercentage +
                ", percentageAmount=" + percentageAmount +
                ", result=" + result +
                ", usedCachedPercentage=" + usedCachedPercentage +
                ", calculatedAt=" + calculatedAt +
                '}';
    }

    public static class Builder {
        private BigDecimal sum;
        private Percentage appliedPercentage;
        private BigDecimal percentageAmount;
        private BigDecimal result;
        private LocalDateTime calculatedAt;
        private boolean usedCachedPercentage;

        public Builder sum(BigDecimal sum) {
            this.sum = sum;
            return this;
        }

        public Builder appliedPercentage(Percentage appliedPercentage) {
            this.appliedPercentage = appliedPercentage;
            return this;
        }

        public Builder percentageAmount(BigDecimal percentageAmount) {
            this.percentageAmount = percentageAmount;
            return this;
        }

        public Builder result(BigDecimal result) {
            this.result = result;
            return this;
        }

        public Builder calculatedAt(LocalDateTime timestamp) {
            this.calculatedAt = timestamp;
            return this;
        }

        public Builder usedCachedPercentage(boolean usedCachedPercentage) {
            this.usedCachedPercentage = usedCachedPercentage;
            return this;
        }

        public CalculationResult build() {
            Objects.requireNonNull(sum, "Sum cannot be null");
            Objects.requireNonNull(appliedPercentage, "Applied percentage cannot be null");
            Objects.requireNonNull(percentageAmount, "Percentage amount cannot be null");
            Objects.requireNonNull(result, "Final result cannot be null");

            return new CalculationResult(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
