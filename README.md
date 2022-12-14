![Hex.pm](https://img.shields.io/hexpm/l/apa)


# exception-wrapper
Exception wrapper is a very simple Java library the main purpose is convert checked exceptions to runtime (unchecked) exceptions into lambda or other part of code.


## Download ##
[From maven repository](https://mvnrepository.com/)
Just add the following dependency in your `pom.xml`:
```xml
<dependency>
    <groupId>ltd.clear-solutions</groupId>
    <artifactId>exception-wrapper</artifactId>
    <version>1.0</version>
</dependency>
```
### How it wrap the exceptions
- `RuntimeException` are just propagated as they are
- `java.io.IOException` are wrapped inside `java.io.UncheckedIOException`
- other  checked exceptions are wrapped inside RuntimeException

