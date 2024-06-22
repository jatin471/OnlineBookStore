package com.example.bookstore;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class BookStoreClient {

    public static void main(String[] args) {
        // Create a channel to the server
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080)
                .usePlaintext()
                .build();

        // Create a stub for making calls to the gRPC service
        BookServiceGrpc.BookServiceBlockingStub stub = BookServiceGrpc.newBlockingStub(channel);

        // 1. Add a book
        Book book = Book.newBuilder()
                .setIsbn("12345")
                .setTitle("Test Book")
                .addAuthors("Author One")
                .addAuthors("Author Two")
                .setPageCount(100)
                .build();

        AddBookResponse addResponse = stub.addBook(AddBookRequest.newBuilder().setBook(book).build());
        System.out.println("AddBookResponse: " + addResponse.getMessage());

        // 2. Get all books
        GetBooksResponse getResponse = stub.getBooks(GetBooksRequest.newBuilder().build());
        System.out.println("GetBooksResponse: ");
        getResponse.getBooksList().forEach(b -> System.out.println(b.getTitle() + " by " + b.getAuthorsList()));

        // 3. Update the book
        Book updatedBook = Book.newBuilder()
                .setIsbn("12345")
                .setTitle("Updated Test Book")
                .addAuthors("Author One")
                .addAuthors("Author Three")
                .setPageCount(150)
                .build();

        UpdateBookResponse updateResponse = stub.updateBook(UpdateBookRequest.newBuilder().setBook(updatedBook).build());
        System.out.println("UpdateBookResponse: " + updateResponse.getMessage());

        // 4. Get all books after update
        getResponse = stub.getBooks(GetBooksRequest.newBuilder().build());
        System.out.println("GetBooksResponse after update: ");
        getResponse.getBooksList().forEach(b -> System.out.println(b.getTitle() + " by " + b.getAuthorsList()));

        // 5. Delete the book
        DeleteBookResponse deleteResponse = stub.deleteBook(DeleteBookRequest.newBuilder().setIsbn("12345").build());
        System.out.println("DeleteBookResponse: " + deleteResponse.getMessage());

        // 6. Get all books after delete
        getResponse = stub.getBooks(GetBooksRequest.newBuilder().build());
        System.out.println("GetBooksResponse after delete: ");
        getResponse.getBooksList().forEach(b -> System.out.println(b.getTitle() + " by " + b.getAuthorsList()));

        // Shutdown the channel
        channel.shutdown();
    }
}
