package com.example.demo.JavaRushTestTask;

import com.example.demo.Entity.Book;

public class BookBuilder {
    private Book book;

    public BookBuilder() {
        book = new Book();
    }
    public BookBuilder id(Long id) {
        book.setId(id);
        return this;
    }
    public BookBuilder title(String title) {
        book.setTitle(title);
        return this;
    }
    public BookBuilder description(String description) {
        book.setDescription(description);
        return this;
    }
    public BookBuilder author(String author) {
        book.setAuthor(author);
        return this;
    }
    public BookBuilder isbn(String isbn) {
        book.setIsbn(isbn);
        return this;
    }
    public BookBuilder printYear(int printYear) {
        book.setPrintYear(printYear);
    return this;
    }
    public BookBuilder readAlready(boolean readAlready) {
        book.setReadAlready(readAlready);
        return this;
    }
    public Book build() {
        return book;
    }
}
