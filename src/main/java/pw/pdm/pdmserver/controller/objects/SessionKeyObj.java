package pw.pdm.pdmserver.controller.objects;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class SessionKeyObj {
    private String sessionKey;
    private LocalDateTime expirationTime;

    public SessionKeyObj(String sessionKey, LocalDateTime expirationTime) {
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
}
