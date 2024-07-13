package pw.pdm.pdmserver.controller.objects.request;

public class RefreshKeyValidationRequest {
    private String refreshKey;

    public RefreshKeyValidationRequest(String refreshKey) {
        this.refreshKey = refreshKey;
    }

    // Getters and Setters
    public String getRefreshKey() {
        return refreshKey;
    }

    public void setRefreshKey(String refreshKey) {
        this.refreshKey = refreshKey;
    }
}
