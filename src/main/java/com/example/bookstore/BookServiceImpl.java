package com.example.bookstore;

import io.grpc.stub.StreamObserver;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class BookServiceImpl extends BookServiceGrpc.BookServiceImplBase {

    private final ConcurrentMap<String, Book> bookStore = new ConcurrentHashMap<>();

    @Override
    public void addBook(AddBookRequest request, StreamObserver<AddBookResponse> responseObserver) {
        Book book = request.getBook();
        bookStore.put(book.getIsbn(), book);
        AddBookResponse response = AddBookResponse.newBuilder().setMessage("Book added successfully").build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void updateBook(UpdateBookRequest request, StreamObserver<UpdateBookResponse> responseObserver) {
        Book book = request.getBook();
        if (bookStore.containsKey(book.getIsbn())) {
            bookStore.put(book.getIsbn(), book);
            UpdateBookResponse response = UpdateBookResponse.newBuilder().setMessage("Book updated successfully").build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } else {
            UpdateBookResponse response = UpdateBookResponse.newBuilder().setMessage("Book not found").build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void deleteBook(DeleteBookRequest request, StreamObserver<DeleteBookResponse> responseObserver) {
        String isbn = request.getIsbn();
        if (bookStore.remove(isbn) != null) {
            DeleteBookResponse response = DeleteBookResponse.newBuilder().setMessage("Book deleted successfully").build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } else {
            DeleteBookResponse response = DeleteBookResponse.newBuilder().setMessage("Book not found").build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void getBooks(GetBooksRequest request, StreamObserver<GetBooksResponse> responseObserver) {
        List<Book> books = new ArrayList<>(bookStore.values());
        GetBooksResponse response = GetBooksResponse.newBuilder().addAllBooks(books).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
