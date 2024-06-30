package pw.pdm.pdmserver.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pw.pdm.pdmserver.model.SessionKey;
import pw.pdm.pdmserver.repository.SessionKeyRepository;
import pw.pdm.pdmserver.model.User;
import pw.pdm.pdmserver.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class SessionKeyService {

    private static final int SESSION_KEY_EXPIRATION_MINUTES = 30;
    private static final int MAX_SESSIONS_PER_USER = 5;

    @Autowired
    private SessionKeyRepository sessionKeyRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public String generateSessionKey(String userEmail) {
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // Clean up expired sessions
        sessionKeyRepository.deleteExpiredSessionsForUser(user.getId(), LocalDateTime.now());

        // Check if user has reached the session limit
        int activeSessions = sessionKeyRepository.countActiveSessionsForUser(user.getId(), LocalDateTime.now());
        if (activeSessions >= MAX_SESSIONS_PER_USER) {
            throw new RuntimeException("Maximum number of active sessions reached");
        }

        // Generate new session key
        String sessionKey = UUID.randomUUID().toString();
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(SESSION_KEY_EXPIRATION_MINUTES);

        SessionKey sk = new SessionKey();
        sk.setSessionKey(sessionKey);
        sk.setUserId(user.getId());
        sk.setExpirationTime(expirationTime);

        sessionKeyRepository.save(sk);
        return sessionKey;
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