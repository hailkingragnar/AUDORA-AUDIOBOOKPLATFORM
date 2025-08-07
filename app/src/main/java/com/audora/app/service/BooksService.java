package com.audora.app.service;

import com.audora.app.model.Books;
import com.audora.app.model.Categories;
import com.audora.app.repo.BooksRepo;
import com.audora.app.repo.CategoriesRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.print.Book;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BooksService {

    @Autowired
    private BooksRepo booksRepo;

    @Autowired
    private CategoriesRepo categoriesRepo;


    @Transactional
    public void saveAudiobook(Books book) {
       booksRepo.save(book); // Save the audiobook
    }

    @Autowired
    private CategoryService categoryService;

//    @Transactional
//    public void saveSampleAudiobooks() {
////        Books audiobook1 = new Books(UUID.randomUUID().toString(),"Executive Impacts and Influence", "John Doe", "Jane Smith", "12:30:00", "English", "Fiction",
////                "A thrilling journey through time and space.", new Date(), "978-1234567890", "MP3",
//                15.99, "Available", "executiveimpactsandinfulence.jpg");
//
//        Books audiobook2 = new Books(UUID.randomUUID().toString(),"From Faith to Prosperity", "Alice Brown", "Tom Harris", "10:45:00", "English", "Adventure",
//                "A story of survival and discovery at sea.", new Date(), "978-2345678901", "MP3",
//                12.99, "Available", "fromfaithtoprosperity.jpg");
//
//        Books audiobook3 = new Books(UUID.randomUUID().toString(),"Habits for Better Vision", "Sarah Lee", "David Clark", "8:20:00", "English", "Mystery",
//                "A gripping tale of crime and deception.", new Date(), "978-3456789012", "M4B",
//                19.99, "Available", "habitsforbettervision.jpg");
//
//        Books audiobook4 = new Books(UUID.randomUUID().toString(),"Hope is not a four letter word", "Mark Johnson", "Emily White", "14:05:00", "English", "Biography",
//                "An inside look at the world's top tech leaders.", new Date(), "978-4567890123", "WAV",
//                24.99, "Available", "hopeisnotafourletterword.jpg");
//
//        Books audiobook5 = new Books(UUID.randomUUID().toString(),"I wish I was that good enough", "Peter Green", "Robert Black", "11:15:00", "English", "Horror",
//                "A terrifying tale of ancient woods and hidden secrets.", new Date(), "978-5678901234", "MP3",
//                16.50, "Available", "iwishiwasthatgoodenough.jpg");
//
//        Books audiobook6 = new Books(UUID.randomUUID().toString(),"Ladder in your success", "Laura White", "Chris Redfield", "9:50:00", "English", "Science Fiction",
//                "A futuristic story about the survival of Earth.", new Date(), "978-6789012345", "M4B",
//                17.75, "Available", "ladderingyoursuccess.jpg");
//
//        Books audiobook7 = new Books(UUID.randomUUID().toString(),"Living empowered lifestyle", "Emily Rose", "Rachel Moore", "6:40:00", "French", "Romance",
//                "A beautiful love story set in Paris.", new Date(), "978-7890123456", "MP3",
//                9.99, "Available", "livingempoweredlifestyle.jpg");
//
//        Books audiobook8 = new Books(UUID.randomUUID().toString(),"See it and be it", "Michael Scott", "John King", "13:25:00", "English", "Fantasy",
//                "A magical adventure through ancient lands.", new Date(), "978-8901234567", "M4B",
//                21.99, "Available", "Seeitandbeit.jpg");
//
//        Books audiobook9 = new Books(UUID.randomUUID().toString(),"Unbroken threads of hope", "Claire Adams", "Samantha Rose", "7:10:00", "English", "Adventure",
//                "A journey to the unknown lands on Earth.", new Date(), "978-9012345678", "MP3",
//                13.99, "Available", "unbrokenthreadsofhope.jpg");
//
//        Books audiobook10 = new Books(UUID.randomUUID().toString(),"War is personal", "Thomas Lee", "Andrew Harris", "11:55:00", "English", "Thriller",
//                "A heart-pounding thriller filled with twists and turns.", new Date(), "978-0123456789", "MP3",
//                18.99, "Available", "warispersonal.jpg");
//
//
//        booksRepo.save(audiobook1); // Saving first audiobook
//        booksRepo.save(audiobook2); // Saving second audiobook
//        booksRepo.save(audiobook3); // Saving first audiobook
//        booksRepo.save(audiobook4); // Saving second audiobook
//        booksRepo.save(audiobook5); // Saving first audiobook
//        booksRepo.save(audiobook6); // Saving second audiobook
//        booksRepo.save(audiobook7); // Saving first audiobook
//        booksRepo.save(audiobook8); // Saving second audiobook
//        booksRepo.save(audiobook9); // Saving first audiobook
//        booksRepo.save(audiobook10); // Saving second audiobook
//    }

    public List<Books> getAllBooks(){
        return booksRepo.findAll();
    }

    @Transactional
    public void setCategoryandBook(String bookName, List<String> categoryNames){

    Books book = booksRepo.findByTitle(bookName).get();
    System.out.println("The bookname is : "+ book.getTitle());
        List<Categories> categories = categoryNames.stream()
                .map(categoryService::setCategoryName)
                .collect(Collectors.toList());

        book.setCategoriesList(categories);
        booksRepo.save(book);
    }

    public List<Categories> getCategory() {
   return categoriesRepo.findAll();

    }
}
