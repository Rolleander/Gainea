package com.broll.gainea.server.core.utils

import java.util.function.Consumer
import java.util.stream.Collectors
import java.util.stream.Stream

object StreamUtils {
    fun <T> safeForEach(stream: Stream<T>, consumer: Consumer<in T>?) {
        copy(stream).forEach(consumer)
    }

    fun <T> copy(stream: Stream<T>): Stream<T> {
        return stream.collect(Collectors.toList()).stream()
    }
}
