package pw.pdm.pdmserver.controller;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SessionKeyObj {
    private String sessionKey;

    public SessionKeyObj(String sessionKey) {
        this.sessionKey = sessionKey;
    }

}