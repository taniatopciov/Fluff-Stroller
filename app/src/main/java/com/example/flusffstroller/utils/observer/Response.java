package com.example.flusffstroller.utils.observer;

public class Response<T> {
    public final T data;
    public final Exception exception;

    public Response(T data) {
        this.data = data;
        exception = null;
    }

    public Response(Exception exception) {
        data = null;
        this.exception = exception;
    }

    public boolean hasErrors() {
        return exception != null;
    }
}
