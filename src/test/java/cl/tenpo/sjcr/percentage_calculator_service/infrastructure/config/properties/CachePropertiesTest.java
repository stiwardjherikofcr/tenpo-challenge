package cl.tenpo.sjcr.percentage_calculator_service.infrastructure.config.properties;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CachePropertiesTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should validate valid cache properties")
    void shouldValidateValidCacheProperties() {

        CacheProperties properties = new CacheProperties();
        properties.setName("testCache");
        properties.setExpirationMinutes(30);
        properties.setMaximumSize(100);
        properties.setRecordStats(true);

        Set<ConstraintViolation<CacheProperties>> violations = validator.validate(properties);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should reject blank cache name")
    void shouldRejectBlankCacheName() {

        CacheProperties properties = new CacheProperties();
        properties.setName("");
        properties.setExpirationMinutes(30);
        properties.setMaximumSize(100);

        Set<ConstraintViolation<CacheProperties>> violations = validator.validate(properties);

        assertThat(violations)
                .hasSize(1)
                .extracting(ConstraintViolation::getMessage)
                .containsExactly("Cache name must not be blank");
    }

    @Test
    @DisplayName("Should reject expiration minutes less than 1")
    void shouldRejectInvalidExpirationMinutes() {

        CacheProperties properties = new CacheProperties();
        properties.setName("testCache");
        properties.setExpirationMinutes(0);
        properties.setMaximumSize(100);

        Set<ConstraintViolation<CacheProperties>> violations = validator.validate(properties);

        assertThat(violations)
                .hasSize(1)
                .extracting(ConstraintViolation::getMessage)
                .containsExactly("Cache expiration must be at least 1 minute");
    }

    @Test
    @DisplayName("Should reject maximum size less than 1")
    void shouldRejectInvalidMaximumSize() {

        CacheProperties properties = new CacheProperties();
        properties.setName("testCache");
        properties.setExpirationMinutes(30);
        properties.setMaximumSize(0);

        Set<ConstraintViolation<CacheProperties>> violations = validator.validate(properties);

        assertThat(violations)
                .hasSize(1)
                .extracting(ConstraintViolation::getMessage)
                .containsExactly("Cache maximum size must be at least 1");
    }

    @Test
    @DisplayName("Should accept default values")
    void shouldAcceptDefaultValues() {

        CacheProperties properties = new CacheProperties();

        Set<ConstraintViolation<CacheProperties>> violations = validator.validate(properties);

        assertThat(violations).isEmpty();
        assertThat(properties.getName()).isEqualTo("percentageCache");
        assertThat(properties.getExpirationMinutes()).isEqualTo(30);
        assertThat(properties.getMaximumSize()).isEqualTo(100);
        assertThat(properties.isRecordStats()).isTrue();
    }
}
