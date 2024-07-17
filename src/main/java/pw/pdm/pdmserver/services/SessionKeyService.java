package pw.pdm.pdmserver.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pw.pdm.pdmserver.controller.UserController;
import pw.pdm.pdmserver.controller.objects.SessionKeyObj;
import pw.pdm.pdmserver.exception.MaxSessionsReachedException;
import pw.pdm.pdmserver.model.SessionKey;
import pw.pdm.pdmserver.model.dto.UserDto;
import pw.pdm.pdmserver.repository.SessionKeyRepository;
import pw.pdm.pdmserver.model.User;
import pw.pdm.pdmserver.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SessionKeyService {
    private static final Logger logger = LoggerFactory.getLogger(SessionKeyService.class);

    private static final int SESSION_KEY_EXPIRATION_MINUTES = 30;
    private static final int MAX_SESSIONS_PER_USER = 5;

    private final SessionKeyRepository sessionKeyRepository;

    private final UserRepository userRepository;

    public SessionKeyService(SessionKeyRepository sessionKeyRepository, UserRepository userRepository) {
        this.sessionKeyRepository = sessionKeyRepository;
        this.userRepository = userRepository;
    }

    public SessionKey findBySessionKey(String sessionKey) {
        return sessionKeyRepository.findBySessionKey(sessionKey);
    }

    @Transactional
    public SessionKeyObj generateSessionKey(String userEmail) {
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // Clean up expired sessions
        sessionKeyRepository.deleteExpiredSessionsForUser(user.getId(), LocalDateTime.now());

        // Check if user has reached the session limit
        int activeSessions = sessionKeyRepository.countActiveSessionsForUser(user.getId(), LocalDateTime.now());
        logger.info("User Generating session key, currently active {} session keys.",activeSessions);
        if (activeSessions >= MAX_SESSIONS_PER_USER) {
            throw new MaxSessionsReachedException("Maximum number of active sessions reached");
        }

        // Generate new session key
        String sessionKey = UUID.randomUUID().toString();
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(SESSION_KEY_EXPIRATION_MINUTES);

        SessionKey sk = new SessionKey();
        sk.setSessionKey(sessionKey);
        sk.setUserId(user.getId());
        sk.setExpirationTime(expirationTime);
        SessionKeyObj skObj = new SessionKeyObj(sessionKey, expirationTime);

        sessionKeyRepository.save(sk);
        return skObj;
    }

    public boolean isValidSessionKey(String sessionKey) {
        SessionKey sk = sessionKeyRepository.findBySessionKey(sessionKey);
        return sk != null && sk.getExpirationTime().isAfter(LocalDateTime.now());
    }

    public String getUserEmailBySessionKey(String sessionKey) {
        SessionKey sk = sessionKeyRepository.findBySessionKey(sessionKey);
        if (sk != null && sk.getExpirationTime().isAfter(LocalDateTime.now())) {
            User user = userRepository.findById(sk.getUserId()).orElse(null);
            return user != null ? user.getEmail() : null;
        }
        return null;
    }

    public User getUserBySessionKey(String sessionKey) {
        SessionKey sk = sessionKeyRepository.findBySessionKey(sessionKey);
        if (sk != null && sk.getExpirationTime().isAfter(LocalDateTime.now())) {
            return userRepository.findById(sk.getUserId()).orElse(null);
        }
        return null;
    }

    public UserDto getUserDtoBySessionKey(String sessionKey) {
        SessionKey sk = sessionKeyRepository.findBySessionKey(sessionKey);
        if (sk != null && sk.getExpirationTime().isAfter(LocalDateTime.now())) {
            Optional<User> userOptional = userRepository.findById(sk.getUserId());
            return userOptional.map(user -> new UserDto(
                    user.getId(),
                    user.getName(),
                    user.getProduct(),
                    user.getCreation(),
                    user.getEmail()
            )).orElse(null);
        }
        return null;
    }

    public Long getUserIdBySessionKey(String sessionKey) {
        SessionKey sessionKeyEntity = sessionKeyRepository.findBySessionKey(sessionKey);
        return sessionKeyEntity != null ? sessionKeyEntity.getUserId() : null;
    }

    @Transactional
    public boolean refreshSession(String sessionKey) {
        SessionKey sk = sessionKeyRepository.findBySessionKey(sessionKey);
        if (sk != null && sk.getExpirationTime().isAfter(LocalDateTime.now())) {
            sk.setExpirationTime(LocalDateTime.now().plusMinutes(SESSION_KEY_EXPIRATION_MINUTES));
            sessionKeyRepository.save(sk);
            return true;
        }
        return false;
    }

    @Transactional
    public void invalidateSession(String sessionKey) {
        SessionKey sk = sessionKeyRepository.findBySessionKey(sessionKey);
        if (sk != null) {
            sessionKeyRepository.delete(sk);
        }
    }

    @Transactional
    public void invalidateAllUserSessions(Long userId) {
        List<SessionKey> userSessions = sessionKeyRepository.findByUserIdOrderByExpirationTimeDesc(userId);
        sessionKeyRepository.deleteAll(userSessions);
    }

    @Transactional
    public void cleanupExpiredSessions() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            sessionKeyRepository.deleteExpiredSessionsForUser(user.getId(), LocalDateTime.now());
        }
    }
}