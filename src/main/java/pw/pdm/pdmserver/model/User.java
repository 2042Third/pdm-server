package pw.pdm.pdmserver.model;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Entity
@Table(name = "userinfo")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String spw; // This will store the pre-hashed password from the client
    private String creation;
    private String product;
    private String email;
    private String registerKey;
    private String logs;
    private String registered;
}