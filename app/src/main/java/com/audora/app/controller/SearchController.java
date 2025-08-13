package com.audora.app.controller;

import com.audora.app.dto.BookSearchDTO;
import com.audora.app.dto.BooksDTO;
import com.audora.app.model.Books;
import com.audora.app.service.BooksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    @Autowired
    private BooksService booksService;

    @GetMapping("/autocomplete")
    public ResponseEntity<List<BookSearchDTO>> autoCompleteSearch(
            @RequestParam String q,
            @RequestParam(defaultValue = "5") int limit){

        if(q.length()<2){
            return ResponseEntity.badRequest().build();
        }

      List<BookSearchDTO> results = booksService.autocomplete(q,limit);
        return ResponseEntity.ok(results);

    }


}
