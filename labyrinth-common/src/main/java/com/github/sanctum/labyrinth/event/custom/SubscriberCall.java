package com.github.sanctum.labyrinth.event.custom;

@FunctionalInterface
public interface SubscriberCall<T extends Vent> {

	void accept(T event, Vent.Subscription<T> subscription);

}
