package pw.pdm.pdmserver.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pw.pdm.pdmserver.controller.objects.RefreshKeyObj;
import pw.pdm.pdmserver.controller.objects.SessionKeyObj;
import pw.pdm.pdmserver.controller.objects.UserCredentialsObj;
import pw.pdm.pdmserver.controller.objects.response.CommonResponse;
import pw.pdm.pdmserver.exception.MaxSessionsReachedException;
import pw.pdm.pdmserver.services.RefreshKeyService;
import pw.pdm.pdmserver.services.SessionKeyService;
import pw.pdm.pdmserver.util.Common;

import java.util.Map;

import static pw.pdm.pdmserver.util.Common.getClientIp;

@RestController
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;

    private final SessionKeyService sessionKeyService;

    private final RefreshKeyService refreshKeyService;

    public AuthController(AuthenticationManager authenticationManager, SessionKeyService sessionKeyService, RefreshKeyService refreshKeyService) {
        this.authenticationManager = authenticationManager;
        this.sessionKeyService = sessionKeyService;
        this.refreshKeyService = refreshKeyService;
    }

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@RequestBody UserCredentialsObj credentials, @RequestHeader("SCOPE") String scope, HttpServletRequest request) {
        // Log the login user IP, user agent, and email
        String userAgent = request.getHeader("User-Agent");
        String ipAddress = getClientIp(request);

        logger.info("Login attempt from {} with IP {} and User-Agent {}", credentials.getEmail(), ipAddress, userAgent);

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(credentials.getEmail(), credentials.getPassword())
            );

            if (authentication.isAuthenticated()) {
                SessionKeyObj sessionKey = sessionKeyService.generateSessionKey(credentials.getEmail());
                if (scope!=null && scope.contains("refresh")) {
                    logger.info("Login with refresh key scope");
                    RefreshKeyObj refreshKeyObj = refreshKeyService.generateRefreshKey(credentials.getEmail());
                    return ResponseEntity.ok().body(Map.of(
                            "sessionKey", sessionKey.getSessionKey(),
                            "expirationTime", sessionKey.getExpirationTimeUnix(),
                            "refreshKey", refreshKeyObj.getRefreshKey(),
                            "refreshKeyExpirationTime", refreshKeyObj.getRefreshKeyExpirationTime(),
                            "message", "Login successful"
                    ));
                }

                return ResponseEntity.ok().body(Map.of(
                        "sessionKey", sessionKey.getSessionKey(),
                        "expirationTime", sessionKey.getExpirationTimeUnix(),
                        "message", "Login successful"
                ));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Invalid credentials"));
            }
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid credentials"));
        } catch (MaxSessionsReachedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new CommonResponse("Maximum number of active sessions reached", "Error"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Unknown Error"));
        }

    }

    @PostMapping(value = "/refresh", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> refresh(@RequestHeader("Session-Key") String sessionKey, @RequestBody Map<String, String> body, HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        String ipAddress = getClientIp(request);

        logger.info("Refresh attempt from {} with IP {} and User-Agent {}", body.get("refreshKey"), ipAddress, userAgent);

        String refreshKey = body.get("refreshKey");
        if (refreshKey == null || refreshKey.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Refresh key is required"));
        }

        if (!refreshKeyService.isValidRefreshKey(refreshKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid refresh key"));
        }

        SessionKeyObj sessionKeyObj = refreshKeyService.refreshSessionKey(refreshKey);

        if (sessionKey != null) {
            logger.info("Has input session key, it is being invalidated.");
            sessionKeyService.invalidateSession(sessionKey);
        }

        return ResponseEntity.ok().body(Map.of(
                "sessionKey", sessionKeyObj.getSessionKey(),
                "expirationTime", sessionKeyObj.getExpirationTimeUnix(),
                "message", "Refresh successful"
        ));
    }



}
