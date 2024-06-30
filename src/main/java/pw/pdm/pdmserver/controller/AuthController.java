package pw.pdm.pdmserver.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pw.pdm.pdmserver.controller.objects.SessionKeyObj;
import pw.pdm.pdmserver.controller.objects.UserCredentialsObj;
import pw.pdm.pdmserver.services.SessionKeyService;

@RestController
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private SessionKeyService sessionKeyService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserCredentialsObj credentials, HttpServletRequest request) {
        // Log the login user IP, user agent, and email
        String userAgent = request.getHeader("User-Agent");
        String ipAddress = getClientIp(request);

        logger.info("Login attempt from {} with IP {} and User-Agent {}", credentials.getEmail(), ipAddress, userAgent);

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(credentials.getEmail(), credentials.getPassword())
            );

            if (authentication.isAuthenticated()) {
                String sessionKey = sessionKeyService.generateSessionKey(credentials.getEmail());
                return ResponseEntity.ok(new SessionKeyObj(sessionKey));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Session-Key") String sessionKey) {
        sessionKeyService.invalidateSession(sessionKey);
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().build();
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
