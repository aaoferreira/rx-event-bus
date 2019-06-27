package com.github.aaoferreira.rxeventbus.api;

import lombok.Value;

import java.util.Random;
import java.util.UUID;

/**
 * Event encapsulation class
 */
@Value
public class Event<T> {

    // The random correlationId is just used to match responses, so we don't need a cryptographically strong random,
    // therefore we can optimise the random correlationId generation with a simple Random
    private static final Random random = new Random();

    private final String correlationId;
    private final T value;

    private Event(final String correlationId, final T value) {
        this.correlationId = correlationId;
        this.value = value;
    }

    /**
     * Creates a new {@link Event} with the correlationId and value passed
     *
     * @param correlationId event correlation id
     * @param value         event value
     */
    public static <T> Event<T> of(final String correlationId, final T value) {
        return new Event<>(correlationId, value);
    }

    /**
     * Creates a new {@link Event} with a random correlationId
     *
     * @param value event value
     */
    public static <T> Event<T> of(final T value) {
        return new Event<>(new UUID(random.nextLong(), random.nextLong()).toString(), value);
    }
}
