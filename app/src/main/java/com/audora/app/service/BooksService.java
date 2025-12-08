package com.audora.app.service;

import com.audora.app.dto.BookSearchDTO;
import com.audora.app.dto.BooksDTO;
import com.audora.app.dto.ChaptersDTO;
import com.audora.app.model.*;
import com.audora.app.repo.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BooksService {

    @Autowired
    private S3Service s3Service;

    @Autowired
    private UsersRepo usersRepo;

    @Autowired
    private SavedBookRepo savedBookRepo;

    @Autowired
    private BooksRepo booksRepo;

    @Autowired
    public FavoritesBooksRepo favoritesBooksRepo;

    @Autowired
    private CategoriesRepo categoriesRepo;

    @Autowired
    private CategoryService categoryService;

    @Transactional
    public void saveAudiobook(Books book) {
       booksRepo.save(book); // Save the audiobook
    }

    @Autowired
    private ChapterService chapterService;



    public List<Books> getAllBooks(){
        return booksRepo.findAll();
    }


    @Transactional
    public boolean setCategoryandBook(BooksDTO booksDTO, String fileName){
    boolean status = false;
    try{
     List<String> categoryNames = booksDTO.getCategoryNames();
     Books book = new Books();

     System.out.println("The bookname is : "+ booksDTO.getTitle());
        List<Categories> categories = categoryNames.stream()
                .map(categoryService::setCategoryName)
                .collect(Collectors.toList());


        System.out.println("The chapters are : "+ booksDTO.getChapters());

        book.setCategoriesList(categories);
        book.setAuthor(booksDTO.getAuthor());
        book.setAvailability(booksDTO.getAvailability());
        book.setDuration(booksDTO.getDuration());
        book.setGenre(booksDTO.getGenre());
        book.setDescription(booksDTO.getDescription());
        book.setFileFormat(booksDTO.getFileFormat());
        book.setImage(fileName);
        book.setIsbn(booksDTO.getIsbn());
        book.setNarrator(booksDTO.getNarrator());
        book.setLanguage(booksDTO.getLanguage());
        book.setPrice(booksDTO.getPrice());
        book.setReleaseDate(booksDTO.getReleaseDate());
        book.setTitle(booksDTO.getTitle());
        Books savedBook = booksRepo.save(book);

        System.out.println("The Book obj is : " + book);

        List<Chapter> chapternew =  chapterService.setchapterDetails(booksDTO.getChapters(),savedBook);


        savedBook.setChapters(chapternew);
        booksRepo.save(book);
        status = true;
    } catch(Exception e){
        return status;
    }
        return status;
    }

    public List<Categories> getCategory() {
    return categoriesRepo.findAll();

    }

    public Books findBookById(String audiobookId) {

    return booksRepo.findById(audiobookId).get();

    }

    public BooksDTO bookUpdateDTO(Books book){
    return new BooksDTO(book.getAudiobookId(),book.getTitle(),book.getAuthor(),book.getNarrator(),book.getDuration(),book.getLanguage(),book.getGenre(),book.getDescription(),book.getReleaseDate(),book.getIsbn(),book.getFileFormat(),book.getPrice(),book.getAvailability(),getCategoriesList(book),book.getImage(), convertToChaptersDto(book));
    }



    public List<String> getCategoriesList(Books book){
    return book.getCategoriesList()
            .stream()
            .map(Categories :: getCategoryName)
            .toList();
    }


    public List<ChaptersDTO> convertToChaptersDto(Books book){
       return book.getChapters().stream()
               .map(chapters->
               {

                   ChaptersDTO chaptersDTO = new ChaptersDTO();
                   chaptersDTO.setId(chapters.getId());
                   chaptersDTO.setTitle(chapters.getTitle());
                   chaptersDTO.setDuration(chapters.getDuration());
                   chaptersDTO.setAudioFileUrl(chapters.getAudioFileKey());
                   return chaptersDTO;
               })
               .collect(Collectors.toList());
    }


    public List<BookSearchDTO> autocomplete(String q, int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by("title").ascending());


      return    booksRepo
               .findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(
               q,q, pageable)
               .stream()
               .map(this :: convertToDTO)
               .collect(Collectors.toList());

    }

    private BookSearchDTO convertToDTO(Books books){
         return new BookSearchDTO(
                 books.getAudiobookId(),
                 books.getTitle(),
                 books.getAuthor(),
                 books.getImage()
         );
    }

    @Transactional
    public boolean updateCategoryAndBook(BooksDTO booksDTO, String fileName){
        boolean status = false;
        try{
            List<String> categoryNames = booksDTO.getCategoryNames();
            Books book = findBookById(booksDTO.getAudiobookId());

            System.out.println("The bookname is : "+ booksDTO.getTitle());
            List<Categories> categories = categoryNames.stream()
                    .map(categoryService::setCategoryName)
                    .collect(Collectors.toList());


            System.out.println("The chapters are : "+ booksDTO.getChapters());

            book.setCategoriesList(categories);
            book.setAuthor(booksDTO.getAuthor());
            book.setAvailability(booksDTO.getAvailability());
            book.setDuration(booksDTO.getDuration());
            book.setGenre(booksDTO.getGenre());
            book.setDescription(booksDTO.getDescription());
            book.setFileFormat(booksDTO.getFileFormat());
            book.setImage(fileName);
            book.setIsbn(booksDTO.getIsbn());
            book.setNarrator(booksDTO.getNarrator());
            book.setLanguage(booksDTO.getLanguage());
            book.setPrice(booksDTO.getPrice());
            book.setReleaseDate(booksDTO.getReleaseDate());
            book.setTitle(booksDTO.getTitle());
            Books savedBook = booksRepo.save(book);

            System.out.println("The Book obj is : " + book);

            if(booksDTO.getChapters() != null && !booksDTO.getChapters().isEmpty()) {

                List<Chapter> chapternew = chapterService.setchapterDetails(booksDTO.getChapters(), savedBook);
                savedBook.setChapters(chapternew);
                booksRepo.save(book);
            }

            status = true;
        } catch(Exception e){
            return status;
        }
        return status;
    }

    @Transactional
    public boolean deleteBook(String id) {
        try {
            return booksRepo.findById(id).map(book -> {
                booksRepo.delete(book);
                return true;
            }).orElse(false);
        } catch (Exception e) {
            System.out.println("Exception while deleting book: {}" + e.getMessage() + e);
            return false;
        }
    }

    public List<Categories> getCategoriesById(String audiobookId) {
        Optional<Books> books =  booksRepo.findById(audiobookId);
        if(books.isPresent()){
            return books.get().getCategoriesList();
        } else {
            throw new RuntimeException("Book is not found");
        }
    }

    public Boolean checkBookByUserIdAndBookId(String userId, String bookId) {
        return savedBookRepo.existsByBookIdAndUserId(userId,bookId);

    }

    @Modifying
    @Transactional
    public void removeBookFromSaved(String userId, String bookId) {
        try{
            savedBookRepo.removeBookFromSavedList(userId,bookId);
        } catch (Exception e){
            throw new RuntimeException("Book deletion failed");
        }
    }

    @Transactional
    public void saveBook(String userId, String bookId) {

        // ✅ Prevent duplicate saves
        boolean alreadySaved = checkBookByUserIdAndBookId(userId, bookId);
        if (alreadySaved) {
            throw new RuntimeException("Book already saved");
        }

        Users user = usersRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Books book = booksRepo.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        SavedBooks savedBooks = new SavedBooks();
        savedBooks.setUser(user);
        savedBooks.setBooks(book);
        savedBooks.setSavedAt(LocalDateTime.now());

        savedBookRepo.save(savedBooks);
    }


    public List<SavedBooks> getSavedBooksByUserId(String userId) {
        return savedBookRepo.findByUserUserId(userId); // returns empty list if nothing found
    }


    public List<Books> findBookByAuthorId(String id) {
        return booksRepo.findByAuthor_Id(id);
    }




    public Boolean checkBookIsFavOrNot(String userId, String bookId) {
        return favoritesBooksRepo.isFavOrNot(userId,bookId);
    }

    @Transactional
    public void addBookToFav(String userId, String bookId) {
        // ✅ Prevent duplicate saves
        boolean alreadyAdded = checkBookIsFavOrNot(userId, bookId);
        if (alreadyAdded) {
            throw new RuntimeException("Book already saved");
        }

        Users user = usersRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Books book = booksRepo.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        Favourites favourites = new Favourites();
        favourites.setUser(user);
        favourites.setBooks(book);
        favourites.setLocalDateTime(LocalDateTime.now());
        favoritesBooksRepo.save(favourites);
    }

    @Modifying
    @Transactional
    public void removeBookFromFav(String userId, String bookId) {
        try{
            favoritesBooksRepo.removeBookFromSavedList(userId,bookId);
        } catch (Exception e){
            throw new RuntimeException("Book deletion failed");
        }
    }

    public List<Favourites> getFavBooksByUserId(String userId) {
        return favoritesBooksRepo.findByUserUserId(userId); // returns empty list if nothing found
    }

}
