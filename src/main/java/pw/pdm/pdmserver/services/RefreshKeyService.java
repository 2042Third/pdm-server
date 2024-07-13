package pw.pdm.pdmserver.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pw.pdm.pdmserver.controller.objects.RefreshKeyObj;
import pw.pdm.pdmserver.controller.objects.SessionKeyObj;
import pw.pdm.pdmserver.model.RefreshKey;
import pw.pdm.pdmserver.model.User;
import pw.pdm.pdmserver.repository.RefreshKeyRepository;
import pw.pdm.pdmserver.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RefreshKeyService {
    private static final Logger logger = LoggerFactory.getLogger(RefreshKeyService.class);

    private static final int REFRESH_KEY_EXPIRATION_DAYS = 15;
    private static final int MAX_REFRESH_KEYS_PER_USER = 5;

    private final RefreshKeyRepository refreshKeyRepository;
    private final UserRepository userRepository;
    private final SessionKeyService sessionKeyService;

    public RefreshKeyService(RefreshKeyRepository refreshKeyRepository, UserRepository userRepository, SessionKeyService sessionKeyService) {
        this.refreshKeyRepository = refreshKeyRepository;
        this.userRepository = userRepository;
        this.sessionKeyService = sessionKeyService;
    }

    public RefreshKey findByRefreshKey(String refreshKey) {
        return refreshKeyRepository.findByRefreshKey(refreshKey);
    }

    @Transactional
    public RefreshKeyObj generateRefreshKey(String userEmail) {
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        Long userId = user.getId();

        // Clean up expired refresh keys
        refreshKeyRepository.deleteExpiredRefreshKeysForUser(userId, LocalDateTime.now());

        // Check if user has reached the refresh key limit
        int activeRefreshKeys = refreshKeyRepository.countActiveRefreshKeysForUser(userId, LocalDateTime.now());
        logger.info("User Generating refresh key, currently active {} refresh keys.", activeRefreshKeys);
        if (activeRefreshKeys >= MAX_REFRESH_KEYS_PER_USER) {
            throw new RuntimeException("Maximum number of active refresh keys reached");
        }

        // Generate new refresh key
        String refreshKey = UUID.randomUUID().toString();
        LocalDateTime expirationTime = LocalDateTime.now().plusDays(REFRESH_KEY_EXPIRATION_DAYS);

        RefreshKey newRefreshKey = new RefreshKey();
        newRefreshKey.setUserId(userId);
        newRefreshKey.setRefreshKey(refreshKey);
        newRefreshKey.setExpirationTime(expirationTime);
        newRefreshKey.setUsageCount(0);
        RefreshKeyObj refreshKeyObj = new RefreshKeyObj(refreshKey, expirationTime);

        refreshKeyRepository.save(newRefreshKey);
        return refreshKeyObj;
    }

    @Transactional
    public RefreshKey getAndIncrementRefreshKey(String refreshKey) {
        refreshKeyRepository.incrementUsageCount(refreshKey);
        return refreshKeyRepository.findByRefreshKey(refreshKey);
    }

    @Transactional
    public void deleteRefreshKey(String refreshKey) {
        refreshKeyRepository.delete(refreshKeyRepository.findByRefreshKey(refreshKey));
    }

    public boolean isValidRefreshKey(String refreshKey) {
        RefreshKey rk = refreshKeyRepository.findByRefreshKey(refreshKey);
        return rk != null && rk.getExpirationTime().isAfter(LocalDateTime.now());
    }

    @Transactional
    public SessionKeyObj refreshSessionKey(String refreshKey) {
        RefreshKey rk = getAndIncrementRefreshKey(refreshKey);
        if (rk != null && rk.getExpirationTime().isAfter(LocalDateTime.now())) {
            User user = userRepository.findById(rk.getUserId()).orElse(null);
            if (user != null) {
                return sessionKeyService.generateSessionKey(user.getEmail());
            }
        }
        return null;
    }


}
