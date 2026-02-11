package com.rejs.flashnote.global.meilisearch.document;

import com.rejs.flashnote.domain.note.entity.Note;
import com.rejs.flashnote.domain.note.search.NoteDocument;
import com.rejs.flashnote.global.meilisearch.exception.DocumentNotExistException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum DocumentMetadatas {
    NOTE("notes", "note_id", NoteDocument.class)
    ;
    private final String indexName;
    private final String primarykey;
    private final Class<?> clazz;

    public static DocumentMetadatas getByClazz(Class<?> clazz){
        return Arrays.stream(DocumentMetadatas.values())
                .filter(meta -> meta.clazz.equals(clazz))
                .findFirst()
                .orElseThrow(DocumentNotExistException::new);
    }
}
