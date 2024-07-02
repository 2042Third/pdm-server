package pw.pdm.pdmserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pw.pdm.pdmserver.model.Notes;

import java.util.List;

@Repository
public interface notesRepository extends JpaRepository<Notes, Integer> {
    // You can add custom query methods here if needed

    List<Notes> findByUserId(Long userId);
}
