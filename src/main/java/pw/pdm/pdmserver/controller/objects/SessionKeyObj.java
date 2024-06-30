package pw.pdm.pdmserver.controller.objects;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class SessionKeyObj {
    private String sessionKey;
    private LocalDateTime expirationTime;

    public SessionKeyObj(String sessionKey, LocalDateTime expirationTime) {
        this.expirationTime = expirationTime;
        this.sessionKey = sessionKey;
    }

}