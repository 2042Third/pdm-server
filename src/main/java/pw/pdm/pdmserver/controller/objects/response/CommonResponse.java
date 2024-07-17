package pw.pdm.pdmserver.controller.objects.response;

public class CommonResponse {
    private String message;
    private String type;

    public CommonResponse() {
    }

    public CommonResponse(String message, String type) {
        this.message = message;
        this.type = type;
    }

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
