package pw.pdm.pdmserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pw.pdm.pdmserver.model.notes;

import java.util.List;

@Repository
public interface notesRepository extends JpaRepository<notes, Integer> {
    // You can add custom query methods here if needed

    List<notes> findByUserId(Long userId);
}
