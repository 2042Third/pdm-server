package pw.pdm.pdmserver.controller.objects;

import java.time.LocalDateTime;

public class RefreshKeyObj {
    private String refreshKey;
    private LocalDateTime refreshKeyExpirationTime;

    public RefreshKeyObj(String refreshKey, LocalDateTime refreshKeyExpirationTime) {
        this.refreshKey = refreshKey;
        this.refreshKeyExpirationTime = refreshKeyExpirationTime;
    }

    // Getters and Setters
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
