package com.seera.lumi.partner.service.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class AdminTokenFilterTest {

    private static final String ADMIN_TOKEN = "super-secret-admin-token";

    private AdminTokenFilter filter;

    @Mock
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        filter = new AdminTokenFilter();
        ReflectionTestUtils.setField(filter, "adminToken", ADMIN_TOKEN);
    }

    @Test
    void validToken_passesThrough() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/partners");
        request.addHeader("X-Admin-Token", ADMIN_TOKEN);
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    void missingToken_returns401() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/partners");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentType()).isEqualTo("application/json");
        assertThat(response.getContentAsString()).contains("UNAUTHORIZED");
        assertThat(response.getContentAsString()).contains("Invalid or missing X-Admin-Token");
        verifyNoInteractions(filterChain);
    }

    @Test
    void wrongToken_returns401() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/partners");
        request.addHeader("X-Admin-Token", "wrong-token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentAsString()).contains("UNAUTHORIZED");
        verifyNoInteractions(filterChain);
    }

    @Test
    void nonAdminPath_skipsFilter() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/auth/token");

        boolean shouldSkip = filter.shouldNotFilter(request);

        assertThat(shouldSkip).isTrue();
    }

    @Test
    void adminPath_doesNotSkipFilter() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/partners/KAYAK");

        boolean shouldSkip = filter.shouldNotFilter(request);

        assertThat(shouldSkip).isFalse();
    }
}
