package com.vrtherapy.util;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Generic result monad container representing either a successful outcome with a payload
 * or a failure with an explanatory error message.
 *
 * Demonstrates:
 * - Generics (Type parameters & wildcards)
 * - Functional interfaces (Function, Consumer)
 * - Optional integration
 *
 * @param <T> The payload type
 */
public class Result<T> {

    private final boolean success;
    private final T data;
    private final String message;
    private final int statusCode;

    private Result(boolean success, T data, String message, int statusCode) {
        this.success = success;
        this.data = data;
        this.message = message;
        this.statusCode = statusCode;
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(true, data, "Success", 200);
    }

    public static <T> Result<T> success(T data, String message) {
        return new Result<>(true, data, message, 200);
    }

    public static <T> Result<T> failure(String message) {
        return new Result<>(false, null, message, 400);
    }

    public static <T> Result<T> failure(String message, int statusCode) {
        return new Result<>(false, null, message, statusCode);
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean isFailure() {
        return !success;
    }

    public T getData() {
        return data;
    }

    public Optional<T> toOptional() {
        return Optional.ofNullable(data);
    }

    public String getMessage() {
        return message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public <R> Result<R> map(Function<? super T, ? extends R> mapper) {
        if (isSuccess()) {
            return Result.success(mapper.apply(data), message);
        }
        return Result.failure(message, statusCode);
    }

    public void ifSuccess(Consumer<? super T> action) {
        if (isSuccess() && data != null) {
            action.accept(data);
        }
    }

    @Override
    public String toString() {
        return "Result{" +
                "success=" + success +
                ", data=" + data +
                ", message='" + message + '\'' +
                ", statusCode=" + statusCode +
                '}';
    }
}
