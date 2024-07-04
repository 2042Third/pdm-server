package pw.pdm.pdmserver.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.stereotype.Component;
import pw.pdm.pdmserver.controller.objects.CustomUserDetails;
import pw.pdm.pdmserver.services.SessionKeyService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CustomAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationFilter.class);

    private static final List<String> WHITELISTED_ENDPOINTS = Arrays.asList("/login", "/register");

    @Autowired
    private SessionKeyService sessionKeyService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String requestUri = request.getRequestURI();
        logger.info("[CustomAuthenticationFilter] Request URI: {}", requestUri);
        logger.info("[CustomAuthenticationFilter] Request Method: {}", request.getMethod());

        if (isWhitelistedEndpoint(requestUri)) {
            logger.info("[CustomAuthenticationFilter] Skipping authentication for whitelisted endpoint: {}", requestUri);
            filterChain.doFilter(request, response);
            return;
        }

        logger.info("[CustomAuthenticationFilter] Checking for Session-Key header...");
        String sessionKey = request.getHeader("Session-Key");
        if (sessionKey != null && !sessionKey.trim().isEmpty()) {
            sessionKey = sessionKey.trim();
            logger.info("[CustomAuthenticationFilter] Session-Key found: {}", sessionKey);
            if (sessionKey.length() == 36 && sessionKeyService.isValidSessionKey(sessionKey)) {
                logger.info("[CustomAuthenticationFilter] Valid Session-Key");
                String userEmail = sessionKeyService.getUserEmailBySessionKey(sessionKey);
                Long userId = sessionKeyService.getUserIdBySessionKey(sessionKey);
                if (userEmail != null && !userEmail.isEmpty() && userId != null) {
                    CustomUserDetails userDetails = new CustomUserDetails(userEmail, userId, new ArrayList<>());
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    filterChain.doFilter(request, response);
                    return;
                }
            } else {
                logger.info("[CustomAuthenticationFilter] Invalid Session-Key");
                sendInvalidSessionKeyResponse(response);
                return;
            }
        } else {
            logger.info("[CustomAuthenticationFilter] No Session-Key found");
            sendMissingSessionKeyResponse(response);
            return;
        }
    }

    private boolean isWhitelistedEndpoint(String requestUri) {
        return WHITELISTED_ENDPOINTS.stream().anyMatch(requestUri::startsWith);
    }

    private void sendInvalidSessionKeyResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("error", "Invalid session key");
        errorDetails.put("message", "The provided session key is invalid or expired");
        response.getWriter().write(objectMapper.writeValueAsString(errorDetails));
    }

    private void sendMissingSessionKeyResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("error", "Missing session key");
        errorDetails.put("message", "No session key provided in the request");
        response.getWriter().write(objectMapper.writeValueAsString(errorDetails));
    }
}