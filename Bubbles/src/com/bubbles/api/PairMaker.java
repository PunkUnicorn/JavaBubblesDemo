package com.bubbles.api;

public class PairMaker<T> implements IMakeT<Pair<T>> {
	@Override
	public Pair<T> Make() {
		return new Pair<T>();
	}
}