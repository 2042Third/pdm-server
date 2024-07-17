package pw.pdm.pdmserver.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pw.pdm.pdmserver.controller.objects.request.RefreshKeyValidationRequest;
import pw.pdm.pdmserver.controller.objects.response.RefreshKeyValidationResponse;
import pw.pdm.pdmserver.controller.objects.response.SessionValidationResponse;
import pw.pdm.pdmserver.services.RefreshKeyService;
import pw.pdm.pdmserver.services.SessionKeyService;

import static pw.pdm.pdmserver.util.Common.getClientIp;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final AuthenticationManager authenticationManager;

    private final SessionKeyService sessionKeyService;
    private final RefreshKeyService refreshKeyService;

    public UserController(AuthenticationManager authenticationManager, SessionKeyService sessionKeyService, RefreshKeyService refreshKeyService) {
        this.authenticationManager = authenticationManager;
        this.sessionKeyService = sessionKeyService;
        this.refreshKeyService = refreshKeyService;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/logout", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> logout(@RequestHeader("Session-Key") String sessionKey, HttpServletRequest request) {
        logger.info("Logout attempt using Session Key {} with IP {}", sessionKey, getClientIp(request));

        if (sessionKeyService.isValidSessionKey(sessionKey)) {
            sessionKeyService.invalidateSession(sessionKey);
            SecurityContextHolder.clearContext();
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid session key");
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUser(@RequestHeader("Session-Key") String sessionKey, HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        logger.info("[UserController] getUser - Authentication: {}", auth);
        logger.info("[UserController] getUser - Is authenticated: {}", auth != null && auth.isAuthenticated());
        logger.info("[UserController] getUser - Principal: {}", auth != null ? auth.getPrincipal() : "null");

        if (sessionKeyService.isValidSessionKey(sessionKey)) {
            return ResponseEntity.ok(sessionKeyService.getUserDtoBySessionKey(sessionKey));
        } else {
            logger.warn("[UserController] getUser - Invalid session key: {}", sessionKey);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid session key");
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(value="/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUserWithPOST(@RequestHeader("Session-Key") String sessionKey, HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        logger.info("[UserController] getUserWithPOST - Authentication: {}", auth);
        logger.info("[UserController] getUserWithPOST - Is authenticated: {}", auth != null && auth.isAuthenticated());
        logger.info("[UserController] getUserWithPOST - Principal: {}", auth != null ? auth.getPrincipal() : "null");

        if (sessionKeyService.isValidSessionKey(sessionKey)) {
            return ResponseEntity.ok(sessionKeyService.getUserDtoBySessionKey(sessionKey));
        } else {
            logger.warn("[UserController] getUserWithPOST - Invalid session key: {}", sessionKey);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid session key");
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value="/validate", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> validateSession(@RequestHeader("Session-Key") String sessionKey) {
        if (sessionKeyService.isValidSessionKey(sessionKey)) {
            SessionValidationResponse response = new SessionValidationResponse(true, "Session key is valid");
            return ResponseEntity.ok(response);
        } else {
            SessionValidationResponse response = new SessionValidationResponse(false, "Session key is invalid");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(value="/validate", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> validateRefreshKey(@RequestBody RefreshKeyValidationRequest refreshKey) {
        if (refreshKeyService.isValidRefreshKey(refreshKey.getRefreshKey())) {
            RefreshKeyValidationResponse response = new RefreshKeyValidationResponse(true, "Refresh key is valid");
            return ResponseEntity.ok(response);
        } else {
            RefreshKeyValidationResponse response = new RefreshKeyValidationResponse(false, "Refresh key is invalid");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
}
