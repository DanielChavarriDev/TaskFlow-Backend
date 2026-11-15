
package com.danielchavarri.taskflow.shared.infrastructure.security;

import com.danielchavarri.taskflow.user.domain.model.User;
import com.danielchavarri.taskflow.user.domain.model.UserRole;
import com.danielchavarri.taskflow.user.domain.port.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    private JwtService jwtService;
    private UserRepository userRepository;
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    private RestAuthenticationEntryPoint authenticationEntryPoint;

    private User user;
    private Claims claims;

    @BeforeEach
    void setUp() {
        jwtService = mock(JwtService.class);
        userRepository = mock(UserRepository.class);

        authenticationEntryPoint = new RestAuthenticationEntryPoint();

        jwtAuthenticationFilter = new JwtAuthenticationFilter(
                jwtService,
                userRepository,
                authenticationEntryPoint);

        user = User.reconstitute(
                UUID.randomUUID(),
                "Daniel",
                "daniel@example.com",
                "$2a$12$encodedPasswordHash",
                UserRole.USER,
                Instant.now());

        claims = mock(Claims.class);

        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldContinueWhenAuthorizationHeaderIsMissing()
            throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        AtomicBoolean chainInvoked = new AtomicBoolean(false);

        jwtAuthenticationFilter.doFilterInternal(
                request,
                response,
                (req, res) -> chainInvoked.set(true));

        assertTrue(chainInvoked.get());

        verifyNoInteractions(jwtService, userRepository);
    }

    @Test
    void shouldAuthenticateUserWithValidToken()
            throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        request.addHeader("Authorization", "Bearer valid-token");

        when(jwtService.validateToken("valid-token"))
                .thenReturn(claims);

        when(claims.getSubject())
                .thenReturn(user.getId().toString());

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        AtomicBoolean chainInvoked = new AtomicBoolean(false);

        jwtAuthenticationFilter.doFilterInternal(
                request,
                response,
                (req, res) -> chainInvoked.set(true));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        assertTrue(chainInvoked.get());
        assertNotNull(authentication);
        assertEquals(user.getId(), authentication.getPrincipal());
        assertEquals(
                user.getEmail(),
                authentication.getDetails());

        assertTrue(authentication.getAuthorities().contains(
                new SimpleGrantedAuthority("ROLE_USER")));

        verify(userRepository).findById(user.getId());
    }

    @Test
    void shouldRejectTokenWhenUserDoesNotExist()
            throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        request.addHeader("Authorization", "Bearer valid-token");

        when(jwtService.validateToken("valid-token"))
                .thenReturn(claims);

        when(claims.getSubject())
                .thenReturn(user.getId().toString());

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.empty());

        AtomicBoolean chainInvoked = new AtomicBoolean(false);

        jwtAuthenticationFilter.doFilterInternal(
                request,
                response,
                (req, res) -> chainInvoked.set(true));

        assertEquals(
                HttpServletResponse.SC_UNAUTHORIZED,
                response.getStatus());

        assertFalse(chainInvoked.get());
        assertNull(
                SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void shouldRejectInvalidToken()
            throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        request.addHeader("Authorization", "Bearer invalid-token");

        when(jwtService.validateToken("invalid-token"))
                .thenThrow(new MalformedJwtException("Invalid token"));

        AtomicBoolean chainInvoked = new AtomicBoolean(false);

        jwtAuthenticationFilter.doFilterInternal(
                request,
                response,
                (req, res) -> chainInvoked.set(true));

        assertEquals(
                HttpServletResponse.SC_UNAUTHORIZED,
                response.getStatus());

        assertFalse(chainInvoked.get());
        assertNull(
                SecurityContextHolder.getContext().getAuthentication());

        verifyNoInteractions(userRepository);
    }

    @Test
    void shouldRejectTokenWithInvalidUserId()
            throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        request.addHeader("Authorization", "Bearer valid-token");

        when(jwtService.validateToken("valid-token"))
                .thenReturn(claims);

        when(claims.getSubject())
                .thenReturn("invalid-uuid");

        AtomicBoolean chainInvoked = new AtomicBoolean(false);

        jwtAuthenticationFilter.doFilterInternal(
                request,
                response,
                (req, res) -> chainInvoked.set(true));

        assertEquals(
                HttpServletResponse.SC_UNAUTHORIZED,
                response.getStatus());

        assertFalse(chainInvoked.get());
        assertNull(
                SecurityContextHolder.getContext().getAuthentication());

        verifyNoInteractions(userRepository);
    }
}