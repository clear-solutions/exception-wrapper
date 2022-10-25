package ltd.clearsolutions.exceptionwrapper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static ltd.clearsolutions.exceptionwrapper.ExceptionWrapper.wrap;
import static ltd.clearsolutions.exceptionwrapper.ExceptionWrapper.invoke;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExceptionWrapperTest {

    private final PrintStream standardOut = System.out;

    @AfterEach
    public void setStandardOut() {
        System.setOut(standardOut);
    }

    @Test
    void wrap_FunctionWithoutException_returnFunction() {
        Function<Integer, Boolean> wrap = wrap(o -> {return o.equals(0);});

        assertThat(wrap.apply(0))
            .isEqualTo(true);
    }

    @Test
    void wrap_FunctionWithRuntimeException_throwRuntimeException() {
        Function<Integer, Integer> wrap = wrap(o -> 10 / o);

        assertThatThrownBy(() -> wrap.apply(0))
            .isInstanceOf(ArithmeticException.class)
            .hasMessageContaining("/ by zero");
    }

    @Test
    void wrap_FunctionWithIOException_throwIOUncheckedException() {
        Function<String, String> wrap = wrap(path -> {
            return new String(Files.readAllBytes(Paths.get(path)));
        });

        assertThatThrownBy(() -> wrap.apply("/"))
            .isInstanceOf(java.io.UncheckedIOException.class)
            .hasMessageContaining("Is a directory");
    }

    @Test
    void wrap_FunctionWithException_throwUnCheckedException() {
        Function<String, String> wrap = wrap(path ->
                                             {
                                                 if ("/".equals(path)) {
                                                     throw new Exception("incorrect path");
                                                 }
                                                 return path;
                                             });

        assertThatThrownBy(() -> wrap.apply("/"))
            .isInstanceOf(ExceptionWrapper.UncheckedException.class)
            .hasMessageContaining("incorrect path");
    }

    @Test
    void invoke_FunctionWithoutException_returnFunction() {
        Boolean actualResult = invoke(o ->
                                      {
                                          return o.equals(10);
                                      }, 10);

        assertThat(actualResult).isEqualTo(true);
    }

    @Test
    void invoke_FunctionWithRuntimeException_throwRuntimeException() {
        assertThatThrownBy(() -> invoke(o -> 10 / o, 0))
            .isInstanceOf(ArithmeticException.class)
            .hasMessageContaining("/ by zero");
    }

    @Test
    void invoke_FunctionWithIOException_throwIOUncheckedException() {
        assertThatThrownBy(() -> invoke(path -> {
                                            return new String(Files.readAllBytes(Paths.get(path)));
                                        },
                                        "/"))
            .isInstanceOf(java.io.UncheckedIOException.class)
            .hasMessageContaining("Is a directory");
    }

    @Test
    void invoke_FunctionWithException_throwUnCheckedException() {
        assertThatThrownBy(() -> invoke(path -> {
                                            if ("/".equals(path)) {
                                                throw new Exception("incorrect path");
                                            }
                                            return path;
                                        },
                                        "/"))
            .isInstanceOf(ExceptionWrapper.UncheckedException.class)
            .hasMessageContaining("incorrect path");
    }

    @Test
    void wrap_BiFunctionWithoutException_returnFunction() {
        BiFunction<Integer, Integer, Boolean> wrap = wrap(Integer::equals);

        assertThat(wrap.apply(10, 10))
            .isEqualTo(true);
    }

    @Test
    void wrap_BiFunctionWithRuntimeException_throwRuntimeException() {
        BiFunction<Integer, Integer, Integer> wrap = wrap((t1, t2) -> t1 / t2);

        assertThatThrownBy(() -> wrap.apply(10, 0))
            .isInstanceOf(ArithmeticException.class)
            .hasMessageContaining("/ by zero");
    }

    @Test
    void wrap_BiFunctionWithIOException_throwIOUncheckedException() {
        BiFunction<String, String, String> wrap = wrap((path1, path2) -> {
            return new String(Files.readAllBytes(Paths.get(path1)));
        });

        assertThatThrownBy(() -> wrap.apply("/", "/"))
            .isInstanceOf(java.io.UncheckedIOException.class)
            .hasMessageContaining("Is a directory");
    }

    @Test
    void wrap_BiFunctionWithException_throwUnCheckedException() {
        BiFunction<String, String, String> wrap = wrap((path1, path2) ->
                                                       {
                                                           if ("/".equals(path1)) {
                                                               throw new Exception("incorrect path");
                                                           }
                                                           return path1;
                                                       });

        assertThatThrownBy(() -> wrap.apply("/", "/"))
            .isInstanceOf(ExceptionWrapper.UncheckedException.class)
            .hasMessageContaining("incorrect path");
    }

    @Test
    void invoke_BiFunctionWithoutException_returnFunction() {
        assertThat(invoke(Integer::equals, 10, 10))
            .isEqualTo(true);
    }

    @Test
    void invoke_BiFunctionWithRuntimeException_throwRuntimeException() {
        assertThatThrownBy(() -> invoke((t1, t2) -> t1 / t2, 10, 0))
            .isInstanceOf(ArithmeticException.class)
            .hasMessageContaining("/ by zero");
    }

    @Test
    void invoke_BiFunctionWithIOException_throwIOUncheckedException() {
        assertThatThrownBy(() -> invoke((path1, path2) -> {
            return new String(Files.readAllBytes(Paths.get(path1)));
        }, "/", "/"))
            .isInstanceOf(java.io.UncheckedIOException.class)
            .hasMessageContaining("Is a directory");
    }

    @Test
    void invoke_BiFunctionWithException_throwUnCheckedException() {
        assertThatThrownBy(() -> invoke((path1, path2) -> {
                                            if ("/".equals(path1)) {
                                                throw new Exception("incorrect path");
                                            }
                                            return path1;
                                        },
                                        "/", "/"))
            .isInstanceOf(ExceptionWrapper.UncheckedException.class)
            .hasMessageContaining("incorrect path");
    }

    @Test
    void wrap_ConsumerWithoutException_returnFunction() {
        //Given: replace systemOutput to ByteArrayOutputStream
        ByteArrayOutputStream actualOutputResult = new ByteArrayOutputStream();
        System.setOut(new PrintStream(actualOutputResult));

        //Given: create consumer
        Consumer<String> wrap = wrap(t -> {System.out.println(t);});

        //When
        wrap.accept("Hello from system output");

        //Side  effect from consumer have to be a message in output
        assertThat(actualOutputResult.toString().trim())
            .isEqualTo("Hello from system output");
    }

    @Test
    void wrap_ConsumerWithRuntimeException_throwRuntimeException() {
        Consumer<Integer> wrap = wrap(o -> {int i = 10 / o;});

        assertThatThrownBy(() -> wrap.accept(0))
            .isInstanceOf(ArithmeticException.class)
            .hasMessageContaining("/ by zero");
    }

    @Test
    void wrap_ConsumerWithIOException_throwIOUncheckedException() {
        Consumer<String> wrap = wrap(path -> {
            new String(Files.readAllBytes(Paths.get(path)));
        });

        assertThatThrownBy(() -> wrap.accept("/"))
            .isInstanceOf(java.io.UncheckedIOException.class)
            .hasMessageContaining("Is a directory");
    }

    @Test
    void wrap_ConsumerWithException_throwUnCheckedException() {
        Consumer<String> wrap = wrap(path -> {
            if ("/".equals(path)) {
                throw new Exception("incorrect path");
            }
        });

        assertThatThrownBy(() -> wrap.accept("/"))
            .isInstanceOf(ExceptionWrapper.UncheckedException.class)
            .hasMessageContaining("incorrect path");
    }

    @Test
    void wrap_BiConsumerWithoutException_returnFunction() {
        //Given: replace systemOutput to ByteArrayOutputStream
        ByteArrayOutputStream actualOutputResult = new ByteArrayOutputStream();
        System.setOut(new PrintStream(actualOutputResult));

        //Given: create consumer
        BiConsumer<String, String> wrap = wrap((t1, t2) -> {System.out.println(t1 + t2);});

        //When
        wrap.accept("Hello", " from system output");

        //Side  effect from consumer have to be a message in output
        assertThat(actualOutputResult.toString().trim())
            .isEqualTo("Hello from system output");
    }

    @Test
    void wrap_BiConsumerWithRuntimeException_throwRuntimeException() {
        BiConsumer<Integer, Integer> wrap = wrap((o1, o2) -> {int i = 10 / o1;});

        assertThatThrownBy(() -> wrap.accept(0, 0))
            .isInstanceOf(ArithmeticException.class)
            .hasMessageContaining("/ by zero");
    }

    @Test
    void wrap_BiConsumerWithIOException_throwIOUncheckedException() {
        BiConsumer<String, String> wrap = wrap((path1, path2) -> {
            new String(Files.readAllBytes(Paths.get(path1)));
        });

        assertThatThrownBy(() -> wrap.accept("/", "/"))
            .isInstanceOf(java.io.UncheckedIOException.class)
            .hasMessageContaining("Is a directory");
    }

    @Test
    void wrap_BiConsumerWithException_throwUnCheckedException() {
        BiConsumer<String, String> wrap = wrap((path1, path2) -> {
            if ("/".equals(path1)) {
                throw new Exception("incorrect path");
            }
        });

        assertThatThrownBy(() -> wrap.accept("/", "/"))
            .isInstanceOf(ExceptionWrapper.UncheckedException.class)
            .hasMessageContaining("incorrect path");
    }

    @Test
    void invoke_ConsumerWithoutException_returnFunction() {
        //Given: replace systemOutput to ByteArrayOutputStream
        ByteArrayOutputStream actualOutputResult = new ByteArrayOutputStream();
        System.setOut(new PrintStream(actualOutputResult));

        //When
        invoke(o -> {System.out.println(o);}, "Hello");

        //Side  effect from consumer have to be a message in output
        assertThat(actualOutputResult.toString().trim())
            .isEqualTo("Hello");
    }

    @Test
    void invoke_ConsumerWithRuntimeException_throwRuntimeException() {
        assertThatThrownBy(() -> invoke(o -> {int i = 10 / o;}, 0))
            .isInstanceOf(ArithmeticException.class)
            .hasMessageContaining("/ by zero");
    }

    @Test
    void invoke_ConsumerWithIOException_throwIOUncheckedException() {
        assertThatThrownBy(() -> invoke(path -> {
                                            new String(Files.readAllBytes(Paths.get(path)));
                                        },
                                        "/"))
            .isInstanceOf(java.io.UncheckedIOException.class)
            .hasMessageContaining("Is a directory");
    }

    @Test
    void invoke_ConsumerWithException_throwUnCheckedException() {
        assertThatThrownBy(() -> invoke(path -> {
                                            if ("/".equals(path)) {
                                                throw new Exception("incorrect path");
                                            }
                                        },
                                        "/"))
            .isInstanceOf(ExceptionWrapper.UncheckedException.class)
            .hasMessageContaining("incorrect path");
    }

    @Test
    void invoke_BiConsumerWithoutException_returnFunction() {
        //Given: replace systemOutput to ByteArrayOutputStream
        ByteArrayOutputStream actualOutputResult = new ByteArrayOutputStream();
        System.setOut(new PrintStream(actualOutputResult));

        //When
        invoke((o1, o2) -> {System.out.println(o1 + o2);}, "Hello", " Portugal!");

        //Side  effect from consumer have to be a message in output
        assertThat(actualOutputResult.toString().trim())
            .isEqualTo("Hello Portugal!");
    }

    @Test
    void invoke_BiConsumerWithRuntimeException_throwRuntimeException() {
        assertThatThrownBy(() -> invoke((o1, o2) -> {int i = 10 / o1;}, 0, 0))
            .isInstanceOf(ArithmeticException.class)
            .hasMessageContaining("/ by zero");
    }

    @Test
    void invoke_BiConsumerWithIOException_throwIOUncheckedException() {
        assertThatThrownBy(() -> invoke((path1, path2) -> {
                                            new String(Files.readAllBytes(Paths.get(path1)));
                                        },
                                        "/", "/"))
            .isInstanceOf(java.io.UncheckedIOException.class)
            .hasMessageContaining("Is a directory");
    }

    @Test
    void invoke_BiConsumerWithException_throwUnCheckedException() {
        assertThatThrownBy(() -> invoke((path1, path2) -> {
                                            if ("/".equals(path1)) {
                                                throw new Exception("incorrect path");
                                            }
                                        },
                                        "/", "/"))
            .isInstanceOf(ExceptionWrapper.UncheckedException.class)
            .hasMessageContaining("incorrect path");
    }

    @Test
    void wrap_SupplierWithoutException_returnFunction() {
        Supplier<Integer> wrap = wrap(() -> 5);

        assertThat(wrap.get())
            .isEqualTo(5);
    }

    @Test
    void wrap_SupplierWithRuntimeException_throwRuntimeException() {
        Supplier<Integer> wrap = wrap(() -> 10 / 0);

        assertThatThrownBy(wrap::get)
            .isInstanceOf(ArithmeticException.class)
            .hasMessageContaining("/ by zero");
    }

    @Test
    void wrap_SupplierWithIOException_throwIOUncheckedException() {
        Supplier<String> wrap = wrap(() -> {
            return new String(Files.readAllBytes(Paths.get("/")));
        });

        assertThatThrownBy(wrap::get)
            .isInstanceOf(java.io.UncheckedIOException.class)
            .hasMessageContaining("Is a directory");
    }

    @Test
    void wrap_SupplierWithException_throwUnCheckedException() {
        Supplier<String> wrap = wrap(() -> {
            if ("/".equals("/")) {
                throw new Exception("incorrect path");
            }
            return "/";
        });

        assertThatThrownBy(wrap::get)
            .isInstanceOf(ExceptionWrapper.UncheckedException.class)
            .hasMessageContaining("incorrect path");
    }

    @Test
    void invoke_SupplierWithoutException_returnFunction() {
        assertThat(invoke(() -> 5))
            .isEqualTo(5);
    }

    @Test
    void invoke_SupplierWithRuntimeException_throwRuntimeException() {
        assertThatThrownBy(() -> invoke(() -> 10 / 0))
            .isInstanceOf(ArithmeticException.class)
            .hasMessageContaining("/ by zero");
    }

    @Test
    void invoke_SupplierWithIOException_throwIOUncheckedException() {
        assertThatThrownBy(() -> invoke(() -> {
            return new String(Files.readAllBytes(Paths.get("/")));
        }))
            .isInstanceOf(java.io.UncheckedIOException.class)
            .hasMessageContaining("Is a directory");
    }

    @Test
    void invoke_SupplierWithException_throwUnCheckedException() {
        assertThatThrownBy(() -> {
            invoke(() -> {
                if ("/".equals("/")) {
                    throw new Exception("incorrect path");
                }
                return "/";
            });
        })
            .isInstanceOf(ExceptionWrapper.UncheckedException.class)
            .hasMessageContaining("incorrect path");
    }

    @Test
    void wrap_CallWithoutException_returnFunction() {
        //Given: replace systemOutput to ByteArrayOutputStream
        ByteArrayOutputStream actualOutputResult = new ByteArrayOutputStream();
        System.setOut(new PrintStream(actualOutputResult));

        //Given: create consumer
        Runnable wrap = wrap(() -> {System.out.println("Hi from Ukraine");});

        //When
        wrap.run();

        //Side  effect from consumer have to be a message in output
        assertThat(actualOutputResult.toString().trim())
            .isEqualTo("Hi from Ukraine");
    }

    @Test
    void wrap_CallWithRuntimeException_throwRuntimeException() {
        Runnable wrap = wrap(() -> {int i = 10 / 0;});

        assertThatThrownBy(wrap::run)
            .isInstanceOf(ArithmeticException.class)
            .hasMessageContaining("/ by zero");
    }

    @Test
    void wrap_CallWithIOException_throwIOUncheckedException() {
        Runnable wrap = wrap(() -> {
            new String(Files.readAllBytes(Paths.get("/")));
        });

        assertThatThrownBy(wrap::run)
            .isInstanceOf(java.io.UncheckedIOException.class)
            .hasMessageContaining("Is a directory");
    }

    @Test
    void wrap_CallWithException_throwUnCheckedException() {
        Runnable wrap = wrap(() -> {
            if ("/".equals("/")) {
                throw new Exception("incorrect path");
            }
        });

        assertThatThrownBy(wrap::run)
            .isInstanceOf(ExceptionWrapper.UncheckedException.class)
            .hasMessageContaining("incorrect path");
    }

    @Test
    void invoke_CallWithoutException_returnFunction() {
        //Given: replace systemOutput to ByteArrayOutputStream
        ByteArrayOutputStream actualOutputResult = new ByteArrayOutputStream();
        System.setOut(new PrintStream(actualOutputResult));

        //When
        invoke(() -> {System.out.println("Hi from Ukraine!!");});

        //Side  effect from consumer have to be a message in output
        assertThat(actualOutputResult.toString().trim())
            .isEqualTo("Hi from Ukraine!!");
    }

    @Test
    void invoke_CallWithRuntimeException_throwRuntimeException() {
        assertThatThrownBy(() -> {invoke(() -> {int i = 10 / 0;});})
            .isInstanceOf(ArithmeticException.class)
            .hasMessageContaining("/ by zero");
    }

    @Test
    void invoke_CallWithIOException_throwIOUncheckedException() {
        assertThatThrownBy(() ->
                           {
                               invoke(() -> {
                                   new String(Files.readAllBytes(Paths.get("/")));
                               });
                           })
            .isInstanceOf(java.io.UncheckedIOException.class)
            .hasMessageContaining("Is a directory");
    }

    @Test
    void invoke_CallWithException_throwUnCheckedException() {
        assertThatThrownBy(() ->
                           {
                               invoke(() -> {
                                   String str = "";
                                   if ("/".equals(str)) {
                                       System.out.println(str);
                                   }
                                   throw new Exception("incorrect path");
                               });
                           })
            .isInstanceOf(ExceptionWrapper.UncheckedException.class)
            .hasMessageContaining("incorrect path");
    }
}
