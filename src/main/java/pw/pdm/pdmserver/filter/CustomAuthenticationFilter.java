package pw.pdm.pdmserver.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

    @Autowired
    private SessionKeyService sessionKeyService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String sessionKey = request.getHeader("Session-Key");
        if (sessionKey != null && !sessionKey.trim().isEmpty()) {
            sessionKey = sessionKey.trim();
            if (sessionKey.length() == 36 && sessionKeyService.isValidSessionKey(sessionKey)) { // Assuming UUID
                String userEmail = sessionKeyService.getUserEmailBySessionKey(sessionKey);
                Long userId = sessionKeyService.getUserIdBySessionKey(sessionKey); // Add this method to your SessionKeyService
                if (userEmail != null && !userEmail.isEmpty() && userId != null) {
                    CustomUserDetails userDetails = new CustomUserDetails(userEmail, userId, new ArrayList<>());
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}