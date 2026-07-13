package io.metersphere.agent.security;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgentTokenFilterTests {

    @Test
    void isAgentTokenCallShouldDetectBearerMsatToken() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer msat_demo_token_for_local_testing_01");

        Assertions.assertTrue(AgentTokenFilter.isAgentTokenCall(request));
    }

    @Test
    void isAgentTokenCallShouldRejectMissingBearerPrefix() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("msat_demo_token_for_local_testing_01");

        Assertions.assertFalse(AgentTokenFilter.isAgentTokenCall(request));
    }

    @Test
    void isAgentTokenCallShouldRejectBlankAuthorization() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("");

        Assertions.assertFalse(AgentTokenFilter.isAgentTokenCall(request));
    }

    @Test
    void isAgentTokenCallShouldRejectNonMsatBearer() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer other_token_scheme");

        Assertions.assertFalse(AgentTokenFilter.isAgentTokenCall(request));
    }
}
