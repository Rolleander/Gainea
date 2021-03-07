package com.broll.gainea.server.core.utils;

import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class StreamUtils {

    private StreamUtils() {

    }

    public static <T> void safeForEach(Stream<T> stream, Consumer<? super T> consumer) {
        copy(stream).forEach(consumer);
    }

    public static <T> Stream<T> copy(Stream<T> stream) {
        return stream.collect(Collectors.toList()).stream();
    }
}
