package com.audora.app.repo;

import com.audora.app.model.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChapterRepo extends JpaRepository<Chapter, String> {

    @Query(value = "SELECT * FROM chapter WHERE audiobook_id = :id", nativeQuery = true)
    List<Chapter> findByIdAudioBookId(String id);
}
