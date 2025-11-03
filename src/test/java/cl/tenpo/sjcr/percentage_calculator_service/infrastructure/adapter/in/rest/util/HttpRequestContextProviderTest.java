package cl.tenpo.sjcr.percentage_calculator_service.infrastructure.adapter.in.rest.util;

import cl.tenpo.sjcr.percentage_calculator_service.infrastructure.config.context.HttpRequestContext;
import cl.tenpo.sjcr.percentage_calculator_service.infrastructure.config.context.HttpRequestContextProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("HTTP Request Context Provider Tests")
class HttpRequestContextProviderTest {

    private final HttpRequestContextProvider provider = new HttpRequestContextProvider();

    @Test
    @DisplayName("Should return current context when HTTP request is present")
    void shouldReturnCurrentContextWhenHttpRequestPresent() {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.setRequestURI("/api/calculate");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        try {

            HttpRequestContext context = provider.getCurrentContext();

            assertThat(context).isNotNull();
            assertThat(context.httpMethod()).isEqualTo("POST");
            assertThat(context.endpoint()).isEqualTo("/api/calculate");
            assertThat(context.isUnknown()).isFalse();
        } finally {

            RequestContextHolder.resetRequestAttributes();
        }
    }

    @Test
    @DisplayName("Should return unknown context when no HTTP request is present")
    void shouldReturnUnknownContextWhenNoHttpRequest() {

        RequestContextHolder.resetRequestAttributes();

        HttpRequestContext context = provider.getCurrentContext();

        assertThat(context).isNotNull();
        assertThat(context.httpMethod()).isEqualTo("UNKNOWN");
        assertThat(context.endpoint()).isEqualTo("UNKNOWN");
        assertThat(context.isUnknown()).isTrue();
    }

    @Test
    @DisplayName("Should return true when HTTP context exists")
    void shouldReturnTrueWhenHttpContextExists() {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.setRequestURI("/api/history");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        try {

            boolean hasContext = provider.hasHttpContext();

            assertThat(hasContext).isTrue();
        } finally {

            RequestContextHolder.resetRequestAttributes();
        }
    }

    @Test
    @DisplayName("Should return false when HTTP context does not exist")
    void shouldReturnFalseWhenHttpContextDoesNotExist() {

        RequestContextHolder.resetRequestAttributes();

        boolean hasContext = provider.hasHttpContext();

        assertThat(hasContext).isFalse();
    }

    @Test
    @DisplayName("Should handle different HTTP methods")
    void shouldHandleDifferentHttpMethods() {

        String[] methods = { "GET", "POST", "PUT", "DELETE", "PATCH" };

        for (String method : methods) {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setMethod(method);
            request.setRequestURI("/api/test");
            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

            try {

                HttpRequestContext context = provider.getCurrentContext();

                assertThat(context.httpMethod()).isEqualTo(method);
                assertThat(context.endpoint()).isEqualTo("/api/test");
            } finally {
                RequestContextHolder.resetRequestAttributes();
            }
        }
    }

    @Test
    @DisplayName("Should handle different endpoints")
    void shouldHandleDifferentEndpoints() {

        String[] endpoints = { "/api/calculate", "/api/history", "/actuator/health", "/api/v1/test" };

        for (String endpoint : endpoints) {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setMethod("GET");
            request.setRequestURI(endpoint);
            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

            try {

                HttpRequestContext context = provider.getCurrentContext();

                assertThat(context.endpoint()).isEqualTo(endpoint);
                assertThat(context.httpMethod()).isEqualTo("GET");
            } finally {
                RequestContextHolder.resetRequestAttributes();
            }
        }
    }

    @Test
    @DisplayName("Should handle request with query parameters")
    void shouldHandleRequestWithQueryParameters() {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.setRequestURI("/api/history");
        request.setQueryString("page=0&size=10");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        try {

            HttpRequestContext context = provider.getCurrentContext();

            assertThat(context).isNotNull();
            assertThat(context.endpoint()).isEqualTo("/api/history");
            assertThat(context.httpMethod()).isEqualTo("GET");
        } finally {
            RequestContextHolder.resetRequestAttributes();
        }
    }
}
