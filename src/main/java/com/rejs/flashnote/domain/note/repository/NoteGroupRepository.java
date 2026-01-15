package com.rejs.flashnote.domain.note.repository;

import com.rejs.flashnote.domain.note.entity.NoteGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteGroupRepository extends JpaRepository<NoteGroup, Long> {
}
