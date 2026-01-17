package com.rejs.flashnote.domain.note.repository;

import com.rejs.flashnote.domain.note.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteRepository extends JpaRepository<Note, Long> {
}
