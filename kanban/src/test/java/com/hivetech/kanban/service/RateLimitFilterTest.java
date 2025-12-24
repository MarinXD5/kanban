package com.hivetech.kanban.service;

import com.hivetech.kanban.util.RateLimitFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RateLimitFilterTest {

    @InjectMocks
    private RateLimitFilter rateLimitFilter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private StringWriter responseWriter;

    @BeforeEach
    void setup() throws IOException {
        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
    }

    @Test
    void shouldAllowNonApiPathsWithoutRateLimiting() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("/health");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        rateLimitFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(response, never()).setStatus(429);
    }

    @Test
    void shouldAllowApiRequestWhenUnderRateLimit() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("/api/tasks");
        when(request.getRemoteAddr()).thenReturn("192.168.1.1");

        rateLimitFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(response, never()).setStatus(429);
    }

    @Test
    void shouldBlockRequestWhenRateLimitExceeded() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("/api/tasks");
        when(request.getRemoteAddr()).thenReturn("10.0.0.1");

        for (int i = 0; i < 100; i++) {
            rateLimitFilter.doFilter(request, response, filterChain);
        }

        rateLimitFilter.doFilter(request, response, filterChain);

        verify(response).setStatus(429);
        assertEquals(
                "Too many requests – please try again later.",
                responseWriter.toString().trim()
        );
    }

    @Test
    void shouldUseXForwardedForHeaderIfPresent() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("/api/tasks");
        when(request.getHeader("X-Forwarded-For"))
                .thenReturn("203.0.113.10, 70.41.3.18");
        when(request.getRemoteAddr()).thenReturn("10.0.0.99");

        rateLimitFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(response, never()).setStatus(429);
    }
}
