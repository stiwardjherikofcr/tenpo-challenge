package cl.tenpo.sjcr.percentage_calculator_service.infrastructure.config.context;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class HttpRequestContextProvider {

    private static final Logger log = LoggerFactory.getLogger(HttpRequestContextProvider.class);

    public HttpRequestContext getCurrentContext() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes();

            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String endpoint = request.getRequestURI();
                String httpMethod = request.getMethod();

                log.trace("Captured HTTP context: {} {}", httpMethod, endpoint);
                return new HttpRequestContext(endpoint, httpMethod);
            } else {
                log.trace("No HTTP request context available (likely async or test scenario)");
                return HttpRequestContext.unknown();
            }
        } catch (Exception e) {
            log.warn("Error capturing HTTP request context: {}", e.getMessage());
            return HttpRequestContext.unknown();
        }
    }

    public boolean hasHttpContext() {
        try {
            return RequestContextHolder.getRequestAttributes() != null;
        } catch (Exception e) {
            return false;
        }
    }
}
