package com.bubbles.api;

public class Pair<T> implements IBankable {
	private static int nextId;
	private int id;
	public T p1;
	public T p2;

	public Pair(final T p1, final T p2) {
		this.p1 = p1;
		this.p2 = p2;
		makeId();
	}

	public Pair() {
		makeId();
	}

	private synchronized void makeId() {
		id = nextId++;
	}

	public void set(final T p1, final T p2) {
		this.p1 = p1;
		this.p2 = p2;
	}

	public boolean equals(final Object o) {
		if (!(o instanceof Pair<?>))
			return false;
		try {
			@SuppressWarnings("unchecked")
			final Pair<T> cvrt = (Pair<T>) o;
			return this.equals(cvrt);
		} catch (final Exception ex) {
			return false;
		}
	}

	public boolean equals(final Pair<T> key) {
		return p1 == key.p1 && p2 == key.p2;
	}

	public int hashCode() {
		int result = p2.hashCode();
		result = 181 * result + p1.hashCode();
		return result;
	}

	@Override
	// IBankable
	public int getId() {
		return id;
	}

	@Override
	public String toString() {
		return p1.toString() + ", " + p2.toString();
	}
}