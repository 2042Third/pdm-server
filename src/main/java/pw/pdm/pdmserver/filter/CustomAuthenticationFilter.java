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
                if (userEmail != null && !userEmail.isEmpty()) {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userEmail, null, new ArrayList<>());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}