package ltd.clearsolutions.exceptionwrapper;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ExceptionWrapper {

    @FunctionalInterface
    public interface FunctionWithException<T, R> {
        R apply(T t) throws Exception;
    }

    @FunctionalInterface
    public interface BiFunctionWithException<T1, T2, R> {
        R apply(T1 t1, T2 t2) throws Exception;
    }

    @FunctionalInterface
    public interface ConsumerWithException<T> {
        void apply(T t) throws Exception;
    }

    @FunctionalInterface
    public interface BiConsumerWithException<T1, T2> {
        void apply(T1 t1, T2 t2) throws Exception;
    }

    @FunctionalInterface
    public interface SupplierWithException<T> {
        T get() throws Exception;
    }

    @FunctionalInterface
    public interface CallWithException {
        void apply() throws Exception;
    }

    /**
     * Wraps an {@link Exception} with an unchecked exception.
     *
     */
    public static class UncheckedException extends RuntimeException {
        @SuppressWarnings("unused")
        public UncheckedException(String message, Throwable cause) {
            super(message, cause);
        }

        public UncheckedException(Throwable cause) {
            super(cause);
        }

    }

    /**
     * wraps given function with checked exception
     * catches and rethrows checked exceptions as unchecked exceptions: IOException to UncheckedIOException; Exception to UncheckedException
     * @return returns function without checked exceptions
     * @see java.io.UncheckedIOException
     * @see ltd.clearsolutions.exceptionwrapper.ExceptionWrapper.UncheckedException
     */
    public static <T, R> Function<T, R> wrap(FunctionWithException<T, R> function) {
        return arg -> invoke(function, arg);
    }

    /**
     * invoke given function with checked exception
     * catches and rethrows checked exceptions as unchecked exceptions: IOException to UncheckedIOException; Exception to UncheckedException
     * @return {@link R}
     * @see java.io.UncheckedIOException
     * @see ltd.clearsolutions.exceptionwrapper.ExceptionWrapper.UncheckedException
     */
    public static <T, R> R invoke(FunctionWithException<T, R> functional, T t) {
        try {
            return functional.apply(t);
        } catch (Exception e) {
            throw mapException(e);
        }
    }

    /**
     * wraps given BiFunction with checked exception
     * catches and rethrows checked exceptions as unchecked exceptions: IOException to UncheckedIOException; Exception to UncheckedException
     * @return returns BiFunction without checked exceptions
     * @see java.io.UncheckedIOException
     * @see ltd.clearsolutions.exceptionwrapper.ExceptionWrapper.UncheckedException
     */
    public static <T1, T2, R> BiFunction<T1, T2, R> wrap(BiFunctionWithException<T1, T2, R> function) {
        return (t1, t2) -> invoke(function, t1, t2);
    }

    /**
     * invoke given BiFunction with checked exception
     * catches and rethrows checked exceptions as unchecked exceptions: IOException to UncheckedIOException; Exception to UncheckedException
     * @return {@link R}
     * @see java.io.UncheckedIOException
     * @see ltd.clearsolutions.exceptionwrapper.ExceptionWrapper.UncheckedException
     */
    public static <T1, T2, R> R invoke(BiFunctionWithException<T1, T2, R> function, T1 t1, T2 t2) {
        try {
            return function.apply(t1, t2);
        } catch (Exception e) {
           throw mapException(e);
        }
    }

    /**
     * wraps given consumer with checked exception
     * catches and rethrows checked exceptions as unchecked exceptions: IOException to UncheckedIOException; Exception to UncheckedException
     * @return returns consumer without checked exceptions
     * @see java.io.UncheckedIOException
     * @see ltd.clearsolutions.exceptionwrapper.ExceptionWrapper.UncheckedException
     */
    public static <T> Consumer<T> wrap(ConsumerWithException<T> consumer) {
        return o -> invoke(consumer, o);
    }


    /**
     * wraps given BiConsumer with checked exception
     * catches and rethrows checked exceptions as unchecked exceptions: IOException to UncheckedIOException; Exception to UncheckedException
     * @return returns BiConsumer without checked exceptions
     * @see java.io.UncheckedIOException
     * @see ltd.clearsolutions.exceptionwrapper.ExceptionWrapper.UncheckedException
     */
    public static <T1, T2> BiConsumer<T1, T2> wrap(BiConsumerWithException<T1, T2> consumer) {
        return (t1, t2) -> invoke(consumer, t1, t2);
    }

    /**
     * invoke given Consumer with checked exception
     * catches and rethrows checked exceptions as unchecked exceptions: IOException to UncheckedIOException; Exception to UncheckedException
     * @see java.io.UncheckedIOException
     * @see ltd.clearsolutions.exceptionwrapper.ExceptionWrapper.UncheckedException
     */
    public static <T> void invoke(ConsumerWithException<T> consumer, T t) {
        try {
            consumer.apply(t);
        } catch (Exception e) {
            handleException(e);
        }
    }

    /**
     * invoke given BiConsumer with checked exception
     * catches and rethrows checked exceptions as unchecked exceptions: IOException to UncheckedIOException; Exception to UncheckedException
     * @see java.io.UncheckedIOException
     * @see ltd.clearsolutions.exceptionwrapper.ExceptionWrapper.UncheckedException
     */
    public static <T1, T2> void invoke(BiConsumerWithException<T1, T2> consumer, T1 t1, T2 t2) {
        try {
            consumer.apply(t1, t2);
        } catch (Exception e) {
            throw mapException(e);
        }
    }

    /**
     * wraps given Supplier with checked exception
     * catches and rethrows checked exceptions as unchecked exceptions: IOException to UncheckedIOException; Exception to UncheckedException
     * @return returns Supplier without checked exceptions
     * @see java.io.UncheckedIOException
     * @see ltd.clearsolutions.exceptionwrapper.ExceptionWrapper.UncheckedException
     */
    public static <R> Supplier<R> wrap(SupplierWithException<R> supplier) {
        return () -> invoke(supplier);
    }

    /**
     * invoke given Supplier with checked exception
     * catches and rethrows checked exceptions as unchecked exceptions: IOException to UncheckedIOException; Exception to UncheckedException
     * @see java.io.UncheckedIOException
     * @see ltd.clearsolutions.exceptionwrapper.ExceptionWrapper.UncheckedException
     */
    public static <R> R invoke(SupplierWithException<R> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            throw mapException(e);
        }
    }

    /**
     * wraps given Runnable with checked exception
     * catches and rethrows checked exceptions as unchecked exceptions: IOException to UncheckedIOException; Exception to UncheckedException
     * @return returns Runnable without checked exceptions
     * @see java.io.UncheckedIOException
     * @see ltd.clearsolutions.exceptionwrapper.ExceptionWrapper.UncheckedException
     */
    public static Runnable wrap(CallWithException consumer) {
        return () -> invoke(consumer);
    }

    /**
     * invoke given Runnable with checked exception
     * catches and rethrows checked exceptions as unchecked exceptions: IOException to UncheckedIOException; Exception to UncheckedException
     * @see java.io.UncheckedIOException
     * @see ltd.clearsolutions.exceptionwrapper.ExceptionWrapper.UncheckedException
     */
    public static void invoke(CallWithException consumer) {
        try {
            consumer.apply();
        } catch (Exception e) {
            handleException(e);
        }
    }

    private static <E extends Exception> void handleException(E e) {
        throw mapException(e);
    }

    private static <E extends Exception> RuntimeException mapException(E e) {
        if (e instanceof IOException) {
            return new UncheckedIOException((IOException) e);
        } else if (e instanceof RuntimeException) {
            return (RuntimeException) e;
        } else {
            return new UncheckedException(e);
        }
    }
}
