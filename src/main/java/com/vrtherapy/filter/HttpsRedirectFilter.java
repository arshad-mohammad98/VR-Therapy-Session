package com.vrtherapy.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Filter that automatically redirects HTTP requests to HTTPS for WebXR and sensor-dependent routes
 * (such as /vr/*, /simulator, and /patient/device-pairing) when running on non-localhost network IPs.
 * <p>
 * Configurable via classpath `app.properties`. Automatically enriches session cookies with
 * `Secure` and `SameSite=Lax` attributes when requests are served over HTTPS without breaking plain HTTP.
 */
@WebFilter(filterName = "HttpsRedirectFilter", urlPatterns = {"/*"})
public class HttpsRedirectFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(HttpsRedirectFilter.class);

    private boolean redirectEnabled = true;
    private int httpsPort = 8443;
    private final List<String> protectedPrefixes = new ArrayList<>();

    @Override
    public void init(FilterConfig filterConfig) {
        loadConfiguration();
    }

    public void loadConfiguration() {
        protectedPrefixes.clear();
        protectedPrefixes.add("/vr/");
        protectedPrefixes.add("/simulator");
        protectedPrefixes.add("/patient/device-pairing");

        try (InputStream in = getClass().getClassLoader().getResourceAsStream("app.properties")) {
            if (in != null) {
                Properties props = new Properties();
                props.load(in);

                String enabledVal = props.getProperty("security.https.redirect.enabled");
                if (enabledVal != null) {
                    redirectEnabled = Boolean.parseBoolean(enabledVal.trim());
                }

                String portVal = props.getProperty("server.https.port");
                if (portVal != null) {
                    try {
                        httpsPort = Integer.parseInt(portVal.trim());
                    } catch (NumberFormatException ignored) {}
                }

                String pathsVal = props.getProperty("security.https.redirect.paths");
                if (pathsVal != null && !pathsVal.isBlank()) {
                    protectedPrefixes.clear();
                    String[] tokens = pathsVal.split(",");
                    for (String token : tokens) {
                        String trimmed = token.trim();
                        if (!trimmed.isEmpty()) {
                            protectedPrefixes.add(trimmed);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            logger.warn("Could not load app.properties for HttpsRedirectFilter: {}", ex.getMessage());
        }

        logger.info("HttpsRedirectFilter initialized (enabled={}, httpsPort={}, protectedPaths={})",
                redirectEnabled, httpsPort, protectedPrefixes);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (!(request instanceof HttpServletRequest httpRequest) || !(response instanceof HttpServletResponse httpResponse)) {
            chain.doFilter(request, response);
            return;
        }

        boolean isSecure = httpRequest.isSecure() || "https".equalsIgnoreCase(httpRequest.getHeader("X-Forwarded-Proto"));

        if (!isSecure && redirectEnabled && shouldRedirect(httpRequest)) {
            String serverName = httpRequest.getServerName();
            String uri = httpRequest.getRequestURI();
            String queryString = httpRequest.getQueryString();

            String redirectUrl = "https://" + serverName + (httpsPort == 443 ? "" : ":" + httpsPort) + uri
                    + (queryString != null && !queryString.isBlank() ? "?" + queryString : "");

            logger.info("Redirecting unencrypted WebXR request from {} to secure origin: {}", uri, redirectUrl);
            httpResponse.sendRedirect(redirectUrl);
            return;
        }

        // When request is already over HTTPS, ensure Set-Cookie has Secure and SameSite=Lax flags
        if (isSecure) {
            HttpServletResponse wrappedResponse = new HttpServletResponseWrapper(httpResponse) {
                @Override
                public void addHeader(String name, String value) {
                    if ("Set-Cookie".equalsIgnoreCase(name) && value != null) {
                        value = appendCookieSecurityFlags(value);
                    }
                    super.addHeader(name, value);
                }

                @Override
                public void setHeader(String name, String value) {
                    if ("Set-Cookie".equalsIgnoreCase(name) && value != null) {
                        value = appendCookieSecurityFlags(value);
                    }
                    super.setHeader(name, value);
                }
            };
            chain.doFilter(request, wrappedResponse);
        } else {
            chain.doFilter(request, response);
        }
    }

    public boolean shouldRedirect(HttpServletRequest req) {
        String path = req.getRequestURI();
        String contextPath = req.getContextPath();
        if (contextPath != null && !contextPath.isEmpty() && path.startsWith(contextPath)) {
            path = path.substring(contextPath.length());
        }

        for (String prefix : protectedPrefixes) {
            if (path.startsWith(prefix) || path.equalsIgnoreCase(prefix)) {
                return true;
            }
        }
        return false;
    }

    private String appendCookieSecurityFlags(String cookieHeader) {
        String updated = cookieHeader;
        if (!updated.toLowerCase().contains("secure")) {
            updated += "; Secure";
        }
        if (!updated.toLowerCase().contains("samesite")) {
            updated += "; SameSite=Lax";
        }
        return updated;
    }

    public boolean isRedirectEnabled() {
        return redirectEnabled;
    }

    public void setRedirectEnabled(boolean redirectEnabled) {
        this.redirectEnabled = redirectEnabled;
    }

    public int getHttpsPort() {
        return httpsPort;
    }

    public void setHttpsPort(int httpsPort) {
        this.httpsPort = httpsPort;
    }

    public List<String> getProtectedPrefixes() {
        return protectedPrefixes;
    }
}
