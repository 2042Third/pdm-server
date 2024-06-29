package pw.pdm.pdmserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pw.pdm.pdmserver.model.SessionKey;

@Repository
public interface SessionKeyRepository extends JpaRepository<SessionKey, Long> {
    SessionKey findBySessionKey(String sessionKey);
}
