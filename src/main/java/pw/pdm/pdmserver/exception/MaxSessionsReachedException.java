package pw.pdm.pdmserver.exception;

public class MaxSessionsReachedException extends RuntimeException {
    public MaxSessionsReachedException(String message) {
        super(message);
    }
}