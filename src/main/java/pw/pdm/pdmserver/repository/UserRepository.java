package pw.pdm.pdmserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pw.pdm.pdmserver.model.User;
import pw.pdm.pdmserver.model.dto.UserDto;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    @Query("SELECT new pw.pdm.pdmserver.model.dto.UserDto(u.id, u.name, u.product, u.creation, u.email) FROM User u WHERE u.id = :id")
    Optional<UserDto> findDtoById(@Param("id") Long id);
}
