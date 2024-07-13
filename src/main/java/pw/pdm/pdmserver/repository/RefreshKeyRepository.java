package pw.pdm.pdmserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pw.pdm.pdmserver.model.RefreshKey;

import java.time.LocalDateTime;

@Repository
public interface RefreshKeyRepository extends JpaRepository<RefreshKey, Long> {
    RefreshKey findByRefreshKey(String refreshKey);

    @Modifying
    @Query("DELETE FROM RefreshKey rk WHERE rk.userId = :userId AND rk.expirationTime < :expirationTime")
    void deleteExpiredRefreshKeysForUser(Long userId, LocalDateTime expirationTime);

    @Query("SELECT COUNT(rk) FROM RefreshKey rk WHERE rk.userId = :userId AND rk.expirationTime > :now")
    int countActiveRefreshKeysForUser(Long userId, LocalDateTime now);

    @Modifying
    @Query("UPDATE RefreshKey rk SET rk.usage_count = rk.usage_count + 1 WHERE rk.refreshKey = :refreshKey")
    void incrementUsageCount(@Param("refreshKey") String refreshKey);

}
