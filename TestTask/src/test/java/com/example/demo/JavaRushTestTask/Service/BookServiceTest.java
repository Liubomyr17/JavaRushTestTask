package com.example.demo.JavaRushTestTask.Service;

import com.example.demo.Controller.BookRestController;
import com.example.demo.Entity.Book;
import com.example.demo.JavaRushTestTask.BookBuilder;
import com.example.demo.JavaRushTestTask.PageBuilder;
import com.example.demo.Service.BookService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.ExtendedModelMap;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.isA;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.*;

public class BookServiceTest {
    private final List<Book> books = new ArrayList<>();

    @Before
    public void initBooks() {
        Book book = new BookBuilder()
                .id(1L)
                .author("Иван Портянкин")
                .title("Swing. Эффектные пользовательские интерфейсы")
                .description("Создание пользовательских интерфейсов Java-приложений с помощью библиотеки Swing и Java Foundation Classes")
                .isbn("978-5-85582-305-9")
                .printYear(2011)
                .readAlready(false)
                .build();
        books.add(book);
    }
    @Test
    public void getAllBookTest() throws Exception {
        BookService bookService = mock(BookService.class);
        when(bookService.findAll()).thenReturn(books);
        BookRestController bookRestController = new BookRestController();
        ReflectionTestUtils.setField(bookRestController, "bookService", bookService);
        ExtendedModelMap uiModel = new ExtendedModelMap();
        uiModel.addAttribute("book", bookRestController.getAllBook());
        assertEquals(1, books.size());
    }
    @Test
    public void getPageBooksTest() throws Exception {
        Sort sort = new Sort(Sort.Direction.ASC, "id");
        Page<Book> bookPage = new PageBuilder<Book>()
                .elements(books)
                .pageRequest(new PageRequest(0, 10, sort))
                .totalEements(1)
                .build();
        BookService bookService = mock(BookService.class);
        when(bookService.findAllByPage(isA(Pageable.class))).thenReturn(bookPage);
        BookRestController bookRestController = new BookRestController();
        ReflectionTestUtils.setField(bookRestController, "bookService", bookService);
        Page<Book> books = bookRestController.getPageBooks(1, "id", "ask");
        verify(bookService).findAllByPage(notNull(Pageable.class));
        assertEquals(1, books.getTotalElements());
        assertEquals(1, books.getTotalPages());
    }
    @Test
    public void findBookByIdTest() throws Exception {
        BookService bookService = mock(BookService.class);
        when(bookService.findById(1L)).thenAnswer((Answer<Book>) invocationOnMock -> {
            for (Book book : books)
                if (book.getId() == 1L) return book;
                return null;
        });
        BookRestController bookRestController = new BookRestController();
        ReflectionTestUtils.setField(bookRestController, "bookService", bookService);
        Book book = bookRestController.findBookById(1L);
        assertEquals(1, book.getId());
    }
    @Test
    public void createTest() throws Exception{
        final Book newBook = new BookBuilder()
                .id(999L)
                .author("Джошуа Блох")
                .title("Java. Эффективное программирование")
                .description("Первое издание книги \"Java. Эффективное программирование\", содержащей пятьдесят семь ценных правил, предлагает решение задач программирования, с которыми большинство разработчиков сталкиваются каждый день")
                .isbn("978-5-85582-347-9")
                .printYear(2014)
                .readAlready(false)
                .build();

        BookService bookService = mock(BookService.class);
        when(bookService.save(newBook)).thenAnswer((Answer<Book>) invocationOnMock -> {
            books.add(newBook);
            return newBook;
        });

        BookRestController bookRestController = new BookRestController();
        ReflectionTestUtils.setField(bookRestController, "bookService", bookService);

        Book book = bookRestController.create(newBook);
        assertEquals(999L, book.getId());
        assertEquals("Джошуа Блох", book.getAuthor());
        assertEquals("978-5-85582-347-9", book.getIsbn());

        assertEquals(2, books.size());
    }


    @Test
    public void updateTest() throws Exception{
        final Book updateDataBook = new BookBuilder().readAlready(true).build();

        BookService bookService = mock(BookService.class);
        when(bookService.update(updateDataBook, 1L)).thenAnswer((Answer<Book>) invocationOnMock -> {
            Book book = books.get(0);
            if (updateDataBook.getAuthor() != null) book.setAuthor(book.getAuthor());
            if (updateDataBook.getTitle() != null) book.setTitle(book.getTitle());
            if (updateDataBook.getDescription() != null) book.setDescription(book.getDescription());
            if (updateDataBook.getIsbn() != null) book.setIsbn(book.getIsbn());
            if (updateDataBook.getPrintYear() != 0) book.setPrintYear(book.getPrintYear());
            book.setReadAlready(true);
            return book;
        });

        BookRestController bookRestController = new BookRestController();
        ReflectionTestUtils.setField(bookRestController, "bookService", bookService);

        Book book = bookRestController.update(updateDataBook, 1L);
        assertEquals(1L, book.getId());
        assertEquals(true, book.isReadAlready());

        assertEquals(1, books.size());
    }
    @Test
    public void deleteTest() throws Exception {
        BookService bookService = mock(BookService.class);
        doAnswer((Answer<Void>) invocationOnMock -> {
            books.remove(books.get(0));
            return null;
        }).when(bookService).delete(any(Book.class));
        BookRestController bookRestController = new BookRestController();
        ReflectionTestUtils.setField(bookRestController, "bookService", bookService);
        bookRestController.delete(1L);
        assertEquals(0, books.size());
    }
    @Test
    public void searchTest() throws Exception {
     Sort sort = new Sort(Sort.Direction.ASC, "id");
     Page<Book> bookPage = new PageBuilder<Book>()
             .elements(books)
             .pageRequest(new PageRequest(0, 10, sort))
             .totalEements(1)
             .build();
     BookService bookService = mock(BookService.class);
     when(bookService.search(eq("java"), eq(2000), eq(true), isA(Pageable.class))).thenReturn(bookPage);
     BookRestController bookRestController = new BookRestController();
     ReflectionTestUtils.setField(bookRestController, "bookService", bookService);
     Page<Book> books = bookRestController.search(1, "id", "ask", "java", 2000, "true");
     verify(bookService).search(eq("java"), eq(2000), eq(true), notNull(Pageable.class));
     assertEquals(1, books.getTotalElements());
     assertEquals(1, books.getTotalPages());
    }
}
