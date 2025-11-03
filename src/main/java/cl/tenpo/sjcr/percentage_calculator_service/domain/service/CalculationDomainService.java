package cl.tenpo.sjcr.percentage_calculator_service.domain.service;

import cl.tenpo.sjcr.percentage_calculator_service.domain.exception.CalculationException;
import cl.tenpo.sjcr.percentage_calculator_service.domain.valueobject.CalculationRequest;
import cl.tenpo.sjcr.percentage_calculator_service.domain.valueobject.CalculationResult;
import cl.tenpo.sjcr.percentage_calculator_service.domain.valueobject.Percentage;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class CalculationDomainService {

    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    public CalculationResult calculate(
            CalculationRequest request,
            Percentage percentage,
            boolean usedCache
    ) {
        try {

            BigDecimal sum = request.calculateSum();

            BigDecimal percentageAmount = percentage.applyTo(sum);

            BigDecimal result = sum.add(percentageAmount)
                    .setScale(SCALE, ROUNDING_MODE);

            return CalculationResult.builder()
                    .sum(sum.setScale(SCALE, ROUNDING_MODE))
                    .appliedPercentage(percentage)
                    .percentageAmount(percentageAmount.setScale(SCALE, ROUNDING_MODE))
                    .result(result)
                    .usedCachedPercentage(usedCache)
                    .build();

        } catch (ArithmeticException e) {
            throw new CalculationException(
                    "Error performing calculation: arithmetic overflow or precision issue", e
            );
        } catch (Exception e) {
            throw new CalculationException(
                    "Unexpected error during calculation", e
            );
        }
    }

    public void validateResult(CalculationResult result) {
        Objects.requireNonNull(result, "Calculation result cannot be null");

        if (result.getResult().compareTo(BigDecimal.ZERO) < 0) {
            throw new CalculationException("Final result cannot be negative");
        }

        BigDecimal expectedResult = result.getSum()
                .add(result.getPercentageAmount())
                .setScale(SCALE, ROUNDING_MODE);

        if (result.getResult().compareTo(expectedResult) != 0) {
            throw new CalculationException(
                    String.format("Result inconsistency: expected %s but got %s",
                            expectedResult, result.getResult())
            );
        }
    }

}
