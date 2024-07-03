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


import java.io.IOException;
import java.util.ArrayList;

@Component
public class CustomAuthenticationFilter extends OncePerRequestFilter {
    Logger logger = LoggerFactory.getLogger(CustomAuthenticationFilter.class);

    @Autowired
    private SessionKeyService sessionKeyService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        logger.info("[CustomAuthenticationFilter] Request URI: {}", request.getRequestURI());
        logger.info("[CustomAuthenticationFilter] Request Method: {}", request.getMethod());
        logger.info("[CustomAuthenticationFilter] Checking for Session-Key header...");
        String sessionKey = request.getHeader("Session-Key");
        if (sessionKey != null && !sessionKey.trim().isEmpty()) {
            sessionKey = sessionKey.trim();
            logger.info("[CustomAuthenticationFilter] Session-Key found: {}", sessionKey);
            if (sessionKey.length() == 36 && sessionKeyService.isValidSessionKey(sessionKey)) {
                logger.info("[CustomAuthenticationFilter] Valid Session-Key"); // Assuming UUID
                String userEmail = sessionKeyService.getUserEmailBySessionKey(sessionKey);
                Long userId = sessionKeyService.getUserIdBySessionKey(sessionKey); // Add this method to your SessionKeyService
                if (userEmail != null && !userEmail.isEmpty() && userId != null) {
                    CustomUserDetails userDetails = new CustomUserDetails(userEmail, userId, new ArrayList<>());
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } else {
                logger.info("[CustomAuthenticationFilter] Invalid Session-Key");
            }
        } else {
            logger.info("[CustomAuthenticationFilter] No Session-Key found");
        }
        filterChain.doFilter(request, response);
    }
}