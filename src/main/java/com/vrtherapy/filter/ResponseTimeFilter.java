package com.vrtherapy.filter;

import com.vrtherapy.service.impl.SystemMetricsServiceImpl;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Filter measuring execution duration for every HTTP request and populating
 * platform response-time metrics for administrative dashboard monitoring.
 */
@WebFilter(filterName = "ResponseTimeFilter", urlPatterns = "/*")
public class ResponseTimeFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        long startTime = System.currentTimeMillis();

        chain.doFilter(request, response);

        long duration = System.currentTimeMillis() - startTime;
        if (request instanceof HttpServletRequest req && response instanceof HttpServletResponse res) {
            String uri = req.getRequestURI();
            if (!uri.contains("/static/")) {
                SystemMetricsServiceImpl.getInstance().recordRequestMetric(uri, duration);
                res.setHeader("X-Response-Time-Ms", String.valueOf(duration));
            }
        }
    }
}
