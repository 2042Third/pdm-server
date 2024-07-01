package pw.pdm.pdmserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pw.pdm.pdmserver.model.SessionKey;
import pw.pdm.pdmserver.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SessionKeyRepository extends JpaRepository<SessionKey, Long> {
    SessionKey findBySessionKey(String sessionKey);

    List<SessionKey> findByUserIdOrderByExpirationTimeDesc(Long userId);

    @Modifying
    @Query("DELETE FROM SessionKey sk WHERE sk.userId = :userId AND sk.expirationTime < :expirationTime")
    void deleteExpiredSessionsForUser(Long userId, LocalDateTime expirationTime);

    @Query("SELECT COUNT(sk) FROM SessionKey sk WHERE sk.userId = :userId AND sk.expirationTime > :now")
    int countActiveSessionsForUser(Long userId, LocalDateTime now);

}
