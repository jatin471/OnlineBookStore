package com.example.bookstore;

import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class BookServiceImplTest {

    @Rule
    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

    @Test
    public void addBookTest() throws Exception {
        String serverName = InProcessServerBuilder.generateName();
        grpcCleanup.register(InProcessServerBuilder.forName(serverName).directExecutor().addService(new BookServiceImpl()).build().start());

        BookServiceGrpc.BookServiceBlockingStub blockingStub = BookServiceGrpc.newBlockingStub(grpcCleanup.register(InProcessChannelBuilder.forName(serverName).directExecutor().build()));

        Book book = Book.newBuilder().setIsbn("12345").setTitle("Test Book").addAuthors("Author").setPageCount(100).build();
        AddBookResponse response = blockingStub.addBook(AddBookRequest.newBuilder().setBook(book).build());

        assertEquals("Book added successfully", response.getMessage());
    }

    @Test
    public void updateBookTest() throws Exception {
        String serverName = InProcessServerBuilder.generateName();
        grpcCleanup.register(InProcessServerBuilder.forName(serverName).directExecutor().addService(new BookServiceImpl()).build().start());

        BookServiceGrpc.BookServiceBlockingStub blockingStub = BookServiceGrpc.newBlockingStub(grpcCleanup.register(InProcessChannelBuilder.forName(serverName).directExecutor().build()));

        Book book = Book.newBuilder().setIsbn("12345").setTitle("Test Book").addAuthors("Author").setPageCount(100).build();
        blockingStub.addBook(AddBookRequest.newBuilder().setBook(book).build());

        Book updatedBook = Book.newBuilder().setIsbn("12345").setTitle("Updated Test Book").addAuthors("Updated Author").setPageCount(150).build();
        UpdateBookResponse response = blockingStub.updateBook(UpdateBookRequest.newBuilder().setBook(updatedBook).build());

        assertEquals("Book updated successfully", response.getMessage());

        GetBooksResponse getBooksResponse = blockingStub.getBooks(GetBooksRequest.newBuilder().build());
        Book retrievedBook = getBooksResponse.getBooksList().get(0);

        assertEquals("Updated Test Book", retrievedBook.getTitle());
        assertEquals("Updated Author", retrievedBook.getAuthors(0));
        assertEquals(150, retrievedBook.getPageCount());
    }

    @Test
    public void deleteBookTest() throws Exception {
        String serverName = InProcessServerBuilder.generateName();
        grpcCleanup.register(InProcessServerBuilder.forName(serverName).directExecutor().addService(new BookServiceImpl()).build().start());

        BookServiceGrpc.BookServiceBlockingStub blockingStub = BookServiceGrpc.newBlockingStub(grpcCleanup.register(InProcessChannelBuilder.forName(serverName).directExecutor().build()));

        Book book = Book.newBuilder().setIsbn("12345").setTitle("Test Book").addAuthors("Author").setPageCount(100).build();
        blockingStub.addBook(AddBookRequest.newBuilder().setBook(book).build());

        DeleteBookResponse response = blockingStub.deleteBook(DeleteBookRequest.newBuilder().setIsbn("12345").build());

        assertEquals("Book deleted successfully", response.getMessage());

        GetBooksResponse getBooksResponse = blockingStub.getBooks(GetBooksRequest.newBuilder().build());
        assertTrue(getBooksResponse.getBooksList().isEmpty());
    }

    @Test
    public void getBooksTest() throws Exception {
        String serverName = InProcessServerBuilder.generateName();
        grpcCleanup.register(InProcessServerBuilder.forName(serverName).directExecutor().addService(new BookServiceImpl()).build().start());

        BookServiceGrpc.BookServiceBlockingStub blockingStub = BookServiceGrpc.newBlockingStub(grpcCleanup.register(InProcessChannelBuilder.forName(serverName).directExecutor().build()));

        Book book1 = Book.newBuilder().setIsbn("12345").setTitle("Test Book 1").addAuthors("Author 1").setPageCount(100).build();
        Book book2 = Book.newBuilder().setIsbn("67890").setTitle("Test Book 2").addAuthors("Author 2").setPageCount(200).build();
        blockingStub.addBook(AddBookRequest.newBuilder().setBook(book1).build());
        blockingStub.addBook(AddBookRequest.newBuilder().setBook(book2).build());

        GetBooksResponse getBooksResponse = blockingStub.getBooks(GetBooksRequest.newBuilder().build());

        assertEquals(2, getBooksResponse.getBooksCount());
        assertEquals("Test Book 1", getBooksResponse.getBooksList().get(0).getTitle());
        assertEquals("Test Book 2", getBooksResponse.getBooksList().get(1).getTitle());
    }
}
