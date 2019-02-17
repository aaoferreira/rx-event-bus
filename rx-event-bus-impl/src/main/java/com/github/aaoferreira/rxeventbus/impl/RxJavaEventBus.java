package com.github.aaoferreira.rxeventbus.impl;

import com.github.aaoferreira.rxeventbus.api.Event;
import com.github.aaoferreira.rxeventbus.api.EventBus;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.subjects.PublishSubject;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/** RxJava {@link EventBus} implementation */
@Slf4j
public class RxJavaEventBus implements EventBus {

    private final PublishSubject<Event> subject = PublishSubject.create();

    @Override
    public <T> void publish(@NonNull final Event<T> event) {
        log.debug("Publishing event [{}]", event);
        subject.onNext(event);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Flowable<Event<T>> subscribe(@NonNull final Class<T> eventType) {
        log.debug("Subscribing to events of type [{}]", eventType.getName());
        return subject
                .toFlowable(BackpressureStrategy.BUFFER)
                .filter(event -> eventType.isInstance(event.getValue()))
                .map(event -> (Event<T>) event);
    }

    @Override
    public <T> Flowable<Event<T>> subscribe(
            @NonNull final String correlationId,
            @NonNull final Class<T> eventType
    ) {
        log.debug("Subscribing to events of correlationId [{}], type [{}]", correlationId, eventType.getName());
        return subscribe(eventType)
                .filter(event -> correlationId.equals(event.getCorrelationId()));
    }
}
