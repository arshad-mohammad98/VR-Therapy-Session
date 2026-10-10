package com.vrtherapy.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class HttpsRedirectFilterTest {

    private HttpsRedirectFilter filter;

    @BeforeEach
    void setUp() {
        filter = new HttpsRedirectFilter();
        filter.loadConfiguration();
    }

    @Test
    @DisplayName("Should flag WebXR launch and simulator paths for HTTPS redirection")
    void testShouldRedirectProtectedPaths() {
        HttpServletRequest reqVr = Mockito.mock(HttpServletRequest.class);
        when(reqVr.getRequestURI()).thenReturn("/vr/launch");
        when(reqVr.getContextPath()).thenReturn("");
        assertTrue(filter.shouldRedirect(reqVr));

        HttpServletRequest reqSim = Mockito.mock(HttpServletRequest.class);
        when(reqSim.getRequestURI()).thenReturn("/simulator");
        when(reqSim.getContextPath()).thenReturn("");
        assertTrue(filter.shouldRedirect(reqSim));

        HttpServletRequest reqPairing = Mockito.mock(HttpServletRequest.class);
        when(reqPairing.getRequestURI()).thenReturn("/patient/device-pairing");
        when(reqPairing.getContextPath()).thenReturn("");
        assertTrue(filter.shouldRedirect(reqPairing));
    }

    @Test
    @DisplayName("Should not redirect regular pages like login or dashboard")
    void testShouldNotRedirectUnprotectedPages() {
        HttpServletRequest reqLogin = Mockito.mock(HttpServletRequest.class);
        when(reqLogin.getRequestURI()).thenReturn("/login");
        when(reqLogin.getContextPath()).thenReturn("");
        assertFalse(filter.shouldRedirect(reqLogin));

        HttpServletRequest reqDash = Mockito.mock(HttpServletRequest.class);
        when(reqDash.getRequestURI()).thenReturn("/dashboard");
        when(reqDash.getContextPath()).thenReturn("");
        assertFalse(filter.shouldRedirect(reqDash));
    }

    @Test
    @DisplayName("Should execute redirection to HTTPS port 8443 when unencrypted request targets /vr/launch")
    void testDoFilterRedirectsHttpToHttps() throws Exception {
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);
        FilterChain chain = Mockito.mock(FilterChain.class);

        when(req.isSecure()).thenReturn(false);
        when(req.getHeader("X-Forwarded-Proto")).thenReturn(null);
        when(req.getRequestURI()).thenReturn("/vr/launch");
        when(req.getContextPath()).thenReturn("");
        when(req.getServerName()).thenReturn("192.168.0.237");
        when(req.getQueryString()).thenReturn("sessionId=1");

        filter.doFilter(req, resp, chain);

        verify(resp).sendRedirect("https://192.168.0.237:8443/vr/launch?sessionId=1");
        verify(chain, never()).doFilter(req, resp);
    }

    @Test
    @DisplayName("Should pass through if request is already HTTPS secure")
    void testDoFilterPassesSecureRequests() throws Exception {
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);
        FilterChain chain = Mockito.mock(FilterChain.class);

        when(req.isSecure()).thenReturn(true);

        filter.doFilter(req, resp, chain);

        verify(resp, never()).sendRedirect(Mockito.anyString());
    }
}
