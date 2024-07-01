package pw.pdm.pdmserver.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pw.pdm.pdmserver.services.SessionKeyService;

import static pw.pdm.pdmserver.util.Common.getClientIp;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private SessionKeyService sessionKeyService;

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Session-Key") String sessionKey) {
        sessionKeyService.invalidateSession(sessionKey);
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<?> getUser(@RequestHeader("Session-Key") String sessionKey, HttpServletRequest request) {
        logger.info("User Data attempt using Session Key {} with IP {}", sessionKey, getClientIp(request) );

        if (sessionKeyService.isValidSessionKey(sessionKey)) {
            return ResponseEntity.ok(sessionKeyService.getUserBySessionKey(sessionKey));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid session key");
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateSession(@RequestHeader("Session-Key") String sessionKey) {
        if (sessionKeyService.isValidSessionKey(sessionKey)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid session key");
        }
    }
}
