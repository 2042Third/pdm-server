package pw.pdm.pdmserver.controller.objects;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class RefreshKeySessionKeyObj {
    private String sessionKey;
    private LocalDateTime expirationTime;
    private String refreshKey;
    private LocalDateTime refreshKeyExpirationTime;

    public RefreshKeySessionKeyObj(String sessionKey, LocalDateTime expirationTime) {
        this.expirationTime = expirationTime;
        this.sessionKey = sessionKey;
    }

    // Getters and Setters
    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public LocalDateTime getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(LocalDateTime expirationTime) {
        this.expirationTime = expirationTime;
    }

    public long getExpirationTimeUnix() {
        return this.expirationTime.toEpochSecond(ZoneOffset.UTC);
    }

    public String getRefreshKey() {
        return refreshKey;
    }

    public void setRefreshKey(String refreshKey) {
        this.refreshKey = refreshKey;
    }

    public LocalDateTime getRefreshKeyExpirationTime() {
        return refreshKeyExpirationTime;
    }

    public void setRefreshKeyExpirationTime(LocalDateTime refreshKeyExpirationTime) {
        this.refreshKeyExpirationTime = refreshKeyExpirationTime;
    }
}
