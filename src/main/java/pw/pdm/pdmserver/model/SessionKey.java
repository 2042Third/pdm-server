package pw.pdm.pdmserver.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Data
@Entity
public class SessionKey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sessionKey;
    private Long userId;
    private LocalDateTime expirationTime;
}

