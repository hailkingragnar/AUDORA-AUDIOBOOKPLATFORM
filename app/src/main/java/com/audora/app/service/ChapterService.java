package com.audora.app.service;

import com.audora.app.dto.BooksDTO;
import com.audora.app.dto.ChaptersDTO;
import com.audora.app.model.Books;
import com.audora.app.model.Chapter;
import com.audora.app.repo.ChapterRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChapterService {
    @Autowired
    private ChapterRepo chapterRepo;

    @Autowired
    private S3Service s3Service;

@Transactional
    public List<Chapter> setchapterDetails(List<ChaptersDTO> chapters, Books books) {
    List<Chapter> chapter1 = new ArrayList<>();
    try{

        for(ChaptersDTO chapter : chapters) {
            System.out.println("The chapters are : " + chapter);
            Chapter newChapter = new Chapter();
            newChapter.setBooks(books);

            System.out.println("Attempting to upload chapter audio file: {} " + chapter.getAudioFile().getOriginalFilename());
            System.out.println("File details - Size: {} ContentType: {} " +
                    chapter.getAudioFile().getSize() + " " +
                    chapter.getAudioFile().getContentType());
            newChapter.setDuration(chapter.getDuration());


            String audioFileUrl = s3Service.uploadFile(chapter.getAudioFile());
            System.out.println("Successfully uploaded file to: {} " + audioFileUrl);

            newChapter.setAudioFileKey(audioFileUrl);
            newChapter.setTitle(chapter.getTitle());
            chapter1.add(newChapter);
            chapterRepo.save(newChapter);
        }
        }catch (Exception e) {
            System.out.println("Failed to upload audio file for chapter: " + e);
            throw new RuntimeException("Audio file upload failed", e);
        }

        return chapter1;


    }

    public List<Chapter> findChapterById(String id) {
    return chapterRepo.findByIdAudioBookId(id);
    }
}
