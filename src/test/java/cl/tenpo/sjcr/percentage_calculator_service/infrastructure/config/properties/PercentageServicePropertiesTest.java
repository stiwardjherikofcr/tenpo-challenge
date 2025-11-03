package cl.tenpo.sjcr.percentage_calculator_service.infrastructure.config.properties;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class PercentageServicePropertiesTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should validate valid percentage service properties")
    void shouldValidateValidProperties() {

        PercentageServiceProperties properties = new PercentageServiceProperties();
        PercentageServiceProperties.MockConfig mockConfig = new PercentageServiceProperties.MockConfig();
        mockConfig.setEnabled(true);
        mockConfig.setDefaultPercentage(new BigDecimal("15.0"));
        mockConfig.setFailureRate(0.3);
        properties.setMock(mockConfig);

        Set<ConstraintViolation<PercentageServiceProperties>> violations = validator.validate(properties);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should reject null mock configuration")
    void shouldRejectNullMockConfig() {

        PercentageServiceProperties properties = new PercentageServiceProperties();
        properties.setMock(null);

        Set<ConstraintViolation<PercentageServiceProperties>> violations = validator.validate(properties);

        assertThat(violations)
                .hasSize(1)
                .extracting(ConstraintViolation::getMessage)
                .containsExactly("Mock service configuration must not be null");
    }

    @Test
    @DisplayName("Should reject default percentage less than 0")
    void shouldRejectNegativeDefaultPercentage() {

        PercentageServiceProperties properties = new PercentageServiceProperties();
        PercentageServiceProperties.MockConfig mockConfig = new PercentageServiceProperties.MockConfig();
        mockConfig.setDefaultPercentage(new BigDecimal("-1.0"));
        mockConfig.setFailureRate(0.3);
        properties.setMock(mockConfig);

        Set<ConstraintViolation<PercentageServiceProperties>> violations = validator.validate(properties);

        assertThat(violations)
                .hasSize(1)
                .extracting(ConstraintViolation::getMessage)
                .containsExactly("Default percentage must be at least 0");
    }

    @Test
    @DisplayName("Should reject default percentage greater than 100")
    void shouldRejectExcessiveDefaultPercentage() {

        PercentageServiceProperties properties = new PercentageServiceProperties();
        PercentageServiceProperties.MockConfig mockConfig = new PercentageServiceProperties.MockConfig();
        mockConfig.setDefaultPercentage(new BigDecimal("101.0"));
        mockConfig.setFailureRate(0.3);
        properties.setMock(mockConfig);

        Set<ConstraintViolation<PercentageServiceProperties>> violations = validator.validate(properties);

        assertThat(violations)
                .hasSize(1)
                .extracting(ConstraintViolation::getMessage)
                .containsExactly("Default percentage must be at most 100");
    }

    @Test
    @DisplayName("Should reject failure rate less than 0")
    void shouldRejectNegativeFailureRate() {

        PercentageServiceProperties properties = new PercentageServiceProperties();
        PercentageServiceProperties.MockConfig mockConfig = new PercentageServiceProperties.MockConfig();
        mockConfig.setDefaultPercentage(new BigDecimal("15.0"));
        mockConfig.setFailureRate(-0.1);
        properties.setMock(mockConfig);

        Set<ConstraintViolation<PercentageServiceProperties>> violations = validator.validate(properties);

        assertThat(violations)
                .hasSize(1)
                .extracting(ConstraintViolation::getMessage)
                .containsExactly("Failure rate must be at least 0.0");
    }

    @Test
    @DisplayName("Should reject failure rate greater than 1")
    void shouldRejectExcessiveFailureRate() {

        PercentageServiceProperties properties = new PercentageServiceProperties();
        PercentageServiceProperties.MockConfig mockConfig = new PercentageServiceProperties.MockConfig();
        mockConfig.setDefaultPercentage(new BigDecimal("15.0"));
        mockConfig.setFailureRate(1.5);
        properties.setMock(mockConfig);

        Set<ConstraintViolation<PercentageServiceProperties>> violations = validator.validate(properties);

        assertThat(violations)
                .hasSize(1)
                .extracting(ConstraintViolation::getMessage)
                .containsExactly("Failure rate must be at most 1.0");
    }

    @Test
    @DisplayName("Should accept default values")
    void shouldAcceptDefaultValues() {

        PercentageServiceProperties properties = new PercentageServiceProperties();

        Set<ConstraintViolation<PercentageServiceProperties>> violations = validator.validate(properties);

        assertThat(violations).isEmpty();
        assertThat(properties.getMock()).isNotNull();
        assertThat(properties.getMock().isEnabled()).isTrue();
        assertThat(properties.getMock().getDefaultPercentage()).isEqualByComparingTo("15.0");
        assertThat(properties.getMock().getFailureRate()).isEqualTo(0.3);
    }

    @Test
    @DisplayName("Should accept boundary values for percentage")
    void shouldAcceptBoundaryPercentageValues() {

        PercentageServiceProperties propertiesMin = new PercentageServiceProperties();
        PercentageServiceProperties.MockConfig mockConfigMin = new PercentageServiceProperties.MockConfig();
        mockConfigMin.setDefaultPercentage(new BigDecimal("0.0"));
        mockConfigMin.setFailureRate(0.0);
        propertiesMin.setMock(mockConfigMin);

        PercentageServiceProperties propertiesMax = new PercentageServiceProperties();
        PercentageServiceProperties.MockConfig mockConfigMax = new PercentageServiceProperties.MockConfig();
        mockConfigMax.setDefaultPercentage(new BigDecimal("100.0"));
        mockConfigMax.setFailureRate(1.0);
        propertiesMax.setMock(mockConfigMax);

        Set<ConstraintViolation<PercentageServiceProperties>> violationsMin = validator.validate(propertiesMin);
        Set<ConstraintViolation<PercentageServiceProperties>> violationsMax = validator.validate(propertiesMax);

        assertThat(violationsMin).isEmpty();
        assertThat(violationsMax).isEmpty();
    }
}
