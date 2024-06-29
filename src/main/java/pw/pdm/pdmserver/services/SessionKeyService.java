package pw.pdm.pdmserver.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pw.pdm.pdmserver.model.SessionKey;
import pw.pdm.pdmserver.repository.SessionKeyRepository;
import pw.pdm.pdmserver.model.User;
import pw.pdm.pdmserver.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class SessionKeyService {

    @Autowired
    private SessionKeyRepository sessionKeyRepository;

    @Autowired
    private UserRepository userRepository;

    public String generateSessionKey(String userEmail) {
        String sessionKey = UUID.randomUUID().toString();
        LocalDateTime expirationTime = LocalDateTime.now().plusHours(2);

        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

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
}