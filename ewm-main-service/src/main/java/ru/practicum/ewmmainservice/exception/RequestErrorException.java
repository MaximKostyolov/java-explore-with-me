package ru.practicum.ewmmainservice.exception;

public class RequestErrorException extends RuntimeException {

    public RequestErrorException(String message) {
        super(message);
    }
}
