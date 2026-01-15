package com.rejs.flashnote.domain.note.repository;

import com.rejs.flashnote.domain.note.entity.NoteGroup;
import com.rejs.flashnote.domain.note.entity.NotePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface NotePermissionRepository extends JpaRepository<NotePermission, Long> {
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("delete from NotePermission np where np.noteGroup= :noteGroup")
    void deleteByNoteGroup(@Param("noteGroup") NoteGroup noteGroup);
}
