package cl.tenpo.sjcr.percentage_calculator_service.infrastructure.config.context;

public record HttpRequestContext(
        String endpoint,
        String httpMethod) {
    private static final String UNKNOWN_VALUE = "UNKNOWN";

    public static HttpRequestContext unknown() {
        return new HttpRequestContext(UNKNOWN_VALUE, UNKNOWN_VALUE);
    }

    public boolean isUnknown() {
        return UNKNOWN_VALUE.equals(endpoint) && UNKNOWN_VALUE.equals(httpMethod);
    }
}
