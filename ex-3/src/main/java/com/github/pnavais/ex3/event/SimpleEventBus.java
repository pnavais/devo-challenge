/*
 *
 * Copyright 2020 Pablo Navais
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.github.pnavais.ex3.event;

import com.github.pnavais.ex3.api.event.BusEvent;
import com.github.pnavais.ex3.api.event.BusEventListener;
import lombok.NonNull;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A very simplistic implementation of an event bus
 * based on the publish/subscribe pattern
 */
public class SimpleEventBus {

	/**
	 * The subscribers.
	 */
	private final Map<Class<? extends BusEvent>, Set<BusEventListener>> subscribers;

	/**
	 * Ensures non instantiable.
	 */
	private SimpleEventBus()
	{
		this.subscribers = new ConcurrentHashMap<>();
	}

	/**
	 * Gets the EventBus instance.
	 *
	 * @return the EventBus instance
	 */
	public static SimpleEventBus getDefault() {
		return EventBusHolder.instance;
	}

	/**
	 * Contains the EventBus instance.
	 */
	private static class EventBusHolder {
		private static final SimpleEventBus instance = new SimpleEventBus();
	}

	/**
	 * Registers the subscriber for the given event
	 *
	 * @param e the event
	 * @param subscriber the subscriber
	 */
	public void register(@NonNull Class<? extends BusEvent> e, @NonNull BusEventListener subscriber) {
		this.subscribers.computeIfAbsent(e, aClass -> new HashSet<>());
		this.subscribers.get(e).add(subscriber);
	}


	/**
	 * Unregisters the subscriber from reception of
	 * the given event
	 *
	 * @param e the event
	 * @param subscriber the subscriber
	 */
	public void unregister(@NonNull Class<? extends BusEvent> e, @NonNull BusEventListener subscriber) {
		Set<BusEventListener> receivers = this.subscribers.get(e);
		if (receivers != null) {
			receivers.remove(subscriber);
		}
	}

	/**
	 * Publish a new event on the bus
	 *
	 * @param e the event to publish
	 */
	public void publish(BusEvent e) {
		if (e!=null) {
			Set<BusEventListener> receivers = this.subscribers.get(e.getClass());
			if (receivers!=null) {
				for (BusEventListener subscriber : receivers) {
					subscriber.onEvent(e);
				}
			}
		}
	}

}