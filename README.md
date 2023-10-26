![Hex.pm](https://img.shields.io/hexpm/l/apa)


# exception-wrapper
wrapper is a very simple Java library the main purpose is convert checked exceptions to runtime (unchecked) exceptions into lambda or other part of code.


The `ExceptionWrapper` library/class you provided is a utility class designed to convert checked exceptions into unchecked exceptions. This is achieved by providing wrapper methods for different functional interfaces, such as `Function`, `Consumer`, and `Supplier`, that might throw checked exceptions.

The checked exceptions are caught inside these wrapper methods, and then rethrown as unchecked exceptions. Specifically, `IOException` is rethrown as `UncheckedIOException`, and other `Exception` types are rethrown as a custom unchecked exception, `UncheckedException`.

Below is a summary of the main components of the class:

### Inner Functional Interfaces with Exceptions
- `FunctionWithException<T, R>`: Represents a function that accepts one argument, returns a result, and might throw an exception.
- `BiFunctionWithException<T1, T2, R>`: Represents a function that accepts two arguments, returns a result, and might throw an exception.
- `ConsumerWithException<T>`: Represents an operation that accepts a single argument and returns no result but might throw an exception.
- `BiConsumerWithException<T1, T2>`: Represents an operation that accepts two arguments, returns no result, and might throw an exception.
- `SupplierWithException<T>`: Represents a supplier of results, which might throw an exception.
- `CallWithException`: Represents a runnable operation that might throw an exception.

### Unchecked Exception Wrapper
- `UncheckedException`: A custom unchecked exception class to wrap checked exceptions.

### Wrapper Methods
- `wrap(FunctionWithException<T, R> function)`: Wraps a `FunctionWithException` with a `Function`.
- `invoke(FunctionWithException<T, R> function, T t)`: Invokes a `FunctionWithException` and rethrows checked exceptions as unchecked exceptions.
- Similar `wrap` and `invoke` methods are provided for `BiFunction`, `Consumer`, `BiConsumer`, `Supplier`, and `Runnable` with their respective `WithException` counterparts.

### Usage
The class provides a convenient way to use lambda expressions or method references that throw checked exceptions in contexts where a functional interface that does not allow checked exceptions is expected. For example, you can use it with Java Streams API to handle operations that might throw `IOException` or other checked exceptions.

### Example Usage

```java
List<String> list = Arrays.asList("1", "2", "x", "4");

List<Integer> integers = list.stream()
        .map(ExceptionWrapper.wrap(s -> {
            if ("x".equals(s)) throw new IOException("Invalid number format");
            return Integer.parseInt(s);
        }))
        .collect(Collectors.toList());
```

In the above example, the `map` operation is expecting a `Function`, but the lambda expression throws a checked `IOException`. The `ExceptionWrapper.wrap` method converts the function into a form that catches the `IOException` and rethrows it as an unchecked `UncheckedIOException`. This allows the lambda expression to be used within the `map` operation without having to handle the checked exception explicitly.
