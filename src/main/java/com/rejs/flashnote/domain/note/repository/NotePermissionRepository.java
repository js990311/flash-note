package com.rejs.flashnote.domain.note.repository;

import com.rejs.flashnote.domain.note.entity.NotePermission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotePermissionRepository extends JpaRepository<NotePermission, Long> {
}
