package pw.pdm.pdmserver.controller.objects;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserCredentialsObj {
    // Getters and setters
    private String email;
    private String password;

    // Constructors
    public UserCredentialsObj() {}

    public UserCredentialsObj(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
