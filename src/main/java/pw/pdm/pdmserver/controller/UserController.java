package pw.pdm.pdmserver.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
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

    @GetMapping("/logout")
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

    @GetMapping
    public ResponseEntity<?> getUser(@RequestHeader("Session-Key") String sessionKey, HttpServletRequest request) {
        logger.info("User Data attempt using Session Key {} with IP {}", sessionKey, getClientIp(request) );

        if (sessionKeyService.isValidSessionKey(sessionKey)) {
            return ResponseEntity.ok(sessionKeyService.getUserDtoBySessionKey(sessionKey));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid session key");
        }
    }

    @PostMapping
    public ResponseEntity<?> getUserWithPOST(@RequestHeader("Session-Key") String sessionKey, HttpServletRequest request) {
        logger.info("User Data with POST attempt using Session Key {} with IP {}", sessionKey, getClientIp(request) );

        if (sessionKeyService.isValidSessionKey(sessionKey)) {
            return ResponseEntity.ok(sessionKeyService.getUserDtoBySessionKey(sessionKey));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid session key");
        }
    }

    @GetMapping(value = "/validate", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> validateSession(@RequestHeader("Session-Key") String sessionKey, HttpServletRequest request) {
        logger.info("Validate session request received. Session-Key: {}, Accept header: {}",
                sessionKey, request.getHeader("Accept"));

        boolean isValid = sessionKeyService.isValidSessionKey(sessionKey);
        logger.info("Session key validity: {}", isValid);

        SessionValidationResponse response = new SessionValidationResponse(isValid,
                isValid ? "Session key is valid" : "Session key is invalid");

        ResponseEntity<?> responseEntity = isValid ?
                ResponseEntity.ok(response) :
                ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);

        logger.info("Returning response. Status: {}, Body: {}",
                responseEntity.getStatusCode(), responseEntity.getBody());

        return responseEntity;
    }

    @PostMapping("/validate")
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
