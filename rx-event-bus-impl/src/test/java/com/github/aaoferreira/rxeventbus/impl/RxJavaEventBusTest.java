package com.github.aaoferreira.rxeventbus.impl;

import com.github.aaoferreira.rxeventbus.api.Event;
import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
class RxJavaEventBusTest {

    private static final long TIMEOUT_VALUE = 10L;
    private static final TimeUnit TIMEOUT_UNIT = TimeUnit.MILLISECONDS;

    private RxJavaEventBus bus;

    @BeforeEach
    void setUp() {
        bus = new RxJavaEventBus();
    }

    @AfterEach
    void tearDown() {
        bus = null;
    }

    @Test
    void testPublishNullFailure() {
        assertThatThrownBy(() -> bus.publish(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void testPublishSubscribeSuccessful() throws Exception {
        // Setup
        final Event<EventTypeA> eventA = Event.of(new EventTypeA());
        final Event<EventTypeB> eventB = Event.of(new EventTypeB());
        // Act
        final TestSubscriber<Event<EventTypeA>> testSubscriberA = testSubscribe(EventTypeA.class);
        final TestSubscriber<Event<EventTypeB>> testSubscriberB = testSubscribe(EventTypeB.class);

        bus.publish(eventA);
        bus.publish(eventB);
        // Assert
        assertEvents(testSubscriberA, eventA);
        assertEvents(testSubscriberB, eventB);
    }

    @Test
    void testPublishSubscribeWithCorrelationIdSuccessful() throws Exception {
        // Setup
        final String correlationIdA = "eventA";
        final Event<EventTypeA> eventA = Event.of(correlationIdA, new EventTypeA());
        final String correlationIdB = "eventB";
        final Event<EventTypeA> eventB = Event.of(correlationIdB, new EventTypeA());
        final String correlationIdC = "eventC";
        final Event<EventTypeA> eventC = Event.of(correlationIdC, new EventTypeA());
        // Act
        final TestSubscriber<Event<EventTypeA>> testSubscriberA = testSubscribe(correlationIdA, EventTypeA.class);
        final TestSubscriber<Event<EventTypeA>> testSubscriberB = testSubscribe(correlationIdB, EventTypeA.class);

        bus.publish(eventA);
        bus.publish(eventB);
        bus.publish(eventC);
        // Assert
        assertEvents(testSubscriberA, eventA);
        assertEvents(testSubscriberB, eventB);
    }

    // Helper methods and classes

    private class EventTypeA {}

    private class EventTypeB {}

    private <T> TestSubscriber<Event<T>> testSubscribe(final Supplier<Flowable<Event<T>>> subscription) {
        return subscription.get()
                .doOnNext(event -> log.info("Got event [{}]", event))
                .doOnError(e -> log.error(e.getMessage(), e))
                .test();
    }

    private <T> TestSubscriber<Event<T>> testSubscribe(final Class<T> eventType) {
        return testSubscribe(() -> bus.subscribe(eventType));
    }

    private <T> TestSubscriber<Event<T>> testSubscribe(final String correlationId, final Class<T> eventType) {
        return testSubscribe(() -> bus.subscribe(correlationId, eventType));
    }

    @SafeVarargs
    private final <T> void assertEvents(final TestSubscriber<Event<T>> testSubscriber, final Event<T> ... expectedEvents)
            throws InterruptedException {
        testSubscriber.await(TIMEOUT_VALUE, TIMEOUT_UNIT);
        testSubscriber.assertNoErrors();

        log.info("Expected events [{}]", Arrays.asList(expectedEvents));
        log.info("Received events [{}]", testSubscriber.values());

        testSubscriber.assertValueCount(expectedEvents.length);
        testSubscriber.assertValueSet(Arrays.asList(expectedEvents));
        testSubscriber.dispose();
    }
}
