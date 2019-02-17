package com.github.aaoferreira.rxeventbus.api;

import io.reactivex.Flowable;

/** Event bus spec */
public interface EventBus {

    /** Publishes event through the event bus
     *
     * @param event event to be published
     * @param <T> type of the event value
     * @throws NullPointerException when {@code event} is {@code null}
     */
    <T> void publish(Event<T> event) throws NullPointerException;

    /** Subscribes to events. By nature, the stream returned by this method is an infinite one
     *
     * @param eventType event type {@link Class} to subscribe to
     * @param <T> type of the event subscription
     * @return an infinite stream of events of the type requested
     * @throws NullPointerException when {@code eventType} is {@code null}
     */
    <T> Flowable<Event<T>> subscribe(Class<T> eventType) throws NullPointerException;

    /** Subscribes to events filtered by the {@code correlationId} passed
     *
     * @param correlationId correlation id of the event
     * @param eventType event type {@link Class} to subscribe to
     * @param <T> type of the event subscription
     * @return a stream of events of the type requested filtered by the {@code correlationId} passed
     * @throws NullPointerException when any of the parameters is {@code null}
     */
    <T> Flowable<Event<T>> subscribe(String correlationId, Class<T> eventType) throws NullPointerException;
}
