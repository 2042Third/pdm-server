package pw.pdm.pdmserver.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pw.pdm.pdmserver.model.notes;
import pw.pdm.pdmserver.repository.notesRepository;

import java.util.List;
import java.util.Optional;

@Service
public class notesService {

    private final notesRepository notesRepo;

    @Autowired
    public notesService(notesRepository notesRepo) {
        this.notesRepo = notesRepo;
    }

    public List<notes> getAllNotes() {
        return notesRepo.findAll();
    }

    public List<notes> getAllNotesForUser(Long userId) {
        return notesRepo.findByUserId(userId);
    }

    public Optional<notes> getNoteById(Integer id) {
        return notesRepo.findById(id);
    }

    public notes saveNote(notes note) {
        return notesRepo.save(note);
    }

    public void deleteNote(Integer id) {
        notesRepo.deleteById(id);
    }
}
