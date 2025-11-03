package cl.tenpo.sjcr.percentage_calculator_service.infrastructure.config.context;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("HTTP Request Context Tests")
class HttpRequestContextTest {

    @Test
    @DisplayName("Should create unknown context")
    void shouldCreateUnknownContext() {

        HttpRequestContext context = HttpRequestContext.unknown();

        assertThat(context).isNotNull();
        assertThat(context.httpMethod()).isEqualTo("UNKNOWN");
        assertThat(context.endpoint()).isEqualTo("UNKNOWN");
        assertThat(context.isUnknown()).isTrue();
    }

    @Test
    @DisplayName("Should create context with method and endpoint")
    void shouldCreateContextWithMethodAndEndpoint() {

        HttpRequestContext context = new HttpRequestContext("/api/calculate", "POST");

        assertThat(context).isNotNull();
        assertThat(context.httpMethod()).isEqualTo("POST");
        assertThat(context.endpoint()).isEqualTo("/api/calculate");
        assertThat(context.isUnknown()).isFalse();
    }

    @Test
    @DisplayName("Should identify unknown context correctly")
    void shouldIdentifyUnknownContextCorrectly() {

        HttpRequestContext unknownContext = HttpRequestContext.unknown();
        HttpRequestContext knownContext = new HttpRequestContext("/api/history", "GET");

        assertThat(unknownContext.isUnknown()).isTrue();
        assertThat(knownContext.isUnknown()).isFalse();
    }

    @Test
    @DisplayName("Should be equal when method and endpoint match")
    void shouldBeEqualWhenMethodAndEndpointMatch() {

        HttpRequestContext context1 = new HttpRequestContext("/api/calculate", "POST");
        HttpRequestContext context2 = new HttpRequestContext("/api/calculate", "POST");

        assertThat(context1).isEqualTo(context2);
        assertThat(context1).hasSameHashCodeAs(context2);
    }

    @Test
    @DisplayName("Should not be equal when method differs")
    void shouldNotBeEqualWhenMethodDiffers() {

        HttpRequestContext context1 = new HttpRequestContext("/api/calculate", "POST");
        HttpRequestContext context2 = new HttpRequestContext("/api/calculate", "GET");

        assertThat(context1).isNotEqualTo(context2);
    }

    @Test
    @DisplayName("Should not be equal when endpoint differs")
    void shouldNotBeEqualWhenEndpointDiffers() {

        HttpRequestContext context1 = new HttpRequestContext("/api/calculate", "POST");
        HttpRequestContext context2 = new HttpRequestContext("/api/history", "POST");

        assertThat(context1).isNotEqualTo(context2);
    }

    @Test
    @DisplayName("Should have readable toString")
    void shouldHaveReadableToString() {

        HttpRequestContext context = new HttpRequestContext("/api/calculate", "POST");

        String toString = context.toString();

        assertThat(toString).contains("POST");
        assertThat(toString).contains("/api/calculate");
    }

    @Test
    @DisplayName("Unknown contexts should be equal")
    void unknownContextsShouldBeEqual() {

        HttpRequestContext unknown1 = HttpRequestContext.unknown();
        HttpRequestContext unknown2 = HttpRequestContext.unknown();

        assertThat(unknown1).isEqualTo(unknown2);
    }
}
