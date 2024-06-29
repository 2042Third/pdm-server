package pw.pdm.pdmserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pw.pdm.pdmserver.model.notes;
import pw.pdm.pdmserver.services.notesService;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class notesController {

    private final notesService noteService;

    @Autowired
    public notesController(notesService noteService) {
        this.noteService = noteService;
    }

    @GetMapping
    public List<notes> getAllNotes() {
        return noteService.getAllNotes();
    }

    @GetMapping("/{id}")
    public ResponseEntity<notes> getNoteById(@PathVariable Integer id) {
        return noteService.getNoteById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public notes createNote(@RequestBody notes note) {
        return noteService.saveNote(note);
    }

    @PutMapping("/{id}")
    public ResponseEntity<notes> updateNote(@PathVariable Integer id, @RequestBody notes note) {
        return noteService.getNoteById(id)
                .map(existingNote -> {
                    note.setNoteid(id);
                    return ResponseEntity.ok(noteService.saveNote(note));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable Integer id) {
        return noteService.getNoteById(id)
                .map(note -> {
                    noteService.deleteNote(id);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
