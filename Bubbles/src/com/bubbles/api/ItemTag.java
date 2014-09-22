package com.bubbles.api;

import java.util.concurrent.atomic.AtomicBoolean;

class ItemTag<T> {
	private final T item;

	public T getItem() {
		return item;
	}

	private final AtomicBoolean tagged = new AtomicBoolean(false);

	public AtomicBoolean getTagged() {
		return tagged;
	}

	public ItemTag(final T defValue) {
		item = defValue;
	}
}