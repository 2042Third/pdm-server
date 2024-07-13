package pw.pdm.pdmserver.controller.objects.response;

public class RefreshKeyValidationResponse {
    private final boolean valid;
    private final String message;

    public RefreshKeyValidationResponse(boolean valid, String message) {
        this.valid = valid;
        this.message = message;
    }

    // Getters
    public boolean isValid() {
        return valid;
    }

    public boolean getValid() {
        return valid;
    }

    public String getMessage() {
        return message;
    }
}
