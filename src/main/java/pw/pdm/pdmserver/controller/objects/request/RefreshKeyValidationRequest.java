package pw.pdm.pdmserver.controller.objects.request;

public class RefreshKeyValidationRequest {
    private String refreshKey;

    // Add this no-argument constructor
    public RefreshKeyValidationRequest() {
    }

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